package com.example.ediblebluechese;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONArray;

public class Utility {
	private static final int MAX_SIZE = 1024 * 1024 * 10;
	
	public static byte[] FISToByteArray( FileInputStream fis ){
		BufferedInputStream in = new BufferedInputStream( fis );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int size = 0;
		byte[] temp = new byte[MAX_SIZE];
		try {
			while( ( size = in.read( temp ) ) != -1 ) {
				out.write( temp, 0, size );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	
	public static byte[] objectToBytArray( Object ob ){
		 return ((ob.toString()).getBytes());
	}
}
