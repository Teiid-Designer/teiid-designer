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
package net.sourceforge.sqlexplorer.dbviewer.actions;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.actions.editdialog.EditorDialog;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.Action;

public class TableDirectEdit extends Action {

	SessionTreeNode sessionNode;
	TableNode node;
	public TableDirectEdit(SessionTreeNode sessionNode, TableNode node) {
		this.node=node;
		this.sessionNode=sessionNode;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#getText()
	 */
	@Override
    public String getText() {
		return Messages.getString("TableDirectEdit.Edit_table_data_1"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
    public void run() {
		try{
			EditorDialog ed=new EditorDialog(sessionNode, node);
			ed.open();
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error in editorDialog ",e);
		}
		
	}

}
