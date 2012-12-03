/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.util;

import java.text.MessageFormat;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.ui.UiConstants;

/**
 * @author Dan Florian
 * @since 8.0
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
    @Override
	public ICommand getCommand(Object theNode) {
        checkState();
        
        ICommand result = null;
        
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
    @Override
	public String getInvalidMessage(Object theNode) {
        return INVALID_SELECTION_MSG;
    }
    
    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.util.ICriteriaStrategy#getTreeViewer()
     */
    @Override
	public TreeViewer getTreeViewer() {
        return viewer;
    }

    /**
     * Uses the TreeViewer's ITreeContentProvider and ILabelProvider to build a full name.
     * @see CriteriaStrategy#getRuntimeFullName(Object)
     */
    @Override
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
     * @see org.teiid.query.ui.builder.util.ICriteriaStrategy#getNode(org.teiid.query.sql.LanguageObject)
     */
    @Override
	public Object getNode(ILanguageObject theLangObj) {
        Object result = null;

        if (isValid(theLangObj)) {
            if (theLangObj instanceof IElementSymbol) {
                result = ((IElementSymbol)theLangObj).getMetadataID();
            }
        }

        return result;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.toolbox.ui.query.builder.criteria.SubqueryCommandOwner#isValid(com.metamatrix.common.tree.TreeNode)
     */
    @Override
	public boolean isValid(Object theNode) {
        checkState();
        
        return ((theNode == null) || (viewer == null) || (viewer.getContentProvider() == null))
            ? false
            : ! ((ITreeContentProvider) viewer.getContentProvider()).hasChildren(theNode);
    }

    protected ICommand parseSql(String theSql) {
        ICommand command = null;

        if (theSql != null) {
            try {
                // QueryParser is not thread-safe, construct new
                IQueryParser parser = ModelerCore.getTeiidQueryService().getQueryParser();
                command = parser.parseCommand(theSql);
            }
            catch (Exception theException) {
                // No need to act on this or log message.  Null return means it failed.
            }
        }

        return command;
    }
    
    @Override
	public void setTreeViewer(TreeViewer theViewer) {
        viewer = theViewer;
        labelProvider =  (ILabelProvider) getTreeViewer().getLabelProvider();
        contentProvider = (ITreeContentProvider) getTreeViewer().getContentProvider();
    }
    
    protected char getDelimiterChar() {
        return '.';
    }
    
}
