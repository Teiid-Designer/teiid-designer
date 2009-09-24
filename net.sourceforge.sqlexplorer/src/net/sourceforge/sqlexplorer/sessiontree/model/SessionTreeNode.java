package net.sourceforge.sqlexplorer.sessiontree.model;
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


import java.io.File;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.IdentifierFactory;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.model.CatalogNode;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseModel;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.SchemaNode;
import net.sourceforge.sqlexplorer.ext.PluginManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.utility.Dictionary;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Table;

public class SessionTreeNode  implements ISessionTreeNode {

    private static final SessionDateFormat dateFormat = new SessionDateFormat("HH:mm:ss MMM dd");

	private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);
	public DatabaseModel  dbModel;
	private RootSessionTreeNode parent;
	private SQLConnection m_conn;
	private ISQLAlias alias;
	private SessionTreeModel model;
	private ArrayList ls=new ArrayList(10);
	//private ViewerManager mg;
	//private TreeViewer tv;
	//private boolean initialized=false;
	PluginManager pm=null;
	private IIdentifier _id = IdentifierFactory.getInstance().createIdentifier();

	private Dictionary dictionary=new Dictionary();
	public Dictionary getDictionary(){
		return dictionary;
	}


	public IIdentifier getIdentifier()
	{
		return _id;
	}


	public SQLConnection getConnection(){
		return m_conn;
	}
	/**
	 * @see org.gnu.amaz.ISessionTreeNode#getChildren()
	 */
	public Object[] getChildren() {
		return ls.toArray();
	}
	Table table;
	final private String pswd;
	boolean assistanceEnabled;
	/**
	 * @see org.gnu.amaz.ISessionTreeNode#getParent()
	 */
	public Object getParent() {
		return parent;
	}
	public SQLConnection getSQLConnection(){
		return m_conn;
	}
	public String getCurrentConnectionPassword(){
		return pswd;
	}
	public boolean supportsCatalogs(){
		return dbModel.supportsCatalogs();
	}
	public String [] getCatalogs(){
		return dbModel.getCatalogNames();
	}
	public String getCatalog(){
		String cat="";
		try{
			cat=m_conn.getCatalog();
		}catch(Throwable e){
		}
		return cat;
	}
	public void setCatalog(String cat) throws SQLException{
		m_conn.getConnection().setCatalog(cat);
	}
	public SessionTreeNode(final SQLConnection conn,ISQLAlias alias,SessionTreeModel md,IProgressMonitor monitor, final String pswd) throws InterruptedException{
		m_conn=conn;
		this.alias=alias;
		pm=SQLExplorerPlugin.getDefault().pluginManager;
		pm.sessionStarted(SessionTreeNode.this);
		dbModel=new DatabaseModel(this,pm);
		model=md;
		parent=md.getRoot();
		parent.add(this);
		this.pswd=pswd;


			//	DataBaseSessionTreeNode treeNode=createDatabaseSessionTreeNode();

				//DatabaseModel dbModel=null;//JFaceDbcPlugin.getDefault().treeNode.getDBModel();
		assistanceEnabled=SQLExplorerPlugin.getDefault().getPreferenceStore().getBoolean(IConstants.SQL_ASSIST);
		final String cancelled="Operation was cancelled by user";
		if(monitor !=null && monitor.isCanceled())
			throw new InterruptedException(cancelled);

		try{

			Object []children=dbModel.getChildren();
			DatabaseNode dbNode=((DatabaseNode)children[0]);
			children=dbNode.getChildren();
			if(children==null)
				return;
			for(int i=0;i<children.length;i++){
				if(monitor!=null)
					monitor.subTask(children[i].toString());
				if(monitor !=null && monitor.isCanceled())
					throw new InterruptedException(cancelled);
				IDbModel idbModel=(IDbModel)children[i];//Catalog or Schema Node
				dictionary.putCatalogSchemaName(idbModel.toString(),idbModel);
				if(assistanceEnabled){
					//Object [] children2=null;
					if(idbModel instanceof SchemaNode){
						((SchemaNode)idbModel).fastLoadSchema();
					}
					else if (idbModel instanceof CatalogNode){
						((CatalogNode)idbModel).fastLoadCatalog();
					}
/*						children2=idbModel.getChildren();
						if(children2!=null){
							for(int j=0;j<children2.length;j++){
								if(monitor !=null && monitor.isCanceled())
									throw new InterruptedException(cancelled);
								if(children2[j] instanceof TableObjectTypeNode){
									scanTableNode((TableObjectTypeNode)children2[j]);
								}
							}
						}
					}*/
				}
			}
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error enabling assistance ", e); //$NON-NLS-1$
		}
	}

	/*private void scanTableNode(TableObjectTypeNode parent){
		Object [] children=parent.getChildren();
	}*/

    /**
     * Obtains the alias short name concatenated with the time the connection was opened.
     * @return the string representation
     */
	@Override
    public String toString(){
		try{
            StringBuffer result = new StringBuffer(getShortName()).append('-');
            SQLConnection conn = getConnection();

            if ((conn != null) && (conn.getTimeClosed() == null)) {
                result.append(dateFormat.format(conn.getTimeOpened()));
            } else {
                result.append(Messages.getString("SessionTreeNode.connectionClosed")); //$NON-NLS-1$
            }

            return result.toString();
		}
		catch(java.lang.Throwable e){
			SQLExplorerPlugin.error("Error getting the alias name ",e); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

    /**
     * Obtains the full name of the alias.
     * @return the full name
     * @since 5.0.1
     */
    public String getFullName() {
        return this.alias.getName();
    }

    /**
     * Obtains the short name of the alias.
     * @return the short name
     * @since 5.0.1
     */
    public String getShortName() {
        return new File(getFullName()).getName();
    }

    public void add(ISessionTreeNode n){
		ls.add(n);
		//model.ModelChanged();
	}
	public void remove(ISessionTreeNode n){
		ls.remove(n);
		//model.ModelChanged();
	}

	public void close(){
		pm.sessionEnding(this);
		parent.remove(this);
		//closeWindow();

		Object []ls=listeners.getListeners();

		for(int i=0;i<ls.length;++i){
			try{
				((ISessionTreeClosedListener)ls[i]).sessionTreeClosed();
			}catch(Throwable e){
			}

		}
		model.modelChanged(null);
		try{
			m_conn.close();
		}catch(Throwable e){
			SQLExplorerPlugin.error("Error closing database connection ",e); //$NON-NLS-1$
		}

		//int sz=ls.size();
	}
	public void commit(){
		try{
			m_conn.commit();
		}
		catch(Throwable e){
			SQLExplorerPlugin.error("Error committing ",e); //$NON-NLS-1$
		}

	}
	public void rollback(){
		try{
			m_conn.rollback();
		}
		catch(Throwable e){
			SQLExplorerPlugin.error("Error rollbacking ",e); //$NON-NLS-1$

		}
	}

	public PluginManager getPluginManager(){
		return pm;
	}
	public boolean isAutoCommitMode(){
		boolean result=false;
		try{
			result=m_conn.getAutoCommit();
		}catch(Throwable e){
		}
		return result;
	}





	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */

	public void addListener(ISessionTreeClosedListener listener) {
		listeners.add(listener);
	}

	public ISQLAlias getAlias() {
		return alias;
	}

    private static class SessionDateFormat extends SimpleDateFormat {
        /**
         */
        private static final long serialVersionUID = 1L;

        public SessionDateFormat(String pattern) {
            super(pattern);
        }

        @Override
        public synchronized Date parse(String source) throws ParseException {
            return super.parse(source);
        }

        @Override
        public synchronized StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            return super.format(date, toAppendTo, fieldPosition);
        }
    }

}

