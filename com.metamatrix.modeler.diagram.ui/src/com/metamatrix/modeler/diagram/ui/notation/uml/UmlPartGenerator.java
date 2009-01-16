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

package com.metamatrix.modeler.diagram.ui.notation.uml;

import org.eclipse.gef.EditPart;

import com.metamatrix.modeler.diagram.ui.notation.NotationPartGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAssociationNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAttributeNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlOperationNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAssociationEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAttributeEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierContainerEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlOperationEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlPackageEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * UmlPartGenerator
 */
public class UmlPartGenerator implements NotationPartGenerator {

    String sNotationId;

    /**
     * @see org.eclipse.gef.EditPartFactory#createEditPart(EditPart, Object)
    **/
    public EditPart createEditPart(EditPart iContext, Object iModel, String diagramTypeId) {
        EditPart editPart = null;

        if (iModel instanceof UmlPackageNode) {
            editPart = new UmlPackageEditPart(diagramTypeId);
        } else if (iModel instanceof UmlClassifierNode) {
            editPart = new UmlClassifierEditPart(diagramTypeId);
        } else if (iModel instanceof UmlClassifierContainerNode) {
            editPart = new UmlClassifierContainerEditPart();
        } else if (iModel instanceof UmlAttributeNode) {
            editPart = new UmlAttributeEditPart();
        } else if (iModel instanceof UmlAssociationNode) {
            editPart = new UmlAssociationEditPart();
        } else if (iModel instanceof UmlOperationNode) {
			editPart = new UmlOperationEditPart();
		}
        
        if (editPart != null) {
            editPart.setModel(iModel);
            ((DiagramEditPart)editPart).setNotationId( getNotationId() );            
        }

        return editPart;
    }
    
    /**
      * 
      */
     public String getNotationId() {
         return sNotationId;
     }

     /**
      * 
      */
     public void setNotationId(String sNotationId) {
         this.sNotationId = sNotationId;
     }    

}
