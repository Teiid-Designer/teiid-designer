/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.transaction.UnitOfWork;

/**<p>
 * </p>
 * @since 8.0
 */
public interface TransactionRunnable {
    //============================================================================================================================
    // Methods
    
    Object run(UnitOfWork uow) throws ModelerCoreException;
}
