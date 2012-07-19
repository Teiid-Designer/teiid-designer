/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.StructuralFeatureValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;
import org.teiid.designer.metamodels.core.extension.XAttribute;



/** 
 * @since 8.0
 */
public class XAttributeDefaultValueDatatypeRule implements StructuralFeatureValidationRule {

    /** 
     * @see org.teiid.designer.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject, java.lang.Object, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(EStructuralFeature theStructuralFeature,
                         EObject theObject,
                         Object theValue,
                         ValidationContext theContext) {
        CoreArgCheck.isInstanceOf(XAttribute.class, theObject);

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
