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
