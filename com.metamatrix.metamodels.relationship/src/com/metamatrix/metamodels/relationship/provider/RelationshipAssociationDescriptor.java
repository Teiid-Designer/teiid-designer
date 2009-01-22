/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.provider;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;

/**
 * RelationshipAssociationDescriptor
 */
public class RelationshipAssociationDescriptor extends AbstractAssociationDescriptor {

    private static final String TYPE = "RelationshipAssociationDescriptor"; //$NON-NLS-1$

    private final String text;

    /**
     * Construct an instance of RelationshipAssociationDescriptor.
     * 
     * @param eObjects
     */
    public RelationshipAssociationDescriptor( final List eObjects ) {
        super(eObjects);
        this.text = RelationshipMetamodelPlugin.Util.getString("RelationshipAssociationDescriptor.RelationshipAssociationDescriptorLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getType()
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#isComplete()
     */
    @Override
    public boolean isComplete() {
        final List eObjects = this.getEObjects();
        if (eObjects == null || eObjects.size() < 2) {
            return false;
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getImage()
     */
    @Override
    public Object getImage() {
        return RelationshipMetamodelPlugin.getPluginResourceLocator().getImage("full/obj16/Relationship"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getText()
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor#canCreate()
     */
    @Override
    public boolean canCreate() {
        return this.isComplete();
    }

    /**
     * @see com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor#execute()
     */
    @Override
    public EObject create() {
        if (!isComplete()) {
            return null;
        }
        final List eObjects = this.getEObjects();

        // Create the relationship ...
        final Relationship relationship = RelationshipFactory.eINSTANCE.createRelationship();

        // If there are two objects, put one on each side (first one on source, second on target side) ...
        if (eObjects.size() == 2) {
            final EObject object1 = (EObject)eObjects.get(0);
            final EObject object2 = (EObject)eObjects.get(1);
            relationship.getSources().add(object1);
            relationship.getTargets().add(object2);
        } else {
            // There are more than two objects, so put all of them on the source side ...
            relationship.getSources().addAll(eObjects);
        }
        return relationship;
    }
}
