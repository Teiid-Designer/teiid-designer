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
