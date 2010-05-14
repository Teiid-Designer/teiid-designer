/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDSimpleTypeDefinition.Assessment;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.aspects.sql.ProcedureParameterAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * @since 5.5
 */
public class ParameterValueValidator implements
                                    DqpUiConstants {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    private static final IStatus EMPTY_STATUS = new Status(
                                                           IStatus.ERROR,
                                                           PLUGIN_ID,
                                                           IStatus.OK,
                                                           UTIL.getString(I18nUtil.getPropertyPrefix(ParameterValueValidator.class)
                                                                          + "emptyValue"), null); //$NON-NLS-1$

    private static final IStatus LENGTH_ERROR_STATUS = new Status(
                                                                  IStatus.ERROR,
                                                                  PLUGIN_ID,
                                                                  IStatus.OK,
                                                                  UTIL.getString(I18nUtil.getPropertyPrefix(ParameterValueValidator.class)
                                                                                 + "maxLengthExceeded"), null); //$NON-NLS-1$

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * This method checks to see if a parameter can have a <code>null</code> value. Valid parameter object types are SQL table
     * columns, procedure parameters, and XSD elements can be checked.
     * 
     * @param param the param being checked
     * @return <code>true</code> if the object can have a <code>null</code> value or if not a parameter type
     * @since 6.0.0
     */
    public static boolean canBeNull( EObject param ) {
        if (param instanceof XSDElementDeclaration) {
            return ((XSDElementDeclaration)param).isNillable();
        }

        SqlAspect aspect = SqlAspectHelper.getSqlAspect(param);

        if (aspect instanceof SqlColumnAspect) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)aspect;
            return (columnAspect.getNullType(param) == NullableType.NULLABLE);
        }

        if (aspect instanceof ProcedureParameterAspect) {
            ProcedureParameterAspect paramAspect = (ProcedureParameterAspect)aspect;
            return (paramAspect.getNullType(param) == NullableType.NULLABLE);
        }

        return false;
    }

    /**
     * Validates the value for the specified object. Empty or <code>null</code> values are not checked and will return
     * <code>null</code>.
     * 
     * @param object
     *            the object whose value is being validated
     * @param value
     *            the value being validated
     * @return <code>null</code> if value is valid or the status if not valid
     */
    public static IStatus isValidValue(EObject object,
                                       String value) {
        IStatus status = null;
        EObject dataType = null;
        
        if (value == null) {
            if (canBeNull(object)) {
                // null value is OK
                return null;
            }
            
            // must have a value so it is an error
            return EMPTY_STATUS;
        }

        if (CoreStringUtil.isEmpty(value)) {
            return EMPTY_STATUS;
        }

        if (object instanceof XSDElementDeclaration) {
            dataType = ((XSDElementDeclaration)object).getTypeDefinition();
        } else {
            SqlAspect aspect = SqlAspectHelper.getSqlAspect(object);

            if (aspect instanceof SqlColumnAspect) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)aspect;
                    
                // check length first since it is not checked by the datatype
                // note: for some types (like short) length defaults to zero
                if ((columnAspect.getLength(object) > 0) && (value.length() > columnAspect.getLength(object))) {
                    return LENGTH_ERROR_STATUS;
                }

                dataType = columnAspect.getDatatype(object);
            } else if (aspect instanceof ProcedureParameterAspect) {
                ProcedureParameterAspect paramAspect = (ProcedureParameterAspect)aspect;

                // check length first since it is not checked by the datatype
                // note: for some types (like short) length defaults to zero
                if ((paramAspect.getLength(object) > 0) && (value.length() > paramAspect.getLength(object))) {
                    return LENGTH_ERROR_STATUS;
                }

                dataType = paramAspect.getDatatype(object);
            }
        }

        if (dataType instanceof XSDSimpleTypeDefinition) {
            Assessment assessment = ((XSDSimpleTypeDefinition)dataType).assess(value);
            Collection<XSDDiagnostic> diagnostics = assessment.getLocalDiagnostics();

            if ((diagnostics != null) && !diagnostics.isEmpty()) {
                for (Iterator itr = diagnostics.iterator(); itr.hasNext();) {
                    XSDDiagnostic diagnostic = (XSDDiagnostic)itr.next();

                    if (diagnostic.getSeverity() == XSDDiagnosticSeverity.ERROR_LITERAL) {
                        String name = ModelerCore.getModelEditor().getName(object);

                        if (CoreStringUtil.isEmpty(name)) {
                            name = object.getClass().getSimpleName();
                        }

                        String param = UTIL.getString(I18nUtil.getPropertyPrefix(ParameterValueValidator.class) + "parameter"); //$NON-NLS-1$
                        String msg = MessageFormat.format(diagnostic.getMessage(), new Object[] {
                            param, name
                        });
                        status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, null);
                        break;
                    }
                }
            }
        }

        return status;
    }

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 5.5.3
     */
    private ParameterValueValidator() {
        super();
    }

}
