/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.IDiagramType;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.NotationFigureGenerator;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlAssociationFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlAttributeFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierContainerFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlOperationFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlPackageFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAssociationNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAttributeNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlOperationNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * UmlFigureGenerator
 */
public class UmlFigureGenerator implements NotationFigureGenerator {
    private static final int UML_CLASS = 1;
    private static final int UML_PACKAGE = 2;
    private static final int UML_CLASS_CONTAINER = 3;
    private static final int UML_ATTRIBUTE = 4;
    private static final int UML_ASSOCIATION = 5;
    private static final int UML_OPERATION = 6;
    private static final int UML_CLASS_IN_CLASS = 7;

    /**
     * Construct an instance of UmlFigureGenerator.
     * 
     */
    public UmlFigureGenerator() {
        super();
    }

    public Figure createFigure(Object modelObject) {
        int objectType = getObjectType(modelObject);
        
        Figure newFigure = null;
        ColorPalette colorPalette = null;
        switch( objectType ) {
            
            case UML_PACKAGE: {
                UmlPackageNode node = (UmlPackageNode)modelObject;
                String nodeName = node.getName();
                Image icon = null;
                if( node.getModelObject() != null ) {
                    colorPalette = getColorPalette(node);
                    icon = getImage(node);
                }
                if( nodeName == null || nodeName.equalsIgnoreCase("null")) //$NON-NLS-1$
                    nodeName = "UnknownPackage"; //$NON-NLS-1$
                
                String path = node.getPath();
                if( node.hideLocation())
                    path = null;
                newFigure = new UmlPackageFigure(node.getStereotype(), nodeName, path, icon, colorPalette);
                
            } break;
            
            case UML_CLASS_IN_CLASS: {
                UmlClassifierNode node = (UmlClassifierNode)modelObject;
                
                Image icon = null;
                if( node.getModelObject() != null ) {
                    colorPalette = getColorPalette(node);
                    icon = getImage(node);
                }
                newFigure = new UmlClassifierFigure(node, node.getStereotype(), node.getName(), null, icon, colorPalette);
                
            } break;
        
            case UML_CLASS: {
                UmlClassifierNode node = (UmlClassifierNode)modelObject;
                
                Image icon = null;
                if( node.getModelObject() != null ) {
                    colorPalette = getColorPalette(node);
                    icon = getImage(node);
                }
                String path = node.getPath();
                if( node.hideLocation())
                    path = null;
                newFigure = new UmlClassifierFigure(node,  node.getStereotype(), node.getName(), path, icon, colorPalette);
                
            } break;
            
            case UML_CLASS_CONTAINER: {
                DiagramModelNode node = (DiagramModelNode)modelObject;
                colorPalette = getColorPalette(node);
                newFigure = new UmlClassifierContainerFigure(node, colorPalette);
            } break;
            
            case UML_ATTRIBUTE: {
                UmlAttributeNode node = (UmlAttributeNode)modelObject;
                String signature = node.getSignature();
                                  
                Image icon = null;
                if( node.getModelObject() != null ) {
                    colorPalette = getColorPalette(node);
                    icon = getImage(node);
                }
                UmlAttributeFigure figure = new UmlAttributeFigure(signature, icon, colorPalette);
                figure.updateForType(node);
                newFigure = figure;
            } break;
            
            case UML_ASSOCIATION: {
                UmlAssociationNode node = (UmlAssociationNode)modelObject;
                String signature = node.getSignature();
                    
                Image icon = null;
                if( node.getModelObject() != null ) {
                    colorPalette = getColorPalette(node);
                    icon = getImage(node);
                }
                newFigure = new UmlAssociationFigure(signature, icon, colorPalette);
            } break;
            
            case UML_OPERATION: {
                UmlOperationNode node = (UmlOperationNode)modelObject;
				String signature = node.getSignature();
                                  
				Image icon = null;
				if( node.getModelObject() != null ) {
					colorPalette = getColorPalette(node);
                    icon = getImage(node);
				}
                newFigure = new UmlOperationFigure(signature, icon, colorPalette); 
            } break;
        }
        if( newFigure != null && modelObject instanceof DiagramModelNode ) {
            DiagramModelNode node = (DiagramModelNode)modelObject;
            ((DiagramFigure)newFigure).setDiagramModelNode(node);
            if( node.hasErrors())
                ((DiagramFigure)newFigure).updateForError(true);
            else if( node.hasWarnings() )
                ((DiagramFigure)newFigure).updateForWarning(true);
        }
        return newFigure;
    }
    
