/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

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

import com.metamatrix.metamodels.function.ScalarFunction;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;

/**
 * Implementation of {@link IModelNotificationHandler} to handle notifications for changes in function model objects.
 * 
 * In particular, if a function method (name) changes, we'd like to invalidate the status for any transformation SQL
 * that uses that method name, then perform the SQL rename operation.
 * 
 * Also, if a function model is reloaded/changed or removed we'd like to invalidate any Transformation status objects
 * for models dependent on that function model.
 * 
 */
public class FunctionModelNotificationHandler implements IModelNotificationHandler, UiConstants {
    
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
                if (TransformationHelper.isFunctionModelObject(changedObj) ) {
                	if (result == null) {
                        result = new ArrayList(notifications.size());
                    }
                    result.add(notification);
                    // Remove from notifications collection
                    iter.remove();
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

	            	if( changedObj instanceof ScalarFunction ) {
	            		EObject eObject = (EObject)changedObj;
            			ModelResource changedModelResource = ModelUtilities.getModelResource(eObject);
            			// Get the dependent resources
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

								        SqlMappingRootCache.invalidateStatus(mappingRoot, true, txnSource);
								    }
								}
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
	            	if( changedObj instanceof ScalarFunction ) {
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
	        	            	
	        	            	if( dependentModelChanged && modelResource != null ) {
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
		return TransformationHelper.isFunctionModelObject(object);
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
}
