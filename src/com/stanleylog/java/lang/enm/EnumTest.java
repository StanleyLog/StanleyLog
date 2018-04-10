package com.stanleylog.java.lang.enm;

/**
 * 
 * 
 * @author Zhiguang Sun
 * 
 */
public class EnumTest {

	/**
	 * 打印枚举中的名称以及顺序值。
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		for (EnumExample i : EnumExample.values()) {
			System.out.println(i + ": " + i.ordinal());
		}

	}

}
