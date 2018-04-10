package com.stanleylog.java.init;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;

/**
 * 
 * <p>���ڼ�������ĿĿ¼�е�Properties�ļ��е����ݡ�</p>
 * 
 * @author Zhiguang Sun
 * 
 */
public class PropertyHelper {

	/**
	 * When null (the usual case), no diagnostic output will be generated by
	 * LogFactory or LogFactoryImpl. When non-null, interesting events will be
	 * written to the specified object.
	 */
	private static PrintStream diagnosticsStream = null;

	/**
	 * A string that gets prefixed to every message output by the logDiagnostic
	 * method, so that users can clearly see which LogFactory class is
	 * generating the output.
	 */
	private static final String diagnosticPrefix;

	/**
	 * The name (<code>priority</code>) of the key in the config file used to
	 * specify the priority of that particular config file. The associated value
	 * is a floating-point number; higher values take priority over lower
	 * values.
	 */
	public static final String PRIORITY_KEY = "priority";

	/**
	 * A reference to the classloader that loaded this class. This is the same
	 * as LogFactory.class.getClassLoader(). However computing this value isn't
	 * quite as simple as that, as we potentially need to use AccessControllers
	 * etc. It's more efficient to compute it once and cache it here.
	 */
	private static final ClassLoader thisClassLoader;

	/**
	 * The name (<code>org.apache.commons.logging.diagnostics.dest</code>) of
	 * the property used to enable internal commons-logging diagnostic output,
	 * in order to get information on what logging implementations are being
	 * discovered, what classloaders they are loaded through, etc.
	 * <p>
	 * If a system property of this name is set then the value is assumed to be
	 * the name of a file. The special strings STDOUT or STDERR (case-sensitive)
	 * indicate output to System.out and System.err respectively.
	 * <p>
	 * Diagnostic logging should be used only to debug problematic
	 * configurations and should not be set in normal production use.
	 */
	public static final String DIAGNOSTICS_DEST_PROPERTY = "diagnostics.dest";

	// ----------------------------------------------------------------------
	// Static initialiser block to perform initialisation at class load time.
	//
	// We can't do this in the class constructor, as there are many
	// static methods on this class that can be called before any
	// LogFactory instances are created, and they depend upon this
	// stuff having been set up.
	//
	// Note that this block must come after any variable declarations used
	// by any methods called from this block, as we want any static initialiser
	// associated with the variable to run first. If static initialisers for
	// variables run after this code, then (a) their value might be needed
	// by methods called from here, and (b) they might *override* any value
	// computed here!
	//
	// So the wisest thing to do is just to place this code at the very end
	// of the class file.
	// ----------------------------------------------------------------------
	static {
		
		// note: it's safe to call methods before initDiagnostics (though
		// diagnostic output gets discarded).
		thisClassLoader = getClassLoader(LogFactory.class);
		
		// In order to avoid confusion where multiple instances of JCL are
		// being used via different classloaders within the same app, we
		// ensure each logged message has a prefix of form
		// [LogFactory from classloader OID]
		//
		// Note that this prefix should be kept consistent with that
		// in LogFactoryImpl. However here we don't need to output info
		// about the actual *instance* of LogFactory, as all methods that
		// output diagnostics from this class are static.
		String classLoaderName;
		
		try {
			ClassLoader classLoader = thisClassLoader;
			
			if (thisClassLoader == null) {
				classLoaderName = "BOOTLOADER";
			} else {
				classLoaderName = objectId(classLoader);
			}
		} catch (SecurityException e) {
			classLoaderName = "UNKNOWN";
		}
		
		diagnosticPrefix = "[PropertyHelper from " + classLoaderName + "] ";
		diagnosticsStream = initDiagnostics();
		logClassLoaderEnvironment(PropertyHelper.class);
		
		if (isDiagnosticsEnabled()) {
			logDiagnostic("BOOTSTRAP COMPLETED");
		}
	}

	
	/**
	 * Determines whether the user wants internal diagnostic output. If so,
	 * returns an appropriate writer object. Users can enable diagnostic output
	 * by setting the system property named {@link #DIAGNOSTICS_DEST_PROPERTY}
	 * to a filename, or the special values STDOUT or STDERR.
	 */
	private static PrintStream initDiagnostics() {
		
		String dest;
		
		try {
			dest = getSystemProperty(DIAGNOSTICS_DEST_PROPERTY, "STDOUT");
			if (dest == null) {
				return null;
			}
		} catch (SecurityException ex) {
			// We must be running in some very secure environment.
			// We just have to assume output is not wanted..
			return null;
		}

		if (dest.equals("STDOUT")) {
			return System.out;
		} else if (dest.equals("STDERR")) {
			return System.err;
		} else {
			try {
				// open the file in append mode
				FileOutputStream fos = new FileOutputStream(dest, true);
				return new PrintStream(fos);
			} catch (IOException ex) {
				// We should report this to the user - but how?
				return null;
			}
		}
	}
	
