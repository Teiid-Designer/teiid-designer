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

//import net.sourceforge.jfacedbc.DatabaseModel;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;



import org.eclipse.swt.widgets.Composite;




public class DataBaseSessionTreeNode  implements ISessionTreeNode {

	ArrayList ls = new ArrayList();
	
	
	public Object[] getChildren() {
		return ls.toArray();
	}
	SessionTreeNode parent;
	
	
	Composite c;
	DetailManager dm;
	DataBaseSessionTreeNode(SessionTreeNode p){
		
		parent=p;
		p.add(this);
		
		
	}

	public Object getParent() {
		return parent;
	}

	
	
	@Override
    public String toString(){
		return Messages.getString("Structure_1"); //$NON-NLS-1$
	}
	

	public void refresh() {
		//dbModel=new DatabaseModel(parent,dm,vm.jface.getPluginManager());
		//tv.setInput(dbModel);
		
	}

}

