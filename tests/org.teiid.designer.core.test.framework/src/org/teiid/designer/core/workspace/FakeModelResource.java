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
    @Override
	public ModelType getModelType() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getDescription()
     */
    @Override
	public String getDescription() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getUuid()
     */
    @Override
	public String getUuid() {
        return null;
    }

    @Override
	public String getPrimaryMetamodelUri() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelAnnotation()
     */
    @Override
	public ModelAnnotation getModelAnnotation() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelDiagrams()
     */
    @Override
	public ModelDiagrams getModelDiagrams() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelTransformations()
     */
    @Override
	public ModelTransformations getModelTransformations() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelMappingClassSets()
     */
    @Override
	public ModelMappingClassSets getModelMappingClassSets() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#isLoaded()
     */
    @Override
	public boolean isLoaded() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#unload()
     */
    @Override
	public void unload() {

    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getAnnotations()
     */
    @Override
	public ModelObjectAnnotations getAnnotations() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getEObjects()
     */
    @Override
	public List getEObjects() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getAllRootEObjects()
     */
    @Override
	public List getAllRootEObjects() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getEmfResource()
     */
    @Override
	public Resource getEmfResource() {
        return this.resource;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getPrimaryMetamodelDescriptor()
     */
    @Override
	public MetamodelDescriptor getPrimaryMetamodelDescriptor() {
        return this.descriptor;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getAllMetamodelDescriptors()
     */
    @Override
	public List getAllMetamodelDescriptors() {
        return Collections.singletonList(this.descriptor);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getModelImports()
     */
    @Override
	public List getModelImports() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#close()
     */
    @Override
	public void close() {

    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#hasUnsavedChanges()
     */
    @Override
	public boolean hasUnsavedChanges() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#isOpen()
     */
    @Override
	public boolean isOpen() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void open( IProgressMonitor progress ) {

    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    @Override
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
    @Override
	public boolean isXsd() {
        return ModelUtil.isXsdFile(getEmfResource());
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getIndexType()
     */
    @Override
	public int getIndexType() {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#setIndexType(int)
     */
    @Override
	public void setIndexType( int indexType ) {
    }

    @Override
	public void refreshIndexType() {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getTargetNamespace()
     */
    @Override
	public String getTargetNamespace() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#hasErrors()
     * @since 4.2
     */
    @Override
	public boolean hasErrors() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getErrors()
     * @since 4.2
     */
    @Override
	public IStatus getErrors() {
        return null;
    }

}
