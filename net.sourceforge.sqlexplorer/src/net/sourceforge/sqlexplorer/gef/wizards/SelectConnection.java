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

package net.sourceforge.sqlexplorer.gef.wizards;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class SelectConnection extends WizardPage {
	ReverseDatabaseWizard wizard;
	/**
	 * @param pageName
	 */
	public SelectConnection(String pageName,ReverseDatabaseWizard wizard) {
		super(pageName);
		this.setTitle(pageName);
		this.setPageComplete(false);
		this.wizard=wizard;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite composite) {
		initializeDialogUnits(composite);
		Composite composite1 = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite1.setLayout(layout);
		final Table tb=new Table(composite1,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION|SWT.SINGLE);
		tb.setHeaderVisible(true);
		TableColumn tc=new TableColumn(tb,SWT.NULL);
		tc.setText("Connection");
		tb.setLayoutData(new GridData(GridData.FILL_BOTH));
		tb.layout();
		SessionTreeNode[] nodes=SQLExplorerPlugin.getDefault().stm.getRoot().getSessionTreeNodes();
		for(int i=0;i<nodes.length;i++){
			TableItem ti=new TableItem(tb,SWT.NONE);
			ti.setText(0,nodes[i].toString());
			ti.setData(nodes[i]);
		}
		
		TableLayout tableLayout=new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(1, 200, true));
		tb.setLayout(tableLayout);
		tb.layout();
		tb.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(tb.getSelectionCount()>0){
					SelectConnection.this.setPageComplete(true);
					wizard.setConnection((SessionTreeNode)tb.getSelection()[0].getData());	
				}
				
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
		});
		super.setControl(composite1);


	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
	 */
	@Override
    public boolean canFlipToNextPage() {
		return this.isPageComplete();
	}

}
