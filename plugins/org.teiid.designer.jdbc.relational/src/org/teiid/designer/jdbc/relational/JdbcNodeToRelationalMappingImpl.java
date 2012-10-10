/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.jdbc.metadata.JdbcCatalog;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcProcedure;
import org.teiid.designer.jdbc.metadata.JdbcSchema;
import org.teiid.designer.jdbc.metadata.JdbcTable;
import org.teiid.designer.metamodels.relational.RelationalPackage;


/**
 * JdbcNodeToRelationalMappingImpl
 *
 * @since 8.0
 */
public class JdbcNodeToRelationalMappingImpl implements JdbcNodeToRelationalMapping {

    public static final String UBIQUITOUS_VIEW_NAME = "View"; //$NON-NLS-1$
    public static final String DEFAULT_VIEW_NAME = org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Util.getString("JdbcNodeToRelationalMappingImpl.ViewTableType"); //$NON-NLS-1$

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
     * @See org.teiid.designer.jdbc.relational.JdbcNodeToRelationalMapping#getRelationalClassForJdbcNode(org.teiid.designer.jdbc.metadata.JdbcNode)
     */
    @Override
	public EClass getRelationalClassForJdbcNode(final JdbcNode jdbcNode) {
        CoreArgCheck.isNotNull(jdbcNode);
        
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
     * @See org.teiid.designer.jdbc.relational.JdbcNodeToRelationalMapping#setRelationalClassForJdbcTableType(java.lang.String, java.lang.String)
     */
    @Override
	public void setRelationalClassForJdbcTableType(final String tableType, final String eClassName) {
        CoreArgCheck.isNotNull(tableType);
        CoreArgCheck.isNotNull(eClassName);
        CoreArgCheck.isNotZeroLength(eClassName);
        this.tableTypeNameToEClassName.put(tableType,eClassName);
        this.upperTableTypeNameToEClassName.put(tableType.toUpperCase(),eClassName);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.relational.JdbcNodeToRelationalMapping#getRelationalClassForJdbcTableType(java.lang.String)
     */
    @Override
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
