package net.sourceforge.sqlexplorer.dbviewer.model;

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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.swt.widgets.Composite;

public class TableNode implements IDbModel {
    static String sep = System.getProperty("line.separator");//$NON-NLS-1$

    public Composite getComposite( DetailManager detailManager ) {
        return null;
    }

    private IDbModel parent;
    private String txt;
    private ArrayList list = new ArrayList(1);

    public Object getParent() {
        return parent;
    }

    public Object[] getChildren() {
        return list.toArray();
    }

    @Override
    public String toString() {
        return txt;
    }

    private SQLConnection conn;
    private ITableInfo iTableInfo;
    private volatile int hash = 0;
    String tableDesc = null;

    public boolean isTable() {
        return iTableInfo.getType().equalsIgnoreCase("TABLE");//$NON-NLS-1$
    }

    public boolean isView() {
        return iTableInfo.getType().equalsIgnoreCase("VIEW");//$NON-NLS-1$
    }

    public boolean isSynonym() {
        return iTableInfo.getType().equalsIgnoreCase("SYNONYM");//$NON-NLS-1$
    }

    public boolean isDocument() {
        return iTableInfo.getType().equalsIgnoreCase("DOCUMENT");//$NON-NLS-1$
    }

    private ArrayList listColNames = null;

    TableNode( IDbModel s,
               String name,
               SQLConnection conn,
               ITableInfo iTableInfo ) {
        this.conn = conn;
        parent = s;
        txt = name;

        this.iTableInfo = iTableInfo;
    }

    public long getRowCount() throws Throwable {
        // loadTableInfo();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select count(*) from " + iTableInfo.getQualifiedName()); //$NON-NLS-1$
        long result = 0;
        if (rs.next()) {
            result = rs.getLong(1);
        }

        rs.close();
        st.close();
        return result;
    }

    public ArrayList getColumnNames() throws Exception {
        if (listColNames == null) {
            listColNames = new ArrayList();
            ResultSet rs = getColumns();
            if (rs != null) {
                while (rs.next()) {
                    listColNames.add(rs.getString("COLUMN_NAME"));//$NON-NLS-1$
                }
                rs.close();
            }
        }
        return listColNames;
    }

    public ResultSet getColumns() throws java.lang.Exception {
        // loadTableInfo();
        if (iTableInfo != null) return conn.getSQLMetaData().getColumns(iTableInfo);
        return null;
    }

    public ResultSet getIndexes() throws java.lang.Exception {
        // loadTableInfo();
        if (iTableInfo != null) {
            return conn.getSQLMetaData().getIndexInfo(iTableInfo);

        }
        return null;
    }

    // public ResultSet getPreview()throws java.lang.Exception{
    public ResultSet getPreview( int iMaxRowCount ) throws java.lang.Exception {

        // loadTableInfo();
        Statement st = conn.createStatement();
        try {
            st.setMaxRows(iMaxRowCount);
        } catch (Throwable e) {
        }
        ResultSet rs = null;
        if (iTableInfo != null) {
            rs = st.executeQuery("select * from " + iTableInfo.getQualifiedName()); //$NON-NLS-1$
        }
        return rs;
    }

    public ResultSet getPK() throws java.lang.Exception {
        // loadTableInfo();
        if (iTableInfo != null) return conn.getSQLMetaData().getPrimaryKeys(iTableInfo);
        return null;
    }

    public String[] getPrimaryKeyColumns() {
        if (iTableInfo != null) {
            try {
                ResultSet rs = conn.getSQLMetaData().getPrimaryKeys(iTableInfo);
                ArrayList ls = new ArrayList(10);
                while (rs.next()) {
                    ls.add(rs.getString("COLUMN_NAME"));//$NON-NLS-1$
                }

                rs.close();
                return (String[])ls.toArray(new String[ls.size()]);
            } catch (Throwable e) {
            }
        }

        return new String[0];
    }

    // Add LUJ
    public ResultSet getFK() throws java.lang.Exception {
        // loadTableInfo();
        if (iTableInfo != null) {
            return conn.getSQLMetaData().getImportedKeys(iTableInfo);
        }
        return null;
    }

    /*private void loadTableInfo()throws java.lang.Exception{
    	if(iTableInfo!=null)
    		return;*/
    // TableObjectTypeNode totn=((TableObjectTypeNode)parent);
    /*	//ITableInfo[] tables = conn.getSQLMetaData().getTables(null, totn.getParent().toString(), txt, new String[]{totn.toString()});
    	String sCatalog = totn.getParent().toString();
    	if(sCatalog != null && sCatalog.equals(Messages.getString("NoCatalog_2"))) sCatalog = null;
    	
    	//System.out.println("loading table info "+sCatalog+" "+txt+ " "+totn.toString() );
    	
    	ITableInfo[] tables = conn.getSQLMetaData().getTables(null, sCatalog, txt, new String[]{totn.toString()});
    */

    /*	IDbModel tableParent = (IDbModel)parent.getParent();

    	String catName = null, schemaName = null;
    	  if(tableParent instanceof CatalogNode) {
    	   catName = tableParent.toString();
    	   if (catName.equals(Messages.getString("NoCatalog_2"))) { //$NON-NLS-1$
    		catName = null;
    	   }
    	  } else if(tableParent instanceof SchemaNode) {
    	   schemaName = tableParent.toString();
    	  }

    	  ITableInfo[] tables = conn.getSQLMetaData().getTables(catName,
    	schemaName,
    	txt, new String[]{parent.toString()}); //$NON-NLS-1$
    	if(tables.length==1){
    		iTableInfo=tables[0];
    	}
    }*/
    public ITableInfo getTableInfo() {
        // loadTableInfo();
        return iTableInfo;
    }

    /**
     * @return
     */
    public String getTableDesc() {
        if (tableDesc == null) {
            StringBuffer sb = new StringBuffer(200);

            try {
                sb.append(" " + getTableInfo().getQualifiedName() + " ");//$NON-NLS-1$ //$NON-NLS-2$
                sb.append(sep).append(sep);
                ResultSet rs = getColumns();
                while (rs.next()) {
                    StringBuffer line = new StringBuffer(40);
                    line.append(" " + rs.getString("COLUMN_NAME"));//$NON-NLS-1$ //$NON-NLS-2$
                    int length = line.length();
                    if (length < 25) {
                        for (int i = length; i < 25; i++)
                            line.append(" ");//$NON-NLS-1$
                    }

                    line.append(rs.getString("TYPE_NAME"));//$NON-NLS-1$
                    line.append("(");//$NON-NLS-1$
                    line.append(rs.getInt("COLUMN_SIZE"));//$NON-NLS-1$
                    line.append(")" + sep);//$NON-NLS-1$
                    sb.append(line);
                }
                rs.close();
            } catch (Throwable e) {
            }
            tableDesc = sb.toString();
        }

        return tableDesc;
    }

    /**
     * @param node
     */
    public void setParent( TableObjectTypeNode node ) {
        parent = node;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == this) return true;
        if (!(obj instanceof TableNode)) return false;
        TableNode tn = (TableNode)obj;
        return tn.txt == this.txt && getSchemaOrCatalog().toString().equals(tn.getSchemaOrCatalog().toString());
    }

    public IDbModel getSchemaOrCatalog() {
        if (parent != null) {
            IDbModel pp = parent;
            return (IDbModel)pp.getParent();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            int result = 17;
            if (txt != null) result = 37 * result + txt.hashCode();
            if (parent != null) {
                // IDbModel pp=(IDbModel)parent;
                result = 37 * result + getSchemaOrCatalog().toString().hashCode();
            }
            hash = result;
        }
        return hash;
    }

}
