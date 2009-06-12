package net.sourceforge.sqlexplorer.sessiontree.actions;

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
 

import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.LoggingProgress;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.RetrievingTableDataProgress;
import net.sourceforge.sqlexplorer.dialogs.PasswordConnDlg;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class OpenPasswordConnectDialogAction extends Action {
	
	Shell shell;
	ISQLAlias alias;
	DriverModel driverModel;
	IPreferenceStore store;
	SQLDriverManager dmgr;
	
	public OpenPasswordConnectDialogAction(Shell shell, ISQLAlias alias, DriverModel model,IPreferenceStore store,SQLDriverManager dmgr){
		this.shell=shell;
		this.alias=alias;
		this.driverModel=model;
		this.store=store;
		this.dmgr=dmgr;
		
	}
	@Override
    public void run(){
		PasswordConnDlg dlg=new PasswordConnDlg(shell,alias,driverModel,store);
		if(dlg.open()==Window.OK){
			String pswd=dlg.getPassword();
			String user=dlg.getUser();
			ISQLDriver dv=driverModel.getDriver(alias.getDriverIdentifier());
			try{
				LoggingProgress lp=new LoggingProgress(dmgr,dv,alias,user,pswd);
				ProgressMonitorDialog pg=new ProgressMonitorDialog(shell);
				pg.run(true, false, lp);
				if(lp.isOk()){
					SQLConnection conn=lp.getConn();
					boolean autoCommit=dlg.getAutoCommit();
					conn.setAutoCommit(autoCommit);
					if(autoCommit==false){
						conn.setCommitOnClose(dlg.getCommitOnClose());
					}
					RetrievingTableDataProgress rtdp=new RetrievingTableDataProgress(conn,alias,SQLExplorerPlugin.getDefault().stm,pswd);
					ProgressMonitorDialog pg2=new ProgressMonitorDialog(shell);
					pg2.run(true,true,rtdp);
				}else{
					MessageDialog.openError(shell,Messages.getString("Error..._4"),lp.getError());
				}

			}catch(java.lang.Exception e){
				SQLExplorerPlugin.error("Error Logging ",e); //$NON-NLS-1$
			}
		}

	}
}
