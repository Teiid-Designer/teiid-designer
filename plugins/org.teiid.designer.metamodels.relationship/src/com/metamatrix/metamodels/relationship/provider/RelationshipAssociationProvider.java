/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

        //final RelationshipAssociationDescriptor desc = new RelationshipAssociationDescriptor(eObjects);
        return Collections.EMPTY_LIST; //Collections.singletonList(desc);
    }

}
