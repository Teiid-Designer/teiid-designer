package net.sourceforge.sqlexplorer.plugin;

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

/*import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;*/

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import net.sourceforge.sqlexplorer.AliasModel;
import net.sourceforge.sqlexplorer.DataCache;
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.ext.PluginManager;
import net.sourceforge.sqlexplorer.plugin.views.SqlHistoryRecord;
import net.sourceforge.sqlexplorer.sessiontree.model.RootSessionTreeNode;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModel;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class SQLExplorerPlugin extends AbstractUIPlugin {

	public static void error(String message, Throwable t){
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, String.valueOf(message), t));
	}
	private int count=0;
	public final static String PLUGIN_ID = "net.sourceforge.sqlexplorer"; //$NON-NLS-1$
	public SessionTreeModel stm=new SessionTreeModel();
	public PluginManager pluginManager;
	//The shared instance.
	private static SQLExplorerPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private SQLDriverManager _driverMgr;
	private DataCache _cache;
	private AliasModel aliasModel;
	private DriverModel driverModel;
    private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

    private int nextId;

    private ArrayList sqlHistory=new ArrayList();
    public ArrayList getSQLHistory(){
        return sqlHistory;
    }

    /**
     * Indicates if the query plan should be part of the query results.
     * 
     * @return <code>true</code>if show query plan; <code>false</code> otherwise.
     * @since 5.0
     */
    public boolean shouldShowQueryPlan() {
        boolean defaultValue = new DefaultScope().getNode(getBundle().getSymbolicName()).getBoolean(IConstants.SHOW_QUERY_PLAN,
                                                                                                    true);
        return new InstanceScope().getNode(getBundle().getSymbolicName()).getBoolean(IConstants.SHOW_QUERY_PLAN, defaultValue);
    }

    public void addListener(SqlHistoryChangedListener listener) {
        listeners.add(listener);
    }
    public void removeListener(SqlHistoryChangedListener listener){
        listeners.remove(listener);
    }

    public SqlHistoryRecord addSQLtoHistory(String theSql,
                                            SessionTreeNode theSession) {
        if (theSql == null) {
            throw new IllegalArgumentException("SQL can't be null when adding to the history");
        }

        String sql = theSql.trim();
        SqlHistoryRecord result = null;

        // don't add if we already have a record, just put in dummy ID as it is not used in the equals.
        SqlHistoryRecord temp = new SqlHistoryRecord(sql, new Object(), theSession);

        for (int size = this.sqlHistory.size(), i = 0; i < size; ++i) {
            SqlHistoryRecord record = (SqlHistoryRecord)this.sqlHistory.get(i);

            if (record.equals(temp)) {
                result = record;
                break;
            }
        }

        if (result == null) {
            result = new SqlHistoryRecord(sql, getNextId(), theSession);
    		this.sqlHistory.add(result);

            // notify listeners
            Object[] ls = this.listeners.getListeners();

            for (int i = 0; i < ls.length; ++i) {
                try {
                    ((SqlHistoryChangedListener)ls[i]).added(result.getId());
                } catch (Throwable e) {
                    this.listeners.remove(ls[i]);
                }
            }
        }

        return result;
	}

    private Object getNextId() {
        if (this.sqlHistory.isEmpty()) {
            this.nextId = 0;
        }

        return Integer.toString(++this.nextId);
    }

    public Object removeSQLHistory(String theSql) {
        SqlHistoryRecord result = null;

        if (theSql != null) {
            String sql = theSql.trim();

            for (int size = this.sqlHistory.size(), i = 0; i < size; ++i) {
                SqlHistoryRecord record = (SqlHistoryRecord)this.sqlHistory.get(i);

                if (record.getSql().equals(sql)) {
                    result = record;
                    this.sqlHistory.remove(result);

                    // notify listeners
                    Object[] ls = this.listeners.getListeners();

                    for (int j = 0; j < ls.length; ++j) {
                        try {
                            ((SqlHistoryChangedListener)ls[j]).removed(result.getId());
                        } catch (Throwable e) {
                            this.listeners.remove(ls[j]);
                        }
                    }

                    break;
                }
            }
        }

        performGc(1000);
        return result;
    }

    public Object getSqlHistoryRecordId(String theSql) {
        Object result = null;

        if (theSql != null) {
            String sql = theSql.trim();

            for (int size = this.sqlHistory.size(), i = 0; i < size; ++i) {
                SqlHistoryRecord record = (SqlHistoryRecord)this.sqlHistory.get(i);

                if (record.getSql().equals(sql)) {
                    result = record.getId();
                    break;
                }
            }
        }

        return result;
    }

	public SQLDriverManager getSQLDriverManager(){
		return _driverMgr;
	}
	public AliasModel getAliasModel(){
		return aliasModel;
	}
	public DriverModel getDriverModel(){
		return driverModel;
	}


	/**
	 * The constructor.
	 */
	public SQLExplorerPlugin() {
		plugin = this;
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start( BundleContext context ) throws Exception {
		super.start(context);

		//if(!earlyStarted){
			try{
				pluginManager=new PluginManager();
				pluginManager.loadPlugins();
				pluginManager.initializePlugins();

			}catch(Throwable e){
				error("Error loading plugins preference pages",e);//$NON-NLS-1$
			}
			try{
				//DriverManager.setLogWriter(new PrintWriter(new BufferedOutputStream(new FileOutputStream("C:\\out1.txt"))));
			}catch(Throwable e){
			}

			_driverMgr=new SQLDriverManager();
			_cache = new DataCache(_driverMgr);
			aliasModel=new AliasModel(_cache);
			driverModel=new DriverModel(_cache);
			Object [] aliases= aliasModel.getElements();
			for(int i=0;i<aliases.length;i++){
				final ISQLAlias alias=(ISQLAlias)aliases[i];
				if(alias.isConnectAtStartup()){
					try{
						ISQLDriver dv=driverModel.getDriver(alias.getDriverIdentifier());
						final SQLConnection conn=_driverMgr.getConnection(dv, alias,alias.getUserName(),alias.getPassword());

						Display.getDefault().asyncExec(new Runnable(){
							public void run() {

									try {
										stm.createSessionTreeNode(conn,alias,null,alias.getPassword());
									} catch (InterruptedException e) {
										throw new RuntimeException();
									}

							}
						});


					}catch(Throwable e){
						error("Error creating sql connection to "+alias.getName(),e);//$NON-NLS-1$
					}
				}
			}
		//}

		try {
			resourceBundle= ResourceBundle.getBundle("net.sourceforge.sqlexplorer.test"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}

	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop( BundleContext context ) throws Exception {
		super.stop(context);
		closeAllConnections();
	}

	/**
	 * Close all sessions
	 */
	public void closeAllConnections() {
		RootSessionTreeNode rstn = stm.getRoot();
		rstn.closeAllConnections();
	}

	/**
	 * Returns the shared instance.
	 */
	public static SQLExplorerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= SQLExplorerPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	/**
	 * @return
	 */
	public int getNextElement() {

		return count++;
	}

    public void performGc(long delay ) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.gc();
            }
        }, delay);
    }
}
