/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.IToken;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;

/**
 * The <code>DisplayNode</code> class is the base class used by <code>QueryDisplayComponent</code> to represent all types of
 * Display Nodes.
 *
 * @since 8.0
 */
public class DisplayNode implements DisplayNodeConstants {

    // /////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////

    protected int startIndex = 0;
    protected int endIndex = 0;
    protected DisplayNode parentNode = null;
    protected ILanguageObject languageObject = null;
    protected List<DisplayNode> childNodeList = new ArrayList(1);
    protected List<DisplayNode> displayNodeList = new ArrayList(1);
    protected List<CommentDisplayNode> commentNodeList = new ArrayList<CommentDisplayNode>(1);
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
    public ILanguageObject getLanguageObject() {
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
     * @return all the comments surrounding this node tree
     */
    public List<CommentDisplayNode> getCommentNodeList() {
        return commentNodeList;
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

    private String escape(String text, String character, boolean optional) {
        String target = ESCAPE + character;
        String replacement = REGEX_ESCAPE + character + (optional ? QMARK : BLANK);
        text = text.replaceAll(target, replacement);
        return text;
    }

    private int nextAvailableSpace(String sql, int index) {
        for (int i = index - 1; i < sql.length(); ++i) {
            char c = sql.charAt(i);

            if (System.lineSeparator().equals(Character.toString(c))) {
                // Character is a newline
                return i + 1;
            }
        }

        return sql.length();
    }

    private int calculateLocation(String sql, CommentDisplayNode comment) {
        int cmtIdx = comment.getOffset();
        LinkedList<IToken> preTokens = new LinkedList(comment.getPreTokens());

        if (preTokens.isEmpty())
            return cmtIdx; // can happen if offset is 0 and comment is at the start

        // i : case-insensitive mode
        // s: include line terminators in . matchings
        StringBuffer regex = new StringBuffer("(?is)"); //$NON-NLS-1$
        Iterator<IToken> iterator = preTokens.iterator();
        while(iterator.hasNext()) {
            IToken token = iterator.next();
            ISQLStringVisitor visitor = ModelerCore.getTeiidQueryService().getSQLStringVisitor();
            String text = visitor.displayName(token);

            // Must escape question marks first since optional is represented by question marks in regex
            text = escape(text, QMARK, false);

            text = escape(text, SPEECH_MARK, true);
            text = escape(text, QUOTE, true);
            text = escape(text, LTPAREN, true);
            text = escape(text, RTPAREN, true);

            // The zero at the end of a time is optional, eg. 19:00:02.50, so
            // gets dropped by the production. Thus, need to make it
            // optional. Have to do it prior to escaping DOT, otherwise the
            // replace regex becomes consumed by backslashes!
            String target = "([0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9])0"; //$NON-NLS-1$
            String replacement = "$1[0-9]?'"; //$NON-NLS-1$
            text = text.replaceAll(target, replacement);

            // The source hint is parseable as /*+ sh ... */ but all spaces are removed
            // between the + and sh upon conversion.
            target = "\\/\\*\\+\\s*sh"; //$NON-NLS-1$
            replacement = "/*+sh"; //$NON-NLS-1$
            text = text.replaceAll(target, replacement);

            text = escape(text, DOT, false);
            text = escape(text, LTBRACE, false);

            // Makes right brace optional since fn{ is dropped as the prefix of functions
            // and its closing brace will remain as a pre token
            text = escape(text, RTBRACE, true);
            text = escape(text, FORWARD_SLASH, false);
            text = escape(text, STAR, false);
            text = escape(text, PLUS, false);
            text = escape(text, PIPE, false);

            regex.append(text);

            if (iterator.hasNext())
                regex.append(DOT).append(STAR).append(QMARK);
        }

        Pattern pattern = Pattern.compile(regex.toString());
        Matcher matcher = pattern.matcher(sql);

        //
        // Find ALL the possible sub sequences that the matcher could match.
        // Most of the time it should be only one but sometimes a SELECT subquery
        // could have the same WHERE condition as the outer master query.
        // WITH x AS (SELECT a FROM db.g WHERE b = aString /* Comment 1 */)
        //                     SELECT a FROM db.g WHERE b = aString /* Comment 2 */
        List<Integer> results = new ArrayList<Integer>();
        boolean result = true;
        while (result) {
            result = matcher.find();
            if (result) {
                // We know that the matcher has succeeded once but is possible that
                // since its a reluctant matcher it is in fact finding a result far earlier
                // than required.
                results.add(matcher.end());
            }
        }

        if (results.isEmpty())
            return -1;

        //
        // Loop through the candidates and choose the pair {start, end} which has an end
        // value closest to the offset of the comment
        //
        Integer pref = results.get(0);
        int diff = Math.abs(pref - cmtIdx);
        for (Integer poss : results) {
            int pdiff = Math.abs(poss - cmtIdx);
            if (pdiff < diff)
                pref = poss;
        }

        int offset = pref + 1;
        if (offset != cmtIdx)
            cmtIdx = offset;

        //
        // At this point, we have a location index for the comment.
        // However, its still possible, despite best efforts that that index
        // is within a term. Thus, check the index finally...
        //
        return nextAvailableSpace(sql, cmtIdx);
    }

    private int findIndentLevel(String sql, int index) {
        // Find the next set of tabs
        int nextTabs = 0;
        for (int i = index; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if ('\t' == c)
                nextTabs++;
            else if (nextTabs > 0)
                // found all the next tabs available
                break;
        }

        // The index position is counted in the 'next' count above
        // so this needs to use the previous index. However, still
        // want any trailing comments to be non-tabbed so use
        // index rather than i to test against sql.length()
        int prevTabs = 0;
        for (int i = index - 1; i > 0 && index < sql.length(); --i) {
            char c = sql.charAt(i);
            if ('\t' == c)
                prevTabs++;
            else if (prevTabs > 0)
                // found all the prev tabs available
                break;
        }

        return Math.max(nextTabs, prevTabs);
    }

    private void addComments(StringBuffer buf) {
        for (CommentDisplayNode comment : commentNodeList) {
            String text = buf.toString();
            int insertIdx = calculateLocation(text, comment);
            if (insertIdx == -1)
                continue;

            int indentLevel = findIndentLevel(text, insertIdx);

            // Handling trailing comments
            if (insertIdx >= text.length()) {
                // insert index is the end of the sql string
                if (! text.endsWith(CR))
                    buf.append(CR);

                // Add in tabs prior to comment
                for (int i = 0; i < indentLevel; ++i)
                    buf.append(TAB);

                buf.append(comment.toDisplayString());

                if (! comment.isMultiLine())
                    buf.append(CR);

            } else {
                // Most of the comments with be dealt with here

                // Add in tabs prior to comment
                StringBuffer cmt = new StringBuffer();
                for (int i = 0; i < indentLevel; ++i)
                    cmt.append(TAB);

                cmt.append(comment.toDisplayString());
                cmt.append(CR);

                buf.insert(insertIdx, cmt.toString());
            }
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

        addComments(sb);

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

        addComments(sb);

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
            if (parentNode.languageObject != null && parentNode.languageObject instanceof IExpression) {
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
            if (parentNode.languageObject instanceof ICriteria) {
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

    /**
     * @param commentNode
     */
    public void addCommentNode(CommentDisplayNode commentNode) {
        commentNodeList.add(commentNode);
    }

}
