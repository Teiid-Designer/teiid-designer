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
package net.sourceforge.sqlexplorer.sessiontree.actions;

import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mazzolini
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Rollback extends Action implements IViewActionDelegate {
	SessionTreeNode _stn;
	private ImageDescriptor img=ImageDescriptor.createFromURL(SqlexplorerImages.getRollbackIcon());
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if(_stn!=null)
			_stn.rollback();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection){
			IStructuredSelection iss=(IStructuredSelection)selection;
			Object obj=iss.getFirstElement();
			if(obj instanceof SessionTreeNode){
				SessionTreeNode stn=(SessionTreeNode)obj;
				if(!stn.isAutoCommitMode()){
					_stn=stn;
					action.setEnabled(true);
				}
					
				else{
					action.setEnabled(false);
					_stn=null;
				}
					
			}else{
				action.setEnabled(false);
				_stn=null;
			}
		}
	}
	@Override
    public ImageDescriptor getImageDescriptor() {
				return img;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IAction#getHoverImageDescriptor()
			 */
			@Override
            public ImageDescriptor getHoverImageDescriptor() {
				return img;
			}
}
