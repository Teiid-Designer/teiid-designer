/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;

/**
 * @author mdrilling
 *
 * Model Node for UML Attribute.
 */
public class UmlOperationNode extends UmlModelNode {

    public UmlOperationNode( EObject modelObject, UmlOperation aspect ) {
        super( modelObject, aspect );
    }
    
    @Override
    public String getName() {
		if( getModelObject() != null )
        	return aspect.getSignature(getModelObject(),UmlOperation.SIGNATURE_NAME);
      	return null;
    }
    
	public String getSignature() {
		if( getModelObject() != null )
			return ((UmlOperation)aspect).getSignature(getModelObject(),UmlOperation.SIGNATURE_NAME | UmlOperation.SIGNATURE_PARAMETERS | UmlOperation.SIGNATURE_RETURN);
		return null;
	}
    
    @Override
    public void setName(String name) {
        aspect.setSignature(getModelObject(),name);
//        super.setName(name);
    }

    public String getReturnType() {
		if( getModelObject() != null )
        	return aspect.getSignature(getModelObject(),UmlOperation.SIGNATURE_RETURN);
		return null;
    }
    
    @Override
    public String toString() {
        return "UmlOperationNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    
    public void refreshForNameChange(){
        if( getDiagramModelObject() != null && !isReadOnly()) {
            String oldName = "xxxXxxx"; //$NON-NLS-1$
			String signature = getSignature();

			DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
			firePropertyChange(DiagramNodeProperties.NAME, oldName, signature);
		} else
			firePropertyChange(DiagramNodeProperties.NAME, null, null);
    }
}
