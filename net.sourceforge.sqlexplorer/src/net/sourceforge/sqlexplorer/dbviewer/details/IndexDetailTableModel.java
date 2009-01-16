package net.sourceforge.sqlexplorer.dbviewer.details;

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
import java.util.ArrayList;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

 
public class IndexDetailTableModel implements IndexInterface{
	
	private ArrayList list =new ArrayList();
	
	public IndexDetailTableModel(ResultSet rs,IndexDetail indexDetail){
		String previousColumnName=null;
		IndexDetailRow previousRow=null;
		if(rs==null)
			return;
		try{
			while(rs.next()==true){
				boolean nonUnique=rs.getBoolean("NON_UNIQUE"); //$NON-NLS-1$
				String str=rs.getString("INDEX_NAME"); //$NON-NLS-1$
				short type=rs.getShort("TYPE"); //$NON-NLS-1$
				short order=rs.getShort("ORDINAL_POSITION");  //$NON-NLS-1$
				String name=rs.getString("COLUMN_NAME"); //$NON-NLS-1$
				String asc_or_des=rs.getString("ASC_OR_DESC");  //$NON-NLS-1$
				if((str!=null)&&(!str.equals(previousColumnName))){
					previousColumnName=str;
					previousRow=new IndexDetailRow(str,nonUnique,type,this);
					list.add(previousRow);
				}
				if(str!=null){
					IndexDetailSubRow subRow=new IndexDetailSubRow(previousRow,name,asc_or_des,order);
					previousRow.addChild(subRow);
				}
			}
		}
		catch(java.lang.Exception e){
			SQLExplorerPlugin.error("Error getting pk data ",e); //$NON-NLS-1$
			indexDetail.setMessage(e.getMessage());
		}
		finally{
			try{
				rs.close();
			}catch(java.lang.Exception e){
				indexDetail.setMessage(e.getMessage());
			}
		}
	}
	public Object[] getElements(){
    	return list.toArray();
    }

    public Object getValue(Object element,int property){
    	return ((IndexInterface)element).getValue(property);
    }
    public Object getParent(){return null;}
    public Object getValue(int i){return null;}
	public Object[]getChildren(){return getElements();}
}
