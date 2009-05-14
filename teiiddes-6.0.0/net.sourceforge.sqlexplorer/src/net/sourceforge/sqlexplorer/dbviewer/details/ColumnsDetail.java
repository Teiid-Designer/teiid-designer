package net.sourceforge.sqlexplorer.dbviewer.details;

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

 import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.actions.CopyTableAction;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sqlpanel.SQLTableSorter;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class ColumnsDetail implements IDetailLogDisplay{

	TableViewer viewer;
	boolean activated=false;
	TableNode node;
	String errorMessage;
	IDetailLogDisplay detailLog;
	public void setMessage(String s){
		errorMessage=s;
		detailLog.setMessage(s);
	}
	Image imgAsc=null;
	Image imgDesc=null;
	MySQLTableSorter sorter;

	public ColumnsDetail(TabItem itemTab,Composite parent,IDetailLogDisplay detailLog){
		this.detailLog=detailLog;
		viewer = new TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
		itemTab.setControl(viewer.getControl());
		final Table table=viewer.getTable();

		imgAsc=ImageDescriptor.createFromURL(SqlexplorerImages.getAscOrderIcon()).createImage();
		imgDesc=ImageDescriptor.createFromURL(SqlexplorerImages.getDescOrderIcon()).createImage();

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		MenuManager  menuMgr= new MenuManager("#TableMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(false);
		menuMgr.add(new CopyTableAction(table));
		Menu fDbContextMenu= menuMgr.createContextMenu(table);
		table.setMenu(fDbContextMenu);

		TableColumn tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Column_Name_1")); //$NON-NLS-1$

		tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Data_Type_2"));			 //$NON-NLS-1$

		tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Size_3"));					 //$NON-NLS-1$

		tc=new TableColumn(table,SWT.NULL);
		tc.setText("Decimal Digits");					 //$NON-NLS-1$

		tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Default_Value_4"));		 //$NON-NLS-1$

		tc=new TableColumn(table,SWT.NULL);
		tc.setText(Messages.getString("Accept_Null_Value_5"));		 //$NON-NLS-1$

		tc=new TableColumn(table,SWT.NULL);
		tc.setText("Comments");//$NON-NLS-1$

		TableLayout tableLayout=new TableLayout();
		for(int i=0;i<7;i++)
			tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		table.setLayout(tableLayout);
		viewer.setContentProvider(new ColumnDetailContentProvider());
		final SelectionListener headerListener = new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
				// column selected - need to sort
				if(viewer.getSorter()==null)
					viewer.setSorter(sorter);
				int column = viewer.getTable().indexOf((TableColumn) e.widget);
				if (column == sorter.getTopPriority()){
					int k=sorter.reverseTopPriority();
					if(k==SQLTableSorter.ASCENDING)
						((TableColumn) e.widget).setImage(imgAsc);
					else
						((TableColumn) e.widget).setImage(imgDesc);
				}else {
					sorter.setTopPriority(column);
					((TableColumn) e.widget).setImage(imgAsc);
				}
				TableColumn[] tcArr=viewer.getTable().getColumns();
				for(int i=0;i<tcArr.length;i++){
					if(i!=column){
						tcArr[i].setImage(null);
					}
				}
				viewer.refresh();

			}
		};
		for(int i=0;i<7;i++){
			tc=table.getColumn(i);
			//tc.setImage(null);
			//if(i==0)
			//	tc.setImage(imgAsc);
			//tc.setText(metaData.getColumnLabel(i+1));
			//ss[i]=new String(metaData.getColumnLabel(i+1));
			tc.addSelectionListener(headerListener);
		}

        for(int i=0;i<7;i++){
             tc=table.getColumn(i);
             tc.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e){
                  ((TableColumn) e.getSource()).removeSelectionListener(headerListener);
                }
             });
        }

        table.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e){
				imgAsc.dispose();
				imgDesc.dispose();
			}
		});

	}
	public void setNode(net.sourceforge.sqlexplorer.dbviewer.model.TableNode node){
		this.node=node;
		activated=false;
		viewer.setSorter(null);
		for(int i=0;i<7;i++){
			TableColumn tc=viewer.getTable().getColumn(i);
			tc.setImage(null);
			//if(i==0)
			//	tc.setImage(imgAsc);
			//tc.setText(metaData.getColumnLabel(i+1));
			//ss[i]=new String(metaData.getColumnLabel(i+1));

		}
	}

	public void activate(){
		if(!activated){
			Display display=viewer.getControl().getDisplay();
			BusyIndicator.showWhile(display,new Runnable(){
				public void run(){

					ResultSet rs=null;
					int count=0;
					ResultSetMetaData metaData=null;
					ResultSetReader reader=null;
					try{
						rs=node.getColumns();
						metaData=rs.getMetaData();
						count=metaData.getColumnCount();
						reader=new ResultSetReader(rs);
					}catch(java.lang.Exception e){
						SQLExplorerPlugin.error("Error activating columns detail view ",e); //$NON-NLS-1$
						errorMessage=e.getMessage();
						detailLog.setMessage(errorMessage);
						return;
					}

					sorter=new MySQLTableSorter(count,metaData);
					//final String[]ss=new String[count];
					//viewer.setSorter(sorter);


					//viewer.setColumnProperties(ss);


					ColumnDetailTableModel cdtm=new ColumnDetailTableModel(reader,ColumnsDetail.this);
					ColumnDetailLabelProvider cdlp=new ColumnDetailLabelProvider(cdtm);
					viewer.setLabelProvider(cdlp);
					viewer.setInput(cdtm);

					activated=true;
					try{
						Statement st=rs.getStatement();
						if(st!=null)
							st.close();
						rs.close();
					}catch(Throwable e){
					}
				}
			});
		}
		else
			detailLog.setMessage(errorMessage);
	}

}
class MySQLTableSorter extends SQLTableSorter{

