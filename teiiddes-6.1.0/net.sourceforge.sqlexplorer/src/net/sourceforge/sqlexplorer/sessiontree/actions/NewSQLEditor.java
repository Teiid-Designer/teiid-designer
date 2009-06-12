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
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;

public class NewSQLEditor extends Action implements IViewActionDelegate {

	private ImageDescriptor img=ImageDescriptor.createFromURL(SqlexplorerImages.getOpenSQLIcon());
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	IViewPart view;
	SessionTreeNode _stn;
	public void init(IViewPart view) {
		this.view=view;

	}
	public NewSQLEditor(SessionTreeNode node){
		_stn=node;
        
        // set enabled state
        ISelection selection = (this._stn == null) ? StructuredSelection.EMPTY : new StructuredSelection(_stn);
        selectionChanged(null, selection);
	}
	public NewSQLEditor(){
        this(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		run();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection){
			IStructuredSelection iss=(IStructuredSelection)selection;
			Object obj=iss.getFirstElement();
			if(obj instanceof SessionTreeNode){
				_stn=(SessionTreeNode)obj;
			}else{
				_stn=null;
			}
		}

        boolean enable = (this._stn != null);
        setEnabled(enable);
        
        if (action != null) {
            action.setEnabled(enable);
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
    public void run() {
		SQLEditorInput input = new SQLEditorInput("SQL Editor ("+SQLExplorerPlugin.getDefault().getNextElement()+").sql");
		//SessionTreeNode sessionNode=(SessionTreeNode) ((SqlEditNode)sel.getFirstElement()).getParent();
		input.setSessionNode(_stn);
		IWorkbenchPage page= SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try{
			page.openEditor(input,"net.sourceforge.sqlexplorer.plugin.editors.SQLEditor");
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error creating sql editor",e);
		}
	}
	@Override
    public String getText() {
		return "Open SQL Editor";
	}
	/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IAction#getImageDescriptor()
			 */
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
