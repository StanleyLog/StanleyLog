package com.stanleylog.java.lang.enm;

/**
 * 
 * 
 * @author Zhiguang Sun
 * 
 */
public class EnumTest {

	/**
	 * ��ӡö���е������Լ�˳��ֵ��
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		for (EnumExample i : EnumExample.values()) {
			System.out.println(i + ": " + i.ordinal());
		}

	}

}
