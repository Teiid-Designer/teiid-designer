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

package com.metamatrix.modeler.jdbc.relational;

import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.relational.impl.DefaultCostAnalyzerImpl;
import com.metamatrix.modeler.jdbc.relational.impl.OracleCostAnalyzerImpl;

/** 
 * @since 4.3
 */
public class CostAnalyzerFactory {

    private static CostAnalyzerFactory costAnalyzerFactory = null;
    
    private static class DBMS {
        public static final String ORACLE = "oracle"; //$NON-NLS-1$
        public static final String DB2 = "db2"; //$NON-NLS-1$
        public static final String SQL_SERVER = "sqlserver"; //$NON-NLS-1$
    }
    
    
    private CostAnalyzerFactory() {        
    }
    
    public static CostAnalyzerFactory getCostAnalyzerFactory() {
        if (CostAnalyzerFactory.costAnalyzerFactory == null) {
            CostAnalyzerFactory.costAnalyzerFactory = new CostAnalyzerFactory();
        }
        return CostAnalyzerFactory.costAnalyzerFactory;
    }
    
    public CostAnalyzer getCostAnalyzer(JdbcSource src, String password) {
        String dbmsType = getDbmsType(src.getDriverName());
        if (DBMS.ORACLE.equals(dbmsType)) {
            return new OracleCostAnalyzerImpl(src, password);
        }
        return new DefaultCostAnalyzerImpl(src, password);
    }
    
    private String getDbmsType(String driverName) {
        driverName = driverName.toLowerCase();
        if (driverName.indexOf(DBMS.ORACLE) > -1) {
            return DBMS.ORACLE;
        }
        if (driverName.indexOf(DBMS.DB2) > -1) {
            return DBMS.DB2;
        }
        if (driverName.indexOf(DBMS.SQL_SERVER) > -1) {
            return DBMS.SQL_SERVER;
        }
        return ""; //$NON-NLS-1$
    }
}
