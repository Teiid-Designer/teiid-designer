/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.AmbiguousModelImportsRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.AnnotationExtensionAttributeDefaultValueRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.DeprecatedMetamodelUriRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.InvalidModelImportRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.InvalidNamespaceUriRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.MissingModelImportRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.ModelAnnotationUuidRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.NullModelTypeRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.NullPrimaryMetamodelUriRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.RestPropertiesRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.UnresolvedModelImportRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XAttributeDefaultValueDatatypeRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XAttributeFeatureRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XAttributeMaxOccursRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XAttributeNameRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XAttributeUniqueNameInXClassRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XClassExtendedClassRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XClassNameRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XClassUniqueExtendedClassInXPackageRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XClassUniqueNameInXPackageRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XEnumLiteralNameRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XEnumLiteralValueRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XEnumNameRule;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules.XPackageNameRule;
import com.metamatrix.modeler.core.validation.ValidationRule;

/**
 * CoreEntityAspect
 */
public abstract class CoreEntityAspect extends AbstractValidationAspect {

    public static final ValidationRule UNRESOLVED_MODEL_IMPORT_RULE = new UnresolvedModelImportRule();
    public static final ValidationRule NULL_PRIMARY_METAMODEL_RULE = new NullPrimaryMetamodelUriRule(
                                                                                                     CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI);
    public static final ValidationRule NULL_MODEL_TYPE_RULE = new NullModelTypeRule(CorePackage.MODEL_ANNOTATION__MODEL_TYPE);
    public static final ValidationRule MISSING_MODEL_IMPORT_RULE = new MissingModelImportRule();
    public static final ValidationRule AMBIGUOUS_MODEL_IMPORTS_RULE = new AmbiguousModelImportsRule();
    public static final ValidationRule MODEL_ANNOTATION_UUID_RULE = new ModelAnnotationUuidRule();
    public static final ValidationRule DEPRECATED_METAMODEL_URI_RULE = new DeprecatedMetamodelUriRule();
    public static final ValidationRule XCLASS_UNIQUE_EXTENDED_CLASS_IN_XPACKAGE_RULE = new XClassUniqueExtendedClassInXPackageRule();
    public static final ValidationRule XCLASS_UNIQUE_NAME_IN_XPACKAGE_RULE = new XClassUniqueNameInXPackageRule();
    public static final ValidationRule XCLASS_EXTENDED_CLASS_RULE = new XClassExtendedClassRule();
    public static final ValidationRule XPACKAGE_NAME_RULE = new XPackageNameRule();
    public static final ValidationRule XCLASS_NAME_RULE = new XClassNameRule();
    public static final ValidationRule XATTRIBUTE_NAME_RULE = new XAttributeNameRule();
    public static final ValidationRule XATTRIBUTE_FEATURE_RULE = new XAttributeFeatureRule();
    public static final ValidationRule XATTRIBUTE_MAX_OCCURS_RULE = new XAttributeMaxOccursRule();
    public static final ValidationRule XATTRIBUTE_UNIQUE_NAME_IN_XCLASS_RULE = new XAttributeUniqueNameInXClassRule();
    public static final ValidationRule XATTRIBUTE_DEFAULT_VALUE_DATATYPE_RULE = new XAttributeDefaultValueDatatypeRule();
    public static final ValidationRule XENUM_NAME_RULE = new XEnumNameRule();
    public static final ValidationRule XENUM_LITERAL_NAME_RULE = new XEnumLiteralNameRule();// MyDefect : Added for 17364
    public static final ValidationRule XENUM_LITERAL_VALUE_RULE = new XEnumLiteralValueRule();// MyDefect : Added for 17364
    public static final ValidationRule ANNOTATION_EXTENSION_ATTRIBUTE_DEFAULT_VALUE_RULE = new AnnotationExtensionAttributeDefaultValueRule();
    public static final ValidationRule INVALID_NAMESPACE_URI_RULE = new InvalidNamespaceUriRule();
    public static final ValidationRule REST_PROPERTIES_RULE = new RestPropertiesRule();
    public static final ValidationRule INVALID_MODEL_IMPORT_RULE = new InvalidModelImportRule();// MyDefect : Added for 17511

    protected CoreEntityAspect( MetamodelEntity entity ) {
        super(entity);
    }
}
