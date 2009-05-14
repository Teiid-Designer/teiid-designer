/*
 * Copyright � 2000-2006 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.actions;

import java.text.MessageFormat;
import java.util.List;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.IConstants.ExtensionPoints;
import net.sourceforge.sqlexplorer.dbviewer.model.WebServiceNode;
import net.sourceforge.sqlexplorer.ext.IRequestDocumentGenerator;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * Action to invoke a Web Service operation in the SQL Editor panel
 */
public class InvokeWebService extends Action {


    // ****************
    //     Static
    // ****************

    static private IRequestDocumentGenerator requestDocGenerator;
    
    static {

        // get the Request Document Generator extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(SQLExplorerPlugin.PLUGIN_ID,
                                                                                           ExtensionPoints.RequestDocGenerator.ID);

        // get the all extensions to this extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // if no extensions no work to do
        if (extensions.length == 0) {
            requestDocGenerator = null;
        } else {

            // make executable extension
            IConfigurationElement[] elements = extensions[0].getConfigurationElements();

            for (int j = 0; j < elements.length; ++j) {
                try {
                    Object extension = elements[j].createExecutableExtension(ExtensionPoints.RequestDocGenerator.CLASS_NAME);

                    if (extension instanceof IRequestDocumentGenerator) {
                        requestDocGenerator = (IRequestDocumentGenerator)extension;
                    } else {
                        // not an IResultSetProcessor. just log it and continue
                        String msg = MessageFormat.format(Messages.getString("InvokeWebService.invalidExtensionClass"), //$NON-NLS-1$
                                                          new Object[] {
                                                              extension.getClass().getName()
                                                          });
                        SQLExplorerPlugin.error(msg, null);
                    }
                } catch (Exception theException) {
                    String msg = MessageFormat.format(Messages.getString("InvokeWebService.extensionInitializationProblem"), //$NON-NLS-1$
                                                      new Object[] {
                                                          elements[j].getAttribute(ExtensionPoints.ResultSetProcessor.CLASS_NAME)
                                                      });
                    SQLExplorerPlugin.error(msg, theException); 
                }
            }
        }

    }

    
    
    // ****************
    //     Instance 
    // ****************

    SessionTreeNode node;
    WebServiceNode procNode;
	public InvokeWebService(SessionTreeNode node, WebServiceNode procNode) {
		this.node=node;
		this.procNode=procNode;
	}

	
	private ImageDescriptor img=ImageDescriptor.createFromURL(SqlexplorerImages.getSqlIcon()); 
	@Override
    public String getText(){
		return Messages.getString("InvokeWebService.Open_in_Sql_Editor"); //$NON-NLS-1$
	}
	@Override
    public void run(){
		try{
           
			List ls=procNode.getArgumentNameList();
			StringBuffer sb=new StringBuffer(100);
			sb.append("EXEC "); //$NON-NLS-1$
            sb.append(procNode.getFullName());
			
            if ( requestDocGenerator != null ) {
                sb.append("('"); //$NON-NLS-1$
                String doc = requestDocGenerator.generateRequestDocument(procNode.getModelUUID(), procNode.getUUID());
                if ( doc != null ) {
                    sb.append(doc);
                } else {
                    sb.append(ls.get(0));
                }
                sb.append("\n')"); //$NON-NLS-1$
            } else {
                sb.append("( "); //$NON-NLS-1$
                for(int i=0;i<ls.size();i++){
    				if(i!=0)
    					sb.append(" , "); //$NON-NLS-1$
    				sb.append(ls.get(i));
    			}
                sb.append(" )"); //$NON-NLS-1$
            }
            
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
