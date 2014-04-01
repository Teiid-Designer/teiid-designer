/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.language;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *
 */
public abstract class SQLConstants {

    public interface Tokens {
        String ALL_COLS = "*"; //$NON-NLS-1$
        String SPACE = " "; //$NON-NLS-1$
        String COMMA = ","; //$NON-NLS-1$
        String NEWLINE = "\n";//$NON-NLS-1$
        String TAB = "\t"; //$NON-NLS-1$
        String DOT = "."; //$NON-NLS-1$
        String QUOTE = "'"; //$NON-NLS-1$
        String EQ = "="; //$NON-NLS-1$
        String NE = "<>"; //$NON-NLS-1$
        String LT = "<"; //$NON-NLS-1$
        String GT = ">"; //$NON-NLS-1$
        String LE = "<="; //$NON-NLS-1$
        String GE = ">="; //$NON-NLS-1$
        String LPAREN = "("; //$NON-NLS-1$
        String RPAREN = ")"; //$NON-NLS-1$
        String COLON = ":"; //$NON-NLS-1$
        @Since ("8.0.0")
        String TICK = "'"; //$NON-NLS-1$
        @Since ("8.0.0")
        String SEMICOLON = ";"; //$NON-NLS-1$
    }

    public interface NonReserved {
        String SQL_TSI_FRAC_SECOND = "SQL_TSI_FRAC_SECOND"; //$NON-NLS-1$
        String SQL_TSI_SECOND = "SQL_TSI_SECOND"; //$NON-NLS-1$
        String SQL_TSI_MINUTE = "SQL_TSI_MINUTE"; //$NON-NLS-1$
        String SQL_TSI_HOUR = "SQL_TSI_HOUR"; //$NON-NLS-1$
        String SQL_TSI_DAY = "SQL_TSI_DAY"; //$NON-NLS-1$
        String SQL_TSI_WEEK = "SQL_TSI_WEEK"; //$NON-NLS-1$
        String SQL_TSI_MONTH = "SQL_TSI_MONTH"; //$NON-NLS-1$
        String SQL_TSI_QUARTER = "SQL_TSI_QUARTER"; //$NON-NLS-1$
        String SQL_TSI_YEAR = "SQL_TSI_YEAR"; //$NON-NLS-1$
        String TIMESTAMPADD = "TIMESTAMPADD"; //$NON-NLS-1$
        String TIMESTAMPDIFF = "TIMESTAMPDIFF"; //$NON-NLS-1$
        //aggregate functions
        String MAX = "MAX"; //$NON-NLS-1$
        String MIN = "MIN"; //$NON-NLS-1$
        String COUNT = "COUNT"; //$NON-NLS-1$
        String AVG = "AVG"; //$NON-NLS-1$
        String SUM = "SUM"; //$NON-NLS-1$
        //texttable
        String WIDTH = "WIDTH"; //$NON-NLS-1$
        String DELIMITER = "DELIMITER"; //$NON-NLS-1$
        String HEADER = "HEADER"; //$NON-NLS-1$
        String QUOTE = "QUOTE"; //$NON-NLS-1$
        String COLUMNS = "COLUMNS"; //$NON-NLS-1$
        @Since ("8.0.0")
        String SELECTOR = "SELECTOR"; //$NON-NLS-1$
        @Since ("8.0.0")
        String SKIP = "SKIP"; //$NON-NLS-1$
        //xmltable
        String ORDINALITY = "ORDINALITY"; //$NON-NLS-1$
        String PASSING = "PASSING"; //$NON-NLS-1$
        String PATH = "PATH"; //$NON-NLS-1$
        //xmlserialize
        String DOCUMENT = "DOCUMENT"; //$NON-NLS-1$
        String CONTENT = "CONTENT"; //$NON-NLS-1$
        //xmlquery
        String RETURNING = "RETURNING"; //$NON-NLS-1$
        String SEQUENCE = "SEQUENCE"; //$NON-NLS-1$
        String EMPTY = "EMPTY"; //$NON-NLS-1$
        //querystring function
        String QUERYSTRING = "QUERYSTRING"; //$NON-NLS-1$
        //xmlparse
        String WELLFORMED = "WELLFORMED"; //$NON-NLS-1$
        //agg
        String EVERY = "EVERY"; //$NON-NLS-1$
        String STDDEV_POP = "STDDEV_POP"; //$NON-NLS-1$
        String STDDEV_SAMP = "STDDEV_SAMP"; //$NON-NLS-1$
        String VAR_SAMP = "VAR_SAMP"; //$NON-NLS-1$
        String VAR_POP = "VAR_POP"; //$NON-NLS-1$
        
