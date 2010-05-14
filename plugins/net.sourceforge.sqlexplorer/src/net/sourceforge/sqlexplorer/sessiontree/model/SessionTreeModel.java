package net.sourceforge.sqlexplorer.sessiontree.model;



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

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;

public class SessionTreeModel implements ISessionTreeNode{
	RootSessionTreeNode root=new RootSessionTreeNode();
	private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);
	public SessionTreeModel(){

	}

	/**
	 * @return
	 */
	public RootSessionTreeNode getRoot() {

		return root;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.sessiontree.model.ISessionTreeNode#getChildren()
	 */
	public Object[] getChildren() {
		return new Object[]{root};
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.sessiontree.model.ISessionTreeNode#getParent()
	 */
	public Object getParent() {
		return root.getParent();
	}

	/**
	 * @param conn
	 * @param alias
	 */
	public void createSessionTreeNode(SQLConnection conn, ISQLAlias alias,IProgressMonitor monitor, String pswd) throws InterruptedException {
		//MessageDialog.openInformation(null,"createSessionTreeNode","");
		SessionTreeNode tt=null;
		try{
			tt=new SessionTreeNode(conn,alias,this,monitor,pswd);
		}finally{
			modelChanged(tt);
		}
		//MessageDialog.openInformation(null,"createdSessionTreeNode","");


	}

    /**
     * Obtain an existing <code>SessionTreeNode</code> having the same connection and alias. The connection must be open.
     * The alias is checked to see if the name and URL are the same.
     * @param theAlias the alias being searched for
     * @return the session or <code>null</code>
     * @since 4.3
     */
    public SessionTreeNode findOpenSessionTreeNode(ISQLAlias theAlias) {
        SessionTreeNode result = null;
        Object[] sessions = root.getChildren();

        if ((sessions != null) && (sessions.length != 0)) {
            for (int i = 0; i < sessions.length; ++i) {
                if (sessions[i] instanceof SessionTreeNode) {
                    SessionTreeNode sessionNode = (SessionTreeNode)sessions[i];
                    ISQLAlias alias = sessionNode.getAlias();

                    if (alias.getName().equals(theAlias.getName())
                        && alias.getUrl().equals(theAlias.getUrl())) {

                        if (sessionNode.getConnection().getTimeClosed() == null) {
                            result = sessionNode;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

	/**
	 * @param listener
	 */
	public void addListener(SessionTreeModelChangedListener listener) {
		listeners.add(listener);
	}
	public void removeListener(SessionTreeModelChangedListener listener){
		listeners.remove(listener);
	}
	public void modelChanged(SessionTreeNode stn){

		Object []ls=listeners.getListeners();
		//MessageDialog.openInformation(null,"modelChanged",""+ls.length);
		for(int i=0;i<ls.length;++i){
			try{
				((SessionTreeModelChangedListener)ls[i]).modelChanged(stn);
			}catch(Throwable e){
			}

		}
	}
}

