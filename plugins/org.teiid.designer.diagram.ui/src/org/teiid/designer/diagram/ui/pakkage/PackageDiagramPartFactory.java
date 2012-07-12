/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.pakkage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.diagram.ui.connection.AnchorManager;
import org.teiid.designer.diagram.ui.connection.BlockAnchorManager;
import org.teiid.designer.diagram.ui.connection.DiagramUmlAssociation;
import org.teiid.designer.diagram.ui.connection.DiagramUmlDependency;
import org.teiid.designer.diagram.ui.connection.DiagramUmlGeneralization;
import org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart;
import org.teiid.designer.diagram.ui.editor.DiagramViewer;
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;
import org.teiid.designer.diagram.ui.model.LabelModelNode;
import org.teiid.designer.diagram.ui.notation.NotationPartGenerator;
import org.teiid.designer.diagram.ui.notation.uml.part.ForeignKeyLinkEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlAssociationEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlAssociationLinkEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlAttributeEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlDependencyLinkEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlGeneralizationLinkEditPart;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlOperationEditPart;
import org.teiid.designer.diagram.ui.part.AbstractDefaultEditPart;
import org.teiid.designer.diagram.ui.part.AbstractDiagramEditPart;
import org.teiid.designer.diagram.ui.part.AbstractDiagramEditPartFactory;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.diagram.ui.part.LabelEditPart;
import org.teiid.designer.diagram.ui.part.RelationalDropEditPartHelper;


/**
 * PackageDiagramPartFactory
 */
public class PackageDiagramPartFactory extends AbstractDiagramEditPartFactory implements DiagramUiConstants {
    private DiagramFigureFactory figureFactory;
    private static final String diagramTypeId = PluginConstants.PACKAGE_DIAGRAM_TYPE_ID;

    @Override
    public EditPart createEditPart( EditPart iContext,
                                    Object iModel ) {
        EditPart editPart = createEditPart(iContext, iModel, diagramTypeId);

        return editPart;
    }

    public EditPart createEditPart( EditPart iContext,
                                    Object iModel,
                                    String diagramType ) {
        EditPart editPart = null;

        if (figureFactory == null) figureFactory = new PackageDiagramFigureFactory();

        if (iModel instanceof PackageDiagramNode) {
            editPart = new PackageDiagramEditPart();
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
        } else if (iModel instanceof LabelModelNode) {
            editPart = new LabelEditPart();
        } else if (iModel instanceof DiagramUmlGeneralization) {
            editPart = new UmlGeneralizationLinkEditPart();
        } else if (iModel instanceof DiagramUmlDependency) {
            editPart = new UmlDependencyLinkEditPart();
        } else if (iModel instanceof DiagramUmlAssociation) {
            editPart = new UmlAssociationLinkEditPart();
        } else {
            // Here's where we get the notation manager and tell it to create an EditPart
            // for this modelObject. So it'll come back in whatever "Notation" it desires.
            NotationPartGenerator generator = DiagramUiPlugin.getDiagramNotationManager().getEditPartGenerator(getNotationId());
            if (generator != null) {
                editPart = generator.createEditPart(iContext, iModel, diagramType);
            } else {
                ModelerCore.Util.log(IStatus.ERROR, Util.getString(Errors.PART_GENERATOR_FAILURE));
            }
        }

        if (editPart != null) {
            if (editPart instanceof NodeConnectionEditPart) {
                editPart.setModel(iModel);
                ((NodeConnectionEditPart)editPart).setDiagramViewer((DiagramViewer)iContext.getViewer());
                ((NodeConnectionEditPart)editPart).setSourceAndTarget(iContext);
            } else if (editPart instanceof DiagramEditPart) {
                editPart.setModel(iModel);
                ((DiagramEditPart)editPart).setNotationId(getNotationId());
                ((DiagramEditPart)editPart).setSelectionHandler(getSelectionHandler());
                ((DiagramEditPart)editPart).setDiagramTypeId(diagramType);

                if (editPart instanceof UmlClassifierEditPart || editPart instanceof UmlAttributeEditPart
                    || editPart instanceof UmlAssociationEditPart || editPart instanceof UmlOperationEditPart) {
                    ((AbstractDefaultEditPart)editPart).setDropHelper(new RelationalDropEditPartHelper((DiagramEditPart)editPart));
                }
            }

        } else {
            if (diagramType.equals(diagramTypeId)) ModelerCore.Util.log(IStatus.ERROR, Util.getString(Errors.EDIT_PART_FAILURE));
        }

        if (editPart instanceof DiagramEditPart) {
            ((DiagramEditPart)editPart).setUnderConstruction(true);
        }
        return editPart;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    @Override
    public NodeConnectionEditPart getConnectionEditPart() {
        return new ForeignKeyLinkEditPart();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getAnchorManager(org.teiid.designer.diagram.ui.part.DiagramEditPart)
     */
    public AnchorManager getAnchorManager( DiagramEditPart editPart ) {
        return new BlockAnchorManager(editPart);
    }

}
