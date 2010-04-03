/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.execution;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @since 4.3
 */
public class TestVdbExecutionValidator extends TestCase {

    private static String PHYSICAL_MODEL_NAME1 = "PhysicalModel1"; //$NON-NLS-1$
    private static String PHYSICAL_MODEL_NAME2 = "PhysicalModel2"; //$NON-NLS-1$
    private static String PHYSICAL_MODEL_NAME3 = "PhysicalModel3"; //$NON-NLS-1$

    /**
     * Constructor for TestVdbExecutionValidator.
     * 
     * @param name
     */
    public TestVdbExecutionValidator( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestVdbExecutionValidator"); //$NON-NLS-1$
        suite.addTestSuite(TestVdbExecutionValidator.class);
        //        suite.addTest(new TestVdbExecutionValidator("testPathExist"));//$NON-NLS-1$

        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    // public VirtualDatabase helpGetVirtualDatabaseWithErrors() {
    // ManifestFactory factory = ManifestFactory.eINSTANCE;
    // VirtualDatabaseImpl database = (VirtualDatabaseImpl)factory.createVirtualDatabase();
    // database.setSeverity(Severity.ERROR_LITERAL);
    // Collection markers = database.getMarkers();
    // ProblemMarker marker = factory.createProblemMarker();
    // marker.setSeverity(Severity.ERROR_LITERAL);
    // markers.add(marker);
    // return database;
    // }
    //
    // public VirtualDatabase helpGetVirtualDatabaseWithNoModels() {
    // ManifestFactory factory = ManifestFactory.eINSTANCE;
    // VirtualDatabaseImpl database = (VirtualDatabaseImpl)factory.createVirtualDatabase();
    // return database;
    // }
    //
    // public VirtualDatabase helpGetVirtualDatabaseWithPhysicalModels() {
    // ManifestFactory factory = ManifestFactory.eINSTANCE;
    // VirtualDatabaseImpl database = (VirtualDatabaseImpl)factory.createVirtualDatabase();
    // ModelReference reference1 = factory.createModelReference();
    // reference1.setModelType(ModelType.PHYSICAL_LITERAL);
    // reference1.setName(PHYSICAL_MODEL_NAME1);
    // database.getModels().add(reference1);
    // ModelReference reference2 = factory.createModelReference();
    // reference2.setModelType(ModelType.PHYSICAL_LITERAL);
    // reference2.setName(PHYSICAL_MODEL_NAME2);
    // database.getModels().add(reference2);
    // ModelReference reference3 = factory.createModelReference();
    // reference3.setModelType(ModelType.PHYSICAL_LITERAL);
    // reference3.setName(PHYSICAL_MODEL_NAME3);
    // database.getModels().add(reference3);
    // return database;
    // }

    public Object helpGetVdbDefnWithConnectorBindingss() {
        return null;
    }

    public Object helpGetVdbDefnWithMissingConnectorBindings() {
        return null;
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    // public void testVdbWithValidationErrors() {
    // VirtualDatabase database = helpGetVirtualDatabaseWithErrors();
    // VdbExecutionValidatorImpl validator = new VdbExecutionValidatorImpl();
    // IStatus status = validator.validateVirtualDatabase(database);
    // assertEquals(IStatus.ERROR, status.getSeverity());
    // }
    //
    // public void testVdbWithValidationNoErrors() {
    // VirtualDatabase database = helpGetVirtualDatabaseWithPhysicalModels();
    // VdbExecutionValidatorImpl validator = new VdbExecutionValidatorImpl();
    // IStatus status = validator.validateVirtualDatabase(database);
    // assertEquals(IStatus.OK, status.getSeverity());
    // }
    //
    // public void testVdbWithNoModels() {
    // VirtualDatabase database = helpGetVirtualDatabaseWithNoModels();
    // VdbExecutionValidatorImpl validator = new VdbExecutionValidatorImpl();
    // IStatus status = validator.validateVirtualDatabase(database);
    // assertEquals(IStatus.WARNING, status.getSeverity());
    // }
    //
    // public void testVdbWithModels() {
    // VirtualDatabase database = helpGetVirtualDatabaseWithPhysicalModels();
    // VdbExecutionValidatorImpl validator = new VdbExecutionValidatorImpl();
    // IStatus status = validator.validateVirtualDatabase(database);
    // assertEquals(IStatus.OK, status.getSeverity());
    // }

    // public void testStaleVdb() throws Exception {
    // FakeVdbEditingContext context = (FakeVdbEditingContext) helpGetStaleEditingContext();
    // VirtualDatabase database = helpGetVirtualDatabaseWithPhysicalModels();
    // context.setDatabase(database);
    // VdbExecutionValidator validator = new VdbExecutionValidator();
    // validator.setHelper(new FakeVdbDefnHelper(null));
    // IStatus status = validator.validate(context);
    // assertEquals(IStatus.WARNING, status.getSeverity());
    // }

    // public void testVdbWithNoDefinition() {
    //        String vdbPath = SmartTestSuite.getTestDataPath() + File.separator + "partssupplierConfig/PartsSupplier.vdb"; //$NON-NLS-1$
    // VdbExecutionValidatorImpl validator = new VdbExecutionValidatorImpl();
    // IStatus status = validator.validateVdb(vdbPath);
    // assertEquals(IStatus.ERROR, status.getSeverity());
    // }
}
