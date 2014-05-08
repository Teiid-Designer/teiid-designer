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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.VdbModelBuilder;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.VdbFileEntry.FileEntryType;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ImportVdbElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.TranslatorElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

/**
 * 
 *
 * @since 8.0
 */
@ThreadSafe
// TODO: File constructor
public final class Vdb {

    /**
     * The file extension of VDBs including the dot. ( {@value} )
     */
    public static final String FILE_EXTENSION = ".vdb"; //$NON-NLS-1$

    /**
     * The file extension of a VDB but does not include the dot. ( {@value} )
     */
    public static final String FILE_EXTENSION_NO_DOT = "vdb"; //$NON-NLS-1$

    private static final String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$
    
    private static final int DEFAULT_TIMEOUT = 0;

    /**
     * The prefix used before the workspace identifier when creating a Preview VDB name.
     */
    public static final String PREVIEW_PREFIX = "PREVIEW_"; //$NON-NLS-1$

    /**
     * @param resource the resource whose Preview VDB prefix is being requested (cannot be <code>null</code>)
     * @return the Preview VDB prefix (never <code>null</code>)
     */
    public static String getPreviewVdbPrefix( IResource resource ) {
        CoreArgCheck.isNotNull(resource, "resource is null"); //$NON-NLS-1$
        char delim = '_';
        StringBuilder name = new StringBuilder(PREVIEW_PREFIX + ModelerCore.workspaceUuid().toString() + delim);

        if (resource instanceof IFile) {
            IPath path = resource.getParent().getFullPath();

            for (String segment : path.segments()) {
                name.append(segment).append(delim);
            }
        }

        String prefix = name.toString();

        if (prefix.contains(StringConstants.SPACE)) {
            prefix = prefix.replaceAll(StringConstants.SPACE, StringConstants.UNDERSCORE);
        }

        return prefix;
    }

    final IFile file;

    private final File folder;
    final CopyOnWriteArraySet<VdbEntry> entries = new CopyOnWriteArraySet<VdbEntry>();
    final CopyOnWriteArraySet<VdbModelEntry> modelEntries = new CopyOnWriteArraySet<VdbModelEntry>();
    final CopyOnWriteArraySet<VdbDataRole> dataPolicyEntries = new CopyOnWriteArraySet<VdbDataRole>();
    final CopyOnWriteArraySet<VdbImportVdbEntry> importModelEntries = new CopyOnWriteArraySet<VdbImportVdbEntry>();
    final Set<TranslatorOverride> translatorOverrides = new TreeSet<TranslatorOverride>(new Comparator<TranslatorOverride>() {
        @Override
        public int compare( TranslatorOverride translator1,
                            TranslatorOverride translator2 ) {
            return translator1.getName().compareTo(translator2.getName());
        }
    });
    final Map<String, String> generalPropertiesMap = new HashMap<String, String>();
    final Set<String> allowedLanguages = new TreeSet<String>(new Comparator<String>() {
        @Override
        public int compare( String str1, String str2 ) {
            return str1.compareTo(str2);
        }
    });
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    final AtomicBoolean modified = new AtomicBoolean();
    private final AtomicReference<String> description = new AtomicReference<String>();
    private final boolean preview;
    private final int version;
    private int queryTimeout = DEFAULT_TIMEOUT;
    private boolean autoGenerateRESTWAR;
    private Date validateDateTime;
    private String validationVersion;
    private String securityDomain;
    private String gssPattern;
    private String passwordPattern;
    private String authenticationType;
    
    private VdbModelBuilder builder;
    private Map<String, Set<String>> modelToImportVdbMap = new HashMap<String, Set<String>>();

