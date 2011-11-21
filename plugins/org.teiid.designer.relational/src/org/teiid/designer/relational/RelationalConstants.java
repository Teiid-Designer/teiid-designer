/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational;

/**
 * 
 */
public interface RelationalConstants {
    final static String XMI_EXT = ".xmi"; //$NON-NLS-1$
    
    class TYPES {
        public static final int UNDEFINED = -1;
        public static final int MODEL = 0;
        public static final int SCHEMA = 1;
        public static final int CATELOG = 2;
        public static final int TABLE = 3;
        public static final int VIEW = 4;
        public static final int PROCEDURE = 5;
        public static final int PARAMETER = 6;
        public static final int COLUMN = 7;
        public static final int PK = 8;
        public static final int FK = 9;
        public static final int UC = 10;
        public static final int AP = 11;
        public static final int RESULT_SET = 12;
        public static final int INDEX = 13;
    }
    
    final static String[] TYPE_NAMES = {
    	"Model", //$NON-NLS-1$
    	"Schema", //$NON-NLS-1$
    	"Catalog", //$NON-NLS-1$
    	"Table", //$NON-NLS-1$
    	"View", //$NON-NLS-1$
    	"Procedure", //$NON-NLS-1$
    	"Parameter", //$NON-NLS-1$
    	"Column", //$NON-NLS-1$
    	"Primary Key", //$NON-NLS-1$
    	"Foreign Key", //$NON-NLS-1$
    	"Unique Constraint", //$NON-NLS-1$
    	"Result Set", //$NON-NLS-1$
    	"Index" //$NON-NLS-1$
    };
    
    class TYPES_LITERAL {
        public static final String UNDEFINED = "UNDEFINED"; //$NON-NLS-1$
        public static final String MODEL = "MODEL"; //$NON-NLS-1$
        public static final String SCHEMA = "SCHEMA"; //$NON-NLS-1$
        public static final String CATELOG = "CATALOG"; //$NON-NLS-1$
        public static final String TABLE = "TABLE"; //$NON-NLS-1$
        public static final String VIEW = "VIEW"; //$NON-NLS-1$
        public static final String PROCEDURE = "PROCEDURE"; //$NON-NLS-1$
        public static final String PARAMETER = "PARAMETER"; //$NON-NLS-1$
        public static final String COLUMN = "COLUMN"; //$NON-NLS-1$
        public static final String PRIMARYKEY = "PRIMARY-KEY"; //$NON-NLS-1$
        public static final String FOREIGNKEY = "FOREIGN-KEY"; //$NON-NLS-1$
        public static final String UNIQUECONSTRAINT = "UNIQUE-CONSTRAINT"; //$NON-NLS-1$
        public static final String ACCESSPATTERN = "ACCESS-PATTERN"; //$NON-NLS-1$
        public static final String RESULTSET = "RESULT-SET"; //$NON-NLS-1$
        public static final String INDEX = "INDEX"; //$NON-NLS-1$
    }
    
    // ALL_EXCEPT_LIKE, LIKE_ONLY, SEARCHABLE, UNSEARCHABLE

    class SEARCHABILITY {
        public static final String ALL_EXCEPT_LIKE = "ALL_EXCEPT_LIKE"; //$NON-NLS-1$
        public static final String LIKE_ONLY = "LIKE_ONLY"; //$NON-NLS-1$
        public static final String SEARCHABLE = "SEARCHABLE"; //$NON-NLS-1$
        public static final String UNSEARCHABLE = "UNSEARCHABLE"; //$NON-NLS-1$
    }
    
    // NO_NULLS, NULLABLE, NULLABLE_UNKNOWN
    class NULLABLE {
        public static final String NO_NULLS = "NO_NULLS"; //$NON-NLS-1$
        public static final String NULLABLE = "NULLABLE"; //$NON-NLS-1$
        public static final String NULLABLE_UNKNOWN = "NULLABLE_UNKNOWN"; //$NON-NLS-1$
    }
    
    // MANY, ONE, UNSPECIFICIED, ZERO_TO_MANY, ZERO_TO_ONE
    class MULTIPLICITY {
        public static final String MANY = "MANY"; //$NON-NLS-1$
        public static final String ONE = "ONE"; //$NON-NLS-1$
        public static final String UNSPECIFICIED = "UNSPECIFICIED"; //$NON-NLS-1$
        public static final String ZERO_TO_MANY = "ZERO_TO_MANY"; //$NON-NLS-1$
        public static final String ZERO_TO_ONE = "ZERO_TO_ONE"; //$NON-NLS-1$
        public static final String[] AS_ARRAY = { MANY, ONE, UNSPECIFICIED, ZERO_TO_MANY, ZERO_TO_ONE };
    }
    
    // AUTO, MULTIPLE, ONE, ZERO
    class UPDATE_COUNT {
        public static final String AUTO = "AUTO"; //$NON-NLS-1$
        public static final String MULTIPLE = "MULTIPLE"; //$NON-NLS-1$
        public static final String ONE = "ONE"; //$NON-NLS-1$
        public static final String ZERO = "ZERO"; //$NON-NLS-1$
    }
    
    // IN, IN_OUT, OUT, RETURN, UNKNOWN
    class DIRECTION {
        public static final String IN = "IN"; //$NON-NLS-1$
        public static final String IN_OUT = "IN_OUT"; //$NON-NLS-1$
        public static final String OUT = "OUT"; //$NON-NLS-1$
        public static final String RETURN = "RETURN"; //$NON-NLS-1$
        public static final String UNKNOWN = "UNKNOWN"; //$NON-NLS-1$
    }

}
