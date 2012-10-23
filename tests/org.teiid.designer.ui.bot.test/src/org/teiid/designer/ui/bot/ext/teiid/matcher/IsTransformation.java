package org.teiid.designer.ui.bot.ext.teiid.matcher;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 * This matcher tells if a particular EditPart is a Transformation figure.
 * 
 * @author apodhrad
 * 
 */
public class IsTransformation extends BaseMatcher<EditPart> {

	public static final int WIDTH = 40;
	public static final int HEIGHT = 60;

	@Override
	public boolean matches(Object item) {
		if (item instanceof GraphicalEditPart) {
			IFigure figure = ((GraphicalEditPart) item).getFigure();
			Rectangle rectangle = figure.getBounds().getCopy();
			return rectangle.width == WIDTH && rectangle.height == HEIGHT;
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is a transformation edit part");
	}

	@Factory
	public static IsTransformation isTransformation() {
		return new IsTransformation();
	}

}
