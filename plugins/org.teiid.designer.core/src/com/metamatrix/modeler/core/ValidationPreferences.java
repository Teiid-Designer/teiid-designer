/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.util.List;
import java.util.Map;

/**
 * ValidationPreferences
 */
public interface ValidationPreferences {

    /**
     * Preference name for eObjects with same uuids in a container
     */
    String EOBJECT_UUID_UNIQUENESS = "corePreferences.eObjectUuidUniquess"; //$NON-NLS-1$    

    /**
     * Preference name for indexes with columns from more than one table.
     */
    String CORE_STRING_FUNCTIONS_ONE_BASED = "corePreferences.stringFunctionsAreOneBased"; //$NON-NLS-1$
    
    /**
     * Preference name for Name character restriction (RelationalStringNameRule) rule.
     */
    String RELATIONAL_NAME_CHARACTER_RESTRICTION = "relationalPreferences.nameCharacterRestriction"; //$NON-NLS-1$
    
    /**
     * Preference name for table missing nameInSource rule.
     */
    String RELATIONAL_TABLE_MISSING_NAME_IN_SOURCE = "relationalPreferences.missingNameInSource"; //$NON-NLS-1$

    /**
     * Preference name for siblings having same nameInSource rule.
     */
    String RELATIONAL_SIBLING_NAME_IN_SOURCE = "relationalPreferences.siblingNameInSource"; //$NON-NLS-1$

    /**
     * Preference name for string/char datatype columns whose length is undefined.
     */
    String RELATIONAL_MISSING_COLUMN_LENGTH = "relationalPreferences.missingColumnLength"; //$NON-NLS-1$
    
    /**
     * Preference name for string/char datatype columns whose length is undefined.
     */
    String RELATIONAL_EMPTY_TRANSFORMATIONS = "relationalPreferences.emptyTransformations"; //$NON-NLS-1$
    
    /**
     * Preference name for numeric/timestamp datatype columns whose precision is undefined.
     */
    String RELATIONAL_MISSING_COLUMN_PRECISION = "relationalPreferences.missingColumnPrecision"; //$NON-NLS-1$    

    /**
     * Preference name for integer datatype elements.
     */
    String RELATIONAL_COLUMN_INTEGER_TYPE = "relationalPreferences.integerDatatype"; //$NON-NLS-1$

    /**
     * Preference name for indexes with columns from more than one table.
     */
    String RELATIONAL_INDEXES_WITH_COLUMNS_FROM_MULTIPLE_TABLES = "relationalPreferences.crossTableIndexes"; //$NON-NLS-1$
    
    /**
     * Preference name for siblings having same nameInSource rule.
     */
    String DATAACCESS_SIBLING_NAME_IN_SOURCE = "dataaccessPreferences.siblingNameInSource"; //$NON-NLS-1$

    /**
     * Preference name for string/char datatype columns whose length is undefined.
     */
    String DATAACCESS_MISSING_ELEMENT_LENGTH = "dataaccessPreferences.missingElementLength"; //$NON-NLS-1$
    
    /**
     * Preference name for string/char datatype columns whose precision is undefined.
     */
    String DATAACCESS_MISSING_ELEMENT_PRECISION = "dataaccessPreferences.missingElementPrecision"; //$NON-NLS-1$    

    /**
     * Preference name for integer datatype elements.
     */
    String DATAACCESS_ELEMENT_INTEGER_TYPE = "dataaccessPreferences.integerDatatype"; //$NON-NLS-1$

    /**
     * Preference name for indexes with columns from more than one table.
     */
    String DATAACCESS_INDEXES_WITH_COLUMNS_FROM_MULTIPLE_TABLES = "dataaccessPreferences.crossTableIndexes"; //$NON-NLS-1$

    /**
     * Preference name for elements/attributes in a document having no schema reference.
     */
    String XML_ELEMENT_SCHEMA_REFERENCE = "xmlPreferences.elementSchemaRef"; //$NON-NLS-1$

