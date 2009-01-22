/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.compare;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;


/** 
 * @since 4.2
 */
public class VdbMatcherFactory implements EObjectMatcherFactory {

    private final List standardMatchers;

    /** 
     * @since 4.2
     * @since 4.2
     */
    public VdbMatcherFactory() {
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add( new VdbNameToNameMatcher() );
        this.standardMatchers.add( new VdbNameToNameIgnorecaseMatcher() );

    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     * @since 4.2
     */
    public List createEObjectMatchersForRoots() {
        return this.standardMatchers;
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     * @since 4.2
     */
    public List createEObjectMatchers(EReference reference) {
        // Make sure the reference is in the Relational metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !ManifestPackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the relational metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }

	    // Create the appropriate matchers ...
	    final List results = new LinkedList();
	    final int featureId = reference.getFeatureID();

        if(containingClass.getInstanceClass().equals(VirtualDatabase.class)) {
            if(featureId == ManifestPackage.VIRTUAL_DATABASE__MODELS) {
                results.add( new ModelReferenceNameToNameMatcher() );
                results.add( new ModelRefernceNameToNameIgnoreCaseMatcher() );
            }

            if(featureId == ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS) {
                results.add( new WsdlOptionsMatcher() );
            }
        }

        if(containingClass.getInstanceClass().equals(ModelReference.class)) {
            if(featureId == ManifestPackage.MODEL_REFERENCE__MODEL_SOURCE) {
                results.add( new ModelSourceMatcher() );
            }
        }

        if(containingClass.getInstanceClass().equals(ModelSource.class)) {
            if(featureId == ManifestPackage.MODEL_SOURCE__PROPERTIES) {
                results.add( new ModelSourcePropertyMatcher() );
            }
        }

        if(containingClass.getInstanceClass().equals(ProblemMarkerContainer.class)) {
            if(featureId == ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS) {
                results.add( new ProblemMarkerMatcher() );
            }
        }

        return results;
    }

}
