/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.common.vdb.SystemVdbUtility;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.xml.XmlAttribute;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlElement;


/**
 * XmlDocumentSqlAspect
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVirtual(EObject eObject) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isSystem(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSystem(EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlDocument.class, eObject);
        String modelName = getModelName(eObject);
        if (modelName != null && SystemVdbUtility.isSystemModelWithSystemTableType(modelName)) {
            return true;
        }
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMaterialized(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isMaterialized(EObject eObject) {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getMaterializedTableId(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getMaterializedTableId(EObject eObject) {
        return null;
    } 

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getRecordType()
     */
    @Override
	public boolean isRecordType( char type ) {
    	return (type == IndexConstants.RECORD_TYPE.TABLE );
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlDocument.class,eObject);
        final XmlDocument doc = (XmlDocument)eObject;
        return doc.getName();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#supportsUpdate(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsUpdate(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isTransformable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isTransformable(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getColumns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getColumns(EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlDocument.class,eObject);
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getIndexes(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getIndexes(EObject eObject) {
        // There are no indexes in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getUniqueKeys(EObject eObject) {
        // There are no unique keys in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getForeignKeys(EObject eObject) {
        // There are no foreign keys in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getPrimaryKey(EObject eObject) {
        // There are no primary keys in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getAccessPatterns(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Collection getAccessPatterns(EObject eObject) {
        // There are no access patterns in a document
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getCardinality(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#getTableType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getTableType(EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlDocument.class, eObject);
        return MetadataConstants.TABLE_TYPES.DOCUMENT_TYPE;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect#getColumnSetType()
     */
    @Override
	public int getColumnSetType() {
        return MetadataConstants.COLUMN_SET_TYPES.TABLE;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public boolean isMappable(EObject eObject, int mappingType) {
        return (mappingType == SqlTableAspect.MAPPINGS.TREE_TRANSFORM);
    }

    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canAcceptTransformationSource(EObject target, EObject source) {
        CoreArgCheck.isInstanceOf(XmlDocument.class,target);
        CoreArgCheck.isNotNull(source);
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canBeTransformationSource(EObject source, EObject target) {
        CoreArgCheck.isInstanceOf(XmlDocument.class,source);
        CoreArgCheck.isNotNull(target);
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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public void setSupportsUpdate(EObject eObject, boolean supportsUpdate) {
        // documents are never updatable, this wont be set
    }

}