        String NULLS = "NULLS"; //$NON-NLS-1$
        String FIRST = "FIRST"; //$NON-NLS-1$
        String LAST = "LAST"; //$NON-NLS-1$
        
        String KEY = "KEY"; //$NON-NLS-1$
        
        String SERIAL = "SERIAL"; //$NON-NLS-1$
        
        String ENCODING = "ENCODING"; //$NON-NLS-1$
        String TEXTAGG = "TEXTAGG"; //$NON-NLS-1$
        
        String ARRAYTABLE = "ARRAYTABLE"; //$NON-NLS-1$
        
        String VIEW = "VIEW"; //$NON-NLS-1$
        String INSTEAD = "INSTEAD"; //$NON-NLS-1$
        String ENABLED = "ENABLED"; //$NON-NLS-1$
        String DISABLED = "DISABLED"; //$NON-NLS-1$
        
        String TRIM = "TRIM"; //$NON-NLS-1$
        @Since ("8.0.0")
        String RESULT = "RESULT"; //$NON-NLS-1$
        @Since ("8.0.0")
        String OBJECTTABLE = "OBJECTTABLE"; //$NON-NLS-1$
        @Since ("8.0.0")
        String VERSION = "VERSION"; //$NON-NLS-1$
        @Since ("8.0.0")
        String INCLUDING = "INCLUDING"; //$NON-NLS-1$
        @Since ("8.0.0")
        String EXCLUDING = "EXCLUDING"; //$NON-NLS-1$
        @Since ("8.0.0")
        String XMLDECLARATION = "XMLDECLARATION"; //$NON-NLS-1$
        @Since ("8.0.0")
        String VARIADIC = "VARIADIC"; //$NON-NLS-1$
        @Since ("8.0.0")
        String INDEX = "INDEX"; //$NON-NLS-1$
        @Since ("8.0.0")
        String EXCEPTION = "EXCEPTION"; //$NON-NLS-1$
        @Since ("8.0.0")
        String RAISE = "RAISE"; //$NON-NLS-1$
        @Since ("8.0.0")
        String CHAIN = "CHAIN"; //$NON-NLS-1$
        @Since ("8.0.0")
        String JSONOBJECT = "JSONOBJECT"; //$NON-NLS-1$
        @Since ("8.7.0")
        String AUTO_INCREMENT = "AUTO_INCREMENT"; //$NON-NLS-1$
    }
    
    public interface Reserved {
        //Teiid specific
        String BIGDECIMAL = "BIGDECIMAL"; //$NON-NLS-1$
        String BIGINTEGER = "BIGINTEGER"; //$NON-NLS-1$
        String BREAK = "BREAK"; //$NON-NLS-1$
        String BYTE = "BYTE"; //$NON-NLS-1$
        String CRITERIA = "CRITERIA"; //$NON-NLS-1$
        String ERROR = "ERROR";  //$NON-NLS-1$
        String LIMIT = "LIMIT"; //$NON-NLS-1$
        String LONG = "LONG"; //$NON-NLS-1$
        String LOOP = "LOOP"; //$NON-NLS-1$
        String MAKEDEP = "MAKEDEP"; //$NON-NLS-1$
        String MAKENOTDEP = "MAKENOTDEP"; //$NON-NLS-1$
        String NOCACHE = "NOCACHE"; //$NON-NLS-1$
        String STRING = "STRING"; //$NON-NLS-1$
        String VIRTUAL = "VIRTUAL"; //$NON-NLS-1$
        String WHILE = "WHILE"; //$NON-NLS-1$
        
