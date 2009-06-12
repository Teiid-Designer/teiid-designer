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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.sun.rowset.JdbcRowSetImpl;



public class EditorDialog extends TitleAreaDialog {
	SessionTreeNode sessionNode;
	TableNode node;
	String[] columns;
	SQLComposite sqlComp;
	SqlTableModel mo;
	ResultSetMetaData metaData;
	RowSet rowSet;
	ResultSet rs;
	String oldCatalog=null;
	String whereCondition=""; //$NON-NLS-1$
	/**
	 * @param parentShell
	 */
	public EditorDialog(SessionTreeNode sessionNode, TableNode node) throws SQLException {
		super(null);
		this.node=node;
		this.sessionNode=sessionNode;
		if(sessionNode.supportsCatalogs()){
			oldCatalog=sessionNode.getCatalog();
			sessionNode.setCatalog(node.getTableInfo().getCatalogName());
		}
			
	}
	@Override
    protected Control createContents(Composite parent) {
	
		Control contents = super.createContents(parent);
		setTitle("Editing "+node.getTableInfo().getQualifiedName()); //$NON-NLS-1$
		setMessage(Messages.getString("EditorDialog.Note__the_Direct_Table_Editing_is_an_experimental_feature._1")); //$NON-NLS-1$
		return contents;
	}


	RowSet getRowSet(SQLConnection conn,String baseTable)
		throws SQLException
	{
		Statement stmt=null;
		String sql=null;
		//if(conn.getSQLMetaData().getJDBCMetaData().supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE)==false){
		//	throw new SQLException("This database doesn't support updatable result sets");
		//}
		if(conn.getSQLMetaData().getDatabaseProductName().toLowerCase().startsWith("oracle")) //$NON-NLS-1$
			sql="select aaalias.* from " + baseTable+" aaalias "+whereCondition;//$NON-NLS-1$ //$NON-NLS-2$
		else
			sql="select * from " + baseTable+" "+whereCondition;//$NON-NLS-1$ //$NON-NLS-2$
		
		if(rs!=null){
			try{
				rs.close();
				rs.getStatement().close();
			}catch(Throwable e){
			}
		}
		if(rowSet!=null){
			try{
				rowSet.close();
				rowSet.getStatement().close();
			}catch(Throwable e){
			}
		}
		stmt = conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
		stmt.setMaxRows(SQLExplorerPlugin.getDefault().getPreferenceStore().getInt(IConstants.MAX_SQL_ROWS));
		rs = stmt.executeQuery(sql);
		if(rs.getConcurrency()!=ResultSet.CONCUR_UPDATABLE){
			throw new SQLException("The result set is not updatable"); //$NON-NLS-1$
		}
		
		JdbcRowSet crs = new JdbcRowSetImpl(rs);
		crs.setReadOnly(false);
		//crs.setMaxRows(5);
		return crs;
	}


