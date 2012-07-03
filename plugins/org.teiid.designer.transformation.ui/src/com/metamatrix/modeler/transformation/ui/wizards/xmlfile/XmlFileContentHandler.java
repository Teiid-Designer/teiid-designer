/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlFileContentHandler extends DefaultHandler implements ContentHandler
{


    private XmlElement rootElement;

    private XmlElement parentElement;
    
    private Map<String, String> namespaceMap;

    public XmlFileContentHandler()
    {
        super();
        
        this.namespaceMap = new HashMap<String, String>();
    }

	@Override
	public void startElement(String uri, String lName, String qName, Attributes attributes) throws SAXException {
		if( rootElement ==  null ) {
			rootElement = new XmlElement(qName);
	        if (attributes != null)
	        {
	            int attributeLength = attributes.getLength();
	            for (int i = 0; i < attributeLength; i++)
	            {
	                String value = attributes.getValue(i);
	                String localName = attributes.getLocalName(i);

	                rootElement.addChildAttribute(new XmlAttribute(localName, value, rootElement));
	            }
	        }
			parentElement = rootElement;
		} else {
			XmlElement newElement = new XmlElement(qName);
	        if (attributes != null)
	        {
	            int attributeLength = attributes.getLength();
	            for (int i = 0; i < attributeLength; i++)
	            {
	                String value = attributes.getValue(i);
	                String localName = attributes.getLocalName(i);

	                newElement.addChildAttribute(new XmlAttribute(localName, value, newElement));
	            }
	        }
			parentElement.addChildElement(newElement);
			parentElement = newElement;
		}
		super.startElement(uri, lName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		parentElement = parentElement.getParent();
		
		super.endElement(uri, localName, qName);
	}
	
	
    
    @Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if( ! this.namespaceMap.containsKey(prefix) ) {
			this.namespaceMap.put(prefix, uri);
		}
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// DO NOTHING
	}

	public XmlElement getRootElement()
    {
        return rootElement;
    }
	
	public Map<String, String> getNamespaceMap() {
		return this.namespaceMap;
	}
    
}