/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * This class finds all non-containment references to a specified EObject within a resource and removes or unsets those
 * references. This class is a simple visitor that examines all non-containment EReference features of the visited EObject looking
 * for references to the "refdObject". If one is found the reference is unset if the feature is single valued or the object is
 * removed if the feature is multivalued. This vistor will only clear references for the objects inside the resource being visited
 * and will not check the opposite end of any EReference for a reference to "refdObject".
 */
public class ClearEObjectReferences implements ModelVisitor {

    private final EObject refdObject;
    private final Set affectedObjects;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ClearEObjectReferences.
     */
    public ClearEObjectReferences( final EObject refdObject ) {
        ArgCheck.isNotNull(refdObject);
        this.refdObject = refdObject;
        this.affectedObjects = new HashSet();
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean visit( final EObject object ) {
        // Find all references ...
        final EClass eclass = object.eClass();
        final Collection allRefs = eclass.getEAllReferences();

        for (final Iterator i = allRefs.iterator(); i.hasNext();) {
            final EReference reference = (EReference)i.next();

            // Process only non-containment references ...
            if (!reference.isContainment() && !reference.isContainer() && !reference.isVolatile()) {
                final Object value = object.eGet(reference, false);

                if (reference.isMany()) {
                    // There may be many values ...
                    boolean removeRefdValue = false;
                    for (Iterator j = ((List)value).iterator(); j.hasNext();) {
                        final Object valueInList = j.next();
                        if (valueInList instanceof EObject && valueInList == refdObject) {
                            removeRefdValue = true;
                        }
                    }
                    if (removeRefdValue && reference.isChangeable()) {
                        ((List)value).remove(refdObject);
                        this.affectedObjects.add(object);
                    }

                } else {
                    // There may be 0..1 value ...
                    if (value instanceof EObject && value == refdObject) {
                        if (reference.isChangeable()) {
                            object.eUnset(reference);
                            this.affectedObjects.add(object);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean visit( Resource resource ) {
        return resource != null;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Return the collection of EObject instances that were affected by unsetting or removing one or more of their references to
     * the specified EObject
     * 
     * @return Returns the affectedObjects.
     * @since 4.3
     */
    public Collection getAffectedObjects() {
        return this.affectedObjects;
    }

}
