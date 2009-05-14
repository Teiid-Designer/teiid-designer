/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.VisibilityKind;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;

/**
 * ParameterAspect
 */
public class Uml2ParameterUmlAspect
	extends AbstractUml2NamedElementUmlAspect
	implements UmlProperty {
	/**
	 * Construct an instance of ParameterAspect.
	 * @param entity
	 */
	public Uml2ParameterUmlAspect(MetamodelEntity entity) {
		super(entity);
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    public boolean isAssociationEnd(Object property) {
        return false;
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
	 */
	@Override
    public IStatus setSignature(Object eObject, String newSignature) {
		try {
			final Parameter parameter = assertParameter(eObject);
			parameter.setName(newSignature);
		} catch (Throwable e) {
			return new Status(
				IStatus.ERROR,
				Uml2Plugin.PLUGIN_ID,
				0,
				e.getMessage(),
				e);
		}

		final String msg = Uml2Plugin.Util.getString("Uml2ParameterUmlAspect.Signature_changed_1"); //$NON-NLS-1$
		return new Status(IStatus.OK, Uml2Plugin.PLUGIN_ID, 0, msg, null);
	}

	public String getSignature(Object eObject, int showMask) {
		Parameter parameter = assertParameter(eObject);
		StringBuffer result = new StringBuffer();
		//case 16 is for properties, which should return an empty string, so 
		//it has been added in to the remaining cases where applicable.
		switch (showMask) {
			case 1 :
			case 17 :
				//Name
				appendVisibility(parameter, result);
				appendName(parameter, result);
				break;
			case 2 :
			case 18 :
				//Stereotype
				appendStereotype(parameter, result, true);
				break;
			case 3 :
			case 19 :
				//Name and Stereotype
				appendStereotype(parameter, result, true);
				appendName(parameter, result);
				break;
			case 4 :
			case 20 :
				//Type
				appendType(parameter, result, false);
				break;
			case 5 :
			case 21 :
				//Name and type
				appendVisibility(parameter, result);
				appendName(parameter, result);
				appendType(parameter, result, true);
				break;
			case 6 :
			case 22 :
				//Type and Stereotype
				appendStereotype(parameter, result, true);
				appendType(parameter, result, true);
				break;
			case 7 :
			case 23 :
				//Name, Stereotype and type
				appendStereotype(parameter, result, true);
				appendVisibility(parameter, result);
				appendName(parameter, result);
				appendType(parameter, result, true);
				break;
			case 8 :
			case 24 :
				//Initial Value
				appendInitialValue(parameter, result, false);
				break;
			case 9 :
			case 25 :
				//Name and Initial Value
				appendVisibility(parameter, result);
				appendName(parameter, result);
				appendInitialValue(parameter, result, true);
				break;
			case 10 :
			case 26 :
				//Initial Value and Stereotype
				appendStereotype(parameter, result, true);
				appendInitialValue(parameter, result, true);
				break;
			case 11 :
			case 27 :
				//Stereotype, Name and Initial Value, 
				appendStereotype(parameter, result, true);
				appendVisibility(parameter, result);
				appendName(parameter, result);
				appendInitialValue(parameter, result, true);
				break;
			case 12 :
			case 28 :
				//Initial Value and Type
				appendType(parameter, result, true);
				appendInitialValue(parameter, result, true);
				break;
			case 13 :
			case 29 :
				//Name, Type, InitialValue 
				appendVisibility(parameter, result);
				appendName(parameter, result);
				appendType(parameter, result, true);
				appendInitialValue(parameter, result, true);
				break;
			case 14 :
			case 30 :
				//Stereotype, Type and Initial Value
				appendStereotype(parameter, result, true);
				appendType(parameter, result, true);
				appendInitialValue(parameter, result, true);
				break;
			case 15 :
			case 31 :
				//Name, Stereotype, Type and Initial Value
				appendStereotype(parameter, result, true);
				appendVisibility(parameter, result);
				appendName(parameter, result);
				appendType(parameter, result, true);
				appendInitialValue(parameter, result, true);
				break;
			case 16 :
				//Properties
				return (""); //$NON-NLS-1$
			default :
                throw new MetaMatrixRuntimeException(Uml2Plugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
		}
		return result.toString();
	}

	protected Parameter assertParameter(Object eObject) {
		ArgCheck.isInstanceOf(Parameter.class, eObject);
		return (Parameter) eObject;
	}

	protected void appendName(
		final Property umlProperty,
		final StringBuffer sb) {
		sb.append(umlProperty.getName());
	}

	protected void appendType(
		final Parameter parameter,
		final StringBuffer result,
		final boolean includePrefix) {
		final Type type = parameter.getType();
		if (type != null) {
			if (includePrefix) {
				result.append(":"); //$NON-NLS-1$
			}
			result.append(type.getName());
		}
	}

	protected void appendInitialValue(
		final Parameter parameter,
		final StringBuffer result,
		final boolean includePrefix) {
		final ValueSpecification defaultValue = parameter.getDefaultValue();
		if (defaultValue != null) {
			if (includePrefix) {
				result.append("="); //$NON-NLS-1$
			}
			result.append(defaultValue.toString());
		}
	}

	protected void appendVisibility(
		final Parameter parameter,
		final StringBuffer result) {
		VisibilityKind vk = parameter.getVisibility();
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

}
