/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

/**
 * The <code>DisplayNodeConstants</code> interface contains constants used when working with {@link DisplayNode}s.
 * @author Mark Drilling 11/22/02
 * @since 8.0
 * @version 1.0
 */
public interface DisplayNodeConstants {

    // Node String Constants
    String BLANK = ""; //$NON-NLS-1$
    String SPACE = " "; //$NON-NLS-1$
    String DBLSPACE = "  "; //$NON-NLS-1$
    String TAB = "\t"; //$NON-NLS-1$
    String CR = "\n"; //$NON-NLS-1$
    String COMMA = ","; //$NON-NLS-1$
    String COLON = ":"; //$NON-NLS-1$
    String SEMICOLON = ";"; //$NON-NLS-1$
    String LTPAREN = "("; //$NON-NLS-1$
    String RTPAREN = ")"; //$NON-NLS-1$
    String EQUALS = "="; //$NON-NLS-1$
    String ERROR = "ERROR"; //$NON-NLS-1$
    String GT = ">"; //$NON-NLS-1$
    String GE = ">="; //$NON-NLS-1$
    String LT = "<"; //$NON-NLS-1$
    String LE = "<="; //$NON-NLS-1$
    String NE = "<>"; //$NON-NLS-1$
    String NULL = "null"; //$NON-NLS-1$
    String UNKNOWN = "Unknown"; //$NON-NLS-1$
    String SELECT_STR = "SELECT"; //$NON-NLS-1$
    String FROM_STR = "FROM"; //$NON-NLS-1$
    String WHERE_STR = "WHERE"; //$NON-NLS-1$
    String OPTIONAL_COMMENTS = "/*+ optional */"; //$NON-NLS-1$
    String UNDEFINED = "<undefined>"; //$NON-NLS-1$
    String EXEC = "EXEC"; //$NON-NLS-1$
    String NAME = "NAME"; //$NON-NLS-1$
    char ID_ESCAPE_CHAR = '\"';
    String BEGIN_HINT = "/*+"; //$NON-NLS-1$
    String END_HINT = "*/"; //$NON-NLS-1$
    String QMARK = "?"; //$NON-NLS-1$
    String LTBRACE = "{"; //$NON-NLS-1$
    String RTBRACE = "}"; //$NON-NLS-1$
    String DOT = "."; //$NON-NLS-1$
    String SPEECH_MARK = "\""; //$NON-NLS-1$
    String QUOTE = "'"; //$NON-NLS-1$
    String FORWARD_SLASH = "/"; //$NON-NLS-1$
    String STAR = "*"; //$NON-NLS-1$
    String PLUS = "+"; //$NON-NLS-1$
    String PIPE = "|"; //$NON-NLS-1$
    String ESCAPE = "\\"; //$NON-NLS-1$
    String REGEX_ESCAPE = "\\\\"; //$NON-NLS-1$

    // Node Type Identifiers
    int ELEMENT = 0;
    int GROUP = 1;
    int CRITERIA = 2;
    int EXPRESSION = 3;
    int COMMAND = 4;
    int CLAUSE = 5;
    int SELECT = 6;
    int INTO = 7;
    int FROM = 8;
    int WHERE = 9;
    int GROUPBY = 10;
    int HAVING = 11;
    int ORDERBY = 12;
    int OPTION = 13;
    int EDITABLE_CRITERIA = 14;
    int SCALAR_SUBQUERY = 15;

}
