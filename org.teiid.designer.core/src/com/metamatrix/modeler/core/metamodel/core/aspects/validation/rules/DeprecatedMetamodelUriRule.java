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
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.3
 */
public class DeprecatedMetamodelUriRule implements ObjectValidationRule {


    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isNotNull(context);
        CoreArgCheck.isInstanceOf(ModelAnnotation.class, eObject);
        
        final ModelAnnotation annot = (ModelAnnotation)eObject;
        MetamodelRegistry registry = ModelerCore.getMetamodelRegistry();
        if (registry != null) {
            // Check if the primary metamodel URI stored in the ModelAnnotation is the same
            // as the current metamodel URI found in the registry.  If it is not, the URI 
            // in the ModelAnnotation must be some older deprecated metamodel URI.  We need
            // to warn the user that their model must be resaved to update the metamodel
            // namespaces/URIs to their most current values.  The only impact of having a
            // deprecated metamodel namespace/URI in the model file is that the DTC shredder
            // will not be able to look up the EClass so it will not be loaded into the DTC.
            // See defect 18269.
            String primaryMetamodelUri = annot.getPrimaryMetamodelUri();
            URI metamodelUri = registry.getURI(primaryMetamodelUri);
            
            if (metamodelUri != null) {
                MetamodelDescriptor descriptor = registry.getMetamodelDescriptor(metamodelUri);
                if ( descriptor != null && !primaryMetamodelUri.equals(descriptor.getNamespaceURI()) ) {
                    
                    // Since the primary metamodel URI in the model file is out-of-date with the
                    // current metamodel URIs, warn that the model needs to be resaved
                    Object[] params = new Object[] {primaryMetamodelUri,descriptor.getNamespaceURI()};
                    String msg = ModelerCore.Util.getString("DeprecatedMetamodelUriRule.metamodelURI_has_changed_please_resave_model",params); //$NON-NLS-1$
                    final ValidationResult result = new ValidationResultImpl(eObject);
                    // create validation problem and addit to the result
                    final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING, msg);
                    result.addProblem(problem);
                    context.addResult(result);
                }
            }
            
            // NOTE:  This validation rule only checks the primary metamodel URI stored in the 
            //        ModelAnnotation instance.  The rule does not check all metamodel namespace
            //        URIs found in the XMI model file.  We may want to add this code at some
            //        point to catch changes to secondary metamodel URIs.
         }
    }

}
