/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.webservice.Message;
import org.teiid.designer.metamodels.webservice.Operation;

/**
 * @since 8.0
 */
public class MessageFinder extends WebServiceComponentFinder {

    /**
     * @since 4.2
     */
    public MessageFinder() {
        super();
    }

    /**
     * This method accumulates the {@link Message} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean visit( final EObject object ) {
        // Inputs are contained by Operations directly, and Interfaces and Resources indirectly
        if (object instanceof Message) {
            found((Message)object);
            return false;
        }
        if (object instanceof Operation) {
            // Operations can contain Messages
            return true;
        }
        if (object instanceof Interface) {
            // Interfaces can contain Operations that can contain Messages
            return true;
        }
        return false;
    }

}
