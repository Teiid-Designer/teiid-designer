/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author vanhalbert
 *
 */
public class Util {

	public static Class<?> getJavaClass(String className,
			ClassLoader classLoader) {

		// is there a better way to get array class from string name?

		if (className == null) {
			throw new NullPointerException("Null class name");
		}

		// ClassLoader classLoader =
		// classLoaderManager.getClassLoader(className.replace('.', '/'));

		// use custom logic on failure only, assuming primitives and arrays are
		// not that common
		try {
			return Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			if (!className.endsWith("[]")) {
				if ("byte".equals(className)) {
					return Byte.TYPE;
				} else if ("int".equals(className)) {
					return Integer.TYPE;
				} else if ("short".equals(className)) {
					return Short.TYPE;
				} else if ("char".equals(className)) {
					return Character.TYPE;
				} else if ("double".equals(className)) {
					return Double.TYPE;
				} else if ("long".equals(className)) {
					return Long.TYPE;
				} else if ("float".equals(className)) {
					return Float.TYPE;
				} else if ("boolean".equals(className)) {
					return Boolean.TYPE;
				} else if ("void".equals(className)) {
					return Void.TYPE;
				}
				// try inner class often specified with "." instead of $
				else {
					int dot = className.lastIndexOf('.');
					if (dot > 0 && dot + 1 < className.length()) {
						className = className.substring(0, dot) + "$"
								+ className.substring(dot + 1);
						try {
							return Class.forName(className, true, classLoader);
						} catch (ClassNotFoundException nestedE) {
							// ignore, throw the original exception...
						}
					}
				}

				throw new RuntimeException(e);
				// "Invalid class: %s", e, className);
			}

			if (className.length() < 3) {
				throw new IllegalArgumentException("Invalid class name: "
						+ className);
			}

			// TODO: support for multi-dim arrays
			className = className.substring(0, className.length() - 2);

			if ("byte".equals(className)) {
				return byte[].class;
			} else if ("int".equals(className)) {
				return int[].class;
			} else if ("long".equals(className)) {
				return long[].class;
			} else if ("short".equals(className)) {
				return short[].class;
			} else if ("char".equals(className)) {
				return char[].class;
			} else if ("double".equals(className)) {
				return double[].class;
			} else if ("float".equals(className)) {
				return float[].class;
			} else if ("boolean".equals(className)) {
				return boolean[].class;
			}

			try {
				return Class.forName("[L" + className + ";", true, classLoader);
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException(e1);
				// "Invalid class: %s", e1, className);
			}
		}
	}

	/**
	 * Creates an XMLReader with default feature set.
	 * 
	 * @return XMLReader
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static XMLReader createXmlReader() throws SAXException,
			ParserConfigurationException {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		// Create a JAXP SAXParser
		SAXParser saxParser = spf.newSAXParser();

		// Get the encapsulated SAX XMLReader
		XMLReader reader = saxParser.getXMLReader();

		// set default features
		reader.setFeature("http://xml.org/sax/features/namespaces", true);

		return reader;
	}

	public static URL getURL(String relativePath) {
		try {
			return new URL(relativePath);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	  *  converts uppercase, underscore separated column name to title case
	  *  example:  ORDER_ID becomes OrderID
	  *  @param inputString the string to be converted
	  *  @return  the converted string
	  */
	 static public String columnNameToMemberName (String inputString) {
	
	    //Start by converting entire string to lower case
	    char input[] = (inputString.toLowerCase()).toCharArray();
	    StringBuffer output = new StringBuffer(inputString.length());
	
	    for (int x = 0; x < inputString.length(); x++) {
	      if (x != 0 && input[x] != '_') {
	        output.append(input[x]);
	      } else { 
	        if (input[x] == '_') {
	          x++;  //skip over it
	        }
	        //The first character and any character following the _ 
	        //need to be capitalized  
	        output.append(Character.toUpperCase(input[x]));
	      }
	    } //for
	
	    return output.toString();
	
	  } //columnNameToMemberName()
	 
	 /**
	 * Return the tokens in a string in a list. This is particularly
	 * helpful if the tokens need to be processed in reverse order. In that case,
	 * a list iterator can be acquired from the list for reverse order traversal.
	 *
	 * @param str String to be tokenized
	 * @param delimiter Characters which are delimit tokens
	 * @return List of string tokens contained in the tokenized string
	 */
	public static List<String> getTokens(String str, String delimiter) {
		ArrayList<String> l = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer(str, delimiter);
		while(tokens.hasMoreTokens()) {
			l.add(tokens.nextToken());
		}
		return l;
    }
	
    /**
     * Returns a <code>File</code> array that will contain all the files that
     * exist in the directory that have the specified extension.
     * @return File[] of files having a certain extension
     */
    public static File[] findAllFilesInDirectoryHavingExtension(String dir, final String extension) {

      // Find all files in that directory that end in XML and attempt to
      // load them into the runtime metadata database.
      File modelsDirFile = new File(dir);
      FileFilter fileFilter = new FileFilter() {
          public boolean accept(File file) {
              if(file.isDirectory()) {
                  return false;
              }


              String fileName = file.getName();

              if (fileName==null || fileName.length()==0) {
                  return false;
              }

              // here we check to see if the file is an .xml file...
              int index = fileName.lastIndexOf("."); //$NON-NLS-1$

              if (index<0 || index==fileName.length()) {
                  return false;
              }

              if (fileName.substring(index, fileName.length()).equalsIgnoreCase(extension)) {
                  return true;
              }
              return false;
          }
      };

      File[] modelFiles = modelsDirFile.listFiles(fileFilter);

      return modelFiles;

  }
}
