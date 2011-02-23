/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.validation.rules;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.util.XmlServiceComponentUtil;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;

/**
 * RelationshipStringNameRule
 */
public class XmlServiceComponentStringNameRule extends StringNameRule {

    /**
     * Construct an instance of RelationshipStringNameRule.
     * @param featureID
     */
    public XmlServiceComponentStringNameRule(int featureID) {
        super(featureID);
    }
    
    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validateCharacters()
     */
    @Override
    protected boolean validateCharacters() {
        return false;
    }

    
    /**
     * This method groups siblings into the following domains, and chooses only those siblings that are in
     * the same domain as the supplied object.
     * <ul>
     *  <li>{@link Relationship} instance</li>
     *  <li>{@link RelationshipType} instance</li>
     *  <li>{@link RelationshipRole} instance</li> 
     * </ul>
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public List getSiblingsForUniquenessCheck(final EObject eObject) {
        Object parent = eObject.eContainer();
        if ( parent == null ) {
            parent = eObject.eResource();
        }
        if ( eObject instanceof XmlOperation ) {
            return XmlServiceComponentUtil.findXmlOperations(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if ( eObject instanceof XmlInput ) {
            return XmlServiceComponentUtil.findXmlInputs(parent, ModelVisitorProcessor.DEPTH_ONE);
        } 

        return super.getSiblingsForUniquenessCheck(eObject);
    }

}
