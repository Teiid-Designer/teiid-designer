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
package net.sourceforge.sqlexplorer.gef.wizards;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;

import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.gef.model.Column;
import net.sourceforge.sqlexplorer.gef.model.Fk;
import net.sourceforge.sqlexplorer.gef.model.FkDetailSub;
import net.sourceforge.sqlexplorer.gef.model.Index;
import net.sourceforge.sqlexplorer.gef.model.IndexDetailSub;
import net.sourceforge.sqlexplorer.gef.model.Pk;
import net.sourceforge.sqlexplorer.gef.model.PkDetailSub;
import net.sourceforge.sqlexplorer.gef.model.Table;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;


public class TableAdapter {
	ArrayList columnList=new ArrayList(); //Each object is a Column
	Pk pk;
	ArrayList fkList=new ArrayList();//Each object is a Fk
	ArrayList indexList=new ArrayList();//Each object is a Fk
	ITableInfo tableInfo;
	/**
	 * @param tn
	 */
	public TableAdapter(TableNode tn) throws Exception{
		tableInfo=tn.getTableInfo();
		ResultSet rs=null;
		try{
			rs=tn.getColumns();
			ResultSetReader reader=new ResultSetReader(rs);
			Object [] obj=null;
			
			while((obj=reader.readRow())!=null){
				columnList.add(new Column(obj));
			}
		}catch(Throwable e){
		}
		finally{
			try{
				rs.close();
			}catch(Throwable e){
			}
		}
		try{
			
			rs=tn.getPK();
			
			
			String previousPKName=null;
			Pk previousPkRow=null;
			while(rs.next()==true){
				String colName=rs.getString("COLUMN_NAME"); //$NON-NLS-1$
				short order=rs.getShort("KEY_SEQ"); //$NON-NLS-1$
				String pkName=rs.getString("PK_NAME");  //$NON-NLS-1$
				if((pkName!=null)&&(!pkName.equals(previousPKName))){
					previousPkRow=new Pk(pkName);
					previousPKName=pkName;
					pk=previousPkRow;//Actually we should have only one primary key
				}
				if(pkName!=null){
					PkDetailSub subRow=new PkDetailSub(colName,order);
					previousPkRow.addChild(subRow);
				}
			}
		}catch(Throwable e){
		}finally{
			try{
				rs.close();
			}catch(Throwable e){
			}
		}
		
		
		try{
			rs=tn.getFK();
			/*
			 * Retrieves a description of the primary key columns that 
			 * are referenced by a table's foreign key columns (the primary keys imported by a table). 
			 * They are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ. 
			 */
			String previousFKName=null;
			Fk previousFkRow=null;
			while(rs.next()==true){
				String fkName=rs.getString("FK_NAME"); //$NON-NLS-1$
				rs.getString("FKTABLE_SCHEM");
				String fkColumnName=rs.getString("FKCOLUMN_NAME"); //$NON-NLS-1$
				short order=rs.getShort("KEY_SEQ"); //$NON-NLS-1$
	
				short updateRule=rs.getShort("UPDATE_RULE");//$NON-NLS-1$
				String updateRuleDes="";//$NON-NLS-1$
				switch(updateRule){
					case DatabaseMetaData.importedKeyNoAction:
					case DatabaseMetaData.importedKeyRestrict:
						updateRuleDes="do not allow update of primary key if it has been imported ";//$NON-NLS-1$
					break;
					case DatabaseMetaData.importedKeyCascade:
						updateRuleDes="change imported key to agree with primary key update ";//$NON-NLS-1$
					break;
					case DatabaseMetaData.importedKeySetNull:
						updateRuleDes="change imported key to NULL if its primary key has been updated ";//$NON-NLS-1$
					break;
					case DatabaseMetaData.importedKeySetDefault:
						updateRuleDes="change imported key to default values if its primary key has been updated ";//$NON-NLS-1$
					break;
				}
				
				short deleteRule=rs.getShort("DELETE_RULE");//$NON-NLS-1$
				String deleteRuleDes="";//$NON-NLS-1$
				switch(deleteRule){
					case DatabaseMetaData.importedKeyNoAction:
					case DatabaseMetaData.importedKeyRestrict:
						deleteRuleDes="do not allow delete of primary key if it has been imported";//$NON-NLS-1$
						break;
					case DatabaseMetaData.importedKeyCascade:
						deleteRuleDes="delete rows that import a deleted key ";//$NON-NLS-1$
					break;
					case DatabaseMetaData.importedKeySetNull:
						deleteRuleDes="change imported key to NULL if its primary key has been deleted ";//$NON-NLS-1$
					break;
					case DatabaseMetaData.importedKeySetDefault:
						deleteRuleDes="change imported key to default if its primary key has been deleted ";//$NON-NLS-1$
						break;
				}
				
				String pkTableCatalog=rs.getString("PKTABLE_CAT"); //$NON-NLS-1$
				String pkName=rs.getString("PK_NAME"); //$NON-NLS-1$
				String pkTableSchema=rs.getString("PKTABLE_SCHEM"); //$NON-NLS-1$
				String tabName=rs.getString("PKTABLE_NAME"); //$NON-NLS-1$
				String fkTable=rs.getString("FKTABLE_NAME"); //$NON-NLS-1$
				String fkTableSchema=rs.getString("FKTABLE_SCHEM");//$NON-NLS-1$
				String fkTableCatalog=rs.getString("FKTABLE_CAT");//$NON-NLS-1$

				//if (!pkTableSchema.equals(fkTableSchem)) {
				//	tabName=pkTableSchema+"."+tabName; //$NON-NLS-1$
				//}
				String pkColumnName=rs.getString("PKCOLUMN_NAME"); //$NON-NLS-1$
				 
	
				if((fkName!=null)&&(!fkName.equals(previousFKName))){
					previousFkRow=new Fk(fkName,tabName, pkName,pkTableSchema,pkTableCatalog,updateRuleDes,deleteRuleDes,fkTable,fkTableSchema,fkTableCatalog);
					previousFKName=fkName;
					fkList.add(previousFkRow);
				}
				if(fkName!=null){
					FkDetailSub subRow=new FkDetailSub(previousFkRow,fkColumnName,order,pkColumnName);
					previousFkRow.addChild(subRow);
				}
			}
		}catch(Throwable e){	
		}finally{
			try{
				rs.close();
			}catch(Throwable e){
			}
		}
		
		try{
			rs=tn.getIndexes();	
			String previousIndexName=null;
			Index previousIndexRow=null;
		
			while(rs.next()==true){
				boolean nonUnique=rs.getBoolean("NON_UNIQUE"); //$NON-NLS-1$
				String str=rs.getString("INDEX_NAME"); //$NON-NLS-1$
				short type=rs.getShort("TYPE"); //$NON-NLS-1$
				short order=rs.getShort("ORDINAL_POSITION");  //$NON-NLS-1$
				String name=rs.getString("COLUMN_NAME"); //$NON-NLS-1$
				String asc_or_des=rs.getString("ASC_OR_DESC");  //$NON-NLS-1$
				if((str!=null)&&(!str.equals(previousIndexName))){
					previousIndexName=str;
					previousIndexRow=new Index(str,nonUnique,type);
					indexList.add(previousIndexRow);
				}
				if(str!=null){
					IndexDetailSub subRow=new IndexDetailSub(previousIndexRow,name,asc_or_des,order);
					previousIndexRow.addChild(subRow);
				}
			}
			
		}catch(Throwable e){
		}
		finally{
			try{
				rs.close();
			}catch(Throwable e){
			}
		}
			
		
	}
	public Table adapt(){
		//Table table=new Table(tableInfo.getQualifiedName());
		Table table=new Table(tableInfo.getSimpleName());
		table.setQualifiedName(tableInfo.getQualifiedName());
		table.setColumns(this.columnList);
		table.setPk(pk);
		table.setFkList(this.fkList);
		table.setCatalogName(tableInfo.getCatalogName());
		table.setSchemaName(tableInfo.getSchemaName());
		return table;
	}

}