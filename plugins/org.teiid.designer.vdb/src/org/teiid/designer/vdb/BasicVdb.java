/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.KeyInValueHashMap;
import org.teiid.designer.core.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.vdb.dynamic.DynamicVdb;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

/**
 * @author blafond
 *
 */
public abstract class BasicVdb extends AbstractVdbObject implements Vdb {

    private class TranslatorOverrideKeyAdapter implements KeyFromValueAdapter<String, TranslatorOverride> {
        @Override
        public String getKey(TranslatorOverride value) {
            return value.getName();
        }        
    }

    private class VdbEntryKeyAdapter implements KeyFromValueAdapter<String, VdbEntry> {
        @Override
        public String getKey(VdbEntry value) {
            return value.getName();
        }        
    }

    private class DataRoleKeyAdapter implements KeyFromValueAdapter<String, DataRole> {
        @Override
        public String getKey(DataRole value) {
            return value.getName();
        }        
    }

    private class ImportVdbKeyAdapter implements KeyFromValueAdapter<String, VdbImportVdbEntry> {
        @Override
        public String getKey(VdbImportVdbEntry value) {
            return value.getName();
        }        
    }

	private IFile sourceFile;
	
	// VDBs have the following possible structure
	/* ATTRIBUTES
	 	<xs:attribute name="name" type="xs:string" use="required"/>
	 	<xs:attribute name="version" type="xs:int" use="required"/>
	 */
	private int version = 1;
	
	/* Elements:
		<xs:element name="description" type="xs:string" minOccurs="0"/>
	    <xs:element name="connection-type" type="xs:string" minOccurs="0" default="BY_VERSION"/>
		<xs:element name="property" type="property" minOccurs="0" maxOccurs="unbounded"/>
	*/

	private String connectionType;
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
	private boolean preview;
	private int queryTimeout = DEFAULT_TIMEOUT;
	private AllowedLanguages allowedLanguages = new AllowedLanguages(this);
	private String securityDomain;
	private String gssPattern;
	private String passwordPattern;
	private String authenticationType;
	private Date validateDateTime;
	private String validationVersion;
	private boolean autoGenerateRESTWar;
	
	private KeyInValueHashMap<String, VdbImportVdbEntry> importVdbs = new KeyInValueHashMap<String, VdbImportVdbEntry>(new ImportVdbKeyAdapter());
	private KeyInValueHashMap<String, TranslatorOverride> translatorOverrides = new KeyInValueHashMap<String, TranslatorOverride>(new TranslatorOverrideKeyAdapter());
	private KeyInValueHashMap<String, DataRole> dataRoles = new KeyInValueHashMap<String, DataRole>(new DataRoleKeyAdapter());

	private final CopyOnWriteArrayList<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
	
	private IStatus currentStatus;

	/**
	 * Default constructor
	 */
	protected BasicVdb() {
	    super();
	}

	/**
	 * @param sourceFile the source file of the vdb
	 *
	 * <p>
	 * <li>The name of the vdb is the source file name WITHOUT the extension
     * <li>The file name ({@link #getFileName()}) is the source file name WITH the extension
     * <li>The path of the vdb can be extracted from {@link #getSourceFile()}
	 * @throws Exception
	 */
	protected BasicVdb(IFile sourceFile) throws Exception {
		super();
		this.sourceFile = sourceFile;

		setName(FileUtils.getNameWithoutExtension(sourceFile));

		// Create folder for VDB in state folder
        File stagingFolder = getStagingFolder();
        if (stagingFolder != null)
            stagingFolder.mkdirs();

		read(sourceFile);

		setChanged(false);
		currentStatus = Status.OK_STATUS;
	}

	/**
     * This will import the contents of the vdb from the given file and
     * replace the point this vdb at the given file
     *
     * @param file
     *
     * @throws Exception
     */
    public abstract void read(final IFile file) throws Exception;

