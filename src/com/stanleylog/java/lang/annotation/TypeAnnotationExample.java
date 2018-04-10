package com.stanleylog.java.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//@Retention: ����ע��ı�������
//@Retention(RetentionPolicy.SOURCE)   //ע���������Դ���У���class�ֽ����ļ��в�����
//@Retention(RetentionPolicy.CLASS)     // Ĭ�ϵı������ԣ�ע�����class�ֽ����ļ��д��ڣ�������ʱ�޷���ã�
//@Retention(RetentionPolicy.RUNTIME)  // ע�����class�ֽ����ļ��д��ڣ�������ʱ����ͨ�������ȡ��
//
//@Target������ע�������Ŀ��    
//@Target(ElementType.TYPE)   //�ӿڡ��ࡢö�١�ע��
//@Target(ElementType.FIELD) //�ֶΡ�ö�ٵĳ���
//@Target(ElementType.METHOD) //����
//@Target(ElementType.PARAMETER) //��������
//@Target(ElementType.CONSTRUCTOR)  //���캯��
//@Target(ElementType.LOCAL_VARIABLE)//�ֲ�����
//@Target(ElementType.ANNOTATION_TYPE)//ע��
//@Target(ElementType.PACKAGE) ///��   
//
//@Document��˵����ע�⽫��������javadoc��    
//
//@Inherited��˵��������Լ̳и����еĸ�ע��


@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
@Documented 
@Inherited
public @interface TypeAnnotationExample {
	public enum Type {ONE, TWO, THREE}
	public Type classType() default Type.ONE;
}