        //SQL2003 keywords
        String ADD = "ADD"; //$NON-NLS-1$
        String ANY = "ANY"; //$NON-NLS-1$
        String ALL = "ALL"; //$NON-NLS-1$
        String ALLOCATE = "ALLOCATE"; //$NON-NLS-1$
        String ALTER = "ALTER"; //$NON-NLS-1$
        String AND = "AND"; //$NON-NLS-1$
        String ARE = "ARE"; //$NON-NLS-1$
        String ARRAY = "ARRAY"; //$NON-NLS-1$s
        String AS = "AS"; //$NON-NLS-1$
        String ASC = "ASC"; //$NON-NLS-1$
        String ASENSITIVE = "ASENSITIVE"; //$NON-NLS-1$
        String ASYMETRIC = "ASYMETRIC"; //$NON-NLS-1$
        String ATOMIC = "ATOMIC"; //$NON-NLS-1$
        String AUTHORIZATION = "AUTHORIZATION"; //$NON-NLS-1$
        String BEGIN = "BEGIN"; //$NON-NLS-1$
        String BETWEEN = "BETWEEN"; //$NON-NLS-1$
        String BIGINT = "BIGINT"; //$NON-NLS-1$
        String BINARY = "BINARY"; //$NON-NLS-1$
        String BLOB = "BLOB"; //$NON-NLS-1$
        String BOTH = "BOTH"; //$NON-NLS-1$
        String BY = "BY"; //$NON-NLS-1$
        String CALL = "CALL"; //$NON-NLS-1$
        String CALLED = "CALLED"; //$NON-NLS-1$
        String CASE = "CASE"; //$NON-NLS-1$
        String CAST = "CAST"; //$NON-NLS-1$
        String CASCADED = "CASCADED"; //$NON-NLS-1$
        String CHAR = "CHAR"; //$NON-NLS-1$
        String CHARACTER = "CHARACTER"; //$NON-NLS-1$
        String CHECK = "CHECK"; //$NON-NLS-1$
        String CLOB = "CLOB"; //$NON-NLS-1$
        String CLOSE = "CLOSE"; //$NON-NLS-1$
        String COLLATE = "COLLATE"; //$NON-NLS-1$
        String COLUMN = "COLUMN"; //$NON-NLS-1$
        String COMMIT = "COMMIT"; //$NON-NLS-1$
        String CONNECT = "CONNECT"; //$NON-NLS-1$
        String CONVERT = "CONVERT"; //$NON-NLS-1$
        String CONSTRAINT = "CONSTRAINT"; //$NON-NLS-1$
        String CONTINUE = "CONTINUE"; //$NON-NLS-1$
        String CORRESPONDING = "CORRESPONDING"; //$NON-NLS-1$
        String CREATE = "CREATE"; //$NON-NLS-1$
        String CROSS = "CROSS"; //$NON-NLS-1$
        String CURRENT_DATE = "CURRENT_DATE"; //$NON-NLS-1$
        String CURRENT_TIME = "CURRENT_TIME"; //$NON-NLS-1$
        String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$
        String CURRENT_USER = "CURRENT_USER"; //$NON-NLS-1$
        String CURSOR = "CURSOR"; //$NON-NLS-1$
        String CYCLE = "CYCLE"; //$NON-NLS-1$
        String DATE = "DATE"; //$NON-NLS-1$
        String DAY = "DAY"; //$NON-NLS-1$
        String DEALLOCATE = "DEALLOCATE"; //$NON-NLS-1$
        String DEC = "DEC"; //$NON-NLS-1$
        String DECIMAL = "DECIMAL"; //$NON-NLS-1$
        String DECLARE = "DECLARE";     //$NON-NLS-1$
        String DEFAULT = "DEFAULT"; //$NON-NLS-1$
        String DELETE = "DELETE"; //$NON-NLS-1$
        String DEREF = "DEREF"; //$NON-NLS-1$
        String DESC = "DESC"; //$NON-NLS-1$
        String DESCRIBE = "DESCRIBE"; //$NON-NLS-1$
        String DETERMINISTIC = "DETERMINISTIC"; //$NON-NLS-1$
        String DISCONNECT = "DISCONNECT"; //$NON-NLS-1$
        String DISTINCT = "DISTINCT"; //$NON-NLS-1$
        String DOUBLE = "DOUBLE"; //$NON-NLS-1$
        String DROP = "DROP"; //$NON-NLS-1$
        String DYNAMIC = "DYNAMIC"; //$NON-NLS-1$
        String EACH = "EACH"; //$NON-NLS-1$
        String ELEMENT = "ELEMENT"; //$NON-NLS-1$
        String ELSE = "ELSE";    //$NON-NLS-1$
        String END = "END"; //$NON-NLS-1$
        String ESCAPE = "ESCAPE"; //$NON-NLS-1$
        String EXCEPT = "EXCEPT"; //$NON-NLS-1$
        String EXEC = "EXEC"; //$NON-NLS-1$
        String EXECUTE = "EXECUTE"; //$NON-NLS-1$
        String EXISTS = "EXISTS"; //$NON-NLS-1$
        String EXTERNAL = "EXTERNAL"; //$NON-NLS-1$
        String FALSE = "FALSE"; //$NON-NLS-1$
        String FETCH = "FETCH"; //$NON-NLS-1$
        String FILTER = "FILTER"; //$NON-NLS-1$
        String FLOAT = "FLOAT"; //$NON-NLS-1$
        String FOR = "FOR";     //$NON-NLS-1$
        String FOREIGN = "FOREIGN"; //$NON-NLS-1$
        String FREE = "FREE"; //$NON-NLS-1$
        String FROM = "FROM"; //$NON-NLS-1$
        String FULL = "FULL"; //$NON-NLS-1$
        String FUNCTION = "FUNCTION"; //$NON-NLS-1$
        String GET = "GET"; //$NON-NLS-1$
        String GLOBAL = "GLOBAL"; //$NON-NLS-1$
        String GRANT = "GRANT"; //$NON-NLS-1$
        String GROUP = "GROUP"; //$NON-NLS-1$
        String GROUPING = "GROUPING"; //$NON-NLS-1$
        String HAS = "HAS";  //$NON-NLS-1$
        String HAVING = "HAVING"; //$NON-NLS-1$
        String HOLD = "HOLD"; //$NON-NLS-1$
        String HOUR = "HOUR"; //$NON-NLS-1$
        String IDENTITY = "IDENTITY"; //$NON-NLS-1$
        String INDICATOR = "INDICATOR"; //$NON-NLS-1$
        String IF = "IF";     //$NON-NLS-1$
        String IMMEDIATE = "IMMEDIATE"; //$NON-NLS-1$
        String IN = "IN"; //$NON-NLS-1$
        String INOUT = "INOUT"; //$NON-NLS-1$
        String INNER = "INNER"; //$NON-NLS-1$
        String INPUT = "INPUT"; //$NON-NLS-1$
        String INSENSITIVE = "INSENSITIVE"; //$NON-NLS-1$
        String INSERT = "INSERT"; //$NON-NLS-1$
        String INTEGER = "INTEGER"; //$NON-NLS-1$
        String INTERSECT = "INTERSECT"; //$NON-NLS-1$
        String INTERVAL = "INTERVAL"; //$NON-NLS-1$
        String INT = "INT"; //$NON-NLS-1$
        String INTO = "INTO"; //$NON-NLS-1$
        String IS = "IS";     //$NON-NLS-1$
        String ISOLATION = "ISOLATION"; //$NON-NLS-1$
        String JOIN = "JOIN"; //$NON-NLS-1$
        String LANGUAGE = "LANGUAGE"; //$NON-NLS-1$
        String LARGE = "LARGE"; //$NON-NLS-1$
        String LATERAL = "LATERAL"; //$NON-NLS-1$
        String LEADING = "LEADING"; //$NON-NLS-1$
        String LEAVE = "LEAVE"; //$NON-NLS-1$
        String LEFT = "LEFT"; //$NON-NLS-1$
        String LIKE = "LIKE"; //$NON-NLS-1$
        String LIKE_REGEX = "LIKE_REGEX"; //$NON-NLS-1$
        String LOCAL = "LOCAL"; //$NON-NLS-1$
        String LOCALTIME = "LOCALTIME"; //$NON-NLS-1$
        String LOCALTIMESTAMP = "LOCALTIMESTAMP"; //$NON-NLS-1$
        String MATCH = "MATCH"; //$NON-NLS-1$
        String MEMBER = "MEMBER"; //$NON-NLS-1$
        String MERGE = "MERGE"; //$NON-NLS-1$
        String METHOD = "METHOD"; //$NON-NLS-1$
        String MINUTE = "MINUTE"; //$NON-NLS-1$
        String MODIFIES = "MODIFIES"; //$NON-NLS-1$
        String MODULE = "MODULE"; //$NON-NLS-1$
        String MONTH = "MONTH"; //$NON-NLS-1$
        String MULTISET = "MULTISET"; //$NON-NLS-1$
        String NATIONAL = "NATIONAL"; //$NON-NLS-1$
        String NATURAL = "NATURAL"; //$NON-NLS-1$
        String NCHAR = "NCHAR"; //$NON-NLS-1$
        String NCLOB = "NCLOB"; //$NON-NLS-1$
        String NEW = "NEW"; //$NON-NLS-1$
        String NO = "NO"; //$NON-NLS-1$
        String NONE = "NONE"; //$NON-NLS-1$
        String NOT = "NOT"; //$NON-NLS-1$
        String NULL = "NULL"; //$NON-NLS-1$
        String NUMERIC = "NUMERIC"; //$NON-NLS-1$
        String OBJECT = "OBJECT"; //$NON-NLS-1$
        String OF = "OF"; //$NON-NLS-1$
        String OFFSET = "OFFSET"; //$NON-NLS-1$
        String OLD = "OLD"; //$NON-NLS-1$
        String ON = "ON"; //$NON-NLS-1$
        String ONLY = "ONLY"; //$NON-NLS-1$
        String OPEN = "OPEN"; //$NON-NLS-1$
        String OR = "OR"; //$NON-NLS-1$
        String ORDER = "ORDER"; //$NON-NLS-1$
        String OUT = "OUT"; //$NON-NLS-1$
        String OUTER = "OUTER"; //$NON-NLS-1$
        String OUTPUT = "OUTPUT"; //$NON-NLS-1$
        String OPTION = "OPTION"; //$NON-NLS-1$
        @Since ("8.0.0")
        String OPTIONS = "OPTIONS"; //$NON-NLS-1$
        String OVER = "OVER"; //$NON-NLS-1$
        String OVERLAPS = "OVERLAPS"; //$NON-NLS-1$
        String PARAMETER = "PARAMETER"; //$NON-NLS-1$
        String PARTITION = "PARTITION"; //$NON-NLS-1$
        String PRECISION = "PRECISION"; //$NON-NLS-1$
        String PREPARE = "PREPARE"; //$NON-NLS-1$
        String PRIMARY = "PRIMARY"; //$NON-NLS-1$
        String PROCEDURE = "PROCEDURE"; //$NON-NLS-1$
        String RANGE = "RANGE"; //$NON-NLS-1$
        String READS = "READS"; //$NON-NLS-1$
        String REAL = "REAL"; //$NON-NLS-1$
        String RECURSIVE = "RECURSIVE"; //$NON-NLS-1$
        String REFERENCES = "REFERENCES"; //$NON-NLS-1$
        String REFERENCING = "REFERENCING"; //$NON-NLS-1$
        String RELEASE = "RELEASE"; //$NON-NLS-1$
        String RETURN = "RETURN"; //$NON-NLS-1$
        String RETURNS = "RETURNS"; //$NON-NLS-1$
        String REVOKE = "REVOKE"; //$NON-NLS-1$
        String RIGHT = "RIGHT"; //$NON-NLS-1$
        String ROLLBACK = "ROLLBACK"; //$NON-NLS-1$
        String ROLLUP = "ROLLUP"; //$NON-NLS-1$
        String ROW = "ROW"; //$NON-NLS-1$
        String ROWS = "ROWS"; //$NON-NLS-1$
        String SAVEPOINT = "SAVEPOINT"; //$NON-NLS-1$
        String SCROLL = "SCROLL"; //$NON-NLS-1$
        String SEARCH = "SEARCH"; //$NON-NLS-1$
        String SECOND = "SECOND"; //$NON-NLS-1$
        String SELECT = "SELECT"; //$NON-NLS-1$
        String SENSITIVE = "SENSITIVE"; //$NON-NLS-1$
        String SESSION_USER = "SESSION_USER"; //$NON-NLS-1$
        String SET = "SET"; //$NON-NLS-1$
        String SHORT = "SHORT"; //$NON-NLS-1$
        String SIMILAR = "SIMILAR"; //$NON-NLS-1$
        String SMALLINT = "SMALLINT"; //$NON-NLS-1$
        String SOME = "SOME"; //$NON-NLS-1$
        String SPECIFIC = "SPECIFIC"; //$NON-NLS-1$
        String SPECIFICTYPE = "SPECIFICTYPE"; //$NON-NLS-1$
        String SQL = "SQL"; //$NON-NLS-1$
        String SQLEXCEPTION = "SQLEXCEPTION"; //$NON-NLS-1$
        String SQLSTATE = "SQLSTATE"; //$NON-NLS-1$
        String SQLWARNING = "SQLWARNING"; //$NON-NLS-1$
        String SUBMULTILIST = "SUBMULTILIST"; //$NON-NLS-1$
        String START = "START"; //$NON-NLS-1$
        String STATIC = "STATIC"; //$NON-NLS-1$
        String SYMETRIC = "SYMETRIC"; //$NON-NLS-1$
        String SYSTEM = "SYSTEM"; //$NON-NLS-1$
        String SYSTEM_USER = "SYSTEM_USER"; //$NON-NLS-1$
        String TABLE = "TABLE"; //$NON-NLS-1$
        String TEMPORARY = "TEMPORARY"; //$NON-NLS-1$
        String THEN = "THEN"; //$NON-NLS-1$
        String TIME = "TIME"; //$NON-NLS-1$
        String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
        String TIMEZONE_HOUR = "TIMEZONE_HOUR"; //$NON-NLS-1$
        String TIMEZONE_MINUTE = "TIMEZONE_MINUTE"; //$NON-NLS-1$
        String TO = "TO"; //$NON-NLS-1$
        String TREAT = "TREAT"; //$NON-NLS-1$
        String TRAILING = "TRAILING"; //$NON-NLS-1$
        String TRANSLATE = "TRANSLATE";  //$NON-NLS-1$
        String TRANSLATION = "TRANSLATION";  //$NON-NLS-1$
        String TRIGGER = "TRIGGER"; //$NON-NLS-1$
        String TRUE = "TRUE"; //$NON-NLS-1$
        String UNION = "UNION"; //$NON-NLS-1$
        String UNIQUE = "UNIQUE"; //$NON-NLS-1$
        String UNKNOWN = "UNKNOWN"; //$NON-NLS-1$
        String UPDATE = "UPDATE"; //$NON-NLS-1$
        String USER = "USER"; //$NON-NLS-1$
        String USING = "USING";  //$NON-NLS-1$
        String VALUE = "VALUE"; //$NON-NLS-1$
        String VALUES = "VALUES"; //$NON-NLS-1$
        String VARCHAR = "VARCHAR"; //$NON-NLS-1$
        String VARYING = "VARYING"; //$NON-NLS-1$
        String WHEN = "WHEN";     //$NON-NLS-1$
        String WHENEVER = "WHENEVER";     //$NON-NLS-1$
        String WHERE = "WHERE"; //$NON-NLS-1$
        String WINDOW = "WINDOW"; //$NON-NLS-1$
        String WITH = "WITH";     //$NON-NLS-1$
        String WITHIN = "WITHIN"; //$NON-NLS-1$
        String WITHOUT = "WITHOUT"; //$NON-NLS-1$
        String YEAR = "YEAR"; //$NON-NLS-1$
        
