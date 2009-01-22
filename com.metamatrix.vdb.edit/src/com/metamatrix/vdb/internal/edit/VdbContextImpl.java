/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.util.XSDConstants;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import com.metamatrix.common.xml.XMLReaderWriter;
import com.metamatrix.common.xml.XMLReaderWriterImpl;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.EnhancedStringTokenizer;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.ObjectConverterUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.core.util.UriUtil;
import com.metamatrix.internal.core.xml.vdb.VdbHeader;
import com.metamatrix.internal.core.xml.vdb.VdbHeaderReader;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.resource.EResourceImpl;
import com.metamatrix.vdb.edit.VdbContext;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl;

/**
 * @since 5.0
 */
public class VdbContextImpl implements VdbContext {

    private static final String PRODUCER_NAME = ModelerCore.ILicense.PRODUCER_NAME;
    private static final String PRODUCER_VERSION = ModelerCore.ILicense.VERSION;
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final String XSD_IMPORT_TAG_NAME = "import"; //$NON-NLS-1$
    private static final String XSD_INCLUDE_TAG_NAME = "include"; //$NON-NLS-1$
    private static final String XSD_SCHEMA_DIRECTIVE_LOCATION_ATTRIBUTE_NAME = "schemaLocation"; //$NON-NLS-1$
    private static final String XML_FORMAT_INDENT = "    "; //$NON-NLS-1$

    private static final String URL_ROOT_FOR_VDB = VdbEditPlugin.URL_ROOT_FOR_VDB;
    private static final String URL_SUFFIX_FOR_VDB = VdbEditPlugin.URL_SUFFIX_FOR_VDB;

    /**
     * Ensure that the VDB metamodel is initialized
     */
    static {
        // Initialize the metamodel(s) ...
        ManifestPackageImpl.init();
    }

    private File vdbFile;
    private TempDirectory tempDirectory;

    private Resource manifestResource;
    private Map options;

    private VirtualDatabase virtualDatabase;

    private final ListenerList changeListeners;
    private final ListenerList vetoListeners;

