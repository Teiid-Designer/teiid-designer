/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.custom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.List;


/** 
 * @since 4.3
 */
public class ExcelResultSetMetaDataHandler implements
                                          InvocationHandler {

    private List columnNames;
    
    protected ExcelResultSetMetaDataHandler(List columnNames) {
        this.columnNames = columnNames;
    }
    
    /** 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     * @since 4.3
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {
        String methodName = method.getName();
        if(methodName.equals("getColumnCount")) {//$NON-NLS-1$
            return new Integer(columnNames.size());
        }
        if(methodName.equals("getColumnDisplaySize")) {//$NON-NLS-1$
            return new Integer(Integer.MAX_VALUE);
        }
        if(methodName.equals("isNullable")) {//$NON-NLS-1$
            return new Integer(ResultSetMetaData.columnNullable);
        }
        if(methodName.equals("getPrecision")) {//$NON-NLS-1$
            return new Integer(0);
        }
        if(methodName.equals("getScale")) {//$NON-NLS-1$
            return new Integer(0);
        }
        if(methodName.equals("getColumnType")) {//$NON-NLS-1$
            return new Integer(Types.VARCHAR);
        }
        if(methodName.equals("isAutoIncrement")) {//$NON-NLS-1$
            return Boolean.TRUE;
        }
        if(methodName.equals("isCaseSensitive")) {//$NON-NLS-1$
            return Boolean.TRUE;
        }
        if(methodName.equals("isCurrency")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("isDefinitelyWritable")) {//$NON-NLS-1$
            return Boolean.TRUE;
        }
        if(methodName.equals("isReadOnly")) {//$NON-NLS-1$
            return Boolean.TRUE;
        }
        if(methodName.equals("isSearchable")) {//$NON-NLS-1$
            return Boolean.TRUE;
        }
        if(methodName.equals("isSigned")) {//$NON-NLS-1$
            return Boolean.FALSE;
        }
        if(methodName.equals("isWritable")) {//$NON-NLS-1$
            return Boolean.TRUE;
        }
        if(methodName.equals("getColumnName") || methodName.equals("getColumnLabel")) {//$NON-NLS-1$ //$NON-NLS-2$
            int columnIndex = ((Integer)args[0]).intValue() -1;
            if(columnIndex >=0 && columnNames.size() > columnIndex) {         
                return columnNames.get(columnIndex);
            }
        }

        return null;
    }

}
