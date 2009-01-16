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

package com.metamatrix.metamodels.relationship.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.metamatrix.modeler.core.association.AssociationProvider;

/**
 * RelationshipAssociationProvider
 */
public class RelationshipAssociationProvider implements AssociationProvider {

    /**
     * Construct an instance of RelationshipAssociationProvider.
     */
    public RelationshipAssociationProvider() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationProvider#getNewAssociationDescriptors(java.util.List)
     */
    public Collection getNewAssociationDescriptors( final List eObjects ) {
        // There must be at least two objects to create a relationship ...
        if (eObjects == null || eObjects.size() < 2) {
            return Collections.EMPTY_LIST;
        }

        final RelationshipAssociationDescriptor desc = new RelationshipAssociationDescriptor(eObjects);
        return Collections.singletonList(desc);
    }

}
