/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.transformation.impl.MappingClassImpl;
import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlValueHolder;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.index.TargetLocationIndexSelector;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.mapping.factory.DefaultMappableTree;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.xml.aspects.sql.XmlElementSqlAspect;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.metadata.QueryMetadataContext;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.modeler.xml.PluginConstants;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.report.ReportItem;
import com.metamatrix.query.resolver.util.ResolverVisitor;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.GroupSymbol;
import com.metamatrix.query.sql.visitor.ElementCollectorVisitor;
import com.metamatrix.query.validator.Validator;
import com.metamatrix.query.validator.ValidatorReport;

/**
 * XmlDocumentValidationRule
 */
public class XmlDocumentValidationRule implements ObjectValidationRule {

    private static String RULE_NAME = XmlDocumentValidationRule.class.getName();

    // map between XmlElement and MappingClassColumn
    private Map elementColumnMap = null;
    // map between XmlElement and MappingClass
    private Map elementMappingClassMap = null;
    // preferences to be used for validation
    private Preferences preferences;

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public synchronized void validate( final EObject eObject,
                                       final ValidationContext context ) {
        ArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);

        // Defect 23839 - improved XML Document model validation by moving some of this methods code further down in the method.
        // Basically, each tree root is validated, but we were returning if the Document for the tree root was already validated.
        // For large XML Documents, there are many tree roots and this method was creating a TreeMappingAdapter BEFORE it did the
        // hasRunRule() check. By delaying the creation of the TreeMappingAdapter, we DRASTICALLY improved validation.
        // A large XAL.xsd document mode (~10 MB) validates in < 1 minute, versus a few hours.

        TreeMappingRoot transRoot = (TreeMappingRoot)eObject;

        EObject target = transRoot.getTarget();
        // this rule does not apply if the target is not a XMLDocument
        if (target == null || !(target instanceof XmlDocument)) {
            return;
        }

        XmlDocument document = (XmlDocument)target;

        // document is found once per TreeMappingRoot ...we need to validate a document just once
        // so tract its validation
        // we are overloading the use of hasRunRule(typically tracks containers of EObjects) on the validation context
        // to tract if a particular rule has been run on a given document
        String uuid = ModelerCore.getObjectIdString(document);
        if (context.hasRunRule(uuid, RULE_NAME)) {
            return;
        }
        context.recordRuleRun(uuid, RULE_NAME);

        Resource documentResource = document.eResource();
        // get the preferences from the context
        this.preferences = context.getPreferences();

        // model contents for this resource
        ModelContents mdlContents = new ModelContents(documentResource);
        Iterator contentIter = mdlContents.getTransformations(document).iterator();
        if (!contentIter.hasNext()) {
            return;
        }

