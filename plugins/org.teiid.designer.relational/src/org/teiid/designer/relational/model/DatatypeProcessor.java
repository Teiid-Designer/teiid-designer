/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.metamodels.relational.util.RelationalTypeMappingImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * Handles basic datatype conversion from JDBC-type and native types to Teiid-supported Runtime types
 */
public class DatatypeProcessor {
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    public static final int DEFAULT_DATATYPE_LENGTH = 255;
    
    //private static final String NUMBER = "NUMBER"; //$NON-NLS-1$
    private static final String VARCHAR2_TYPE_NAME = "VARCHAR2"; //$NON-NLS-1$
    private static final String NVARCHAR2_TYPE_NAME = "NVARCHAR2"; //$NON-NLS-1$
    private static final String TIMESTAMP_TYPE_NAME = "TIMESTAMP("; //$NON-NLS-1$
    private static final String BIT = "BIT"; //$NON-NLS-1$
    private static final String BINARY = "BINARY"; //$NON-NLS-1$
    
    private static final String BOOLEAN_TYPE_NAME   = "BOOL"; //$NON-NLS-1$
    private static final String TEXT_TYPE_NAME = "TEXT"; //$NON-NLS-1$
    private static final String IMAGE_TYPE_NAME = "IMAGE"; //$NON-NLS-1$
    //private static final String CHAR_VARYING_TYPE_NAME = "CHARACTER VARYING"; //$NON-NLS-1$
    //private static final String VARCHAR_TYPE_NAME = "VARCHAR"; //$NON-NLS-1$
    //private static final String SERIAL_TYPE_NAME = "SERIAL"; //$NON-NLS-1$
    //private static final String SERIAL4_TYPE_NAME = "SERIAL4"; //$NON-NLS-1$
    //private static final String SERIAL8_TYPE_NAME = "SERIAL8"; //$NON-NLS-1$
    //private static final String BIGSERIAL_TYPE_NAME = "BIGSERIAL"; //$NON-NLS-1$
    //private static final int TEXT_TYPE_MAX_LENGTH = 4000;
    
    // NOTE from MS SQLServer 2000 Doc:
    //
    // "The IDENTITY property can be assigned to tinyint, smallint, int, bigint, decimal(p,0), or numeric(p,0) columns."
    //
    private static final String IDENTITY_STR = "identity"; //$NON-NLS-1$
    
    private RelationalTypeMapping typeMapping;
    private DatatypeManager datatypeManager;
    
    public DatatypeProcessor() {
        super();

        this.typeMapping = RelationalTypeMappingImpl.getInstance();
        this.datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    }
    
    /**
     * For a given datatype name or 'identifier', returns an <code>EObject</code> runtime datatype
     * 
     * @param identifier
     * @return
     * @throws ModelerCoreException
     */
    public EObject findDatatype( final String identifier ) throws ModelerCoreException {
        EObject result = this.datatypeManager.getBuiltInDatatype(identifier);
        if (result == null) {
            result = this.datatypeManager.findDatatype(identifier);
        }
        return result;
    }
    
