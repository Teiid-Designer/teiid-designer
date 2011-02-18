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
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping;
import org.teiid.query.sql.LanguageVisitor;
import org.teiid.query.sql.lang.AbstractCompareCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;


/** 
 * This visitor is used to update a CreateProcedureCommand using 
 * {@link com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping} objects. The
 * mapping objects are translated into assignment statements and criteria objects. 
 * @since 4.3
 */
public class ProcedureUpdateVisitor extends LanguageVisitor {

    private Collection assignmentStatements;
    private Collection compareCriteria;

    /** 
     * Constructor ProcedureUpdateVisitor.
     * @param procCriteriaMappings A collection of 
     * {@link com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping} objects.
     * @since 4.3
     */
    public ProcedureUpdateVisitor(final Collection procCriteriaMappings) {
        CoreArgCheck.isNotNull(procCriteriaMappings);
        // read in the mappings and tranlate info into language objects
        init(procCriteriaMappings);
    }

    /**
     *  
     * @param procCriteriaMappings A collection of 
     * {@link com.metamatrix.modeler.webservice.procedure.ProcedureCriteriaMapping} objects.
     * @since 4.3
     */
    private void init(final Collection procCriteriaMappings) {
        this.assignmentStatements = new HashSet();
        this.compareCriteria = new HashSet();
        for(final Iterator iter = procCriteriaMappings.iterator(); iter.hasNext();) {
            ProcedureCriteriaMapping mapping = (ProcedureCriteriaMapping) iter.next();
            // get element information on the mapping
            String elementName = mapping.getCriteriaElementName();
            String elementFunction = mapping.getFunctionOnCriteriaElement();
            // build an expression given the element information
            Expression leftExpression = null;
            if(elementFunction == null) {
                leftExpression = new ElementSymbol(elementName);
            } else {
                Expression[] expr = {new ElementSymbol(elementName)};
                leftExpression = new Function(elementFunction, expr);
            }
            
            // get variable information from mapping
            String variableName = mapping.getVariableName();
            String variableFunction = mapping.getFunctionOnVariable();
            // build the right expression
            Expression rightExpression = null;
            if(elementFunction == null) {
                leftExpression = new ElementSymbol(variableName);
            } else {
                Expression[] expr = {new ElementSymbol(variableName)};
                rightExpression = new Function(variableFunction, expr);
            }
            // create compare criteria
            CompareCriteria criteria = new CompareCriteria();
            criteria.set(leftExpression, AbstractCompareCriteria.EQ, rightExpression);
            // collect the criteria
            this.compareCriteria.add(criteria);
            
            // get XPAth expression information
            String xPathExpr = mapping.getXPathExpression();
            AssignmentStatement stmnt = new AssignmentStatement();
            stmnt.setVariable(new ElementSymbol(variableName));
            stmnt.setExpression(new ElementSymbol(xPathExpr));
            // collect the assignments
            this.assignmentStatements.add(stmnt);
        }
    }

    /** 
     * @see com.metamatrix.query.sql.LanguageVisitor#visit(com.metamatrix.query.sql.proc.Block)
     * @since 4.3
     */
    @Override
    public void visit(final Block obj) {
        super.visit(obj);
    }

    /**
     * @see com.metamatrix.query.sql.LanguageVisitor#visit(com.metamatrix.query.sql.proc.CreateUpdateProcedureCommand)
     * @since 4.3
     */
    @Override
    public void visit(final CreateUpdateProcedureCommand obj) {
        super.visit(obj);
    }

    /** 
     * @see com.metamatrix.query.sql.LanguageVisitor#visit(com.metamatrix.query.sql.lang.Query)
     * @since 4.3
     */
    @Override
    public void visit(Query obj) {
        super.visit(obj);
    }

}
