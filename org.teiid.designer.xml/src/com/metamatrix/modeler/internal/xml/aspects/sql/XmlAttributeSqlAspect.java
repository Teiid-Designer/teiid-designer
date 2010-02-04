/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.aspects.sql;

import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.xml.PluginConstants;

/**
 * XmlContainerNodeSqlAspect
 */
public class XmlAttributeSqlAspect extends AbstractXmlDocumentEntitySqlAspect implements SqlColumnAspect {

    private static char NAME_PREFIX = '@';

    /**
     * Construct an instance of XmlContainerNodeSqlAspect.
     * 
     */
    public XmlAttributeSqlAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

	/**
	 * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
	 */
	public String getName(final EObject eObject) {
		ArgCheck.isInstanceOf(XmlAttribute.class, eObject);

		IPath path = new Path(getShortName(eObject));
		EObject parent = eObject.eContainer();
		SqlAspect parentAspect = AspectManager.getSqlAspect(parent);
		while ( parentAspect != null ) {
			if(parentAspect instanceof SqlColumnAspect) {
				String name = null;				
				if(parentAspect instanceof AbstractXmlDocumentEntitySqlAspect) {
					name = ((AbstractXmlDocumentEntitySqlAspect) parentAspect).getShortName(parent);
				} else {
					name = parentAspect.getName(parent);					
				}				
				path = new Path("").append(name).append(path); //$NON-NLS-1$
			} else if (parentAspect instanceof SqlTableAspect) {
				break;
			}
			// Walk up to the parent ...
			parent = parent.eContainer();
			parentAspect = AspectManager.getSqlAspect(parent);
		}
		
		return path.toString().replace(IPath.SEPARATOR,'.');
	}

	@Override
    protected String getShortName(final EObject eObject) {
		ArgCheck.isInstanceOf(XmlAttribute.class, eObject);
		XmlAttribute xmlAttribute = (XmlAttribute) eObject;
		String name = NAME_PREFIX + xmlAttribute.getName();
		return name;
	}