	/**
	 * @param count
	 * @param metaData
	 */
	public MySQLTableSorter(int count, ResultSetMetaData metaData) {
		super(count, metaData);
	}

	@Override
    public int compare(Viewer viewer, Object e1, Object e2) {
		return compareColumnValue((ColumnDetailRow)e1, (ColumnDetailRow)e2, 0);
	}

	private int compareColumnValue(ColumnDetailRow m1, ColumnDetailRow m2, int depth) {
		if (depth >= priorities.length)
			return 0;

		int columnNumber = priorities[depth];
		int direction = directions[columnNumber];
		int result=0;
		String v1=m1.getValue(columnNumber).toString();
		String v2=m2.getValue(columnNumber).toString();
		int colNumber=0;
		switch(columnNumber){
			case 0:
				colNumber=3;
				break;
			case 1:
				colNumber=5;
				break;
			case 2:
				colNumber=6;
				break;
			case 3:
				colNumber=8;
				break;
			case 4:
				colNumber=12;
				break;
			case 5:
				colNumber=17;
				break;
			case 6:
				colNumber=11;
				break;
		}

		switch(SQLType[colNumber]){
			case Types.CHAR :
			case Types.VARCHAR :
			case Types.LONGVARCHAR :
			case -9 :
				result = getComparator().compare(m1.getValue(columnNumber), m2.getValue(columnNumber));
				break;
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.REAL:
				double d1=0;
				double d2=0;
				try{
					d1=Double.parseDouble(v1);
				}
				catch(Exception e){
				}
				try{
					d2=Double.parseDouble(v2);
				}catch(Exception e){
				}
				if(d1==d2)
					result=0;
				else if(d1>d2)
					result=1;
				else
					result=-1;
				break;
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.BIGINT :
				v1=m1.getValue(columnNumber).toString();
				v2=m2.getValue(columnNumber).toString();

				long l1=0;
				long l2=0;
				try{
					l1=Long.parseLong(v1);
				}
				catch(Exception e){
				}
				try{
					l2=Long.parseLong(v2);
				}catch(Exception e){
				}
				if(l1==l2)
					result=0;
				else if(l1>l2)
					result=1;
				else
					result=-1;
				break;
			case Types.DATE :
				try{
					Date dt1=(Date)m1.el[columnNumber];
					Date dt2=(Date)m2.el[columnNumber];
					if(dt1==null && dt2==null)
						result=0;
					if(dt2==null)
						result=1;
					else if (dt1==null)
						result=-1;
					else
						result=dt1.compareTo(dt2);
				}catch(Exception e){

				}

				break;
			case Types.TIMESTAMP:
				try{
					Timestamp t1=(Timestamp)m1.el[columnNumber];
					Timestamp t2=(Timestamp)m2.el[columnNumber];
					if(t1==null && t2==null)
						result=0;
					if(t2==null)
						result=1;
					else if (t1==null)
						result=-1;
					else
						result=t1.compareTo(t2);
				}catch(Exception e){

				}
				break;
			case Types.TIME:
				try{
					Time t11=(Time)m1.el[columnNumber];
					Time t22=(Time)m2.el[columnNumber];
					if(t11==null && t22==null)
						result=0;
					if(t22==null)
						result=1;
					else if (t11==null)
						result=-1;
					else
						result=t11.compareTo(t22);
				}catch(Exception e){

				}
				break;
		}

		if (result == 0)
			return compareColumnValue(m1, m2, depth + 1);
		return result * direction;
	}
}
