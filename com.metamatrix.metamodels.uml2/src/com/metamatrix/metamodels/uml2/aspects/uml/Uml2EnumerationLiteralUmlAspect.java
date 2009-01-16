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

package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
/*
 * Copyright MetaMatrix 2000-2004.
 */
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.VisibilityKind;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.uml2.Uml2MetamodelConstants;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
/**
 * Enumeration Literal Aspect
 */
public class Uml2EnumerationLiteralUmlAspect
	extends AbstractUml2NamedElementUmlAspect implements UmlProperty {

	/**
     * Construct an instance of EnumerationLiteralAspect.
     * @param entity
	 */
	public Uml2EnumerationLiteralUmlAspect(MetamodelEntity entity) {
		super(entity);
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    public boolean isAssociationEnd(Object property) {
        return false;
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
	 */
	public String getSignature(Object eObject, int showMask) {
		EnumerationLiteral enumLiteral = assertEnumerationLiteral(eObject);
		StringBuffer result = new StringBuffer();
		//case 16 is for properties, which should return an empty string, so 
		//it has been added in to the remaining cases where applicable.
		switch (showMask) {
			case 1 :
			case 17:
				//Name
				appendVisibility(enumLiteral,result);
				appendName(enumLiteral,result);
				break;
			case 2 :
			case 18:
				//Stereotype
				appendStereotype(enumLiteral,result,true);
				break;
			case 3 :
			case 19:
				//Name and Stereotype
				appendStereotype(enumLiteral,result,true);
				appendName(enumLiteral,result);
				break;
			case 4 :
			case 20: 
				//Type
				break;
			case 5 :
			case 21:
				//Name and type
				appendName(enumLiteral,result);

				break;
			case 6 :
			case 22:
				//Type and Stereotype
				appendStereotype(enumLiteral,result,true);

				break;
			case 7 :
			case 23:
				//Name, Stereotype and type
				appendStereotype(enumLiteral,result,true);
				appendName(enumLiteral,result);

				break;
			case 8 :
			case 24:
				//Initial Value
				result.append(""); //$NON-NLS-1$
				break;
			case 9 :
			case 25:
				//Name and Initial Value
				appendName(enumLiteral,result);
				break;
			case 10 :
			case 26 :
				//Initial Value and Stereotype
				appendStereotype(enumLiteral,result,true);
				break;
			case 11 :
			case 27 :
				//Stereotype, Name and Initial Value, 
				appendStereotype(enumLiteral,result,true);
				appendName(enumLiteral,result);
				break;
			case 12 :
			case 28 :
				//Initial Value and Type
				break;
			case 13 :
			case 29 :
				//Name, Type, InitialValue 
				appendName(enumLiteral,result);
				break;
			case 14 :
			case 30 :
				//Stereotype, Type and Initial Value
				appendStereotype(enumLiteral,result,true);
				break;
			case 15 :
			case 31 :
				//Name, Stereotype, Type and Initial Value
				appendStereotype(enumLiteral,result,true);
				appendName(enumLiteral,result);
				break;
			case 16 :
				//Properties
				return (""); //$NON-NLS-1$
			default :
				throw new MetaMatrixRuntimeException(ModelerCore.Util.getString("Uml2EnumerationLiteralUmlAspect.Invalid_showMask_for_getSignature",showMask ));//$NON-NLS-1$
		}
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
	 */
	@Override
    public IStatus setSignature(Object eObject, String newSignature) {
		try {
			final EnumerationLiteral literal = assertEnumerationLiteral(eObject);
			literal.setName(newSignature);
		} catch (Throwable e) {
			return new Status(
				IStatus.ERROR,
				Uml2MetamodelConstants.PLUGIN_ID,
				0,
				e.getMessage(),
				e);
		}

		return new Status(IStatus.OK, Uml2MetamodelConstants.PLUGIN_ID, 0, Uml2Plugin.Util.getString("Uml2EnumerationLiteralUmlAspect.OK_1"), null); //$NON-NLS-1$
	}
	
	private EnumerationLiteral assertEnumerationLiteral(Object eObject) {
		ArgCheck.isInstanceOf(EnumerationLiteral.class, eObject);
		return (EnumerationLiteral)eObject;
	}

	private void appendName( final EnumerationLiteral umlEnumLiteral, final StringBuffer sb ) {
		sb.append(umlEnumLiteral.getName());
	}
    
	private void appendVisibility(
		final EnumerationLiteral enumLiteral,
		final StringBuffer result) {
		VisibilityKind vk = enumLiteral.getVisibility();
		if (vk.getValue() == VisibilityKind.PUBLIC) {
			result.append("+"); //$NON-NLS-1$
		} else if (vk.getValue() == VisibilityKind.PRIVATE) {
			result.append("-"); //$NON-NLS-1$
		} else if (vk.getValue() == VisibilityKind.PROTECTED) {
			result.append("#"); //$NON-NLS-1$
		}
	}

}
