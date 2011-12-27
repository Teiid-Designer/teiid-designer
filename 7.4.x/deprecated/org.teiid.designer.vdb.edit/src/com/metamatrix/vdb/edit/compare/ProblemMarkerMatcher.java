/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher;
import com.metamatrix.modeler.core.validation.ProblemMarker;
import com.metamatrix.modeler.core.validation.Severity;


/** 
 * @since 4.2
 */
public class ProblemMarkerMatcher extends AbstractEObjectMatcher 
                                implements TwoPhaseEObjectMatcher {

    /** 
     * 
     * @since 4.2
     */
    public ProblemMarkerMatcher() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(List inputs,
                                    List outputs,
                                    Map inputsToOutputs,
                                    Mapping mapping,
                                    MappingFactory factory) {
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings(EReference reference,
                            List inputs,
                            List outputs,
                            Map inputsToOutputs,
                            Mapping mapping,
                            MappingFactory factory) {
        
            
        // Loop over the inputs and capture the ProblemMarkers in a HashMap 
        final Map hmProblemMarkers = new HashMap();
        for (final Iterator iter = inputs.iterator();iter.hasNext();) {
            final EObject obj = (EObject)iter.next();
            if ( obj instanceof ProblemMarker ) {
                final ProblemMarker pmInputProblemMarker = (ProblemMarker)obj;
                final ProblemMarkerWrapper pmwInWrappper = new ProblemMarkerWrapper( pmInputProblemMarker );
                hmProblemMarkers.put( pmwInWrappper, pmInputProblemMarker );
            }
        }
        
        
        // Loop over the outputs and compare the objects...
        for (final Iterator outputIter = outputs.iterator();outputIter.hasNext();) {
            final EObject output = (EObject)outputIter.next();
            if ( output instanceof ProblemMarker ) {
                final ProblemMarker pmOutputProblemMarker = (ProblemMarker)output;
                
                // wrap the output ProblemMarker
                final ProblemMarkerWrapper pmwOutWrapper = new ProblemMarkerWrapper( pmOutputProblemMarker );
                
                // use the HashMap.get() to do a match: do we have an input that matches this output?
                ProblemMarker pmInputProblemMarker = (ProblemMarker)hmProblemMarkers.get( pmwOutWrapper );
                
                // if so, apply the match to various mapping collections
                if( pmInputProblemMarker != null ) {
                    inputs.remove(pmInputProblemMarker);
                    outputIter.remove();
                    addMapping( pmInputProblemMarker, pmOutputProblemMarker, mapping, factory );

                    inputsToOutputs.put( pmInputProblemMarker, pmOutputProblemMarker );
                }
            }
        }
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(List inputs,
                                    List outputs,
                                    Mapping mapping,
                                    MappingFactory factory) {
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings(EReference reference,
                            List inputs,
                            List outputs,
                            Mapping mapping,
                            MappingFactory factory) {
    }

    

    private class ProblemMarkerWrapper {
        
        private ProblemMarker pmProblemMarker;

        public ProblemMarkerWrapper( ProblemMarker pmProblemMarker ) {
            this.pmProblemMarker = pmProblemMarker;
        }
                
        private String getMessage() {
            return pmProblemMarker.getMessage();
        }
       
        private Severity getSeverity() {
            return pmProblemMarker.getSeverity();
        }

        private String getTarget() {
            return pmProblemMarker.getTarget();
        }

        @Override
        public boolean equals( Object obj ) {
            boolean bResult = true;

            ProblemMarkerWrapper pmwToCompare;
            if(obj == this) {
                return true;
            }
            
            if(obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            
            pmwToCompare = (ProblemMarkerWrapper)obj;
            
            if ( !getSeverity().getName().equals(  pmwToCompare.getSeverity().getName() ) ) {
                bResult = false;
            }
            if ( !getTarget().equals(  pmwToCompare.getTarget() ) ) {
                bResult = false;
            }
            if ( !getMessage().equals(  pmwToCompare.getMessage() ) ) {
                bResult = false;
            }

            return bResult;
        }

        @Override
        public int hashCode() {
            
            int myHash = 0;
            if(this.getMessage() != null) {
                myHash = HashCodeUtil.hashCode(myHash, this.getMessage().toUpperCase());
            }
            if(this.getTarget() != null) {
                myHash = HashCodeUtil.hashCode(myHash, this.getTarget().toUpperCase());
            }
            return myHash;
        }

    }    
}
