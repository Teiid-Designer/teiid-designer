package org.teiid.designer.ui.bot.ext.teiid.matcher;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 * 
 * @author apodhrad
 *
 */
public class WithLabel extends BaseMatcher<IFigure> {

	private String label;

	public WithLabel(String label) {
		super();
		this.label = label;
	}

	@Override
	public boolean matches(Object item) {
		if (item instanceof Label) {
			return ((Label) item).getText().equals(label);
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("with label '").appendText(label).appendText("'");
	}

	@Factory
	public static WithLabel withLabel(String label) {
		return new WithLabel(label);
	}

}
