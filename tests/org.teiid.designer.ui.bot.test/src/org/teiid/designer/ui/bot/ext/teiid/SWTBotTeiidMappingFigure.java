package org.teiid.designer.ui.bot.ext.teiid;

import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polygon;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

import com.metamatrix.modeler.mapping.ui.figure.MappingExtentFigure;

public class SWTBotTeiidMappingFigure extends SWTBotTeiidFigure {

	MappingExtentFigure figure;

	public SWTBotTeiidMappingFigure(MappingExtentFigure figure, FigureCanvas w)
			throws WidgetNotFoundException {
		super(w);
		this.figure = figure;
	}

	@Override
	public void doubleClick() {

		UIThreadRunnable.syncExec(new VoidResult() {

			@Override
			public void run() {
				SWTBotGefFigureCanvas c = new SWTBotGefFigureCanvas(widget);
				List<IFigure> children = figure.getChildren();
				for (IFigure child : children) {
					if (child instanceof Polygon) {
						int x = child.getBounds().getCenter().x;
						int y = child.getBounds().getCenter().y;
						c.mouseMoveDoubleClick(x, y);
						break;
					}
				}
			}
		});

	}
}
