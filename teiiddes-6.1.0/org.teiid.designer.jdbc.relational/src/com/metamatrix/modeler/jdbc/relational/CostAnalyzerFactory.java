/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
