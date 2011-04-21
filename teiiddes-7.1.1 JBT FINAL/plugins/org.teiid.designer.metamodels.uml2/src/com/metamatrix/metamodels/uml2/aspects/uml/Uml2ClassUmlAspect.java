/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Property;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.metamodels.uml2.util.Uml2Util;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;

/**
 * TableAspect
 */
public class Uml2ClassUmlAspect extends AbstractUml2NamedElementUmlAspect implements UmlClassifier {
    /**
     * Construct an instance of TableAspect.
     * @param entity
     */
    public Uml2ClassUmlAspect(MetamodelEntity entity){
        super(entity);
    }
    
    public Collection getRelationships(Object eObject) {
        final Class c = assertUmlClass(eObject);
        final Collection result = new ArrayList();
        // Iterate through the Classifier properties looking for association ends. 
        for (Iterator iter = c.getOwnedAttributes().iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element instanceof Property) {
                final Property p = (Property) element;
                final Association a = p.getAssociation();
                if( a != null ) {
                    result.add(a);
                }
            }
            
        }
        // Add any associations found in the model that have this class as an end point
        final Resource container = c.eResource();
        final Collection associations = Uml2Util.findAssocations(container,ModelVisitorProcessor.DEPTH_INFINITE);
        for (Iterator iter = associations.iterator(); iter.hasNext();) {
            final Association a  = (Association)iter.next();
            final List ownedEnds = a.getOwnedEnds();
            for (Iterator iterator = ownedEnds.iterator(); iterator.hasNext();) {
                Property aEnd = (Property)iterator.next();
                if (aEnd.getType() == c) {
                    result.add(a);
                }
            }
        }
        
        // Add any dependencies found in the model that have this class as an end point
        final Collection dependencies = Uml2Util.findDependencies(container,ModelVisitorProcessor.DEPTH_INFINITE);
        for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
            final Dependency d = (Dependency)iter.next();
            if (!d.getSources().contains(c) && !d.getTargets().contains(c)) {
                iter.remove();
            }
        }
        result.addAll(dependencies);
        
        return result;
    }

	public Collection getSupertypes(Object eObject) {
		final Class c = assertUmlClass(eObject);
        final Collection result = new ArrayList();
        result.addAll(c.getSuperClasses());
        result.addAll(c.getGeneralizations());
		return result;
	}
    
	@Override
    public String getStereotype(Object eObject) {
        final Class c = assertUmlClass(eObject);
        return super.getStereotype(c);
	}

    public String getSignature(Object eObject, int showMask) {
        final Class umlClass = assertUmlClass(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                appendName(umlClass,result,true);
                break;
            case 2 :
                //Stereotype
                appendStereotype(umlClass,result,true);
                appendName(umlClass,result,true);
                break;
            case 3 :
                //Name and Stereotype
                appendStereotype(umlClass,result,true);
                appendName(umlClass,result,true);
                break;
            default :
                final int params = showMask;
                final String msg = Uml2Plugin.Util.getString("Uml2ClassUmlAspect.Invalid_showMask_for_getSignature_0_1",params); //$NON-NLS-1$
                throw new TeiidRuntimeException(msg);
        }
        return result.toString();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object eObject) {
        final Class c = assertUmlClass(eObject);
        return c.isAbstract();
    }

    protected Class assertUmlClass(Object eObject) {
        CoreArgCheck.isInstanceOf(Class.class, eObject);
    
        return (Class)eObject;
    }

    protected void appendName( final Class c, final StringBuffer sb, final boolean upperCaseFirstLetter ) {
        super.appendName(c, sb, upperCaseFirstLetter);
//        if (c.isIsAbstract()) {
//            sb.append("{abstract}"); //$NON-NLS-1$
//        }
    }

}
