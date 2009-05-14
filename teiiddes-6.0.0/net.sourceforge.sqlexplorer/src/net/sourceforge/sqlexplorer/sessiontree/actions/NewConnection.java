package net.sourceforge.sqlexplorer.sessiontree.actions;
/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Mazzolini
 *
 */
public class NewConnection extends Action {
	ISQLAlias alias;
	/**
	 * @param alias
	 */
	public NewConnection(ISQLAlias alias) {
		this.alias=alias;
	}
	@Override
    public void run(){
		final DriverModel driverModel=SQLExplorerPlugin.getDefault().getDriverModel();
		final IPreferenceStore store=SQLExplorerPlugin.getDefault().getPreferenceStore();
		final SQLDriverManager driverMgr=SQLExplorerPlugin.getDefault().getSQLDriverManager();

		OpenPasswordConnectDialogAction openDlgAction=
			new OpenPasswordConnectDialogAction(null,alias,driverModel,store,driverMgr);
		openDlgAction.run();
					
	}
	@Override
    public String getText(){
		String name=alias.getName();
		name=name.replace('@','_');
		return name;
	}

}
