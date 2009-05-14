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
package net.sourceforge.sqlexplorer.gef.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mazzolini
 *
 */
public class SchemaViewerActionBarContributor extends ActionBarContributor {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	@Override
    protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction((RetargetAction)ActionFactory.COPY.create(Workbench.getInstance().getActiveWorkbenchWindow()));
		addRetargetAction((RetargetAction)ActionFactory.PASTE.create(Workbench.getInstance().getActiveWorkbenchWindow()));
		addRetargetAction(new AlignmentRetargetAction(1));
		addRetargetAction(new AlignmentRetargetAction(2));
		addRetargetAction(new AlignmentRetargetAction(4));
		addRetargetAction(new AlignmentRetargetAction(8));
		addRetargetAction(new AlignmentRetargetAction(16));
		addRetargetAction(new AlignmentRetargetAction(32));
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	@Override
    protected void declareGlobalActionKeys() {
		addGlobalActionKey("print");//$NON-NLS-1$

	}
	@Override
    public void contributeToToolBar(IToolBarManager itoolbarmanager)
	{

		itoolbarmanager.add(getAction(ActionFactory.UNDO.getId()));
		itoolbarmanager.add(getAction(ActionFactory.REDO.getId()));
		itoolbarmanager.add(new Separator());
		itoolbarmanager.add(getAction(GEFActionConstants.ALIGN_LEFT));
		itoolbarmanager.add(getAction(GEFActionConstants.ALIGN_CENTER));
		itoolbarmanager.add(getAction(GEFActionConstants.ALIGN_RIGHT));
		itoolbarmanager.add(new Separator());
		itoolbarmanager.add(getAction(GEFActionConstants.ALIGN_TOP));
		itoolbarmanager.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
		itoolbarmanager.add(getAction(GEFActionConstants.ALIGN_BOTTOM));
		itoolbarmanager.add(new Separator());
		itoolbarmanager.add(new ZoomComboContributionItem(getPage()));
	}

	@Override
    public void contributeToMenu(IMenuManager imenumanager)
	{
		super.contributeToMenu(imenumanager);
		MenuManager menumanager = new MenuManager("View");//$NON-NLS-1$
		menumanager.add(getAction(GEFActionConstants.ZOOM_IN));
		menumanager.add(getAction(GEFActionConstants.ZOOM_OUT));
		imenumanager.insertAfter("edit", menumanager);//$NON-NLS-1$
	}

}
