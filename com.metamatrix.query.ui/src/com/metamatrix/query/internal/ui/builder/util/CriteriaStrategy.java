/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.builder.util;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com.metamatrix.api.exception.query.QueryParserException;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.ui.UiConstants;

/**
 * @author Dan Florian
 * @since 3.1
 * @version 1.0
 */
public class CriteriaStrategy implements ICriteriaStrategy {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected static final String INVALID_SELECTION_MSG = UiConstants.Util.getString("CriteriaStrategy.invalidSelectionMsg"); //$NON-NLS-1$ 
    protected static final String SUBQUERY_SELECT_STATEMENT = "SELECT {0} FROM {1}";  //$NON-NLS-1$ this is not a message.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected TreeViewer viewer;
    protected ILabelProvider labelProvider;
    protected ITreeContentProvider contentProvider;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * If this constructor is used, the <code>TreeViewer</code> must be set prior to using this
     * strategy.
     */
    public CriteriaStrategy() {
    }
    
    public CriteriaStrategy(TreeViewer treeViewer) {
        setTreeViewer(treeViewer);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Checks to see if the strategy is in a complete state.
     * @throws IllegalStateException if the viewer, content provider, or label provider is null
     */
    private void checkState() {
        if ((viewer == null) || (labelProvider == null) || (contentProvider == null)) {
            throw new IllegalStateException(UiConstants.Util.getString("CriteriaStrategy.invalidStateMsg")); //$NON-NLS-1$);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.toolbox.ui.query.builder.criteria.SubqueryCommandOwner#getCommand(com.metamatrix.common.tree.TreeNode)
     */
    public Command getCommand(Object theNode) {
        checkState();
        
        Command result = null;
        
        if (isValid(theNode)) {
        	String nodeName = getRuntimeFullName(theNode);
        	String parentName = getRuntimeFullName(contentProvider.getParent(theNode));
            String sql = MessageFormat.format(SUBQUERY_SELECT_STATEMENT,
                              new Object[] {nodeName, parentName});
            result = parseSql(sql);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.toolbox.ui.query.builder.criteria.SubqueryCommandOwner#getInvalidMessage(com.metamatrix.common.tree.TreeNode)
     */
    public String getInvalidMessage(Object theNode) {
        return INVALID_SELECTION_MSG;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.util.ICriteriaStrategy#getTreeViewer()
     */
    public TreeViewer getTreeViewer() {
        return viewer;
    }

    /**
     * Uses the TreeViewer's ITreeContentProvider and ILabelProvider to build a full name.
     * @see com.metamatrix.toolbox.ui.query.builder.criteria.CriteriaStrategy#getRuntimeFullName(com.metamatrix.common.tree.TreeNode)
     */
    public String getRuntimeFullName(Object theNode) {
        checkState();

        String result = labelProvider.getText(theNode);
        while (true) {
            
            Object parent = contentProvider.getParent(theNode);
            if ( parent == null ) {
                break;
            }
            result = labelProvider.getText(parent) + getDelimiterChar() + result;
            theNode = parent;                
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.util.ICriteriaStrategy#getNode(com.metamatrix.query.sql.LanguageObject)
     */
    public Object getNode(LanguageObject theLangObj) {
        Object result = null;

        if (isValid(theLangObj)) {
            if (theLangObj instanceof ElementSymbol) {
                result = ((ElementSymbol)theLangObj).getMetadataID();
            }
        }

        return result;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.toolbox.ui.query.builder.criteria.SubqueryCommandOwner#isValid(com.metamatrix.common.tree.TreeNode)
     */
    public boolean isValid(Object theNode) {
        checkState();
        
        return ((theNode == null) || (viewer == null) || (viewer.getContentProvider() == null))
            ? false
            : ! ((ITreeContentProvider) viewer.getContentProvider()).hasChildren(theNode);
    }

    protected Command parseSql(String theSql) {
        Command command = null;

        if (theSql != null) {
            try {
                // QueryParser is not thread-safe, construct new
                QueryParser parser = new QueryParser();
                command = parser.parseCommand(theSql);
            }
            catch (QueryParserException theException) {
                // No need to act on this or log message.  Null return means it failed.
            }
        }

        return command;
    }
    
    public void setTreeViewer(TreeViewer theViewer) {
        viewer = theViewer;
        labelProvider =  (ILabelProvider) getTreeViewer().getLabelProvider();
        contentProvider = (ITreeContentProvider) getTreeViewer().getContentProvider();
    }
    
    protected char getDelimiterChar() {
        return '.';
    }
    
}