        // SQL 2008 words
        String ARRAY_AGG= "ARRAY_AGG"; //$NON-NLS-1$
        
        //SQL/XML
        
        String XML = "XML"; //$NON-NLS-1$
        String XMLAGG = "XMLAGG"; //$NON-NLS-1$
        String XMLATTRIBUTES = "XMLATTRIBUTES"; //$NON-NLS-1$
        String XMLBINARY = "XMLBINARY"; //$NON-NLS-1$
        String XMLCAST = "XMLCAST"; //$NON-NLS-1$
        String XMLCOMMENT = "XMLCOMMENT"; //$NON-NLS-1$
        String XMLCONCAT = "XMLCONCAT"; //$NON-NLS-1$
        String XMLDOCUMENT = "XMLDOCUMENT"; //$NON-NLS-1$
        String XMLELEMENT = "XMLELEMENT"; //$NON-NLS-1$
        String XMLEXISTS = "XMLEXISTS"; //$NON-NLS-1$
        String XMLFOREST = "XMLFOREST"; //$NON-NLS-1$
        String XMLITERATE = "XMLITERATE"; //$NON-NLS-1$
        String XMLNAMESPACES = "XMLNAMESPACES"; //$NON-NLS-1$
        String XMLPARSE = "XMLPARSE"; //$NON-NLS-1$
        String XMLPI = "XMLPI"; //$NON-NLS-1$
        String XMLQUERY = "XMLQUERY"; //$NON-NLS-1$
        String XMLSERIALIZE = "XMLSERIALIZE"; //$NON-NLS-1$
        String XMLTABLE = "XMLTABLE"; //$NON-NLS-1$
        String XMLTEXT = "XMLTEXT"; //$NON-NLS-1$
        String XMLVALIDATE = "XMLVALIDATE"; //$NON-NLS-1$
        
