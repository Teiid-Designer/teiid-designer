/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.figure;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Font;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.figure.AbstractDiagramFigure;
import org.teiid.designer.diagram.ui.util.DiagramUiUtilities;
import org.teiid.designer.diagram.ui.util.colors.ColorPalette;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.XQueryTransformationMappingRoot;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.model.TransformationNode;



/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 *
 * @since 8.0
 */
public class TransformationFigure extends AbstractDiagramFigure {
//	private static final Font tFont = new Font(null, "Comic Sans MS", 16, 3); //$NON-NLS-1$

    private ImageFigure transformationIcon;

    private Label subscript = new Label("u"); //$NON-NLS-1$
    private TransformationNode tNode = null;
    private ImageFigure errorIcon;
    private ImageFigure warningIcon;
    
    private int status = IStatus.OK;
    
    
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

        if( subscript != null ) {
            subscript.setFont(DiagramUiUtilities.getToolTipFont());
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
        
        if( this.getChildren().contains(subscript)) {
            if( isOnDependencyDiagram() ) {
                subscript.setLocation( new Point(centerX + 3, centerY - 2) );
            } else {
                subscript.setLocation( new Point(centerX + 7, centerY) );
            }
            
        }

    }
    
    @Override
    public void updateForSize(Dimension size){

        this.layoutThisFigure(size);
        
        this.repaint();
        
    }
    
    public void layoutComponent() {
        this.layoutThisFigure(this.getSize());
    }
    
    public void updateSize() {
       
    }
    
    /** 
     * @see org.eclipse.draw2d.IFigure#getToolTip()
     * @since 4.2
     */
    @Override
    public IFigure getToolTip() {
        List<String> toolTips = tNode.getToolTipStrings();
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
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#updateForError(boolean)
     */
    @Override
    public void updateForError(boolean hasErrors) {
        if( hasErrors ) {
            if( errorIcon == null ) {
            	updateTransformationIcon(IStatus.ERROR);
            	
                errorIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON));
                if( errorIcon != null ) {
                    this.add(errorIcon);
                    int centerX = this.getBounds().width/2;
                    int centerY = this.getBounds().height/2;
                    
                    if( isOnDependencyDiagram() ) {
                    	errorIcon.setLocation( new Point(centerX - 11, centerY -2 ) );
                    } else {
                    	errorIcon.setLocation( new Point(centerX - 7, centerY) );
                    }
                    errorIcon.setSize(errorIcon.getPreferredSize());
                }
            }
            status = IStatus.ERROR;
        } else if( errorIcon != null ) {
        	updateTransformationIcon(IStatus.OK);
            this.remove(errorIcon);
            errorIcon = null;
            status = IStatus.OK;
        }
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#updateForWarning(boolean)
     */
    @Override
    public void updateForWarning(boolean hasWarnings) {
        if( hasWarnings ) {
            if( warningIcon == null ) {
            	updateTransformationIcon(IStatus.WARNING);
            	
                warningIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON));
                if( warningIcon != null ) {
                    this.add(warningIcon);
                    int centerX = this.getBounds().width/2;
                    int centerY = this.getBounds().height/2;
                    
                    if( isOnDependencyDiagram() ) {
                    	warningIcon.setLocation( new Point(centerX - 3, centerY + 2) );
                    } else {
                    	warningIcon.setLocation( new Point(centerX - 7, centerY) );
                    }
                    warningIcon.setSize(warningIcon.getPreferredSize());
                }
            }
            status = IStatus.WARNING;
        } else if( warningIcon != null ) {
        	updateTransformationIcon(IStatus.OK);
            this.remove(warningIcon);
            warningIcon = null;
            status = IStatus.OK;
        }
    }
    
    private void updateTransformationIcon(int newStatus) {
    	if( newStatus == status ) return;
    	
    	this.remove(transformationIcon);

        if( isOnDependencyDiagram() ) {
        	if( newStatus > IStatus.INFO ) transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.TRANSFORMATION_NODE_ICON_ERROR));
        	else transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.TRANSFORMATION_NODE_ICON));
        } else {
        	if( newStatus > IStatus.INFO ) transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.ARROW_TRANSFORMATION_NODE_ICON_ERROR));
        	else transformationIcon = new ImageFigure(UiPlugin.getDefault().getImage(PluginConstants.Images.ARROW_TRANSFORMATION_NODE_ICON));
        }
        this.add(transformationIcon);
        transformationIcon.setSize(transformationIcon.getPreferredSize());
    }
}


