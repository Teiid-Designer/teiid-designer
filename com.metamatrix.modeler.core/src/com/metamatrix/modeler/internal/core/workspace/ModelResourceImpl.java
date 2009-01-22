/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Checksum;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.ChecksumUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.xmi.ModelImportInfo;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.internal.core.xml.xmi.XMIHeaderReader;
import com.metamatrix.internal.core.xml.xsd.XsdHeader;
import com.metamatrix.internal.core.xml.xsd.XsdHeaderReader;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.DuplicateResourceException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelBuffer;
import com.metamatrix.modeler.core.workspace.ModelDiagrams;
import com.metamatrix.modeler.core.workspace.ModelMappingClassSets;
import com.metamatrix.modeler.core.workspace.ModelObjectAnnotations;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelTransformations;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * ModelResourceImpl
 */
public class ModelResourceImpl extends OpenableImpl implements ModelResource {

    private int indexType;
    private XMIHeader xmiHeader;
    private XsdHeader xsdHeader;
    private ModelType mdlType = null;
    private IStatus errors = null;
    private long checkSumForIndexType = 0;
    private long fileSizeForIndexType = 0;

    /**
     * Constructor needed for test cases.
     */
    ModelResourceImpl() {
        super(MODEL_RESOURCE, null, null);
        this.xmiHeader = null;
        this.xsdHeader = null;
        this.setIndexType(NOT_INDEXED);
        ModelWorkspaceManager.getModelWorkspaceManager().setIndexType(NOT_INDEXED);
    }

    /**
     * Construct an instance of ModelProjectImpl.
     * 
     * @since 4.0
     */
    public ModelResourceImpl( final ModelWorkspaceItem parent,
                              final String name ) {
        this(parent, name, true);
    }

