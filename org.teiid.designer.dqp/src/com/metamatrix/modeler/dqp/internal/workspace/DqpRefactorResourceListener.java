/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import com.metamatrix.modeler.core.refactor.IRefactorResourceListener;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.dqp.DqpPlugin;


/** 
 * @since 5.0
 */
public class DqpRefactorResourceListener implements
                                        IRefactorResourceListener {

    /** 
     * 
     * @since 5.0
     */
    public DqpRefactorResourceListener() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.refactor.IRefactorResourceListener#notifyRefactored(com.metamatrix.modeler.core.refactor.RefactorResourceEvent)
     * @since 5.0
     */
    public void notifyRefactored(RefactorResourceEvent theEvent) {
        DqpPlugin.getInstance().getWorkspaceConfig().notifyRefactored(theEvent);
    }

}
