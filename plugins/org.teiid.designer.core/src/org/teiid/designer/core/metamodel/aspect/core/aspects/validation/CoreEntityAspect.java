/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.AmbiguousModelImportsRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.AnnotationExtensionAttributeDefaultValueRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.DeprecatedMetamodelUriRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.InvalidModelImportRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.InvalidNamespaceUriRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.MissingModelImportRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.ModelAnnotationUuidRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.NullModelTypeRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.NullPrimaryMetamodelUriRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.RestPropertiesRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.UnresolvedModelImportRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XAttributeDefaultValueDatatypeRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XAttributeFeatureRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XAttributeMaxOccursRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XAttributeNameRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XAttributeUniqueNameInXClassRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XClassExtendedClassRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XClassNameRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XClassUniqueExtendedClassInXPackageRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XClassUniqueNameInXPackageRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XEnumLiteralNameRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XEnumLiteralValueRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XEnumNameRule;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules.XPackageNameRule;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.metamodels.core.CorePackage;


/**
 * CoreEntityAspect
 *
 * @since 8.0
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
