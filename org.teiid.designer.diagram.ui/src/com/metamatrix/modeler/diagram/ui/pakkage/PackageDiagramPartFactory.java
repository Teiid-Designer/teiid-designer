/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.pakkage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.BlockAnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlAssociation;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlDependency;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlGeneralization;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingPartFactory;
import com.metamatrix.modeler.diagram.ui.drawing.model.DrawingModelNode;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.notation.NotationPartGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.ForeignKeyLinkEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAssociationEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAssociationLinkEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAttributeEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlDependencyLinkEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlGeneralizationLinkEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlOperationEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.LabelEditPart;
import com.metamatrix.modeler.diagram.ui.part.RelationalDropEditPartHelper;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * PackageDiagramPartFactory
 */
public class PackageDiagramPartFactory extends AbstractDiagramEditPartFactory implements DiagramUiConstants  {
    protected DrawingPartFactory drawingPartFactory;
    private DiagramFigureFactory figureFactory;
    private static final String diagramTypeId = PluginConstants.PACKAGE_DIAGRAM_TYPE_ID;
    
	@Override
    public EditPart createEditPart(EditPart iContext, Object iModel) {
		EditPart editPart = createEditPart(iContext, iModel, diagramTypeId);
		
		return editPart;
	}
    
    public EditPart createEditPart(EditPart iContext, Object iModel, String diagramType) {
        EditPart editPart = null;

        if( drawingPartFactory == null )
            drawingPartFactory = new DrawingPartFactory();

        if( figureFactory == null )
            figureFactory = new PackageDiagramFigureFactory();

        if( iModel instanceof DrawingModelNode ) {
            editPart = drawingPartFactory.createEditPart(iContext, iModel);
        } else if( iModel instanceof PackageDiagramNode ) {
            editPart = new PackageDiagramEditPart();
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
        } else if( iModel instanceof LabelModelNode ) {
            editPart = new LabelEditPart();
        } else  if( iModel instanceof DiagramUmlGeneralization ) {
			editPart = new UmlGeneralizationLinkEditPart();
		} else  if( iModel instanceof DiagramUmlDependency ) {
            editPart = new UmlDependencyLinkEditPart();
        }  else  if( iModel instanceof DiagramUmlAssociation ) {
			editPart = new UmlAssociationLinkEditPart();
		}else {
            // Here's where we get the notation manager and tell it to create an EditPart
            // for this modelObject.  So it'll come back in whatever "Notation" it desires.
            NotationPartGenerator generator = DiagramUiPlugin.getDiagramNotationManager().getEditPartGenerator(getNotationId());
            if( generator != null ) {
                editPart = generator.createEditPart(iContext, iModel, diagramType);
            } else {
                ModelerCore.Util.log( IStatus.ERROR, Util.getString(Errors.PART_GENERATOR_FAILURE));
            }
        }
        
        if (editPart != null) {
            if( editPart instanceof NodeConnectionEditPart ) {
                editPart.setModel(iModel);
				((NodeConnectionEditPart)editPart).setDiagramViewer((DiagramViewer)iContext.getViewer());
                ((NodeConnectionEditPart)editPart).setSourceAndTarget(iContext);
            } else if( editPart instanceof DiagramEditPart ) {
                editPart.setModel(iModel);
                ((DiagramEditPart)editPart).setNotationId( getNotationId());
                ((DiagramEditPart)editPart).setSelectionHandler(getSelectionHandler());
                ((DiagramEditPart)editPart).setDiagramTypeId(diagramType);
                
                if( editPart instanceof UmlClassifierEditPart ||
                    editPart instanceof UmlAttributeEditPart ||
                    editPart instanceof UmlAssociationEditPart ||
                    editPart instanceof UmlOperationEditPart ) {
                    ((AbstractDefaultEditPart)editPart).setDropHelper(new RelationalDropEditPartHelper((DiagramEditPart)editPart));
                }
            }
            

        } else {
        	if( diagramType.equals(diagramTypeId))
            	ModelerCore.Util.log( IStatus.ERROR, Util.getString(Errors.EDIT_PART_FAILURE));
        }
        
        if( editPart instanceof DiagramEditPart ) {
            ((DiagramEditPart)editPart).setUnderConstruction(true);
        }
        return editPart;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    @Override
    public NodeConnectionEditPart getConnectionEditPart() {
        return new ForeignKeyLinkEditPart();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getAnchorManager(com.metamatrix.modeler.diagram.ui.part.DiagramEditPart)
     */
    public AnchorManager getAnchorManager(DiagramEditPart editPart) {
        return new BlockAnchorManager(editPart);
    }

}
