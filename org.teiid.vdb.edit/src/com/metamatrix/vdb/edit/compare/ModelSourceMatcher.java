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
import com.metamatrix.vdb.edit.manifest.ModelSource;



    /** 
     * @since 4.2
     */
    public class ModelSourceMatcher extends AbstractEObjectMatcher 
                                  implements TwoPhaseEObjectMatcher {

        /** 
         * 
         * @since 4.2
         */
        public ModelSourceMatcher() {
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
            
//            System.out.println("\n\n[ModelSourceMatcher.addMappings(6) TOP"); //$NON-NLS-1$
//            System.out.println("[ModelSourceMatcher.addMappings(6) BEFORE the process..."); //$NON-NLS-1$
//            System.out.println("\t\t Inputs Count: " + inputs.size()); //$NON-NLS-1$
//            System.out.println("\t\tOutputs Count: " + outputs.size()); //$NON-NLS-1$

//            System.out.println("\t\t Inputs: " + inputs); //$NON-NLS-1$
//            System.out.println("\t\tOutputs: " + outputs); //$NON-NLS-1$
//            System.out.println( "\n" ); //$NON-NLS-1$
            
                
            // Loop over the inputs and capture the ModelSources in a HashMap 
            final Map hmModelSources = new HashMap();
            for (final Iterator iter = inputs.iterator();iter.hasNext();) {
                final EObject obj = (EObject)iter.next();
                if ( obj instanceof ModelSource ) {
                    final ModelSource msInputModelSource = (ModelSource)obj;
                    final ModelSourceWrapper mswInWrappper = new ModelSourceWrapper( msInputModelSource );
                    hmModelSources.put( mswInWrappper, msInputModelSource );
                }
            }
            
//            System.out.println("\n[ModelSourceMatcher.addMappings After processing Inputs; Before processing Outputs: "  ); //$NON-NLS-1$
//            System.out.println("\t\t HashMap Entry Count: " + hmModelSources.size() ); //$NON-NLS-1$
//            System.out.println("\t\t HashMap: " + hmModelSources.entrySet() ); //$NON-NLS-1$
            
            // Loop over the outputs and compare the objects...
            for (final Iterator outputIter = outputs.iterator();outputIter.hasNext();) {
                final EObject output = (EObject)outputIter.next();
                if ( output instanceof ModelSource ) {
                    final ModelSource msOutputModelSource = (ModelSource)output;
                    
                    // wrap the output ModelSource
                    final ModelSourceWrapper mswOutWrapper = new ModelSourceWrapper( msOutputModelSource );
                    
                    // use the HashMap.get() to do a match: do we have an input that matches this output?
                    ModelSource msInputModelSource = (ModelSource)hmModelSources.get( mswOutWrapper );
                    
                    // if so, apply the match to various mapping collections
                    if( msInputModelSource != null ) {
                        inputs.remove(msInputModelSource);
//                        System.out.println("[ModelSourceMatcher.addMappings Were we successful? " + bWasRemoved + " in removing from ~inputs~: " + msInputModelSource ); //$NON-NLS-1$
                        outputIter.remove();
                        addMapping( msInputModelSource, msOutputModelSource, mapping, factory );

//                        System.out.println("[ModelSourceMatcher.addMappings About to remove: "); //$NON-NLS-1$
//                        System.out.println("\t\t Input: " + msInputModelSource.getModel() ); //$NON-NLS-1$
//                        System.out.println("\t\tOutput: " + msOutputModelSource.getModel() ); //$NON-NLS-1$
                        inputsToOutputs.put( msInputModelSource, msOutputModelSource );
                    }
                }
            }
//            System.out.println("[ModelSourceMatcher.addMappings(6) AFTER the process..."); //$NON-NLS-1$
//            System.out.println("\t\t Inputs: " + inputs); //$NON-NLS-1$
//            System.out.println("\t\tOutputs: " + outputs); //$NON-NLS-1$

            
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

        

        private class ModelSourceWrapper {
            
            private ModelSource msModelSource;

            public ModelSourceWrapper( ModelSource msModelSource ) {
                this.msModelSource = msModelSource;
            }
            
            private String getModelName() {
                return msModelSource.getModel().getName();
            }
                    
            
            private String getModelPath() {
                return msModelSource.getModel().getModelLocation();
            }

            @Override
            public boolean equals( Object obj ) {

                boolean bResult = true;
                ModelSourceWrapper mswToCompare;
                
                if(obj == this) {
                    return true;
                }
                
                if(obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }
                
                mswToCompare = (ModelSourceWrapper)obj;
                
                if ( !getModelName().equals(  mswToCompare.getModelName() ) ) {
                    bResult = false;
                }
                
                if ( !getModelPath().equals(  mswToCompare.getModelPath() ) ) {
                    bResult = false;
                }
                
                return bResult;
            }

            @Override
            public int hashCode() {

                int myHash = 0;

                if( getModelName() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getModelName() );
                }

                if( getModelPath() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getModelPath() );
                }
 
                return myHash;
            }

        }    
    }
    
