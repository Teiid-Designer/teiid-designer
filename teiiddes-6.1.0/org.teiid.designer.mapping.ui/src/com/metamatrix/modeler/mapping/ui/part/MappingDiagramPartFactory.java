/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.part;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.ChopBoxAnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingPartFactory;
import com.metamatrix.modeler.diagram.ui.drawing.model.DrawingModelNode;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.notation.NotationPartGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.LabelEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.mapping.ui.DebugConstants;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.connection.EnumeratedTypeLink;
import com.metamatrix.modeler.mapping.ui.connection.MappingClassAnchorManager;
import com.metamatrix.modeler.mapping.ui.connection.MappingExtentAnchorManager;
import com.metamatrix.modeler.mapping.ui.connection.MappingLink;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.mapping.ui.figure.MappingDiagramFigureFactory;
import com.metamatrix.modeler.mapping.ui.model.MappingDiagramNode;
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;
import com.metamatrix.modeler.mapping.ui.model.StagingTableExtentNode;
import com.metamatrix.modeler.mapping.ui.model.SummaryExtentNode;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;
import com.metamatrix.modeler.transformation.ui.connection.TransformationAnchorManager;
import com.metamatrix.modeler.transformation.ui.connection.TransformationLink;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;
import com.metamatrix.modeler.transformation.ui.part.TransformationEditPart;
import com.metamatrix.modeler.transformation.ui.part.TransformationLinkEditPart;

/**
 * TransformationPartFactory
 */
