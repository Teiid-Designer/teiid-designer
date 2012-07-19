/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import java.util.Iterator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.ObjectExtension;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.Annotation;



/** 
 * @since 8.0
 */
public class AnnotationExtensionAttributeDefaultValueRule implements ObjectValidationRule {

    /** 
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(EObject theObject,
                         ValidationContext theContext) {
        CoreArgCheck.isNotNull(theContext);
        CoreArgCheck.isInstanceOf(Annotation.class, theObject);

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