    /**
     * Preference name for entity that violates shemas max occurs specification.
     */
    String XML_ENTITY_MAXOCCURS_VIOLATION = "xmlPreferences.maxOccursViolation"; //$NON-NLS-1$

    // The XML_REQUIRED_ELEMENT_MAPPING preference is not referenced in any validation rule
    /**
     * Preference name for elements/attributes in a document not mapped to a mapping class column attribute.
     */
    String XML_REQUIRED_ELEMENT_MAPPING = "xmlPreferences.requiredElementMapping"; //$NON-NLS-1$

    /**
     * Preference name for elements/attributes required by the schema excluded in the document.
     */
    String XML_REQUIRED_ELEMENT_EXCLUDE = "xmlPreferences.requiredElementExclude"; //$NON-NLS-1$

    /**
     * Preference name for fixed/default value elements/attributes mapped to a mapping class column attribute.
     */
    String XML_FIXED_DEFAULT_ELEMENT_MAPPED = "xmlPreferences.fixedDefaultElementMapped"; //$NON-NLS-1$

    /**
     * Preference name for excluded elements/attributes mapped to a mapping class column attribute.
     */
    String XML_EXCLUDED_ELEMENT_MAPPED = "xmlPreferences.excludeElementMapped"; //$NON-NLS-1$

    /**
     * Preference name for zero min occurs elements/attributes mapped to a mapping class column attribute.
     */
    String XML_ELEMENT_ZERO_MIN_MAPPED = "xmlPreferences.mappedElementZeroMinOccurs"; //$NON-NLS-1$

    /**
     * Preference name for one max occurs elements/attributes mapped to a mapping class.
     */
    String XML_ELEMENT_ONE_MAX_MAPPED = "xmlPreferences.mappedElementOneMaxOccurs"; //$NON-NLS-1$

    /**
     * Preference name for root element mapped to a mapping class.
     */
    String XML_ROOT_ELEMENT_MAPPING_CLASS = "xmlPreferences.rootElementMappingClass"; //$NON-NLS-1$

    /**
     * Preference name for nillable elements/attributes mapped to a mapping class column attribute.
     */
    String XML_ELEMENT_NILLABLE_MAPPED = "xmlPreferences.mappedElementNillable"; //$NON-NLS-1$

//  Defect 18718 - Cannot find any reason why we check for this condition
//    /**
//     * Preference name for elements/attributes mapped to a mapping class column attribute have element/attribute children.
//     */
//    String XML_ELEMENT_CHILDREN_MAPPED = "xmlPreferences.mappedElementChildren"; //$NON-NLS-1$

    /**
     * Preference name for elements/attributes excluded from the document needing validation.
     */
    String XML_ELEMENT_VALIDATE_EXCLUDED = "xmlPreferences.validateExcludedElements"; //$NON-NLS-1$

    /**
     * Preference name for how to handle XSD validation errors
     */
    String XSD_MODEL_VALIDATION = "xsdPreferences.performValidation"; //$NON-NLS-1$

    /**
     * Preference name for incompatible element/mapping class column datatypes.
     */
    String XML_INCOMPATIBLE_ELEMENT_COLUMN_DATATYPE = "xmlPreferences.incompatibleElementColumnDatatypes"; //$NON-NLS-1$
    /**
     * Get all the validation descriptors defines in the plugin.
     * @return list of validation descriptors.
     */
    List getValidationDescriptors();

    /**
     * Sets the map of options. All and only the options explicitly included in the given map
     * are remembered; all previous option settings are forgotten, including ones not explicitly
     * mentioned.
     * 
     * @param newOptions the new options (key type: <code>ValidationDescriptor</code>; value type: <code>String</code>)
     */
    void setOptions(Map newOptions);

    /**
     * Returns the map of the current options. Initially, all options have their default values,
     * and this method returns a map that includes all known options.
     * 
     * @return table of current settings of all options 
     *   (key type: <code>String</code>; value type: <code>String</code>)
     */
    Map getOptions();
}
