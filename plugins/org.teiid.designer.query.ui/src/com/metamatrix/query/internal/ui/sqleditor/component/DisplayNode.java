/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.teiid.query.sql.LanguageObject;

/**
 * The <code>DisplayNode</code> class is the base class used by <code>QueryDisplayComponent</code> to represent all types of
 * Display Nodes.
 */
public abstract class DisplayNode implements
                                 DisplayNodeConstants {

    // /////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////

    protected int startIndex = 0;
    protected int endIndex = 0;
    protected DisplayNode parentNode = null;
    protected LanguageObject languageObject = null;
    protected List<DisplayNode> childNodeList = Collections.EMPTY_LIST;
    protected List<DisplayNode> displayNodeList = Collections.EMPTY_LIST;
    protected int indentLevel = 0;
    private boolean visible = true;

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the Child Nodes of this Display Node
     */
    public DisplayNode getParent() {
        return parentNode;
    }

    /**
     * Get the LanguageObject associated with this DisplayNode
     */
    public LanguageObject getLanguageObject() {
        return languageObject;
    }

    /**
     * Get the Child Nodes of this Display Node
     */
    public List<DisplayNode> getChildren() {
        return childNodeList;
    }

    /**
     * Returns a flattened display node list of the entire tree under this Node.
     */
    public List getDisplayNodeList() {
        return displayNodeList;
    }

    /**
     * @return True if this display node is visible within it's containing UI component.
     * @since 5.0.1
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * @param visible
     *            <code>true</code> if this display node is visible within it's containing UI component.
     * @param includeDescendents
     *            <code>true</code> if the visibility of this node's descendents should also be affected.
     * @since 5.0.1
     */
    public void setVisible(boolean visible,
                           boolean includeDescendents) {
        this.visible = visible;
        if (includeDescendents && this.childNodeList != null) {
            for (Iterator iter = this.childNodeList.iterator(); iter.hasNext();) {
                ((DisplayNode)iter.next()).setVisible(visible, includeDescendents);
            } // for
        }
        if (this.displayNodeList != null) {
            for (Iterator iter = this.displayNodeList.iterator(); iter.hasNext();) {
                DisplayNode node = (DisplayNode)iter.next();
                if (node.parentNode == this) {
                    node.setVisible(visible, includeDescendents);
                }
            } // for
        }
    }

    /**
     * @return The displayable String representation for this display node.
     * @since 5.0.1
     */
    public String toDisplayString() {
        StringBuffer sb = new StringBuffer();
        Iterator iter = displayNodeList.iterator();
        while (iter.hasNext()) {
            sb.append(((DisplayNode)iter.next()).toDisplayString());
        }
        return sb.toString();
    }

    /**
     * Returns the String representation for this display node.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator iter = displayNodeList.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().toString());
        }
        return sb.toString();
    }

    /**
     * Returns whether the node has any children
     */
    public boolean hasChildren() {
        return ( childNodeList != null && childNodeList.size() > 0) ? true : false;
    }

    /**
     * Returns whether the node has any display nodes
     */
    public boolean hasDisplayNodes() {
        return (displayNodeList.size() > 0) ? true : false;
    }

    /**
     * Determine if the DisplayNode supports elements in it. Default implementation returns false.
     */
    public boolean supportsElement() {
        return false;
    }

    /**
     * Determine if the DisplayNode supports groups in it. Default implementation returns false.
     */
    public boolean supportsGroup() {
        return false;
    }

    /**
     * Determine if the DisplayNode supports expressions in it. Default implementation returns false.
     */
    public boolean supportsExpression() {
        return isInExpression();
    }

    /**
     * Determine if this DisplayNode is within an expression. Checks whether the parentNode is an ExpressionDisplayNode
     */
    public boolean isInExpression() {
        return (getExpression() != null);
    }

    /**
     * Get ExpressionDisplayNode
     */
    public ExpressionDisplayNode getExpression() {
        if (this instanceof ExpressionDisplayNode) {
            return (ExpressionDisplayNode)this;
        }

        // lookup parent hierarchy
        DisplayNode parentNode = this.getParent();
        return (parentNode == null) ? null : parentNode.getExpression();
    }

    /**
     * Determine if the DisplayNode supports criteria in it. Default implementation returns false.
     */
    public boolean supportsCriteria() {
        return false;
    }

    /**
     * Determine if the DisplayNode is within a criteria. Checks whether the parentNode is a CriteriaDisplayNode.
     */
    public boolean isInCriteria() {
        DisplayNode parentNode = this.getParent();
        while (parentNode != null) {
            if (parentNode instanceof CriteriaDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * Get CriteriaDisplayNode
     */
    public CriteriaDisplayNode getCriteria() {
        DisplayNode parentNode = this.getParent();
        while (parentNode != null) {
            if (parentNode instanceof CriteriaDisplayNode) {
                return (CriteriaDisplayNode)parentNode;
            }
            parentNode = parentNode.getParent();
        }
        return null;
    }

    /**
     * Sets the starting index for this node and reindex everything under it.
     */
    public int setStartIndex(int index) {
        startIndex = index;
        endIndex = index;

        // ------------------------------------------
        // Reindex the DisplayNodeList
        // ------------------------------------------
        Iterator iter = displayNodeList.iterator();
        DisplayNode node = null;
        if (iter.hasNext()) {
            node = (DisplayNode)iter.next();
            endIndex = node.setStartIndex(endIndex);
        }
        startIndex = endIndex + 1;
        while (iter.hasNext()) {
            node = (DisplayNode)iter.next();
            endIndex = node.setStartIndex(startIndex);
            startIndex = endIndex + 1;
        }

        // ---------------------------------------------------
        // Reindex all of the display node parents
        // ---------------------------------------------------
        iter = displayNodeList.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            reindexParents(displayNode);
        }

        return endIndex;
    }

    /**
     * Reindex the Parents of this display Node
     */
    private void reindexParents(DisplayNode node) {
        while (node != null) {
            DisplayNode parentNode = node.getParent();
            // ------------------------------------------------
            // If currentNode has Parent, index the Parent
            // ------------------------------------------------
            if (parentNode != null) {
                List childDisplayNodes = parentNode.getDisplayNodeList();
                int nd = childDisplayNodes.size();
                if (nd != 0) {
                    parentNode.startIndex = ((DisplayNode)childDisplayNodes.get(0)).getStartIndex();
                    parentNode.endIndex = ((DisplayNode)childDisplayNodes.get(nd - 1)).getEndIndex();
                }
            }
            // Reset Node to parent
            node = parentNode;
        }
    }

    /**
     * Returns the starting index for this node
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Returns the ending index for this node
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Returns the length of the node
     */
    public int length() {
        return endIndex - startIndex + 1;
    }

    /**
     * Returns true if index is at the start of the display node
     */
    public boolean isIndexAtStart(int index) {
        return (index != startIndex) ? false : true;
    }

    /**
     * Returns true if index is at the end of the display node
     */
    public boolean isIndexAtEnd(int index) {
        return (index != (endIndex + 1)) ? false : true;
    }

    /**
     * Returns true if index is anywhere within the display node, including the start and end position
     */
    public boolean isAnywhereWithin(int index) {
        return (index >= startIndex && index <= (endIndex + 1)) ? true : false;
    }

    /**
     * Returns true if index is anywhere within the display node, NOT including the start and end position
     */
    public boolean isWithin(int index) {
        return (index > startIndex && index < (endIndex + 1)) ? true : false;
    }

    /**
     * Sets the indent level for this node
     */
    public void setIndentLevel(int iIndent) {
        this.indentLevel = iIndent;
    }

    /**
     * Returns the indent level for this node
     */
    public int getIndentLevel() {
        // defect 16240 - be a little more sane about indentation.
        return getDepth();// indentLevel;
    }

    /**
     * Get the distance from this node to the root
     * 
     * @return
     */
    public int getDepth() {
        int rv = 0;
        DisplayNode parentNode = this.getParent();
        while (parentNode != null) {
            rv++;
            parentNode = parentNode.getParent();
        } // endwhile

        return rv;
    }

    private List addIndentToRealNodes(List statementNodes,
                                      int childIndent) {
        // if indent turned on, add it:
        if (DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
            List rv = new ArrayList();

            // loop through the contents; every non-indented keyword
            // should have indents added:
            boolean lastWasNewLine = true;
            Iterator itor = statementNodes.iterator();
            while (itor.hasNext()) {
                DisplayNode node = (DisplayNode)itor.next();
                if (lastWasNewLine && !(node instanceof SeparatorDisplayNode)) {
                    rv.addAll(DisplayNodeUtils.getIndentNodes(this, childIndent));
                } // endif
                rv.add(node);
                lastWasNewLine = DisplayNodeConstants.CR.equals(node.toString());
            } // endwhile

            return rv;
        } // endif -- indent on

        // indent not on, just return list as passed:
        return statementNodes;
    }

    protected void addIndentedNodes(DisplayNode statementNode,
                                    int childIndent) {
        if (statementNode.hasDisplayNodes()) {
            List statementNodes = statementNode.getDisplayNodeList();
            displayNodeList.addAll(addIndentToRealNodes(statementNodes, childIndent));
        } else {
            if (DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.addAll(DisplayNodeUtils.getIndentNodes(this, childIndent));
            } // endif -- indent on
            displayNodeList.add(statementNode);
        }
    }
}
