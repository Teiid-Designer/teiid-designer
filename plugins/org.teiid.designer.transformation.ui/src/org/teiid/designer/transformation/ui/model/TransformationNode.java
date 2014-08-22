/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.model.AbstractDiagramModelNode;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.transformation.util.SqlMappingRootCache;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.validation.SqlTransformationResult;
import org.teiid.query.ui.sqleditor.sql.SqlFormattingStrategy;




/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 *
 * @since 8.0
 */
public class TransformationNode extends AbstractDiagramModelNode {
    private static final String T_STRING = "T"; //$NON-NLS-1$
    private static final String U_STRING = "u"; //$NON-NLS-1$
    boolean logging = true;
    
    public TransformationNode(Diagram diagramModelObject, EObject modelObject ) {
        super( diagramModelObject, modelObject);
        setName(T_STRING);
    }
    
    @Override
    public String toString() {
        return "TransformNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public boolean isUnion() {
        return TransformationHelper.isUnionCommand(getModelObject());
    }
    
    public String getSubscript() {
        if( isUnion() )
            return U_STRING;
        
        return null;
    }
    
	public List<String> getToolTipStrings() {
		List<String> returnList = new ArrayList<String>();
        // Defect 23027 
        // Putting in defensive code here because the model may be in the process of being deleted and therefore will have no
        // eResource.
		if( getModelObject() != null && getModelObject().eResource() != null ) {
    		String sqlString = TransformationHelper.getSelectSqlString(getModelObject());
    		SqlFormattingStrategy sfs = new SqlFormattingStrategy();
    		String newString = sfs.format(sqlString);
    		if( newString != null && newString.length() > 1)
    			returnList.add(newString);
        }
		return returnList;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.teiid.designer.diagram.ui.model.AbstractDiagramModelNode#setErrorState()
	 */
	@Override
    public void setErrorState() {
        int state = DiagramUiConstants.NO_ERRORS;
        errorState = false;
        warningState = false;

        if (getModelObject() != null) {
        	// The modelObject for the t-node is the SQL mapping root. need to get the Target table to find it's "errors"
        	EObject targetTable = TransformationHelper.getTransformationTarget(getModelObject());
            state = DiagramEditorUtil.getErrorState(targetTable);
            if (state == DiagramUiConstants.HAS_ERROR) { 
            	// Note that if a virtual table has an ERROR it MAY NOT be the transformation.
            	// So check the SQL cached status
            	
            	SqlTransformationResult result = SqlMappingRootCache.getSqlTransformationStatus(
            					(SqlTransformationMappingRoot)getModelObject(), QueryValidator.SELECT_TRNS, true, null);
            			
            	if( result != null && result.getMaxSeverity() == IStatus.ERROR) {
            		errorState = true;
            		return;
            	}
            	
            	result = SqlMappingRootCache.getSqlTransformationStatus(
            			(SqlTransformationMappingRoot)getModelObject(), QueryValidator.INSERT_TRNS, true, null);
            			
            	if( result != null && result.getMaxSeverity() == IStatus.ERROR) {
            		errorState = true;
            		return;
            	}
            	
            	result = SqlMappingRootCache.getSqlTransformationStatus(
    					(SqlTransformationMappingRoot)getModelObject(), QueryValidator.UPDATE_TRNS, true, null);
    			
		    	if( result != null && result.getMaxSeverity() == IStatus.ERROR) {
		    		errorState = true;
		    		return;
		    	}
		    	
		    	result = SqlMappingRootCache.getSqlTransformationStatus(
    					(SqlTransformationMappingRoot)getModelObject(), QueryValidator.DELETE_TRNS, true, null);
    			
		    	if( result != null && result.getMaxSeverity() == IStatus.ERROR) {
		    		errorState = true;
		    		return;
		    	}
            	
            }
        }

    }
}



