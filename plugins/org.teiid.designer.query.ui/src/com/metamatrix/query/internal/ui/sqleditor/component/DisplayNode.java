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
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.symbol.Expression;

/**
 * The <code>DisplayNode</code> class is the base class used by <code>QueryDisplayComponent</code> to represent all types of
 * Display Nodes.
 */
public class DisplayNode implements DisplayNodeConstants {

    // /////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////

    protected int startIndex = 0;
    protected int endIndex = 0;
    protected DisplayNode parentNode = null;
    protected LanguageObject languageObject = null;
    protected List<DisplayNode> childNodeList = new ArrayList(1);
    protected List<DisplayNode> displayNodeList = new ArrayList(1);
    private boolean visible = true;

    protected DisplayNode() {

    }

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
     * @param visible <code>true</code> if this display node is visible within it's containing UI component.
     * @param includeDescendents <code>true</code> if the visibility of this node's descendents should also be affected.
     * @since 5.0.1
     */
    public void setVisible( boolean visible,
                            boolean includeDescendents ) {
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
        return (childNodeList != null && childNodeList.size() > 0) ? true : false;
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
     * Determine if the DisplayNode supports expressions in it.
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
    public DisplayNode getExpression() {
        DisplayNode parentNode = this;
        while (parentNode != null) {
            if (parentNode.languageObject instanceof Expression) {
                return parentNode;
            }
            parentNode = parentNode.getParent();
        }
        return null;
    }

    /**
     * Determine if the DisplayNode supports criteria in it.
     */
    public boolean supportsCriteria() {
        return isInCriteria();
    }

    /**
     * Determine if the DisplayNode is within a criteria. Checks whether the parentNode is a CriteriaDisplayNode.
     */
    public boolean isInCriteria() {
        return getCriteria() != null;
    }

    /**
     * Get CriteriaDisplayNode
     */
    public DisplayNode getCriteria() {
        DisplayNode parentNode = this;
        while (parentNode != null) {
            if (parentNode.languageObject instanceof Criteria) {
                return parentNode;
            }
            parentNode = parentNode.getParent();
        }
        return null;
    }

    /**
     * Sets the starting index for this node and reindex everything under it.
     */
    public int setStartIndex( int index ) {
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
    private void reindexParents( DisplayNode node ) {
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
    public boolean isIndexAtStart( int index ) {
        return (index != startIndex) ? false : true;
    }

    /**
     * Returns true if index is at the end of the display node
     */
    public boolean isIndexAtEnd( int index ) {
        return (index != (endIndex + 1)) ? false : true;
    }

    /**
     * Returns true if index is anywhere within the display node, including the start and end position
     */
    public boolean isAnywhereWithin( int index ) {
        return (index >= startIndex && index <= (endIndex + 1)) ? true : false;
    }

    /**
     * Returns true if index is anywhere within the display node, NOT including the start and end position
     */
    public boolean isWithin( int index ) {
        return (index > startIndex && index < (endIndex + 1)) ? true : false;
    }

    protected void addChildNode( DisplayNode child ) {
        childNodeList.add(child);
        displayNodeList.add(child);
    }

}
