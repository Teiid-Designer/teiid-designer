/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.jdbc;


import java.util.Iterator;
import java.util.List;
import net.sourceforge.sqlexplorer.plugin.views.SqlexplorerViewConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * Customization of the SQLExplorer perspective for use in the Modeler.
 */
public class JdbcClientPluginPerspective implements IPerspectiveFactory
{     
   /**
    * Creates the default initial layout for this plugin.  This method fufills
    * the contract for the IPerspectiveFactory interface
    * 
    * @param IPageLayout
    */                      
    public void createInitialLayout(IPageLayout layout)
    {
        defineActions(layout);
        defineLayout(layout);
    }

   /** 
    * Define the actions and views you want to make available from the menus.
    * 
    * @param IPageLayout
    */                      
    private void defineActions(IPageLayout layout)
    {
        // You can add "new" wizards" here if you want, but none seem applicable
        // in the case of this plugin
        
        // Grab the list of all available views defined in the constants class
        List views = SqlexplorerViewConstants.getInstance().getFullViewList();
        Iterator iterator = views.iterator();
        
        // Iterate through those views and add them to the Show Views menu.
        while (iterator.hasNext()) {
            String viewId = (String)iterator.next();
            
            // don't show there SQL results view as it had a lot of problems. See defect 19020.
            if (!viewId.equals(SqlexplorerViewConstants.SQLEXPLORER_SQLRESULT)) {
                layout.addShowViewShortcut(viewId);
            } else {
                layout.addShowViewShortcut(DqpUiConstants.Extensions.SQL_RESULTS_VIEW);
            }
        }
    }
    
    
    private void defineLayout(IPageLayout layout)
    {
        //
        // editor is the top right area
        //
        String editorArea = layout.getEditorArea();

        //
        // top left area contains the Database Structure View
        //
        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.33f, editorArea); //$NON-NLS-1$
        topLeft.addView(SqlexplorerViewConstants.SQLEXPLORER_DBVIEW);
        
        //
        // middle left area contains the SQL History View
        //
        IFolderLayout middleLeft = layout.createFolder("middleLeft", IPageLayout.BOTTOM, 0.6f, "topLeft"); //$NON-NLS-1$ //$NON-NLS-2$
        middleLeft.addView(SqlexplorerViewConstants.SQLEXPLORER_SQLHISTORY);
        
        //
        // bottom left area contains the Connections View and the Message Log
        //
        IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.5f, "middleLeft"); //$NON-NLS-1$ //$NON-NLS-2$
        bottomLeft.addView(SqlexplorerViewConstants.SQLEXPLORER_CONNECTIONS);
        bottomLeft.addView(SqlexplorerViewConstants.MESSAGE_LOG);
        
        //
        // bottom right area contains the SQL Results View
        //
        IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.33f, editorArea); //$NON-NLS-1$
        bottomRight.addView(DqpUiConstants.Extensions.SQL_RESULTS_VIEW);
    }

    
    
}
