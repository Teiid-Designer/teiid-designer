package org.teiid.designer.ui.bot.ext.teiid.matcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.ListResult;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.hamcrest.Matcher;

/**
 * 
 * @author apodhrad
 *
 */
public class WaitForFigure extends WaitForObjectCondition<IFigure> {

	private FigureCanvas figureCanvas;

	public WaitForFigure(Matcher<IFigure> matcher, FigureCanvas figureCanvas) {
		super(matcher);
		this.figureCanvas = figureCanvas;
	}

	@Override
	public String getFailureMessage() {
		return "Could not find figure matching: " + matcher;
	}

	@Override
	protected List<IFigure> findMatches() {
		return UIThreadRunnable.syncExec(new ListResult<IFigure>() {
			private List<IFigure> list = new ArrayList<IFigure>();

			@SuppressWarnings("unchecked")
			private void find(List<IFigure> figures, Matcher matcher) {
				for (IFigure figure : figures) {
					if (matcher.matches(figure)) {
						list.add(figure);
					}
					find(figure.getChildren(), matcher);
				}
			}

			public List<IFigure> run() {
				find(figureCanvas.getContents().getChildren(), matcher);
				return list;
			}
		});
	}

}
