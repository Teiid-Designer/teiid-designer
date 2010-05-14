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

import java.util.ArrayList;


import net.sourceforge.sqlexplorer.Messages;


/**
 * The root node in the session tree;
 * It shows the number of active sessions
 */
public class RootSessionTreeNode  implements ISessionTreeNode {
	
	public RootSessionTreeNode(){
	}
	public SessionTreeNode[] getSessionTreeNodes(){
		return (SessionTreeNode[])ls.toArray(new SessionTreeNode[0]);
	}

	private ArrayList ls=new ArrayList(10);
	
	public Object[] getChildren() {
		return ls.toArray();
	}

	/**
	 * @see org.gnu.amaz.ISessionTreeNode#getParent()
	 */
	public Object getParent() {
		return null;
	}
	@Override
    public String toString(){
		int sz=ls.size();
		if(sz==0)
			return Messages.getString("No_Active_Sessions_1"); //$NON-NLS-1$
		else if(sz==1)
			return Messages.getString("1_Active_Session_2"); //$NON-NLS-1$
		else
			return ""+ls.size()+ Messages.getString("_Active_Sessions_4"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	public void add(ISessionTreeNode node){
		ls.add(node);
	}
	public void remove(SessionTreeNode nd){ls.remove(nd);}
	public void closeAllConnections(){
		int s=ls.size();
		Object obj[]=ls.toArray();
		for(int i=0;i<s;i++){
			((SessionTreeNode)obj[i]).close();
		}
	}
}

