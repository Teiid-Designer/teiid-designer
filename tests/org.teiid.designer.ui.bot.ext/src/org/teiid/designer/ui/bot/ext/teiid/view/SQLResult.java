package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SQLResult {

	public static final String STATUS_SUCCEEDED = "Succeeded";
	
	private SWTBotView view;
	
	private SWTBotTreeItem resultRow;
	
	public SQLResult(SWTBotView view, SWTBotTreeItem resultRow) {
		this.resultRow = resultRow;
		this.view = view;
	}

	public String getStatus(){
		return resultRow.cell(0);
	}
	
	public int getCount(){
		getBot().cTabItem("Result1").activate();
		return getBot().table().rowCount();
	}
	
	private SWTBot getBot(){
		return view.bot();
	}
}
