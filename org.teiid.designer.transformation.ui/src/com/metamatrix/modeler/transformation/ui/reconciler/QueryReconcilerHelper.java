/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.internal.transformation.util.AttributeMappingHelper;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.QueryCommand;
import com.metamatrix.query.sql.lang.SetQuery;

/**
 * Reconciler Helper that the Reconciler Panel works with
 * 
 */
public class QueryReconcilerHelper  {
    
    private static final String REORDER_DETECTED_TITLE      = UiConstants.Util.getString("QueryReconcilerHelper.reorderDetectedTitle"); //$NON-NLS-1$
    private static final String REORDER_DETECTED_MESSAGE    = UiConstants.Util.getString("QueryReconcilerHelper.reorderDetectedMessage"); //$NON-NLS-1$

    //============================================================
    // Instance variables
    //============================================================
    private SqlTransformationMappingRoot transMappingRoot; // the transformation Object being worked on 
    private ReconcilerObject reconcilerObject = null;
    private boolean isPrimarySelectClause = true;
    
    //============================================================
    // Constructors
    //============================================================
    /**
     * Constructor.
     * 
     * @param transformationObj the TransformationMappingRoot this is based on.
     * @param unionQuerySegment the index of the union Query segment to reconcile, -1 if not
     *                           a union query or no segment specified.
     */
    public QueryReconcilerHelper(SqlTransformationMappingRoot transformationObj,
                                  int unionQuerySegment) {
        this.transMappingRoot = transformationObj;
        init(this.transMappingRoot,unionQuerySegment);
    }
    //============================================================
    // Instance methods
    //============================================================
    
