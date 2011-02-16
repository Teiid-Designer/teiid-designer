/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.core.TeiidComponentException;
import org.teiid.language.SQLConstants;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.SelectSymbol;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.util.ElementSymbolOptimizer;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.query.ui.UiConstants;

/**
 * The <code>QueryDisplayComponent</code> class is the top-level component for
 * the various types of queries.  The user can setText on this component, and the
 * text will be parsed, reconciled and optimized into the appropriate type of SQL
 * query.  If the string is not recognized, it is stored as "Unknown".  There are
 * various accessor methods to get information about the parsed query.
 */
public class QueryDisplayComponent implements DisplayNodeConstants, UiConstants {

    // ===========================================================================================================================
    // Constants
    
    private static final String DEFAULT_QUERY                       = Util.getString("QueryDisplayComponent.defaultSqlText"); //$NON-NLS-1$
    //private static final String QUERY_PARSABLE_MSG                  = Util.getString("QueryDisplayComponent.queryParsableMsg"); //$NON-NLS-1$
    private static final String QUERY_NOT_PARSABLE_MSG              = Util.getString("QueryDisplayComponent.queryNotParsableMsg"); //$NON-NLS-1$
    private static final String QUERY_PARSABLE_NOT_RESOLVABLE_MSG   = Util.getString("QueryDisplayComponent.queryParsableNotResolvableMsg"); //$NON-NLS-1$
    private static final String QUERY_NOT_VALID_MSG                 = Util.getString("QueryDisplayComponent.queryNotValidMsg"); //$NON-NLS-1$
    //private static final String QUERY_VALID_MSG                     = Util.getString("QueryDisplayComponent.queryValidMsg"); //$NON-NLS-1$
    private static final String RESOLVER_ERROR_MSG                  = Util.getString("QueryDisplayComponent.resolverErrorMsg"); //$NON-NLS-1$
    private static final String EMPTY_SQL_MSG                       = Util.getString("QueryDisplayComponent.emptySqlMsg"); //$NON-NLS-1$
    private static final String DEFAULT_SQL_MSG                     = Util.getString("QueryDisplayComponent.defaultSqlMsg"); //$NON-NLS-1$
    
    private static final String MONITOR_CHECKING_DISPLAY_NODES      = Util.getString("QueryDisplayComponent.checkingDisplayNodes"); //$NON-NLS-1$
    private static final String MONITOR_VALIDATING_SQL              = Util.getString("QueryDisplayComponent.validatingSQL"); //$NON-NLS-1$
    private static final String MONITOR_PREPARING_RESULTS           = Util.getString("QueryDisplayComponent.preparingResults"); //$NON-NLS-1$
    // ===========================================================================================================================
    // Variables
    
	/** QueryValidator **/
    private QueryValidator queryValidator = null;
	/** External MetadataMap, for resolving SQL */
    //private Map externalMetadataMap = Collections.EMPTY_MAP;
    private int queryType = QueryValidator.SELECT_TRNS;
    private QueryValidationResult validationResult;
    
	/** Parsable Status */
    private boolean isParsable = false;
	/** Resolvable Status */
    private boolean isResolvable = false;
	/** Validatable Status */
    private boolean isValidatable = false;
    
	/** Use External Map status */
    //private boolean useExternalMetadataMap = false;

	/** StatusMessage for the current displayComponent */
    private String statusMessage = null;
	/** Highlight startIndex for error highlighting */
    private int errorStart = -1;
	/** Highlight endIndex for error highlighting */
    private int errorEnd = -1;
    
	/** DisplayNode for the display Component */
    private DisplayNode sqlDisplayNode = null;
    
	/** Command LanguageObject corresponding to the current DisplayNode, if any */
    private Command command = null;
    
	/** Optimizer Enabled status */
	private boolean optimizerEnabled = true;
	/** Optimizer On status */
	private boolean optimizerOn = false;
	/** Optimized status */
	private boolean isOptimized = false;
    
    private PropertyChangeSupport propChgSupport = new PropertyChangeSupport(this);
    
    // -------------------------------------------------------------------------------------------------------------------
    // DEFECT 23230
    // We need to cache the sqlText here to maintain the last setText() value
    // -------------------------------------------------------------------------------------------------------------------
    private String sqlText = null;
    //private static int nValidations = 0;
    
    // ===========================================================================================================================
    // Constructors

    /**
     *  QueryDisplayComponent constructor
     *  @param resolver the resolver to use in resolving the query.
     */
    public QueryDisplayComponent(QueryValidator queryValidator, int type) {
        this.queryValidator = queryValidator;
        this.queryType = type;
    }

    // ===========================================================================================================================
    // Methods
    