	@Override
	public void setDescription(String newDescription) {
	    if (StringUtilities.isEmpty(newDescription))
	        newDescription = null;

	    String oldDescription = getDescription();
	    if (StringUtilities.isEmpty(newDescription))
            oldDescription = null;

	    if (StringUtilities.areDifferent(oldDescription, newDescription)) {
	        super.setDescription(newDescription);
	        setModified(this, Event.DESCRIPTION, oldDescription, newDescription);
	    }
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#addDataRole(DataRole)
	 */
	@Override
	public boolean addDataRole(DataRole dataRole) {
        if (dataRoles.add(dataRole)) {
            dataRole.setVdb(this);
            setModified(this, Event.DATA_POLICY_ADDED, null, dataRole);
            return true;
        }
        return false;
	}

	/**
	 *  (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#addImport(java.lang.String)
	 */
	@Override
    public VdbImportVdbEntry addImport(String vdbName) {
		 VdbImportVdbEntry importVdbEntry = new VdbImportVdbEntry(this, vdbName);
		 importVdbs.add(importVdbEntry);
		 setModified(this, Event.IMPORT_VDB_ENTRY_ADDED, null, importVdbEntry);
		 return importVdbEntry;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#addImport(org.teiid.designer.vdb.VdbImportVdbEntry)
	 */
	@Override
    public boolean addImport(VdbImportVdbEntry vdbImport) {
		if (importVdbs.add(vdbImport)) {
		    vdbImport.setVdb(this);
		    setModified(this, Event.IMPORT_VDB_ENTRY_ADDED, null, vdbImport);
		    return true;
		}
		return false;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#addTranslator(org.teiid.designer.vdb.TranslatorOverride)
	 */
	@Override
	public boolean addTranslator(final TranslatorOverride translatorOverride) {
        if (this.translatorOverrides.add(translatorOverride)) {
            translatorOverride.setVdb(this);
            setModified(this, Event.TRANSLATOR_OVERRIDE_ADDED, null, translatorOverride);
            return true;
        }

        return false;
	}

	/** (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#getJaxbContext()
     */
    @Override
    public JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] { VdbElement.class });
    }

