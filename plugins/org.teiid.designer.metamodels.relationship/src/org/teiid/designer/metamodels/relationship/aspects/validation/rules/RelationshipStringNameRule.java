/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.validation.rules;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.validation.rules.StringNameRule;
import org.teiid.designer.metamodels.relationship.Relationship;
import org.teiid.designer.metamodels.relationship.RelationshipRole;
import org.teiid.designer.metamodels.relationship.RelationshipType;
import org.teiid.designer.metamodels.relationship.util.RelationshipUtil;


/**
 * RelationshipStringNameRule
 */
public class RelationshipStringNameRule extends StringNameRule {

    /**
     * Construct an instance of RelationshipStringNameRule.
     * 
     * @param featureID
     */
    public RelationshipStringNameRule( int featureID ) {
        super(featureID);
    }

    /**
     * @see org.teiid.designer.core.validation.rules.StringNameRule#validateCharacters()
     */
    @Override
    protected boolean validateCharacters() {
        return false;
    }

    /**
     * This method groups siblings into the following domains, and chooses only those siblings that are in the same domain as the
     * supplied object.
     * <ul>
     * <li>{@link Relationship} instance</li>
     * <li>{@link RelationshipType} instance</li>
     * <li>{@link RelationshipRole} instance</li>
     * </ul>
     * 
     * @see org.teiid.designer.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public List getSiblingsForUniquenessCheck( final EObject eObject ) {
        Object parent = eObject.eContainer();
        if (parent == null) {
            parent = eObject.eResource();
        }
        if (eObject instanceof Relationship) {
            return RelationshipUtil.findRelationships(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof RelationshipType) {
            return RelationshipUtil.findRelationshipTypes(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if (eObject instanceof RelationshipRole) {
            return RelationshipUtil.findRelationshipRoles(parent, ModelVisitorProcessor.DEPTH_ONE);
        }

        return super.getSiblingsForUniquenessCheck(eObject);
    }

}
