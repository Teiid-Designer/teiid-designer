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
import com.metamatrix.vdb.edit.manifest.WsdlOptions;


    /** 
     * @since 4.2
     */
    public class WsdlOptionsMatcher extends AbstractEObjectMatcher 
                                  implements TwoPhaseEObjectMatcher {

        /** 
         * 
         * @since 4.2
         */
        public WsdlOptionsMatcher() {
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
                            
            // Loop over the inputs and capture the WsdlOptionss in a HashMap 
            final Map hmWsdlOptionss = new HashMap();
            for (final Iterator iter = inputs.iterator();iter.hasNext();) {
                final EObject obj = (EObject)iter.next();
                if ( obj instanceof WsdlOptions ) {
                    final WsdlOptions woInputWsdlOptions = (WsdlOptions)obj;
                    final WsdlOptionsWrapper wowInWrappper = new WsdlOptionsWrapper( woInputWsdlOptions );
                    hmWsdlOptionss.put( wowInWrappper, woInputWsdlOptions );
                }
            }
            
            // Loop over the outputs and compare the objects...
            for (final Iterator outputIter = outputs.iterator();outputIter.hasNext();) {
                final EObject output = (EObject)outputIter.next();
                if ( output instanceof WsdlOptions ) {
                    final WsdlOptions woOutputWsdlOptions = (WsdlOptions)output;
                    
                    // wrap the output WsdlOptions
                    final WsdlOptionsWrapper wowOutWrapper = new WsdlOptionsWrapper( woOutputWsdlOptions );
                    
                    // use the HashMap.get() to do a match: do we have an input that matches this output?
                    WsdlOptions woInputWsdlOptions = (WsdlOptions)hmWsdlOptionss.get( wowOutWrapper );
                    
                    // if so, apply the match to various mapping collections
                    if( woInputWsdlOptions != null ) {

                        inputs.remove(woInputWsdlOptions);
                        outputIter.remove();
                        addMapping( woInputWsdlOptions, woOutputWsdlOptions, mapping, factory );
                        inputsToOutputs.put( woInputWsdlOptions, woOutputWsdlOptions );
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

        

        private class WsdlOptionsWrapper {
            
            private WsdlOptions woWsdlOptions;

            public WsdlOptionsWrapper( WsdlOptions woWsdlOptions ) {
                this.woWsdlOptions = woWsdlOptions;
            }
            
            private String getVirtualDatabaseName() {
                return woWsdlOptions.getVirtualDatabase().getName();
            }
                    
            
            private String getDefaultNamespaceUri() {
                return woWsdlOptions.getDefaultNamespaceUri();
            }
           
            private String getTargetNamespaceUri() {
                return woWsdlOptions.getTargetNamespaceUri();
            }

            @Override
            public boolean equals( Object obj ) {
                boolean bResult = true;
            
                WsdlOptionsWrapper wowToCompare;
                if(obj == this) {
                    return true;
                }
                
                if(obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }
                
                wowToCompare = (WsdlOptionsWrapper)obj;
                
                if ( !getVirtualDatabaseName().equals(  wowToCompare.getVirtualDatabaseName() ) ) {
                    bResult = false;
                }
                
                if ( !getDefaultNamespaceUri().equals(  wowToCompare.getDefaultNamespaceUri() ) ) {
                    bResult = false;
                }
                
                if ( !getTargetNamespaceUri().equals(  wowToCompare.getTargetNamespaceUri() ) ) {
                    bResult = false;
                }

                return bResult;
            }

            @Override
            public int hashCode() {
            
                int myHash = 0;

                if( getVirtualDatabaseName() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getVirtualDatabaseName() );
                }

                if( getDefaultNamespaceUri() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getDefaultNamespaceUri() );
                }
 
                if( getTargetNamespaceUri() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getTargetNamespaceUri() );
                }

                return myHash;
            }

        }    
    }
    
