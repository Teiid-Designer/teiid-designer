/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.common.vdb.api.SystemVdbUtility;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;

/**
 * XmlDocumentSqlAspect
 */
public class XmlDocumentSqlAspect extends AbstractXmlDocumentEntitySqlAspect implements SqlTableAspect {

    private final static int CARDINALITY = 0;

    /**
     * Construct an instance of XmlDocumentSqlAspect.
     * 
     */
    public XmlDocumentSqlAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    public boolean isVirtual(EObject eObject) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSystem(EObject eObject) {
        ArgCheck.isInstanceOf(XmlDocument.class, eObject);
        String modelName = getModelName(eObject);
        if (modelName != null && SystemVdbUtility.isSystemModelWithSystemTableType(modelName)) {
            return true;
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isMaterialized(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getRecordType()
     */
    public boolean isRecordType( char type ) {
    	return (type == IndexConstants.RECORD_TYPE.TABLE );
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        ArgCheck.isInstanceOf(XmlDocument.class,eObject);
        final XmlDocument doc = (XmlDocument)eObject;
        return doc.getName();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#supportsUpdate(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsUpdate(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isTransformable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isTransformable(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    public List getColumns(EObject eObject) {
        ArgCheck.isInstanceOf(XmlDocument.class,eObject);
        final XmlDocument doc = (XmlDocument)eObject;
        // collect all the elements and attributes of the document
        // as columns
        List columns = new ArrayList();
        Iterator contentIter = doc.eAllContents();
        while(contentIter.hasNext()) {
            Object entity = contentIter.next();
            if(isElementOrAttribute(entity)) {
                columns.add(entity);    
            }
        }
        return columns;
    }

    /*
     * Entity is an element or an attribute
     */
    private boolean isElementOrAttribute(Object entity) {
        if(entity instanceof XmlElement || entity instanceof XmlAttribute) {
            return true;
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getIndexes(org.eclipse.emf.ecore.EObject)
     */
    public Collection getIndexes(EObject eObject) {
        // There are no indexes in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
    public Collection getUniqueKeys(EObject eObject) {
        // There are no unique keys in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    public Collection getForeignKeys(EObject eObject) {
        // There are no foreign keys in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    public Object getPrimaryKey(EObject eObject) {
        // There are no primary keys in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getAccessPatterns(org.eclipse.emf.ecore.EObject)
     */
    public Collection getAccessPatterns(EObject eObject) {
        // There are no access patterns in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getCardinality(org.eclipse.emf.ecore.EObject)
     */
    public int getCardinality(EObject eObject) {
        return CARDINALITY;
    }
    
    @Override
    protected String getParentFullName(EObject eObject) {
        return ModelerCore.getModelEditor().getModelName(eObject);
    }

    @Override
    protected IPath getParentPath(EObject eObject) {
        return new Path(ModelerCore.getModelEditor().getModelName(eObject));
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getTableType(org.eclipse.emf.ecore.EObject)
     */
    public int getTableType(EObject eObject) {
        ArgCheck.isInstanceOf(XmlDocument.class, eObject);
        return MetadataConstants.TABLE_TYPES.DOCUMENT_TYPE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     */
    public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.TABLE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    public boolean isMappable(EObject eObject, int mappingType) {
        return (mappingType == SqlTableAspect.MAPPINGS.TREE_TRANSFORM);
    }

    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canAcceptTransformationSource(EObject target, EObject source) {
        ArgCheck.isInstanceOf(XmlDocument.class,target);
        ArgCheck.isNotNull(source);
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canBeTransformationSource(EObject source, EObject target) {
        ArgCheck.isInstanceOf(XmlDocument.class,source);
        ArgCheck.isNotNull(target);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        SqlAspect sqlAspect = SqlAspectHelper.getSqlAspect(target);
        if(sqlAspect != null) {
            if(sqlAspect instanceof SqlTableAspect) {
                // target cannot be an XmlDocument
                if(target instanceof XmlDocument) {
                    return canAcceptTransformationSource(target, source);
                }                
                return ((SqlTableAspect) sqlAspect).isVirtual(target);
            } else if(sqlAspect instanceof SqlProcedureAspect) {
                return ((SqlProcedureAspect) sqlAspect).isVirtual(target);                
            }
        }
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    public void setSupportsUpdate(EObject eObject, boolean supportsUpdate) {
        // documents are never updatable, this wont be set
    }

}
