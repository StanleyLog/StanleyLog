package com.stanleylog.java.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileInputSteamExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("c:\\test.txt");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(1);
			fos.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
