package net.sourceforge.sqlexplorer.ext;
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeViewer;

import net.sourceforge.sqlexplorer.dbviewer.model.CatalogNode;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.SchemaNode;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

public abstract class DefaultSessionPlugin extends DefaultPlugin implements ISessionPlugin {

	public boolean sessionStarted(SessionTreeNode sessionNode){
		return true;
	}


	public void sessionEnding(SessionTreeNode sessionNode){
	}

	/**
	 * Let app know what extra types of objects in object tree that
	 * plugin can handle.
	 */
	public IDbModel[] getSchemaAddedTypes(SchemaNode schemaNode, SessionTreeNode session){
		return null;
	}
	public IDbModel[] getCatalogAddedTypes(CatalogNode catalogNode, SessionTreeNode session){
		return null;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.ext.ISessionPlugin#getDbRootAddedTypes(net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode, net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode)
	 */
	public IDbModel[] getDbRootAddedTypes(DatabaseNode root, SessionTreeNode session) {
		
		return null;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.ext.ISessionPlugin#getTypeActionsAdded(net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode, net.sourceforge.sqlexplorer.dbviewer.model.IDbModel)
	 */
	public IAction[] getTypeActionsAdded(SessionTreeNode sessionNode, IDbModel node, TreeViewer tv) {
		
		return null;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.ext.ISessionPlugin#getAddedActions(net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode, net.sourceforge.sqlexplorer.dbviewer.model.IDbModel)
	 */
	public IAction[] getAddedActions(SessionTreeNode sessionNode, IDbModel node, TreeViewer tv) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.ext.IPlugin#getIconMap()
	 */
	@Override
    public Map getIconMap() {
		return new HashMap();
	}
	public IActivablePanel[] getAddedPanels(SessionTreeNode sessionNode, IDbModel node){
		return null;
	}

}

