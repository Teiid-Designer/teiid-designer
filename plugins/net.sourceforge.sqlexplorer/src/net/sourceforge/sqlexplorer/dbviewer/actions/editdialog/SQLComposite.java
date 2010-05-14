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
package net.sourceforge.sqlexplorer.dbviewer.actions.editdialog;

import net.sourceforge.sqlexplorer.Messages;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class SQLComposite extends Composite {
	TableViewer tableViewer;
	StatusLineManager statusMgr;
	EditorDialog editDialog;
	Composite tableComposite;
	public StatusLineManager getStatusLineManager(){
		return statusMgr;
	}
	private void createTableViewer(Composite myParent){
		myParent.setLayout(new FillLayout());
		//if(tableViewer!=null)
		//	tableViewer.getTable().dispose();
		tableViewer=new TableViewer(myParent,SWT.V_SCROLL | SWT.H_SCROLL|SWT.FULL_SELECTION); 
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().addMouseListener(new MouseAdapter()
		{
			@Override
            public void mouseDoubleClick(MouseEvent evt)
			{
				handleDoubleClick(evt);
			}
		});

		tableViewer.setCellModifier(new TableCellModifier(editDialog));

	
		tableViewer.setContentProvider(new SQLTableContentProvider());
		tableViewer.setUseHashlookup(true);	

	}
	public SQLComposite(Composite parent, int style, final EditorDialog editDialog) {
		super(parent, style);
		this.editDialog=editDialog;
		setLayout(new FillLayout());
		Composite myParent=new Composite(this,SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 
		layout.horizontalSpacing = layout.verticalSpacing = 0;
		myParent.setLayout(layout);
	    
		Composite cmpTextEditor=new Composite(myParent,SWT.NULL);
		cmpTextEditor.setLayout(new GridLayout(3,false));
		Label lbWhere=new Label(cmpTextEditor,SWT.NULL);
		lbWhere.setText(Messages.getString("SQLComposite.Write_your_where_condition__1")); //$NON-NLS-1$
		final Text txt=new Text(cmpTextEditor,SWT.BORDER);
		GridData txtGridData=new GridData(GridData.FILL_HORIZONTAL);
		txt.setLayoutData(txtGridData);
		Button btn=new Button(cmpTextEditor,SWT.PUSH);
		btn.setText(Messages.getString("SQLComposite.Apply_2")); //$NON-NLS-1$
		editDialog.getShell().setDefaultButton(btn);
		btn.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				editDialog.updateWhereCondition(txt.getText());	
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});    
	    
		GridData gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = GridData.BEGINNING;
		cmpTextEditor.setLayoutData(gid);
		tableComposite=new Composite(myParent,SWT.NULL);
		gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		tableComposite.setLayoutData(gid);
		createTableViewer(tableComposite);
    
		statusMgr=new StatusLineManager();
		statusMgr.createControl(myParent);
		new Label((Composite)statusMgr.getControl(),SWT.NULL);
		gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = GridData.BEGINNING;
		statusMgr.getControl().setLayoutData(gid);
	
		myParent.layout();
	}
	protected void handleDoubleClick(MouseEvent evt)
	{
		Table t = (Table) evt.widget;
		int index = t.getSelectionIndex();
		//System.out.println("selection index "+index);
		if (index != -1)
		{
			for (int i = 0; i < t.getColumnCount(); i++)
			{
				if (t.getItem(index).getBounds(i).contains(evt.x, evt.y))
				{
					try{
						tableViewer.editElement(tableViewer.getElementAt(index), i);
					}catch(Throwable e){
						//e.printStackTrace();
					}
					break;
				}
			}
		}
		// clicked into empty area, add a new row?
		else
		{
		//???
		}
	}
	/**
	 * @return
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

}
