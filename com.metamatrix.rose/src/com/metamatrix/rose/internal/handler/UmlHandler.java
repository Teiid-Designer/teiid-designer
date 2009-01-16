/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.rose.internal.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.importer.rose.builder.RoseStrings;
import org.eclipse.emf.importer.rose.parser.RoseNode;
import org.eclipse.emf.importer.rose.parser.Util;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.CallConcurrencyKind;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.resource.UMLResource;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.uml2.compare.UmlObjectMatcherFactory;
import com.metamatrix.metamodels.uml2.util.PrimitiveTypeManager;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.rose.internal.IRoseConstants;
import com.metamatrix.rose.internal.impl.Unit;

/**
 * Handles importing a Rose model to a UML model.
 * 
 * @since 4.1
 */
public final class UmlHandler extends AbstractRoseHandler implements IRoseConstants.IMetamodelExtensionProperties {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(UmlHandler.class);
    private static final String I18N_STEREOTYPE_PREFIX = "stereotype."; //$NON-NLS-1$

    private static final String APPLYING_STEREOTYPES_MESSAGE = getString("applyingStereotypesMessage"); //$NON-NLS-1$
    private static final String ASSOCIATION_CLASS_MESSAGE = getString("associationClassMessage"); //$NON-NLS-1$
    private static final String CREATING_PROFILE_MESSAGE = getString("creatingProfileMessage"); //$NON-NLS-1$
    private static final String RESOLVING_OWNER_REFERENCES_MESSAGE = getString("resolvingOwnerReferencesMessage"); //$NON-NLS-1$
    private static final String RESOLVING_REFERENCES_MESSAGE = getString("resolvingReferencesMessage"); //$NON-NLS-1$

    private static final String DUPLICATE_CUSTOM_PROPERTY_MESSAGE_ID = "duplicateCustomPropertyMessage"; //$NON-NLS-1$
    private static final String INVALID_PARENT_MESSAGE_ID = "invalidParentMessage"; //$NON-NLS-1$
    private static final String INVALID_SUPERCLASS_MESSAGE_ID = "invalidSuperclassMessage"; //$NON-NLS-1$
    private static final String UNDEFINED_PRIMITIVE_MESSAGE_ID = "undefinedPrimitiveMessage"; //$NON-NLS-1$
    private static final String UNRESOLVABLE_REFERENCE_MESSAGE_ID = "unresolvableReferenceMessage"; //$NON-NLS-1$

    private static final String MULTIPLICITY_DELIMITER = "."; //$NON-NLS-1$
    private static final String PACKAGE_DELIMITER = NamedElement.SEPARATOR;

    private static final int DEFAULT_CARDINALITY = 1;

    private static final String MISSING_VALUE_PREFIX = "<"; //$NON-NLS-1$

    private static final String DATATYPE = "dataType"; //$NON-NLS-1$
    private static final String ENUMERATION = "enumeration"; //$NON-NLS-1$
    private static final String INTERFACE = "interface"; //$NON-NLS-1$
    private static final String PRIMITIVE = "primitive"; //$NON-NLS-1$

    private static final String ASSOCIATION_CLASS = "AssociationClass"; //$NON-NLS-1$

    private static final String STEREOTYPE_PROFILE = getString("stereotypeProfile"); //$NON-NLS-1$
    private static final String ROSE_PROFILE = getString("roseProfile"); //$NON-NLS-1$

    static final String ROSE_STEREOTYPE = getString("roseStereotype"); //$NON-NLS-1$
    private static final String MISSING_STEREOTYPE = getString("missingStereotype"); //$NON-NLS-1$

    static final String QUALIFIED_PREFIX = ROSE_PROFILE + PACKAGE_DELIMITER;
    private static final String QUALIFIED_MISSING_STEREOTYPE = QUALIFIED_PREFIX + MISSING_STEREOTYPE;

    private static final String ASSOCIATION_PROPERTY = DEFAULT_SET + "Association"; //$NON-NLS-1$
    private static final String ATTRIBUTE_PROPERTY = DEFAULT_SET + "Attribute"; //$NON-NLS-1$
    private static final String CATEGORY_PROPERTY = DEFAULT_SET + "Category"; //$NON-NLS-1$
    private static final String CLASS_PROPERTY = DEFAULT_SET + "Class"; //$NON-NLS-1$
    private static final String ROLE_PROPERTY = DEFAULT_SET + "Role"; //$NON-NLS-1$

    private static final String ARRAY = "[]"; //$NON-NLS-1$

    /**
     * @since 4.1
     */
    public static interface IConstants {

        /**
         * @since 4.1
         */
        String QUALIFIED_ROSE_STEREOTYPE = QUALIFIED_PREFIX + ROSE_STEREOTYPE;
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter1,
                                     final Object parameter2 ) {
        return UTIL.getString(I18N_PREFIX + id, parameter1, parameter2);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter1,
                                     final Object parameter2,
                                     final Object parameter3,
                                     final Object parameter4 ) {
        final List prms = Arrays.asList(new Object[] {parameter1, parameter2, parameter3, parameter4});
        return UTIL.getString(I18N_PREFIX + id, prms);
    }

    private UMLFactory factory = UMLFactory.eINSTANCE;

    private RoseNode oppNode;

    private Map stereotypeMap = new HashMap();

    private Map customStereotypeMap = new HashMap();

    private transient Profile stereotypeProfile;

    private transient Model model;

    private Map assocMap = new HashMap();

    private Model umlModel;

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#clear()
     * @since 4.2
     */
    @Override
    public void clear() {
        super.clear();
        this.oppNode = null;
        this.stereotypeMap.clear();
        this.customStereotypeMap.clear();
        this.stereotypeProfile = null;
        this.model = null;
        this.assocMap.clear();
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#createMissingObject(java.lang.String, java.lang.Object, java.lang.String,
     *      java.lang.String)
     * @since 4.1
     */
    public Object createMissingObject( final String type,
                                       final Object referencer,
                                       final String quid,
                                       final String name ) {
        Classifier classifier;
        if (GENERALIZATION.equals(type)) {
            // Haven't bothered to figure out whether all of the following are possible...
            if (referencer instanceof Enumeration) {
                classifier = this.factory.createEnumeration();
            } else if (referencer instanceof PrimitiveType) {
                classifier = this.factory.createPrimitiveType();
            } else if (referencer instanceof DataType) {
                classifier = this.factory.createDataType();
            } else if (referencer instanceof Interface) {
                classifier = this.factory.createInterface();
            } else if (referencer instanceof AssociationClass) {
                classifier = this.factory.createAssociationClass();
            } else {
                classifier = this.factory.createClass();
            }
        } else {
            classifier = this.factory.createClass();
        }
        classifier.setName(name);
        // Set parent to referencer's package
        Element owner = ((NamedElement)referencer).getOwner();
        if (owner == null) {
            // Must be an role, so treat association as owner
            owner = ((Property)referencer).getAssociation();
        }
        // If owner is operation, set owner to operation's owning classifier
        if (owner instanceof Operation) {
            owner = ((Operation)owner).getOwner();
        }
        if (!(owner instanceof Package)) {
            // Must be a classifier, so get its package
            owner = ((Classifier)owner).getPackage();
        }
        // Return if package still not found
        if (owner == null) {
            return null;
        }
        ((Package)owner).getOwnedMembers().add(classifier);
        // Apply missing stereotype
        classifier.applyStereotype(classifier.getApplicableStereotype(QUALIFIED_MISSING_STEREOTYPE));
        // Set Rose extension property values
        setRoseExtensionProperties(classifier, quid, name);
        return classifier;
    }