    public void addPropertyListener(PropertyChangeListener listener) {
        this.propChgSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyListener(PropertyChangeListener listener) {
        this.propChgSupport.removePropertyChangeListener(listener);
    }
    
    /**
     *   Set the QueryValidator
     * @param validator the query validator
     */
    public void setQueryValidator(QueryValidator validator) {
        this.queryValidator = validator;
        this.validationResult = null;
    }
    
    private void setText(String sqlString) {
        setText(sqlString, true, null, new NullProgressMonitor());
    }

    /**
     *   Set the text for this QueryDisplayComponent.
     * @param sqlString the SQL text string
     */
    public void setText(String sqlString, boolean doResolveAndValidate, QueryValidationResult theResult) {
        this.setText(sqlString, doResolveAndValidate, theResult, new NullProgressMonitor());
    }
    
    /**
     *   Set the text for this QueryDisplayComponent.
     * @param sqlString the SQL text string
     */
    public void setText(String sqlString, boolean doResolveAndValidate, QueryValidationResult theResult, IProgressMonitor monitor) {
        // reset isOptimized flag
        this.isOptimized = false;
        
        // -------------------------------------------------------------------------------------------------------------------
        // DEFECT 23230
        // Added String check here to prevent unnecessary validation if SQL doesn't change.
        // -------------------------------------------------------------------------------------------------------------------
        boolean setSqlText = false;
        
        if( isEmptySql(sqlText) || // sqlText == null || sqlText.length() == 0 || 
            !sqlText.equalsIgnoreCase(sqlString) ) {
            setSqlText = true;
        }
        if( setSqlText ) {
            sqlText = sqlString;
            // this does full validation and resets internal variables
            validateSql(sqlString, doResolveAndValidate, theResult, monitor);
            
            // determine if optimization is required
            if(this.isResolvable) {
                if(isOptimizerEnabled()) {
                    if(isOptimizerOn()) {
                        optimize();
                    } else {
                        deoptimize();
                    }
                } else {
                    String startingSQL = getCommand().toString();
                    ElementSymbolOptimizer.fullyQualifyElements(getCommand());
                    // revalidate if sql has changed
                    if(startingSQL!=null && !startingSQL.equalsIgnoreCase(getCommand().toString())) {
                        validateSql(getCommand().toString(), doResolveAndValidate, theResult, monitor);
                    }
                }
            }
        }
    }
    
    public void reset() {
        this.sqlDisplayNode = null;
        this.sqlText = null;
    }
    
    private void validateSql(String inputSqlString, boolean doResolveAndValidate, QueryValidationResult theResult) {
        this.validateSql(inputSqlString, doResolveAndValidate, theResult, new NullProgressMonitor());
    }
    
    private void validateSql(String inputSqlString, boolean doResolveAndValidate, QueryValidationResult theResult, IProgressMonitor monitor) {
        //Stopwatch watch = new Stopwatch();
        //watch.start();
        // -------------------------------------------------------------------------------------------------------------------
        // DEFECT 23230
        // SQL Editor input may be NULL, so the query validator may be null
        // -------------------------------------------------------------------------------------------------------------------
        
        String sqlString = inputSqlString;
        
        if( sqlString == null || isEmptySql(sqlString)) {
            sqlString = BLANK;
        }
        
        if( queryValidator == null || !queryValidator.isValidRoot()) {
            // Defect 23421 - in the case of a "delete operation", the sqlString is set to "" and we need to create a display
            // node to update/clear the sql panel text.
            sqlDisplayNode = DisplayNodeFactory.createUnknownQueryDisplayNode(null,BLANK);
            return;
        }

        monitor.subTask(MONITOR_CHECKING_DISPLAY_NODES);
        monitor.worked(5);
        //nValidations++;
        // Replace sqlString with the combined toSting() values of sqlDisplayNode's display node list, replacing the visible
        // nodes' text with the current value of sqlString.
        if (!sqlString.trim().toUpperCase().startsWith(SQLConstants.Reserved.CREATE) && this.sqlDisplayNode != null) {
            StringBuffer text = new StringBuffer();
            boolean replaced = false;
            for (Iterator iter = this.sqlDisplayNode.getDisplayNodeList().iterator(); iter.hasNext();) {
                DisplayNode node = (DisplayNode)iter.next();
                if (node.isVisible()) {
                    if (!replaced) {
                        text.append(sqlString);
                        replaced = true;
                    }
                } else {
                    if (!replaced && node.getParent().languageObject instanceof Block && SQLConstants.Reserved.END.equals(node.toString())) {
                        text.append(sqlString);
                    }
                    text.append(node.toString());
                }
            } // for
            sqlString = text.toString();
        }
        Collection statusList = Collections.EMPTY_LIST;
        
        monitor.subTask(MONITOR_VALIDATING_SQL);
        monitor.worked(5);
        
        if( !doResolveAndValidate && theResult != null) {
            this.validationResult = theResult;
        } else {
        	try {
        		this.validationResult = queryValidator.validateSql(sqlString, this.queryType , false);
        	} catch (Exception ex) {
                sqlDisplayNode = DisplayNodeFactory.createUnknownQueryDisplayNode(null,sqlString);
                
	            StringBuffer buff = new StringBuffer("Error encountered while validating the transformation."); //$NON-NLS-1$
	            buff.append(CR+"Please check the Message log for exceptions"); //$NON-NLS-1$
	            statusMessage = buff.toString();
	            
        		UiConstants.Util.log(IStatus.ERROR,ex, statusMessage);
        		
                setCommand(null);

        		return;
        	}
        }
        monitor.subTask(MONITOR_PREPARING_RESULTS);
        monitor.worked(20);
        this.isParsable = validationResult.isParsable();
        this.isResolvable = validationResult.isResolvable();
        this.isValidatable = validationResult.isValidatable();
        statusList= validationResult.getStatusList();
        Collection moreStatus = Collections.EMPTY_LIST;
        if( this.queryType == QueryValidator.INSERT_TRNS ) {
        	moreStatus = validationResult.getUpdateStatusList(QueryValidator.INSERT_TRNS);
        } else if( this.queryType == QueryValidator.UPDATE_TRNS ) {
        	moreStatus = validationResult.getUpdateStatusList(QueryValidator.UPDATE_TRNS);
        } else if( this.queryType == QueryValidator.DELETE_TRNS ) {
        	moreStatus = validationResult.getUpdateStatusList(QueryValidator.DELETE_TRNS);
        }
        if( moreStatus != null && !moreStatus.isEmpty() ) {
            statusList = new ArrayList(statusList);
        	statusList.addAll(moreStatus);
        }
        setCommand(validationResult.getCommand());
        
        if(!this.isParsable) {
            setCommand(null);
            sqlDisplayNode = DisplayNodeFactory.createUnknownQueryDisplayNode(null,sqlString);
            
        	// SQL is empty
        	if( isEmptySql(sqlString) ) {
        	  	statusMessage = EMPTY_SQL_MSG;
        	// SQL is default (SELECT * FROM)
        	} else if( isDefaultSql(sqlString) ) {
        		statusMessage = DEFAULT_SQL_MSG;
        	} else {
	            StringBuffer buff = new StringBuffer(QUERY_NOT_PARSABLE_MSG);
	            if(statusList!=null && !statusList.isEmpty()) {
	                Iterator iter = statusList.iterator();
	                while(iter.hasNext()) {
	                    IStatus status = (IStatus)iter.next();
	                    buff.append(CR+status.getMessage());
	                }
	            }
	            statusMessage = buff.toString();
        	}
        } else {
            sqlDisplayNode = DisplayNodeFactory.createDisplayNode(null,getCommand());
            if(!this.isResolvable) {
                StringBuffer buff = new StringBuffer(QUERY_PARSABLE_NOT_RESOLVABLE_MSG);
                if(statusList!=null && !statusList.isEmpty()) {
                    Iterator iter = statusList.iterator();
                    while(iter.hasNext()) {
                        IStatus status = (IStatus)iter.next();
                        buff.append(CR+RESOLVER_ERROR_MSG+COLON+SPACE+status.getMessage());
                    }
                }
                statusMessage = buff.toString();
            } else if(!this.isValidatable) {
                StringBuffer buff = new StringBuffer(QUERY_NOT_VALID_MSG);
                if(statusList!=null && !statusList.isEmpty()) {
                    Iterator iter = statusList.iterator();
                    while(iter.hasNext()) {
                        IStatus status = (IStatus)iter.next();
                        buff.append(CR+status.getMessage());
                    }
                }
                statusMessage = buff.toString();
            } else {
                statusMessage = BLANK;
            }
        }
        sqlDisplayNode.setStartIndex(0);
        this.propChgSupport.firePropertyChange(null, null, null);
        //watch.stopPrintIncrementAndRestart("QueryDisplayComponent.validateSql() took ");
        //System.out.println("   >> SQL = \n " + sqlString);
    }

	/**
	 *  Method to enable or disable the Optimizer.
	 * @param status 'true' to enable the optimizer, 'false' to disable it.
	 */
	public void setOptimizerEnabled(boolean status) {
		this.optimizerEnabled = status;
	}
	
	/**
	 *  Method to toggle the Optimizer on or off.
	 * @param status 'true' to enable the optimizer, 'false' to disable it.
	 */
	public void setOptimizerOn(boolean status) {
		this.optimizerOn = status;
		if(optimizerEnabled) {
			if(optimizerOn) {
				optimize();
			} else{
				deoptimize();
			}
		}
	}
    
	/**
	* Determine if the current SQL is optimized.
	* @return the SQL status - 'true' if optimized, 'false' if not.
	*/
	public boolean isOptimized() {
		return this.isOptimized;
	}

	/**
	 *  Check the Optimizer enabled status.
	 * @return 'true' if the optimizer is enabled, 'false' if not.
	 */
	public boolean isOptimizerEnabled() {
		return this.optimizerEnabled;
	}
	
	/**
	 *  Check the Optimizer On/Off status.
	 * @return 'true' if the optimizer is on, 'false' if not.
	 */
	public boolean isOptimizerOn() {
		return this.optimizerOn;
	}
    
    /**
    * Determine if the optimizer can be used for the current SQL.
    * @return 'true' if the optimizer can be used, 'false' is not.
    */
    public boolean canOptimize() {
    	boolean result = true;
    	// If no Optimizer set or not resolvable, cannot optimize.
    	if(!isResolvable()) {
    		result = false;
    	// If resolvable, check further.  Should not optimze CreateUpdateProcedureCommands
    	} else if (getCommand() instanceof CreateUpdateProcedureCommand) {
    		result = false;
    	} else if (getCommand() instanceof TriggerAction) {
            result = false;
        }
    	return result;
    }

    /**
     *  Get the displayNode representing this QueryDisplayComponent.
     * @return the DisplayNode
     */
    public DisplayNode getDisplayNode() {
        return sqlDisplayNode;
    }

    /**
     *  Get the list of all displayNodes representing this QueryDisplayComponent.
     * @return the DisplayNode List
     */
    public List getDisplayNodeList() {
    	if(sqlDisplayNode!=null) {
        	return sqlDisplayNode.getDisplayNodeList();
    	}
  		return Collections.EMPTY_LIST;
    }

    /**
     *  Get the parsable status for this QueryDisplayComponent.
     * @return 'true' if Parsable, 'false' if not.
     */
    public boolean isParsable() {
        return isParsable;
    }

    /**
     *  Get the resolvable status for this QueryDisplayComponent.
     * @return 'true' if Resolvable, 'false' if not.
     */
    public boolean isResolvable() {
        return isResolvable;
    }

    /**
     *  Get the validatable status for this QueryDisplayComponent.
     * @return 'true' if Validatable, 'false' if not.
     */
    public boolean isValidatable() {
        return isValidatable;
    }

    /**
     *  Check whether this QueryDisplayComponent is the Default Query.
     * @return 'true' if default query, 'false' if not.
     */
    public boolean isDefaultQuery() {
        String str = this.toString();
        return isDefaultSql(str);
    }
    
    /**
     *  Check whether the provided SQL string is the Default Query.
     * @return 'true' if default sql, 'false' if not.
     */
    private boolean isDefaultSql(String sqlString) {
        StringBuffer sb = new StringBuffer(sqlString);
        CoreStringUtil.replaceAll(sb,CR,BLANK);
        CoreStringUtil.replaceAll(sb,TAB,SPACE);
        CoreStringUtil.replaceAll(sb,DBLSPACE,SPACE);
        String newString = sb.toString();
        if(newString!=null && DEFAULT_QUERY!=null && 
           newString.trim().equalsIgnoreCase(DEFAULT_QUERY.trim())) {
            return true;
        }
        return false;
    }
    
    /**
     *  Check whether this QueryDisplayComponent is the Empty Query.
     * @return 'true' if empty query, 'false' if not.
     */
    public boolean isEmptyQuery() {
        String str = this.toString();
        return isEmptySql(str);
    }
    
    /**
     *  Check whether the provided Sql String is the Empty Query.
     * @return 'true' if empty sql, 'false' if not.
     */
    private boolean isEmptySql(String sqlString) {
    	boolean result = false;
    	if(sqlString==null) {
    		result=true;
    	} else {
	        StringBuffer sb = new StringBuffer(sqlString);
	        CoreStringUtil.replaceAll(sb,CR,BLANK);
	        CoreStringUtil.replaceAll(sb,TAB,BLANK);
	        CoreStringUtil.replaceAll(sb,DBLSPACE,SPACE);
	        String newString = sb.toString();
	        if(newString!=null && newString.trim().length()==0) {
	            result = true;
	        }
    	}
        return result;
    }

    /**
     *   Get the number of projected symbols for the current query.
     * @return the number of projected symbols.  
     */
	public int getProjectedSymbolCount() {
	    if(getCommand()!=null) {
	        List symbols = getCommand().getProjectedSymbols();
	        if(symbols!=null) return symbols.size();
	    }
	    return 0;	    
	}
	
    /**
     *   Tests whether this query is a SELECT *.  
     * @return true if the query is a SELECT *, false if not. 
     */
    public boolean isSelectStar() {
        boolean isSelectStar = false;
        if(isParsable && getCommand()!=null && getCommand() instanceof Query) {
            Select select = ((Query)getCommand()).getSelect();
            if(select!=null) {
                isSelectStar = select.isStar();
            }
        }
        return isSelectStar;
    }

    /**
     *   Expand the Query SELECT *.  If the current query is a SELECT *, it is expanded. 
     */
    public void expandSelect() {
    	Command theCommand = getCommand();
        if(isSelectStar() && theCommand instanceof Query) {
            Query query = (Query)theCommand;
            // If ANY of the Select Symbols are Multi-Element Symbols, expand
            boolean expandSelect = false;
            List syms = query.getSelect().getSymbols();
            for(int i=0; i<syms.size(); i++) {
                if(syms.get(i) instanceof MultipleElementSymbol) {
                    expandSelect = true;
                    break;
                }
            }
            // expand Select if required
            if(expandSelect) {
                // Get the list of SELECT symbols
                List symbols = query.getProjectedSymbols();
                StringBuffer selectStr = new StringBuffer(SELECT_STR+SPACE);
                for(int i=0; i<symbols.size(); i++) {
                    if(i!=0) selectStr.append(COMMA+SPACE);
                    String symbolName = ((SelectSymbol)symbols.get(i)).toString();
                    selectStr.append(symbolName);
                }
                if(symbols.size()>0) selectStr.append(SPACE+CR);
                replaceSelect(selectStr.toString());
            }
        }
    }

    /**
     *   Test whether the query at the supplied cursor location can be expanded.  If the
     * cursor is not within a query, returns false.
     * @param theIndex the cursor index to test.
     * @return true if the query can be expanded, false if not. 
     */
    public boolean canExpandSelect(int index) {
        boolean canExpand = false;
        // Get the command displayNode at the cursor index
        DisplayNode commandNode = getCommandDisplayNodeAtIndex(index);
        if(isParsable && commandNode!=null) {
        	// command DisplayNode is a QueryDisplayNode
            if ( commandNode instanceof QueryDisplayNode ) {
                canExpand = canExpand((Query)commandNode.getLanguageObject());
        	// command DisplayNode is a SetQueryDisplayNode (Union)
            } else if ( commandNode instanceof SetQueryDisplayNode ) {
            	// Get the query at the index
            	QueryDisplayNode qdn = ((SetQueryDisplayNode)commandNode).getQueryAtIndex(index);
            	if(qdn!=null) {
                	canExpand = canExpand((Query)qdn.getLanguageObject());
            	}
            }
        }
        return canExpand;
    }

    /**
     *   Expand the Query that the cursor is in.  If the cursor is not within a query,
     * no action will be taken.
     * @param index the cursor index.
     */
    public void expandSelect(int index) {
    	if( !canExpandSelect(index) ) {
    		return;
    	}
        
        DisplayNode commandDisplayNode = getCommandDisplayNodeAtIndex(index);
        if(commandDisplayNode!=null && commandDisplayNode instanceof QueryDisplayNode) {
        	QueryDisplayNode queryDisplayNode = (QueryDisplayNode)commandDisplayNode;
            SelectDisplayNode selectDisplayNode = 
            	(SelectDisplayNode)queryDisplayNode.getClauseDisplayNode(SELECT);
            Query query = (Query)queryDisplayNode.getLanguageObject();

            // expand Select 
            // Get the list of SELECT symbols
            List symbols = query.getProjectedSymbols();
            StringBuffer selectStr = new StringBuffer(SELECT_STR+SPACE);
            for(int i=0; i<symbols.size(); i++) {
                if(i!=0) selectStr.append(COMMA+SPACE);
                String symbolName = ((SelectSymbol)symbols.get(i)).toString();
                selectStr.append(symbolName);
            }
            if(symbols.size()>0) selectStr.append(SPACE+CR);
            int startIndex = selectDisplayNode.getStartIndex();
            int endIndex = selectDisplayNode.getEndIndex();
            replace(startIndex, endIndex+1, selectStr.toString());
        }
    }

    /**
     * Get the current Query command.
     * @return the query command.
     */
    public Command getCommand() {
        return command;
    }

    private void setCommand(Command theCommand) {
        command = theCommand;
    }

    /**
     * Get the current Status message string for this display component.
     * @return the error message.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Get the start index for this QueryDisplayComponent.
     * @return the starting index.
     */
    public int getErrorStart() {
        return errorStart;
    }

    /**
     * Get the ending index for this QueryDisplayComponent.
     * @return the ending index.
     */
    public int getErrorEnd() {
        return errorEnd;
    }

    /**
     * Returns the query clause DisplayNode at a given index.  If this component is not
     * a QueryDisplayNode or SetQueryDisplayNode, will return null.
     * @param the cursor index.
     * @return the query Clause that the cursor is within, null if not.
     */
    public DisplayNode getQueryClauseAtIndex(int index) {
        DisplayNode node;
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode) {
            node =  ((QueryDisplayNode)sqlDisplayNode).getClauseAtIndex(index);
        } else if(sqlDisplayNode!=null && sqlDisplayNode instanceof SetQueryDisplayNode) {
            node = ((SetQueryDisplayNode)sqlDisplayNode).getClauseAtIndex(index);
        } else {
            node = null;
        }
        if (node == null) {
            UiConstants.Util.log(IStatus.ERROR, null, "DisplayNode: " + sqlDisplayNode.getClass() + "generated null result at index " + index); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return node;
    }
    
    /**
     * Returns the DisplayNode for the command at a given index.  If the index is within
     * a SubQuery, the subQuery is returned.  If not within a SubQuery, the Command that the
     * index is within is returned.
     * @param index the cursor index.
     * @return the command DisplayNode that the cursor is within.
     */
    public DisplayNode getCommandDisplayNodeAtIndex(int index) {
        DisplayNode commandDisplayNode = null;
        List nodes = getDisplayNodesAtIndex(index);
        //--------------------------------------------------
        // Index is between nodes, look at both
        //--------------------------------------------------
        if(nodes.size()==2) {
            // Get the index nodes
            DisplayNode node1 = (DisplayNode)nodes.get(0);
            DisplayNode node2 = (DisplayNode)nodes.get(1);
            // If second node is within a SubQuery, return SubQuery node
            if(DisplayNodeUtils.isWithinSubQueryNode(node2)) {
            	commandDisplayNode = DisplayNodeUtils.getSubQueryCommandDisplayNode(node2);
            // If first node is within a SubQuery, return SubQuery node
            } else if(DisplayNodeUtils.isWithinSubQueryNode(node1)) {
            	commandDisplayNode = DisplayNodeUtils.getSubQueryCommandDisplayNode(node1);
            // Otherwise, return command for either node, looking at second node first
            } else {
            	commandDisplayNode = DisplayNodeUtils.getCommandForNode(node2);
                // If node2 resulted in null, try first node
                if(commandDisplayNode==null) {
                    commandDisplayNode = DisplayNodeUtils.getCommandForNode(node1);
                }
            }
        //--------------------------------------------------
        // Index is within a node
        //--------------------------------------------------
        } else if(nodes.size()==1) {
        	// return command for the node
            commandDisplayNode = DisplayNodeUtils.getCommandForNode((DisplayNode)nodes.get(0));
        } 
        
        return commandDisplayNode;
    }
    
    /**
     * Returns the QueryDisplayNode index for a given cursor index. Will return -1 if the
     * index is not within a QueryDisplayNode.
     * @param the cursor index.
     * @return the query index that the cursor is within, -1 if not within a query.
     */
    public int getQueryIndex(int index) {
        QueryDisplayNode queryDisplayNode = null;
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode) {
            // If a query, will have one QueryDisplayNode
            queryDisplayNode = (QueryDisplayNode)sqlDisplayNode;
            if(queryDisplayNode.isAnywhereWithin(index)) {
                return 0;
            }
        } else if(sqlDisplayNode!=null && sqlDisplayNode instanceof SetQueryDisplayNode) {
            // If a set query, will have one SetQueryDisplayNode
            SetQueryDisplayNode setQueryDisplayNode = (SetQueryDisplayNode)sqlDisplayNode;
            return setQueryDisplayNode.getQueryIndex(index);
        } 
        return -1;
    }

