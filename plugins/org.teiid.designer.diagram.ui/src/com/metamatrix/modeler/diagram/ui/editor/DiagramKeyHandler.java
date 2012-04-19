/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Key Handler for binding key presses to the {@link DiagramViewer}.
 * 
 * Unlike {@link GraphicalViewerKeyHandler}, this tries to keep the scrolling
 * key bindings simple with a key press incrementing the scroll location rather
 * than navigate the figures on the canvas.
 */
public class DiagramKeyHandler extends KeyHandler {

	private GraphicalViewer viewer;

	/**
	 * Create new instance
	 * 
	 * @param viewer
	 */
	public DiagramKeyHandler(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	private boolean isViewerMirrored() {
		return (viewer.getControl().getStyle() & SWT.MIRRORED) != 0;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!(viewer.getControl() instanceof FigureCanvas))
			return false;

		FigureCanvas figCanvas = (FigureCanvas) viewer.getControl();
		Viewport viewport = figCanvas.getViewport();
		Point location = viewport.getViewLocation();
		Rectangle clientArea = viewport.getClientArea(Rectangle.SINGLETON);

		// Scaling rectangle used for arrow key scrolling
		Rectangle area = clientArea.getCopy().scale(.1);

		// Scaling rectangle used for paging key scrolling
		Rectangle pageArea = clientArea.getCopy().scale(.3);

		switch (event.keyCode) {
		case SWT.ARROW_DOWN:
			figCanvas.scrollToY(location.y + area.height);
			return true;
		case SWT.PAGE_DOWN:
			figCanvas.scrollToY(location.y + pageArea.height);
			return true;
		case SWT.ARROW_UP:
			figCanvas.scrollToY(location.y - area.height);
			return true;
		case SWT.PAGE_UP:
			figCanvas.scrollToY(location.y - pageArea.height);
			return true;
		case SWT.ARROW_LEFT:
			if (isViewerMirrored())
				figCanvas.scrollToX(location.x + area.width);
			else
				figCanvas.scrollToX(location.x - area.width);

			return true;
		case SWT.ARROW_RIGHT:
			if (isViewerMirrored())
				figCanvas.scrollToX(location.x - area.width);
			else
				figCanvas.scrollToX(location.x + area.width);

			return true;
		}

		return super.keyPressed(event);
	}
}