        try {
            // Create the initial map of outputs(XML Elements) to inputs (mapping class columns)
            elementColumnMap = new HashMap();
            elementMappingClassMap = new HashMap();
            // get the mapping root associated with the transformation
            while (contentIter.hasNext()) {
                MappingRoot mappingRoot = (MappingRoot)contentIter.next();
                // if there is a mapping root
                if (mappingRoot != null && mappingRoot instanceof TreeMappingRoot) {
                    List inputClasses = mappingRoot.getInputs();
                    List outputRootElements = mappingRoot.getOutputs();
                    if (!outputRootElements.isEmpty() && !inputClasses.isEmpty()) {
                        // every mapping class could be mapped to one or more
                        // xml elements
                        Object input = inputClasses.iterator().next();
                        for (final Iterator outputIter = outputRootElements.iterator(); outputIter.hasNext();) {
                            // fill the map with element to its mappingClass
                            elementMappingClassMap.put(outputIter.next(), input);
                        }
                    }
                    for (Iterator mappingIter = mappingRoot.getNested().iterator(); mappingIter.hasNext();) {
                        Mapping nestedMapping = (Mapping)mappingIter.next();
                        // mapping Class columns
                        List inputColumns = nestedMapping.getInputs();
                        // xml elements
                        List outputElements = nestedMapping.getOutputs();
                        if (!outputElements.isEmpty() && !inputColumns.isEmpty()) {
                            // every mapping class column could be mapped to one or more
                            // xml elements/attribues
                            Object input = inputColumns.iterator().next();
                            for (final Iterator outputIter = outputElements.iterator(); outputIter.hasNext();) {
                                // fill the map with element to its mappingClass column value
                                elementColumnMap.put(outputIter.next(), input);
                            }
                        }
                    }
                }
            }
            // create a result for the XmlDocument
            ValidationResult validationResult = new ValidationResultImpl(transRoot, target);

            // collect all the xml Element in the document
            DocumentVisitor visitor = new DocumentVisitor();
            ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            try {
                processor.walk(document, ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException e) {
                ValidationProblem problem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.ERROR,
                                                                      TransformationPlugin.Util.getString("XmlDocumentValidationRule.Error_trying_to_collect_XmlElements_in_an_XmlDocument__1") + e.getMessage()); //$NON-NLS-1$
                validationResult.addProblem(problem);
                return;
            }

            // validate the elements and attributes in the document
            Collection entities = new ArrayList();
            entities.addAll(visitor.getElements());
            entities.addAll(visitor.getAttributes());

            // validate attributes and elements in the document
            validateEntities(entities, validationResult, context);
            // validate mapping classes marked for recursion
            validateMappingClassRecursionAllowed(validationResult, context);

            // validate entities directly under a choice owner
            // This may take a long time, so we don't want to create a mapping adapter or a mappable tree unless we get this far.
            if (!visitor.getChoices().isEmpty()) {
                TreeMappingAdapter mappingAdapter = new TreeMappingAdapter(document);
                IMappableTree mappableTree = new DefaultMappableTree(document);

                validateChoiceEntities(visitor.getChoices(), mappingAdapter, mappableTree, validationResult, context);
            }

            // Only validate mapping column to XSD component types for virtual
            // XML document models used by the server
            if (!isLogicalModel(eObject)) {
                validateColumnToElementTypes(validationResult, context);
            }

            // add the result to the context
            context.addResult(validationResult);
        } finally {
            // clear any state on this rule, since this object may be reused
            elementColumnMap = null;
            preferences = null;
            elementMappingClassMap = null;
        }
    }

    private boolean isLogicalModel( final EObject eObject ) {
        ArgCheck.isNotNull(eObject);
        Resource r = eObject.eResource();
        if (r instanceof EmfResource) {
            EmfResource eResource = (EmfResource)r;
            if (eResource.getModelType() == ModelType.LOGICAL_LITERAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks to ensure that the column type is compatible with the element type
     */
    private void validateColumnToElementTypes( ValidationResult validationResult,
                                               ValidationContext context ) {
        for (Iterator mappingIter = elementColumnMap.entrySet().iterator(); mappingIter.hasNext();) {
            Map.Entry entry = (Map.Entry)mappingIter.next();
            if (!(entry.getKey() instanceof XmlDocumentNode)) {
                continue;
            }
            XmlDocumentNode element = (XmlDocumentNode)entry.getKey();

            XSDTypeDefinition xsdType = XmlDocumentUtil.findXSDType(element);

            DatatypeManager dtm = ModelerCore.getDatatypeManager(xsdType, true);

            if (xsdType == null || !(xsdType instanceof XSDSimpleTypeDefinition) || isStringType(xsdType, dtm)) {
                continue;
            }

            MappingClassColumn column = (MappingClassColumn)entry.getValue();

            EObject columnType = column.getType();

            if (columnType == null || !(columnType instanceof XSDSimpleTypeDefinition) || isStringType(columnType, dtm)) {
                continue;
            }

            // check for exact match
            if (columnType == xsdType) {
                continue;
            }

            // check the hierarchies. this is only possible if the types are Atomic
            if (!isAtomicLiteral((XSDSimpleTypeDefinition)xsdType) || !isAtomicLiteral((XSDSimpleTypeDefinition)columnType)) {
                continue;
            }

            List xsdTypeList = createHierarcyList(xsdType, dtm);

            List columnTypeList = createHierarcyList(columnType, dtm);

            if (columnTypeList.size() > 0 && xsdTypeList.size() > 0) {
                boolean compatible = false;
                boolean nonAtomicAncestor = false;
                // start at the 1 index to skip the top level element type
                for (int i = 1; i < xsdTypeList.size(); i++) {
                    XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition)xsdTypeList.get(i);
                    if (!isAtomicLiteral(std)) {
                        nonAtomicAncestor = true;
                        break;
                    }
                    int index = columnTypeList.indexOf(std);
                    if (index != -1) {
                        // if not the first element then a common ancestor is shared
                        if (index != 0) {
                            final String msg = TransformationPlugin.Util.getString("XmlDocumentValidationRule.Column_and_element_types_possibly_not_compatible", new Object[] {column.getName(), dtm.getName(columnType), element.getName(), dtm.getName(xsdType)}); //$NON-NLS-1$
                            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg,
                                                                                  getLocationPath(column), getURIString(column));
                            validationResult.addProblem(problem);
                        }
                        compatible = true;
                        break;
                    }
                }
                if (compatible || nonAtomicAncestor) {
                    continue;
                }
            }

            // not compatible
            // Defect 23464 - changing this rule to us a preference with default WARNING
            int status = context.getPreferenceStatus(ValidationPreferences.XML_INCOMPATIBLE_ELEMENT_COLUMN_DATATYPE,
                                                     IStatus.WARNING);
            final String msg = TransformationPlugin.Util.getString("XmlDocumentValidationRule.Column_and_element_types_not_compatible", new Object[] {column.getName(), dtm.getName(columnType), element.getName(), dtm.getName(xsdType)}); //$NON-NLS-1$
            ValidationProblem problem = new ValidationProblemImpl(0, status, msg, getLocationPath(column), getURIString(column));
            validationResult.addProblem(problem);
        }

    }

    /**
     * returns true if the first built in type in the heirachy is string
     */
    private boolean isStringType( EObject xsdType,
                                  DatatypeManager dtm ) {
        EObject object = null;
        try {
            object = dtm.getDatatypeForXsdType(xsdType);
        } catch (ModelerCoreException err) {
            PluginConstants.Util.log(err);
        }

        if (object != null) {
            if (dtm.isBuiltInDatatype(object) && DatatypeConstants.BuiltInNames.STRING.equals(dtm.getName(object))) {
                return true;
            }
        }

        return false;
    }

    private boolean isAtomicLiteral( XSDSimpleTypeDefinition type ) {
        if (type.getVariety() == XSDVariety.ATOMIC_LITERAL) {
            return true;
        }
        return false;
    }

    private List createHierarcyList( EObject type,
                                     DatatypeManager dtm ) {

        EObject[] hierarchy = dtm.getTypeHierarchy(type);

        if (hierarchy.length < 2) {
            return Collections.EMPTY_LIST;
        }

        ArrayList result = new ArrayList();

        result.addAll(Arrays.asList(hierarchy));
        // remove anySimpleType
        result.remove(result.size() - 1);

        return result;
    }

    /**
     * Mapping classes bound to the element type of sequence, choice, or all cannot be marked for recursion
     */
    private boolean validateMappingClassRecursionAllowed( final ValidationResult validationResult,
                                                          final ValidationContext context ) {
        for (Iterator iter = this.elementMappingClassMap.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry entry = (Map.Entry)iter.next();
            final EObject xmlNode = (EObject)entry.getKey();
            final EObject mapping = (EObject)entry.getValue();

            // Error if a mapping class marked for recursion is bound to an XmlChoice, XmlSequence, or XmlAll
            if (mapping instanceof MappingClassImpl && xmlNode instanceof XmlContainerNode) {
                final MappingClassImpl mc = (MappingClassImpl)mapping;
                if (mc.isRecursionAllowed() && mc.isRecursive()) {
                    final String msg = TransformationPlugin.Util.getString("XmlDocumentValidationRule.Recursion_not_allowed_on_compositor"); //$NON-NLS-1$
                    ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg, getLocationPath(xmlNode),
                                                                          getURIString(xmlNode));
                    validationResult.addProblem(problem);
                    return false;
                }
            }
        }
        return true;
    }

    private static String getURIString( EObject eoj ) {
        return ModelerCore.getModelEditor().getUri(eoj).toString();
    }

    private static String getLocationPath( EObject eoj ) {
        return ModelerCore.getModelEditor().getModelRelativePath(eoj).toString();
    }

    /**
     * A criteria statement must be defined on an element, sequence, choice or all (the "entity") only when 1) The entity exists
     * under a choice (the "owner") 2) The entity is not the default for the owner 3) The entity is not excluded from the document
     * Parse, Resolve and validate the criteria on an entity that satisfies the above conditions.
     */
    private boolean validateChoiceEntities( final Collection choices,
                                            final TreeMappingAdapter mappingAdapter,
                                            final IMappableTree mappableTree,
                                            final ValidationResult validationResult,
                                            final ValidationContext context ) {
        // If this is a Logical model, just return (i.e. XML Message Structure Model)
        // Defect 22678
        boolean isLogicalXMLModel = false;
        if (choices != null && choices.size() > 0) {
            EmfResource emfResource = (EmfResource)((EObject)choices.iterator().next()).eResource();
            if (emfResource.getModelAnnotation() != null) {
                ModelType type = emfResource.getModelAnnotation().getModelType();
                String stringURI = emfResource.getModelAnnotation().getPrimaryMetamodelUri();
                if (type.equals(ModelType.LOGICAL_LITERAL) && XmlDocumentPackage.eNS_URI.equals(stringURI)) {
                    isLogicalXMLModel = true;
                }
            }
        }
        for (Iterator choiceIter = choices.iterator(); choiceIter.hasNext();) {
            XmlChoice xmlChoice = (XmlChoice)choiceIter.next();
            ChoiceOption defaultOption = xmlChoice.getDefaultOption();
            // collect all the xml Element in the document
            DocumentVisitor visitor = new DocumentVisitor();
            ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            try {
                processor.walk(xmlChoice, ModelVisitorProcessor.DEPTH_ONE);
            } catch (ModelerCoreException e) {
                ValidationProblem problem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.ERROR,
                                                                      TransformationPlugin.Util.getString("XmlDocumentValidationRule.Error_trying_to_collect_XmlElements_in_an_XmlDocument__2") + e.getMessage(), getLocationPath(xmlChoice), getURIString(xmlChoice)); //$NON-NLS-1$
                validationResult.addProblem(problem);
                return false;
            }

            // Defect 22678 - Don't validate the choice node if Logical XML model
            if (isLogicalXMLModel) {
                continue;
            }

            Collection choiceContents = visitor.getEntities();
            for (Iterator choiceCntIter = choiceContents.iterator(); choiceCntIter.hasNext();) {
                // The entity exists under a choice (the "owner")
                Object choiceEntity = choiceCntIter.next();
                // choice its self would be part of contents ignore it.
                if (choiceEntity.equals(xmlChoice)) {
                    continue;
                }
                if (choiceEntity instanceof ChoiceOption) {
                    ChoiceOption option = (ChoiceOption)choiceEntity;

                    if (choiceEntity instanceof XmlDocumentNode) {
                        XmlDocumentNode documentNode = (XmlDocumentNode)choiceEntity;
                        // The entity is excluded from the document
                        if (documentNode.isExcludeFromDocument()) {
                            continue;
                        }
                    }

                    // criteria must be defined
                    String choiceCriteria = option.getChoiceCriteria();
                    if (StringUtil.isEmpty(choiceCriteria)) {
                        // The entity is the default for the owner
                        if (ModelerCore.getModelEditor().equals(option, defaultOption)) {
                            continue;
                        }
                        ValidationProblem problem = new ValidationProblemImpl(
                                                                              0,
                                                                              IStatus.ERROR,
                                                                              TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_option_of_a_Choice_must_either_have_the_criteria_defined,_or_be_the_default._3"), getLocationPath(option), getURIString(option)); //$NON-NLS-1$
                        validationResult.addProblem(problem);
                        return false;
                    }

                    // If a criteria statement is defined, it must be parsable and resolvable
                    if (!validateCriteria(option, xmlChoice, mappingAdapter, mappableTree, validationResult, context)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Rules that apply to elements and attributes. An element/attribute may be mapped to a mapping class column except when any
     * of the following conditions are true: 1) The element/attribute is the parent of other elements or attributes 2) The
     * element/attribute has a value type of "DEFAULT" or "FIXED" 3) The element/attribute is excluded from the document 4) The
     * referenced schema component has a min occurs of 0 4) The referenced schema component has a max occurs of 1, as this may
     * result in a document that violates the schema. 5) The referenced schema component is nillable
     */
    private boolean validateEntities( final Collection entities,
                                      final ValidationResult validationResult,
                                      final ValidationContext context ) {
        // If this is a Logical model, just return (i.e. XML Message Structure Model)

        // Defect 22678
        boolean isLogicalXMLModel = false;
        if (entities != null && entities.size() > 0) {
            EmfResource emfResource = (EmfResource)((EObject)entities.iterator().next()).eResource();
            if (emfResource.getModelAnnotation() != null) {
                ModelType type = emfResource.getModelAnnotation().getModelType();
                String stringURI = emfResource.getModelAnnotation().getPrimaryMetamodelUri();
                if (type.equals(ModelType.LOGICAL_LITERAL) && XmlDocumentPackage.eNS_URI.equals(stringURI)) {
                    isLogicalXMLModel = true;
                }
            }
        }

        for (final Iterator entIter = entities.iterator(); entIter.hasNext();) {
            Object entity = entIter.next();
            // ignore XmlDocument object
            if (entity instanceof XmlDocument || !(entity instanceof XmlDocumentNode)) {
                continue;
            }

            // validate the document node
            XmlDocumentNode node = (XmlDocumentNode)entity;

            // validate entities against their schema components
            validateSchemaComponent(node, validationResult, context);

            // node is an element or an attribute
            if (isElementOrAttribute(node)) {
                boolean isProcMapping = false;
                final SqlAspect aspect = AspectManager.getSqlAspect(node);
                if (aspect != null && aspect instanceof XmlElementSqlAspect) {
                    // Ignore certain warnings for Elements mapped to Procedure Inputs
                    isProcMapping = ((XmlElementSqlAspect)aspect).isTranformationInputParameter(node);
                }

                // get the xsdComponent for the node
                XSDComponent xsdComponent = node.getXsdComponent();
                int minOccurs = xsdComponent != null ? XsdUtil.getMinOccurs(xsdComponent) : -1;
                int maxOccurs = xsdComponent != null ? XsdUtil.getMaxOccurs(xsdComponent) : -1;
                // if the node is mapped to a mapping class column
                if (hasMappedMappingClassColumn(node)) {
                    // alll elements and attributes are value holders
                    XmlValueHolder valueHolder = (XmlValueHolder)node;
                    // The element/attribute has a value type of "DEFAULT" or "FIXED"
                    if (valueHolder.isValueFixed() || valueHolder.isValueDefault()) {
                        int status = context.getPreferenceStatus(ValidationPreferences.XML_FIXED_DEFAULT_ELEMENT_MAPPED,
                                                                 IStatus.WARNING);
                        if (!isProcMapping && status != IStatus.OK) {
                            ValidationProblem problem = new ValidationProblemImpl(
                                                                                  0,
                                                                                  status,
                                                                                  TransformationPlugin.Util.getString("XmlDocumentValidationRule.This_entity_{0}_is_fixed_or_default_and_should_not_have_a_mapping_attribute_defined_in_MappingClasses_1", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                            problem.setHasPreference(this.preferences != null);
                            validationResult.addProblem(problem);
                            if (status == IStatus.ERROR) {
                                return false;
                            }
                        }
                    }

                    // The element/attribute is excluded from the document
                    if (node.isExcludeFromDocument()) {
                        int status = context.getPreferenceStatus(ValidationPreferences.XML_EXCLUDED_ELEMENT_MAPPED,
                                                                 IStatus.WARNING);
                        if (!isProcMapping && status != IStatus.OK) {
                            ValidationProblem problem = new ValidationProblemImpl(
                                                                                  0,
                                                                                  status,
                                                                                  TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_entity_{0}_has_been_selected_to_be_excluded_from_the_Document,_but_has_a_mapping_attribute_defined_in_MappingClasses_2", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                            problem.setHasPreference(this.preferences != null);
                            validationResult.addProblem(problem);
                            if (status == IStatus.ERROR) {
                                return false;
                            }
                        }
                    }
                    // The referenced schema component has a min occurs = 0
                    // Validation may not be needed
                    if (minOccurs == 0) {
                        int status = context.getPreferenceStatus(ValidationPreferences.XML_ELEMENT_ZERO_MIN_MAPPED,
                                                                 IStatus.WARNING);
                        if (status != IStatus.OK) {
                            ValidationProblem problem = new ValidationProblemImpl(
                                                                                  0,
                                                                                  status,
                                                                                  TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_entity_{0}_has_a_min_occurs_of_zero,_but_has_a_mapping_attribute_defined_in_MappingClasses_3", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                            validationResult.addProblem(problem);
                            problem.setHasPreference(this.preferences != null);
                            if (status == IStatus.ERROR) {
                                return false;
                            }
                        }
                    }
                    // The referenced schema component is nillable
                    if (XsdUtil.isNillable(xsdComponent)) {
                        int status = context.getPreferenceStatus(ValidationPreferences.XML_ELEMENT_NILLABLE_MAPPED,
                                                                 IStatus.WARNING);
                        if (!isProcMapping && status != IStatus.OK) {
                            ValidationProblem problem = new ValidationProblemImpl(
                                                                                  0,
                                                                                  status,
                                                                                  TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_entity_{0}__s_schema_component_reference_is_nullable,_but_has_a_mapping_attribute_defined_in_MappingClasses._5", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                            validationResult.addProblem(problem);
                            problem.setHasPreference(this.preferences != null);
                            if (status == IStatus.ERROR) {
                                return false;
                            }
                        }
                    }

                    // Defect 18718 - Cannot find any reason why we check for this condition
                    // // The element/attribute is the parent of other elements or attributes
                    // int status = getPreferenceStatus(ValidationPreferences.XML_ELEMENT_CHILDREN_MAPPED, IStatus.WARNING);
                    // if(status != IStatus.OK) {
                    // for(final Iterator contentIter = node.eAllContents();contentIter.hasNext();) {
                    // Object child = contentIter.next();
                    // if(isElementOrAttribute(child)) {
                    //	                          ValidationProblem problem = new ValidationProblemImpl(0, status, TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_entity_{0}_having_element/attribute_children_may_not_have_a_mapping_attribute_defined_in_MappingClasses._6", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                    // problem.setHasPreference(this.preferences != null);
                    // validationResult.addProblem(problem);
                    // if(status == IStatus.ERROR) {
                    // return false;
                    // }
                    // }
                    // }
                    // }

                    // if the node is mapped to a mapping class
                } else if (hasMappedMappingClass(node)) {
                    // Add check to ignore Logical models
                    if (!isLogicalXMLModel) {
                        if (node instanceof XmlRoot) {
                            int status = context.getPreferenceStatus(ValidationPreferences.XML_ROOT_ELEMENT_MAPPING_CLASS,
                                                                     IStatus.WARNING);
                            if (status != IStatus.OK) {
                                ValidationProblem problem = new ValidationProblemImpl(
                                                                                      0,
                                                                                      status,
                                                                                      TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_element_{0}_is_root_but_is_mapped_to_a_MappingClass", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                                problem.setHasPreference(this.preferences != null);
                                validationResult.addProblem(problem);
                                if (status == IStatus.ERROR) {
                                    return false;
                                }
                            }
                            // The referenced schema component has a max occurs == 1
                        } else if (maxOccurs == 1 && node instanceof XmlElement) {
                            int status = context.getPreferenceStatus(ValidationPreferences.XML_ELEMENT_ONE_MAX_MAPPED,
                                                                     IStatus.WARNING);
                            if (status != IStatus.OK) {
                                ValidationProblem problem = new ValidationProblemImpl(
                                                                                      0,
                                                                                      status,
                                                                                      TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_element_{0}_has_a_max_occurs_of_one,_but_is_mapped_to_a_MappingClass", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                                problem.setHasPreference(this.preferences != null);
                                validationResult.addProblem(problem);
                                if (status == IStatus.ERROR) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Rules that apply to entity's schema compatability. Potential Problems (some based on preference) 1) The entity does not
     * reference a schema component. 2) Number of entity's siblings referencing a schema component exceeds the maxOccurrence. 3)
     * Entity is excluded from the document and is not optional. 4) Entity is excluded from the document and is not under an
     * optional entity.
     */
    private boolean validateSchemaComponent( final XmlDocumentNode node,
                                             final ValidationResult validationResult,
                                             final ValidationContext context ) {

        // get the xsdComponent for the node
        XSDComponent xsdComponent = node.getXsdComponent();
        if (xsdComponent == null) {
            if (isElementOrAttribute(node)) {
                int status = context.getPreferenceStatus(ValidationPreferences.XML_ELEMENT_SCHEMA_REFERENCE, IStatus.WARNING);
                if (status != IStatus.OK) {
                    EObject containerNode = node.eContainer();
                    ArgCheck.isInstanceOf(XmlDocumentEntity.class, containerNode);
                    Object containerComponent = getXsdComponent((XmlDocumentEntity)containerNode);
                    if (containerComponent != null) {
                        ValidationProblem problem = new ValidationProblemImpl(
                                                                              0,
                                                                              status,
                                                                              TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_document_element/attribute_{0}_doesn__t_reference_a_schema_component._10", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                        problem.setHasPreference(this.preferences != null);
                        validationResult.addProblem(problem);
                        return false;
                    }
                }
            }
            return true;
        }

        // resolve it if its a proxy
        if (xsdComponent.eIsProxy()) {
            // resolve the proxy
            try {
                final Container container = ModelerCore.getModelContainer();
                EcoreUtil.resolve(xsdComponent, container);
            } catch (CoreException err) {
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, err.getMessage());
                validationResult.addProblem(problem);
                return false;
            }
        }
        // get the max occurs
        int maxOccurs = XsdUtil.getMaxOccurs(xsdComponent);

        // find all the siblings that reference the same schema object
        EObject parent = node.eContainer();
        // if maxOccurs is not unlimited
        if (parent != null && maxOccurs > -1) {
            int childCount = 0;
            for (Iterator contentIter = parent.eContents().iterator(); contentIter.hasNext();) {
                Object child = contentIter.next();
                if (child instanceof XmlDocumentNode) {
                    XmlDocumentNode childNode = (XmlDocumentNode)child;
                    if (!childNode.isExcludeFromDocument()) {
                        // get childNodes xsd component
                        XSDComponent childComponent = childNode.getXsdComponent();
                        if (childComponent != null && ModelerCore.getModelEditor().equals(xsdComponent, childComponent)) {
                            childCount++;
                        }
                    }
                }
            }

            // check the number of children of each schema object to the max number ...
            if (childCount > maxOccurs) {
                int status = context.getPreferenceStatus(ValidationPreferences.XML_ENTITY_MAXOCCURS_VIOLATION, IStatus.WARNING);
                if (status != IStatus.OK) {
                    if (maxOccurs == 0 && XsdUtil.isAttribute(xsdComponent)) {
                        ValidationProblem problem = new ValidationProblemImpl(
                                                                              0,
                                                                              status,
                                                                              TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_attribute_{0}_references_a_prohibited_schema_attribute._7", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                        problem.setHasPreference(this.preferences != null);
                        validationResult.addProblem(problem);
                    } else {
                        ValidationProblem problem = new ValidationProblemImpl(
                                                                              0,
                                                                              status,
                                                                              TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_entity_{0},_may_be_violating_maxOccurs_specified_by_the_schema._1", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                        problem.setHasPreference(this.preferences != null);
                        validationResult.addProblem(problem);
                    }
                }
            }
        }

        // An entity(element/attribute) may be excluded from the document when any of the following conditions are true
        // 1) The element/attribute is optional (i.e., has a reference to a schema component with a minOccurs of 0 or is nillable)
        // 2) The element/attribute exists below an element that is optional
        if (node.isExcludeFromDocument()) {
            Object container = node.eContainer();
            ArgCheck.isInstanceOf(XmlDocumentEntity.class, container);
            XmlDocumentEntity containerNode = (XmlDocumentEntity)container;
            if (!isOptional(node) && !isOptional(containerNode)) {
                boolean isProcMapping = false;
                final SqlAspect aspect = AspectManager.getSqlAspect(node);
                if (aspect != null && aspect instanceof XmlElementSqlAspect) {
                    // Ignore this warning for Elements mapping to Procedure Inputs
                    isProcMapping = ((XmlElementSqlAspect)aspect).isTranformationInputParameter(node);
                }

                int status = context.getPreferenceStatus(ValidationPreferences.XML_REQUIRED_ELEMENT_EXCLUDE, IStatus.WARNING);
                if (!isProcMapping && status != IStatus.OK) {
                    ValidationProblem problem = new ValidationProblemImpl(
                                                                          0,
                                                                          status,
                                                                          TransformationPlugin.Util.getString("XmlDocumentValidationRule.The_entity_{0}_has_been_selected_to_be_excluded_from_the_Document,_but_the_neither_the_entity_nor_its_parent_are_optional._9", node.getName()), getLocationPath(node), getURIString(node)); //$NON-NLS-1$
                    problem.setHasPreference(this.preferences != null);
                    validationResult.addProblem(problem);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * If a criteria statement is defined, it must be parsable and resolvable. Also check if the choice is in the context of the
     * mapping class whose columns are used in the criteria. 1) Error -> Choice criteria is not parsable, resolvable or invalid.
     * 2) Error - > If the choice criteria is referencing groups other than valid mapping classes for this choice node.
     */
    private boolean validateCriteria( final ChoiceOption option,
                                      final XmlChoice xmlChoice,
                                      final TreeMappingAdapter mappingAdapter,
                                      final IMappableTree mappableTree,
                                      final ValidationResult validationResult,
                                      final ValidationContext context ) {

        String choiceCriteria = option.getChoiceCriteria();
        // have a criteria, validate it
        Criteria criteria = null;
        Collection groups = null;
        try {
            // Parse
            QueryParser parser = new QueryParser();
            criteria = parser.parseCriteria(choiceCriteria);

            QueryMetadataInterface metadata = null;
            if (context != null && context.useServerIndexes()) {
                // Validating within the vdb context. The TargetLocationIndexSelector will gather
                // all index files under a specified directory location.
                IndexSelector selector = new TargetLocationIndexSelector(context.getIndexLocation());
                QueryMetadataContext queryContext = new QueryMetadataContext(selector);
                // set the resource scope
                queryContext.setResources(Arrays.asList(context.getResourcesInScope()));
                // set the restrict search falg
                queryContext.setRestrictedSearch(true);
                metadata = TransformationMetadataFactory.getInstance().getVdbMetadata(queryContext,
                                                                                      context.getResourceContainer());
            } else {
                // Validating within the modeler workspace
                metadata = TransformationMetadataFactory.getInstance().getModelerMetadata(option, true);
            }

            // Determine groups
            groups = getGroups(criteria, metadata);

            // Resolve
            ResolverVisitor.resolveLanguageObject(criteria, groups, metadata);

            // validate
            ValidatorReport report = Validator.validate(criteria, metadata);
            if (report.hasItems()) {
                Collection problems = createValidationProblems(report);
                for (Iterator probIter = problems.iterator(); probIter.hasNext();) {
                    validationResult.addProblem((ValidationProblem)probIter.next());
                }
                return false;
            }
        } catch (Throwable e) {
            ValidationProblem problem = new ValidationProblemImpl(
                                                                  0,
                                                                  IStatus.ERROR,
                                                                  TransformationPlugin.Util.getString("XmlDocumentValidationRule.Error_trying_validate_choice_criteria__14") + choiceCriteria + TransformationPlugin.Util.getString("XmlDocumentValidationRule.__15") + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            validationResult.addProblem(problem);
            return false;
        }

        // ----------------------------------------------------------------------------------------
        // Find the valid mapping classes / staging tables for this choice node
        // Verify that the mapping classes used in the criteria are among the valid mapping classes
        // ----------------------------------------------------------------------------------------
        // Gather up the valid mapping classes
        MappingClass mc = mappingAdapter.getMappingClass(xmlChoice);
        if (mc == null) {
            EObject parent = xmlChoice.getParent();
            while (parent != null && mc == null) {
                mc = mappingAdapter.getMappingClass(parent);
                parent = parent.eContainer();
            }
        }
        List validMappingClasses = getParentMappingClasses(mc, mappingAdapter, mappableTree);
        if (mc != null) {
            validMappingClasses.add(mc);
        }

        // Iterate the criteria groups - ensure that they are among the valid list of mapping classes
        for (final Iterator grpIter = groups.iterator(); grpIter.hasNext();) {
            GroupSymbol grpSymbol = (GroupSymbol)grpIter.next();
            MetadataRecord record = (MetadataRecord)grpSymbol.getMetadataID();
            EObject grpObject = (EObject)context.getResourceContainer().getEObjectFinder().find(record.getUUID());
            if (!validMappingClasses.contains(grpObject)) {
                String choicePath = ModelerCore.getModelEditor().getModelRelativePath(option).toString();
                ValidationProblem problem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.ERROR,
                                                                      TransformationPlugin.Util.getString("XmlDocumentValidationRule.0", grpSymbol, choicePath)); //$NON-NLS-1$
                validationResult.addProblem(problem);
                return false;
            }
        }

        return true;
    }

    // Access the TreeMappingAdapter framework and return the collection of parent or previous
    // mapping classes and staging tables that can be used in the choice criteria.
    static List getParentMappingClasses( final MappingClass mappingClass,
                                         final TreeMappingAdapter mappingAdapter,
                                         final IMappableTree mappableTree ) {
        List result = new ArrayList();

        if (mappingAdapter != null) {
            Collection parentMappingClasses = mappingAdapter.getParentMappingClasses(mappingClass, mappableTree, false);
            if (parentMappingClasses != null && !parentMappingClasses.isEmpty()) {
                result.addAll(parentMappingClasses);
            }

        }

        return result;
    }

    class DocumentVisitor implements ModelVisitor {
        Collection elements = new ArrayList();
        Collection attributes = new ArrayList();
        Collection choices = new ArrayList();
        Collection entities = new ArrayList();

        public boolean visit( final EObject eObject ) {
            if (eObject instanceof XmlElement) {
                elements.add(eObject);
            }
            if (eObject instanceof XmlAttribute) {
                attributes.add(eObject);
            }
            if (eObject instanceof XmlChoice) {
                choices.add(eObject);
            }
            entities.add(eObject);
            return true;
        }

        public Collection getElements() {
            return elements;
        }

        public Collection getEntities() {
            return entities;
        }

        public Collection getChoices() {
            return choices;
        }

        public Collection getAttributes() {
            return attributes;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         */
        public boolean visit( final Resource resource ) {
            return true;
        }
    }

    /**
     * Get the resolved groups for the elements on the criteria, including any groups used in subqueries.
     */
    private Collection getGroups( final Criteria criteria,
                                  final QueryMetadataInterface metadata ) throws Exception {
        Collection groups = new HashSet();
        Iterator elemIter = ElementCollectorVisitor.getElements(criteria, true, true).iterator();
        while (elemIter.hasNext()) {
            ElementSymbol element = (ElementSymbol)elemIter.next();
            String groupName = metadata.getGroupName(element.getName());
            GroupSymbol group = new GroupSymbol(groupName);
            if (!groups.contains(group)) {
                group.setMetadataID(metadata.getGroupID(groupName));
                groups.add(group);
            }
        }
        return groups;
    }

    /*
     * Entity is an element or an attribute
     */
    private boolean isElementOrAttribute( final Object entity ) {
        if (entity instanceof XmlElement || entity instanceof XmlAttribute) {
            return true;
        }
        return false;
    }

    /**
     * The element/attribute is optional (i.e., has a reference to a schema component with a minOccurs of 0 or is nillable)
     */
    private boolean isOptional( final XmlDocumentEntity node ) {
        if (isElementOrAttribute(node)) {
            XSDComponent xsdComponent = getXsdComponent(node);
            Assertion.isNotNull(xsdComponent);
            if (XsdUtil.getMinOccurs(xsdComponent) == 0) {
                return true;
            }
            if (XsdUtil.isNillable(xsdComponent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the xsdComponet for the given node.
     */
    private XSDComponent getXsdComponent( final XmlDocumentEntity node ) {
        XSDComponent xsdComponent = null;
        if (node instanceof XmlDocumentNode) {
            xsdComponent = ((XmlDocumentNode)node).getXsdComponent();
        } else if (node instanceof XmlContainerNode) {
            xsdComponent = ((XmlContainerNode)node).getXsdComponent();
        }
        return xsdComponent;
    }

    /**
     * Entity has a mapping to a mapping class column
     */
    private boolean hasMappedMappingClassColumn( final Object entity ) {
        Object mappedObj = this.elementColumnMap.get(entity);
        if (mappedObj != null) {
            return true;
        }
        return false;
    }

    /**
     * Entity has a mapping to a mapping class
     */
    private boolean hasMappedMappingClass( final Object entity ) {
        Object mappedObj = this.elementMappingClassMap.get(entity);
        if (mappedObj != null) {
            return true;
        }
        return false;
    }

    /**
     * Private method for creating a List of ValidationProblem objects from a ValidatorReport
     * 
     * @param report the ValidatorReport
     * @return the List of ValidationProblem
     */
    private Collection createValidationProblems( final ValidatorReport report ) {
        if (report != null && report.hasItems()) {
            Collection items = report.getItems();
            Collection problemList = new ArrayList(items.size());
            for (Iterator itemIter = items.iterator(); itemIter.hasNext();) {
                ReportItem item = (ReportItem)itemIter.next();
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, item.toString());
                problemList.add(problem);
            }
            return problemList;
        }
        return Collections.EMPTY_LIST;
    }
}
