/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import net.jcip.annotations.ThreadSafe;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.comments.CommentSets;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.vdb.dynamic.DynamicModel;
import org.teiid.designer.vdb.dynamic.DynamicVdb;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 *
 * @since 8.0
 */
@ThreadSafe
public interface Vdb extends VdbConstants {

    /**
     * Option for telling the ddl importer during {@link #convert(Class, IFile, Properties)}
     * whether to include the original ddl string as the description
     */
    public static final String SET_DDL_AS_DESCRIPTION = "setDDLAsDescription"; //$NON-NLS-1$

    /**
     * Option to determine whether to suppress xml attributes that retain default values
     * when writing out vdb xml
     */
    public static final String SUPPRESS_XML_DEFAULT_ATTRIBUTES = "suppressXmlDefaultAttributes"; //$NON-NLS-1$

    /**
     * The prefix used before the workspace identifier when creating a Preview VDB name.
     */
    public static final String PREVIEW_PREFIX = "PREVIEW_"; //$NON-NLS-1$


    /**
     * The default query timeout value
     */
    int DEFAULT_TIMEOUT = 0;

    /**
     * @param dataRole
     * @return TODO
     */
    boolean addDataRole(final DataRole dataRole);

    /**
     * @param listener
     */
    void addChangeListener(final PropertyChangeListener listener);

    /**
     * @param path
     *
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied path.
     * @throws Exception
     */
    <T extends VdbEntry> T addEntry(final IPath path) throws Exception;

    /**
     * @param translatorOverride the translator override (may not be <code>null</code>)
     * @return <code>true</code> if successfully added
     */
    boolean addTranslator(TranslatorOverride translatorOverride);

    /**
     * Add an allowed language property
     * 
     * @param name
     * 
     * @return whether the import vdb attribute was successfully added
     */
    boolean addAllowedLanguage(String name);

    /**
     * Synchronize the Vdb file entries.  The supplied entries must be included - it's VdbModelEntry
     * may not exist in the vdb yet.
     * @param newJarEntries the supplied new entries which must exist 
     */
    void synchronizeUdfJars(Set<VdbFileEntry> newJarEntries);

    /**
     * 
     */
    void close();

    /**
     * 
     * @return the immutable set of allowed-languages strings
     */
    AllowedLanguages getAllowedLanguages();

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    Collection<DataRole> getDataRoles();

    /**
     * @return description
     */
    String getDescription();

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    Collection<VdbEntry> getEntries();

    /**
     * Get the current set of schema entries.
     * @return the set of VdbSchemaEntry objects
     */
    Set<VdbSchemaEntry> getSchemaEntries();

    /**
     * Get the current set of UDF jar entries.
     * @return the set of VdbFileEntry UDF jar objects
     */
    Set<VdbFileEntry> getUdfJarEntries();

    /**
     * Get the current set of UDF jar entries.
     * @return the set of VdbEntry UDF jar objects
     */
    Set<String> getUdfJarNames();

    /**
     * Get the current set of UserFile entries.
     * @return the set of VdbFileEntry userFile objects
     */
    Set<VdbFileEntry> getUserFileEntries();

    /**
     * @return the map of general VDB properties
     */
    Properties getProperties();

    /**
     * @return JAXBContext
     * @throws JAXBException
     */
    JAXBContext getJaxbContext() throws JAXBException;

    /**
     * @return Schema
     * @throws SAXException
     */
    Schema getManifestSchema() throws SAXException;

    /**
     * @return the immutable set of model entries within this VDB
     */
    Set<VdbModelEntry> getModelEntries();

    /**
     * @return the immutable set of import vdb entries within this VDB
     */
    Collection<VdbImportVdbEntry> getImports();

    /**
     * Method to return the File objects associated with each model in this VDB.
     * The intention is to allow the Data Policy wizard to display contents of these models in EMF form so users can 
     * pick/chose and set-up their data entitlements.
     *
     * Note: This will no longer return the schema files since they have their own
     *          collection and convert getter method.
     *
     * @return the immutable list of model files within this VDB
     */
    Collection<File> getModelFiles();

