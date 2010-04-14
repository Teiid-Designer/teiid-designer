/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Option;

/**
 * The <code>OptionDisplayNode</code> class is used to represent a Query's OPTION clause.
 */
public class OptionDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * OptionDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param option the query language object used to construct this display node.
     */
    public OptionDisplayNode( DisplayNode parentNode,
                              Option option ) {
        this.parentNode = parentNode;
        this.languageObject = option;
        createChildNodes();
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList() {
        // if(childNodeList.size()==0) return;

        displayNodeList = new ArrayList();
        // int indent = this.getIndentLevel();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.OPTION));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        // if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        // displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        // indent++;
        // }

        Option option = (Option)(this.getLanguageObject());

        List makeDepGroups = option.getDependentGroups();
        if (makeDepGroups != null && makeDepGroups.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.MAKEDEP));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));

            Iterator iter = makeDepGroups.iterator();
            String group = (String)iter.next();
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, group));
            while (iter.hasNext()) {
                group = (String)iter.next();
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, COMMA + SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, group));
            }
        }

        List makeNotDepGroups = option.getNotDependentGroups();
        if (makeNotDepGroups != null && makeNotDepGroups.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.MAKENOTDEP));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));

            Iterator iter = makeNotDepGroups.iterator();
            String group = (String)iter.next();
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, group));
            while (iter.hasNext()) {
                group = (String)iter.next();
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, COMMA + SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, group));
            }
        }

        Collection noCacheGroups = option.getNoCacheGroups();
        if (noCacheGroups != null && noCacheGroups.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.NOCACHE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));

            Iterator gIter = noCacheGroups.iterator();
            String group = (String)gIter.next();
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, group));
            while (gIter.hasNext()) {
                group = (String)gIter.next();
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, COMMA + SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, group));
            }
        } else if (option.isNoCache()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ReservedWords.NOCACHE));
        }

        // No options were set, omit option
        if (displayNodeList.size() == 2) {
            displayNodeList.clear();
        }

        // if(isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        // displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        // }
    }

}
