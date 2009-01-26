/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.sdt.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDPackageImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.core.container.FakeContainer;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;

/**
 * TestBuiltInTypesManager
 */
public class TestBuiltInTypesManager extends TestCase {

    // private static final URI BUILTIN_DATATYPES_URI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
    // private static final String DATATYPES_MODEL_FILE_NAME = DatatypeConstants.DATATYPES_MODEL_FILE_NAME;

    // http://www.w3.org/2001/XMLSchema
    private static final String XSD_SCHEMA_URI_STRING = ModelerCore.XML_SCHEMA_GENERAL_URI;

    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------

    private static BuiltInTypesManager dtMgr;

    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------

    // =========================================================================
    // F R A M E W O R K
    // =========================================================================

    /**
     * Constructor for PdeTestBuiltInTypesManager.
     * 
     * @param name
     */
    public TestBuiltInTypesManager( String name ) {
        super(name);
    }

    // =========================================================================
    // T E S T C O N T R O L
    // =========================================================================

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestBuiltInTypesManager("testNothing")); //$NON-NLS-1$
        // suite.addTestSuite(TestBuiltInTypesManager.class);

        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    // =========================================================================
    // M A I N
    // =========================================================================

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    // =========================================================================
    // S E T U P A N D T E A R D O W N
    // =========================================================================

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public static void oneTimeSetUp() throws Exception {
        // Ensure that the metamodels are initialized
        XSDPackageImpl.init();

        // Ensure that the XSD global resources are initialized
        XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001);

        // Create a new BuiltInTypesManager instance ...
        dtMgr = new BuiltInTypesManager();
        Container cntr = new FakeContainer();
        cntr.start();
        // dtMgr.initialize(cntr);
    }

    public static void oneTimeTearDown() {
        dtMgr = null;
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    private static Resource helpGetEmfXsdResource() {
        final XSDSchema schema = XSDSchemaImpl.getSchemaForSchema(XSD_SCHEMA_URI_STRING);
        assertNotNull(schema);
        Resource resource = schema.eResource();
        assertNotNull(resource);
        return resource;
    }

    private static Resource helpGetMmXsdResource() {
        Resource resource = ModelerSdtPlugin.getBuiltInTypesResource();
        assertNotNull(resource);
        return resource;
    }

    private static void helpCheckType( final EObject eObject,
                                       final String expectedName ) {
        assertNotNull(eObject);
        assertTrue(eObject instanceof XSDSimpleTypeDefinition);
        final XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObject;

        assertNotNull("The eContainer may not be null for " + eObject, eObject.eContainer()); //$NON-NLS-1$
        assertNotNull("The basetype may not be null for " + type, type.getBaseTypeDefinition()); //$NON-NLS-1$

        final String name = type.getName();
        assertNotNull(name);
        if (expectedName != null) {
            assertEquals(expectedName, name);
        }
        assertEquals(true, dtMgr.isBuiltInDatatype(eObject));

        // If the data type is a MetaMatrix extended built-in type then it
        // should be contained within the MetaMatrix built-in datatypes resource
        if (DatatypeConstants.getMetaMatrixExtendedBuiltInTypeNames().contains(name)) {
            assertEquals("Resource checked failed for " + name, helpGetMmXsdResource(), type.eResource()); //$NON-NLS-1$
        } else if (DatatypeConstants.BuiltInNames.ANY_SIMPLE_TYPE.equals(name)
                   || DatatypeConstants.BuiltInNames.ANY_TYPE.equals(name)) {
            // do nothing - the ur-type exist in a different model
        }
        // Else the data type should be contained within the built-in
        // datatypes resource within the org.eclipse.xsd plugin
        else {
            assertEquals("Resource checked failed for " + name, helpGetEmfXsdResource(), type.eResource()); //$NON-NLS-1$
        }
    }

    private static void helpPrintSubtypesRecursive( final DatatypeManager mgr,
                                                    final EObject parent,
                                                    final String indent ) {
        try {
            assertNotNull(parent);
            printEObject(parent, indent);
            EObject[] eObjects = mgr.getSubtypes(parent);
            for (int i = 0; i < eObjects.length; i++) {
                helpPrintSubtypesRecursive(mgr, eObjects[i], indent + "  "); //$NON-NLS-1$
            }
        } catch (ModelerCoreException e) {
            e.printStackTrace();
        }
    }

    private static void printEObject( EObject eObj,
                                      String indent ) {
        if (eObj instanceof XSDSimpleTypeDefinition) {
            XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObj;
            System.out.println(indent + type.getName());
        }
    }

    private static void printEObject( EObject eObj ) {
        if (eObj instanceof XSDTypeDefinition) {
            XSDTypeDefinition type = (XSDTypeDefinition)eObj;
            System.out.println("  " + type.getName() + ", datatypeID = " + type.getURI()); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            System.out.println("  " + eObj); //$NON-NLS-1$
        }
    }

    private static void printEObjects( EObject[] eObjs ) {
        System.out.println("  EObject[].length = " + eObjs.length); //$NON-NLS-1$
        for (int i = 0; i < eObjs.length; i++) {
            printEObject(eObjs[i]);
        }
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testNothing() {
        // placeholder until I can get the ModelerSdtPlugin.getBuiltInTypesResource()
        // method to work in the nightly test environment. I do not have to comment
        // out all the test methods.
    }

    public void testGetBuiltInTypesResource() {
        XSDResourceImpl xsdResource = (XSDResourceImpl)ModelerSdtPlugin.getBuiltInTypesResource();
        assertNotNull(xsdResource);
        XSDSchema schema = xsdResource.getSchema();
        List contents = schema.eContents();
        assertNotNull(contents);
        assertEquals(52, contents.size());
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            assertTrue(iter.next() instanceof XSDSimpleTypeDefinition);
        }
    }

    public void testGetAnyType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetAnyType()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getAnyType();
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.ANY_TYPE);
        printEObject(eObject);
    }

    public void testGetAnySimpleType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetAnySimpleType()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getAnySimpleType();
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.ANY_SIMPLE_TYPE);
        printEObject(eObject);
    }

    public void testGetMetaMatrixExtendedTypesList() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetMetaMatrixExtendedTypesList()"); //$NON-NLS-1$
        List types = dtMgr.getMetaMatrixExtendedTypesList();
        assertEquals(8, types.size());
    }

    public void testGetAllDatatypes() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetAllDatatypes()"); //$NON-NLS-1$
        EObject[] eObjects = dtMgr.getAllDatatypes();
        assertNotNull(eObjects);
        assertEquals(53, eObjects.length);
        printEObjects(eObjects);
        // Make sure the results contain all the built-in types defined in the DatatypeConstants list
        List builtInTypeNames = new ArrayList(DatatypeConstants.getBuiltInTypeNames());
        for (int i = 0; i < eObjects.length; i++) {
            final XSDTypeDefinition type = (XSDTypeDefinition)eObjects[i];
            if (builtInTypeNames.contains(type.getName())) {
                builtInTypeNames.remove(type.getName());
            }
        }
        // The only two types left in the list should be "null" and "anyType"
        assertEquals(2, builtInTypeNames.size());
    }

    public void testGetBuiltInDatatypeByName() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByName()"); //$NON-NLS-1$
        // Look up the datatype by name
        String id = "string"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
        printEObject(eObject);

        // Make sure the lookup is case-insensitive
        id = "stRiNg"; //$NON-NLS-1$
        eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
    }

    public void testGetBuiltInDatatypeByAllNames() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByAllNames()"); //$NON-NLS-1$
        final Collection builtInNames = DatatypeConstants.getBuiltInTypeNames();
        final List unresolvedTypeNames = new ArrayList(3);
        for (Iterator iter = builtInNames.iterator(); iter.hasNext();) {
            String name = (String)iter.next();
            EObject eObject = dtMgr.getBuiltInDatatype(name);
            if (eObject == null) {
                unresolvedTypeNames.add(name);
            } else {
                helpCheckType(eObject, name);
            }
        }

        assertEquals(1, unresolvedTypeNames.size());
        assertEquals("null", unresolvedTypeNames.get(0)); //$NON-NLS-1$
    }

    public void testGetBuiltInDatatypeByUUID() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByUUID()"); //$NON-NLS-1$
        // Look up the datatype by name
        String id = "mmuuid:bf6c34c0-c442-1e24-9b01-c8207cd53eb7"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
        printEObject(eObject);
    }

    public void testGetBuiltInDatatypeByURI() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByURI()"); //$NON-NLS-1$
        String id = "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance#sTrinG"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
        printEObject(eObject);

        id = "http://www.w3.org/2001/XMLSchema#StriNg"; //$NON-NLS-1$
        eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
    }

    public void testGetBuiltInDatatypeByURI2() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByURI2()"); //$NON-NLS-1$
        String id = "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance#cloB"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.CLOB);
        printEObject(eObject);

        id = "http://www.w3.org/2001/XMLSchema#cLob"; //$NON-NLS-1$
        eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.CLOB);
    }

    public void testGetBuiltInDatatypeByNonExistentName() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByNonExistentName()"); //$NON-NLS-1$
        // Look up the datatype by name
        String id = "spring"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        assertNull(eObject);
    }

    public void testGetBuiltInDatatypeByNonExistentUUID() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByNonExistentUUID()"); //$NON-NLS-1$
        // Look up the datatype by name
        String id = "mmuuid:abcc34c0-c442-1e24-9b01-c8207cd53eb7"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        assertNull(eObject);
    }

    public void testGetBuiltInDatatypeByNonExistentURI() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInDatatypeByNonExistentURI()"); //$NON-NLS-1$
        String id = "http://www.w3.org/2020/XMLSchemaOfSchema#StriNg"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        assertNull(eObject);
    }

    public void testIsBuiltInDatatype() {
        System.out.println("\nPdeTestBuiltInTypesManager.testIsBuiltInDatatype()"); //$NON-NLS-1$
        String id = "int"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.INT);
        printEObject(eObject);
        assertEquals(true, dtMgr.isBuiltInDatatype(eObject));
    }

    public void testIsBuiltInDatatypeForNonExistentType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testIsBuiltInDatatypeForNonExistentType()"); //$NON-NLS-1$
        String id = "nonExistentType"; //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        assertNull(eObject);
        assertEquals(false, dtMgr.isBuiltInDatatype(eObject));
    }

    public void testGetBasetype() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBasetype()"); //$NON-NLS-1$
        String id = "http://www.w3.org/2001/XMLSchema#long"; //$NON-NLS-1$
        EObject eObject = dtMgr.findDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.LONG);
        printEObject(eObject);
        eObject = dtMgr.getBaseType(eObject);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.INTEGER);
        printEObject(eObject);
    }

    public void testGetRuntimeTypeNameById() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetRuntimeTypeNameById()"); //$NON-NLS-1$
        String id = "http://www.w3.org/2001/XMLSchema#long"; //$NON-NLS-1$
        String runtimeType = dtMgr.getRuntimeTypeName(id);
        assertNotNull(runtimeType);
        assertEquals("long", runtimeType); //$NON-NLS-1$
        // Test the other form of getRuntimeTypeName
        EObject eObject = dtMgr.getBuiltInDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.LONG);
        assertEquals(runtimeType, dtMgr.getRuntimeTypeName(eObject));
    }

    public void testGetRuntimeTypeNameById2() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetRuntimeTypeNameById2()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype("long"); //$NON-NLS-1$
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.LONG);
        String runtimeType = dtMgr.getRuntimeTypeName(eObject);
        assertNotNull(runtimeType);
        assertEquals("long", runtimeType); //$NON-NLS-1$
    }

    public void testGetSubtypesForNCName() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetSubtypesForNCName()"); //$NON-NLS-1$        String filepath = UnitTestUtil.Data.getTestDataPath() + File.separator + TEST_ZIP_FILE_NAME;
        String id = "http://www.w3.org/2001/XMLSchema#NCName"; //$NON-NLS-1$
        EObject eObject = dtMgr.findDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.NCNAME);
        printEObject(eObject);
        EObject[] eObjects = dtMgr.getSubtypes(eObject);
        assertNotNull(eObjects);
        assertEquals(3, eObjects.length);
        for (int i = 0; i < eObjects.length; i++) {
            String name = ((XSDSimpleTypeDefinition)eObjects[i]).getName();
            if (!name.equals("ID") && !name.equals("IDREF") && !name.equals("ENTITY")) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                fail("The entity " + name + " is not a correct subtype of NCName"); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        printEObjects(eObjects);
    }

    public void testGetSubtypesForInt() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetSubtypesForInt()"); //$NON-NLS-1$
        String id = "http://www.w3.org/2001/XMLSchema#int"; //$NON-NLS-1$
        EObject eObject = dtMgr.findDatatype(id);
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.INT);
        printEObject(eObject);
        EObject[] eObjects = dtMgr.getSubtypes(eObject);
        assertNotNull(eObjects);
        assertEquals(1, eObjects.length);
        helpCheckType(eObjects[0], DatatypeConstants.BuiltInNames.SHORT);
        printEObjects(eObjects);
    }

    public void testGetSubtypesForAnyType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetSubtypesForAnyType()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getAnyType();
        assertNotNull(eObject);
        printEObject(eObject);
        EObject[] eObjects = dtMgr.getSubtypes(eObject);
        assertNotNull(eObjects);
        printEObjects(eObjects);
        assertEquals(1, eObjects.length);
    }

    public void testGetSubtypesForAnySimpleType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetSubtypesForAnySimpleType()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getAnySimpleType();
        assertNotNull(eObject);
        printEObject(eObject);
        EObject[] eObjects = dtMgr.getSubtypes(eObject);
        assertNotNull(eObjects);
        printEObjects(eObjects);
        assertEquals(19, eObjects.length);
    }

    public void testGetBuiltInPrimitiveTypes() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInPrimitiveTypes()"); //$NON-NLS-1$
        EObject[] eObjects = dtMgr.getBuiltInPrimitiveTypes();
        assertNotNull(eObjects);
        assertEquals(19, eObjects.length);
        printEObjects(eObjects);
    }

    public void testGetBuiltInPrimitiveType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInPrimitiveType()"); //$NON-NLS-1$
        // Check the primitive type returned for unsignedShort
        String id = "unsignedShort"; //$NON-NLS-1$
        EObject dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.UNSIGNED_SHORT);
        EObject primType = dtMgr.getBuiltInPrimitiveType(dataType);
        helpCheckType(primType, DatatypeConstants.BuiltInNames.DECIMAL);
        primType = dtMgr.getBuiltInPrimitiveType(primType);
        helpCheckType(primType, DatatypeConstants.BuiltInNames.DECIMAL);

        // Check the primitive type returned for token
        id = "token"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.TOKEN);
        primType = dtMgr.getBuiltInPrimitiveType(dataType);
        helpCheckType(primType, DatatypeConstants.BuiltInNames.STRING);
        primType = dtMgr.getBuiltInPrimitiveType(primType);
        helpCheckType(primType, DatatypeConstants.BuiltInNames.STRING);

        // Check the primitive type returned for token
        id = "IDREFS"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.IDREFS);
        primType = dtMgr.getBuiltInPrimitiveType(dataType);
        helpCheckType(primType, DatatypeConstants.BuiltInNames.STRING);

        // Check the primitive type returned for token
        id = "object"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.OBJECT);
        primType = dtMgr.getBuiltInPrimitiveType(dataType);
        helpCheckType(primType, DatatypeConstants.BuiltInNames.BASE64_BINARY);
    }

    public void testIsNumeric() {
        System.out.println("\nPdeTestBuiltInTypesManager.testIsNumeric()"); //$NON-NLS-1$
        // Check the primitive type returned for unsignedShort
        String id = "unsignedShort"; //$NON-NLS-1$
        EObject dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.UNSIGNED_SHORT);
        assertEquals(true, dtMgr.isNumeric(dataType));

        // Check the primitive type returned for token
        id = "token"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.TOKEN);
        assertEquals(false, dtMgr.isNumeric(dataType));

        // Check the primitive type returned for token
        id = "object"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.OBJECT);
        assertEquals(false, dtMgr.isNumeric(dataType));
    }

    public void testIsCharacter() {
        System.out.println("\nPdeTestBuiltInTypesManager.testIsCharacter()"); //$NON-NLS-1$
        // Check the primitive type returned for unsignedShort
        String id = "unsignedShort"; //$NON-NLS-1$
        EObject dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.UNSIGNED_SHORT);
        assertEquals(false, dtMgr.isCharacter(dataType));

        // Check the primitive type returned for token
        id = "token"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.TOKEN);
        assertEquals(true, dtMgr.isCharacter(dataType));

        // Check the primitive type returned for token
        id = "object"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.OBJECT);
        assertEquals(false, dtMgr.isCharacter(dataType));
    }

    public void testIsBinary() {
        System.out.println("\nPdeTestBuiltInTypesManager.testIsBinary()"); //$NON-NLS-1$
        // Check the primitive type returned for unsignedShort
        String id = "unsignedShort"; //$NON-NLS-1$
        EObject dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.UNSIGNED_SHORT);
        assertEquals(false, dtMgr.isBinary(dataType));

        // Check the primitive type returned for token
        id = "token"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.TOKEN);
        assertEquals(false, dtMgr.isBinary(dataType));

        // Check the primitive type returned for object
        id = "object"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.OBJECT);
        assertEquals(true, dtMgr.isBinary(dataType));

        // Check the primitive type returned for clob
        id = "blob"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.BLOB);
        assertEquals(true, dtMgr.isBinary(dataType));

        // Check the primitive type returned for clob
        id = "clob"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.CLOB);
        assertEquals(true, dtMgr.isBinary(dataType));
    }

    public void testGetBuiltInPrimitiveTypeRuntimeTypes() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetBuiltInPrimitiveTypeRuntimeTypes()"); //$NON-NLS-1$
        EObject[] eObjects = dtMgr.getBuiltInPrimitiveTypes();
        assertNotNull(eObjects);
        assertEquals(19, eObjects.length);
        for (int i = 0; i != eObjects.length; ++i) {
            String runtimeType = dtMgr.getRuntimeTypeName(eObjects[i]);
            assertNotNull(runtimeType);
            assertTrue(runtimeType.length() > 0);
        }
    }

    public void testGetAllDatatypeRuntimeTypes() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetAllDatatypeRuntimeTypes()"); //$NON-NLS-1$
        EObject[] eObjects = dtMgr.getAllDatatypes();
        assertNotNull(eObjects);
        for (int i = 0; i != eObjects.length; ++i) {
            final XSDTypeDefinition type = (XSDTypeDefinition)eObjects[i];
            if (DatatypeConstants.getBuiltInTypeNames().contains(type.getName())) {
                String runtimeType = dtMgr.getRuntimeTypeName(type);
                assertNotNull("Error null runtime type for " + type.getName(), runtimeType); //$NON-NLS-1$
                assertTrue("Error zero-length runtime type for " + type.getName(), runtimeType.length() > 0); //$NON-NLS-1$
            }
        }
    }

    public void testGetDatatypeHierarchyForAnySimpleType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetDatatypeHierarchyForAnySimpleType()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getAnySimpleType();
        helpPrintSubtypesRecursive(dtMgr, eObject, "  "); //$NON-NLS-1$
    }

    public void testGetDatatypeHierarchyForAnyType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetDatatypeHierarchyForAnyType()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getAnyType();
        helpPrintSubtypesRecursive(dtMgr, eObject, "  "); //$NON-NLS-1$
    }

    public void testGetDefaultDatatypeForRuntimeTypeName() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetDefaultDatatypeForRuntimeTypeName()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getDefaultDatatypeForRuntimeTypeName("integer"); //$NON-NLS-1$
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.INT);
        printEObject(eObject);
    }

    public void testGetUuidString() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetUuidString()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype("string"); //$NON-NLS-1$
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
        final String uuid = dtMgr.getUuidString(eObject);
        System.out.println(uuid);
        assertNotNull(uuid);
        assertEquals(uuid, "mmuuid:bf6c34c0-c442-1e24-9b01-c8207cd53eb7"); //$NON-NLS-1$
    }

    public void testGetUuid() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetUuid()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype("string"); //$NON-NLS-1$
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.STRING);
        final Object uuid = dtMgr.getUuid(eObject);
        System.out.println(uuid);
        assertNotNull(uuid);
        assertEquals("mmuuid:bf6c34c0-c442-1e24-9b01-c8207cd53eb7", uuid.toString()); //$NON-NLS-1$
    }

    public void testGetExtensionMap() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetExtensionMap()"); //$NON-NLS-1$
        EObject eObject = dtMgr.getBuiltInDatatype("integer"); //$NON-NLS-1$
        helpCheckType(eObject, DatatypeConstants.BuiltInNames.INTEGER);
        Map extensionMap = dtMgr.getEnterpriseExtensionsMap(eObject);
        System.out.println(extensionMap);
        assertNotNull(extensionMap);
        assertEquals(3, extensionMap.size());
    }

    public void testGetRuntimeTypeJavaClassName() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetRuntimeTypeJavaClassName()"); //$NON-NLS-1$
        String className = dtMgr.getRuntimeTypeJavaClassName("string"); //$NON-NLS-1$
        System.out.println(className);
        assertNotNull(className);
        assertEquals("java.lang.String", className); //$NON-NLS-1$
    }

    public void testGetMetaMatrixExtendedBuiltInBaseType() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetMetaMatrixExtendedBuiltInBaseType()"); //$NON-NLS-1$
        EObject dataType = dtMgr.getAnySimpleType();
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.ANY_SIMPLE_TYPE);
        assertEquals(null, dtMgr.getMetaMatrixExtendedBuiltInBaseType(dataType));

        String id = "unsignedShort"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.UNSIGNED_SHORT);
        assertEquals(null, dtMgr.getMetaMatrixExtendedBuiltInBaseType(dataType));

        id = "object"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.OBJECT);
        assertEquals(dataType, dtMgr.getMetaMatrixExtendedBuiltInBaseType(dataType));

        id = "timestamp"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.TIMESTAMP);
        assertEquals(dataType, dtMgr.getMetaMatrixExtendedBuiltInBaseType(dataType));

        id = "char"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.CHAR);
        assertEquals(dataType, dtMgr.getMetaMatrixExtendedBuiltInBaseType(dataType));
    }

    public void testGetTypeHierarchy() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetTypeHierarchy()"); //$NON-NLS-1$
        String id = "short"; //$NON-NLS-1$
        EObject dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.SHORT);
        EObject[] eObjects = dtMgr.getTypeHierarchy(dataType);
        printEObjects(eObjects);
        assertEquals(6, eObjects.length);
        assertEquals(dataType, eObjects[0]);
        assertEquals(dtMgr.getAnySimpleType(), eObjects[5]);

        id = "gMonth"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.GMONTH);
        eObjects = dtMgr.getTypeHierarchy(dataType);
        printEObjects(eObjects);
        assertEquals(2, eObjects.length);
        assertEquals(dataType, eObjects[0]);
        assertEquals(dtMgr.getAnySimpleType(), eObjects[1]);

        id = "anySimpleType"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.ANY_SIMPLE_TYPE);
        eObjects = dtMgr.getTypeHierarchy(dataType);
        printEObjects(eObjects);
        assertEquals(1, eObjects.length);
        assertEquals(dataType, eObjects[0]);
    }

    public void testGetAllDescriptions() {
        System.out.println("\nPdeTestBuiltInTypesManager.testGetAllDescriptions()"); //$NON-NLS-1$
        EObject[] eObjects = dtMgr.getAllDatatypes();
        assertNotNull(eObjects);
        for (int i = 0; i != eObjects.length; ++i) {
            final XSDTypeDefinition type = (XSDTypeDefinition)eObjects[i];
            if (DatatypeConstants.getBuiltInTypeNames().contains(type.getName())) {
                String description = dtMgr.getDescription(type);
                System.out.println("\n" + type.getName() + "\n" + description); //$NON-NLS-1$ //$NON-NLS-2$
                assertNotNull(description);
            }
        }

        String id = "short"; //$NON-NLS-1$
        EObject dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.SHORT);
        String description = dtMgr.getDescription(dataType);
        assertTrue(description.trim().length() > 0);

        id = "gMonthDay"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.GMONTH_DAY);
        description = dtMgr.getDescription(dataType);
        assertTrue(description.trim().length() > 0);

        id = "biginteger"; //$NON-NLS-1$
        dataType = dtMgr.getBuiltInDatatype(id);
        helpCheckType(dataType, DatatypeConstants.BuiltInNames.BIG_INTEGER);
        description = dtMgr.getDescription(dataType);
        assertTrue(description.trim().length() > 0);
    }
}
