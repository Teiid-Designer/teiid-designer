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


import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.ui.SessionViewer;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andrea Mazzolini
 *
 */
public class ConnectionsView extends ViewPart{
	
	private TreeViewer treeViewer;
	//private SQLDriverManager _driverMgr;
	//private AliasModel aliasModel;
	//private DriverModel driverModel;
	@Override
    public void createPartControl(Composite parent) {
		SQLExplorerPlugin plugin=SQLExplorerPlugin.getDefault();
		
		treeViewer = new SessionViewer(parent,SWT.V_SCROLL|SWT.H_SCROLL,plugin.stm,this);
		
		this.getSite().setSelectionProvider(treeViewer);
		final ConnectionsActionGroup actGroup=new ConnectionsActionGroup(this);
		MenuManager  menuMgr= new MenuManager("#ConnectionsPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		Menu fDbContextMenu= menuMgr.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(fDbContextMenu);
		menuMgr.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager manager){
				//MessageDialog.openInformation(null,"fillContextMenu","");
				actGroup.fillContextMenu(manager);
			}
		});
		
//		getViewSite().getActionBars().getToolBarManager().add(new NewConnectionDropDownAction());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
    public void setFocus() {
		
	}

	/**
	 * 
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
		
	}

}
	