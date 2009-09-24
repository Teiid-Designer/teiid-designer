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
 
 import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

 
public class PreviewDetail implements IDetailLogDisplay{
	
	public void setMessage(String s){
		detailLog.setMessage(s);
		errorMessage=s;
	}
	TableViewer viewer;
	boolean activated=false;	
	TableNode node;
	private IDetailLogDisplay detailLog;
	String errorMessage=""; //$NON-NLS-1$
	IPreferenceStore store;
	public PreviewDetail(TabItem itemTab,TabFolder parent,IDetailLogDisplay detailLog,IPreferenceStore store){
		this.itemTab=itemTab;
		this.parent=parent;
		this.detailLog=detailLog;
		this.store=store;
	}
	TabItem itemTab;
	TabFolder parent;
	public void setNode(TableNode node){
		this.node=node;
		activated=false;
		if(viewer!=null){
			itemTab.setControl(null);
			viewer.getTable().dispose();
			viewer=null;
		}
	}
	
	public void activate(){
		if(!activated){
			
			Display display=parent.getDisplay();
			BusyIndicator.showWhile(display,new Runnable(){
				public void run(){
					PreviewDetailTableModel pdtm=null;
					int count=0;
					ResultSet set=null;
                    boolean wasError = false;
					try{
//						ResultSet set=node.getPreview();
						int iMaxRowCount = store.getInt(IConstants.PRE_ROW_COUNT);
						set=node.getPreview(iMaxRowCount);
						ResultSetReader reader=new ResultSetReader(set);
						ResultSetMetaData metaData=set.getMetaData();
						count=metaData.getColumnCount();
						viewer = new TableViewer(parent,SWT.BORDER|SWT.FULL_SELECTION);
						itemTab.setControl(viewer.getControl());
						final Table table=viewer.getTable();
						table.setLinesVisible(true);
						table.setHeaderVisible(true);
						viewer.setContentProvider(new PreviewDetailContentProvider());
						//TableLayout tableLayout = new TableLayout();
						for(int i=0;i<count;i++){
							//tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
							TableColumn tc=new TableColumn(viewer.getTable(),SWT.NULL);
							tc.setText(metaData.getColumnLabel(i+1));
						}
						//viewer.getTable().setLayout(tableLayout);
						//pdtm=new PreviewDetailTableModel(set,PreviewDetail.this,store.getInt("preRowCount")); //$NON-NLS-1$
						pdtm=new PreviewDetailTableModel(reader,metaData,PreviewDetail.this,iMaxRowCount); 
						PreviewDetailLabelProvider pdlp=new PreviewDetailLabelProvider(pdtm);
						viewer.setLabelProvider(pdlp);	
						viewer.setInput(pdtm);
						for (int i = 0; i < count; i++) {
							table.getColumn(i).pack();
						}
						viewer.getTable().layout();
					}catch(java.lang.Throwable e){
						SQLExplorerPlugin.error("Error activating preview ",e); //$NON-NLS-1$
						errorMessage=e.getMessage();
						setMessage(errorMessage);
                        wasError = true;
					}finally{
                        if( !wasError ) {
                            setMessage(null);
                        }
						try{
							Statement st=set.getStatement();
							if(st!=null)
								st.close();
							set.close();
							
						}catch(Throwable e){
						}
                        
					}
					activated=true;					
				}
			});
		}
		else
			detailLog.setMessage(errorMessage);
	}

}
