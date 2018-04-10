package com.stanleylog.java.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited

public @interface MethodAnnotationExample {
	public enum Name {I, II, III}; 
	
	public Name getName() default Name.I;
}
