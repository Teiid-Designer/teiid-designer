/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

public class XmlAttribute {
	private String name;
	private String value;
	private XmlElement element;

	public XmlAttribute(String name)
	{
		super();
		this.name = name;
	}

	public XmlAttribute(String name, String value, XmlElement element)
	{
		super();
		this.name = name;
		this.value = value;
		this.element = element;
	}

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}
	
	public XmlElement getElement() {
		return this.element;
	}
}
