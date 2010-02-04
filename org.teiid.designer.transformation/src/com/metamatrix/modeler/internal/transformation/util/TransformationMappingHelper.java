/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.query.QueryMetadataException;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.query.function.FunctionDescriptor;
import com.metamatrix.query.function.FunctionLibrary;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.metadata.TempMetadataID;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.SetQuery;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.Function;
import com.metamatrix.query.sql.symbol.GroupSymbol;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.query.sql.visitor.FunctionCollectorVisitor;

/**
 * TransformationMappingHelper
 * This class is responsible for handling mapping changes and source / target
 * changes, in response to SQL changes.
 */
public class TransformationMappingHelper implements SqlConstants {

    private static final TransformationMappingHelper INSTANCE = new TransformationMappingHelper();

    //private static final String TYPE_FEATURE = "type"; //$NON-NLS-1$
//    private static final String ADD_ATTRIBUTES_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationMappingHelper.addAttributesTransactionDescription");    //$NON-NLS-1$
//    private static final String ADD_ATTRIBUTES_BEGIN_ERROR     = TransformationPlugin.Util.getString("TransformationMappingHelper.addAttributesBeginTransactionError");     //$NON-NLS-1$
//    private static final String ADD_ATTRIBUTES_COMMIT_ERROR    = TransformationPlugin.Util.getString("TransformationMappingHelper.addAttributesCommitTransactionError");    //$NON-NLS-1$
//    private static final String RECONCILE_MAPPINGS_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationMappingHelper.reconcileMappingsTransactionDescription");    //$NON-NLS-1$
//    private static final String RECONCILE_MAPPINGS_BEGIN_ERROR     = TransformationPlugin.Util.getString("TransformationMappingHelper.reconcileMappingsBeginTransactionError");     //$NON-NLS-1$
//    private static final String RECONCILE_MAPPINGS_COMMIT_ERROR    = TransformationPlugin.Util.getString("TransformationMappingHelper.reconcileMappingsCommitTransactionError");    //$NON-NLS-1$

    private static final boolean NOT_SIGNIFICANT = false;
    private static final boolean IS_UNDOABLE = true;

    public static final int TRANSFORMATION_CHANGED   = 10;
    public static final int TRANSFORMATION_UNCHANGED = 20;
    
    private static final IStatus TRANSFORMATION_OK_CHANGED   = new Status(IStatus.OK,TransformationPlugin.PLUGIN_ID,TRANSFORMATION_CHANGED,"internal: transformation parsed successfully, changes were applied",null); //$NON-NLS-1$
    private static final IStatus TRANSFORMATION_OK_UNCHANGED = new Status(IStatus.OK,TransformationPlugin.PLUGIN_ID,TRANSFORMATION_UNCHANGED,"internal: transformation parsed successfully, no changes applied",null); //$NON-NLS-1$
    private static final IStatus TRANSFORMATION_ISSUE        = new Status(IStatus.WARNING,TransformationPlugin.PLUGIN_ID,TRANSFORMATION_UNCHANGED,"internal: transformation has warnings, no changes applied",null); //$NON-NLS-1$    
    
    private static boolean createTargetAttributes = true;
    /**
     * Get the SqlTransformationMappingHelper instance for this VM.
     * @return the singleton instance for this VM; never null
     */
    public static TransformationMappingHelper getInstance() {
        return INSTANCE;
    }

        
    /**
     * reconcile the mapping root inputs / attributes / etc to conform to the 
     * SQL strings
     * @param transMappingRoot the transformation mapping root object.
     * @param source the source of the transaction; may be null
     */
    public static void reconcileMappingsOnSqlChange(EObject transMappingRoot, Object source) {
        if ( TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot) ) {
            // If source is null, use this Helper as the source
            if(source==null) {
                source=getInstance();
            }
            boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);

            //--------------------------------------------------------------
            // If the Query is Valid, continue
            //--------------------------------------------------------------
            if(isValid || TransformationHelper.isEmptySelect(transMappingRoot) ) {
                // Check whether Target Virtual Group is Locked
                boolean isLocked = TransformationHelper.isTargetGroupLocked(transMappingRoot);

                //start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Update Atribute Mappings", source); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Reconcile the mapping sources to the supplied command
                    reconcileSources(transMappingRoot,source);
    
                    // Reconcile VirtualGroup Target Only if unlocked
                    if(!isLocked && shouldCreateTargetAttributes()) {
                        reconcileTargetAttributes(transMappingRoot,source);
                    }
                    
                    // Update the attributeMappings
                    AttributeMappingHelper.updateAttributeMappings(transMappingRoot,source);
                    succeeded = true;
                } finally {
                    //if we started the txn, commit it.
                    if(requiredStart){
                        if(succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                if(requiredStart) {
                    SqlMappingRootCache.invalidateSelectStatus(transMappingRoot,true,source);
                }
            }
        }
    }
        
    /**
     * Compares the query Target Group Attributes with the SQL command projected Symbols. 
     * A status array with three boolean values is returned.  The array elements are 
     * (1) The number of target attributes is equal to the number of projected symbols
     * (2) The number of attributes/symbols is equal AND names are the same (ignoring case).
     * (3) The number of attributes/symbols is equal AND types are the same
     * @param transMappingRoot the Transformation MappingRoot object
     * @param type the command type
     * @return the status array
     */
    public static boolean[] compareQueryTargetAndSQLOutput(EObject transMappingRoot, int type) {
        // Must be a QueryCommand, or there are no projected Symbols.
        boolean sizesMatch = false;
        boolean namesMatch = false;
        boolean typesMatch = false;
        
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            //----------------------------------------------------------
            // Get the Reconciler Target Group for the Query MetaObject
            //----------------------------------------------------------
            EObject targetGroup = TransformationHelper.getTransformationTarget(transMappingRoot);
            
            //------------------------------------------------------------
            // Compare Target Group attribute size to SQL Output Size
            //------------------------------------------------------------
            if(targetGroup!=null) {
                // Get attributes that arent in an AccessPattern
                //List attributes = TransformationHelper.getTransformationTargetAttributesNotInAccessPattern(transMappingRoot);
                List attributes = TransformationHelper.getTransformationTargetAttributes(transMappingRoot);       
                
                // Get the Command for the supplied Type
                Command command = TransformationHelper.getCommand(transMappingRoot,type);
                // Get the list of names for the Select
                List sqlNames = TransformationSqlHelper.getProjectedSymbolNames(command);
                
                // check number of projected symbols for select - if zero, mark as failed
                if(sqlNames.size()==0 && type==QueryValidator.SELECT_TRNS) {
                    sizesMatch = false;
                // check number of Virtual Group Elems vs. number of projected symbols.
                } else if(sqlNames.size()==attributes.size()) {
                    sizesMatch = true;
                }
                
                // if sizes match, check names and types
                if(sizesMatch) {
                    //----------------------------------------------------------
                    // Compare names
                    //----------------------------------------------------------
                    List attributeNames = getAttributeNames(attributes);
                    namesMatch = allStringsMatch(sqlNames, attributeNames);
                    //----------------------------------------------------------
                    // Compare types
                    //----------------------------------------------------------
                    if(TransformationHelper.isValid(transMappingRoot,type)) {
                        // Get the list of SQL Symbols
                        List sqlSyms = command.getProjectedSymbols();
                        // Compare the types
                        typesMatch = allTypesMatch(sqlSyms,attributes);
                    }
                }
            }
            
        }

