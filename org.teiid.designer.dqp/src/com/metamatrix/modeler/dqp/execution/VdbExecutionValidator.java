/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.execution;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

public interface VdbExecutionValidator {

    public static final int EXECPTION_ERROR_CODE = 90;
    public static final int SAVE_REQUIRED_ERROR_CODE = 91;
    public static final int NO_DEF_FILE_ERROR_CODE = 92;
    public static final int INCOMPLETE_BINDINGS_ERROR_CODE = 93;
    public static final int VDB_VALIDATION_ERROR_CODE = 94;
    public static final int NO_MODELS_ERROR_CODE = 95;
    public static final int SYNCH_WARNING_CODE = 96;
    public static final int DECRYPTION_ERROR_CODE = 97;
    public static final int BINDING_PROPERTY_ERROR_CODE = 98;
    
    public static final IStatus OK_STATUS = new Status(IStatus.OK, DqpPlugin.PLUGIN_ID, IStatus.OK, DqpPlugin.Util.getString("VdbExecutionValidator.okMessage"), null); //$NON-NLS-1$
	
    
    /**
     * Validate the vdb at the given location. 
     * @param pathToVdb The path to the vdb file
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    IStatus validateVdb(final String pathToVdb);
    
    /**
     * Validate the vdb given the vdb editing context. 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    IStatus validateVdb(final VdbEditingContext context);
    
    /**
     * Validate the vdb given the vdb editing context. 
     * @param context The editing context for vdb containing the definition file.
     * @return The validation status for the vdb indication if its ready for execution.
     * @since 4.3
     */
    IStatus validateVdb(final VdbContextEditor context);

    /**
     * Validate a vdb to check if it is ready for execution. Checks if the physical models
     * in the vdb have connector bindings defined. Also checks if the vdb has build validation
     * problems.
     * @param database The VirtualDatabase object to validate.
     * @param vdbDefn The object representation of VDBDefn file
     * @return The status of vdb execution validation
     * @since 4.3
     */
    IStatus validateVdbModels(final VirtualDatabase database, final VDBDefn vdbDefn);

}