        //SQL/MED
        
        String DATALINK = "DATALINK"; //$NON-NLS-1$
        String DLNEWCOPY = "DLNEWCOPY"; //$NON-NLS-1$
        String DLPREVIOUSCOPY = "DLPREVIOUSCOPY"; //$NON-NLS-1$
        String DLURLCOMPLETE = "DLURLCOMPLETE"; //$NON-NLS-1$
        String DLURLCOMPLETEWRITE = "DLURELCOMPLETEWRITE"; //$NON-NLS-1$
        String DLURLCOMPLETEONLY = "DLURLCOMPLETEONLY"; //$NON-NLS-1$
        String DLURLPATH = "DLURLPATH"; //$NON-NLS-1$
        String DLURLPATHWRITE = "DLURLPATHWRITE"; //$NON-NLS-1$
        String DLURLPATHONLY = "DLURLPATHONLY"; //$NON-NLS-1$
        String DLURLSCHEME = "DLURLSCHEME"; //$NON-NLS-1$
        String DLURLSERVER = "DLURLSEVER"; //$NON-NLS-1$
        String DLVALUE = "DLVALUE"; //$NON-NLS-1$
        String IMPORT = "IMPORT"; //$NON-NLS-1$
    }

    private static ITeiidServerVersion CACHED_TEIID_VERSION = null;

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

