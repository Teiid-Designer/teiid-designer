/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.impl.XSDFactoryImpl;
import com.metamatrix.core.selection.TreeSelection;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.metamodels.relational.impl.RelationalFactoryImpl;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.impl.RelationshipFactoryImpl;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.modelgenerator.processor.RelationTracker;
import com.metamatrix.modeler.modelgenerator.uml2.processor.RelationTrackerImpl;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;
import com.metamatrix.modeler.modelgenerator.util.AnnotationHelper;
import com.metamatrix.modeler.modelgenerator.util.AnnotationHelperImpl;
import com.metamatrix.modeler.modelgenerator.util.EObjectUtil;
import com.metamatrix.modeler.modelgenerator.util.FakeDatatypeFinder;
import com.metamatrix.modeler.modelgenerator.util.FakeEObjectUtil;
import com.metamatrix.modeler.modelgenerator.util.FakeTreeSelection;
import com.metamatrix.modeler.modelgenerator.util.NullSimpleDatatypeUtil;
import com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil;

/**
 * Uml2RelationalUtilTest
 */
public class TestRelationalObjectGenerator extends TestCase {

    private static final UMLFactory UML2_FACTORY = UMLFactory.eINSTANCE;
    private static final XSDFactory XSD_FACTORY = new XSDFactoryImpl();

    private static final RelationTracker TRACKER;
    private static final FakeDatatypeFinder FINDER = new FakeDatatypeFinder();
    private static final Uml2RelationalOptions NULL_OPTIONS = new Uml2RelationalOptions();
    private static final EObjectUtil OBJECT_UTIL = new FakeEObjectUtil();
    private static final RelationalObjectNamingStrategy NULL_NAMING_STRATEGY = new NullRelationalObjectNamingStrategy();
    private static final TreeSelection TREE_SELECTION = new FakeTreeSelection();
    private static final SimpleDatatypeUtil DATATYPE_UTIL = new NullSimpleDatatypeUtil();

    private static final TransientModelSelector SELECTOR = new TransientModelSelector("Fake URI"); //$NON-NLS-1$

    private static final EObject SIMPLEDATATYPE = XSD_FACTORY.createXSDSimpleTypeDefinition();

    private static final DataType DATATYPE = UML2_FACTORY.createPrimitiveType();

    private static final String DATATYPE_NAME = "TestDatatype"; //$NON-NLS-1$

    private static final List STEREOTYPES = new ArrayList();

    static {
        RelationshipMetamodelPlugin rmPlugin = new RelationshipMetamodelPlugin();
        ((PluginUtilImpl)RelationshipMetamodelPlugin.Util).initializePlatformLogger(rmPlugin);
        Uml2Plugin uml2Plugin = new Uml2Plugin();
        ((PluginUtilImpl)Uml2Plugin.Util).initializePlatformLogger(uml2Plugin);

        SELECTOR.open();
        DATATYPE.setName(DATATYPE_NAME);
        FINDER.addMapping(DATATYPE_NAME, SIMPLEDATATYPE);

        TRACKER = new RelationTrackerImpl(SELECTOR, new RelationshipFactoryImpl());

        Stereotype testType1 = UML2_FACTORY.createStereotype();
        testType1.setName("PrimaryKey, otherKey"); //$NON-NLS-1$
        Stereotype testType2 = UML2_FACTORY.createStereotype();
        testType2.setName("ABC"); //$NON-NLS-1$
        Stereotype testType3 = UML2_FACTORY.createStereotype();
        testType3.setName("key"); //$NON-NLS-1$
        Stereotype testType4 = UML2_FACTORY.createStereotype();
        testType4.setName("primarykey"); //$NON-NLS-1$

        STEREOTYPES.add(testType1);
        STEREOTYPES.add(testType2);
        STEREOTYPES.add(testType3);
        STEREOTYPES.add(testType4);
        STEREOTYPES.add(null);
    }

    private static AnnotationHelper ANNOTATION_HELPER = new AnnotationHelperImpl(SELECTOR);

    private static final RelationalObjectGeneratorImpl GENERATOR = new RelationalObjectGeneratorImpl(TRACKER, FINDER,
                                                                                                     NULL_OPTIONS,
                                                                                                     NULL_NAMING_STRATEGY,
                                                                                                     OBJECT_UTIL,
                                                                                                     new RelationalFactoryImpl(),
                                                                                                     TREE_SELECTION,
                                                                                                     DATATYPE_UTIL,
                                                                                                     ANNOTATION_HELPER);

