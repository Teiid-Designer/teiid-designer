package com.metamatrix.modeler.modelgenerator.xsd.procedures;

public interface IBuilderConstants {
	
	static final Object V_FUNC_SPACE = " "; //$NON-NLS-1$
	static final Object V_FUNC_PREAMBLE = "CREATE VIRTUAL PROCEDURE BEGIN"; //$NON-NLS-1$
	static final Object V_FUNC_POSTSCRIPT = "END"; //$NON-NLS-1$
	static final Object V_FUNC_SELECT = "SELECT"; //$NON-NLS-1$
	static final Object V_FUNC_SELECT_FROM_T = "SELECT t.* FROM"; //$NON-NLS-1$
	static final Object V_FUNC_XMLTABLE = "XMLTABLE"; //$NON-NLS-1$
	static final Object V_FUNC_OPEN = "("; //$NON-NLS-1$
	static final Object V_FUNC_CLOSE = ")"; //$NON-NLS-1$
	static final Object V_FUNC_AS_T = "AS t;"; //$NON-NLS-1$
	static final Object V_FUNC_QUOTE = "'"; //$NON-NLS-1$
	static final Object V_FUNC_DOUBLE_QUOTE = "\""; //$NON-NLS-1$
	static final Object V_FUNC_PASSING = "PASSING"; //$NON-NLS-1$
	static final Object V_FUNC_COLUMNS = "COLUMNS"; //$NON-NLS-1$
	static final Object V_FUNC_COMMA = ","; //$NON-NLS-1$
	static final Object V_FUNC_XML_ELEMENT = "XMLELEMENT"; //$NON-NLS-1$
	static final Object V_FUNC_NAME = "NAME"; //$NON-NLS-1$
	static final Object V_FUNC_AS_XML_OUT = "AS xml_out;"; //$NON-NLS-1$
	static final Object V_FUNC_XMLNAMESPACES = "XMLNAMESPACES"; //$NON-NLS-1$
	static final Object V_FUNC_AS = "AS"; //$NON-NLS-1$
	static final Object V_FUNC_DEFAULT = "DEFAULT"; //$NON-NLS-1$
	static final Object V_FUNC_RESULT = "_RESULT"; //$NON-NLS-1$
}
