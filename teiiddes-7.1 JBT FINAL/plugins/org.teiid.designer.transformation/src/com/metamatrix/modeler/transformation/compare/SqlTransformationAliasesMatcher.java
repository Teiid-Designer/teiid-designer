/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import org.teiid.core.util.HashCodeUtil;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher;

/** 
 * @since 4.2
 */
public class SqlTransformationAliasesMatcher extends AbstractEObjectMatcher implements
                                                                                      TwoPhaseEObjectMatcher {
    public SqlTransformationAliasesMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs,
                                    final Mapping mapping, final MappingFactory factory) {
    }    

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference, final List inputs, final List outputs, 
                             final Mapping mapping, final MappingFactory factory) {
    }

    /**
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings(final EReference reference, final List inputs, final List outputs, final Map inputsToOutputs, final Mapping mapping, final MappingFactory factory) {
        // Loop over the inputs and accumulate the select/insert/update/delete transforms ...
        final Map sqlAliasesByAliasInfo = new HashMap();
        for (final Iterator iter = inputs.iterator();iter.hasNext();) {
            final EObject input = (EObject)iter.next();
            if(input instanceof SqlAlias) {
                final SqlAliasInfo inputInfo = new SqlAliasInfo((SqlAlias)input);
                sqlAliasesByAliasInfo.put(inputInfo, input);
            }
        }
        
        // Loop over the outputs and find matches for any of the above objects ...
        for (final Iterator outputIter = outputs.iterator();outputIter.hasNext();) {
            final EObject output = (EObject)outputIter.next();
            if(output instanceof SqlAlias) {
                final SqlAliasInfo outputInfo = new SqlAliasInfo((SqlAlias)output);
                final SqlAlias inputAlias = (SqlAlias) sqlAliasesByAliasInfo.get(outputInfo);
                if(inputAlias != null ) {
                    inputs.remove(inputAlias);
                    outputIter.remove();
                    addMapping(inputAlias,output,mapping,factory);
                    inputsToOutputs.put(inputAlias,output);
                }
            }
        }        
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(final List inputs,
                                    final List outputs,
                                    final Map inputsToOutputs,
                                    final Mapping mapping,
                                    final MappingFactory factory) {
        addMappings(null, inputs, outputs, inputsToOutputs, mapping, factory);
    }

    private class SqlAliasInfo {
        private SqlAlias alias;
        
        public SqlAliasInfo(SqlAlias alias) {
            this.alias = alias;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
    		if(obj == null || obj.getClass() != this.getClass()) {
    			return false;
    		}
    		SqlAliasInfo aliasInfo = (SqlAliasInfo) obj;
            if ( aliasEquals(this.getAlias(), aliasInfo.getAlias())) {
                return true;
            }
            return false;
        }

        /** 
         * @return Returns the alias.
         * @since 4.2
         */
        public SqlAlias getAlias() {
            return this.alias;
        }
        @Override
        public int hashCode() {
        	int myHash = 0;
    	    myHash = HashCodeUtil.hashCode(myHash, this.getAlias().getAlias());
    		return myHash;
    	}

        private boolean aliasEquals(SqlAlias sqlAlias1, SqlAlias sqlAlias2) {
            if(sqlAlias1 == sqlAlias2) {
                return true;
            }
            
            if(!sqlAlias1.getAlias().equalsIgnoreCase(sqlAlias2.getAlias())) {
                return false;
            }

            EObject aliasedObj1 = sqlAlias1.getAliasedObject();
            EObject aliasedObj2 = sqlAlias2.getAliasedObject();

            if(aliasedObj1 == aliasedObj2) {
                return true;
            }            
            
            if(!aliasedObj1.eClass().equals(aliasedObj2.eClass())) {
                return false;
            }            

            if(ModelerCore.getModelEditor().equals(aliasedObj1, aliasedObj2)) {
                return true;
            }

            return true;
        }
    }    

}
