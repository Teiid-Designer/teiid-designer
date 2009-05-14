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
package net.sourceforge.sqlexplorer.dbviewer.actions.editdialog;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import javax.sql.RowSet;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

public class SqlTableModel {
	ArrayList list =new ArrayList();
	public SqlTableModel(RowSet crs, ResultSetMetaData metaData) throws Exception{
		int count=metaData.getColumnCount();
		int maxRows=SQLExplorerPlugin.getDefault().getPreferenceStore().getInt(IConstants.MAX_SQL_ROWS);
		int iCount=0;
		while(crs.next()){
			iCount++;
			if(iCount>maxRows)
				break;
			ArrayList internalList=new ArrayList();
			for(int i=0;i<count;i++)
				internalList.add(crs.getString(i+1));
			list.add(internalList);
		}
	}
	public Object[] getElements(){
		return list.toArray();
	}
}
