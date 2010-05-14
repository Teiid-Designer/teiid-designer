/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.internal.core.resource.FakeResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

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
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelType()
     */
    public ModelType getModelType() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getDescription()
     */
    public String getDescription() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getUuid()
     */
    public String getUuid() {
        return null;
    }

    public String getPrimaryMetamodelUri() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelAnnotation()
     */
    public ModelAnnotation getModelAnnotation() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelDiagrams()
     */
    public ModelDiagrams getModelDiagrams() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelTransformations()
     */
    public ModelTransformations getModelTransformations() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelMappingClassSets()
     */
    public ModelMappingClassSets getModelMappingClassSets() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#isLoaded()
     */
    public boolean isLoaded() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#unload()
     */
    public void unload() {

    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getAnnotations()
     */
    public ModelObjectAnnotations getAnnotations() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getEObjects()
     */
    public List getEObjects() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getAllRootEObjects()
     */
    public List getAllRootEObjects() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getEmfResource()
     */
    public Resource getEmfResource() {
        return this.resource;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getPrimaryMetamodelDescriptor()
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor() {
        return this.descriptor;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getAllMetamodelDescriptors()
     */
    public List getAllMetamodelDescriptors() {
        return Collections.singletonList(this.descriptor);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getModelImports()
     */
    public List getModelImports() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#close()
     */
    public void close() {

    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#hasUnsavedChanges()
     */
    public boolean hasUnsavedChanges() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#isOpen()
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void open( IProgressMonitor progress ) {

    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
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
     * @see com.metamatrix.modeler.core.workspace.ModelResource#isXsd()
     */
    public boolean isXsd() {
        return ModelUtil.isXsdFile(getEmfResource());
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getIndexType()
     */
    public int getIndexType() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#setIndexType(int)
     */
    public void setIndexType( int indexType ) {
    }

    public void refreshIndexType() {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getTargetNamespace()
     */
    public String getTargetNamespace() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#hasErrors()
     * @since 4.2
     */
    public boolean hasErrors() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getErrors()
     * @since 4.2
     */
    public IStatus getErrors() {
        return null;
    }

}
