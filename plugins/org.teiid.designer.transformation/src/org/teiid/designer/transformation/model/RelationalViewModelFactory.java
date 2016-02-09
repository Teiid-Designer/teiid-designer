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
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionAssistant;
import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.relational.model.RelationalViewTable;
import org.teiid.designer.transformation.Messages;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.util.TransformationHelper;


/**
 * Class provides building EMF Relational Metamodel objects from Relational Model objects
 *
 * @since 8.0
 */
public class RelationalViewModelFactory extends RelationalModelFactory {

    /**
     * 
     */
    public RelationalViewModelFactory() {
        super();
    }
    
    /**
     * Creates a relational view model given a <code>IPath</code> location and a model name
     * 
     * @param location the workspace location of the new model
     * @param modelName the model name
     * @return the new model resource
     * @throws ModelWorkspaceException error thrown when problem creating new model
     */
    public ModelResource createRelationalViewModel( IPath location, String modelName) throws ModelWorkspaceException {
    	final ModelResource resrc = ModelerCore.createModelResource(location, modelName);
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
        ModelerCore.getModelEditor().getAllContainers(resrc.getEmfResource());
        
        return resrc;
    }
    
    /**
     * Creates a relational view model given a <code>IContainer</code> location (Project or Folder) and a model name
     * 
     * @param container the workspace container of the new model
     * @param modelName  the model name
     * @return the new model resource
     * @throws ModelWorkspaceException  error thrown when problem creating new model
     */
    public ModelResource createRelationalViewModel( IContainer container, String modelName) throws ModelWorkspaceException {
        IProject project = container.getProject();
        String actualModelName = modelName;
        if( !modelName.toLowerCase().endsWith(XMI_EXT)) {
        	actualModelName = modelName + XMI_EXT;
        }
        IPath relativeModelPath = container.getFullPath().removeFirstSegments(1).append(actualModelName);
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
            buildFullModel(model, modelResource, progressMonitor);

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
            	throw new UnsupportedOperationException(Messages.virtualSchemaUnsupportedMessage);
            }
            case TYPES.CATALOG: {
            	throw new UnsupportedOperationException(Messages.virtualCatalogUnsupportedMessage);
            }
            case TYPES.TABLE: {
                // Create the Table
                EObject baseTable = createBaseTable(obj, modelResource);
                modelResource.getEmfResource().getContents().add(baseTable);
                applyTableExtensionProperties((RelationalTable)obj, (BaseTable)baseTable, true);

                // Set the transformation SQL
                RelationalViewTable viewTable = (RelationalViewTable)obj;
                TransformationHelper.createTransformation(baseTable, viewTable.getTransformationSQL());
            }
                break;
            case TYPES.VIEW: {
                throw new UnsupportedOperationException(Messages.virtualViewUnsupportedMessage);
            }
            case TYPES.PROCEDURE: {
                EObject procedure = createProcedure(obj, modelResource);
                modelResource.getEmfResource().getContents().add(procedure);

                // Set the transformation SQL
                RelationalViewProcedure viewProc = (RelationalViewProcedure)obj;
                TransformationHelper.createTransformation(procedure, viewProc.getTransformationSQL());
                
                applyProcedureExtensionProperties((RelationalProcedure)obj,(Procedure) procedure);
            }
                break;
            case TYPES.INDEX: {
                super.buildObject(obj, modelResource, progressMonitor);
            }
                break;

            case TYPES.UNDEFINED:
            default: {
                RelationalPlugin.Util.log(IStatus.WARNING,
                                          NLS.bind(org.teiid.designer.relational.Messages.relationalModelFactory_unknown_object_type_0_cannot_be_processed,
                                                   obj.getName()));
            }
                break;
        }

        return newEObject;
    }
    
    @Override
	protected void applyTableExtensionProperties(RelationalTable tableRef, BaseTable baseTable, boolean isVirtual) {
    	
        // Set Extension Properties here
        final RelationalModelExtensionAssistant assistant = getExtensionAssistant();
        if( assistant != null ) {
        	try {
				if( ((RelationalViewTable)tableRef).isGlobalTempTable() ) {
					assistant.setPropertyValue(baseTable, 
							BASE_TABLE_EXT_PROPERTIES.VIEW_TABLE_GLOBAL_TEMP_TABLE, 
							Boolean.toString(true) );
        		}
			} catch (Exception ex) {
				RelationalPlugin.Util.log(IStatus.ERROR, ex, 
	                	NLS.bind(org.teiid.designer.relational.Messages.relationalModelFactory_error_setting_extension_props_on_0, tableRef.getName()));
			}
        }
    }

	@Override
	protected void applyProcedureExtensionProperties(
			RelationalProcedure procedureRef, Procedure procedure) {
		RelationalViewProcedure viewProcedure = (RelationalViewProcedure)procedureRef;
		if( !viewProcedure.isFunction() ) {
			RestModelExtensionAssistant.setRestProperties(
				procedure, viewProcedure.getRestMethod(), viewProcedure.getRestUri(),  viewProcedure.getRestCharSet(),  viewProcedure.getRestHeaders(), viewProcedure.getRestDescription());
		}
		super.applyProcedureExtensionProperties(procedureRef, procedure);
	}
	
	public void addTransformation(BaseTable baseTable, RelationalViewTable viewTable) {
        // Set the transformation SQL
        TransformationHelper.createTransformation(baseTable, viewTable.getTransformationSQL());
	}

}
