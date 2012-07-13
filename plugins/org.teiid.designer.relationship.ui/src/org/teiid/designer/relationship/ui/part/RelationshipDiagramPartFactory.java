/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.part;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.connection.AnchorManager;
import org.teiid.designer.diagram.ui.connection.DiagramUmlDependency;
import org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart;
import org.teiid.designer.diagram.ui.editor.DiagramViewer;
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;
import org.teiid.designer.diagram.ui.model.LabelModelNode;
import org.teiid.designer.diagram.ui.part.AbstractDiagramEditPart;
import org.teiid.designer.diagram.ui.part.AbstractDiagramEditPartFactory;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.diagram.ui.part.LabelEditPart;
import org.teiid.designer.relationship.ui.PluginConstants;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.relationship.ui.connection.RelationshipAnchorManager;
import org.teiid.designer.relationship.ui.connection.RelationshipLink;
import org.teiid.designer.relationship.ui.figure.RelationshipDiagramFigureFactory;
import org.teiid.designer.relationship.ui.model.FocusModelNode;
import org.teiid.designer.relationship.ui.model.RelationshipDiagramNode;
import org.teiid.designer.relationship.ui.model.RelationshipFolderModelNode;
import org.teiid.designer.relationship.ui.model.RelationshipModelNode;
import org.teiid.designer.relationship.ui.model.RelationshipTypeModelNode;


/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class RelationshipDiagramPartFactory extends AbstractDiagramEditPartFactory implements UiConstants {

    private DiagramFigureFactory figureFactory;
    //	private static final String THIS_CLASS = "RelationshipDiagramPartFactory"; //$NON-NLS-1$
    private static final String diagramTypeId = PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID;

    /**
     * @see org.eclipse.gef.EditPartFactory#createEditPart(EditPart, Object)
     **/
    @Override
    public EditPart createEditPart( EditPart iContext,
                                    Object iModel ) {
        EditPart editPart = null;

        if (figureFactory == null) figureFactory = new RelationshipDiagramFigureFactory();

        // Create the appropriate Edit Part
        switch (getModelTypeID(iModel)) {

            case RelationshipModelTypes.DIAGRAM: {
                editPart = new RelationshipDiagramEditPart();
                ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
            }
                break;

            case RelationshipModelTypes.RELATIONSHIP: {
                editPart = new RelationshipNodeEditPart(diagramTypeId);
                ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
                ((DiagramEditPart)editPart).setResizable(false);
            }
                break;

            case RelationshipModelTypes.FOLDER: {
                editPart = new RelationshipFolderEditPart(diagramTypeId);
                ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
                ((DiagramEditPart)editPart).setResizable(false);
            }
                break;

            case RelationshipModelTypes.ROLE: {

            }
                break;

            case RelationshipModelTypes.TYPE: {
                editPart = new RelationshipTypeNodeEditPart(diagramTypeId);
                ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
                ((DiagramEditPart)editPart).setResizable(false);
            }
                break;

            case RelationshipModelTypes.LABEL: {
                editPart = new LabelEditPart();
                ((DiagramEditPart)editPart).setResizable(false);
            }
                break;

            case RelationshipModelTypes.LINK: {
                editPart = getConnectionEditPart(iModel);
            }
                break;

            case RelationshipModelTypes.TYPE_LINK: {
                editPart = getConnectionEditPart(iModel);
            }
                break;

            case RelationshipModelTypes.FOCUS_NODE: {
                editPart = new FocusNodeEditPart(diagramTypeId);
                ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
                ((DiagramEditPart)editPart).setResizable(false);
            }
                break;

            default: {
                ModelerCore.Util.log(IStatus.ERROR, Util.getString(DiagramUiConstants.Errors.EDIT_PART_FAILURE)
                                                    + " Model Object = " + iModel); //$NON-NLS-1$);

            }
                break;
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
                ((DiagramEditPart)editPart).setDiagramTypeId(diagramTypeId);
            }

            // }
        }

        return editPart;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    public NodeConnectionEditPart getConnectionEditPart( Object iModel ) {
        if (iModel instanceof RelationshipLink) return new RelationshipLinkEditPart();

        if (iModel instanceof DiagramUmlDependency) {
            return new RelationshipTypeLinkEditPart();
        }

        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getAnchorManager(org.teiid.designer.diagram.ui.part.DiagramEditPart)
     */
    @Override
	public AnchorManager getAnchorManager( DiagramEditPart editPart ) {
        return new RelationshipAnchorManager(editPart);
    }

    private int getModelTypeID( Object iModel ) {
        int typeID = RelationshipModelTypes.OTHER;

        if (iModel instanceof RelationshipDiagramNode) {
            typeID = RelationshipModelTypes.DIAGRAM;
        } else if (iModel instanceof RelationshipModelNode) {
            typeID = RelationshipModelTypes.RELATIONSHIP;
        } else if (iModel instanceof RelationshipTypeModelNode) {
            typeID = RelationshipModelTypes.TYPE;
        } else if (iModel instanceof LabelModelNode) {
            typeID = RelationshipModelTypes.LABEL;
        } else if (iModel instanceof FocusModelNode) {
            typeID = RelationshipModelTypes.FOCUS_NODE;
        } else if (iModel instanceof RelationshipLink) {
            typeID = RelationshipModelTypes.LINK;
        } else if (iModel instanceof RelationshipFolderModelNode) {
            typeID = RelationshipModelTypes.FOLDER;
        } else if (iModel instanceof DiagramUmlDependency) {
            typeID = RelationshipModelTypes.TYPE_LINK;
        }

        return typeID;
    }
}
