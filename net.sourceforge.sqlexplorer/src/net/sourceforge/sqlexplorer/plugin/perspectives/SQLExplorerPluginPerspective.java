package net.sourceforge.sqlexplorer.plugin.perspectives;

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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.sqlexplorer.plugin.views.SqlexplorerViewConstants;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Provides an Eclipse perspective for this plugin.
 * 
 * @author Macon Pegram
 */
public class SQLExplorerPluginPerspective implements IPerspectiveFactory
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
        while (iterator.hasNext())
           layout.addShowViewShortcut((String)iterator.next());          
    }
    
   /** 
    * Controls the physical default layout of the perspective
    * 
    * @param IPageLayout
    */                      
//    private void defineLayout(IPageLayout layout)
//    {
//        // Hide the editor.  Note the user can always make it come back.  
//        layout.setEditorAreaVisible(true);
//        
//		String editorArea = layout.getEditorArea();
//
//		IFolderLayout left = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f,editorArea); //$NON-NLS-1$
//		left.addView(SqlexplorerViewConstants.SQLEXPLORER_DRIVER);
//		left.addView(SqlexplorerViewConstants.SQLEXPLORER_ALIAS);
//        left.addView(SqlexplorerViewConstants.SQLEXPLORER_DBVIEW);
//
//		IFolderLayout main = 
//						 layout.createFolder("main", IPageLayout.RIGHT, 0.75f,editorArea); //$NON-NLS-1$
//		
//				//main.addView(JFaceDbcViewConstants.JFACEDBC_VIEW);
//		main.addView(SqlexplorerViewConstants.SQLEXPLORER_DBVIEW);
//		main.addView(SqlexplorerViewConstants.SQLEXPLORER_CONNINFO);
//		main.addView(SqlexplorerViewConstants.SQLEXPLORER_SQLHISTORY);
//		
//		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f,
//			   "topLeft");
//		
//		bottomLeft.addView(SqlexplorerViewConstants.SQLEXPLORER_CONNECTIONS);
//		IFolderLayout bottom=layout.createFolder("bottom",IPageLayout.BOTTOM,0.5f,editorArea);
//		bottom.addView(SqlexplorerViewConstants.SQLEXPLORER_SQLRESULT);
//		
//    }
//    
    
    private void defineLayout(IPageLayout layout)
    {
        // Hide the editor.  Note the user can always make it come back.  
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);

        IFolderLayout left = layout.createFolder("topLeft", IPageLayout.LEFT, 0.5f,editorArea); //$NON-NLS-1$
//      left.addView(JFaceDbcViewConstants.JFACEDBC_DRIVER);
//      left.addView(JFaceDbcViewConstants.JFACEDBC_ALIAS);
        left.addView(SqlexplorerViewConstants.SQLEXPLORER_DBVIEW);
        
//      IFolderLayout main = 
//                       layout.createFolder("main", IPageLayout.RIGHT, 0.75f,editorArea); //$NON-NLS-1$
        
                //main.addView(JFaceDbcViewConstants.JFACEDBC_VIEW);
//      main.addView(JFaceDbcViewConstants.JFACEDBC_CONNINFO);
//      main.addView(JFaceDbcViewConstants.JFACEDBC_SQLHISTORY);
        
        IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.75f, //$NON-NLS-1$
               "topLeft"); //$NON-NLS-1$
        
//      bottomLeft.addView(JFaceDbcViewConstants.JFACEDBC_CONNECTIONS);
        bottomLeft.addView(SqlexplorerViewConstants.SQLEXPLORER_CONNECTIONS);
        
        IFolderLayout bottom=layout.createFolder("bottom",IPageLayout.BOTTOM,0.5f,editorArea); //$NON-NLS-1$
//      bottom.addView(JFaceDbcViewConstants.JFACEDBC_SQLEDITOR);
        bottom.addView(SqlexplorerViewConstants.SQLEXPLORER_SQLRESULT);
        bottom.addView(SqlexplorerViewConstants.SQLEXPLORER_SQLHISTORY);
        
    }

    
    
}
