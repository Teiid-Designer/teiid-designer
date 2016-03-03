/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.util.ModelObjectCollector;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.function.FunctionParameter;
import org.teiid.designer.metamodels.function.ReturnParameter;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.transformation.SqlAlias;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.editors.TransformationObjectEditorPage;
import org.teiid.designer.transformation.util.SqlMappingRootCache;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.editors.ModelObjectEditorPage;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * Implementation of {@link IModelNotificationHandler} to handle notifications for changes in function model objects.
 * 
 * In particular, if a function method (name) changes, we'd like to invalidate the status for any transformation SQL
 * that uses that method name, then perform the SQL rename operation.
 * 
 * Also, if a function model is reloaded/changed or removed we'd like to invalidate any Transformation status objects
 * for models dependent on that function model.
 * 
 *
 * @since 8.0
 */
public class FunctionModelNotificationHandler implements IModelNotificationHandler, UiConstants {
    
	private boolean isFunctionModelObject( Object obj) {
    	if( obj instanceof ScalarFunction || 
    		   obj instanceof FunctionParameter || 
    		   obj instanceof ReturnParameter ) {
    		return true;
    	} else if( obj instanceof Procedure ) {
    		return ((Procedure)obj).isFunction();
    	} else if(obj instanceof ProcedureParameter) {
    		return ((ProcedureParameter)obj).getProcedure().isFunction();
    	}
    	return false;
    }
	
