package org.teiid.designer.ui.bot.ext.teiid;

import org.eclipse.draw2d.FigureCanvas;

import com.metamatrix.modeler.transformation.ui.figure.TransformationFigure;

public class SWTBotTeiidTFigure extends SWTBotTeiidFigure{

	private TransformationFigure tFig;
	
	public SWTBotTeiidTFigure(TransformationFigure fig, FigureCanvas c) {
		super(c);
		tFig = fig;
	}

	@Override
	public void doubleClick() {
		doubleClickXY(tFig.getBounds().getCenter().x, 
				      tFig.getBounds().getCenter().y);
	}

}
