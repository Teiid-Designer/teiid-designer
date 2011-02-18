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
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;


    /** 
     * @since 4.2
     */
    public class ModelSourcePropertyMatcher extends AbstractEObjectMatcher 
                                  implements TwoPhaseEObjectMatcher {

        /** 
         * 
         * @since 4.2
         */
        public ModelSourcePropertyMatcher() {
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
                        
            // Loop over the inputs and capture the ModelSourcePropertys in a HashMap 
            final Map hmModelSourceProperties = new HashMap();
            for (final Iterator iter = inputs.iterator();iter.hasNext();) {
                final EObject obj = (EObject)iter.next();
                if ( obj instanceof ModelSourceProperty ) {
                    final ModelSourceProperty mspInputModelSourceProperty = (ModelSourceProperty)obj;
                    final ModelSourcePropertyWrapper mspwInWrappper = new ModelSourcePropertyWrapper( mspInputModelSourceProperty );
                    hmModelSourceProperties.put( mspwInWrappper, mspInputModelSourceProperty );
                }
            }
            
            
            // Loop over the outputs and compare the objects...
            for (final Iterator outputIter = outputs.iterator();outputIter.hasNext();) {
                final EObject output = (EObject)outputIter.next();
                if ( output instanceof ModelSourceProperty ) {
                    final ModelSourceProperty mspOutputModelSourceProperty = (ModelSourceProperty)output;
                    
                    // wrap the output ModelSourceProperty
                    final ModelSourcePropertyWrapper mspwOutWrapper = new ModelSourcePropertyWrapper( mspOutputModelSourceProperty );
                    
                    // use the HashMap.get() to do a match: do we have an input that matches this output?
                    ModelSourceProperty mspInputModelSourceProperty = (ModelSourceProperty)hmModelSourceProperties.get( mspwOutWrapper );
                    
                    // if so, apply the match to various mapping collections
                    if( mspInputModelSourceProperty != null ) {
                        inputs.remove(mspInputModelSourceProperty);

                        outputIter.remove();
                        addMapping( mspInputModelSourceProperty, mspOutputModelSourceProperty, mapping, factory );

                        inputsToOutputs.put( mspInputModelSourceProperty, mspOutputModelSourceProperty );
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

        

        private class ModelSourcePropertyWrapper {
            
            private ModelSourceProperty mspModelSourceProperty;

            public ModelSourcePropertyWrapper( ModelSourceProperty mspModelSourceProperty ) {
                this.mspModelSourceProperty = mspModelSourceProperty;
            }
            
            private String getName() {
                return mspModelSourceProperty.getName();
            }
                    
            
            private String getValue() {
                return mspModelSourceProperty.getValue();
            }

            @Override
            public boolean equals( Object obj ) {
                boolean bResult = true;
            
                ModelSourcePropertyWrapper mspwToCompare;
                if(obj == this) {
                    return true;
                }
                
                if(obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }
                
                mspwToCompare = (ModelSourcePropertyWrapper)obj;
                
                if ( !getName().equals(  mspwToCompare.getName() ) ) {
                    bResult = false;
                }
                
                if ( !getValue().equals(  mspwToCompare.getValue() ) ) {
                    bResult = false;
                }
                
                return bResult;
            }

            @Override
            public int hashCode() {
            
                int myHash = 0;

                if( getName() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getName() );
                }

                if( getValue() != null ) {
                    myHash = HashCodeUtil.hashCode( myHash, getValue() );
                }
 
                return myHash;
            }

        }    
    }
    
