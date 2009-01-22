/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ForeignKeyColumnsRule
 */
public class MissingModelImportRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(ModelAnnotation.class, eObject);

        final ModelAnnotation modelAnnot = (ModelAnnotation) eObject;
        final Resource resource = modelAnnot.eResource();
        if (resource == null) {
            return;
        }
        
        
        // Resolve the Resource referenced by each model import ...
        final Container container = context.getResourceContainer();
        Assertion.isNotNull(container);

        // Get the list of all unresolved resource locations for references inside the resource being validated
        final ResourceFinder finder = container.getResourceFinder();
        String[] unresolvedLocations = finder.findMissingImportLocations(resource);
        for (int i = 0; i != unresolvedLocations.length; ++i) {
            String pathToExternalResource = unresolvedLocations[i];
            Object[] params = new Object[]{pathToExternalResource};

            ValidationResult result = new ValidationResultImpl(eObject,eObject.eResource());
            String msg = ModelerCore.Util.getString("MissingModelImportRule.Missing_model_import_to_resource_0_4",params); //$NON-NLS-1$
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
            result.addProblem(problem);
            
            // do not validate the resource any more
            result.setFatalResource(true);
            context.addResult(result);
        }
        
        // Get the list of all resources referenced by the resource being validated
        List refs = Arrays.asList( finder.findReferencesFrom(resource, false, true) );
        
        // Check the existing model imports against the list of external resource
        // paths looking for imports that are missing or no longer needed.
        final List imports = new ArrayList( modelAnnot.getModelImports() );
        for (Iterator i = imports.iterator(); i.hasNext();) {
            final ModelImport modelImport = (ModelImport)i.next();

            // If the modelLocation in the ModelImport is a logical location to a built-in resource
            final String location = modelImport.getModelLocation();
            if ( !StringUtil.isEmpty(location) ) {
                URI uri = URI.createURI(location);
                if (finder.isBuiltInResource(uri)) {
                    continue;
                }
            }
            
            Resource importResource = finder.findByImport(modelImport, true);
            
            // Missing model import ...
            if (importResource == null) {
                Object[] params = new Object[]{location};

                ValidationResult result = new ValidationResultImpl(eObject,eObject.eResource());
                String msg = ModelerCore.Util.getString("MissingModelImportRule.Bad_model_import_to_resource_0_4",params); //$NON-NLS-1$
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg,
                                                                       getLocationPath(modelImport), getURIString(modelImport));
                result.addProblem(problem);
                
                // do not validate the resource any more
                result.setFatalResource(true);
                context.addResult(result);
                
            } else {
                
                // If the resourceURI for the external reference is to the same resource as the
                // one containing this ModelImport then this is really an internal reference.
                // A reference of this type may show up if an href appears in the model file
                // and cannot be resolved.  Unresolved references will be caught by the 
                // ResourceInScopeValidationRule.
                if (importResource == resource) {
                    continue;
                }
                
                // If the resource associated with the ModelImport is not in the list
                // of all resources referenced by the resource being validated, then
                // it is no longer needed
                if (!refs.contains(importResource)) {
                    Object[] params = new Object[]{location};

                    ValidationResult result = new ValidationResultImpl(eObject,modelImport);
                    String msg = ModelerCore.Util.getString("MissingModelImportRule.Model_import_0_is_no_longer_needed._3",params); //$NON-NLS-1$
                    ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING ,msg,
                                                                           getLocationPath(modelImport), getURIString(modelImport));
                    result.addProblem(problem);
                    context.addResult(result);
                }
                
            }
            
        }
    }
    
    private static String getURIString(EObject eoj) {
        return ModelerCore.getModelEditor().getUri(eoj).toString();
    }

    private static String getLocationPath(EObject eoj) {
        return ModelerCore.getModelEditor().getModelRelativePath(eoj).toString();    
    }

}
