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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.jdom.Document;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.index.IIndex;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.ChecksumUtil;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.core.util.UriUtil;
import com.metamatrix.core.vdb.VdbConstants;
import com.metamatrix.core.xslt.Style;
import com.metamatrix.core.xslt.StyleRegistry;
import com.metamatrix.internal.core.index.BlocksIndexInput;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.internal.core.index.IndexInput;
import com.metamatrix.internal.core.xml.JdomHelper;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.internal.core.xml.xmi.XMIHeaderReader;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.wsdl.io.WsdlResourceFactoryImpl;
import com.metamatrix.metamodels.xsd.XsdResourceFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelSourceAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.refactor.ExternalReferenceVisitor;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.util.EnhancedStringTokenizer;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.ProblemMarker;
import com.metamatrix.modeler.core.validation.ProblemMarkerContainer;
import com.metamatrix.modeler.core.validation.Severity;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.ddl.DdlOptions;
import com.metamatrix.modeler.ddl.DdlPlugin;
import com.metamatrix.modeler.ddl.DdlWriter;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceFactory;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.vdb.edit.VdbArtifactGenerator;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.VdbGenerationContext;
import com.metamatrix.vdb.edit.VdbGenerationContextFactory;
import com.metamatrix.vdb.edit.VdbGenerationContextParameters;
import com.metamatrix.vdb.edit.VdbWsdlGenerationOptions;
import com.metamatrix.vdb.edit.loader.VDBWriter;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelAccessibility;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;
import com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl;
import com.metamatrix.vdb.edit.modelgen.MaterializedViewModelGenerator;
import com.metamatrix.vdb.materialization.DatabaseDialect;
import com.metamatrix.vdb.materialization.MaterializedViewScriptGenerator;
import com.metamatrix.vdb.materialization.MaterializedViewScriptGeneratorImpl;
import com.metamatrix.vdb.materialization.ScriptDecorator;
import com.metamatrix.vdb.materialization.ScriptType;
import com.metamatrix.vdb.materialization.template.MaterializedViewData;
import com.metamatrix.vdb.materialization.template.TemplateData;

/**
 * VdbEditingContextImpl
 */
public class VdbEditingContextImpl implements VdbEditingContext, InternalVdbEditingContext {

    private static final boolean DEBUG_ON = ModelerCore.DEBUG_VDB_EDITING_CONTEXT;

    protected static final int AMOUNT_OF_WORK_FOR_MANIFEST_MODEL = 400;
    protected static final int AMOUNT_OF_WORK_FOR_GENERATING_INDEXES_PER_FILE = 400;
    protected static final int AMOUNT_OF_WORK_FOR_SAVING_ARCHIVE_PER_FILE = 100;
    protected static final int AMOUNT_OF_WORK_FOR_MANIFEST_MODEL_PROBLEMS = 50;
    protected static final int AMOUNT_OF_WORK_FOR_VALIDATING_RESOURCES = 1000;
    protected static final int AMOUNT_OF_WORK_FOR_WSDL = 400;
    protected static final int AMOUNT_OF_WORK_FOR_MATERIALIZED_VIEW = 400;

    public static final int PRODUCE_INDEX_WITH_NO_PROBLEMS = 3001;
    public static final int PRODUCE_INDEX_WITH_WARNINGS = 3002;
    public static final int PRODUCE_INDEX_WITH_ERRORS = 3003;
    public static final int PRODUCE_INDEX_WITH_WARNINGS_AND_ERRORS = 3004;
    public static final int PRODUCE_INDEX_WITH_NO_WARNINGS_AND_ERRORS = 3005;

    public static final int DEFAULT_VDB_FORM = VdbFileWriter.FORM_JAR;

    public static final String PATH_OF_INDEXES_IN_ARCHIVE = "runtime-inf/"; //$NON-NLS-1$

    protected static final String MODEL_FILE_PREFIX = "ManfiestModelFor"; //$NON-NLS-1$
    protected static final String MODEL_FILE_SUFFIX = ".xml"; //$NON-NLS-1$

    protected static final String MATERIALIZATION_MODEL_NAME = "MaterializationModel"; //$NON-NLS-1$
    protected static final String MATERIALIZATION_MODEL_FILE_SUFFIX = ".xmi"; //$NON-NLS-1$

    protected static final String MATERIALIZATION_DDL_FILE_PREFIX = ScriptType.MATERIALIZATION_MODEL_FILE_PREFIX;
    protected static final String MATERIALIZATION_DDL_FILE_SUFFIX = ScriptType.MATERIALIZATION_SCRIPT_FILE_SUFFIX;

    protected static final String WSDL_FILE_PREFIX = "WsdlModelFor"; //$NON-NLS-1$
    protected static final String WSDL_FILE_SUFFIX = ".wsdl"; //$NON-NLS-1$

    protected static final String PATH_OF_USERFILES_IN_ARCHIVE = "user-files/"; //$NON-NLS-1$

    private static final ModelReference[] EMPTY_MODEL_REFERENCE_ARRAY = new ModelReference[0];

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    static {
        // Initialize the metamodel(s) ...
        ManifestPackageImpl.init();
    }

    private IPath vdbFilePath;
    private final IPath vdbWorkingPath;
    private final Container resourceSet;
    private final VdbInputResourceFinder vdbInputResourceFinder;
    private Map resourceOptions;
    private ZipFile vdbArchive;
    private TempDirectory tempDirectory;
    private int vdbArchiveForm;

    private Resource manifestResource;
    private Resource materializationModel;

    private Collection materializationModelDdlFiles;
    private Collection userFiles;
    private Map materializedModelMap;
    private final VdbWsdlGenerationOptionsImpl wsdlOptions;

    private VirtualDatabase virtualDatabase;
    private Collection addedResourceUrisSinceOpen;
    private Map modelRefsByPath;
    private Map pathsByResourceUri;
    private boolean loadModelsOnOpen;
    private boolean performServerValidation;
    private final ListenerList changeListeners;
    private final ListenerList vetoListeners;
    private Properties executionProperties;
    private VdbGenerationContextFactory vdbGenerationContextFactory;

    /**
     * Construct an instance of a VDB editing context. This constructor uses the {@link WsVdbInputResourceFinder}.
     * 
     * @param vdbFilePath the fully-qualified and absolute path to the VDB archive file; may not be null
     */
    public VdbEditingContextImpl( final IPath vdbFilePath ) throws CoreException {
        this(vdbFilePath, null, new WsVdbInputResourceFinder());
    }

    /**
     * Construct an instance of VdbEditingContextImpl.
     * 
     * @param vdbFilePath the fully-qualified and absolute path to the VDB archive file; may not be null
     * @param finder the {@link VdbInputResourceFinder}that should be used to locate models given {@link ModelReference}s; may not
     *        be null
     */
    public VdbEditingContextImpl( final IPath vdbFilePath,
                                  final VdbInputResourceFinder finder ) throws CoreException {
        this(vdbFilePath, null, finder);
    }

    /**
     * Construct an instance of VdbEditingContextImpl.
     * 
     * @param vdbFilePath the fully-qualified and absolute path to the VDB archive file; may not be null
     * @param vdbWorkingPath the fully-qualified and absolute path to the VDB working folder. The working folder is used for
     *        temporary extraction of the vdb contents when the editor is opened. If null is specified the location is defined by
     *        VdbEditPlugin.getVdbWorkingDirectory().
     * @param finder the {@link VdbInputResourceFinder}that should be used to locate models given {@link ModelReference}s; may not
     *        be null
     */
    public VdbEditingContextImpl( final IPath vdbFilePath,
                                  final IPath vdbWorkingPath,
                                  final VdbInputResourceFinder finder ) throws CoreException {
        ArgCheck.isNotNull(vdbFilePath);

        this.vdbFilePath = vdbFilePath;
        this.vdbWorkingPath = vdbWorkingPath;
        this.resourceSet = ModelerCore.createContainer(VDB_CONTAINER_NAME);
        // this.addedModelPathsSinceOpen = new HashSet();
        this.addedResourceUrisSinceOpen = new ArrayList();
        this.pathsByResourceUri = new HashMap();
        this.vdbArchiveForm = DEFAULT_VDB_FORM;
        this.vdbInputResourceFinder = finder;
        this.wsdlOptions = new VdbWsdlGenerationOptionsImpl(this);
        this.materializationModel = null;
        this.materializedModelMap = null;
        this.materializationModelDdlFiles = null;
        this.userFiles = null;
        this.loadModelsOnOpen = (VdbEditPlugin.getInstance() == null ? false : true);
        this.performServerValidation = true;
        this.changeListeners = new ListenerList(ListenerList.IDENTITY);
        this.vetoListeners = new ListenerList(ListenerList.IDENTITY);

        initialize();
    }

    /**
     * Set to true if we only need to examine the contents of the VDB and not edit and save it. We do not need to load all the
     * model files into the context's internal resource set
     * 
     * @param b
     * @since 4.2
     */
    public void setLoadModelsOnOpen( final boolean b ) {
        this.loadModelsOnOpen = b;
    }

    /**
     * @return
     */
    public int getVdbArchiveForm() {
        return vdbArchiveForm;
    }

    /**
     * @param i
     */
    public void setVdbArchiveForm( int form ) {
        VdbFileWriter.argCheckForm(form);
        vdbArchiveForm = form;
    }

    private void initialize() {

        // Register a resource factory ...
        Resource.Factory.Registry reg = this.getVdbContainer().getResourceFactoryRegistry();
        if (reg == null) {
            reg = new ResourceFactoryRegistryImpl();
            this.getVdbContainer().setResourceFactoryRegistry(reg);
        }
        final Map extensionMap = reg.getExtensionToFactoryMap();
        final Map protocolMap = reg.getProtocolToFactoryMap();

        // Make sure the MtkXmiResourceFactory factory is registered
        ResourceFactoryImpl factory = new MtkXmiResourceFactory();
        if (!extensionMap.containsKey(ModelUtil.EXTENSION_XMI)) {
            extensionMap.put(ModelUtil.EXTENSION_XMI, factory);
        }

        if (!extensionMap.containsKey(ModelUtil.EXTENSION_XML)) {
            extensionMap.put(ModelUtil.EXTENSION_XML, factory);
        }

        if (!extensionMap.containsKey(ModelUtil.EXTENSION_ECORE)) {
            extensionMap.put(ModelUtil.EXTENSION_ECORE, factory);
        }

        // Make sure the XSDResourceFactoryImpl factory is registered
        if (!extensionMap.containsKey(ModelUtil.EXTENSION_XSD)) {
            factory = new XsdResourceFactory();
            extensionMap.put(ModelUtil.EXTENSION_XSD, factory);
        }

        // Make sure the WsdlResourceFactoryImpl factory is registered
        if (!extensionMap.containsKey(ModelUtil.EXTENSION_WSDL)) {
            factory = new WsdlResourceFactoryImpl();
            extensionMap.put(ModelUtil.EXTENSION_WSDL, factory);
        }

        // Register the XMI factory for all unknown extensions and protocols
        factory = new XMIResourceFactoryImpl();
        extensionMap.put("*", factory); //$NON-NLS-1$
        protocolMap.put("*", factory); //$NON-NLS-1$

        // Set the TempDirectory reference on the VdbUriConverter
        this.resourceSet.setURIConverter(new VdbUriConverter(this.getTempDirectory()));

        // // Use a specialized ResourceFinder that can resolve schema location URIs that
        // // have been modified for the XSD resources inside the VDB
        // this.resourceSet.setResourceFinder(new VdbContainerResourceFinder(this.resourceSet));

    }

    public Map getOptions() {
        if (this.resourceOptions == null) {
            this.resourceOptions = createOptions();
        }
        return this.resourceOptions;
    }

    public ManifestFactory getManifestFactory() {
        return ManifestFactory.eINSTANCE;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#open()
     */
    public synchronized void open() throws VdbEditException, IOException {
        open(true);
    }

    private synchronized void open( boolean notify ) throws VdbEditException, IOException {
        if (this.vdbArchive == null) {
            if (DEBUG_ON) {
                VdbEditPlugin.Util.log("open(): Opening VdbEditingContextImpl - " + this.vdbFilePath); //$NON-NLS-1$
            }
            final File vdbFile = this.vdbFilePath.toFile();

            // Open the archive ...
            if (vdbFile.exists() && vdbFile.length() > 0) {
                this.vdbArchive = new ZipFile(vdbFile);
            }

            // Load the manifest into the context's internal resource set ...
            IPath manifestPath = new Path(MANIFEST_MODEL_NAME);
            URI fakeUri = this.getInternalResourceUri(manifestPath);
            InputStream istream = null;

            try {
                istream = this.getArchiveResourceStream(MANIFEST_MODEL_NAME);
                this.manifestResource = this.addToInternalResourceSet(istream, fakeUri, manifestPath);
            } catch (Exception theException) {
                // make sure manifestResource is null so that block of code after the finally throws exception
                this.manifestResource = null;
                VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
            } finally {
                // close input stream if necessary
                if (istream != null) {
                    try {
                        istream.close();
                    } catch (IOException theException) {
                        VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    }
                }
            }

            if (this.manifestResource == null) {
                final Object[] params = new Object[] {MANIFEST_MODEL_NAME, this.vdbFilePath};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_load_manifest_file", params); //$NON-NLS-1$
                throw new VdbEditException(msg);
            }

            // If we only need to examine the contents of the VDB and not
            // edit and save it then we do not need to load all the model
            // files into the context's internal resource set
            if (!this.loadModelsOnOpen) {
                return;
            }

            // Load the manifest model into the TempDirectory location
            VirtualDatabase virtualDatabase = this.getVirtualDatabase();
            if (virtualDatabase != null) {
                this.addToTempDirectory(this.manifestResource, MANIFEST_MODEL_NAME);
                // defect 16579 - set up no models warning if needed:
                // (Note that this method checks whether adding this is necessary,
                // so I won't duplicate that code here.)
                addNoModelsWarning(virtualDatabase);
            }

            // First load all models referenced by the manifest into the context's temp directory ...
            final List modelRefs = virtualDatabase.getModels();
            for (final Iterator iter = modelRefs.iterator(); iter.hasNext();) {
                final ModelReference modelRef = (ModelReference)iter.next();
                final String modelPath = modelRef.getModelLocation();
                if (DEBUG_ON) {
                    VdbEditPlugin.Util.log("open(): Loading ModelReference " + modelPath); //$NON-NLS-1$
                }

                try {
                    istream = this.getArchiveResourceStream(modelPath);

                    final File modelFile = this.addToTempDirectory(istream, modelPath);

                    if (modelRef.getChecksum() == 0) {
                        final long checkSum = this.getCheckSum(modelFile);
                        modelRef.setChecksum(checkSum);
                    }
                } catch (Exception theException) {
                    String eMsg = theException.getLocalizedMessage();
                    if ((eMsg == null) || (eMsg.length() == 0)) {
                        eMsg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.noDetailErrorMsg"); //$NON-NLS-1$
                    }

                    final Object[] params = new Object[] {modelPath, eMsg};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorLoadingReferencedModel", params); //$NON-NLS-1$
                    createProblem(virtualDatabase, IStatus.ERROR, msg, theException);
                } finally {
                    // close input stream if necessary
                    if (istream != null) {
                        try {
                            istream.close();
                        } catch (IOException theException) {
                            VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getMessage());
                        }
                    }
                }
            }

            // Load all models referenced by the manifest into the context's internal resource set ...
            Collection xsdResources = new ArrayList(modelRefs.size());
            for (final Iterator iter = modelRefs.iterator(); iter.hasNext();) {
                final ModelReference modelRef = (ModelReference)iter.next();
                final String modelPath = modelRef.getModelLocation();

                fakeUri = this.getInternalResourceUri(new Path(modelPath));

                try {
                    istream = this.getArchiveResourceStream(modelPath);
                    Resource r = this.addToInternalResourceSet(istream, fakeUri, new Path(modelPath));
                    if (r instanceof XSDResourceImpl) {
                        xsdResources.add(r);
                    }
                } catch (Exception theException) {
                    String eMsg = theException.getLocalizedMessage();
                    if ((eMsg == null) || (eMsg.length() == 0)) {
                        eMsg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.noDetailErrorMsg"); //$NON-NLS-1$
                    }

                    final Object[] params = new Object[] {modelPath, eMsg};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorLoadingReferencedModel", params); //$NON-NLS-1$
                    createProblem(virtualDatabase, IStatus.ERROR, msg, theException);
                } finally {
                    // close input stream if necessary
                    if (istream != null) {
                        try {
                            istream.close();
                        } catch (IOException theException) {
                            VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getMessage());
                        }
                    }
                }
            }

            // Reset the schemaLocation URLs of the form "http://vdb.metamatrix.com<ModelPath>?vdbTokens=true",
            // back to the relative file location form. The locations will remain this way until the context
            // is saved again at which point the locations are updated before saving in the zip file
            for (Iterator iter = xsdResources.iterator(); iter.hasNext();) {
                Resource r = (Resource)iter.next();
                resetXsdResource(r);
            }

            // Write all non-models referenced by the manifest into the context's temp directory ...
            final List nonModelRefs = virtualDatabase.getNonModels();
            for (final Iterator iter = nonModelRefs.iterator(); iter.hasNext();) {
                final NonModelReference nonModelRef = (NonModelReference)iter.next();
                final String nonModelPath = nonModelRef.getPath();

                try {
                    istream = this.getArchiveResourceStream(nonModelPath);
                    final File modelFile = this.addToTempDirectory(istream, nonModelPath);

                    if (nonModelRef.getChecksum() == 0) {
                        final long checkSum = this.getCheckSum(modelFile);
                        nonModelRef.setChecksum(checkSum);
                    }
                } catch (Exception theException) {
                    String eMsg = theException.getLocalizedMessage();
                    if ((eMsg == null) || (eMsg.length() == 0)) {
                        eMsg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.noDetailErrorMsg"); //$NON-NLS-1$
                    }

                    final Object[] params = new Object[] {nonModelPath, eMsg};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorLoadingReferencedNonModel", params); //$NON-NLS-1$
                    createProblem(virtualDatabase, IStatus.ERROR, msg, theException);
                } finally {
                    // close input stream if necessary
                    if (istream != null) {
                        try {
                            istream.close();
                        } catch (IOException theException) {
                            VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getMessage());
                        }
                    }
                }
            }

            // Writes the User Files into the context's temp directory
            loadUserFiles();

            // Opening the VDB should not cause the manifest model to get marked as modified.
            // However, this can occur as a result of setting the checksum value on a
            // ModelReference. Before exiting the open method, ensure that the manifest
            // model is marked as not modified.
            this.manifestResource.setModified(false);

