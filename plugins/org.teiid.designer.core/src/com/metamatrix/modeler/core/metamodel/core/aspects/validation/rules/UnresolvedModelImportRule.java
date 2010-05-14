/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
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
public class UnresolvedModelImportRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate( EObject eObject,
                          ValidationContext context ) {
        CoreArgCheck.isInstanceOf(ModelImport.class, eObject);

        final ModelImport modelImport = (ModelImport)eObject;

        final String location = modelImport.getModelLocation();
        final Resource resource = eObject.eResource();
        if (resource == null || location == null) {
            ValidationResult result = new ValidationResultImpl(eObject, resource);
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR,
                                                                  ModelerCore.Util.getString("UnresolvedModelImportRule.0"), //$NON-NLS-1$
                                                                  getLocationPath(modelImport), getURIString(modelImport));
            result.addProblem(problem);
            context.addResult(result);
            return;
        }

        // Find the EMF Resource referenced by the model import ...
        final Container container = context.getResourceContainer();
        if (container != null) {
            final ResourceFinder finder = container.getResourceFinder();

            // Check to see if the import is to the Teiid Designer built-in datatypes
            // resource or to one of the Emf XMLSchema resources
            URI uri = URI.createURI(location);
            if (finder.isBuiltInResource(uri)) {
                return;
            }

            // Attempt to find the resource referenced by the ModelImport in the container
            Resource importResource = finder.findByImport(modelImport, true);
            if (importResource == null) {
                ValidationResult result = new ValidationResultImpl(eObject, eObject.eResource());
                String msg = ModelerCore.Util.getString("UnresolvedModelImportRule.The_model_import_cannot_be_resolved", location); //$NON-NLS-1$
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg, getLocationPath(modelImport),
                                                                      getURIString(modelImport));
                result.addProblem(problem);
                context.addResult(result);

                // jh fix for Defect 23067: added missing 'return'
                return;
            }

            // If the model location value found in the ModelImport is different than the
            // computed relative location, then the resource may have been moved or renamed
            URI resourceURI = resource.getURI();
            URI importURI = importResource.getURI();
            if (resourceURI.isFile() && importURI.isFile()) {
                boolean deresolve = (!resourceURI.isRelative() && resourceURI.isHierarchical());
                if (deresolve && !importURI.isRelative()) {
                    URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
                    if (deresolvedURI.hasRelativePath()) {
                        importURI = deresolvedURI;
                    }
                }

                if (!location.equals(URI.decode(importURI.toString()))) {
                    ValidationResult result = new ValidationResultImpl(eObject, eObject.eResource());
                    String msg = ModelerCore.Util.getString("UnresolvedModelImportRule.The_model_import_cannot_be_resolved", location); //$NON-NLS-1$
                    ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg, getLocationPath(modelImport),
                                                                          getURIString(modelImport));
                    result.addProblem(problem);
                    context.addResult(result);
                }
            }

            // final boolean containerIsModelContainer = isModelContainer(container);
            // if ( !containerIsModelContainer ) {
            // final URI uriFromImport = URI.createURI(path);
            // final Resource refedResource = container.getResource(uriFromImport, false);
            // if ( refedResource == null ) {
            // // Can't find referenced resource ...
            // ValidationResult result = new ValidationResultImpl(eObject,eObject.eResource());
            // Object[] params = new Object[]{path};
            //                    String msg = ModelerCore.Util.getString("UnresolvedModelImportRule.The_model_import_cannot_be_resolved",params); //$NON-NLS-1$
            // ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
            // result.addProblem(problem);
            // context.addResult(result);
            // }
            // return;
            // }
        }

        // // There either was no container or it was the model container, so check the workspace ...
        // IResource iResource = WorkspaceResourceFinderUtil.findIResource(path);
        //        if (iResource == null && !path.startsWith("http")) { //$NON-NLS-1$
        // ValidationResult result = new ValidationResultImpl(eObject,eObject.eResource());
        // Object[] params = new Object[]{path};
        //            String msg = ModelerCore.Util.getString("UnresolvedModelImportRule.The_model_import_cannot_be_resolved_in_the_workspace_1",params); //$NON-NLS-1$
        // ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
        // result.addProblem(problem);
        // context.addResult(result);
        // }
    }

    private static String getURIString( EObject eoj ) {
        return ModelerCore.getModelEditor().getUri(eoj).toString();
    }

    private static String getLocationPath( EObject eoj ) {
        return ModelerCore.getModelEditor().getModelRelativePath(eoj).toString();
    }
    // protected boolean isModelContainer( final Container container ) {
    // final Container modelContainer;
    // try {
    // modelContainer = ModelerCore.getModelContainer();
    // } catch (CoreException err) {
    // ModelerCore.Util.log(err);
    // return false;
    // }
    // return container == modelContainer;
    // }

}
