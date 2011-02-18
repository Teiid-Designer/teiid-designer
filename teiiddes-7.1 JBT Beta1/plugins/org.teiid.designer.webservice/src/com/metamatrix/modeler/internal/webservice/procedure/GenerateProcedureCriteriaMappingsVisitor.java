/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.procedure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.teiid.query.sql.LanguageVisitor;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;


/** 
 * This would visit language objects inside a CreateProcedureCommand and collect 
 * {@link com.metamatrix.query.sql.proc.AssignmentStatement}s and {@link com.metamatrix.query.sql.lang.CompareCriteria}s
 * objects and create {@link com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping}s
 * @since 4.3
 */
public class GenerateProcedureCriteriaMappingsVisitor extends LanguageVisitor {

    // collection of assignment statements
    private final LinkedHashMap assignmentStmts;
    // collection of compare criteria
    private final Set compareCriteria;
    // collection of criteria mappings
    private Set mappings;
    
    /**
     * Construct a new visitor. 
     */
    public GenerateProcedureCriteriaMappingsVisitor() {
    	this.assignmentStmts = new LinkedHashMap();
        this.compareCriteria = new HashSet();
    }
    
    @Override
    public void visit(CreateUpdateProcedureCommand obj) {
    	super.visit(obj);
    	Block block = obj.getBlock();
    	if (block != null) {
    		List statements = block.getStatements();
    		if (statements != null) {
    			for (Iterator i = statements.iterator(); i.hasNext();) {
    				Statement statement = (Statement)i.next();
    				if (statement instanceof AssignmentStatement) {
    					AssignmentStatement assStmt = (AssignmentStatement)statement;
    					assignmentStmts.put(assStmt.getVariable(), assStmt);
    				}
    			}
    		}
    	}
    }

    /** 
     * @see com.metamatrix.query.sql.LanguageVisitor#visit(com.metamatrix.query.sql.lang.CompareCriteria)
     * @since 4.3
     */
    @Override
    public void visit(CompareCriteria obj) {
        this.compareCriteria.add(obj);
    }
       
    /** 
     * @return Returns the assignmentStmts.
     * @since 4.3
     */
    public Collection getAssignmentStmts() {
        return this.assignmentStmts.values();
    }
    
    /** 
     * @return Returns the compareCriteria.
     * @since 4.3
     */
    public Collection getCompareCriteria() {
        return this.compareCriteria;
    }

    /**
     * Create a collection of ProcedureCriteriaMapping objects by reading in assignment statements
     * and the criteria they are used in the procedure command 
     * @return A collection of {@link com.metamatrix.webservice.procedure.ProcedureCriteriaMapping} objects.
     * @since 4.3
     */
    public Collection createProcedureCriteriaMappingsMappings() {
        if(this.mappings == null) {
            this.mappings = new HashSet();
        }
        // for each assignment statement get the variableName and the XPathExpression and try to find the
        // corresponding compare criteria
        for(final Iterator assignIter = getAssignmentStmts().iterator(); assignIter.hasNext();) {
            // create a mapping object for each assignemnt
            ProcedureCriteriaMappingImpl mapping = new ProcedureCriteriaMappingImpl();            
            AssignmentStatement assignStmt = (AssignmentStatement) assignIter.next();
            String variableName = assignStmt.getVariable().getName();
            if (assignStmt.getExpression()==null){
            	continue;
            }
            String xPathExpression = assignStmt.getExpression().toString();
            // set the variable and XPathExpression
            mapping.setVariableName(variableName);
            mapping.setXPathExpression(xPathExpression);
            // try to find the criteria that uses the given variable
            for(final Iterator critIter = this.compareCriteria.iterator(); critIter.hasNext();) {
                CompareCriteria criteria = (CompareCriteria) critIter.next();
                
                // get the elementName and the function name if any for the left side
                // expression
                Expression leftExpr = criteria.getLeftExpression();
                String leftFunctionName = null;
                String leftElementName = null;
                if(leftExpr instanceof Function) {
                    Function function = (Function) leftExpr;
                    leftFunctionName = function.getName();
                    // assumption: first argument on the function is the element
                    leftElementName = function.getArg(0).toString();
                } else {
                    leftElementName = leftExpr.toString();
                }

                // get the elementName and the function name if any for the right side
                // expression
                Expression rightExpr = criteria.getRightExpression();
                String rightFunctionName = null;
                String rightElementName = null;
                if(rightExpr instanceof Function) {
                    Function function = (Function) rightExpr;
                    rightFunctionName = function.getName();
                    // assumption: first argument on the function is the element
                    rightElementName = function.getArg(0).toString();
                } else {
                    rightElementName = rightExpr.toString();
                }

                // check if leftside is a variable
                if(leftElementName != null && leftElementName.equalsIgnoreCase(variableName)) {
                    mapping.setFunctionOnVariable(leftFunctionName);
                    mapping.setCriteriaElementName(rightElementName);
                    mapping.setFunctionOnCriteriaElement(rightFunctionName);
                }

                // check if rightside is a variable
                if(rightElementName != null && rightElementName.equalsIgnoreCase(variableName)) {
                    mapping.setFunctionOnVariable(rightFunctionName);
                    mapping.setCriteriaElementName(leftElementName);
                    mapping.setFunctionOnCriteriaElement(leftFunctionName);
                }
            }
            this.mappings.add(mapping);
        }
        
        return mappings;
    }

    /**
     * Helper to quickly get the assignment statements from obj
     * @param obj Language object
     */
    public static final Collection getAssignmentStmts(CreateUpdateProcedureCommand obj) {
        GenerateProcedureCriteriaMappingsVisitor visitor = new GenerateProcedureCriteriaMappingsVisitor();
        PreOrderNavigator.doVisit(obj, visitor);
        return visitor.getAssignmentStmts();
    }

    /**
     * Helper to quickly get the compare criteria from obj
     * @param obj Language object
     */
    public static final Collection getCompareCriteria(CreateUpdateProcedureCommand obj) {
        GenerateProcedureCriteriaMappingsVisitor visitor = new GenerateProcedureCriteriaMappingsVisitor();
        PreOrderNavigator.doVisit(obj, visitor);
        return visitor.getCompareCriteria();
    }

    /**
     * Helper to quickly get the groups from obj in the groups collection
     * @param obj Language object
     * @param elements Collection to collect groups in
     */
    public static final Collection getMappins(CreateUpdateProcedureCommand obj) {
        GenerateProcedureCriteriaMappingsVisitor visitor = new GenerateProcedureCriteriaMappingsVisitor();
        PreOrderNavigator.doVisit(obj, visitor);
        return visitor.createProcedureCriteriaMappingsMappings();
    }    
}