    /**
     * Find the type given the supplied information. This method is called by the various <code>create*</code> methods, and is
     * currently implemented to use {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)} (by
     * name) for other types.
     * <p>
     * In general, this method should not be overridden, since it contains logic that calls other <code>findType</code> methods
     * that each perform less complex operations. Instead, it is usually easier and better to override one or more of the
     * <code>findType</code> methods.
     * </p>
     * 
     * @param type the {@link Types JDBC type} value
     * @param typeName the (potentially DBMS-specific) type name
     * @param precision the precision of the type
     * @param scale the scale of the type
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype, or null if no such type could be found
     */
    public EObject findType( final String typeName,
                                final int length,
                                final int precision,
                                final int scale,
                                final List problems ) {
        // If the type is NUMERIC and precision is non-zero, then look at the length of the column ...
        // (assume zero length means the length isn't known)
        EObject result = null;
        
        try {
            result = findDatatype(typeName);
        } catch (ModelerCoreException e) {
            String message = RelationalPlugin.Util.getString("DatatypeProcessor.Error_finding_datatatype", typeName); //$NON-NLS-1$
            RelationalPlugin.Util.log(IStatus.WARNING, message);
        }

        String convertedTypeName = typeName;
        
        if (typeName.equalsIgnoreCase(BIT) && precision > 1) {
            // BINARY is the closest type,
            // for mysql a long may also be a valid representation
            convertedTypeName = BINARY;
        }
        
        // Oracle 9i introduced the "timestamp" type name (with type=1111, or OTHER)
        if (convertedTypeName.startsWith(TIMESTAMP_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.TIMESTAMP, problems);
        }
        if (result != null) {
            return result;
        }
        
        // Map the Postgres type of "BOOL" to our built-in type of Boolean
        if (BOOLEAN_TYPE_NAME.equalsIgnoreCase(convertedTypeName)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BOOLEAN,problems);
        } else if (convertedTypeName.startsWith(TEXT_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.CLOB, problems);
        } else if (convertedTypeName.startsWith(IMAGE_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BLOB, problems);
        }
        if ( result != null ) {
            return result;
        }

        if (VARCHAR2_TYPE_NAME.equalsIgnoreCase(convertedTypeName) || NVARCHAR2_TYPE_NAME.equalsIgnoreCase(convertedTypeName)) {
            convertedTypeName = RelationalTypeMapping.SQL_TYPE_NAMES.VARCHAR;
        }
        
        // String make sure the name is trimmed
        String trimmedTypeName = convertedTypeName.trim();

        if (trimmedTypeName.toUpperCase().startsWith(TEXT_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.CLOB, problems);
        } else if (trimmedTypeName.toUpperCase().startsWith(IMAGE_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BLOB, problems);
        } else if( trimmedTypeName.endsWith(IDENTITY_STR) ) {
            int identIndex = trimmedTypeName.indexOf(IDENTITY_STR);
            String realType = trimmedTypeName.substring(0, identIndex).trim();
            result = findBuiltinType(realType, problems);
        }
        if (result != null) {
            return result;
        }

        // First look up by type code ...
        result = findType(convertedTypeName, problems);
        if (result != null) {
            return result;
        }

        // Still haven't found one, so look it up by name ...
        result = findType(typeName, problems);
        return result;
    }

    /**
     * Find the datatype by name.
     * 
     * @param jdbcTypeName the name of the JDBC (or DBMS) type
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype that is able to represent data with the supplied criteria, or null if no datatype could be found
     */
    private EObject findType( final String jdbcTypeName, final List problems ) {
        if (jdbcTypeName != null && this.typeMapping != null) {
            try {
                return this.typeMapping.getDatatype(jdbcTypeName);
            } catch (ModelerCoreException e) {
                final String msg = RelationalPlugin.Util.getString("DatatypeProcessor.Error_while_computing_datatype" + jdbcTypeName); //$NON-NLS-1$
                final IStatus status = new Status(
                                                  IStatus.ERROR,
                                                  RelationalPlugin.PLUGIN_ID,
                                                  0, msg, e);
                problems.add(status);
            }
        }
        return null;
    }

    /**
     * Find a built-in datatype by it's name.
     * 
     * @param builtinTypeName the identifier of the built-in datatype; see {@link DatatypeConstants.BuiltInNames}.
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype, or null if no such type could be found
     */
    private EObject findBuiltinType( final String typeName, final List problems ) {
        if (typeName != null && datatypeManager != null) {
            try {
                final EObject obj = datatypeManager.getBuiltInDatatype(typeName);
                if (obj != null) {
                    return obj;
                }
            } catch (ModelerCoreException e) {
                final String msg = RelationalPlugin.Util.getString("DatatypeProcessor.Error_while_computing_datatype" + typeName); //$NON-NLS-1$
                final IStatus status = new Status(
                                                  IStatus.ERROR,
                                                  RelationalPlugin.PLUGIN_ID,
                                                  0, msg, e);
                problems.add(status);
            }
        }
        return null;
    }

}
