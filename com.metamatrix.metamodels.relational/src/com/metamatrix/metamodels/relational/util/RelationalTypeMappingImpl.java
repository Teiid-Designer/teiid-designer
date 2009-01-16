/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relational.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * This class provides a mapping between the built-in types and the {@link JDBC types}.
 */
public class RelationalTypeMappingImpl implements RelationalTypeMapping {

    private static RelationalTypeMapping instance;
    
    public static RelationalTypeMapping getInstance() {
        if (RelationalTypeMappingImpl.instance == null) {
            RelationalTypeMappingImpl.instance = new RelationalTypeMappingImpl();
        }
        return RelationalTypeMappingImpl.instance;
    }

    protected static DatatypeManager getStandardDatatypeManager() {
        try {
            return ModelerCore.getWorkspaceDatatypeManager();
        } catch (ModelerCoreRuntimeException e) {
            RelationalPlugin.Util.log(e);
        }
        return null;
    }


//    /**
//     * Method used to instantiate the {@link RelationalTypeMapping#INSTANCE}
//     * @return an instance of this type mapping
//     */
//    public static RelationalTypeMapping init() {
//        return new RelationalTypeMappingImpl();    
//    }
//
    /** Map from JDBC type name to built-in type name */
    private final Map jdbcToBuiltInType;
    /** Map from built-in type name to JDBC type name */
    private final Map builtInTypeToJdbc;
    /** Map from JDBC type integer to JDBC type name  */
    private final Map jdbcIntToJdbcName;
    
    /** Map from Datatype to JDBC type name  */
    private final Map datatypeToJdbcName;
    
    private final DatatypeManager datatypeManager;
    
    private boolean initialized;

    /**
     * Construct an instance of RelationalTypeMapping.
     */
    public RelationalTypeMappingImpl() {
        this(RelationalTypeMappingImpl.getStandardDatatypeManager());
    }
    
    /**
     * Construct an instance of RelationalTypeMapping.
     */
    public RelationalTypeMappingImpl( final DatatypeManager datatypeManager ) {
        super();
        this.jdbcToBuiltInType = new HashMap();
        this.builtInTypeToJdbc = new HashMap();
        this.jdbcIntToJdbcName = new HashMap();
        this.datatypeToJdbcName = new HashMap();
        this.datatypeManager = datatypeManager;
        if ( this.datatypeManager == null ) {
            final String msg = RelationalPlugin.Util.getString("RelationalTypeMapping.No_DatatypeManager"); //$NON-NLS-1$
            RelationalPlugin.Util.log(IStatus.ERROR,msg);
        }
    }
    
