/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.execution;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

public class FakeVdbExecutionValidator implements VdbExecutionValidator {

    private static final IStatus OK_STATUS = new Status(IStatus.OK, DqpPlugin.PLUGIN_ID, IStatus.OK, "test", null); //$NON-NLS-1$

	private IStatus testStatus = OK_STATUS;
	
	public FakeVdbExecutionValidator() {
	}
    
	public FakeVdbExecutionValidator(IStatus testStatus) {
		this.testStatus = testStatus;
	}

	public IStatus validateVdb(String pathToVdb) {
		return this.testStatus;
	}

	public IStatus validateVdb(VdbEditingContext context) {
		return this.testStatus;
	}

	public IStatus validateVdb(VdbContextEditor context) {
        return null;
    }

    public IStatus validateVdbModels(VirtualDatabase database, VDBDefn vdbDefn) {
		return this.testStatus;
	}

}
