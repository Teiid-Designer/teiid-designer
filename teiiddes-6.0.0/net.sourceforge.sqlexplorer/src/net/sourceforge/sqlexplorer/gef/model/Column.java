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


public class Column implements Serializable{
	static final long serialVersionUID = 1;
	public String columnName;
	public String typeName;
	public String columnSize;
	public String decimalDigits;
	public String typeNameExtended;

		public Column(Object[] obj) {
			
			columnName=obj[3].toString();
			typeName = obj[5].toString();
			columnSize=obj[6]!=null?obj[6].toString():"0";//new Integer(set.getInt("COLUMN_SIZE")); //$NON-NLS-1$
					//el[3]=new Integer(obj[8].toString());//new Integer(set.getInt("DECIMAL_DIGITS")); //$NON-NLS-1$
			decimalDigits=obj[8]!=null?obj[8].toString():"0";//new Integer(set.getInt("DECIMAL_DIGITS")); //$NON-NLS-1$
			String sLower = typeName.toLowerCase();
			StringBuffer buf=new StringBuffer(100);
			buf.append(typeName);
			if(sLower.indexOf("char") != -1||sLower.indexOf("int") != -1){ //$NON-NLS-1$ 
				buf.append("("); //$NON-NLS-1$
				buf.append(columnSize);
				buf.append(")"); //$NON-NLS-1$
			}else if(sLower.equals("numeric")|| sLower.equals("number")|| sLower.equals("decimal")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				buf.append("("); //$NON-NLS-1$
				buf.append(columnSize);
				String tmp = decimalDigits;
				if(tmp != null && !"".equals(tmp) &&!"0".equals(tmp)) //$NON-NLS-1$ //$NON-NLS-2$
				{
					buf.append(","); //$NON-NLS-1$
					buf.append(tmp);
				}
				buf.append(")"); //$NON-NLS-1$
			}
			typeNameExtended=buf.toString();
		}

	}