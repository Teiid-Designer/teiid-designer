/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;

/**
 * RelationalEntityAspect
 */
public abstract class RelationalEntityAspect extends AbstractMetamodelAspect implements SqlAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    protected RelationalEntityAspect( MetamodelEntity entity ) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    /**
     * Get the value of the given feature by looking up the EClass of the EObejct.
     * 
     * @param featureID The ID of the feature to lookup.
     * @param The EObejct whose feature is lookedup.
     * @return The value of the feature
     */
    protected Object getFeatureValue( EObject eObject, // NO_UCD
                                      int featureID ) { // NO_UCD
        EClass eClass = eObject.eClass();
        EStructuralFeature feature = eClass.getEStructuralFeature(featureID);
        return eObject.eGet(feature);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( EObject eObject ) {
        CoreArgCheck.isInstanceOf(RelationalEntity.class, eObject);
        RelationalEntity entity = (RelationalEntity)eObject;
        return entity.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource( EObject eObject ) {
        CoreArgCheck.isInstanceOf(RelationalEntity.class, eObject);
        RelationalEntity entity = (RelationalEntity)eObject;
        return entity.getNameInSource();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID( EObject eObject ) {
        return super.getObjectID(eObject);
    }

    /**
     * Return the relative path to the EObject within its model. The path includes the name of the model that contains this
     * object.
     * 
     * @param eObject
     * @return
     */
    @Override
    public IPath getPath( final EObject eObject ) {
        String pathString = getFullName(eObject).replace(DELIMITER_CHAR, IPath.SEPARATOR);
        return new Path(pathString);
    }

    /**
     * Return the fully qualified name of the metamodel entity.
     * 
     * @param eObject
     * @return The fully qualified name of the entity.
     */
    @Override
    public String getFullName( final EObject eObject ) {
        ModelEditor editor = ModelerCore.getModelEditor();
        String modelName = editor.getModelName(eObject);

        String name = getName(eObject);
        EObject container = eObject.eContainer();
        // append parent information in front of the eObject name
        while (container != null) {
            String partName = getName(container);
            name = partName + DELIMITER_CHAR + name;
            container = container.eContainer();
        }
        // return the fully qualified name with model name appended
        return modelName + DELIMITER_CHAR + name;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable( final EObject eObject ) {
        return true;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected int convertDirectionKindToMetadataConstant( final DirectionKind kind ) {
        switch (kind.getValue()) {
            case DirectionKind.IN:
                return MetadataConstants.PARAMETER_TYPES.IN_PARM;
            case DirectionKind.OUT:
                return MetadataConstants.PARAMETER_TYPES.OUT_PARM;
            case DirectionKind.INOUT:
                return MetadataConstants.PARAMETER_TYPES.INOUT_PARM;
            case DirectionKind.RETURN:
                return MetadataConstants.PARAMETER_TYPES.RETURN_VALUE;
            case DirectionKind.UNKNOWN:
                return MetadataConstants.PARAMETER_TYPES.RESULT_SET;
            default:
                return -1;
        }
    }

    protected DirectionKind convertMetadataContantToDirectionKind( final int direction ) {
        switch (direction) {
            case MetadataConstants.PARAMETER_TYPES.IN_PARM:
                return DirectionKind.IN_LITERAL;
            case MetadataConstants.PARAMETER_TYPES.OUT_PARM:
                return DirectionKind.OUT_LITERAL;
            case MetadataConstants.PARAMETER_TYPES.INOUT_PARM:
                return DirectionKind.INOUT_LITERAL;
            case MetadataConstants.PARAMETER_TYPES.RETURN_VALUE:
                return DirectionKind.RETURN_LITERAL;
            default:
            case MetadataConstants.PARAMETER_TYPES.RESULT_SET:
                return DirectionKind.UNKNOWN_LITERAL;
        }
    }

    protected int convertNullableTypeToMetadataConstant( final NullableType type ) {
        switch (type.getValue()) {
            case NullableType.NO_NULLS:
                return MetadataConstants.NULL_TYPES.NOT_NULL;
            case NullableType.NULLABLE:
                return MetadataConstants.NULL_TYPES.NULLABLE;
            case NullableType.NULLABLE_UNKNOWN:
                return MetadataConstants.NULL_TYPES.UNKNOWN;
            default:
                return -1;
        }
    }

    protected NullableType convertMetadataConstantToNullableType( final int nullType ) {
        switch (nullType) {
            case MetadataConstants.NULL_TYPES.NOT_NULL:
                return NullableType.NO_NULLS_LITERAL;
            case MetadataConstants.NULL_TYPES.NULLABLE:
                return NullableType.NULLABLE_LITERAL;
            case MetadataConstants.NULL_TYPES.UNKNOWN:
                return NullableType.NULLABLE_UNKNOWN_LITERAL;
            default:
                return NullableType.NULLABLE_UNKNOWN_LITERAL;
        }
    }

    protected int convertSearchabilityTypeToMetadataConstant( final SearchabilityType type ) {
        switch (type.getValue()) {
            case SearchabilityType.SEARCHABLE:
                return MetadataConstants.SEARCH_TYPES.SEARCHABLE;
            case SearchabilityType.ALL_EXCEPT_LIKE:
                return MetadataConstants.SEARCH_TYPES.ALLEXCEPTLIKE;
            case SearchabilityType.LIKE_ONLY:
                return MetadataConstants.SEARCH_TYPES.LIKE_ONLY;
            case SearchabilityType.UNSEARCHABLE:
                return MetadataConstants.SEARCH_TYPES.UNSEARCHABLE;
            default:
                return -1;
        }
    }

    protected SearchabilityType convertMetadataConstantToSearchabilityType( final int searchabilityType ) {
        switch (searchabilityType) {
            case MetadataConstants.SEARCH_TYPES.SEARCHABLE:
                return SearchabilityType.SEARCHABLE_LITERAL;
            case MetadataConstants.SEARCH_TYPES.ALLEXCEPTLIKE:
                return SearchabilityType.ALL_EXCEPT_LIKE_LITERAL;
            case MetadataConstants.SEARCH_TYPES.LIKE_ONLY:
                return SearchabilityType.LIKE_ONLY_LITERAL;
            case MetadataConstants.SEARCH_TYPES.UNSEARCHABLE:
                return SearchabilityType.UNSEARCHABLE_LITERAL;
            default:
                return SearchabilityType.SEARCHABLE_LITERAL;
        }
    }

}
