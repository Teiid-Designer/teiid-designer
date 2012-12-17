/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class AssignmentStatementImpl extends LanguageObjectImpl implements IAssignmentStatement {

    /**
     * @param assignmentStatement
     */
    public AssignmentStatementImpl(AssignmentStatement assignmentStatement) {
        super(assignmentStatement);
    }

    @Override
    public AssignmentStatement getDelegate() {
        return (AssignmentStatement) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AssignmentStatementImpl clone() {
        return new AssignmentStatementImpl((AssignmentStatement) getDelegate().clone());
    }

    @Override
    public IExpression getExpression() {
        return getFactory().convert(getDelegate().getExpression());
    }

    @Override
    public IElementSymbol getVariable() {
        return getFactory().convert(getDelegate().getVariable());
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public void setValue(IExpression value) {
        Expression expressionImpl = getFactory().convert(value);
        getDelegate().setValue(expressionImpl);
    }
}