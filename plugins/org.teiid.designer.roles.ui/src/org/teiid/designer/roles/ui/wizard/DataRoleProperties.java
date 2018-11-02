/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard;

public class DataRoleProperties {
	private String dataRoleName;
    private String description;
    private boolean allowSystemTables;
    private boolean allowSystemRead;
    private boolean allowSystemExecute;
    
    private boolean anyAuthentication;
    private boolean allowCreateTempTables;
    private boolean grantAll;
    
	public DataRoleProperties() {
		super();
	}
    
    public String getDataRoleName() {
		return dataRoleName;
	}
	public void setDataRoleName(String dataRoleName) {
		this.dataRoleName = dataRoleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isAllowSystemTables() {
		return allowSystemTables;
	}
	public void setAllowSystemTables(boolean allowSystemTables) {
		this.allowSystemTables = allowSystemTables;
	}
	public boolean isAllowSystemRead() {
		return allowSystemRead;
	}
	public void setAllowSystemRead(boolean allowSystemRead) {
		this.allowSystemRead = allowSystemRead;
	}
	public boolean isAllowSystemExecute() {
		return allowSystemExecute;
	}
	public void setAllowSystemExecute(boolean allowSystemExecute) {
		this.allowSystemExecute = allowSystemExecute;
	}
	public boolean isAnyAuthentication() {
		return anyAuthentication;
	}
	public void setAnyAuthentication(boolean anyAuthentication) {
		this.anyAuthentication = anyAuthentication;
	}
	public boolean isAllowCreateTempTables() {
		return allowCreateTempTables;
	}
	public void setAllowCreateTempTables(boolean allowCreateTempTables) {
		this.allowCreateTempTables = allowCreateTempTables;
	}
	public boolean isGrantAll() {
		return grantAll;
	}
	public void setGrantAll(boolean grantAll) {
		this.grantAll = grantAll;
	}

}
