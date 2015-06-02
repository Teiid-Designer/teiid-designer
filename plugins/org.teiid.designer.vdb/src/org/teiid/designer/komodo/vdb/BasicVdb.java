/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;

/**
 * @author blafond
 *
 */
public abstract class BasicVdb extends VdbObject implements Vdb {
	boolean changed;
	
	String vdbFilePath;
	
	// VDBs have the following possible structure
	/* ATTRIBUTES
	 	<xs:attribute name="name" type="xs:string" use="required"/>
	 	<xs:attribute name="version" type="xs:int" use="required"/>
	 */
	int version;
	
	/* Elements:
		<xs:element name="description" type="xs:string" minOccurs="0"/>
	    <xs:element name="connection-type" type="xs:string" minOccurs="0" default="BY_VERSION"/>
		<xs:element name="property" type="property" minOccurs="0" maxOccurs="unbounded"/>
	*/

	String connectionType;
	// known properties
	/*
		<property name="preview" value="false"/>
	    <property name="query-timeout" value="23000"/>
	    <property name="allowed-languages" value="ddddd, sssss"/>
	    <property name="security-domain" value="ssdfsdf"/>
	    <property name="gss-pattern" value="aaaaadddd"/>
	    <property name="password-pattern" value="asdfasdfasdf*.xyz"/>
	    <property name="authentication-type" value="none"/>
	    <property name="validationDateTime" value="Mon Mar 16 09:16:10 CDT 2015"/>
	    <property name="validationVersion" value="8.7.0"/>
	 */
	boolean preview;
	int queryTimeout;
	AllowedLanguages allowedLanguages;
	String securityDomain;
	String gssPattern;
	String passwordPattern;
	String authenticationType;
	String validatDataTime;
	String validationVersion;
	boolean autoGenerateRESTWar;
	
	Map<String, VdbImport> importVdbs;
	Map<String, Model> models;
	Map<String, Translator> translatorOverrides;
	Map<String, DataRole> dataRoles;
	Map<String, Entry> entries;
	
    TeiidType IDENTIFIER = TeiidType.VDB;

