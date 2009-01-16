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

import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.2
 */
public class AnnotationExtensionAttributeDefaultValueRule implements ObjectValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EObject theObject,
                         ValidationContext theContext) {
        ArgCheck.isNotNull(theContext);
        ArgCheck.isInstanceOf(Annotation.class, theObject);

        Annotation annotation = (Annotation)theObject;
        
        if (annotation != null) {
            EMap tags = annotation.getTags();
            
            if ((tags != null) && !tags.isEmpty()) {
                EObject annotatedObj = annotation.getAnnotatedObject();
                
                // always should have an annotated object. if not just log it as the user can't fix it.
                if (annotatedObj == null) {
		            final IPath path = ModelerCore.getModelEditor().getModelRelativePath(annotation);
		            final String msg = ModelerCore.Util.getString("AnnotationExtensionAttributeDefaultValueRule.NullAnnotatedObject", //$NON-NLS-1$
		                                                          path);
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                    return;
                }
                
                try {
                    EObject temp = ModelerCore.getModelEditor().getExtension(annotatedObj);

                    // should be ObjectExtension but just in case
                    if (temp instanceof ObjectExtension) {
                        ObjectExtension extension = (ObjectExtension)temp;
                        Iterator itr = tags.keySet().iterator();
                        
                        // loop through all tags making sure they are valid.
                        // create problem for all bad values.
                        while (itr.hasNext()) {
                            Object propertyName = itr.next();
                            
                            if (!extension.isValid(propertyName)) {
        			            final ValidationResult result = new ValidationResultImpl(annotation, annotatedObj);
        			            Object name = ModelerCore.getModelEditor().getName(annotatedObj);
        			            
        			            if (name == null) {
        			                IPath path = ModelerCore.getModelEditor().getModelRelativePath(annotatedObj);
        			                name = path.toString();
        			            }
        			            
        			            final Object params = new Object[] {propertyName, tags.get(propertyName), name};
        			            final String msg = ModelerCore.Util.getString("AnnotationExtensionAttributeDefaultValueRule.ExtensionPropertyDefaultValueInvalid", //$NON-NLS-1$
        			                                                          params);
        			            final ValidationProblem problem  = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, msg);
        			            result.addProblem(problem);
        			            theContext.addResult(result);
                            }
                        }
                    }
                } catch (ModelerCoreException theException) {
                    ModelerCore.Util.log(theException);
                    
                    // create problem
		            final ValidationResult result = new ValidationResultImpl(annotation);
		            final IPath path = ModelerCore.getModelEditor().getModelRelativePath(annotation);
	                final String msg = ModelerCore.Util.getString("AnnotationExtensionAttributeDefaultValueRule.UnexpectedError", //$NON-NLS-1$
		                                                          path);
		            final ValidationProblem problem  = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, msg);
		            result.addProblem(problem);
		            theContext.addResult(result);
                }
            }
        }
    }

}
