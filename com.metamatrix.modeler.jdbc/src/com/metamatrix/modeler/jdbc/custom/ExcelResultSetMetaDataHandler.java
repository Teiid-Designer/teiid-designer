/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
