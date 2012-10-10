/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.model;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.util.TransformationHelper;


/**
 * Class provides building EMF Relational Metamodel objects from Relational Model objects
 *
 * @since 8.0
 */
public class RelationalViewModelFactory extends RelationalModelFactory {

    public RelationalViewModelFactory() {
        super();
    }
    
    /**
     * Creates a relational view model given a <code>IPath</code> location and a model name
     * 
     * @param location
     * @param modelName
     * @return
     * @throws ModelWorkspaceException
     */
    public ModelResource createRelationalViewModel( IPath location, String modelName) throws ModelWorkspaceException {
        ModelWorkspaceItem mwItem = null;
        if( location.segmentCount() == 1 ) {
        	// Project for ONE segment
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.PROJECT);
        } else {
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.FOLDER);
        }
        
        IProject project = mwItem.getResource().getProject();
        IPath relativeModelPath = mwItem.getPath().removeFirstSegments(1).append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
        ModelerCore.getModelEditor().getAllContainers(resrc.getEmfResource());
        
        return resrc;
    }
    
    /**
     * Creates a relational view model given a <code>IContainer</code> location (Project or Folder) and a model name
     * 
     * @param container
     * @param modelName
     * @return
     * @throws ModelWorkspaceException
     */
    public ModelResource createRelationalViewModel( IContainer container, String modelName) throws ModelWorkspaceException {
        IProject project = container.getProject();
        IPath relativeModelPath = container.getFullPath().removeFirstSegments(1).append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
        ModelerCore.getModelEditor().getAllContainers(resrc.getEmfResource());
        
        return resrc;
    }

    @Override
	public void build( ModelResource modelResource,
                       RelationalModel model,
                       IProgressMonitor progressMonitor ) {

        try {
            RelationalViewModelFactory builder = new RelationalViewModelFactory();

            builder.buildFullModel(model, modelResource, progressMonitor);

            modelResource.save(new NullProgressMonitor(), true);
        } catch (ModelerCoreException e) {
            RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    @Override
	public EObject buildObject( RelationalReference obj,
                                ModelResource modelResource,
                                IProgressMonitor progressMonitor ) throws ModelWorkspaceException {
        EObject newEObject = null;

        String msg = TransformationPlugin.Util.getString("RelationalViewModelFactory.relationalModelFactory_creatingModelChild", obj.getName()); //$NON-NLS-1$

        progressMonitor.setTaskName(msg);
        switch (obj.getType()) {
            case TYPES.MODEL: {
                // NOOP. Shouldn't get here
            }
                break;
            case TYPES.SCHEMA: {
                // NOOP. Shouldn't get here
            }
                break;
            case TYPES.CATALOG: {
                // NOOP. Shouldn't get here
            }
                break;
            case TYPES.TABLE: {
                // Create the Table
                EObject baseTable = createBaseTable(obj, modelResource);
                modelResource.getEmfResource().getContents().add(baseTable);

                // Set the transformation SQL
                RelationalViewTable viewTable = (RelationalViewTable)obj;
                TransformationHelper.createTransformation(baseTable, viewTable.getTransformationSQL());
            }
                break;
            case TYPES.VIEW: {
                // Create the View
                EObject view = createView(obj, modelResource);
                modelResource.getEmfResource().getContents().add(view);

                // Set the transformation SQL
                RelationalViewView viewView = (RelationalViewView)obj;
                TransformationHelper.createTransformation(view, viewView.getTransformationSQL());
            }
                break;
            case TYPES.PROCEDURE: {
                EObject procedure = createProcedure(obj, modelResource);
                modelResource.getEmfResource().getContents().add(procedure);

                // Set the transformation SQL
                RelationalViewProcedure viewProc = (RelationalViewProcedure)obj;
                TransformationHelper.createTransformation(procedure, viewProc.getTransformationSQL());
            }
                break;
            case TYPES.INDEX: {
                // NOOP. Shouldn't get here
            }
                break;

            case TYPES.UNDEFINED:
            default: {
                RelationalPlugin.Util.log(IStatus.WARNING,
                                          NLS.bind(Messages.relationalModelFactory_unknown_object_type_0_cannot_be_processed,
                                                   obj.getName()));
            }
                break;
        }

        return newEObject;
    }

}