	@Override
    protected String getParentFullName(EObject eObject) {
		EObject parent = eObject.eContainer();
		if ( parent != null ) {
			SqlAspect parentAspect = AspectManager.getSqlAspect(parent);
			while ( parentAspect != null ) {
				if (parentAspect instanceof SqlTableAspect) {
					return parentAspect.getFullName(parent);
				}
				parent = parent.eContainer();
				if ( parent != null ) {
					parentAspect = AspectManager.getSqlAspect(parent);
				} else {
					return null;
				}
			}
		}
		return null;
	}

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    public int getCharOctetLength(EObject eObject) {
        return XmlElementSqlAspect.CHAR_OCTET_LENGTH;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getNativeType(EObject eObject) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(XmlAttribute.class, eObject);
        XmlAttribute xmlAttribute = (XmlAttribute) eObject;
        // Defect 23709 - this method was getting called from the property page after the XML Attribute was deleted, so the
        // attribute's element was NULL.
        XmlElement xmlElement = xmlAttribute.getElement();
        if( xmlElement != null ) {
            XmlElementSqlAspect elementAspect = getXmlElementAspect(xmlElement);
            Map elementMap = elementAspect.getMappingInfo(xmlAttribute);
            Object lookupObj = elementMap.get(xmlAttribute);
            if(lookupObj != null) {
                Assertion.isInstanceOf(lookupObj, MappingClassColumn.class, null);
                MappingClassColumn mapingColumn = (MappingClassColumn) lookupObj;
                return mapingColumn.getType();
            }
    
            try {
                return ModelerCore.getDatatypeManager(eObject).getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
            } catch(Throwable e) {}// ignore
        }
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeName(EObject eObject) {
        EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        final String dtName = dtMgr.getName(datatype);
        return dtName == null ? DatatypeConstants.BuiltInNames.STRING : dtName;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeObjectID(EObject eObject) {
        EObject datatype = getDatatype(eObject);
        try {
            if(datatype == null) {
                datatype = ModelerCore.getWorkspaceDatatypeManager().getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING);
            }
        } catch(Throwable e) {}// ignore
        
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype) == null ? "" : dtMgr.getUuidString(datatype); //$NON-NLS-1$
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getDefaultValue(EObject eObject) {
        return XmlElementSqlAspect.DEFAULT_VALUE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    public String getFormat(EObject eObject) {
        return XmlElementSqlAspect.FORMAT;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    public int getLength(EObject eObject) {
        return XmlElementSqlAspect.LENGTH;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMaxValue(EObject eObject) {
        return XmlElementSqlAspect.MIN_VALUE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMinValue(EObject eObject) {
        return XmlElementSqlAspect.MAX_VALUE;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getDistinctValues(EObject eObject) {
        return XmlElementSqlAspect.DISTINCT_VALUES;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getNullValues(EObject eObject) {
        return XmlElementSqlAspect.NULL_VALUES;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    public int getNullType(EObject eObject) {
        return XmlElementSqlAspect.NULL_TYPE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    public int getPosition(EObject eObject) {
        return XmlElementSqlAspect.POSITION;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    public int getPrecision(EObject eObject) {
        return XmlElementSqlAspect.PRECISION;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    public int getRadix(EObject eObject) {
        return XmlElementSqlAspect.RADIX;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeType(EObject eObject) {
        final EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        final String rtt = dtMgr.getRuntimeTypeName(datatype);
        return rtt == null ? DatatypeConstants.RuntimeTypeNames.STRING : rtt;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    public int getScale(EObject eObject) {
        return XmlElementSqlAspect.SCALE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     */
    public int getSearchType(EObject eObject) {
        return XmlElementSqlAspect.SEARCH_TYPE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isAutoIncrementable(EObject eObject) {
        return XmlElementSqlAspect.AUTO_INCREMENT;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCaseSensitive(EObject eObject) {
        return XmlElementSqlAspect.CASE_SENSITIVE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCurrency(EObject eObject) {
        return XmlElementSqlAspect.CURRENCY;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    public boolean isFixedLength(EObject eObject) {
        return XmlElementSqlAspect.FIXED_LENGTH;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSelectable(EObject eObject) {
        return XmlElementSqlAspect.SELECTABLE;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSigned(EObject eObject) {
        return XmlElementSqlAspect.SIGNED;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isTranformationInputParameter(EObject eObject) {
        return XmlElementSqlAspect.TRANSFORMATION_INPUT_PARAMETER;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isUpdatable(EObject eObject) {
        return XmlElementSqlAspect.UPDATABLE;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    public boolean canSetDatatype() {
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setDatatype(org.eclipse.emf.ecore.EObject, com.metamatrix.metamodels.core.Datatype)
     */
    public void setDatatype(EObject eObject, EObject datatype) {
        throw new UnsupportedOperationException(PluginConstants.Util.getString("XmlAttributeSqlAspect.Datatype_cannot_be_set_on_an_XMLAttribute_1")); //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    public boolean canSetLength() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    public void setLength(EObject eObject, int length) {
        throw new UnsupportedOperationException(PluginConstants.Util.getString("XmlAttributeSqlAspect.Length_cannot_be_set_on_an_XMLAttribute_2")); //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    public boolean canSetNullType() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    public void setNullType(EObject eObject, int nullType) {
        throw new UnsupportedOperationException(PluginConstants.Util.getString("XmlAttributeSqlAspect.NullType_cannot_be_set_on_an_XMLAttribute_3")); //$NON-NLS-1$
    }

    private XmlElementSqlAspect getXmlElementAspect(EObject eObject) {
        ArgCheck.isInstanceOf(XmlElement.class, eObject);
        XmlElement element = (XmlElement) eObject;
        // get xmlElement aspect for the element
        return (XmlElementSqlAspect) AspectManager.getSqlAspect(element);
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        // there is no datattype feature on xml attributes
        return false;
    }

}
