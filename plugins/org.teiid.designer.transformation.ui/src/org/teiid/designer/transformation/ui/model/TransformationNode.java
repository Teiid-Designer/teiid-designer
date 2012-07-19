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
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.model.AbstractDiagramModelNode;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.util.TransformationHelper;
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
    
	public List getToolTipStrings() {
		List returnList = new ArrayList();
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
}



