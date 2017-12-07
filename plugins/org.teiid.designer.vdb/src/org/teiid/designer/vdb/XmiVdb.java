/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.FileUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.ModelType;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.VdbModelBuilder;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.transformation.ddl.TeiidModelToDdlGenerator;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.VdbFileEntry.FileEntryType;
import org.teiid.designer.vdb.dynamic.DynamicModel;
import org.teiid.designer.vdb.dynamic.DynamicVdb;
import org.teiid.designer.vdb.dynamic.Metadata;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ImportVdbElement;
import org.teiid.designer.vdb.manifest.MetadataElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.TranslatorElement;
import org.teiid.designer.vdb.manifest.VdbElement;

import net.jcip.annotations.ThreadSafe;

/**
 * 
 *
 * @since 8.0
 */
@ThreadSafe
public final class XmiVdb extends BasicVdb {

    private static final String WORKING_FILES = "working-files"; //$NON-NLS-1$
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

    //
    // All lazily initialised through their own internal methods
    //
    private CopyOnWriteArraySet<VdbFileEntry> fileEntries;
    private CopyOnWriteArraySet<VdbFileEntry> udfJarEntries;
    private CopyOnWriteArraySet<VdbSchemaEntry> schemaEntries;
    private CopyOnWriteArraySet<VdbModelEntry> modelEntries;
    private VdbModelBuilder builder;

    private boolean excludeSourceMetadata;

    /**
     * Default constructor
     */
    public XmiVdb() {
        super();
    }

    /**
     * @param file
     * @param preview indicates if this is a Preview VDB
     * @throws Exception
     */
    public XmiVdb( final IFile file, final boolean preview ) throws Exception {
        super(file);
        setPreview(preview);
    }

    /**
     * @param file
     * @throws Exception
     */
    public XmiVdb( final IFile file) throws Exception {
        this(file, false);
    }

    

    private Set<VdbFileEntry> fileEntries() {
        if (fileEntries == null)
            fileEntries = new CopyOnWriteArraySet<VdbFileEntry>();

        return fileEntries;
    }

    private Set<VdbFileEntry> udfJarEntries() {
        if (udfJarEntries == null)
            udfJarEntries = new CopyOnWriteArraySet<VdbFileEntry>();

        return udfJarEntries;
    }

    private Set<VdbSchemaEntry> schemaEntries() {
        if (schemaEntries == null)
            schemaEntries = new CopyOnWriteArraySet<VdbSchemaEntry>();

        return schemaEntries;
    }

    private Set<VdbModelEntry> modelEntries() {
        if (modelEntries == null)
            modelEntries = new CopyOnWriteArraySet<VdbModelEntry>();

        return modelEntries;
    }

    private VdbModelBuilder builder() {
        if (builder == null)
            builder = new VdbModelBuilder();

        return builder;
    }