    protected void initialize() {
        if ( this.initialized ) {
            return;
        }
        // Set the initialized state right away, in case there are any exceptions later on
        // (we don't want to keep initializing over and over if there are problems).
        // Plus, the 'register' call also calls initialize, so we don't want it to be an infinite loop
        this.initialized = true;
        
        if ( this.datatypeManager == null ) {
            return;
        }

        //register(Types.ARRAY,         SQL_TYPE_NAMES.ARRAY,        internalFindDatatype(DatatypeConstants.BuiltInNames.) );
        register(Types.BIGINT,        SQL_TYPE_NAMES.BIGINT,       internalFindDatatype(DatatypeConstants.BuiltInNames.BIG_INTEGER) );
        register(Types.BINARY,        SQL_TYPE_NAMES.BINARY,       internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        register(Types.BIT,           SQL_TYPE_NAMES.BIT,          internalFindDatatype(DatatypeConstants.BuiltInNames.SHORT) );
        register(Types.BLOB,          SQL_TYPE_NAMES.BLOB,         internalFindDatatype(DatatypeConstants.BuiltInNames.BLOB) );
        register(Types.CHAR,          SQL_TYPE_NAMES.CHAR,         internalFindDatatype(DatatypeConstants.BuiltInNames.CHAR) );
        register(Types.CLOB,          SQL_TYPE_NAMES.CLOB,         internalFindDatatype(DatatypeConstants.BuiltInNames.CLOB) );
        register(Types.DATE,          SQL_TYPE_NAMES.DATE,         internalFindDatatype(DatatypeConstants.BuiltInNames.DATE) );
        register(Types.DECIMAL,       SQL_TYPE_NAMES.DECIMAL,      internalFindDatatype(DatatypeConstants.BuiltInNames.DOUBLE) );
        //register(Types.DISTINCT,      SQL_TYPE_NAMES.DISTINCT,     internalFindDatatype(DatatypeConstants.BuiltInNames.) );
        register(Types.DOUBLE,        SQL_TYPE_NAMES.DOUBLE,       internalFindDatatype(DatatypeConstants.BuiltInNames.DOUBLE) );
        register(Types.NUMERIC,       SQL_TYPE_NAMES.NUMERIC,      internalFindDatatype(DatatypeConstants.BuiltInNames.INTEGER) );
        register(Types.INTEGER,       SQL_TYPE_NAMES.INTEGER,      internalFindDatatype(DatatypeConstants.BuiltInNames.INT) );
        register(Types.FLOAT,         SQL_TYPE_NAMES.FLOAT,        internalFindDatatype(DatatypeConstants.BuiltInNames.FLOAT) );
        register(Types.LONGVARBINARY, SQL_TYPE_NAMES.LONGVARBINARY,internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        register(Types.LONGVARCHAR,   SQL_TYPE_NAMES.LONGVARCHAR,  internalFindDatatype(DatatypeConstants.BuiltInNames.STRING) );
        register(NO_INT_TYPE,         SQL_TYPE_NAMES.NCHAR,        internalFindDatatype(DatatypeConstants.BuiltInNames.STRING) );
        register(NO_INT_TYPE,         SQL_TYPE_NAMES.NTEXT,        internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        //register(Types.NULL,          SQL_TYPE_NAMES.NULL,         internalFindDatatype(DatatypeConstants.BuiltInNames.) );
        register(Types.NUMERIC,       SQL_TYPE_NAMES.NUMERIC,      internalFindDatatype(DatatypeConstants.BuiltInNames.LONG) );
        register(Types.NUMERIC,       SQL_TYPE_NAMES.NUMERIC,      internalFindDatatype(DatatypeConstants.BuiltInNames.BIG_DECIMAL) );
        register(Types.OTHER,         SQL_TYPE_NAMES.OTHER,        internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        register(Types.REAL,          SQL_TYPE_NAMES.REAL,         internalFindDatatype(DatatypeConstants.BuiltInNames.DOUBLE) );
        register(Types.REF,           SQL_TYPE_NAMES.REF,          internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        register(Types.SMALLINT,      SQL_TYPE_NAMES.SMALLINT,     internalFindDatatype(DatatypeConstants.BuiltInNames.SHORT) );
        register(Types.STRUCT,        SQL_TYPE_NAMES.STRUCT,       internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        register(Types.TIME,          SQL_TYPE_NAMES.TIME,         internalFindDatatype(DatatypeConstants.BuiltInNames.TIME) );
        register(Types.TIMESTAMP,     SQL_TYPE_NAMES.TIMESTAMP,    internalFindDatatype(DatatypeConstants.BuiltInNames.TIMESTAMP) );
        register(Types.TINYINT,       SQL_TYPE_NAMES.TINYINT,      internalFindDatatype(DatatypeConstants.BuiltInNames.SHORT) );
        register(Types.VARBINARY,     SQL_TYPE_NAMES.VARBINARY,    internalFindDatatype(DatatypeConstants.BuiltInNames.OBJECT) );
        register(Types.VARCHAR,       SQL_TYPE_NAMES.VARCHAR,      internalFindDatatype(DatatypeConstants.BuiltInNames.STRING) );
    }
    
    protected EObject internalFindDatatype( final String identifier ) {
        try {
            return this.datatypeManager.getBuiltInDatatype(identifier);
        } catch (ModelerCoreException e) {
            RelationalPlugin.Util.log(e);
        }
        return null;
    }
    
    protected EObject findDatatype( final String identifier ) throws ModelerCoreException {
        EObject result = this.datatypeManager.getBuiltInDatatype(identifier);
        if ( result == null ) {
            result = this.datatypeManager.findDatatype(identifier);
        }
        return result;
    }
    
    /**
     * Register the JDBC type name and datatype pair.  This method obtains the 
     * {@link SqlDatatypeAspect} for the supplied datatype, gets from it the 
     * {@link SqlDatatypeAspect#getDatatypeID(EObject) datatype ID}, and then
     * calls {@link #register(String, String) register(jdbcTypeName,datatype ID)}.
     * @param jdbcTypeInt the JDBC type constant (see {@link Types}),
     * or {@link #NO_INT_TYPE} if the type shouldn't be registered with an integer type
     * @param jdbcTypeName the name of the JDBC type; may not be null
     * @param datatype the {@link Datatype}; may not be null
     */
    public void register( final int jdbcTypeInt, final String jdbcTypeName, final EObject datatype ) {
        initialize();
        ArgCheck.isNotNull(jdbcTypeName);
        ArgCheck.isNotNull(datatype);
        final String identifier = getIdentifier(datatype);
        register(jdbcTypeInt,jdbcTypeName,identifier);
    }
    
    /**
     * Register the JDBC type name and datatype pair.
     * @param jdbcTypeInt the JDBC type constant (see {@link Types}),
     * or {@link #NO_INT_TYPE} if the type shouldn't be registered with an integer type
     * @param jdbcTypeName the name of the JDBC type; may not be null
     * @param datatypeUri the unique identifier for the {@link Datatype}; may not be null
     */
    public void register( final int jdbcTypeInt, final String jdbcTypeName, final String datatypeUri ) {
        initialize();
        ArgCheck.isNotNull(jdbcTypeName);
        ArgCheck.isNotNull(datatypeUri);
        final String jdbcUpperTypeName = jdbcTypeName.toUpperCase();
        this.jdbcToBuiltInType.put(jdbcUpperTypeName,datatypeUri);
        this.builtInTypeToJdbc.put(datatypeUri,jdbcUpperTypeName);
        if ( jdbcTypeInt != NO_INT_TYPE ) {
            this.jdbcIntToJdbcName.put(new Integer(jdbcTypeInt),jdbcUpperTypeName);
        }
    }
    
    protected String getIdentifier( final EObject datatype ) {
        ArgCheck.isNotNull(datatype);
        final SqlAspect sqlAspect = (SqlAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(datatype,SqlAspect.class);
        if ( sqlAspect == null ) {
            return this.datatypeManager.getName(datatype);
        }
        if ( sqlAspect instanceof SqlDatatypeAspect ) {
            final SqlDatatypeAspect datatypeAspect = (SqlDatatypeAspect)sqlAspect;
            final String id = datatypeAspect.getDatatypeID(datatype);
            return id;
        }
        final Object id = sqlAspect.getObjectID(datatype);
        if ( id != null ) {
            return id.toString();
        }
        return null;
    }
    
    /**
     * Find the {@link Datatype} that corresponds to the supplied type name from a JDBC data source.
     * @param jdbcTypeName the name of the JDBC type
     * @return the Datatype that best corresponds to the JDBC type name
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public EObject getDatatype( final String jdbcTypeName ) throws ModelerCoreException {
        initialize();
        EObject result = null;
        if ( jdbcTypeName != null ) {
            final String identifier = (String)this.jdbcToBuiltInType.get(jdbcTypeName.toUpperCase());
            if ( identifier != null ) {
                result = findDatatype(identifier);
            }
        }
        if ( result == null ) {
            result = findDatatype(DatatypeConstants.BuiltInNames.OBJECT);
        }
        return result;
    }
    
    /**
     * Find the {@link Datatype} that corresponds to the supplied {@link java.sql.Types JDBC type}.
     * @param jdbcType the {@link Type JDBC type}
     * @return the Datatype that best corresponds to the JDBC type, or null if no 
     * Datatype could be found or if the type is ambiguous (such as {@link Types#OTHER}).
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public EObject getDatatype( final int jdbcType ) throws ModelerCoreException {
        initialize();
        final String jdbcTypeName = (String)this.jdbcIntToJdbcName.get(new Integer(jdbcType));
        EObject result = null;
        if ( jdbcTypeName != null ) {
            result = getDatatype(jdbcTypeName);
        }
        return result;
    }
    
    /**
     * Find the name of the JDBC type that corresponds to the supplied datatype.  If there is not
     * corresponding JDBC type, this method obtains the base type for the supplied type and looks
     * for its corresponding JDBC type.  This process continues until either the 
     * @param type the datatype for which the corresponding JDBC type is to be found
     * @return the name of the JDBC type that best corresponds to the supplied type; never null
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public String getJdbcTypeName( final EObject type ) throws ModelerCoreException {
        ArgCheck.isNotNull(type);
        initialize();
        String name = (String)datatypeToJdbcName.get(type);
        if ( name == null ) {
            EObject theType = type;
            // Go up the hiearchy until we get to a built-in ...
            while ( name == null && theType != null ) {
                final String id = getIdentifier(theType);
                name = (String) this.builtInTypeToJdbc.get(id);
                if ( name == null && this.datatypeManager != null ) {
                    theType = this.datatypeManager.getBaseType(theType);
                    if ( theType == null ) {
                        break;
                    }
                }
            }
            if ( name == null ) {
                final EObject objType = findDatatype(DatatypeConstants.BuiltInNames.OBJECT);
                ArgCheck.isNotNull(objType);
                final String id = getIdentifier(objType);
                ArgCheck.isNotNull(id);
                name = (String) this.builtInTypeToJdbc.get(id);
            }
            ArgCheck.isNotNull(name);

            this.datatypeToJdbcName.put(type,name);
        }
        return name;
    }
    
    public SearchabilityType getSearchabilityType( final EObject datatype ) {
        if ( datatype == null ) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        initialize();
        EObject dt = datatype;
        while ( dt != null && !this.datatypeManager.isBuiltInDatatype(dt) ) {
            final EObject baseType = this.datatypeManager.getBaseType(dt);
            dt = this.datatypeManager.isSimpleDatatype(baseType) ? baseType : null;
        }
        
        final String typeName = this.datatypeManager.getName(dt);
        // These are SEARCHABLE
        if ( DatatypeConstants.BuiltInNames.STRING.equals(typeName) ) {
            return SearchabilityType.SEARCHABLE_LITERAL;
        }
        if ( DatatypeConstants.BuiltInNames.CHAR.equals(typeName) ) {
            return SearchabilityType.SEARCHABLE_LITERAL;
        }
        if ( DatatypeConstants.BuiltInNames.CLOB.equals(typeName) ) {
            return SearchabilityType.LIKE_ONLY_LITERAL;     // per defect 10501
        }
        // These are UNSEARCHABLE ...
        if ( DatatypeConstants.BuiltInNames.BLOB.equals(typeName) ) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        if ( DatatypeConstants.BuiltInNames.OBJECT.equals(typeName) ) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        // The rest are numbers or dates ...
        return SearchabilityType.ALL_EXCEPT_LIKE_LITERAL;
    }

}