    /**
     * @return the immutable list of schema files within this VDB
     */
    Collection<File> getSchemaFiles();

    /**
     * @return the name of this VDB
     */
    String getName();

    /**
     * @return <code>true</code> if this VDB has been modified since its creation of last {@link #save() save}.
     */
    boolean isModified();

    /**
     * @return <code>true</code> if this is a Preview VDB
     */
    boolean isPreview();

    /**
     * @return the problem markers (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the problem markers
     */
    IMarker[] getProblems() throws Exception;

    /**
     * @return the immutable set of overridden translators within this VDB (never <code>null</code>)
     */
    Collection<TranslatorOverride> getTranslators();

    /**
     * @return the VDB version
     */
    int getVersion();

    /**
     * @return the query timeout value for this VDB (in seconds)
     */
    int getQueryTimeout();

    /**
     * @return the VDB validation version
     */
    String getValidationVersion();

    /**
     * @return the VDB validation date-time
     */
    Date getValidationDateTime();

    /**
     * @return the VDB validation security-domain
     */
    String getSecurityDomain();

    /**
     * @return the VDB validation date-time
     */
    String getGssPattern();

    /**
     * @return the VDB validation date-time
     */
    String getPasswordPattern();

    /**
     * @return the VDB validation date-time
     */
    String getAuthenticationType();
    
    /**
     * @return the current status of the VDB
     * 
     */
    IStatus getStatus();

    /**
     * @return <code>true</code> if all model entries in this VDB are either synchronized with their associated models or no
     *         associated model exists..
     */
    boolean isSynchronized();

    /**
     * @param source
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    void notifyChangeListeners(final Object source, final String propertyName, final Object oldValue, final Object newValue);

    /**
     * @param listener
     */
    void removeChangeListener(final PropertyChangeListener listener);

    /**
     * Remove the allowed language from the property list from this VDB
     * @param name the language name
     * 
     * @return whether the property was successfully removed
     */
    boolean removeAllowedLanguage(String name);

    /**
     * @param dataRoleToRemove
     *        the name of the data role being removed (cannot be empty)
     * @return <code>true</code> if successfully removed
     */
    boolean removeDataRole(final String dataRoleToRemove);

    /**
     * @param entry
     * @return <code>true</code> if successfully removed
     */
    boolean removeEntry(final VdbEntry entry);

    /**
     * @param translatorOverride the translator override being removed (may not be <code>null</code>)
     * @return <code>true</code> if successfully removed
     */
    boolean removeTranslator(TranslatorOverride translatorOverride);

    /**
     * @param translatorToRemove
     *        the name of the translator being removed (cannot be empty)
     * @return <code>true</code> if successfully removed
     */
    boolean removeTranslator(final String translatorToRemove);

    /**
     * Remove the given {@link VdbImportVdbEntry entry} from this VDB
     * 
     * @param entry
     * @return <code>true</code> if successfully removed
     */
    boolean removeImport(VdbImportVdbEntry entry);

    /**
     * @param importToRemove
     *        the name of the VDB import being removed (cannot be empty)
     * @return <code>true</code> if successfully removed
     */
    boolean removeImport(final String importToRemove);

    /**
     * Remove the given {@link VdbImportVdbEntry entry} from this VDB
     */
    void removeAllImports();

    /**
     * Must not be called unless this VDB has been {@link #isModified() modified}
     *
     * @throws Exception
     */
    void save() throws Exception;

    /**
     * @param description Sets description to the specified value.
     */
    void setDescription(String description);

    /**
     * @param source
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    void setModified(final Object source, final String propertyName, final Object oldValue, final Object newValue);

    /**
     * @param valueInSeconds Sets query time-out to the specified value.
     */
    void setQueryTimeout(int valueInSeconds);
    
    /**
     * @param status 
     * 
     */
    void setStatus(IStatus status);

