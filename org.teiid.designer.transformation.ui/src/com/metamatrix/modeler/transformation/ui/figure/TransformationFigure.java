/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.figure;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Font;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;


/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TransformationFigure extends AbstractDiagramFigure {
//	private static final Font tFont = new Font(null, "Comic Sans MS", 16, 3); //$NON-NLS-1$
//    private Polygon transformOutline;
    private ImageFigure transformationIcon;
//    private Label nameLabel;
    private Label subscript = new Label("u"); //$NON-NLS-1$
    private TransformationNode tNode = null;
    
//    private PointList transformPoints = new PointList();
    
    /**
     * 
     */
    public TransformationFigure(TransformationNode tNode, String name, ColorPalette colorPalette) {
        super(colorPalette);
        this.tNode = tNode;
        init(name);
        
        createComponent();

     }
    
    private void init(String name) {
        if( isOnDependencyDiagram() ) {
            if ( this.tNode.getModelObject() instanceof XQueryTransformationMappingRoot ) {
                transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.XTRANSFORMATION_NODE_ICON));
            } else {
                transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.TRANSFORMATION_NODE_ICON));
            }
        } else {
            if ( this.tNode.getModelObject() instanceof XQueryTransformationMappingRoot ) {
                transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.ARROW_XTRANSFORMATION_NODE_ICON));
            } else {
                transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.ARROW_TRANSFORMATION_NODE_ICON));
            }
        }
        this.add(transformationIcon);
        transformationIcon.setSize(transformationIcon.getPreferredSize());
//        transformOutline = new Polygon();
//        transformPoints.addPoint(0, 20);
//        transformPoints.addPoint(20, 0);
//        transformPoints.addPoint(40, 20);
//        transformPoints.addPoint(20, 40);
//        transformPoints.addPoint(0, 20);
//        transformOutline.setPoints(transformPoints);
//
//        this.add(transformOutline);

//        transformOutline.setLineWidth(2);
//        transformOutline.setForegroundColor(ColorConstants.darkBlue);
//        transformOutline.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
        
//        if( name != null ) {
//
//            nameLabel = new Label(name);
//            nameLabel.setFont(tFont);
//            this.add(nameLabel);
//
//            nameLabel.setForegroundColor(ColorConstants.black);
//            nameLabel.setBackgroundColor(this.getBackgroundColor());
//            setLabelSize(nameLabel);
//        }
        if( subscript != null ) {
            subscript.setFont(DiagramUiUtilities.getToolTipFont());
//            this.add(subscript);
            subscript.setForegroundColor(ColorConstants.black);
            subscript.setBackgroundColor(this.getBackgroundColor());
            setLabelSize(subscript);
        }
    }
    
    private void createComponent() {        
        setInitialSize();
        layoutThisFigure(this.getSize());
    }
    
    private void setLabelSize( Label label ) {

        Font theFont = label.getFont();

        int labelWidth = FigureUtilities.getTextExtents(label.getText(), theFont).width;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width;
        int labelHeight = FigureUtilities.getTextExtents(label.getText(), theFont).height;
    
        label.setSize(labelWidth, labelHeight);
    }
     
    private void setInitialSize() {

        int maxWidth = 40;
        int maxHeight = 60;
        if( isOnDependencyDiagram() ) {
            maxHeight = 40;
        }
        this.setSize(new Dimension(maxWidth, maxHeight));
    }
    
    private void layoutThisFigure(Dimension newSize) {

        int centerX = newSize.width/2;
        int centerY = newSize.height/2;
        
//        if( nameLabel != null ) {
//            nameLabel.setLocation( 
//                new Point(centerX - nameLabel.getBounds().width/2,
//                          centerY - nameLabel.getBounds().height/2) );
//        }
        if( this.getChildren().contains(subscript)) {
            if( isOnDependencyDiagram() ) {
                subscript.setLocation( new Point(centerX + 3, centerY - 2) );
            } else {
                subscript.setLocation( new Point(centerX + 7, centerY) );
            }
            
        }
//                new Point(8 + centerX - nameLabel.getBounds().width/2,
//                          12 + centerY - nameLabel.getBounds().height/2) );

    }
    
    @Override
    public void updateForSize(Dimension size){
        
//        int thisHeight = size.height;
//        int thisWidth = size.width;
//        replacePoint( 0, 0, thisHeight/2);
//        replacePoint( 1, thisWidth/2, 0);
//        replacePoint( 2, thisWidth, thisHeight/2);
//        replacePoint( 3, thisWidth/2, thisHeight);
//        replacePoint( 4, 0, thisHeight/2);
//        transformOutline.setPoints(transformPoints);
        
        this.layoutThisFigure(size);
        
        this.repaint();
        
    }
    
    public void layoutComponent() {
        this.layoutThisFigure(this.getSize());
    }
    
    public void updateSize() {
       
    }
    
//    private void replacePoint(int index, int newX, int newY ) {
//        transformPoints.setPoint(new Point(newX, newY), index);
//    }
    /** 
     * @see org.eclipse.draw2d.IFigure#getToolTip()
     * @since 4.2
     */
    @Override
    public IFigure getToolTip() {
        List toolTips = tNode.getToolTipStrings();
        if( toolTips != null && !toolTips.isEmpty() )
            super.setToolTip(super.createToolTip(toolTips));
        
        return super.getToolTip();
    }
    
    public void setSubscript(String ss) {
        // change text if changed, add if not added
        // remove if input string ss == NULL
        if( ss != null ) {
            if( !this.getChildren().contains(subscript) ) {
                if( subscript.getText() != null &&  !(subscript.getText().equals(ss)) ) {
                    subscript.setText(ss);
                }
                this.add(subscript);
            } else {
                if( subscript.getText() != null &&  !(subscript.getText().equals(ss)) ) {
                    subscript.setText(ss);
                }
            }
        } else {
            if( this.getChildren().contains(subscript) )
                this.remove(subscript);
        }
        layoutComponent();
    }
    
    private boolean isOnDependencyDiagram() {
        if( tNode != null ) {
            // Let's get the diagram for the tNode
            Diagram diagram = tNode.getDiagram();
            if( diagram != null && diagram.getType() != null && diagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) {
                return true;
            }
        }
        
        return false;
    }
}


