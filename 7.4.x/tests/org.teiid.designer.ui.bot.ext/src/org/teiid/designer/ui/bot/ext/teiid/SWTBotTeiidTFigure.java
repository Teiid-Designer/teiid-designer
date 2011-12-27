package org.teiid.designer.ui.bot.ext.teiid;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

import com.metamatrix.modeler.transformation.ui.figure.TransformationFigure;

public class SWTBotTeiidTFigure extends SWTBotTeiidFigure{

	private TransformationFigure tFig;
	
	public SWTBotTeiidTFigure(TransformationFigure fig, FigureCanvas c) {
		super(c);
		tFig = fig;
	}

	@Override
	public void doubleClick() {

		UIThreadRunnable.syncExec(new VoidResult() {
			
			@Override
			public void run() {
								
				// Move mouse
				Event event = new Event();
				event.type = SWT.MouseMove;
				event.x = widget.toDisplay(tFig.getBounds().getCenter().x, tFig.getBounds().getCenter().y).x;
				event.y = widget.toDisplay(tFig.getBounds().getCenter().x, tFig.getBounds().getCenter().y).y;
				widget.getDisplay().post(event);

				// Mouse down
				event = new Event();
				event.type = SWT.MouseDown;
				event.button = 1;
				widget.getDisplay().post(event);
				// Mouse Up
				event = new Event();
				event.type = SWT.MouseUp;
				event.button = 1;
				widget.getDisplay().post(event);
				// Mouse down
				event = new Event();
				event.type = SWT.MouseDown;
				event.button = 1;
				widget.getDisplay().post(event);
				// Mouse Up
				event = new Event();
				event.type = SWT.MouseUp;
				event.button = 1;
				widget.getDisplay().post(event);
				
			}
		});
	}

}
