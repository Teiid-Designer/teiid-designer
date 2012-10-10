/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.validation.rules;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.validation.StructuralFeatureValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;



/** 
 * @since 8.0
 */
public class ResourceInScopeValidationRule implements StructuralFeatureValidationRule {
    
    /** 
     * @see org.teiid.designer.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject, java.lang.Object, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(EStructuralFeature theStructuralFeature,
                         EObject theObject,
                         Object theValue,
                         ValidationContext theContext) {
        
        if ((theStructuralFeature instanceof EReference) && (theValue != null)) {
            if (theValue instanceof EObject) {
                validateReferenceValue(theStructuralFeature, theObject, (EObject)theValue, theContext);
            } else if (theValue instanceof Collection) {
                Collection theValues = (Collection)theValue;
                for (final Iterator iter = theValues.iterator(); iter.hasNext();) {
                    final EObject value = (EObject)iter.next();
                    validateReferenceValue(theStructuralFeature, theObject, value, theContext);
                }
                
            }
        }
    }
    
    private void validateReferenceValue(EStructuralFeature theStructuralFeature,
                                       EObject theObject,
                                       EObject theValue,
                                       ValidationContext theContext) {
        if (theValue != null) {
            Resource resource = theValue.eResource();
            
            // valid if EObject resource is the same as the reference resource
            if ((resource != null) && theObject.eResource().equals(resource)) {
                return;
            }
            
            // proxy won't have a resource so resolve
            if ((resource == null) && theValue.eIsProxy()) {
                final Container container = ModelerCore.getContainer(theObject);
                                               
//              MyDefect : 16368 added code to find the resource by URI                                
                URI valueURI  = ((InternalEObject)theValue).eProxyURI().trimFragment();
                if (valueURI.isRelative() && valueURI.hasRelativePath()){
                    URI objectURI = theObject.eResource().getURI();
                    URI resolvedURI = valueURI.resolve(objectURI);
                    resource = container.getResource(resolvedURI, false);
                }
                
                if (resource == null) {                
                    resource = ModelerCore.getModelEditor().findResource(container,theValue);
                }
            }

            // eFactories will not have a resource
            if (EcorePackage.eINSTANCE.getEPackage_EFactoryInstance() == theStructuralFeature) {
                return;
            }
            
            // validation error if unresolved resource
            if (resource == null) {
                String uri = theValue.eIsProxy() ? ((InternalEObject)theValue).eProxyURI().trimFragment().toString() : theValue.toString();
                String msg = ModelerCore.Util.getString(I18nUtil.getPropertyPrefix(this) + "unresolvedReference", uri); //$NON-NLS-1$
                ValidationProblem problem  = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, msg);
                ValidationResult result = new ValidationResultImpl(theObject);        
                result.addProblem(problem);
                theContext.addResult(result); 
            } else {
                // valid if global resource
                if (WorkspaceResourceFinderUtil.isGlobalResource(resource.getURI().toString())) {
                    return;
                }
                
                // if resource scope is defined, ensure this resource is one of the valid resources
                Resource[] validResources = theContext.getResourcesInScope();
                
                // scope defined
                if (validResources.length > 0) {
                    boolean valid = false;
    
                    for (int i = 0; i < validResources.length; ++i) {
                        if (validResources[i].equals(resource)) {
                            valid = true;
                            break;
                        }
                    }
                    
                    // found out of scope resource
                    if (!valid) {
                        String wsUri = WorkspaceResourceFinderUtil.getWorkspaceUri(resource);
                        String uri   = (wsUri != null ? wsUri : resource.getURI().toString());
                        String msg = ModelerCore.Util.getString(I18nUtil.getPropertyPrefix(this) + "resourceOutOfScope", uri);   //$NON-NLS-1$
                        ValidationProblem problem  = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, msg);
                        ValidationResult result = new ValidationResultImpl(theObject);        
                        result.addProblem(problem);
                        theContext.addResult(result); 
                    }
                }
            }
        }
    }
    
    

}
