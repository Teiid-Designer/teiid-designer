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
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;

public class FakeVdbExecutionValidator implements VdbExecutionValidator {

    private static final IStatus OK_STATUS = new Status(IStatus.OK, DqpPlugin.PLUGIN_ID, IStatus.OK, "test", null); //$NON-NLS-1$

    private IStatus testStatus = OK_STATUS;

    public FakeVdbExecutionValidator() {
    }

    public FakeVdbExecutionValidator( IStatus testStatus ) {
        this.testStatus = testStatus;
    }

    public IStatus validateVdb( String pathToVdb ) {
        return this.testStatus;
    }

    public IStatus validateVdb( Vdb vdb ) {
        return this.testStatus;
    }

    public IStatus validateVdbModels( Vdb vdb ) {
        return this.testStatus;
    }

}
