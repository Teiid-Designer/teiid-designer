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
package net.sourceforge.sqlexplorer.gef.commands;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.gef.model.AbstractModelObject;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.model.Table;


import org.eclipse.gef.commands.Command;

/**
 * @author MAZZOLINI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoveObjectModelCommand extends Command {
	AbstractModelObject tb;
	AbstractModelObject sc;
	/**
	 * 
	 */
	public RemoveObjectModelCommand() {
		super();
	}


	/**
	 * @param table
	 */
	public void setObjectModel(AbstractModelObject table) {
		this.tb=table;
		this.sc=(AbstractModelObject)table.getParent();
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
    public void execute() {
		sc.removeChild(tb);
		if(tb instanceof Table){
			((Table)tb).removeLinks((Schema) sc);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
    public void redo() {
		execute();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
    public void undo() {
		sc.addChild(tb);
		if(tb instanceof Table){
			((Table)tb).createLinks((Schema) sc);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	@Override
    public String getLabel() {
		
		return Messages.getString("RemoveObjectModelCommand.Remove_object__1")+tb; //$NON-NLS-1$
	}

}