            // notify everyone about the change:
            if (notify) {
                fireStateChanged();
            } // endif
        }
    }

    /**
     * @since 4.2
     */
    private void resolveSchemaDirectives() {

        final List resources = new ArrayList(this.getVdbContainer().getResources());
        final List xsdResources = new ArrayList(resources.size());

        // Create a list of all XSD resources in the VDB
        for (Iterator i = resources.iterator(); i.hasNext();) {
            final Resource resource = (Resource)i.next();
            if (resource instanceof XSDResourceImpl) {
                xsdResources.add(resource);
            }
        }

        // Check each XSD resource for unresolved schema directives
        boolean hasUnresolvedSchemaDirective = false;
        for (Iterator i = xsdResources.iterator(); i.hasNext();) {
            final XSDResourceImpl xsdResource = (XSDResourceImpl)i.next();

            // The resource needs to be loaded to examine its contents ...
            try {
                if (!xsdResource.isLoaded()) {
                    xsdResource.load(getOptions());
                }
            } catch (IOException err) {
                VdbEditPlugin.Util.log(IStatus.ERROR, err, err.getLocalizedMessage());
                continue;
            }

            // Iterate over the contents looking for SchemaDirective instances (import, include, redefine)
            for (Iterator j = xsdResource.getSchema().eContents().iterator(); j.hasNext();) {
                Object content = j.next();
                if (content instanceof XSDSchemaDirective) {
                    final XSDSchemaDirective directive = (XSDSchemaDirective)content;

                    // If the directive is not yet resolved ...
                    if (directive.getResolvedSchema() == null) {
                        hasUnresolvedSchemaDirective = true;
                        break;
                    }
                }
            }
            if (hasUnresolvedSchemaDirective) {
                break;
            }
        }

        // If an unresolved directive was found ...
        if (hasUnresolvedSchemaDirective) {
            // First, unload all XML Schema resources ...
            for (Iterator i = xsdResources.iterator(); i.hasNext();) {
                final Resource resource = (Resource)i.next();
                resource.unload();
            }
            // Next, reload the XML Schema resources to ensure that the include, import,
            // or redefine directives are resolved ...
            for (Iterator i = xsdResources.iterator(); i.hasNext();) {
                final Resource resource = (Resource)i.next();
                try {
                    if (!resource.isLoaded()) {
                        resource.load(getOptions());
                    }
                } catch (IOException err) {
                    VdbEditPlugin.Util.log(IStatus.ERROR, err, err.getLocalizedMessage());
                }
            }
            // Finally, mark all the XSD schema resources as not being modified...
            for (Iterator i = xsdResources.iterator(); i.hasNext();) {
                final Resource resource = (Resource)i.next();
                resource.setModified(false);
            }
        }

    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isOpen()
     */
    public boolean isOpen() {
        return (this.manifestResource != null);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#save()
     */
    public IStatus save( final IProgressMonitor monitor ) {
        return save(monitor, false);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    public IStatus save( final IProgressMonitor progressMonitor,
                         boolean minimal ) {
        assertIsOpen();
        assertModelsLoaded();

        if (isSaveRequired() || (!minimal && isSaveRequiredForValidVdb())) {

            // --------------------------------------------------------------------------------------------------------
            // Initializion of VirtualDatabase
            // --------------------------------------------------------------------------------------------------------

            // Compute the amount of work and start the monitor ...
            final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
            try {
                final int numModels = this.getVirtualDatabase().getModels().size();
                final int totalWork = AMOUNT_OF_WORK_FOR_MANIFEST_MODEL + AMOUNT_OF_WORK_FOR_MANIFEST_MODEL_PROBLEMS
                                      + AMOUNT_OF_WORK_FOR_WSDL + (AMOUNT_OF_WORK_FOR_SAVING_ARCHIVE_PER_FILE * (numModels + 1))
                                      + (AMOUNT_OF_WORK_FOR_GENERATING_INDEXES_PER_FILE * numModels)
                                      + (AMOUNT_OF_WORK_FOR_VALIDATING_RESOURCES);
                String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Saving", this.vdbFilePath); //$NON-NLS-1$
                monitor.beginTask(taskName, totalWork);

                // clear all ProblemMarkers prior to saving the vdb
                this.manifestResource.getErrors().clear();
                this.manifestResource.getWarnings().clear();

                final List problems = new LinkedList();

                // boolean indicating that the VDB has fatal erros
                // if true, ignore all operations and save the VDB
                boolean hasFatalProblems = false;

                // Add the built-in types resource to the VDB
                addGlobalResourcesToInternalResourceSet();

                // Depending on the order the XSDResources were loaded into the VDB there may
                // be an XSD resource in which import/include is unresolved. We need to check
                // for this and ensure all schema directives are resolved before proceeding.
                this.resolveSchemaDirectives();

                // Ensure that there are no eProxies before proceeding. This EcoreUtil method
                // only resolves external references if the EReference is a proxy-resolving
                // non-containment reference. Typically our EReferences are not of this type.
                // EcoreUtil.resolveAll(this.getVdbContainer());

                // --------------------------------------------------------------------------------------------------------
                // Validate the VDB manifest model to ensure there are no missing dependent models
                // --------------------------------------------------------------------------------------------------------

                // Validate the vdb itself checking for missing dependent models
                List eResources = new ArrayList(this.getVdbContainer().getResources());

                // MyDefect : 14430 Added code
                // Clear the problem markers only for those resources being validated
                this.clearProblemMarkers(this.getVirtualDatabase());
                final Collection modelRefsToValidate1 = this.getModelReferences(this.getInternalResourcesToValidate(problems),
                                                                                problems);
                for (Iterator iter = modelRefsToValidate1.iterator(); iter.hasNext();) {
                    final ModelReference modelRef = (ModelReference)iter.next();
                    this.clearProblemMarkers(modelRef);
                    if (DEBUG_ON) {
                        VdbEditPlugin.Util.log("save(): Clearing problem markers on " + modelRef.getModelLocation()); //$NON-NLS-1$
                    }
                }

                // MyDefect 16743
                IStatus validateStatus = validateVdb(eResources, true); // don't persist
                if (validateStatus != null && validateStatus.getSeverity() == IStatus.ERROR) {
                    hasFatalProblems = true; // catastrophic, missing models
                }

                // --------------------------------------------------------------------------------------------------------
                // Generate the materialization model
                // --------------------------------------------------------------------------------------------------------

                // Create the materialization model resource from all "materializable" virtual tables
                // found in the supplied collection of resources. Also populate the materializedModelMap
                // which is the map between the virtual table and the collection of physical tables
                // in the materialization model.
                if (!hasFatalProblems) {
                    // Remove any existing ModelReference to the materialized view model from
                    // the virtual database manifest and the internal resource set
                    final String modelPath = this.getMaterializedViewModelName(true);
                    final ModelReference modelRef = this.getModelReferenceByPath(new Path(modelPath));

                    if (modelRef != null) {
                        this.removeFromInternalResourceSet(modelRef);
                        this.getVirtualDatabase().getModels().remove(modelRef);
                    }

                    eResources = new ArrayList(this.getVdbContainer().getResources());
                    this.materializedModelMap = new HashMap();
                    this.materializationModel = this.createMaterialization(eResources,
                                                                           this.materializedModelMap,
                                                                           problems,
                                                                           monitor);
                    this.addMaterializationToVdb(this.materializationModel, this.getVirtualDatabase(), problems, monitor);

                    // Generate all the ddls scripts for the materialization model
                    this.materializationModelDdlFiles = this.generateDdlsForMaterialization(materializationModel,
                                                                                            problems,
                                                                                            monitor);

                    // Add the EMF Resource for the materialization model to the eResources collection
                    if (this.materializationModel != null && !eResources.contains(this.materializationModel)) {
                        this.addToInternalResourceSet(this.materializationModel, new Path(modelPath));
                    }
                }
                monitor.worked(AMOUNT_OF_WORK_FOR_MATERIALIZED_VIEW);

                // --------------------------------------------------------------------------------------------------------
                // Generate the materialization scripts
                // --------------------------------------------------------------------------------------------------------

                // Generate and add materialization scripts to VDB
                Collection scriptFiles = Collections.EMPTY_LIST;
                if (!hasFatalProblems) {
                    if (this.materializationModel != null) {
                        scriptFiles = this.generateMateriailizedViewLoadRefreshScripts(getVdbName(),
                                                                                       this.materializedModelMap,
                                                                                       problems,
                                                                                       monitor);
                    }
                }

                // --------------------------------------------------------------------------------------------------------
                // Generate the WSDL if required ...
                // --------------------------------------------------------------------------------------------------------

                File wsdlFile = null;
                if (!hasFatalProblems && this.wsdlOptions.canWsdlBeGenerated()) {
                    // Generate and write the WSDL ...
                    OutputStream wsdlStream = null;
                    IStatus genStatus = null;
                    try {
                        wsdlFile = this.getTempDirectoryFile(GENERATED_WSDL_FILENAME);
                        wsdlStream = new FileOutputStream(wsdlFile);
                        wsdlStream = new BufferedOutputStream(wsdlStream);

                        // Ensure that, at minimum, the target namespace is defined for the WSDL
                        // otherwise the resultant VDB will will not be deployable - defect 20917
                        WsdlOptions theWsdlOptions = this.wsdlOptions.getWsdlOptions(false);
                        if (theWsdlOptions == null) {
                            theWsdlOptions = ManifestFactory.eINSTANCE.createWsdlOptions();
                            theWsdlOptions.setTargetNamespaceUri(WSDL_DEFAULT_TARGET_NAMESPACE_URI_PREFIX
                                                                 + this.getVirtualDatabase().getName());
                            theWsdlOptions.setDefaultNamespaceUri(WSDL_DEFAULT_NAMESPACE_URI);
                            this.getVirtualDatabase().setWsdlOptions(theWsdlOptions);
                        }

                        genStatus = this.wsdlOptions.generateWsdl(monitor, wsdlStream);
                    } catch (IOException e) {
                        final Object[] params = new Object[] {wsdlFile.getAbsolutePath(), this.vdbFilePath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.ErrorWritingTemporaryFileForWsdl", params); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                        hasFatalProblems = true; // catastrophic, problems generating wsdl
                    } finally {
                        if (wsdlStream != null) {
                            try {
                                wsdlStream.close();
                            } catch (IOException e2) {
                                final Object[] params = new Object[] {wsdlFile.getAbsolutePath(), this.vdbFilePath};
                                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.ErrorClosingTemporaryWsdlFile", params); //$NON-NLS-1$
                                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e2));
                            }
                        }
                        monitor.worked(AMOUNT_OF_WORK_FOR_WSDL);
                    }
                    if (!genStatus.isOK()) {
                        if (genStatus instanceof MultiStatus) {
                            final IStatus[] children = ((MultiStatus)genStatus).getChildren();
                            for (int i = 0; i < children.length; i++) {
                                problems.add(children[i]);
                            }
                        } else {
                            problems.add(genStatus);
                        }
                    }
                } else {
                    monitor.worked(AMOUNT_OF_WORK_FOR_WSDL);
                }

                // --------------------------------------------------------------------------------------------------------
                // Generate the index files for all models in the VDB
                // --------------------------------------------------------------------------------------------------------
                // list of index files
                final List indexFiles = new LinkedList();
                final List indexFilenames = new LinkedList();
                if (!hasFatalProblems) {
                    // Update the model status information prior to indexing
                    List modelErrors = this.manifestResource.getErrors();
                    List modelWarnings = this.manifestResource.getWarnings();
                    IStatus modelStatus = createModelStatus(problems, modelErrors, modelWarnings);
                    if (modelStatus.getSeverity() == IStatus.ERROR) {
                        problems.add(modelStatus);
                        hasFatalProblems = true; // catastrophic, bad manifest model
                    }

                    // get all the resources in the resource set
                    final Container container = this.getVdbContainer();
                    // we need a copy of the resource list, so that if indexing modifies the list
                    // we dont modify the original list
                    eResources = new ArrayList(container.getResources());

                    if (DEBUG_ON) {
                        for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                            final Resource eResource = (Resource)iter.next();
                            VdbEditPlugin.Util.log("save(): Indexing " + eResource.getURI().lastSegment()); //$NON-NLS-1$
                        }
                    }

                    // Generate all of the index files ...
                    IStatus productionStatus = null;
                    if (!minimal) {
                        productionStatus = this.produce(modelStatus, indexFiles, indexFilenames, eResources, container, monitor);
                        if (!productionStatus.isOK()) {
                            problems.add(productionStatus);
                            hasFatalProblems = true; // catastrophic, problems generating indexes
                        }
                    }

                    // Update the model status information after to indexing
                    modelErrors = this.manifestResource.getErrors();
                    modelWarnings = this.manifestResource.getWarnings();
                    modelStatus = createModelStatus(problems, modelErrors, modelWarnings);
                    if (modelStatus.getSeverity() == IStatus.ERROR) {
                        problems.add(modelStatus);
                        hasFatalProblems = true; // catastrophic, bad manifest model
                    }
                }

                // --------------------------------------------------------------------------------------------------------
                // Validate the EMF resources in the VDB
                // --------------------------------------------------------------------------------------------------------

                if (!hasFatalProblems) {
                    taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.validating_the_resources"); //$NON-NLS-1$
                    monitor.setTaskName(taskName);

                    // Create a new ValidationContext to use for all validation
                    final ValidationContext context = (this.performServerValidation ? new ValidationContext() : ModelBuildUtil.createValidationContext());

                    // Get the list of resources to validate
                    eResources = this.getInternalResourcesToValidate(problems);

                    // Clear the problem markers only for those resources being validated
                    this.clearProblemMarkers(this.getVirtualDatabase());
                    final Collection modelRefsToValidate = this.getModelReferences(eResources, problems);
                    for (Iterator iter = modelRefsToValidate.iterator(); iter.hasNext();) {
                        final ModelReference modelRef = (ModelReference)iter.next();
                        this.clearProblemMarkers(modelRef);
                        if (DEBUG_ON) {
                            VdbEditPlugin.Util.log("save(): Clearing problem markers on " + modelRef.getModelLocation()); //$NON-NLS-1$
                        }
                    }

                    // Set the resources defining the extent of the validation
                    context.setResourceContainer(this.getVdbContainer());

                    Resource[] resourcesToValidate = new Resource[eResources.size()];
                    eResources.toArray(resourcesToValidate);
                    context.setResourcesToValidate(resourcesToValidate);
                    Resource[] resourcesInScope = new Resource[this.getVdbContainer().getResources().size()];
                    this.getVdbContainer().getResources().toArray(resourcesInScope);
                    context.setResourcesInScope(resourcesInScope);
                    context.setIndexLocation(this.getTempDirectory().getPath());
                    context.setUseServerIndexes(true);
                    context.setUseIndexesToResolve(true);
                    context.setCacheMappingRootResults(false);

                    // Validate the vdb itself checking for missing dependent models
                    // validateStatus = validateVdb(eResources,true); // and persist problem markers
                    // MyDefect 16743
                    validateStatus = validateVdb(eResources, false); // and persist problem markers
                    if (validateStatus != null && validateStatus.getSeverity() == IStatus.ERROR) {
                        hasFatalProblems = true; // catastrophic, missing models
                    }

                    // Validate each resource found in the editor's internal resource set
                    for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                        final Resource eResource = (Resource)iter.next();
                        if (DEBUG_ON) {
                            VdbEditPlugin.Util.log("save(): Validating " + eResource.getURI().lastSegment()); //$NON-NLS-1$
                        }
                        // always mark it not modified, the resources in
                        // the vdb are always in a non-modified state
                        eResource.setModified(false);
                        ModelBuildUtil.validateResource(monitor, eResource, context);

                        // The URI for each internal resource was creating using ModelReference.getPath();
                        String modelPath = getResourcePath(eResource);
                        if (MANIFEST_MODEL_NAME.equals(modelPath) || GENERATED_WSDL_FILENAME.equals(modelPath)) {
                            this.setContainerProblems(this.getVirtualDatabase(), context);
                        } else {
                            if (modelPath != null) {
                                final ModelReference modelRef = this.getModelReferenceByPath(new Path(modelPath));
                                // Was an EMF model that isn't a ModelReference (e.g., WSDL) ...
                                if (modelRef != null) {
                                    this.setModelReferenceProblems(modelRef, context);
                                }
                            }
                        }
                        context.clearResults();
                    }
                    context.clearState();
                }

                // validated any models
                monitor.worked(AMOUNT_OF_WORK_FOR_VALIDATING_RESOURCES);

                // --------------------------------------------------------------------------------------------------------
                // Update schema locations for all schemas in vdb
                // --------------------------------------------------------------------------------------------------------
                if (!hasFatalProblems) {
                    eResources = new ArrayList(this.getVdbContainer().getResources());
                    for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                        Resource eResource = (Resource)iter.next();
                        // update the xsd resource by placing tokens
                        updateXsdResource(eResource);
                    }
                }

                // --------------------------------------------------------------------------------------------------------
                // Compute the list of paths for archive entries ...
                // --------------------------------------------------------------------------------------------------------
                final List artifactsToWrite = new ArrayList();

                // Add the contents of the editor's internal resource set ...
                eResources = new ArrayList(this.getVdbContainer().getResources());
                for (final Iterator resourceIter = eResources.iterator(); resourceIter.hasNext();) {
                    final Resource eResource = (Resource)resourceIter.next();

                    // The URI for each internal resource was creating using ModelReference.getPath();
                    final String fileName = getResourcePath(eResource);
                    if (fileName == null) {
                        // Was an EMF model that isn't a ModelReference (e.g., WSDL) ...
                        continue;
                    }
                    final File tempFile = this.getTempDirectoryFile(fileName);
                    final IPath modelPath = new Path(fileName);

                    artifactsToWrite.add(new Artifact(modelPath, tempFile, AMOUNT_OF_WORK_FOR_SAVING_ARCHIVE_PER_FILE));
                }

                // Add the contents of the editor's non-model references ...
                for (final Iterator nonModelIter = this.getVirtualDatabase().getNonModels().iterator(); nonModelIter.hasNext();) {
                    final NonModelReference nonModelRef = (NonModelReference)nonModelIter.next();
                    final File tempFile = this.getTempDirectoryFile(nonModelRef);

                    // Defect 20444: update the properties in ConfigurationInfo.DEF that carry thd VDB name
                    if (nonModelRef.getName().equals(VdbConstants.DEF_FILE_NAME)) {
                        try {
                            // make sure the name of the VDB is correct in the DEF
                            VDBWriter.updateConfigDefFile(tempFile, getVdbName(), this.getExecutionProperties());
                        } catch (Exception theException) {
                            String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorUpdatingDefFileName", this.vdbFilePath); //$NON-NLS-1$
                            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, theException));
                        }
                    }

                    final IPath modelPath = new Path(nonModelRef.getPath());

                    artifactsToWrite.add(new Artifact(modelPath, tempFile, AMOUNT_OF_WORK_FOR_SAVING_ARCHIVE_PER_FILE));
                }

                // Add the indexes ...
                final IPath indexPath = new Path(PATH_OF_INDEXES_IN_ARCHIVE);
                final Iterator indexIter = indexFiles.iterator();
                final Iterator nameIter = indexFilenames.iterator();
                while (indexIter.hasNext()) {
                    final File indexFile = (File)indexIter.next();
                    final String indexFilename = (String)nameIter.next();
                    if (indexFile != null) {
                        // Remove the leading timestamp information from the temp file ...
                        final IPath indexFilePath = indexPath.append(indexFilename);
                        artifactsToWrite.add(new Artifact(indexFilePath, indexFile, AMOUNT_OF_WORK_FOR_SAVING_ARCHIVE_PER_FILE));
                    }
                }

                // Add the generated WSDL ...
                if (wsdlFile != null) {
                    artifactsToWrite.add(new Artifact(new Path(wsdlFile.getName()), wsdlFile, 0));
                }

                // Add the DDL scripts ...
                if (this.materializationModelDdlFiles != null && !this.materializationModelDdlFiles.isEmpty()) {
                    for (final Iterator iter2 = this.materializationModelDdlFiles.iterator(); iter2.hasNext();) {
                        final File ddlFile = (File)iter2.next();
                        artifactsToWrite.add(new Artifact(new Path(ddlFile.getName()), ddlFile, 0));
                    }
                }

                // Add the Materialization load/refresh scripts
                if (scriptFiles.size() > 0) {
                    for (final Iterator iter2 = scriptFiles.iterator(); iter2.hasNext();) {
                        final File scriptFile = (File)iter2.next();
                        artifactsToWrite.add(new Artifact(new Path(scriptFile.getName()), scriptFile, 0));
                    }
                }

                // Add user Files
                if (this.userFiles != null && !this.userFiles.isEmpty()) {
                    for (final Iterator iter2 = this.userFiles.iterator(); iter2.hasNext();) {
                        final File userFile = (File)iter2.next();
                        artifactsToWrite.add(new Artifact(new Path(PATH_OF_USERFILES_IN_ARCHIVE + userFile.getName()), userFile,
                                                          0));
                    }
                }

                // NOTE:
                // The artifact generators may add more artifacts to this list.

                // --------------------------------------------------------------------------------------------------------
                // Generate additional artifacts ...
                // --------------------------------------------------------------------------------------------------------
                if (!hasFatalProblems) {
                    taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.executingArtifactGenerators"); //$NON-NLS-1$
                    monitor.setTaskName(taskName);

                    hasFatalProblems = doGenerateAdditionalArtifacts(monitor, problems, artifactsToWrite);
                }

                // --------------------------------------------------------------------------------------------------------
                // Update the VDB manifest model
                // --------------------------------------------------------------------------------------------------------

                // Set the ProblemMarkers on the VirtualDatabase
                this.setVdbProblems(this.getVirtualDatabase(), problems);

                // Update the virtual database object (if required) ...
                updateVirtualDatabase(this.getVirtualDatabase());

                // all other problems are saved on the vdb, clear
                // subsequent problems are related to saving the VDB
                // we return these as a MultiStatus
                problems.clear();

                // --------------------------------------------------------------------------------------------------------
                // Write the contents of the internal resource set to the temporary directory location
                // --------------------------------------------------------------------------------------------------------

                taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.writing_the_resources"); //$NON-NLS-1$ 
                monitor.setTaskName(taskName);

                eResources = new ArrayList(this.getVdbContainer().getResources());
                for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                    final Resource eResource = (Resource)iter.next();

                    // The URI for each internal resource was creating using ModelReference.getPath();
                    String modelPath = getResourcePath(eResource);
                    if (modelPath == null) {
                        // Was an EMF model that isn't a ModelReference (e.g., WSDL) ...
                        continue;
                    }

                    try {
                        this.addToTempDirectory(eResource, modelPath);
                    } catch (FileNotFoundException e) {
                        final Object[] params = new Object[] {modelPath, this.vdbFilePath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_temporary_file_for_model", params); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                    } catch (IOException e) {
                        final Object[] params = new Object[] {modelPath, this.vdbFilePath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_writing_temporary_file_for_model", params); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                    } catch (Throwable e) {
                        final Object[] params = new Object[] {modelPath, this.vdbFilePath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unexpected_error_writing_temporary_file_for_model", params); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                    } finally {
                        monitor.worked(AMOUNT_OF_WORK_FOR_MANIFEST_MODEL);
                    }
                }

                // --------------------------------------------------------------------------------------------------------
                // Save the VDB contents to a zip file
                // --------------------------------------------------------------------------------------------------------

                // Build up the specification for the archive ...
                final VdbFileWriter writer = new VdbFileWriter(this.vdbFilePath, this.getVdbArchiveForm());

                // Add all of the artifacts ...
                final Iterator artifactIter = artifactsToWrite.iterator();
                while (artifactIter.hasNext()) {
                    final Artifact artifact = (Artifact)artifactIter.next();
                    final IPath path = artifact.path;
                    final File content = artifact.content;
                    final int work = artifact.workToWrite;
                    try {
                        writer.addEntry(path, content);
                    } catch (IOException e2) {
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.adding_to_archive", path); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e2));
                    } finally {
                        monitor.worked(work);
                    }
                }

                if (this.vdbArchive != null) {
                    // Close the archive (so we can write to the same file) ...
                    try {
                        this.vdbArchive.close();
                    } catch (Throwable e1) {
                        final Object[] params = new Object[] {this.vdbFilePath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_closing_vdb_archive_file_before_saving", params); //$NON-NLS-1$
                        problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e1));
                    } finally {
                        this.vdbArchive = null;
                    }
                }

                // Save the archive over the top ...
                IStatus writeStatus = null;
                final String desc = VdbEditPlugin.Util.getString("VdbEditingContextImpl.writing_archive"); //$NON-NLS-1$
                try {
                    writer.open();
                    writeStatus = writer.write(monitor);
                } catch (Throwable e2) {
                    final Object[] params = new Object[] {this.vdbFilePath};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_writing_archive", params); //$NON-NLS-1$
                    IStatus errStatus = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID,
                                                   VdbFileWriter.WRITING_ARCHIVE_WITH_ERRORS, msg, e2);
                    writeStatus = merge(errStatus, writeStatus, desc);
                } finally {
                    try {
                        writer.close();
                    } catch (Throwable e3) {
                        final Object[] params = new Object[] {this.vdbFilePath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_closing_archive_writer", params); //$NON-NLS-1$
                        IStatus errStatus = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
                        writeStatus = merge(errStatus, writeStatus, desc);
                    }
                }

                // // Clean up the temporary files ...
                // this.getTempDirectory().remove();
                // this.tempDirectory = null;

                // Reopen the archive ...
                try {
                    this.close(true, false, false);
                    this.setLoadModelsOnOpen(true);
                    this.open(false);
                } catch (Throwable e3) {
                    final Object[] params = new Object[] {this.vdbFilePath};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_reopening_archive_writer_after_save", params); //$NON-NLS-1$
                    IStatus errStatus = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
                    writeStatus = merge(errStatus, writeStatus, desc);
                }

                // Clear the collection holding all the URIs of any newly added resources if
                // and only if their were no fatal problems up to this point. If there were
                // fatal problem then we want to make sure we revalidate those same resources
                // next save
                if (!hasFatalProblems) {
                    this.addedResourceUrisSinceOpen.clear();
                    if (DEBUG_ON) {
                        VdbEditPlugin.Util.log("save(): Clearing addedResourceUrisSinceOpen collection"); //$NON-NLS-1$
                    }
                }

                // If we fail on writing the vdb then add a problem marker to
                // the VirtualDatabase and set the resource as still modified
                if (writeStatus.getSeverity() == IStatus.ERROR) {
                    final Object[] params = new Object[] {this.vdbFilePath};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Errors_were_encountered_writing_the_VDB_definition_file_0_-_see_the_error_log_for_details_1", params); //$NON-NLS-1$
                    this.createProblem(this.getVirtualDatabase(), IStatus.ERROR, msg, null);
                    this.setModified();
                }

                // collect write status
                problems.add(writeStatus);
                final IStatus allStatus = createSingleIStatus(problems, desc);

                // notify listeners save is complete
                fireStateChanged();
                return allStatus;
            } finally {
                // Mark the monitor as completed ...
                monitor.done();
            }
        }
        // Nothing to write ...
        final Object[] params = new Object[] {this.vdbFilePath};
        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.save_not_required", params); //$NON-NLS-1$
        final IStatus status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
        return status;
    }

    private class Artifact {
        public final IPath path;
        public final File content;
        public final int workToWrite;

        public Artifact( final IPath path,
                         final File content,
                         final int workToWrite ) {
            this.path = path;
            this.content = content;
            this.workToWrite = workToWrite;
        }
    }

    private class ArtifactGeneratorThread extends Thread {
        private VdbGenerationContext context;
        private VdbArtifactGenerator generator;
        private Throwable throwable;

        protected ArtifactGeneratorThread( final VdbGenerationContext context,
                                           final VdbArtifactGenerator generator ) {
            super("VdbArtifactGeneratorThread"); //$NON-NLS-1$
            this.context = context;
            this.generator = generator;
        }

        @Override
        public void run() {
            try {
                this.generator.execute(context);
            } catch (Throwable t) {
                this.throwable = t;
            }
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

    }

    public VdbGenerationContextFactory getVdbGenerationContextFactory() {
        if (this.vdbGenerationContextFactory == null) {
            this.vdbGenerationContextFactory = new VdbGenerationContextFactoryImpl();
        }
        return this.vdbGenerationContextFactory;
    }

    /**
     * @param monitor
     * @param problems
     * @param artifactsToWrite
     * @since 4.2
     */
    protected boolean doGenerateAdditionalArtifacts( final IProgressMonitor monitor,
                                                     final List problems,
                                                     final List artifactsToWrite ) {
        boolean hasFatalProblems = false;

        final List artifactGenerators = VdbEditPlugin.getVdbArtifactGenerators();
        if (artifactGenerators.size() == 0) {
            return hasFatalProblems;
        }

        // Create the list of existing models ...
        final List modelList = this.getVdbContainer().getResources();
        final Resource[] models = (Resource[])modelList.toArray(new Resource[modelList.size()]);

        // Iterate over the resources and create the maps ...
        final Map modelNamesByResource = new HashMap();
        final Map modelPathsByResource = new HashMap();
        final Map modelVisibilityByResource = new HashMap();
        final Map problemsByObjectId = new HashMap();
        final Iterator modelIter = modelList.iterator();
        while (modelIter.hasNext()) {
            final Resource eResource = (Resource)modelIter.next();
            final String modelPathString = getResourcePath(eResource);
            modelPathsByResource.put(eResource, modelPathString);
            final IPath path = new Path(modelPathString);
            // find model reference
            final ModelReference model = getModelReferenceByPath(path);
            if (model != null) {
                // Some Resources don't have ModelReferences (e.g., the manifest model)
                final String modelName = model.getName();
                modelNamesByResource.put(eResource, modelName);
            }
            final ModelReference modelRef = getModelReference(eResource);
            if (modelRef != null) {
                Boolean isVisible = (modelRef.isVisible() ? Boolean.TRUE : Boolean.FALSE);
                modelVisibilityByResource.put(eResource, isVisible);
                for (Iterator iter = modelRef.getMarkers().iterator(); iter.hasNext();) {
                    ProblemMarker marker = (ProblemMarker)iter.next();
                    EObject target = getProblemMarkerEObject(marker);
                    if (target != null) {
                        ObjectID targetId = ModelerCore.getObjectId(target);
                        List targetProblems = (List)problemsByObjectId.get(targetId);
                        if (targetProblems == null) {
                            targetProblems = new ArrayList(7);
                            problemsByObjectId.put(targetId, targetProblems);
                        }
                        targetProblems.add(marker);
                    }
                }
            }
        }

        // Determine the existing paths ...
        final List paths = new ArrayList();
        final Iterator artifactIter = artifactsToWrite.iterator();
        while (artifactIter.hasNext()) {
            final Artifact artifact = (Artifact)artifactIter.next();
            final IPath path = artifact.path;
            paths.add(path);
        }
        final IPath[] existingArtifactPaths = (IPath[])paths.toArray(new IPath[paths.size()]);

        // Find the temporary directory ...
        final TempDirectory tempDir = this.getTempDirectory();
        final File tempDirFile = new File(tempDir.getPath());
        final File tempDtcFolder = new File(tempDirFile, "" + System.currentTimeMillis() + "Dtc"); //$NON-NLS-1$ //$NON-NLS-2$
        /*final boolean created = */tempDtcFolder.mkdir();

        // Create the generation context ...
        final VdbGenerationContextParameters parameters = new VdbGenerationContextParameters();
        parameters.setModels(models);
        parameters.setExistingPathsInVdb(existingArtifactPaths);
        parameters.setModelNameByResource(modelNamesByResource);
        parameters.setModelVisibilityByResource(modelVisibilityByResource);
        parameters.setProblemsByObjectId(problemsByObjectId);
        parameters.setTempFolderAbsolutePath(tempDtcFolder.getAbsolutePath());
        parameters.setWorkspacePathByResource(modelPathsByResource);
        final VdbGenerationContext genContext = getVdbGenerationContextFactory().createVdbGenerationContext(parameters, monitor);

        // Loop over the generators and execute them ...
        final Iterator genIter = artifactGenerators.iterator();
        while (genIter.hasNext()) {
            if (monitor.isCanceled()) {
                return hasFatalProblems;
            }

            final VdbArtifactGenerator generator = (VdbArtifactGenerator)genIter.next();

            // Create a separate thread to do the generation. This is so that we can cancel it
            // if the (customer-supplied) generator goes on ad infinitum.
            final ArtifactGeneratorThread thread = new ArtifactGeneratorThread(genContext, generator);
            thread.start();

            // Poll for cancellation ...
            try {
                while (!monitor.isCanceled() && thread.isAlive()) {
                    final String displayMessage = genContext.getProgressMessage();
                    monitor.setTaskName(displayMessage);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // shouldn't really happen!
            } finally {
                // Looks for an exception in the connection thread ...
                final Throwable error = thread.getThrowable();
                if (error instanceof InterruptedException) {
                    // canceled!!!
                    final Object[] params = new Object[] {generator.getClass().getName()};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.generatorCancelled", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.WARNING, VdbEditPlugin.PLUGIN_ID, 0, msg, error);
                    problems.add(status);
                } else if (error != null) {
                    final Object[] params = new Object[] {generator.getClass().getName()};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorExecutingGenerator", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, error);
                    problems.add(status);
                    VdbEditPlugin.Util.log(status);
                }
            }
        }

        // Add all of the problems ...
        problems.addAll(genContext.getProblems());

        // Get all of the additional artifacts ...
        final Map newArtifactsByPath = genContext.getGeneratedArtifactsByPath();
        final Iterator iter = newArtifactsByPath.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = (Map.Entry)iter.next();
            // Write the stream to a temp file and add the File to the artifacts ...
            File tempFile = null;
            try {
                final IPath path = new Path((String)entry.getKey());
                final Object content = entry.getValue();
                tempFile = this.getTempDirectoryFile(path.makeAbsolute().toString());
                if (content instanceof String) {
                    InputStream stream = null;
                    try {
                        stream = new ByteArrayInputStream(((String)content).getBytes());
                        FileUtils.write(stream, tempFile);
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e2) {
                                final Object[] params = new Object[] {tempFile.getAbsolutePath(), this.vdbFilePath};
                                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.ErrorClosingTemporaryWsdlFile", params); //$NON-NLS-1$
                                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e2));
                            }
                        }
                    }
                } else if (content instanceof Document) {
                    // Write to temp file and add file to artifacts ...
                    final OutputStream fileStream = new FileOutputStream(tempFile);
                    try {
                        JdomHelper.write((Document)content, fileStream);
                    } finally {
                        try {
                            fileStream.close();
                        } catch (IOException err) {
                            final Object[] params = new Object[] {tempFile.getAbsolutePath(), this.vdbFilePath};
                            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.ErrorClosingTemporaryWsdlFile", params); //$NON-NLS-1$
                            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, err));
                        }
                    }
                } else if (content instanceof InputStream) {
                    // Write to temp file and add file to artifacts ...
                    InputStream stream = (InputStream)content;
                    try {
                        FileUtils.write(stream, tempFile);
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException e2) {
                            final Object[] params = new Object[] {tempFile.getAbsolutePath(), this.vdbFilePath};
                            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.ErrorClosingTemporaryWsdlFile", params); //$NON-NLS-1$
                            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e2));
                        }
                    }
                } else if (content instanceof File) {
                    tempFile = (File)content;
                }

                // Record that there is a new artifact (temp file) to be written to the VDB archive file
                if (tempFile != null & tempFile.exists()) {
                    artifactsToWrite.add(new Artifact(path, tempFile, 0));
                }
            } catch (Throwable e) {
                final Object[] params = new Object[] {tempFile.getName(), this.vdbFilePath, e.getMessage()};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_writing_artifact_file_for_vdb", params); //$NON-NLS-1$
                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e));
                hasFatalProblems = true; // catastrophic, problems writing artifact temp files
            }

        }
        return hasFatalProblems;
    }

    protected URI getEscapedURI( final Resource resource ) {
        String modelPath = getResourcePath(resource);
        if (modelPath == null) {
            final URI resourceURI = resource.getURI();
            final Object[] params = new Object[] {resourceURI};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.finding_model_path_for_resource", params); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, msg);
            return resourceURI;
        }
        // Encode any characters within the path segments ...
        modelPath = encodePathForUseInUri(modelPath);
        // if the referenced resource is a schema...update the location by placing tokens
        String updatedLocation = VdbEditPlugin.URL_ROOT_FOR_VDB + modelPath + VdbEditPlugin.URL_SUFFIX_FOR_VDB;
        final URI result = URI.createURI(updatedLocation);
        return result;
    }

    /**
     * Update XSDs by placing tokens in schema locations of the XSD, the tokens may be replaced at runtime by server urls.
     * 
     * @param resource The internal resource that may be an xsd.
     * @since 4.2
     */
    private void updateXsdResource( final Resource eResource ) {
        if (eResource instanceof XSDResourceImpl) {

            boolean saveRequired = false;

            // The resource must be loaded to retrieve its contents
            if (!eResource.isLoaded()) {
                try {
                    eResource.load(this.getOptions());
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(e);
                }
            }

            // Iterate through each directive ...
            XSDSchema schema = ((XSDResourceImpl)eResource).getSchema();
            for (Iterator iter = schema.eContents().iterator(); iter.hasNext();) {
                EObject eObj = (EObject)iter.next();
                if (eObj instanceof XSDSchemaDirective) {
                    XSDSchemaDirective directive = (XSDSchemaDirective)eObj;

                    // Directive is not yet resolved, attempt to locate the reference ...
                    XSDSchema resolvedSchema = directive.getResolvedSchema();
                    XSDResourceImpl refdResource = null;
                    if (resolvedSchema == null) {

                        String schemaLocation = directive.getSchemaLocation();
                        URI baseLocationURI = eResource.getURI();
                        URI schemaLocationURI = (baseLocationURI.isFile() ? URI.createURI(schemaLocation, false) : URI.createURI(schemaLocation));
                        if (baseLocationURI.isHierarchical() && !baseLocationURI.isRelative() && schemaLocationURI.isRelative()) {
                            schemaLocationURI = schemaLocationURI.resolve(baseLocationURI);
                        }
                        refdResource = (XSDResourceImpl)getVdbContainer().getResource(schemaLocationURI, false);
                    } else {
                        if (resolvedSchema.eIsProxy()) {
                            resolvedSchema = (XSDSchema)EcoreUtil.resolve(resolvedSchema, getVdbContainer());
                        }
                        refdResource = (XSDResourceImpl)resolvedSchema.eResource();
                    }
                    if (refdResource != null) {
                        // Check if the referenced XSD is one of the global resources
                        Container cntr = ModelerCore.getContainer(refdResource);
                        if (cntr != null && cntr.getResourceFinder().isBuiltInResource(refdResource)) {
                            continue;
                        }
                        final URI escapedURI = getEscapedURI(refdResource);
                        String updatedLocation = escapedURI.toString();

                        // If the updated location is different than the current schema location
                        // then reset it and flag that the resource should be resaved
                        if (updatedLocation != null && !updatedLocation.equals(directive.getSchemaLocation())) {
                            directive.setSchemaLocation(updatedLocation);
                            saveRequired = true;
                        }
                    } else {
                        String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_resolve_schemaLocation", directive.getSchemaLocation()); //$NON-NLS-1$
                        VdbEditPlugin.Util.log(IStatus.ERROR, msg);
                    }
                }
            }

            // Resave the schema with the updated WSDL location strings. I am not sure saving is
            // necessary since each resource gets rewritten to the temp directory immediately before
            // it is zipped up. A save should only be required if there is a chance the resource
            // could be unloaded prior to the completion of the save.
            if (saveRequired) {

                // If the XSD resource was changed to read-only then we need to delete it
                // prior to the save otherwise the write operation will fail
                File xsdResourceFile = new File(eResource.getURI().toFileString());
                if (xsdResourceFile.exists() && !xsdResourceFile.canWrite()) {
                    xsdResourceFile.delete();
                }
                // Save the XSD resource with the updated WSDL location strings
                try {
                    eResource.save(this.getOptions());
                } catch (Throwable e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR,
                                           e,
                                           VdbEditPlugin.Util.getString("VdbEditingContextImpl.1", getResourcePath(eResource))); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Update XSDs by placing tokens in schema locations of the XSD, the tokens may be replaced at runtime by server urls.
     * 
     * @param resource The internal resource that may be an xsd.
     * @since 4.2
     */
    private void resetXsdResource( final Resource eResource ) {
        if (eResource instanceof XSDResourceImpl) {

            boolean saveRequired = false;

            // The resource must be loaded to retrieve its contents
            if (!eResource.isLoaded()) {
                try {
                    eResource.load(this.getOptions());
                } catch (IOException e) {
                    VdbEditPlugin.Util.log(e);
                }
            }

            // Iterate through each directive ...
            XSDSchema schema = ((XSDResourceImpl)eResource).getSchema();
            for (Iterator iter = schema.eContents().iterator(); iter.hasNext();) {
                EObject eObj = (EObject)iter.next();
                if (eObj instanceof XSDSchemaDirective) {
                    XSDSchemaDirective directive = (XSDSchemaDirective)eObj;

                    String location = directive.getSchemaLocation();
                    int endIndex = location.lastIndexOf(VdbEditPlugin.URL_SUFFIX_FOR_VDB);
                    int beginIndex = VdbEditPlugin.URL_ROOT_FOR_VDB.length();

                    // If the location is of the form "http://vdb.metamatrix.com/...?vdbToken=..." then
                    // we need to find the underlying file location in order to resolve the import
                    if (endIndex > 0 && beginIndex < endIndex) {
                        URI locationURI = URI.createURI(location);
                        XSDResourceImpl refdResource = (XSDResourceImpl)getVdbContainer().getResource(locationURI, false);

                        if (refdResource == null) {
                            IPath modelPath = new Path(location.substring(VdbEditPlugin.URL_ROOT_FOR_VDB.length(), endIndex));
                            locationURI = getInternalResourceUri(modelPath);
                            refdResource = (XSDResourceImpl)getVdbContainer().getResource(locationURI, true);
                            if (refdResource == null) {
                                VdbEditPlugin.Util.log(IStatus.ERROR,
                                                       VdbEditPlugin.Util.getString("VdbEditingContextImpl.cannotLoadResource", locationURI.lastSegment())); //$NON-NLS-1$
                            }
                        }
                        if (refdResource != null) {
                            URI resourceURI = eResource.getURI();
                            URI importURI = refdResource.getURI();
                            if (importURI.isFile()) {
                                boolean deresolve = (resourceURI != null && !resourceURI.isRelative() && resourceURI.isHierarchical());
                                if (deresolve && !importURI.isRelative()) {
                                    URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
                                    if (deresolvedURI.hasRelativePath()) {
                                        importURI = deresolvedURI;
                                    }
                                }
                                directive.setSchemaLocation(importURI.toString());
                                saveRequired = true;
                            }
                        }
                    }
                }
            }

            // Resave the schema with the reset location strings
            if (saveRequired) {
                try {
                    eResource.save(this.getOptions());
                } catch (Throwable e) {
                    VdbEditPlugin.Util.log(IStatus.ERROR,
                                           e,
                                           VdbEditPlugin.Util.getString("VdbEditingContextImpl.1", getResourcePath(eResource))); //$NON-NLS-1$
                }
            }

        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isSaveRequired()
     */
    public boolean isSaveRequired() {
        return (isOpen() && this.manifestResource.isModified());
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isSaveRequiredForValidVdb()
     */
    public boolean isSaveRequiredForValidVdb() {
        if (!isOpen()) {
            return false;
        }
        if (this.manifestResource.isModified()) {
            return true;
        }
        // Count the number of entries ...
        if (this.vdbArchive != null) {
            // There is at least a manifest model and (if a Jar) a Jar manifest file
            final int minNum = this.getVdbArchiveForm() == VdbFileWriter.FORM_JAR ? 2 : 1;
            if (this.vdbArchive.size() <= minNum) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#setModified()
     */
    public void setModified() {
        if (isOpen()) {
            this.manifestResource.setModified(true);
            fireStateChanged();
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getIndexNames()
     */
    public String[] getIndexNames() {
        assertIsOpen();
        return IndexConstants.INDEX_NAME.INDEX_NAMES;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getIndexContent(java.lang.String)
     */
    public String getIndexContent( final String indexName ) {
        assertIsOpen();

        // Look for the specified index in the jar file ...
        if (this.vdbArchive == null) {
            // defect 16579 - change text for initial no content:
            return VdbEditPlugin.Util.getString("VdbEditingContextImpl.No_content_found_for_index", indexName); //$NON-NLS-1$
        }
        ZipEntry entry = null;
        final Enumeration iter = this.vdbArchive.entries();
        while (iter.hasMoreElements()) {
            entry = (ZipEntry)iter.nextElement();
            if (entry.getName().endsWith(indexName)) {
                break;
            }
            entry = null;
        }
        if (entry == null) {
            // defect 16579 - change text for initial no content:
            return VdbEditPlugin.Util.getString("VdbEditingContextImpl.No_content_found_for_index", indexName); //$NON-NLS-1$
        }

        String contents;
        File tmpFile = null;
        IndexInput input = null;
        try {
            // Copy to a temporary location ...
            final String prefix = Long.toString(System.currentTimeMillis());
            tmpFile = this.getTempDirectoryFile(prefix + this.getVdbName() + indexName);

            // Put the contents in the temporary file ...
            final InputStream stream = this.vdbArchive.getInputStream(entry);
            FileUtils.write(stream, tmpFile);
            stream.close();

            // Stringify the contents ...
            input = new BlocksIndexInput(tmpFile);
            input.open();
            StringBuffer buffer = new StringBuffer();
            while (input.hasMoreWords()) {
                buffer.append(input.getCurrentWordEntry().getWord());
                buffer.append(StringUtil.LINE_SEPARATOR);
                input.moveToNextWordEntry();
            }
            contents = buffer.toString();
        } catch (Throwable t) {
            contents = StringUtil.getStackTrace(t);
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
                tmpFile = null;
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }

        if (contents.length() == 0) {
            contents = VdbEditPlugin.Util.getString("VdbEditingContextImpl.No_content_found_for_index", indexName); //$NON-NLS-1$
        }

        // And return the contents ...
        return contents;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVdbWsdlGenerationOptions()
     * @since 4.2
     */
    public VdbWsdlGenerationOptions getVdbWsdlGenerationOptions() {
        return this.wsdlOptions;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getModelReference(java.lang.String)
     * @since 4.3
     */
    public ModelReference getModelReference( String pathInVdb ) {
        return getModelReferenceByPath(new Path(pathInVdb));
    }

    public synchronized void close() throws IOException {
        close(false, true, true);
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#close(boolean,boolean)
     */
    public synchronized void close( final boolean reuseTempDir,
                                    final boolean fireStateChangedEvent,
                                    boolean allowVeto ) throws IOException {
        if (isOpen()) {
            if (!allowVeto || fireVetoableChange(CLOSING, "opened", "closed")) { //$NON-NLS-1$//$NON-NLS-2$
                // no one vetoed, proceed:
                if (DEBUG_ON) {
                    VdbEditPlugin.Util.log("close(): Closing VdbEditingContextImpl - " + this.vdbFilePath); //$NON-NLS-1$
                }
                try {
                    // Unload and remove all the resources in the VDB container ...
                    final List resources = new ArrayList(this.getVdbContainer().getResources());
                    for (final Iterator iter = resources.iterator(); iter.hasNext();) {
                        final Resource eResource = (Resource)iter.next();
                        this.removeFromInternalResourceSet(eResource.getURI());
                    }

                    // Close the VDB archive ...
                    if (this.vdbArchive != null) {
                        this.vdbArchive.close();
                    }

                    // Clean up by removing the temp directory and clearing the maps ...
                    if (!reuseTempDir) {
                        this.getTempDirectory().remove();
                    }
                    if (this.materializedModelMap != null) {
                        this.materializedModelMap.clear();
                    }
                    if (this.modelRefsByPath != null) {
                        this.modelRefsByPath.clear();
                    }
                    if (this.pathsByResourceUri != null) {
                        this.pathsByResourceUri.clear();
                    }
                    if (this.userFiles != null) {
                        this.userFiles.clear();
                    }
                } finally {
                    this.vdbArchive = null;
                    this.virtualDatabase = null;
                    this.manifestResource = null;
                    if (!reuseTempDir) {
                        this.tempDirectory = null;
                    }
                    this.materializationModel = null;
                    this.materializationModelDdlFiles = null;
                    this.userFiles = null;

                    // notify listeners that VDB state has changed
                    if (fireStateChangedEvent) {
                        fireStateChanged();
                    }
                }
            } // endif -- veto
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVirtualDatabase()
     */
    public VirtualDatabase getVirtualDatabase() {
        assertIsOpen();

        if (virtualDatabase == null) {
            final List roots = this.manifestResource.getContents();
            if (roots.isEmpty()) {
                this.virtualDatabase = getManifestFactory().createVirtualDatabase();
                this.manifestResource.getContents().add(this.virtualDatabase);

                // Create the model annotation with the primary metamodel URI reference ...
                ModelAnnotation annotation = CoreFactory.eINSTANCE.createModelAnnotation();
                annotation.setPrimaryMetamodelUri(ManifestPackage.eNS_URI);
                roots.add(annotation);

                setModified();
                updateVirtualDatabase(this.virtualDatabase);
            } else {
                ModelAnnotation modelAnnotation = null;
                final Iterator iter = roots.iterator();
                while (iter.hasNext()) {
                    final Object rootObject = iter.next();
                    if (rootObject instanceof VirtualDatabase) {
                        this.virtualDatabase = (VirtualDatabase)rootObject;
                    }
                    if (rootObject instanceof ModelAnnotation) {
                        modelAnnotation = (ModelAnnotation)rootObject;
                    }
                }
                boolean updated = false;
                if (this.virtualDatabase == null) {
                    this.virtualDatabase = getManifestFactory().createVirtualDatabase();
                    this.manifestResource.getContents().add(this.virtualDatabase);
                    updated = true;
                }
                if (modelAnnotation == null) {
                    // Create the model annotation with the primary metamodel URI reference ...
                    modelAnnotation = CoreFactory.eINSTANCE.createModelAnnotation();
                    modelAnnotation.setPrimaryMetamodelUri(ManifestPackage.eNS_URI);
                    this.manifestResource.getContents().add(0, modelAnnotation);
                    updated = true;
                } else if (modelAnnotation.getPrimaryMetamodelUri() == null) {
                    modelAnnotation.setPrimaryMetamodelUri(ManifestPackage.eNS_URI);
                }
                if (updated) {
                    setModified();
                    updateVirtualDatabase(this.virtualDatabase);
                }
            }
        }
        return this.virtualDatabase;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVdbContainer()
     */
    public Container getVdbContainer() {
        return this.resourceSet;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#refreshNonModel(org.eclipse.core.runtime.IProgressMonitor, java.io.File)
     * @since 4.3
     */
    public NonModelReference refreshNonModel( final IProgressMonitor theMonitor,
                                              final File theNonModel,
                                              final IPath theArchivePath ) throws VdbEditException {
        NonModelReference result = null;

        final IPath path = theArchivePath.makeAbsolute();
        final String fileName = path.lastSegment();
        final NonModelReference existing = getNonModelReferenceByFileName(fileName);

        try {
            // remove if previously added
            if (existing != null) {
                IStatus status = removeNonModel(theArchivePath);

                if (status.getSeverity() == IStatus.ERROR) {
                    throw new VdbEditException(VdbEditPlugin.Util.getString("VdbEditingContextImpl.refreshNonModelRemoveError", //$NON-NLS-1$
                                                                            theNonModel.getAbsolutePath()));
                }
            }

            // register file as a non-model
            result = addNonModel(theMonitor, theNonModel, theArchivePath);
        } catch (Exception theException) {
            VdbEditException e = null;

            if (theException instanceof VdbEditException) {
                e = (VdbEditException)theException;
            } else {
                e = new VdbEditException(e);
            }

            throw e;
        }

        return result;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#addNonModel(org.eclipse.core.runtime.IProgressMonitor, java.io.File,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public NonModelReference addNonModel( final IProgressMonitor monitor,
                                          final File nonModel,
                                          final IPath pathInArchive ) throws VdbEditException {
        ArgCheck.isNotNull(nonModel);
        ArgCheck.isNotNull(pathInArchive);
        assertIsOpen();
        assertModelsLoaded();

        if (monitor != null) {
            String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Adding_model_to_vdb", nonModel.getName()); //$NON-NLS-1$
            monitor.setTaskName(taskName);
            monitor.worked(1);
        }

        // See if there is already a non-model reference with this file name ...
        final IPath path = (pathInArchive.segmentCount() > 1 ? pathInArchive.makeAbsolute() : pathInArchive);
        final String pathString = path.toString();
        final String fileName = path.lastSegment();
        final NonModelReference existing = this.getNonModelReferenceByFileName(fileName);
        if (existing != null) {
            return existing;
        }

        // If the file does not exist on the file system ...
        final String filePath = nonModel.getAbsolutePath();
        if (!nonModel.exists()) {
            final Object[] params = new Object[] {filePath};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.unable_to_locate_nonModel_file", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, null);
            throw new VdbEditException(status);
        }

        // Get input stream for resource
        InputStream istream = null;
        try {
            istream = new FileInputStream(nonModel);
        } catch (IOException theException) {
            final Object[] params = new Object[] {filePath};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_getting_an_inputstream_to_model", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, theException);
            throw new VdbEditException(status);
        }

        // Setup in temporary directory
        try {
            this.addToTempDirectory(istream, pathString);
        } catch (IOException theException) {
            final Object[] params = new Object[] {fileName};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorAddingResourceToTempDirectory", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, theException);
            throw new VdbEditException(status);
        }

        // Create the new object ...
        final NonModelReference nonModelRef = this.getManifestFactory().createNonModelReference();
        nonModelRef.setPath(pathString);
        nonModelRef.setName(path.lastSegment());
        nonModelRef.setChecksum(this.getCheckSum(nonModel));
        nonModelRef.setVirtualDatabase(this.getVirtualDatabase());

        // notify listeners context has changed
        fireStateChanged();

        return nonModelRef;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#addModel(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IPath, boolean)
     */
    public ModelReference[] addModel( final IProgressMonitor monitor,
                                      final IPath pathInWorkspace,
                                      final boolean addDependentModels ) throws VdbEditException {
        VdbEditPlugin.setAutoBind(true);
        final List eResourcesToBeAdded = new ArrayList();
        ModelReference[] result = addModel(monitor, pathInWorkspace, addDependentModels, eResourcesToBeAdded);
        VdbEditPlugin.setAutoBind(false);
        return result;
    }

    /**
     * Add a model from the workspace with the specified path to this virtual database. The method takes a list of resources to be
     * added that is used to track what will be added to the vdb as this method is recursively called for every model import. The
     * list is necessary to ensure that same resource is not processed more than once in the recursive call hierarchy of this
     * method. (defect 18733)
     */
    private ModelReference[] addModel( final IProgressMonitor monitor,
                                       final IPath pathInWorkspace,
                                       final boolean addDependentModels,
                                       final List eResourcesToBeAdded ) throws VdbEditException {
        ArgCheck.isNotNull(pathInWorkspace);
        assertIsOpen();
        assertModelsLoaded();

        if (monitor != null) {
            String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Adding_model_to_vdb", pathInWorkspace); //$NON-NLS-1$
            monitor.setTaskName(taskName);
            monitor.worked(1);
        }

        ModelReference[] result = EMPTY_MODEL_REFERENCE_ARRAY;

        // See if there is already a model reference with that path ...
        final IPath absoluteModelPath = pathInWorkspace.makeAbsolute();
        final ModelReference existing = this.getModelReferenceByPath(absoluteModelPath);
        if (existing != null) {
            return result;
        }

        // See if there is already a model reference with that model name but different location.
        // We cannot allow two XMI models with the same name even if they have different paths because
        // the runtime metadata created for the server does not contain path information so we
        // cannot distinguish which models a table or procedure belongs to. (defect 17751, 19011)
        final ModelReference existingWithSameName = this.getModelReferenceByFileName(absoluteModelPath.lastSegment());
        if (existingWithSameName != null && !ModelUtil.isXsdFile(new Path(absoluteModelPath.toString().toLowerCase()))) {
            final Object[] params = new Object[] {absoluteModelPath, existingWithSameName.getModelLocation()};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.model_has_same_name_as_existing", params); //$NON-NLS-1$
            createProblem(this.getVirtualDatabase(), IStatus.ERROR, msg, null);
            throw new VdbEditException(msg);
        }

        // find the EMF resource to the model specified by the path
        Resource eResource = null;
        Throwable throwable = null;

        try {
            eResource = this.vdbInputResourceFinder.getEmfResource(pathInWorkspace);
            if (!eResourcesToBeAdded.contains(eResource)) {
                eResourcesToBeAdded.add(eResource);
            }
        } catch (Exception theException) {
            throwable = theException;
        } finally {
            String errorMessage = null;
            if ((throwable == null) && (eResource != null)) {
                // check to see if resource has any other errors that should prevent it from being added.
                List errors = eResource.getErrors();

                if ((errors != null) && !errors.isEmpty()) {
                    // take first throwable. should all be throwable but just in case.
                    for (int size = errors.size(), i = 0; i < size; ++i) {
                        if (errors.get(i) instanceof Throwable) {
                            throwable = (Throwable)errors.get(i);
                            break;
                        }
                        if (errors.get(i) instanceof Resource.Diagnostic) {
                            Resource.Diagnostic diagnostic = (Resource.Diagnostic)errors.get(i);
                            errorMessage = diagnostic.getMessage();
                            break;
                        }
                    }
                }
            }

            if (throwable != null) {
                if (errorMessage == null) {
                    errorMessage = throwable.toString();
                }
                final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, errorMessage, throwable);
                throw new VdbEditException(status);
            } else if (errorMessage != null) {
                final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, errorMessage, null);
                throw new VdbEditException(status);
            }
        }

        // Check if we found a resource by the specified path
        if (eResource == null) {
            final Object[] params = new Object[] {absoluteModelPath};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.5", params); //$NON-NLS-1$
            createProblem(this.getVirtualDatabase(), IStatus.ERROR, msg, throwable);
            throw new VdbEditException(msg);
        }

        // Check if the referenced model is one of the global resources
        Container cntr = ModelerCore.getContainer(eResource);
        if (cntr != null && cntr.getResourceFinder().isBuiltInResource(eResource)) {
            final Object[] params = new Object[] {absoluteModelPath};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.6", params); //$NON-NLS-1$
            createProblem(this.getVirtualDatabase(), IStatus.ERROR, msg, null);
            throw new VdbEditException(msg);
        }

        final URI eResourceURI = eResource.getURI();
        if (!eResourceURI.isFile()) {
            final Object[] params = new Object[] {eResourceURI};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.2", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, null);
            throw new VdbEditException(status);
        }

        ArrayList tmp = new ArrayList();

        // Add dependent models by reading the import declarations
        if (addDependentModels) {
            try {

                // Get all resources referenced by the resource being added
                Resource[] importedResources = cntr.getResourceFinder().findReferencesFrom(eResource, false, false);
                for (int i = 0; i != importedResources.length; ++i) {
                    Resource importResource = importedResources[i];

                    if (importResource == null) {
                        final Object[] params = new Object[] {absoluteModelPath};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.9", params); //$NON-NLS-1$
                        createProblem(this.getVirtualDatabase(), IStatus.WARNING, msg, throwable);
                        continue;
                    }

                    // If the dependent resource is already in the list of resources to be added, then skip it
                    if (eResourcesToBeAdded.contains(importResource)) {
                        continue;
                    }
                    eResourcesToBeAdded.add(importResource);

                    // If the imported resource does not yet exist in the editing context's internal
                    // resource set then add it to the vdb ...
                    IPath importPath = this.vdbInputResourceFinder.getEmfResourcePath(importResource);
                    if (this.getModelReferenceByPath(importPath) == null) {

                        ModelReference[] modelRefs = this.addModel(monitor, importPath, addDependentModels, eResourcesToBeAdded);
                        if (modelRefs != null && modelRefs.length > 0) {
                            tmp.addAll(Arrays.asList(modelRefs));
                        }
                    }
                }

            } catch (Exception e) {
                final Object[] params = new Object[] {absoluteModelPath};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_retrieve_model_dependency_information_from_0_2", params); //$NON-NLS-1$
                createProblem(existing, IStatus.ERROR, msg, e);
            }

        }

        // See if there is already a resource with this UUID in the VDB ...
        if (eResource instanceof EmfResource) {
            final ModelReference existingUuid = this.getModelReferenceByUuid(((EmfResource)eResource).getUuid());
            if (existingUuid != null) {
                final int code = 0;
                final String uri = existingUuid.getUri();
                final String path = existingUuid.getModelLocation();
                final Object[] params = uri != null ? new Object[] {absoluteModelPath, uri} : new Object[] {absoluteModelPath,
                    path};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Model_already_exists_with_uuid", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.INFO, VdbEditPlugin.PLUGIN_ID, code, msg, null);
                throw new VdbEditException(status);
            }
        }

        // Get input stream for resource
        InputStream istream = this.vdbInputResourceFinder.getEmfResourceStream(eResource);

        // Setup internal resource
        final URI internalUri = this.getInternalResourceUri(absoluteModelPath);
        Resource internalResource = null;
        try {
            internalResource = this.addToInternalResourceSet(istream, internalUri, absoluteModelPath);
        } catch (Exception theException) {
            // cleanup internal resource set
            removeFromInternalResourceSet(getInternalResourceUri(absoluteModelPath));

            // throw exception
            final Object[] params = new Object[] {absoluteModelPath};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorAddingToInternalResourceSet", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, theException);
            throw new VdbEditException(status);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException theCloseStreamException) {
                }
            }
        }

        // Setup in temporary directory
        try {
            final String modelPath = absoluteModelPath.toString();

            // If the model file being added exists in the file system location as
            // the temp directory being used by the VDB to persist models then we
            // do not need to write that file back out to that temp directory location.
            // The one exception to this is if the file being added is read-only then
            // we need to rewrite the file in order to make it writable.
            File outputTempDirFile = this.getTempDirectoryFile(modelPath);
            File inputResourceFile = new File(eResourceURI.toFileString());
            if (!inputResourceFile.canWrite() || (inputResourceFile.exists() && !inputResourceFile.equals(outputTempDirFile))) {
                this.addToTempDirectory(internalResource, modelPath);
            }

        } catch (IOException theException) {
            // cleanup internal resource set
            removeFromInternalResourceSet(getInternalResourceUri(absoluteModelPath));

            // throw exception
            final Object[] params = new Object[] {absoluteModelPath};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errorAddingResourceToTempDirectory", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, theException);
            throw new VdbEditException(status);
        }

        // go ahead and add model. create problem markers for any further problems.

        // Create the new object ...
        final ModelReference modelReference = createModelReference(eResource, absoluteModelPath);

        // Add to the map ...
        this.setModelReferenceForPath(absoluteModelPath, modelReference);

        // Add the new resource for validation ...
        this.addModelToValidate(internalResource);

        // Add the new model reference to the result
        tmp.add(modelReference);
        result = new ModelReference[tmp.size()];
        tmp.toArray(result);

        // mark as modified (seems like you shouldn't have to do this??)
        setModified();

        return result;
    }

    protected ModelReference createModelReference( final Resource eResource,
                                                   final IPath pathInWorkspace ) {
        ArgCheck.isNotNull(eResource);
        ArgCheck.isNotNull(pathInWorkspace);

        final ModelReference modelReference = this.getManifestFactory().createModelReference();
        modelReference.setModelLocation(pathInWorkspace.toString());
        modelReference.setUri(null);
        modelReference.setVirtualDatabase(this.getVirtualDatabase());
        modelReference.setVisible(true); // may set to false later after interrogating the model
        final Date currentDate = DateUtil.getCurrentDate();
        modelReference.setTimeLastSynchronizedAsDate(currentDate);

        final File resourceFile = new File(eResource.getURI().toFileString());

        String modelName = eResource.getURI().lastSegment();
        ModelType modelType = null;
        String modelUuid = null;
        boolean isVisible = true;
        String primaryMetamodelUri = null;
        Properties modelSourceProps = null;

        try {
            // If the Resource represents a model resource ...
            boolean isXsd = false;
            if (eResource instanceof EmfResource) {
                final EmfResource resrc = (EmfResource)eResource;
                if (resrc.isLoaded()) {
                    modelType = resrc.getModelType();
                    modelUuid = resrc.getUuid().toString();
                    primaryMetamodelUri = resrc.getPrimaryMetamodelUri().toString();
                } else {
                    final XMIHeader header = XMIHeaderReader.readHeader(resourceFile);
                    modelType = ModelType.get(header.getModelType());
                    modelUuid = header.getUUID();
                    isVisible = header.isVisible();
                    primaryMetamodelUri = header.getPrimaryMetamodelURI();
                }

                // Set the model source information on the reference
                modelSourceProps = this.getModelSourceProperties(eResource);
                if (modelSourceProps != null) {
                    final ModelSource modelSource = this.getManifestFactory().createModelSource();
                    modelReference.setModelSource(modelSource);
                    for (Iterator iter = modelSourceProps.entrySet().iterator(); iter.hasNext();) {
                        final Map.Entry entry = (Map.Entry)iter.next();
                        final String name = (String)entry.getKey();
                        final String value = (String)entry.getValue();
                        if (name != null && value != null) {
                            ModelSourceProperty srcProp = this.getManifestFactory().createModelSourceProperty();
                            srcProp.setName(name);
                            srcProp.setValue(value);
                            srcProp.setSource(modelSource);
                        }
                    }
                }

                // If the Resource represents an XSD resource ...
            } else if (eResource instanceof XSDResourceImpl) {
                isXsd = true;
                modelType = ModelType.TYPE_LITERAL;
                primaryMetamodelUri = XSDPackage.eNS_URI;

                // Else no ModelResource was found so the path must represent a
                // non-Teiid Designer model file so store information about the
                // file in the ModelReference properties
            } else {
                // Set the model type information on the reference
                modelReference.setModelType(ModelType.UNKNOWN_LITERAL);
                // Set the model name information on the reference
                modelReference.setName(pathInWorkspace.lastSegment());
            }

            // Set the model name information on the reference
            if (modelName != null) {
                modelReference.setName(modelName);
            } else {
                final Object[] params = new Object[] {pathInWorkspace};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_determine_model_name", params); //$NON-NLS-1$
                createProblem(modelReference, IStatus.WARNING, msg, null);
            }

            // Set the model type information on the reference
            if (modelType != null) {
                modelReference.setModelType(modelType);
            } else {
                final Object[] params = new Object[] {pathInWorkspace};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_determine_model_type", params); //$NON-NLS-1$
                createProblem(modelReference, IStatus.WARNING, msg, null);
            }

            // Set the primary metamodel information on the reference
            if (primaryMetamodelUri != null) {
                modelReference.setPrimaryMetamodelUri(primaryMetamodelUri);
            } else {
                final Object[] params = new Object[] {pathInWorkspace};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_determine_primary_metamodel", params); //$NON-NLS-1$
                createProblem(modelReference, IStatus.WARNING, msg, null);
            }

            // Set the model uuid information on the reference
            if (modelUuid != null) {
                modelReference.setUuid(modelUuid);
            } else if (!isXsd) {
                final Object[] params = new Object[] {pathInWorkspace};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_determine_model_UUID", params); //$NON-NLS-1$
                createProblem(modelReference, IStatus.WARNING, msg, null);
            }

            // Set the visibility and checksum
            modelReference.setVisible(isVisible);

            // Get checksum from file to later set on ModelReference
            final File modelFile = new File(resourceFile.getAbsolutePath());
            if (modelFile.exists()) {
                final long checkSum = this.getCheckSum(modelFile);
                modelReference.setChecksum(checkSum);
            }
        } catch (Exception e) {
            // cleanup internal resource set
            removeFromInternalResourceSet(getInternalResourceUri(pathInWorkspace));

            try {
                // Try to remove the model reference that failed ...
                modelReference.setVirtualDatabase(null);
            } catch (Exception e2) {
                VdbEditPlugin.Util.log(e2);
            }

            // create problem
            final Object[] params = new Object[] {pathInWorkspace};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.4", params); //$NON-NLS-1$
            createProblem(modelReference, IStatus.WARNING, msg, e);

            // set as not modified. creating a problem markers causes the resource to be dirty
            this.manifestResource.setModified(false);
        }

        return modelReference;

    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#refreshModel(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public ModelReference refreshModel( final IProgressMonitor monitor,
                                        final IPath pathInWorkspace ) throws VdbEditException {
        ArgCheck.isNotNull(pathInWorkspace);
        assertIsOpen();
        assertModelsLoaded();

        if (monitor != null) {
            String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Refreshing_model_in_vdb", pathInWorkspace); //$NON-NLS-1$
            monitor.setTaskName(taskName);
            monitor.worked(1);
        }

        // See if there is already a model reference with that path ...
        final ModelReference existing = this.getModelReferenceByPath(pathInWorkspace);

        // No model reference exists for this path so treat as new model ...
        if (existing == null) {
            ModelReference[] result = addModel(monitor, pathInWorkspace, false);
            return (result.length > 0 ? result[0] : null);
        }

        // find the EMF resource to the model specified by the path
        Resource eResource = null;
        try {
            eResource = this.vdbInputResourceFinder.getEmfResource(pathInWorkspace);
        } catch (Exception theException) {
            throw new VdbEditException(theException);
        }

        // Check if we found a resource by the specified path
        if (eResource == null) {
            final Object[] params = new Object[] {pathInWorkspace};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.5", params); //$NON-NLS-1$
            createProblem(this.getVirtualDatabase(), IStatus.ERROR, msg, null);
            throw new VdbEditException(msg);
        }

        final URI eResourceURI = eResource.getURI();
        if (!eResourceURI.isFile()) {
            final Object[] params = new Object[] {eResourceURI};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.2", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, IStatus.OK, msg, null);
            throw new VdbEditException(status);
        }

        // If the pathInWorkspace points to the resource within the temp directory then do not delete the local file
        final URI existingUri = this.getInternalResourceUri(pathInWorkspace);
        boolean deleteLocal = (eResourceURI.equals(existingUri) ? false : true);

        // Remove and re-add the model
        removeModel(pathInWorkspace, deleteLocal);
        ModelReference[] result = addModel(monitor, pathInWorkspace, false);
        return (result.length > 0 ? result[0] : null);
    }

    /**
     * @param i
     * @param e1
     */
    public void createProblem( final ProblemMarkerContainer marked,
                               final int severity,
                               final String msg,
                               final Throwable t ) {
        final ProblemMarker marker = this.getManifestFactory().createProblemMarker();
        switch (severity) {
            case IStatus.ERROR:
                marker.setSeverity(Severity.ERROR_LITERAL);
                break;
            case IStatus.WARNING:
                marker.setSeverity(Severity.WARNING_LITERAL);
                break;
            case IStatus.INFO:
                marker.setSeverity(Severity.INFO_LITERAL);
                break;
            case IStatus.OK:
                marker.setSeverity(Severity.OK_LITERAL);
                break;
        }
        if (marked instanceof VirtualDatabase && ((VirtualDatabase)marked).getName() != null) {
            marker.setTarget(((VirtualDatabase)marked).getName());
        } else if (marked instanceof ModelReference && ((ModelReference)marked).getModelLocation() != null) {
            marker.setTarget(((ModelReference)marked).getModelLocation());
        }
        marker.setMessage(msg);
        if (t != null) {
            final String trace = StringUtil.getStackTrace(t);
            marker.setStackTrace(trace);
        }
        marker.setMarked(marked);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#removeModel(org.eclipse.core.runtime.IPath)
     */
    public IStatus removeModel( final IPath pathInWorkspace ) {
        return removeModel(pathInWorkspace, true);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#removeModel(org.eclipse.core.runtime.IPath, boolean)
     */
    public IStatus removeModel( final IPath pathInWorkspace,
                                boolean deleteLocal ) {
        assertIsOpen();
        assertModelsLoaded();

        // See if there is already a model reference with that path ...
        final ModelReference existing = getModelReferenceByPath(pathInWorkspace);
        if (existing == null) {
            final int code = 0;
            final Object[] params = new Object[] {pathInWorkspace};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Model_with_path_doesnt_exist", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, code, msg, null);
            return status;
        }
        final VirtualDatabase vdb = getVirtualDatabase();
        final boolean removed = vdb.getModels().remove(existing);
        if (!removed) {
            final int code = 0;
            final Object[] params = new Object[] {pathInWorkspace};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Model_with_path_couldnt_be_removed", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, code, msg, null);
            return status;
        }
        removeModelReferenceForPath(pathInWorkspace);

        // Remove any reference to this model reference from the set of resources to be validated ...
        final URI uri = getInternalResourceUri(pathInWorkspace);
        final Resource eResource = getVdbContainer().getResource(uri, false);
        if (eResource != null) {
            removeModelFromValidate(eResource);
        }

        // Remove resource from the editing context's internal resource set ...
        removeFromInternalResourceSet(existing);

        // Remove resource from the temporary directory location ...
        if (deleteLocal) {
            final File tempDirFile = getTempDirectoryFile(existing);
            tempDirFile.delete();
        }

        // mark as modified (seems like you shouldn't have to do this??)
        setModified();

        // notify listeners:
        fireStateChanged();

        final int code = 0;
        final Object[] params = new Object[] {pathInWorkspace};
        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Removed_model", params); //$NON-NLS-1$
        return new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, code, msg, null);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#removeNonModel(org.eclipse.core.runtime.IPath)
     * @since 4.3
     */
    public IStatus removeNonModel( final IPath pathInArchive ) {
        ArgCheck.isNotNull(pathInArchive);
        assertIsOpen();
        assertModelsLoaded();

        // See if there is already a non-model reference with this file name ...
        final IPath path = pathInArchive.makeAbsolute();
        final String fileName = path.lastSegment();
        final NonModelReference existing = this.getNonModelReferenceByFileName(fileName);
        if (existing == null) {
            final int code = 0;
            final Object[] params = new Object[] {pathInArchive};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.no_nonModel_file_with_path_found", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, code, msg, null);
            return status;
        }

        final VirtualDatabase vdb = this.getVirtualDatabase();
        final boolean removed = vdb.getNonModels().remove(existing);
        if (!removed) {
            final int code = 0;
            final Object[] params = new Object[] {pathInArchive};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.nonModel_with_path_not_removed", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, code, msg, null);
            return status;
        }

        // Remove resource from the temporary directory location ...
        final File tempDirFile = this.getTempDirectoryFile(existing);
        tempDirFile.delete();

        final int code = 0;
        final Object[] params = new Object[] {pathInArchive};
        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.removed_nonModel_with_path", params); //$NON-NLS-1$
        final IStatus status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, code, msg, null);

        // notify listeners context has changed
        fireStateChanged();

        return status;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isVisible(ModelReference model)
     */
    public boolean isVisible( final ModelReference model ) {
        ArgCheck.isNotNull(model);
        return model.isVisible();
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isVisible(java.lang.String)
     * @since 4.2
     */
    public boolean isVisible( final String pathInVdb ) {
        ArgCheck.isNotNull(pathInVdb);
        // vdb needs to be open
        assertIsOpen();
        // get IPath
        IPath path = new Path(pathInVdb);
        // find model reference
        ModelReference model = getModelReferenceByPath(path);
        // found the model
        if (model != null) {
            return isVisible(model);
        }
        // The path may have been manupulated by adding a starting delimiter "/"
        // if it does not exist, since the webservices servlet expects such a delimiter
        // removing the first charchter and cheking if the entry exists in vdb
        String newPath = pathInVdb.substring(1);
        model = getModelReferenceByPath(new Path(newPath));
        if (model != null) {
            return isVisible(model);
        }
        // if not a model, find the file in the vdb
        if (this.vdbArchive != null) {
            ZipEntry vdbEntry = this.vdbArchive.getEntry(pathInVdb);
            if (vdbEntry == null) {
                vdbEntry = this.vdbArchive.getEntry(newPath);
                if (vdbEntry == null) {
                    return false;
                }
            }

            String fileName = path.lastSegment();
            if (fileName != null) {
                // index files should not be visible
                if (IndexUtil.isIndexFile(fileName)) {
                    return false;
                }
                // manifest file should not be visible
                if (fileName.equalsIgnoreCase(MANIFEST_MODEL_NAME)) {
                    return false;
                }
                // materialization models should not be visible
                if (StringUtil.startsWithIgnoreCase(fileName, MATERIALIZATION_MODEL_NAME)
                    && StringUtil.endsWithIgnoreCase(fileName, MATERIALIZATION_MODEL_FILE_SUFFIX)) {
                    return false;
                }
                // ddl files for materialization should not be visible
                if (isInternalDDLFile(fileName)) {
                    return false;
                }
                // wldl file should be visible
                if (fileName.equalsIgnoreCase(GENERATED_WSDL_FILENAME)) {
                    return true;
                }
                // any other file should be visible
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getPathToVdb()
     * @since 4.3
     */
    public IPath getPathToVdb() {
        return this.vdbFilePath;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#isReadOnly()
     * @since 4.3
     */
    public boolean isReadOnly() {
        return !this.vdbFilePath.toFile().canWrite();
    }

    /**
     * Check if this is an internal DDL file, internal DDl files are generated for matrialization feature.
     * 
     * @param pathInVdb The path to the file in the vdb
     * @return True if the file is an internal ddl file
     * @since 4.2
     */
    public boolean isInternalDDL( final String pathInVdb ) {
        ArgCheck.isNotNull(pathInVdb);
        // vdb needs to be open
        assertIsOpen();
        // if not a model, find the file in the vdb
        if (this.vdbArchive != null && this.vdbArchive.getEntry(pathInVdb) != null) {
            // get IPath
            IPath path = new Path(pathInVdb);
            String fileName = path.lastSegment();
            return isInternalDDLFile(fileName);
        }
        return false;
    }

    /**
     * Check if this is an internal DDL file, internal DDl files are generated for matrialization feature.
     * 
     * @param pathInVdb The path to the file in the vdb
     * @return True if the file is an internal ddl file
     * @since 4.2
     */
    private boolean isInternalDDLFile( final String fileName ) {
        ArgCheck.isNotNull(fileName);
        // ddl suffix
        if (ScriptType.isDDLScript(fileName)) {
            // swap, truncate, load, materialization model contained in file name
            if ((ScriptType.isMaterializationScript(fileName)) || (ScriptType.isCreateScript(fileName))
                || (ScriptType.isSwapScript(fileName)) || (ScriptType.isTruncateScript(fileName))
                || (ScriptType.isLoadScript(fileName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#hasWsdl()
     * @since 4.2
     */
    public boolean hasWsdl() {
        // vdb needs to be open
        assertIsOpen();
        // if not a model, find the file in the vdb
        if (this.vdbArchive != null && this.vdbArchive.getEntry(GENERATED_WSDL_FILENAME) != null) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getVdbDefinitionFile()
     * @since 4.3
     */
    public File getVdbDefinitionFile() {
        // vdb needs to be open
        assertIsOpen();
        String[] paths = getResourcePaths();

        if (paths != null) {
            TempDirectory directory = this.getTempDirectory();
            File tmpDir = new File(directory.getPath());

            for (int i = 0; i < paths.length; i++) {
                String defPath = paths[i];
                // find the DEF file in the archive
                if (defPath.toUpperCase().equalsIgnoreCase(VdbConstants.DEF_FILE_NAME)) {
                    File defFile = new File(tmpDir.getAbsolutePath() + File.separator + defPath);

                    // If the .DEF file already exists in the temp directory return the reference
                    if (defFile.exists()) {
                        return defFile;
                    }
                    // Write .DEF file to the temp directory and return the reference
                    InputStream istream = null;
                    try {
                        istream = this.getArchiveResourceStream(defPath);
                        return this.addToTempDirectory(istream, defPath);
                    } catch (Exception e) {
                        VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
                    } finally {
                        // close input stream if necessary
                        if (istream != null) {
                            try {
                                istream.close();
                            } catch (IOException e) {
                                VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getVdbContentsFolder()
     * @since 4.3
     */
    public File getVdbContentsFolder() {
        // vdb needs to be open
        assertIsOpen();

        TempDirectory tempDir = getTempDirectory();
        return new File(tempDir.getPath());
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#isStale()
     * @since 4.3
     */
    public boolean isStale() {
        // vdb needs to be open
        assertIsOpen();

        for (final Iterator iter = this.getVirtualDatabase().getModels().iterator(); iter.hasNext();) {
            ModelReference model = (ModelReference)iter.next();
            if (isStale(model)) {
                return true;
            }
        }

        return false;
    }

    public boolean isStale( final ModelReference modelRef ) {
        ArgCheck.isNotNull(modelRef);
        // vdb needs to be open
        assertIsOpen();

        // If the ModelReference is PRIVATE then there will be no workspace model to check against
        if (modelRef.getAccessibility() == ModelAccessibility.PRIVATE_LITERAL) {
            return false;
        }

        InputStream istream = null;
        try {
            // Locate the corresponding model in the workspace
            final Resource eResource = this.vdbInputResourceFinder.getEmfResource(modelRef);
            if (eResource == null) {
                return false;
            }
            final IPath pathInWorkspace = this.vdbInputResourceFinder.getEmfResourcePath(eResource);
            if (pathInWorkspace == null) {
                return false;
            }

            // If the resource path, from the source, is different than the ModelReference
            // path then resource must have been moved so we consider this as stale
            final String modelReferencePath = modelRef.getModelLocation();
            final String modelResourcePath = pathInWorkspace.makeAbsolute().toString();
            if (!modelReferencePath.equals(modelResourcePath)) {
                return true;
            }

            // Compute the CheckSum for the corresponding resource
            long wsModCrc = 0;
            istream = this.vdbInputResourceFinder.getEmfResourceStream(eResource);
            if (istream != null) {
                wsModCrc = ChecksumUtil.computeChecksum(istream).getValue();
            } else {
                final Object[] params = new Object[] {modelRef.getModelLocation()};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_getting_an_inputstream_to_model", params); //$NON-NLS-1$
                VdbEditPlugin.Util.log(IStatus.ERROR, msg);
            }

            // Get the CheckSum for the VDB model (stored on the ModelReference)
            final long vdbModCrc = modelRef.getChecksum();

            return vdbModCrc != wsModCrc;
        } catch (Exception err) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.unexpectedException", modelRef.getName()); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, err, msg);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException err1) {
                }
            }
        }

        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#setVisible(ModelReference model, boolean isVisible)
     */
    public void setVisible( final ModelReference model,
                            final boolean isVisible ) {
        ArgCheck.isNotNull(model);
        model.setVisible(isVisible);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#setPerformServerValidation(boolean)
     * @since 4.2
     */
    public void setPerformServerValidation( final boolean serverValidation ) {
        this.performServerValidation = serverValidation;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getInternalResource(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Resource getInternalResource( final ModelReference modelRef ) {
        ArgCheck.isNotNull(modelRef);
        return findInternalResource(modelRef.getModelLocation());
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbEditingContext#getModelReference(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public ModelReference getModelReference( final Resource internalResource ) {
        ArgCheck.isNotNull(internalResource);
        final String modelPath = (String)this.pathsByResourceUri.get(internalResource.getURI());
        if (modelPath != null) {
            return getModelReference(modelPath);
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getResource(java.lang.String)
     * @since 4.2
     */
    public InputStream getResource( final String pathInVdb ) {
        ArgCheck.isNotNull(pathInVdb);
        if (this.vdbArchive != null) {
            try {
                for (Enumeration e = this.vdbArchive.entries(); e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry)e.nextElement();
                    if (entry != null && entry.getName().equals(pathInVdb)) {
                        return this.vdbArchive.getInputStream(entry);
                    }
                }
            } catch (IOException e) {
                VdbEditPlugin.Util.log(IStatus.ERROR,
                                       e,
                                       VdbEditPlugin.Util.getString("VdbEditingContextImpl.0", pathInVdb, this.vdbArchive.getName())); //$NON-NLS-1$
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbEditingContext#getResourcePaths()
     * @since 4.2
     */
    public String[] getResourcePaths() {
        if (this.vdbArchive != null) {
            Collection resourcePaths = new HashSet();
            for (Enumeration e = this.vdbArchive.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry)e.nextElement();
                if (entry != null) {
                    String path = entry.getName();
                    resourcePaths.add(path);
                }
            }
            return (String[])resourcePaths.toArray(new String[0]);
        }
        return new String[0];
    }

    protected String getResourcePath( final Resource resource ) {
        // The URI for each internal resource was creating using ModelReference.getModelLocation();
        String modelPath = (String)this.pathsByResourceUri.get(resource.getURI());

        // the resourceURI may have been updated dues to updating the schema imports with tokens
        if (modelPath == null) {
            String uriString = resource.getURI().toString();
            int endIndex = uriString.lastIndexOf(VdbEditPlugin.URL_SUFFIX_FOR_VDB);
            int beginIndex = VdbEditPlugin.URL_ROOT_FOR_VDB.length();
            if (endIndex > 0 && beginIndex < endIndex) {
                modelPath = uriString.substring(VdbEditPlugin.URL_ROOT_FOR_VDB.length(), endIndex);
            }
        }

        return modelPath;
    }

    protected ModelReference getModelReferenceByUuid( final ObjectID uuid ) {
        if (uuid != null) {
            final String uuidString = uuid.toString();
            for (final Iterator iter = this.getVirtualDatabase().getModels().iterator(); iter.hasNext();) {
                final ModelReference modelRef = (ModelReference)iter.next();
                if (modelRef != null && uuidString.equals(modelRef.getUuid())) {
                    return modelRef;
                }
            }
        }
        return null;
    }

    protected Map getModelReferencesByPath() {
        if (this.modelRefsByPath == null) {
            this.modelRefsByPath = new HashMap();
        } else {
            this.modelRefsByPath.clear();
        }
        // always update the map with modelReferences from the virtualDatabase
        // this is to avoid virtualDatabase getting out of synch with the map
        // when the VDB gets saved(virtual database gets updated with new ModelReferences)
        final Iterator iter = this.getVirtualDatabase().getModels().iterator();
        while (iter.hasNext()) {
            final ModelReference modelRef = (ModelReference)iter.next();
            final IPath path = new Path(modelRef.getModelLocation());
            final String key = path.makeAbsolute().toString().toUpperCase();
            this.modelRefsByPath.put(key, modelRef);
        }
        return this.modelRefsByPath;
    }

    public ModelReference getModelReferenceByPath( final IPath path ) {
        return getModelReferenceByPath(path, getModelReferencesByPath());
    }

    protected ModelReference getModelReferenceByPath( final IPath path,
                                                      Map modelRefPaths ) {
        final Object key = path.makeAbsolute().toString().toUpperCase();
        return (ModelReference)modelRefPaths.get(key);
    }

    protected ModelReference getModelReferenceByFileName( final String modelFileName ) {
        final Iterator iter = this.getVirtualDatabase().getModels().iterator();
        while (iter.hasNext()) {
            final ModelReference modelRef = (ModelReference)iter.next();
            if (modelFileName != null && modelRef != null && modelFileName.equalsIgnoreCase(modelRef.getName())) {
                return modelRef;
            }
        }
        return null;
    }

    protected NonModelReference getNonModelReferenceByFileName( final String nonModelFileName ) {
        final Iterator iter = this.getVirtualDatabase().getNonModels().iterator();
        while (iter.hasNext()) {
            final NonModelReference nonModelRef = (NonModelReference)iter.next();
            if (nonModelFileName != null && nonModelRef != null && nonModelFileName.equalsIgnoreCase(nonModelRef.getName())) {
                return nonModelRef;
            }
        }
        return null;
    }

    protected ModelReference getModelReferenceByPartialPath( final IPath path ) {

        // Return the ModelReference that fully matches the specified path
        ModelReference modelRef = this.getModelReferenceByPath(path, getModelReferencesByPath());
        if (modelRef != null) {
            return modelRef;
        }

        // Return the ModelReference that partially matches the specified path
        final String pathToMatch = path.toString().toUpperCase();
        if (pathToMatch != null && pathToMatch.length() != 0) {
            for (final Iterator iter = this.getVirtualDatabase().getModels().iterator(); iter.hasNext();) {
                modelRef = (ModelReference)iter.next();
                final String modelRefPath = modelRef.getModelLocation().toUpperCase();
                if (pathToMatch.indexOf(modelRefPath) != -1) {
                    return modelRef;
                }
            }
        }

        return null;
    }

    protected void setModelReferenceForPath( final IPath path,
                                             final ModelReference modelRef ) {
        final Object key = path.makeAbsolute().toString().toUpperCase();
        this.getModelReferencesByPath().put(key, modelRef);
    }

    protected Object removeModelReferenceForPath( final IPath path ) {
        final Object key = path.makeAbsolute().toString().toUpperCase();
        return this.getModelReferencesByPath().remove(key);
    }

    protected IStatus merge( final IStatus status1,
                             final IStatus status2,
                             final String desc ) {
        if (status2 == null) {
            return status1;
        }
        if (status2 instanceof Status && status2.isOK()) {
            return status1;
        }
        // Otherwise merge status2 into status 1 ...
        final List statuses = new LinkedList();
        for (int i = 0; i != 2; ++i) {
            final IStatus xstatus = (i == 0 ? status2 : status1);
            // Add the model status information ...
            if (xstatus instanceof MultiStatus) {
                final IStatus[] xStatuses = ((MultiStatus)xstatus).getChildren();
                for (int j = 0; j < xStatuses.length; ++j) {
                    final IStatus status = xStatuses[j];
                    statuses.add(status);
                }
            } else {
                statuses.add(status2);
            }
        }
        final IStatus results = createSingleIStatus(statuses, desc);
        return results;
    }

    protected IStatus produce( final IStatus modelStatus,
                               final List indexFiles,
                               final List indexFilenames,
                               final Collection eResources,
                               final Container container,
                               final IProgressMonitor monitor ) {
        assertIsOpen();
        assertModelsLoaded();

        final List problems = new LinkedList();
        final List resourcesToIndex = new ArrayList(eResources);

        // Add the model status information ...
        if (modelStatus != null && modelStatus instanceof MultiStatus) {
            final IStatus[] modelStatuses = ((MultiStatus)modelStatus).getChildren();
            for (int i = 0; i < modelStatuses.length; ++i) {
                final IStatus status = modelStatuses[i];
                problems.add(status);
            }
        }

        // Create the different index files ...
        final String[] indexNames = getIndexNames();
        final File[] indexFilesArray = new File[indexNames.length];
        boolean produceIndexes = true;
        try {
            for (int i = 0; i < indexNames.length; ++i) {
                final String indexName = indexNames[i];
                // final File indexFile = this.getTempDirectoryFile(prefix+this.getVdbName()+indexName);
                final File indexFile = this.getTempDirectoryFile(indexName);
                indexFiles.add(indexFile);
                indexFilesArray[i] = indexFile;
                indexFilenames.add(indexName);
            }
        } catch (Throwable e) {
            final Object[] params = new Object[] {this.vdbFilePath};
            final int code = 0;
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_while_generating_index_files", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, code, msg, e);
            problems.add(status);
            produceIndexes = false;
        }

        if (produceIndexes) {

            // Produce the indexes ...
            try {
                final String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Generating_index", this.vdbFilePath); //$NON-NLS-1$
                monitor.setTaskName(taskName);

                // Add in any global resources to be indexed ...
                this.addGlobalResourcesForIndexing(resourcesToIndex);

                // Create the indexer to index runtime metadata
                final IndexingContext context = this.getContextForIndexer(eResources, container);
                final IIndexer runtimeIndexer = (context != null ? new VdbIndexer(context) : new VdbIndexer());
                for (int i = 0; i < indexFilesArray.length; ++i) {
                    final File indexFile = indexFilesArray[i];
                    monitor.setTaskName(VdbEditPlugin.Util.getString("VdbEditingContextImpl.Indexing_Resources_1")); //$NON-NLS-1$
                    monitor.subTask(null);
                    produceIndex(runtimeIndexer, resourcesToIndex, indexFile);
                }
                context.clearState();
            } catch (Throwable e) {
                final int code = 0;
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_while_generating_index", this.vdbFilePath); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, code, msg, e);
                problems.add(status);
            } finally {
                final int numModels = this.getVirtualDatabase().getModels().size();
                monitor.worked(numModels * AMOUNT_OF_WORK_FOR_GENERATING_INDEXES_PER_FILE);
            }
        }

        // Set the producer information ...
        this.getVirtualDatabase().setProducerName(PRODUCER_NAME);
        this.getVirtualDatabase().setProducerVersion(PRODUCER_VERSION);

        final String desc = VdbEditPlugin.Util.getString("VdbEditingContextImpl.production_of_indexes"); //$NON-NLS-1$
        final IStatus results = createSingleIStatus(problems, desc);
        return results;
    }

    /**
     * Apply VDB specific validation checks, create problems on ModelReferences as needed
     * 
     * @return The status of validation.
     */
    private IStatus validateVdb( final Collection eResources,
                                 final boolean partialValidation ) {

        VdbEditingContextImplValidationHelper vdbEditingContextImplValidationHelper = new VdbEditingContextImplValidationHelper(
                                                                                                                                this,
                                                                                                                                eResources,
                                                                                                                                partialValidation);

        IStatus status = vdbEditingContextImplValidationHelper.getVdbStatus();
        vdbEditingContextImplValidationHelper.markProblems();

        return status;
    }

    /**
     * Return a collection of strings representing external resource reference paths that could not be resolved using the editing
     * context's internal resource set.
     * 
     * @param eResource
     * @return @since 4.2
     */
    protected Collection getUnresolvedExternalReferencePaths( final Resource eResource ) {
        final Collection result = new ArrayList();

        // Collect all the external references ...
        final ExternalReferenceVisitor visitor = new ExternalReferenceVisitor(eResource);
        visitor.setIncludeDiagramReferences(false);
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            processor.walk(eResource, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable t) {
            final Object[] params = new Object[] {eResource.getURI()};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_processing_external_references_for_resource", params); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, t, msg);
        }

        // Create a collection of external resource paths ...
        Collection externalReferences = visitor.getExternalReferences();
        if (externalReferences == null) {
            externalReferences = Collections.EMPTY_LIST;
        }
        // Iterate over all the external references to check if they exist in the editing context's internal resource set
        for (Iterator iter = externalReferences.iterator(); iter.hasNext();) {
            final ExternalReferenceVisitor.ExternalReferences externalRefs = (ExternalReferenceVisitor.ExternalReferences)iter.next();

            Resource theResource = externalRefs.getResource();
            URI resourceUri = externalRefs.getResourceUri();
            if (theResource != null) {
                resourceUri = theResource.getURI();
            }

            // Check if the referenced model is one of the global resources
            Container cntr = ModelerCore.getContainer(eResource);
            if (cntr != null && cntr.getResourceFinder().isBuiltInResource(resourceUri)) {
                continue;
            }

            if (this.getVdbContainer().getResource(resourceUri, false) == null) {
                String resourcePath = URI.decode(resourceUri.toString());
                final String tempDirPath = new Path(this.getTempDirectory().getPath()).toString();
                int index = resourcePath.indexOf(tempDirPath);
                if (index != -1) {
                    resourcePath = resourcePath.substring(index + tempDirPath.length());
                }
                result.add(resourcePath);
            }

        }
        return result;

    }

    /**
     * Add {@link Resource}references to the specified list for any of the well-known Teiid Designer/Emf resources that are
     * required for index file production such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * 
     * @param eResources
     */
    private IStatus addGlobalResourcesForIndexing( final Collection eResources ) {
        assertIsOpen();
        assertModelsLoaded();

        final List problems = new LinkedList();

        // Add a reference to the built-in datatypes resource
        URI builtInDatatypesURI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
        Resource builtInDatatypes = getVdbContainer().getResource(builtInDatatypesURI, false);
        if (builtInDatatypes != null && !eResources.contains(builtInDatatypes)) {
            eResources.add(builtInDatatypes);
        }

        final String desc = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Adding_in_global_resources_into_the_list_of_resources_for_indexing_2"); //$NON-NLS-1$
        final IStatus results = createSingleIStatus(problems, desc);
        return results;
    }

    // dffFIXME - finish implementation
    /**
     * Add {@link Resource}references to the specified list for any of the well-known Teiid Designer/Emf resources that are
     * required for index file production such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * 
     * @param eResources
     */
    private void addGlobalResourcesToInternalResourceSet() {
        assertIsOpen();
        assertModelsLoaded();

        final List eResources = new ArrayList(this.getVdbContainer().getResources());

        try {
            // Create a list of any XSDSchemaDirectives and ModelImports that reference the
            // built-in datatypes resource identified by
            // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
            final List xsdDirectives = new ArrayList();
            final List modelImports = new ArrayList();
            for (Iterator i = eResources.iterator(); i.hasNext();) {
                final Resource eResource = (Resource)i.next();

                if (!eResource.isLoaded()) {
                    eResource.load(getOptions());
                }

                if (eResource instanceof XSDResourceImpl) {
                    XSDResourceImpl xsdResource = (XSDResourceImpl)eResource;

                    for (final Iterator j = xsdResource.getSchema().eContents().iterator(); j.hasNext();) {
                        EObject eObj = (EObject)j.next();
                        if (eObj instanceof XSDSchemaDirective) {
                            XSDSchemaDirective directive = (XSDSchemaDirective)eObj;
                            if (directive.getSchemaLocation().equalsIgnoreCase(DatatypeConstants.BUILTIN_DATATYPES_URI)) {
                                xsdDirectives.add(directive);
                            }
                        }
                    }
                } else if (eResource instanceof EmfResource) {
                    EmfResource emfResource = (EmfResource)eResource;

                    for (final Iterator j = emfResource.getModelAnnotation().getModelImports().iterator(); j.hasNext();) {
                        ModelImport modelImport = (ModelImport)j.next();
                        if (modelImport.getModelLocation().equalsIgnoreCase(DatatypeConstants.BUILTIN_DATATYPES_URI)) {
                            modelImports.add(modelImport);
                        }
                    }
                }
            }

            // If we found XSDSchemaDirectives or ModelImports referencing the "built-in datatypes"
            // resource then add this resource to the VDB's internal resource set to allow the directives
            // and imports to be resolvable
            if (!xsdDirectives.isEmpty() || !modelImports.isEmpty()) {
                URI builtInDatatypesURI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
                Resource builtInDatatypes = getVdbContainer().getResource(builtInDatatypesURI, false);

                String builtInDatatypesName = DatatypeConstants.DATATYPES_MODEL_FILE_NAME;
                IPath builtInDatatypesPath = new Path(builtInDatatypesName);
                URI internalResourceUri = this.getInternalResourceUri(builtInDatatypesPath);

                // Add a reference to the built-in datatypes resource
                if (builtInDatatypes != null && !getVdbContainer().getResources().contains(builtInDatatypes)) {

                    // Add the built-in datatypes resource to the temporary directory folder
                    File tempDirFile = null;
                    InputStream istream = null;
                    try {
                        tempDirFile = this.addToTempDirectory(builtInDatatypes, builtInDatatypesName);
                    } catch (Exception theException) {
                        VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
                    }

                    // Add the built-in datatypes resource to the internal resource set
                    try {
                        if (tempDirFile != null && tempDirFile.exists()) {
                            istream = new FileInputStream(tempDirFile);
                            Resource r = this.addToInternalResourceSet(istream, internalResourceUri, builtInDatatypesPath);

                            // Create the new object ...
                            final ModelReference modelReference = createModelReference(r, builtInDatatypesPath);
                            modelReference.setAccessibility(ModelAccessibility.PRIVATE_LITERAL);

                            // Add to the map ...
                            this.setModelReferenceForPath(builtInDatatypesPath, modelReference);
                        }
                    } catch (Exception theException) {
                        VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
                    } finally {
                        // close input stream if necessary
                        if (istream != null) {
                            try {
                                istream.close();
                            } catch (IOException theException) {
                                VdbEditPlugin.Util.log(IStatus.ERROR, theException, theException.getMessage());
                            }
                        }
                    }

                }

                // Update any XSDSchemaDirective schemaLocations to reference the
                // "builtInDataTypes.xsd" resource just added to the VDB.
                for (Iterator i = xsdDirectives.iterator(); i.hasNext();) {
                    final XSDSchemaDirective directive = (XSDSchemaDirective)i.next();
                    final Resource eResource = directive.eResource();
                    URI deresolvedURI = ModelUtil.getRelativeLocation(eResource.getURI(), internalResourceUri);
                    directive.setSchemaLocation(deresolvedURI.toString());

                    // I am not sure if saving the resource is necessary since each resource
                    // gets rewritten to the temp directory immediately before it is zipped up.
                    // A save should only be required if there is a chance the resource could
                    // be unloaded prior to the completion of the save.
                    try {
                        eResource.save(Collections.EMPTY_MAP);
                    } catch (IOException e) {
                        VdbEditPlugin.Util.log(IStatus.ERROR,
                                               e,
                                               VdbEditPlugin.Util.getString("VdbEditingContextImpl.1", getResourcePath(eResource))); //$NON-NLS-1$                                    
                    }
                }

                // Update any ModelImport modelLocations to reference the
                // "builtInDataTypes.xsd" resource just added to the VDB.
                for (Iterator i = modelImports.iterator(); i.hasNext();) {
                    final ModelImport modelImport = (ModelImport)i.next();
                    final Resource eResource = modelImport.eResource();
                    URI deresolvedURI = ModelUtil.getRelativeLocation(eResource.getURI(), internalResourceUri);
                    modelImport.setModelLocation(URI.decode(deresolvedURI.toString()));

                    // I am not sure if saving the resource is necessary since each resource
                    // gets rewritten to the temp directory immediately before it is zipped up.
                    // A save should only be required if there is a chance the resource could
                    // be unloaded prior to the completion of the save.
                    try {
                        eResource.save(Collections.EMPTY_MAP);
                    } catch (IOException e) {
                        VdbEditPlugin.Util.log(IStatus.ERROR,
                                               e,
                                               VdbEditPlugin.Util.getString("VdbEditingContextImpl.1", getResourcePath(eResource))); //$NON-NLS-1$                                    
                    }
                }

            }
        } catch (Exception e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
        }
    }

    /**
     * Create the EMF resource representing the materialized view model and add it to the VDB. The model is created from the
     * collection of "materializable" virtual tables across all models in the VDB.
     * 
     * @param eResources the collection of EMF resources to check for "materializable" tables
     * @param virtToPhysMappings the Map to which any virtual table to physical table relationship mappings will be added
     * @return the materialization model or null if one could not be created
     * @since 4.2
     */
    protected Resource createMaterialization( final Collection eResources,
                                              final Map virtToPhysMappings,
                                              final List problems,
                                              final IProgressMonitor monitor ) {
        ArgCheck.isNotNull(eResources);
        ArgCheck.isNotNull(virtToPhysMappings);
        if (monitor != null) {
            String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.generating_materialization_model"); //$NON-NLS-1$
            monitor.setTaskName(taskName);
        }

        try {
            final MaterializedViewModelGenerator mvmg = new MaterializedViewModelGenerator();
            final String modelName = this.getMaterializedViewModelName(false);
            final URI folderUri = URI.createFileURI(getTempDirectory().getPath());

            for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                final Resource eResource = (Resource)iter.next();
                if (eResource instanceof EmfResource) {
                    EmfResource emfResource = (EmfResource)eResource;
                    if (emfResource.getModelAnnotation().getModelType() == ModelType.VIRTUAL_LITERAL) {
                        mvmg.execute(emfResource, true, modelName, folderUri);
                    }
                }
            }

            final Resource materialization = mvmg.getMaterializedViewModel();
            if (materialization != null && mvmg.getVirtToPhysMappings() != null) {
                for (Iterator iter = mvmg.getVirtToPhysMappings().entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry entry = (Map.Entry)iter.next();
                    final Object key = entry.getKey();
                    final Object value = entry.getValue();
                    virtToPhysMappings.put(key, value);
                }
            }
            return materialization;

        } catch (Throwable t) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_materialized_view"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, t);
            if (problems != null) {
                problems.add(status);
            }
        }

        return null;
    }

    /**
     * Add the EMF resource representing the materialized view model to the VDB.
     * 
     * @param materialization the materialization model to add
     * @param vdb the virtual database to add the model to
     * @return @since 4.2
     */
    protected void addMaterializationToVdb( final Resource materialization,
                                            final VirtualDatabase vdb,
                                            final List problems,
                                            final IProgressMonitor monitor ) {
        ArgCheck.isNotNull(vdb);
        try {
            if (monitor != null) {
                String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.adding_materialization_model_to_vdb"); //$NON-NLS-1$
                monitor.setTaskName(taskName);
            }

            // Create a new ModelReference for the materialized view model ...
            if (materialization != null) {
                final String modelName = this.getMaterializedViewModelName(false);
                final String modelPath = this.getMaterializedViewModelName(true);

                final ModelReference modelReference = this.getManifestFactory().createModelReference();
                final URI uri = materialization.getURI();
                modelReference.setModelLocation(modelPath);
                // modelReference.setPath(modelPath);
                modelReference.setUri(uri.toString());
                modelReference.setVirtualDatabase(vdb);
                modelReference.setVisible(true);
                modelReference.setName(modelName);
                modelReference.setModelType(ModelType.MATERIALIZATION_LITERAL);
                modelReference.setAccessibility(ModelAccessibility.PRIVATE_LITERAL);

                final ModelAnnotation modelAnnotation = ((EmfResource)materialization).getModelAnnotation();
                if (modelAnnotation != null) {
                    modelReference.setPrimaryMetamodelUri(modelAnnotation.getPrimaryMetamodelUri());
                    modelReference.setVisible(modelAnnotation.isVisible());
                    final String uuid = ModelerCore.getObjectIdString(modelAnnotation);
                    if (uuid != null) {
                        modelReference.setUuid(uuid);
                    }
                }

                // Add the ModelReference for the materialized view to the VDB
                vdb.getModels().add(modelReference);
            }

        } catch (Throwable t) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_adding_materialization_model_to_vdb"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, t);
            problems.add(status);
        }
    }

    /**
     * Return the collection of the {@link java.io.File}instances representing the set of DDL scripts for the materialization
     * model
     * 
     * @param materialization
     * @param problems
     * @param monitor
     * @return collection
     * @par @since 4.2
     */
    protected Collection generateDdlsForMaterialization( final Resource materialization,
                                                         final List problems,
                                                         final IProgressMonitor monitor ) {
        Collection result = Collections.EMPTY_LIST;
        if (materialization != null) {
            if (monitor != null) {
                String taskName = VdbEditPlugin.Util.getString("VdbEditingContextImpl.creating_ddl_scripts"); //$NON-NLS-1$
                monitor.setTaskName(taskName);
            }

            final StyleRegistry registry = DdlPlugin.getStyleRegistry();
            final Collection styles = registry.getStyles();
            result = new ArrayList(styles.size());

            for (final Iterator iter = styles.iterator(); iter.hasNext();) {
                final Style ddlType = (Style)iter.next();
                final File ddlFile = generateDdlFile(materialization, ddlType, problems, monitor);
                result.add(ddlFile);
            }
        }
        return result;
    }

    /**
     * Create a DDL script file for the specified resource and type.
     * 
     * @param resource
     * @param ddlType
     * @param problems
     * @param monitor
     * @return @since 4.2
     */
    protected File generateDdlFile( final Resource resource,
                                    final Style ddlType,
                                    final List problems,
                                    final IProgressMonitor monitor ) {

        if (resource != null && ddlType != null) {
            final String modelName = resource.getURI().lastSegment();
            final String typeName = ddlType.getName();
            final String modelFileName = this.createValidFileName(MATERIALIZATION_DDL_FILE_PREFIX + typeName)
                                         + MATERIALIZATION_DDL_FILE_SUFFIX;

            final DdlWriter writer = DdlPlugin.getInstance().createDdlWriter();
            final DdlOptions ddlOptions = writer.getOptions();
            ddlOptions.setGenerateComments(false);
            ddlOptions.setGenerateDropStatements(true);
            ddlOptions.setNativeTypeUsed(true);
            ddlOptions.setStyle(ddlType);

            File ddlFile = null;
            FileOutputStream stream = null;
            ByteArrayOutputStream tempStream = null;
            try {
                ddlFile = this.getTempDirectoryFile(modelFileName);
                stream = new FileOutputStream(ddlFile);

                // Create Tempory 5K Byte Arraystream
                tempStream = new ByteArrayOutputStream(5 * 1024);
                writer.write(resource, modelName, modelFileName, tempStream, monitor);
                byte[] orginalDDL = tempStream.toByteArray();

                // Now provide a way to modify the DDL, specific to a given
                // datasource, adding any addtional stuff.
                ScriptDecorator scriptDecorator = new ScriptDecorator();
                byte[] modifiedDDL = scriptDecorator.modifyDDL(ddlType.getName(), orginalDDL);

                // Now copy the modified DDL into the file
                stream.write(modifiedDDL);

            } catch (Throwable t) {
                final Object[] params = new Object[] {typeName, modelName};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_creating_ddl_scripts", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, t);
                problems.add(status);
            } finally {
                try {
                    if (tempStream != null) {
                        tempStream.close();
                    }
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException err) {
                }
            }
            return ddlFile;
        }
        return null;
    }

    /**
     * Create a user file from the input File
     * 
     * @param file the supplied user file
     * @return @since 5.5.3
     */
    protected File generateUserFile( final File file ) {

        if (file != null) {
            final String fileName = file.getName();

            final String userFileName = PATH_OF_USERFILES_IN_ARCHIVE + fileName;

            File userFile = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream(file);

                userFile = this.getTempDirectoryFile(userFileName);
                fos = new FileOutputStream(userFile);

                // Copy the original file contents into the userFile
                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }

            } catch (Throwable t) {
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_creating_user_file", userFileName); //$NON-NLS-1$
                VdbEditPlugin.Util.log(IStatus.ERROR, msg);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException err) {
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.error_creating_user_file", userFileName); //$NON-NLS-1$
                    VdbEditPlugin.Util.log(IStatus.ERROR, msg);
                }
            }
            return userFile;
        }
        return null;
    }

    /**
     * Return the collection of script files used to load and refresh all materialized views in a VDB.
     * 
     * @param vdbName The name of the VDB for which to create scripts
     * @param virtualToPhysicalTableMappings the set of bindings of
     *        <code>MaterializedViewModel->[MaterializationTable, MaterializationStagingTable]</code>.
     * @param problems List of problems that occured during script generation, if any.
     * @param monitor
     * @return the Collection of script files for all materialized models in a VDB.
     * @since 4.2
     */
    protected Collection generateMateriailizedViewLoadRefreshScripts( final String vdbName,
                                                                      final Map virtualToPhysicalTableMappings,
                                                                      final List problems,
                                                                      final IProgressMonitor monitor ) {
        Collection result = new ArrayList();
        // Create template data
        Collection templateData = this.createMaterializedViewTemplateData(virtualToPhysicalTableMappings);
        // ================================
        // Foreach supported database type
        // ================================
        Iterator databaseItr = DatabaseDialect.getAllDialects().iterator();
        while (databaseItr.hasNext()) {
            DatabaseDialect aDialect = (DatabaseDialect)databaseItr.next();
            // File name and stream for Truncate file
            String truncFileName = ScriptType.truncateScriptFileName(aDialect, vdbName);
            File truncFile = this.getTempDirectoryFile(truncFileName);
            OutputStream truncStream = null;
            try {
                truncStream = new FileOutputStream(truncFile);
            } catch (FileNotFoundException e) {
                final Object[] params = new Object[] {vdbName};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_turncate_scripts", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e);
                problems.add(status);
            }
            // File name and stream for Swap file
            String swapFileName = ScriptType.swapScriptFileName(aDialect, vdbName);
            File swapFile = this.getTempDirectoryFile(swapFileName);
            OutputStream swapStream = null;
            try {
                swapStream = new FileOutputStream(swapFile);
            } catch (FileNotFoundException e) {
                final Object[] params = new Object[] {vdbName};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_swap_scripts", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e);
                problems.add(status);
            }
            try {
                // ================================
                // Foreach materialization
                // ================================
                Iterator materializationDataItr = templateData.iterator();
                while (materializationDataItr.hasNext()) {
                    MaterializedViewScriptGenerator scriptGen = new MaterializedViewScriptGeneratorImpl(
                                                                                                        (TemplateData)materializationDataItr.next());
                    // ========================================================================================
                    // Gen truncate scripts - one for each materialization per supported RDBMS type
                    // ========================================================================================
                    try {
                        scriptGen.generateMaterializationTruncateScript(truncStream, aDialect);
                    } catch (Throwable t) {
                        final Object[] params = new Object[] {vdbName};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_turncate_scripts", params); //$NON-NLS-1$
                        final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, t);
                        problems.add(status);
                    }
                    // ========================================================================================
                    // Gen swap scripts - one for each materialization per supported RDBMS type
                    // ========================================================================================
                    try {
                        scriptGen.generateMaterializationSwapScript(swapStream, aDialect);
                    } catch (Throwable t) {
                        final Object[] params = new Object[] {vdbName};
                        final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_swap_scrips", params); //$NON-NLS-1$
                        final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, t);
                        problems.add(status);
                    }
                }
            } finally {
                if (truncStream != null) {
                    try {
                        truncStream.close();
                    } catch (IOException err) {
                    }
                }
                if (swapStream != null) {
                    try {
                        swapStream.close();
                    } catch (IOException err) {
                    }
                }
            }
            // Add results for this database type
            if (truncFile.length() > 0) {
                result.add(truncFile);
            }
            if (swapFile.length() > 0) {
                result.add(swapFile);
            }
        }

        // ========================================================================================
        // Gen load scripts - one (MM server) for each materialization
        // ========================================================================================
        // File name and stream for Load file
        String loadFileName = ScriptType.loadScriptFileName(DatabaseDialect.METAMATRIX, vdbName);
        File loadFile = this.getTempDirectoryFile(loadFileName);
        OutputStream loadStream = null;
        try {
            loadStream = new FileOutputStream(loadFile);
        } catch (FileNotFoundException e) {
            final Object[] params = new Object[] {vdbName};
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_load_scripts", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, e);
            problems.add(status);
        }
        // ================================
        // For each materialization
        // ================================
        try {
            Iterator materializationDataItr = templateData.iterator();
            while (materializationDataItr.hasNext()) {
                final TemplateData template = (TemplateData)materializationDataItr.next();
                MaterializedViewScriptGenerator scriptGen = new MaterializedViewScriptGeneratorImpl(template);
                try {
                    scriptGen.generateMaterializationLoadScript(loadStream);
                } catch (Throwable t) {
                    final Object[] params = new Object[] {vdbName};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Error_creating_load_scripts", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, t);
                    problems.add(status);
                }
            }
        } finally {
            if (loadStream != null) {
                try {
                    loadStream.close();
                } catch (IOException err) {
                }
            }
        }
        if (loadFile.length() > 0) {
            result.add(loadFile);
        }
        return result;
    }

    /**
     * Utility to take a file name string (without any extension) and return a new name with all non letter or digit characters
     * replaced by an underscore.
     * 
     * @param fileName
     * @return @since 4.2
     */
    protected String createValidFileName( final String fileName ) {
        // Go through the string and ensure that each character is valid ...
        StringBuffer sb = new StringBuffer(100);
        CharacterIterator charIter = new StringCharacterIterator(fileName);
        char c = charIter.first();

        // The remaining characters must be either alphabetic or digit character ...
        while (c != CharacterIterator.DONE) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                sb.append('_');
            }
            c = charIter.next();
        }

        return sb.toString();
    }

    /**
     * Create the IndexingContext to use for indexing the virtual database
     * 
     * @return @since 4.2
     */
    protected IndexingContext getContextForIndexer( final Collection eResources,
                                                    final Container container ) {
        final IndexingContext context = new IndexingContext();
        if (eResources != null) {
            // Create the collection of resources to be used within the IndexContext. The "resources
            // in context" collection provides the scope of EMF resources used by SqlAspects when
            // resolving UUIDs for index records
            Collection resourcesInScope = new ArrayList(eResources);

            // MyCode : 18565
            updateResource(resourcesInScope, container);

            context.setResourcesInContext(resourcesInScope);
        }

        if (this.materializedModelMap != null && !this.materializedModelMap.isEmpty()) {
            for (Iterator iter = this.materializedModelMap.entrySet().iterator(); iter.hasNext();) {
                final Map.Entry entry = (Map.Entry)iter.next();
                final EObject virtualTable = (EObject)entry.getKey();
                final Collection physicalTables = (Collection)entry.getValue();
                context.addMaterializedTables(virtualTable, physicalTables);
            }
        }
        return context;
    }

    /**
     * updates the collection if the systemVdbResources are not already in the collection
     * 
     * @param resourcesInScope
     * @since 4.3
     */
    private void updateResource( Collection resourcesInScope,
                                 final Container container ) {
        Resource[] systemVdbResources = ModelerCore.getSystemVdbResources();
        for (int idx = 0; idx < systemVdbResources.length; idx++) {
            ObjectID objectID = ((EmfResource)systemVdbResources[idx]).getUuid();

            Resource resrc = container.getResourceFinder().findByUUID(objectID, false);
            if (resrc != null && !resourcesInScope.contains(resrc)) {
                resourcesInScope.add(resrc);
            }
        }
    }

    /**
     * Index the collection of EmfResources in the given list of resources, adds an EmfResource to an Index if the IResource for
     * the EmfResource exists, if the resource does not exist, the EmfResource is removed from the document.
     * 
     * @param emfResource The list of {@link Resource EMF Resource}object to index; may not be null
     * @param indexFile the {@link File}to which the index information should be written; may not be null
     */
    protected void produceIndex( final IIndexer indexer,
                                 final List eResources,
                                 final File indexFile ) throws IOException {
        // runtime index
        IIndex runtimeIndex = null;
        if (IndexUtil.indexFileExists(indexFile.getAbsolutePath())) {
            runtimeIndex = new Index(indexFile.getAbsolutePath(), true);
        } else {
            runtimeIndex = new Index(indexFile.getAbsolutePath(), false);
        }

        // add EMfResource to the Runtime Index
        // emf document
        final VdbDocumentImpl document = new VdbDocumentImpl(indexFile.getName(), eResources,
                                                             Collections.unmodifiableMap(this.pathsByResourceUri));

        runtimeIndex.add(document, indexer);
        runtimeIndex.save();
    }

    protected String getVdbName() {
        final String filename = vdbFilePath.lastSegment();
        final String vdbName = FileUtils.getFilenameWithoutExtension(filename);
        return vdbName;
    }

    /**
     * Return the name of the materialized view model that may be added to the VDB if "materializable" virtual tables are detected
     * in the VDB's models.
     * 
     * @param includeExtension
     * @return @since 4.2
     */
    protected String getMaterializedViewModelName( boolean includeExtension ) {
        String mvModelName = MATERIALIZATION_MODEL_NAME;
        if (includeExtension) {
            mvModelName += MATERIALIZATION_MODEL_FILE_SUFFIX;
        }
        return mvModelName;
    }

    protected IStatus createSingleIStatus( final List problems,
                                           final String desc ) {
        // Put all of the problems into a single IStatus ...
        final String PLUGINID = VdbEditPlugin.PLUGIN_ID;
        IStatus resultStatus = null;
        if (problems.isEmpty()) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.completed", desc); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, PLUGINID, PRODUCE_INDEX_WITH_NO_PROBLEMS, msg, null);
            resultStatus = status;
        } else if (problems.size() == 1) {
            resultStatus = (IStatus)problems.get(0);
        } else {
            // There were problems, so determine whether there were warnings and errors ...
            int numErrors = 0;
            int numWarnings = 0;
            final Iterator iter = problems.iterator();
            while (iter.hasNext()) {
                final IStatus aStatus = (IStatus)iter.next();
                if (aStatus.getSeverity() == IStatus.WARNING) {
                    ++numWarnings;
                } else if (aStatus.getSeverity() == IStatus.ERROR) {
                    ++numErrors;
                }
            }

            // Create the final status ...
            final IStatus[] statusArray = (IStatus[])problems.toArray(new IStatus[problems.size()]);
            if (numWarnings != 0 && numErrors == 0) {
                final Object[] params = new Object[] {desc, new Integer(numWarnings)};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.completed_with_warnings", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            } else if (numWarnings == 0 && numErrors != 0) {
                final Object[] params = new Object[] {desc, new Integer(numErrors)};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.resulted_in_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            } else if (numWarnings != 0 && numErrors != 0) {
                final Object[] params = new Object[] {desc, new Integer(numWarnings), new Integer(numErrors)};
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.resulted_in_warnings_and_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            } else {
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.completed_with_no_warnings_or_errors", desc); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            }
        }
        return resultStatus;
    }

    /**
     * @param modelErrors
     * @param modelWarnings
     * @return
     */
    private IStatus createModelStatus( final List problemStatuses,
                                       final List modelErrors,
                                       final List modelWarnings ) {
        final int numProblems = problemStatuses.size();
        final int numErrors = modelErrors.size();
        final int numWarnings = modelWarnings.size();
        if (numProblems == 0 && numErrors == 0 && numWarnings == 0) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Saving_manifest_model_completed"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
            return status;
        }
        // Add the errors and warnings to the problems ...
        final List statuses = new ArrayList(problemStatuses);
        final Iterator iter = modelErrors.iterator();
        while (iter.hasNext()) {
            final Resource.Diagnostic diag = (Resource.Diagnostic)iter.next();
            String msg = diag.getMessage();
            final Object[] params = new Object[] {new Integer(diag.getLine()), new Integer(diag.getColumn()), diag.getLocation()};
            msg = msg + VdbEditPlugin.Util.getString("VdbEditingContextImpl.LineSuffix", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
            statuses.add(status);
        }
        final Iterator iter2 = modelWarnings.iterator();
        while (iter2.hasNext()) {
            final Resource.Diagnostic diag = (Resource.Diagnostic)iter2.next();
            String msg = diag.getMessage();
            final Object[] params = new Object[] {new Integer(diag.getLine()), new Integer(diag.getColumn()), diag.getLocation()};
            msg = msg + VdbEditPlugin.Util.getString("VdbEditingContextImpl.LineSuffix", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.WARNING, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
            statuses.add(status);
        }
        final String desc = VdbEditPlugin.Util.getString("VdbEditingContextImpl.saving_manifest_model"); //$NON-NLS-1$
        return createSingleIStatus(statuses, desc);
    }

    protected void assertIsOpen() {
        if (!isOpen()) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.must_be_open"); //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Assert that the model files contained within the VDB have been loaded into the context's internal resource set. Certain
     * operations require that all models be in the resource set and will potentially fail if they are not. If a user only needs
     * to examine the contents of the VDB without modifying it then the model files do not have to be loaded.
     */
    protected void assertModelsLoaded() {
        if (!this.loadModelsOnOpen) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.the_models_files_within_the_vdb_must_be_loaded"); //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }

    }

    /**
     * Create the options used when saving and loading the manifest model. This implementation returns an empty map.
     * 
     * @return the options; may not be null
     */
    protected Map createOptions() {
        return this.getVdbContainer().getLoadOptions();
    }

    /**
     * This method can be overridden to alter how a VirtualDatabase object is updated.
     * 
     * @param vdbObj
     * @param vdbfile
     */
    protected void updateVirtualDatabase( final VirtualDatabase vdbObj ) {
        // Ensure the name is correct
        vdbObj.setName(this.getVdbName());

        if (vdbObj.getUuid() == null) {
            final ObjectID id = IDGenerator.getInstance().getFactory(UUID.PROTOCOL).create();
            vdbObj.setUuid(id.toString());
        }

        // Update the timestamp ...
        final Date currentDate = DateUtil.getCurrentDate();
        vdbObj.setTimeLastChangedAsDate(currentDate);
        vdbObj.setTimeLastProducedAsDate(currentDate);
    }

    // MyDefect : Added for defect 17255 ResourceRenameVdbCommand is the only class should use this.
    public void updateVdbXmiName() {
        if (this.manifestResource != null) {
            this.getVirtualDatabase().setName(this.getVdbName());
            this.manifestResource.setModified(true);
        }
    }

    /**
     * Add a warning marker to the specified virtual database indicating that it contains no models - if applicable.
     * 
     * @param vdbObj
     */
    private void addNoModelsWarning( final VirtualDatabase vdbObj ) {
        // Add a warning to the VirtualDatabase if there are no models
        if (vdbObj.getModels().isEmpty()) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.The_VDB_has_no_models_1"); //$NON-NLS-1$

            // Check existing markers for "The VDB has no models" warning
            boolean foundNoModelsWarning = false;
            for (Iterator iter = vdbObj.getMarkers().iterator(); iter.hasNext();) {
                ProblemMarker marker = (ProblemMarker)iter.next();
                if (marker.getMessage().equals(msg)) {
                    foundNoModelsWarning = true;
                    break;
                }
            }
            // Add the warning if it did not already exist then add it
            if (!foundNoModelsWarning) {
                this.createProblem(vdbObj, IStatus.WARNING, msg, null);
            }
        }
    }

    /**
     * This method returns a Properties object representing all model source information available through the
     * SqlModelSourceAspect.
     * 
     * @param eResource the Resource instance to process; may not be null
     * @return @throws ModelWorkspaceException
     */
    private Properties getModelSourceProperties( final Resource eResource ) {
        ArgCheck.isNotNull(eResource);

        // Check for model source information immediately under the model root
        for (Iterator iter = eResource.getContents().iterator(); iter.hasNext();) {
            EObject eObject = (EObject)iter.next();
            SqlAspect aspect = AspectManager.getSqlAspect(eObject);
            if (aspect != null && aspect instanceof SqlModelSourceAspect) {
                return ((SqlModelSourceAspect)aspect).getProperties(eObject);
            }
        }
        return null;
    }

    /**
     * Set the ProblemMarkers on the VirtualDatabase instance
     * 
     * @param vdb
     * @param problems
     * @return
     */
    private void setVdbProblems( final VirtualDatabase vdb,
                                 final List statuses ) {

        // Set the ProblemMarkers on the VirtualDatabase instance
        for (Iterator iter = statuses.iterator(); iter.hasNext();) {
            IStatus status = (IStatus)iter.next();
            this.addVdbStatus(vdb, status);
        }

        // Add a warning to the VirtualDatabase if there are no models
        if (vdb.getModels().isEmpty()) {
            addNoModelsWarning(vdb);
        }
    }

    private void addVdbStatus( final VirtualDatabase vdb,
                               final IStatus status ) {
        if (status == null) {
            return;
        }
        if (status.isMultiStatus()) {
            IStatus[] statuses = status.getChildren();
            for (int i = 0; i < statuses.length; i++) {
                this.addVdbStatus(vdb, statuses[i]);
            }
        } else if (status.getSeverity() == IStatus.WARNING) {
            this.createProblem(vdb, status.getSeverity(), status.getMessage(), status.getException());
        } else if (status.getSeverity() == IStatus.ERROR) {
            this.createProblem(vdb, status.getSeverity(), status.getMessage(), status.getException());
        }
    }

    /**
     * Set the ProblemMarkers on the specified model reference
     * 
     * @param container
     * @param context
     * @return
     */
    private void setModelReferenceProblems( final ModelReference modelRef,
                                            final ValidationContext context ) {
        ArgCheck.isNotNull(modelRef);
        ArgCheck.isNotNull(context);

        // If the ModelReference is PRIVATE then there should be no workspace problem markers
        if (modelRef.getAccessibility() == ModelAccessibility.PRIVATE_LITERAL) {
            return;
        }
        this.setContainerProblems(modelRef, context);
    }

    /**
     * Set the ProblemMarkers on the specifed container
     * 
     * @param container
     * @param context
     * @return
     */
    private void setContainerProblems( final ProblemMarkerContainer container,
                                       final ValidationContext context ) {
        ArgCheck.isNotNull(container);
        ArgCheck.isNotNull(context);

        if (context.hasResults()) {
            final List results = context.getValidationResults();
            for (final Iterator iter = results.iterator(); iter.hasNext();) {
                final ValidationResult result = (ValidationResult)iter.next();
                final String eObjectUri = (result.getLocationUri() != null ? result.getLocationUri() : result.getTargetUri());
                final EObject eObject = (eObjectUri != null ? this.getVdbContainer().getEObject(URI.createURI(eObjectUri), false) : null);
                if (result.hasProblems()) {
                    ValidationProblem[] problems = result.getProblems();
                    for (int probCnt = 0; probCnt < problems.length; probCnt++) {
                        addProblemMarker(container, eObject, problems[probCnt]);
                    }
                }
            }
        }
    }

    /**
     * Create and add a new ProblemMarker instance to the specified container
     * 
     * @param container
     * @param problem
     * @return
     */
    private void addProblemMarker( final ProblemMarkerContainer container,
                                   final EObject targetObject,
                                   final ValidationProblem problem ) {
        if (problem != null) {
            final ProblemMarker marker = this.getManifestFactory().createProblemMarker();
            marker.setCode(problem.getCode());
            marker.setMessage(problem.getMessage());
            if (targetObject != null) {
                marker.setTargetUri(ModelerCore.getObjectIdString(targetObject));
            }
            String target = null;
            if (container instanceof VirtualDatabase) {
                target = ((VirtualDatabase)container).getName();
            } else if (container instanceof ModelReference) {
                target = ((ModelReference)container).getModelLocation();
            }
            marker.setTarget(target);
            switch (problem.getSeverity()) {
                case IStatus.ERROR:
                    marker.setSeverity(Severity.ERROR_LITERAL);
                    break;
                case IStatus.WARNING:
                    marker.setSeverity(Severity.WARNING_LITERAL);
                    break;
                case IStatus.INFO:
                    marker.setSeverity(Severity.INFO_LITERAL);
                    break;
                default:
                    marker.setSeverity(Severity.OK_LITERAL);
            }
            marker.setMarked(container);
        }
    }

    private EObject getProblemMarkerEObject( final ProblemMarker marker ) {
        if (marker != null && marker.getTargetUri() != null && marker.getTargetUri().startsWith(UUID.PROTOCOL)) {
            return (EObject)getVdbContainer().getEObjectFinder().find(marker.getTargetUri());
        }
        return null;
    }

    /**
     * Clear all problem markers before beggining the save operation.
     * 
     * @param container The problem marker container
     */
    private void clearProblemMarkers( final ProblemMarkerContainer container ) {
        // Clear the current problem markers for this model reference
        container.getMarkers().clear();
    }

    /**
     * Return an InputStream to the archive entry specified by the ModelReference
     * 
     * @param modelReference
     * @return
     */
    protected InputStream getArchiveResourceStream( final ModelReference modelReference ) throws IOException {
        ArgCheck.isNotNull(modelReference);
        return this.getArchiveResourceStream(modelReference.getModelLocation());
    }

    /**
     * Return an InputStream to the archive entry specified by the ModelReference
     * 
     * @param modelReference
     * @return
     */
    protected InputStream getArchiveResourceStream( final String entryName ) throws IOException {
        ArgCheck.isNotNull(entryName);
        ArgCheck.isNotZeroLength(entryName);

        // Make sure the archive is open ...
        if (this.vdbArchive == null) {
            final File vdbFile = this.vdbFilePath.toFile();
            if (vdbFile.exists() && vdbFile.length() > 0) {
                this.vdbArchive = new ZipFile(vdbFile);
            }
        }

        InputStream istream = null;
        if (this.vdbArchive != null) {

            // Find the archive entry specified in the ModelReference
            ZipEntry zipEntry = this.vdbArchive.getEntry(entryName);

            // If the entry was found, return the InputStream
            if (zipEntry != null) {
                istream = this.vdbArchive.getInputStream(zipEntry);
                if (istream == null) {
                    final Object[] params = new Object[] {entryName, this.vdbFilePath};
                    final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_obtain_input_stream", params); //$NON-NLS-1$
                    throw new IOException(msg);
                }
            }

        }

        return istream;
    }

    /**
     * Return the URI for the internal resource
     */
    protected URI getInternalResourceUri( final IPath modelPath ) {
        ArgCheck.isNotNull(modelPath);

        final TempDirectory tempDir = this.getTempDirectory();
        final IPath tempDirPath = new Path(tempDir.getPath());
        final IPath tempDirFilePath = tempDirPath.append(modelPath);
        return URI.createFileURI(tempDirFilePath.toString());
    }

    /**
     * Add the {@link org.eclipse.emf.ecore.resource.Resource}instance to the vdb editing context's internal resource set. This
     * method will remove any existing resource with the same URI prior to adding the new instance.
     * 
     * @param resource
     * @return @throws Exception if there are problems loading from the input stream
     * @since 4.2
     */
    protected void addToInternalResourceSet( final Resource resource,
                                             final IPath modelPath ) {
        ArgCheck.isNotNull(resource);
        ArgCheck.isNotNull(modelPath);

        // Remove any existing resource with this uri ...
        this.removeFromInternalResourceSet(resource.getURI());

        // Create a new resource with the specified uri
        this.getVdbContainer().getResources().add(resource);

        try {
            String modelPathString = null;
            if (modelPath.segmentCount() == 1 && modelPath.lastSegment().equals(MANIFEST_MODEL_NAME)) {
                modelPathString = MANIFEST_MODEL_NAME;
            } else if (modelPath.segmentCount() == 1
                       && modelPath.lastSegment().equals(MATERIALIZATION_MODEL_NAME + MATERIALIZATION_MODEL_FILE_SUFFIX)) {
                modelPathString = MATERIALIZATION_MODEL_NAME + MATERIALIZATION_MODEL_FILE_SUFFIX;
            }
            if (modelPathString == null) {
                modelPathString = modelPath.makeAbsolute().toString();
            }
            this.pathsByResourceUri.put(resource.getURI(), modelPathString);

            final URIConverter converter = this.getVdbContainer().getURIConverter();
            if (converter != null) {
                // Put in the URI converter the model path ...
                final URI actualUri = resource.getURI();
                final URI modelPathUri = URI.createURI(modelPathString);
                converter.getURIMap().put(modelPathUri, actualUri);

                if (resource instanceof XSDResourceImpl) {
                    // If this model was an XSD, then we should add an entry into the URIConverter to convert the VDB-based URI
                    // back to this resource. For example, XSD files that import/reference other XSD files have their
                    // schemaLocation URLs changed to be of the form "http://vdb.metamatrix.com<ModelPath>?vdbTokens=true",
                    // where the "<ModelPath>" is an escaped form of the URI (see getEscapedURI)

                    // Compute the escaped form of the URI ...
                    final URI escapedUri = getEscapedURI(resource);

                    // Add to the URIConverter ...
                    converter.getURIMap().put(escapedUri, actualUri);
                }
            }
        } catch (RuntimeException theException) {
            // Remove any existing resource with this uri ...
            this.removeFromInternalResourceSet(resource.getURI());
            throw theException;
        }
    }

    /**
     * Create a new {@link org.eclipse.emf.ecore.resource.Resource}instance within the vdb editing context's internal resource
     * set. The resource will be created for the specified URI and loaded using the InputStream. This method will remove any
     * existing resource with this URI prior to creating and adding the new instance. The method will also close the input stream
     * before returning.
     * 
     * @param istream
     * @param uri
     * @return @throws Exception if there are problems loading from the input stream
     * @since 4.2
     */
    protected Resource addToInternalResourceSet( final InputStream istream,
                                                 final URI uri,
                                                 final IPath modelPath ) throws IOException {
        ArgCheck.isNotNull(uri);

        Resource eResource = null;

        try {

            // If the resource already exists in the container due to the loading of
            // a prior resource then only reload it if it is not currently loaded
            eResource = this.getVdbContainer().getResource(uri, false);
            if (eResource != null && !eResource.isLoaded()) {
                this.removeFromInternalResourceSet(uri);
                eResource = null;
            }
            if (DEBUG_ON) {
                VdbEditPlugin.Util.log("addToInternalResourceSet(): Adding " + uri.lastSegment()); //$NON-NLS-1$
            }

            if (eResource == null) {
                // Create a new resource with the specified uri
                eResource = this.getVdbContainer().createResource(uri);

                // Load the resource using the input stream contents
                if (istream != null) {
                    try {
                        eResource.load(istream, this.getOptions());
                    } finally {
                        try {
                            istream.close();
                        } catch (IOException e) {
                            VdbEditPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
                        }
                    }
                }
            }

            String modelPathString = modelPath.toString();
            this.pathsByResourceUri.put(eResource.getURI(), modelPathString);

            final URIConverter converter = this.getVdbContainer().getURIConverter();
            if (converter != null) {
                // Put in the URI converter the model path ...
                final URI actualUri = eResource.getURI();
                final URI modelPathUri = URI.createURI(modelPathString);
                converter.getURIMap().put(modelPathUri, actualUri);

                if (eResource instanceof XSDResourceImpl) {
                    // If this model was an XSD, then we should add an entry into the URIConverter to convert the VDB-based URI
                    // back to this resource. For example, XSD files that import/reference other XSD files have their
                    // schemaLocation URLs changed to be of the form "http://vdb.metamatrix.com<ModelPath>?vdbTokens=true",
                    // where the "<ModelPath>" is an escaped form of the URI (see getEscapedURI)

                    // Compute the escaped form of the URI ...
                    final URI escapedUri = getEscapedURI(eResource);

                    // Add to the URIConverter ...
                    converter.getURIMap().put(escapedUri, actualUri);
                    if (DEBUG_ON) {
                        VdbEditPlugin.Util.log("addToInternalResourceSet(): Adding URI mapping from " + escapedUri + " -> " + actualUri); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    // Get the namespace form of the URI ...
                    final String namespace = ((XSDResourceImpl)eResource).getSchema().getTargetNamespace();
                    if (namespace != null) {
                        // Add to the URIConverter ...
                        converter.getURIMap().put(URI.createURI(namespace), actualUri);
                        if (DEBUG_ON) {
                            VdbEditPlugin.Util.log("addToInternalResourceSet(): Adding URI mapping from " + URI.createURI(namespace) + " -> " + actualUri); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }
            }
        } catch (IOException theException) {
            // System.err.println("**** Exception encountered "+theException);
            this.removeFromInternalResourceSet(uri);
            throw theException;
        } catch (RuntimeException theException) {
            // System.err.println("**** Exception encountered "+theException);
            this.removeFromInternalResourceSet(uri);
            throw theException;
        }

        return eResource;
    }

    protected Resource findInternalResource( final String modelPath ) {
        final URI uri = this.getInternalResourceUri(new Path(modelPath));
        return this.getVdbContainer().getResource(uri, false);
    }

    /**
     * Remove the {@link org.eclipse.emf.ecore.resource.Resource}instance within the vdb editing context's internal resource set
     * corresponding to the ModelReference.
     * 
     * @param modelReference
     * @throws Exception
     * @since 4.2
     */
    protected void removeFromInternalResourceSet( final ModelReference modelReference ) {
        ArgCheck.isNotNull(modelReference);
        final URI uri = this.getInternalResourceUri(new Path(modelReference.getModelLocation()));
        this.removeFromInternalResourceSet(uri);
    }

    /**
     * Remove the {@link org.eclipse.emf.ecore.resource.Resource}instance within the vdb editing context's internal resource set
     * corresponding to the URI.
     * 
     * @param uri
     * @throws Exception
     * @since 4.2
     */
    protected void removeFromInternalResourceSet( final URI uri ) {
        ArgCheck.isNotNull(uri);

        final Resource eResource = this.getVdbContainer().getResource(uri, false);
        if (eResource != null) {

            final String modelPath = (String)this.pathsByResourceUri.get(eResource.getURI());

            // Unload the resource ...
            if (eResource.isLoaded()) {
                if (DEBUG_ON) {
                    VdbEditPlugin.Util.log("removeFromInternalResourceSet(): Unloading " + eResource.getURI().lastSegment()); //$NON-NLS-1$
                }
                eResource.unload();
            }

            if (DEBUG_ON) {
                VdbEditPlugin.Util.log("removeFromInternalResourceSet(): Removing " + eResource.getURI().lastSegment()); //$NON-NLS-1$
            }

            // Remove it from the resource set ...
            this.getVdbContainer().getResources().remove(eResource);

            final URIConverter converter = this.getVdbContainer().getURIConverter();
            if (converter != null) {
                // Remove the modelPath uri entry ...
                if (modelPath != null) {
                    final URI modelPathUri = URI.createURI(modelPath);
                    converter.getURIMap().remove(modelPathUri);
                }

                if (eResource instanceof XSDResourceImpl) {
                    // If this model was an XSD, then we should REMOVE an entry from the URIConverter to convert the VDB-based URI
                    // back to this resource. For example, XSD files that import/reference other XSD files have their
                    // schemaLocation URLs changed to be of the form "http://vdb.metamatrix.com<ModelPath>?vdbTokens=true",
                    // where the "<ModelPath>" is an escaped form of the URI (see getEscapedURI)

                    // Compute the escaped form of the URI ...
                    final URI escapedUri = getEscapedURI(eResource);

                    // Add to the URIConverter ...
                    converter.getURIMap().remove(escapedUri);
                }
            }

            this.pathsByResourceUri.remove(eResource.getURI()); // remove last!
        }
    }

    protected synchronized TempDirectory getTempDirectory() {
        if (this.tempDirectory == null) {
            // Create a temporary directory under the vdb working folder location to be
            // used by the editing context to extract the contents of the zip file
            final VdbEditPlugin plugin = VdbEditPlugin.getInstance();
            final String absolutePath;

            // Define the location of the vdb working folder
            if (this.vdbWorkingPath != null) {
                final File vdbWorkingFolder = new File(this.vdbWorkingPath.toOSString());
                if (!vdbWorkingFolder.exists()) {
                    vdbWorkingFolder.mkdir();
                }
                absolutePath = vdbWorkingFolder.getAbsolutePath();
            } else if (plugin != null) {
                final File vdbWorkingFolder = VdbEditPlugin.getVdbWorkingDirectory();
                if (!vdbWorkingFolder.exists()) {
                    vdbWorkingFolder.mkdir();
                }
                absolutePath = vdbWorkingFolder.getAbsolutePath();
            } else {
                absolutePath = FileUtils.TEMP_DIRECTORY;
            }

            // Create temporary directory checking the file system to ensure the
            // file represents a new and non-existent folder
            this.tempDirectory = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
            while (new File(this.tempDirectory.getPath()).exists()) {
                VdbEditPlugin.Util.log(IStatus.WARNING,
                                       "Temporary Folder " + this.tempDirectory.getPath() + " already exists; Creating new folder..."); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    Thread.sleep(10);
                } catch (final InterruptedException ignored) {
                }
                this.tempDirectory = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
            }
            this.tempDirectory.create();
        }
        return this.tempDirectory;
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
    protected File addToTempDirectory( final InputStream istream,
                                       final String fileName ) throws IOException {
        ArgCheck.isNotNull(istream);
        if (DEBUG_ON) {
            VdbEditPlugin.Util.log("addToTempDirectory(): Adding " + fileName); //$NON-NLS-1$
        }

        OutputStream fos = null;
        OutputStream bos = null;
        File tempDirFile = null;
        try {
            tempDirFile = this.getTempDirectoryFile(fileName);
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
                } catch (IOException err) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException err) {
                }
            }
        }
        return tempDirFile;
    }

    /**
     * Create a new {@link java.io.File}instance within the TempDirectory location using the specified name and InputStream. This
     * method will remove any existing file with this name prior to creating the new file.
     * 
     * @param istream
     * @param fileName
     * @return @throws Exception if there are problems reading from the input stream
     * @since 4.2
     */
    protected File addToTempDirectory( final Resource eResource,
                                       final String fileName ) throws IOException {
        ArgCheck.isNotNull(eResource);
        ArgCheck.isNotNull(fileName);
        ArgCheck.isNotZeroLength(fileName);
        if (DEBUG_ON) {
            VdbEditPlugin.Util.log("addToTempDirectory(): Adding " + eResource.getURI().lastSegment()); //$NON-NLS-1$
        }

        OutputStream fos = null;
        OutputStream bos = null;
        File tempDirFile = null;
        try {
            tempDirFile = this.getTempDirectoryFile(fileName);
            // If the file already exists in the temp directory then remove it
            // so it can be replaced with the contents of the Resource
            if (tempDirFile.exists()) {
                tempDirFile.delete();
            }

            fos = new FileOutputStream(tempDirFile);
            bos = new BufferedOutputStream(fos);
            eResource.save(bos, this.getOptions());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException err) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException err) {
                }
            }
        }
        return tempDirFile;
    }

    /**
     * Return an InputStream to the file within the TempDirectory location as specified by the ModelReference
     * 
     * @param modelReference
     * @return
     */
    protected InputStream getTempDirectoryResourceStream( final ModelReference modelReference ) throws IOException {
        ArgCheck.isNotNull(modelReference);
        final File tempDirFile = this.getTempDirectoryFile(modelReference);
        if (tempDirFile != null && tempDirFile.exists()) {
            return new FileInputStream(tempDirFile);
        }
        return null;
    }

    /**
     * Return the reference to the file within the TempDirectory location as specified by the ModelReference
     * 
     * @param modelReference
     * @return @since 4.2
     */
    protected File getTempDirectoryFile( final ModelReference modelReference ) {
        ArgCheck.isNotNull(modelReference);
        return this.getTempDirectoryFile(modelReference.getModelLocation());
    }

    /**
     * Return the reference to the file within the TempDirectory location as specified by the NonModelReference
     * 
     * @param nonModelReference
     * @return @since 4.2
     */
    protected File getTempDirectoryFile( final NonModelReference nonModelReference ) {
        ArgCheck.isNotNull(nonModelReference);
        return this.getTempDirectoryFile(nonModelReference.getPath());
    }

    /**
     * Return the reference to the file within the TempDirectory location as specified by the file name. The result may or may not
     * exist on the file system.
     * 
     * @param fileName
     * @return @since 4.2
     */
    protected File getTempDirectoryFile( final String fileName ) {
        ArgCheck.isNotNull(fileName);
        ArgCheck.isNotZeroLength(fileName);

        final TempDirectory tempDir = this.getTempDirectory();
        final IPath tempDirPath = new Path(tempDir.getPath());
        final String tempDirName = tempDirPath.lastSegment();

        // Remove the tempDirPath from the fileName if it already prepended
        String tempFileName = fileName;
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

    /**
     * Create a collection of TemplateData objects for a VDB that contains materializations. This method will create the data
     * objects that are required for the generate methods of all materializations in a VDB.
     * 
     * @param vdbName The name of the VDB for which the materialization scripts will be generated.
     * @param virtualToPhysicalTableMappings The mappings for the materialized virtual groups to their physical materialization
     *        tables.
     * @return Collection of TemplateData that will be used to generate templates for all materializations in a VDB.
     * @since 4.2
     */
    private Collection createMaterializedViewTemplateData( Map virtualToPhysicalTableMappings ) {
        final int size = virtualToPhysicalTableMappings.size();
        final List dataObjs = new ArrayList(size);
        final List created = new ArrayList(size);

        for (final Iterator iter = virtualToPhysicalTableMappings.entrySet().iterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            createMaterializedViewTemplateData(virtualToPhysicalTableMappings, (Table)entry.getKey(), created, dataObjs);
        }

        return dataObjs;
    }

    private void createMaterializedViewTemplateData( final Map virtualToPhysicalTableMappings,
                                                     final Table virtTable,
                                                     final List created,
                                                     final List dataObjs ) {
        // Return if template data already created for table
        if (created.contains(virtTable)) {
            return;
        }

        // Create template data for materialized tables this table depends upon
        final MtkXmiResourceImpl resrc = (MtkXmiResourceImpl)virtTable.eResource();
        for (final Iterator xformIter = resrc.getModelContents().getTransformations(virtTable).iterator(); xformIter.hasNext();) {
            final TransformationMappingRoot root = (TransformationMappingRoot)xformIter.next();
            for (final Iterator inputIter = root.getInputs().iterator(); inputIter.hasNext();) {
                final Object input = inputIter.next();
                if (input instanceof Table) {
                    final Table table = (Table)input;
                    if (table.isMaterialized()) {
                        createMaterializedViewTemplateData(virtualToPhysicalTableMappings, table, created, dataObjs);
                    }
                }
            }
        }

        String virtTableFullName = null;
        SqlAspect sqlAspect = (SqlAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(virtTable, SqlAspect.class);
        if (sqlAspect != null) {
            virtTableFullName = sqlAspect.getFullName(virtTable);
        } else {
            Object[] params = new Object[] {virtTable};
            VdbEditPlugin.Util.log(IStatus.ERROR,
                                   VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_get_sql_aspect.", params)); //$NON-NLS-1$
        }

        // Get virt table name
        String virtTableName = virtTable.getName();

        // Get column names
        List columns = virtTable.getColumns();
        Iterator columnItr = columns.iterator();
        String[] columnNames = new String[columns.size()];
        for (int i = 0; columnItr.hasNext(); i++) {
            Column aColumn = (Column)columnItr.next();
            columnNames[i] = aColumn.getName();
        }

        // Get phys mat view table and staging table
        String physTableFullName = null;
        String physTableNameInSrc = null;
        String physStageTableFullName = null;
        String physStageTableNameInSrc = null;
        Iterator physTablesItr = ((Collection)virtualToPhysicalTableMappings.get(virtTable)).iterator();
        while (physTablesItr.hasNext()) {
            Table aTable = (Table)physTablesItr.next();
            String aTableNameInSrc = aTable.getNameInSource();
            String aTableFullName = null;
            sqlAspect = (SqlAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(aTable, SqlAspect.class);
            if (sqlAspect != null) {
                aTableFullName = sqlAspect.getFullName(aTable);
            } else {
                Object[] params = new Object[] {aTable};
                VdbEditPlugin.Util.log(IStatus.ERROR,
                                       VdbEditPlugin.Util.getString("VdbEditingContextImpl.Unable_to_get_sql_aspect.", params)); //$NON-NLS-1$
            }

            // There are only 2 tables in this collection currently (4.2).
            // The only way to tell which is which is to compare with know suffix.
            if (aTableNameInSrc.endsWith(SqlTableAspect.STAGING_TABLE_SUFFIX)) {
                physStageTableNameInSrc = aTableNameInSrc;
                physStageTableFullName = aTableFullName;
            } else {
                physTableNameInSrc = aTableNameInSrc;
                physTableFullName = aTableFullName;
            }
        }

        // Create TemplateData for this materialization
        dataObjs.add(new MaterializedViewData(virtTableName, columnNames, virtTableFullName, physTableNameInSrc,
                                              physTableFullName, physStageTableNameInSrc, physStageTableFullName));

        // Remember that template data has been created for this table
        created.add(virtTable);
    }

    private long getCheckSum( final File f ) {
        ArgCheck.isNotNull(f);
        try {
            return FileUtils.getCheckSum(f);
        } catch (Throwable err) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.unexpectedException", f); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, err, msg);
        }
        return 0;
    }

    /**
     * Utility method to encode the segments of the supplied path such that the total path can be used within a URI. For example,
     * the path "<code>/Project Name/folder name/model name.txt</code>" cannot be directly placed into a URL, but instead needs to
     * be escaped: <code>/Project%20Name/folder%20name/model%20name.txt</code>.
     */
    protected IPath encodePathForUseInUri( final IPath pathToResourceInVdb ) {
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
     * Utility method to encode the segments of the supplied path such that the total path can be used within a URI. For example,
     * the path "<code>/Project Name/folder name/model name.txt</code>" cannot be directly placed into a URL, but instead needs to
     * be escaped: <code>/Project%20Name/folder%20name/model%20name.txt</code>.
     */
    protected String encodePathForUseInUri( final String pathToResourceInVdb ) {
        final IPath path = new Path(pathToResourceInVdb);
        final IPath pathWithEncoding = encodePathForUseInUri(path);
        return pathWithEncoding.toString();
    }

    // --------------------------------------------------------------------------------
    // Helper methods for determining which resources require validation on save
    // --------------------------------------------------------------------------------

    /**
     * Add to the set of model paths added to the VDB since it was first opened. We store model path reference instead of a direct
     * reference to the resource itself so that if a resource instance is removed it can be garbage collected.
     */
    protected void addModelToValidate( final Resource eResource ) {
        Assertion.isNotNull(eResource);
        this.addedResourceUrisSinceOpen.add(eResource.getURI());
        if (DEBUG_ON) {
            VdbEditPlugin.Util.log("addModelToValidate(): Adding " + eResource.getURI().lastSegment()); //$NON-NLS-1$
        }
    }

    /**
     * Remove from the set of resource URIs added to the VDB since it was first opened. We store URI reference instead of a direct
     * reference to the resource itself so that if a resource instance is removed it can be garbage collected.
     */
    protected void removeModelFromValidate( final Resource eResource ) {
        Assertion.isNotNull(eResource);
        this.addedResourceUrisSinceOpen.remove(eResource.getURI());
        if (DEBUG_ON) {
            VdbEditPlugin.Util.log("removeModelFromValidate(): Removing " + eResource.getURI().lastSegment()); //$NON-NLS-1$
        }
    }

    /**
     * Return the collection of ModelReference instances corresponding to the input set of emf resource intsances. If any problems
     * are encountered determining a ModelReference for a resource then a problem is added to the problems list.
     * 
     * @return
     * @since 4.2
     */
    protected Collection getModelReferences( final List eResources,
                                             final List problems ) {

        // Iterate over the list of emf resources and build a list of ModelReferences
        final Collection modelReferences = new HashSet(eResources.size());
        for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
            final Resource eResource = (Resource)iter.next();
            final ModelReference modelRef = this.getModelReference(eResource);
            if (modelRef != null) {
                modelReferences.add(modelRef);
            }
        }
        return modelReferences;
    }

    /**
     * Return a collection of newly added resources along with associated upward dependent resources that must be validated before
     * the VDB can be successfully saved.
     * 
     * @return
     * @since 4.2
     */
    protected List getInternalResourcesToValidate( final List problems ) {

        // Create a collection representing all resources added since open was called on the context
        final List resourcesToValidate = new ArrayList(this.getVdbContainer().getResources().size());
        for (Iterator i = this.addedResourceUrisSinceOpen.iterator(); i.hasNext();) {
            final Resource eResource = this.getVdbContainer().getResource((URI)i.next(), false);
            if (eResource != null && !resourcesToValidate.contains(eResource)) {
                resourcesToValidate.add(eResource);
                if (DEBUG_ON) {
                    VdbEditPlugin.Util.log("getInternalResourcesToValidate(): Newly added resource " + eResource.getURI().lastSegment()); //$NON-NLS-1$
                }
            }
        }

        // Create a collection of all resources that reference the newly added resources
        final List referencingResources = new ArrayList(this.getVdbContainer().getResources().size());
        for (Iterator i = resourcesToValidate.iterator(); i.hasNext();) {
            final Resource eResource = (Resource)i.next();

            // Get the array of resources directly referencing this resource
            Resource[] refs = this.getVdbContainer().getResourceFinder().findReferencesTo(eResource, false);
            for (int j = 0; j != refs.length; ++j) {
                Resource r = refs[j];
                if (!referencingResources.contains(r) && !resourcesToValidate.contains(r)) {
                    referencingResources.add(r);
                }
            }
        }

        // Combine the collection of newly added resources with those resources that reference them
        resourcesToValidate.addAll(referencingResources);

        // Add the VDB manifest model - we always want to validate this model.
        resourcesToValidate.add(this.manifestResource);

        return resourcesToValidate;

    }

    /**
     * Add User artifact. If there is already a user artifact with the same name, the existing artifact will be replaced.
     * 
     * @since 5.3.3
     * @param userFile the supplied userFile to add to the vdb
     * @return the added file
     */
    public File addUserFile( final File userFile ) {
        File newFile = generateUserFile(userFile);
        String newFileName = newFile.getName();

        if (this.userFiles == null) this.userFiles = new ArrayList();

        // If file with same name existed, remove from list
        Iterator iter = this.userFiles.iterator();
        while (iter.hasNext()) {
            File theFile = (File)iter.next();
            if (theFile.getName().equals(newFileName)) {
                this.userFiles.remove(theFile);
                break;
            }
        }
        // add file to the list
        this.userFiles.add(newFile);
        return newFile;
    }

    /**
     * Remove User artifact with the given name.
     * 
     * @since 5.3.3
     * @param name the name of the artifact to remove
     */
    public void removeUserFileWithName( final String name ) {
        // Remove resource from the temporary directory location ...
        final File tempDirFile = this.getTempDirectoryFile(PATH_OF_USERFILES_IN_ARCHIVE + name);
        if (tempDirFile != null && tempDirFile.exists()) {
            tempDirFile.delete();
        }

        if (this.userFiles == null) return;
        // Remove file if it already exists
        Iterator iter = this.userFiles.iterator();
        while (iter.hasNext()) {
            File theFile = (File)iter.next();
            if (theFile.getName().equals(name)) {
                this.userFiles.remove(theFile);
                break;
            }
        }

        // Clean up empty UserFiles folder
        if (this.userFiles.isEmpty()) {
            final File tempDirFolder = this.getTempDirectoryFile(PATH_OF_USERFILES_IN_ARCHIVE);
            if (tempDirFolder != null && tempDirFolder.exists() && tempDirFolder.isDirectory()) {
                tempDirFolder.delete();
            }
        }
    }

    /**
     * Get User file names
     * 
     * @since 5.3.3
     * @return the collection of user file names
     */
    public Collection getUserFileNames() {
        if (this.userFiles == null) return Collections.EMPTY_LIST;

        // Generate the list of names
        List nameList = new ArrayList(this.userFiles.size());
        Iterator iter = this.userFiles.iterator();
        while (iter.hasNext()) {
            File theFile = (File)iter.next();
            nameList.add(theFile.getName());
        }
        return nameList;
    }

    /**
     * Sets the userFiles list to the list of user files currently in the VDB
     * 
     * @since 5.3.3
     */
    private void loadUserFiles() throws IOException {
        if (this.vdbArchive != null) {
            this.userFiles = new ArrayList();
            final Enumeration iter = this.vdbArchive.entries();
            while (iter.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)iter.nextElement();
                String entryName = entry.getName();
                if (entryName != null && entryName.startsWith(PATH_OF_USERFILES_IN_ARCHIVE)) {
                    InputStream istream = this.vdbArchive.getInputStream(entry);
                    final File modelFile = this.addToTempDirectory(istream, entryName);
                    this.userFiles.add(modelFile);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
        this.changeListeners.add(theListener);
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        this.changeListeners.remove(theListener);
    }

    /**
     * Notify all registered {@link IChangeListener}s that the state has changed.
     * 
     * @since 4.3
     */
    public void fireStateChanged() {
        Object[] listeners = this.changeListeners.getListeners();

        for (int i = 0; i < listeners.length; ++i) {
            ((IChangeListener)listeners[i]).stateChanged(this);
        }
    }

    /**
     * Note: currently only supports vetoing when the context is closing.
     */
    public void addVetoableChangeListener( VetoableChangeListener listener ) {
        vetoListeners.add(listener);
    }

    public void removeVetoableChangeListener( VetoableChangeListener listener ) {
        vetoListeners.remove(listener);
    }

    /**
     * Notify all registered {@link VetoableChangeListener}s that the state has changed.
     * 
     * @since 5.0
     * @return true if no listeners vetoed the change
     */
    private boolean fireVetoableChange( String key,
                                        Object oldValue,
                                        Object newValue ) {
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

    public void rename( String newName ) throws Exception {
        if (this.vdbArchive != null) {
            this.vdbArchive.close();
        }
        boolean prevLoadModelsOnOpen = this.loadModelsOnOpen;
        this.loadModelsOnOpen = false;
        File newFile = new File(newName);
        if (newFile.exists()) {
            throw new IOException(VdbEditPlugin.Util.getString("VdbEditingContextImpl.exists", newName)); //$NON-NLS-1$
        }
        try {
            if (!this.vdbFilePath.toFile().renameTo(newFile)) {
                throw new IOException(VdbEditPlugin.Util.getString("VdbEditingContextImpl.noRename", newName)); //$NON-NLS-1$
            }
            this.vdbFilePath = new Path(newName);
        } finally {
            open();
            this.loadModelsOnOpen = prevLoadModelsOnOpen;
        }
    }

    public Properties getExecutionProperties() {
        if (executionProperties == null) {
            executionProperties = new Properties();
        }
        return this.executionProperties;
    }

    public void setExecutionProperty( String propertyName,
                                      String propertyValue ) {
        this.getExecutionProperties().setProperty(propertyName, propertyValue);
    }
}