    /**
     * Initialize the reconciler helper.  
     * @param mappingRoot the TransformationMappingRoot object
     * @param unionQuerySegment for a union query, this is the segment currenly being worked on.
     *                           If not a union query, or not working in a segment, -1 is expected.
     */    
    private void init(SqlTransformationMappingRoot mappingRoot,int unionQuerySegment) {
        if(mappingRoot!=null) {
            // Get the transformation SELECT command
            Command originalCommand = SqlMappingRootCache.getSelectCommand(mappingRoot);
            
            if(originalCommand instanceof SetQuery && unionQuerySegment!=-1) {
                List queries = ((SetQuery)originalCommand).getQueryCommands();
                originalCommand = (Command)queries.get(unionQuerySegment);
                // If command is not resolved, attempt to resolve it.
                if(!originalCommand.isResolved()) {
                    QueryValidator validator = new TransformationValidator(mappingRoot);
                    QueryValidationResult result = validator.validateSql(originalCommand.toString(), QueryValidator.SELECT_TRNS, false, false);
                    if(result.isResolvable()) {
                        originalCommand = result.getCommand();
                    }
                }
            }
            
            // Get the target Attributes 
            EObject virtualTarget = TransformationHelper.getTransformationTarget(mappingRoot);
            
            // Check if output is locked
            boolean isLocked = mappingRoot.isOutputReadOnly();
            
            this.reconcilerObject = new ReconcilerObject(virtualTarget,originalCommand,isLocked);
            if( unionQuerySegment > 0)
                this.isPrimarySelectClause = false;
        }
    }
    
    
    /**
     * Apply all of the pending modifications
     * @param uIndex the union segment index to apply changes to.  For non-union, -1 is supplied
     * @param txnSource the transaction source
     */
    public void applyAllModifications(int uIndex, Object txnSource) {
        if(this.reconcilerObject.hasValidModifications()) {
            // If reconciling a union segment (other than the first), only the sql is updated.
            if(uIndex>0) {
                applyUnionSegmentModifications(uIndex,txnSource);
                return;
            }
            // Target Attributes Deleted
            if(this.reconcilerObject.hasTargetAttributesToDelete()) {
                List attributes = this.reconcilerObject.getTargetAttributesToDelete();
                TransformationMappingHelper.removeTargetAttributes(this.transMappingRoot,attributes,true,txnSource);
            }
            
            // If there are target attrbutes to create, create them (only if unlocked AND createAttributesOnExit == TRUE)
            if( TransformationMappingHelper.shouldCreateTargetAttributes() && 
                !this.reconcilerObject.isTargetLocked() && 
                this.reconcilerObject.hasTargetAttributesToCreate()) {
                List attributeNames = this.reconcilerObject.getTargetAttributeNamesToCreate();
                EObject targetGroup = TransformationHelper.getTransformationTarget(this.transMappingRoot);
                TransformationMappingHelper.addTargetAttributes(targetGroup,attributeNames,txnSource);
                
                // Transfer Lengths for newly created attributes
                // Set lengths if not mappingClass attributes
                if( !(targetGroup instanceof MappingClass) ) {
                    // Get the Map of Symbol Name to the lengths for Strings, (-1) if not Datatype
                	Map attrLengthMap = this.reconcilerObject.getCreatedAttrLengthMap();
                    // Set attribute lengths for String datatypes
                    TransformationMappingHelper.setGroupAttributeLengths(targetGroup,attrLengthMap);
                }

                // find attribute EObject for each new attribute and set binding
                // the binding object is used later to determine the attribute type
                List allAttributes = TransformationHelper.getTransformationTargetAttributes(transMappingRoot);
                
                for (int numAttributes = allAttributes.size(), i = 0; i < numAttributes; i++) {
                    EObject attr = (EObject)allAttributes.get(i);
                    
                    if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(attr)) {
                        SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(attr);
                        String name = columnAspect.getName(attr);
                        
                        if (hasNameMatch(attributeNames,name)) {
                            // set binding here
                            List bindings = this.reconcilerObject.getBindingList().getAll();
                            
                            BINDINGS_LOOP:
                            for (int numBindings = bindings.size(), j = 0; j < numBindings; j++) {
                                Binding binding = (Binding)bindings.get(j);
                                
                                if (binding.getAttributeName().equalsIgnoreCase(name)) {
                                    binding.setAttribute(attr);
                                    break BINDINGS_LOOP;
                                }
                            }
                        }
                    }
                }
            }
            
            // Target Attribute Type Changes
            if(this.reconcilerObject.hasTargetAttributeTypeMods()) {
                this.reconcilerObject.applyTargetAttributeTypeMods(txnSource);
            }
            // SQL Modifications
            if(uIndex==0) {
                applyUnionSegmentModifications(uIndex,txnSource);
            } else if(this.reconcilerObject.hasValidSqlModifications()) {
                String newSql = this.reconcilerObject.getModifiedSql();
                TransformationHelper.setSelectSqlString(this.transMappingRoot,newSql,true,txnSource);
            } 
            AttributeMappingHelper.updateAttributeMappings(this.transMappingRoot,txnSource);
            // Check ordering
            TransformationMappingHelper.orderGroupAttributes(this.transMappingRoot, false, null);
        }
    }
    
    public void applyPreModifications(Object txnSource) {
        if(this.reconcilerObject.hasTargetAttributesToRename()) {
            this.reconcilerObject.applyTargetAttributeRenames(txnSource);
        }
    }
    
    public boolean hasPreModifications() {
        return this.reconcilerObject.hasTargetAttributesToRename();
    }
    
    /**
     * Get this helpers Reconciler business object.
     * @return the ReconcilerObject for this helper
     * @since 4.2
     */
    public ReconcilerObject getReconcilerObject() {
        return this.reconcilerObject;
    }
    
    /**
     *   Set the Target Virtual Group Locked state
     * @param shouldLock 'true' if the transformation target group is to be Locked (Readonly), 'false' if not.
     */
    public void setTargetLocked(boolean shouldLock) {
        // If different than current state, change it.
        if(transMappingRoot!=null && transMappingRoot.isOutputReadOnly()!=shouldLock) {
            transMappingRoot.setOutputReadOnly(shouldLock);
        }
        this.reconcilerObject.setTargetLocked(shouldLock);
    }

    /**
     * Method to apply changes in the case of a union (SetQuery).
     * @param uIndex the index of the query segment being worked on (-1 if none)
     * @param txnSource the transaction source
     * @since 4.2
     */
    private void applyUnionSegmentModifications(int uIndex,Object txnSource) {
        // Create Target Attributes Created (only if unlocked)
        if(!this.reconcilerObject.isTargetLocked() && this.reconcilerObject.hasTargetAttributesToCreate()) {
            List attributeNames = this.reconcilerObject.getTargetAttributeNamesToCreate();
            EObject targetGroup = TransformationHelper.getTransformationTarget(this.transMappingRoot);
            TransformationMappingHelper.addTargetAttributes(targetGroup,attributeNames,txnSource);
            
            // Transfer Lengths for newly created attributes
            // Set lengths if not mappingClass attributes
            if( !(targetGroup instanceof MappingClass) ) {
                // Get the Map of Symbol Name to the lengths for Strings, (-1) if not Datatype
                Map attrLengthMap = this.reconcilerObject.getCreatedAttrLengthMap();
                // Set attribute lengths for String datatypes
                TransformationMappingHelper.setGroupAttributeLengths(targetGroup,attrLengthMap);
            }

            // find attribute EObject for each new attribute and set binding
            // the binding object is used later to determine the attribute type
            List allAttributes = TransformationHelper.getTransformationTargetAttributes(transMappingRoot);
            
            for (int numAttributes = allAttributes.size(), i = 0; i < numAttributes; i++) {
                EObject attr = (EObject)allAttributes.get(i);
                
                if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(attr)) {
                    SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(attr);
                    String name = columnAspect.getName(attr);
                    
                    if (hasNameMatch(attributeNames,name)) {
                        // set binding here
                        List bindings = this.reconcilerObject.getBindingList().getAll();
                        
                        BINDINGS_LOOP:
                        for (int numBindings = bindings.size(), j = 0; j < numBindings; j++) {
                            Binding binding = (Binding)bindings.get(j);
                            
                            if (binding.getAttributeName().equalsIgnoreCase(name)) {
                                binding.setAttribute(attr);
                                break BINDINGS_LOOP;
                            }
                        }
                    }
                }
            }
        }

        
        // SQL Modifications
        if(this.reconcilerObject.hasValidSqlModifications()) {
            String newSegmentSql = this.reconcilerObject.getModifiedSql();
            QueryValidator validator = new TransformationValidator(this.transMappingRoot);
            QueryValidationResult result = validator.validateSql(newSegmentSql, QueryValidator.SELECT_TRNS, false, false);
            QueryCommand newSegmentCommand = null;
            if(result.isParsable()) {
                newSegmentCommand = (QueryCommand)result.getCommand();
            }
            Command command = SqlMappingRootCache.getSelectCommand(this.transMappingRoot);
            if(command!=null && command instanceof SetQuery) {
                SetQuery unionQuery = (SetQuery)command.clone();
                switch (uIndex) {
                    case 0:
                        unionQuery.setLeftQuery(newSegmentCommand);
                        break;
                    case 1:
                        unionQuery.setRightQuery(newSegmentCommand);
                        break;
                }
                String newUnionSql = unionQuery.toString();
                TransformationHelper.setSelectSqlString(this.transMappingRoot,newUnionSql,true,txnSource);
            }
        }
        
        if(this.reconcilerObject.hasTargetAttributesToDelete()) {
            List attributes = this.reconcilerObject.getTargetAttributesToDelete();
            TransformationMappingHelper.removeTargetAttributes(this.transMappingRoot,attributes,true,txnSource);
        }
        
        // Target Attribute Name Changes
        if(this.reconcilerObject.hasTargetAttributesToRename()) {
            this.reconcilerObject.applyTargetAttributeRenames(txnSource);
        }
        
        // Target Attribute Type Changes
        if(this.reconcilerObject.hasTargetAttributeTypeMods()) {
            this.reconcilerObject.applyTargetAttributeTypeMods(txnSource);
        }
        
        AttributeMappingHelper.updateAttributeMappings(this.transMappingRoot,txnSource);
        // Check ordering
        boolean wasReordered = TransformationMappingHelper.orderGroupAttributes(this.transMappingRoot, true, reconcilerObject.getModifiedCommand());
    
        // Throw up a dialog here that warns the user that they need to reconcile
        // ALL UNION SEGMENTS to insure ordering of all Symbols is as desired.
        if( wasReordered ) {
            Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
            MessageDialog.openWarning( shell, REORDER_DETECTED_TITLE, REORDER_DETECTED_MESSAGE);
        }
        
    }

    /**
     * Determine if the list has an entry matching the supplied String (ignoring the case).
     * @param list the list of Strings
     * @param str the str to look for in the list
     * @return 'true' if name match exists in the list, 'false' if not.
     */
    private boolean hasNameMatch(List list, String str) {
        boolean hasMatch = false;
        Iterator listIter = list.iterator();
        // Iterate list1, look for match in list2
        while(listIter.hasNext()) {
            String listStr = (String)listIter.next();
            if(listStr!=null && listStr.equalsIgnoreCase(str)) {
                hasMatch = true;
                break;
            }
        }
        return hasMatch;
    }
                
    public boolean isPrimarySelectClause() {
        return this.isPrimarySelectClause;
    }
}
