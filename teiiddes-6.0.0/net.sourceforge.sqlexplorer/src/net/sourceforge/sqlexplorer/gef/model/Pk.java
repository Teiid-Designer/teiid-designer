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
import java.util.ArrayList;

public class Pk implements Serializable{
	static final long serialVersionUID = 1;
	public String pkName;
	ArrayList ls=new ArrayList();
	/**
	 * @param pkName
	 */
	public Pk(String pkName) {
		this.pkName=pkName;
	}

	/**
	 * @param subRow
	 */
	public void addChild(PkDetailSub subRow) {
		ls.add(subRow);
	}
	public boolean containsColumn(String columnName){
		for(int i=0;i<ls.size();i++){
			PkDetailSub sb=(PkDetailSub)ls.get(i);
			if(sb.getColumnName().equals(columnName))
				return true;
		}
		return false;
	}

}