package com.stanleylog.java.lang.annotation;

import java.lang.annotation.Annotation;

import com.stanleylog.java.lang.annotation.MethodAnnotationExample.Name;
import com.stanleylog.java.lang.annotation.TypeAnnotationExample.Type;

@TypeAnnotationExample(classType = Type.THREE)
public class AnnotationTest {

	
	@MethodAnnotationExample(getName=Name.III)
	public void test() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnnotationTest test = new AnnotationTest();
		for(Annotation i : test.getClass().getAnnotations()){
			System.out.println(i.toString());
		}
		
		try {
			for(Annotation i : test.getClass().getMethod("test").getAnnotations()){
				System.out.println(i.toString());
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
