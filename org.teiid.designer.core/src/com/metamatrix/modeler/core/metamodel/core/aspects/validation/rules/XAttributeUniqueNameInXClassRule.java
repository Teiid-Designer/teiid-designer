/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.2
 */
public class XAttributeUniqueNameInXClassRule implements ObjectValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(XClass.class, eObject);

        final XClass xclass = (XClass) eObject;
        
        // Get the XClasses and check for duplicate names ...
        final Map xattributesByName = new HashMap();
        final List xattributes = xclass.getEAllAttributes();
        
        if ( xattributes.isEmpty() ) {
            final ValidationResult result = new ValidationResultImpl(xclass);
            final String msg = ModelerCore.Util.getString("XAttributeUniqueNameInXClassRule.NoAttributeDefined"); //$NON-NLS-1$
            final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
            result.addProblem(problem);
            context.addResult(result);
            return;
        }
        
        final Iterator iter = xattributes.iterator();
        while (iter.hasNext()) {
            final EAttribute attribute = (EAttribute)iter.next();
            if ( attribute instanceof XClass ) {
                final XAttribute xattribute = (XAttribute)attribute;

                // Look for (or create & add) the list of XClasses for the name
                final String name = xattribute.getName();
                if ( name != null ) {
                    List xattributesForName = (List) xattributesByName.get(name);
                    if ( xattributesForName == null ) {
                        xattributesForName = new ArrayList(5);
                        xattributesByName.put(name,xattributesForName);
                    }
                    // Add this XClass to the list ...
                    xattributesForName.add(xattribute);
                }
            }
        }
        
        // Check for duplicate names ...
        final Iterator nameIter = xattributesByName.entrySet().iterator();
        while (nameIter.hasNext()) {
            final Map.Entry entry = (Map.Entry)nameIter.next();
            final List xattributesWithSameName = (List)entry.getValue();
            final int numDuplicates = xattributesWithSameName.size();
            if ( numDuplicates > 1 ) {
                // Iterate and create error messages for each ...
                final Integer numOthers = new Integer(numDuplicates-1);
                final Iterator xattributeIter = xattributesWithSameName.iterator();
                while (xattributeIter.hasNext()) {
                    final XAttribute xattribute = (XAttribute)xattributeIter.next();
                    final ValidationResult result = new ValidationResultImpl(xattribute);
                    final Object[] params = new Object[]{numOthers};
                    final String msg = ModelerCore.Util.getString("XAttributeUniqueNameInXClassRule.AttributeHasSameNameAs_n_Others",params); //$NON-NLS-1$
                    final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
                    result.addProblem(problem);
                    context.addResult(result);
                }
            }
            
        }
    }

}
