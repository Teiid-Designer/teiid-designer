/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.MappingPackage;

import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;


/** 
 * TransformationMatcherFactory
 */
public class TransformationMatcherFactory implements EObjectMatcherFactory {

    //private final List standardMatchers;
    
    /**
     * Construct an instance of TransformationMatcherFactory.
     */
    public TransformationMatcherFactory() {
        super();
//        this.standardMatchers = new LinkedList();
//        this.standardMatchers.add( new MappingClassObjectNameToNameMatcher() );
//        this.standardMatchers.add( new MappingClassObjectNameToNameIgnoreCaseMatcher() );
//        this.standardMatchers.add( new InputParameterNameToNameMatcher() );
//        this.standardMatchers.add( new InputParameterNameToNameIgnoreCaseMatcher() );
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        // Create the appropriate matchers ...
        final List results = new LinkedList();
        results.add( new TransformationRootObjectMatcher() );
        return results;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
	    // Make sure the reference is in the xml metamodel ...
	    final EClass containingClass = reference.getEContainingClass();
	    final EPackage metamodel = containingClass.getEPackage();
	    if (!TransformationPackage.eINSTANCE.equals(metamodel) && !MappingPackage.eINSTANCE.equals(metamodel)) {
	         //The feature isn't in the transformation or mapping metamodel so return nothing ...
	        return Collections.EMPTY_LIST;
	    }
	    
	    // Create the appropriate matchers ...
	    final List results = new LinkedList();
	    final int featureId = reference.getFeatureID();
	    if(containingClass.getInstanceClass().equals(TransformationContainer.class)) {
	        if(featureId == TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS) {
	            results.add( new TransformationMappingRootTargetMatcher() );
	        }
	    } else if (containingClass.getInstanceClass().equals(MappingClassSet.class)) {
	        if(featureId == TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES) {
	            results.add( new MappingClassObjectNameToNameMatcher() );
	            results.add( new MappingClassObjectNameToNameIgnoreCaseMatcher() );
	        }
	    } else if (containingClass.getInstanceClass().equals(MappingClass.class)) {
	        if(featureId == TransformationPackage.MAPPING_CLASS__COLUMNS) {
	            results.add( new MappingClassObjectNameToNameMatcher() );
	            results.add( new MappingClassObjectNameToNameIgnoreCaseMatcher() );
	        }
	    } else if (containingClass.getInstanceClass().equals(InputSet.class)) {
	        if(featureId == TransformationPackage.INPUT_SET__INPUT_PARAMETERS) {
	            results.add( new InputParameterNameToNameMatcher() );
	            results.add( new InputParameterNameToNameIgnoreCaseMatcher() );
	        }	        
	    } else {
	        // not checking the container class in these cases, since the container is
	        // from mapping metamodel
	        if(featureId == TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED ||
	           featureId == TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER) {
	            results.add( new SqlTransformationSqlToSqlMatcher() );
	        } else if(featureId == TransformationPackage.SQL_TRANSFORMATION__ALIASES) {
	            results.add( new SqlTransformationAliasesMatcher() );
	        }
	    }

        return results;
    }
}
