/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation.rules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/** 
 * @since 4.3
 */
public class TestMappingClassTransformationValidationHelper extends TestCase {

    private Collection<ITeiidServerVersion> serverVersions;

    /**
     * Constructor for TestInputSetPramReplacementVisitor.
     * @param name
     */
    public TestMappingClassTransformationValidationHelper(String name) throws Exception {
        super(name);

        serverVersions = TeiidRuntimeRegistry.getInstance().getSupportedVersions();
    }

    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================

    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestMappingClassTransformationValidationHelper"); //$NON-NLS-1$
        suite.addTestSuite(TestMappingClassTransformationValidationHelper.class);
        //suite.addTest(new TestMappingClassTransformationValidationHelper("testGetCharacterVDBResource")); //$NON-NLS-1$
        //suite.addTest(new TestMappingClassTransformationValidationHelper("testGetElementIDsInKey13760")); //$NON-NLS-1$

        return new TestSetup(suite);
    }

    private void setDefaultServerVersion(ITeiidServerVersion version) {
        ITeiidServer teiidServer = mock(ITeiidServer.class);
        when(teiidServer.getServerVersion()).thenReturn(version);

        ITeiidServerManager teiidServerManager = mock(ITeiidServerManager.class);
        when(teiidServerManager.getDefaultServer()).thenReturn(teiidServer);
        when(teiidServerManager.getDefaultServerVersion()).thenReturn(version);

        ModelerCore.setTeiidServerManager(teiidServerManager);
    }

    public MappingClass helpGetMappingClass() {
        return TransformationFactory.eINSTANCE.createMappingClass();
    }

    public SqlTransformationMappingRoot helpTransformationMappingRoot() {
        SqlTransformationMappingRoot root = TransformationFactory.eINSTANCE.createSqlTransformationMappingRoot();
        root.setTarget(helpGetMappingClass());
        return root;
    }

    public MappingClass helpGetRecursiveMappingClass() {
        MappingClass mappingClass = TransformationFactory.eINSTANCE.createMappingClass();
        mappingClass.setRecursive(true);
        return mappingClass;
    }

    public ICommand parseCommand(String sql) throws Exception {
        IQueryParser parser = ModelerCore.getTeiidQueryService().getQueryParser();
        return parser.parseCommand(sql);
    }

    public void testNonQueryNonSetQuery() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("Delete from x");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate(command, helpTransformationMappingRoot(), result);
            assertTrue(result.hasProblems());
            assertEquals(1, result.getProblems().length);
            assertEquals(IStatus.ERROR, (result.getProblems()[0]).getSeverity());
        }
    }

    public void testNoInputSelectNoRecursiveMappingClass() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("SELECT e FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetMappingClass();
            ValidationResult result = new ValidationResultImpl(mappingClass);
            rule.validate((IQuery)command, mappingClass, result);
            assertTrue(!result.hasProblems());
        }
    }

    public void testInputSelectNoRecursiveMappingClass() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("SELECT e, INPUT.f FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((IQuery)command, mappingClass, result);
            assertFalse(result.hasProblems());
            assertEquals(0, result.getProblems().length);
        }
    }

    public void testNoInputSelectRecursiveMappingClassNoCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("SELECT e FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((IQuery)command, mappingClass, result);
            assertTrue(result.hasProblems());
            assertEquals(1, result.getProblems().length);
            assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
        }
    }

    public void testInputSelectRecursiveMappingClassNoCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("SELECT e, INPUT.f FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((IQuery)command, mappingClass, result);
            assertTrue(result.hasProblems());
            assertEquals(1, result.getProblems().length);
            assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
        }
    }

    public void testNoInputSelectRecursiveMappingClassNoInputCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("SELECT e FROM g where x = y");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((IQuery)command, mappingClass, result);
            assertTrue(result.hasProblems());
            assertEquals(1, result.getProblems().length);
            assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
        }
    }

    public void testInputSelectRecursiveMappingClassInputCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ICommand command = parseCommand("SELECT e FROM g where x = Input.y");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((IQuery)command, mappingClass, result);

            /* 
             * Changes to client runtime mean a different result. Since canonical name has been removed from
             * the Symbol implementation in 8.0+, the word 'input' is case-sensitive and never upper-cased hence
             * the validator never decides that 'input' and 'INPUT' are the same, which is what happened in 7.7.
             */
            if (ModelerCore.getTeiidServerVersion().isSevenServer()) {
                assertFalse(result.hasProblems());
            } else {
                assertTrue(result.hasProblems());
                assertEquals(1, result.getProblems().length);
                assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
            }
        }
    }

    public void testNoInputUNIONSelectRecursiveMappingClassNoInputCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ICommand command = parseCommand("SELECT e FROM g where x = y UNION SELECT e FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((ISetQuery)command, mappingClass, result);

            assertTrue(result.hasProblems());
            assertEquals(1, result.getProblems().length);
            assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
        }
    }

    public void testInputUNIONSelectRecursiveMappingClassInputCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ICommand command = parseCommand("SELECT e FROM g where x = Input.y UNION SELECT e FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((ISetQuery)command, mappingClass, result);

            /* 
             * Changes to client runtime mean a different result. Since canonical name has been removed from
             * the Symbol implementation in 8.0+, the word 'input' is case-sensitive and never upper-cased hence
             * the validator never decides that 'input' and 'INPUT' are the same, which is what happened in 7.7.
             */
            if (ModelerCore.getTeiidServerVersion().isSevenServer()) {
                assertFalse(result.hasProblems());
            } else {
                assertTrue(result.hasProblems());
                assertEquals(1, result.getProblems().length);
                assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
            }
        }
    }

    public void testInputUNIONSelectRecursiveMappingClassNoCriteria() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICommand command = parseCommand("SELECT e, INPUT.f FROM g  UNION SELECT e FROM g");//$NON-NLS-1$
            MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
            MappingClass mappingClass = helpGetRecursiveMappingClass();
            ValidationResult result = new ValidationResultImpl(new Object());
            rule.validate((ISetQuery)command, mappingClass, result);
            assertTrue(result.hasProblems());
            assertEquals(1, result.getProblems().length);
            assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
        }
    }
}
