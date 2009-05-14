/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.sqlexplorer.dbviewer.details;

import java.text.MessageFormat;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author mazzolini
 */
public class RowCountDetail implements IDetailLogDisplay{

	
	public void setMessage(String s){
		detailLog.setMessage(s);
		errorMessage=s;
	}
	Text textWidget;
	//TableViewer viewer;
	boolean activated=false;	
	TableNode node;
	private IDetailLogDisplay detailLog;
	String errorMessage=""; //$NON-NLS-1$
	public RowCountDetail(TabItem itemTab,TabFolder parent,IDetailLogDisplay detailLog){
		this.itemTab=itemTab;
		this.parent=parent;
		this.detailLog=detailLog;
	}
	TabItem itemTab;
	TabFolder parent;
	public void setNode(TableNode node){
		this.node=node;
		activated=false;
		if(textWidget!=null){
			itemTab.setControl(null);
			textWidget.dispose();
			textWidget=null;
		}
	}
	
	public void activate(){
		if(!activated){
			activated=true;
			textWidget=new Text(parent,SWT.BORDER);
			textWidget.setEditable(false);
			itemTab.setControl(textWidget);			
			Display display=textWidget.getDisplay();
			BusyIndicator.showWhile(display,new Runnable(){
				public void run(){
					//int count=0;
					try{
						//System.out.println("size:"+node.getRowCount());
                        if (node.isTable() || node.isView() || node.isSynonym()) {
                            textWidget.setText(""+node.getRowCount()); //$NON-NLS-1$
                        } else {
                            String pattern = Messages.getString("RowCountDetail.notSupported"); //$NON-NLS-1$
                            String msg = MessageFormat.format(pattern, new Object[] {node.getTableInfo().getType()});
                            textWidget.setText(msg);
                        }
                        
                        // clear status message
                        setMessage(""); //$NON-NLS-1$
					}catch(java.lang.Throwable e){
						SQLExplorerPlugin.error("Error activating preview ",e); //$NON-NLS-1$
						errorMessage=e.getMessage();
						setMessage(errorMessage);
					}
					activated=true;					
				}
			});
		}
		else
			detailLog.setMessage(errorMessage);
	}

}

