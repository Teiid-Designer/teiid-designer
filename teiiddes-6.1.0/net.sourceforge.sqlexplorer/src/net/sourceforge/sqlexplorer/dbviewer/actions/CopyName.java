/*
 * Copyright (C) 2003 Andrea Mazzolini
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
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;


public class CopyName extends Action {
	IDbModel nd;
	ImageDescriptor ids=ImageDescriptor.createFromURL(SqlexplorerImages.getCopyAlias());
	public CopyName(IDbModel nd) {
		this.nd=nd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#getImageDescriptor()
	 */
	@Override
    public ImageDescriptor getImageDescriptor() {
		return ids;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#getText()
	 */
	@Override
    public String getText() {
		return Messages.getString("CopyName.Copy_Name_1"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
    public void run() {
		Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[]{nd.toString()}, new Transfer[]{textTransfer});
	}

}
