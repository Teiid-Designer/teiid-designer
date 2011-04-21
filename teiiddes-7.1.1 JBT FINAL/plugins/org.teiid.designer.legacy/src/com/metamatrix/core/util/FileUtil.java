package com.metamatrix.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.util.StringUtil;

public class FileUtil {
    private File file;
    
    public FileUtil(String fileName) {
        this.file = new File(fileName);
    }
    
    public FileUtil(File file) {
        this.file = file;
    }
    
    public String readSafe() throws FileNotFoundException {
        String result;
        FileReader reader = null;
        try {
            reader = new FileReader(this.file);
            result = read(reader);
        } finally {
        	if (reader != null) {
	            try {
	                reader.close();
	            } catch (Exception e) {                
	            }
        	}
        }
        
        return result;
    }
    
    
    public static String read(Reader reader) {
        StringWriter writer = new StringWriter();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();     
                writer.write(line);  
                writer.write(StringUtil.LINE_SEPARATOR);
            }
        } catch (IOException e) {
            throw new TeiidRuntimeException(e);
        } finally {
        	if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (Exception e) {                
	            }
        	}
        }
        return writer.toString();
    }
}
