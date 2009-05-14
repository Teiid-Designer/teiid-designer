/*
 * Copyright (C) 2003 Andrea Mazzolini
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
package net.sourceforge.sqlexplorer.dbviewer.actions;

import java.util.ArrayList;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.JDBCReservedWords;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author mazzolini
 */
public class SelectAll extends Action {
	SessionTreeNode node;
	TableNode tableNode;
	public SelectAll(SessionTreeNode node,TableNode tableNode) {
		this.node=node;
		this.tableNode=tableNode;
	}

	
	private ImageDescriptor img=ImageDescriptor.createFromURL(SqlexplorerImages.getSqlIcon()); 
	@Override
    public String getText(){
		return Messages.getString("SelectAll.Open_in_Sql_Editor_1"); //$NON-NLS-1$
	}
	@Override
    public void run(){
		try{
			ArrayList ls=tableNode.getColumnNames();
			StringBuffer sb=new StringBuffer(100);
			sb.append("select "); //$NON-NLS-1$
            if(tableNode.isDocument()){
                sb.append(" *");
            }else {
    			for(int i=0;i<ls.size();i++){
    				if(i!=0)
    					sb.append(", "); //$NON-NLS-1$
                    String colNames = (String)ls.get(i);
                    if(JDBCReservedWords.isReservedWord(colNames)) {
                        sb.append(tableNode.getTableInfo().getQualifiedName());
                        sb.append(".\"");
                        sb.append(ls.get(i));
                        sb.append("\"");
                    }else {
                        sb.append(ls.get(i));
                    }
    			}
            }
			String sql=sb.toString()+" from "+tableNode.getTableInfo().getQualifiedName(); //$NON-NLS-1$
			SQLEditorInput input = new SQLEditorInput("SQL Editor ("+SQLExplorerPlugin.getDefault().getNextElement()+").sql"); //$NON-NLS-1$  //$NON-NLS-2$
			input.setSessionNode(node);
			IWorkbenchPage page=SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

			SQLEditor editorPart= (SQLEditor) page.openEditor(input,"net.sourceforge.sqlexplorer.plugin.editors.SQLEditor");  //$NON-NLS-1$
			editorPart.setText(sql);
		
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error creating sql editor",e); //$NON-NLS-1$
		}
	}
	@Override
    public ImageDescriptor getHoverImageDescriptor(){
		return img;
	}
	@Override
    public ImageDescriptor getImageDescriptor(){
		return img;            		
	}

}
