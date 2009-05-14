package net.sourceforge.sqlexplorer.plugin.views;
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
import java.util.ArrayList;
import java.util.List;


 
/**
 * Constants that define all the available views in this plugin, and what we
 * want the default views to be.  Each view constant is the string ID provided
 * in the plugin.xml file. 
 * 
 * @author Macon Pegram
 */
public final class SqlexplorerViewConstants
{
	private static SqlexplorerViewConstants singleton = null;
	
    public static String SQLEXPLORER_CONNECTIONS="net.sourceforge.sqlexplorer.plugin.views.ConnectionsView";
	public static final String SQLEXPLORER_DRIVER  = "net.sourceforge.sqlexplorer.plugin.views.DriverView"; //$NON-NLS-1$
	public static final String SQLEXPLORER_ALIAS  = "net.sourceforge.sqlexplorer.plugin.views.AliasView"; //$NON-NLS-1$
	public static final String SQLEXPLORER_SQLRESULT="net.sourceforge.sqlexplorer.plugin.views.SqlResultsView";//$NON-NLS-1$
	public static final String SQLEXPLORER_DBVIEW="net.sourceforge.sqlexplorer.plugin.views.DBView";//$NON-NLS-1$
	public static final String SQLEXPLORER_CONNINFO="net.sourceforge.sqlexplorer.plugin.views.ConnectionInfo";//$NON-NLS-1$
	public static final String SQLEXPLORER_SQLHISTORY="net.sourceforge.sqlexplorer.plugin.views.SQLHistoryView";
    public static final String MESSAGE_LOG = "views.logView";

    /** Collection of the default views which this plugin offers */
    private List defaultViewList = null;
    
    /** Collection of all the views which this plugin offers */
    private List fullViewList = null;
    
   /**
    * Don't allow public construction. 
    */
    private SqlexplorerViewConstants ()
    {
        super();
        setupDefaultViewList();
        setupFullViewList();
    }

   /**
    * Sets up the list of views the user will should see by default
    *
    * @return List - Collection of view ids used by default.
    */    
    private void setupDefaultViewList()
    {
        defaultViewList = new ArrayList(5);
        defaultViewList.add(SQLEXPLORER_DBVIEW);
    	defaultViewList.add(SQLEXPLORER_DRIVER);
    	defaultViewList.add(SQLEXPLORER_ALIAS);
    	defaultViewList.add(SQLEXPLORER_CONNECTIONS);
		defaultViewList.add(SQLEXPLORER_SQLRESULT);
		defaultViewList.add(SQLEXPLORER_CONNINFO);
		defaultViewList.add(SQLEXPLORER_SQLHISTORY);
        defaultViewList.add(SQLEXPLORER_SQLHISTORY);
        defaultViewList.add(MESSAGE_LOG);
        
    }
    
   /**
    * Sets up the list of views the user will should see by default
    *
    * @return List - Collection of view ids used by default.
    */    
    private void setupFullViewList()
    {
        fullViewList = new ArrayList(getDefaultViewList());
    }
   /** 
    * Accessor for singleton instance.  Lazy constructs the singleton if needed.
    *
    * @return JFaceDbcViewConstants singleton
    */
    public static SqlexplorerViewConstants getInstance()
    {
        if (singleton == null)
           singleton = new SqlexplorerViewConstants();
           
        return singleton;
    }
    /**
     * Returns the list of default view Ids which we want to see by default when
     * a perspective is opened for the first time.
     * 
     * @return List
     */
    public List getDefaultViewList()
    {
        return defaultViewList;
    }

    /**
     * Returns the List of all available view Ids
     * 
     * @return List
     */
    public List getFullViewList()
    {
        return fullViewList;
    }
    
}
