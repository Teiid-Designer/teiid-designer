/*
 * Copyright � 2000-2005 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.actions;

import java.util.List;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.model.ProcedureNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author steve jacobs
 */
public class ExecProcedure extends Action {
	SessionTreeNode node;
    ProcedureNode procNode;
	public ExecProcedure(SessionTreeNode node, ProcedureNode procNode) {
		this.node=node;
		this.procNode=procNode;
	}

	
	private ImageDescriptor img=ImageDescriptor.createFromURL(SqlexplorerImages.getSqlIcon()); 
	@Override
    public String getText(){
		return Messages.getString("ExecProcedure.Open_in_Sql_Editor"); //$NON-NLS-1$
	}
	@Override
    public void run(){
		try{
			List ls=procNode.getArgumentNameList();
			StringBuffer sb=new StringBuffer(100);
			sb.append("EXEC "); //$NON-NLS-1$
            sb.append(procNode.toString());
            sb.append("( "); //$NON-NLS-1$
            
			for(int i=0;i<ls.size();i++){
				if(i!=0)
					sb.append(" , "); //$NON-NLS-1$
				sb.append(ls.get(i));
			}
            sb.append(" )"); //$NON-NLS-1$
            
			String sql=sb.toString();
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
