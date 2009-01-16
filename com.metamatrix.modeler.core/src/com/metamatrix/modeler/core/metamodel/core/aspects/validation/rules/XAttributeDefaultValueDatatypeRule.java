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

package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.2
 */
public class XAttributeDefaultValueDatatypeRule implements StructuralFeatureValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EStructuralFeature theStructuralFeature,
                         EObject theObject,
                         Object theValue,
                         ValidationContext theContext) {
        ArgCheck.isInstanceOf(XAttribute.class, theObject);

        if (theValue != null) {
	        final XAttribute xattribute = (XAttribute)theObject;
	        final EDataType type = (EDataType)xattribute.getEType();

	        if (type != null) {
		        if (theStructuralFeature.getFeatureID() == ExtensionPackage.XATTRIBUTE__DEFAULT_VALUE_LITERAL) {
			        final EPackage ePackage = type.getEPackage();
			        final EFactory factory = ePackage.getEFactoryInstance();
			        
			        try {
			            factory.createFromString(type, (String)theValue);
			        } catch (RuntimeException theException) {
			            final ValidationResult result = new ValidationResultImpl(xattribute);
			            final Object params = new Object[] {theValue, type.getName(), theStructuralFeature.getName()};
			            final String msg = ModelerCore.Util.getString("XAttributeValueDatatypeRule.DefaultValueCannotBeConvertedToCorrectDatatype", //$NON-NLS-1$
			                                                          params);
			            final ValidationProblem problem  = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, msg);
			            result.addProblem(problem);
			            theContext.addResult(result);
			        }
	            }
	        }
        }
    }

}
