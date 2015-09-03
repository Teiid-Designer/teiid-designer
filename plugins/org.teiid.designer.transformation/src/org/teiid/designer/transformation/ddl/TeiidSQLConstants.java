/*************************************************************************************
 * JBoss, Home of Professional Open Source.
* See the COPYRIGHT.txt file distributed with this work for information
* regarding copyright ownership. Some portions may be licensed
* to Red Hat, Inc. under one or more contributor license agreements.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
* 02110-1301 USA.
 ************************************************************************************/
package org.teiid.designer.transformation.ddl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.teiid.core.designer.util.StringConstants;


/**
 *
 */
@SuppressWarnings({"javadoc", "nls"})
public abstract class TeiidSQLConstants {

    public interface Tokens extends StringConstants {
        String ALL_COLS = STAR;
        String NEWLINE = NEW_LINE;
        String EQ = EQUALS;
        String NE = "<>";
        String NE2 = "!=";
        String LT = "<";
        String GT = ">";
        String LE = "<=";
        String GE = ">=";
        String LPAREN = OPEN_BRACKET;
        String RPAREN = CLOSE_BRACKET;
        String LBRACE = OPEN_BRACE;
        String RBRACE = CLOSE_BRACE;
        String TICK = "'";
        String LOGICAL_OR = "||";
        String LOGICAL_AND = "&&";
        String DOLLAR = "$";
        String ID_ESCAPE_CHAR = SPEECH_MARK;
    }

    public interface NonReserved {
        String SQL_TSI_FRAC_SECOND = "SQL_TSI_FRAC_SECOND";
        String SQL_TSI_SECOND = "SQL_TSI_SECOND";
        String SQL_TSI_MINUTE = "SQL_TSI_MINUTE";
        String SQL_TSI_HOUR = "SQL_TSI_HOUR";
        String SQL_TSI_DAY = "SQL_TSI_DAY";
        String SQL_TSI_WEEK = "SQL_TSI_WEEK";
        String SQL_TSI_MONTH = "SQL_TSI_MONTH";
        String SQL_TSI_QUARTER = "SQL_TSI_QUARTER";
        String SQL_TSI_YEAR = "SQL_TSI_YEAR";
        String TIMESTAMPADD = "TIMESTAMPADD";
        String TIMESTAMPDIFF = "TIMESTAMPDIFF";
        //aggregate functions
        String MAX = "MAX";
        String MIN = "MIN";
        String COUNT = "COUNT";
        String ROW_NUMBER = "ROW_NUMBER";
        String RANK = "RANK";
        String DENSE_RANK = "DENSE_RANK";
        String AVG = "AVG";
        String SUM = "SUM";
        //texttable
        String WIDTH = "WIDTH";
        String DELIMITER = "DELIMITER";
        String HEADER = "HEADER";
        String QUOTE = "QUOTE";
        String COLUMNS = "COLUMNS";
        String SELECTOR = "SELECTOR";
        String SKIP = "SKIP";
        //xmltable
        String ORDINALITY = "ORDINALITY";
        String PASSING = "PASSING";
        String NAME = "NAME";
        String PATH = "PATH";
        //xmlserialize
        String DOCUMENT = "DOCUMENT";
        String CONTENT = "CONTENT";
        //xmlquery
        String RETURNING = "RETURNING";
        String SEQUENCE = "SEQUENCE";
        String EMPTY = "EMPTY";
        //querystring function
        String QUERYSTRING = "QUERYSTRING";
        String NAMESPACE = "NAMESPACE";
        //xmlparse
        String WELLFORMED = "WELLFORMED";
        //agg
        String EVERY = "EVERY";
        String STDDEV_POP = "STDDEV_POP";
        String STDDEV_SAMP = "STDDEV_SAMP";
        String VAR_SAMP = "VAR_SAMP";
        String VAR_POP = "VAR_POP";
        
        String NULLS = "NULLS";
        String FIRST = "FIRST";
        String LAST = "LAST";
        String NEXT = "NEXT";
        String SUBSTRING = "SUBSTRING";
        String EXTRACT = "EXTRACT";
        String TO_CHARS = "TO_CHARS";
        String TO_BYTES = "TO_BYTES";
        
