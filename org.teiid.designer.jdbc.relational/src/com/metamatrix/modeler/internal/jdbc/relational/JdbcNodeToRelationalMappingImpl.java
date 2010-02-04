/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.relational;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcProcedure;
import com.metamatrix.modeler.jdbc.metadata.JdbcSchema;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.JdbcNodeToRelationalMapping;

/**
 * JdbcNodeToRelationalMappingImpl
 */
public class JdbcNodeToRelationalMappingImpl implements JdbcNodeToRelationalMapping {

    public static final String UBIQUITOUS_VIEW_NAME = "View"; //$NON-NLS-1$
    public static final String DEFAULT_VIEW_NAME = ModelerJdbcRelationalConstants.Util.getString("JdbcNodeToRelationalMappingImpl.ViewTableType"); //$NON-NLS-1$

    private final Map tableTypeNameToEClassName;
    private final Map upperTableTypeNameToEClassName;

    /**
     * Construct an instance of JdbcNodeToRelationalMappingImpl.
     * 
     */
    public JdbcNodeToRelationalMappingImpl() {
        super();
        tableTypeNameToEClassName = new HashMap();
        upperTableTypeNameToEClassName = new HashMap();
        
        // Register the standard view names
        final EClass viewClass = findClassifierById(RelationalPackage.VIEW);
        final String viewClassName = viewClass.getName();
        setRelationalClassForJdbcTableType(UBIQUITOUS_VIEW_NAME,viewClassName);
        setRelationalClassForJdbcTableType(DEFAULT_VIEW_NAME,viewClassName);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.JdbcNodeToRelationalMapping#getRelationalClassForJdbcNode(com.metamatrix.modeler.jdbc.metadata.JdbcNode)
     */
    public EClass getRelationalClassForJdbcNode(final JdbcNode jdbcNode) {
        ArgCheck.isNotNull(jdbcNode);
        
        if ( jdbcNode instanceof JdbcCatalog ) {
            return findClassifierById(RelationalPackage.CATALOG);
        }
        if ( jdbcNode instanceof JdbcSchema ) {
            return findClassifierById(RelationalPackage.SCHEMA);
        }
        if ( jdbcNode instanceof JdbcProcedure ) {
            return findClassifierById(RelationalPackage.PROCEDURE);
        }
        if ( jdbcNode instanceof JdbcTable) {
            final String typeName = jdbcNode.getTypeName(); // should be the table type
            
            // See if the table type was bound to an EClass ...
            final String eClassName = getRelationalClassForJdbcTableType(typeName);
            if ( eClassName != null ) {
                final EClassifier eClassForTypeName = RelationalPackage.eINSTANCE.getEClassifier(eClassName);
                if ( eClassForTypeName != null && eClassForTypeName instanceof EClass ) {
                    return (EClass) eClassForTypeName;
                }
            }
            // Falls through to bind to Table
            return findClassifierById(RelationalPackage.TABLE);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.JdbcNodeToRelationalMapping#setRelationalClassForJdbcTableType(java.lang.String, java.lang.String)
     */
    public void setRelationalClassForJdbcTableType(final String tableType, final String eClassName) {
        ArgCheck.isNotNull(tableType);
        ArgCheck.isNotNull(eClassName);
        ArgCheck.isNotZeroLength(eClassName);
        this.tableTypeNameToEClassName.put(tableType,eClassName);
        this.upperTableTypeNameToEClassName.put(tableType.toUpperCase(),eClassName);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.relational.JdbcNodeToRelationalMapping#getRelationalClassForJdbcTableType(java.lang.String)
     */
    public String getRelationalClassForJdbcTableType( final String tableType) {
        if ( tableType == null ) {
            return null;
        }
        final String exactMatch = (String)this.tableTypeNameToEClassName.get(tableType);
        if ( exactMatch != null ) {
            return exactMatch;
        }
        return (String)this.upperTableTypeNameToEClassName.get(tableType.toUpperCase());
    }

    
    /**
     * @param i
     * @return
     */
    protected EClass findClassifierById(int classifierId) {
        final Iterator iter = RelationalPackage.eINSTANCE.getEClassifiers().iterator();
        while (iter.hasNext()) {
            final EClassifier classifier = (EClassifier)iter.next();
            if ( classifier.getClassifierID() == classifierId ) {
                if ( classifier instanceof EClass ) {
                    return (EClass) classifier;
                }
            }
        }
        return null;
    }

}
