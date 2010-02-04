/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation.rules;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.SetQuery;


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

    public Command parseCommand(String sql) throws Exception {
        QueryParser parser = new QueryParser();
        return parser.parseCommand(sql);
    }
    
    public void testNonQueryNonSetQuery() throws Exception {
        Command command = parseCommand("Delete from x");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate(command, helpTransformationMappingRoot(), result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.ERROR, (result.getProblems()[0]).getSeverity());        
    }

    public void testNoInputSelectNoRecursiveMappingClass() throws Exception {
        Command command = parseCommand("SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetMappingClass();
        ValidationResult result = new ValidationResultImpl(mappingClass);
        rule.validate((Query)command, mappingClass, result);
        assertTrue(!result.hasProblems());
    }

    public void testInputSelectNoRecursiveMappingClass() throws Exception {
        Command command = parseCommand("SELECT e, INPUT.f FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((Query)command, mappingClass, result);
        assertFalse(result.hasProblems());
        assertEquals(0, result.getProblems().length);
    }
    
    public void testNoInputSelectRecursiveMappingClassNoCriteria() throws Exception {
        Command command = parseCommand("SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((Query)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testInputSelectRecursiveMappingClassNoCriteria() throws Exception {
        Command command = parseCommand("SELECT e, INPUT.f FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((Query)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testNoInputSelectRecursiveMappingClassNoInputCriteria() throws Exception {
        Command command = parseCommand("SELECT e FROM g where x = y");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((Query)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }
    
    public void testNoInputSelectRecursiveMappingClassInputCriteria() throws Exception {
        Command command = parseCommand("SELECT e FROM g where x = Input.y");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((Query)command, mappingClass, result);
        assertTrue(!result.hasProblems());
    }

    public void testNoInputUNIONSelectRecursiveMappingClassNoInputCriteria() throws Exception {
        Command command = parseCommand("SELECT e FROM g where x = y UNION SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((SetQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }

    public void testNoInputUNIONSelectRecursiveMappingClassInputCriteria() throws Exception {
        Command command = parseCommand("SELECT e FROM g where x = Input.y UNION SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((SetQuery)command, mappingClass, result);
        assertTrue(!result.hasProblems());
    }
    
    public void testInputUNIONSelectRecursiveMappingClassNoCriteria() throws Exception {
        Command command = parseCommand("SELECT e, INPUT.f FROM g  UNION SELECT e FROM g");//$NON-NLS-1$
        MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
        MappingClass mappingClass = helpGetRecursiveMappingClass();
        ValidationResult result = new ValidationResultImpl(new Object());
        rule.validate((SetQuery)command, mappingClass, result);
        assertTrue(result.hasProblems());
        assertEquals(1, result.getProblems().length);
        assertEquals(IStatus.WARNING, (result.getProblems()[0]).getSeverity());
    }    
}
