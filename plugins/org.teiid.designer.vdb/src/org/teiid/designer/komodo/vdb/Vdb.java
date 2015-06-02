/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.util.Properties;

import org.w3c.dom.Document;

/**
 * Represents a virtual database manifest.
 */
public interface Vdb {

    /**
     * The type identifier.
     */
    int TYPE_ID = Vdb.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB;

    /**
     * Represents a VDB XML manifest file.
     */
    public interface VdbManifest {

        /**
         * @return the manifest as an XML document (never <code>null</code>)
         */
        Document asDocument();

    }

    /**
     * The default value indicating if this VDB is a preview VDB. Value is {@value} .
     */
    boolean DEFAULT_PREVIEW = false;

    /**
     * The default version number. Value is {@value} .
     */
    int DEFAULT_VERSION = 1;

    /**
     * An empty array of VDBs.
     */
    Vdb[] NO_VDBS = new Vdb[0];

    /**
     * @param dataRole 
     */
    void addDataRole( DataRole dataRole );

    /**
     * @param entryName
     *        the name of the entry being added (cannot be empty)
     * @param entryPath
     *        the path of the entry (cannot be empty)
     * @return the new entry (never <code>null</code>)
     */
    Entry addEntry( final String entryName,
                    final String entryPath );

    /**
     * @param vdbName
     *        the name of the VDB being imported (cannot be empty)
     * @param importDataPolicies 
     * @param version 
     */
    void addImport(String vdbName, boolean importDataPolicies, int version); 
    
    /**
     * @param vdbImport
     */
    void addImport(VdbImport vdbImport);

    /**
     * @param model
     *        the name of the VDB being imported (cannot be empty)
     */
    void addModel(Model model);

    /**
     * @param translator
     */
    void addTranslator(Translator translator );

    /**
     * @param properties (can be <code>null</code> or empty)
     * @return the VDB XML manifest representing the current state of the VDB (never null)
     */
    VdbManifest createManifest(Properties properties );

    /**
     * @return allowed languages
     */
    AllowedLanguages getAllowedLanguages();
    
    /**
     * @return the value of the <code>connection type</code> property (can be empty)
     */
    String getConnectionType();

    /**
     * @return the data roles (never <code>null</code> but can be empty)
     */
    DataRole[] getDataRoles();

    /**
     * @return the value of the <code>description</code> property (can be empty)
     */
    String getDescription();

    /**
     * @return the entries (never <code>null</code> but can be empty)
     */
    Entry[] getEntries();

    /**
     * @return the VDB models (never <code>null</code> but can be empty)
     */
    Model[] getModels();

    /**
     * @return the VDB imports (never <code>null</code> but can be empty)
     */
    VdbImport[] getImports();

    /**
     * @return the value of the <code>original file path</code> property (never empty)
     */
    String getOriginalFilePath();

    /**
     * @return the translators (never <code>null</code> but can be empty)
     */
    Translator[] getTranslators();

    /**
     * A name used by Teiid to reference this VDB.
     *
     * @return the value of the <code>Teiid VDB name</code> property (can be empty)
     */
    String getName();

    /**
     * @return the value of the <code>version</code> property
     * @see #DEFAULT_VERSION
     */
    int getVersion( );

    /**
     * @return <code>true</code> if a preview VDB
     * @see #DEFAULT_PREVIEW
     */
    boolean isPreview( );

    /**
     * @param dataRoleToRemove
     *        the name of the data role being removed (cannot be empty)
     */
    void removeDataRole( final String dataRoleToRemove );

    /**
     * @param entryToRemove
     *        the name of the entry being removed (cannot be empty)
     */
    void removeEntry(final String entryToRemove );

    /**
     * @param importToRemove
     *        the name of the VDB import being removed (cannot be empty)
     */
    void removeImport(final String importToRemove );

    /**
     * @param modelToRemove
     *        the name of the model being removed (cannot be empty)
     */
    void removeModel(final String modelToRemove );

    /**
     * @param translatorToRemove
     *        the name of the translator being removed (cannot be empty)
     */
    void removeTranslator(final String translatorToRemove );

    /**
     * @param newConnectionType
     *        the new value of the <code>connection type</code> property
     */
    void setConnectionType(final String newConnectionType );