    /**
     * Constructor for Uml2RelationalUtilTest.
     * 
     * @param name
     */
    public TestRelationalObjectGenerator( String name ) {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     * @since 4.3
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ModelerCore.testLoadModelContainer();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     * @since 4.3
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProcessFeaturesForClass() {
        Class klass = getClassWMultStarProperties();

        List output = GENERATOR.createBaseTablesForClass(klass, new LinkedList(), new HashSet());
        if (output.size() != 2) {
            fail();
        }
    }

    public void testCreateBaseTablesForClassWUnidirectionalAssociationClassType() {

        Class fromClass = getClassWOProperties();
        Class toClass = getClassWProperties();
        Property ownedEnd = UML2_FACTORY.createProperty();
        ownedEnd.setType(toClass);
        fromClass.getOwnedAttributes().add(ownedEnd);
        addUniDirectionalAssociationPropertyToClass(fromClass, ownedEnd, toClass);

        List output = GENERATOR.createBaseTablesForClass(fromClass, Collections.EMPTY_LIST, Collections.EMPTY_SET);

        if (output.size() != 2) {
            fail();
        }
    }

    public void testCreateBaseTablesForClassWCircularTypeDependency() {

        Class klass1 = UML2_FACTORY.createClass();
        Class klass2 = UML2_FACTORY.createClass();

        Property property1 = UML2_FACTORY.createProperty();
        Property property2 = UML2_FACTORY.createProperty();

        property1.setType(klass2);
        property2.setType(klass1);

        klass1.getOwnedAttributes().add(property1);
        klass2.getOwnedAttributes().add(property2);

        List output = GENERATOR.createBaseTablesForClass(klass1, Collections.EMPTY_LIST, Collections.EMPTY_SET);

        if (output.size() != 2) {
            fail();
        }
    }

    public void testGetSimpleDatatypeForDataType() {

        EObject type = GENERATOR.getSimpleDatatypeForDataType(DATATYPE, new LinkedList());
        if (type != SIMPLEDATATYPE) {
            fail();
        }

    }

    public void testGetSimpleDatatypeForDataTypeWSuperType() {

        DataType subType = UML2_FACTORY.createPrimitiveType();

        Generalization generalization = UML2_FACTORY.createGeneralization();

        generalization.setGeneral(DATATYPE);
        generalization.setSpecific(subType);

        EObject type = GENERATOR.getSimpleDatatypeForDataType(subType, new LinkedList());
        if (type != SIMPLEDATATYPE) {
            fail();
        }
    }

    public void testGetSimpleDatatypeForDataTypeNullConditions() {
        try {
            GENERATOR.getSimpleDatatypeForDataType(null, null);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }

        try {
            GENERATOR.getSimpleDatatypeForDataType(DATATYPE, null);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }

        try {
            GENERATOR.getSimpleDatatypeForDataType(null, new LinkedList());
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }
    }

    public void testCheckStereotypesAgainstStringsPositive() {

        List stringsList = new LinkedList();
        stringsList.add("key"); //$NON-NLS-1$
        stringsList.add("this"); //$NON-NLS-1$
        stringsList.add(null);

        if (!GENERATOR.checkStereotypesAgainstStrings(STEREOTYPES, stringsList)) {
            fail();
        }

    }

    public void testCheckStereotypesAgainstStringsForNullConditions() {

        GENERATOR.checkStereotypesAgainstStrings(null, null);
    }

    public void testCheckStereotypesAgainstEmptyLists() {
        GENERATOR.checkStereotypesAgainstStrings(new ArrayList(), new LinkedList());
    }

    public void testCheckStereotypesAgainstStringsNegative() {
        List stringsList = new LinkedList();

        if (GENERATOR.checkStereotypesAgainstStrings(STEREOTYPES, stringsList)) {
            fail();
        }
    }

    public void testGetMembersToProcessForClassifierNestedClassCase() {

        Class klass = getClassWNestedClassWProperties();
        Set members = GENERATOR.getMembersToProcessForClassifier(klass);

        if (members.size() != 2) {
            System.out.println(members.size());
            fail();
        }

    }

    public void testGetMembersToProcessForClassifierInheritedMembersCase() {

        Class subClass = getSubClassWithInheritance();
        Set members = GENERATOR.getMembersToProcessForClassifier(subClass);

        if (members.size() != 2) {
            System.out.println(members.size());
            fail();
        }

        Class superClass = getSuperClassWithInheritance();
        members = GENERATOR.getMembersToProcessForClassifier(superClass);

        if (members.size() != 1) {
            fail();
        }

    }

    public void testGetMembersToProcessForClassifierNullConditions() {
        Exception e = null;
        try {
            GENERATOR.getMembersToProcessForClassifier(null);
        } catch (IllegalArgumentException e1) {
            e = e1;
        }
        if (e != null) {
            fail();
        }
    }

    public void testGetMembersToProcessForClassifierNoMemberCondition() {
        Class klass = UML2_FACTORY.createClass();

        Set members = GENERATOR.getMembersToProcessForClassifier(klass);

        if (members.size() != 0) {
            fail();
        }
    }

    public void testHasOwnedInheritedOrNestedPropertiesForClassWProperties() {

        Class klass = getClassWMultStarProperties();

        if (!GENERATOR.hasOwnedInheritedOrNestedProperties(klass)) {
            fail();
        }
    }

    public void testHasOwnedInheritedOrNestedPropertiesForClassWOProperties() {
        Class klass = getClassWOProperties();
        if (GENERATOR.hasOwnedInheritedOrNestedProperties(klass)) {
            fail();
        }
    }

    public void testHasOwnedInheritedOrNestedPropertiesForNullConditions() {
        Exception e = null;

        try {
            GENERATOR.hasOwnedInheritedOrNestedProperties(null);
        } catch (IllegalArgumentException e1) {
            e = e1;
        }

        if (e != null) {
            fail();
        }

    }

    public void testRemoveRedefinedPropertiesMultiLevelRedefinitions() {

        Property prop1 = UML2_FACTORY.createProperty();
        Property prop2 = UML2_FACTORY.createProperty();
        Property prop3 = UML2_FACTORY.createProperty();
        Property prop4 = UML2_FACTORY.createProperty();
        Property prop5 = UML2_FACTORY.createProperty();
        Property prop6 = UML2_FACTORY.createProperty();
        Property prop7 = UML2_FACTORY.createProperty();
        Property prop8 = UML2_FACTORY.createProperty();

        LinkedList owned = new LinkedList();
        LinkedList inherited = new LinkedList();

        owned.add(prop6);
        owned.add(prop7);
        owned.add(prop8);

        inherited.add(prop1);
        inherited.add(prop2);
        inherited.add(prop3);
        inherited.add(prop4);
        inherited.add(prop5);

        prop6.getRedefinedProperties().add(prop4);
        prop4.getRedefinedProperties().add(prop2);

        GENERATOR.removeRedefinedProperties(owned, inherited);

        if (inherited.contains(prop2) || inherited.contains(prop4) || !owned.contains(prop6)) {
            fail();
        }

    }

    /*
     *
     * #################################################################################################
     * #################################################################################################
     * #################################################################################################
     *
     *                              Test Helper Methods
     *
     * #################################################################################################
     * #################################################################################################
     */

    private Class getClassWProperties() {
        Class klass = UML2_FACTORY.createClass();
        Property property1 = UML2_FACTORY.createProperty();

        klass.getOwnedAttributes().add(property1);
        return klass;
    }

    private Class getClassWOProperties() {

        Class klass = UML2_FACTORY.createClass();
        return klass;

    }

    private Class getClassWMultStarProperties() {
        Class klass = UML2_FACTORY.createClass();

        addStringTypePropertyWMultStar(klass);
        addStringTypePropertyWMult1(klass);

        return klass;

    }

    private Class getSubClassWithInheritance() {
        Class superClass = getClassWProperties();

        Class subClass = getClassWProperties();

        Generalization generalization = UML2_FACTORY.createGeneralization();
        generalization.setGeneral(superClass);
        generalization.setSpecific(subClass);
        return subClass;

    }

    private Class getSuperClassWithInheritance() {

        Class subClass = getSubClassWithInheritance();
        List generalizations = subClass.getGeneralizations();
        Generalization generalization = (Generalization)generalizations.get(0);
        return (Class)generalization.getGeneral();
    }

    private Class getClassWNestedClassWProperties() {
        Class klass = getClassWProperties();

        Class nestedClass = getClassWProperties();

        klass.getNestedClassifiers().add(nestedClass);
        return klass;
    }

    private DataType getStringDataTypeInstance() {
        DataType type = UML2_FACTORY.createPrimitiveType();
        type.setName("String"); //$NON-NLS-1$
        return type;

    }

    private void addStringTypePropertyWMultStar( Class klass ) {
        Property propertyMultStar = UML2_FACTORY.createProperty();
        LiteralUnlimitedNatural ldUpper = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();

        ldUpper.setValue(LiteralUnlimitedNatural.UNLIMITED);

        propertyMultStar.setUpperValue(ldUpper);

        klass.getOwnedAttributes().add(propertyMultStar);

        DataType type = getStringDataTypeInstance();
        propertyMultStar.setType(type);

    }

    private void addStringTypePropertyWMult1( Class klass ) {

        Property propertyMult1 = UML2_FACTORY.createProperty();

        propertyMult1.setType(getStringDataTypeInstance());

        klass.getOwnedAttributes().add(propertyMult1);
    }

    private void addUniDirectionalAssociationPropertyToClass( Class fromClass,
                                                              Property ownedProperty,
                                                              Class toClass ) {
        Association association = UML2_FACTORY.createAssociation();
        Property toEnd = UML2_FACTORY.createProperty();
        toEnd.setType(fromClass);
        association.getMemberEnds().add(ownedProperty);
        association.getOwnedEnds().add(toEnd);

    }
}
