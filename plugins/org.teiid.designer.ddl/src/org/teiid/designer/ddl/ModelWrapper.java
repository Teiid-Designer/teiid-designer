/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.workspace.ModelWorkspaceSelections;


/**
 * ModelWrapper
 *
 * @since 8.0
 */
public class ModelWrapper {

    private final Resource emfResource;
    private final ModelContents contents;
    private final String modelName;
    private final String modelFilename;
    private final ModelWorkspaceSelections selections;

    /**
     * Construct an instance of ModelWrapper.
     * @param emfResource the {@link org.teiid.designer.metamodels.relational.RelationalPackage relational} 
     * EMF resource that contains the model to be written out; may not be null
     * @param modelContents the ModelContents object to use; may not be null
     * @param modelName the name of the model in the resource
     * @param modelFilename the filename of the model resource
     */
    public ModelWrapper( final Resource emfResource, final ModelContents contents,
                         final ModelWorkspaceSelections selections,
                         final String modelName, final String modelFilename ) {
        CoreArgCheck.isNotNull(emfResource);
        this.emfResource = emfResource;
        this.selections = selections;
        this.contents = contents != null ? contents : 
                        this.emfResource instanceof EmfResource ? 
                                ((EmfResource)this.emfResource).getModelContents() :
                                new ModelContents(this.emfResource);
        this.modelName = modelName != null ? modelName : ""; //$NON-NLS-1$
        this.modelFilename = modelFilename != null ? modelFilename : ""; //$NON-NLS-1$
    }

    public ModelContents getContents() {
        return contents;
    }
    public Resource getEmfResource() {
        return emfResource;
    }
    /**
     * @return
     */
    public String getModelFilename() {
        return modelFilename;
    }

    /**
     * @return
     */
    public String getModelName() {
        return modelName;
    }
    
    public boolean isSelected( final EObject obj ) {
        if ( this.selections == null ) {
            return true;
        }
        final int mode = this.selections.getSelectionMode(obj);
        return ( mode == ModelWorkspaceSelections.SELECTED || mode == ModelWorkspaceSelections.PARTIALLY_SELECTED );
    }

}
