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
package net.sourceforge.sqlexplorer.plugin.views;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sqlpanel.SqlRowElement;
import net.sourceforge.sqlexplorer.sqlpanel.SqlTableContentProvider;
import net.sourceforge.sqlexplorer.sqlpanel.actions.CloseSQLResultTab;
import net.sourceforge.sqlexplorer.sqlpanel.actions.ExportToClipboard;
import net.sourceforge.sqlexplorer.sqlpanel.actions.MoreRowsAction;
import net.sourceforge.sqlexplorer.sqlpanel.actions.RetrieveAllRowsAction;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;

/**
 * @author Mazzolini
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CompositeSQLResultsViewer extends Composite {
	TableViewer tableViewer;
	SqlResultsView sqlResultsView;
	MoreRowsAction moreRowsAction;
	RetrieveAllRowsAction getAllRowsAction;
	ExportToClipboard clipAction;
	StatusLineManager statusMgr;
	Label lb;
	
	private CloseSQLResultTab closeSQLResultTab;
	
	
	/**
	 * @param parent
	 * @param style
	 */
	public CompositeSQLResultsViewer(SqlResultsView sqlResultsView,Composite parent, int style,int ii, TabItem tabItem) {
		super(parent,style);
		this.sqlResultsView=sqlResultsView;
		setLayout(new FillLayout());
		Composite myParent=new Composite(this,SWT.NULL);
		GridLayout layout;
	
		// Define layout.
		layout = new GridLayout();
		layout.marginHeight = 0; 
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		myParent.setLayout(layout);
		//CoolBar coolBar = new CoolBar(this, SWT.FLAT);
		    
		ToolBarManager toolBarMgr = new ToolBarManager(SWT.FLAT);
		//ToolBar toolBar = 
		toolBarMgr.createControl(myParent);
		moreRowsAction=new MoreRowsAction(sqlResultsView,ii);
		getAllRowsAction=new RetrieveAllRowsAction(sqlResultsView,ii);
		clipAction = new ExportToClipboard(sqlResultsView,SQLExplorerPlugin.getDefault().getPreferenceStore(),ii);
		closeSQLResultTab = new CloseSQLResultTab(tabItem);

		//LOOK here is where we create the toolbar of the SQL results view
		toolBarMgr.add(moreRowsAction);
		toolBarMgr.add(getAllRowsAction);
		toolBarMgr.add(clipAction);
		toolBarMgr.add(closeSQLResultTab);
		toolBarMgr.update(true);		
		      
		    
		GridData gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = GridData.BEGINNING;
		toolBarMgr.getControl().setLayoutData(gid);
				
				
				
				
		gid = new GridData();
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;

//		Label label = new Label(myParent, SWT.NULL);
//		label.setText("QQ coisa");
		
		tableViewer=new TableViewer(myParent,SWT.V_SCROLL | SWT.H_SCROLL|SWT.FULL_SELECTION); 
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		        
		tableViewer.getControl().setLayoutData(gid);
		tableViewer.getTable().addMouseListener(new MouseAdapter()
		{
			@Override
            public void mouseDoubleClick(MouseEvent evt)
			{
				handleDoubleClick(evt);
			}
		});
				
		tableViewer.setCellModifier(new TableCellModifier());
					
						
		tableViewer.setContentProvider(new SqlTableContentProvider());
		tableViewer.setUseHashlookup(true);			
		
		        
		statusMgr=new StatusLineManager();
		statusMgr.createControl(myParent);
		lb=new Label((Composite)statusMgr.getControl(),SWT.NULL);
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
					//System.out.println("selection i "+i);
					//((SqlTableLabelProvider)table.getLabelProvider()).getColumnText()
					//System.out.println("Element "+table.getElementAt(index));
					try{
						tableViewer.editElement(tableViewer.getElementAt(index), i);
					}catch(Exception e){
						SQLExplorerPlugin.error("Error editing the table element ",e); //$NON-NLS-1$
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
	/**
	 * @param b
	 */
	public void enableMoreRows(boolean b) {
		moreRowsAction.setEnabled(b);
		getAllRowsAction.setEnabled(b);
		
	}
	
	public void setMessagePanel1(String s){
		statusMgr.setMessage(s);
	}
	public void setMessagePanel2(String s){
		lb.setText(s);
		((Composite)statusMgr.getControl()).layout();
	}

}
class TableCellModifier implements ICellModifier
{
	//private Object cachedValue;
	//private Object erroredValue;

	public boolean canModify(Object element, String property)
	{
		return true;
	}

	public Object getValue(Object element, String property)
	{
		SqlRowElement row=(SqlRowElement)element;
		Object obj=row.getValue(property);
		
		return obj;
	}

	public void modify(Object element, String property, Object value)
	{
	}
}

