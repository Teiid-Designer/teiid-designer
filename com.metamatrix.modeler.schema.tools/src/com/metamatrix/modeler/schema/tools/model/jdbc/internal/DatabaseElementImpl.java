/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.jdbc.internal;

import com.metamatrix.modeler.schema.tools.model.jdbc.DatabaseElement;

public abstract class DatabaseElementImpl implements DatabaseElement {

	private String m_name;

	private String m_inputXPath;

	private String m_outputXPath;

	protected DatabaseElementImpl() {
		m_name = null;
		m_inputXPath = null;
		m_outputXPath = null;
	}

	protected DatabaseElementImpl(String name, String xpathIn, String xpathOut) {
		setName(name);
		setInputXPath(xpathIn);
		setOutputXPath(xpathOut);
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public String getInputXPath() {
		return m_inputXPath;
	}

	public void setInputXPath(String xpathIn) {
		m_inputXPath = xpathIn;
	}

	public String getOutputXPath() {
		return m_outputXPath;
	}

	public void setOutputXPath(String xpath) {
		m_outputXPath = xpath;
	}
}
