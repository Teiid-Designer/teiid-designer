/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.types.JDBCSQLTypeInfo;
import com.metamatrix.core.util.CoreArgCheck;
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

    /**
     * Set of CAPITALIZED reserved words for checking whether a string is a reserved word.
     */
    private static final Map<String, Integer> SQL_TYPE_MAPPING = new HashMap<String, Integer>();

    // This is a poor man's enum.
    static {
        Field[] fields = Types.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == Integer.class) {
                try {
                    SQL_TYPE_MAPPING.put(field.getName(), field.getInt(null));
                } catch (Exception e) {
                }
            }
        }
    }
    
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

    private final DatatypeManager datatypeManager;

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
        this.datatypeManager = datatypeManager;
        if (this.datatypeManager == null) {
            final String msg = RelationalPlugin.Util.getString("RelationalTypeMapping.No_DatatypeManager"); //$NON-NLS-1$
            RelationalPlugin.Util.log(IStatus.ERROR, msg);
        }
    }

    protected EObject findDatatype( final String identifier ) throws ModelerCoreException {
        EObject result = this.datatypeManager.getBuiltInDatatype(identifier);
        if (result == null) {
            result = this.datatypeManager.findDatatype(identifier);
        }
        return result;
    }

    protected String getIdentifier( final EObject datatype ) {
        CoreArgCheck.isNotNull(datatype);
        final SqlAspect sqlAspect = (SqlAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(datatype, SqlAspect.class);
        if (sqlAspect == null) {
            return this.datatypeManager.getName(datatype);
        }
        if (sqlAspect instanceof SqlDatatypeAspect) {
            final SqlDatatypeAspect datatypeAspect = (SqlDatatypeAspect)sqlAspect;
            final String id = datatypeAspect.getDatatypeID(datatype);
            return id;
        }
        final Object id = sqlAspect.getObjectID(datatype);
        if (id != null) {
            return id.toString();
        }
        return null;
    }

    /**
     * Find the {@link Datatype} that corresponds to the supplied type name from a JDBC data source.
     * 
     * @param jdbcTypeName the name of the JDBC type
     * @return the Datatype that best corresponds to the JDBC type name
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public EObject getDatatype( final String jdbcTypeName ) throws ModelerCoreException {
    	EObject result = null;
        if (jdbcTypeName != null) {
        	Integer typeCode = SQL_TYPE_MAPPING.get(jdbcTypeName.toUpperCase());
	        if (typeCode != null) {
	        	result = getDatatype(typeCode);
	        }
        }
        if (result == null) {
            result = findDatatype(DatatypeConstants.BuiltInNames.OBJECT);
        }
        return result;
    }

    /**
     * Find the {@link Datatype} that corresponds to the supplied {@link java.sql.Types JDBC type}.
     * 
     * @param jdbcType the {@link Type JDBC type}
     * @return the Datatype that best corresponds to the JDBC type, or null if no Datatype could be found or if the type is
     *         ambiguous (such as {@link Types#OTHER}).
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public EObject getDatatype( final int jdbcType ) throws ModelerCoreException {
        if (jdbcType == Types.JAVA_OBJECT) {
        	return findDatatype(DatatypeConstants.BuiltInNames.OBJECT);
        }
        String typeName = JDBCSQLTypeInfo.getTypeName(jdbcType);
        String builtinName = DatatypeConstants.getDatatypeNamefromRuntimeType(typeName);
        if (builtinName == null || DatatypeConstants.BuiltInNames.OBJECT.equals(builtinName)) {
        	return null; //not a known sql type
        }
        return findDatatype(builtinName);
    }

    public SearchabilityType getSearchabilityType( final EObject datatype ) {
        if (datatype == null) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        EObject dt = datatype;
        while (dt != null && !this.datatypeManager.isBuiltInDatatype(dt)) {
            final EObject baseType = this.datatypeManager.getBaseType(dt);
            dt = this.datatypeManager.isSimpleDatatype(baseType) ? baseType : null;
        }

        final String typeName = this.datatypeManager.getName(dt);
        // These are SEARCHABLE
        if (DatatypeConstants.BuiltInNames.STRING.equals(typeName)) {
            return SearchabilityType.SEARCHABLE_LITERAL;
        }
        if (DatatypeConstants.BuiltInNames.CHAR.equals(typeName)) {
            return SearchabilityType.SEARCHABLE_LITERAL;
        }
        if (DatatypeConstants.BuiltInNames.CLOB.equals(typeName)) {
            return SearchabilityType.LIKE_ONLY_LITERAL; // per defect 10501
        }
        // These are UNSEARCHABLE ...
        if (DatatypeConstants.BuiltInNames.BLOB.equals(typeName)) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        if (DatatypeConstants.BuiltInNames.XML_LITERAL.equals(typeName)) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        if (DatatypeConstants.BuiltInNames.OBJECT.equals(typeName)) {
            return SearchabilityType.UNSEARCHABLE_LITERAL;
        }
        // The rest are numbers or dates ...
        return SearchabilityType.ALL_EXCEPT_LIKE_LITERAL;
    }

}