        String KEY = "KEY";
        
        String SERIAL = "SERIAL";
        
        String ENCODING = "ENCODING";
        String TEXTAGG = "TEXTAGG";
        
        String ARRAYTABLE = "ARRAYTABLE";
        
        String VIEW = "VIEW";
        String INSTEAD = "INSTEAD";
        String ENABLED = "ENABLED";
        String DISABLED = "DISABLED";
        
        String TRIM = "TRIM";
        String RESULT = "RESULT";
        String OBJECTTABLE = "OBJECTTABLE";
        String TEXTTABLE = "TEXTTABLE";
        String VERSION = "VERSION";
        String INCLUDING = "INCLUDING";
        String EXCLUDING = "EXCLUDING";
        String XMLDECLARATION = "XMLDECLARATION";
        String VARIADIC = "VARIADIC";
        String INDEX = "INDEX";
        String ACCESSPATTERN = "ACCESSPATTERN";
        String EXCEPTION = "EXCEPTION";
        String RAISE = "RAISE";
        String CHAIN = "CHAIN";
        String JSONARRAY_AGG = "JSONARRAY_AGG";
        String JSONOBJECT = "JSONOBJECT";
        String AUTO_INCREMENT = "AUTO_INCREMENT";

        // SubqueryHints
        String DJ = "DJ";
        String MJ = "MJ";

        // Limit
        String NON_STRICT = "NON_STRICT";

        // Data Types
        String STRING = "STRING";
        String VARBINARY = "VARBINARY";
        String VARCHAR = "VARCHAR";
        String BOOLEAN = "BOOLEAN";
        String BYTE = "BYTE";
        String TINYINT = "TINYINT";
        String SHORT = "SHORT";
        String SMALLINT = "SMALLINT";
        String CHAR = "CHAR";
        String INTEGER = "INTEGER";
        String LONG = "LONG";
        String BIGINT = "BIGINT";
        String BIGINTEGER = "BIGINTEGER";
        String FLOAT = "FLOAT";
        String REAL = "REAL";
        String DOUBLE = "DOUBLE";
        String BIGDECIMAL = "BIGDECIMAL";
        String DECIMAL = "DECIMAL";
        String DATE = "DATE";
        String TIME = "TIME";
        String TIMESTAMP = "TIMESTAMP";
        String OBJECT = "OBJECT";
        String BLOB = "BLOB";
        String CLOB = "CLOB";
        String XML = "XML";
    }
    
    public interface Reserved {
        //Teiid specific
        String BIGDECIMAL = "BIGDECIMAL";
        String BIGINTEGER = "BIGINTEGER";
        String BREAK = "BREAK";
        String BYTE = "BYTE";
        String CRITERIA = "CRITERIA";
        String ERROR = "ERROR"; 
        String LIMIT = "LIMIT";
        String LONG = "LONG";
        String LOOP = "LOOP";
        String MAKEDEP = "MAKEDEP";
        String MAKEIND = "MAKEIND";
        String MAKENOTDEP = "MAKENOTDEP";
        String NOCACHE = "NOCACHE";
        String NOUNNEST = "NO_UNNEST";
        String OPTIONAL = "OPTIONAL";
        String PRESERVE = "PRESERVE";
        String STRING = "STRING";
        String VIRTUAL = "VIRTUAL";
        String WHILE = "WHILE";
        
