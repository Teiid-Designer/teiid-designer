/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.Position;

public class XmlElement {
	private final String SEPARATOR = "/";  //$NON-NLS-1$
	private List<XmlElement> elementChildren = new ArrayList<XmlElement>();
	private List<XmlAttribute> attributeChildren = new ArrayList<XmlAttribute>();

	private String name;
	private XmlElement parent;
	private Position position;

	public XmlElement(String name)
	{
		super();
		this.name = name;
	}

	public Object[] getChildrenDTDElements()
	{
		return elementChildren.toArray(new Object[0]);
	}

	public XmlElement addChildElement(XmlElement element)
	{
		elementChildren.add(element);
		element.setParent(this);
		return this;
	}

	public void setParent(XmlElement element)
	{
		this.parent = element;
	}

	public XmlElement getParent()
	{
		return parent;
	}

	public XmlElement addChildAttribute(XmlAttribute attribute)
	{
		attributeChildren.add(attribute);
		return this;
	}

	public String getName()
	{
		return name;
	}
	
	public String getAttributeValue(String localName)
	{
		for (Iterator iter = attributeChildren.iterator(); iter.hasNext();)
		{
			XmlAttribute attribute = (XmlAttribute) iter.next();
			if (attribute.getName().equals(localName)) return attribute.getValue();
		}
		return null;
	}
	
	public Object[] getAttributes() {
		return attributeChildren.toArray(new Object[0]);
	}

	public void clear()
	{
		elementChildren.clear();
		attributeChildren.clear();
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	public Position getPosition()
	{
		return position;
	}
	
	public String getFullPath() {
		String path = SEPARATOR + this.getName();
		XmlElement thisElement = this;
		
		while( thisElement.getParent() != null ) {
			path = SEPARATOR + thisElement.getParent().getName() + path;
			thisElement = thisElement.getParent();
		}
		
		return path;
	}
}
