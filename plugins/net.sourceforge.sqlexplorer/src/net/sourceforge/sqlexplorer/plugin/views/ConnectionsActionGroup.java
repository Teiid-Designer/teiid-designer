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
package net.sourceforge.sqlexplorer.plugin.views;

import net.sourceforge.sqlexplorer.sessiontree.actions.CloseAllConnections;
import net.sourceforge.sqlexplorer.sessiontree.actions.CloseConnection;
import net.sourceforge.sqlexplorer.sessiontree.actions.NewSQLEditor;
import net.sourceforge.sqlexplorer.sessiontree.model.ISessionTreeNode;
import net.sourceforge.sqlexplorer.sessiontree.model.RootSessionTreeNode;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionGroup;

/**
 * @author Andrea Mazzolini
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConnectionsActionGroup extends ActionGroup{
	ConnectionsView view;
	IAction closeAll;
	IAction closeConn;
	IAction openEditor;
	public ConnectionsActionGroup(ConnectionsView view){
		this.view=view;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
    public void fillContextMenu(IMenuManager menuMgr) {
		ISessionTreeNode treeNode=getSelectedNode();
//		Object[] aliases=SQLExplorerPlugin.getDefault().getAliasModel().getElements();
//		if(aliases!=null && aliases.length>0){
//			IMenuManager manager = new MenuManager("New Connection...");
//			for(int i=0;i<aliases.length;i++){
//				final ISQLAlias alias=(ISQLAlias) aliases[i];
//				manager.add(new NewConnection(alias));
//			}
//			menuMgr.add(manager);
//		}
		if(treeNode==null)
			return;
		if(treeNode instanceof RootSessionTreeNode){
			closeAll=new CloseAllConnections((RootSessionTreeNode)treeNode);
			openEditor=new NewSQLEditor(null);
			menuMgr.add(closeAll);
			menuMgr.add(openEditor);
		}else if (treeNode instanceof SessionTreeNode){
			closeConn=new CloseConnection((SessionTreeNode)treeNode);
			openEditor=new NewSQLEditor((SessionTreeNode)treeNode);
			menuMgr.add(closeConn);
			menuMgr.add(openEditor);
		}
	}
	ISessionTreeNode getSelectedNode(){
		TreeViewer viewer=view.getTreeViewer();
		ISelection sel=viewer.getSelection();
		if(sel!=null){
			IStructuredSelection iSel=(IStructuredSelection)sel;
			ISessionTreeNode node=(ISessionTreeNode)iSel.getFirstElement();
			return node;
		}
		return null;
	}

}