        //SQL2003 keywords
        String ADD = "ADD";
        String ANY = "ANY";
        String ALL = "ALL";
        String ALLOCATE = "ALLOCATE";
        String ALTER = "ALTER";
        String AND = "AND";
        String ARE = "ARE";
        String ARRAY = "ARRAY";
        String AS = "AS";
        String ASC = "ASC";
        String ASENSITIVE = "ASENSITIVE";
        String ASYMETRIC = "ASYMETRIC";
        String ATOMIC = "ATOMIC";
        String AUTHORIZATION = "AUTHORIZATION";
        String BEGIN = "BEGIN";
        String BETWEEN = "BETWEEN";
        String BIGINT = "BIGINT";
        String BINARY = "BINARY";
        String BLOB = "BLOB";
        String BOTH = "BOTH";
        String BY = "BY";
        String CALL = "CALL";
        String CALLED = "CALLED";
        String CASE = "CASE";
        String CAST = "CAST";
        String CASCADED = "CASCADED";
        String CHAR = "CHAR";
        String CHARACTER = "CHARACTER";
        String CHECK = "CHECK";
        String CLOB = "CLOB";
        String CLOSE = "CLOSE";
        String COLLATE = "COLLATE";
        String COLUMN = "COLUMN";
        String COMMIT = "COMMIT";
        String CONNECT = "CONNECT";
        String CONVERT = "CONVERT";
        String CONSTRAINT = "CONSTRAINT";
        String CONTINUE = "CONTINUE";
        String CORRESPONDING = "CORRESPONDING";
        String CREATE = "CREATE";
        String CROSS = "CROSS";
        String CURRENT_DATE = "CURRENT_DATE";
        String CURRENT_TIME = "CURRENT_TIME";
        String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
        String CURRENT_USER = "CURRENT_USER";
        String CURSOR = "CURSOR";
        String CYCLE = "CYCLE";
        String DATE = "DATE";
        String DAY = "DAY";
        String DEALLOCATE = "DEALLOCATE";
        String DEC = "DEC";
        String DECIMAL = "DECIMAL";
        String DECLARE = "DECLARE";    
        String DEFAULT = "DEFAULT";
        String DELETE = "DELETE";
        String DEREF = "DEREF";
        String DESC = "DESC";
        String DESCRIBE = "DESCRIBE";
        String DETERMINISTIC = "DETERMINISTIC";
        String DISCONNECT = "DISCONNECT";
        String DISTINCT = "DISTINCT";
        String DOUBLE = "DOUBLE";
        String DROP = "DROP";
        String DYNAMIC = "DYNAMIC";
        String EACH = "EACH";
        String ELEMENT = "ELEMENT";
        String ELSE = "ELSE";   
        String END = "END";
        String ESCAPE = "ESCAPE";
        String EXCEPT = "EXCEPT";
        String EXEC = "EXEC";
        String EXECUTE = "EXECUTE";
        String EXISTS = "EXISTS";
        String EXTERNAL = "EXTERNAL";
        String FALSE = "FALSE";
        String FETCH = "FETCH";
        String FILTER = "FILTER";
        String FLOAT = "FLOAT";
        String FOR = "FOR";    
        String FOREIGN = "FOREIGN";
        String FREE = "FREE";
        String FROM = "FROM";
        String FULL = "FULL";
        String FUNCTION = "FUNCTION";
        String GET = "GET";
        String GLOBAL = "GLOBAL";
        String GRANT = "GRANT";
        String GROUP = "GROUP";
        String GROUPING = "GROUPING";
        String HAS = "HAS"; 
        String HAVING = "HAVING";
        String HOLD = "HOLD";
        String HOUR = "HOUR";
        String IDENTITY = "IDENTITY";
        String INDICATOR = "INDICATOR";
        String IF = "IF";    
        String IMMEDIATE = "IMMEDIATE";
        String IN = "IN";
        String INOUT = "INOUT";
        String INNER = "INNER";
        String INPUT = "INPUT";
        String INSENSITIVE = "INSENSITIVE";
        String INSERT = "INSERT";
        String INTEGER = "INTEGER";
        String INTERSECT = "INTERSECT";
        String INTERVAL = "INTERVAL";
        String INT = "INT";
        String INTO = "INTO";
        String IS = "IS";    
        String ISOLATION = "ISOLATION";
        String JOIN = "JOIN";
        String LANGUAGE = "LANGUAGE";
        String LARGE = "LARGE";
        String LATERAL = "LATERAL";
        String LEADING = "LEADING";
        String LEAVE = "LEAVE";
        String LEFT = "LEFT";
        String LIKE = "LIKE";
        String LIKE_REGEX = "LIKE_REGEX";
        String LOCAL = "LOCAL";
        String LOCALTIME = "LOCALTIME";
        String LOCALTIMESTAMP = "LOCALTIMESTAMP";
        String MATCH = "MATCH";
        String MEMBER = "MEMBER";
        String MERGE = "MERGE";
        String METHOD = "METHOD";
        String MINUTE = "MINUTE";
        String MODIFIES = "MODIFIES";
        String MODULE = "MODULE";
        String MONTH = "MONTH";
        String MULTISET = "MULTISET";
        String NATIONAL = "NATIONAL";
        String NATURAL = "NATURAL";
        String NCHAR = "NCHAR";
        String NCLOB = "NCLOB";
        String NEW = "NEW";
        String NO = "NO";
        String NONE = "NONE";
        String NOT = "NOT";
        String NULL = "NULL";
        String NUMERIC = "NUMERIC";
        String OBJECT = "OBJECT";
        String OF = "OF";
        String OFFSET = "OFFSET";
        String OLD = "OLD";
        String ON = "ON";
        String ONLY = "ONLY";
        String OPEN = "OPEN";
        String OR = "OR";
        String ORDER = "ORDER";
        String OUT = "OUT";
        String OUTER = "OUTER";
        String OUTPUT = "OUTPUT";
        String OPTION = "OPTION";
        String OPTIONS = "OPTIONS";
        String OVER = "OVER";
        String OVERLAPS = "OVERLAPS";
        String PARAMETER = "PARAMETER";
        String PARTITION = "PARTITION";
        String PRECISION = "PRECISION";
        String PREPARE = "PREPARE";
        String PRIMARY = "PRIMARY";
        String PROCEDURE = "PROCEDURE";
        String RANGE = "RANGE";
        String READS = "READS";
        String REAL = "REAL";
        String RECURSIVE = "RECURSIVE";
        String REFERENCES = "REFERENCES";
        String REFERENCING = "REFERENCING";
        String RELEASE = "RELEASE";
        String RETURN = "RETURN";
        String RETURNS = "RETURNS";
        String REVOKE = "REVOKE";
        String RIGHT = "RIGHT";
        String ROLLBACK = "ROLLBACK";
        String ROLLUP = "ROLLUP";
        String ROW = "ROW";
        String ROWS = "ROWS";
        String SAVEPOINT = "SAVEPOINT";
        String SCROLL = "SCROLL";
        String SEARCH = "SEARCH";
        String SECOND = "SECOND";
        String SELECT = "SELECT";
        String SENSITIVE = "SENSITIVE";
        String SESSION_USER = "SESSION_USER";
        String SET = "SET";
        String SHORT = "SHORT";
        String SIMILAR = "SIMILAR";
        String SMALLINT = "SMALLINT";
        String SOME = "SOME";
        String SPECIFIC = "SPECIFIC";
        String SPECIFICTYPE = "SPECIFICTYPE";
        String SQL = "SQL";
        String SQLEXCEPTION = "SQLEXCEPTION";
        String SQLSTATE = "SQLSTATE";
        String SQLWARNING = "SQLWARNING";
        String SUBMULTILIST = "SUBMULTILIST";
        String START = "START";
        String STATIC = "STATIC";
        String SYMETRIC = "SYMETRIC";
        String SYSTEM = "SYSTEM";
        String SYSTEM_USER = "SYSTEM_USER";
        String TABLE = "TABLE";
        String TEMPORARY = "TEMPORARY";
        String THEN = "THEN";
        String TIME = "TIME";
        String TIMESTAMP = "TIMESTAMP";
        String TIMEZONE_HOUR = "TIMEZONE_HOUR";
        String TIMEZONE_MINUTE = "TIMEZONE_MINUTE";
        String TO = "TO";
        String TREAT = "TREAT";
        String TRAILING = "TRAILING";
        String TRANSLATE = "TRANSLATE"; 
        String TRANSLATION = "TRANSLATION"; 
        String TRIGGER = "TRIGGER";
        String TRUE = "TRUE";
        String UNION = "UNION";
        String UNIQUE = "UNIQUE";
        String UNKNOWN = "UNKNOWN";
        String UPDATE = "UPDATE";
        String USER = "USER";
        String USING = "USING"; 
        String VALUE = "VALUE";
        String VALUES = "VALUES";
        String VARCHAR = "VARCHAR";
        String VARYING = "VARYING";
        String WHEN = "WHEN";    
        String WHENEVER = "WHENEVER";    
        String WHERE = "WHERE";
        String WINDOW = "WINDOW";
        String WITH = "WITH";    
        String WITHIN = "WITHIN";
        String WITHOUT = "WITHOUT";
        String YEAR = "YEAR";
        
