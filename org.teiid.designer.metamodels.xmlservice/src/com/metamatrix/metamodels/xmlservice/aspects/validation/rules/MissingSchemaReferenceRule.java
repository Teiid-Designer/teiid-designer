/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * GlobalXSDElementRule
 * 
 * @since 4.2
 */
public class MissingSchemaReferenceRule implements ObjectValidationRule {
    
    private static final String XML_LITERAL_TYPE_URI_STRING = 
        URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI).appendFragment(DatatypeConstants.BuiltInNames.XML_LITERAL).toString();

    /**
     * This rule validates that if there is a single XmlInput of type XMLLiteral then 
     * validation warning if no XSD component referenced
     * 
     * @since 5.1
     */
    public MissingSchemaReferenceRule() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 5.1
     */
    public void validate(final EObject eObject,
                         final ValidationContext context) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        ArgCheck.isNotNull(context);
        
        final XmlOperation op = (XmlOperation)eObject;
        boolean exposeXmlServiceInWsdl = false;
        
        // If the XmlOperation is configured such that it can be exposed as a web service ...
        
        // Make sure this is a virtual model. Source (Physical) models cannot be exposed as a web service.
        Resource eResource = op.eResource();
        if (eResource != null && 
        	eResource instanceof EmfResource &&
        	((EmfResource)eResource).getModelType() == ModelType.VIRTUAL_LITERAL){
        }else{
        	return;
        }
                
        
        if (op.getInputs().size() == 1) {
            XmlInput input = (XmlInput)op.getInputs().get(0);
            if (input != null && input.getType() != null) {
                XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)input.getType();
                if (type.eIsProxy() && XML_LITERAL_TYPE_URI_STRING.equals(((InternalEObject)type).eProxyURI().toString())) {
                    exposeXmlServiceInWsdl = true;
                } else if (XML_LITERAL_TYPE_URI_STRING.equals(type.getURI())) {
                    exposeXmlServiceInWsdl = true;
                }
            }
            
            // Check for XSD element references on XmlInput and XmlOutput
            if (exposeXmlServiceInWsdl) {

                // create a validation result
                final ValidationResult result = new ValidationResultImpl(eObject);

                // get the content element for the XmlInput
                if (input != null && input.getContentElement() == null) {
                    String msg = XmlServiceMetamodelPlugin.Util.getString("MissingSchemaReferenceRule.InputXSDElementIsNotGlobal"); //$NON-NLS-1$
                    result.addProblem(new ValidationProblemImpl(0, IStatus.WARNING, msg));
                }

                // get the content element for the XmlOutput
                if (op.getOutput() != null && op.getOutput().getContentElement() == null) {
                    String msg = XmlServiceMetamodelPlugin.Util.getString("MissingSchemaReferenceRule.OutputXSDElementIsNotGlobal"); //$NON-NLS-1$
                    result.addProblem(new ValidationProblemImpl(0, IStatus.WARNING, msg));
                }

                // add the results to the context
                if (result.hasProblems()) {
                    context.addResult(result);
                }
                
            }
            
        }

    }
}
