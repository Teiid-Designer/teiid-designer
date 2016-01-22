/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc.wsdl;

/**
 *
 */
public interface IWsdlConstants {

    String KEY_REQUEST_PROCEDURE_NAME = "requestProcedureName"; //$NON-NLS-1$
    String KEY_RESPONSE_PROCEDURE_NAME = "responseProcedureName"; //$NON-NLS-1$
    String KEY_WRAPPER_PROCEDURE_NAME = "wrapperProcedureName"; //$NON-NLS-1$
    String SQL_BEGIN = "BEGIN\n"; //$NON-NLS-1$
    String SQL_END = "\nEND"; //$NON-NLS-1$
    String REQUEST = "REQUEST"; //$NON-NLS-1$
    String RESPONSE = "RESPONSE"; //$NON-NLS-1$
    String REQUEST_LOWER = "request"; //$NON-NLS-1$
    String RESPONSE_LOWER = "response"; //$NON-NLS-1$
    String TABLE_EXEC = "TABLE(EXEC "; //$NON-NLS-1$
    String XMI_EXTENSION = ".xmi"; //$NON-NLS-1$
    String RESULT_LOWER = "result"; //$NON-NLS-1$
    String INVOKE_SEGMENT_1 = "invoke('"; //$NON-NLS-1$
    String INVOKE_SEGMENT_2 = "', null, REQUEST.xml_out, null, TRUE))"; //$NON-NLS-1$
    String NULL_LOWER = "null"; //$NON-NLS-1$
    String XSI_NAMESPACE_PREFIX = "xsi"; //$NON-NLS-1$
    int TYPE_BODY = 0;
    int TYPE_HEADER = 1;
    Object XML_OUT = "xml_out"; //$NON-NLS-1$
    
    enum ProcedureType {
        REQUEST,
        RESPONSE,
        BOTH
    }

}
