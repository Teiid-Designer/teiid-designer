/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.figure;

import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.ToolTipUtil;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;

/**
 * AbstractDiagramFigure
 */
public abstract class AbstractDiagramFigure extends Figure implements DiagramFigure {
    private ColorPalette colorPalette;
    private DiagramModelNode diagramModelNode;
    
    /**
     * Construct an instance of AbstractDiagramFigure.
     * 
     */
    public AbstractDiagramFigure(ColorPalette colorPalette) {
        super();
        this.colorPalette = colorPalette;
    }
    
    public AbstractDiagramFigure(DiagramModelNode diagramNode, ColorPalette colorPalette) {
        super();
        this.colorPalette = colorPalette;
        diagramModelNode = diagramNode;
    }

    public void setDiagramModelNode(DiagramModelNode diagramNode) {
        diagramModelNode = diagramNode;
    }

    public void layoutFigure() {
        super.layout();
        
    }
    
    public void activate() {
        // Default implementation does nothing;
    }
    
    public void deactivate() {
        // Default implementation does nothing;
    }
    
    @Override
    protected boolean useLocalCoordinates(){
        return true;
    }
    
    public void updateForSize(Dimension newSize ) {
        this.setSize(newSize);
    }
    
    public void updateForLocation(Point newLocation ) {
        this.setLocation(newLocation);
    }
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForName(java.lang.String, org.eclipse.swt.graphics.Image)
	 */
	public void updateForName(String newName, Image icon) {
		// XXX Auto-generated method stub
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForName(java.lang.String)
	 */
    public void updateForName(String newName ) {
        // Default implementation does nothing
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForFont(org.eclipse.swt.graphics.Font)
     */
    public void updateForFont( Font font ) {
        // Default implementation does nothing
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForError(boolean)
     */
    public void updateForError(boolean hasErrors) {
        // Default does nothing
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#updateForWarning(boolean)
     */
    public void updateForWarning(boolean hasWarnings) {
        // Default does nothing
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    public void hiliteBackground(Color hiliteColor) {
        // Default does nothing
    }
    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    public void shouldHiliteBackground(Color hiliteColor) {
        // Default does nothing
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#refreshFont()
     */
    public void refreshFont( ) {
        // Default implementation does nothing
    }
    
    
    public Color getColor(int colorId ) {
        if( colorPalette != null )
            return colorPalette.getColor(colorId);
        
        return ColorConstants.white;
    }
    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#showSelected(int)
     */
    public void showSelected(boolean selected) {
        // Default does nothong.  Left to concrete classes.
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#addFirstOverlayImage(org.eclipse.swt.graphics.Image)
     */
    public void addEditButton(Image image) {
        // Default Does Nothing
    }
    
	/*
	 *  (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#addUpperLeftButton(org.eclipse.swt.graphics.Image)
	 */
	public void addUpperLeftButton(Image image) {
		// Default Does Nothing
	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#getDiagramModelNode()
     */
    public DiagramModelNode getDiagramModelNode() {
        return diagramModelNode;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#addImage(org.eclipse.swt.graphics.Image, int)
     */
    public void addImage(Image image, int positionIndex) {
        // Default does nothing;
    }
    
	/**
	 * @see org.eclipse.draw2d.IFigure#setToolTip(IFigure)
	 */
	public void setToolTip(String toolTipString) {
		super.setToolTip(createToolTip(toolTipString));
	}
	
	protected IFigure createToolTip(String toolTipString) {
		return ToolTipUtil.createToolTip(toolTipString);
	}
	
	public IFigure createToolTip(List toolTipStrings) {
		
		return ToolTipUtil.createToolTip(toolTipStrings);
	}
	
	public IFigure createButtonToolTip() {
		Button editButton = new Button("R"); //$NON-NLS-1$  
		editButton.setSize(
					new Dimension( 10, 10 ) );

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						System.out.println(" Tooltip Button Pressed!!!"); //$NON-NLS-1$  
					}
				});
			}
		});

		return editButton;
	}


	/**
	 * @return
	 */
	public ColorPalette getColorPalette() {
		return colorPalette;
	}



}
