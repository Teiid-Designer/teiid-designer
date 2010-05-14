package net.sourceforge.sqlexplorer.plugin.views;
/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import net.sourceforge.sqlexplorer.AliasModel;
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.dialogs.AliasContainerGroup;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
//import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;

//import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.part.ViewPart;

public class AliasView extends ViewPart {

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    public void createPartControl(Composite parent) {
		final DriverModel driverModel=SQLExplorerPlugin.getDefault().getDriverModel();
		final AliasModel aliasModel=SQLExplorerPlugin.getDefault().getAliasModel();
		//final IPreferenceStore store=JFaceDbcPlugin.getDefault().getPreferenceStore();
		//final SQLDriverManager driverMgr=JFaceDbcPlugin.getDefault().getSQLDriverManager();
		//JFaceDbcPlugin.getWorkspace().
		//AliasContainerGroup group=
		new AliasContainerGroup(parent,aliasModel,driverModel);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
    public void setFocus() {
	}

}
