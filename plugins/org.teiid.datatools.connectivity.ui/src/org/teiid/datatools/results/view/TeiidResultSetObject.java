package org.teiid.datatools.results.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.datatools.sqltools.result.IResultSetObject;
import org.eclipse.datatools.sqltools.result.IResultSetRow;
import org.eclipse.datatools.sqltools.result.ResultSetRow;
import org.eclipse.datatools.sqltools.result.ResultsViewPlugin;
import org.eclipse.datatools.sqltools.result.internal.utils.ILogger;
import org.eclipse.datatools.sqltools.result.internal.utils.SQLUtil;

public class TeiidResultSetObject implements IResultSetObject {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 1L;
        private String[]          _columnNames;
        private String[]		  _columnTypeNames;
        private List              _rows;
        private int[]             _columnTypes;   
        private int[]             _columnDisplaySizes;
        // total row count loaded into memory
        private int               _rowCountLoaded;
        // total row count of the JDBC result set
        private int               _totalRowCount;
        private File              _backupFile;
        private static ILogger    _log             = ResultsViewPlugin.getLogger(null);
        private transient ResultSetMetaData _meta;

        /**
         * Constructs a ResultSetObject instance from a JDBC ResultSet object
         * 
         * @param resultset the JDBC result set object
         * @param maxRowCount to limit the max row count
         * @param maxDisplayRowCount to limit the max display row count
         * @exception SQLException - if a database access error occurs
         */
        public TeiidResultSetObject(ResultSet resultset, int maxRowCount, int maxDisplayRowCount) throws SQLException
        {
        	this(resultset, maxRowCount, maxDisplayRowCount, true);
        }
        
