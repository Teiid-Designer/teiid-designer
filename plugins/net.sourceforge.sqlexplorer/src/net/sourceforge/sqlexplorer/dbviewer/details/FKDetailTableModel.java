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


class FKDetailTableModel implements FKInterface{
	private ArrayList list =new ArrayList();
		
	public FKDetailTableModel(ResultSet rs,IDetailLogDisplay disp){
		
		String previousFKName=null;
		FKDetailRow previousRow=null;
		if(rs==null)
			return;
		try{
			while(rs.next()==true){
				String fkName=rs.getString("FK_NAME"); //$NON-NLS-1$
				String fkTableSchem=rs.getString("FKTABLE_SCHEM"); //$NON-NLS-1$
				String fkColumnName=rs.getString("FKCOLUMN_NAME"); //$NON-NLS-1$
				short order=rs.getShort("KEY_SEQ"); //$NON-NLS-1$
				
				String pkName=rs.getString("PK_NAME"); //$NON-NLS-1$
				String pkTableSchem=rs.getString("PKTABLE_SCHEM"); //$NON-NLS-1$
				String tabName=rs.getString("PKTABLE_NAME"); //$NON-NLS-1$
				if (!pkTableSchem.equals(fkTableSchem)) {
					tabName=pkTableSchem+"."+tabName; //$NON-NLS-1$
				}
				String pkColumnName=rs.getString("PKCOLUMN_NAME"); //$NON-NLS-1$
				
				
				if((fkName!=null)&&(!fkName.equals(previousFKName))){
					previousRow=new FKDetailRow(fkName,tabName, pkName, this);
					previousFKName=fkName;
					list.add(previousRow);
				}
				if(fkName!=null){
					FKDetailSubRow subRow=new FKDetailSubRow(previousRow,fkColumnName,order,pkColumnName);
					previousRow.addChild(subRow);
				}
			}
		}
		catch(java.lang.Exception e){
			SQLExplorerPlugin.error("Error getting fk data ",e); //$NON-NLS-1$
			disp.setMessage(e.getMessage());
		}
		finally{
			try{
				rs.close();
			}catch(java.lang.Exception e){disp.setMessage(e.getMessage());}
		}
	}
	public Object[] getElements(){
    	return list.toArray();
    }
    
    public Object getValue(Object element,int property){
    	return ((FKInterface)element).getValue(property);
    }

	public Object getParent(){
		return null;
	}
	public Object[] getChildren(){
		return list.toArray();
	}
	public Object getValue(int i){return null;}
}
