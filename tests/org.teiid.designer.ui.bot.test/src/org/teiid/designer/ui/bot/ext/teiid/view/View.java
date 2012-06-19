package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;

public class View {

	private String name;
	
	public View(String name) {
		this.name = name;
	}
	
	public void activate(){
		getView().show();
		getView().setFocus();
	}
	
	protected SWTBotView getView(){
		return SWTBotFactory.getBot().viewByTitle(name);
	}
	
	protected SWTBot getBot(){
		return getView().bot();
	}
}