        /**
         * Constructs a ResultSetObject instance from a JDBC ResultSet object
         * 
         * @param resultset the JDBC result set object
         * @param maxRowCount to limit the max row count
         * @param maxDisplayRowCount to limit the max display row count
         * @param showLabels to check if label is required to display as column heading
         * @exception SQLException - if a database access error occurs
         */
        public TeiidResultSetObject(ResultSet resultset, int maxRowCount, int maxDisplayRowCount, boolean showLabels) throws SQLException
        {
            ObjectOutputStream oos = null;
            try
            {
                ResultSetMetaData meta = resultset.getMetaData();
                _meta = meta;
                int columnCount = meta.getColumnCount();
                _totalRowCount = 0;
                _rows = new ArrayList();
                _columnNames = new String[columnCount];
                _columnDisplaySizes = new int[columnCount];
                _columnTypes = new int[columnCount];
                for (int i = 0; i < columnCount; i++)
                {
                	if (showLabels) // If true, use the column label as the column heading, if available.
                    {
                        _columnNames[i] = meta.getColumnLabel(i + 1);
                        //If no label, use the column name instead. Fix BZ221334
                        if ( null == _columnNames[i] || _columnNames[i].trim().equals("") )
                        {
                            _columnNames[i] = meta.getColumnName(i + 1);
                        }
                    }
                    else // Otherwise use the column name as the heading
                    {	
                        _columnNames[i] = meta.getColumnName(i + 1);
                    }

                    _columnDisplaySizes[i] = meta.getColumnDisplaySize(i + 1);
                    _columnTypes[i] = meta.getColumnType(i + 1);
                    //[bug227975] In SQL debug mode, some JDBC driver will call stored procedure, 
                    //which will suspend until current debugee finished.  
//                    _columnTypeNames[i] = meta.getColumnTypeName(i + 1);
                }

                IResultSetRow row = null;
                while (resultset.next() && (_totalRowCount < maxRowCount || maxRowCount == 0))
                {
                    row = new ResultSetRow(columnCount);
                    for (int i = 0; i < columnCount; i++)
                    {
                        row.setData(getObjectByTypeCoercion(resultset, i + 1, _meta.getColumnType(i + 1)), i);
                    }
                    _totalRowCount++;
                    
                    if (_totalRowCount < maxDisplayRowCount)
                    {
                        _rows.add(row);
                    }
                    else if (_totalRowCount == maxDisplayRowCount)
                    {
                        _rows.add(row);

                        if (_backupFile == null)
                        {
                            File dir = new File(ResultsViewPlugin.getDefault().getTempDir());
                            if (!dir.exists())
                            {
                                dir.mkdir();
                            }
                            _backupFile = File.createTempFile(String.valueOf(resultset.hashCode()), ".result", dir); //$NON-NLS-1$
                            
                            // The file will be deleted when the result instance is removed by the user
                            if (_backupFile.exists())
                            {
                                oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_backupFile)));
                            }
                        }
                        for (Iterator iter = _rows.iterator(); iter.hasNext();)
                        {
                            SQLUtil.saveResultToStream(oos, iter.next());
                        }
                    }
                    else if (_totalRowCount > maxDisplayRowCount)
                    {
                        SQLUtil.saveResultToStream(oos, row);
                    }
                }
                _rowCountLoaded = _rows.size();
            }
            catch (SQLException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                if (_backupFile != null && _backupFile.exists())
                {
                    _backupFile.delete();
                }
            }
            finally
            {
                try
                {
                    if (oos != null)
                    {
                        oos.close();
                        oos = null;
                    }
                }
                catch (IOException e)
                {
                    // ignore
                }

            }
        }

        /**
         * Constructs a ResultSetObject instance using a list of ResultSetRow instances. Notice that we will perform
         * restrict validation during the construction: 
         * <ul>
         * <li>All the parameters should not be null 
         * <li>The length of each array should be the same 
         * <li>The row object should not be null and must be an instance of IResultSetRow
         * </ul>
         * @param rows a list of IResultSetRow objects
         * @param columnNames column name array
         * @param columnTypes column type array (refer java.sql.Types)
         * @param columnDisplaySizes column display size array
         */
        public TeiidResultSetObject(List rows, String[] columnNames, int[] columnTypes, int[] columnDisplaySizes)
        {
            Assert.isTrue(!(rows == null || columnNames == null || columnDisplaySizes == null || columnTypes == null));
            int columnCount = columnNames.length;
            Assert.isTrue(!(columnTypes.length != columnCount || columnDisplaySizes.length != columnCount));

            Iterator iter = rows.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                Assert.isNotNull(obj);
                Assert.isTrue(obj instanceof IResultSetRow);
            }
            for (int i = 0; i < columnCount; i++)
            {
                if (columnDisplaySizes[i] < 0)
                {
                    columnDisplaySizes[i] = 0;
                }
                if (columnNames[i] == null)
                {
                    columnNames[i] = ""; //$NON-NLS-1$
                }
            }

            _rows = rows;
            _columnNames = columnNames;
            _columnTypes = columnTypes;
            _columnDisplaySizes = columnDisplaySizes;
            _rowCountLoaded = _rows.size();
            _totalRowCount = _rowCountLoaded;
        }
        
        /**
         * Constructs a ResultSetObject instance using a list of ResultSetRow instances. Notice that we will perform
         * restrict validation during the construction: 
         * <ul>
         * <li>All the parameters should not be null 
         * <li>The length of each array should be the same 
         * <li>The row object should not be null and must be an instance of IResultSetRow
         * </ul>
         * @param rows a list of IResultSetRow objects
         * @param columnNames column name array
         * @param columnTypes column type array (refer java.sql.Types)
         * @param columnDisplaySizes column display size array
         * @param typeNames the column type names array
         * @param maxDisplayRows the maximum number of rows to display, 0 means 
         * display all rows in the list
         */
        public TeiidResultSetObject(List rows, String[] columnNames, int[] columnTypes, 
        		int[] columnDisplaySizes, String[] typeNames, int maxDisplayRows)
        {
        	this(rows, columnNames, columnTypes, columnDisplaySizes);
        	List temp = new ArrayList();
        	_columnTypeNames = typeNames;
        	_totalRowCount = rows.size();
        	//modify rows according to maxDisplayRows
        	if (maxDisplayRows != 0 && maxDisplayRows < _rows.size())
        	{
        		for (int i=0;i<maxDisplayRows;i++)
        		{
        			temp.add(rows.get(i));    			
        		}
        		_rows = temp;
        		_rowCountLoaded = maxDisplayRows;    		
        	}
        	
        	ObjectOutputStream oos = null;
        	if (_totalRowCount > maxDisplayRows)
        	{
        		// save data to backup file for later retrieval
        		try
        		{
        			if (_backupFile == null)
        			{
        				File dir = new File(ResultsViewPlugin.getDefault().getTempDir());
        				if (!dir.exists())
        				{
        					dir.mkdir();
        				}
        				_backupFile = File.createTempFile(String.valueOf(rows.hashCode()), ".result", dir); //$NON-NLS-1$
                    
        				// The file will be deleted when the result instance is removed by the user
        				if (_backupFile.exists())
        				{
        					oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_backupFile)));
        				}
        			}
        			for (Iterator iter = rows.iterator();iter.hasNext();)
        			{
        				SQLUtil.saveResultToStream(oos, iter.next());
        			}
        		}    		
                catch (IOException e)
                {
                    if (_backupFile != null && _backupFile.exists())
                    {
                        _backupFile.delete();
                    }
                }
                finally
                {
                    try
                    {
                        if (oos != null)
                        {
                            oos.close();
                            oos = null;
                        }
                    }
                    catch (IOException e)
                    {
                        // ignore
                    }

                }
        	}
        }

        public int getColumnCount()
        {
            return _columnNames.length;
        }

        public String[] getColumnNames()
        {
            return _columnNames;
        }
        
        /**
         * Should be called before result set is closed, otherwise there is no guarantee that correct type names can be
         * returned
         * 
         * @return type name of all the columns
         */
        public String[] getColumnTypeNames()
        {
            try
            {
                if ( _columnTypeNames == null && _meta != null )
                {
                    int columnCount = _meta.getColumnCount();
                    _columnTypeNames = new String[columnCount];
                    for (int i = 0; i < columnCount; i++)
                    {
                        _columnTypeNames[i] = _meta.getColumnTypeName(i + 1);
                    }
                }
            }
            catch (Exception e)
            {
                // ignore
            }

        	return _columnTypeNames;
        }

        public String getColumnName(int index)
        {
            if ((index < 1) || (index > _columnNames.length))
            {
                return null;
            }
            return _columnNames[index - 1];
        }

        public int[] getColumnDisplaySizes()
        {
            return _columnDisplaySizes;
        }

        public int getColumnDisplaySize(int index)
        {
            if ((index < 1) || (index > _columnDisplaySizes.length))
            {
                return 0;
            }
            return _columnDisplaySizes[index - 1];
        }

        
        public int[] getColumnSQLTypes()
        {
            return _columnTypes;
        }

        public int getColumnSQLType(int index)
        {
            // If specify a wrong index, we return Types.CHAR
            if ((index < 1) || (index > _columnTypes.length))
            {
                return Types.CHAR;
            }
            return _columnTypes[index - 1];
        }

        public int getRowCount()
        {
            return _rowCountLoaded;
        }

        public int getTotalRowCount()
        {
            return _totalRowCount;
        }

        public IResultSetRow getRowData(int row)
        {
            if ((row < 0) || (row > _rows.size() - 1))
            {
                return null;
            }
            return (IResultSetRow) _rows.get(row);
        }

        public Iterator getAllRecords()
        {
            if (_backupFile == null)
            {
                return _rows.iterator();
            }
            Iterator iter = new BackupRecord(_backupFile);
            return iter;
        }

        public Iterator getDisplayRecords()
        {
            return _rows.iterator();
        }

        public boolean isAllResultLoaded()
        {
            return _totalRowCount == _rowCountLoaded;
        }

        public void dispose()
        {
            if (_backupFile != null && _backupFile.exists())
            {
                _backupFile.delete();
            }
        }

        /**
         * Iterator over a cache file
         * 
         * @author Dafan Yang
         */
        private class BackupRecord implements Iterator
        {

            File              _backupFile;
            ObjectInputStream _ois;
            Object            _nextRecord = null;
            int               _index      = 0;

            /**
             * 
             */
            public BackupRecord(File backupFile)
            {
                _backupFile = backupFile;
                if (backupFile != null && backupFile.exists())
                {
                    try
                    {
                        _ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backupFile)));
                        _nextRecord = SQLUtil.getResultFromStream(_ois);
                    }
                    catch (Exception e)
                    {
                        _log.error("ResultSetObjectImpl_error_iterator", e); //$NON-NLS-1$
                    }
                }
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext()
            {
                // by definition, hasNext should not perform any fetch operation, which should be the job of next().
                return (_nextRecord != null);
            }

            public Object next()
            {
                // cache the current record and fetch the next
                Object current = _nextRecord;
                if (_ois != null)
                {
                    IResultSetRow row = (IResultSetRow) SQLUtil.getResultFromStream(_ois);
                    _nextRecord = row;
                    _index++;
                    // when there're no rows, close the stream
                    if (_nextRecord == null)
                    {
                        try
                        {
                            _ois.close();
                            _ois = null;
                        }
                        catch (IOException e)
                        {
                            _log.error("ResultSetObjectImpl_error_next", e); //$NON-NLS-1$
                        }
                    }
                }

                return current;
            }
        }

    public static Object getObjectByTypeCoercion(ResultSet resultSet, int index, int dataType) throws SQLException
    {
    	switch (dataType)
    	{
            case Types.TIMESTAMP:
                return resultSet.getTimestamp(index);
            case Types.CLOB:                
                return resultSet.getString(index);
            case Types.BLOB:
            	return "<Blob>";
            	//return resultSet.getBlob(index);
            case Types.SQLXML:
            	return resultSet.getSQLXML(index);
    		default:
                return resultSet.getObject(index);
    	}
    }

}
