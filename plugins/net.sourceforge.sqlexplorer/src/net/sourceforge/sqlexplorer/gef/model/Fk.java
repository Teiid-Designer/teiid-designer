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
package net.sourceforge.sqlexplorer.gef.model;

import java.io.Serializable;

public class Fk implements Serializable{
	static final long serialVersionUID = 1;
	public String fkName, tabName, pkName,pkTableSchema,pkTableCatalog;
	String updateRuleDes, deleteRuleDes;
	String fkTable;
	String fkTableSchema,fkTableCatalog;
	/**
	 * @param fkName
	 * @param tabName
	 * @param pkName
	 * @param adapter
	 */
	public Fk(String fkName, String tabName, String pkName,String pkTableSchema,String pkTableCatalog,String updateRuleDes,String deleteRuleDes,String fkTable,String fkTableSchema,String fkTableCatalog) {
		
		this.fkName=fkName;
		this.tabName=tabName;
		this.pkName=pkName;
		this.pkTableCatalog=pkTableCatalog;
		this.pkTableSchema=pkTableSchema;
		this.updateRuleDes=updateRuleDes;
		this.deleteRuleDes=deleteRuleDes;
		this.fkTable=fkTable;
		this.fkTableSchema=fkTableSchema;
		this.fkTableCatalog=fkTableCatalog;
	}
	/**
	 * @param subRow
	 */
	public void addChild(FkDetailSub subRow) {
		
	}
	public boolean isRelated(Pk pk,Table pkTable){
		if(!tabName.equals(pkTable.getSimpleName()))
			return false;
		if(!pkName.equalsIgnoreCase(pk.pkName))
			return false;
		if(pkTableCatalog!=null){
			String cat=pkTable.getCatalogName();
			if(cat==null)
				return false;
			if(!fkTableCatalog.equalsIgnoreCase(cat))
				return false;
		}
		if(pkTableSchema!=null){
			String schema=pkTable.getSchemaName();
			if(schema==null)
				return false;
			if(!pkTableSchema.equalsIgnoreCase(schema))
				return false;
		}
		return true;
	}
}