	@Override
    protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite)super.createDialogArea(parent);
		//parentComposite.setLayout(new FillLayout());
		
		sqlComp=new SQLComposite(parentComposite,SWT.NULL,this);
		sqlComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		BusyIndicator.showWhile(Display.getDefault(),new Runnable(){
			public void run(){
				try{
					rowSet=getRowSet(sessionNode.getConnection(),node.getTableInfo().getQualifiedName());
					showRowSet();
					sqlComp.getStatusLineManager().setMessage(""); //$NON-NLS-1$
				}catch(Throwable e){
					rowSet=null;
					sqlComp.getStatusLineManager().setMessage("Error: can't edit:"+e.getMessage());//$NON-NLS-1$
					SQLExplorerPlugin.error("Error opening edit dialog",e);//$NON-NLS-1$
				}
				
			}
		});
		
		return parentComposite;
		
	}
	public void showRowSet()throws Exception{
		if(rowSet!=null){
			metaData=rowSet.getMetaData();

			final int count=metaData.getColumnCount();



			columns=new String[count];
			for(int i=0;i<count;i++){
				columns[i]=metaData.getColumnName(i+1);
			}
			mo=new SqlTableModel(rowSet,metaData);
			final TableViewer tv=sqlComp.getTableViewer();
			final Table table=tv.getTable();
			if(table.getColumnCount()==0){
				for(int i=0;i<count;i++){
					TableColumn tc=new TableColumn(table,SWT.NULL);
					tc.setText(columns[i]);
				}
				tv.setColumnProperties(columns);
				CellEditor[] cellEditors = new CellEditor[count];
				for (int i = 0; i < cellEditors.length; i++)
				{
					//final int colIndex = i;
					cellEditors[i] = new TextCellEditor(table);

				}
				tv.setCellEditors(cellEditors);

				tv.setLabelProvider(new SqlTableLabelProvider(mo));		
			}
	
			tv.setInput(mo);
			for (int i = 0; i < count; i++) {
				table.getColumn(i).pack();
			}
			table.layout();
			table.redraw();
			tv.refresh();	
				
			
		}
		else{
			sqlComp.getTableViewer().setInput(null);
			sqlComp.getTableViewer().refresh();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	@Override
    protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle|SWT.RESIZE);
	}
	/**
	 * @param element
	 * @param property
	 * @param value
	 */
	int getColumnIndex(String property){
		int index=-1;
		for(int i=0;i<columns.length;i++){
			if(columns[i].equals(property)){
				index=i;
				break;
			}	
		}
		return index;
	}
	public void modify(final Object element, final String property, final Object value) {
		//Display disp=Display.getCurrent();
		BusyIndicator.showWhile(Display.getCurrent(),new Runnable(){
			public void run(){
				TableItem item=(TableItem)element;
				final ArrayList ls=(ArrayList)(mo.getElements()[item.getParent().indexOf(item)]);
				
				int index=getColumnIndex(property);
				Object originalVal=ls.get(index);
				if(originalVal!=null && originalVal.equals(value)){
					sqlComp.getStatusLineManager().setMessage(Messages.getString("EditorDialog.No_change_2")); //$NON-NLS-1$
					return;
				}
					
				try{
					Object value_=value;
					if(value_.equals("<NULL>"))//$NON-NLS-1$
						value_=null;
					rowSet.first();
					rowSet.relative(item.getParent().indexOf(item));
					rowSet.updateObject(property, value_);
					rowSet.updateRow();
					ls.set(index,value_);
					sqlComp.getTableViewer().refresh();//To be optimized!
				}catch(Throwable e){
					sqlComp.getStatusLineManager().setMessage("Error updating: "+e.getMessage());//$NON-NLS-1$
					return;
				}
				sqlComp.getStatusLineManager().setMessage(Messages.getString("EditorDialog.Cell_updated_successfully_3")); //$NON-NLS-1$
			}
		});
		
	}
	/**
	 * @param element
	 * @param property
	 * @return
	 */
	public boolean canModify(Object element, String property) {
		int index=getColumnIndex(property);
		int type=0;
		try{
			type=metaData.getColumnType(index+1);
		}catch(Throwable e){
			//e.printStackTrace();
			return false;
		}
		if(type==Types.VARBINARY || type==Types.BINARY || type==Types.LONGVARBINARY || type==Types.BLOB || type==Types.CLOB || type==Types.OTHER){
			//System.out.println("Non faccio update per il tipo");
			return false;
		}

		return true;
	}
	/**
	 * @param element
	 * @param property
	 * @return
	 */
	public Object getValue(Object element, String property) {
		int index=getColumnIndex(property);
		Object val=((ArrayList)element).get(index);
		if(val==null)
			return ""; //$NON-NLS-1$
		return val.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
    protected void configureShell(Shell newShell) {
		newShell.setText(Messages.getString("EditorDialog.Editing__5")+node.getTableInfo().getSimpleName()); //$NON-NLS-1$
		super.configureShell(newShell);
	}

	@Override
    public int open() {
		int val=super.open();
		if(rowSet!=null){
			try{
				rowSet.close();
			}catch(Throwable e){
			}
		}
		if(rs!=null){
			try{
				rs.close();
				rs.getStatement().close();
			}catch(Throwable e){
			}
		}
		if(sessionNode.supportsCatalogs()){
			if(oldCatalog!=null)
			try{
				sessionNode.setCatalog(oldCatalog);
			}catch(Throwable e){
			}
				
		}
		return val;
	}
	/**
	 * @param string
	 */
	public void updateWhereCondition(String string) {
		whereCondition=string;
		BusyIndicator.showWhile(Display.getDefault(),new Runnable(){
			public void run(){
				try{
					rowSet=getRowSet(sessionNode.getConnection(),node.getTableInfo().getQualifiedName());
					showRowSet();
					sqlComp.getStatusLineManager().setMessage(""); //$NON-NLS-1$
				}catch(Throwable e){
					rowSet=null;
					sqlComp.getStatusLineManager().setMessage("Error: not valid sql statement:"+e.getMessage()); //$NON-NLS-1$
					SQLExplorerPlugin.error("Error in where condition",e); //$NON-NLS-1$
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected Control createButtonBar(Composite parent) {
		return null;//new Composite(parent, SWT.NONE);
		//return super.createButtonBar(parent);
	}

}