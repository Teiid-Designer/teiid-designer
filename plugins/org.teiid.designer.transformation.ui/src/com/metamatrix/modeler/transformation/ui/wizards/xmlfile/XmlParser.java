/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class XmlParser {
	private ErrorHandler errorHandler;
	private ContentHandler contentHandler;

	public void setErrorHandler(ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	public void setContentHandler(ContentHandler contentHandler)
	{
		this.contentHandler = contentHandler;
	}

//	public static void main(String[] args)
//	{
//		try
//		{
//			XmlParser parser = new XmlParser();
//			parser.setErrorHandler(new XmlValidationErrorHandler());
//			parser.doParse(new File(args[0]));
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			System.exit(-1);
//		}
//	}

	public static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation"; //$NON-NLS-1$

	/**
	 * Does DTD-based validation on File
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void doParse(File xmlFilePath) throws RuntimeException, IOException, SAXException
	{

		InputSource inputSource = null;
		try
		{
			inputSource = new InputSource(new FileReader(xmlFilePath));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		doParse(inputSource);

	}

	/**
	 * Does DTD-based validation on text
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void doParse(String xmlText) throws RuntimeException, IOException, SAXException
	{

		InputSource inputSource = new InputSource(new StringReader(xmlText));
		doParse(inputSource);

	}

	/**
	 * Does DTD-based validation on inputSource
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void doParse(InputSource inputSource) throws RuntimeException, IOException, SAXException
	{
		XMLReader reader = new SAXParser();
		reader.setErrorHandler(errorHandler);
		reader.setContentHandler(contentHandler);
		reader.setFeature(VALIDATION_FEATURE, true);
		reader.parse(inputSource);
	}
}