    /**
     * @param intVersion version of vdb
     */
    void setVersion(int intVersion);

    /**
     * @param valVersion Sets validatationVersion to the specified value.
     */
    void setValidationVersion(String valVersion);

    /**
     * @param dateTime Sets validatationDateTime to the specified value.
     */
    void setValidationDateTime(Date dateTime);

    /**
     * @param newValue Sets security-domain to the specified value.
     */
    void setSecurityDomain(String newValue);

    /**
     * @param newValue Sets gss-pattern to the specified value.
     */
    void setGssPattern(String newValue);

    /**
     * @param newValue Sets password-pattern to the specified value.
     */
    void setPasswordPattern(String newValue);

    /**
     * @param newValue Sets query time-out to the specified value.
     */
    void setAuthenticationType(String newValue);

    /**
     * @throws Exception
     */
    void synchronize() throws Exception;

    /**
     *
     */
    interface Event {

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the
         * {@link #getDescription() description} in a VDB is changed
         */
        String DESCRIPTION = "description"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an entry
         * is added to a VDB
         */
        String ENTRY_ADDED = "entryAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * {@link #removeEntry(VdbEntry) entry is removed} from a VDB
         */
        String ENTRY_REMOVED = "entryRemoved"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * entry's {@link VdbEntry#getChecksum() checksum} changes
         */
        String ENTRY_CHECKSUM = "entry.checksum"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * entry's {@link VdbEntry#getSynchronization() synchronization} changes
         */
        String ENTRY_SYNCHRONIZATION = "entry.synchronization"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * entry's {@link VdbEntry#getDescription() description} changes
         */
        String ENTRY_DESCRIPTION = "entry.description"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * model entry's {@link VdbModelEntry#isVisible() visibility} changes
         */
        String MODEL_VISIBLE = "modelentry.visible"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() source name} changes
         */
        String MODEL_SOURCE_NAME = "modelentry.sourceName"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() source translator name} changes
         */
        String MODEL_TRANSLATOR = "modelEntry.translator"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() source JNDI name} changes
         */
        String MODEL_JNDI_NAME = "modelEntry.jndiName"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() data} changes
         */
        String MODEL_SOURCES = "modelEntry.sources"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an data policy is
         * added to a VDB
         * 
         * @see #addDataRole(DataRole)
         */
        String DATA_POLICY_ADDED = "dataPolicyAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * {@link #removeDataRole(String) entry is removed} from a VDB
         */
        String DATA_POLICY_REMOVED = "dataPolicyRemoved"; //$NON-NLS-1$

        /**
         * The property name sent in events to  {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * import VDB entry's {@link VdbImportVdbEntry#getVersion version} changes
         */
        String IMPORT_VDB_ENTRY_VERSION = "importVdbEntryVersion"; //$NON-NLS-1$

        /**
         * The property name sent in events to  {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * import VDB entry's {@link VdbImportVdbEntry#isImportDataPolicies() data policy flag} changes
         */
        String IMPORT_VDB_ENTRY_DATA_POLICY = "importVdbEntryDataPolicies"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * import VDB entry is added 
         */
        String IMPORT_VDB_ENTRY_ADDED = "importVdbEntryAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a 
         * VDBs udf jar entries are changed.
         */
        String UDF_JARS_MODIFIED = "udfJarsModified"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * import VDB entry is removed.
         * 
         */
        String IMPORT_VDB_ENTRY_REMOVED = "importVdbEntryRemoved"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB is
         * {@link #close() closed}
         */
        String CLOSED = "closed"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB is
         * {@link #save() save}
         */
        String SAVED = "saved"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a translator
         * is added.
         * 
         */
        String TRANSLATOR_OVERRIDE_ADDED = "translatorOverrideAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a translator
         * override property is added, changed, or removed.
         * 
         */
        String TRANSLATOR_PROPERTY = "translatorOverrideProperty"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a translator
         * is removed.
         * 
         */
        String TRANSLATOR_OVERRIDE_REMOVED = "translatorOverrideRemoved"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the query timeout
         * is changed.
         * 
         */
        String QUERY_TIMEOUT = "queryTimeout"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the vdb version
         * is changed.
         * 
         */
        String VERSION = "version"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when auto-generate REST WAR
         * is changed.
         * @since 8.2
         * 
         */
        String AUTO_GENERATE_REST_WAR = "autoGenerateRESTWAR"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a general property
         * is changed.
         */
        String GENERAL_PROPERTY = "generalProperty"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the allowed-languages
         * is changed.
         */
        String ALLOWED_LANGUAGES = "allowed-languages"; //$NON-NLS-1$

        /**
         */
        String SECURITY_DOMAIN = "securityDomain"; //$NON-NLS-1$

        /**
         */
        String GSS_PATTERN = "gssPattern"; //$NON-NLS-1$

        /**
         */
        String PASSWORD_PATTERN = "passwordPattern"; //$NON-NLS-1$

        /**
         */
        String AUTHENTICATION_TYPE = "authenticationType"; //$NON-NLS-1$
    }

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
        
        /**
         */
        String USE_CONNECTOR_METADATA = "UseConnectorMetadata"; //$NON-NLS-1$
    }

    /**
     * @param vdbImport
     * @return TODO
     */
    boolean addImport(VdbImportVdbEntry vdbImport);

    /**
     * Add an import VDB attribute to this VDB.
     * 
     * @param importVdbName
     * 
     * @return the newly added import entry
     */
    VdbImportVdbEntry addImport(String importVdbName);

    /**
     * @param model
     *        the name of the VDB being imported (cannot be empty)
     */
    void addDynamicModel(DynamicModel model);

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
     * @return the value of the <code>connection type</code> property (can be empty)
     */
    String getConnectionType();

    /**
     * @return the VDB models (never <code>null</code> but can be empty)
     */
    Collection<DynamicModel> getDynamicModels();

    /**
     * @return the folder where the workings of the vdb are staged either
     *                having been loaded from zip or by artifacts copied to it when
     *                the vdb is synchronized
     */
    File getStagingFolder();

    /**
     * @return source file of the vdb
     */
    IFile getSourceFile();

    /**
     * @return the source file name.
     *                 Convenience method equivalent to {@link #getSourceFile()}.getName()
     */
    String getFileName();

    /**
     * @param modelToRemove
     *        the name of the model being removed (cannot be empty)
     */
    void removeDynamicModel(final String modelToRemove);

    /**
     * @param newConnectionType
     *        the new value of the <code>connection type</code> property
     */
    void setConnectionType(final String newConnectionType);

    /**
     * @param newPreview
     *        the new value for the <code>preview</code> property
     * @see #DEFAULT_PREVIEW
     */
    void setPreview(final boolean newPreview);

    /**
     * Sets the name used by Teiid to reference this VDB.
     *
     * @param newVdbName
     *        the new value of the <code>Teiid VDB name</code> property
     */
    void setName(final String newVdbName);

    /**
     * @return value
     */
    boolean isAutoGenerateRESTWar();

    /**
     * @param autoGenerateRESTWar
     */
    void setAutoGenerateRESTWar(boolean autoGenerateRESTWar);

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
     * @return removed property
     */
    String removeProperty(String key);

    /**
     * Convert this vdb into the given type of vdb
     * 
     * @param vdbType the class of the vdb required (should be either {@link DynamicVdb} or {@link XmiVdb})
     * @param destination location for the conversion
     * @param options any option settings to apply within the conversion
     * @return vdb converted to this type. If the type is the same type
     *                 as this vdb then this will be returned.
     * @throws Exception 
     *
     */
    <V extends Vdb> V convert(Class<V> vdbType, IFile destination, Properties options) throws Exception;

    /**
     * @return the comments associated with this vdb
     */
    CommentSets getComments();

    /**
     * @param propertyName
     *
     * @return the comments associated with the property with the given name
     */
    CommentSets getPropertyComments(String propertyName);
}
