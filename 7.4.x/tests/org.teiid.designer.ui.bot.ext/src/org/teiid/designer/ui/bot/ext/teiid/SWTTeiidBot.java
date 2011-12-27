/*******************************************************************************
 * Copyright (c) 2009 Obeo
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mariot Chauvin <mariot.chauvin@obeo.fr> - initial API and implementation
 *******************************************************************************/
package org.teiid.designer.ui.bot.ext.teiid;

import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;



/**
 * SWTBot extension for Teiid Designer support.
 * 
 * @author psrna
 *
 */
public class SWTTeiidBot extends SWTWorkbenchBot {

	
	public SWTBotTeiidCanvas getTeiidCanvas(int index) {
		List<? extends FigureCanvas> canvases = getFinder().findControls(WidgetMatcherFactory.widgetOfType(FigureCanvas.class));		
		SWTBotTeiidCanvas canvas = new SWTBotTeiidCanvas(canvases.get(0));
		return canvas;
	}
	
}
