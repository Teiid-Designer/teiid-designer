/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.metamatrix.core.modeler.util.ArgCheck;


/** 
 * @since 4.3
 */
public class ExcelDatabaseMetaDataHandler implements
                                         InvocationHandler {

    private static List COLUMN_DESCRIPTIONS = new ArrayList();
    static {
        COLUMN_DESCRIPTIONS.add("TABLE_CAT");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("TABLE_SCHEM");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("TABLE_NAME ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("COLUMN_NAME ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("DATA_TYPE ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("TYPE_NAME ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("COLUMN_SIZE ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("BUFFER_LENGTH ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("DECIMAL_DIGITS ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("NUM_PREC_RADIX ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("NULLABLE ");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("REMARKS");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("COLUMN_DEF");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("SQL_DATA_TYPE");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("SQL_DATETIME_SUB");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("CHAR_OCTET_LENGTH");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("ORDINAL_POSITION");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("IS_NULLABLE");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("SCOPE_CATLOG");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("SCOPE_SCHEMA");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("SCOPE_TABLE");//$NON-NLS-1$
        COLUMN_DESCRIPTIONS.add("SOURCE_DATA_TYPE");//$NON-NLS-1$
    }
        
    private static ResultSet EMPTY_RESULTSET = new ResultSetImpl(Collections.EMPTY_LIST, COLUMN_DESCRIPTIONS);
    private DatabaseMetaData metadata;
    private File excelFile;
    protected ResultSet tables;
    protected Map columns = new HashMap(); //table name -> column result set

    protected ExcelDatabaseMetaDataHandler(DatabaseMetaData metadata, File excelFile) throws SQLException{
        ArgCheck.isNotNull(excelFile);
        this.metadata = metadata;
        this.excelFile = excelFile;
        loadExcelDocument();
    }
    
    /** 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     * @since 4.3
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {
        String methodName = method.getName();

        if(methodName.equals("getColumns")) {//$NON-NLS-1$
            ResultSet results = (ResultSet)this.columns.get(((String)args[2]).toUpperCase());
            if(results == null) {
                results = EMPTY_RESULTSET;
            }
            ((ResultSetImpl)results).reset();
            return results;
        }
        if(methodName.equals("getTables")) {//$NON-NLS-1$
            return this.tables;
        }
        if(methodName.equals("getTableTypes")) {//$NON-NLS-1$
            List types = new ArrayList();
            List row = new ArrayList();
            row.add("TABLE");//$NON-NLS-1$
            types.add(row);
            List columnNames = new ArrayList(1);
            columnNames.add("TABLE_TYPE");//$NON-NLS-1$
            return new ResultSetImpl(types, columnNames);
        }
        if(methodName.equals("supportsCatalogsInDataManipulation")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsCatalogsInIndexDefinitions")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsCatalogsInDataManipulation")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsCatalogsInPrivilegeDefinitions")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsCatalogsInProcedureCalls")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsCatalogsInTableDefinitions")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsSchemasInDataManipulation")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsSchemasInIndexDefinitions")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsSchemasInPrivilegeDefinitions")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsSchemasInProcedureCalls")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("supportsSchemasInTableDefinitions")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("getPrimaryKeys")) {//$NON-NLS-1$
            return EMPTY_RESULTSET;
        }
        if(methodName.equals("getImportedKeys")) {//$NON-NLS-1$
            return EMPTY_RESULTSET;
        }
        if(methodName.equals("getExportedKeys")) {//$NON-NLS-1$
            return EMPTY_RESULTSET;
        }
        return method.invoke(this.metadata, args);
    }

    protected void loadExcelDocument() throws SQLException{
        try {
            //create a POIFSFileSystem object to read the data
            POIFSFileSystem pfs = new POIFSFileSystem(new FileInputStream(this.excelFile));
            HSSFWorkbook workBook = new HSSFWorkbook(pfs);
            int sheetNumber = workBook.getNumberOfSheets();
            if(sheetNumber == 0) {
                return;
            }
            List tables = new ArrayList();
            for(int i=0; i<sheetNumber; i++) {
                HSSFSheet sheet = workBook.getSheetAt(i);
                String tableName = workBook.getSheetName(i);
                int firstRowNumber = sheet.getFirstRowNum();
                HSSFRow firstRow = sheet.getRow(firstRowNumber);
                HSSFRow firstDataRow = sheet.getRow(firstRowNumber + 1);
                if(firstRow == null) {
                    continue;
                }
                short firstCell = firstRow.getFirstCellNum();
                short lastCell = firstRow.getLastCellNum();
                List columns = new ArrayList();
                for(short j=firstCell; j<lastCell; j++) {
                    HSSFCell cell = firstRow.getCell(j);
                    Object[] cellType = getCellType(firstDataRow, j, sheet);
                    columns.add(Arrays.asList(new Object[]{null, null, tableName, cell.getStringCellValue(),cellType[0],cellType[1],null,null,null,null,new Integer(DatabaseMetaData.columnNullable),null,null,null,null,null,null,"YES",null,null,null,null}));//$NON-NLS-1$
                }
                tables.add(Arrays.asList(new Object[]{null, null, tableName, "TABLE", "Excel_Sheet", null,null,null,null,null})); //$NON-NLS-1$         //$NON-NLS-2$       
                this.columns.put(tableName.toUpperCase(), new ResultSetImpl(columns, COLUMN_DESCRIPTIONS));
            }
            this.tables = new ResultSetImpl(tables, COLUMN_DESCRIPTIONS);
        }catch(IOException ioe) {
            throw new SQLException(ioe.getMessage());
        }
    }
    
    private Object[] getCellType(HSSFRow firstDataRow, short cellIndex, HSSFSheet sheet) {
        if(firstDataRow != null) {
            HSSFCell cell = firstDataRow.getCell(cellIndex);
            if(cell == null) {
                int dataRowNumber = firstDataRow.getRowNum();
                while(cell == null) {
                    //go to the next row to find a cell that is not null
                    HSSFRow row = sheet.getRow(++dataRowNumber);
                    if(row == null) {
                        break;
                    }
                    cell = row.getCell(cellIndex);
                }
            }
            if(cell != null) {
                int celltype = cell.getCellType();
                switch(celltype) {
                    case HSSFCell.CELL_TYPE_BOOLEAN :
                        return new Object[] {new Integer(Types.BOOLEAN), "BOOLEAN"};//$NON-NLS-1$
                    case HSSFCell.CELL_TYPE_NUMERIC :
                        //check if it is date
                        if(HSSFDateUtil.isCellDateFormatted(firstDataRow.getCell(cellIndex))) {
                            return new Object[] {new Integer(Types.DATE), "DATE"};//$NON-NLS-1$
                        }
                        return new Object[] {new Integer(Types.DOUBLE), "DOUBLE"};//$NON-NLS-1$
                }
            }
        }
        return new Object[] {new Integer(Types.VARCHAR), "VARCHAR2"};//$NON-NLS-1$
    }
}
