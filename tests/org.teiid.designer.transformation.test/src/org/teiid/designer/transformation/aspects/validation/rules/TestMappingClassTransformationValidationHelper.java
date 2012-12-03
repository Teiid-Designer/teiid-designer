/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation.rules;

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


/** 
 * @since 4.3
 */
public class TestMappingClassTransformationValidationHelper extends TestCase {
    
    /**
     * Constructor for TestInputSetPramReplacementVisitor.
     * @param name
     */
    public TestMappingClassTransformationValidationHelper(String name) {
        super(name);
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
        ICommand command = parseCommand("Delete from x");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate(command, helpTransformationMappingRoot(), result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.ERROR, (result.getProblems()[0]).getSeverity());        
    }

    public void testNoInputSelectNoRecursiveMappingClass() throws Exception {
        ICommand command = parseCommand("SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetMappingClass();
        ValidationResult result = new ValidationResultImpl(mappingClass);
        rule.validate((IQuery)command, mappingClass, result);
        assertTrue(!result.hasProblems());
    }

    public void testInputSelectNoRecursiveMappingClass() throws Exception {
        ICommand command = parseCommand("SELECT e, INPUT.f FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((IQuery)command, mappingClass, result);
        assertFalse(result.hasProblems());
        assertEquals(0, result.getProblems().length);
    }
    
    public void testNoInputSelectRecursiveMappingClassNoCriteria() throws Exception {
        ICommand command = parseCommand("SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((IQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testInputSelectRecursiveMappingClassNoCriteria() throws Exception {
        ICommand command = parseCommand("SELECT e, INPUT.f FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((IQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testNoInputSelectRecursiveMappingClassNoInputCriteria() throws Exception {
        ICommand command = parseCommand("SELECT e FROM g where x = y");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((IQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testInputSelectRecursiveMappingClassInputCriteria() throws Exception {
        ICommand command = parseCommand("SELECT e FROM g where x = Input.y");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((IQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }

    public void testNoInputUNIONSelectRecursiveMappingClassNoInputCriteria() throws Exception {
        ICommand command = parseCommand("SELECT e FROM g where x = y UNION SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((ISetQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }

    public void testInputUNIONSelectRecursiveMappingClassInputCriteria() throws Exception {
        ICommand command = parseCommand("SELECT e FROM g where x = Input.y UNION SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((ISetQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testInputUNIONSelectRecursiveMappingClassNoCriteria() throws Exception {
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
