/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.util;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * @
 */
public abstract class AbstractNameFinder implements ModelVisitor {

    protected final String nameToMatch;
    protected Collection matchingEObjects;
    protected boolean isPartialName;

    public AbstractNameFinder( final String nameToMatch,
                               final boolean isPartialName ) {
        ArgCheck.isNotEmpty(nameToMatch);
        this.nameToMatch = nameToMatch.toUpperCase();
        this.isPartialName = isPartialName;
        this.matchingEObjects = new HashSet();
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean visit( final Resource resource ) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject eObject ) {
        // If the match was already found then do not visit this EObject
        if (!this.isPartialName) {
            if (!this.matchingEObjects.isEmpty()) {
                return false;
            }
        } else {
            if (this.matchingEObjects.contains(eObject)) {
                return false;
            }
        }

        return true;
    }

    protected boolean foundMatch( final String fullName,
                                  final EObject eObject ) {
        if (this.isPartialName) {
            if (fullName.endsWith(this.nameToMatch)) {
                this.matchingEObjects.add(eObject);
                return true;
            }
        } else {
            if (this.nameToMatch.equals(fullName)) {
                this.matchingEObjects.add(eObject);
                return true;
            }
        }

        return false;
    }

    protected boolean isParent( final String parentName ) {
        if (!this.isPartialName && this.nameToMatch.startsWith(parentName)) {
            return true;
        } else if (this.isPartialName) {
            return true;
        }
        return false;
    }

    public Collection getMatchingEObjects() {
        return this.matchingEObjects;
    }

}