    /**
     * Returns the QueryDisplayNode, given a queryIndex, or null if the index is invalid.
     * @param the query index.
     * @return the QueryDisplayNode for the provided queryIndex.
     */
    public QueryDisplayNode getQueryDisplayNode(int queryIndex) {
        QueryDisplayNode queryDisplayNode = null;
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode && queryIndex==0) {
            // If a query, will have one QueryDisplayNode
            queryDisplayNode = (QueryDisplayNode)sqlDisplayNode;
        } else if(sqlDisplayNode!=null && sqlDisplayNode instanceof SetQueryDisplayNode) {
            // If a set query, will have one SetQueryDisplayNode
            SetQueryDisplayNode setQueryDisplayNode = (SetQueryDisplayNode)sqlDisplayNode;
            // Get the queryIndex Query from the SetQuery
            queryDisplayNode = setQueryDisplayNode.getQueryDisplayNode(queryIndex);
        }
		return queryDisplayNode;
    }

    /**
     * Returns a list of DisplayNodes (potentially 2) at a given cursor index
     */
    public List getDisplayNodesAtIndex(int index) {
    	List allNodes = getDisplayNodeList();
        return DisplayNodeUtils.getDisplayNodesAtIndex(allNodes,index);
    }

    /**
     * Returns the DisplayNode for the Select if there is one, null if not
     */
    public SelectDisplayNode getSelectDisplayNode( ) {
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode) {
            // If a query, will have one QueryDisplayNode
            QueryDisplayNode queryDisplayNode = (QueryDisplayNode)sqlDisplayNode;
            return (SelectDisplayNode)queryDisplayNode.getClauseDisplayNode(SELECT);
        }
        return null;
    }
    
    /**
     * Returns the DisplayNode for the Select if there is one, null if not
     * The user supplies a queryIndex (for Union queries) to specify which query they want
     * the select for.  For non-setQuery, only an index of 0 will return non-null value.
     */
    public SelectDisplayNode getSelectDisplayNode(int queryIndex) {
        QueryDisplayNode queryDisplayNode = getQueryDisplayNode(queryIndex);
        
        if(queryDisplayNode!=null) {
            return (SelectDisplayNode)queryDisplayNode.getClauseDisplayNode(SELECT);
        }
        return null;
    }

    /**
     * Returns the DisplayNode for the From if there is one, null if not
     */
    public FromDisplayNode getFromDisplayNode( ) {
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode) {
            // If a query, will have one QueryDisplayNode
            QueryDisplayNode queryDisplayNode = (QueryDisplayNode)sqlDisplayNode;
            return (FromDisplayNode)queryDisplayNode.getClauseDisplayNode(FROM);
        }
        return null;
    }
    
    /**
     * Returns the DisplayNode for the From if there is one, null if not
     * The user supplies a queryIndex (for Union queries) to specify which query they want
     * the select for.  For non-setQuery, only an index of 0 will return non-null value.
     */
    public FromDisplayNode getFromDisplayNode(int queryIndex) {
        QueryDisplayNode queryDisplayNode = getQueryDisplayNode(queryIndex);
        
        if(queryDisplayNode!=null) {
            return (FromDisplayNode)queryDisplayNode.getClauseDisplayNode(FROM);
        }
        return null;
    }

    /**
     * Returns the DisplayNode for the Where if there is one, null if not
     */
    public WhereDisplayNode getWhereDisplayNode( ) {
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode) {
            // If a query, will have one QueryDisplayNode
            QueryDisplayNode queryDisplayNode = (QueryDisplayNode)sqlDisplayNode;
            return (WhereDisplayNode)queryDisplayNode.getClauseDisplayNode(WHERE);
        }
        return null;
    }
    
    /**
     * Returns the DisplayNode for the Where if there is one, null if not
     * The user supplies a queryIndex (for Union queries) to specify which query they want
     * the select for.  For non-setQuery, only an index of 0 will return non-null value.
     */
    public WhereDisplayNode getWhereDisplayNode(int queryIndex) {
        QueryDisplayNode queryDisplayNode = getQueryDisplayNode(queryIndex);
        
        if(queryDisplayNode!=null) {
            return (WhereDisplayNode)queryDisplayNode.getClauseDisplayNode(WHERE);
        }
        return null;
    }

    /**
     * Returns the list of DisplayNodes representing SelectElementSymbols
     */
    public List getSelectSymbolDisplayNodes( ) {
        SelectDisplayNode selectNode = getSelectDisplayNode();
        if(selectNode!=null) {
            return selectNode.getChildren();
        }
        return Collections.EMPTY_LIST;
    }
    
    /**
     * Returns the list of DisplayNodes representing SelectElementSymbols
     * The user supplies a queryIndex (for Union queries) to specify which query they want
     * the select for.  For non-setQuery, only an index of 0 will return non-null value.
     */
    public List getSelectSymbolDisplayNodes(int queryIndex) {
        SelectDisplayNode selectNode = getSelectDisplayNode(queryIndex);
        if(selectNode!=null) {
            return selectNode.getChildren();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Determines whether the index is anywhere within a DisplayNode of the specified 
     * type.
     * @param index the cursor index
     * @param nodeType the type of DisplayNode
     * @return true if the cursor index is within the specified type, false if not
     */
    public boolean isIndexWithin(int index, int type) {
    	boolean result = false;
        if(isParsable()) {
        	result = DisplayNodeUtils.isIndexWithin(getDisplayNodeList(),index,type);
        }
        return result;
    }
    
    /**
     * Determines whether the specified type can be inserted at the specified index.
     * @param index the cursor index
     * @param type the type to insert
     * @return true if the specified type can be inserted, false if not
     */
    public boolean isInsertAllowed(int index, int type) {
    	boolean result = false;
        if(isParsable()) {
        	result = DisplayNodeUtils.isInsertAllowed(getDisplayNodeList(),index,type);
        }
        return result;
    }

    /**
     * Determines whether the specified item is supported at the specified index
     * @return true if the item is supported, false if not
     */
    public boolean isSupportedAtIndex(int itemType, int index) {
        // Handle Default Query case
        if(isDefaultQuery()) {
            String currentQuery = this.toString().toUpperCase();
            int endIndexOfSelect = currentQuery.indexOf(SELECT_STR)+SELECT_STR.length();
            int startIndexOfFrom = currentQuery.indexOf(FROM_STR);
            int endIndexOfFrom = currentQuery.indexOf(FROM_STR)+FROM_STR.length();
            if(itemType==ELEMENT && index>=endIndexOfSelect && index<=startIndexOfFrom) {
                return true;
            } else if(itemType==GROUP && index>=endIndexOfFrom) {
                return true;
            } else {
                return false;
            }
        }
        if(!isParsable()) {
            return false;
        }
        DisplayNode node = getQueryClauseAtIndex(index);
        if(node!=null) {
            switch (itemType) {
                case ELEMENT:
                    return node.supportsElement();
                case GROUP:
                    return node.supportsGroup();
                case EXPRESSION:
                    return node.supportsExpression();
                case CRITERIA:
                    return node.supportsCriteria();
                default:
                    break;
            }
        }
        return false;
    }

    /**
    * Method to insert a group symbol string at the specified index.  If the clause
    * that the index is in will not accept a group, nothing is done.
    * @param groupName the new group name to insert
    * @param index the index location to insert the group
    */
    public void insertGroup(String groupName,int index) {
        // Handle Default Query
        if(isDefaultQuery()) {
            setText(DEFAULT_QUERY+groupName);
            return;
        }
        // If index is at invalid location, return without doing anything
        if(!isSupportedAtIndex(GROUP,index)) {
            return;
        }
        DisplayNode clauseNode = getQueryClauseAtIndex(index);
        // Before inserting group into From Clause, check for duplicates
        if(clauseNode instanceof FromDisplayNode) {
            if( !containsGroup((FromDisplayNode)clauseNode, groupName) ) {
                int nodeIndex = getDisplayNodeInsertIndex(index);
                boolean canOptimize = canOptimize();
                if( canOptimize ) deoptimize();
                insertSymbolNameAtNodeIndex(groupName,nodeIndex);
                if( canOptimize ) optimize();
        		// check to make sure it's still resolvable
            }
        } else {
            int nodeIndex = getDisplayNodeInsertIndex(index);
            boolean canOptimize = canOptimize();
            if( canOptimize ) deoptimize();
            insertSymbolNameAtNodeIndex(groupName,nodeIndex);
            if( canOptimize ) optimize();
        }
	}

    /**
    * Method to insert a list of group symbols at the specified index.  If the clause
    * that the index is in will not accept a group, nothing is done.
    * @param groupNames the list of group names to insert
    * @param index the index location to insert the list
    */
    public void insertGroups(List groupNames,int index) {
        // Handle Default Query
        if(isDefaultQuery()) {
            StringBuffer sb = new StringBuffer();
            int nNames = groupNames.size();
            for(int i=0; i<nNames; i++) {
                if(i==0) {
                    sb.append((String)groupNames.get(i));
                } else {
                    sb.append(COMMA+SPACE+(String)groupNames.get(i));
                }
            }
            setText(DEFAULT_QUERY+sb.toString());
            return;
        }
        // Iterate backwards will insert them in the right order
        int nGroups = groupNames.size();
        if(nGroups==0) {
            return;
        }
        for(int i=nGroups-1; i>=0; i--) {
            insertGroup( (String)groupNames.get(i), index );
        }
	}

    /**
    * Method to insert an element symbol string at the specified index.  If the clause
    * that the index is in will not accept an element, nothing is done.
    * @param elementName the new element name to insert
    * @param index the index location to insert the element
    */
    public void insertElement(String elementName,String parentName,int index) {
        // Handle Default Query
        if(isDefaultQuery()) {
            setText(SELECT_STR+SPACE+elementName+SPACE+FROM_STR+SPACE+parentName);
            return;
        }
        // If index is at invalid location, return without doing anything
        if(!isSupportedAtIndex(ELEMENT,index)) {
            return;
        }
        boolean insertAtEndOfSelect = false;
        
        // If the query is a Select *, expand it first
        if(isSelectStar()) {
            // Get the starting index of the *
            int starIndex = 0;
            Iterator iter = getSelectSymbolDisplayNodes().iterator();
            while(iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if(node.getLanguageObject() instanceof Symbol) {
                    LanguageObject langObj = node.getLanguageObject();
                    if(langObj instanceof MultipleElementSymbol) {
                    	starIndex = node.getStartIndex();
                    	break;
                    }
                }
            }
            // If cursor index is after the *, set flag to insert element 
            // at end of Select clause
            if(index>starIndex) {
                insertAtEndOfSelect = true;
            } 
            // Expand the select clause
            expandSelect();
        }
        // Convert the cursor index to a DisplayNode index
        int nodeIndex = getDisplayNodeInsertIndex(index);
        
        // Insert the elements Group at the end of the FROM
        insertGroupAtEndOfFrom(parentName);
        // Reset insertion index if inserting at end of SELECT
        if(insertAtEndOfSelect) {
            index = getSelectDisplayNode().getEndIndex();
            nodeIndex = getDisplayNodeInsertIndex(index);
        }
        
        boolean canOptimize = canOptimize();
        if( canOptimize && isOptimized()) deoptimize();
        insertSymbolNameAtNodeIndex(elementName,nodeIndex);
        if( canOptimize && isOptimizerOn()) optimize();
	}

    /**
    * Method to insert a list of element symbols at the specified index.  If the clause
    * that the index is in will not accept an element, nothing is done.
    * @param elementNames the list of new element names to insert
    * @param parentNames the list of corresponding parentNames for each element
    * @param index the index location to insert the element
    */
    public void insertElements(List elementNames,List parentNames,int index) {
        //----------------------------------------------------------------
        // Handle Default Query
        //----------------------------------------------------------------
        if(isDefaultQuery()) {
            StringBuffer elementSB = new StringBuffer();
            int nNames = elementNames.size();
            for(int i=0; i<nNames; i++) {
                if(i==0) {
                    elementSB.append((String)elementNames.get(i));
                } else {
                    elementSB.append(COMMA+SPACE+(String)elementNames.get(i));
                }
            }
            // Eliminate duplicate parents
            List uniqueGroups = new ArrayList();
            Iterator iter = parentNames.iterator();
            while(iter.hasNext()) {
                String grp = (String)iter.next();
                if( !uniqueGroups.contains(grp) ) {
                    uniqueGroups.add(grp);
                }
            }
            StringBuffer groupSB = new StringBuffer();
            int nGrps = uniqueGroups.size();
            for(int i=0; i<nGrps; i++) {
                if(i==0) {
                    groupSB.append((String)uniqueGroups.get(i));
                } else {
                    groupSB.append(COMMA+SPACE+(String)uniqueGroups.get(i));
                }
            }
            setText("SELECT "+elementSB.toString()+" FROM "+groupSB.toString()); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        //----------------------------------------------------------------
        // Iterate backwards will insert them in the right order
        //----------------------------------------------------------------
        int nElems = elementNames.size();
        int nParents = parentNames.size();
        if(nParents!=nElems || nElems==0) {
            return;
        }
        for(int i=nElems-1; i>=0; i--) {
            insertElement( (String)elementNames.get(i), (String)parentNames.get(i), index );
        }
	}

    /**
     * Returns the String representation for this QueryDisplayComponent.
     * @return the SQL string for this QueryDisplayComponent.
     */
    public String toDisplayString( ) {
        if (this.sqlDisplayNode != null) {
            return this.sqlDisplayNode.toDisplayString();
        }
        return BLANK;
    }

    /**
     * Returns the String representation for this QueryDisplayComponent.
     * @return the SQL string for this QueryDisplayComponent.
     */
    @Override
    public String toString( ) {
    	if (this.sqlDisplayNode != null) {
    		return this.sqlDisplayNode.toString();
    	}
  		return BLANK;
    }
	
    /**
    * Method to optimize this QueryDisplayComponent.  If the optimizer is not set or is
    * disabled, no action is taken.
    */
    public void optimize() {
        if( canOptimize() ) {
            if(!isOptimized) {
                // ---------------------
                // DEFECT 23230
                // Cache the original command string so we can do the "is changed" check
                // ---------------------
                String unoptimizedSql = getCommand().toString();
                
                QueryMetadataInterface resolver = getQueryResolver();
                try {
                    ElementSymbolOptimizer.optimizeElements(getCommand(),resolver);
                } catch (QueryMetadataException e) {
                    e.printStackTrace();
                } catch (TeiidComponentException e) {
                    e.printStackTrace();
                }
                isOptimized=true;
                // --------------------------------------------------------------------------------------------------------------
                // DEFECT 23230
                // Optimization may NOT change the SQL string, so we don't want to do a second validation if the SQL did NOT change
                // --------------------------------------------------------------------------------------------------------------
                if( unoptimizedSql != null && !unoptimizedSql.equalsIgnoreCase(getCommand().toString())) {
                    validateSql(getCommand().toString(), true, null);
                }
            }
        } else {
        	isOptimized=false;
        }
    }

    /**
    * Method to deoptimize this QueryDisplayComponent.  If the optimizer is not set or is
    * disabled, no action is taken.
    */
    public void deoptimize() {
        if( canOptimize() ) {
            if(isOptimized) {
                // ---------------------
                // DEFECT 23230
                // Cache the original command string so we can do the "is changed" check
                // ---------------------
                String optimizedSql = getCommand().toString();
                
                ElementSymbolOptimizer.fullyQualifyElements(getCommand());
                isOptimized = false;
                // --------------------------------------------------------------------------------------------------------------
                // DEFECT 23230
                // deoptimization may NOT change the SQL string, so we don't want to do a second validation if the SQL did NOT change
                // --------------------------------------------------------------------------------------------------------------
                if( optimizedSql != null && !optimizedSql.equalsIgnoreCase(getCommand().toString())) {
                    validateSql(getCommand().toString(), true, null);
                }
            }
        } else {
        	isOptimized=false;
        }
    }

    /**
    * Get the query resolver
    * @return the QueryMetadataInterface implementation
    */
    private QueryMetadataInterface getQueryResolver() {
        QueryMetadataInterface resolver = null;
        if(queryValidator!=null) {
            resolver = queryValidator.getQueryMetadata();
        }
        return resolver;
    }
    
    /**
    * Get the DisplayNode index for inserting the next node, given a cursor index.
    * If the cursor index is between two DisplayNodes, the index of the second is used.
    * @param cursorIndex the supplied cursor index.
    * @return the corresponding DisplayNode index.
    */
    private int getDisplayNodeInsertIndex(int cursorIndex) {
        List allNodes = getDisplayNodeList();
        // Iterate thru all Display Nodes
        for(int i=0; i<allNodes.size(); i++) {
            DisplayNode node = (DisplayNode)allNodes.get(i);
            // Check whether index is in current node
            if(node.isAnywhereWithin(cursorIndex)) {
                return i+1;
            }
        }
        return -1;
    }

    /**
     *   Test whether the supplied query can be expanded.  The Query select must contain
     * a MultiElementSymbol, and must have at least one projected symbol.
     * @param query the query language object to test.
     * @return true if the query can be expanded, false if not. 
     */
	private boolean canExpand(Query query) {
	    boolean canExpand = false;
	    if(query!=null) {
         	Select select = query.getSelect();
         	boolean hasMultiSymbol = false;
         	// Test whether the SELECT has any multi-symbols.
         	if(select!=null) {
                List syms = select.getSymbols();
                for(int i=0; i<syms.size(); i++) {
                    if(syms.get(i) instanceof MultipleElementSymbol) {
                        hasMultiSymbol = true;
                        break;
                    }
                }
         	}
         	// If the SELECT has at least one multi-symbol, test the projected symbols.
         	if(hasMultiSymbol) {
             	List symbols = query.getProjectedSymbols();
             	if(symbols.size()>0) {
             	    canExpand = true;
             	}
         	}
	    }
	    return canExpand;
	}
	
    /**
     * Replaces the specified query index range with the supplied string.
     * @param startIndex the starting index for the string replace.
     * @param endIndex the ending index for the string replace.
     * @param str the string to replace the index range with 
     */
    private void replace(int startIndex, int endIndex, String str) {
        StringBuffer sb = new StringBuffer(this.toString());
        sb.replace(startIndex, endIndex, str);
        setText(sb.toString());
        return;
    }

    /**
     * Replaces the select string for the current query.  If the current query is not a
     * valid QUERY, no action is taken.
     * @param selectStr the new SELECT string for the current query.
     */
    private void replaceSelect(String selectStr) {
        if(sqlDisplayNode!=null && sqlDisplayNode instanceof QueryDisplayNode) {
            StringBuffer sb = new StringBuffer(BLANK);
            // If a query, will have one QueryDisplayNode
            QueryDisplayNode queryDisplayNode = (QueryDisplayNode)sqlDisplayNode;
            // Iterate through the query clauses
            Iterator iter = queryDisplayNode.getChildren().iterator();
            // Rebuild the sql string, replacing the Select
            while( iter.hasNext() ) {
                DisplayNode clauseNode = (DisplayNode)iter.next();
                if(clauseNode.getLanguageObject() instanceof Select) {
                    sb.append(selectStr);
                } else {
                    sb.append(clauseNode.toString());
                }
            }
            setText( sb.toString() );
        }
        return;
    }

    /**
    * Method to insert a group symbol string at the end of the FROM clause (if there
    * is a FROM clause).
    * @param groupName the new group name to insert
    */
    private void insertGroupAtEndOfFrom(String groupName) {
        FromDisplayNode fromNode = getFromDisplayNode();
        if(fromNode!=null) {
            int fromEndIndex = fromNode.getEndIndex();
            insertGroup(groupName,fromEndIndex-1);
        }
	}

    /**
    * Method to determine whether a From clause already contains the specified
    * group symbol name.
    * @param fromClause the From clause display node.
    * @param groupName the group Name to test for.
    */
    private boolean containsGroup(FromDisplayNode fromClause, String groupName) {
        if(fromClause!=null) {
            From from = (From)fromClause.getLanguageObject();
            List groups = from.getGroups();
            Iterator iter = groups.iterator();
            while(iter.hasNext()) {
                GroupSymbol symbol = (GroupSymbol)iter.next();
                if(symbol.getName().equals(groupName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
    * Method to insert a group or element symbol string into the component at
    * the specified index.  This will get the clause and examine where the index
    * location is relative to other symbols.  It will insert the new symbol name
    * into the clause at the appropriate location.
    * @param symbolName the new symbol name to insert
    * @param index the index location to insert the string
    */
    private void insertSymbolName(String symbolName,int index) {
        // Get the Clause at the index
        DisplayNode clauseNode = getQueryClauseAtIndex(index);

        //------------------------------------------------------------
        // Get the DisplayNode at the Index
        //------------------------------------------------------------
        List displayNodes = getDisplayNodesAtIndex(index);
        int nNodes = displayNodes.size();
        DisplayNode displayNode = null;
        if(nNodes==2) {
            // If there are two nodes, take the 1st node, or second if a symbol
            displayNode = (DisplayNode)displayNodes.get(0);
            if( displayNode.getLanguageObject() instanceof Symbol || displayNode.getLanguageObject() instanceof UnaryFromClause) {
                displayNode = (DisplayNode)displayNodes.get(1);
            }
        } else if(nNodes==1) {
            displayNode = (DisplayNode)displayNodes.get(0);
        } else {
            return;
        }

        //------------------------------------------------------------
        // Clause has no Symbols or Expressions
        //------------------------------------------------------------
        if( clauseNode != null && !DisplayNodeUtils.hasSymbol(clauseNode) && !DisplayNodeUtils.hasExpression(clauseNode) ) {
            insertString(COMMA+symbolName+SPACE,displayNode.getEndIndex()+1);
        //------------------------------------------------------------
        // Index is within an Expression
        //------------------------------------------------------------
        } else if ( displayNode.isInExpression() ) {
            DisplayNode expressionNode = DisplayNodeUtils.getExpressionForNode(displayNode);
            int startIndex = expressionNode.getStartIndex();
            if(index==startIndex) {
                insertString(SPACE+symbolName+COMMA,expressionNode.getStartIndex());
            } else {
                insertString(COMMA+symbolName+SPACE,expressionNode.getEndIndex()+1);
            }
        //------------------------------------------------------------
        // Index is within a Symbol
        //------------------------------------------------------------
        } else if (displayNode.getLanguageObject() instanceof Symbol || displayNode.getLanguageObject() instanceof UnaryFromClause) {
            int startIndex = displayNode.getStartIndex();
            if(index==startIndex) {
                insertString(SPACE+symbolName+COMMA,displayNode.getStartIndex());
            } else {
                insertString(COMMA+symbolName+SPACE,displayNode.getEndIndex()+1);
            }
        //------------------------------------------------------------
        // Index is not within a Symbol or Expression
        //------------------------------------------------------------
        } else if( clauseNode != null ) {
            int startOfNextSymbol = DisplayNodeUtils.getStartIndexOfNextSymbol(clauseNode,index);
            int endOfPrevSymbol = DisplayNodeUtils.getEndIndexOfPreviousSymbol(clauseNode,index);
            int startOfNextExpr = DisplayNodeUtils.getStartIndexOfNextExpression(clauseNode,index);
            int endOfPrevExpr = DisplayNodeUtils.getEndIndexOfPreviousExpression(clauseNode,index);
            int startOfNext = getSmallestNonNegative(startOfNextSymbol,startOfNextExpr);
            int endOfPrev = getLargestNonNegative(endOfPrevSymbol,endOfPrevExpr);
            // If theres a symbol or expression before and after it
            if(startOfNext!=-1 && endOfPrev!=-1) {
                insertString(SPACE+symbolName+COMMA,startOfNext);
            // If theres just a symbol or expression after it
            } else if(startOfNext!=-1) {
                insertString(SPACE+symbolName+COMMA,startOfNext);
            // If theres just a symbol or expression before it
            } else if(endOfPrev!=-1) {
                insertString(COMMA+symbolName+SPACE,endOfPrev);
            }
        }
	}
	
    /**
    * Get the smallest non-negative integer of the two supplied.  Returns -1 if
    * both are negative.
    */
	private int getSmallestNonNegative(int intOne, int intTwo) {
	    // Both Negative, return -1
	    if(intOne<0 && intTwo<0) {
	        return -1;
	    } 
	    // At least one non-Negative, return smallest non-zero.
	    int minimum = Math.min(intOne,intTwo);
	    if(minimum<0) {
	    	return Math.max(intOne,intTwo);
	    }
	    return minimum;
	}
	
    /**
    * Get the largest non-negative integer of the two supplied.  Returns -1 if both
    * are negative.
    */
	private int getLargestNonNegative(int intOne, int intTwo) {
	    // Both Negative, return -1
	    if(intOne<0 && intTwo<0) {
	        return -1;
	    // At least one non-Negative, return largest.
	    }
        return Math.max(intOne,intTwo);
	}

    /**
    * Method to insert a group or element symbol string into the component at
    * the specified NODE index.  This will get the clause and examine where the index
    * location is relative to other symbols.  It will insert the new symbol name
    * into the clause at the appropriate location.
    * @param symbolName the new symbol name to insert
    * @param index the Node index location to insert the string
    */
    private void insertSymbolNameAtNodeIndex(String symbolName,int nodeIndex) {
        // Get the text index of the node
        List allNodes = getDisplayNodeList();
        if(nodeIndex<0 || nodeIndex>allNodes.size()) {
            return;
        }
        int index = 0;
        if(nodeIndex==allNodes.size()) {
            DisplayNode node = (DisplayNode)allNodes.get(nodeIndex-1);
            index = node.getEndIndex()-1;
        } else {
            DisplayNode node = (DisplayNode)allNodes.get(nodeIndex);
            index = node.getStartIndex();
        }
        insertSymbolName(symbolName,index);
	}

	/**
	 * Insert a string into the query Editor Panel at the specified index
     * @param str the string to insert
     * @param index the index location to insert the string
	 */
    private void insertString(String str, int index) {
        StringBuffer currentSQL = new StringBuffer(this.toString());
        currentSQL.insert(index,str);

        setText(currentSQL.toString());
    }
    
    /**
     * Method the SqlEditorPanel or others can call to get the actual index of a visible cursor index value. Hidden/invisible nodes
     * may mask the actual index. All the methods in this class assume ALL NODES ARE VISIBLE.
     * @param index
     * @return
     * @since 5.0
     */
    public int getCorrectedIndex(int visibleCursorIndex) {
        int theIndex = visibleCursorIndex;
        
        if (this.sqlDisplayNode != null) {
            for (Iterator iter = this.sqlDisplayNode.getDisplayNodeList().iterator(); iter.hasNext();) {
                DisplayNode node = (DisplayNode)iter.next();
                if (node.isVisible()) {
                    theIndex = visibleCursorIndex + node.getStartIndex();
                    break;
                }
            } // for
        }
        
        return theIndex;
    }
    
    public DisplayNode getFirstVisibleNode() {
        for (Iterator iter = this.sqlDisplayNode.getDisplayNodeList().iterator(); iter.hasNext();) {
            DisplayNode node = (DisplayNode)iter.next();
            if (node.isVisible()) {
                return node;
            }
        } // for
        
        return null;
    }
    
    public EObject getMappingRoot() {
    	if( this.queryValidator != null ) {
    		return this.queryValidator.getTransformationRoot();
    	}
    	
    	return null;
    }
    
    public int getQueryType() {
    	return this.queryType;
    }
}
