/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.DynamicCommand;
import com.metamatrix.query.sql.symbol.ElementSymbol;

/**
 * The <code>QueryDisplayNode</code> class is used to represent a SELECT Query.
 */
public class DynamicCommandDisplayNode extends DisplayNode {

    /**
     *   QueryDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param query the query language object used to construct this display node.
     */
    public DynamicCommandDisplayNode(DisplayNode parentNode, DynamicCommand query) {
        this.parentNode = parentNode;
        this.languageObject = query;
        createChildNodes();
    }
    
    /**
     *   Create the child nodes for this type of DisplayNode.  For a QueryDisplayNode,
     *  the children are the clauses of the Query.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        DynamicCommand query = (DynamicCommand)(this.getLanguageObject());
        //int indent = this.getIndentLevel();

        displayNodeList = new ArrayList();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.EXECUTE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.STRING));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        addChildNode(DisplayNodeFactory.createDisplayNode(this,query.getSql()));

        if(query.isAsClauseSet()){
            addPostClauseFormatting(this);
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.AS));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            for (int i = 0; i < query.getAsColumns().size(); i++) {
                ElementSymbol symbol = (ElementSymbol)query.getAsColumns().get(i);
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,symbol.getShortName()));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,DataTypeManager.getDataTypeName(symbol.getType())));
                if (i < query.getAsColumns().size() - 1) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA + SPACE));
                }
            }
        }

        if(query.getIntoGroup() != null){
            addPostClauseFormatting(this);
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.INTO));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            addChildNode((DisplayNodeFactory.createDisplayNode(this,query.getIntoGroup())));
        }

        if(query.getUsing() != null && query.getUsing().getClauses().size() > 0) {
            addPostClauseFormatting(this);
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.USING));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            Iterator i = query.getUsing().getClauseMap().entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry)i.next();
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,((ElementSymbol)entry.getKey()).getShortName()));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,EQUALS));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                addChildNode(DisplayNodeFactory.createDisplayNode(this, entry.getValue()));
                if (i.hasNext()) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA + SPACE));
                }
            }
        }

        if (query.getUpdatingModelCount() > 0) {
            addPostClauseFormatting(this);
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.UPDATE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            if (query.getUpdatingModelCount() > 1) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,"*")); //$NON-NLS-1$
            } else {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, new Integer(1)));           
            }
        }

    }
    
    public void addChildNode(DisplayNode cNode) {
        childNodeList.add(cNode);
        List cDisplayNodes = null;
        if(cNode.hasDisplayNodes()) {
            cDisplayNodes = cNode.getDisplayNodeList();
            displayNodeList.addAll(cDisplayNodes);
        } else {
            displayNodeList.add(cNode);
        }
    }
    
    private void addPostClauseFormatting(DisplayNode owner) {
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(owner,SPACE));
        if(DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(owner,CR));
        }
    }

}