    /* 
     * Get all Function Model object change Notifications.
     * @param notifications the collection of all notifications
     * @return the Model Rename Notifications
     */
    private Collection getModelChangeNotifications( Collection notifications,
                                                    Object source ) {
        Collection result = null;
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if (NotificationUtilities.isRemoved(notification) ||
            		NotificationUtilities.isAdded(notification) ||
            		NotificationUtilities.isChanged(notification) ) {
                // Get the object that was changed
                Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
                // changed object -
                if (isFunctionModelObject(changedObj) ) {
                	if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
                } else if( changedObj != null && changedObj instanceof EmfResource) {
                	ModelResource mr = ModelUtilities.getModelResource((Resource)changedObj, false);
                	if( ModelIdentifier.isFunctionModel(mr) ) {
                		if (result == null) {
                            result = new ArrayList(notifications.size());
                        }
                        result.add(notification);
                        // Remove from notifications collection
                        iter.remove();
                	}
                }
            }
        }
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }
	
    private void handleModelChangeNotification(Collection notifications, Object txnSource) {
    	Set<ModelResource> changedMRs = new HashSet<ModelResource>();
		
    	Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            if( NotificationUtilities.isAdded(notification) || NotificationUtilities.isRemoved(notification)) { 
            	// Get all Transformation Roots for the resource and clean out the cache for ALL of them
            	Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            		ModelResource changedModelResource = null;
            		
            		
	            	if( changedObj instanceof ScalarFunction ) {
	            		EObject eObject = (EObject)changedObj;
            			changedModelResource = ModelUtilities.getModelResource(eObject);
            			// Get the dependent resources
	            	} else if( changedObj instanceof EmfResource ) {
	            		ModelResource mr = ModelUtilities.getModelResource((Resource)changedObj, false);
	                	if( ModelIdentifier.isFunctionModel(mr) || isSourceModelWithPushdownFunction(mr) ) {
	                		changedModelResource = mr;
	                	}
	            	}
	            	
	            	if( changedModelResource != null ) {
            			Set<ModelResource> affectedDependentModelResources = new HashSet<ModelResource>();
                		
                    	affectedDependentModelResources.addAll(ModelUtilities.getResourcesThatUse(changedModelResource));
                    	
            			try {
							// Get all transformations for each dependent resource and do a string replace on the SQL for
							// the changed function
							for( ModelResource modelResource : affectedDependentModelResources ) {
								final EmfResource emfRes = (EmfResource)modelResource.getEmfResource();
								
								final List transformations = emfRes.getModelContents().getTransformations();
								for (Iterator i = transformations.iterator(); i.hasNext();) {
								    EObject eObj = (EObject)i.next();
								    if (eObj instanceof SqlTransformationMappingRoot) {
								        SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)eObj;
								        //System.out.println(" >> FunctionModelNotificationHandler.handleModelChanged()  Invalidating Status for Object " + mappingRoot.getTarget());
								        SqlMappingRootCache.invalidateStatus(mappingRoot, true, txnSource);
								    }
								}
								
								rebuildImportsInTransaction(modelResource.getEmfResource());
							}
						} catch (ModelWorkspaceException ex) {
							Util.log(IStatus.ERROR, ex, ex.getMessage());
						}
	            	}
            } else if (NotificationUtilities.isChanged(notification)) {
            	// Get all Transformation Roots for the resource and clean out the cache for ALL of them
            	Object changedObj = ModelerCore.getModelEditor().getChangedObject(notification);
            	boolean dependentModelChanged = false;
            	ModelResource changedModelResource = null;
            	
            	try {
	            	if( changedObj instanceof EObject && isFunction((EObject)changedObj)) {
	            		EObject eObject = (EObject)changedObj;
	            		final EStructuralFeature nameFeature = ModelerCore.getModelEditor().getNameFeature(eObject);
	            		// check that the change was to the "name" property
	            		if( notification.getFeature().equals(nameFeature) ) {
	            			// Get old name and new name
	            			String oldName = notification.getOldStringValue();
	            			String newName = notification.getNewStringValue();
	            			// Get the model resource
	            			changedModelResource = ModelUtilities.getModelResource(changedObj);
	            			// Get the dependent resources
	            			Set<ModelResource> affectedDependentModelResources = new HashSet<ModelResource>();
	                		
	                    	affectedDependentModelResources.addAll(ModelUtilities.getResourcesThatUse(changedModelResource));
	                    	
	            			// Get all transformations for each dependent resource and do a string replace on the SQL for
	            			// the changed function
	                    	for( ModelResource modelResource : affectedDependentModelResources ) {
	                    		final EmfResource emfRes = (EmfResource)modelResource.getEmfResource();
	                    		ModelEditor mEditor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), false);
	                    		
	        					final List transformations = emfRes.getModelContents().getTransformations();
	        					for (Iterator i = transformations.iterator(); i.hasNext();) {
	        					    EObject eObj = (EObject)i.next();
	        					    if (eObj instanceof SqlTransformationMappingRoot) {
	        					        SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)eObj;

	        					        String sql = TransformationHelper.getSelectSqlString(mappingRoot);
	        					        boolean statusWasInvalidated = false;
	        					        if( sql != null && (sql.contains(oldName) || sql.contains(oldName.toUpperCase()) )  ) {
	        					        	String changedSql = null;
	        					        	
	        					        	if( sql.contains(oldName) ) {
	        					        		changedSql = sql.replaceAll(oldName, newName);
	        					        	} else {
	        					        		changedSql = sql.replaceAll(oldName.toUpperCase(), newName.toUpperCase());
	        					        	}
	        					        	
	        					        	statusWasInvalidated = true;
	        					        	SqlMappingRootCache.invalidateStatus(mappingRoot, true, txnSource);
	        					        	
	        					        	boolean handled = false;
	        					        	
	        		                        if (mEditor != null) {
	        		                            // Yes, check to see if dirty:
	        		                            ModelObjectEditorPage moep = mEditor.getActiveObjectEditor();
	        		                            if (moep != null && moep instanceof TransformationObjectEditorPage) {
	        		                                TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
	        		                                handled = updateSqlEditorPanel(transOEP, QueryValidator.SELECT_TRNS, mappingRoot, changedSql);
	        		                            }
	        		                        }
	        		                        if( !handled ) {
	        		                        	dependentModelChanged = true;
	        		                        	updateSqlInTransaction(mappingRoot, QueryValidator.SELECT_TRNS, changedSql, NOT_SIGNIFICANT, txnSource);
	        		                        }
	        					        }
	        					        
	        					        sql = TransformationHelper.getInsertSqlString(mappingRoot);
	        					        if( sql != null && (sql.contains(oldName) || sql.contains(oldName.toUpperCase()) )  ) {
	        					        	String changedSql = null;
	        					        	
	        					        	if( sql.contains(oldName) ) {
	        					        		changedSql = sql.replaceAll(oldName, newName);
	        					        	} else {
	        					        		changedSql = sql.replaceAll(oldName.toUpperCase(), newName.toUpperCase());
	        					        	}

	        					        	if( !statusWasInvalidated ) {
	        					        		SqlMappingRootCache.invalidateStatus(mappingRoot, true, txnSource);
	        					        	}
	        					        	
	        					        	boolean handled = false;
	        					        	
	        		                        if (mEditor != null) {
	        		                            // Yes, check to see if dirty:
	        		                            ModelObjectEditorPage moep = mEditor.getActiveObjectEditor();
	        		                            if (moep != null && moep instanceof TransformationObjectEditorPage) {
	        		                                TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
	        		                                handled = updateSqlEditorPanel(transOEP, QueryValidator.INSERT_TRNS, mappingRoot, changedSql);
	        		                            }
	        		                        }
	        		                        if( !handled ) {
	        		                        	dependentModelChanged = true;
	        		                        	updateSqlInTransaction(mappingRoot, QueryValidator.INSERT_TRNS, changedSql, NOT_SIGNIFICANT, txnSource);
	        		                        }
	        					        }
	        					        
	        					        sql = TransformationHelper.getUpdateSqlString(mappingRoot);
	        					        if( sql != null && (sql.contains(oldName) || sql.contains(oldName.toUpperCase()) )  ) {
	        					        	String changedSql = null;
	        					        	
	        					        	if( sql.contains(oldName) ) {
	        					        		changedSql = sql.replaceAll(oldName, newName);
	        					        	} else {
	        					        		changedSql = sql.replaceAll(oldName.toUpperCase(), newName.toUpperCase());
	        					        	}
	        					        	
	        					        	if( !statusWasInvalidated ) {
	        					        		SqlMappingRootCache.invalidateStatus(mappingRoot, true, txnSource);
	        					        	}

	        					        	boolean handled = false;
	        					        	
	        		                        if (mEditor != null) {
	        		                            // Yes, check to see if dirty:
	        		                            ModelObjectEditorPage moep = mEditor.getActiveObjectEditor();
	        		                            if (moep != null && moep instanceof TransformationObjectEditorPage) {
	        		                                TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
	        		                                handled = updateSqlEditorPanel(transOEP, QueryValidator.UPDATE_TRNS, mappingRoot, changedSql);
	        		                            }
	        		                        }
	        		                        if( !handled ) {
	        		                        	dependentModelChanged = true;
	        		                        	updateSqlInTransaction(mappingRoot, QueryValidator.UPDATE_TRNS, changedSql, NOT_SIGNIFICANT, txnSource);
	        		                        }
	        					        }
	        					        
	        					        sql = TransformationHelper.getDeleteSqlString(mappingRoot);
	        					        if( sql != null && (sql.contains(oldName) || sql.contains(oldName.toUpperCase()) )  ) {
	        					        	String changedSql = null;
	        					        	
	        					        	if( sql.contains(oldName) ) {
	        					        		changedSql = sql.replaceAll(oldName, newName);
	        					        	} else {
	        					        		changedSql = sql.replaceAll(oldName.toUpperCase(), newName.toUpperCase());
	        					        	}

	        					        	if( !statusWasInvalidated ) {
	        					        		SqlMappingRootCache.invalidateStatus(mappingRoot, true, txnSource);
	        					        	}
	        					        	
	        					        	boolean handled = false;
	        					        	
	        		                        if (mEditor != null) {
	        		                            // Yes, check to see if dirty:
	        		                            ModelObjectEditorPage moep = mEditor.getActiveObjectEditor();
	        		                            if (moep != null && moep instanceof TransformationObjectEditorPage) {
	        		                                TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
	        		                                handled = updateSqlEditorPanel(transOEP, QueryValidator.DELETE_TRNS, mappingRoot, changedSql);
	        		                            }
	        		                        }
	        		                        if( !handled ) {
	        		                        	dependentModelChanged = true;
	        		                        	updateSqlInTransaction(mappingRoot, QueryValidator.DELETE_TRNS, changedSql, NOT_SIGNIFICANT, txnSource);
	        		                        }
	        					        }
	        					        
	        					        if( dependentModelChanged ) {
		        					    	// Check the SQL Alias
		        					    	Collection<EObject> sqlAliases = ((SqlTransformation)mappingRoot.getHelper()).getAliases();
		        					    	SqlAlias oldAlias = null;
		        					    	for( EObject sqlAlias : sqlAliases ) {
		        					    		if( sqlAlias instanceof SqlAlias ) {
		        					    			if( ((SqlAlias)sqlAlias).getAlias().equalsIgnoreCase(oldName)) {
		        					    				oldAlias = (SqlAlias)sqlAlias;
		        					    				break;
		        					    			}
		        					    		}
		        					    	}
		        					    	if( oldAlias != null ) {
		        					    		swapAliasInTransaction(mappingRoot, oldAlias, newName);
		        					    	}
		        					    }
	        					    }	   
	        					}
	        	            	
	        	            	if( dependentModelChanged) {
	        	            		changedMRs.add(modelResource);
	        	            	}
	                    	}
	            			
	            		}
	            	}
            	} catch (ModelWorkspaceException ex) {
            		Util.log(IStatus.ERROR, ex, ex.getMessage());
            	}

            }
        }
        
        if( !changedMRs.isEmpty() ) {
        	for( ModelResource mr: changedMRs) {
        		try {
        			rebuildImportsInTransaction(mr.getEmfResource());
					mr.save(new NullProgressMonitor(), false);
				} catch (ModelWorkspaceException ex) {
					Util.log(IStatus.ERROR, ex, ex.getMessage());
				}
        	}
        }
    }
    
    @Override
    public void handleNotifications( Collection notifications, Object txnSource) {
    	if (!notifications.isEmpty()) {
            Collection functionObjectChanges = getModelChangeNotifications(notifications, txnSource);
            if (!functionObjectChanges.isEmpty()) {
            	handleModelChangeNotification(functionObjectChanges, txnSource);
            }
    	}
    }

	@Override
	public void processModelResourceEvent(ModelResourceEvent event) {
        if( ModelIdentifier.isFunctionModel(event.getModelResource())) {
        	if( event.getType() == ModelResourceEvent.RELOADED || 
        		event.getType() == ModelResourceEvent.CHANGED || 
        		event.getType() == ModelResourceEvent.REMOVED) {
        		Set<ModelResource> affectedDependentModelResources = new HashSet<ModelResource>();
        		
            	affectedDependentModelResources.addAll(ModelUtilities.getResourcesThatUse(event.getModelResource()));
            	
            	for( ModelResource modelResource : affectedDependentModelResources ) {
    	            try {
    					// Process all transformations in the TransformationContainer
    					final EmfResource emfRes = (EmfResource)modelResource.getEmfResource();
    					
    					final List transformations = emfRes.getModelContents().getTransformations();
    					for (Iterator i = transformations.iterator(); i.hasNext();) {
    					    EObject eObj = (EObject)i.next();
    					    if (eObj instanceof SqlTransformationMappingRoot) {
    					        final SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)eObj;
    					        
    					        SqlMappingRootCache.invalidateStatus(mappingRoot, false, this);
    					    }
    					}
    				} catch (ModelWorkspaceException e) {
    					Util.log(IStatus.ERROR, e, e.getMessage());
    				}
            	}
        	}
        }
		
	}

	@Override
	public boolean shouldHandleChangedObject(Object object) {
		return isFunctionModelObject(object);
	}

	private void updateSqlInTransaction(EObject mappingRoot, int sqlType, String sql, boolean significant, Object txnSource) {
        // Update the sql and reconcile the attributes in one transaction
        boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, false, "Update SQL on function object change", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	switch( sqlType ) {
	        	case QueryValidator.SELECT_TRNS: {
	        		TransformationHelper.setSelectSqlString(mappingRoot, sql, NOT_SIGNIFICANT, txnSource);
	        	} break;
	        	case QueryValidator.INSERT_TRNS: {
	        		TransformationHelper.setInsertSqlString(mappingRoot, sql, NOT_SIGNIFICANT, txnSource);
	        	} break;
	        	case QueryValidator.UPDATE_TRNS: {
	        		TransformationHelper.setUpdateSqlString(mappingRoot, sql, NOT_SIGNIFICANT, txnSource);
	        	} break;
	        	case QueryValidator.DELETE_TRNS: {
	        		TransformationHelper.setDeleteSqlString(mappingRoot, sql, NOT_SIGNIFICANT, txnSource);
	        	} break;
        	}
            succeeded = true;
        } finally {
            // If we started Txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
	}
	
	private boolean updateSqlEditorPanel(TransformationObjectEditorPage transOEP, int sqlType, EObject mappingRoot, String changedSql) {
		if( transOEP.getCurrentMappingRoot() == mappingRoot ) {
        	// Close editor, then re-open it.
        	int panelType = transOEP.getCurrentSqlEditor().getPanelType();
        	switch( sqlType ) {
	        	case QueryValidator.SELECT_TRNS: {
	            	if( panelType == UiConstants.SQLPanels.SELECT || panelType == UiConstants.SQLPanels.UPDATE_SELECT ) {
	            		updateSqlTextInTransaction(transOEP, mappingRoot, changedSql);
	            		return true;
	            	}
	        	} break;
	        	
	        	case QueryValidator.INSERT_TRNS: {
	            	if( panelType == UiConstants.SQLPanels.UPDATE_INSERT ) {
	            		updateSqlTextInTransaction(transOEP, mappingRoot, changedSql);
	            		return true;
	            	}
	        	} break;
	        	
	        	case QueryValidator.UPDATE_TRNS: {
	            	if( panelType == UiConstants.SQLPanels.UPDATE_UPDATE ) {
	            		updateSqlTextInTransaction(transOEP, mappingRoot, changedSql);
	            		return true;
	            	}
	        	} break;
	        	
	        	case QueryValidator.DELETE_TRNS: {
	            	if( panelType == UiConstants.SQLPanels.UPDATE_DELETE ) {
	            		updateSqlTextInTransaction(transOEP, mappingRoot, changedSql);
	            		return true;
	            	}
	        	} break;
	    	}

        }
		
		return false;
	}
	
	private void updateSqlTextInTransaction(TransformationObjectEditorPage transOEP, EObject mappingRoot, String changedSql) {
        boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, false, "Update SQL on function object change", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
			transOEP.getCurrentSqlEditor().setText(changedSql);
			transOEP.updateMessagePanel();
			succeeded = true;
        } finally {
            // If we started Txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
	}
	
	private void swapAliasInTransaction(EObject mappingRoot, SqlAlias oldAlias, String newName) {
        boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, false, "SQL Alias on function object change", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	EObject target = oldAlias.getAliasedObject();
        	boolean removed = TransformationHelper.removeSourceAlias(mappingRoot, target, oldAlias.getAlias(), false, this);
        	if( removed ) {
        		TransformationHelper.addSqlAlias(mappingRoot, target, newName, false, this);
        	}
			succeeded = true;
        } finally {
            // If we started Txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
	}
	
	private void rebuildImportsInTransaction(final Resource resource) {
        boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, false, "Rebuild Imports", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	ModelBuildUtil.rebuildImports(resource, true);
			succeeded = true;
        } finally {
            // If we started Txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
		
	}
	
	private boolean isSourceModelWithPushdownFunction(ModelResource modelResource) {
		try {
			String uri = modelResource.getPrimaryMetamodelUri();
			if( ! RelationalPackage.eNS_URI.equalsIgnoreCase(uri) &&
				! (modelResource.getModelType() == ModelType.PHYSICAL_LITERAL) ) {
				return false;
			}
	        	
	    	final ModelObjectCollector moc = new ModelObjectCollector(modelResource.getEmfResource());
	        for( Object eObj : moc.getEObjects()){
	        	if( eObj instanceof Procedure ) {
	        		if( ((Procedure)eObj).isFunction()) {
	        			return true;
	        		}
	        	}
	        }
		} catch (ModelWorkspaceException ex) {
			Util.log(IStatus.ERROR, ex, ex.getMessage());
		}
    	
    	return false;
	}
	
	private boolean isFunction(EObject eObject) {
		if( eObject instanceof ScalarFunction ) {
			return true;
		} else if( eObject instanceof Procedure &&
				   ((Procedure)eObject).isFunction()) {
			return true;
    	}
		
		return false;
	}
}