    @Override
    public void read(final IFile file) throws Exception {
        CoreArgCheck.isNotNull(file);

        if (! file.exists())
            return;

        // Open archive and populate model entries
        if (file.getLocation().toFile().length() == 0L) {
            return;
        }

        setSourceFile(file);

        final boolean[] previewable = new boolean[1];
        final String[] vdbVersion = new String[1];
        final int[] queryTimeout = new int[1];
        final String[] valDateTime = new String[1];
        final String[] valVersion = new String[1];
        final boolean[] autoGen = new boolean[1];
        final String[] secDomain = new String[1];
        final String[] gssPatt = new String[1];
        final String[] pwdPatt = new String[1];
        final String[] authType = new String[1];
        final String[] connType = new String[1];

        OperationUtil.perform(new Unreliable() {

            ZipFile archive = null;
            InputStream entryStream = null;

            @Override
            public void doIfFails() {
                //TODO
            }

            @Override
            public void finallyDo() throws Exception {
                if (entryStream != null) entryStream.close();
                if (archive != null) archive.close();
            }

            @Override
            public void tryToDo() throws Exception {
                XmiVdb vdb = XmiVdb.this;

                archive = new ZipFile(file.getLocation().toString());
                for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                    final ZipEntry zipEntry = iter.nextElement();
                    try {
                        entryStream = archive.getInputStream(zipEntry);
                        if (zipEntry.getName().equals(MANIFEST)) {
                            // Initialize using manifest
                            final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                            unmarshaller.setSchema(getManifestSchema());
                            final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(entryStream);
                            setDescription(manifest.getDescription());
                            vdbVersion[0] = manifest.getVersion();
                            vdb.setName(manifest.getName());
                            // VDB properties
                            for (final PropertyElement property : manifest.getProperties()) {
                                final String name = property.getName();
                                final String value = property.getValue();

                                if (Xml.PREVIEW.equals(name)) {
                                    previewable[0] = Boolean.parseBoolean(value);
                                    // The stored timeout is in milliseconds. We
                                    // are converting to seconds for display in
                                    // Designer
                                } else if (Xml.QUERY_TIMEOUT.equals(name)) {
                                    int timeoutMillis = Integer.parseInt(value);
                                    if (timeoutMillis > 0) {
                                        queryTimeout[0] = timeoutMillis / 1000;
                                    }
                                } else if (Xml.ALLOWED_LANGUAGES.equals(name)) {
                                    /*
                                     * EXAMPLE XML FRAGMENT multiple properties
                                     * allowed with SAME KEY different values
                                     * Need to discover and treat these
                                     * differently <property
                                     * name="allowed-languages" value=
                                     * "javascript, perl, php"/>
                                     */
                                    String[] langs = StringUtilities.parseCommaDelimitedString(value);
                                    for (String lang : langs) {
                                        addAllowedLanguage(lang);
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
                                    // The stored timeout is in milliseconds. We
                                    // are converting to seconds for display in
                                    // Designer
                                } else if (Xml.CONNECTION_TYPE.equals(name)) {
                                	connType[0] = value;
                                } else {
                                    setProperty(name, value);
                                }
                            }

                            for (final ModelElement element : manifest.getModels()) {
                                IPath path = null;
                                if (element.getPath() != null) {
                                    path = Path.fromPortableString(element.getPath());
                                }
                                /*
                                 * Allows migration from old vdbs where xsd
                                 * files were considered models
                                 */
                                if (path != null && ModelUtil.isXsdFile(path)) {
                                    VdbSchemaEntry vdbSchemaEntry = new VdbSchemaEntry(XmiVdb.this, element);
                                    schemaEntries().add(vdbSchemaEntry);
                                } else {
                                    modelEntries().add(new VdbModelEntry(XmiVdb.this, element));
                                }
                            }

                            // Initialize model entry imports only after all
                            // model entries have been created
                            for (final VdbModelEntry entry : modelEntries()) {
                                entry.initializeImports();
                            }

                            for (final EntryElement element : manifest.getEntries()) {
                                IPath path = Path.fromPortableString(element.getPath());
                                /*
                                 * Xsd files were be added to the manifest as
                                 * entries but they should become schema entries
                                 * in this runtime instance.
                                 */
                                if (ModelUtil.isXsdFile(path)) {
                                    VdbSchemaEntry vdbSchemaEntry = new VdbSchemaEntry(XmiVdb.this, element);
                                    schemaEntries().add(vdbSchemaEntry);
                                } else {
                                    VdbFileEntry vdbFileEntry = new VdbFileEntry(XmiVdb.this, element);
                                    switch (vdbFileEntry.getFileType()) {
                                        case UDFJar:
                                            udfJarEntries().add(vdbFileEntry);
                                            break;
                                        case UserFile:
                                            fileEntries().add(vdbFileEntry);
                                    }
                                }
                            }

                            // Vdb Import entries
                            for (final ImportVdbElement element : manifest.getImportVdbEntries()) {
                            	if( getImportVdbEntry(element.getName()) == null ) {
                            		addImport(new VdbImportVdbEntry(XmiVdb.this, element));
                            	}
                            }

                            // load translator overrides
                            for (final TranslatorElement translatorElement : manifest.getTranslators()) {
                                addTranslator(new TranslatorOverride(XmiVdb.this, translatorElement));
                            }

                            for (final DataRoleElement element : manifest.getDataPolicies()) {
                                DataRole dataRole = new DataRole(element);
                                addDataRole(dataRole);
                            }
                        } else if (!zipEntry.isDirectory()) {
                            FileUtils.copy(entryStream, new File(getStagingFolder(), zipEntry.getName()));
                        }
                    } finally {
                        if (entryStream != null)
                            entryStream.close();
                    }
                }
                setChanged(false);
            }
        });
        setPreview(previewable[0]);
        setVersion(vdbVersion[0]);
        setQueryTimeout(queryTimeout[0]);
        setAutoGenerateRESTWar(autoGen[0]);
        setSecurityDomain(secDomain[0]);
        setGssPattern(gssPatt[0]);
        setPasswordPattern(pwdPatt[0]);
        setAuthenticationType(authType[0]);
        setConnectionType(connType[0]);
        if( valDateTime[0] != null ) {
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH); //$NON-NLS-1$
            setValidationDateTime(format.parse(valDateTime[0])); //new Date(valDateTime[0]); //DateUtil.convertStringToDate(valDateTime[0]);
        }
        setValidationVersion(valVersion[0]);
    }

    /**
     * @param path

     *
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     * @throws Exception
     */
    @Override
    public final <T extends VdbEntry> T addEntry( final IPath path) throws Exception {
        CoreArgCheck.isNotNull(path);

        if (ModelUtil.isXsdFile(path)) {
           return (T) addSchemaEntry(path);
        } else if (ModelUtil.isModelFile(path) && !ModelUtil.isXsdFile(path))
            return (T) addModelEntry(path);
        else {
            String fileType = FileUtil.guessFileType(path.toFile());

            FileEntryType fileEntryType = FileEntryType.UserFile;
            if(VdbHelper.JAR_MIME_TYPE.equals(fileType)) {
                fileEntryType = FileEntryType.UDFJar;
            }

           return (T) addFileEntry(path, fileEntryType);
        }
    }
    
    /**
     * @param path
     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     * @throws Exception
     */
    private VdbSchemaEntry addSchemaEntry( final IPath path) throws Exception {
        VdbSchemaEntry schemaEntry = new VdbSchemaEntry(this, path);
        VdbSchemaEntry addedEntry = addEntry(schemaEntry, schemaEntries());

        // entry did not exist in VDB
        if (schemaEntry == addedEntry) {
            schemaEntry.synchronizeSchemaEntry();
        } else {
            // entry already existed in VDB
            schemaEntry = addedEntry;
        }

        return schemaEntry;
    }

    /**
     * @param name
     * @param entryType the type of file entry being added

     * @return the newly added {@link VdbEntry entry}, or the existing entry with the supplied name.
     * @throws Exception
     */
    private VdbFileEntry addFileEntry( final IPath name,
                                        final VdbFileEntry.FileEntryType entryType) throws Exception {
        CoreArgCheck.isNotNull(entryType);

        Set<VdbFileEntry> entries = null;
        switch (entryType) {
            case UDFJar:
                entries = udfJarEntries();
                break;
            case UserFile:
                entries = fileEntries();
        }

        return addEntry(new VdbFileEntry(this, name, entryType), entries);
    }

    private <T extends VdbEntry> T addEntry( final T entry, final Set<T> entries) {
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

     * @return the newly added {@link VdbModelEntry model entry}, or the existing entry with the supplied name.
     * @throws Exception
     */
    private VdbModelEntry addModelEntry( final IPath name) throws Exception {
        VdbModelEntry modelEntry = new VdbModelEntry(this, name);
        VdbModelEntry addedEntry = addEntry(modelEntry, modelEntries());

        // entry did not exist in VDB
        if (modelEntry == addedEntry) {
            modelEntry.synchronizeModelEntry();
        } else {
            // entry already existed in VDB
            modelEntry = addedEntry;
        }

        return modelEntry;
    }

    /**
     * Synchronize the Vdb file entries.  The supplied entries must be included - it's VdbModelEntry
     * may not exist in the vdb yet.
     * @param newJarEntries the supplied new entries which must exist 
     */
    @Override
    public final void synchronizeUdfJars(Set<VdbFileEntry> newJarEntries) {
        // Init list of all required Udf jars with supplied list
        Set<VdbFileEntry> allRequiredUdfJars = new HashSet<VdbFileEntry>(newJarEntries);
        
        // Add other Udf jars used by current Model entries
        for(VdbModelEntry entry: modelEntries()) {
            Set<VdbFileEntry> jarEntries = entry.getUdfJars();
            allRequiredUdfJars.addAll(jarEntries);
        }
        
        // Create map of required jarName to its jar entry
        Map<String,VdbFileEntry> allRequiredJarsMap = new HashMap<String,VdbFileEntry>();
        for(VdbFileEntry fileEntry: allRequiredUdfJars) {
            allRequiredJarsMap.put(fileEntry.getPath().toOSString(), fileEntry);
        }
        
        // Get the current Udf jar names for this vdb
        Set<String> currentUdfJarNames = getUdfJarNames();
        
        boolean jarsAdded = false;
        // Add any missing Udf jars to the vdb that are required
        for(VdbFileEntry modelUdfJar: allRequiredUdfJars) {
            if(!currentUdfJarNames.contains(modelUdfJar.getPath().toString())) {
                udfJarEntries().add(modelUdfJar);
                jarsAdded = true;
            }
        }
        
        // Remove any Udf jars that are no longer needed
        boolean jarsRemoved = false;
        currentUdfJarNames = getUdfJarNames();
        for(String currentJarName: currentUdfJarNames) {
            Set<String> allRequiredJarNames = allRequiredJarsMap.keySet();
            if(!allRequiredJarNames.contains(currentJarName)) {
                for(VdbEntry entry: udfJarEntries()) {
                    String entryPath = entry.getPath().toOSString();
                    if(entryPath!=null && entryPath.equals(currentJarName)) {
                        udfJarEntries().remove(entry);
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
    @Override
    public final void close() {
        fileEntries().clear();
        udfJarEntries().clear();
        schemaEntries().clear();
        modelEntries().clear();

        // Clean up state folder
        FileUtils.removeDirectoryAndChildren(VdbPlugin.singleton().getStateLocation().append(getSourceFile().getFullPath().segment(0)).toFile());

        super.close();
    }

    /**
     * @return the immutable set of entries, not including {@link #getModelEntries() model entries}, within this VDB
     */
    @Override
    public final Set<VdbEntry> getEntries() {
        Set<VdbEntry> entries = new HashSet<VdbEntry>();
        entries.addAll(schemaEntries());
        entries.addAll(fileEntries());
        entries.addAll(udfJarEntries());
        return Collections.unmodifiableSet(entries);
    }

    /**
     * Get the current set of schema entries.
     * @return the set of VdbSchemaEntry objects
     */
    @Override
    public final Set<VdbSchemaEntry> getSchemaEntries() {
        return Collections.unmodifiableSet(schemaEntries());
    }

    /**
     * Get the current set of UDF jar entries.
     * @return the set of VdbFileEntry UDF jar objects
     */
    @Override
    public final Set<VdbFileEntry> getUdfJarEntries() {
        return Collections.unmodifiableSet(udfJarEntries());
    }
    
    /**
     * Get the current set of UDF jar entries.
     * @return the set of VdbEntry UDF jar objects
     */
    @Override
    public final Set<String> getUdfJarNames() {
        Set<String> udfJarNames = new HashSet<String>();
        
        // The list of UserFiles are those that begin with the UDF path prefix
        for(VdbFileEntry entry: udfJarEntries()) {
            // Name of VDB entry
            String entryPath = entry.getPath().toOSString();
            udfJarNames.add(entryPath);
        }
        return Collections.unmodifiableSet(udfJarNames);
    }

    /**
     * Get the current set of UserFile entries.
     * @return the set of VdbFileEntry userFile objects
     */
    @Override
    public final Set<VdbFileEntry> getUserFileEntries() {
        return Collections.unmodifiableSet(fileEntries());
    }

    /**
     * @return the immutable set of model entries within this VDB
     */
    @Override
    public final Set<VdbModelEntry> getModelEntries() {
        final Set<VdbModelEntry> entries = new HashSet<VdbModelEntry>();
        for (final VdbModelEntry entry : modelEntries())
            if (!entry.isBuiltIn()) entries.add(entry);
        return Collections.unmodifiableSet(entries);
    }

    /**
     * @param entries
     * @return entries as files
     */
    private final Collection<File> convertEntries(Collection<? extends VdbEntry> entries) {
        final Collection<File> entryFiles = new ArrayList<File>();

        for (VdbEntry entry : entries) {
            entryFiles.add(new File(getStagingFolder().getAbsolutePath(), entry.getPath().toOSString()));
        }

        return Collections.unmodifiableCollection(entryFiles);
    }

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
    @Override
    public final Collection<File> getModelFiles() {
        return convertEntries(getModelEntries());
    }

    /**
     * @return the immutable list of schema files within this VDB
     */
    @Override
    public final Collection<File> getSchemaFiles() {
        return convertEntries(getSchemaEntries());
    }

    /**
     * @param vdbName the name of the imported vdb
     * @return the <code>VdbImportVdbEntry</code>
     */
    private final VdbImportVdbEntry getImportVdbEntry(String vdbName) {
    	for( VdbImportVdbEntry entry : getImports()) {
    		if( entry.getName().equalsIgnoreCase(vdbName)) {
    			return entry;
    		}
    	}
    	
    	return null;
    }
    
    private final void handleRemovedVdbModelEntry(String vdbModelEntryName) {
    	removeStaleVdbImports();
    }
    
    /**
     * @return <code>true</code> if all model entries in this VDB are either synchronized with their associated models or no
     *         associated model exists..
     */
    @Override
    public final boolean isSynchronized() {
        for (final VdbModelEntry entry : modelEntries())
            if (entry.getSynchronization() == Synchronization.NotSynchronized) return false;
        for (final VdbEntry entry : getEntries())
            if (entry.getSynchronization() == Synchronization.NotSynchronized) return false;
        return true;
    }

    /**
     * This method provides VdbModelEntry's the ability to register any dependent import VDB objects
     * 
     * The VDB contains a list of VdbImportVdbEntry objects (VDB name and version)
     * 
     * As models are added to the VDB, they are checked to see if there are model imports that are
     * VDB source models that contain VDB name and VDB Version properties. This is done in the
     * synchronize method of the VdbModelEntry
     * 
     * When a model is removed from a VDB (deleted), the VDB needs to be smart enough to check the Import VDB objects
     * against the remaining models in the VDB to check for VDB References and remove any STALE VDB imports that are not
     * used anymore.
     * 
     * 
     * The responsibility of this method is to look at the current map of import VDBs and 
     * 
     * @param importVdbNames the list of imported vdb names
     * @param modelName the model name (<code>IPath</code>) from the <code>VdbModelEntry</code>
     */
    public final void registerImportVdbs(Collection<VdbImportInfo> vdbImports, String modelName) {

    	removeStaleVdbImports();
    	
    	// Only add the import if it doesn't already exist
    	for( VdbImportInfo vdbImport : vdbImports ) {
	    	if( getImportVdbEntry(vdbImport.getName()) == null ) {
	    		addImport(vdbImport.getName(), vdbImport.getVersion());
	    	}
    	}
    }
    
    /** check for VDB References and remove any STALE VDB imports that are not
    * used anymore.
    * 
    */
    
    private  final void removeStaleVdbImports() {
    	// Collect import vdb names from VdbModelEntries
    	Set<String> allImportVdbNames = new HashSet<String>();
    	for( VdbModelEntry entry : getModelEntries() ) {
    		allImportVdbNames.addAll(entry.getImportVdbNames());
    	}
    	
    	// Now check each VdbImportVdbEntry and see if it's name is in this list
    	// Collect any stale VdbImportVdbEntry's
    	Set<VdbImportVdbEntry> staleImports = new HashSet<VdbImportVdbEntry>();
    	for( VdbImportVdbEntry entry : getImports() ) {
    		if( ! allImportVdbNames.contains(entry.getName())) {
    			staleImports.add(entry);
    		}
    	}
    	
    	for( VdbImportVdbEntry entry : staleImports ) {
    		removeImport(entry);
    	}
    	
    }

    

    /**
     * @param entry
     */
    @Override
    public final boolean removeEntry( final VdbEntry entry ) {
        boolean removed = false;
        entry.dispose();
        if (entry instanceof VdbModelEntry) {
            String entryPath = entry.getPath().toOSString();
            removed = modelEntries().remove(entry);

            synchronizeUdfJars(new HashSet<VdbFileEntry>());

            handleRemovedVdbModelEntry(entryPath);
        } else if (entry instanceof VdbSchemaEntry) {
            String entryName = entry.getPath().toOSString();
            removed = schemaEntries().remove(entry);
            handleRemovedVdbModelEntry(entryName);
        }
        else {
            removed = fileEntries().remove(entry);
            if (!removed)
                removed = udfJarEntries().remove(entry);
        }

        if (removed)
            setModified(this, Event.ENTRY_REMOVED, entry, null);

        return removed;
    }

    /**
     * Must not be called unless this VDB has been {@link #isModified() modified}
     * 
     * @throws Exception
     */
    @Override
    public void save() throws Exception {
        // Build JAXB model
        final VdbElement vdbElement = new VdbElement(this);
        // Save archive
        final File tmpFolder = VdbPlugin.singleton().getStateLocation().toFile();
        OperationUtil.perform(new Unreliable() {

            ZipOutputStream out = null;

            @Override
            public void doIfFails() {
                // Do Nothing
            }

            @Override
            public void finallyDo() throws Exception {
                if (out != null) out.close();
            }

            @Override
            public void tryToDo() throws Exception {
                IPath path = getSourceFile().getFullPath();
                final File tmpArchive = File.createTempFile(path.removeFileExtension().toString(),
                                                            '.' + path.getFileExtension(),
                                                            tmpFolder);
                tmpArchive.getParentFile().mkdirs();
                out = new ZipOutputStream(new FileOutputStream(tmpArchive));
                try {
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
                    IFile file = getSourceFile();
                    if (file != null && file.exists()) {
                        IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
                        if (markers != null) {
                            for (final IMarker marker : markers)
                                marker.delete();
                        }
                    }

                    // Save entries
                    for (final VdbEntry entry : getEntries())
                        entry.save(out);
                    for (final VdbModelEntry entry : modelEntries())
                        entry.save(out);

                } finally {
                    // Close zip output stream so its fully writen and any locks are removed.
                    out.close();
                    out = null;
                }

                // Replace archive in workspace with temporary archive
                File archiveFile = getSourceFile().getLocation().toFile();
                try {
                    Files.deleteIfExists(archiveFile.toPath());
                } catch (Exception ex) {
                    throw new Exception(VdbPlugin.UTIL.getString("unableToDelete", archiveFile), ex); //$NON-NLS-1$
                }

                if (!tmpArchive.renameTo(archiveFile)) throw new Exception(
                                                                                  VdbPlugin.UTIL.getString("unableToRename", tmpArchive, archiveFile)); //$NON-NLS-1$
                // Mark as unmodified
                if (isModified())
                    setChanged(false);

                // Notify change listeners
                notifyChangeListeners(this, Event.SAVED, null, null);
            }
        });
    }

    private final void synchronize( final Collection<VdbEntry> entries) throws Exception {
        for (final VdbEntry entry : entries)
            if (entry.getSynchronization() == Synchronization.NotSynchronized) entry.synchronize();
    }

    /**
     * @throws Exception
     */
    @Override
    public final void synchronize() throws Exception {
    	getBuilder().start();

        synchronize(new HashSet<VdbEntry>(modelEntries()));
        synchronize(getEntries());

        getBuilder().stop();
    }
    

    /**
     * @return builder
     */
    public VdbModelBuilder getBuilder() {
    	return this.builder();
    }

    /**
     * @return
     */
    public boolean excludeSourceMetadata() {
		return excludeSourceMetadata;
	}

	/**
	 * @param excludeSourceMetadata
	 */
	public void setExcludeSourceMetadata(boolean excludeSourceMetadata) {
		this.excludeSourceMetadata = excludeSourceMetadata;
	}

    @Override
    public void addDynamicModel(DynamicModel model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<DynamicModel> getDynamicModels() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDynamicModel(String modelToRemove) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XmiVdb xmiVdbConvert(IFile destination, Properties options) throws Exception {
        //
        // TODO copy this vdb to the new destination
        //
        return this;
    }

    @Override
    public DynamicVdb dynVdbConvert(IFile destination, Properties options) throws Exception {
        try {
            //
            // Broadcast that a conversion is underway
            //
            VdbPlugin.singleton().setConversionInProgress(true);

            DynamicVdb dynVdb = null;
            
            if( destination.exists() ) {
            	dynVdb = new DynamicVdb();
            } else {
            	dynVdb = new DynamicVdb(destination);
            }

            //
            // Populate the new vdb with the basic specification
            //
            populateVdb(dynVdb);

            // Remove non-relevant VDB properties
            // preview true/false

            dynVdb.getProperties().remove(Xml.PREVIEW);
            this.getProperties().remove(Xml.PREVIEW);

            MultiStatus generateStatus = new MultiStatus(VdbConstants.PLUGIN_ID, IStatus.OK, "Exported Dynamic VDB", null); //$NON-NLS-1$

            //
            // Is possible to deploy udf jar archives separately and drop in
            // a property that references the udf.
            //
            // Note that there can be multiple UDF jars and they could be 
            String libString = StringConstants.EMPTY_STRING;
            int nEntries = getUdfJarEntries().size();
            int count = 0;
            for (VdbFileEntry entry : getUdfJarEntries()) {
            	libString = libString + "deployement." + entry.getName() + ".jar"; //$NON-NLS-1$ //$NON-NLS-2$
                count++;
                if( count < nEntries ) {
                	libString = libString + StringConstants.SPACE;
                }

            }
            
            if( !libString.equals(StringConstants.EMPTY_STRING) ) {
                generateStatus.add(new Status(IStatus.WARNING, PLUGIN_ID,
                        VdbPlugin.UTIL.getString("XmiVdb.udfPropertyAdded")) ); //$NON-NLS-1$
            	dynVdb.getProperties().setProperty(VdbFolders.UDF.getWriteFolder(), libString);
        	}

            Collection<ModelResource> modelResources = new ArrayList<ModelResource>();
            ModelUtil.collectModelResources(getSourceFile().getProject(),  modelResources);
            
            //
            // Convert model entries to DynamicModels
            //
            for (VdbModelEntry entry : getModelEntries()) {
                VdbSourceInfo sourceInfo = entry.getSourceInfo();
                
            	boolean isMultiSource = sourceInfo.isMultiSource();
            	String singleTranslatorName = null;
            	if( !isMultiSource && sourceInfo.getSources().size() != 0) {
            		singleTranslatorName = sourceInfo.getSource(0).getTranslatorName();
            	}

                DynamicModel model = new DynamicModel();
                model.setName(entry.getName());
                model.setDescription(entry.getDescription());
                model.setVisible(entry.isVisible());

                for (Map.Entry<Object, Object> prop : entry.getProperties().entrySet()) {
                    String name = prop.getKey().toString();

                    //
                    // Not applicable to dynamic vdb models since index files are not used
                    //
                    if (EntryElement.INDEX_NAME.equals(name))
                        continue;

                    //
                    // Not applicable to dynamic vdb models since models are declarative
                    // within the vdb rather than in their own files
                    //
                    if (EntryElement.CHECKSUM.equals(name))
                        continue;

                    model.setProperty(name, prop.getValue().toString());
                }

                DynamicModel.Type type = DynamicModel.Type.fromString(entry.getType());
                model.setModelType(type);
                model.setAllowMultiSource(sourceInfo.isMultiSource());
                model.setAddColumn(sourceInfo.isAddColumn());
                model.setColumnAlias(sourceInfo.getColumnAlias());

                for (VdbSource source : sourceInfo.getSources()) {
                    VdbSource clone = source.clone();
                    model.addSource(clone);
                }

                boolean isNonRelationalModel = false;
                boolean notInWorkspace = false;
                
				if( entry.getType().equals(ModelType.Type.VIRTUAL.getName()) || !excludeSourceMetadata ) {
	
	                TeiidModelToDdlGenerator generator = new TeiidModelToDdlGenerator();
	
	                IFile entryFile = null;
	                if (Synchronization.Synchronized == entry.getSynchronization()) {
	                    entryFile = entry.findFileInWorkspace();
	                } else {
	                    entryFile = createEntryFile(entry);
	                    notInWorkspace = true;
	                }
	                
	                ModelWorkspaceManager workspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();
   
	                ModelResource modelResource = null;
	                if( ! notInWorkspace ) {
		                for( ModelResource mr : modelResources ) {
		                	// Compare paths
		                	if( entryFile.getProject().getName().equals(mr.getCorrespondingResource().getProject().getName() ) ) {
			                	IPath entryFilePath = entryFile.getProjectRelativePath();
			                	IPath mrPath = mr.getCorrespondingResource().getProjectRelativePath();
			                	if(entryFilePath.toString().equals(mrPath.toString()) ) {
			                		modelResource = mr;
			                		break;
			                	}
		                	}
		                }
	                } else {
						modelResource = (ModelResource) workspaceManager.findModelWorkspaceItem(entryFile, true);
	                }
	                
	                if (modelResource == null)
	                    throw new Exception("Failed to get model resource for " + entryFile.getLocation().toOSString()); //$NON-NLS-1$
	                
                	if( ModelUtil.URI_WEB_SERVICES_VIEW_MODEL.equalsIgnoreCase(modelResource.getPrimaryMetamodelUri()) || 
                		ModelUtil.URI_FUNCTION_MODEL.equalsIgnoreCase(modelResource.getPrimaryMetamodelUri()) ||
                		ModelUtil.URI_XML_SCHEMA_MODEL.equalsIgnoreCase(modelResource.getPrimaryMetamodelUri()) ||
                		ModelUtil.URI_XML_VIEW_MODEL.equalsIgnoreCase(modelResource.getPrimaryMetamodelUri()) ||
                		ModelUtil.URI_EXTENSION_MODEL.equalsIgnoreCase(modelResource.getPrimaryMetamodelUri())) {
                		isNonRelationalModel = true;
                		generateStatus.add(new Status(IStatus.WARNING, PLUGIN_ID, 
                                      VdbPlugin.UTIL.getString("XmiVdb.modelNotIncludedMessage",  entry.getName())) ); //$NON-NLS-1$
                	}
	                
                	if( ! isNonRelationalModel ) {
		                String ddl = generator.generate(modelResource);
		                Metadata metadata = new Metadata(ddl, Metadata.Type.DDL);
		                model.setMetadata(metadata);
                	}
                	
                	String overridedTranslator = null;
                	if(entry.getTranslatorOverride() != null) {
                	    overridedTranslator = entry.getTranslatorOverride().getType();
                	}
                	
	                // Check the translator. If infinispan-hotrod then add second metadata tag of type NATIVE
	                if( singleTranslatorName != null && 
	                    (ModelElement.TEIID_INFINISPAN_HOTROD_DRIVER.equalsIgnoreCase(singleTranslatorName) 
	                    || ModelElement.TEIID_INFINISPAN_HOTROD_DRIVER.equalsIgnoreCase(overridedTranslator))) {
	                	Metadata metadata = new Metadata(null, Metadata.Type.NATIVE);
	                	model.setMetadata(metadata);
	                }
	            }

				if( ! isNonRelationalModel ) {
					dynVdb.addDynamicModel(model);
            	}
            }

            // Check other remaining entries (Xsd, flles etc...)
            Set<VdbEntry> otherEntries = new HashSet<VdbEntry>();
            otherEntries.addAll(getSchemaEntries());
            otherEntries.addAll(getUserFileEntries());
            for (VdbEntry entry : otherEntries) {
            	generateStatus.add(
            			new Status(IStatus.WARNING, PLUGIN_ID,
            					VdbPlugin.UTIL.getString("XmiVdb.fileNotIncludedMessage", entry.getPath())));  //$NON-NLS-1$
            }

            dynVdb.setStatus(generateStatus);
            
            return dynVdb;
        } finally {
            VdbPlugin.singleton().setConversionInProgress(false);
        }
    }

    private IFile createEntryFile(VdbModelEntry entry) throws Exception {
        File realFile = new File(getStagingFolder(), entry.getPath().toOSString());
        if (! realFile.exists()) {
            throw new FileNotFoundException(realFile.getAbsolutePath());
        }

        IProject vdbProject = getSourceFile().getProject();
        IFolder workingFolder = vdbProject.getFolder(WORKING_FILES);
        if (!workingFolder.exists())
            workingFolder.create(IResource.HIDDEN, true, null);

        IFile entryFile = workingFolder.getFile(entry.getPathName());
        if (entryFile.exists())
            entryFile.delete(true, null);

        entryFile.createLink(new Path(realFile.getAbsolutePath()), IResource.HIDDEN, null);
        return entryFile;
    }

    @Override
    public XmiVdb clone() {
        try {
            XmiVdb clone = new XmiVdb(getSourceFile());
            return clone;
        } catch (Exception ex) {
            VdbPlugin.UTIL.log(ex);
            return null;
        }
    }
}
