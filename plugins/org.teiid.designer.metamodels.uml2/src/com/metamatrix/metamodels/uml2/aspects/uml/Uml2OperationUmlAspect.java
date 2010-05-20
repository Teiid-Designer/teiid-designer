/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.VisibilityKind;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation;

/**
 * Operation UML syntax [visibility] name [(parameter-list)] [: return-type] [{property-string}]
 */
public class Uml2OperationUmlAspect extends AbstractUml2NamedElementUmlAspect implements UmlOperation {

    /**
     * @param entity
     */
    public Uml2OperationUmlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation#getOwnerClass(java.lang.Object)
     */
    public EObject getOwnerClass( Object eObject ) {
        Operation o = assertOperation(eObject);
        return o.getClass_();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation#getOwnedParameters(java.lang.Object)
     */
    public List getOwnedParameters( Object eObject ) {
        Operation o = assertOperation(eObject);
        return o.getOwnedParameters();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation#getRaisedExceptions(java.lang.Object)
     */
    public List getRaisedExceptions( Object eObject ) {
        Operation o = assertOperation(eObject);
        return o.getRaisedExceptions();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlOperation#getType(java.lang.Object)
     */
    public Object getType( Object eObject ) {
        Operation o = assertOperation(eObject);
        return o.getType();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    public String getSignature( Object eObject,
                                int showMask ) {
        Operation operation = assertOperation(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1:
                // Name
                appendVisibility(operation, result);
                appendName(operation, result);
                break;
            case 2:
                // Stereotype
                appendStereotype(operation, result, true);
                break;
            case 3:
                // Name and Stereotype
                appendStereotype(operation, result, true);
                appendVisibility(operation, result);
                appendName(operation, result);
                break;
            case 4:
                // ReturnType
                appendReturnType(operation, result, false);
                break;
            case 5:
                // Name and ReturnType
                appendVisibility(operation, result);
                appendName(operation, result);
                appendReturnType(operation, result, true);
                break;
            case 6:
                // Type and Stereotype
                appendStereotype(operation, result, true);
                appendReturnType(operation, result, true);
                break;
            case 7:
                // Name, Stereotype and type
                appendStereotype(operation, result, true);
                appendVisibility(operation, result);
                appendName(operation, result);
                appendReturnType(operation, result, true);
                break;
            case 8:
                // Parameters
                // appendInitialValue(operation,result,false);
                appendParameters(operation, result);
                break;
            case 9:
                // Name and Parameters Value
                appendVisibility(operation, result);
                appendName(operation, result);
                appendParameters(operation, result);
                // appendInitialValue(operation,result,true);
                break;
            case 10:
                // Parameters and Stereotype
                appendStereotype(operation, result, true);
                appendParameters(operation, result);
                break;
            case 11:
                // Stereotype, Name and Parameters,
                appendStereotype(operation, result, true);
                appendVisibility(operation, result);
                appendName(operation, result);
                appendParameters(operation, result);
                break;
            case 12:
                // Initial Value and ReturnType
                appendParameters(operation, result);
                appendReturnType(operation, result, true);
                break;
            case 13:
                // Name, Type, Parameters
                appendVisibility(operation, result);
                appendName(operation, result);
                appendParameters(operation, result);
                appendReturnType(operation, result, true);
                break;
            case 14:
                // Stereotype, Type and Parameters
                appendStereotype(operation, result, true);
                appendParameters(operation, result);
                appendReturnType(operation, result, true);
                break;
            case 15:
                // Name, Stereotype, Type and Parameters
                appendStereotype(operation, result, true);
                appendVisibility(operation, result);
                appendName(operation, result);
                appendReturnType(operation, result, true);
                appendParameters(operation, result);
                break;
            default:
                throw new TeiidRuntimeException(Uml2Plugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected Operation assertOperation( Object eObject ) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        return (Operation)eObject;
    }

    protected void appendName( final Operation umlOperation,
                               final StringBuffer sb ) {
        super.appendName(umlOperation, sb);
    }

    protected void appendReturnType( final Operation operation,
                                     final StringBuffer result,
                                     final boolean includePrefix ) {
        final List returnParams = this.getReturnParameters(operation);
        if (returnParams.size() == 1) {
            if (includePrefix) {
                result.append(':');
            }
            Parameter p = (Parameter)returnParams.get(0);
            result.append((p.getType().getName()));
        }
        // final Type type = operation.getType();
        // if (type != null) {
        // if (includePrefix) {
        //				result.append(':'); //$NON-NLS-1$
        // }
        // result.append(type.getName());
        // }
    }

    protected void appendVisibility( final Operation operation,
                                     final StringBuffer result ) {
        VisibilityKind vk = operation.getVisibility();
        if (vk.getValue() == VisibilityKind.PUBLIC) {
            result.append("+"); //$NON-NLS-1$
        } else if (vk.getValue() == VisibilityKind.PRIVATE) {
            result.append("-"); //$NON-NLS-1$
        } else if (vk.getValue() == VisibilityKind.PROTECTED) {
            result.append("#"); //$NON-NLS-1$
        } else if (vk.getValue() == VisibilityKind.PACKAGE) {
            result.append(" "); //$NON-NLS-1$
        }
    }

    protected void appendParameters( final Operation operation,
                                     final StringBuffer result ) {
        // final List params = operation.getParameter();
        final List params = this.getInputParameters(operation);
        result.append('(');
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Parameter p = (Parameter)iter.next();
            result.append(p.getName());
            if (iter.hasNext()) {
                result.append(',');
            }
        }
        result.append(')');
    }

    protected List getInputParameters( final Operation operation ) {
        final List params = operation.getOwnedParameters();
        final List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Parameter p = (Parameter)iter.next();
            if (p.getDirection() == ParameterDirectionKind.IN_LITERAL || p.getDirection() == ParameterDirectionKind.INOUT_LITERAL) {
                result.add(p);
            }

        }
        return result;
    }

    protected List getOutputParameters( final Operation operation ) { // NO_UCD
        final List params = operation.getOwnedParameters();
        final List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Parameter p = (Parameter)iter.next();
            if (p.getDirection() == ParameterDirectionKind.OUT_LITERAL
                || p.getDirection() == ParameterDirectionKind.INOUT_LITERAL) {
                result.add(p);
            }

        }
        return result;
    }

    protected List getReturnParameters( final Operation operation ) {
        final List params = operation.getOwnedParameters();
        final List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Parameter p = (Parameter)iter.next();
            if (p.getDirection() == ParameterDirectionKind.RETURN_LITERAL) {
                result.add(p);
            }

        }
        return result;
    }

}
