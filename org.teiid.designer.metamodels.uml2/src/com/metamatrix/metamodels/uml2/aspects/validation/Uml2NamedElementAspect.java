/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.validation;

import com.metamatrix.metamodels.uml2.aspects.validation.rules.Uml2StringNameRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;


/** 
 * This Validation aspect was created to validate any UML2 metaclass entities that
 * implement, either directly or indirectly, the org.eclipse.uml2.NamedElement interface.
 * 
 * Previous to the implementation of this class, no checking was done to validate that 
 * sibling UML entities had unique names.
 */
public class Uml2NamedElementAspect extends AbstractValidationAspect {

    public static final ValidationRule NAME_RULE = new Uml2StringNameRule();


    /** 
     * @param entity
     * @since 4.3
     */
    public Uml2NamedElementAspect(MetamodelEntity entity) {
        super(entity);
    }


    /**
     * Get all the validation rules for uml2 entity.
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        //addRule(NAME_RULE);
        return super.getValidationRules();      
    }
    
}
