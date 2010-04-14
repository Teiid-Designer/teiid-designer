/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.AutoMultiStatus;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;

/**
 * This validator verifies that the given vdb has a vdb definition file, all the physical models in a given vdb have connector
 * bindings defined in the vdb definition file. Also makes sure the vdb being executed does not have any validation errors.
 * 
 * @since 4.3
 */
public class VdbExecutionValidatorImpl implements VdbExecutionValidator {
    private static final String I18N_PREFIX = "VdbExecutionValidator."; //$NON-NLS-1$
    //
    // Class constants:
    //
    private static final String TEXT_VDB_VALIDATION_ERRORS = getString("vdb_validation_errors"); //$NON-NLS-1$
    private static final String TEXT_MISSING_MODELS = getString("missing_physical_models"); //$NON-NLS-1$
    private static final String TEXT_UNEXECUTABLE_ERROR = getString("unsavedvdb_cannotexecute"); //$NON-NLS-1$
    private static final String TEXT_SYNC_WARNING = getString("outofsynchvdb_cannotexecute"); //$NON-NLS-1$

    protected static final Status STATUS_NO_MODELS_WARNING = new Status(IStatus.WARNING, DqpPlugin.PLUGIN_ID,
                                                                        NO_MODELS_ERROR_CODE, TEXT_MISSING_MODELS, null);
    private static final Status STATUS_VDB_VALIDATION_ERRORS = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID,
                                                                          VDB_VALIDATION_ERROR_CODE, TEXT_VDB_VALIDATION_ERRORS,
                                                                          null);
    private static final Status STATUS_UNEXECUTABLE_ERROR = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID,
                                                                       SAVE_REQUIRED_ERROR_CODE, TEXT_UNEXECUTABLE_ERROR, null);
    private static final Status STATUS_SYNC_WARNING = new Status(IStatus.WARNING, DqpPlugin.PLUGIN_ID, SYNCH_WARNING_CODE,
                                                                 TEXT_SYNC_WARNING, null);

    private static String getString( String key ) {
        return DqpPlugin.Util.getString(I18N_PREFIX + key);
    }

    private static String getString( String key,
                                     Object obj ) {
        return DqpPlugin.Util.getString(I18N_PREFIX + key, obj);
    }

    //
    // Instance variables:
    //
    // private VetoableChangeListener veto = new ClosePreventionVetoableChangeListener();

    //
    // Implementation of VDBExecutionValidator interface:
    //

    /**
     * Validate the vdb given the vdb editing context.
     * 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb( final Vdb vdb ) {
        return validateVdb(vdb, true);
    }

    /**
     * Validate the vdb given the vdb editing context.
     * 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    public IStatus validateVdb( final Vdb vdb,
                                boolean addSyncWarning ) {
        final AutoMultiStatus status = new AutoMultiStatus(OK_STATUS);

        try {

            // must have a defn for this class to function
            CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$

            // get the vdb definition file
            status.merge(validateVdbModels(vdb));

            if (addSyncWarning) {
                status.add(STATUS_SYNC_WARNING);
            }

        } catch (Exception e) {
            String message = (e.getMessage() != null) ? e.getMessage() : CoreStringUtil.Constants.EMPTY_STRING;
            status.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, EXECPTION_ERROR_CODE, message, e));
        } finally {

        }

        // return what we have built:
        return status;
    }

    /**
     * Validate a vdb to check if it is ready for execution. Checks if the physical models in the vdb have connector bindings
     * defined. Also checks if the vdb has build validation problems.
     * 
     * @param database The VirtualDatabase object to validate.
     * @param vdbDefn The object representation of VDBDefn file
     * @return The status of vdb execution validation
     * @since 4.3
     */
    public IStatus validateVdbModels( final Vdb vdb ) {
        CoreArgCheck.isNotNull(vdb);

        // validate the virtual database
        AutoMultiStatus vdbStatus = new AutoMultiStatus(OK_STATUS);

        // map of modelNames to connector bindings
        // verify each of the physical models has a connector binding defined

        // TODO:

        // for (final Iterator iter = getPhysicalModelNames(vdb).iterator(); iter.hasNext();) {
        // String modelName = (String)iter.next();
        // Collection routingIDList = (Collection)modelBindingMap.get(modelName);
        // if (routingIDList == null || routingIDList.isEmpty()) {
        // vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, INCOMPLETE_BINDINGS_ERROR_CODE,
        //                                         getString("physical_model_no_connector_binding", modelName), null)); //$NON-NLS-1$
        // }
        // }
        // verify each of the materialization models has a connector binding defined

        // TODO:

        // for (final Iterator iter = getMaterializationModelNames(vdb).iterator(); iter.hasNext();) {
        // String modelName = (String)iter.next();
        // Collection routingIDList = (Collection)modelBindingMap.get(modelName);
        // if (routingIDList == null || routingIDList.isEmpty()) {
        // vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, INCOMPLETE_BINDINGS_ERROR_CODE,
        //                                         getString("materialization_model_no_connector_binding", modelName), null)); //$NON-NLS-1$
        // }
        // }

        // for each connector binding make sure their masked properties can be decrypted. stop after finding one that can't.
        Collection modelEntries = vdb.getModelEntries();

        if ((modelEntries != null) && !modelEntries.isEmpty()) {
            Iterator modelItr = modelEntries.iterator();

            while (modelItr.hasNext()) {
                VdbModelEntry modelEntry = (VdbModelEntry)modelItr.next();

                // TODO: Check whether or not a ModelEntry is a source model that requires a ConnectionFactory
                // IF NOT, then add a new status
                // if (modelEntry.requiresConnector()) {
                // // TODO: We don't have a Server defined here yet? How about the concept
                // // of a Preview/Execution Server??
                // Connector connector = DqpPlugin.getInstance().getServerManager().getFirstConnector(model, vdbDefn);
                // // can't assume we get a binding because we no longer stop on the first error:
                // if (connector != null) {
                // ConnectorType type = connector.getType();
                // if (type != null) {
                // for (PropertyDefinition typeDefn : type.getPropertyDefinitions()) {
                //
                // if (typeDefn.isRequired()) {
                // String id = typeDefn.getName();
                // String value = connector.getPropertyValue(id);
                //
                // // look at type for default values as connectors inherit default values
                // if ((value == null || CoreStringUtil.isEmpty(value)) && typeDefn.getDefaultValue() == null) {
                // vdbStatus.add(new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, BINDING_PROPERTY_ERROR_CODE,
                //                                                                 getString("bindingPropertyError", //$NON-NLS-1$
                // new Object[] {id, connector.getName()}), null));
                // }
                // }
                //
                // }
                // }
                // } // endif -- binding not null
                // }
            }
        }

        // validate VDB here so that more useful errors will be up front:
        vdbStatus.add(validateVirtualDatabase(vdb));
        return vdbStatus;
    }

    /**
     * Validate the {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase} object. Checks if the there are any erros markers on
     * the VDB and also warns if there are no physical models in the vdb.
     * 
     * @param database The VirtualDatabase object to validate.
     * @return The validation status.
     * @since 4.3
     */
    protected IStatus validateVirtualDatabase( final Vdb vdb ) {
        IStatus valCheck = checkForValidationErrors(vdb);
        if (valCheck.getSeverity() == IStatus.ERROR) {
            return valCheck;
        }

        Collection physicalModels = getPhysicalModelNames(vdb);
        // if there are no physical models there is nothing more to validate....just warn.
        if (physicalModels.isEmpty()) {
            return STATUS_NO_MODELS_WARNING;
        }
        return OK_STATUS;
    }

    protected IStatus checkForValidationErrors( final Vdb vdb ) {
        // if the vdb has validation errors....exit with an error...cannot execute such a vdb

        // TODO: Somehow DO this?????

        // if (vdb.getSeverity().getValue() == Severity.ERROR) {
        // return STATUS_VDB_VALIDATION_ERRORS;
        // }
        return OK_STATUS;
    }

    /**
     * Get the names of all physical models in the given vdb.
     * 
     * @param database The {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase}
     * @return The names of all physical models in given vdb.
     * @since 4.3
     */
    private Collection getPhysicalModelNames( final Vdb vdb ) {
        Collection physicalModelNames = new ArrayList();

        for (VdbModelEntry entry : vdb.getModelEntries()) {
            if (entry.getType() == ModelType.PHYSICAL_LITERAL) {
                physicalModelNames.add(FileUtils.getFilenameWithoutExtension(entry.getName().lastSegment()));
            }
        }
        return physicalModelNames;
    }

    /**
     * Get the names of all materialization models in the given vdb.
     * 
     * @param database The {@link com.metamatrix.vdb.edit.manifest.VirtualDatabase}
     * @return The names of all materialization models in given vdb.
     * @since 4.3
     */
    private Collection getMaterializationModelNames( final Vdb vdb ) {
        Collection materializationModelNames = new ArrayList();

        for (VdbModelEntry entry : vdb.getModelEntries()) {
            if (entry.getType() == ModelType.MATERIALIZATION_LITERAL) {
                materializationModelNames.add(FileUtils.getFilenameWithoutExtension(entry.getName().lastSegment()));
            }
        }
        return materializationModelNames;
    }

}
