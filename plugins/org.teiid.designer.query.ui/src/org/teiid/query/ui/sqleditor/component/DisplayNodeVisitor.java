/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.ui.sqleditor.component;

import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitorCallback;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;
import org.teiid.designer.query.sql.proc.IExpressionStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;

/**
 * This visitor works intimately with the SQLStringVisitor to construct a display node tree.
 */
public final class DisplayNodeVisitor implements ISQLStringVisitorCallback {

    private final DisplayNode node;
    private final boolean dontAppend;
    private int indentLevel;
    private int originalLevel;
    private final ISQLStringVisitor delegate;

    DisplayNodeVisitor(DisplayNode node,
                       boolean dontAppend,
                       int indentLevel) {
        this.node = node;
        this.dontAppend = dontAppend;
        this.indentLevel = indentLevel;
        this.originalLevel = indentLevel;

        IQueryService queryService = ModelerCore.getTeiidQueryService();
        delegate = queryService.getCallbackSQLStringVisitor(this);
    }

    @Override
    public void visitNode(ILanguageObject obj) {
        if (obj == null) {
            append(DisplayNodeConstants.UNDEFINED);
            return;
        }

        if ((obj instanceof IExpressionSymbol && !(obj instanceof IAggregateSymbol))) {
            obj.acceptVisitor(delegate);
            return;
        }

        // turn off indenting for nested commands
        int childIndent = indentLevel;
        if ((node.languageObject instanceof ISubqueryContainer || node.languageObject instanceof IExpressionStatement)
            && obj instanceof ICommand) {
            childIndent = -1;
        }
        DisplayNode child = DisplayNodeFactory.createDisplayNode(node, obj, childIndent);
        node.addChildNode(child);
    }

    @Override
    public void addTabs(int level) {
        setIndentLevel(this.originalLevel + level);
        if (this.indentLevel > 0) {
            node.displayNodeList.addAll(DisplayNodeUtils.getIndentNodes(node, this.indentLevel));
        }
    }

    @Override
    public void visitCriteria(String keyWord, ICriteria crit) {
        if (ISQLConstants.WHERE.equals(keyWord)) {
            DisplayNode child = new WhereDisplayNode(node, crit);
            createCriteriaNode(keyWord, crit, child);
        } else if (ISQLConstants.HAVING.equals(keyWord)) {
            DisplayNode child = new HavingDisplayNode(node, crit);
            createCriteriaNode(keyWord, crit, child);
        } else {
            append(keyWord);
            append(DisplayNodeConstants.SPACE);
            visitNode(crit);
        }
    }

    private void createCriteriaNode(String keyWord, ICriteria crit, DisplayNode child) {
        node.addChildNode(child);
        child.displayNodeList.add(DisplayNodeFactory.createDisplayNode(child, keyWord));
        setIndentLevel(this.indentLevel + 1);
        beginClause(child, this.indentLevel);
        child.addChildNode(DisplayNodeFactory.createDisplayNode(child, crit, indentLevel));
    }

    @Override
    public void append(Object value) {
        if (dontAppend) {
            return; // for compatibility, this keeps symbols from having children
        }
        
        // otherwise, the value is a string/enum/primitive
        node.displayNodeList.add(DisplayNodeFactory.constructDisplayNode(node, value));
    }

    @Override
    public void beginClause(int level) {
        setIndentLevel(this.originalLevel + level);
        beginClause(node, this.indentLevel);
    }

    private void beginClause(DisplayNode node, int level) {
        if (level >= 0 && DisplayNodeUtils.isClauseCROn()) {
            node.displayNodeList.add(DisplayNodeFactory.createDisplayNode(node, DisplayNodeConstants.CR));
            if (DisplayNodeUtils.isClauseIndentOn()) {
                node.displayNodeList.addAll(DisplayNodeUtils.getIndentNodes(node, level));
            }
        } else {
            node.displayNodeList.add(DisplayNodeFactory.createDisplayNode(node, DisplayNodeConstants.SPACE));
        }
    }

    private void setIndentLevel(int indentLevel) {
        // preserve -1, which means no indenting
        if (this.indentLevel != -1) {
            this.indentLevel = indentLevel;
        }
    }
}
