/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.dqp.internal.execution;

import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.util.crypto.CryptoUtil;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.AutoMultiStatus;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.vdb.edit.ClosePreventionVetoableChangeListener;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;


/** 
 * This validator verifies that the given vdb has a vdb definition file, all the physical models
 * in a given vdb have connector bindings defined in the vdb definition file. Also makes sure the
 * vdb being executed does not have any validation errors.
 * @since 4.3
 */
public class VdbExecutionValidatorImpl implements com.metamatrix.modeler.dqp.execution.VdbExecutionValidator {
    private static final String I18N_PREFIX = "VdbExecutionValidator.";  //$NON-NLS-1$
    //
    // Class constants:
    //
    private static final String TEXT_VDB_VALIDATION_ERRORS      = getString("vdb_validation_errors"); //$NON-NLS-1$
    private static final String TEXT_MISSING_MODELS             = getString("missing_physical_models"); //$NON-NLS-1$
    private static final String TEXT_UNEXECUTABLE_ERROR         = getString("unsavedvdb_cannotexecute"); //$NON-NLS-1$
    private static final String TEXT_SYNC_WARNING               = getString("outofsynchvdb_cannotexecute"); //$NON-NLS-1$

    protected static final Status STATUS_NO_MODELS_WARNING = new Status(IStatus.WARNING, DqpPlugin.PLUGIN_ID, NO_MODELS_ERROR_CODE, TEXT_MISSING_MODELS, null);
    private static final Status STATUS_VDB_VALIDATION_ERRORS = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, VDB_VALIDATION_ERROR_CODE, TEXT_VDB_VALIDATION_ERRORS, null);
    private static final Status STATUS_UNEXECUTABLE_ERROR = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, SAVE_REQUIRED_ERROR_CODE, TEXT_UNEXECUTABLE_ERROR, null);
    private static final Status STATUS_SYNC_WARNING = new Status(IStatus.WARNING, DqpPlugin.PLUGIN_ID, SYNCH_WARNING_CODE, TEXT_SYNC_WARNING, null);

    private static String getString(String key) {
        return DqpPlugin.Util.getString(I18N_PREFIX + key);
    }
    
    private static String getString(String key, Object obj) {
        return DqpPlugin.Util.getString(I18N_PREFIX + key, obj);
    }
    
    //
    // Instance variables:
    //
    private VetoableChangeListener veto = new ClosePreventionVetoableChangeListener();

    //
    // Implementation of VDBExecutionValidator interface:
    //

    /**
     * Validate the vdb at the given location. 
     * @param pathToVdb The path to the vdb file
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb(final String pathToVdb) {
        return validateVdb(pathToVdb, true);
    }

    /**
     * Validate the vdb at the given location. 
     * @param pathToVdb The path to the vdb file
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    protected IStatus validateVdb(final String pathToVdb, boolean addSyncWarning) {
        ArgCheck.isNotNull(pathToVdb);

        try {
            VdbEditingContext context = VdbEditPlugin.createVdbEditingContext(new Path(pathToVdb));
            IStatus result = validateVdb(context);
            if ( result.isOK() ) {
                result = OK_STATUS;
            }
            return result;
        } catch(Exception e) {
            return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, EXECPTION_ERROR_CODE, e.getMessage(), e);
        }
    }

    /**
     * Validate the vdb given the vdb editing context. 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb(final VdbEditingContext context) {
        return validateVdb(context, true);
    }

    /**
     * Validate the vdb given the vdb editing context. 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb(final VdbEditingContext context, boolean addSyncWarning) {
        final AutoMultiStatus status = new AutoMultiStatus(OK_STATUS);
        
        Assertion.assertTrue(context instanceof InternalVdbEditingContext);
        InternalVdbEditingContext internalContext = (InternalVdbEditingContext) context;
        boolean validatorOpenedContext = false;
        try {
            if(!context.isOpen()) {
                internalContext.setLoadModelsOnOpen(false);
                context.open();
                validatorOpenedContext = true;
            }            
            context.addVetoableChangeListener(veto);

            // check save state:
            if(context.isSaveRequired()) {
                status.add(STATUS_UNEXECUTABLE_ERROR);
            } // endif

            // use the helper to determine if we have the def file:
            VdbDefnHelper helper = getHelper(internalContext);
            VDBDefn defFile = helper.getVdbDefn();

            // must have a defn for this class to function
            Assertion.isNotNull(defFile, DqpPlugin.Util.getStringOrKey(VdbDefnHelper.PREFIX + "nullVdbDefn")); //$NON-NLS-1$

            // get the vdb definition file
            status.merge(validateVdbModels(context.getVirtualDatabase(), defFile));

            if(addSyncWarning && context.isStale()) {
                status.add(STATUS_SYNC_WARNING);
            }

        } catch(Exception e) {
            String message = (e.getMessage() != null) ? e.getMessage() : StringUtil.Constants.EMPTY_STRING;
            status.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, EXECPTION_ERROR_CODE, message, e));
        } finally {
            context.removeVetoableChangeListener(veto);
            if (validatorOpenedContext) {
                try {
                    internalContext.close(true,false,true);
                    internalContext.setLoadModelsOnOpen(true);
                } catch (IOException err) {
                    VdbEditPlugin.Util.log(err);
                }
            }
        }

        // return what we have built:
        return status;
    }
    
    /**
     * Validate the vdb given the vdb context editor
     * @param context The context editor for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb(final VdbContextEditor context) {
        return validateVdb(context, true);
    }

    /**
     * Validate the vdb given the vdb editing context. 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb(final VdbContextEditor context, boolean addSyncWarning) {
        final AutoMultiStatus status = new AutoMultiStatus(OK_STATUS);
        
        boolean validatorOpenedContext = false;
        try {
            if(!context.isOpen()) {
                context.open(new NullProgressMonitor());
                validatorOpenedContext = true;
            }            
            context.addVetoableChangeListener(veto);

            // check save state:
            if(context.isSaveRequired()) {
                status.add(STATUS_UNEXECUTABLE_ERROR);
            } // endif

            // use the helper to determine if we have the def file:
            VdbDefnHelper helper = getHelper(context);
            VDBDefn defFile = helper.getVdbDefn();

            // must have a defn for this class to function
            Assertion.isNotNull(defFile, DqpPlugin.Util.getStringOrKey(VdbDefnHelper.PREFIX + "nullVdbDefn")); //$NON-NLS-1$

            // get the vdb definition file
            status.merge(validateVdbModels(context.getVirtualDatabase(), defFile));

        } catch(Exception e) {
            String message = (e.getMessage() != null) ? e.getMessage() : StringUtil.Constants.EMPTY_STRING;
            status.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, EXECPTION_ERROR_CODE, message, e));
        } finally {
            context.removeVetoableChangeListener(veto);
            if (validatorOpenedContext) {
                try {
                    context.close(new NullProgressMonitor());
                    context.dispose();
                } catch (IOException err) {
                    VdbEditPlugin.Util.log(err);
                }
            }
        }

        // return what we have built:
        return status;
    }

    /**
     * Validate a vdb to check if it is ready for execution. Checks if the physical models
     * in the vdb have connector bindings defined. Also checks if the vdb has build validation
     * problems.
     * @param database The VirtualDatabase object to validate.
     * @param vdbDefn The object representation of VDBDefn file
     * @return The status of vdb execution validation
     * @since 4.3
     */
    public IStatus validateVdbModels(final VirtualDatabase database, final VDBDefn vdbDefn) {
        ArgCheck.isNotNull(database);
        ArgCheck.isNotNull(vdbDefn);

        // validate the virtual database
        AutoMultiStatus vdbStatus = new AutoMultiStatus(OK_STATUS);
        // map of modelNames to connector bindings
        Map modelBindingMap = vdbDefn.getModelToBindingMappings();
        // verify each of the physical models has a connecot binding defined
        for(final Iterator iter = getPhysicalModelNames(database).iterator(); iter.hasNext();) {
            String modelName = (String) iter.next();
            Collection routingIDList = (Collection) modelBindingMap.get(modelName);
            if(routingIDList == null || routingIDList.isEmpty() ) {
                vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, INCOMPLETE_BINDINGS_ERROR_CODE, getString("physical_model_no_connector_binding", modelName), null)); //$NON-NLS-1$
            }
        }
        // verify each of the materialization models has a connector binding defined
        for(final Iterator iter = getMaterializationModelNames(database).iterator(); iter.hasNext();) {
            String modelName = (String) iter.next();
            Collection routingIDList = (Collection) modelBindingMap.get(modelName);
            if(routingIDList == null || routingIDList.isEmpty() ) {
                vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, INCOMPLETE_BINDINGS_ERROR_CODE, getString("materialization_model_no_connector_binding", modelName), null)); //$NON-NLS-1$
            }
        }

        // for each connector binding make sure their masked properties can be decrypted. stop after finding one that can't.
        Collection models = vdbDefn.getModels();

        if ((models != null) && !models.isEmpty()) {
            Iterator modelItr = models.iterator();

            while (modelItr.hasNext()) {
                ModelInfo model = (ModelInfo)modelItr.next();

                if (model.requiresConnectorBinding()) {
                    ConnectorBinding binding = ModelerDqpUtils.getFirstConnectorBinding(model, vdbDefn);
                    // can't assume we get a binding because we no longer stop on the first error:
                    if (binding != null) {
                        ComponentType type = ModelerDqpUtils.getConnectorType(binding);
                        if (type != null) {
                            Collection typeDefs = type.getComponentTypeDefinitions();
                            Iterator typeDefItr = typeDefs.iterator();

                            while (typeDefItr.hasNext()) {
                                ComponentTypeDefn typeDefn = (ComponentTypeDefn) typeDefItr.next();

                                if (typeDefn.getPropertyDefinition().isMasked()) {
                                    String id = typeDefn.getPropertyDefinition().getName();
                                    String value = binding.getProperty(id);

                                    // if values exists see if it can be decrypted
                                    if (!StringUtil.isEmpty(value)) {
                                    	if (!CryptoUtil.canDecrypt(value)) {
                                            vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID,
                                                    DECRYPTION_ERROR_CODE, getString("decryptionProblem", //$NON-NLS-1$
                                                            new Object[] { id, binding.getName() }), null));
                                        }
                                    } else if( typeDefn.isRequired() ) {
                                        vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID,
                                                                 BINDING_PROPERTY_ERROR_CODE, getString("bindingPropertyError", //$NON-NLS-1$
                                                                         new Object[] { id, binding.getName() }), null));
                                        //System.out.println("  Missing Required C-Binding Property: " + id);
                                    }
                                }
                                // BML TODO:  waiting on Jeff C
                                else if( typeDefn.isRequired() ) {
                                    String id = typeDefn.getPropertyDefinition().getName();
                                    String value = binding.getProperty(id);
                                    if( value == null || StringUtil.isEmpty(value)) {
                                        vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID,
                                                                 BINDING_PROPERTY_ERROR_CODE, getString("bindingPropertyError", //$NON-NLS-1$
                                                                         new Object[] { id, binding.getName() }), null));
                                    }
                                }
                                
                            }
                        }
                    } // endif -- binding not null
                }
            }
        }

        // validate VDB here so that more useful errors will be up front:
        vdbStatus.add(validateVirtualDatabase(database));
        return vdbStatus;
    }
    
    /** 
     * Get the vdb defn helper used in validation
     * @return Returns the helper.
     * @since 4.3
     */
    private VdbDefnHelper getHelper(InternalVdbEditingContext context) throws Exception {
        return DqpPlugin.getInstance().getVdbDefnHelper(context);
    }
    private VdbDefnHelper getHelper(VdbContextEditor context) throws Exception {
        return DqpPlugin.getInstance().getVdbDefnHelper(context);
    }

    /**
     * Validate the {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase} object. Checks if
     * the there are any erros markers on the VDB and also warns if there are no physical models in the vdb. 
     * @param database The VirtualDatabase object to validate.
     * @return The validation status. 
     * @since 4.3
     */
    protected IStatus validateVirtualDatabase(final VirtualDatabase database) {
    	IStatus valCheck = checkForValidationErrors(database);
    	if ( valCheck.getSeverity() == IStatus.ERROR ) {
    		return valCheck;
    	}
    	
        Collection physicalModels = getPhysicalModelNames(database);
        // if there are no physical models there is nothing more to validate....just warn.
        if(physicalModels.isEmpty()) {
           return STATUS_NO_MODELS_WARNING;
        }
        return OK_STATUS;
    }
    
    protected IStatus checkForValidationErrors(final VirtualDatabase database) {
        // if the vdb has validation errors....exit with an error...cannot execute such a vdb
        if(database.getSeverity().getValue() == Severity.ERROR) {
            return STATUS_VDB_VALIDATION_ERRORS;
        }
        return OK_STATUS;
    }
    
    /**
     * Get the names of all physical models in the given vdb. 
     * @param database The {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase} 
     * @return The names of all physical models in given vdb.
     * @since 4.3
     */
    private Collection getPhysicalModelNames(final VirtualDatabase database) {
        Collection physicalModelNames = new ArrayList();
        for(final Iterator iter = database.getModels().iterator(); iter.hasNext();) {
            ModelReference reference = (ModelReference) iter.next();
            ModelType type = reference.getModelType();
            if(type == ModelType.PHYSICAL_LITERAL) {
                physicalModelNames.add(FileUtils.getFilenameWithoutExtension(reference.getName()));
            }
        }
        return physicalModelNames;
    }
    
    /**
     * Get the names of all materialization models in the given vdb. 
     * @param database The {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase} 
     * @return The names of all materialization models in given vdb.
     * @since 4.3
     */
    private Collection getMaterializationModelNames(final VirtualDatabase database) {
        Collection materializationModelNames = new ArrayList();
        for(final Iterator iter = database.getModels().iterator(); iter.hasNext();) {
            ModelReference reference = (ModelReference) iter.next();
            ModelType type = reference.getModelType();
            if(type == ModelType.MATERIALIZATION_LITERAL) {
                materializationModelNames.add(FileUtils.getFilenameWithoutExtension(reference.getName()));
            }
        }
        return materializationModelNames;
    }

}
