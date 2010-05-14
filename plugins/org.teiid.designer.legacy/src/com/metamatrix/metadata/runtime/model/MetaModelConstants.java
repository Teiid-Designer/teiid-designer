/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package com.metamatrix.metadata.runtime.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MetaModelConstants {

    public static final String DELIMITER = "."; //$NON-NLS-1$
    public static final String PRODUCT_VERSION = "3.0"; //$NON-NLS-1$
    public static final String NAMESPACE_URI_PREFIX = "http://www.metamatrix.com/metabase/" + PRODUCT_VERSION + "/metamodels/"; //$NON-NLS-1$ //$NON-NLS-2$
    public static final String METAMODEL_FILE_EXTENSION = ".xml"; //$NON-NLS-1$

    /** Property names common to multiple metamodel components */
    public static final String UUID_ATTRIBUTE = "uuid"; //$NON-NLS-1$
    public static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
    public static final String NAMESPACE_ATTRIBUTE = "location"; //$NON-NLS-1$
    public static final String DESCRIPTION_ATTRIBUTE = "description"; //$NON-NLS-1$
    public static final String KEYWORDS_ATTRIBUTE = "keywords"; //$NON-NLS-1$
    public static final String ITEM_TYPE_ATTRIBUTE = "itemType"; //$NON-NLS-1$
    public static final String MEMBER_TYPE_ATTRIBUTE = "memberTypes"; //$NON-NLS-1$
    public static final String RUNTIME_TYPE_ATTRIBUTE = "runtimeDataType"; //$NON-NLS-1$
    public static final String BASE_TYPE_ATTRIBUTE = "baseType"; //$NON-NLS-1$
    public static final String TYPE_ATTRIBUTE = "type"; //$NON-NLS-1$
    public static final String REFERENCE_TYPE_ATTRIBUTE = "reference"; //$NON-NLS-1$
    public static final String TARGET_NAMESPACE_ATTRIBUTE = "targetNamespace"; //$NON-NLS-1$
    public static final String COMPONENT_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
    public static final String ALIAS_ATTRIBUTE = "alias"; //$NON-NLS-1$
    public static final String RECURSIVE_ATTRIBUTE = "recursive"; //$NON-NLS-1$
    public static final String RECURSION_CRITERIA_ATTRIBUTE = "recursionCriteria"; //$NON-NLS-1$
    public static final String RECURSION_LIMIT_ATTRIBUTE = "recursionLimit"; //$NON-NLS-1$
    public static final String RECURSION_LIMIT_ERROR_ATTRIBUTE = "recursionLimitError"; //$NON-NLS-1$

    /** Property names associated with DataTypes metamodel components */
    public static final String IN_DIRECTION_KIND = "In"; //$NON-NLS-1$
    public static final String OUT_DIRECTION_KIND = "Out"; //$NON-NLS-1$
    public static final String INOUT_DIRECTION_KIND = "InOut"; //$NON-NLS-1$
    public static final String RETURN_DIRECTION_KIND = "Return"; //$NON-NLS-1$

    /** Property names associated with XML Attribute kind metamodel components */
    public static final String PROHIBITED_ATTRIBUTE_KIND = "prohibited"; //$NON-NLS-1$   
    public static final String OPTIONAL_ATTRIBUTE_KIND = "optional"; //$NON-NLS-1$
    public static final String REQUIRED_ATTRIBUTE_KIND = "required"; //$NON-NLS-1$
    public static final String DEFAULT_ATTRIBUTE_KIND = OPTIONAL_ATTRIBUTE_KIND;

    /** Property names associated with Foundation metamodel components */
    public static final String FOUNDATION_CHANGEABILITY_ATTRIBUTE = "changeability"; //$NON-NLS-1$
    public static final String FOUNDATION_MULTIPLICITY_ATTRIBUTE = "multiplicity"; //$NON-NLS-1$
    public static final String FOUNDATION_DIRECTION_ATTRIBUTE = "direction"; //$NON-NLS-1$
    public static final String FOUNDATION_INITIAL_VALUE_ATTRIBUTE = "initialValue"; //$NON-NLS-1$
    public static final String FOUNDATION_DEFAULT_VALUE_ATTRIBUTE = "defaultValue"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_AND_ATTRIBUTE = "supportsAnd"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_OR_ATTRIBUTE = "supportsOr"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_SET_ATTRIBUTE = "supportsSet"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_WHEREALL_ATTRIBUTE = "supportsWhereAll"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_ORDERBY_ATTRIBUTE = "supportsOrderBy"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_DISTINCT_ATTRIBUTE = "supportsDistinct"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_JOIN_ATTRIBUTE = "supportsJoin"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_OUTERJOIN_ATTRIBUTE = "supportsOuterJoin"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_TRANSACTION_ATTRIBUTE = "supportsTransaction"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_SUBSCRIPTION_ATTRIBUTE = "supportsSubscription"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_UPDATE_ATTRIBUTE = "supportsUpdate"; //$NON-NLS-1$
    public static final String FOUNDATION_SUPPORTS_SELECT_ATTRIBUTE = "supportsSelect"; //$NON-NLS-1$
    public static final String FOUNDATION_MAX_SETSIZE_ATTRIBUTE = "maxSetSize"; //$NON-NLS-1$
    public static final String FOUNDATION_SCALE_ATTRIBUTE = "scale"; //$NON-NLS-1$
    public static final String FOUNDATION_LENGTH_ATTRIBUTE = "length"; //$NON-NLS-1$
    public static final String FOUNDATION_LENGTH_FIXED_ATTRIBUTE = "isLengthFixed"; //$NON-NLS-1$
    public static final String FOUNDATION_IS_NULLABLE_ATTRIBUTE = "isNullable"; //$NON-NLS-1$
    public static final String FOUNDATION_IS_CASE_SENSITIVE_ATTRIBUTE = "isCaseSensitive"; //$NON-NLS-1$
    public static final String FOUNDATION_IS_SIGNED_ATTRIBUTE = "isSigned"; //$NON-NLS-1$
    public static final String FOUNDATION_IS_CURRENCY_ATTRIBUTE = "isCurrency"; //$NON-NLS-1$
    public static final String FOUNDATION_IS_AUTO_INCREMENTED_ATTRIBUTE = "isAutoIncremented"; //$NON-NLS-1$
    public static final String FOUNDATION_MIN_RANGE_ATTRIBUTE = "minRange"; //$NON-NLS-1$
    public static final String FOUNDATION_MAX_RANGE_ATTRIBUTE = "maxRange"; //$NON-NLS-1$
    public static final String FOUNDATION_FORMAT_ATTRIBUTE = "format"; //$NON-NLS-1$
    public static final String FOUNDATION_SEARCH_TYPE_ATTRIBUTE = "searchType"; //$NON-NLS-1$
    public static final String FOUNDATION_CARDINALITY_ATTRIBUTE = "cardinality"; //$NON-NLS-1$

    public static final String FEATURE_ASSOCIATION_END = "feature"; //$NON-NLS-1$
    public static final String UNIQUE_KEY_ASSOCIATION_END = "uniqueKey"; //$NON-NLS-1$
    public static final String KEY_RELATIONSHIP_ASSOCIATION_END = "keyRelationship"; //$NON-NLS-1$
    // The "ClassifierFeature" association between Classifier and Feature
    public static final String CLASSIFIER_FEATURE_FEATURE_ASSOCIATION_END = FEATURE_ASSOCIATION_END;
    public static final String CLASSIFIER_FEATURE_OWNER_ASSOCIATION_END = "owner"; //$NON-NLS-1$
    // The "KeyRelationshipFeatures" association between KeyRelationship and StructuralFeature
    public static final String KEY_RELATIONSHIP_FEATURE_ASSOCIATION_END = FEATURE_ASSOCIATION_END;
    public static final String KEY_RELATIONSHIP_KEYREL_ASSOCIATION_END = KEY_RELATIONSHIP_ASSOCIATION_END;
    // The "UniqueKeyRelationship" association between KeyRelationship and UniqueKey
    public static final String UNIQUE_KEY_RELATIONSHIP_KEY_ASSOCIATION_END = UNIQUE_KEY_ASSOCIATION_END;
    public static final String UNIQUE_KEY_RELATIONSHIP_KEYREL_ASSOCIATION_END = KEY_RELATIONSHIP_ASSOCIATION_END;
    // The "UniqueFeature" association between StructuralFeature and UniqueKey
    public static final String UNIQUE_FEATURE_UNIQUEKEY_ASSOCIATION_END = UNIQUE_KEY_ASSOCIATION_END;
    public static final String UNIQUE_FEATURE_FEATURE_ASSOCIATION_END = FEATURE_ASSOCIATION_END;
    // The "IndexedFeatureInfo" association between Index and IndexFeature
    public static final String INDEXED_FEATURE_INFO_INDEX_ASSOCIATION_END = "index"; //$NON-NLS-1$
    public static final String INDEXED_FEATURE_INFO_INDEXFEATURE_ASSOCIATION_END = "indexedFeature"; //$NON-NLS-1$
    // The "IndexSpansClass" association between Index and Class
    public static final String INDEX_SPANS_CLASS_INDEX_ASSOCIATION_END = "index"; //$NON-NLS-1$
    public static final String INDEX_SPANS_CLASS_CLASS_ASSOCIATION_END = "spannedClass"; //$NON-NLS-1$
    // The "IndexedFeatures" association between IndexFeature and StructuralFeature
    public static final String INDEXED_FEATURE_FEATURE_ASSOCIATION_END = FEATURE_ASSOCIATION_END;
    public static final String INDEXED_FEATURE_INDEX_ASSOCIATION_END = "indexedFeature"; //$NON-NLS-1$
    // The "ParameterOwnership" association between BehavioralFeature and Parameter
    public static final String PARAMETER_BEHAVIORALFEATURE_PARAMETER_ASSOCIATION_END = "parameter"; //$NON-NLS-1$
    public static final String PARAMETER_BEHAVIORALFEATURE_FEATURE_ASSOCIATION_END = "behavioralFeature"; //$NON-NLS-1$

    /** Property names associated with Diagram metamodel components */
    public static final String DIAGRAM_USERSTRING_ATTRIBUTE = "userstring"; //$NON-NLS-1$
    public static final String DIAGRAM_USERTYPE_ATTRIBUTE = "usertype"; //$NON-NLS-1$
    public static final String DIAGRAM_X_ATTRIBUTE = "x"; //$NON-NLS-1$
    public static final String DIAGRAM_Y_ATTRIBUTE = "y"; //$NON-NLS-1$
    public static final String DIAGRAM_H_ATTRIBUTE = "h"; //$NON-NLS-1$
    public static final String DIAGRAM_W_ATTRIBUTE = "w"; //$NON-NLS-1$
    public static final String DIAGRAM_OBJECTID_ATTRIBUTE = "objectID"; //$NON-NLS-1$
    public static final String DIAGRAM_MODEL_OBJECTID_ATTRIBUTE = "modelObjectID"; //$NON-NLS-1$
    // The "DiagramContents" association between Diagram and DiagramComponent
    public static final String DIAGRAM_COMPONENT_ASSOCIATION_END = "component"; //$NON-NLS-1$
    public static final String DIAGRAM_DIAGRAM_ASSOCIATION_END = "diagram"; //$NON-NLS-1$

    /** Property names associated with Virtual metamodel components */
    public static final String VIRTUAL_QUERY_TREE_ATTRIBUTE = "queryTree"; //$NON-NLS-1$
    public static final String VIRTUAL_UPDATE_QUERY_TREE_ATTRIBUTE = "updateQueryTree"; //$NON-NLS-1$
    public static final String VIRTUAL_INSERT_QUERY_TREE_ATTRIBUTE = "insertQueryTree"; //$NON-NLS-1$
    public static final String VIRTUAL_DELETE_QUERY_TREE_ATTRIBUTE = "deleteQueryTree"; //$NON-NLS-1$

    public static final String VIRTUAL_SQL_ATTRIBUTE = "sql"; //$NON-NLS-1$
    public static final String VIRTUAL_ALIAS_ATTRIBUTE = ALIAS_ATTRIBUTE;
    public static final String VIRTUAL_LABEL_ATTRIBUTE = "label"; //$NON-NLS-1$
    public static final String VIRTUAL_OBJECTID_ATTRIBUTE = "objectID"; //$NON-NLS-1$
    public static final String VIRTUAL_MODEL_OBJECTID_ATTRIBUTE = "modelObjectID"; //$NON-NLS-1$
    public static final String VIRTUAL_OBJECT_PATH_ATTRIBUTE = "objectPath"; //$NON-NLS-1$
    public static final String VIRTUAL_IS_INPUT_ATTRIBUTE = "isInput"; //$NON-NLS-1$

    public static final String VIRTUAL_UPDATE_SQL_ATTRIBUTE = "updateSQLStatement"; //$NON-NLS-1$
    public static final String VIRTUAL_INSERT_SQL_ATTRIBUTE = "insertSQLStatement"; //$NON-NLS-1$
    public static final String VIRTUAL_DELETE_SQL_ATTRIBUTE = "deleteSQLStatement"; //$NON-NLS-1$
    public static final String VIRTUAL_ALLOWS_UPDATE_ATTRIBUTE = "allowsUpdate"; //$NON-NLS-1$
    public static final String VIRTUAL_ALLOWS_INSERT_ATTRIBUTE = "allowsInsert"; //$NON-NLS-1$
    public static final String VIRTUAL_ALLOWS_DELETE_ATTRIBUTE = "allowsDelete"; //$NON-NLS-1$

    public static final String VIRTUAL_SELECT_STRING_ATTRIBUTE = "selectSQLString"; //$NON-NLS-1$
    public static final String VIRTUAL_UPDATE_STRING_ATTRIBUTE = "updateSQLString"; //$NON-NLS-1$
    public static final String VIRTUAL_INSERT_STRING_ATTRIBUTE = "insertSQLString"; //$NON-NLS-1$
    public static final String VIRTUAL_DELETE_STRING_ATTRIBUTE = "deleteSQLString"; //$NON-NLS-1$

    public static final String TRANSFORM_ASSOCIATION_END = "transform"; //$NON-NLS-1$
    // The "TransformationContents" association between Transformation and TransformationOperation
    public static final String TRANSFORMATION_CONTENTS_OPERATION_ASSOCIATION_END = "operations"; //$NON-NLS-1$
    public static final String TRANSFORMATION_CONTENTS_TRANSFORM_ASSOCIATION_END = TRANSFORM_ASSOCIATION_END;
    // The "TransformationClass" association between Transformation and TemporaryGroup
    public static final String TRANSFORMATION_CLASS_CLASS_ASSOCIATION_END = "temporaryGroups"; //$NON-NLS-1$
    public static final String TRANSFORMATION_CLASS_TRANSFORM_ASSOCIATION_END = TRANSFORM_ASSOCIATION_END;
    // the "QueryOperationLinks" association between QueryOperation and TransformationLink
    public static final String QUERY_OPERATION_LINK_ASSOCIATION_END = "links"; //$NON-NLS-1$
    public static final String QUERY_OPERATION_QUERY_ASSOCIATION_END = TRANSFORM_ASSOCIATION_END;

    /** Property names associated with MetaMatrix Functions metamodel components */
    public static final String FUNCTION_CATEGORY_ATTRIBUTE = "category"; //$NON-NLS-1$
    public static final String FUNCTION_INVOCATION_CLASS_ATTRIBUTE = "invocationClass"; //$NON-NLS-1$
    public static final String FUNCTION_INVOCATION_METHOD_ATTRIBUTE = "invocationMethod"; //$NON-NLS-1$
    public static final String FUNCTION_DIRECTION_ATTRIBUTE = "direction"; //$NON-NLS-1$
    // the "ParameterOwnership" association between function and Parameter
    public static final String PARAMETER_OWNERSHIP_PARAMETER_ASSOCIATION_END = "parameter"; //$NON-NLS-1$
    public static final String PARAMETER_OWNERSHIP_FUNCTION_ASSOCIATION_END = "function"; //$NON-NLS-1$

    /** Property names associated with SimpleDataTypes metamodel components */
    public static final String SDT_VARIETY_ATTRIBUTE = "variety"; //$NON-NLS-1$
    public static final String SDT_FINAL_ATTRIBUTE = "final"; //$NON-NLS-1$
    public static final String SDT_ORDERED_FACET_ATTRIBUTE = "ordered"; //$NON-NLS-1$
    public static final String SDT_BOUNDED_FACET_ATTRIBUTE = "bounded"; //$NON-NLS-1$
    public static final String SDT_CARDINALITY_FACET_ATTRIBUTE = "cardinality"; //$NON-NLS-1$
    public static final String SDT_NUMERIC_FACET_ATTRIBUTE = "numeric"; //$NON-NLS-1$
    public static final String SDT_PATTERN_FACET_ATTRIBUTE = "pattern"; //$NON-NLS-1$
    public static final String SDT_ENUMERATION_FACET_ATTRIBUTE = "enumeration"; //$NON-NLS-1$
    public static final String SDT_LENGTH_FACET_ATTRIBUTE = "length"; //$NON-NLS-1$
    public static final String SDT_MIN_LENGTH_FACET_ATTRIBUTE = "minLength"; //$NON-NLS-1$
    public static final String SDT_MAX_LENGTH_FACET_ATTRIBUTE = "maxLength"; //$NON-NLS-1$
    public static final String SDT_WHITE_SPACE_FACET_ATTRIBUTE = "whitespace"; //$NON-NLS-1$
    public static final String SDT_MIN_INCLUSIVE_FACET_ATTRIBUTE = "minInclusive"; //$NON-NLS-1$
    public static final String SDT_MIN_EXCLUSIVE_FACET_ATTRIBUTE = "minExclusive"; //$NON-NLS-1$
    public static final String SDT_MAX_INCLUSIVE_FACET_ATTRIBUTE = "maxInclusive"; //$NON-NLS-1$
    public static final String SDT_MAX_EXCLUSIVE_FACET_ATTRIBUTE = "maxExclusive"; //$NON-NLS-1$
    public static final String SDT_TOTAL_DIGITS_FACET_ATTRIBUTE = "totalDigits"; //$NON-NLS-1$
    public static final String SDT_FRACTION_DIGITS_FACET_ATTRIBUTE = "fractionDigits"; //$NON-NLS-1$
    public static final String SDT_PATTERN_FACET_VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$
    public static final String SDT_ENUMERATION_FACET_VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$
    public static final String SDT_CONSTRAINT_FACET_DESCRIPTION_SUFFIX = "Description"; //$NON-NLS-1$
    public static final String SDT_CONSTRAINT_FACET_FIXED_SUFFIX = "Fixed"; //$NON-NLS-1$

    /** Property names associated with XML Schema metamodel components */
    public static final String SCHEMA_FIXED_OR_DEFAULT_CONSTRAINT_ATTRIBUTE = "constraint"; //$NON-NLS-1$
    public static final String SCHEMA_FIXED_OR_DEFAULT_CONSTRAINT_VALUE_ATTRIBUTE = "constraintValue"; //$NON-NLS-1$
    public static final String SCHEMA_FORM_KIND_ATTRIBUTE = "form"; //$NON-NLS-1$
    public static final String SCHEMA_ELEMENT_FORM_DEFAULT_ATTRIBUTE = "elementFormDefault"; //$NON-NLS-1$
    public static final String SCHEMA_ATTRIBUTE_FORM_DEFAULT_ATTRIBUTE = "attributeFormDefault"; //$NON-NLS-1$
    public static final String SCHEMA_NILLABLE_ATTRIBUTE = "nillable"; //$NON-NLS-1$
    public static final String SCHEMA_ABSTRACT_ATTRIBUTE = "abstract"; //$NON-NLS-1$
    public static final String SCHEMA_ANONYMOUS_ATTRIBUTE = "anonymous"; //$NON-NLS-1$
    public static final String SCHEMA_MAX_OCCURS_ATTRIBUTE = "maxOccurs"; //$NON-NLS-1$
    public static final String SCHEMA_MIN_OCCURS_ATTRIBUTE = "minOccurs"; //$NON-NLS-1$
    public static final String SCHEMA_PROCESS_CONTENTS_ATTRIBUTE = "processContents"; //$NON-NLS-1$
    public static final String SCHEMA_NAMESPACE_ATTRIBUTE = "namespace"; //$NON-NLS-1$
    public static final String SCHEMA_SCHEMA_LOCATION_ATTRIBUTE = "schemaLocation"; //$NON-NLS-1$
    public static final String SCHEMA_SOURCE_ATTRIBUTE = "source"; //$NON-NLS-1$
    public static final String SCHEMA_CONTENT_ATTRIBUTE = "content"; //$NON-NLS-1$
    public static final String SCHEMA_INCL_OR_EXCL_CONSTRAINT_ATTRIBUTE = "constraint"; //$NON-NLS-1$
    public static final String SCHEMA_USE_KIND_ATTRIBUTE = "use"; //$NON-NLS-1$
    public static final String SCHEMA_MIXED_ATTRIBUTE = "mixed"; //$NON-NLS-1$
    public static final String SCHEMA_FINAL_ATTRIBUTE = "final"; //$NON-NLS-1$
    public static final String SCHEMA_DERIVATION_METHOD_ATTRIBUTE = "derivationMethod"; //$NON-NLS-1$
    // public static final String SCHEMA_ALLOWS_CHARACTER_DATA_ATTRIBUTE = "allowsCharacterData"; //$NON-NLS-1$
    // public static final String SCHEMA_ALLOWS_ELEMENTS_ATTRIBUTE = "allowsElements"; //$NON-NLS-1$
    // public static final String SCHEMA_ALLOWS_ATTRIBUTES_ATTRIBUTE = "allowsAttributes"; //$NON-NLS-1$
    public static final String SCHEMA_SUBSTITUTION_GROUP_ATTRIBUTE = "substitutionGroup"; //$NON-NLS-1$
    public static final String SCHEMA_PUBLIC_ATTRIBUTE = "public"; //$NON-NLS-1$
    public static final String SCHEMA_SYSTEM_ATTRIBUTE = "system"; //$NON-NLS-1$
    public static final String SCHEMA_BLOCK_ATTRIBUTE = "block"; //$NON-NLS-1$

    /** Property names associated with XML Document metamodel components */
    public static final String XML_SCHEMA_REF_ATTRIBUTE = "schemaReference"; //$NON-NLS-1$
    public static final String XML_SCHEMA_OBJ_REF_ATTRIBUTE = "schemaObjectReference"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_ENCODING_ATTRIBUTE = "charEncoding"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_REFERENCE_ATTRIBUTE = "documentReference"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_MAPPING_ATTRIBUTE = "mapping"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_CHOICE_SQL_CRITERIA_ATTRIBUTE = "criteria"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_COMMENT_ATTRIBUTE = "comment"; //$NON-NLS-1$
    public static final String MAPPING_DEFINITION_ATTRIBUTE = "mappingDefinition"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_FORMATTING_ATTRIBUTE = "formatted"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_FIXED_OR_DEFAULT_CONSTRAINT_ATTRIBUTE = SCHEMA_FIXED_OR_DEFAULT_CONSTRAINT_ATTRIBUTE;
    public static final String XML_DOCUMENT_FIXED_OR_DEFAULT_CONSTRAINT_VALUE_ATTRIBUTE = SCHEMA_FIXED_OR_DEFAULT_CONSTRAINT_VALUE_ATTRIBUTE;
    public static final String XML_DOCUMENT_NAMESPACE_URI_ATTRIBUTE = "namespaceURI"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_NAMESPACE_PREFIX_ATTRIBUTE = "namespacePrefix"; //$NON-NLS-1$

    public static final String XML_DOCUMENT_CHOICE_DEFAULT_OBJ_ATTRIBUTE = "choiceDefaultObject"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_CRITERIA_DEFAULT_ERROR_ATTRIBUTE = "criteriaDefaultError"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_CRITERIA_ATTRIBUTE = "criteria"; //$NON-NLS-1$
    public static final String XML_DOCUMENT_EXCLUDE_OBJ_ATTRIBUTE = "excludeFromDocument"; //$NON-NLS-1$

    private static final Map METAMODEL_NAMES = new HashMap();
    /** Map of metamodel name keyed on metamodel package name */
    public static final Map METAMODEL_NAME_MAP = Collections.unmodifiableMap(METAMODEL_NAMES);

    // Build a map between the metamodel package name and the metamodel name. The full name
    // of an metamodel entity is of the form packageName.className and the packageName is not
    // always the same as the metamodel name.
    static {
        METAMODEL_NAMES.put(MetaModelConstants.DataTypes.PACKAGE_NAME, MetaModelConstants.DataTypes.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.Foundation.PACKAGE_NAME, MetaModelConstants.Foundation.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.Relational.PACKAGE_NAME, MetaModelConstants.Relational.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.DataAccess.PACKAGE_NAME, MetaModelConstants.DataAccess.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.Diagram.PACKAGE_NAME, MetaModelConstants.Diagram.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.Virtual.PACKAGE_NAME, MetaModelConstants.Virtual.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.Function.PACKAGE_NAME, MetaModelConstants.Function.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.SimpleDataTypes.PACKAGE_NAME, MetaModelConstants.SimpleDataTypes.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.XMLSchema.PACKAGE_NAME, MetaModelConstants.XMLSchema.NAME);
        METAMODEL_NAMES.put(MetaModelConstants.XMLDocument.PACKAGE_NAME, MetaModelConstants.XMLDocument.NAME);
    }

    public static class DataTypes {
        public static final String NAME = "MMDataTypes"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "MMDataTypes"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "DataTypes"; //$NON-NLS-1$

        public static interface Class {
        }
    }

    public static class Foundation {
        public static final String NAME = "Foundation"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "Foundation"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "Foundation"; //$NON-NLS-1$

        public static interface Class {
            public static final String ELEMENT = "Element"; //$NON-NLS-1$
            public static final String MODEL_ELEMENT = "ModelElement"; //$NON-NLS-1$
            public static final String NAMESPACE = "Namespace"; //$NON-NLS-1$
            public static final String PACKAGE = "Package"; //$NON-NLS-1$
            public static final String MODEL = "Model"; //$NON-NLS-1$
            public static final String ACCESS_MODEL = "AccessModel"; //$NON-NLS-1$
            public static final String CLASSIFIER = "Classifier"; //$NON-NLS-1$
            public static final String DATA_TYPE = "DataType"; //$NON-NLS-1$
            public static final String CLASS = "Class"; //$NON-NLS-1$
            public static final String FEATURE = "Feature"; //$NON-NLS-1$
            public static final String STRUCTURAL_FEATURE = "StructuralFeature"; //$NON-NLS-1$
            public static final String ATTRIBUTE = "Attribute"; //$NON-NLS-1$
            public static final String RELATIONSHIP = "Relationship"; //$NON-NLS-1$
            public static final String ASSOCIATION = "Association"; //$NON-NLS-1$
            public static final String ASSOCIATION_END = "AssociationEnd"; //$NON-NLS-1$
            public static final String UNIQUE_KEY = "UniqueKey"; //$NON-NLS-1$
            public static final String PRIMARY_KEY = "PrimaryKey"; //$NON-NLS-1$
            public static final String KEY_RELATIONSHIP = "KeyRelationship"; //$NON-NLS-1$
            public static final String INDEX = "Index"; //$NON-NLS-1$
            public static final String INDEXED_FEATURE = "IndexedFeature"; //$NON-NLS-1$
            public static final String BEHAVIORIAL_FEATURE = "BehavioralFeature"; //$NON-NLS-1$
            public static final String METHOD = "Method"; //$NON-NLS-1$
            public static final String PROCEDURE = "Procedure"; //$NON-NLS-1$
            public static final String STORED_QUERY = "StoredQuery"; //$NON-NLS-1$
            public static final String PARAMETER = "Parameter"; //$NON-NLS-1$
            public static final String STORED_QUERY_PARAMETER = "StoredQueryParameter"; //$NON-NLS-1$
            public static final String OPERATION = "Operation"; //$NON-NLS-1$
        }
    }

    public static class Relational {
        public static final String NAME = "Relational"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "Relational"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "Relational"; //$NON-NLS-1$

        public static interface Class {
            public static final String MODEL = "Model"; //$NON-NLS-1$
            public static final String CATALOG = "Catalog"; //$NON-NLS-1$
            public static final String SCHEMA = "Schema"; //$NON-NLS-1$
            public static final String TABLE = "Table"; //$NON-NLS-1$
            public static final String COLUMN = "Column"; //$NON-NLS-1$
            public static final String AP_COLUMNS = "columns"; //$NON-NLS-1$
            public static final String PROCEDURE = "StoredProcedure"; //$NON-NLS-1$
            public static final String PARAMETER = "Parameter"; //$NON-NLS-1$
            public static final String STORED_QUERY_PARAMETER = "StoredQueryParameter"; //$NON-NLS-1$
            public static final String STORED_QUERY = "StoredQuery"; //$NON-NLS-1$
            public static final String BASE_TABLE = "BaseTable"; //$NON-NLS-1$
            public static final String VIEW = "View"; //$NON-NLS-1$
            public static final String RESULT_SET = "ResultSet"; //$NON-NLS-1$
            public static final String SQL_INDEX = "SQLIndex"; //$NON-NLS-1$
            public static final String SQL_INDEX_COLUMN = "SQLIndexColumn"; //$NON-NLS-1$
            public static final String UNIQUE_CONSTRAINT = "UniqueConstraint"; //$NON-NLS-1$
            public static final String PRIMARY_KEY = "PrimaryKey"; //$NON-NLS-1$
            public static final String FOREIGN_KEY = "ForeignKey"; //$NON-NLS-1$
            public static final String ACCESS_PATTERN = "AccessPattern"; //$NON-NLS-1$
        }
    }

    public static class DataAccess {
        public static final String NAME = "DataAccess"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "DataAccess"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "DataAccess"; //$NON-NLS-1$

        public static interface Class {
            public static final String MODEL = "Model"; //$NON-NLS-1$
            public static final String CATEGORY = "Category"; //$NON-NLS-1$
            public static final String GROUP = "Group"; //$NON-NLS-1$
            public static final String ELEMENT = "Element"; //$NON-NLS-1$
            public static final String AP_ELEMENTS = "elements"; //$NON-NLS-1$
            // public static final String PROCEDURE = "Procedure"; //$NON-NLS-1$
            public static final String STORED_QUERY = "StoredQuery"; //$NON-NLS-1$
            public static final String PARAMETER = "Parameter"; //$NON-NLS-1$
            public static final String STORED_QUERY_PARAMETER = "StoredQueryParameter"; //$NON-NLS-1$
            public static final String INDEX = "Index"; //$NON-NLS-1$
            public static final String INDEX_COLUMN = "IndexColumn"; //$NON-NLS-1$
            public static final String UNIQUE_CONSTRAINT = "UniqueConstraint"; //$NON-NLS-1$
            public static final String PRIMARY_KEY = "PrimaryKey"; //$NON-NLS-1$
            public static final String FOREIGN_KEY = "ForeignKey"; //$NON-NLS-1$
            public static final String ACCESS_PATTERN = "AccessPattern"; //$NON-NLS-1$
            public static final String RESULT_SET = "ResultSet"; //$NON-NLS-1$
        }
    }

    public static class Diagram {
        public static final String NAME = "Diagram"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "Diagram"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "Diagram"; //$NON-NLS-1$

        public static interface Class {
            public static final String PRESENTATION_ELEMENT = "PresentationElement"; //$NON-NLS-1$
            public static final String DIAGRAM = "Diagram"; //$NON-NLS-1$
            public static final String DIAGRAM_COMPONENT = "DiagramComponent"; //$NON-NLS-1$
            public static final String CLASS_DIAGRAM = "ClassDiagram"; //$NON-NLS-1$
            public static final String PACKAGE_DIAGRAM = "PackageDiagram"; //$NON-NLS-1$
            public static final String TRANSFORMATION_DIAGRAM = "TransformationDiagram"; //$NON-NLS-1$
            public static final String MAPPING_DIAGRAM = "MappingDiagram"; //$NON-NLS-1$
        }
    }

    public static class Virtual {
        public static final String NAME = "Virtual"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "Virtual"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "Virtual"; //$NON-NLS-1$

        public static interface Class {
            public static final String TRANSFORMATION_ELEMENT = "TransformationElement"; //$NON-NLS-1$
            public static final String TRANSFORMATION = "Transformation"; //$NON-NLS-1$
            public static final String TRANSFORMATION_OPERATION = "TransformationOperation"; //$NON-NLS-1$
            public static final String TEMPORARY_GROUP = "TemporaryGroup"; //$NON-NLS-1$
            public static final String QUERY = "Query"; //$NON-NLS-1$
            public static final String LINK = "Link"; //$NON-NLS-1$
            public static final String TRANSFORMATION_LINK = "TransformationLink"; //$NON-NLS-1$
            public static final String TRANSFORMATION_CONTENT = "TranformationContent"; //$NON-NLS-1$
        }
    }

    public static class Function {
        public static final String NAME = "MetaMatrixFunction"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "MetaMatrixFunction"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "MetaMatrixFunction"; //$NON-NLS-1$

        public static interface Class {
            public static final String MODEL = "Model"; //$NON-NLS-1$
            public static final String FUNCTION = "Function"; //$NON-NLS-1$
            public static final String PARAMETER = "Parameter"; //$NON-NLS-1$
        }
    }

    public static class SimpleDataTypes {
        public static final String NAME = "SimpleDatatypes"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "SimpleDatatypes"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "SimpleDatatypes"; //$NON-NLS-1$

        public static interface Class {
            public static final String MODEL = "SimpleDatatypeModel"; //$NON-NLS-1$
            public static final String NAMESPACE = "Domain"; //$NON-NLS-1$
        }
    }

    public static class XMLSchema {
        public static final String NAME = "XMLSchema"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "MMXMLSchema"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "XMLSchema"; //$NON-NLS-1$

        public static interface Class {
            public static final String MODEL = "Model"; //$NON-NLS-1$
            public static final String SCHEMA_DOCUMENT = "SchemaDocument"; //$NON-NLS-1$
            public static final String SIMPLE_DATA_TYPE = "SimpleType"; //$NON-NLS-1$
            public static final String ATOMIC_DATA_TYPE = "AtomicType"; //$NON-NLS-1$
            public static final String UNION_DATA_TYPE = "UnionType"; //$NON-NLS-1$
            public static final String LIST_DATA_TYPE = "ListType"; //$NON-NLS-1$
            public static final String PATTERN = "Pattern"; //$NON-NLS-1$
            public static final String ENUMERATION = "Enumeration"; //$NON-NLS-1$
            public static final String ATTRIBUTE = "Attribute"; //$NON-NLS-1$
            public static final String ELEMENT = "Element"; //$NON-NLS-1$
            public static final String COMPLEX_TYPE = "ComplexType"; //$NON-NLS-1$
            public static final String ATTRIBUTE_GROUP = "AttributeGroup"; //$NON-NLS-1$
            public static final String GROUP = "Group"; //$NON-NLS-1$
            public static final String ANY = "Any"; //$NON-NLS-1$
            public static final String ANY_TYPE = "AnyType"; //$NON-NLS-1$
            public static final String ANY_ATTRIBUTE = "AnyAttribute"; //$NON-NLS-1$
            public static final String NS_CONSTRAINT = "NamespaceConstraint"; //$NON-NLS-1$
            public static final String INCLUDE = "Include"; //$NON-NLS-1$
            public static final String IMPORT = "Import"; //$NON-NLS-1$
            public static final String REDEFINE = "Redefine"; //$NON-NLS-1$
            public static final String ANNOTATION = "Annotation"; //$NON-NLS-1$
            public static final String APP_INFO = "ApplicationInfo"; //$NON-NLS-1$
            public static final String DOCUMENTATION = "Documentation"; //$NON-NLS-1$
            public static final String CONTENT_MODEL = "AbstractCompositor"; //$NON-NLS-1$
            public static final String SEQUENCE = "Sequence"; //$NON-NLS-1$
            public static final String CHOICE = "Choice"; //$NON-NLS-1$
            public static final String ALL = "All"; //$NON-NLS-1$
            public static final String NOTATION = "Notation"; //$NON-NLS-1$
        }
    }

    public static class XMLDocument {
        public static final String NAME = "XMLDocument"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = "MMXMLDocument"; //$NON-NLS-1$
        public static final String PACKAGE_NAME = "XMLDocument"; //$NON-NLS-1$

        public static interface Class {
            public static final String MODEL = "Model"; //$NON-NLS-1$
            public static final String XML_SCHEMA_REF = "XmlSchema"; //$NON-NLS-1$
            public static final String XML_DOCUMENT = "Document"; //$NON-NLS-1$
            public static final String ELEMENT = "Element"; //$NON-NLS-1$
            public static final String ATTRIBUTE = "Attribute"; //$NON-NLS-1$
            public static final String COMMENT = "Comment"; //$NON-NLS-1$
            public static final String NAMESPACE = "Namespace"; //$NON-NLS-1$
            public static final String CONTENT_MODEL = "AbstractCompositor"; //$NON-NLS-1$
            public static final String SEQUENCE = "Sequence"; //$NON-NLS-1$
            public static final String CHOICE = "Choice"; //$NON-NLS-1$
            public static final String ALL = "All"; //$NON-NLS-1$
            public static final String MAPPING_CATEGORY = "MappingClasses"; //$NON-NLS-1$
            public static final String MAPPING_GROUP = "MappingClass"; //$NON-NLS-1$
            public static final String MAPPING_ELEMENT = "MappingAttribute"; //$NON-NLS-1$
            public static final String TEMPORARY_TABLE = "TemporaryTable"; //$NON-NLS-1$
            public static final String TEMPORARY_COLUMN = "TemporaryColumn"; //$NON-NLS-1$
        }
    }

    public static class Connections {
        public static final String NAME = "Connections"; //$NON-NLS-1$
        public static final String FILENAME = NAME + METAMODEL_FILE_EXTENSION;
        public static final String NAMESPACE_PREFIX = NAME;
        public static final String PACKAGE_NAME = NAME;

        public static interface Class {
            public static final String CONFIGURATION = "Configuration"; //$NON-NLS-1$
            public static final String CONNECTION = "Connection"; //$NON-NLS-1$
            public static final String CONNECTIONS = "Connections"; //$NON-NLS-1$
            public static final String IMPORT_CONNECTION = "ImportConnection"; //$NON-NLS-1$
            public static final String IMPORTER_EXTENSION_NAME = "ImporterExtensionName"; //$NON-NLS-1$
            public static final String OPTION = "Option"; //$NON-NLS-1$
            public static final String OPTION_VALUE = "Value"; //$NON-NLS-1$
            public static final String URL = "URL"; //$NON-NLS-1$
            public static final String USER_ID = "UserID"; //$NON-NLS-1$
        }
    }
}