    /**
     * Recursive.
     * 
     * @see com.metamatrix.rose.internal.IRoseHandler#differenceReportGenerated(java.util.List)
     * @since 4.1
     */
    public void differenceReportGenerated( final List mappings ) {
        // Update difference report for locked UML Elements.
        for (final Iterator mappingIter = mappings.iterator(); mappingIter.hasNext();) {
            final Mapping mapping = (Mapping)mappingIter.next();
            final List outputs = mapping.getOutputs();
            final DifferenceDescriptor descriptor = (DifferenceDescriptor)mapping.getHelper();
            for (final Iterator outputIter = outputs.iterator(); outputIter.hasNext();) {
                final Object obj = outputIter.next();
                if (obj instanceof Element) {
                    final Element elem = (Element)obj;
                    final Stereotype stereotype = elem.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
                    if (stereotype != null) {
                        final Boolean lockedVal = (Boolean)elem.getValue(stereotype, LOCKED);
                        final boolean locked = (lockedVal == null ? false : lockedVal.booleanValue());
                        descriptor.setSkip(locked);
                    }
                }
            }
            // Update difference report for UML Element's locked children.
            differenceReportGenerated(mapping.getNested());
        }
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#getModelType()
     * @since 4.1
     */
    public ModelType getModelType() {
        return ModelType.LOGICAL_LITERAL;
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#getPrimaryMetamodelUri()
     * @since 4.1
     */
    public String getPrimaryMetamodelUri() {
        return UMLPackage.eNS_URI;
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#getUnresolvableReferenceMessage(java.lang.Object, java.lang.String,
     *      java.lang.String)
     * @since 4.1
     */
    public String getUnresolvableReferenceMessage( final Object element,
                                                   final String type,
                                                   final String name ) {
        java.lang.Class clazz = element.getClass();
        String typeName = null;
        final java.lang.Class[] interfaces = clazz.getInterfaces();
        for (int ndx = 0; ndx < interfaces.length; ++ndx) {
            clazz = interfaces[ndx];
            if (NamedElement.class.isAssignableFrom(clazz)) {
                typeName = clazz.getSimpleName();
                break;
            }
        }

        return getString(UNRESOLVABLE_REFERENCE_MESSAGE_ID, typeName, ((NamedElement)element).getQualifiedName(), type, name);
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#initialize(java.util.List)
     * @since 4.1
     */
    public void initialize( final List factories ) {
        // Try to insert the UmlRoseMatcherFactory before the standard UmlObjectMatcherFactory
        int index = 0;
        for (final Iterator iter = factories.iterator(); iter.hasNext();) {
            final Object adapter = iter.next();
            if (adapter instanceof UmlObjectMatcherFactory) {
                // Found it, so break
                break;
            }
            ++index;
        }
        // Add (insert) the new factory at the index
        // (works even if no UmlObjectMatcherFactory was found)
        factories.add(index, new UmlRoseMatcherFactory());
        // Update name map for all NamedElements w/i workspace UML models
        final Map map = getNameMap();
        for (final Iterator modelIter = getWorkspaceModels().iterator(); modelIter.hasNext();) {
            final ModelResource model = (ModelResource)modelIter.next();
            // Save whether model started out open.
            final boolean wasOpen = model.isOpen();
            try {
                try {
                    for (final Iterator objIter = model.getEObjects().iterator(); objIter.hasNext();) {
                        final Object obj = objIter.next();
                        if (obj instanceof NamedElement) {
                            final NamedElement elem = (NamedElement)obj;
                            final Stereotype stereotype = elem.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
                            if (stereotype != null) {
                                final String name = (String)elem.getValue(stereotype, NAME_IN_SOURCE);
                                if (name != null && name.length() > 0) {
                                    map.put(name, elem);
                                }
                                final String elemName = elem.getName();
                                if (elemName != null && elemName.length() > 0 && !elemName.equalsIgnoreCase(name)) {
                                    map.put(elemName, elem);
                                }
                            }
                        }
                    }
                } finally {
                    // Close model if it started out closed.
                    if (!wasOpen) {
                        model.close();
                    }
                }
            } catch (final ModelWorkspaceException err) {
                addProblem(IStatus.ERROR, err, model);
            }
        }
        // Load UML metamodel model for later use in creating and applying stereotypes
        final Resource resrc = new ResourceSetImpl().getResource(URI.createURI(UMLResource.UML_METAMODEL_URI), true);
        this.umlModel = (Model)EcoreUtil.getObjectByType(resrc.getContents(), UMLPackage.eINSTANCE.getModel());
    }

    /**
     * Recursive.
     * 
     * @see com.metamatrix.rose.internal.IRoseHandler#modelsMerged(java.util.List)
     * @since 4.1
     */
    public void modelsMerged( final List mappings ) {
        // Update locked state for UML Elements.
        for (final Iterator mappingIter = mappings.iterator(); mappingIter.hasNext();) {
            final Mapping mapping = (Mapping)mappingIter.next();
            final List outputs = mapping.getOutputs();
            final DifferenceDescriptor descriptor = (DifferenceDescriptor)mapping.getHelper();
            for (final Iterator outputIter = outputs.iterator(); outputIter.hasNext();) {
                final Object obj = outputIter.next();
                if (obj instanceof Element) {
                    final Element elem = (Element)obj;
                    final Stereotype stereotype = elem.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
                    if (stereotype != null) {
                        final Boolean lockedVal = (Boolean)elem.getValue(stereotype, LOCKED);
                        final boolean locked = (lockedVal == null ? false : lockedVal.booleanValue());
                        final boolean skipped = descriptor.isSkip();
                        if (locked != skipped) {
                            elem.setValue(stereotype, LOCKED, skipped ? Boolean.TRUE : Boolean.FALSE);
                        }
                    }
                }
            }
            // Update locked state for UML Element's children.
            modelsMerged(mapping.getNested());
        }
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#resolveReference(java.lang.Object, java.lang.Object, java.lang.String,
     *      java.lang.String)
     * @since 4.1
     */
    public void resolveReference( final Object referencer,
                                  final Object object,
                                  final String type,
                                  final String quid ) {
        if (TYPE.equals(type)) {
            if (referencer instanceof Property) {
                ((Property)referencer).setType((Type)object);
            } else if (referencer instanceof Operation) {
                ((Operation)referencer).setType((Type)object);
            } else { // Must be a parameter
                ((Parameter)referencer).setType((Type)object);
            }
        } else if (GENERALIZATION.equals(type)) {
            resolveGeneralizationReference((Classifier)referencer, (Classifier)object);
        } else { // Must be owner
            resolveOwnerReference((Property)referencer, (Classifier)object, quid);
        }
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createAssociation(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    @Override
    protected Object createAssociation( final String name,
                                        final RoseNode node ) {
        // Create UML Association.
        final Association assoc = this.factory.createAssociation();
        // Set visibility
        final RoseNode visibility = node.findNodeWithKey(RoseStrings.EXPORTCONTROL);
        if (visibility != null) {
            assoc.setVisibility(VisibilityKind.get(visibility.getValue()));
        }
        // Add Association to parent.
        final Package parent = (Package)getParent(node, name, Package.class);
        parent.getOwnedMembers().add(assoc);
        // Set Association's properties.
        assoc.setName(name);
        assoc.setIsDerived(node.isDerived());
        // Set Rose extension property values
        setRoseExtensionProperties(assoc, node, name);
        // If linked to an association class, save association and association class name for correct conversion to UML
        // AssociationClass in parsingFinished method.
        final RoseNode assocClass = node.findNodeWithKey(ASSOCIATION_CLASS);
        if (assocClass != null) {
            this.assocMap.put(assoc, Util.trimQuotes(assocClass.getValue()));
        }
        // Clear opposite property and Rose node (previously set by another association's role) so we can process this
        // association's roles.
        this.oppNode = null;

        return assoc;
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createAttribute(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode, java.lang.String, java.util.Map)
     * @since 4.2
     */
    @Override
    protected void createAttribute( final String name,
                                    final RoseNode node,
                                    final Map propertyMap ) {
        // Check if supported property
        EClass metaclass = null;
        if (CATEGORY_PROPERTY.equals(name)) {
            metaclass = UMLPackage.eINSTANCE.getPackage();
        } else if (CLASS_PROPERTY.equals(name)) {
            metaclass = UMLPackage.eINSTANCE.getClassifier();
        } else if (ATTRIBUTE_PROPERTY.equals(name) || ROLE_PROPERTY.equals(name)) {
            metaclass = UMLPackage.eINSTANCE.getProperty();
        } else if (ASSOCIATION_PROPERTY.equals(name)) {
            metaclass = UMLPackage.eINSTANCE.getAssociation();
        }
        if (metaclass == null) {
            return;
        }
        // Create stereotype
        createStereotypeProfile();
        Stereotype stereotype = getStereotype(name, this.stereotypeProfile);
        Map map;
        if (stereotype == null) {
            stereotype = createStereotype(name);
            // Create appropriate extension for metaclass
            final Class metaclassObj = (Class)this.umlModel.getOwnedType(metaclass.getName());
            if (!this.stereotypeProfile.getReferencedMetaclasses().contains(metaclassObj)) {
                this.stereotypeProfile.createMetaclassReference(metaclassObj);
            }
            stereotype.createExtension(metaclassObj, true);
            // Add stereotype to custom stereotype map
            map = new HashMap();
            this.customStereotypeMap.put(stereotype, map);
        } else {
            map = (Map)this.customStereotypeMap.get(stereotype);
        }
        // Add all names and values from property map to stereotype
        // Note, no checking is done to see if name conflicts with existing feature, but shouldn't happen since name always begins
        // with underscore, and should always begin with _<tool>_ (e.g. _java_read_write).
        for (final Iterator iter = propertyMap.entrySet().iterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            // Check if property already created
            final Object key = entry.getKey();
            String initVal = (String)entry.getValue();
            if (map.containsKey(key)) {
                addProblem(IStatus.WARNING, getString(DUPLICATE_CUSTOM_PROPERTY_MESSAGE_ID, name, getUnit(), key, initVal), node);
                continue;
            }
            // Create UML Property.
            final Property prop = this.factory.createProperty();
            // Set Property's properties
            prop.setName((String)key);
            String type;
            Object val;
            if (initVal.equalsIgnoreCase(Boolean.TRUE.toString())) {
                val = Boolean.TRUE;
                type = PrimitiveTypeManager.BOOLEAN_PRIMITIVE_TYPE;
            } else if (initVal.equalsIgnoreCase(Boolean.FALSE.toString())) {
                val = Boolean.FALSE;
                type = PrimitiveTypeManager.BOOLEAN_PRIMITIVE_TYPE;
            } else {
                try {
                    val = Integer.valueOf(initVal);
                    type = PrimitiveTypeManager.INTEGER_PRIMITIVE_TYPE;
                } catch (final NumberFormatException err) {
                    val = Util.trimQuotes(initVal);
                    type = PrimitiveTypeManager.STRING_PRIMITIVE_TYPE;
                }
            }
            prop.setType(PrimitiveTypeManager.INSTANCE.getPrimitiveType(type));
            // Add key/value to custom stereotype map
            map.put(key, val);
            // Add property to stereotype
            stereotype.getOwnedAttributes().add(prop);
        }
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createClass(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    @Override
    protected Object createClass( final String name,
                                  final RoseNode node ) {
        // Create UML Class, Enumeration, or Datatype depending on stereotype.
        Classifier classifier;
        String stereotypeName = node.getStereotype();
        if (stereotypeName == null) {
            classifier = this.factory.createClass();
        } else {
            // Use i18n mapping to determine appropriate UML stereotype
            final String umlStereotypeName = getString(I18N_STEREOTYPE_PREFIX + stereotypeName.toLowerCase());
            if (!umlStereotypeName.startsWith(MISSING_VALUE_PREFIX)) {
                stereotypeName = umlStereotypeName;
            }
            if (ENUMERATION.equalsIgnoreCase(stereotypeName)) {
                classifier = this.factory.createEnumeration();
            } else if (DATATYPE.equalsIgnoreCase(stereotypeName)) {
                classifier = this.factory.createDataType();
            } else if (INTERFACE.equalsIgnoreCase(stereotypeName)) {
                classifier = this.factory.createInterface();
            } else if (PRIMITIVE.equalsIgnoreCase(stereotypeName)) {
                classifier = this.factory.createPrimitiveType();
            } else if (ASSOCIATION_CLASS.equalsIgnoreCase(stereotypeName)) {
                classifier = this.factory.createAssociationClass();
            } else {
                classifier = this.factory.createClass();
            }
            if (!(classifier instanceof Class)) {
                stereotypeName = null;
            }
        }
        // Add Class to parent
        final Object parent = getParent(node, name, NamedElement.class);
        if (parent instanceof Package) {
            ((Package)parent).getOwnedMembers().add(classifier);
        } else if (parent instanceof Class) {
            ((Class)parent).getNestedClassifiers().add(classifier);
        } else if (parent instanceof Interface) {
            ((Interface)parent).getNestedClassifiers().add(classifier);
        } else {
            addProblem(IStatus.WARNING, getString(INVALID_PARENT_MESSAGE_ID, name, getUnit()), node);
            return null;
        }
        // Set Class's properties
        classifier.setName(name);
        classifier.setIsAbstract(node.isAbstract());
        // Set visibility
        final RoseNode visibility = node.findNodeWithKey(RoseStrings.EXPORTCONTROL);
        if (visibility != null) {
            classifier.setVisibility(VisibilityKind.get(visibility.getValue()));
        }
        // Add stereotype if necessary
        if (stereotypeName != null) {
            createStereotypeProfile();
            Stereotype stereotype = getStereotype(stereotypeName, this.stereotypeProfile);
            if (stereotype == null) {
                stereotype = createStereotype(stereotypeName);
                // Create extension for Classifier class for this stereotype
                final Class metaclass = (Class)this.umlModel.getOwnedType(UMLPackage.eINSTANCE.getClassifier().getName());
                if (!this.stereotypeProfile.getReferencedMetaclasses().contains(metaclass)) {
                    this.stereotypeProfile.createMetaclassReference(metaclass);
                }
                stereotype.createExtension(metaclass, false);
                // Save association of classifier to stereotype for resolution in parsingFinished method.
                final List classifiers = new ArrayList();
                classifiers.add(classifier);
                this.stereotypeMap.put(stereotype, classifiers);
            } else {
                // Save association of classifier to stereotype for resolution in parsingFinished method.
                ((List)this.stereotypeMap.get(stereotype)).add(classifier);
            }
        }
        // Set Rose extension property values
        setRoseExtensionProperties(classifier, node, name);

        return classifier;
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createClassAttribute(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode, java.lang.String)
     * @since 4.1
     */
    @Override
    protected Object createClassAttribute( final String name,
                                           final RoseNode node,
                                           final String multiplicity ) {
        NamedElement elem;
        final Classifier parent = (Classifier)getParent(node, name, Classifier.class);
        if (parent instanceof Enumeration) {
            // Create UML EnumerationLiteral.
            final EnumerationLiteral literal = this.factory.createEnumerationLiteral();
            literal.setName(name);
            // Set visibility
            final RoseNode visibility = node.findNodeWithKey(RoseStrings.EXPORTCONTROL);
            if (visibility != null) {
                literal.setVisibility(VisibilityKind.get(visibility.getValue()));
            }
            // Add Property to parent
            ((Enumeration)parent).getOwnedLiterals().add(literal);

            elem = literal;
        } else {
            // Create UML Property.
            final Property prop = createProperty(name, node);
            // Set multiplicity from stereotype
            setMultiplicity(prop, multiplicity);
            if (parent instanceof Class) {
                // Add Property to parent
                ((Class)parent).getOwnedAttributes().add(prop);
            } else if (parent instanceof Interface) {
                // Add Property to parent
                ((Interface)parent).getOwnedAttributes().add(prop);
            } else if (parent instanceof DataType) {
                // Add Property to parent
                ((DataType)parent).getOwnedAttributes().add(prop);
            } else {
                addProblem(IStatus.WARNING, getString(INVALID_PARENT_MESSAGE_ID, name, getUnit()), node);
                return prop;
            }
            // Save Rose node for reference resolution in parsingFinished method
            getUnresolvedReferences().add(node);

            elem = prop;
        }
        // Now that property/literal has been added to parent, set Rose extension property values
        setRoseExtensionProperties(elem, node, name);

        return elem;
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createInheritanceRelationship(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    @Override
    protected void createInheritanceRelationship( final String name,
                                                  final RoseNode node ) {
        // Save supertype reference for resolution in parsingFinished method
        final Classifier parent = (Classifier)getParent(node, name, Classifier.class);
        node.setNode(parent);
        getUnresolvedReferences().add(node);
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createOperation(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.2
     */
    @Override
    protected Object createOperation( final String name,
                                      final RoseNode node ) {
        // Create UML Operation.
        final Operation op = this.factory.createOperation();
        // Set Operation's properties
        op.setName(name);
        op.setIsAbstract(node.isAbstract());
        op.setIsStatic(!node.isChangeable());
        op.setIsUnique(node.isUnique());
        // Set visibility
        final RoseNode visibility = node.findNodeWithKey(RoseStrings.OPEXPORTCONTROL);
        if (visibility != null) {
            op.setVisibility(VisibilityKind.get(visibility.getValue()));
        }
        // Set concurrency
        final RoseNode concurrency = node.findNodeWithKey(RoseStrings.CONCURRENCY);
        if (concurrency != null) {
            op.setConcurrency(CallConcurrencyKind.get(concurrency.getValue().toLowerCase()));
        }
        // Add to parent
        final Classifier parent = (Classifier)getParent(node, name, Classifier.class);
        if (parent instanceof Class) {
            // Add Property to parent
            ((Class)parent).getOwnedOperations().add(op);
        } else if (parent instanceof Interface) {
            // Add Property to parent
            ((Interface)parent).getOwnedOperations().add(op);
        } else if (parent instanceof DataType) {
            // Add Property to parent
            ((DataType)parent).getOwnedOperations().add(op);
        } else {
            addProblem(IStatus.WARNING, getString(INVALID_PARENT_MESSAGE_ID, name, getUnit()), node);
            return op;
        }
        // Save Rose node for reference resolution in parsingFinished method (for result type and parameters)
        getUnresolvedReferences().add(node);

        return op;
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createPackage(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    @Override
    protected Object createPackage( final String name,
                                    final RoseNode node ) {
        // Create UML Package.
        final Package pkg = this.factory.createPackage();
        // Set Package's properties
        pkg.setName(name);
        // Set visibility
        final RoseNode visibility = node.findNodeWithKey(RoseStrings.EXPORTCONTROL);
        if (visibility != null) {
            pkg.setVisibility(VisibilityKind.get(visibility.getValue()));
        }
        // Add Package to parent
        Package parent = (Package)getParent(node, name, Package.class);
        parent.getOwnedMembers().add(pkg);
        // Set Rose extension property values
        setRoseExtensionProperties(pkg, node, name);

        return pkg;
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#createRole(java.lang.String,
     *      org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    @Override
    protected Object createRole( final String name,
                                 final RoseNode node ) {
        // Create UML Property.
        final Property prop = createProperty(name, node);
        // Set property's multiplicity
        setMultiplicity(prop, node.getRoleMultiplicity());
        if (node.isNavigable()) {
        } else {
        }
        // If first role of association, save property and Rose node until opposite role is processed
        if (this.oppNode == null) {
            this.oppNode = node;
        } else {
            final Property oppProp = (Property)this.oppNode.getNode();
            // Set aggregation kind, if applicable, using opposite Rose node's containment value
            setAggregationKind(this.oppNode, node, prop);
            // Set opposite property's aggregation kind, if applicable, using this Rose node's containment value
            setAggregationKind(node, this.oppNode, oppProp);
            // Add to association as member end if navigable, as owned end otherwise, and save owner reference, obtained from
            // opposite property's type, for resolution in parsingFinished method
            final Association assoc = (Association)getParent(node, name, Association.class);
            if (node.isNavigable() || !this.oppNode.isNavigable()) {
                getOwnerMap().put(prop, this.oppNode);
                assoc.getMemberEnds().add(prop);
            } else {
                assoc.getOwnedEnds().add(prop);
            }
            if (this.oppNode.isNavigable() || !node.isNavigable()) {
                getOwnerMap().put(oppProp, node);
                assoc.getMemberEnds().add(oppProp);
            } else {
                assoc.getOwnedEnds().add(oppProp);
            }
        }
        return prop;
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#parsingFinished(java.util.List)
     * @since 4.1
     */
    @Override
    public void parsingFinished( final List rootsList ) {
        // Transform associations with association classes to UML AssocationClasses. Must be done before refs are resolved.
        // Update monitor
        final ModelEditor editor = ModelerCore.getModelEditor();
        getProgressMonitor().subTask(ASSOCIATION_CLASS_MESSAGE);
        for (final Iterator assocIter = this.assocMap.entrySet().iterator(); assocIter.hasNext();) {
            final Entry assocEntry = (Entry)assocIter.next();
            final Association assoc = (Association)assocEntry.getKey();
            // Find association class
            Class clazz = null;
            final String path = (String)assocEntry.getValue();
            for (final Iterator rootsListIter = rootsList.iterator(); rootsListIter.hasNext();) {
                final StringTokenizer pathIter = new StringTokenizer(path, PACKAGE_DELIMITER);
                pathIter.nextToken(); // Get past the logical view
                final List roots = (List)rootsListIter.next();
                for (final Iterator rootIter = roots.iterator(); rootIter.hasNext();) {
                    final Object root = rootIter.next();
                    if (root instanceof Model) {
                        clazz = findAssociationClass(((Model)root).getOwnedMembers(), pathIter);
                        break;
                    }
                }
                if (clazz != null) {
                    break;
                }
            }
            // Create missing AssociationClass if not found
            if (clazz == null) {
                clazz = this.factory.createAssociationClass();
                clazz.setName(path.substring(path.lastIndexOf(PACKAGE_DELIMITER)) + PACKAGE_DELIMITER.length());
                assoc.getPackage().getOwnedMembers().add(clazz);
                // Apply missing stereotype
                clazz.applyStereotype(clazz.getApplicableStereotype(QUALIFIED_MISSING_STEREOTYPE));
                // Set Rose extension property values
                setRoseExtensionProperties(clazz, EMPTY_STRING, path);
                addProblem(IStatus.WARNING, getUnresolvableReferenceMessage(assoc, ASSOCIATION_CLASS, path), assoc);
            } else if (!(clazz instanceof AssociationClass)) {
                // Create AssociationClass
                final Class oldClass = clazz;
                clazz = this.factory.createAssociationClass();
                // Copy old class properties to AssociationClass
                clazz.setName(oldClass.getName());
                clazz.setIsAbstract(oldClass.isAbstract());
                clazz.setVisibility(oldClass.getVisibility());
                // Remove old class from parent and add AssociationClass to parent
                final Object parent = oldClass.getOwner();
                List members;
                if (parent instanceof Package) {
                    members = ((Package)parent).getOwnedMembers();
                } else if (parent instanceof Class) {
                    members = ((Class)parent).getNestedClassifiers();
                } else { // Must be an interface
                    members = ((Interface)parent).getNestedClassifiers();
                }
                members.add(clazz);
                try {
                    // Move description to association class
                    final Annotation classAnnotation = editor.getAnnotation(oldClass, false);
                    if (classAnnotation != null) {
                        final Annotation assocClassAnnotation = editor.getAnnotation(clazz, true);
                        assocClassAnnotation.setDescription(classAnnotation.getDescription());
                        classAnnotation.getAnnotationContainer().getAnnotations().remove(classAnnotation);
                    }
                } catch (final ModelerCoreException err) {
                    addProblem(IStatus.ERROR, err, assoc);
                }
                // Remove annotations from old class's children
                removeAnnotations(oldClass);
                // Save old class' Rose extension property values
                Stereotype stereotype = oldClass.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
                final Object nameInSrc = oldClass.getValue(stereotype, NAME_IN_SOURCE);
                final Object quid = oldClass.getValue(stereotype, QUID);
                final Object src = oldClass.getValue(stereotype, SOURCE);
                // Remove old class from parent
                members.remove(oldClass);
                // Update maps
                for (final Iterator stereotypeIter = this.stereotypeMap.values().iterator(); stereotypeIter.hasNext();) {
                    final List classifiers = (List)stereotypeIter.next();
                    for (final Iterator classifierIter = classifiers.iterator(); classifierIter.hasNext();) {
                        final Classifier classifier = (Classifier)classifierIter.next();
                        if (classifier == oldClass) {
                            classifiers.set(classifiers.indexOf(oldClass), clazz);
                            break;
                        }
                    }
                }
                final Map idMap = getIdMap();
                for (final Iterator idIter = idMap.entrySet().iterator(); idIter.hasNext();) {
                    final Entry idEntry = (Entry)idIter.next();
                    if (idEntry.getValue() == oldClass) {
                        idMap.put(idEntry.getKey(), clazz);
                        break;
                    }
                }
                // Set Rose extension property values
                stereotype = clazz.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
                clazz.setValue(stereotype, NAME_IN_SOURCE, nameInSrc);
                clazz.setValue(stereotype, LOCKED, Boolean.FALSE);
                clazz.setValue(stereotype, QUID, quid);
                clazz.setValue(stereotype, SOURCE, src);
            }
            // Copy association properties to AssociationClass
            final AssociationClass assocClass = (AssociationClass)clazz;
            assocClass.setIsDerived(assoc.isDerived());
            // Move roles to association class
            final List members = new ArrayList(assoc.getMemberEnds());
            final List owned = new ArrayList(assoc.getOwnedEnds());
            assocClass.getMemberEnds().addAll(members);
            assocClass.getOwnedEnds().addAll(owned);
            try {
                // Move description to association class
                final Annotation assocAnnotation = editor.getAnnotation(assoc, false);
                if (assocAnnotation != null) {
                    final Annotation assocClassAnnotation = editor.getAnnotation(assocClass, true);
                    final String oldDesc = assocClassAnnotation.getDescription();
                    final String desc = assocAnnotation.getDescription();
                    assocClassAnnotation.setDescription(oldDesc == null ? desc : oldDesc + ' ' + desc);
                    assocAnnotation.getAnnotationContainer().getAnnotations().remove(assocAnnotation);
                }
            } catch (final ModelerCoreException err) {
                addProblem(IStatus.ERROR, err, assoc);
            }
            // Remove annotations from old class's children
            removeAnnotations(assoc);
            // Remove old association from parent
            assoc.getPackage().getOwnedMembers().remove(assoc);
        }
        // Resolve references
        // Update monitor
        getProgressMonitor().subTask(RESOLVING_REFERENCES_MESSAGE);
        for (final Iterator refIter = getUnresolvedReferences().iterator(); refIter.hasNext();) {
            final RoseNode node = (RoseNode)refIter.next();
            final NamedElement elem = (NamedElement)node.getNode();
            final String quid = node.getRoseRefId();
            String name = node.getRoseSupplier();
            if (name == null) {
                name = node.getType();
            }
            if (name != null) {
                name = Util.trimQuotes(name);
            }
            if (elem instanceof Classifier) {
                // Resolve generalization reference
                // Try to resolve reference by quid
                Classifier superClassifier = (Classifier)resolveReference(quid);
                if (superClassifier == null) {
                    // Try to resolve by name
                    if (name != null) {
                        List classes = (List)getNameMap().get(name);
                        if (classes != null && elem instanceof Class) {
                            classes = new ArrayList(classes);
                            // Remove non-classes as possible superclass if element is a class
                            for (final Iterator classIter = classes.iterator(); classIter.hasNext();) {
                                if (!(classIter.next() instanceof Class)) {
                                    classIter.remove();
                                }
                            }
                        }
                        superClassifier = (Classifier)resolveReferenceByName(elem, GENERALIZATION, node, name, classes);
                    }
                } else if (elem instanceof Class && !(superClassifier instanceof Class)) {
                    addProblem(IStatus.WARNING, getString(INVALID_SUPERCLASS_MESSAGE_ID, name, elem.getQualifiedName()), elem);
                    superClassifier = null;
                }
                if (superClassifier != null) {
                    resolveGeneralizationReference((Classifier)elem, superClassifier);
                }
            } else if (elem instanceof Property) {
                final Property prop = (Property)elem;
                // Resolve type reference
                final Type type = resolveTypeReference(node, elem, quid, name);
                if (type != null) {
                    prop.setType(type);
                    // Set initial/default value
                    if (type instanceof PrimitiveType) {
                        final String dflt = node.getInitV();
                        prop.setDefaultValue(PrimitiveTypeManager.INSTANCE.createValueSpecification((PrimitiveType)type, dflt));
                    }
                }
                // Change element's multiplicity if type is an array
                if (name != null && name.endsWith(ARRAY) && prop.getUpper() == 1) {
                    final LiteralUnlimitedNatural upper = this.factory.createLiteralUnlimitedNatural();
                    upper.setValue(LiteralUnlimitedNatural.UNLIMITED);
                    prop.setUpperValue(upper);
                }
            } else if (elem instanceof Operation) {
                // Resolve type reference
                if (quid == null && name == null) {
                    final RoseNode result = node.findNodeWithKey(RoseStrings.RESULT);
                    if (result != null) {
                        name = result.getValue();
                        if (name != null) {
                            name = Util.trimQuotes(name);
                        }
                    }
                }
                final Type type = resolveTypeReference(node, elem, quid, name);
                final Operation op = (Operation)elem;
                if (type != null) {
                    op.setType(type);
                }
                // Change element's multiplicity if type is an array
                if (name != null && name.endsWith(ARRAY) && op.getUpper() == 1) {
                    final LiteralUnlimitedNatural upper = this.factory.createLiteralUnlimitedNatural();
                    upper.setValue(LiteralUnlimitedNatural.UNLIMITED);
                    op.setUpper(upper.getValue());
                }
                // Add parameters to operation
                final RoseNode prmNodes = node.findNodeWithKey(RoseStrings.PARAMETERS);
                if (prmNodes != null) {
                    for (final Iterator prmNodeIter = prmNodes.getNodes().iterator(); prmNodeIter.hasNext();) {
                        final RoseNode prmNode = (RoseNode)prmNodeIter.next();
                        final Parameter prm = this.factory.createParameter();
                        prm.setName(Util.getName(prmNode.getValue()));
                        final String prmTypeQuid = prmNode.getRoseRefId();
                        String prmTypeName = prmNode.getRoseSupplier();
                        if (prmTypeName == null) {
                            prmTypeName = prmNode.getType();
                        }
                        if (prmTypeName != null) {
                            prmTypeName = Util.trimQuotes(prmTypeName);
                        }
                        // Add parameter to operation
                        op.getOwnedParameters().add(prm);
                        // Resolve type reference
                        final Type prmType = resolveTypeReference(prmNode, prm, prmTypeQuid, prmTypeName);
                        if (prmType != null) {
                            prm.setDirection(ParameterDirectionKind.IN_LITERAL);
                            prm.setType(prmType);
                        }
                        // Change element's multiplicity if type is an array
                        if (prmTypeName != null && prmTypeName.endsWith(ARRAY) && prm.getUpper() == 1) {
                            final LiteralUnlimitedNatural upper = this.factory.createLiteralUnlimitedNatural();
                            upper.setValue(LiteralUnlimitedNatural.UNLIMITED);
                            prm.setUpperValue(upper);
                        }
                    }
                }
            }
        }
        // Resolve association property owner references
        // Update monitor
        getProgressMonitor().subTask(RESOLVING_OWNER_REFERENCES_MESSAGE);
        for (final Iterator iter = getOwnerMap().entrySet().iterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            final Property prop = (Property)entry.getKey();
            final RoseNode node = (RoseNode)entry.getValue();
            final String quid = node.getRoseRefId();
            final String name = node.getRoseSupplier();
            // Try to resolve reference by quid
            Classifier classifier = (Classifier)resolveReference(quid);
            if (classifier == null && name != null) {
                // Try to resolve by name
                classifier = (Classifier)resolveReferenceByName(prop, OWNER, node, name, (List)getNameMap().get(name));
            }
            if (classifier != null) {
                resolveOwnerReference(prop, classifier, node.getRoseId());
            }
            // Set documentation on node now that it has a name
            final String text = node.getDocumentation();
            if (text != null && text.length() > 0) {
                try {
                    final Annotation annotation = ModelerCore.getModelEditor().getAnnotation(prop, true);
                    annotation.setDescription(text);
                } catch (final ModelerCoreException err) {
                    addProblem(IStatus.ERROR, err, prop);
                }
            }
        }
        // Define and apply stereotype profiles
        // Update monitor
        getProgressMonitor().subTask(CREATING_PROFILE_MESSAGE);
        for (final Iterator rootsListIter = rootsList.iterator(); rootsListIter.hasNext();) {
            final List roots = (List)rootsListIter.next();
            // Find stereotype profile and model
            Profile stereotypeProfile = null;
            Model model = null;
            for (final Iterator rootIter = roots.iterator(); rootIter.hasNext();) {
                final Object root = rootIter.next();
                if (root instanceof Profile) {
                    final Profile profile = (Profile)root;
                    if (STEREOTYPE_PROFILE.equals(profile.getName())) {
                        stereotypeProfile = profile;
                    }
                } else if (root instanceof Model) {
                    model = (Model)root;
                }
            }
            if (model != null && stereotypeProfile != null) {
                stereotypeProfile.define();
                // Apply stereotype profile to model
                model.applyProfile(stereotypeProfile);
                // Apply metaclass-specific stereotypes
                for (final Iterator elemIter = model.eAllContents(); elemIter.hasNext();) {
                    final Object obj = elemIter.next();
                    if (!(obj instanceof Element)) {
                        continue;
                    }
                    final Element elem = (Element)obj;
                    Stereotype stereotype = null;
                    if (elem instanceof Class) {
                        stereotype = getStereotype(CLASS_PROPERTY, stereotypeProfile);
                    } else if (elem instanceof Package) {
                        stereotype = getStereotype(CATEGORY_PROPERTY, stereotypeProfile);
                    } else if (elem instanceof Association) {
                        stereotype = getStereotype(ASSOCIATION_PROPERTY, stereotypeProfile);
                    } else if (elem instanceof Property) {
                        if (((Property)elem).getOwningAssociation() == null) {
                            stereotype = getStereotype(ATTRIBUTE_PROPERTY, stereotypeProfile);
                        } else {
                            stereotype = getStereotype(ROLE_PROPERTY, stereotypeProfile);
                        }
                    }
                    if (stereotype != null) {
                        final Map propMap = (Map)this.customStereotypeMap.get(stereotype);
                        for (final Iterator propIter = propMap.entrySet().iterator(); propIter.hasNext();) {
                            final Entry entry = (Entry)propIter.next();
                            elem.setValue(stereotype, (String)entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }
        // Apply stereotypes
        // Update monitor
        getProgressMonitor().subTask(APPLYING_STEREOTYPES_MESSAGE);
        // Apply class-specific stereotypes
        for (final Iterator stereotypeIter = this.stereotypeMap.entrySet().iterator(); stereotypeIter.hasNext();) {
            final Entry entry = (Entry)stereotypeIter.next();
            final Stereotype stereotype = (Stereotype)entry.getKey();
            for (final Iterator classifierIter = ((List)entry.getValue()).iterator(); classifierIter.hasNext();) {
                final Classifier classifier = (Classifier)classifierIter.next();
                classifier.applyStereotype(stereotype);
            }
        }
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#parsingStarting(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.1
     */
    @Override
    public void parsingStarting( final IProgressMonitor monitor ) {
        super.parsingStarting(monitor);
        this.stereotypeMap.clear();
        this.customStereotypeMap.clear();
    }

    /**
     * @see com.metamatrix.rose.internal.handler.AbstractRoseHandler#unitParsingStarting(com.metamatrix.rose.internal.impl.Unit,
     *      java.util.List)
     * @since 4.2
     */
    @Override
    public void unitParsingStarting( final Unit unit,
                                     final List roots ) {
        super.unitParsingStarting(unit, roots);
        // Cache the model and stereotypes profile from the root package if either exists
        this.model = null;
        this.stereotypeProfile = null;
        for (final Iterator iter = roots.iterator(); iter.hasNext();) {
            final Object root = iter.next();
            if (root instanceof Profile) {
                final Profile profile = (Profile)root;
                if (STEREOTYPE_PROFILE.equals(profile.getName())) {
                    this.stereotypeProfile = profile;
                }
            } else if (root instanceof Model) {
                this.model = (Model)root;
            }
        }
        if (this.model == null) {
            // Create model
            this.model = this.factory.createModel();
            this.model.setName(getModelType().getName());
            // Add model to root
            roots.add(this.model);
            // Set model type
            ((EmfResource)this.model.eResource()).getModelAnnotation().setModelType(getModelType());
            // Create Rose profile
            final Profile profile = this.factory.createProfile();
            profile.setName(ROSE_PROFILE);
            // Create Rose stereotype
            Stereotype stereotype = profile.createOwnedStereotype(ROSE_STEREOTYPE, false);
            // Create Rose extension properties
            createRoseExtensionProperty(stereotype, QUID, PrimitiveTypeManager.STRING_PRIMITIVE_TYPE);
            createRoseExtensionProperty(stereotype, NAME_IN_SOURCE, PrimitiveTypeManager.STRING_PRIMITIVE_TYPE);
            createRoseExtensionProperty(stereotype, SOURCE, PrimitiveTypeManager.STRING_PRIMITIVE_TYPE);
            createRoseExtensionProperty(stereotype, LOCKED, PrimitiveTypeManager.BOOLEAN_PRIMITIVE_TYPE);
            // Create extension for Rose stereotype that will automatically get applied to all UML Elements
            Class metaclass = (Class)this.umlModel.getOwnedType(UMLPackage.eINSTANCE.getElement().getName());
            if (!profile.getReferencedMetaclasses().contains(metaclass)) {
                profile.createMetaclassReference(metaclass);
            }
            stereotype.createExtension(metaclass, true);
            // Create <missing> stereotype
            stereotype = profile.createOwnedStereotype(MISSING_STEREOTYPE, false);
            // Create extension for <missing> stereotype that applies to Classifiers
            metaclass = (Class)this.umlModel.getOwnedType(UMLPackage.eINSTANCE.getClassifier().getName());
            if (!profile.getReferencedMetaclasses().contains(metaclass)) {
                profile.createMetaclassReference(metaclass);
            }
            stereotype.createExtension(metaclass, false);
            // Define Rose profile
            profile.define();
            // Apply Rose profile to model
            this.model.applyProfile(profile);
            // Add Rose profile to root after ModelAnnotation
            roots.add(0, profile);
        }
    }

    /**
     * @since 4.1
     */
    private Property createProperty( final String name,
                                     final RoseNode node ) {
        // Create UML Property.
        final Property prop = this.factory.createProperty();
        // Set Property's properties
        prop.setName(name);
        prop.setIsDerived(node.isDerived());
        prop.setIsReadOnly(!node.isChangeable());
        prop.setIsUnique(node.isUnique());
        // Set visibility
        final RoseNode visibility = node.findNodeWithKey(RoseStrings.EXPORTCONTROL);
        if (visibility != null) {
            prop.setVisibility(VisibilityKind.get(visibility.getValue()));
        }

        return prop;
    }

    /**
     * @since 4.1
     */
    private void createRoseExtensionProperty( final Stereotype stereotype,
                                              final String name,
                                              final String type ) {
        stereotype.createOwnedAttribute(name, PrimitiveTypeManager.INSTANCE.getPrimitiveType(type));
    }

    /**
     * @since 4.2
     */
    private Stereotype createStereotype( final String name ) {
        return this.stereotypeProfile.createOwnedStereotype(name, false);
    }

    /**
     * @since 4.2
     */
    private void createStereotypeProfile() {
        // Create stereotype profile if necessary and save in roots.
        if (this.stereotypeProfile == null) {
            this.stereotypeProfile = this.factory.createProfile();
            this.stereotypeProfile.setName(STEREOTYPE_PROFILE);
            // Add to roots after ModelAnnotation
            getRoots().add(1, this.stereotypeProfile);
        }
    }

    /**
     * Recursive.
     * 
     * @since 4.1
     */
    private Class findAssociationClass( final List elements,
                                        final StringTokenizer path ) {
        if (path.hasMoreTokens()) {
            String name = path.nextToken();
            for (final Iterator elemIter = elements.iterator(); elemIter.hasNext();) {
                final Object elem = elemIter.next();
                if (elem instanceof NamedElement) {
                    final NamedElement namedElem = (NamedElement)elem;
                    if (name.equals(namedElem.getName())) {
                        if (!path.hasMoreTokens()) {
                            return (Class)namedElem;
                        }
                        return findAssociationClass(namedElem.getOwnedElements(), path);
                    }
                }
            }
        }
        return null;
    }

    /**
     * @since 4.1
     */
    private Object getParent( final RoseNode node,
                              final String name,
                              final java.lang.Class type ) {
        RoseNode parentRoseNode = node.getParent();
        if (parentRoseNode == null) {
            return this.model;
        }
        Object parentUmlNode = parentRoseNode.getNode();
        if (parentUmlNode == null || !(parentUmlNode instanceof NamedElement)) {
            return getParent(parentRoseNode, name, type);
        }
        if (type.isInstance(parentUmlNode)) {
            return parentUmlNode;
        }
        addProblem(IStatus.WARNING, getString(INVALID_PARENT_MESSAGE_ID, name, getUnit()), node);
        return null;
    }

    /**
     * @since 4.2
     */
    private Stereotype getStereotype( final String name,
                                      final Profile profile ) {
        // Check if stereotype already exists in profile
        for (final Iterator iter = profile.getOwnedStereotypes().iterator(); iter.hasNext();) {
            final Stereotype stereotype = (Stereotype)iter.next();
            if (stereotype.getName().equals(name)) {
                return stereotype;
            }
        }
        return null;
    }

    /**
     * @since 4.1
     */
    private void removeAnnotations( final Element element ) {
        final ModelEditor editor = ModelerCore.getModelEditor();
        for (final Iterator iter = element.eAllContents(); iter.hasNext();) {
            final Object obj = iter.next();
            if (obj instanceof Element) {
                try {
                    // Move description to association class
                    final Annotation annotation = editor.getAnnotation((Element)obj, false);
                    if (annotation != null) {
                        annotation.getAnnotationContainer().getAnnotations().remove(annotation);
                    }
                } catch (final ModelerCoreException err) {
                    addProblem(IStatus.ERROR, err, element);
                }
            }
        }
    }

    /**
     * @since 4.1
     */
    private void resolveGeneralizationReference( final Classifier classifier,
                                                 final Classifier superClassifier ) {
        // Resolve generalization reference
        final Generalization generalization = this.factory.createGeneralization();
        generalization.setGeneral(superClassifier);
        generalization.setSpecific(classifier);
        classifier.getGeneralizations().add(generalization);
    }

    /**
     * @since 4.1
     */
    private void resolveOwnerReference( final Property property,
                                        final Classifier classifier,
                                        final String quid ) {
        if (classifier instanceof Class) {
            ((Class)classifier).getOwnedAttributes().add(property);
        } else if (classifier instanceof Interface) {
            ((Interface)classifier).getOwnedAttributes().add(property);
        } else { // Must be DataType
            ((DataType)classifier).getOwnedAttributes().add(property);
        }
        // Now that property has been added to parent, set Rose extension property values
        setRoseExtensionProperties(property, quid, property.getName());
    }

    /**
     * @since 4.1
     */
    private Element resolveReference( final String quid ) {
        if (quid == null) {
            return null;
        }
        Element elem = (Classifier)getIdMap().get(quid);
        if (elem != null) {
            return elem;
        }
        // Search all UML models in workspace for Element with matching quid, resetting open state of model after visiting
        for (final Iterator modelIter = getWorkspaceModels().iterator(); modelIter.hasNext();) {
            final ModelResource model = (ModelResource)modelIter.next();
            // Save whether model started out open.
            final boolean wasOpen = model.isOpen();
            try {
                try {
                    for (final Iterator objIter = model.getEObjects().iterator(); objIter.hasNext();) {
                        final Object obj = objIter.next();
                        if (obj instanceof Element) {
                            elem = (Element)obj;
                            final Stereotype stereotype = elem.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
                            if (stereotype != null && quid.equals(elem.getValue(stereotype, QUID))) {
                                return elem;
                            }
                        }
                    }
                } finally {
                    // Close model if it started out closed.
                    if (!wasOpen) {
                        model.close();
                    }
                }
            } catch (final ModelWorkspaceException err) {
                addProblem(IStatus.ERROR, err, model);
            }
        }
        return null;
    }

    /**
     * @since 4.2
     */
    private Type resolveTypeReference( final RoseNode node,
                                       final NamedElement elem,
                                       final String quid,
                                       String name ) {
        // Try to resolve reference by quid
        Type type = (Type)resolveReference(quid);
        if (type == null && name != null) {
            // Remove any array brackets
            if (name.endsWith(ARRAY)) {
                name = name.substring(0, name.length() - ARRAY.length());
            }
            // Try to resolve by name
            // Check if type name represents a UML primitive using i18n primitive name mappings
            final String primitiveName = getString(name.toLowerCase());
            if (primitiveName.startsWith(MISSING_VALUE_PREFIX)) {
                type = (Type)resolveReferenceByName(elem, TYPE, node, name, (List)getNameMap().get(name));
            } else {
                // Get primitive defined by UML metamodel
                type = PrimitiveTypeManager.INSTANCE.getPrimitiveType(primitiveName);
                if (type == null) {
                    addProblem(IStatus.ERROR,
                               getString(UNDEFINED_PRIMITIVE_MESSAGE_ID, primitiveName, elem.getQualifiedName()),
                               elem);
                }
            }
        }
        return type;
    }

    /**
     * @since 4.1
     */
    private void setAggregationKind( final RoseNode node,
                                     final RoseNode oppositeRoseNode,
                                     final Property property ) {
        if (node.isAggregate()) {
            final String containment = oppositeRoseNode.getContainment();
            AggregationKind kind;
            if (containment == null) {
                kind = AggregationKind.NONE_LITERAL;
            } else if (RoseStrings.BY_VALUE.equals(containment)) {
                kind = AggregationKind.COMPOSITE_LITERAL;
            } else {
                kind = AggregationKind.SHARED_LITERAL;
            }
            property.setAggregation(kind);
        }
    }

    /**
     * @since 4.1
     */
    private void setMultiplicity( final Property property,
                                  final String multiplicity ) {
        int lowerCardinality = DEFAULT_CARDINALITY;
        int upperCardinality = DEFAULT_CARDINALITY;
        if (multiplicity != null) {
            final StringTokenizer iter = new StringTokenizer(multiplicity, MULTIPLICITY_DELIMITER);
            if (iter.hasMoreTokens()) {
                final String token = iter.nextToken();
                try {
                    int cardinality = Integer.valueOf(token).intValue();
                    if (iter.hasMoreTokens()) {
                        // Cardinality is a lower cardinality
                        lowerCardinality = cardinality;
                        try {
                            upperCardinality = Integer.valueOf(iter.nextToken()).intValue();
                            // Upper cardinality must be greater than lower cardinality
                            if (upperCardinality < lowerCardinality) {
                                upperCardinality = lowerCardinality;
                            }
                        } catch (final NumberFormatException err) {
                            // Assume unbounded upper cardinality (e.g., "*" or "n")
                            upperCardinality = LiteralUnlimitedNatural.UNLIMITED;
                        }
                    } else if (cardinality > 0) { // Upper cardinality must be greater than lower cardinality
                        // Cardinality is both lower and upper cardinality
                        lowerCardinality = upperCardinality = cardinality;
                    }
                } catch (final NumberFormatException err) {
                    if (token.length() == 1) {
                        // Assume unbounded upper cardinality (e.g., "*" or "n") with zero lower cardinality
                        lowerCardinality = 0;
                        upperCardinality = LiteralUnlimitedNatural.UNLIMITED;
                    }
                }
            }
        }
        // Set lower bound of property's multiplicity
        final LiteralInteger lower = this.factory.createLiteralInteger();
        lower.setValue(lowerCardinality);
        property.setLowerValue(lower);
        // Set upper bound of property's multiplicity
        final LiteralUnlimitedNatural upper = this.factory.createLiteralUnlimitedNatural();
        upper.setValue(upperCardinality);
        property.setUpperValue(upper);
    }

    /**
     * @since 4.1
     */
    private void setRoseExtensionProperties( final Element element,
                                             final RoseNode node,
                                             final String name ) {
        setRoseExtensionProperties(element, node.getRoseId(), name);
    }

    /**
     * @since 4.1
     */
    private void setRoseExtensionProperties( final Element element,
                                             final String quid,
                                             final String name ) {
        final Stereotype stereotype = element.getAppliedStereotype(IConstants.QUALIFIED_ROSE_STEREOTYPE);
        element.setValue(stereotype, NAME_IN_SOURCE, name);
        element.setValue(stereotype, LOCKED, Boolean.FALSE);
        element.setValue(stereotype, QUID, quid == null ? EMPTY_STRING : quid);
        element.setValue(stereotype, SOURCE, getUnit().getResolvedPath());
    }
}
