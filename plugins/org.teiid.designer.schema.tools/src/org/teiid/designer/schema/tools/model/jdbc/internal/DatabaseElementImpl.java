/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.jdbc.internal;

import org.teiid.designer.schema.tools.model.jdbc.DatabaseElement;

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

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	@Override
	public String getInputXPath() {
		return m_inputXPath;
	}

	@Override
	public void setInputXPath(String xpathIn) {
		m_inputXPath = xpathIn;
	}

	@Override
	public String getOutputXPath() {
		return m_outputXPath;
	}

	@Override
	public void setOutputXPath(String xpath) {
		m_outputXPath = xpath;
	}
}