	/** (non-Javadoc)
     * @return schema
     * @throws SAXException 
     * @see org.teiid.designer.vdb.Vdb#getManifestSchema()
     */
    @Override
    public Schema getManifestSchema() throws SAXException {
        return VdbUtil.getManifestSchema();
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getConnectionType()
	 */
	@Override
	public String getConnectionType() {
		return connectionType;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getDataRoles()
	 */
	@Override
	public Collection<DataRole> getDataRoles() {
		return Collections.unmodifiableCollection(dataRoles.values());
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getImports()
	 */
	@Override
	public Collection<VdbImportVdbEntry> getImports() {
		return Collections.unmodifiableCollection(importVdbs.values());
	}

	/** (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#getStagingFolder()
     */
    @Override
    public File getStagingFolder() {
        return VdbPlugin.singleton().getStateLocation().append(getSourceFile().getFullPath()).toFile();
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getSourceFile()
	 */
	@Override
	public IFile getSourceFile() {
		return sourceFile;
	}

	/** (non-Javadoc)
     * @param file
     */
    protected void setSourceFile(IFile file) {
        this.sourceFile = file;
    }

	@Override
    public String getFileName() {
	    if (sourceFile == null)
	        return null;

	    return sourceFile.getName();
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getTranslators()
	 */
	@Override
	public Collection<TranslatorOverride> getTranslators() {
		return Collections.unmodifiableCollection(translatorOverrides.values());
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getVersion()
	 */
	@Override
	public int getVersion() {
		return version;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#isPreview()
	 */
	@Override
	public boolean isPreview() {
		return preview;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#removeDataRole(java.lang.String)
	 */
	@Override
	public boolean removeDataRole(String dataRoleToRemove) {
	    DataRole dataRole = this.dataRoles.get(dataRoleToRemove);
		if (dataRole == null)
		    return false;

		dataRole = this.dataRoles.remove(dataRole);
		if (dataRole != null) {
		    setModified(this, Event.DATA_POLICY_REMOVED, dataRole, null);
		    return true;
		}

		return false;
	}

	@Override
    public boolean removeImport(VdbImportVdbEntry importToRemove) {
        if (importVdbs.remove(importToRemove) != null) {
            setModified(this, Event.IMPORT_VDB_ENTRY_REMOVED, importToRemove, null);
            return true;
        }

        return false;
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#removeImport(java.lang.String)
	 */
	@Override
	public boolean removeImport(String importToRemove) {
	    VdbImportVdbEntry importEntry = this.importVdbs.get(importToRemove);
        if (importEntry == null)
            return false;

        return removeImport(importEntry);
	}

	@Override
    public void removeAllImports() {
        Collection<VdbImportVdbEntry> entries = new ArrayList<VdbImportVdbEntry>(this.importVdbs.values());
        if (entries.isEmpty())
            return;

        this.importVdbs.clear();
        setModified(this, Event.IMPORT_VDB_ENTRY_REMOVED, entries, null);
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#removeTranslator(java.lang.String)
	 */
	@Override
    public boolean removeTranslator( TranslatorOverride translatorOverride) {
        if (this.translatorOverrides.remove(translatorOverride) != null) {
            setModified(this, Event.TRANSLATOR_OVERRIDE_REMOVED, translatorOverride, null);
            return true;
        }

        return false;
    }

	   /** (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#removeTranslator(java.lang.String)
     */
    @Override
    public boolean removeTranslator(String name) {
        TranslatorOverride translatorOverride = this.translatorOverrides.get(name);
        if (translatorOverride == null)
            return false;

        return removeTranslator(translatorOverride);
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#setConnectionType(java.lang.String)
	 */
	@Override
	public void setConnectionType(String newConnectionType) {
		if( StringUtilities.areDifferent(this.connectionType, newConnectionType)) {
			this.connectionType = newConnectionType;
			setChanged(true);
		}
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#setPreview(boolean)
	 */
	@Override
	public void setPreview(boolean newPreview) {
		if( this.preview != newPreview) {
			this.preview = newPreview;
			setChanged(true);
		}
	}

	/**
     * @param intVersion version of vdb
     */
    @Override
    public void setVersion( int intVersion ) {
        final int oldVersion = this.version;
        if( oldVersion == intVersion ) return;
        this.version = intVersion;
        setModified(this, Event.VERSION, oldVersion, intVersion);
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getQueryTimeout()
	 */
	@Override
    public int getQueryTimeout() {
		return this.queryTimeout;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#setQueryTimeout(int)
	 */
    @Override
    public void setQueryTimeout( int valueInSeconds ) {
        final int oldTimeout = this.queryTimeout;
        if( oldTimeout == valueInSeconds ) return;
        this.queryTimeout = valueInSeconds;
        setModified(this, Event.QUERY_TIMEOUT, oldTimeout, valueInSeconds);
    }
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getSecurityDomain()
	 */
	@Override
    public String getSecurityDomain() {
		return this.securityDomain;
	}

    /**
     * @param newValue Sets security-domain to the specified value.
     */
    @Override
    public void setSecurityDomain( String newValue ) {
        final String old = this.securityDomain;
        if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
        if( newValue.length() == 0 ) {
            this.securityDomain = null;
        } else {
            this.securityDomain = newValue;
        }
        setModified(this, Event.SECURITY_DOMAIN, old, newValue);
    }
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getGssPattern()
	 */
	@Override
    public String getGssPattern() {
		return this.gssPattern;
	}

	/**
     * @param newValue Sets gss-pattern to the specified value.
     */
    @Override
    public void setGssPattern( String newValue ) {
        final String old = this.gssPattern;
        if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
        if( newValue.length() == 0 ) {
            this.gssPattern = null;
        } else {
            this.gssPattern = newValue;
        }
        setModified(this, Event.GSS_PATTERN, old, newValue);
    }

	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getPasswordPattern()
	 */
	@Override
    public String getPasswordPattern() {
		return this.passwordPattern;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#setPasswordPattern(java.lang.String)
	 */
    @Override
    public void setPasswordPattern( String newValue ) {
        final String old = this.passwordPattern;
        if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
        if( newValue.length() == 0 ) {
            this.passwordPattern = null;
        } else {
            this.passwordPattern = newValue;
        }
        setModified(this, Event.PASSWORD_PATTERN, old, newValue);
    }
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getAuthenticationType()
	 */
	@Override
    public String getAuthenticationType() {
		return this.authenticationType;
	}
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#setAuthenticationType(java.lang.String)
	 */
    @Override
    public void setAuthenticationType( String newValue ) {
        final String old = this.authenticationType;
        if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
        if( newValue.length() == 0 ) {
            this.authenticationType = null;
        } else {
            this.authenticationType = newValue;
        }
        setModified(this, Event.AUTHENTICATION_TYPE, old, newValue);
    }
	
	/** (non-Javadoc)
	 * @see org.teiid.designer.vdb.Vdb#getAllowedLanguages()
	 */
	@Override
    public AllowedLanguages getAllowedLanguages() {
		return this.allowedLanguages;
	}

    @Override
    public boolean addAllowedLanguage(String name) {
        this.allowedLanguages.addAllowedLanguage(name);
        if(this.allowedLanguages.isChanged()) {
            setModified(this, Event.ALLOWED_LANGUAGES, name, name);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAllowedLanguage(String name) {
        this.allowedLanguages.removeAllowedLanguage(name);
        if (allowedLanguages.isChanged()) {
            setModified(this, Event.ALLOWED_LANGUAGES, name, null);
            return true;
        }

        return false;
    }

	@Override
    public boolean isAutoGenerateRESTWar() {
		return autoGenerateRESTWar;
	}


	@Override
    public void setAutoGenerateRESTWar(boolean autoGenerateRESTWar) {
		final boolean oldValue = this.autoGenerateRESTWar;
        if( oldValue == autoGenerateRESTWar ) return;

        this.autoGenerateRESTWar = autoGenerateRESTWar;
        setModified(this, Event.AUTO_GENERATE_REST_WAR, oldValue, autoGenerateRESTWar);
	}

	/**
     * @param valVersion Sets validatationVersion to the specified value.
     */
    @Override
    public void setValidationVersion( String valVersion ) {
        final String oldVersion = this.validationVersion;
        if( StringUtilities.equals(oldVersion, valVersion)) return;
        this.validationVersion = valVersion;
        setModified(this, Event.GENERAL_PROPERTY, oldVersion, valVersion);
    }

    /**
     * @return the VDB validation date-time
     */
    @Override
    public Date getValidationDateTime() {
        return validateDateTime;
    }

    /**
     * @param dateTime Sets validatationDateTime to the specified value.
     */
    @Override
    public void setValidationDateTime( Date dateTime ) {
        final Date oldDateTime = this.validateDateTime;
        if( oldDateTime != null && oldDateTime.equals(dateTime)) return;
        this.validateDateTime = dateTime;
        setModified(this, Event.GENERAL_PROPERTY, oldDateTime, dateTime);
    }

    /**
     * @return the VDB validation version
     */
    @Override
    public String getValidationVersion() {
        return validationVersion;
    }
    
    /**
     * @param key
     * @param value
     */
    @Override
    public void setProperty(String key, String value) {
    	super.setProperty(key, value);
    	setModified(this, Event.GENERAL_PROPERTY, null, value);
    }

    /**
     * @param key
     * @return removed property
     */
    @Override
    public String removeProperty(String key) {
    	String prop = super.removeProperty(key);
    	setModified(this, Event.GENERAL_PROPERTY, null, prop);
        return prop;
    }
    
    /* (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#getStatus()
     */
    @Override
    public IStatus getStatus() {
		return currentStatus;
	}
    
    /* (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#setStatus(org.eclipse.core.runtime.IStatus)
     */
    @Override
	public void setStatus(IStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	/* (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#notifyChangeListeners(java.lang.Object, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyChangeListeners( final Object source,
                                final String propertyName,
                                final Object oldValue,
                                final Object newValue ) {
        PropertyChangeEvent event = null;
        if (!isPreview()) {
            for (final PropertyChangeListener listener : listeners) {
                if (event == null) event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
                listener.propertyChange(event);
            }
        }
    }

    /**
     * @param listener
     */
    @Override
    public void addChangeListener( final PropertyChangeListener listener ) {
        listeners.addIfAbsent(listener);
    }

    /**
     * @param listener
     */
    @Override
    public void removeChangeListener( final PropertyChangeListener listener ) {
        listeners.remove(listener);
    }

    @Override
    public boolean isModified() {
        return isChanged();
    }

	@Override
    public void setModified( final Object source,
                      final String propertyName,
                      final Object oldValue,
                      final Object newValue ) {
        setChanged(true);
        notifyChangeListeners(source, propertyName, oldValue, newValue);
    }

	@Override
	public void close() {

	    setDescription(EMPTY_STRING);
        // Mark VDB as unmodified
        if (isModified())
            setChanged(false);

        // Notify change listeners VDB is closed
        notifyChangeListeners(this, Event.CLOSED, null, null);

        listeners.clear();
	}


    /**
     * @return the problem markers (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the problem markers
     */
    @Override
    public IMarker[] getProblems() throws Exception {
        IFile file = getSourceFile();
        if (file == null)
            return new IMarker[0];

        return file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    }

    /**
     * Populate the given vdb with the basic properties and settings
     *
     * @param vdb
     */
    protected void populateVdb(BasicVdb vdb) {
    	// Note that at this time, the input VDB would have validation version and validation date defined as properties.
    	// So need to either copy them instead of generating them.
    	// {validationVersion=8.7.1, validationDateTime=Thu Aug 06 14:29:43 CDT 2015}
    	Properties existingProps = this.getProperties();
        vdb.setName(getName());
        vdb.setDescription(getDescription());
        String validationVersion = existingProps.getProperty("validationVersion");
        if( validationVersion != null ) {
        	vdb.setValidationVersion(validationVersion);
        	existingProps.remove("validationVersion");
        } else {
        	vdb.setValidationVersion(ModelerCore.getTeiidServerVersion().toString());
        }
        
        
        String validationDateTime = existingProps.getProperty("validationDateTime");
        if( validationDateTime != null ) {
        	existingProps.remove("validationDateTime");
        	try {
        		SimpleDateFormat inFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        		// Mon Aug 10 15:21:50 CDT 2015
        		Date dtIn = inFormat.parse(validationDateTime);

				vdb.setValidationDateTime(dtIn);
			} catch (ParseException e) {
				e.printStackTrace();
				vdb.setValidationDateTime(new Date());
			}
        } else {
        	vdb.setValidationDateTime(new Date());
        }

        for (Map.Entry<Object, Object> entry : existingProps.entrySet()) {
        	if( ! entry.getKey().toString().equals("validationVersion") && 
        		! entry.getKey().toString().equals("validationDateTime") ) {
        		vdb.setProperty(entry.getKey().toString(), entry.getValue().toString());
        	}
        }

        if (vdb.getSourceFile() == null)
            vdb.setSourceFile(sourceFile);

        vdb.setVersion(getVersion());

        vdb.setConnectionType(getConnectionType());
        vdb.setPreview(isPreview());
        vdb.setQueryTimeout(getQueryTimeout());

        for (String language : getAllowedLanguages().getAllowedLanguageValues()) {
            vdb.addAllowedLanguage(language);
        }

        vdb.setSecurityDomain(getSecurityDomain());
        vdb.setGssPattern(getGssPattern());
        vdb.setPasswordPattern(getPasswordPattern());
        vdb.setAuthenticationType(getAuthenticationType());
        vdb.setValidationDateTime(getValidationDateTime());
        vdb.setAutoGenerateRESTWar(isAutoGenerateRESTWar());
        
        // Look for other specific VDB properties
        if(getProperties().keySet().contains(Vdb.Xml.USE_CONNECTOR_METADATA) ) {
        	vdb.setProperty(Vdb.Xml.USE_CONNECTOR_METADATA, getProperties().getProperty(Vdb.Xml.USE_CONNECTOR_METADATA));
        }

        for (VdbImportVdbEntry entry : getImports()) {
            VdbImportVdbEntry clone = entry.clone();
            vdb.addImport(clone);
        }
        
        for (TranslatorOverride translator : getTranslators()) {
            TranslatorOverride clone = translator.clone();
            vdb.addTranslator(clone);
        }

        for (DataRole dataRole : getDataRoles()) {
            DataRole clone = dataRole.clone();
            vdb.addDataRole(clone);
        }

        vdb.setChanged(isChanged());
    }

    /**
     * @param destination location for the conversion
     * @param options options being passed to the conversion implimentations
     * @return an {@link XmiVdb} of this vdb
     * @throws Exception 
     */
    public abstract XmiVdb xmiVdbConvert(IFile destination, Properties options) throws Exception;

    /**
     * @param destination location for the conversion
     * @param options options being passed to the conversion implimentations
     * @return a {@link DynamicVdb} of this vdb
     * @throws Exception 
     */
    public abstract DynamicVdb dynVdbConvert(IFile destination, Properties options) throws Exception;

    @Override
    public <V extends Vdb> V convert(final Class<V> vdbType, final IFile destination, Properties options) throws Exception {
        CoreArgCheck.isNotNull(vdbType);
        CoreArgCheck.isNotNull(destination);

        if (DynamicVdb.class.equals(vdbType))
            return (V) dynVdbConvert(destination, options);
        else if (XmiVdb.class.equals(vdbType))
            return (V) xmiVdbConvert(destination, options);

        throw new UnsupportedOperationException();
    }
}