	/**
	 * Generate useful diagnostics regarding the classloader tree for the
	 * specified class.
	 * <p>
	 * As an example, if the specified class was loaded via a webapp's
	 * classloader, then you may get the following output:
	 * 
	 * <pre>
	 * Class com.acme.Foo was loaded via classloader 11111
	 * ClassLoader tree: 11111 -> 22222 (SYSTEM) -> 33333 -> BOOT
	 * </pre>
	 * <p>
	 * This method returns immediately if isDiagnosticsEnabled() returns false.
	 * 
	 * @param clazz
	 *            is the class whose classloader + tree are to be output.
	 */
	private static void logClassLoaderEnvironment(Class clazz) {
		
		if (!isDiagnosticsEnabled()) {
			return;
		}

		try {
			// Deliberately use System.getProperty here instead of
			// getSystemProperty; if
			// the overall security policy for the calling application forbids
			// access to
			// these variables then we do not want to output them to the
			// diagnostic stream.
			logDiagnostic("[ENV] Extension directories (java.ext.dir): "
					+ System.getProperty("java.ext.dir"));
			logDiagnostic("[ENV] Application classpath (java.class.path): "
					+ System.getProperty("java.class.path"));
		} catch (SecurityException ex) {
			logDiagnostic("[ENV] Security setting prevent interrogation of system classpaths.");
		}

		String className = clazz.getName();
		ClassLoader classLoader;

		try {
			classLoader = getClassLoader(clazz);
		} catch (SecurityException ex) {
			// not much useful diagnostics we can print here!
			logDiagnostic("[ENV] Security forbids determining the classloader for "
					+ className);
			return;
		}

		logDiagnostic("[ENV] Class " + className
				+ " was loaded via classloader " + objectId(classLoader));
		logHierarchy("[ENV] Ancestry of classloader which loaded " + className
				+ " is ", classLoader);
	}

	/**
	 * Logs diagnostic messages about the given classloader and it's hierarchy.
	 * The prefix is prepended to the message and is intended to make it easier
	 * to understand the logs.
	 * 
	 * @param prefix
	 * @param classLoader
	 */
	private static void logHierarchy(String prefix, ClassLoader classLoader) {
		if (!isDiagnosticsEnabled()) {
			return;
		}
		ClassLoader systemClassLoader;
		if (classLoader != null) {
			final String classLoaderString = classLoader.toString();
			logDiagnostic(prefix + objectId(classLoader) + " == '"
					+ classLoaderString + "'");
		}

		try {
			systemClassLoader = ClassLoader.getSystemClassLoader();
		} catch (SecurityException ex) {
			logDiagnostic(prefix
					+ "Security forbids determining the system classloader.");
			return;
		}
		if (classLoader != null) {
			final StringBuffer buf = new StringBuffer(prefix
					+ "ClassLoader tree:");
			for (;;) {
				buf.append(objectId(classLoader));
				if (classLoader == systemClassLoader) {
					buf.append(" (SYSTEM) ");
				}

				try {
					classLoader = classLoader.getParent();
				} catch (SecurityException ex) {
					buf.append(" --> SECRET");
					break;
				}

				buf.append(" --> ");
				if (classLoader == null) {
					buf.append("BOOT");
					break;
				}
			}
			logDiagnostic(buf.toString());
		}
	}

