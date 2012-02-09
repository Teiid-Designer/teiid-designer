package org.teiid.designer.ui.bot.ext.teiid.perspective;

import org.teiid.designer.ui.bot.ext.teiid.view.DataSourceExplorer;
import org.teiid.designer.ui.bot.ext.teiid.view.SQLResultView;

/**
 * Represents a Database development perspective. It is a singleton.
 * 
 * @author Lucia Jelinkova
 *
 */
public class DatabaseDevelopmentPerspective extends Perspective {

	private static final String NAME = "Database Development";
	
	private static final DatabaseDevelopmentPerspective INSTANCE = new DatabaseDevelopmentPerspective();
	
	private DataSourceExplorer explorerView;
	
	private SQLResultView sqlResultsView;
	
	private DatabaseDevelopmentPerspective() {
		super(NAME);
	}

	public static final DatabaseDevelopmentPerspective getInstance(){
		INSTANCE.open();
		return INSTANCE;
	}
	
	public DataSourceExplorer getExplorerView() {
		if (explorerView == null){
			explorerView = new DataSourceExplorer();
		}
		explorerView.activate();
		return explorerView;
	}
	
	public SQLResultView getSqlResultsView() {
		if (sqlResultsView == null){
			sqlResultsView = new SQLResultView();
		}
		sqlResultsView.activate();
		return sqlResultsView;
	}
}