            // If teiid version is less than the version listed in the since annotation
            // then word should not be included in the word sets
            if (AnnotationUtils.hasAnnotation(field, Since.class)) {
                Since since = AnnotationUtils.getAnnotation(field, Since.class);
                if (! AnnotationUtils.isGreaterThanOrEqualTo(since, CACHED_TEIID_VERSION))
                    continue;
            }

            try {
                if (!result.add((String)field.get(null))) {
                    throw new AssertionError("Duplicate value for " + field.getName()); //$NON-NLS-1$
                }
            } catch (Exception e) {}
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * @param teiidVersion
     */
    private static void initialiseConstants(ITeiidServerVersion teiidVersion) {
        CACHED_TEIID_VERSION = teiidVersion;
        RESERVED_WORDS = extractFieldNames(SQLConstants.Reserved.class);
        NON_RESERVED_WORDS = extractFieldNames(SQLConstants.NonReserved.class);
    }

    /**
     * @return nonReservedWords
     */
    public static Set<String> getNonReservedWords(ITeiidServerVersion teiidVersion) {
        if (CACHED_TEIID_VERSION == null || ! CACHED_TEIID_VERSION.equals(teiidVersion) || NON_RESERVED_WORDS == null)
            initialiseConstants(teiidVersion);

        return NON_RESERVED_WORDS;
    }

    /**
     * @return reservedWords
     */
    public static Set<String> getReservedWords(ITeiidServerVersion teiidVersion) {
        if (CACHED_TEIID_VERSION == null || ! CACHED_TEIID_VERSION.equals(teiidVersion) || RESERVED_WORDS == null)
            initialiseConstants(teiidVersion);

        return RESERVED_WORDS;
    }
    
    /** Can't construct */
    private SQLConstants() {}   

    /**
     * Check whether a string is a reserved word.  
     * @param str String to check
     * @return True if reserved word, false if not or null
     */
    public static final boolean isReservedWord(ITeiidServerVersion teiidVersion, String str) {
        if(str == null) { 
            return false;    
        }

        String word = str.toUpperCase();
        if (CACHED_TEIID_VERSION == null || ! CACHED_TEIID_VERSION.equals(teiidVersion) || RESERVED_WORDS == null)
            initialiseConstants(teiidVersion);

        return RESERVED_WORDS.contains(word);
    }
}
