/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v8;

import java.util.Properties;

import org.teiid.designer.runtime.spi.ITeiidDataSource;

public class CommonDataSource extends AdminUtil implements ITeiidDataSource {

	private ITeiidDataSource tds;
	private String jndiName;
	private String poolName;
	private String resAdapterID;
	
	private DataSourceType type;
	
	public CommonDataSource(ITeiidDataSource tds, DataSourceType type, String resAdapterID) {
		super();
		this.jndiName = tds.getPropertyValue("jndi-name");
		this.poolName = tds.getName();
		this.type = type;
		this.resAdapterID = resAdapterID;
		this.tds = tds;
	}
	
	public boolean isDataSource() {
		return this.type == DataSourceType.DATA_SOURCE;
	}
	
	public boolean isXADataSource() {
		return this.type == DataSourceType.XA_DATA_SOURCE;
	}
	
	public boolean isResourceAdapter() {
		return this.type == DataSourceType.RESOURCE_ADAPTER;
	}
	
	public ITeiidDataSource getTds() {
		return tds;
	}

	public String getJndiName() {
		return jndiName;
	}

	public String getPoolName() {
		return poolName;
	}

	public DataSourceType getDSType() {
		return type;
	}
	
	public String getResourceAdapterID() {
		return resAdapterID;
	}

	@Override
	public String getDisplayName() {
		return tds.getDisplayName();
	}

	@Override
	public String getName() {
		return tds.getName();
	}

	@Override
	public String getType() {
		return tds.getType();
	}

	@Override
	public Properties getProperties() {
		return tds.getProperties();
	}

	@Override
	public String getPropertyValue(String name) {
		return tds.getPropertyValue(name);
	}

	@Override
	public void setProfileName(String name) {
		tds.setProfileName(name);
	}

	@Override
	public String getProfileName() {
		return tds.getProfileName();
	}

	@Override
	public boolean isPreview() {
		return tds.isPreview();
	}

	@Override
	public void setPreview(boolean isPreview) {
		tds.setPreview(isPreview);
	}

}
