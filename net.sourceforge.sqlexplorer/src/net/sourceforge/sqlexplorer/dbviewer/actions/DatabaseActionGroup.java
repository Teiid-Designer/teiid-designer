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

import java.util.List;

import net.sourceforge.sqlexplorer.dbviewer.model.CatalogNode;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.ProcedureNode;
import net.sourceforge.sqlexplorer.dbviewer.model.SchemaNode;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.dbviewer.model.TableObjectTypeNode;
import net.sourceforge.sqlexplorer.dbviewer.model.WebServiceNode;
import net.sourceforge.sqlexplorer.ext.PluginManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.DBView;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionGroup;

/**
 * @author mazzolini
 */
public class DatabaseActionGroup extends ActionGroup{
	IAction selectAll;
//	IAction tableScript;
	IAction refresh;
	TreeViewer tv;
	
	PluginManager pluginManager=SQLExplorerPlugin.getDefault().pluginManager;
	List extensionsActions;
	DBView view;
	
	@Override
    public void fillContextMenu(IMenuManager  menu){
		IDbModel nd=getDbNode();
		if(nd==null)
			return;
		SessionTreeNode node=view.getSessionTreeNode();
		if(node==null)
			return;
		
		if(nd instanceof TableNode){
			TableNode tn=(TableNode)nd;
			selectAll=new SelectAll(node,tn);
			menu.add(selectAll);
			
			if(tn.isTable()){
//				IAction tableEdit=new TableDirectEdit(node,tn);
//				menu.add(tableEdit);
				
				IAction refreshTable=new RefreshTable(view,node,tn);
				menu.add(refreshTable);
			}
			
//			tableScript=new ExportTableScript(node,tn);
//			
//			menu.add(tableScript);
//			try{
//				IAction[] obj=(IAction[])pluginManager.getAddedActions(node,this.getDbNode(),tv);
//				if(obj!=null)
//					for(int i=0;i<obj.length;i++){
//						menu.add(obj[i]);
//					}
//			}catch(Throwable e){
//			}
		}else if (nd instanceof SchemaNode){
			SchemaNode schema=(SchemaNode)nd;
			IAction refresh1=new RefreshSchema(schema,tv);
			menu.add(refresh1);
		}
		else if (nd instanceof CatalogNode){
			CatalogNode cat=(CatalogNode)nd;
			IAction refresh1=new RefreshCatalog(cat,tv);
			menu.add(refresh1);
		}
		else if (nd instanceof TableObjectTypeNode){
			TableObjectTypeNode totn=(TableObjectTypeNode)nd;
			IAction refresh1=new TableObjectTypeRefresh(totn,tv);
			menu.add(refresh1);
			try{
				IAction[] obj=pluginManager.getAddedActions(node,this.getDbNode(),tv);
				if(obj!=null)
					for(int i=0;i<obj.length;i++){
						menu.add(obj[i]);
					}
			}catch(Throwable e){
			}
		}else if (nd instanceof DatabaseNode){
			refresh=new Refresh(view);
			menu.add(refresh);
        }else if (nd instanceof WebServiceNode) {
            WebServiceNode wsn = (WebServiceNode) nd;
            IAction execAction = new InvokeWebService(node, wsn);
            menu.add(execAction);
		}else if (nd instanceof ProcedureNode) {
            ProcedureNode pn = (ProcedureNode) nd;
            IAction execAction = new ExecProcedure(node, pn);
            menu.add(execAction);
        }
		if((!(nd instanceof TableObjectTypeNode)) && !(nd instanceof DatabaseNode))
			menu.add(new CopyName(nd));
		try{
			IAction[] obj=pluginManager.getTypeActions(node,this.getDbNode(),tv);
			if(obj!=null)
				for(int i=0;i<obj.length;i++){
					menu.add(obj[i]);
				}
		}catch(Throwable e){
		}
	}
	public DatabaseActionGroup(DBView view, TreeViewer tv){
		this.view=view;
		this.tv=tv;
		
	}
	public IDbModel getDbNode(){
		ISelection sel=tv.getSelection();	
		IStructuredSelection iSel=(IStructuredSelection)sel;
		return (IDbModel)(iSel).getFirstElement();
	}

}
