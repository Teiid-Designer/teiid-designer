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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;
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
public class XClassUniqueExtendedClassInXPackageRule implements ObjectValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(XPackage.class, eObject);

        final XPackage xpackage = (XPackage) eObject;
        final Resource resource = xpackage.eResource();
        if (resource == null) {
            return;
        }
        
        // Get the XClasses and check for duplicate names ...
        final Map xclassesByMetaclasses = new HashMap();
        final List xclasses = xpackage.getEClassifiers();
        final Iterator iter = xclasses.iterator();
        while (iter.hasNext()) {
            final EClassifier classifier = (EClassifier)iter.next();
            if ( classifier instanceof XClass ) {
                final XClass xclass = (XClass)classifier;
                final EClass extendedMetaclass = xclass.getExtendedClass();
                if ( extendedMetaclass != null ) {
                    // Look for (or create & add) the list of XClasses for the metaclass
                    List xclassesForMetaclass = (List) xclassesByMetaclasses.get(extendedMetaclass);
                    if ( xclassesForMetaclass == null ) {
                        xclassesForMetaclass = new ArrayList(5);
                        xclassesByMetaclasses.put(extendedMetaclass,xclassesForMetaclass);
                    }
                    // Add this XClass to the list ...
                    xclassesForMetaclass.add(xclass);
                }
            }
        }
        
        // Check for duplicate metaclasses ...
        final Iterator extendedClassIter = xclassesByMetaclasses.entrySet().iterator();
        while (extendedClassIter.hasNext()) {
            final Map.Entry entry = (Map.Entry)extendedClassIter.next();
            final List xclassesWithSameMetaclass = (List)entry.getValue();
            final int numDuplicates = xclassesWithSameMetaclass.size();
            if ( numDuplicates > 1 ) {
                // Iterate and create error messages for each ...
                final Integer numOthers = new Integer(numDuplicates-1);
                final Iterator xclassIter = xclassesWithSameMetaclass.iterator();
                while (xclassIter.hasNext()) {
                    final XClass xclass = (XClass)xclassIter.next();
                    final ValidationResult result = new ValidationResultImpl(xclass);
                    final Object[] params = new Object[]{numOthers};
                    final String msg = ModelerCore.Util.getString("XClassUniqueExtendedClassInXPackageRule.ExtendedClassHasSameMetaclassAs_n_Others",params); //$NON-NLS-1$
                    final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
                    result.addProblem(problem);
                    context.addResult(result);
                }
            }
            
        }
    }

}
