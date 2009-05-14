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

//import net.sourceforge.jfacedbc.Messages;
class ColumnDetailRow {
    // private static java.util.HashMap map=new java.util.HashMap();

    ColumnDetailRow( Object[] obj ) throws java.lang.Exception {

        el = new Object[7];
        el[0] = obj[3];//("COLUMN_NAME"); 
        el[1] = obj[5];//set.getString("TYPE_NAME");//map.get(new Integer(set.getShort("DATA_TYPE")));				 
        //el[2]=new Integer(obj[6].toString());//new Integer(set.getInt("COLUMN_SIZE")); //$NON-NLS-1$
        el[2] = obj[6];//new Integer(set.getInt("COLUMN_SIZE")); 
        //el[3]=new Integer(obj[8].toString());//new Integer(set.getInt("DECIMAL_DIGITS")); //$NON-NLS-1$
        el[3] = obj[8];//new Integer(set.getInt("DECIMAL_DIGITS")); 
        el[6] = obj[11];//set.getString("REMARKS"); 
        el[4] = obj[12];//set.getString("COLUMN_DEF"); 
        el[5] = obj[17];//set.getString("IS_NULLABLE"); 

    }

    Object[] el;

    public Object getValue( int k ) {
        return el[k];
    }
    /*private void loadMap(){
    	map.put(new Integer(java.sql.Types.ARRAY),Messages.getString("Array_6")); //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.BIGINT),Messages.getString("Big_Integer_7")); //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.BINARY),Messages.getString("Binary_8"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.BIT),Messages.getString("Bit_9"));				 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.BLOB),Messages.getString("Blob_10"));						 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.CHAR),Messages.getString("Char_11"));								 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.CLOB),Messages.getString("Clob_12"));										 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.DATE),Messages.getString("Date_13"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.DECIMAL),Messages.getString("Decimal_14"));				 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.DISTINCT),Messages.getString("Distinct_15"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.DOUBLE),Messages.getString("Double_16"));				 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.FLOAT),Messages.getString("Float_17"));						 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.INTEGER),Messages.getString("Integer_18"));								 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.JAVA_OBJECT),Messages.getString("Java_Object_19")); //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.LONGVARBINARY),Messages.getString("Long_Varbinary_20"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.LONGVARCHAR),Messages.getString("Long_Varchar_21"));				 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.NULL),Messages.getString("Null_22"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.NUMERIC),Messages.getString("Numeric_23"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.OTHER),Messages.getString("Other_24"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.REAL),Messages.getString("Real_25"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.REF),Messages.getString("Ref_26"));				 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.SMALLINT),Messages.getString("Small_Integer_27"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.STRUCT),Messages.getString("Struct_28"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.STRUCT),Messages.getString("Struct_29"));				 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.TIME),Messages.getString("Time_30"));						 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.TIMESTAMP),Messages.getString("TimeStamp_31"));								 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.TINYINT),Messages.getString("Tiny_Integer_32"));		 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.VARBINARY),Messages.getString("Varbinary_33"));								 //$NON-NLS-1$
    	map.put(new Integer(java.sql.Types.VARCHAR),Messages.getString("Varchar_34")); //$NON-NLS-1$
    	
    	
    }*/
}