    /**
     * Construct an instance of ModelProjectImpl. Added boolean for ability to create non-pde tests
     * 
     * @since 4.0
     */
    ModelResourceImpl( final ModelWorkspaceItem parent,
                       final String name,
                       boolean createEmfResource ) {
        super(MODEL_RESOURCE, parent, name);
        if (createEmfResource) {
            try {
                IResource iResource = getResource();
                IPath path = iResource.getLocation();
                final URI uri = URI.createFileURI(path.toOSString());
                ResourceSet resourceSet = super.getBufferManager().getResourceSetFinder().getResourceSet(iResource);
                if (resourceSet.getResource(uri, false) == null) {
                    resourceSet.createResource(uri);
                }
            } catch (DuplicateResourceException dre) {
                ModelerCore.Util.log(dre);
            } catch (ModelWorkspaceException mwe) {
                ModelerCore.Util.log(mwe);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.workspace.OpenableImpl#createItemInfo()
     */
    @Override
    protected OpenableModelWorkspaceItemInfo createItemInfo() {
        return new ModelResourceInfo();
    }

    /**
     * Return true if an open buffer exists for this ModelResource
     */
    public boolean isLoaded() {
        return getBufferManager().getOpenBuffer(this) != null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#hasErrors()
     * @since 4.2
     */
    public boolean hasErrors() {
        if (this.errors == null) {
            try {
                getBuffer(); // may record errors
            } catch (ModelWorkspaceException err) {
                final Throwable nested = err.getException();
                if (!(nested instanceof DuplicateResourceException)) {
                    ModelerCore.Util.log(err);
                }
            }
        }
        if (this.errors == null || this.errors.isOK()) {
            return false;
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getErrors()
     * @since 4.2
     */
    public IStatus getErrors() {
        try {
            final ModelBuffer buffer = getBuffer();
            if (buffer != null) {
                return buffer.getErrors();
            }
        } catch (ModelWorkspaceException err) {
            final Throwable nested = err.getException();
            if (nested instanceof DuplicateResourceException) {
                return this.errors;
            }
            ModelerCore.Util.log(err);
        }
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
    }

    /**
     * Return true if the ModelResource represents an XSD Resource
     * 
     * @see com.metamatrix.modeler.core.workspace.ModelResource#isXsd()
     */
    public boolean isXsd() {
        return ModelUtil.isXsdFile(getResource());
    }

    /**
     * Returns the value of the '<em><b>Target Namespace</b></em>' attribute if the ModelResource represents an XSD resource
     * otherwise null is returned.
     * 
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getTargetNamespace()
     */
    public String getTargetNamespace() throws ModelWorkspaceException {
        String targetNamespace = null;

        // If the model resource has not been opened then retrieve the
        // description information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XsdHeader header = this.getXsdHeader();
            if (header != null) {
                targetNamespace = header.getTargetNamespaceURI();
            }
        }
        // The model resource is already open in the workspace so look for
        // the model annotation node
        else {
            final Resource resource = this.getEmfResource();
            if (resource instanceof XSDResourceImpl) {
                final XSDSchema xsdSchema = ((XSDResourceImpl)resource).getSchema();
                if (xsdSchema != null) {
                    targetNamespace = xsdSchema.getTargetNamespace();
                }
            }
        }
        return targetNamespace;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#unload()
     */
    public void unload() {
        final ModelBuffer modelBuffer = getBufferManager().getOpenBuffer(this);
        if (modelBuffer != null) {
            closeBuffer(null);
            // throw out the current buffer and cause the creation of a new one ...
            ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
            // modelBuffer.unload();
            this.xmiHeader = null;
            this.xsdHeader = null;
            this.mdlType = null;
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getIndexType()
     */
    public int getIndexType() {
        return this.indexType;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#refreshIndexType()
     */
    public void refreshIndexType() {
        try {
            final ModelBuffer buf = getBuffer();
            if (buf != null) {
                IResource iResource = getResource();
                if (iResource instanceof IFile) {
                    if (this.indexType == ModelResource.NOT_INDEXED) {
                        return;
                    }
                    // Compare the file sizes ...
                    boolean fileSizeDifference = false;
                    final IPath rawLocation = iResource.getRawLocation();
                    if (rawLocation != null) {
                        final File rawFile = new File(rawLocation.toString());
                        if (rawFile.exists()) {
                            long fileSize = rawFile.length();
                            if (fileSize != this.fileSizeForIndexType) {
                                this.fileSizeForIndexType = fileSize;
                                fileSizeDifference = true;
                            }
                        }
                    }

                    // Compare checksum sizes
                    boolean checksumDifference = false;
                    InputStream stream = null;
                    try {
                        stream = ((IFile)iResource).getContents();
                        final Checksum checksum = ChecksumUtil.computeChecksum(stream);
                        final long checksumValue = checksum.getValue();
                        if (checksumValue != this.checkSumForIndexType) {
                            this.checkSumForIndexType = checksumValue;
                            checksumDifference = true;
                        }
                    } catch (Exception e) {
                        // System.out.println("eeee> Marking ModelResource as NOT_INDEXED "+this.getResource().getFullPath());
                        this.indexType = ModelResource.NOT_INDEXED;
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException ignore) {
                            }
                        }
                    }

                    if (fileSizeDifference || checksumDifference) {
                        // System.out.println("====> Marking ModelResource as NOT_INDEXED "+this.getResource().getFullPath());
                        this.indexType = ModelResource.NOT_INDEXED;
                    }
                }
            }
        } catch (ModelWorkspaceException e) {
            // ModelerCore.Util.log(err); // okay to eat, since no buffer, nothing to unload/reload?
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#setIndexType(int)
     */
    public void setIndexType( int indexType ) {
        // if (indexType == ModelResource.NOT_INDEXED) {
        // System.out.println("----> Marking ModelResource as NOT_INDEXED "+this.getResource().getFullPath());
        // }
        this.indexType = indexType;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem
     */
    @Override
    public boolean isReadOnly() {
        if (this.isLoaded() && this.hasErrors()) {
            // If the model has errors, then consider as read only to turn off some functionality.
            // There are never errors if not opened ...
            return true;
        }
        return ModelUtil.isIResourceReadOnly(getResource());
    }

    /**
     * Returns true if this represents a source element. Openable source elements have an associated buffer created when they are
     * opened.
     */
    @Override
    protected boolean isSourceElement() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.OpenableImpl#generateInfos(com.metamatrix.modeler.internal.core.OpenableModelWorkspaceItemInfo, org.eclipse.core.runtime.IProgressMonitor, java.util.Map, org.eclipse.core.resources.IResource)
     */
    @Override
    protected boolean generateInfos( final OpenableModelWorkspaceItemInfo info,
                                     final IProgressMonitor pm,
                                     final Map newElements,
                                     final IResource underlyingResource ) throws ModelWorkspaceException {
        boolean validInfo = false;
        try {
            // put the info now, because getting the contents requires it
            ModelWorkspaceManager.getModelWorkspaceManager().putInfo(this, info);

            // generate structure
            validInfo = this.updateModelContents();
        } finally {
            if (!validInfo) {
                ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
            }
        }
        return validInfo;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ModelResource#getEObjects()
     */
    public List getEObjects() throws ModelWorkspaceException {
        // They want the objects, so force the buffer to be loaded ...
        final ModelBufferImpl buffer = (ModelBufferImpl)super.getBuffer();
        return (buffer.getModelContents() != null ? buffer.getModelContents().getEObjects() : Collections.EMPTY_LIST);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ModelResource#getEObjects()
     */
    public List getAllRootEObjects() throws ModelWorkspaceException {
        // They want the objects, so force the buffer to be loaded ...
        final ModelBufferImpl buffer = (ModelBufferImpl)super.getBuffer();
        return (buffer.getModelContents() != null ? buffer.getModelContents().getAllRootEObjects() : Collections.EMPTY_LIST);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ModelResource#getEmfResource()
     */
    public synchronized Resource getEmfResource() throws ModelWorkspaceException {
        final Resource resource = super.getBuffer().getEmfResource();
        Assertion.isNotNull(resource);
        return resource;
    }

    // /**
    // *
    // * @see IOpenable
    // */
    // public boolean isOpen() {
    // ModelBuffer buffer = getBufferManager().getOpenBuffer(this);
    // if (buffer == null) {
    // return false;
    // }
    //
    // return !buffer.isClosed();
    // }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem#getResource()
     */
    public IResource getResource() {
        final ModelWorkspaceItem parent = this.getParent();
        if (parent == null) {
            return null;
        }
        final IContainer parentResource = (IContainer)parent.getResource();
        final IPath path = new Path(this.getItemName());
        return parentResource.getFile(path);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ModelWorkspaceItem#getPath()
     */
    public IPath getPath() {
        final ModelWorkspaceItem parent = this.getParent();
        if (parent == null) {
            return null;
        }
        return parent.getPath().append(this.getItemName());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.workspace.OpenableImpl#hasBuffer()
     */
    @Override
    protected boolean hasBuffer() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.workspace.OpenableImpl#openBuffer(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected ModelBuffer openBuffer( final IProgressMonitor pm ) throws ModelWorkspaceException {
        this.opening = true;

        ModelBuffer buffer = null;
        try {
            // create buffer
            ModelBufferManager bufManager = getBufferManager();
            buffer = bufManager.createBuffer(this);
            if (buffer == null) return null;

            // Open the buffer
            if (buffer instanceof ModelBufferImpl) {
                final ModelBufferImpl theBuffer = (ModelBufferImpl)buffer;
                try {
                    theBuffer.open(pm);
                } catch (DuplicateResourceException err) {
                    throw new ModelWorkspaceException(err, err.getMessage());
                } finally {
                    this.errors = theBuffer.getErrors();
                }

                // Refresh the file (which may have been created in the open call)
                // theBuffer.refresh(pm);
                // DON'T REFRESH HERE, BECAUSE THIS IS CALLED WITHIN SYNCHRONIZED METHODS
                // AND THIS REFRESH CAUSES A NOTIFICATION AND ULTIMATELY A DEADLOCK.
            }

            // add buffer to buffer cache
            bufManager.addBuffer(buffer);

            // listen to buffer changes
            // buffer.addBufferChangedListener(this);
        } finally {
            this.opening = false;
        }

        return buffer;
    }

    /**
     * A model resource has a corresponding resource unless it is contained in a jar.
     * 
     * @see ModelWorkspaceItem#getCorrespondingResource()
     */
    @Override
    public IResource getCorrespondingResource() {
        // IPackageFragmentRoot root= (IPackageFragmentRoot)getParent().getParent();
        // if (root.isArchive()) {
        // return null;
        // } else {
        return getUnderlyingResource();
        // }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    @Override
    public IResource getUnderlyingResource() {
        return getResource();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelDiagramContainer()
     */
    public ModelDiagrams getModelDiagrams() throws ModelWorkspaceException {
        return (ModelDiagrams)this.getFirstChildrenOfType(DIAGRAMS);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelTransformations()
     */
    public ModelTransformations getModelTransformations() throws ModelWorkspaceException {
        return (ModelTransformations)this.getFirstChildrenOfType(TRANSFORMATIONS);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelTransformations()
     */
    public ModelObjectAnnotations getAnnotations() throws ModelWorkspaceException {
        return (ModelObjectAnnotations)this.getFirstChildrenOfType(ANNOTATIONS);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelMappingClassSets()
     */
    public ModelMappingClassSets getModelMappingClassSets() throws ModelWorkspaceException {
        return (ModelMappingClassSets)this.getFirstChildrenOfType(MAPPING_CLASS_SETS);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelType()
     */
    public ModelType getModelType() throws ModelWorkspaceException {
        if (this.mdlType != null) {
            return this.mdlType;
        }

        // If the model resource has not been opened then retrieve the
        // model type information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null && header.getModelType() != null) {
                this.mdlType = ModelType.get(header.getModelType());
            }
        }
        // The model resource is already open in the workspace so look for
        // the model annotation node
        else {
            final ModelAnnotation annotation = this.getModelAnnotation();
            this.mdlType = (annotation != null ? annotation.getModelType() : null);
            if (this.mdlType == ModelType.UNKNOWN_LITERAL) {
                this.mdlType = null; // don't cache ...
            }
        }
        // If the model type is unknown but this model is an XSD resource ...
        if (this.mdlType == null && this.isXsd()) {
            this.mdlType = ModelType.TYPE_LITERAL;
        }
        return (mdlType != null ? mdlType : ModelType.PHYSICAL_LITERAL);
    }

    /**
     * @since 4.2
     */
    public void setModelType( final ModelType type ) {
        this.mdlType = type;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getDescription()
     */
    public String getDescription() throws ModelWorkspaceException {
        String description = null;

        // If the model resource has not been opened then retrieve the
        // description information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null) {
                description = header.getDescription();
            }
        }
        // The model resource is already open in the workspace so look for
        // the model annotation node
        else {
            final ModelAnnotation annotation = this.getModelAnnotation();
            description = (annotation != null ? annotation.getDescription() : null);
        }
        return description;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getUuid()
     */
    public String getUuid() throws ModelWorkspaceException {
        String uuidString = null;

        // If the model resource has not been opened then retrieve the
        // description information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null) {
                uuidString = header.getUUID();
            }
        }
        // The model resource is already open in the workspace so look for
        // the model annotation node
        else {
            final ModelAnnotation annotation = this.getModelAnnotation();
            if (annotation != null) {
                String uuid = ModelerCore.getObjectIdString(annotation);
                if (uuid != null) {
                    return uuid;
                }
            }
        }
        return uuidString;
    }

    public String getPrimaryMetamodelUri() throws ModelWorkspaceException {
        String uriString = null;

        // If the model resource has not been opened then retrieve the
        // description information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null) {
                uriString = header.getPrimaryMetamodelURI();
            }
        }
        // The model resource is already open in the workspace so look for
        // the model annotation node
        else {
            final ModelAnnotation annotation = this.getModelAnnotation();
            if (annotation != null) {
                uriString = annotation.getPrimaryMetamodelUri();
            }
        }
        return uriString;
    }

    // /* (non-Javadoc)
    // * @see com.metamatrix.modeler.core.ModelResource#getModelImportContainer()
    // */
    // public ModelImports getModelImportContainer() throws ModelWorkspaceException {
    // return null;
    // }
    //
    // /* (non-Javadoc)
    // * @see com.metamatrix.modeler.core.ModelResource#getMetamodelImportContainer()
    // */
    // public MetamodelImports getMetamodelImportContainer() throws ModelWorkspaceException {
    // return null;
    // }

    // /* (non-Javadoc)
    // * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelAnnotation()
    // */
    // public ModelAnnotation getModelAnnotation() throws ModelWorkspaceException {
    // return getModelAnnotation(false);
    // }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelAnnotation()
     */
    public synchronized ModelAnnotation getModelAnnotation() throws ModelWorkspaceException {
        // This is either loaded with no errors or unloaded ...
        final ModelBufferImpl buffer = (ModelBufferImpl)getBufferHack();
        final ModelContents contents = buffer.getModelContents();
        return (contents != null ? contents.getModelAnnotation() : null);
    }

    /**
     * Convenience method that returns the specific type of info for a ModelResource.
     */
    protected ModelResourceInfo getModelResourceInfo() throws ModelWorkspaceException {
        return (ModelResourceInfo)getItemInfo();
    }

    /**
     * Reset the collection of package fragment roots (local ones) - only if opened. Need to check *all* package fragment roots in
     * order to reset NameLookup
     * 
     * @since 4.0
     */
    public boolean updateModelContents() throws ModelWorkspaceException {
        if (this.isOpen()) {
            boolean failed = false;
            try {
                ModelResourceInfo info = getModelResourceInfo();
                info.setChildren(computeModelContents());
                return true;
            } catch (ModelWorkspaceException e) {
                failed = true;
                throw e;
            } catch (RuntimeException e) {
                failed = true;
                throw e;
            } finally {
                if (failed) {
                    try {
                        close(); // could not do better
                    } catch (ModelWorkspaceException ex) {
                    }
                }
            }
        }

        return false;
    }

    public ModelWorkspaceItem[] computeModelContents() {
        if (ModelUtil.isXsdFile(this.getCorrespondingResource())) {
            return new ModelWorkspaceItem[] {};
        }

        // Create a ModelDiagrams ...
        final ModelDiagrams diagramContainer = new ModelDiagramsImpl(this);

        // Create a ModelTransformations ...
        final ModelTransformations transformationContainer = new ModelTransformationsImpl(this);

        // Create a ModelAnnotations ...
        final ModelObjectAnnotations annotationContainer = new ModelObjectAnnotationsImpl(this);

        // Create a ModelMappingClassSets ...
        final ModelMappingClassSets mcSets = new ModelMappingClassSetsImpl(this);

        // Return the children
        return new ModelWorkspaceItem[] {diagramContainer, transformationContainer, annotationContainer, mcSets};
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getPrimaryMetamodelDescriptor()
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor() throws ModelWorkspaceException {
        String primaryMetamodelUri = null;

        // defect 19183 - prevent undo stack filling when selecting XSD models.
        // ... mainly by checking XSDness right away, instead of after pulling
        // the header.
        if (ModelUtil.isXsdFile(getResource())) {
            primaryMetamodelUri = XSDPackage.eNS_URI;
        }
        // If the model resource has not been opened then retrieve the
        // metamodel information by reading the XMI header
        else if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null) {
                primaryMetamodelUri = header.getPrimaryMetamodelURI();
            }
        }
        // The model resource is already open in the workspace so look for
        // the model annotation root node
        else {
            final ModelAnnotation annotation = this.getModelAnnotation();
            primaryMetamodelUri = (annotation != null ? annotation.getPrimaryMetamodelUri() : null);
        }

        if (primaryMetamodelUri == null) {
            return null; // unknown metamodel
        }

        // MyDefect : 18566 just for reference.
        // Look up the descriptor in the metamodels ...
        final MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(primaryMetamodelUri);

        // Log an error if there is no metamodel with this primary metamodel URI. There may be
        // an inconsistency between the metamodel extenion URIs and the primary metamodel URI
        // in the resource.
        if (descriptor == null) {
            final StringBuffer sb = new StringBuffer();
            final MetamodelDescriptor[] descriptors = ModelerCore.getMetamodelRegistry().getMetamodelDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                final MetamodelDescriptor mmd = descriptors[i];
                if (mmd.isPrimary() && !StringUtil.isEmpty(mmd.getNamespaceURI())) {
                    sb.append(mmd.getNamespaceURI());
                    sb.append(StringUtil.Constants.SPACE);
                }
            } // for
            final Object[] params = new Object[] {primaryMetamodelUri, sb.toString()};
            final String msg = ModelerCore.Util.getString("ModelResourceImpl.no_metamodel_found_for_primary_metamodel_URI", params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, msg);
        }

        return descriptor;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getAllMetamodelDescriptors()
     */
    public List getAllMetamodelDescriptors() throws ModelWorkspaceException {

        // If the model resource has not been opened then retrieve the
        // metamodel information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null) {
                final String primaryMetamodelUri = header.getPrimaryMetamodelURI();
                final String[] namespaceURIs = header.getNamespaceURIs();
                final List result = new ArrayList(namespaceURIs.length);
                for (int i = 0; i < namespaceURIs.length; i++) {
                    final String nsUri = namespaceURIs[i];
                    final MetamodelDescriptor desc = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(nsUri);
                    if (desc != null) {
                        if (nsUri.equals(primaryMetamodelUri)) {
                            result.add(0, desc);
                        } else {
                            result.add(desc);
                        }
                    }
                }
                return result;
            }
        }
        // The model resource is already open in the workspace so look for
        // namespace URIs in the model annotation node
        else {
            final Resource resource = this.getEmfResource();
            if (resource instanceof EmfResource) {
                // Get the primary metamodel ...
                final ModelAnnotation annotation = this.getModelAnnotation();
                final String primaryMetamodelUri = (annotation != null ? annotation.getPrimaryMetamodelUri() : null);
                final List result = new ArrayList(9);
                final List nsPrefixToUri = ((EmfResource)resource).getNamespacePrefixToUris();
                final Iterator iter = nsPrefixToUri.iterator();
                while (iter.hasNext()) {
                    final String nsUri = (String)iter.next();
                    // Look up the descriptor in the metamodels ...
                    final MetamodelDescriptor desc = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(nsUri);
                    if (desc != null) {
                        if (nsUri.equals(primaryMetamodelUri)) {
                            result.add(0, desc);
                        } else {
                            result.add(desc);
                        }
                    }
                }
                return result;
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelImports()
     */
    public List getModelImports() throws ModelWorkspaceException {

        // If the model resource has not been opened then retrieve the
        // model import information by reading the XMI header
        if (!this.isResourceOpenAndLoaded()) {
            final XMIHeader header = this.getXmiHeader();
            if (header != null) {
                ModelImportInfo[] infos = header.getModelImportInfos();
                final List result = new ArrayList(infos.length);
                for (int i = 0; i < infos.length; i++) {
                    final ModelImport modelImport = CoreFactory.eINSTANCE.createModelImport();
                    modelImport.setModelType(ModelType.get(infos[i].getModelType()));
                    modelImport.setName(infos[i].getName());
                    modelImport.setPrimaryMetamodelUri(infos[i].getPrimaryMetamodelURI());
                    modelImport.setUuid(infos[i].getUUID());

                    String location = infos[i].getLocation();
                    String path = infos[i].getPath();
                    if (!StringUtil.isEmpty(location)) {
                        modelImport.setModelLocation(location);

                    } else if (!StringUtil.isEmpty(path)) {
                        if (WorkspaceResourceFinderUtil.isGlobalResource(path)) {
                            modelImport.setModelLocation(location);

                        } else {
                            IResource iResource = WorkspaceResourceFinderUtil.findIResource(path);
                            if (iResource != null) {
                                URI importURI = URI.createFileURI(iResource.getLocation().toFile().getAbsolutePath());
                                URI modelURI = URI.createFileURI(this.getUnderlyingResource().getLocation().toFile().getAbsolutePath());
                                if (importURI.isFile()) {
                                    boolean deresolve = (modelURI != null && !modelURI.isRelative() && modelURI.isHierarchical());
                                    if (deresolve && !importURI.isRelative()) {
                                        URI deresolvedURI = importURI.deresolve(modelURI, true, true, false);
                                        if (deresolvedURI.hasRelativePath()) {
                                            importURI = deresolvedURI;
                                        }
                                    }
                                    modelImport.setModelLocation(URI.decode(importURI.toString()));
                                }
                            }
                        }
                    }
                    result.add(modelImport);
                }
                return result;
            }
        }
        // The model resource is already open in the workspace so retrieve
        // model imports from the model annotation node
        else {
            final Resource resource = this.getEmfResource();
            if (resource instanceof EmfResource) {
                // Get the primary metamodel ...
                final ModelAnnotation annotation = this.getModelAnnotation();
                return annotation.getModelImports();
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see IParent
     */
    @Override
    public boolean hasChildren() {
        return true;
    }

    private XMIHeader getXmiHeader() throws ModelWorkspaceException {
        if (this.xmiHeader == null) {
            try {
                final IPath modelPath = this.getUnderlyingResource().getLocation();
                final File modelFile = new File(modelPath.toOSString());
                if (modelFile.exists()) {
                    this.xmiHeader = XMIHeaderReader.readHeader(modelFile);
                }
            } catch (MetaMatrixCoreException e) {
                // do nothing
            } catch (Throwable e) {
                throw new ModelWorkspaceException(
                                                  e,
                                                  ModelerCore.Util.getString("ModelResourceImpl.Error_reading_XMI_header_from_resource_1", this.getUnderlyingResource())); //$NON-NLS-1$
            }
        }
        return this.xmiHeader;
    }

    private XsdHeader getXsdHeader() throws ModelWorkspaceException {
        if (this.xsdHeader == null) {
            try {
                final IPath modelPath = this.getUnderlyingResource().getLocation();
                final File modelFile = new File(modelPath.toOSString());
                if (modelFile.exists() && ModelUtil.isXsdFile(modelFile)) {
                    this.xsdHeader = XsdHeaderReader.readHeader(modelFile);
                }
            } catch (MetaMatrixCoreException e) {
                // do nothing
            } catch (Throwable e) {
                throw new ModelWorkspaceException(
                                                  e,
                                                  ModelerCore.Util.getString("ModelResourceImpl.Error_reading_Xsd_header_from_resource_1", this.getUnderlyingResource())); //$NON-NLS-1$
            }
        }
        return this.xsdHeader;
    }

    private boolean isResourceOpenAndLoaded() {
        return (!this.isOpening() && this.isOpen() && this.isLoaded());
    }

    /**
     * Process the contents change notification to see whether the EMF resource needs to be unloaded. This method looks at the old
     * and new
     * 
     * @param notification
     * @since 4.2
     */
    protected boolean processContentsChange( final ModelWorkspaceNotification notification ) {
        boolean reloaded = false;
        try {
            final ModelBuffer buf = getBuffer();
            if (buf != null && !buf.isClosed()) {
                // Found a buffer, so it might be loaded
                final IResourceDelta delta = notification.getDelta();
                final IResource resource = delta.getResource();
                if (resource instanceof IFile) {
                    final long newModStamp = resource.getModificationStamp();
                    final long lastModStamp = buf.getLastModificationStamp();
                    final boolean bufferIsInProcessOfSaving = buf.isInProcessOfSaving();
                    if (newModStamp != ModelBuffer.INITIAL_MOD_STAMP && newModStamp != lastModStamp && !bufferIsInProcessOfSaving) {
                        // The file has been changed on the file system by something other than this ModelBuffer ...

                        // Check the file size ...
                        final IPath rawLocation = resource.getRawLocation();
                        long fileSize = 0;
                        if (rawLocation != null) {
                            final File rawFile = new File(rawLocation.toString());
                            if (rawFile.exists()) {
                                fileSize = rawFile.length();
                            }
                        }
                        boolean askToReload = true;
                        if (fileSize == buf.getLastFileSize()) {
                            // The file sizes are the same, so compare the checksum ...
                            InputStream stream = null;
                            try {
                                stream = ((IFile)resource).getContents();
                                final Checksum checksum = ChecksumUtil.computeChecksum(stream);
                                final long checksumValue = checksum.getValue();
                                if (checksumValue == buf.getLastChecksum()) {
                                    // The size is the same and the checksums are the same, so assume no need to reload ...
                                    // (see defect 15065)
                                    askToReload = false;
                                }
                            } catch (CoreException err1) {
                                // don't log, but treat as files are different ...
                                askToReload = true;
                            } catch (IOException err1) {
                                // don't log, but treat as files are different ...
                                askToReload = true;
                            } finally {
                                if (stream != null) {
                                    try {
                                        stream.close();
                                    } catch (IOException ignore) {
                                    }
                                }
                            }
                        }

                        if (askToReload) {
                            setIndexType(ModelResource.NOT_INDEXED);

                            final boolean reload = ModelWorkspaceManager.getModelWorkspaceManager().canReload(this);
                            if (reload) {
                                // throw out the current buffer and cause the creation of a new one ...
                                this.unload();
                                // Get a new buffer and cause it to be opened ...
                                this.getBufferHack();
                                reloaded = true;
                            }
                        }
                    }
                }
            }
        } catch (ModelWorkspaceException err) {
            // ModelerCore.Util.log(err); // okay to eat, since no buffer, nothing to unload/reload?
        }
        return reloaded;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     * @since 5.0
     */
    @Override
    public void save( final IProgressMonitor pm,
                      final boolean force ) throws ModelWorkspaceException {
        super.save(pm, force);
        refreshIndexType();
    }

    @Override
    public synchronized void close() throws ModelWorkspaceException {
        super.close();

        // we only want to remove the emf resource from the resourceset
        // (container) when it's containing model project is closing
        if (getModelProject().isClosing()) {
            removeEmfResource();
        }
    }

    public synchronized void removeEmfResource() {
        IResource iResource = getResource();
        if (iResource != null) {
            IPath path = iResource.getLocation();
            if (path != null) {
                final URI uri = URI.createFileURI(path.toString());
                try {
                    ResourceSet resourceSet = ModelerCore.getModelContainer();
                    // If, in the future, we support multiple containers in the workspace, we can use the resource set finder to
                    // locate the appropriate resource set
                    // ResourceSet resourceSet = super.getBufferManager().getResourceSetFinder().getResourceSet(iResource);
                    Resource resource = resourceSet.getResource(uri, false);
                    if (resource != null) {
                        if (resource.isLoaded()) {
                            resource.unload();
                        }
                        resourceSet.getResources().remove(resource);
                    }
                } catch (CoreException err) {
                    ModelerCore.Util.log(err);
                }
            }
        }
    }
}
