/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlFileContentHandler extends DefaultHandler implements ContentHandler
{


    private XmlElement rootElement;

    private XmlElement parentElement;

    public XmlFileContentHandler()
    {
        super();
    }

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if( rootElement ==  null ) {
			rootElement = new XmlElement(qName);
			parentElement = rootElement;
		} else {
			XmlElement newElement = new XmlElement(qName);
			parentElement.addChildElement(newElement);
			parentElement = newElement;
		}
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		parentElement = parentElement.getParent();
		
		super.endElement(uri, localName, qName);
	}
    
    public XmlElement getRootElement()
    {
        return rootElement;
    }
    
}