    protected int getObjectType( Object modelObject ) {
        int objectType = -1;
        
        if( modelObject != null ) {
            if (modelObject instanceof UmlPackageNode) {
                objectType = UML_PACKAGE;
            }   else if (modelObject instanceof UmlClassifierNode) {
                if( ((DiagramModelNode)modelObject).getParent() instanceof UmlClassifierContainerNode )
                    objectType = UML_CLASS_IN_CLASS;
                else 
                    objectType = UML_CLASS;
            } else if (modelObject instanceof UmlClassifierContainerNode){
                objectType = UML_CLASS_CONTAINER;
            } else if (modelObject instanceof UmlAttributeNode) {
                objectType = UML_ATTRIBUTE;
            } else if (modelObject instanceof UmlAssociationNode) {
                objectType = UML_ASSOCIATION;
            }  else if (modelObject instanceof UmlOperationNode) {
                objectType = UML_OPERATION;
            }
        }
        return objectType;
    }
    
    public ColorPaletteManager getColorPaletteManager(DiagramModelNode modelObject) {
        int objectType = getObjectType(modelObject);
        
        ColorPaletteManager cpm = null;
        String diagramType = null;
        DiagramModelNode dmn = null;
        // Need to get the "Diagram" for a diagramModelObject
        if( objectType == UML_PACKAGE || 
            objectType == UML_CLASS ) {
            dmn = modelObject.getParent();

        } else if( objectType == UML_CLASS_CONTAINER ) {
            DiagramModelNode classNode = modelObject.getParent();
            dmn = classNode.getParent();
        } else if( objectType == UML_ATTRIBUTE ||
                   objectType == UML_ASSOCIATION ||
                   objectType == UML_OPERATION ) {
            DiagramModelNode parentClassNode = modelObject.getParent().getParent();
            if( getObjectType(parentClassNode) == UML_CLASS_IN_CLASS) {
                return getColorPaletteManager(parentClassNode);
            }
            DiagramModelNode classContainerNode = modelObject.getParent();
            dmn = classContainerNode.getParent().getParent();
        } else if(objectType == UML_CLASS_IN_CLASS) {
            DiagramModelNode classContainerNode = modelObject.getParent();
            DiagramModelNode parentClassNode = classContainerNode.getParent();
            return getColorPaletteManager(parentClassNode);
        }
        
        if( dmn != null ) {
            diagramType = ((Diagram)dmn.getModelObject()).getType();
            if( diagramType != null) {
                IDiagramType diagram = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagramType);
                if( diagram != null ) {
                    cpm = diagram.getColorPaletteManager();
                }
            }
        }
        
        return cpm;
    }
    
    private Image getImage(DiagramModelNode node ) {
        Image icon = null;
        if( node instanceof UmlModelNode ) {
            Object imageURL = ((UmlModelNode)node).getUmlAspect().getImage(node.getModelObject());
            icon = ModelUtilities.getModelObjectLabelProvider().getImage(node.getModelObject(), imageURL);
        }
        if( icon == null )
            icon = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider().getImage(node.getModelObject());
        
        return icon;
    }
    
    private ColorPalette getColorPalette(DiagramModelNode inputNode) {
        int objectType = getObjectType(inputNode);
        DiagramModelNode dmn = inputNode;

        // Container nodes don't have an EObject model object, so in this case
        // we get it's diagramNode parent.
        if( objectType == UML_CLASS_CONTAINER ) {
            dmn = inputNode.getParent();
        }
        
        return getColorPaletteManager(dmn).getColorPalette(dmn.getModelObject());
    }
}

