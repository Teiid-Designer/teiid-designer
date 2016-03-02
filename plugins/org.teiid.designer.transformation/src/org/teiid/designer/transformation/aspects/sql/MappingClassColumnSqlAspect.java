/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.MappingClassSet;
import org.teiid.designer.metamodels.transformation.TransformationPackage;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlValueHolder;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * MappingClassColumnSqlAspect
 *
 * @since 8.0
 */
public class MappingClassColumnSqlAspect extends MappingClassObjectSqlAspect implements SqlColumnAspect {
    /**
     * Construct an instance of MappingClassColumnSqlAspect.
     */
    public MappingClassColumnSqlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType( char recordType ) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable( final EObject eObject ) {
        return true;
    }

    private List getElementsFromMappingClassColumn( MappingClassColumn column ) {
        EObject mc = column.eContainer();
        CoreArgCheck.isInstanceOf(MappingClass.class, mc);
        EObject mcs = ((MappingClass)mc).eContainer();
        CoreArgCheck.isInstanceOf(MappingClassSet.class, mcs);
        EObject target = ((MappingClassSet)mcs).getTarget();
        CoreArgCheck.isInstanceOf(XmlDocument.class, target);
        XmlDocument document = (XmlDocument)target;
        Resource documentResource = document.eResource();

        // model contents for this resource
        ModelContents mdlContents = new ModelContents(documentResource);
        Iterator contentIter = mdlContents.getTransformations(document).iterator();

        // get the mapping root associated with the transformation
        while (contentIter.hasNext()) {
            MappingRoot mappingRoot = (MappingRoot)contentIter.next();
            // if there is a mapping root
            if (mappingRoot != null && mappingRoot instanceof TreeMappingRoot) {
                for (Iterator mappingIter = mappingRoot.getNested().iterator(); mappingIter.hasNext();) {
                    Mapping nestedMapping = (Mapping)mappingIter.next();
                    // mapping Class columns
                    List inputColumns = nestedMapping.getInputs();
                    // xml elements
                    List outputElements = nestedMapping.getOutputs();
                    if (inputColumns.contains(column)) {
                        return outputElements;
                    }
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSelectable( EObject eObject ) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isUpdatable( EObject eObject ) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getNullType( EObject eObject ) {
        return 1;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isAutoIncrementable( EObject eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCaseSensitive( EObject eObject ) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSigned( EObject eObject ) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCurrency( EObject eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isFixedLength( EObject eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isTranformationInputParameter( EObject eObject ) {
        return !isSelectable(eObject);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getSearchType( EObject eObject ) {
        return SearchabilityType.SEARCHABLE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDefaultValue( EObject eObject ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);

        List elements = getElementsFromMappingClassColumn((MappingClassColumn)eObject);

        if (elements.size() != 1) {
            return null;
        }

        XmlValueHolder valueHolder = (XmlValueHolder)elements.get(0);

        if ((valueHolder.isValueDefault() || valueHolder.isValueFixed()) && valueHolder.getValue() != null) {
            return valueHolder.getValue();
        }

        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getMinValue( EObject eObject ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getMaxValue( EObject eObject ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getFormat( EObject eObject ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getLength( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getScale( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getRadix( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getDistinctValues( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getNullValues( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getNativeType( EObject eObject ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeName( EObject eObject ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        MappingClassColumn column = (MappingClassColumn)eObject;

        final EObject dataType = column.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column, true);
        final String dtName = dtMgr.getName(dataType);
        return dtName == null ? "" : dtName; //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    @Override
	public boolean canSetDatatype() {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setDatatype(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.metamodels.core.Datatype)
     */
    @Override
	public void setDatatype( EObject eObject,
                             EObject datatype ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        MappingClassColumn column = (MappingClassColumn)eObject;
        column.setType(datatype);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getDatatype( EObject eObject ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        MappingClassColumn column = (MappingClassColumn)eObject;

        return column.getType();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getRuntimeType( EObject eObject ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        MappingClassColumn column = (MappingClassColumn)eObject;
        final EObject datatype = column.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column, true);
        final String rtType = dtMgr.getRuntimeTypeName(datatype);
        return rtType == null ? "" : rtType; //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeObjectID( EObject eObject ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        MappingClassColumn column = (MappingClassColumn)eObject;
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column, true);
        return dtMgr.getUuidString(column.getType());
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPrecision( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getCharOctetLength( EObject eObject ) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPosition( EObject eObject ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        MappingClassColumn column = (MappingClassColumn)eObject;

        MappingClass mappingClass = column.getMappingClass();
        CoreArgCheck.isNotNull(mappingClass);
        // correct from '0' to '1' based position
        return mappingClass.getColumns().indexOf(column) + 1;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void setLength( EObject eObject,
                           int length ) {
        throw new UnsupportedOperationException(
                                                TransformationPlugin.Util.getString("MappingClassColumnSqlAspect.Length_cannot_be_set_on_a_MappingClassColumn_1")); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    @Override
	public boolean canSetNullType() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void setNullType( EObject eObject,
                             int nullType ) {
        throw new UnsupportedOperationException(
                                                TransformationPlugin.Util.getString("MappingClassColumnSqlAspect.NullType_cannot_be_set_on_a_MappingClassColumn_2")); //$NON-NLS-1$
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject( EObject targetObject,
                              EObject sourceObject ) {
        CoreArgCheck.isNotNull(sourceObject);
        SqlAspect columnAspect = AspectManager.getSqlAspect(sourceObject);
        CoreArgCheck.isInstanceOf(SqlColumnAspect.class, columnAspect);
        // get the source column type
        EObject srcType = ((SqlColumnAspect)columnAspect).getDatatype(sourceObject);
        setDatatype(targetObject, srcType);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public boolean isDatatypeFeature( final EObject eObject,
                                      final EStructuralFeature eFeature ) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case TransformationPackage.MAPPING_CLASS_COLUMN__TYPE:
                    return true;
            }
        }
        return false;
    }

}
