/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.resource.FakeResource;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;


/**
 * FakeModelResource
 */
public class FakeModelResource extends FakeModelWorkspaceItem implements ModelResource {

    private final MetamodelDescriptor descriptor;
    private final Resource resource;

    /**
     * Construct an instance of FakeModelResource.
     */
    public FakeModelResource( final String path ) {
        this(path, null);
    }

    /**
     * Construct an instance of FakeModelResource.
     */
    public FakeModelResource( final String path,
                              final MetamodelDescriptor descriptor ) {
        super(ModelWorkspaceItem.MODEL_RESOURCE, path);
        this.descriptor = descriptor;
        this.resource = new FakeResource(path);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelType()
     */
    public ModelType getModelType() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getDescription()
     */
    public String getDescription() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getUuid()
     */
    public String getUuid() {
        return null;
    }

    public String getPrimaryMetamodelUri() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelAnnotation()
     */
    public ModelAnnotation getModelAnnotation() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelDiagrams()
     */
    public ModelDiagrams getModelDiagrams() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelTransformations()
     */
    public ModelTransformations getModelTransformations() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelMappingClassSets()
     */
    public ModelMappingClassSets getModelMappingClassSets() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#isLoaded()
     */
    public boolean isLoaded() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#unload()
     */
    public void unload() {

    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getAnnotations()
     */
    public ModelObjectAnnotations getAnnotations() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getEObjects()
     */
    public List getEObjects() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getAllRootEObjects()
     */
    public List getAllRootEObjects() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getEmfResource()
     */
    public Resource getEmfResource() {
        return this.resource;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getPrimaryMetamodelDescriptor()
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor() {
        return this.descriptor;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getAllMetamodelDescriptors()
     */
    public List getAllMetamodelDescriptors() {
        return Collections.singletonList(this.descriptor);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelImports()
     */
    public List getModelImports() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#close()
     */
    public void close() {

    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#hasUnsavedChanges()
     */
    public boolean hasUnsavedChanges() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#isOpen()
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void open( IProgressMonitor progress ) {

    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    public void save( IProgressMonitor progress,
                      boolean force ) {

    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#isXsd()
     */
    public boolean isXsd() {
        return ModelUtil.isXsdFile(getEmfResource());
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getIndexType()
     */
    public int getIndexType() {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#setIndexType(int)
     */
    public void setIndexType( int indexType ) {
    }

    public void refreshIndexType() {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getTargetNamespace()
     */
    public String getTargetNamespace() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#hasErrors()
     * @since 4.2
     */
    public boolean hasErrors() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getErrors()
     * @since 4.2
     */
    public IStatus getErrors() {
        return null;
    }

}