        // SQL 2008 words
        String ARRAY_AGG= "ARRAY_AGG";

        //SQL/XML        
        String XML = "XML";
        String XMLAGG = "XMLAGG";
        String XMLATTRIBUTES = "XMLATTRIBUTES";
        String XMLBINARY = "XMLBINARY";
        String XMLCAST = "XMLCAST";
        String XMLCOMMENT = "XMLCOMMENT";
        String XMLCONCAT = "XMLCONCAT";
        String XMLDOCUMENT = "XMLDOCUMENT";
        String XMLELEMENT = "XMLELEMENT";
        String XMLEXISTS = "XMLEXISTS";
        String XMLFOREST = "XMLFOREST";
        String XMLITERATE = "XMLITERATE";
        String XMLNAMESPACES = "XMLNAMESPACES";
        String XMLPARSE = "XMLPARSE";
        String XMLPI = "XMLPI";
        String XMLQUERY = "XMLQUERY";
        String XMLSERIALIZE = "XMLSERIALIZE";
        String XMLTABLE = "XMLTABLE";
        String XMLTEXT = "XMLTEXT";
        String XMLVALIDATE = "XMLVALIDATE";
        
        //SQL/MED
        
        String DATALINK = "DATALINK";
        String DLNEWCOPY = "DLNEWCOPY";
        String DLPREVIOUSCOPY = "DLPREVIOUSCOPY";
        String DLURLCOMPLETE = "DLURLCOMPLETE";
        String DLURLCOMPLETEWRITE = "DLURELCOMPLETEWRITE";
        String DLURLCOMPLETEONLY = "DLURLCOMPLETEONLY";
        String DLURLPATH = "DLURLPATH";
        String DLURLPATHWRITE = "DLURLPATHWRITE";
        String DLURLPATHONLY = "DLURLPATHONLY";
        String DLURLSCHEME = "DLURLSCHEME";
        String DLURLSERVER = "DLURLSEVER";
        String DLVALUE = "DLVALUE";
        String IMPORT = "IMPORT";

