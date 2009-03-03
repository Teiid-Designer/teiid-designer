/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.uml2.uml.NamedElement;
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
 * PropertyAspect
 */
public class Uml2PropertyUmlAspect extends AbstractUml2NamedElementUmlAspect implements UmlProperty {

	/**
	 * Construct an instance of PropertyAspect.
	 * @param entity
	 */
	public Uml2PropertyUmlAspect(MetamodelEntity entity) {
		super(entity);
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    public boolean isAssociationEnd(Object eObject) {
        Property property = assertProperty(eObject);
        return (property.getAssociation() != null);
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
	 */
	@Override
    public IStatus setSignature(Object eObject, String newSignature) {
		try {
			final Property umlProperty = assertProperty(eObject);
			umlProperty.setName(newSignature);
		} catch (Throwable e) {
			return new Status(
				IStatus.ERROR,
				Uml2Plugin.PLUGIN_ID,
				0,
				e.getMessage(),
				e);
		}

		final String msg = Uml2Plugin.Util.getString("Uml2PropertyUmlAspect.Signature_changed_1"); //$NON-NLS-1$
		return new Status(IStatus.OK, Uml2Plugin.PLUGIN_ID, 0, msg, null);
	}
    
    /**
     * An attribute can be shown as a text string that can be parsed into the 
     * the various properties of an attribute.  The basic syntax is (with optional
     * parts shown in braces):
     * [visibility][/]name[:type][multiplicity][=default][{property-string}] 
     * In the following bullets, each of these parts is described:
     * <li>
     * visibility is a visibility symbol such as +, -, #, or ~.
     * / means the attribute is derived.
     * name is the name of the attribute.
     * type identifies a classifier that is the attribute�s type.
     * multiplicity shows the attribute�s multiplicity in square brackets. The term may be omitted when a multiplicity of 1 (exactly one) is to be assumed.
     * default is an expression for the default value or values of the attribute.
     * property-string indicates property values that apply to the attribute. The property string is optional (the braces are omitted if no properties are specified).
     * </li>
     * <p>
     * The following property strings can be applied to an attribute: {readOnly}, {union}, {subsets <property-name>}, 
     * {redefines <property-name>}, {ordered}, {bag}, {seq} or {sequence}, and {composite}. An attribute with the same 
     * name as an attribute that would have been inherited is interpreted to be a redefinition, without the need for 
     * a {redefines <x>} property string. Note that a redefined attribute is not inherited into a namespace where it is 
     * redefined, so its name can be reused in the featuring classifier, either for the redefining attribute, or 
     * alternately for some other attribute.
     * </p>
     * <p>
     * If the property represents an association end The following property strings can be applied:
     * <li>
     * {subsets <property-name>} to show that the end is a subset of the property called <property-name>.
     * {redefined <end-name>} to show that the end redefines the one named <end-name>.
     * {union} to show that the end is derived by being the union of its subsets.
     * {ordered} to show that the end represents an ordered set.
     * {bag} to show that the end represents a collection that permits the same element to appear more than once.
     * {sequence} or {seq} to show that the end represents a sequence (an ordered bag).
     * </li>
     * </p>
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
	public String getSignature(Object eObject, int showMask) {
		Property property = assertProperty(eObject);
		StringBuffer result = new StringBuffer();
		//case 16 is for properties, which should return an empty string, so 
		//it has been added in to the remaining cases where applicable.
		switch (showMask) {
			case 1 :
			case 17 :
				//Name
				appendVisibility(property, result);
				appendName(property, result);
				break;
			case 2 :
			case 18 :
				//Stereotype
				appendStereotype(property, result, true);
				break;
			case 3 :
			case 19 :
				//Name and Stereotype
				appendStereotype(property, result, true);
				appendName(property, result);
				break;
			case 4 :
			case 20 :
				//Type
				appendType(property, result, false);
				break;
			case 5 :
			case 21 :
				//Name and type
				appendVisibility(property, result);
                appendDerived(property,result);
				appendName(property, result);
				appendType(property, result, true);
                
                //mtkTODO remove additional notation strings
                appendMultiplicity(property, result);
                appendInitialValue(property, result, true);
                appendProperties(property, result);
				break;
			case 6 :
			case 22 :
				//Type and Stereotype
				appendStereotype(property, result, true);
				appendType(property, result, true);
				break;
			case 7 :
			case 23 :
				//Name, Stereotype and type
				appendStereotype(property, result, true);
				appendVisibility(property, result);
				appendName(property, result);
				appendType(property, result, true);
				break;
			case 8 :
			case 24 :
				//Initial Value
				appendInitialValue(property, result, false);
				break;
			case 9 :
			case 25 :
				//Name and Initial Value
				appendVisibility(property, result);
				appendName(property, result);
				appendInitialValue(property, result, true);
				break;
			case 10 :
			case 26 :
				//Initial Value and Stereotype
				appendStereotype(property, result, true);
				appendInitialValue(property, result, true);
				break;
			case 11 :
			case 27 :
				//Stereotype, Name and Initial Value, 
				appendStereotype(property, result, true);
				appendVisibility(property, result);
				appendName(property, result);
				appendInitialValue(property, result, true);
				break;
			case 12 :
			case 28 :
				//Initial Value and Type
				appendType(property, result, true);
				appendInitialValue(property, result, true);
				break;
			case 13 :
			case 29 :
				//Name, Type, InitialValue 
				appendVisibility(property, result);
				appendName(property, result);
				appendType(property, result, true);
				appendInitialValue(property, result, true);
				break;
			case 14 :
			case 30 :
				//Stereotype, Type and Initial Value
				appendStereotype(property, result, true);
				appendType(property, result, true);
				appendInitialValue(property, result, true);
				break;
			case 15 :
			case 31 :
				//Name, Stereotype, Type and Initial Value
				appendStereotype(property, result, true);
				appendVisibility(property, result);
				appendName(property, result);
				appendType(property, result, true);
				appendInitialValue(property, result, true);
				break;
			case 16 :
				//Properties
				return (""); //$NON-NLS-1$
			default :
                throw new MetaMatrixRuntimeException(Uml2Plugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
		}
		return result.toString();
	}

	protected Property assertProperty(Object eObject) {
		ArgCheck.isInstanceOf(Property.class, eObject);
		return (Property) eObject;
	}

	protected void appendName(final Property p, final StringBuffer sb) {
        ArgCheck.isNotNull(p);
		sb.append(p.getName());
	}

    protected void appendMultiplicity(final Property p, final StringBuffer result) {
        ArgCheck.isNotNull(p);
        final int lower = p.getLower();
        final int upper = p.getUpper();
        result.append('[');
        result.append(lower);
        if (upper != lower) {
            result.append(".."); //$NON-NLS-1$
            if (upper == -1) {
                result.append('*');
            } else {
                result.append(upper);
            }
        }
        result.append(']');
    }

    /**
     * An attribute can be shown as a text string that can be parsed into the 
     * the various properties of an attribute.  The basic syntax is (with optional
     * parts shown in braces):
     * [visibility][/]name[:type][multiplicity][=default][{property-string}] 
     * The / means the attribute is derived.
     * @param p
     * @param result
     */
    protected void appendDerived(final Property p, final StringBuffer result) {
        ArgCheck.isNotNull(p);
        if (p.isDerived()) {
            result.append('/');
        }
    }

    /**
     * The following property strings can be applied to an attribute: {readOnly}, {union}, {subsets <property-name>}, 
     * {redefines <property-name>}, {ordered}, {bag}, {seq} or {sequence}, and {composite}. An attribute with the same 
     * name as an attribute that would have been inherited is interpreted to be a redefinition, without the need for 
     * a {redefines <x>} property string. Note that a redefined attribute is not inherited into a namespace where it is 
     * redefined, so its name can be reused in the featuring classifier, either for the redefining attribute, or 
     * alternately for some other attribute.
     * <p>
     * If the property represents an association end The following property strings can be applied:
     * <li>
     * {subsets <property-name>} to show that the end is a subset of the property called <property-name>.
     * {redefined <end-name>} to show that the end redefines the one named <end-name>.
     * {union} to show that the end is derived by being the union of its subsets.
     * {ordered} to show that the end represents an ordered set.
     * {bag} to show that the end represents a collection that permits the same element to appear more than once.
     * {sequence} or {seq} to show that the end represents a sequence (an ordered bag).
     * </li>
     * </p>
     * @param p
     * @param result
     */
    protected void appendProperties(final Property p, final StringBuffer result) {
        ArgCheck.isNotNull(p);
        if (p.isReadOnly()) {
            result.append("{readOnly}"); //$NON-NLS-1$
        }
        if (p.isDerivedUnion()) {
            result.append("{union}"); //$NON-NLS-1$
        }
        if (p.isOrdered()) {
            result.append("{ordered}"); //$NON-NLS-1$
        }
        if (p.isComposite()) {
            result.append("{composite}"); //$NON-NLS-1$
        }
        if (!p.isUnique() && p.isMultivalued()) {
            if (p.isOrdered()) {
                result.append("{bag}"); //$NON-NLS-1$
            } else {
                result.append("{seq}"); //$NON-NLS-1$
            }
        }
        final List redefinedProps = p.getRedefinedProperties();
        if (redefinedProps != null && !redefinedProps.isEmpty()) {
            result.append('{');
            appendListAsCommaDelimitedString(redefinedProps,"redefines ",result); //$NON-NLS-1$
            result.append('}');
        }
        final List subsettedProps = p.getSubsettedProperties();
        if (subsettedProps != null && !subsettedProps.isEmpty()) {
            result.append('{');
            appendListAsCommaDelimitedString(subsettedProps,"subsets ",result); //$NON-NLS-1$
            result.append('}');
        }
    }

	protected void appendType(final Property p, final StringBuffer result, final boolean includePrefix) {
        ArgCheck.isNotNull(p);
		final Type type = p.getType();
		if (type != null) {
			if (includePrefix) {
				result.append(':');
			}
			result.append(type.getName());
		}
	}

	protected void appendInitialValue(final Property p, final StringBuffer result, final boolean includePrefix) {
        ArgCheck.isNotNull(p);
		final ValueSpecification defaultValue = p.getDefaultValue();
		if (defaultValue != null) {
			if (includePrefix) {
				result.append('=');
			}
			result.append(defaultValue.getName());
		}
	}

	protected void appendVisibility(final Property p, final StringBuffer result) {
        ArgCheck.isNotNull(p);
		VisibilityKind vk = p.getVisibility();
		if (vk.getValue() == VisibilityKind.PUBLIC) {
			result.append('+'); 
		} else if (vk.getValue() == VisibilityKind.PRIVATE) {
			result.append('-'); 
		} else if (vk.getValue() == VisibilityKind.PROTECTED) {
			result.append('#'); 
		} else if (vk.getValue() == VisibilityKind.PACKAGE) {
			result.append('~'); 
		}
	}
    
    protected boolean isAssociationEnd(final Property p) {
        ArgCheck.isNotNull(p);
        return p.getAssociation() != null;
    }
    
    protected void appendListAsCommaDelimitedString(final List values, final String prefix, final StringBuffer result) {
        if (values == null || values.size() == 0) {
            return;
        }
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            NamedElement ne = (NamedElement)iter.next();
            if (prefix != null) {
                result.append(prefix);
            }
            result.append(ne.getName());
            if (iter.hasNext()) {
                result.append(',');
            }
        }
    }

}
