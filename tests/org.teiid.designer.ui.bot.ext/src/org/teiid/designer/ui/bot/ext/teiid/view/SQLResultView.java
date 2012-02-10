package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SQLResultView extends View{

	public SQLResultView() {
		super("SQL Results");
	}

	public SQLResult getByOperation(String operation){
		SWTBotTreeItem found = null;

		SWTBotTreeItem[] items = getBot().tree(0).getAllItems();
		for(SWTBotTreeItem item : items){

			if(item.cell(1).trim().equals(operation)){
				found = item;
				break;
			}
		}
		
		if(found == null){
			throw new WidgetNotFoundException("Cannot find sql result for operation " + operation);
		}

		found.click();
		return new SQLResult(getView(), found);
	}
}
