package org.teiid.designer.ui.bot.ext.teiid.matcher;

import org.eclipse.draw2d.IFigure;

/**
 * 
 * @author apodhrad
 * 
 */
public abstract class FigureMatcherFactory {

	public static org.hamcrest.Matcher<IFigure> withLabel(String label) {
		return org.teiid.designer.ui.bot.ext.teiid.matcher.WithLabel.withLabel(label);
	}

	public static org.hamcrest.Matcher<IFigure> withBounds(int width, int height) {
		return org.teiid.designer.ui.bot.ext.teiid.matcher.WithBounds.withBounds(width, height);
	}
}
