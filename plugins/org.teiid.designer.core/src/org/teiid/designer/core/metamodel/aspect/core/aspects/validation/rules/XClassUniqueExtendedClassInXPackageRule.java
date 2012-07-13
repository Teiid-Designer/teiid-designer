/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

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
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.extension.XClass;
import org.teiid.designer.metamodels.core.extension.XPackage;



/** 
 * @since 4.2
 */
public class XClassUniqueExtendedClassInXPackageRule implements ObjectValidationRule {

    /** 
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(XPackage.class, eObject);

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