    /**
     * @param file
     * @param preview indicates if this is a Preview VDB
     * @param monitor
     */
    public Vdb( final IFile file,
                final boolean preview,
                final IProgressMonitor monitor ) {
    	this.builder = new VdbModelBuilder();
        this.file = file;
        // Create folder for VDB in state folder
        folder = VdbPlugin.singleton().getStateLocation().append(file.getFullPath()).toFile();
        folder.mkdirs();

        // Open archive and populate model entries
        if (file.getLocation().toFile().length() == 0L) {
            this.preview = preview;
            this.version = 1;
            return;
        }

        final boolean[] previewable = new boolean[1];
        final int[] vdbVersion = new int[1];
        final int[] queryTimeout = new int[1];
        final String[] valDateTime = new String[1];
        final String[] valVersion = new String[1];
        final boolean[] autoGen = new boolean[1];
        final String[] secDomain = new String[1];
        final String[] gssPatt = new String[1];
        final String[] pwdPatt = new String[1];
        final String[] authType = new String[1];

        OperationUtil.perform(new Unreliable() {

            ZipFile archive = null;
            InputStream entryStream = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (entryStream != null) entryStream.close();
                if (archive != null) archive.close();
            }

            @Override
            public void tryToDo() throws Exception {
                archive = new ZipFile(file.getLocation().toString());
                for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                    final ZipEntry zipEntry = iter.nextElement();
                    entryStream = archive.getInputStream(zipEntry);
                    if (zipEntry.getName().equals(MANIFEST)) {
                        // Initialize using manifest
                        final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                        unmarshaller.setSchema(getManifestSchema());
                        final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(entryStream);
                        setDescription(manifest.getDescription());
                        vdbVersion[0] = manifest.getVersion();
                        // VDB properties
                        for (final PropertyElement property : manifest.getProperties()) {
                            final String name = property.getName();
                            final String value = property.getValue();
                            
                            if (Xml.PREVIEW.equals(name)) {
                            	previewable[0] = Boolean.parseBoolean(value);
                                // The stored timeout is in milliseconds. We are converting to seconds for display in Designer
                            } else if (Xml.QUERY_TIMEOUT.equals(name)) { 
                                int timeoutMillis = Integer.parseInt(value);
                                if (timeoutMillis > 0) {
                                    queryTimeout[0] = timeoutMillis / 1000;
                                }
                            } else if(Xml.ALLOWED_LANGUAGES.equals(name) ) {
                            	/*
                            	 *  EXAMPLE XML FRAGMENT
                            	 *  multiple properties allowed with SAME KEY different values
                            	 *  Need to discover and treat these differently
								    <property name="allowed-languages" value="javascript, perl, php"/>
                            	 */
                            	String[] langs = StringUtilities.parseCommaDelimitedString(value);
                            	for( String lang : langs ) {
                            		allowedLanguages.add(lang);
                            	}
                            } else if (Xml.VALIDATION_DATETIME.equals(name)) { 
                            	valDateTime[0] = value;
                            } else if (Xml.VALIDATION_VERSION.equals(name)) { 
                                valVersion[0] = value;
                            } else if (Xml.SECURITY_DOMAIN.equals(name)) { 
                                secDomain[0] = value;
                            } else if (Xml.GSS_PATTERN.equals(name)) { 
                                gssPatt[0] = value;
                            } else if (Xml.PASSWORD_PATTERN.equals(name)) { 
                                pwdPatt[0] = value;
                            } else if (Xml.AUTHENTICATION_TYPE.equals(name)) { 
                                authType[0] = value;
                            } else if (Xml.AUTO_GENERATE_REST_WAR.equals(name)) {
                            	autoGen[0] = Boolean.parseBoolean(value);
                                // The stored timeout is in milliseconds. We are converting to seconds for display in Designer
                            } else {
                            	generalPropertiesMap.put(name, value);
                            }
                        }
                        
                        for (final ModelElement element : manifest.getModels()) 
                            modelEntries.add(new VdbModelEntry(Vdb.this, element, monitor));

                        // Initialize model entry imports only after all model entries have been created
                        for (final VdbModelEntry entry : modelEntries) {
                            entry.initializeImports();
                        }
                                                
                        for (final EntryElement element : manifest.getEntries()) {
                            entries.add(new VdbFileEntry(Vdb.this, element, monitor));
                        }
                        
                        // Vdb Import entries
                        for (final ImportVdbElement element : manifest.getImportVdbEntries()) {
                        	importModelEntries.add(new VdbImportVdbEntry(Vdb.this, element));
                        }
                        
                        // load translator overrides
                        for (final TranslatorElement translatorElement : manifest.getTranslators()) {
                            translatorOverrides.add(new TranslatorOverride(Vdb.this, translatorElement));
                        }

                        for (final DataRoleElement element : manifest.getDataPolicies()) {
                            dataPolicyEntries.add(new VdbDataRole(Vdb.this, element));
                        }
                    } else FileUtils.copy(entryStream, new File(getFolder(), zipEntry.getName()));
                }
                modified.set(false);
            }
        });
        this.preview = previewable[0];
        this.version = vdbVersion[0];
        this.queryTimeout = queryTimeout[0];
        this.autoGenerateRESTWAR = autoGen[0];
        this.securityDomain = secDomain[0];
        this.gssPattern = gssPatt[0];
        this.passwordPattern = pwdPatt[0];
        this.authenticationType = authType[0];
        if( valDateTime[0] != null ) {
        	try {
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //$NON-NLS-1$
				this.validateDateTime = format.parse(valDateTime[0]); //new Date(valDateTime[0]); //DateUtil.convertStringToDate(valDateTime[0]);
			} catch (ParseException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
        }
        this.validationVersion = valVersion[0];
    }

    /**
     * @param file
     * @param monitor
     */
    public Vdb( final IFile file,
                final IProgressMonitor monitor ) {
        this(file, false, monitor);
    }

    /**
     * @param dataPolicy
     * @param monitor
     * @return the new data policy
     */
    public final VdbDataRole addDataPolicy(
    										final DataRole dataPolicy, 
                                            final IProgressMonitor monitor ) {
        VdbDataRole policy = new VdbDataRole(this, dataPolicy, monitor);
        dataPolicyEntries.add(policy);
        setModified(this, Event.DATA_POLICY_ADDED, policy, null);
        return policy;
    }

    /**
     * @param listener
     */
    public final void addChangeListener( final PropertyChangeListener listener ) {
        listeners.addIfAbsent(listener);
    }

    /**
     * @param name
     * @param monitor
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     */
    public final VdbEntry addEntry( final IPath name,
                                    final IProgressMonitor monitor ) {
        return addEntry(new VdbEntry(this, name, monitor), entries, monitor);
    }
    
    /**
     * @param name
     * @param entryType the type of file entry being added
     * @param monitor
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     */
    public final VdbEntry addFileEntry( final IPath name,
                                        final VdbFileEntry.FileEntryType entryType,
                                        final IProgressMonitor monitor ) {
        return addEntry(new VdbFileEntry(this, name, entryType, monitor), entries, monitor);
    }

    private <T extends VdbEntry> T addEntry( final T entry,
                                             final Set<T> entries,
                                             final IProgressMonitor monitor ) {
        // Return existing entry if it exists
        if (!entries.add(entry)) {
            for (final T existingEntry : entries) {
                if (existingEntry.equals(entry))
                    return existingEntry;
            }
        }

        // Mark VDB as modified
        setModified(this, Event.ENTRY_ADDED, null, entry);
        return entry;
    }

    /**
     * @param name
     * @param monitor
     * @return the newly added {@link VdbModelEntry model entry}, or the existing entry with the supplied name.
     */
    public final VdbModelEntry addModelEntry( final IPath name,
                                              final IProgressMonitor monitor ) {
        VdbModelEntry modelEntry = new VdbModelEntry(this, name, monitor);
        VdbModelEntry addedEntry = addEntry(modelEntry, modelEntries, monitor);

        // entry did not exist in VDB
        if (modelEntry == addedEntry) {
            modelEntry.synchronizeModelEntry(monitor);
        } else {
            // entry already existed in VDB
            modelEntry = addedEntry;
        }

        return modelEntry;
    }

    /**
     * @param translatorOverride the translator override (may not be <code>null</code>)
     * @param monitor the progress monitor (may be <code>null</code>)
     * @return <code>true</code> if successfully added
     */
    public final boolean addTranslator( TranslatorOverride translatorOverride,
                                        IProgressMonitor monitor ) {
        if (this.translatorOverrides.add(translatorOverride)) {
            setModified(this, Event.TRANSLATOR_OVERRIDE_ADDED, null, translatorOverride);
            return true;
        }

        return false;
    }
    
    /**
     * Add an import VDB attribute to this VDB.
     * 
     * @param importVdbName
     * 
     * @return whether the import vdb attribute was successfully added
     */
    public final boolean addImportVdb(String importVdbName) {
    	if (this.importModelEntries.add(new VdbImportVdbEntry(this, importVdbName))) {
    		setModified(this, Event.IMPORT_VDB_ENTRY_ADDED, null, importVdbName);
            return true;
    	}
    	
    	return false;
    }
    
    /**
     * Add an allowed language property
     * 
     * @param name
     * 
     * @return whether the import vdb attribute was successfully added
     */
    public final boolean addAllowedLanguage(String name) {
    	if( this.allowedLanguages.add(name) ) {
    		setModified(this, Event.ALLOWED_LANGUAGES, name, name);
            return true;
    	}
    	return false;
    }

    /**
     * add general name-value pair property to VDB
     * @param key
     * @param value
     * @return if value was added or not
     */
    public final boolean setGeneralProperty(String key, String value) {
    	if( value == null ) {
    		return removeGeneralProperty(key, value);
    	}
    	String oldValue = this.generalPropertiesMap.put(key, value);
    	if( oldValue == null ) {
    		setModified(this, Event.GENERAL_PROPERTY, null, value);
    		return true;
    	} else if( ! oldValue.equals(value)) {
    		setModified(this, Event.GENERAL_PROPERTY, oldValue, value);
    		return true;
    	}
    	
    	return false;
    }

    /**
     * Synchronize the Vdb file entries.  The supplied entries must be included - it's VdbModelEntry
     * may not exist in the vdb yet.
     * @param newJarEntries the supplied new entries which must exist 
     */
    public final void synchronizeUdfJars(Set<VdbFileEntry> newJarEntries) {
        // Init list of all required Udf jars with supplied list
        Set<VdbFileEntry> allRequiredUdfJars = new HashSet<VdbFileEntry>(newJarEntries);
        
        // Add other Udf jars used by current Model entries
        for(VdbModelEntry entry: modelEntries) {
            Set<VdbFileEntry> jarEntries = entry.getUdfJars();
            allRequiredUdfJars.addAll(jarEntries);
        }
        
        // Create map of required jarName to its jar entry
        Map<String,VdbFileEntry> allRequiredJarsMap = new HashMap<String,VdbFileEntry>();
        for(VdbFileEntry fileEntry: allRequiredUdfJars) {
            allRequiredJarsMap.put(fileEntry.getName().toString(), fileEntry);
        }
        
        // Get the current Udf jar names for this vdb
        Set<String> currentUdfJarNames = getUdfJarNames();
        
        boolean jarsAdded = false;
        // Add any missing Udf jars to the vdb that are required
        for(VdbFileEntry modelUdfJar: allRequiredUdfJars) {
            if(!currentUdfJarNames.contains(modelUdfJar.getName().toString())) {
                entries.add(modelUdfJar);
                jarsAdded = true;
            }
        }
        
        // Remove any Udf jars that are no longer needed
        boolean jarsRemoved = false;
        currentUdfJarNames = getUdfJarNames();
        for(String currentJarName: currentUdfJarNames) {
            Set<String> allRequiredJarNames = allRequiredJarsMap.keySet();
            if(!allRequiredJarNames.contains(currentJarName)) {
                for(VdbEntry entry: entries) {
                    String entryName = entry.getName().toString();
                    if(entryName!=null && entryName.equals(currentJarName)) {
                        entries.remove(entry);
                        break;
                    }
                }
                jarsRemoved = true;
            }
        }
        
        if(jarsAdded || jarsRemoved) {
            setModified(this, Event.UDF_JARS_MODIFIED, null, null);
        }
    }
    
    /**
     * 
     */
    public final void close() {
        entries.clear();
        modelEntries.clear();
        listeners.clear();
        description.set(StringConstants.EMPTY_STRING);
        // Clean up state folder
        FileUtils.removeDirectoryAndChildren(VdbPlugin.singleton().getStateLocation().append(file.getFullPath().segment(0)).toFile());
        // Mark VDB as unmodified
        if (isModified()) modified.set(false);
        // Notify change listeners VDB is closed
        notifyChangeListeners(this, Event.CLOSED, null, null);
    }
    
    /**
     * 
     * @return the immutable set of allowed-languages strings
     */
    public final Set<String> getAllowedLanguages() {
    	return Collections.unmodifiableSet(allowedLanguages);
    }

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    public final Set<VdbDataRole> getDataPolicyEntries() {
        return Collections.unmodifiableSet(dataPolicyEntries);
    }

    /**
     * @return description
     */
    public final String getDescription() {
        return description.get();
    }

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    public final Set<VdbEntry> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * Get the current set of UDF jar entries.  These are the current entries that begin with the UDF path prefix
     * @return the set of VdbEntry UDF jar objects
     */
    public final Set<VdbEntry> getUdfJarEntries() {
        // Get all non-model entries
        final Set<VdbEntry> entries = getEntries();
        Set<VdbEntry> udfJarEntries = new HashSet<VdbEntry>();
        
        // The list of UserFiles are those that begin with the UDF path prefix
        for(VdbEntry entry: entries) {
            // Name of VDB entry
            String zipName = entry.getName().toString();
            // Strip off any leading delimiter if it exists.
            if(zipName!=null && zipName.startsWith("/") ) { //$NON-NLS-1$
                zipName = zipName.substring(1, zipName.length());
            }
            if(zipName.startsWith(VdbFolders.UDF.getReadFolder())) {
                udfJarEntries.add(entry);
            }
        }
        return Collections.unmodifiableSet(udfJarEntries);
    }
    
    /**
     * Get the current set of UDF jar entries.  These are the current entries that begin with the UDF path prefix
     * @return the set of VdbEntry UDF jar objects
     */
    public final Set<String> getUdfJarNames() {
        // Get all non-model entries
        final Set<VdbEntry> entries = getEntries();
        Set<String> udfJarNames = new HashSet<String>();
        
        // The list of UserFiles are those that begin with the UDF path prefix
        for(VdbEntry entry: entries) {
            // Name of VDB entry
            String entryName = entry.getName().toString();
            String entryShortenedName = entryName;
            // Strip off any leading delimiter if it exists.
            if(entryName!=null && entryName.startsWith("/") ) { //$NON-NLS-1$
                entryShortenedName = entryName.substring(1, entryName.length());
            }
            if(entryShortenedName.startsWith(VdbFolders.UDF.getReadFolder())) {
                udfJarNames.add(entryName);
            }
        }
        return Collections.unmodifiableSet(udfJarNames);
    }

    /**
     * Get the current set of UserFile entries.  These are the current entries that begin with the UserFile path prefix
     * @return the set of VdbEntry userFile objects
     */
    public final Set<VdbEntry> getUserFileEntries() {
        // Get all non-model entries
        final Set<VdbEntry> entries = getEntries();
        Set<VdbEntry> userFileEntries = new HashSet<VdbEntry>();
        
        // Narrow list of UserFiles based on path prefix in the VDB
        for(VdbEntry entry: entries) {
            // Name of VDB entry
            String zipName = entry.getName().toString();
            // Strip off any leading delimiter if it exists.
            if(zipName!=null && zipName.startsWith("/") ) { //$NON-NLS-1$
                zipName = zipName.substring(1, zipName.length());
            }
            userFileEntries.add(entry);
        }
        return userFileEntries;
    }
    
    /**
     * @return the map of general VDB properties
     */
    public final Map<String, String> getGeneralProperties() {
    	return Collections.unmodifiableMap(generalPropertiesMap);
    }

    /**
     * @return The workspace file that represents this VDB
     */
    public final IFile getFile() {
        return file;
    }

    final File getFolder() {
        return folder;
    }

    JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] { VdbElement.class });
    }

    Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
    }

    /**
     * @return the immutable set of model entries within this VDB
     */
    public final Set<VdbModelEntry> getModelEntries() {
        final Set<VdbModelEntry> entries = new HashSet<VdbModelEntry>();
        for (final VdbModelEntry entry : modelEntries)
            if (!entry.isBuiltIn()) entries.add(entry);
        return Collections.unmodifiableSet(entries);
    }
    
    /**
     * @return the immutable set of import vdb entries within this VDB
     */
    public final Collection<VdbImportVdbEntry> getImportVdbEntries() {
    	 return Collections.unmodifiableSet(importModelEntries);
    }

    /**
     * Method to return the File objects associated with each model in this VDB.
     * The intention is to allow the Data Policy wizard to display contents of these models in EMF form so users can 
     * pick/chose and set-up their data entitlements.
     * 
     * @return the immutable list of model files within this VDB
     */
    public final Collection<File> getModelFiles() {
        final Collection<File> modelFiles = new ArrayList<File>();

        for (VdbModelEntry modelEntry : getModelEntries()) {
            IPath modelPath = new Path(folder.getAbsolutePath() + modelEntry.getName());
            modelFiles.add(modelPath.toFile());
        }

        return Collections.unmodifiableCollection(modelFiles);
    }

    /**
     * @return the name of this VDB
     */
    public final IPath getName() {
        return file.getFullPath();
    }
    
    /**
     * @param vdbName the name of the imported vdb
     * @return the <code>VdbImportVdbEntry</code>
     */
    private final VdbImportVdbEntry getImportVdbEntry(String vdbName) {
    	for( VdbImportVdbEntry entry : getImportVdbEntries()) {
    		if( entry.getName().equalsIgnoreCase(vdbName)) {
    			return entry;
    		}
    	}
    	
    	return null;
    }
    
    private final void handleRemovedVdbModelEntry(String vdbModelEntryName) {
    	// Clean up import VDBs
    	// Assume that any registered vdb names for the model entry are stale
    	Set<String> existingSet = modelToImportVdbMap.get(vdbModelEntryName);
    	
    	
    	// If Set does not exist for modelName, create it
    	if( existingSet != null ) {
    		unregisterStaleImportVdbs(existingSet, new NullProgressMonitor());
    		modelToImportVdbMap.remove(vdbModelEntryName);
    	}
    }

    /**
     * @return <code>true</code> if this VDB has been modified since its creation of last {@link #save(IProgressMonitor) save}.
     */
    public final boolean isModified() {
        return modified.get();
    }

    /**
     * @return <code>true</code> if this is a Preview VDB
     */
    public final boolean isPreview() {
        return preview;
    }

    /**
     * @return the problem markers (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the problem markers
     */
    public IMarker[] getProblems() throws Exception {
        return file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    }

    /**
     * @return the immutable set of overridden translators within this VDB (never <code>null</code>)
     */
    public final Set<TranslatorOverride> getTranslators() {
        return Collections.unmodifiableSet(this.translatorOverrides);
    }

    /**
     * @return the VDB version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return the query timeout value for this VDB (in seconds)
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * @return the auto generate REST WAR value for this VDB
     * @since 8.2
     */
    public boolean isAutoGenerateRESTWAR() {
        return autoGenerateRESTWAR;
    }
    
    /**
     * @return the VDB validation version
     */
    public String getValidationVersion() {
        return validationVersion;
    }
    
    /**
     * @return the VDB validation date-time
     */
    public Date getValidationDateTime() {
        return validateDateTime;
    }
    
    /**
     * @return the VDB validation security-domain
     */
    public String getSecurityDomain() {
        return securityDomain;
    }
    
    /**
     * @return the VDB validation date-time
     */
    public String getGssPattern() {
        return gssPattern;
    }
    
    /**
     * @return the VDB validation date-time
     */
    public String getPasswordPattern() {
        return passwordPattern;
    }
    
    /**
     * @return the VDB validation date-time
     */
    public String getAuthenticationType() {
        return authenticationType;
    }
    
    /**
     * @return <code>true</code> if all model entries in this VDB are either synchronized with their associated models or no
     *         associated model exists..
     */
    public final boolean isSynchronized() {
        for (final VdbModelEntry entry : modelEntries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) return false;
        for (final VdbEntry entry : entries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) return false;
        return true;
    }

    void notifyChangeListeners( final Object source,
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
    public final void removeChangeListener( final PropertyChangeListener listener ) {
        listeners.remove(listener);
    }
    
    /**
     * @param importVdbNames the list of imported vdb names
     * @param modelName the model name (<code>IPath</code>) from the <code>VdbModelEntry</code>
     * @param monitor the progress monitor
     */
    public final void registerImportVdbs(Collection<String> importVdbNames, String modelName, IProgressMonitor monitor) {
    	Set<String> existingSet = modelToImportVdbMap.get(modelName);
    	Set<String> staleImportVdbs = new HashSet<String>();
    	
    	// If Set does not exist for modelName, create it
    	if( existingSet == null ) {
    		existingSet = new HashSet<String>();
    		modelToImportVdbMap.put(modelName, existingSet);
    	} else { // If set exists, then need to check collect any potential stale import vdb names
    		for( String importVdb : existingSet ) {
    			if( !importVdbNames.contains(importVdb)) {
    				staleImportVdbs.add(importVdb);
    			}
    		}
    	}
    	existingSet.addAll(importVdbNames);
    	
    	unregisterStaleImportVdbs(staleImportVdbs, monitor);
    	
    	// Only add the import if it doesn't already exist
    	for( String importVdbName : importVdbNames ) {
	    	if( getImportVdbEntry(importVdbName) == null ) {
	    		addImportVdb(importVdbName);
	    	}
    	}
    }
    
    /**
     * Remove the allowed language from the property list from this VDB
     * @param name the language name
     * 
     * @return whether the property was successfully removed
     */
    public final boolean removeAllowedLanguage(String name) {
    	if (this.allowedLanguages.remove(name) ) {
    		setModified(this, Event.ALLOWED_LANGUAGES, name, null);
    		return true;
    	}

    	return false;
    }
    

    /**
     * @param entry
     */
    public final void removeEntry( final VdbEntry entry ) {
        entry.dispose();
        if (entry instanceof VdbModelEntry) {
            String entryName = entry.getName().toString();
            modelEntries.remove(entry);

            synchronizeUdfJars(new HashSet<VdbFileEntry>());

            handleRemovedVdbModelEntry(entryName);
        }
        else entries.remove(entry);
        setModified(this, Event.ENTRY_REMOVED, entry, null);
        
    }

    /**
     * @param policy
     */
    public final void removeDataPolicy( final VdbDataRole policy ) {
        dataPolicyEntries.remove(policy);
        setModified(this, Event.DATA_POLICY_REMOVED, policy, null);
    }

    /**
     * @param translatorOverride the translator override being removed (may not be <code>null</code>)
     * @param monitor the progress monitor (may be <code>null</code>)
     * @return <code>true</code> if successfully removed
     */
    public final boolean removeTranslator( TranslatorOverride translatorOverride,
                                           IProgressMonitor monitor ) {
        if (this.translatorOverrides.remove(translatorOverride)) {
            setModified(this, Event.TRANSLATOR_OVERRIDE_REMOVED, translatorOverride, null);
            return true;
        }

        return false;
    }

    /**
     * Remove the given {@link VdbImportVdbEntry entry} from this VDB
     * 
     * @param entry
     * @param monitor
     * @return whether the entry was successfully removed
     */
    public final boolean removeImportVdb( VdbImportVdbEntry entry, IProgressMonitor monitor ) {
    	if (this.importModelEntries.remove(entry)) {
    		setModified(this, Event.IMPORT_VDB_ENTRY_REMOVED, entry, null);
    		return true;
    	}

    	return false;
    }
    
    /**
     * Remove the given {@link VdbImportVdbEntry entry} from this VDB
     * 
     */
    public final void removeAllImportVdbs() {
    	Collection<VdbImportVdbEntry> entries = new ArrayList<VdbImportVdbEntry>(this.importModelEntries);
    	for( VdbImportVdbEntry entry : entries ) {
	    	if (this.importModelEntries.remove(entry)) {
	    		setModified(this, Event.IMPORT_VDB_ENTRY_REMOVED, entry, null);
	    	}
    	}
    }
    
    /**
     * Remove the given property from this VDB
     * @param key the property key
     * @param value the current value to remove
     * @return whether the property was successfully removed
     */
    public final boolean removeGeneralProperty(String key, String value) {
    	if (this.generalPropertiesMap.remove(key) != null ) {
    		setModified(this, Event.GENERAL_PROPERTY, value, null);
    		return true;
    	}

    	return false;
    }
    
    /**
     * Must not be called unless this VDB has been {@link #isModified() modified}
     * 
     * @param monitor
     */
    public final void save( final IProgressMonitor monitor ) {
        // Build JAXB model
        final VdbElement vdbElement = new VdbElement(this);
        // Save archive
        final File tmpFolder = VdbPlugin.singleton().getStateLocation().toFile();
        OperationUtil.perform(new Unreliable() {

            ZipOutputStream out = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (out != null) out.close();
            }

            @Override
            public void tryToDo() throws Exception {
                final IPath path = file.getFullPath();
                final File tmpArchive = File.createTempFile(path.removeFileExtension().toString(),
                                                            '.' + path.getFileExtension(),
                                                            tmpFolder);
                tmpArchive.getParentFile().mkdirs();
                out = new ZipOutputStream(new FileOutputStream(tmpArchive));
                // Create VDB manifest
                final ZipEntry zipEntry = new ZipEntry(MANIFEST);
                zipEntry.setComment(getDescription());
                out.putNextEntry(zipEntry);
                try {
                    final Marshaller marshaller = getJaxbContext().createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.setSchema(getManifestSchema());
                    marshaller.marshal(vdbElement, out);
                } finally {
                    out.closeEntry();
                }
                // Clear all problem markers on VDB file
                for (final IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE))
                    marker.delete();

                // Save entries
                for (final VdbEntry entry : entries)
                    entry.save(out, monitor);
                for (final VdbModelEntry entry : modelEntries)
                    entry.save(out, monitor);

                // Close zip output stream so its fully writen and any locks are removed.
                out.close();
                out = null;
                // Replace archive in workspace with temporary archive
                final File archiveFile = ModelerCore.getWorkspace().getRoot().findMember(getName()).getLocation().toFile();
                if (!archiveFile.delete()) throw new RuntimeException(VdbPlugin.UTIL.getString("unableToDelete", archiveFile)); //$NON-NLS-1$
                if (!tmpArchive.renameTo(archiveFile)) throw new RuntimeException(
                                                                                  VdbPlugin.UTIL.getString("unableToRename", tmpArchive, archiveFile)); //$NON-NLS-1$
                // Mark as unmodified
                if (isModified()) modified.set(false);
                // Notify change listeners
                notifyChangeListeners(this, Event.SAVED, null, null);
            }
        });
    }

    /**
     * @param description Sets description to the specified value.
     */
    public final void setDescription( String description ) {
        if (StringUtilities.isEmpty(description)) description = null;
        final String oldDescription = this.description.get();
        if (StringUtilities.equals(description, oldDescription)) return;
        this.description.set(description);
        setModified(this, Event.DESCRIPTION, oldDescription, description);
    }

    void setModified( final Object source,
                      final String propertyName,
                      final Object oldValue,
                      final Object newValue ) {
        this.modified.set(true);
        notifyChangeListeners(source, propertyName, oldValue, newValue);
    }
    
    /**
     * @param valueInSeconds Sets query time-out to the specified value.
     */
    public final void setQueryTimeout( int valueInSeconds ) {
    	final int oldTimeout = this.queryTimeout;
    	if( oldTimeout == valueInSeconds ) return;
    	this.queryTimeout = valueInSeconds;
    	setModified(this, Event.QUERY_TIMEOUT, oldTimeout, valueInSeconds);
    }

    /**
     * @param autoGenerateRESTWAR Sets autoGenerateRESTWAR to the specified value.
     * @since 8.2
     */
    public final void setAutoGenerateRESTWAR( boolean autoGenerateRESTWAR ) {
    	final boolean oldValue = this.autoGenerateRESTWAR;
    	if( oldValue == autoGenerateRESTWAR ) return;
    	this.autoGenerateRESTWAR = autoGenerateRESTWAR;
    	setModified(this, Event.AUTO_GENERATE_REST_WAR, oldValue, autoGenerateRESTWAR);
    }
    
    /**
     * @param valVersion Sets validatationVersion to the specified value.
     */
    public final void setValidationVersion( String valVersion ) {
    	final String oldVersion = this.validationVersion;
    	if( StringUtilities.equals(oldVersion, valVersion)) return;
    	this.validationVersion = valVersion;
    	setModified(this, Event.GENERAL_PROPERTY, oldVersion, valVersion);
    }
    
    
    /**
     * @param dateTime Sets validatationDateTime to the specified value.
     */
    public final void setValidationDateTime( Date dateTime ) {
    	final Date oldDateTime = this.validateDateTime;
    	if( oldDateTime != null && oldDateTime.equals(dateTime)) return;
    	this.validateDateTime = dateTime;
    	setModified(this, Event.GENERAL_PROPERTY, oldDateTime, dateTime);
    }
    
    /**
     * @param newValue Sets security-domain to the specified value.
     */
    public final void setSecurityDomain( String newValue ) {
    	final String old = this.securityDomain;
    	if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
    	if( newValue.length() == 0 ) {
    		this.securityDomain = null;
    	} else {
    		this.securityDomain = newValue;
    	}
    	setModified(this, Event.SECURITY_DOMAIN, old, newValue);
    }
    
    /**
     * @param newValue Sets gss-pattern to the specified value.
     */
    public final void setGssPattern( String newValue ) {
    	final String old = this.gssPattern;
    	if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
    	if( newValue.length() == 0 ) {
    		this.gssPattern = null;
    	} else {
    		this.gssPattern = newValue;
    	}
    	setModified(this, Event.GSS_PATTERN, old, newValue);
    }
    
    /**
     * @param newValue Sets password-pattern to the specified value.
     */
    public final void setPasswordPattern( String newValue ) {
    	final String old = this.passwordPattern;
    	if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
    	if( newValue.length() == 0 ) {
    		this.passwordPattern = null;
    	} else {
    		this.passwordPattern = newValue;
    	}
    	setModified(this, Event.PASSWORD_PATTERN, old, newValue);
    }
    
    /**
     * @param newValue Sets query time-out to the specified value.
     */
    public final void setAuthenticationType( String newValue ) {
    	final String old = this.authenticationType;
    	if( (old == null && (newValue == null || newValue.length() == 0)) || newValue.equals(old) ) return;
    	if( newValue.length() == 0 ) {
    		this.authenticationType = null;
    	} else {
    		this.authenticationType = newValue;
    	}
    	setModified(this, Event.AUTHENTICATION_TYPE, old, newValue);
    }

    private final void synchronize( final Collection<VdbEntry> entries,
                                    final IProgressMonitor monitor ) {
        for (final VdbEntry entry : entries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) entry.synchronize(monitor);
    }

    /**
     * @param monitor
     */
    public final void synchronize( final IProgressMonitor monitor ) {
    	getBuilder().start();

        synchronize(new HashSet<VdbEntry>(modelEntries), monitor);
        synchronize(entries, monitor);

        getBuilder().stop();
    }
    
    private final void unregisterStaleImportVdbs(Set<String> proposedStaleImportVdbs, IProgressMonitor monitor) {
    	Set<String> actualStaleImportVdbs = new HashSet<String>();
		for( String importVdb : proposedStaleImportVdbs ) {
			boolean keep = true;
			for( String modelName : modelToImportVdbMap.keySet() ) {
				Set<String> importVdbSet = modelToImportVdbMap.get(modelName);
				if( importVdbSet.contains(importVdb)) {
					keep = false;
					break;
				}
			}
			
			if( !keep ) {
				actualStaleImportVdbs.add(importVdb);
			}
		}
		
		for( String staleImportVdb : actualStaleImportVdbs ) {
			VdbImportVdbEntry entry = getImportVdbEntry(staleImportVdb);
			if( entry != null ) {
				removeImportVdb(entry, monitor);
			}
		}
    }
    
    
    /**
     * @return builder
     */
    public VdbModelBuilder getBuilder() {
    	return this.builder;
    }

    private boolean isXsdSchema(VdbModelEntry modelEntry) {
        return ModelUtil.MODEL_CLASS_XML_SCHEMA.equalsIgnoreCase(modelEntry.getModelClass());
    }

    /**
     * Moves any Xsd files in the model collection
     * into the otherFiles collection
     *
     * @param monitor
     */
    public void migrateXsdFiles(IProgressMonitor monitor) {
        Map<VdbModelEntry, VdbFileEntry> modelIndex = new HashMap<VdbModelEntry, VdbFileEntry>();

        /*
         * Cycle through all the model entries and if there are any
         * Xsd files then add them as file entries.
         *
         * Index them so that the obsolete model entries can be cleaned up.
         */
        for (VdbModelEntry modelEntry : modelEntries) {
            // No longer valid to make xsd files model entries so pick them out and
            // add them as normal entries instead
            if (!isXsdSchema(modelEntry))
                continue;

            VdbFileEntry entry = (VdbFileEntry) addFileEntry(modelEntry.getName(), FileEntryType.UserFile, monitor);
            if (entry != null) {
                modelIndex.put(modelEntry, entry);
            }
        }

        /*
         * Clean up the obsolete model entries
         * - replace import references
         */
        for (Entry<VdbModelEntry, VdbFileEntry> mapEntry : modelIndex.entrySet()) {
            VdbModelEntry oldEntry = mapEntry.getKey();
            VdbFileEntry newEntry = mapEntry.getValue();

            /* Update the import collections of entries that import this xsd */
            for (VdbModelEntry importByEntry : oldEntry.getImportedBy()) {
                importByEntry.replaceImport(oldEntry, newEntry);
            }

            /* Remove obsolete entry */
            modelEntries.remove(oldEntry);

            /* Delete the index of the xsd file */
            oldEntry.getIndexFile().delete();
        }
    }

    /**
     *
     */
    public static class Event {

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the
         * {@link #getDescription() description} in a VDB is changed
         */
        public static final String DESCRIPTION = "description"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an entry
         * is added to a VDB
         * 
         * @see #addEntry(IPath, IProgressMonitor)
         * @see #addModelEntry(IPath, IProgressMonitor)
         */
        public static final String ENTRY_ADDED = "entryAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * {@link #removeEntry(VdbEntry) entry is removed} from a VDB
         */
        public static final String ENTRY_REMOVED = "entryRemoved"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * entry's {@link VdbEntry#getChecksum() checksum} changes
         */
        public static final String ENTRY_CHECKSUM = "entry.checksum"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * entry's {@link VdbEntry#getSynchronization() synchronization} changes
         */
        public static final String ENTRY_SYNCHRONIZATION = "entry.synchronization"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * entry's {@link VdbEntry#getDescription() description} changes
         */
        public static final String ENTRY_DESCRIPTION = "entry.description"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * model entry's {@link VdbModelEntry#isVisible() visibility} changes
         */
        public static final String MODEL_VISIBLE = "modelentry.visible"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() source name} changes
         */
        public static final String MODEL_SOURCE_NAME = "modelentry.sourceName"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() source translator name} changes
         */
        public static final String MODEL_TRANSLATOR = "modelEntry.translator"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() source JNDI name} changes
         */
        public static final String MODEL_JNDI_NAME = "modelEntry.jndiName"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB
         * physical model entry's {@link VdbModelEntry#getSourceInfo() data} changes
         */
        public static final String MODEL_SOURCES = "modelEntry.sources"; //$NON-NLS-1$

        /**
	     * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an data policy is
	     * added to a VDB
         * 
         * @see #addDataPolicy(DataRole, IProgressMonitor)
         */
        public static final String DATA_POLICY_ADDED = "dataPolicyAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * {@link #removeDataPolicy(VdbDataRole) entry is removed} from a VDB
         */
        public static final String DATA_POLICY_REMOVED = "dataPolicyRemoved"; //$NON-NLS-1$

        /**
         * The property name sent in events to  {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * import VDB entry's {@link VdbImportVdbEntry#getVersion version} changes
         */
        public static final String IMPORT_VDB_ENTRY_VERSION = "importVdbEntryVersion"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to  {@link #addChangeListener(PropertyChangeListener) change listeners} when VDB
         * import VDB entry's {@link VdbImportVdbEntry#isImportDataPolicies() data policy flag} changes
         */
        public static final String IMPORT_VDB_ENTRY_DATA_POLICY =  "importVdbEntryDataPolicies"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * import VDB entry is added 
         */
        public static final String IMPORT_VDB_ENTRY_ADDED = "importVdbEntryAdded"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a 
         * VDBs udf jar entries are changed.
         */
        public static final String UDF_JARS_MODIFIED = "udfJarsModified"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when an
         * import VDB entry is removed.
         * 
         */
        public static final String IMPORT_VDB_ENTRY_REMOVED = "importVdbEntryRemoved"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB is
         * {@link #close() closed}
         */
        public static final String CLOSED = "closed"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a VDB is
         * {@link #save(IProgressMonitor) save}
         */
        public static final String SAVED = "saved"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a translator
         * is added.
         * 
         */
        public static final String TRANSLATOR_OVERRIDE_ADDED = "translatorOverrideAdded"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a translator
         * override property is added, changed, or removed.
         * 
         */
        public static final String TRANSLATOR_PROPERTY = "translatorOverrideProperty"; //$NON-NLS-1$

        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a translator
         * is removed.
         * 
         */
        public static final String TRANSLATOR_OVERRIDE_REMOVED = "translatorOverrideRemoved"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the query timeout
         * is changed.
         * 
         */
        public static final String QUERY_TIMEOUT = "queryTimeout"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when auto-generate REST WAR
         * is changed.
         * @since 8.2
         * 
         */
        public static final String AUTO_GENERATE_REST_WAR = "autoGenerateRESTWAR"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when a general property
         * is changed.
         */
        public static final String GENERAL_PROPERTY = "generalProperty"; //$NON-NLS-1$
        
        /**
         * The property name sent in events to {@link #addChangeListener(PropertyChangeListener) change listeners} when the allowed-languages
         * is changed.
         */
        public static final String ALLOWED_LANGUAGES = "allowed-languages"; //$NON-NLS-1$
        
        /**
         */
        public static final String SECURITY_DOMAIN = "securityDomain"; //$NON-NLS-1$
        
        /**
         */
        public static final String GSS_PATTERN = "gssPattern"; //$NON-NLS-1$
        
        /**
         */
        public static final String PASSWORD_PATTERN = "passwordPattern"; //$NON-NLS-1$
        
        /**
         */
        public static final String AUTHENTICATION_TYPE = "authenticationType"; //$NON-NLS-1$
    }

    /**
     * Constants relating to the vdb.xml file.
     */
    public static class Xml {

        /**
         */
        public static final String PREVIEW = "preview"; //$NON-NLS-1$
        
        /**
         */
        public static final String QUERY_TIMEOUT = "query-timeout"; //$NON-NLS-1$
        
        /**
         */
        public static final String AUTO_GENERATE_REST_WAR = "{http://teiid.org/rest}auto-generate"; //$NON-NLS-1$
        
        /**
         * 
         */
        public static final String ALLOWED_LANGUAGES = "allowed-languages"; //$NON-NLS-1$
        
        /**
         */
        public static final String VALIDATION_DATETIME = "validationDateTime"; //$NON-NLS-1$
        
        /**
         */
        public static final String VALIDATION_VERSION = "validationVersion"; //$NON-NLS-1$
        
        /*
        <property name="security-domain" value="teiid-spengo" />
		<property name="gss-pattern" value="{regex}" />
		<property name="password-pattern" value="{regex}" />
		<property name="authentication-type" value="GSS or USERPASSWORD" />
         */
        
        /**
         */
        public static final String SECURITY_DOMAIN = "security-domain"; //$NON-NLS-1$
        
        /**
         */
        public static final String GSS_PATTERN = "gss-pattern"; //$NON-NLS-1$
        
        /**
         */
        public static final String PASSWORD_PATTERN = "password-pattern"; //$NON-NLS-1$
        
        /**
         */
        public static final String AUTHENTICATION_TYPE = "authentication-type"; //$NON-NLS-1$
        
        /**
         */
        public static final String AUTHENTICATION_TYPE_GSS = "GSS"; //$NON-NLS-1$
        
        /**
         */
        public static final String AUTHENTICATION_TYPE_USERPASSWORD = "USERPASSWORD"; //$NON-NLS-1$
    }
}
