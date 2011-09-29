/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.relational.RelationalPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * Handles basic datatype lookup for Teiid-supported Runtime types
 */
public class DatatypeProcessor {
    
    public static final String DATATYPE_STRING = "string"; //$NON-NLS-1$
    public static final String DATATYPE_VARCHAR = "varchar"; //$NON-NLS-1$
    public static final String DATATYPE_CHAR = "char"; //$NON-NLS-1$
    public static final String DATATYPE_BOOLEAN = "boolean"; //$NON-NLS-1$
    public static final String DATATYPE_BYTE = "byte"; //$NON-NLS-1$
    public static final String DATATYPE_TINYINT = "tinyint"; //$NON-NLS-1$
    public static final String DATATYPE_SHORT = "short"; //$NON-NLS-1$
    public static final String DATATYPE_SMALLINT = "smallint"; //$NON-NLS-1$ 
    public static final String DATATYPE_INTEGER = "integer"; //$NON-NLS-1$
    public static final String DATATYPE_SERIAL = "serial"; //$NON-NLS-1$
    public static final String DATATYPE_LONG = "long"; //$NON-NLS-1$
    public static final String DATATYPE_BIGINT = "bigint"; //$NON-NLS-1$
    public static final String DATATYPE_BIGINTEGER = "biginteger"; //$NON-NLS-1$
    public static final String DATATYPE_FLOAT = "float"; //$NON-NLS-1$
    public static final String DATATYPE_REAL = "real"; //$NON-NLS-1$
    public static final String DATATYPE_DOUBLE = "double"; //$NON-NLS-1$
    public static final String DATATYPE_BIGDECIMAL = "bigdecimal"; //$NON-NLS-1$
    public static final String DATATYPE_DECIMAL = "decimal"; //$NON-NLS-1$
    public static final String DATATYPE_DATE = "date"; //$NON-NLS-1$
    public static final String DATATYPE_TIME = "time"; //$NON-NLS-1$
    public static final String DATATYPE_TIMESTAMP = "timestamp"; //$NON-NLS-1$
    public static final String DATATYPE_OBJECT = "object"; //$NON-NLS-1$
    public static final String DATATYPE_BLOB = "blob"; //$NON-NLS-1$
    public static final String DATATYPE_CLOB = "clob"; //$NON-NLS-1$
    public static final String DATATYPE_XML = "xml"; //$NON-NLS-1$
    
    
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    public static final int DEFAULT_DATATYPE_LENGTH = 255;
    
    private DatatypeManager datatypeManager;
    
    private EObject objectDatatype;
    
    public DatatypeProcessor() {
        super();
        this.datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    }
    
    /**
     * For a given datatype name or 'identifier', returns an <code>EObject</code> runtime datatype
     * 
     * @param identifier
     * @return
     * @throws ModelerCoreException
     */
    public EObject findDatatype( final String identifier ) {
        String convertedTypeName = identifier;
        
        if( DATATYPE_SERIAL.equalsIgnoreCase(identifier) )  {
            convertedTypeName = DATATYPE_INTEGER;
        } else if( DATATYPE_VARCHAR.equalsIgnoreCase(identifier) ) {
            convertedTypeName = DATATYPE_STRING;
        } else if( DATATYPE_TINYINT.equalsIgnoreCase(identifier) ) {
            convertedTypeName = DATATYPE_BYTE;
        } else if( DATATYPE_SMALLINT.equalsIgnoreCase(identifier) ) {
            convertedTypeName = DATATYPE_SHORT;
        } else if( DATATYPE_BIGINT.equalsIgnoreCase(identifier) ) {
            convertedTypeName = DATATYPE_LONG;
        } else if( DATATYPE_REAL.equalsIgnoreCase(identifier) ) {
            convertedTypeName = DATATYPE_FLOAT;
        } else if( DATATYPE_XML.equalsIgnoreCase(identifier) ) {
            convertedTypeName = DATATYPE_OBJECT;
        }
        
        EObject result = null;
        
        try {
            result = this.datatypeManager.getBuiltInDatatype(convertedTypeName);
            if (result == null) {
                result = this.datatypeManager.findDatatype(convertedTypeName);
            }
        } catch (ModelerCoreException e) {
            String message = RelationalPlugin.Util.getString("DatatypeProcessor.Error_finding_datatatype", convertedTypeName); //$NON-NLS-1$
            RelationalPlugin.Util.log(IStatus.WARNING, message);
            result = getObjectDatatype();
        }
        return result;
    }
    
    private EObject getObjectDatatype() {
        if( this.objectDatatype == null ) {
            try {
                objectDatatype = this.datatypeManager.getBuiltInDatatype(DATATYPE_OBJECT);
            } catch (ModelerCoreException e) {
                String message = RelationalPlugin.Util.getString("DatatypeProcessor.Error_finding_datatatype", DATATYPE_OBJECT); //$NON-NLS-1$
                RelationalPlugin.Util.log(IStatus.WARNING, message);
            }
        }
        
        return this.objectDatatype;
    }
}
