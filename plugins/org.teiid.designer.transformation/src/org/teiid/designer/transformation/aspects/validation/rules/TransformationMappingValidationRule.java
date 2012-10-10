/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation.rules;

import java.util.Iterator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.mapping.Mapping;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.transformation.TransformationMapping;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * TransformationMappingValidationRule
 *
 * @since 8.0
 */
public class TransformationMappingValidationRule implements ObjectValidationRule {
    
    private static char DELIMITER = '.';

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(TransformationMapping.class, eObject);

        Mapping transMapping = (Mapping) eObject;
        String problemMessage = getProblem(transMapping);
        
        if(problemMessage != null) {
            ValidationResult validationResult = new ValidationResultImpl(eObject);
            // create validation problem and addit to the resuls
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, problemMessage);
            validationResult.addProblem(problem);
            context.addResult(validationResult);
        }
    }

    /**
     * Return a problem message after validating the Mapping object.
     */
    private String getProblem(Mapping transMapping) {
        EList outputs = transMapping.getOutputs();
        EList inputs = transMapping.getInputs();
        
        String modelName = getModelName(transMapping);
        
        // Check for unresolved proxies in the inputs
        for (Iterator iter = inputs.iterator(); iter.hasNext();) {
            EObject inputElement = (EObject)iter.next();
            if (inputElement.eIsProxy()) {
                Object[] params = new Object[]{modelName,((InternalEObject)inputElement).eProxyURI()};
                return TransformationPlugin.Util.getString("TransformationMappingValidationRule.Sql_transformation_in_the_model_contains_the_unresolved_reference_1",params); //$NON-NLS-1$
            }
        }
        
        String inputElements = getInputNames(inputs);        

        if(outputs.size() < 1) {
            if(!inputElements.equals(CoreStringUtil.Constants.EMPTY_STRING)) { 
                return TransformationPlugin.Util.getString("TransformationMappingValidationRule.Sql_transformation_in_the_model__1")+modelName+TransformationPlugin.Util.getString("TransformationMappingValidationRule._does_not_have_targets_mapped_for_source_element/s__2")+inputElements; //$NON-NLS-1$ //$NON-NLS-2$
            }
            return TransformationPlugin.Util.getString("TransformationMappingValidationRule.Sql_transformation_in_the_model__3")+modelName+TransformationPlugin.Util.getString("TransformationMappingValidationRule._does_not_have_target_elements._4"); //$NON-NLS-1$ //$NON-NLS-2$
        } else if(outputs.size() > 1) {
            if(!inputElements.equals(CoreStringUtil.Constants.EMPTY_STRING)) { 
                return TransformationPlugin.Util.getString("TransformationMappingValidationRule.Sql_transformation_in_the_model__5")+modelName+TransformationPlugin.Util.getString("TransformationMappingValidationRule._cannot_have_multiple_targets_mapped_for_source_element/s__6")+inputElements; //$NON-NLS-1$ //$NON-NLS-2$
            }
            return TransformationPlugin.Util.getString("TransformationMappingValidationRule.Sql_transformation_in_the_model__7")+modelName+TransformationPlugin.Util.getString("TransformationMappingValidationRule._cannot_have_multiple_target_elements._8"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return null;
    }

    /**
     * Get the model name of an EObject
     */
    private String getModelName(EObject eObj) {
        ModelEditor modelEditor = ModelerCore.getModelEditor();
        return modelEditor.getModelName(eObj).toString();
    }

    /**
     * Get the fully qualified name of an EObject
     */
    private String getName(EObject eObj) {
        ModelEditor modelEditor = ModelerCore.getModelEditor();
        String objectPath = modelEditor.getModelRelativePathIncludingModel(eObj).toString();

        return objectPath.replace(IPath.SEPARATOR, DELIMITER);
    }

    /**
     * Return a String containing fully qualified names of the EObjects in the given list.
     */
    private String getInputNames(EList inputs) {
        Iterator inputIter = inputs.iterator();
        StringBuffer buf = new StringBuffer();
        while(inputIter.hasNext()) {
           EObject inputElement = (EObject) inputIter.next();    
           String elementName = getName(inputElement);
           buf.append(elementName);
           if(inputIter.hasNext()) {
               buf.append(", "); //$NON-NLS-1$
           }
        }

        return buf.toString();
    }

}
