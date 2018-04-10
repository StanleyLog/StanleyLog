package com.stanleylog.java.lang.clazz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassTest {

	private static Log log = LogFactory.getLog(ClassTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ClassExample foo = new ClassExample();
//		System.out.println(foo.getClass().getClassLoader().getClass().getName());
		
		log.info(foo.getClass().getClassLoader().getClass().getName());
	}

}
