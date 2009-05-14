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
package net.sourceforge.sqlexplorer.plugin.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author MAZZOLINI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NewSchemaVisualizer extends Wizard implements INewWizard {

	private NewSchemaVisualizerPage page;
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public NewSchemaVisualizer()
	{
	}

	@Override
    public void addPages()
	{
		page = new NewSchemaVisualizerPage(workbench, selection);
		addPage(page);
	}

	public void init(IWorkbench iworkbench, IStructuredSelection istructuredselection)
	{
		workbench = iworkbench;
		selection = istructuredselection;
	}

	@Override
    public boolean performFinish()
	{
		return page.performFinish();
	}

}
