package com.stanleylog.java.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// System.out.println("We will not use 'Hello Word!'");
		// String b = "hello";
		// System.out.println(b.charAt(2));
		//
		// String input = JOptionPane.showInputDialog("how old are you?");
		// int age = Integer.parseInt(input);
		// int[] a = new int[] { age };
		// for (int i = 0; i < a.length; i++) {
		// System.out.println(a[i]);
		// }
		//
		// System.out.println();
		// System.out.println(new Date());

		List<String> c = new ArrayList<String>();
		c.add("sdf");
		c.add("fff");
		// c.remove(1);
		for (String a : c) {
			System.out.println(a);
		}

		Map<String, String> d = new HashMap<String, String>();
		d.put("a", "b");

		System.out.println(d);
	}
}