        String ANTI_SEMI = "ANTI SEMI";
        String SEMI = "SEMI";

        String NO_DEFAULT = "NO DEFAULT";

        String VARBINARY = "VARBINARY";
        String BOOLEAN = "BOOLEAN";
        String TINYINT = "TINYINT";
    }
    
    /**
     * Set of CAPITALIZED reserved words for checking whether a string is a reserved word.
     */
    private static Set<String> RESERVED_WORDS = null;
    private static Set<String> NON_RESERVED_WORDS = null;

    /**
     * @throws AssertionError
     */
    private static Set<String> extractFieldNames(Class<?> clazz) throws AssertionError {
        HashSet<String> result = new HashSet<String>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() != String.class)
                continue;

            try {
                if (!result.add((String)field.get(null))) {
                    throw new AssertionError("Duplicate value for " + field.getName());
                }
            } catch (Exception e) {
                // Exception should not be thrown
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * @param teiidVersion
     */
    private static void initialiseConstants() {
        RESERVED_WORDS = extractFieldNames(TeiidSQLConstants.Reserved.class);
        NON_RESERVED_WORDS = extractFieldNames(TeiidSQLConstants.NonReserved.class);
    }

    /**
     * @return nonReservedWords
     */
    public static Set<String> getNonReservedWords() {
        if (NON_RESERVED_WORDS == null)
            initialiseConstants();

        return NON_RESERVED_WORDS;
    }

    /**
     * @return reservedWords
     */
    public static Set<String> getReservedWords() {
            initialiseConstants();

        return RESERVED_WORDS;
    }
    
    /** Can't construct */
    private TeiidSQLConstants() {}   

    /**
     * Check whether a string is a reserved word.  
     * @param str String to check
     * @return True if reserved word, false if not or null
     */
    public static final boolean isReservedWord(String str) {
        if(str == null) { 
            return false;    
        }

        String word = str.toUpperCase();
        if (RESERVED_WORDS == null)
            initialiseConstants();

        return RESERVED_WORDS.contains(word);
    }
}