        boolean[] result = {sizesMatch,namesMatch,typesMatch};
        return result;
    }    

    /**
     * Determine if a supplied command output is reconciled with the transformation target group.
     * This allows us to pull only one query from a UNION instead of looking at the whole mappingRoot
     * command.  The argument list includes a boolean which indicates whether the names are required 
     * to match.  This is because queries other than the first in a UNION do not need to match names 
     * with the target - just the number of args and the types.
     * @param transMappingRoot the transformation mappingroot object
     * @param command the Command to compare the query MetaObject to
     * @param nameMatchReqd 'true' if names must match, 'false' if not.
     * @return 'true' if the reconciled, 'false' if not
     */
    public static boolean targetAndCommandReconcile(EObject transMappingRoot, Command command, 
                                                    boolean nameMatchReqd) {
        // Must be a QueryCommand, or there are no projected Symbols.
        boolean sizesMatch = false;
        boolean namesMatch = false;
        boolean typesMatch = false;
        boolean areReconciled = false;
        
        if(transMappingRoot!=null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            //----------------------------------------------------------
            // Get the Reconciler Target Group for the Query MetaObject
            //----------------------------------------------------------
            EObject targetGroup = TransformationHelper.getTransformationTarget(transMappingRoot);
            
            //------------------------------------------------------------
            // Compare Target Group attribute size to SQL Output Size
            //------------------------------------------------------------
            if(targetGroup!=null) {
                // Get attributes that arent in an AccessPattern
                List attributes = TransformationHelper.getTransformationTargetAttributes(transMappingRoot);
                // Get the list of names for the Select
                List sqlNames = TransformationSqlHelper.getProjectedSymbolNames(command);
        
                // check number of Virtual Group Elems vs. number of projected symbols.
                if(sqlNames.size()==attributes.size()) {
                    sizesMatch = true;
                }
                
                if(sizesMatch) {
                    //----------------------------------------------------------
                    // Compare names
                    //----------------------------------------------------------
                    List attributeNames = getTargetAttributeNames(transMappingRoot);
                    namesMatch=allStringsMatch(sqlNames,attributeNames);
                    //----------------------------------------------------------
                    // Compare types
                    //----------------------------------------------------------
                    if(commandValid(transMappingRoot,command)) {
                        // Get the list of SQL Symbols
                        List sqlSyms = command.getProjectedSymbols();
                        // Compare the types
                        typesMatch = allTypesMatch(sqlSyms,attributes);
                    }
                }
            }
            
        }

        if(nameMatchReqd && sizesMatch && namesMatch && typesMatch) {
            areReconciled = true;
        } else if(!nameMatchReqd && sizesMatch && typesMatch) {
            areReconciled = true;
        }
        return areReconciled;
    }

    /**
     * Compares a SingleElementSymbol and an attribute to see if their types match.
     * @param seSymbol the SingleElementSymbol
     * @param attribute the EObject attribute
     * @return 'true' if the types match, 'false' if not
     */
    private static boolean typesMatch( SingleElementSymbol seSymbol, EObject attribute) {
        boolean typesMatch = false;
        if(seSymbol!=null && attribute!=null) {
            Class symType = seSymbol.getType();
            if(symType!=null && symType!=DataTypeManager.DefaultDataClasses.NULL) {
                // If explicit match, set flag to true
                if(RuntimeTypeConverter.isExplicitMatch(seSymbol,attribute)) {
                    typesMatch=true;
                }
            }
        }
        return typesMatch;
    }

    /**
     * Compares a List of SingleElementSymbols and a List of attributes to see if all
     * their types match.
     * @param symbolList the List of SingleElementSymbols
     * @param attributeList the List of EObject attributes
     * @return 'true' if all types match, 'false' if not
     */
    private static boolean allTypesMatch(List symbolList, List attributeList) {
        if(symbolList!=null && attributeList!=null && symbolList.size()==attributeList.size()) {
            for(int i=0; i<symbolList.size(); i++) {
                SingleElementSymbol seSymbol = (SingleElementSymbol)symbolList.get(i);
                EObject attr = (EObject)attributeList.get(i);
                if(!typesMatch(seSymbol,attr)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Compares two Lists of strings to see if they all match (ignoring 'case').
     * @param list1 the first list of names
     * @param list1 the second list of names
     * @return 'true' if all strings match, 'false' if not
     */
     private static boolean allStringsMatch(List list1, List list2) {
         if(list1!=null && list2!=null && list1.size()==list2.size()) {
             for(int i=0; i<list1.size(); i++) {
                 String str1 = (String)list1.get(i);
                 String str2 = (String)list2.get(i);
                 if( !str1.equalsIgnoreCase(str2) ) {
                     return false;
                 }
             }
             
             return true;
         }
         return false;
     }

    /**
     * determine if the supplied command is valid
     * @param command the Command 
     * @return 'true' if the command is valid, 'false' if not
     */
    private static boolean commandValid(EObject transMappingRoot, Command command) {
        boolean isCommandValid = false;
        // If command is not resolved, attempt to resolve it.
        if(!command.isResolved()) {
            QueryValidator validator = new TransformationValidator((SqlTransformationMappingRoot)transMappingRoot);
            QueryValidationResult result = validator.validateSql(command.toString(), QueryValidator.SELECT_TRNS, false, false);
            if(result.isValidatable()) {
                isCommandValid=true;
            }
        } else {
            isCommandValid = true;
        }
        return isCommandValid;
    }

    // This method attempts to reconcile the transformation mapping sources to match
    // the supplied sql
    public static boolean reconcileSources(final EObject transMappingRoot, Object source) {
        boolean success = false;  
        
        // If source is null, then use this Helper as the source
        if(source==null) {
            source = getInstance();        
        }

        boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);

        //--------------------------------------------------------------
        // If the Query is Valid, continue
        //--------------------------------------------------------------
        if(isValid && !isReadOnly(transMappingRoot)) {
            //start txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Reconcile Sources", source); //$NON-NLS-1$
            boolean succeeded = false;
            try {
            	// Defect 20912 - added code to check for orphaned inputs or aliases
            	TransformationHelper.reconcileInputsAndAliases(transMappingRoot);
            	
            	List allCommands = new ArrayList();
                Command selectCommand = SqlMappingRootCache.getSelectCommand(transMappingRoot);
                allCommands.add(selectCommand);
                
                // Include sources for INS/UPD/DEL transformations also.
                EObject target = TransformationHelper.getTransformationTarget(transMappingRoot);
                if(TransformationHelper.tableSupportsUpdate(target)) {
	                if(TransformationHelper.isInsertAllowed(transMappingRoot) && SqlMappingRootCache.isInsertValid(transMappingRoot)) {
	                	allCommands.add(SqlMappingRootCache.getInsertCommand(transMappingRoot));
	                }
	                if(TransformationHelper.isUpdateAllowed(transMappingRoot) && SqlMappingRootCache.isUpdateValid(transMappingRoot)) {
	                	allCommands.add(SqlMappingRootCache.getUpdateCommand(transMappingRoot));
	                }
	                if(TransformationHelper.isDeleteAllowed(transMappingRoot) && SqlMappingRootCache.isDeleteValid(transMappingRoot)) {
	                	allCommands.add(SqlMappingRootCache.getDeleteCommand(transMappingRoot));
	                }
                }

                // Get the Command Lookup GroupSymbols that arent in the mapping        
                QueryMetadataInterface metadata = TransformationMetadataFactory.getInstance().getModelerMetadata(transMappingRoot,false);
                
                List symbolsToAdd = new ArrayList();
                List symbolsIncluded = new ArrayList();
                
                Iterator commandIter = allCommands.iterator();
                while(commandIter.hasNext()) {
                    Command command = (Command)commandIter.next();
                    getGroupSymbols(transMappingRoot,command, symbolsToAdd, symbolsIncluded, metadata);
                }
                
                // All Aliases
                List allAliases = new LinkedList(TransformationHelper.getAllSqlAliases(transMappingRoot));
                
                // Remove the extra aliases
                Iterator iter = allAliases.iterator();
                while(iter.hasNext()) {
                    SqlAlias sqlAlias = (SqlAlias)iter.next();
                    
                    if (!sqlAliasInGroupSymbolList(sqlAlias, symbolsIncluded)) {
                        EObject sqlEObj = sqlAlias.getAliasedObject();
                        String sqlAliasName = sqlAlias.getAlias();
                        TransformationHelper.removeSourceAlias(transMappingRoot,sqlEObj,sqlAliasName,true,source);
                    }
                }
                
                // add aliases for the Group symbols
                iter = symbolsToAdd.iterator();
                while(iter.hasNext()) {
                    GroupSymbol gSymbol = (GroupSymbol)iter.next();
                    EObject symbolEObj = TransformationSqlHelper.getGroupSymbolEObject(gSymbol);
                    String symbolName = TransformationSqlHelper.getGroupSymbolShortName(gSymbol);
                    if(symbolEObj!=null) {
                        TransformationHelper.addSqlAlias(transMappingRoot,symbolEObj,symbolName,true,source);
                    }
                }

                succeeded = true;
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        } else if(TransformationHelper.isEmptySelect(transMappingRoot)) {
            //start txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Reconcile Sources", source); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                TransformationHelper.removeAllSourcesAndAliases(transMappingRoot,true,source);
                succeeded = true;
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
                
        return success;
    }
        
    static List getLookupGroupSymbols(Collection functions,QueryMetadataInterface metadata) {
    	List groupSymbols = new ArrayList();
    	Iterator iter = functions.iterator();
    	while(iter.hasNext()) {
    		Function function = (Function)iter.next();
    		FunctionDescriptor fd = function.getFunctionDescriptor();
    		if(fd!=null && fd.getName().equalsIgnoreCase(FunctionLibrary.LOOKUP)) {
    	        Expression[] args = function.getArgs();
	            // First arg of the lookup is the group
	            if( args[0] instanceof Constant ) {
	                String groupName = (String) ((Constant)args[0]).getValue() ;  
	
	                try {
	                    Object metadataID = metadata.getGroupID(groupName);
	                    GroupSymbol symbol = new GroupSymbol(groupName);
	                    symbol.setMetadataID(metadataID);
	                    if(!groupSymbols.contains(symbol)) {
	                    	groupSymbols.add(symbol);
	                    }
	                } catch(QueryMetadataException e) {
	                	String message = TransformationPlugin.Util.getString("TransformationSqlHelper.groupIDNotFoundError", //$NON-NLS-1$
	                			                                             groupName);
	                    TransformationPlugin.Util.log(IStatus.WARNING,e,message);
	                } catch(MetaMatrixComponentException e) {
	                	String message = TransformationPlugin.Util.getString("TransformationSqlHelper.groupIDNotFoundError", //$NON-NLS-1$
                                                                             groupName);
	                	TransformationPlugin.Util.log(IStatus.WARNING,e,message);
					}
	            } 
    		}
    	}
    	return groupSymbols;
    }
    
    // Get the list of GroupSymbols from the command that are not represented in the current
    // transformation mapping.
    private static void getGroupSymbols(EObject transMappingRoot, Command command, List toAdd, List included, QueryMetadataInterface metadata) {
        
        // Get the GroupSymbols from the SQL command (duplicates removed)      
        Collection sqlGroupSymbols = TransformationSqlHelper.getGroupSymbols(command);
        
        Collection functions = FunctionCollectorVisitor.getFunctions(command,true,true);
        
        sqlGroupSymbols.addAll(getLookupGroupSymbols(functions,metadata));
        
        // Get the SqlAliases for the mapping
        List mappingAliases = TransformationHelper.getAllSqlAliases(transMappingRoot);
        
        // Check each GroupSymbol against the mapping SqlAliases.  If not there add to result list
        Iterator iter = sqlGroupSymbols.iterator();
        while(iter.hasNext()) {
            GroupSymbol gSymbol = (GroupSymbol)iter.next();
            // If group symbol not in alias list, add to result list
            if(!(gSymbol.getMetadataID() instanceof TempMetadataID) || gSymbol.isProcedure()) {
                if (!groupSymbolInAliasList(gSymbol,mappingAliases)) {
                    toAdd.add(gSymbol);
                } else {
                    included.add(gSymbol);
                }
            } 
        }
        
    }

    /**
     * Determine if GroupSymbol is represented in the SqlAlias List.
     * @param gSymbol the GroupSymbol
     * @param sqlAliases the SqlAlias List
     * @return 'true' if the sqlAlias List contains the groupSymbol, 'false' if not.
     */
    private static boolean groupSymbolInAliasList(GroupSymbol gSymbol,List sqlAliases) {
        boolean inSqlList = false;
        // Get the Group EObject and Name
        EObject groupEObj = TransformationSqlHelper.getGroupSymbolEObject(gSymbol);
        String groupName = TransformationSqlHelper.getGroupSymbolShortName(gSymbol);
        
        if(groupEObj!=null && groupName!=null) {
            // Look for corresponding SqlAlias
            Iterator iter = sqlAliases.iterator();
            while(iter.hasNext()) {
                SqlAlias sqlAlias = (SqlAlias)iter.next();
                EObject aEObj = sqlAlias.getAliasedObject();
                String aName = sqlAlias.getAlias();
                if(aEObj!=null && aName!=null && 
                   aEObj.equals(groupEObj) && aName.equalsIgnoreCase(groupName)) {
                   inSqlList = true;
                   break;
                }
            }
        }
        
        return inSqlList;
    }

    /**
     * Determine if GroupSymbol is represented in the SqlAlias List.
     * @param sqlAlias the SqlAlias
     * @param groupSymbols the GroupSymbol List
     * @return 'true' if the group Symbol List contains the sqlAlias, 'false' if not.
     */
    private static boolean sqlAliasInGroupSymbolList(SqlAlias sqlAlias,Collection groupSymbols) {
        boolean inGroupSymbolList = false;
        
        // Get the SqlAlias info
        EObject aliasEObj = sqlAlias.getAliasedObject();
        String aliasName = sqlAlias.getAlias();
        
        // Look for corresponding GroupSymbol
        Iterator iter = groupSymbols.iterator();
        while(iter.hasNext()) {
            GroupSymbol gSymbol = (GroupSymbol)iter.next();
            // Get the Group EObject and Name
            EObject groupEObj = TransformationSqlHelper.getGroupSymbolEObject(gSymbol);
            String groupName = TransformationSqlHelper.getGroupSymbolShortName(gSymbol);
        
            if(groupEObj!=null && groupName!=null && 
               groupEObj.equals(aliasEObj) && groupName.equalsIgnoreCase(aliasName)) {
                   inGroupSymbolList = true;
               break;
            }
        }
        
        return inGroupSymbolList;
    }
    
    /**
     * Reconcile the Query Select symbols with the existing Virtual Target attributes.
     * If reconcile is possible, the target attributes will be created / reordered to
     * match the SQL projected symbols. If the target Virtual Group is locked, no action 
     * will be taken.  If it cannot be reconciled without user assistance, returns false.  
     * @param transMappingRoot the transformation mapping root.
     * @param reconcileMappingClassTarget 'true' if auto-reconcile of MappingClass target is desired, 'false' if not.
     * @param source txn source
     * @return an OK status if reconcile was successful, a WARNING status if not.  The code
     *   of the status will indicate whether any changes were made, via MAPPING_CHANGED or
     *   MAPPING_NOT_CHANGED
     */
    public static IStatus reconcileTargetAttributes(EObject transMappingRoot, boolean reconcileMappingClassTarget, 
                                                    Object txnSource) {
        boolean success = false;  
        boolean changed = false;

        // Do not reconcile if the target Group is Locked
        if(!TransformationHelper.isTargetGroupLocked(transMappingRoot)) {
            
            // If source is null, then use this Helper as the source
            if(txnSource==null) {
                txnSource = getInstance();        
            }
    
            // Get the Target for this MappingRoot
            EObject targetGroup = TransformationHelper.getTransformationTarget(transMappingRoot);
            // If the target is a MappingClass, check reconcile flag before auto-reconciling.  
            // StagingTable passes thru
            if(targetGroup instanceof MappingClass && !(targetGroup instanceof StagingTable) &&
               !reconcileMappingClassTarget) {
                return TRANSFORMATION_ISSUE;
            }

            // Check whether the mappingRoot source is XML Document - special handling            
            boolean hasXMLDocSource = false;
            List sources = TransformationHelper.getTransformationSources(transMappingRoot);
            if(sources.size()==1 && sources.get(0) instanceof XmlDocument) {
                hasXMLDocSource = true;
            }
                        
            boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
            Command validCommand = null;
            if(isValid) {
                validCommand = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            }
            
            // Target was found
            if ( targetGroup != null && validCommand!=null ) {   
                //start txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Reconcile Target Attributes", txnSource); //$NON-NLS-1$
                boolean succeeded = false;
                try {                    
                    // Get the attribute Short Names
                    List currentTargetAttrNames = getTargetAttributeNames(transMappingRoot);
                    
                    // Get projected symbol names.  If target is Procedure ResultSet, dont use inputParams
                    List projectedSymbolNames = TransformationSqlHelper.getProjectedSymbolUniqueNames(validCommand);
                    
                    int nCurrentTargetAttrs = currentTargetAttrNames.size();
                    int nProjectedSymbols = projectedSymbolNames.size();
            
                    // Number of Virtual Target attributes is more than the projected symbols
                    // Dont remove attributes, just modify query if there's name conflict
                    if(nProjectedSymbols < nCurrentTargetAttrs) {
                        // Modify query if there's a name conflict
                        resetSelectSqlOnNameConflict(validCommand,transMappingRoot,true,txnSource);
                        return TRANSFORMATION_ISSUE;
                    }
            
                    // --------------------- BML 3/21/07 --------------------------
                    // Defect 23839 - Modifications to do additional up-front checks to see if working on attribute mappings
                    // is necessary or not.
                    
                    // Get the list of matchedNames between the two Lists
                    boolean namesMatch = compareLists(currentTargetAttrNames, projectedSymbolNames, true);
                    
                    // If lists are a complete match, we're done.
                    if( !namesMatch ) {
                        List matchedNames = getMatchedNames(currentTargetAttrNames,projectedSymbolNames);
                        // Get the list of unMatchedNames in the VirtualGroup
                        List unmatchedVirtualNames = removeNames(currentTargetAttrNames,matchedNames);
                
                        if(unmatchedVirtualNames.size()!=0) {
                            // Modify query if there's a name conflict
                            resetSelectSqlOnNameConflict(validCommand,transMappingRoot,true,txnSource);
                            return TRANSFORMATION_ISSUE;
                        }
                        
                        // Get the list of Extra Names in the Select List
                        List extraSymbolNames = removeNames(projectedSymbolNames,matchedNames);
                
                        // Add the New Elements to the Target Group
                        if( extraSymbolNames.size()!=0 ) {
                            // Get the Map of ProjectedSymbol Name to the corresponding EObject (if exists)
                            Map eObjectMap = TransformationSqlHelper.getProjectedSymbolAndProcInputEObjects(validCommand);
                            
                            addTargetAttributes(extraSymbolNames, eObjectMap, targetGroup, txnSource);
                            changed = true;
                        }
                    }

            
                    // Modify query if there's a name conflict
                    resetSelectSqlOnNameConflict(validCommand,transMappingRoot,true,txnSource);
            
                    // Ensure that the ordering is correct 
                    // If it is NOT, then and only then do we call orderGroupAttributes()
                    if( attributesOutOfOrder(targetGroup,projectedSymbolNames)) {
                        changed = orderGroupAttributes(targetGroup,projectedSymbolNames) || changed;
                    }
                    // Set attribute types
                    // Get the Map of ProjectedSymbol Name to the corresponding MetaObject
                    Map symbolTypeMap = TransformationSqlHelper.getProjectedSymbolAndProcInputUniqueTypes(validCommand);
                    
                    changed = setGroupAttributeTypes(targetGroup,symbolTypeMap) || changed;

                    // Set lengths if not mappingClass attributes
                    if( !(targetGroup instanceof MappingClass) ) {
                        // Get the Map of ProjectedSymbol Name to the lengths for Strings (-1) if not Datatype
                        Map symbolLengthMap = TransformationSqlHelper.getProjectedSymbolAndProcInputLengths(validCommand,hasXMLDocSource);
                        // Set attribute lengths for String datatypes
                        changed = setGroupAttributeLengths(targetGroup,symbolLengthMap) || changed;
                    }
                    
                    // Update the attribute mappings
                    changed =  AttributeMappingHelper.updateAttributeMappings(transMappingRoot,txnSource) || changed;
                    
                    succeeded = true;
                } finally { 
                    if(requiredStart){
                        if(succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                if( changed && requiredStart ) {
                    SqlMappingRootCache.invalidateSelectStatus(transMappingRoot,true,txnSource);
                }
                success=true;
            }
        }
        
        IStatus rv;
        if (success) {
            rv = (changed)? TRANSFORMATION_OK_CHANGED : TRANSFORMATION_OK_UNCHANGED;
        } else {
            rv = TRANSFORMATION_ISSUE;
        } // endif
        
        return rv;
    }

    /**
     * Reconcile the Query Select symbols with the existing Virtual Target attributes.
     * If reconcile is possible, the target attributes will be created / reordered to
     * match the SQL projected symbols. If the target Virtual Group is locked, no action 
     * will be taken.  If it cannot be reconciled without user assistance, returns false.
     * This method will not auto-reconcile the target, if the target is a MappingClass.
     * @param transMappingRoot the transformation mapping root.
     * @param source txn source
     * @return an OK status if reconcile was successful, a WARNING status if not.  The code
     *   of the status will indicate whether any changes were made, via MAPPING_CHANGE or
     *   MAPPING_NO_CHANGE
     */
    public static IStatus reconcileTargetAttributes(EObject transMappingRoot, Object txnSource) {
        return reconcileTargetAttributes(transMappingRoot,false,txnSource);                                                
    }

    /**
     * Determine if any of the supplied objects are Procedures
     * @param objList the list of objects
     * @return 'true' if any of the supplied objects are SqlProcedures, 'false' if not.
     */
    public static boolean hasProcedure(List objList) {
        boolean hasProcedure = false;
        // determine if any of the supplied objects are procedures
        Iterator iter = objList.iterator();
        while(iter.hasNext()) {
            Object source = iter.next();
            if(TransformationHelper.isSqlProcedure(source)) {
                hasProcedure = true;
                break;
            }
        }
        return hasProcedure;
    }
    
    /**
     * Get an ordered List of the Current Target Group Attribute Names
     * @param the transformation mapping root
     * @return the ordered list of attribute names
     */
    public static List getTargetAttributeNames(EObject transMappingRoot) {
        List attributes = TransformationHelper.getTransformationTargetAttributes(transMappingRoot);
        return getAttributeNames(attributes);
    }
    
    /**
     * Get the names for the supplied attributes list
     * @param attributes the list of attributes
     * @return the list of attribute names
     */
    public static List getAttributeNames(List attributes) {
        List attributeNames = new ArrayList();
        Iterator iter = attributes.iterator();
        while(iter.hasNext()) {
            EObject attr = (EObject)iter.next();
            if( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(attr) ) {
                // Handle special case when transformation target is WebService Operation
                if( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isXmlDocument(attr)) {
                    attributeNames.add("xml"); //$NON-NLS-1$
                } else {
                    SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(attr);
                    attributeNames.add(columnAspect.getName(attr));
                }
            }
        }
        return attributeNames;
    }

    /**
     * Get the names which occur in both lists, ignoring the case of the string.
     * @param list1 the first list of Strings
     * @param list2 the second list of Strings
     * @return the list of strings which match between the lists
     */
    private static List getMatchedNames(List list1, List list2) {
        List newList = new ArrayList();
        Iterator list1Iter = list1.iterator();
        // Iterate list1, look for match in list2
        while(list1Iter.hasNext()) {
            String list1Str = (String)list1Iter.next();
            if(list1Str!=null) {
                Iterator list2Iter = list2.iterator();
                while(list2Iter.hasNext()) {
                    String list2Str = (String)list2Iter.next();
                    if(list1Str.equalsIgnoreCase(list2Str)) {
                        newList.add(list1Str);
                        break;
                    }
                }
            }
        }
        return newList;
    }
    
    /**
     * Remove the names in the removeList from the list.
     * @param list the list of Strings to remove from
     * @param removeList the list of Strings to remove
     * @return the original list of strings minus the removeList
     */
    private static List removeNames(List list, List removeList) {
        // create a copy of the original list to work with
        List newList = new ArrayList(list);
        // Iterate the working list, remove items if match is found
        Iterator removeIter = removeList.iterator();
        while(removeIter.hasNext()) {
            String removeStr = (String)removeIter.next();
            Iterator newListIter = newList.iterator();
            while(newListIter.hasNext()) {
                String listName = (String)newListIter.next();
                if(listName!=null && listName.equalsIgnoreCase(removeStr)) {
                    newListIter.remove();
                    break;
                }
            }
        }
        return newList;
    }

    /**
     * This method will reset the Select SQL strings when there is a name conflict with the
     * projected symbol short names.
     * @param queryCommand the query Command object
     * @return the modified Query language object
     */
    public static void resetSelectSqlOnNameConflict(Command command, EObject transMappingRoot, 
                                                    boolean isSignificant, Object source) {
        // Modify query select if necessary
        if( command!=null && command instanceof Query 
                          && TransformationSqlHelper.hasProjectedSymbolNameConflict(command) ) {
            Query newQuery = TransformationSqlHelper.createQueryFixNameConflicts((Query)command);
            // Set the SQL Statement
            if(newQuery!=null) {
                TransformationHelper.setSelectSqlString(transMappingRoot,newQuery.toString(),
                                                        isSignificant,source);
            }
        }
    }
    

    /**
     * Create the Target Attributes with the provided names
     * @param targetEObject the target EObject to add the attributes to
     * @param attributeNames a list of the attributeNames (correspond to map keys), 
     *        in the correct order
     * @param isSignificant significance flag for undo
     * @param source the source for the transaction
     */
    public static void addTargetAttributes(final EObject targetEObject,final List attributeNames,
                                           final Object source) {
        if(targetEObject!=null && attributeNames!=null && !attributeNames.isEmpty()) {                  
            //start txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Add Target Attributes", source); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                //---------------------------------------------------------
                // Iterate thru the supplied list, creating new attributes
                //---------------------------------------------------------
                String attrName = null;
                //Object typeObj = null;
                //Iterate on the attributeNames
                Iterator iter = attributeNames.iterator();
                while(iter.hasNext()) {
                    // Name of the attribute to create
                    attrName = (String)iter.next();
                    
                    // Get the descriptor for a SqlColumn from the target
                    org.eclipse.emf.common.command.Command paramToCreate = getSqlColumnDescriptor(targetEObject);
                    // Object to determine type
                    //typeObj = objectTypeMap.get(attrName);
                    
                    if(paramToCreate!=null) {
                        // Create a new Attribute with the specified name
                        try {
                            EObject newEObject = ModelerCore.getModelEditor().createNewChildFromCommand(targetEObject, paramToCreate);
                            ModelerCore.getModelEditor().rename(newEObject,attrName); 
                        } catch (ModelerCoreException theException) {
                            String message = TransformationPlugin.Util.getString("TransformationMappingHelper.errorCreatingAttribute",     //$NON-NLS-1$
                                                                                  attrName); 
                            TransformationPlugin.Util.log(IStatus.ERROR, theException, message); 
                        }
                    }
                }
                succeeded = true;
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    private static org.eclipse.emf.common.command.Command getSqlColumnDescriptor(EObject targetEObject) {
        org.eclipse.emf.common.command.Command colDescriptor = null;
        //------------------------------------------------
        // Get the Descriptor for ColumnAspect
        //------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {        
            descriptors = ModelerCore.getModelEditor().getNewChildCommands(targetEObject);
        } catch (ModelerCoreException e) {
            String message = TransformationPlugin.Util.getString("TransformationMappingHelper.getChildDescriptorError",     //$NON-NLS-1$
                                                                  targetEObject.toString()); 
            TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
            return null;
        }
    
        // Use the first ColumnAspect found
        Iterator iter = descriptors.iterator();
        while( iter.hasNext() ) {
            colDescriptor = (org.eclipse.emf.common.command.Command) iter.next(); 
            EObject eObj = (EObject) colDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnAspect, stop
            if( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj) ) {
                break;
            }                       
        }
        return colDescriptor;
    }
    
    /**
     * Create the Target Attributes using the names/ordering provided in the attributeNames list. 
     * If there is a source EObject provided in the sourceEObject Map, then transfer its properties
     * to the new Target Attribute.
     * @param attributeNames a list of the attributeNames (correspond to map keys), 
     *        in the correct order
     * @param sourceEObjMap the map of attribute names (keys) to EObject (values)
     * @param targetEObject the target EObject to add the attributes to
     * @param isSignificant significance flag for undo
     * @param source the source for the transaction
     */
    public static void addTargetAttributes(final List attributeNames,final Map sourceEObjectMap,
                                           final EObject targetEObject,
                                           final Object txnSource) {
                   
        if(targetEObject!=null && attributeNames!=null && !attributeNames.isEmpty()) {                  
            //start txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT, IS_UNDOABLE, "Add Target Attributes", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                //---------------------------------------------------------
                // Iterate thru the supplied list, creating new attributes
                //---------------------------------------------------------
                String attrName = null;
                //Iterate on the attributeNames
                Iterator iter = attributeNames.iterator();
                while(iter.hasNext()) {
                    // Name of the attribute to create
                    attrName = (String)iter.next();
                    
                    // Get the descriptor for a SqlColumn from the target
                    org.eclipse.emf.common.command.Command paramToCreate = getSqlColumnDescriptor(targetEObject);
                    // Source EObject for transfer of properties
                    EObject srcEObj = (EObject)sourceEObjectMap.get(attrName);
                    // Create a new Attribute with the specified name and properties
                    try {
                        EObject newEObject = ModelerCore.getModelEditor().createNewChildFromCommand(targetEObject, paramToCreate);
                        ModelerCore.getModelEditor().rename(newEObject,attrName);
                        // Transfer properties if src EObj exists 
                        TransformationHelper.transferSqlColumnProperties(newEObject,srcEObj,txnSource);
                    } catch (ModelerCoreException e) {
                        String message = TransformationPlugin.Util.getString("TransformationMappingHelper.createNewAttrError",     //$NON-NLS-1$
                                                                             targetEObject.toString()); 
                        TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
                    }
                }            
                succeeded = true;            
            } finally {
                //if we started the txn, commit it.
                if(requiredStart){
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    // Get an ordered List of the Current Target Group Attribute Names
    public static void removeTargetAttributes(Object mappingRoot,List attributes,
                                              final boolean isSignificant,final Object source) {
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - refactored this method a little to use SqlAspects directly instead of calling all TransformationHelper
        // methods. It's more efficient to do this.
        // start Txn
        boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, "Add Target Attributes", source); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            SqlTransformationMappingRoot transMappingRoot = null;        
            if(TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
                transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
            }
            
            EObject virtualTarget = TransformationHelper.getTransformationTarget(transMappingRoot);
            SqlAspect theTargetAspect = AspectManager.getSqlAspect(virtualTarget);
            List deleteList = new ArrayList();
            if(theTargetAspect instanceof SqlTableAspect || theTargetAspect instanceof SqlColumnSetAspect) {
                // Get the current virtual target attributes
                List columns = null;
                if(theTargetAspect instanceof SqlTableAspect) {
                    columns = ((SqlTableAspect)theTargetAspect).getColumns(virtualTarget);
                } else if(theTargetAspect instanceof SqlColumnSetAspect) {
                    columns = ((SqlColumnSetAspect)theTargetAspect).getColumns(virtualTarget);
                }
                // Add the attributes to the delete list
                Iterator colIter = columns.iterator();
                while(colIter.hasNext()) {
                    EObject attr = (EObject)colIter.next();
                    SqlAspect theChildAspect = AspectManager.getSqlAspect(attr);
                    if( theChildAspect instanceof SqlColumnAspect ) {
                        if( containsName(attributes,((SqlColumnAspect)theChildAspect).getName(attr))) {
                            deleteList.add(attr);
                        }
                    }
                }
            }  
            
            if(deleteList.size()>0) {
                // Delete the list of eObjects
                try {
                    ModelerCore.getModelEditor().delete(deleteList);
                } catch (ModelerCoreException e) {
                    String message = TransformationPlugin.Util.getString("TransformationMappingHelper.errorDeletingAttributes"); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
                }
            }
            succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return;
    }

    private static boolean containsName(List attributes, String name) {
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - utilizing SqlAspects directly instead of using TransformationHelper methods.  More efficient. 
        boolean contains = false;
        
        Iterator iter = attributes.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            SqlAspect theAspect = AspectManager.getSqlAspect(eObj);
            if( theAspect != null && theAspect instanceof SqlColumnAspect ) {
                String objName = ((SqlColumnAspect)theAspect).getName(eObj);
                if(objName.equalsIgnoreCase(name)) {
                    contains=true;
                    break;
                }
            }
        }
        
        return contains;
    }
    
    public static EObject getListAttrByName(List attributes, String name) {
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - utilizing SqlAspects directly instead of using TransformationHelper methods.  More efficient. 
        EObject result = null;
        Iterator iter = attributes.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            SqlAspect theAspect = AspectManager.getSqlAspect(eObj);
            if( theAspect != null && theAspect instanceof SqlColumnAspect ) {
                String objName = ((SqlColumnAspect)theAspect).getName(eObj);
                if(objName.equalsIgnoreCase(name)) {
                    result=eObj;
                    break;
                }
            }
        }
        return result;
    }
    
    public static boolean hasTypeConflict(Object attribute, Object symbol) {
        return !RuntimeTypeConverter.isExplicitMatch(attribute,symbol);
    }
    
    /**
     * Order the groups attributes to match the SQL projected symbol names
     * @param transMappingRoot the SqlTransformationMappingRoot
     */
    public static boolean orderGroupAttributes(final EObject transMappingRoot, boolean requiresValidSelect, Command modifiedCommand) {
        // Do nothing if the target Group is Locked
        if(!TransformationHelper.isTargetGroupLocked(transMappingRoot)) {
            boolean isValid = true;
            if( requiresValidSelect)
                isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
            
            Command validCommand = null;
            if(isValid) {
                validCommand = SqlMappingRootCache.getSelectCommand(transMappingRoot);
                // Get the Target for this MappingRoot
                EObject targetGroup = TransformationHelper.getTransformationTarget(transMappingRoot);
                List projectedSymbolNames = null;
                if( modifiedCommand != null && validCommand instanceof SetQuery ) {
                    projectedSymbolNames = TransformationSqlHelper.getProjectedSymbolUniqueNames(modifiedCommand);
                } else {
                    // Get the list of proposed unique names for the ProjectedSymbols
                    projectedSymbolNames = TransformationSqlHelper.getProjectedSymbolUniqueNames(validCommand);
                }
                // Order the target attributes
                return orderGroupAttributes(targetGroup,projectedSymbolNames);
            }
        }
        
        return false;
    }
    
    

    /**
     * Order the groups attributes in the order of the supplied list of names.
     * @param groupEObject the group that contains the attributes to reorder.
     * @param attrNames a list of attribute names in the correct order.
     */
    private static boolean orderGroupAttributes(final EObject groupEObject, final List attrNames) {
        // Get the list of current SqlColumn index positions
        List columnIndices = getColumnIndices(groupEObject);
        boolean attributeMoved = false;
        if(columnIndices.size()==attrNames.size()) {
            for(int i=0; i<attrNames.size(); i++) {
                String name = (String)attrNames.get(i);
                boolean wasMoved = moveAttribute(groupEObject,name,((Integer)columnIndices.get(i)).intValue());
                if( wasMoved )
                    attributeMoved = wasMoved;
            }
        }
        
        return attributeMoved;
    }
    
    /**
     * Determines whether or not a a list of source "attributes" are represented by the sql column attributes for a target group 
     * @param groupEObject
     * @param attrNames
     * @return
     * @since 5.0
     */
    private static boolean attributesOutOfOrder(final EObject groupEObject, final List attrNames) {
        List children = groupEObject.eContents();
        int iSqlColumn = 0;
        for(int i=0; i<children.size(); i++) {
            EObject nextChild = (EObject)children.get(i);
            if( TransformationHelper.isSqlColumn(nextChild) ) {
                String targetChildName = ModelerCore.getModelEditor().getName(nextChild);
                String sourceAttrName = (String)attrNames.get(iSqlColumn++);
                // Only do the move if the names match and its at the wrong index
                if (targetChildName != null && !targetChildName.equalsIgnoreCase(sourceAttrName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set the attribute types for the supplied group, using the supplied type map.  An attribute type will
     * only be set if it is null.  Will not be changed if it has previously been set.
     * The map is name (key) to type object (value) map.
     * @param groupEObject the group that contains the attributes to reorder.
     * @param attrTypemap the map to use in setting the group attribute types
     * @return true if any data was changed.
     */
    private static boolean setGroupAttributeTypes(final EObject groupEObject, final Map attrTypeMap) {
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - refactored a little to move the attrTypeMap.get() call to AFTER we've determined that the type does not
        // exist.  This should improve speed by eliminated ALL calls to get for a table where all column datatypes are already
        // set.
        boolean changed = false;
        List children = groupEObject.eContents();
        Iterator iter = children.iterator();
        while(iter.hasNext()) {
            EObject child = (EObject)iter.next();
            SqlAspect theAspect = AspectManager.getSqlAspect(child);
            if( theAspect != null && theAspect instanceof SqlColumnAspect ) {
                String colName = theAspect.getName(child);
                
                // Only set the datatype if it has not already been set
                EObject dtype = ((SqlColumnAspect)theAspect).getDatatype(child);
                if(dtype==null) {
                    Object theType = attrTypeMap.get(colName);
                    if( theType != null ) {
                        setAttributeType(child,theType);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Set the attribute lengths for the supplied group, using the supplied type map. 
     * The map is name (key) to length integer (value) map.
     * @param groupEObject the group that contains the attributes to set lengths
     * @param attrLengthMap the map to use in setting the group attribute lengths
     * @return true if any data was changed.
     */
    public static boolean setGroupAttributeLengths(final EObject groupEObject, final Map attrLengthMap) {
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - refactored a little to move the attrTypeMap.get() call to AFTER we've determined that the type does not
        // exist.  This should improve speed by eliminated ALL calls to get for a table where all column datatypes are already
        // set.
        boolean changed = false;
        if(attrLengthMap!=null && !attrLengthMap.isEmpty()) {
            List children = groupEObject.eContents();
            Iterator iter = children.iterator();
            while(iter.hasNext()) {
                EObject child = (EObject)iter.next();
                SqlAspect theAspect = AspectManager.getSqlAspect(child);
                if(theAspect != null && theAspect instanceof SqlColumnAspect ) {
                    String targetColName = theAspect.getName(child);
                    // Current length for the attribute        
                    int currentLength = ((SqlColumnAspect)theAspect).getLength(child);
                    // Only set it if it hasn't been set yet
                    if(currentLength<=0 && attrLengthMap.get(targetColName) != null ) {
                        // New length
                        int length = ((Integer)attrLengthMap.get(targetColName)).intValue();
                        if( length!=-1 && length != currentLength ) {
                            if (((SqlColumnAspect)theAspect).canSetLength()) {
                                setAttributeLength(child,length);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Get a list of index locations of the Columns in the supplied group EObject
     * @param groupEObject the supplied group
     * @return the List of column index positions for the supplied group.
     */
    private static List getColumnIndices(EObject groupEObject) {
        List columnIndices = new ArrayList();
        List children = groupEObject.eContents();
        for(int i=0; i<children.size(); i++) {
            if( TransformationHelper.isSqlColumn(children.get(i)) ) {
                columnIndices.add(new Integer(i));
            }
        }
        return columnIndices;
    }
    
    /**
     * Move a child attribute to a specific index
     * @param attrName the name of the child attribute to move
     * @param newIndex the index to place the element at
     */
    private static boolean moveAttribute(EObject groupEObject, String attrName, int newIndex) {
        List children = groupEObject.eContents();
        boolean moved = false;
        // Go through the child list and move to the desired index
        Iterator iter = children.iterator();
        int index = 0;
        while (iter.hasNext()) {
            EObject eObj = (EObject) iter.next();

            if ( TransformationHelper.isSqlColumn(eObj) ) {
                String currentAttrName = TransformationHelper.getSqlEObjectName(eObj);
                // Only do the move if the names match and its at the wrong index
                if (currentAttrName.equalsIgnoreCase(attrName)) {
                    // Move the Element if at wrong index
                    //----------------------------------------
                    if (newIndex != index) {
                        try {
                            ModelerCore.getModelEditor().move(groupEObject,eObj,newIndex);
                            moved = true;
                        } catch (ModelerCoreException err) {
                            String message = TransformationPlugin.Util.getString("TransformationMappingHelper.moveTargetAttrError",     //$NON-NLS-1$
                                                                     groupEObject.toString()); 
                            TransformationPlugin.Util.log(IStatus.ERROR, err, message); 
                        }
                        break;
                    }
                }
            }
            index++;
        }
        
        return moved;
    }

    /**
     * Set the type on the supplied attribute EObject.  The type object should be
     * 1) Datatype
     * 2) java Class
     * 3) null
     * @param attrEObject the attribute EObject
     * @param typeObj the new attribute type
     */
    private static void setAttributeType(EObject attrEObject, Object typeObj) {
        if ( TransformationHelper.isSqlColumn(attrEObject) ) {
            
            // Get new datatype for the supplied typeObj
            EObject newDatatype = getDatatype(typeObj);

            // Current datatype for the attribute        
            EObject currentDatatype = TransformationHelper.getSqlColumnDatatype(attrEObject);
            
            // If new datatype is different that current, set it
            if( (newDatatype==null && currentDatatype!=null) ||
                (newDatatype!=null && !newDatatype.equals(currentDatatype)) ) {
                TransformationHelper.setSqlColumnDatatype(attrEObject,newDatatype,getInstance());
            }
        }
    }
    
    /**
     * Set the length on the supplied attribute EObject.  The type object should be
     * @param attrEObject the attribute EObject
     * @param length the new attribute length
     */
    private static void setAttributeLength(EObject attrEObject, int newLength) {
        if ( TransformationHelper.isSqlColumn(attrEObject) ) {
            // Current length for the attribute        
            int currentLength = TransformationHelper.getSqlColumnLength(attrEObject);
            
            // If new datatype is different that current, set it
            if( newLength != currentLength ) {
                TransformationHelper.setSqlColumnLength(attrEObject,newLength,getInstance());
            }
        }
    }

    /**
     * Get the appropriate Datatype, given a "type" Object.  The type Object will be one of
     * 1) Datatype
     * 2) java Class
     * 3) null
     * @param typeObj Object from which to determine the Datatype
     * @return the Datatype for the supplied object
     */
    private static EObject getDatatype(Object typeObj) {
        EObject datatype = null;
        
      //------------------------------------------------------
      // Set the element type according to the supplied type
      //------------------------------------------------------
      if (typeObj!=null) {
          //------------------------------------------------
          // Supplied type is a Datatype, just return it
          //------------------------------------------------
          if(typeObj instanceof EObject ) {
              final EObject eObject = (EObject)typeObj;
              final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
              if ( dtMgr.isSimpleDatatype(eObject) ) {
                  datatype = (EObject)typeObj;
              }
          } else if(typeObj instanceof Class) {
              // check for the NullType
             if (typeObj == com.metamatrix.common.types.NullType.class) {
                 // convert the NullType constant to the String type
                 typeObj = String.class;
             }
             // Get the runtime type for the java Class
             String runtimeTypeName = DataTypeManager.getDataTypeName((Class) typeObj);
             
             datatype = getDefaultDatatypeForRuntimeTypeName(runtimeTypeName);
          }
        }
        
        return datatype;
    }
    
    public static EObject getDefaultDatatypeForRuntimeTypeName(String runtimeTypeName) {
        
        EObject datatype = null;
        // Get the default Datatype for this runtime type
        EObject typeEObj = null;
        try {
            typeEObj = ModelerCore.getWorkspaceDatatypeManager().getDefaultDatatypeForRuntimeTypeName(runtimeTypeName);             
        } catch (ModelerCoreException e) {
            String message = TransformationPlugin.Util.getString("TransformationMappingHelper.errorFindingDefaultType",     //$NON-NLS-1$
                                                                  runtimeTypeName); 
            TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
        }
             
        if(typeEObj != null ) {
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(typeEObj,true);
            if ( dtMgr.isSimpleDatatype( typeEObj) ) {
                return typeEObj;
            }
        }
        
        return datatype;
    }
    
    /**
     * Indicates if the given <code>EObject</code> is contained within a read-only resource.
     * @param theEObject the object being checked
     * @return <code>true</code> if the object is read-only; <code>false</code> otherwise.
     */
    private static boolean isReadOnly(EObject theEObject) {
        // consider it read-only until proven otherwise
        boolean result = true;
        if ( theEObject != null ) {
            ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(theEObject);
            if ( modelResource != null ) {
                result = ModelUtil.isIResourceReadOnly(modelResource.getResource());
            }else {
                //outside workspace
                result = false;
            }
        }
        return result;
    }

    /**
     * Determines if the lists of strings contain equal strings 
     * @param strings1
     * @param strings2
     * @param ignoreCase
     * @return
     * @since 5.0
     */
    public static boolean compareLists(List strings1, List strings2, boolean ignoreCase) {
        int size1 = strings1.size();
        int size2 = strings2.size();
        if( size1 != size2 ) {
            return false;
        }
        
        for( int i=0; i<size1; i++ ) {
            if( ignoreCase ) {
                if( ! ((String)strings1.get(i)).equals(strings2.get(i))) {
                    return false;
                }
            } else {
                if( ! ((String)strings1.get(i)).equalsIgnoreCase((String)strings2.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }


    
    public static boolean shouldCreateTargetAttributes() {
        return createTargetAttributes;
    }
    
    public static void setCreateTargetAttributes(boolean theCreateTargetAttributes) {
        createTargetAttributes = theCreateTargetAttributes;
    }
}
