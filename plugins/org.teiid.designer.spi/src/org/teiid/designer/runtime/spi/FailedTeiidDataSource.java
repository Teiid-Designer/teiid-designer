/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.util.Properties;

/**
 *
 */
public class FailedTeiidDataSource implements ITeiidDataSource {

	String modelName;
	String jndiName;
	int reasonCode;
	
	/**
	 * 
	 */
	public FailedTeiidDataSource(String modelName, String jndiName, int reasonCode) {
		this.modelName = modelName;
		this.jndiName = jndiName;
		this.reasonCode = reasonCode;
	}
	
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return this.modelName;
	}

	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return this.jndiName;
	}

	/**
	 * @return the reasonCode
	 */
	public int getReasonCode() {
		return this.reasonCode;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#getProperties()
	 */
	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#getPropertyValue(java.lang.String)
	 */
	@Override
	public String getPropertyValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#setProfileName(java.lang.String)
	 */
	@Override
	public void setProfileName(String name) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#getProfileName()
	 */
	@Override
	public String getProfileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#isPreview()
	 */
	@Override
	public boolean isPreview() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.teiid.designer.runtime.spi.ITeiidDataSource#setPreview(boolean)
	 */
	@Override
	public void setPreview(boolean isPreview) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPoolName() {
		// TODO Auto-generated method stub
		return null;
	}

}
