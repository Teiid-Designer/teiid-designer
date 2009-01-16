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

package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;
import com.metamatrix.modeler.diagram.ui.model.AbstractDiagramModelNode;

/**
 * @author mdrilling
 *
 * Model Node for UML Classifier.
 */
abstract public class UmlModelNode extends AbstractDiagramModelNode {

    protected UmlDiagramAspect aspect;

    public UmlModelNode( Diagram diagramModelObject, EObject modelObject, UmlDiagramAspect aspect ) {
        super( diagramModelObject, modelObject );
        this.aspect = aspect;
    }
    
    public UmlModelNode(  EObject modelObject, UmlDiagramAspect aspect ) {
        super( modelObject );
        this.aspect = aspect;
    }
    
	public UmlModelNode( Diagram diagramModelObject, EObject modelObject, UmlDiagramAspect aspect, boolean isNested ) {
		super( diagramModelObject, modelObject, !isNested );
		this.aspect = aspect;
	}
    
//    public String getName() {
//        return aspect.getSignature(getModelObject(),UmlClassifier.SIGNATURE_NAME);
//    }
//    
    /**
     * Return the Visibility constant for the given eObject
     * @return the visibility string
     */
    public String getVisibility() {
        int iVis = aspect.getVisibility(getModelObject());
        String visibility = ""; //$NON-NLS-1$
        switch (iVis) {
            case UmlDiagramAspect.VISIBILITY_PRIVATE:
                visibility="private"; //$NON-NLS-1$
                break;
            case UmlDiagramAspect.VISIBILITY_PROTECTED:
                visibility="protected"; //$NON-NLS-1$
                break;
            case UmlDiagramAspect.VISIBILITY_PUBLIC:
                visibility="public"; //$NON-NLS-1$
                break;
            default:
                break; 
        }
        return visibility;
    }
    
    /**
     * Return the Sterotype string for this ModelNode
     * @return the Sterotype string
     */
    public String getStereotype( ) {
        String sterotypeString = null;
        if( getModelObject() != null && aspect != null ) {
            String subString = aspect.getStereotype(getModelObject());
            if( subString != null && subString.length() > 0)
            	sterotypeString = "<<" + subString + ">>"; //$NON-NLS-2$ //$NON-NLS-1$
        }

        return sterotypeString;
    }
    
    public String getLocation() {
        return "location"; //$NON-NLS-1$
    }
    
    public UmlDiagramAspect getUmlAspect() {
        return this.aspect;
    }

}

