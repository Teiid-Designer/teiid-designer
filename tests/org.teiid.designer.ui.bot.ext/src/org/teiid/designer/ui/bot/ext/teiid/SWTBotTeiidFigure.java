package org.teiid.designer.ui.bot.ext.teiid;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;
import org.eclipse.ui.internal.handlers.WizardHandler.New;

import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.transformation.ui.figure.TransformationFigure;

public abstract class SWTBotTeiidFigure extends AbstractSWTBot<FigureCanvas>{

	
	
	public SWTBotTeiidFigure(FigureCanvas c){
		super(c);
	}

	abstract public void doubleClick();
}
