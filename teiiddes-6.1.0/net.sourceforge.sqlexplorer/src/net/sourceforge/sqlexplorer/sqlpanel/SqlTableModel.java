package net.sourceforge.sqlexplorer.sqlpanel;

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

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

public class SqlTableModel {
    ArrayList list = new ArrayList();

    ResultSetReader rs;
    static final int limitRows = 100;
    boolean bFinished = false;
    boolean allRetrieved = false;
    private int count;
    private int maxRows;
    private HashMap columnMap = new HashMap();
    ResultSetMetaData metaData;
    String[] columnNames;
    SQLConnection conn;
    private String sql;

    public int getColumnIndex( String property ) {
        Integer ind = (Integer)columnMap.get(property);
        int i = -1;
        if (ind != null) {
            i = ind.intValue();
        }

        return i;
    }

    public String ss[];
    public SQLTableSorter sorter;

    public SqlTableModel( ResultSetReader rs,
                          ResultSetMetaData metaData,
                          int maxRows,
                          SQLConnection conn,
                          String[] ss,
                          SQLTableSorter sorter ) throws java.lang.Exception {
        this.sorter = sorter;
        this.ss = ss;
        this.conn = conn;
        this.rs = rs;
        this.maxRows = maxRows;
        this.metaData = metaData;
        count = metaData.getColumnCount();
        columnNames = new String[count];
        for (int i = 0; i < count; i++) {
            columnNames[i] = metaData.getColumnName(i + 1);
            columnMap.put(columnNames[i], new Integer(i));
        }

        int jj = 0;
        Object[] obj = null;
        while ((obj = rs.readRow()) != null) {

            jj++;
            list.add(new SqlRowElement(obj, count, this));
            if (jj == limitRows) return;

        }
        allRetrieved = true;
        bFinished = true;

    }

    public SqlTableModel( ResultSetReader theResultSetReader,
                          ResultSetMetaData theMetaData,
                          int theMaxRows,
                          SQLConnection theConnection,
                          String[] theColumnNames,
                          SQLTableSorter theSorter,
                          String theSql ) throws java.lang.Exception {
        this(theResultSetReader, theMetaData, theMaxRows, theConnection, theColumnNames, theSorter);
        this.sql = theSql;
    }

    public void moreRows() throws java.lang.Exception {// Retrieves "limitRows" lines each time
        if (conn == null || conn.getConnection() == null || conn.getConnection().isClosed()) return;
        int jj = 0;
        Object[] obj = null;
        while ((obj = rs.readRow()) != null) {
            jj++;
            list.add(new SqlRowElement(obj, count, this));
            if (list.size() >= maxRows) {
                bFinished = true;// We can't retrieve more than maxRows rows

                return;
            }
            if (jj == limitRows) return;

        }
        allRetrieved = true;
        bFinished = true;

    }

    /**
     * @return false when the connection is closed
     * @throws java.lang.Exception
     */
    public boolean tenMoreRows() throws java.lang.Exception {
        if (conn == null || conn.getConnection() == null || conn.getConnection().isClosed()) return false;
        int jj = 0;
        Object[] obj = null;
        while ((obj = rs.readRow()) != null) {
            jj++;
            SqlRowElement el = new SqlRowElement(obj, count, this);
            list.add(el);
            if (list.size() >= maxRows) {
                bFinished = true;// We can't retrieve more than maxRows rows

                return true;
            }
            if (jj == 10) return true;

        }
        // We are here only if the resultset has no more elements;
        bFinished = true;
        allRetrieved = true;
        return true;

    }

    public Object[] getElements() {
        return list.toArray();
    }

    // TODO remove this
    public void closeResultSet() {

        if (!bFinished) {
        }

    }

    public int getSize() {
        return list.size();
    }

    public String getPartial() {
        if (!allRetrieved) return new String("" + list.size() + "\\?"); //$NON-NLS-1$ //$NON-NLS-2$
        return new String("" + list.size() + "\\" + list.size()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isFinished() {
        return bFinished;
    }

    public String[] getColumns() {
        return columnNames;

    }

    public String getSql() {
        return this.sql;
    }

}