    private Properties executionProperties;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Create an instance of the context for the specified VDB file. If the file does not yet exist on the file system it is
     * considered a new VDB. The VDB may be opened or viewed through this context but cannot be modified.
     * 
     * @since 5.0
     */
    public VdbContextImpl( final File theVdbFile,
                           final File vdbWorkingFolder ) {
        ArgCheck.isNotNull(theVdbFile);
        ArgCheck.isNotNull(vdbWorkingFolder);

        if (!vdbWorkingFolder.exists() || !vdbWorkingFolder.isDirectory() || !vdbWorkingFolder.canWrite()) {
            final String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Working_folder_must_be_existing_folder", vdbWorkingFolder); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        this.vdbFile = theVdbFile;

        // Initialize the TempDirectory instance
        this.tempDirectory = createTempDirectory(vdbWorkingFolder);

        this.options = new HashMap();

        this.changeListeners = new ListenerList(ListenerList.IDENTITY);
        this.vetoListeners = new ListenerList(ListenerList.IDENTITY);

    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getTempDirectory()
     * @since 5.0
     */
    public TempDirectory getTempDirectory() {
        return this.tempDirectory;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getVdbFile()
     * @since 5.0
     */
    public File getVdbFile() {
        return this.vdbFile;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getSeverity()
     * @since 5.0
     */
    public Severity getSeverity() {
        if (isOpen()) {
            return getVirtualDatabase().getSeverity();
        }
        VdbHeader header = null;
        try {
            header = peekAtContents();
        } catch (IOException e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }
        if (header != null) {
            return Severity.get(header.getSeverity());
        }
        return Severity.OK_LITERAL;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getDescription()
     * @since 5.0
     */
    public String getDescription() {
        if (isOpen()) {
            return getVirtualDatabase().getDescription();
        }
        VdbHeader header = null;
        try {
            header = peekAtContents();
        } catch (IOException e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }
        if (header != null) {
            return header.getDescription();
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.prototype.VdbEditorContext#isReadOnly()
     * @since 5.0
     */
    public boolean isReadOnly() {
        if (getVdbFile().exists() && !getVdbFile().canWrite()) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#isEmpty()
     * @since 5.0
     */
    public boolean isEmpty() {
        if ((!getVdbFile().exists()) || (getVdbFile().exists() && getVdbFile().length() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#peekAtContents()
     * @since 5.0
     */
    public VdbHeader peekAtContents() throws IOException {
        if (getVdbFile().exists() && getVdbFile().canRead()) {
            try {
                return VdbHeaderReader.readHeader(getVdbFile());
            } catch (MetaMatrixCoreException e) {
                throw new IOException(e.getLocalizedMessage());
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#isOpen()
     * @since 5.0
     */
    public boolean isOpen() {
        return (getManifestResource() != null);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#open(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0
     */
    public synchronized void open( final IProgressMonitor theMonitor ) throws IOException {
        open(theMonitor, true);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#open(org.eclipse.core.runtime.IProgressMonitor, boolean)
     * @since 5.0
     */
    public synchronized void open( final IProgressMonitor theMonitor,
                                   final boolean notify ) throws IOException {
        // If the context is already open then return
        if (isOpen()) {
            return;
        }

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

        ZipFile vdbArchive = null;
        try {

            // Open the vdb archive file (i.e. ZipFile) if it exists ...
            if (getVdbFile().exists() && getVdbFile().length() > 0) {
                vdbArchive = new ZipFile(getVdbFile());
            }

            // Create the manifest resource instance
            Resource manifest = createManifestResource(URI.createURI(MANIFEST_MODEL_NAME));

            // If the vdb archive is new then return
            if (vdbArchive == null) {
                this.manifestResource = manifest;
                return;
            }
            final TempDirectory tempDir = getTempDirectory();

            // Set the task name and total work for the progress monitor
            VdbHeader header = peekAtContents();
            int totalFileCount = (header != null ? header.getModelInfos().length + header.getNonModelInfos().length + 1 : 1);
            String taskName = VdbEditPlugin.Util.getString("VdbContextImpl.Opening", getVdbFile().getName()); //$NON-NLS-1$
            monitor.beginTask(taskName, totalFileCount);
            int amountWorked = 0;

            // Initialize the manifest resource instance from the archive
            loadManifestResource(manifest, vdbArchive);
            this.manifestResource = manifest;
            monitor.worked(amountWorked++);

            // Extract all models referenced in the manifest to the context's temp directory ...
            final List problems = new ArrayList();
            final List modelRefs = getVirtualDatabase().getModels();
            for (final Iterator i = modelRefs.iterator(); i.hasNext();) {
                final ModelReference modelRef = (ModelReference)i.next();
                try {
                    final File f = writeArchiveEntryToTempDirectory(modelRef.getModelLocation(), vdbArchive, tempDir);
                    if (modelRef.getChecksum() == 0 && f != null && f.exists()) {
                        modelRef.setChecksum(getCheckSum(f));
                    }
                    monitor.worked(amountWorked++);
                } catch (Exception e) {
                    problems.add(e.getLocalizedMessage());
                }
            }

            // Extract all non-models referenced in the manifest to the context's temp directory ...
            final List nonModelRefs = getVirtualDatabase().getNonModels();
            for (final Iterator i = nonModelRefs.iterator(); i.hasNext();) {
                final NonModelReference nonModelRef = (NonModelReference)i.next();
                try {
                    final File f = writeArchiveEntryToTempDirectory(nonModelRef.getPath(), vdbArchive, tempDir);
                    if (nonModelRef.getChecksum() == 0 && f != null && f.exists()) {
                        nonModelRef.setChecksum(getCheckSum(f));
                    }
                    monitor.worked(amountWorked++);
                } catch (Exception e) {
                    problems.add(e.getLocalizedMessage());
                }
            }

            // If any problems were encountered extracting the models throw an exception
            if (!problems.isEmpty()) {
                StringBuffer sb = new StringBuffer(2000);
                for (Iterator i = problems.iterator(); i.hasNext();) {
                    sb.append((String)i.next());
                    if (i.hasNext()) {
                        sb.append(StringUtil.Constants.NEW_LINE);
                    }
                }
                throw new IOException(sb.toString());
            }

        } finally {
            // Close the VDB archive ...
            if (vdbArchive != null) {
                vdbArchive.close();
            }
        }

        // Notify all listeners about the change
        if (notify) {
            fireStateChanged();
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getVirtualDatabase()
     * @since 5.0
     */
    public VirtualDatabase getVirtualDatabase() {
        assertContextIsOpen();

        if (this.virtualDatabase == null && this.manifestResource != null) {
            ModelAnnotation model = null;
            VirtualDatabase vdb = null;
            for (Iterator i = this.manifestResource.getContents().iterator(); i.hasNext();) {
                Object obj = i.next();
                if (obj instanceof ModelAnnotation) {
                    model = (ModelAnnotation)obj;
                } else if (obj instanceof VirtualDatabase) {
                    vdb = (VirtualDatabase)obj;
                }
            }

            // If no ModelAnnotation exists, create one and add it to the manifest resource
            if (model == null) {
                boolean requiredStart = ModelerCore.startTxn(false, false, "Adapter Property Source", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    model = CoreFactory.eINSTANCE.createModelAnnotation();
                    model.setPrimaryMetamodelUri(ManifestPackage.eNS_URI);
                    model.setModelType(ModelType.VDB_ARCHIVE_LITERAL);
                    model.setProducerName(PRODUCER_NAME);
                    model.setProducerVersion(PRODUCER_VERSION);
                    this.manifestResource.getContents().add(model);
                    succeeded = true;
                } finally {
                    // If we start txn, commit it
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }

            // If no VirtualDatabase exists, create one and add it to the manifest resource
            if (vdb == null) {
                vdb = ManifestFactory.eINSTANCE.createVirtualDatabase();
                vdb.setName(URI.createFileURI(getVdbFile().getAbsolutePath()).trimFileExtension().lastSegment());
                vdb.setProducerName(PRODUCER_NAME);
                vdb.setProducerVersion(PRODUCER_VERSION);
                final Date currentDate = DateUtil.getCurrentDate();
                vdb.setTimeLastChangedAsDate(currentDate);
                vdb.setTimeLastProducedAsDate(currentDate);
                this.manifestResource.getContents().add(vdb);
            }
            this.virtualDatabase = vdb;
        }
        return this.virtualDatabase;
    }

    public void setVirtualDatabase( final VirtualDatabase vdb ) {
        this.virtualDatabase = vdb;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getModelReference(java.lang.String)
     * @since 5.0
     */
    public ModelReference getModelReference( final String pathInArchive ) {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        assertContextIsOpen();

        final IPath pathToMatch = createNormalizedPath(pathInArchive);
        for (Iterator i = getVirtualDatabase().getModels().iterator(); i.hasNext();) {
            final ModelReference ref = (ModelReference)i.next();
            final IPath refPath = createNormalizedPath(ref.getModelLocation());
            if (pathToMatch.equals(refPath)) {
                return ref;
            }
        }
        final IPath upperPathToMatch = createNormalizedPath(pathInArchive.toUpperCase());
        for (Iterator i = getVirtualDatabase().getModels().iterator(); i.hasNext();) {
            final ModelReference ref = (ModelReference)i.next();
            final IPath upperRefPath = createNormalizedPath(ref.getModelLocation().toUpperCase());
            if (upperPathToMatch.equals(upperRefPath)) {
                return ref;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getModelReference(java.io.File)
     * @since 5.0
     */
    public ModelReference getModelReference( final File tempDirFile ) {
        ArgCheck.isNotNull(tempDirFile);

        final File tempDirFolder = new File(getTempDirectory().getPath());
        String pathInTempDir = getPathRelativeToFolder(tempDirFolder, tempDirFile);

        if (pathInTempDir != null) {
            return getModelReference(pathInTempDir);
        }

        VdbEditPlugin.Util.log(IStatus.ERROR,
                               VdbEditPlugin.Util.getString("VdbContextImpl.Error_getting_model_reference", tempDirFile)); //$NON-NLS-1$

        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getModelReference(com.metamatrix.core.id.ObjectID)
     * @since 5.0
     */
    public ModelReference getModelReference( final ObjectID uuid ) {
        ArgCheck.isNotNull(uuid);
        assertContextIsOpen();

        for (Iterator i = getVirtualDatabase().getModels().iterator(); i.hasNext();) {
            final ModelReference ref = (ModelReference)i.next();
            final String uuidString = ref.getUuid();
            try {
                if (!StringUtil.isEmpty(uuidString)) {
                    final ObjectID refUuid = IDGenerator.getInstance().stringToObject(uuidString, UUID.PROTOCOL);
                    if (uuid.equals(refUuid)) {
                        return ref;
                    }
                }
            } catch (InvalidIDException e) {
                VdbEditPlugin.Util.log(e);
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getNonModelReference(java.lang.String)
     * @since 5.0
     */
    public NonModelReference getNonModelReference( final String pathInArchive ) {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        assertContextIsOpen();

        final IPath pathToMatch = createNormalizedPath(pathInArchive);
        for (Iterator i = getVirtualDatabase().getNonModels().iterator(); i.hasNext();) {
            final NonModelReference ref = (NonModelReference)i.next();
            final IPath refPath = createNormalizedPath(ref.getPath());
            if (pathToMatch.equals(refPath)) {
                return ref;
            }
        }
        final IPath upperPathToMatch = createNormalizedPath(pathInArchive.toUpperCase());
        for (Iterator i = getVirtualDatabase().getNonModels().iterator(); i.hasNext();) {
            final NonModelReference ref = (NonModelReference)i.next();
            final IPath upperRefPath = createNormalizedPath(ref.getPath().toUpperCase());
            if (upperPathToMatch.equals(upperRefPath)) {
                return ref;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getNonModelReference(java.io.File)
     * @since 5.0
     */
    public NonModelReference getNonModelReference( File tempDirFile ) {
        ArgCheck.isNotNull(tempDirFile);

        final File tempDirFolder = new File(getTempDirectory().getPath());
        String pathInTempDir = getPathRelativeToFolder(tempDirFolder, tempDirFile);

        return getNonModelReference(pathInTempDir);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getInputStream(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 5.0
     */
    public InputStream getInputStream( final ModelReference modelRef ) {
        ArgCheck.isNotNull(modelRef);
        assertContextIsOpen();

        File model = getTempDirectoryFile(getTempDirectory(), modelRef.getModelLocation());
        if (model.exists()) {
            return ObjectConverterUtil.convertToInputStream(model);
        }

        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getInputStream(com.metamatrix.vdb.edit.manifest.NonModelReference)
     * @since 5.0
     */
    public InputStream getInputStream( final NonModelReference nonModelRef ) {
        ArgCheck.isNotNull(nonModelRef);
        assertContextIsOpen();

        File model = getTempDirectoryFile(getTempDirectory(), nonModelRef.getPath());
        if (model.exists()) {
            return ObjectConverterUtil.convertToInputStream(model);
        }

        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#close(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0
     */
    public synchronized void close( final IProgressMonitor theMonitor ) {
        close(theMonitor, true, true);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#close(org.eclipse.core.runtime.IProgressMonitor, boolean, boolean)
     * @since 5.0
     */
    public synchronized void close( final IProgressMonitor theMonitor,
                                    final boolean notify,
                                    final boolean vetoable ) {
        if (isOpen()) {
            if (!vetoable || fireVetoableChange(CLOSING_EVENT_NAME, OPENED_EVENT_NAME, CLOSED_EVENT_NAME)) {
                // no one vetoed, proceed:

                // If no progress monitor was specified then create a NullProgressMonitor so
                // that we do not have to check for null everywhere within this method
                final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

                // Set the task name and total work for the progress monitor
                String taskName = VdbEditPlugin.Util.getString("VdbContextImpl.Closing", getVdbFile().getName()); //$NON-NLS-1$
                monitor.beginTask(taskName, 2);
                int amountWorked = 0;

                try {
                    if (this.options != null) {
                        this.options.clear();
                    }
                    if (this.manifestResource != null) {
                        this.manifestResource.unload();
                    }
                    monitor.worked(amountWorked++);

                    if (this.tempDirectory != null) {
                        File tempDirectoryFolder = new File(this.tempDirectory.getPath());
                        FileUtils.removeChildrenRecursively(tempDirectoryFolder);
                    }
                    monitor.worked(amountWorked++);

                } finally {
                    this.virtualDatabase = null;
                    this.manifestResource = null;

                    // Notify all listeners about the change
                    if (notify) {
                        fireStateChanged();
                    }
                }
            } // endif -- veto
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#dispose()
     * @since 5.0
     */
    public synchronized void dispose() {
        try {
            // First ensure that the context is closed
            close(null, false, false);

            if (this.options != null) {
                this.options.clear();
            }
            if (this.manifestResource != null) {
                this.manifestResource.unload();
            }

            if (this.changeListeners != null) {
                Object[] listeners = this.changeListeners.getListeners();
                for (int i = 0; i < listeners.length; ++i) {
                    this.changeListeners.remove(listeners[i]);
                }
            }

            if (this.vetoListeners != null) {
                Object[] listeners = this.vetoListeners.getListeners();
                for (int i = 0; i < listeners.length; ++i) {
                    this.vetoListeners.remove(listeners[i]);
                }
            }

            if (this.tempDirectory != null) {
                this.tempDirectory.remove();
            }

        } catch (Exception e) {
            String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Error_disposing_of_vdbContext"); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
        } finally {
            this.options = null;
            this.virtualDatabase = null;
            this.manifestResource = null;
            this.tempDirectory = null;
            this.vdbFile = null;
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#addVetoableChangeListener(java.beans.VetoableChangeListener)
     * @since 5.0
     */
    public void addVetoableChangeListener( final VetoableChangeListener theListener ) {
        if (theListener != null) {
            this.vetoListeners.add(theListener);
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#removeVetoableChangeListener(java.beans.VetoableChangeListener)
     * @since 5.0
     */
    public void removeVetoableChangeListener( final VetoableChangeListener theListener ) {
        this.vetoListeners.remove(theListener);
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 5.0
     */
    public void addChangeListener( final IChangeListener theListener ) {
        if (theListener != null) {
            this.changeListeners.add(theListener);
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 5.0
     */
    public void removeChangeListener( final IChangeListener theListener ) {
        this.changeListeners.remove(theListener);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#getExecutionProperties()
     * @since 5.0.2
     */
    public Properties getExecutionProperties() {
        if (executionProperties == null) {
            executionProperties = new Properties();
        }
        return this.executionProperties;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected void assertContextIsOpen() {
        if (!isOpen()) {
            final String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Context_must_be_open_before_calling"); //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    protected void fireStateChanged() {
        Object[] listeners = this.changeListeners.getListeners();

        for (int i = 0; i < listeners.length; ++i) {
            ((IChangeListener)listeners[i]).stateChanged(this);
        }
    }

    protected boolean fireVetoableChange( final String key,
                                          final Object oldValue,
                                          final Object newValue ) {
        try {
            Object[] listeners = vetoListeners.getListeners();
            PropertyChangeEvent evt = null;

            for (int i = 0; i < listeners.length; ++i) {
                // lazily construct event:
                if (evt == null) {
                    evt = new PropertyChangeEvent(this, key, oldValue, newValue);
                } // endif
                ((VetoableChangeListener)listeners[i]).vetoableChange(evt);
            } // endfor

            return true;
        } catch (PropertyVetoException ex) {
            return false;
        } // endtry
    }

    protected Map getLoadOptions() {
        if (!this.options.containsKey(XMLResource.OPTION_DISABLE_NOTIFY)) {
            this.options.put(XMLResource.OPTION_DISABLE_NOTIFY, Boolean.TRUE);
        }
        if (!this.options.containsKey(XMLResource.OPTION_PROCESS_DANGLING_HREF)) {
            this.options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_THROW);
        }
        return this.options;
    }

    protected File getVdbWorkingFolder() {
        final TempDirectory tempDir = getTempDirectory();
        return (new File(tempDir.getPath())).getParentFile();
    }

    protected Resource getManifestResource() {
        return this.manifestResource;
    }

    protected Resource createManifestResource( final URI uri ) {
        return new EResourceImpl(uri);
        // return new XMIResourceImpl(uri);
    }

    protected void loadManifestResource( final Resource manifest,
                                         final ZipFile archiveFile ) throws IOException {
        ArgCheck.isNotNull(manifest);

        // If the vdb archive is new then return
        if (archiveFile == null) {
            return;
        }

        // Load the manifest model from the archive file ...
        InputStream istream = null;
        try {
            istream = getArchiveEntryStream(MANIFEST_MODEL_NAME, archiveFile);
            if (istream != null) {
                try {
                    manifest.load(istream, getLoadOptions());
                } finally {
                    try {
                        istream.close();
                    } catch (IOException e) {
                        VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                    }
                }
            }
        } catch (Exception e) {
            manifest.getContents().clear();
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
            throw new IOException(e.getLocalizedMessage());
        }
    }

    protected IPath createNormalizedPath( final String pathInArchive ) {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        final IPath path = new Path(pathInArchive);
        return (path.segmentCount() == 1 ? path.removeTrailingSeparator().makeRelative() : path.makeAbsolute());
    }

    // -------------------------------------------------------------------------
    // Helper methods for accessing vdb archive entries
    // -------------------------------------------------------------------------

    protected File writeArchiveEntryToTempDirectory( final String pathInArchive,
                                                     final ZipFile archiveFile,
                                                     final TempDirectory tempDir ) throws IOException {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        ArgCheck.isNotNull(archiveFile);
        ArgCheck.isNotNull(tempDir);

        File result = null;
        InputStream istream = null;
        try {
            istream = getArchiveEntryStream(pathInArchive, archiveFile);
            if (istream != null) {
                if (pathInArchive.endsWith(".xsd")) { //$NON-NLS-1$
                    // If the entry is an XML Schema file ...
                    istream = decodeXsdStream(istream, pathInArchive, tempDir);

                } else if (!pathInArchive.endsWith(".xmi")) { //$NON-NLS-1$
                    // If the entry is an XMI model file ...
                    istream = decodeXmiStream(istream, pathInArchive, tempDir);
                }
                result = writeToTempDirectory(istream, pathInArchive, tempDir);
            }
        } catch (Exception e) {
            final Object[] params = new Object[] {pathInArchive, e.getLocalizedMessage()};
            final String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Error_streaming_entry", params); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
            throw new IOException(msg);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
        }
        return result;
    }

    protected InputStream decodeXsdStream( final InputStream istream,
                                           final String pathInArchive,
                                           final TempDirectory tempDir ) throws IOException {
        ArgCheck.isNotNull(istream);

        // If the entry is not an XML Schema file then return the original stream
        if (!pathInArchive.endsWith(".xsd")) { //$NON-NLS-1$
            return istream;
        }

        Document doc = null;
        InputStream result = null;
        if (istream != null) {
            try {
                // Build a JDOM document for the schema file
                doc = buildDocument(istream);

                // Decode the "schemaLocation" values found in "xs:import" and "xs:include" declarations
                decodeXsdSchemaDirectivePaths(doc, pathInArchive, new File(tempDir.getPath()));

                // Get an InputStream to the stringified document
                result = ObjectConverterUtil.convertToInputStream(writeToString(doc, XML_FORMAT_INDENT, true));
            } finally {
                try {
                    istream.close();
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
        }
        return result;
    }

    protected InputStream decodeXmiStream( final InputStream istream,
                                           final String pathInArchive,
                                           final TempDirectory tempDir ) {
        ArgCheck.isNotNull(istream);

        // If the entry is not an XMI model file then return the original stream
        if (!pathInArchive.endsWith(".xmi")) { //$NON-NLS-1$
            return istream;
        }

        // Do something ...
        return istream;
    }

    /**
     * Return an InputStream to the archive entry with the specified name or null if no entry by that name could be found
     * 
     * @param pathInArchive the name of the zip entry to extract; may not be null or empty
     * @param archiveFile the ZipFile to use; may not be null
     * @return
     */
    protected InputStream getArchiveEntryStream( final String pathInArchive,
                                                 final ZipFile archiveFile ) throws IOException {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        ArgCheck.isNotNull(archiveFile);

        // Find the archive entry specified in the ModelReference
        ZipEntry zipEntry = archiveFile.getEntry(pathInArchive);

        // If the entry was found, return the InputStream
        InputStream istream = null;
        if (zipEntry != null) {
            istream = archiveFile.getInputStream(zipEntry);
            if (istream == null) {
                final Object[] params = new Object[] {pathInArchive, getVdbFile()};
                final String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Error_obtaining_input_stream", params); //$NON-NLS-1$
                throw new IOException(msg);
            }
        }

        return istream;
    }

    // --------------------------------------------------------------------------------
    // Helper methods for accessing temp directory files
    // --------------------------------------------------------------------------------

    protected File copyTempDirectoryEntryForSave( final String pathInTempDir,
                                                  final TempDirectory sourceDir,
                                                  final TempDirectory targetDir ) throws IOException {
        ArgCheck.isNotNull(pathInTempDir);
        ArgCheck.isNotZeroLength(pathInTempDir);
        ArgCheck.isNotNull(sourceDir);
        ArgCheck.isNotNull(targetDir);

        File result = null;
        InputStream istream = null;
        try {
            File sourceFile = getTempDirectoryFile(sourceDir, pathInTempDir);
            istream = ObjectConverterUtil.convertToInputStream(sourceFile);
            if (istream != null) {
                if (pathInTempDir.endsWith(".xsd")) { //$NON-NLS-1$
                    // If the entry is an XML Schema file ...
                    istream = encodeXsdStream(istream, pathInTempDir, sourceDir);

                } else if (!pathInTempDir.endsWith(".xmi")) { //$NON-NLS-1$
                    // If the entry is an XMI model file ...
                    istream = encodeXmiStream(istream, pathInTempDir, sourceDir);
                }
                result = writeToTempDirectory(istream, pathInTempDir, targetDir);
            }
        } catch (Exception e) {
            final Object[] params = new Object[] {pathInTempDir, e.getLocalizedMessage()};
            final String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Error_streaming_entry", params); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
            throw new IOException(msg);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
        }
        return result;
    }

    protected InputStream encodeXsdStream( final InputStream istream,
                                           final String pathInTempDir,
                                           final TempDirectory tempDir ) throws IOException {
        ArgCheck.isNotNull(istream);

        // If the entry is not an XML Schema file then return the original stream
        if (!pathInTempDir.endsWith(".xsd")) { //$NON-NLS-1$
            return istream;
        }

        Document doc = null;
        InputStream result = null;
        if (istream != null) {
            try {
                // Build a JDOM document for the schema file
                doc = buildDocument(istream);

                // Decode the "schemaLocation" values found in "xs:import" and "xs:include" declarations
                encodeXsdSchemaDirectivePaths(doc, pathInTempDir, new File(tempDir.getPath()));

                // Get an InputStream to the stringified document
                result = ObjectConverterUtil.convertToInputStream(writeToString(doc, XML_FORMAT_INDENT, true));
            } finally {
                try {
                    istream.close();
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
        }
        return result;
    }

    protected InputStream encodeXmiStream( final InputStream istream,
                                           final String pathInTempDir,
                                           final TempDirectory tempDir ) {
        ArgCheck.isNotNull(istream);

        // If the entry is not an XMI model file then return the original stream
        if (!pathInTempDir.endsWith(".xmi")) { //$NON-NLS-1$
            return istream;
        }

        // Do something ...
        return istream;
    }

    /**
     * Create a new {@link java.io.File}instance within the TempDirectory location using the specified name and InputStream. This
     * method will remove any existing file with this name prior to creating the new file.
     * 
     * @param istream
     * @param fileNmae
     * @return @throws Exception if there are problems reading from the input stream
     * @since 4.2
     */
    protected File writeToTempDirectory( final InputStream istream,
                                         final String pathInTempDir,
                                         final TempDirectory tempDir ) throws IOException {
        ArgCheck.isNotNull(istream);

        OutputStream fos = null;
        OutputStream bos = null;
        File tempDirFile = null;
        try {
            tempDirFile = getTempDirectoryFile(tempDir, pathInTempDir);
            // If the file already exists in the temp directory then remove it
            // so it can be replaced with the contents of the InputStream
            if (tempDirFile.exists()) {
                tempDirFile.delete();
            }

            fos = new FileOutputStream(tempDirFile);
            bos = new BufferedOutputStream(fos);

            byte[] buff = new byte[2048];
            int bytesRead;

            // Simple read/write loop.
            while (-1 != (bytesRead = istream.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }

            bos.flush();
        } finally {
            try {
                istream.close();
            } catch (IOException e) {
                VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
        }
        return tempDirFile;
    }

    /**
     * Return the reference to the file within the TempDirectory location as specified by the file name. The result may or may not
     * already exist on the file system.
     * 
     * @param pathInTempDir
     * @return
     */
    protected File getTempDirectoryFile( final TempDirectory tempDir,
                                         final String pathInTempDir ) {
        ArgCheck.isNotNull(tempDir);
        ArgCheck.isNotNull(pathInTempDir);
        ArgCheck.isNotZeroLength(pathInTempDir);

        final IPath tempDirPath = new Path(tempDir.getPath());
        final String tempDirName = tempDirPath.lastSegment();

        // Remove the tempDirPath from the fileName if it already prepended
        String tempFileName = pathInTempDir;
        int tempDirNameIndex = tempFileName.indexOf(tempDirName);
        if (tempDirNameIndex != -1) {
            tempDirNameIndex += tempDirName.length();
            if (tempDirNameIndex < tempFileName.length()) {
                tempFileName = tempFileName.substring(tempDirNameIndex);
            }
        }
        final IPath pathToFile = new Path(tempFileName);

        // Create an folder hierarchy to define the path to the file
        if (pathToFile.segmentCount() > 1) {
            File baseFile = tempDirPath.toFile();
            String[] segments = pathToFile.segments();
            for (int i = 0; i != (segments.length - 1); ++i) {
                String folderName = segments[i];
                File folder = new File(baseFile, folderName);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                baseFile = folder;
            }
        }

        final IPath tempDirFilePath = tempDirPath.append(new Path(tempFileName));
        return tempDirFilePath.toFile();
    }

    protected synchronized TempDirectory createTempDirectory( final File vdbWorkingFolder ) {
        ArgCheck.isNotNull(vdbWorkingFolder);

        // Create a temporary directory under the vdb working folder location to be
        // used by the context to extract the contents of the zip file. If the working
        // folder does not yet exist, create it
        if (!vdbWorkingFolder.exists()) {
            vdbWorkingFolder.mkdirs();
        }
        final String absolutePath = vdbWorkingFolder.getAbsolutePath();

        // Create temporary directory checking the file system to ensure the
        // file represents a new and non-existent folder
        TempDirectory tempDir = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
        File tempDirectory = new File(tempDir.getPath());
        while (tempDirectory.exists()) {
            try {
                Thread.sleep(10);
            } catch (final InterruptedException ignored) {
            }
            tempDir = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
            tempDirectory = new File(tempDir.getPath());
        }
        tempDir.create();

        return tempDir;
    }

    // --------------------------------------------------------------------------------
    // Helper methods to work with XSD file using JDOM
    // --------------------------------------------------------------------------------

    /**
     * Build a JDOM Document instance from the specified stream.
     * 
     * @param istream the InputStream; may not be null
     * @return
     * @throws VdbEditException
     * @since 5.0
     */
    protected Document buildDocument( final InputStream istream ) throws IOException {
        ArgCheck.isNotNull(istream);
        Document doc = null;
        try {
            doc = getXMLReaderWriter().readDocument(istream);
        } catch (Exception e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
            throw new IOException(e.getLocalizedMessage());
        } finally {
            try {
                istream.close();
            } catch (IOException e) {
                VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
            }
        }
        return doc;
    }

    protected XMLReaderWriter getXMLReaderWriter() {
        return new XMLReaderWriterImpl();
    }

    /**
     * Encode any xs:import or xs:include "schemaLocation" values such that the path can be used as a URL within a MetaMatrix
     * server.
     * 
     * @param doc the JDOM Document instance for the XSD file being processed
     * @param pathInArchive the archive path for the XSD file being processed
     * @param targetDirectory the base directory to which all archive paths are relative to
     * @since 5.0
     */
    protected void encodeXsdSchemaDirectivePaths( final Document doc,
                                                  final String pathInArchive,
                                                  final File targetDirectory ) {
        ArgCheck.isNotNull(doc);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        ArgCheck.isNotNull(targetDirectory);

        // Create the absolute path to the target XSD file. This should be the same
        // file that the Document instance was created from
        File targetFile = new File(targetDirectory, pathInArchive);

        // Retrieve all "xs:import" and "xs:include" elements under the root
        List directives = new ArrayList();
        Namespace xsNS = Namespace.getNamespace(XSDPackage.eNS_PREFIX, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        directives.addAll(doc.getRootElement().getChildren(XSD_IMPORT_TAG_NAME, xsNS));
        directives.addAll(doc.getRootElement().getChildren(XSD_INCLUDE_TAG_NAME, xsNS));

        for (Iterator i = directives.iterator(); i.hasNext();) {
            Element elm = (Element)i.next();
            for (Iterator j = elm.getAttributes().iterator(); j.hasNext();) {
                Attribute a = (Attribute)j.next();

                // Get the "schemaLocation" attribute value ...
                if (XSD_SCHEMA_DIRECTIVE_LOCATION_ATTRIBUTE_NAME.equals(a.getName())) {

                    // If the location is relative to the XSD file containing the import/include
                    // then first convert it to an absolute location prior to encoding
                    URI baseUri = URI.createFileURI(targetFile.getAbsolutePath());
                    URI locationUri = URI.createURI(a.getValue());
                    if (baseUri.isHierarchical() && !baseUri.isRelative() && locationUri.isRelative()) {
                        locationUri = locationUri.resolve(baseUri);
                    }

                    // Next convert the import/include location to be consistent with the path to
                    // that file within the zip or temp directory then encode the path so that it
                    // can be used as a URL within a MetaMatrix server
                    if (locationUri.isFile()) {
                        String relativePath = getPathRelativeToFolder(targetDirectory, new File(locationUri.toFileString()));
                        String updatedPath = encodePathForUrl(new Path(relativePath)).toString();
                        a.setValue(URL_ROOT_FOR_VDB + updatedPath + URL_SUFFIX_FOR_VDB);

                        // Convert any import/include references to
                        // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
                        // to the "builtInDataTypes.xsd" resource that will be added to the VDB when it is saved
                    } else if (DatatypeConstants.BUILTIN_DATATYPES_URI.equals(locationUri.toString())) {
                        String relativePath = DatatypeConstants.DATATYPES_MODEL_FILE_NAME;
                        String updatedPath = encodePathForUrl(new Path(relativePath)).toString();
                        a.setValue(URL_ROOT_FOR_VDB + updatedPath + URL_SUFFIX_FOR_VDB);
                    }
                }
            }
        }
    }

    /**
     * Decode any xs:import or xs:include "schemaLocation" values associated with XSD files stored within a VDB. Those paths would
     * have been previously encoded so that they can be used as a URL within a MetaMatrix server.
     * 
     * @param doc the JDOM Document instance for the XSD file being processed
     * @param pathInArchive the archive path for the XSD file being processed
     * @param targetDirectory the base directory to which all archive paths are relative to
     * @since 5.0
     */
    protected void decodeXsdSchemaDirectivePaths( final Document doc,
                                                  final String pathInArchive,
                                                  final File targetDirectory ) {
        ArgCheck.isNotNull(doc);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        ArgCheck.isNotNull(targetDirectory);

        // Create the absolute path to the target XSD file. This should be the same
        // file that the Document instance was created from
        File targetFile = new File(targetDirectory, pathInArchive);
        URI baseUri = URI.createFileURI(targetFile.getAbsolutePath());

        // Retrieve all "xs:import" and "xs:include" elements under the root
        List directives = new ArrayList();
        Namespace xsNS = Namespace.getNamespace(XSDPackage.eNS_PREFIX, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        directives.addAll(doc.getRootElement().getChildren(XSD_IMPORT_TAG_NAME, xsNS));
        directives.addAll(doc.getRootElement().getChildren(XSD_INCLUDE_TAG_NAME, xsNS));

        for (Iterator i = directives.iterator(); i.hasNext();) {
            Element elm = (Element)i.next();

            for (Iterator j = elm.getAttributes().iterator(); j.hasNext();) {
                Attribute a = (Attribute)j.next();

                // Get the "schemaLocation" attribute value ...
                if (XSD_SCHEMA_DIRECTIVE_LOCATION_ATTRIBUTE_NAME.equals(a.getName())) {
                    String location = a.getValue();

                    // Remove the "http://vdb.metamatrix.com" prefix and "?vdbTokens=true" suffix from the
                    // schema location. The result should be an encoded path consistent with the path to
                    // that file within the zip or temp directory
                    if (location != null && location.startsWith(URL_ROOT_FOR_VDB) && location.endsWith(URL_SUFFIX_FOR_VDB)) {
                        int endIndex = location.lastIndexOf(URL_SUFFIX_FOR_VDB);
                        int beginIndex = URL_ROOT_FOR_VDB.length();
                        if (endIndex > 0 && beginIndex < endIndex) {
                            location = location.substring(beginIndex, endIndex);

                            // Next decode the path removing all escape characters
                            location = decodePathForUrl(new Path(location)).toString();

                            // Convert any path to "builtInDataTypes.xsd" back to its logical location of
                            // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
                            if (location.endsWith(DatatypeConstants.DATATYPES_MODEL_FILE_NAME)) {
                                a.setValue(DatatypeConstants.BUILTIN_DATATYPES_URI);
                                continue;
                            }

                            // Finally, convert into a path relative to the XSD file containing the import/include
                            File importFile = new File(targetDirectory, location);
                            URI importUri = URI.createFileURI(importFile.getAbsolutePath());
                            boolean deresolve = (baseUri != null && !baseUri.isRelative() && baseUri.isHierarchical());
                            if (deresolve && !importUri.isRelative()) {
                                URI deresolvedURI = importUri.deresolve(baseUri, true, true, false);
                                if (deresolvedURI.hasRelativePath()) {
                                    importUri = deresolvedURI;
                                }
                            }
                            a.setValue(URI.decode(importUri.toString()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Utility method to encode the segments of the supplied path such that the total path can be used within a URL. For example,
     * the path "<code>/Project Name/folder name/model name.txt</code>" cannot be directly placed into a URL, but instead needs to
     * be escaped: <code>/Project%20Name/folder%20name/model%20name.txt</code>.
     */
    protected IPath encodePathForUrl( final IPath pathToResourceInVdb ) {
        final String[] pathSegments = pathToResourceInVdb.segments();
        // Go through the segments and check each for validity ...
        IPath pathWithEncoding = Path.ROOT;
        for (int i = 0; i < pathSegments.length; i++) {
            final String segment = pathSegments[i];

            // Handle the '.' character explicity ...
            final StringBuffer encodedSegment = new StringBuffer();
            final String tokenDelimSet = ".?_"; //$NON-NLS-1$
            final EnhancedStringTokenizer segmentTokens = new EnhancedStringTokenizer(segment, tokenDelimSet);
            while (segmentTokens.hasMoreTokens()) {
                final String token = segmentTokens.nextToken();
                final String delims = segmentTokens.nextDelimiters();
                final String encodedSegmentToken = UriUtil.escape(token);
                encodedSegment.append(encodedSegmentToken);
                if (delims != null) {
                    encodedSegment.append(delims);
                }
            }

            pathWithEncoding = pathWithEncoding.append(encodedSegment.toString());
        }
        return pathWithEncoding;
    }

    /**
     * Utility method to decode the segments of the supplied path such that the total path can be used within a URI. For example,
     * the path "<code>/Project Name/folder name/model name.txt</code>" cannot be directly placed into a URL, but instead needs to
     * be escaped: <code>/Project%20Name/folder%20name/model%20name.txt</code>.
     */
    protected IPath decodePathForUrl( final IPath pathToResourceInVdb ) {
        final String[] pathSegments = pathToResourceInVdb.segments();
        // Go through the segments and check each for validity ...
        IPath pathWithEncoding = Path.ROOT;
        for (int i = 0; i < pathSegments.length; i++) {
            final String segment = pathSegments[i];

            // Handle the '.' character explicity ...
            final StringBuffer decodedSegment = new StringBuffer();
            final String tokenDelimSet = ".?_"; //$NON-NLS-1$
            final EnhancedStringTokenizer segmentTokens = new EnhancedStringTokenizer(segment, tokenDelimSet);
            while (segmentTokens.hasMoreTokens()) {
                final String token = segmentTokens.nextToken();
                final String delims = segmentTokens.nextDelimiters();
                final String decodedSegmentToken = UriUtil.unescape(token);
                decodedSegment.append(decodedSegmentToken);
                if (delims != null) {
                    decodedSegment.append(delims);
                }
            }

            pathWithEncoding = pathWithEncoding.append(decodedSegment.toString());
        }
        return pathWithEncoding;
    }

    protected String writeToString( final Document doc,
                                    final String indent,
                                    final boolean newlines ) throws IOException {
        ArgCheck.isNotNull(doc);

        Format format = Format.getPrettyFormat();
        format.setIndent(indent);
        if (newlines) {
            format.setLineSeparator("\n"); //$NON-NLS-1$
        } else {
            format.setLineSeparator(""); //$NON-NLS-1$
        }
        XMLOutputter outputter = new XMLOutputter(format);
        StringWriter writer = new StringWriter();
        outputter.output(doc, writer);

        return writer.getBuffer().toString();
    }

    protected String getPathRelativeToFolder( final File parentFolder,
                                              final File f ) {
        ArgCheck.isNotNull(parentFolder);
        ArgCheck.isNotNull(f);

        String relativePath = null;
        try {
            String folderPath = parentFolder.getCanonicalPath();
            String filePath = f.getCanonicalPath();
            if (filePath.startsWith(folderPath)) {
                relativePath = filePath.substring(folderPath.length());
            }
        } catch (IOException e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }
        return relativePath;
    }

    protected long getCheckSum( final File f ) {
        ArgCheck.isNotNull(f);
        try {
            return FileUtils.getCheckSum(f);
        } catch (Exception e) {
            final String msg = VdbEditPlugin.Util.getString("VdbContextImpl.Error_computing_checksum", f); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
        }
        return 0;
    }

}
