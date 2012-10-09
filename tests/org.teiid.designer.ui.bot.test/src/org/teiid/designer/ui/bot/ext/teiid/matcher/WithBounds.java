package org.teiid.designer.ui.bot.ext.teiid.matcher;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 * 
 * @author apodhrad
 * 
 */
public class WithBounds extends BaseMatcher<IFigure> {

	private int width;
	private int height;

	public WithBounds(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean matches(Object item) {
		if (item instanceof IFigure) {
			Rectangle rectangle = ((IFigure) item).getBounds().getCopy();
			return rectangle.width == width && rectangle.height == height;
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("with width " + width + " and height " + height);
	}

	@Factory
	public static WithBounds withBounds(int width, int height) {
		return new WithBounds(width, height);
	}

}
