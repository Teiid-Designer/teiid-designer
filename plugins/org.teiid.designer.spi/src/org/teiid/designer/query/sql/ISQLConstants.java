/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.sql;

/**
 * SqlConstants
 *
 * @since 8.0
 */
public interface ISQLConstants {

    String EMPTY_STR = ""; //$NON-NLS-1$
    String TAB = "\t"; //$NON-NLS-1$
    String TAB2 = "\t\t"; //$NON-NLS-1$
    String TAB3 = "\t\t\t"; //$NON-NLS-1$
    String TAB4 = "\t\t\t\t"; //$NON-NLS-1$
    String CR = "\n"; //$NON-NLS-1$
    String CR_1 = "\\r\\n"; //$NON-NLS-1$
    String CR_2 = "\\n"; //$NON-NLS-1$
    String BLANK = ""; //$NON-NLS-1$
    String DBL_SPACE = "  "; //$NON-NLS-1$
    String SPACE = " "; //$NON-NLS-1$
    String COMMA = ","; //$NON-NLS-1$
    String TRUE = "TRUE"; //$NON-NLS-1$
    String FALSE = "FALSE"; //$NON-NLS-1$
    String RETURN = "\n"; //$NON-NLS-1$
    String SELECT = "SELECT"; //$NON-NLS-1$
    String FROM = "FROM"; //$NON-NLS-1$
    String WHERE = "WHERE"; //$NON-NLS-1$
    String HAVING = "HAVING"; //$NON-NLS-1$
	String DOT = "."; //$NON-NLS-1$
	String STAR = "*"; //$NON-NLS-1$
    String L_PAREN = "("; //$NON-NLS-1$
    String R_PAREN = ")"; //$NON-NLS-1$
    String S_QUOTE = "\'"; //$NON-NLS-1$
    String D_QUOTE = "\""; //$NON-NLS-1$
    String SEMI_COLON = ";"; //$NON-NLS-1$
    String COLON = ":"; //$NON-NLS-1$
    String BAR = "|"; //$NON-NLS-1$
    String AS = "AS"; //$NON-NLS-1$
    String COLUMNS = "COLUMNS"; //$NON-NLS-1$
    String BEGIN = "BEGIN"; //$NON-NLS-1$
    String END = "END"; //$NON-NLS-1$
    String XMLELEMENT = "XMLELEMENT"; //$NON-NLS-1$
    String XMLATTRIBUTES = "XMLATTRIBUTES"; //$NON-NLS-1$
    String NAME = "NAME"; //$NON-NLS-1$
    String XMLNAMESPACES = "XMLNAMESPACES"; //$NON-NLS-1$
    String DEFAULT = "DEFAULT"; //$NON-NLS-1$
    String NO_DEFAULT = "NO DEFAULT"; //$NON-NLS-1$
    String XMLTABLE = "XMLTABLE"; //$NON-NLS-1$
    String TEXTTABLE = "TEXTTABLE"; //$NON-NLS-1$
    String TABLE = "TABLE"; //$NON-NLS-1$
    String EXEC = "EXEC"; //$NON-NLS-1$
    String CONVERT = "CONVERT"; //$NON-NLS-1$
    String NULL = "NULL"; //$NON-NLS-1$
    String PARAM_ASSIGNMENT = "=>"; //$NON-NLS-1$
   
    //PARAMETERS FOR invoke()
    String BINDING_PARAM = "binding"; //$NON-NLS-1$
    String ACTION_PARAM = "action"; //$NON-NLS-1$
    String REQUEST_PARAM = "request"; //$NON-NLS-1$
    String ENDPOINT_PARAM = "endpoint"; //$NON-NLS-1$
    String STREAM_PARAM = "stream"; //$NON-NLS-1$
    
    String ENVELOPE_NS = "http://schemas.xmlsoap.org/soap/envelope/"; //$NON-NLS-1$
    String ENVELOPE_NS_ALIAS = "soap"; //$NON-NLS-1$
    String ENVELOPE_NAME = ENVELOPE_NS_ALIAS+":Envelope"; //$NON-NLS-1$
    String HEADER_NAME = ENVELOPE_NS_ALIAS+":Header"; //$NON-NLS-1$
    String BODY_NAME = ENVELOPE_NS_ALIAS+":Body"; //$NON-NLS-1$
    
    String PATH = "PATH"; //$NON-NLS-1$
    String FOR_ORDINALITY = "FOR ORDINALITY"; //$NON-NLS-1$
    String DEFAULT_XQUERY = "/"; //$NON-NLS-1$
    String GET = "GET"; //$NON-NLS-1$
    String PASSING = "PASSING"; //$NON-NLS-1$

    String SQL_TYPE_CREATE_STRING = "CREATE"; //$NON-NLS-1$
    String SQL_TYPE_SELECT_STRING = "SELECT"; //$NON-NLS-1$
    String SQL_TYPE_UPDATE_STRING = "UPDATE"; //$NON-NLS-1$
    String SQL_TYPE_INSERT_STRING = "INSERT"; //$NON-NLS-1$
    String SQL_TYPE_DELETE_STRING = "DELETE"; //$NON-NLS-1$
    String SQL_TYPE_UNKNOWN_STRING = "UNKNOWN"; //$NON-NLS-1$
    
    String FUNCTION_GET_FILES = "getFiles"; //$NON-NLS-1$
    String FUNCTION_GET_TEXT_FILES = "getTextFiles"; //$NON-NLS-1$
    String FUNCTION_SAVE_FILE = "saveFile"; //$NON-NLS-1$
    
    String FUNCTION_INVOKE = "invoke"; //$NON-NLS-1$
    String FUNCTION_INVOKE_HTTP = "invokeHttp"; //$NON-NLS-1$
    
    String ROWCOUNT = "ROWCOUNT"; //$NON-NLS-1$
    String CHANGING = "CHANGING"; //$NON-NLS-1$
    String VARIABLES = "VARIABLES"; //$NON-NLS-1$
    String DVARS = "DVARS"; //$NON-NLS-1$
    
    String DEFAULT_DELIMITER = ","; //$NON-NLS-1$
    String DEFAULT_QUOTE = "\""; //$NON-NLS-1$
    String DEFAULT_ESCAPE = "\\"; //$NON-NLS-1$
    
    String HEADER = "HEADER"; //$NON-NLS-1$
    String SKIP = "SKIP"; //$NON-NLS-1$
    String WIDTH = "width"; //$NON-NLS-1$
    String NO_TRIM = "NO TRIM"; //$NON-NLS-1$
    String ESCAPE_STR = "ESCAPE"; //$NON-NLS-1$
    String QUOTE_STR = "QUOTE"; //$NON-NLS-1$
    String DELIMITER_STR = "DELIMITER"; //$NON-NLS-1$
    
    String NO_ROW_DELIMIETER_STR = "NO ROW DELIMITER"; //$NON-NLS-1$
    String ROW_DELIMIETER_STR= "ROW DELIMITER"; //$NON-NLS-1$
    
    interface INSERT_OPTIONS {
    	int REPLACE_ALL = 0;
    	int INSERT_AT_BEGINNING = 1;
    	int INSERT_AT_CURSOR =  2;
    	int INSERT_AT_END = 3;
    	
    }
    
    interface ROW_DELIMETER_OPTIONS {
    	int DEFAULT = 0;
    	int CUSTOM = 1;
    	int NONE =  2;
    }

}
