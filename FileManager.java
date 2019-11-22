package com.pro.managers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.Part;

public class FileManager extends File {
	
	private static final long serialVersionUID = 1L;
	
	public static final int EOF = -1;
	public static final int MAX_BUFFER_SIZE = 10*1024; // 10KO
	public static final int BUFFER_ROOM = 10;
	
	private BufferedInputStream in =  null;
	private BufferedOutputStream out = null;
	
	public FileManager(String parent) {
		super(parent);
	}
	
	public FileManager(String parent, String child) {
		super(parent, child);
	}
	
	// TEXT READ AND WRITE
	// WRITE TEXT DATA TO THIS FILE
	public void write(String data) throws Exception {
		try {
			out = new BufferedOutputStream(new FileOutputStream(this), MAX_BUFFER_SIZE);
			byte[] data_bytes = data.getBytes();
			out.write(data_bytes, 0, data_bytes.length);
		} finally {
			if(out != null) out.close();
		} 
	}
	
	// READ TEXT DATA FROM THIS FILE
	public String read() throws Exception {
		String ret = "";
		try {
			in = new BufferedInputStream(new FileInputStream(this), MAX_BUFFER_SIZE);
			byte[] data_bytes = new byte[BUFFER_ROOM];
			while(in.read(data_bytes) != EOF) {
				for(byte b : data_bytes) ret += (char)b;
				data_bytes = new byte[BUFFER_ROOM];
			}
		} finally {
			if(in != null) in.close();
		}
		return ret.trim();
	}
	
	// APPEND TEXT DATA TO THIS FILE
	public void append(String data) throws Exception {
		try {
			write(read() + data);
		} catch (FileNotFoundException e) {
			// IF THE FILE IS NOT EXIST THEN CREATE A VOID FILE AND APEND DATA AGAIN
			write("");
			append(data);
		}
	}
	
	// DATA COPYING CONTENTS
	// COPY DATA FROM A PARAM SOURCE TO THIS FILE
	public void copy(InputStream is) throws Exception {
		try {
			in = new BufferedInputStream(is, MAX_BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(this), MAX_BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_ROOM];
			int length;
			while((length = in.read(buffer)) != EOF) {
				out.write(buffer, 0, length);
				buffer = new byte[BUFFER_ROOM];
			}
		} finally {
			if(in != null) in.close();
			if(out != null) out.close();
		}
	}
	
	// COPY DATA FROM THIS FILE TO A PARAM SOURCE
	public void copy(OutputStream os) throws Exception {
		try {
			in = new BufferedInputStream(new FileInputStream(this), MAX_BUFFER_SIZE);
			out = new BufferedOutputStream(os, MAX_BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_ROOM];
			int length;
			while((length = in.read(buffer)) != EOF) {
				out.write(buffer, 0, length);
				buffer = new byte[BUFFER_ROOM];
			}
		} finally {
			if(in != null) in.close();
			if(out != null) out.close();
		}
	}
	
	// GET THIS FILE INPUT STREAM
	public InputStream getIn() throws Exception {
		return new FileInputStream(this);
	}
	
	// GET THIS FILE OUTPUT STREAM
	public OutputStream getOut() throws Exception {
		return new FileOutputStream(this);
	}
	
	// COPY DATA FROM A PARAM SOURCE TO ANOTHER PARAM SOURCE 
	public static void copy(InputStream is, OutputStream os) throws Exception {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(is, MAX_BUFFER_SIZE);
			out = new BufferedOutputStream(os, MAX_BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_ROOM];
			int length;
			while((length = in.read(buffer)) != EOF) {
				out.write(buffer, 0, length);
				buffer = new byte[BUFFER_ROOM];
			}
		} finally {
			if(in != null) in.close();
			if(out != null) out.close();
		}
	}
	
	// GET THE FILENAME FROM PART HTTP REQUEST
	public static String getPartFilename(Part p) {
		String ret = null;
		for(String field : p.getHeader("content-disposition").split(";")) {
			if(field.trim().startsWith("filename")) {
				ret = field.substring(field.indexOf("=") + 1).replace('"', ' ');
				ret = ret.substring(ret.lastIndexOf("/") + 1).substring(ret.lastIndexOf("\\") + 1).trim();
			}
		}
		return ret;
	}
	
	// GET THE FILE EXTENSION
	public static String getExtension(String filename) {
		String ret = null;
		if(!filename.equals(null) && !filename.isEmpty()) {
			ret = filename.substring(filename.lastIndexOf(".") + 1).trim();
		}
		return ret;
	}
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		FileManager f = new FileManager("C:\\Users\\hamza\\Desktop\\My Projects\\Programme 4.2 (JEE)\\pro\\WebContent\\files\\","273547.jpg");
		FileManager f2 = new FileManager("copy2.jpg");
		FileManager f3 = new FileManager("test3.bin");
		try {
			//FileManager.copy(f.getIn(), f2.getOut());
			//f3.write("hello there");
			f3.append(" im hamza");
			System.out.println(f3.read());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
