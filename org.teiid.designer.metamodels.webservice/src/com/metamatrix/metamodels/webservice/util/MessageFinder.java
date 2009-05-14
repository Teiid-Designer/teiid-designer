/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.Operation;

/**
 * @since 4.2
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
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
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