    /**
     * @param newDescription
     *        the new value of the <code>description</code> property
     */
    void setDescription( final String newDescription );

    /**
     * @param newOriginalFilePath
     *        the new value of the <code>original file path</code> property (cannot be empty)
     */
    void setOriginalFilePath(final String newOriginalFilePath );

    /**
     * @param newPreview
     *        the new value for the <code>preview</code> property
     * @see #DEFAULT_PREVIEW
     */
    void setPreview(final boolean newPreview );

    /**
     * Sets the name used by Teiid to reference this VDB.
     *
     * @param newVdbName
     *        the new value of the <code>Teiid VDB name</code> property
     */
    void setName(final String newVdbName );

    /**
     * @param newVersion
     *        the new value of the <code>version</code> property
     * @see #DEFAULT_VERSION
     */
    void setVersion(final int newVersion );
    
	/**
	 * @return timeout
	 */
	int getQueryTimeout();
	
	/**
	 * @param queryTimeout
	 */
	void setQueryTimeout(int queryTimeout);
	
	
	/**
	 * @return security domain
	 */
	String getSecurityDomain();
	
	/**
	 * @param domain
	 */
	void setSecurityDomain(String domain);
	
	/**
	 * @return gss pattern
	 */
	String getGssPattern();
	
	/**
	 * @param gssPattern
	 */
	void setGssPattern(String gssPattern);
	
	/**
	 * @return password pattern
	 */
	String getPasswordPattern();
	
	/**
	 * @param passwordPattern
	 */
	void setPasswordPattern(String passwordPattern);
	
	/**
	 * @return authentication type
	 */
	String getAuthenticationType();
	
	/**
	 * @param authenticationType
	 */
	void setAuthenticationType(String authenticationType);
	
	/**
	 * @return value
	 */
	boolean isAutoGenerateRESTWar();

	/**
	 * @param autoGenerateRESTWar
	 */
	void setAutoGenerateRESTWar(boolean autoGenerateRESTWar);
	
	/**
	 * @return vdb properties
	 */
	Properties getProperties();
    
	/**
	 * @param properties
	 */
	void setProperties(Properties properties);
	
	/**
	 * @param key
	 * @param value
	 */
	void setProperty(String key, String value);
	
	/**
	 * @param key
	 */
	void removeProperty(String key);
	
    /**
     * @throws Exception 
     * 
     */
    void load() throws Exception ;
    
    /**
     * 
     */
    void export();

    /**
     * Constants relating to the vdb.xml file.
     */
    interface Xml {

        /**
         */
        String PREVIEW = "preview"; //$NON-NLS-1$
        
        /**
         */
        String QUERY_TIMEOUT = "query-timeout"; //$NON-NLS-1$
        
        /**
         */
        String AUTO_GENERATE_REST_WAR = "{http://teiid.org/rest}auto-generate"; //$NON-NLS-1$
        
        /**
         * 
         */
        String ALLOWED_LANGUAGES = "allowed-languages"; //$NON-NLS-1$
        
        /**
         */
        String VALIDATION_DATETIME = "validationDateTime"; //$NON-NLS-1$
        
        /**
         */
        String VALIDATION_VERSION = "validationVersion"; //$NON-NLS-1$
        
        /*
        <property name="security-domain" value="teiid-spengo" />
		<property name="gss-pattern" value="{regex}" />
		<property name="password-pattern" value="{regex}" />
		<property name="authentication-type" value="GSS or USERPASSWORD" />
         */
        
        /**
         */
        String SECURITY_DOMAIN = "security-domain"; //$NON-NLS-1$
        
        /**
         */
        String GSS_PATTERN = "gss-pattern"; //$NON-NLS-1$
        
        /**
         */
        String PASSWORD_PATTERN = "password-pattern"; //$NON-NLS-1$
        
        /**
         */
        String AUTHENTICATION_TYPE = "authentication-type"; //$NON-NLS-1$
        
        /**
         */
        String AUTHENTICATION_TYPE_GSS = "GSS"; //$NON-NLS-1$
        
        /**
         */
        String AUTHENTICATION_TYPE_USERPASSWORD = "USERPASSWORD"; //$NON-NLS-1$
    }
}
