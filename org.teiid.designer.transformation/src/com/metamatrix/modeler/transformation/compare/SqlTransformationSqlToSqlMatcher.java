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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher;

/**
 * @since 4.2
 */
public class SqlTransformationSqlToSqlMatcher extends AbstractEObjectMatcher implements TwoPhaseEObjectMatcher {

    public SqlTransformationSqlToSqlMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List,
     *      org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots( final List inputs,
                                     final List outputs,
                                     final Mapping mapping,
                                     final MappingFactory factory ) {
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List,
     *      java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference,
                             final List inputs,
                             final List outputs,
                             final Mapping mapping,
                             final MappingFactory factory ) {
    }

    /**
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference,
     *      java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping,
     *      org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings( final EReference reference,
                             final List inputs,
                             final List outputs,
                             final Map inputsToOutputs,
                             final Mapping mapping,
                             final MappingFactory factory ) {
        // Loop over the inputs and accumulate the select/insert/update/delete transforms ...
        final Map sqlTransformsByTransforms = new HashMap();
        for (final Iterator iter = inputs.iterator(); iter.hasNext();) {
            final EObject obj = (EObject)iter.next();
            if (obj instanceof SqlTransformation) {
                final SqlTransformation inputTransformObj = (SqlTransformation)obj;
                SqlTransformation nestedTransformObj = null;
                EList nestedList = inputTransformObj.getNested();
                for (Iterator nestIter = nestedList.iterator(); nestIter.hasNext();) {
                    EObject eObj = (EObject)nestIter.next();
                    if (eObj != null && eObj instanceof SqlTransformation) {
                        nestedTransformObj = (SqlTransformation)eObj;
                        break;
                    }
                }
                if (nestedTransformObj == null) {
                    nestedTransformObj = inputTransformObj;
                }
                SqlTransforms sqlTransforms = getSqlTransforms(nestedTransformObj);
                sqlTransformsByTransforms.put(sqlTransforms, inputTransformObj);
            }
        }

        // Loop over the outputs and compare the names ...
        for (final Iterator outputIter = outputs.iterator(); outputIter.hasNext();) {
            final EObject output = (EObject)outputIter.next();
            if (output instanceof SqlTransformation) {
                final SqlTransformation outputTransform = (SqlTransformation)output;
                SqlTransformation nestedTransformObj = null;
                EList nestedList = outputTransform.getNested();
                for (Iterator nestIter = nestedList.iterator(); nestIter.hasNext();) {
                    EObject eObj = (EObject)nestIter.next();
                    if (eObj != null && eObj instanceof SqlTransformation) {
                        nestedTransformObj = (SqlTransformation)eObj;
                        break;
                    }
                }
                if (nestedTransformObj == null) {
                    nestedTransformObj = outputTransform;
                }
                SqlTransforms sqlTransforms = getSqlTransforms(nestedTransformObj);
                SqlTransformation inputTransformation = (SqlTransformation)sqlTransformsByTransforms.get(sqlTransforms);
                if (inputTransformation != null) {
                    inputs.remove(inputTransformation);
                    outputIter.remove();
                    addMapping(inputTransformation, outputTransform, mapping, factory);
                    inputsToOutputs.put(inputTransformation, outputTransform);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappingsForRoots(java.util.List, java.util.List,
     *      java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots( final List inputs,
                                     final List outputs,
                                     final Map inputsToOutputs,
                                     final Mapping mapping,
                                     final MappingFactory factory ) {
        addMappings(null, inputs, outputs, inputsToOutputs, mapping, factory);
    }

    private SqlTransforms getSqlTransforms( SqlTransformation transform ) {
        SqlTransforms sqlTransforms = new SqlTransforms();
        sqlTransforms.setSelectSql(transform.getSelectSql());
        sqlTransforms.setInsertSql(transform.getInsertSql());
        sqlTransforms.setUpdateSql(transform.getUpdateSql());
        sqlTransforms.setDeleteSql(transform.getDeleteSql());
        return sqlTransforms;
    }

    class SqlTransforms {
        private String selectSql;
        private String insertSql;
        private String updateSql;
        private String deleteSql;

        /**
         * @return Returns the deleteSql.
         * @since 4.2
         */
        public String getDeleteSql() {
            return this.deleteSql;
        }

        /**
         * @param deleteSql The deleteSql to set.
         * @since 4.2
         */
        public void setDeleteSql( String deleteSql ) {
            this.deleteSql = deleteSql;
        }

        /**
         * @return Returns the insertSql.
         * @since 4.2
         */
        public String getInsertSql() {
            return this.insertSql;
        }

        /**
         * @param insertSql The insertSql to set.
         * @since 4.2
         */
        public void setInsertSql( String insertSql ) {
            this.insertSql = insertSql;
        }

        /**
         * @return Returns the selectSql.
         * @since 4.2
         */
        public String getSelectSql() {
            return this.selectSql;
        }

        /**
         * @param selectSql The selectSql to set.
         * @since 4.2
         */
        public void setSelectSql( String selectSql ) {
            this.selectSql = selectSql;
        }

        /**
         * @return Returns the updateSql.
         * @since 4.2
         */
        public String getUpdateSql() {
            return this.updateSql;
        }

        /**
         * @param updateSql The updateSql to set.
         * @since 4.2
         */
        public void setUpdateSql( String updateSql ) {
            this.updateSql = updateSql;
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            SqlTransforms sqlTransforms = (SqlTransforms)obj;
            if (equalsIgnoreCase(this.getSelectSql(), sqlTransforms.getSelectSql())
                && equalsIgnoreCase(this.getInsertSql(), sqlTransforms.getInsertSql())
                && equalsIgnoreCase(this.getUpdateSql(), sqlTransforms.getUpdateSql())
                && equalsIgnoreCase(this.getDeleteSql(), sqlTransforms.getDeleteSql())) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int myHash = 0;
            if (this.selectSql != null) {
                myHash = HashCodeUtil.hashCode(myHash, this.selectSql.toUpperCase());
            }
            if (this.insertSql != null) {
                myHash = HashCodeUtil.hashCode(myHash, this.insertSql.toUpperCase());
            }
            if (this.deleteSql != null) {
                myHash = HashCodeUtil.hashCode(myHash, this.deleteSql.toUpperCase());
            }
            if (this.updateSql != null) {
                myHash = HashCodeUtil.hashCode(myHash, this.updateSql.toUpperCase());
            }
            return myHash;
        }

        private boolean equalsIgnoreCase( String string1,
                                          String string2 ) {
            if (CoreStringUtil.isEmpty(string1) && CoreStringUtil.isEmpty(string2)) {
                return true;
            } else if (CoreStringUtil.isEmpty(string1) || CoreStringUtil.isEmpty(string2)) {
                return false;
            }
            return string1.equalsIgnoreCase(string2);
        }
    }
}
