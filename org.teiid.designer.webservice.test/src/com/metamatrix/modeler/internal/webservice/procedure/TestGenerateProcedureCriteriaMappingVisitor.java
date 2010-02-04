/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.procedure;

import junit.framework.TestCase;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.proc.CreateUpdateProcedureCommand;


/** 
 * @since 4.3
 */
public class TestGenerateProcedureCriteriaMappingVisitor extends TestCase {

    /** 
     * 
     * @since 4.3
     */
    public TestGenerateProcedureCriteriaMappingVisitor() {
        super();
    }

    /** 
     * @param name
     * @since 4.3
     */
    public TestGenerateProcedureCriteriaMappingVisitor(String name) {
        super(name);
    }
    
    private String helpGetSampleProcedure1() {
        String procedure = "Create Procedure "+ //$NON-NLS-1$
                           "BEGIN "+ //$NON-NLS-1$
                           "DECLARE String Variables.MyVariable; "+ //$NON-NLS-1$
                           "Variables.MyVariable = xpathValues(WSModel.Operation.Input, \"xyz\"); "+ //$NON-NLS-1$"
                           "Select x from y where z = Variables.MyVariable; "+ //$NON-NLS-1$
                           "END"; //$NON-NLS-1$
        return procedure;
    }

    private String helpGetSampleProcedure2() {
        String procedure = "Create Procedure "+ //$NON-NLS-1$
                           "BEGIN "+ //$NON-NLS-1$
                           "DECLARE String Variables.MyVariable; "+ //$NON-NLS-1$
                           "Variables.MyVariable = xpathValues(WSModel.Operation.Input, \"xyz\"); "+ //$NON-NLS-1$"
                           "Variables.MyVariable = xpathValues(WSModel.Operation.Input, \"xyz\"); "+ //$NON-NLS-1$"
                           "Select x from y where z = Variables.MyVariable; "+ //$NON-NLS-1$
                           "END"; //$NON-NLS-1$
        return procedure;
    }

    public void testGenMappingFindAssignments1() throws Exception {
        String procedureSql = helpGetSampleProcedure1();
        Command cmd = QueryParser.getQueryParser().parseCommand(procedureSql);
        GenerateProcedureCriteriaMappingsVisitor visitor = new GenerateProcedureCriteriaMappingsVisitor(); 
        visitor.visit((CreateUpdateProcedureCommand)cmd);
        assertEquals(1, visitor.getAssignmentStmts().size());
        assertEquals(1, GenerateProcedureCriteriaMappingsVisitor.getCompareCriteria((CreateUpdateProcedureCommand)cmd).size());
    }

    public void testGenMappingFindAssignments2() throws Exception {
        String procedureSql = helpGetSampleProcedure2();
        Command cmd = QueryParser.getQueryParser().parseCommand(procedureSql);
        GenerateProcedureCriteriaMappingsVisitor visitor = new GenerateProcedureCriteriaMappingsVisitor(); 
        visitor.visit((CreateUpdateProcedureCommand)cmd);
        assertEquals(1, visitor.getAssignmentStmts().size());
        assertEquals(1, GenerateProcedureCriteriaMappingsVisitor.getCompareCriteria((CreateUpdateProcedureCommand)cmd).size());
    }
    
    public void testGenMappingFindAssignments3() throws Exception {
        String procedureSql = helpGetSampleProcedure2();
        Command cmd = QueryParser.getQueryParser().parseCommand(procedureSql);
        assertEquals(1, GenerateProcedureCriteriaMappingsVisitor.getMappins((CreateUpdateProcedureCommand)cmd).size());
    }
}