	/**
	 * Read the specified system property, using an AccessController so that the
	 * property can be read if JCL has been granted the appropriate security
	 * rights even if the calling code has not.
	 * <p>
	 * Take care not to expose the value returned by this method to the calling
	 * application in any way; otherwise the calling app can use that info to
	 * access data that should not be available to it.
	 */
	private static String getSystemProperty(final String key, final String def)
			throws SecurityException {
		return (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return System.getProperty(key, def);
			}
		});
	}

	

	/**
	 * Returns a string that uniquely identifies the specified object, including
	 * its class.
	 * <p>
	 * The returned string is of form "classname@hashcode", ie is the same as
	 * the return value of the Object.toString() method, but works even when the
	 * specified object's class has overidden the toString method.
	 * 
	 * @param o
	 *            may be null.
	 * @return a string of form classname@hashcode, or "null" if param o is
	 *         null.
	 * @since 1.1
	 */
	public static String objectId(Object o) {
		if (o == null) {
			return "null";
		} else {
			return o.getClass().getName() + "@" + System.identityHashCode(o);
		}
	}

	// ------------------------------------------------------ Protected Methods

	/**
	 * Safely get access to the classloader for the specified class.
	 * <p>
	 * Theoretically, calling getClassLoader can throw a security exception, and
	 * so should be done under an AccessController in order to provide maximum
	 * flexibility. However in practice people don't appear to use security
	 * policies that forbid getClassLoader calls. So for the moment all code is
	 * written to call this method rather than Class.getClassLoader, so that we
	 * could put AccessController stuff in this method without any disruption
	 * later if we need to.
	 * <p>
	 * Even when using an AccessController, however, this method can still throw
	 * SecurityException. Commons-logging basically relies on the ability to
	 * access classloaders, ie a policy that forbids all classloader access will
	 * also prevent commons-logging from working: currently this method will
	 * throw an exception preventing the entire app from starting up. Maybe it
	 * would be good to detect this situation and just disable all
	 * commons-logging? Not high priority though - as stated above, security
	 * policies that prevent classloader access aren't common.
	 * <p>
	 * Note that returning an object fetched via an AccessController would
	 * technically be a security flaw anyway; untrusted code that has access to
	 * a trusted JCL library could use it to fetch the classloader for a class
	 * even when forbidden to do so directly.
	 * 
	 * @since 1.1
	 */
	protected static ClassLoader getClassLoader(Class clazz) {
		try {
			return clazz.getClassLoader();
		} catch (SecurityException ex) {
			if (isDiagnosticsEnabled()) {
				logDiagnostic("Unable to get classloader for class '" + clazz
						+ "' due to security restrictions - " + ex.getMessage());
			}
			throw ex;
		}
	}

	/**
	 * Write the specified message to the internal logging destination.
	 * <p>
	 * Note that this method is private; concrete subclasses of this class
	 * should not call it because the diagnosticPrefix string this method puts
	 * in front of all its messages is LogFactory@...., while subclasses should
	 * put SomeSubClass@...
	 * <p>
	 * Subclasses should instead compute their own prefix, then call
	 * logRawDiagnostic. Note that calling isDiagnosticsEnabled is fine for
	 * subclasses.
	 * <p>
	 * Note that it is safe to call this method before initDiagnostics is
	 * called; any output will just be ignored (as isDiagnosticsEnabled will
	 * return false).
	 * 
	 * @param msg
	 *            is the diagnostic message to be output.
	 */
	private static final void logDiagnostic(String msg) {
		if (diagnosticsStream != null) {
			diagnosticsStream.print(diagnosticPrefix);
			diagnosticsStream.println(msg);
			diagnosticsStream.flush();
		}
	}

	/**
	 * Indicates true if the user has enabled internal logging.
	 * <p>
	 * By the way, sorry for the incorrect grammar, but calling this method
	 * areDiagnosticsEnabled just isn't java beans style.
	 * 
	 * @return true if calls to logDiagnostic will have any effect.
	 * @since 1.1
	 */
	protected static boolean isDiagnosticsEnabled() {
		return diagnosticsStream != null;
	}

	/**
	 * Given a filename, return an enumeration of URLs pointing to all the
	 * occurrences of that filename in the classpath.
	 * <p>
	 * This is just like ClassLoader.getResources except that the operation is
	 * done under an AccessController so that this method will succeed when this
	 * jarfile is privileged but the caller is not. This method must therefore
	 * remain private to avoid security issues.
	 * <p>
	 * If no instances are found, an Enumeration is returned whose
	 * hasMoreElements method returns false (ie an "empty" enumeration). If
	 * resources could not be listed for some reason, null is returned.
	 */
	private static Enumeration getResources(final ClassLoader loader, final String name) {
		PrivilegedAction action = new PrivilegedAction() {
			public Object run() {
				try {
					if (loader != null) {
						return loader.getResources(name);
					} else {
						return ClassLoader.getSystemResources(name);
					}
				} catch (IOException e) {
					if (isDiagnosticsEnabled()) {
						logDiagnostic("Exception while trying to find configuration file "
								+ name + ":" + e.getMessage());
					}
					return null;
				} catch (NoSuchMethodError e) {
					// we must be running on a 1.1 JVM which doesn't support
					// ClassLoader.getSystemResources; just return null in
					// this case.
					return null;
				}
			}
		};
		
		Object result = AccessController.doPrivileged(action);
		return (Enumeration) result;
	}

	/**
	 * Given a URL that refers to a .properties file, load that file. This is
	 * done under an AccessController so that this method will succeed when this
	 * jarfile is privileged but the caller is not. This method must therefore
	 * remain private to avoid security issues.
	 * <p>
	 * {@code Null} is returned if the URL cannot be opened.
	 */
	private static Properties getProperties(final URL url) {
		PrivilegedAction action = new PrivilegedAction() {
			public Object run() {
				InputStream stream = null;
				try {
					// We must ensure that useCaches is set to false, as the
					// default behaviour of java is to cache file handles, and
					// this "locks" files, preventing hot-redeploy on windows.
					URLConnection connection = url.openConnection();
					connection.setUseCaches(false);
					stream = connection.getInputStream();
					if (stream != null) {
						Properties props = new Properties();
						props.load(stream);
						stream.close();
						stream = null;
						return props;
					}
				} catch (IOException e) {
					if (isDiagnosticsEnabled()) {
						logDiagnostic("Unable to read URL " + url);
					}
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							// ignore exception; this should not happen
							if (isDiagnosticsEnabled()) {
								logDiagnostic("Unable to close stream for URL "
										+ url);
							}
						}
					}
				}

				return null;
			}
		};
		return (Properties) AccessController.doPrivileged(action);
	}

	public static final Properties getPropertyFile(String fileName) {
		return getPropertyFile(null, fileName);
	}
	
	/**
	 * Locate a user-provided configuration file.
	 * <p>
	 * The classpath of the specified classLoader (usually the context
	 * classloader) is searched for properties files of the specified name. If
	 * none is found, null is returned. If more than one is found, then the file
	 * with the greatest value for its PRIORITY property is returned. If
	 * multiple files have the same PRIORITY value then the first in the
	 * classpath is returned.
	 * <p>
	 * This differs from the 1.0.x releases; those always use the first one
	 * found. However as the priority is a new field, this change is backwards
	 * compatible.
	 * <p>
	 * The purpose of the priority field is to allow a webserver administrator
	 * to override logging settings in all webapps by placing a
	 * commons-logging.properties file in a shared classpath location with a
	 * priority > 0; this overrides any commons-logging.properties files without
	 * priorities which are in the webapps. Webapps can also use explicit
	 * priorities to override a configuration file in the shared classpath if
	 * needed.
	 */
	private static final Properties getPropertyFile(ClassLoader classLoader, String fileName) {
		
		Properties props = null;
		double priority = 0.0;
		URL propsUrl = null;
		
		try {
			Enumeration urls = getResources(classLoader, fileName);

			if (urls == null) {
				return null;
			}

			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();

				Properties newProps = getProperties(url);
				if (newProps != null) {
					if (props == null) {
						propsUrl = url;
						props = newProps;
						String priorityStr = props.getProperty(PRIORITY_KEY);
						priority = 0.0;
						if (priorityStr != null) {
							priority = Double.parseDouble(priorityStr);
						}

						if (isDiagnosticsEnabled()) {
							logDiagnostic("[LOOKUP] Properties file found at '"
									+ url + "'" + " with priority " + priority);
						}
					} else {
						String newPriorityStr = newProps
								.getProperty(PRIORITY_KEY);
						double newPriority = 0.0;
						if (newPriorityStr != null) {
							newPriority = Double.parseDouble(newPriorityStr);
						}

						if (newPriority > priority) {
							if (isDiagnosticsEnabled()) {
								logDiagnostic("[LOOKUP] Properties file at '"
										+ url + "'" + " with priority "
										+ newPriority + " overrides file at '"
										+ propsUrl + "'" + " with priority "
										+ priority);
							}

							propsUrl = url;
							props = newProps;
							priority = newPriority;
						} else {
							if (isDiagnosticsEnabled()) {
								logDiagnostic("[LOOKUP] Properties file at '"
										+ url + "'" + " with priority "
										+ newPriority
										+ " does not override file at '"
										+ propsUrl + "'" + " with priority "
										+ priority);
							}
						}
					}

				}
			}
		} catch (SecurityException e) {
			if (isDiagnosticsEnabled()) {
				logDiagnostic("SecurityException thrown while trying to find/read config files.");
			}
		}

		if (isDiagnosticsEnabled()) {
			if (props == null) {
				logDiagnostic("[LOOKUP] No properties file of name '"
						+ fileName + "' found.");
			} else {
				logDiagnostic("[LOOKUP] Properties file of name '" + fileName
						+ "' found at '" + propsUrl + '"');
			}
		}

		return props;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println(System.getProperty("diagnostics.dest"));
		
		Properties p = PropertyHelper.getPropertyFile("init.properties");
		System.out.println(p);
	}

}