public class MappingDiagramPartFactory extends AbstractDiagramEditPartFactory implements UiConstants {
    private DrawingPartFactory drawingPartFactory;
    private DiagramFigureFactory figureFactory;
    private static final String diagramTypeId = PluginConstants.MAPPING_DIAGRAM_TYPE_ID;
    /**
     * @see org.eclipse.gef.EditPartFactory#createEditPart(EditPart, Object)
    **/
    @Override
    public EditPart createEditPart(EditPart iContext, Object iModel) {
        EditPart editPart = null;

        if( drawingPartFactory == null )
            drawingPartFactory = new DrawingPartFactory();
            
        if( figureFactory == null )
            figureFactory = new MappingDiagramFigureFactory();

        if( iModel instanceof DrawingModelNode ) {
            editPart = drawingPartFactory.createEditPart(iContext, iModel);
        } else if( iModel instanceof MappingDiagramNode ) {
            editPart = new MappingDiagramEditPart();
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
            Object transform = getTransformation(iModel);
            if( transform != null ) {
                ((MappingDiagramEditPart)editPart).setDropHelper(new MappingTransformationDropEditPartHelper(transform));
            }
        } else if( iModel instanceof SummaryExtentNode ) {
            editPart = new SummaryExtentEditPart(diagramTypeId);
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
            ((DiagramEditPart)editPart).setResizable(false);
        } else if( iModel instanceof MappingExtentNode || iModel instanceof StagingTableExtentNode) {
            editPart = new MappingExtentEditPart(diagramTypeId);
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
            ((DiagramEditPart)editPart).setResizable(false);
        } else if( iModel instanceof TransformationNode ) {
            editPart = new TransformationEditPart(diagramTypeId);
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
            ((DiagramEditPart)editPart).setResizable(false);
            Object transform = getTransformation(iModel);
            if( transform != null ) {
                ((TransformationEditPart)editPart).setDropHelper(new MappingTransformationDropEditPartHelper(transform));
            }
        } else if( iModel instanceof LabelModelNode ) {
            editPart = new LabelEditPart();
            ((DiagramEditPart)editPart).setResizable(false);
        } else  if( iModel instanceof TransformationLink ) {
            editPart = getTransformationConnectionEditPart(iModel);
        } else  if( iModel instanceof MappingLink ) {
            editPart = getConnectionEditPart(iModel);
        } else if (iModel instanceof EnumeratedTypeLink) {
            editPart = new EnumeratedTypeLinkEditPart();
        } else {
            // Here's where we get the notation manager and tell it to create an EditPart
            // for this modelObject.  So it'll come back in whatever "Notation" it desires.
            NotationPartGenerator generator = DiagramUiPlugin.getDiagramNotationManager().getEditPartGenerator(getNotationId());
            if( generator != null ) {
                editPart = generator.createEditPart(iContext, iModel, diagramTypeId);
                if( editPart instanceof UmlClassifierEditPart ) {
                    if( editPart instanceof UmlClassifierEditPart ) {
                        Object transform = getTransformation(iModel);
                        if( transform != null ) {
                            ((UmlClassifierEditPart)editPart).setDropHelper(new MappingTransformationDropEditPartHelper(transform));
                        }
                    }
                    // Check to see if it's a Staging table, then set the hideLocation to TRUE
                    EObject eObj = ((DiagramModelNode)iModel).getModelObject();
                    if( MappingDiagramUtil.isMappingSqlTable(eObj))
                        ((DiagramModelNode)iModel).setHideLocation(true);
                                        
                }
            } else {
                ModelerCore.Util.log( IStatus.ERROR, Util.getString(DiagramUiConstants.Errors.PART_GENERATOR_FAILURE));
            }
        }
        
        if (editPart != null) {
            if( editPart instanceof NodeConnectionEditPart ) {
                editPart.setModel(iModel);
                ((NodeConnectionEditPart)editPart).setSourceAndTarget(iContext);
            } else if( editPart instanceof DiagramEditPart ){
                editPart.setModel(iModel);
                ((DiagramEditPart)editPart).setNotationId( getNotationId());
                ((DiagramEditPart)editPart).setSelectionHandler(getSelectionHandler());
                ((DiagramEditPart)editPart).setDiagramTypeId(diagramTypeId);
            }
            
            if( UiConstants.Util.isDebugEnabled(DebugConstants.MAPPING_DIAGRAM_EDIT_PARTS)) {  
                String message = "Added Edit Part = " + editPart; //$NON-NLS-1$
                UiConstants.Util.print(DebugConstants.MAPPING_DIAGRAM_EDIT_PARTS, message);
            }
        } else {
            ModelerCore.Util.log( IStatus.ERROR, Util.getString(DiagramUiConstants.Errors.EDIT_PART_FAILURE));
        }
        
        if( editPart instanceof DiagramEditPart ) {
            ((DiagramEditPart)editPart).setUnderConstruction(true);
        }
        return editPart;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    public NodeConnectionEditPart getConnectionEditPart(Object iModel) {
        return new MappingLinkEditPart();
    }

    public NodeConnectionEditPart getTransformationConnectionEditPart(Object iModel) {
        return new TransformationLinkEditPart();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getAnchorManager(com.metamatrix.modeler.diagram.ui.part.DiagramEditPart)
     */
    public AnchorManager getAnchorManager(DiagramEditPart editPart) {
        if( editPart instanceof UmlClassifierEditPart ) {
            return new MappingClassAnchorManager(editPart);
        } else if( editPart instanceof MappingExtentEditPart ) {
            return new MappingExtentAnchorManager(editPart);
        } else if( editPart instanceof TransformationEditPart ) {
            return new ChopBoxAnchorManager(editPart);
        } else {
            return new TransformationAnchorManager(editPart);
        }
    }
    private Object getTransformation(Object iModel) {
        if( iModel instanceof MappingDiagramNode ) {
            Diagram diagram = (Diagram)((MappingDiagramNode)iModel).getModelObject();
            if( diagram != null )
                return TransformationSourceManager.getTransformationFromDiagram(diagram);
        } else if( iModel instanceof TransformationNode ) {
            return ((TransformationNode)iModel).getModelObject();
        } else if( iModel instanceof UmlClassifierNode ) {
            // check to see if it's the "Target"
            DiagramModelNode parentClassifierNode = DiagramUiUtilities.getClassifierParentNode((DiagramModelNode)iModel);
            if( parentClassifierNode != null ) {
                Object thisTransform = null;
                if(TransformationHelper.isValidSqlTransformationTarget(parentClassifierNode.getModelObject())) {
                    thisTransform = TransformationHelper.getTransformationMappingRoot(parentClassifierNode.getModelObject());
                    if( thisTransform != null ) {
                        // Now we get the parent (DiagramEditPart)
                        MappingDiagramNode tdep = (MappingDiagramNode)parentClassifierNode.getParent();
                        Diagram diagram = (Diagram)tdep.getModelObject();
                        if( diagram != null ) {
                            Object diagramTransform = TransformationSourceManager.getTransformationFromDiagram(diagram);
                            if( diagramTransform != null && diagramTransform.equals(thisTransform))
                                return thisTransform;
}
                    }
                }
                // Now we check for is Virtual?
            }
        }
        return null;
    }
}
