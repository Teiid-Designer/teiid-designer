/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.custom;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.JdbcPlugin;


/** 
 * @since 4.3
 */
public class ExcelConnectionHandler implements
                                   InvocationHandler {

    private Connection connection;
    private File excelFile;

    public ExcelConnectionHandler(Connection connection, String url) {
        ArgCheck.isNotNull(connection);
        ArgCheck.isNotNull(url);
        this.connection = connection;
        String fileName = getFilePath(url);
        excelFile = new File(fileName);
        if(!excelFile.exists()) {
            throw new IllegalArgumentException(JdbcPlugin.Util.getString("ExcelConnecton.FileNotFound", fileName)); //$NON-NLS-1$
        }
    }
    
    /** 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     * @since 4.3
     */
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {
        String methodName = method.getName();

        if(methodName.equals("getMetaData")) {//$NON-NLS-1$
            return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { DatabaseMetaData.class }, new ExcelDatabaseMetaDataHandler(this.connection.getMetaData(), excelFile));
        }
        
        return method.invoke(this.connection, args);
    }

    protected static String getFilePath(String url) {
        String upperCaseUrl = url.toUpperCase();
        int startIndex = upperCaseUrl.indexOf("DBQ");//$NON-NLS-1$
        if(startIndex == -1) {
            throw new IllegalArgumentException(JdbcPlugin.Util.getString("ExcelConnecton.invalidUrl", url)); //$NON-NLS-1$
        }
        startIndex = url.indexOf("=", startIndex);//$NON-NLS-1$
        if(startIndex == -1) {
            throw new IllegalArgumentException(JdbcPlugin.Util.getString("ExcelConnecton.invalidUrl", url)); //$NON-NLS-1$
        }
        startIndex++;
        int endIndex = url.indexOf(";", startIndex);//$NON-NLS-1$
        if(endIndex == -1) {
            endIndex = url.indexOf("\"", startIndex);//$NON-NLS-1$
        }
        if(endIndex == -1) {
            return url.substring(startIndex);
        }
        return url.substring(startIndex, endIndex);
    }
}
