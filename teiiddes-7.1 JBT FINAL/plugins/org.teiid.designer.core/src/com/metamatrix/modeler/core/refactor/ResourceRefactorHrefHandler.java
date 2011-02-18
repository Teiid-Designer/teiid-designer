/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles converting/replacing HREF Model path segments.
 * 
 * This is needed because refactor "move" operations will result in <code>ModelImport</code> locations that will not
 * match the href prefixes within the moved model. Upgrades from EMF 2.4 to 2.5 have made using EMF to resolve these 
 * differences, is not reliable.
 * 
 * This method allows re-setting these HREF values
 *
 */
public class ResourceRefactorHrefHandler  {

	protected FileReader fileReader = null;
	protected StringBuilder tokenNameBuffer = new StringBuilder();
	protected String tokenValue = null;
	protected int tokenValueIndex = 0;
	protected int hrefCount = 0;
	protected File outFile;
	protected BufferedWriter out;
	private Map<String, String> changedPathsMap;

	/**
	 * Primary constructor
	 * 
	 * @param fileReader the file reader
	 * @param changedPathsMap map of changed relative paths for hrefs. Intended to be a map of model import locations 
	 * prior to and after the move operation.
	 * @param writeFile the file to be written to
	 */
	public ResourceRefactorHrefHandler(FileReader fileReader, Map<String, String> changedPathsMap, File writeFile) {
		this.fileReader = fileReader;
		this.changedPathsMap = new HashMap<String, String>(changedPathsMap);
		this.outFile = writeFile;
	}
	
	private void writeData(int data) throws IOException {
		if( out != null ) {
			if( data != -1 ) {
//				System.out.print((char)data);
				out.append((char)data);
			}
		}
	}
	
	public void doReadAll() throws IOException {
		if( outFile != null ) {
			out = new BufferedWriter(new FileWriter(outFile));
		}
		
	    int data = this.read();
	    writeData(data);
	    
	    while(data != -1){
	        data = this.read();
	        writeData(data);
	    }
	    
	    try {
	    	fileReader.close();
        } catch (IOException ignored) {
        }
	}
	
	public void doWriteAll() {
		if( out != null ) {
			try {
                out.close();
            } catch (IOException ignored) {
            }
		}
	}

	private int read() throws IOException {
		if (this.tokenValue != null) {
			if (this.tokenValueIndex < this.tokenValue.length()) {
				return this.tokenValue.charAt(this.tokenValueIndex++);
			}
			if (this.tokenValueIndex == this.tokenValue.length()) {
				this.tokenValue = null;
				this.tokenValueIndex = 0;
			}
		}
		// Looking for replacing a segment of the following string type:
		// href="BottomFolder/RelModel_999999.xmi#mmuuid/ff8d1186-3141-4b33-8e2f-f4dc5e771b52"
		
		// Get next character
		int data = this.fileReader.read();
		
		if( hrefCount != 6 ) {
			if (hrefCount == 0 && data == 'h') {
				hrefCount++;
			} else if (hrefCount == 1) {
				if( data == 'r') {
					hrefCount++;
				} else hrefCount = 0;
			} else if (hrefCount == 2) {
				if( data == 'e') {
					hrefCount++;
				} else hrefCount = 0;
			} else if (hrefCount == 3) { 
				if( data == 'f') {
					hrefCount++;
				} else hrefCount = 0;
			} else if (hrefCount == 4) { 
				if( data == '=') {
					hrefCount++;
				} else hrefCount = 0;
			} else if (hrefCount == 5) {
				if( data == '"') {
					hrefCount++;
				} else hrefCount = 0;
			}
			
			return data;
		}
		
		hrefCount = 0;
		this.tokenNameBuffer.delete(0, this.tokenNameBuffer.length());

		while (data != '#') {
			this.tokenNameBuffer.append((char) data);
			data = this.fileReader.read();
		}

		this.tokenValue = this.changedPathsMap.get(this.tokenNameBuffer.toString());

		// If map doesn't contain a mapped token, then replace with what was read
		if (this.tokenValue == null) {
			this.tokenValue = this.tokenNameBuffer.toString();
		}
		tokenValue = tokenValue + '#';
		return this.tokenValue.charAt(this.tokenValueIndex++);

	}
}