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
package net.sourceforge.sqlexplorer.ext;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeViewer;

import net.sourceforge.sqlexplorer.dbviewer.model.CatalogNode;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.SchemaNode;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;




/**
 * Base interface for all plugins associated with a session.
 */
public interface ISessionPlugin extends IPlugin {

	/**
	 * Called when a session started.
	 *
	 * @param	session	The session that is starting.
	 *
	 * @return	<TT>true</TT> if plugin is applicable to passed
	 *			session else <TT>false</TT>.
	 */
	boolean sessionStarted(SessionTreeNode session);

	/**
	 * Called when a session shutdown.
	 */
	void sessionEnding(SessionTreeNode session);

	
	IDbModel []getSchemaAddedTypes(SchemaNode schemaNode, SessionTreeNode session);
	IDbModel []getCatalogAddedTypes(CatalogNode schemaNode, SessionTreeNode session);
	IDbModel []getDbRootAddedTypes(DatabaseNode root,SessionTreeNode session);

	IAction[] getTypeActionsAdded(SessionTreeNode sessionNode, IDbModel node,TreeViewer tv);


	IAction[] getAddedActions(SessionTreeNode sessionNode, IDbModel node, TreeViewer tv);
	
	IActivablePanel[] getAddedPanels(SessionTreeNode sessionNode, IDbModel node);
}