	/**
	 * 
	 */
	public BasicVdb() {
		super();
		name = StringConstants.EMPTY_STRING;
		version = 1;
		allowedLanguages = new AllowedLanguages();
		importVdbs = new HashMap<String, VdbImport>();
		models = new HashMap<String, Model>();
		translatorOverrides = new HashMap<String, Translator>();
		dataRoles = new HashMap<String, DataRole>();
		entries = new HashMap<String, Entry>();
		changed = false;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addDataRole(org.teiid.designer.komodo.vdb.DataRole)
	 */
	@Override
	public void addDataRole(DataRole dataRole) {
		 DataRole existing = dataRoles.put(dataRole.getName(), dataRole);
		 setChanged(existing != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public Entry addEntry(String entryName, String entryPath) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 *  (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addImport(java.lang.String, boolean, int)
	 */
	public void addImport(String vdbName, boolean importDataPolicies, int version) {
		 VdbImport existing = importVdbs.put(vdbName, new VdbImport(vdbName, importDataPolicies, version));
		 setChanged(existing != null);
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addImport(org.teiid.designer.komodo.vdb.VdbImport)
	 */
	/* (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addImport(org.teiid.designer.komodo.vdb.VdbImport)
	 */
	public void addImport(VdbImport vdbImport) {
		VdbImport existing = importVdbs.put(vdbImport.getName(), vdbImport);
		setChanged(existing != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addModel(org.teiid.designer.komodo.vdb.Model)
	 */
	@Override
	public void addModel(Model model) {
		Model existing = models.put(model.getName(), model);
		setChanged(existing != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#addTranslator(org.teiid.designer.komodo.vdb.Translator)
	 */
	@Override
	public void addTranslator(final Translator translator) {
		translatorOverrides.put(translator.getName(), translator);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#createManifest(java.util.Properties)
	 */
	@Override
	public VdbManifest createManifest(Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getConnectionType()
	 */
	@Override
	public String getConnectionType() {
		return connectionType;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getDataRoles()
	 */
	@Override
	public DataRole[] getDataRoles() {
		return (DataRole[])dataRoles.values().toArray(new DataRole[dataRoles.size()]);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getEntries()
	 */
	@Override
	public Entry[] getEntries() {
		return (Entry[])entries.values().toArray(new Entry[entries.size()]);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getModels()
	 */
	@Override
	public Model[] getModels() {
		return (Model[])models.values().toArray(new Model[models.size()]);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getImports()
	 */
	@Override
	public VdbImport[] getImports() {
		return (VdbImport[])importVdbs.values().toArray(new VdbImport[importVdbs.size()]);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getOriginalFilePath()
	 */
	@Override
	public String getOriginalFilePath() {
		return vdbFilePath;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getTranslators()
	 */
	@Override
	public Translator[] getTranslators() {
		return (Translator[])translatorOverrides.values().toArray(new Translator[translatorOverrides.size()]);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getVersion()
	 */
	@Override
	public int getVersion() {
		return version;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#isPreview()
	 */
	@Override
	public boolean isPreview() {
		return preview;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#removeDataRole(java.lang.String)
	 */
	@Override
	public void removeDataRole(String dataRoleToRemove) {
		DataRole removed = this.dataRoles.remove(dataRoleToRemove);
		setChanged(removed != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#removeEntry(java.lang.String)
	 */
	@Override
	public void removeEntry(String entryToRemove) {
		Entry removed = entries.remove(entryToRemove);
		setChanged(removed != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#removeImport(java.lang.String)
	 */
	@Override
	public void removeImport(String importToRemove) {
		VdbImport removed = importVdbs.remove(importToRemove);
		setChanged(removed != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#removeModel(java.lang.String)
	 */
	@Override
	public void removeModel(String modelToRemove) {
		Model removed = models.remove(modelToRemove);
		setChanged(removed != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#removeTranslator(java.lang.String)
	 */
	@Override
	public void removeTranslator(String translatorToRemove) {
		Translator removed = translatorOverrides.remove(translatorToRemove);
		setChanged(removed != null);
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setConnectionType(java.lang.String)
	 */
	@Override
	public void setConnectionType(String newConnectionType) {
		if( StringUtilities.areDifferent(this.connectionType, newConnectionType)) {
			this.connectionType = newConnectionType;
			setChanged(true);
		}
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setOriginalFilePath(java.lang.String)
	 */
	@Override
	public void setOriginalFilePath(String newOriginalFilePath) {
		this.vdbFilePath = newOriginalFilePath;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setPreview(boolean)
	 */
	@Override
	public void setPreview(boolean newPreview) {
		if( this.preview != newPreview) {
			this.preview = newPreview;
			setChanged(true);
		}
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setVersion(int)
	 */
	@Override
	public void setVersion(int newVersion) {
		if( this.version != newVersion ) {
			this.version = newVersion;
			setChanged(true);
		}
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getQueryTimeout()
	 */
	public int getQueryTimeout() {
		return this.queryTimeout;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int queryTimeout) {
		if( this.queryTimeout != queryTimeout ) {
			this.queryTimeout = queryTimeout;
			setChanged(true);
		}
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getSecurityDomain()
	 */
	public String getSecurityDomain() {
		return this.securityDomain;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setSecurityDomain(java.lang.String)
	 */
	public void setSecurityDomain(String domain) {
		this.securityDomain = domain;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getGssPattern()
	 */
	public String getGssPattern() {
		return this.gssPattern;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setGssPattern(java.lang.String)
	 */
	public void setGssPattern(String gssPattern) {
		this.gssPattern = gssPattern;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getPasswordPattern()
	 */
	public String getPasswordPattern() {
		return this.passwordPattern;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setPasswordPattern(java.lang.String)
	 */
	public void setPasswordPattern(String passwordPattern) {
		this.passwordPattern = passwordPattern;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getAuthenticationType()
	 */
	public String getAuthenticationType() {
		return this.authenticationType;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#setAuthenticationType(java.lang.String)
	 */
	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#getAllowedLanguages()
	 */
	public AllowedLanguages getAllowedLanguages() {
		return this.allowedLanguages;
	}


	public boolean isAutoGenerateRESTWar() {
		return autoGenerateRESTWar;
	}


	public void setAutoGenerateRESTWar(boolean autoGenerateRESTWar) {
		this.autoGenerateRESTWar = autoGenerateRESTWar;
	}

}
