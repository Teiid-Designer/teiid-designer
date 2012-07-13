/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.notation.uml;

import org.eclipse.gef.EditPart;
import org.teiid.designer.diagram.ui.notation.NotationPartGenerator;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlAssociationNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlAttributeNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlClassifierNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlOperationNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlPackageNode;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlAssociationEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlAttributeEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlClassifierContainerEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlOperationEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlPackageEditPart;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;

/**
 * UmlPartGenerator
 */
public class UmlPartGenerator implements NotationPartGenerator {

    String sNotationId;

    /**
     * @see org.eclipse.gef.EditPartFactory#createEditPart(EditPart, Object)
    **/
    @Override
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
