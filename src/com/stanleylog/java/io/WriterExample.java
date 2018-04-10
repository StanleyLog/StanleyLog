package com.stanleylog.java.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WriterExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("c:\\test.txt");
		try {
			Writer w = new FileWriter(file);
			w.write("aaaaabbbb??");
			w.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
