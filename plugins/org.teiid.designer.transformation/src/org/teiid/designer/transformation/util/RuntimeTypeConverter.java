/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.util;

import javax.lang.model.type.NullType;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * The <code>RuntimeTypeConverter</code> class reconciles the runtime types of 2 lists.
 * 
 * @author Dan Florian
 * @since 8.0
 * @version 1.0
 */
public class RuntimeTypeConverter {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** The name to use to describe the primary list if no <code>primaryObj</code> is null. */
    private String primaryName = "Primary"; //$NON-NLS-1$

    /** The object to use to describe the primary list of candidates. */
    private Object primaryObj;

    /** The name to use to describe the secondary list if <code>secondaryObj</code> is null. */
    private String secondaryName = "Secondary"; //$NON-NLS-1$

    /** The object to use to describe the secondary list of candidates. */
    private Object secondaryObj;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private String getName( boolean thePrimaryFlag ) {
        String result = (thePrimaryFlag) ? primaryName : secondaryName;
        Object obj = (thePrimaryFlag) ? primaryObj : secondaryObj;

        if (obj != null && obj instanceof EObject && TransformationHelper.isSqlColumn(obj)) {
            EObject eObj = (EObject)obj;
            SqlColumnAspect columnAspect = ((SqlColumnAspect)AspectManager.getSqlAspect(eObj));
            result = columnAspect.getFullName(eObj);
        }

        return result;
    }

    /**
     * Gets a name derived from either the list object being set or the list name.
     * 
     * @return the name associated with the primary candidate list
     * @see #setPrimaryObject(Object)
     * @see #setPrimaryName(String)
     */
    private String getPrimaryName() {
        return getName(true);
    }

    /**
     * Gets the runtime type associated with the given object.
     * 
     * @param theObj the object whose runtime type is being requested
     * @return the requested runtime type
     */
    public static String getRuntimeType( Object theObj ) {
        String type = null;
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        
        if (theObj instanceof EObject) {
            EObject eObj = (EObject)theObj;
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObj, true);

            if (TransformationHelper.isSqlColumn(theObj)) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObj);
                type = columnAspect.getRuntimeType(eObj);
            } else if (dtMgr.isSimpleDatatype((EObject)theObj)) {
                return dtMgr.getRuntimeTypeName(eObj);
            }
        } else if (theObj instanceof Expression) {
            Class objClass = ((Expression)theObj).getType();
            if (objClass == null) {
                type = service.getDataTypeName(NullType.class);
            } else {
                type = service.getDataTypeName(objClass);
            }
        } else {
            throw new IllegalArgumentException(
                                               "RuntimeTypeConverter.getRuntimeType:Object type cannot be determined for <" + theObj + ">."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return type;
    }

    /**
     * Gets a name derived from either the list object being set or the list name.
     * 
     * @return the name associated with the secondary candidate list
     * @see #setSecondaryObject(Object)
     * @see #setSecondaryName(String)
     */
    private String getSecondaryName() {
        return getName(false);
    }

    /**
     * Gets both list names with a delimeter between them.
     * 
     * @return a concatenation of the list names
     */
    @Override
    public String toString() {
        return getPrimaryName() + "<->" + getSecondaryName(); //$NON-NLS-1$
    }

}
