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

package com.metamatrix.modeler.schema.tools.model.schema.impl;

import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public class MergedRelationship extends BaseRelationship {
    // Note: MergedTableRelationships may be nested (i.e. recursive)
    private Relationship parent; // the relationship between the grandparent
    // and the parent

    private Relationship child; // the relationship between the parent and the

    // child

    // Relates to the cardinality that the child appears with inside the parent
    public MergedRelationship( Relationship parent,
                               Relationship child ) {
        this.parent = parent;
        this.child = child;
    }

    public String getParentRelativeXpath() {
        String parentPart = parent.getParentRelativeXpath();
        String childPart = child.getParentRelativeXpath();
        // This is the xpath "from" the child "to" the parent, so it's
        // child/parent, not parent/child
        String retval = childPart + "/" + parentPart; //$NON-NLS-1$
        return retval;
    }

    public String getChildRelativeXpath() {
        String parentPart = parent.getChildRelativeXpath();
        String childPart = child.getChildRelativeXpath();
        String retval = parentPart + "/" + childPart; //$NON-NLS-1$
        return retval;
    }

    public SchemaObject getParent() {
        SchemaObject retval = parent.getParent();
        return retval;
    }

    public SchemaObject getChild() {
        SchemaObject retval = child.getChild();
        return retval;
    }

    public int getMinOccurs() {
        int parentMinOccurs = parent.getMinOccurs();
        int childMinOccurs = child.getMinOccurs();
        int retval = multiplyCardinalities(parentMinOccurs, childMinOccurs);
        return retval;
    }

    public int getMaxOccurs() {
        int parentMinOccurs = parent.getMinOccurs();
        int childMinOccurs = child.getMinOccurs();
        int retval = multiplyCardinalities(parentMinOccurs, childMinOccurs);
        return retval;
    }

    public int getRepresentation() {
        return child.getType();
    }

    @Override
    public String toString() {
        String childName = child.toString();
        String parentName = parent.toString();
        return parentName + ":" + childName; //$NON-NLS-1$
    }

    public void printDebug() {
        System.out.println("MergedRelationship: "); //$NON-NLS-1$
        System.out.println("Begin Parent"); //$NON-NLS-1$
        parent.printDebug();
        System.out.println("parentRelativeXPath = " + getParentRelativeXpath()); //$NON-NLS-1$
        System.out.println("End Parent"); //$NON-NLS-1$
        System.out.println("Begin Child"); //$NON-NLS-1$
        child.printDebug();
        System.out.println("childRelativeXPath = " + getChildRelativeXpath()); //$NON-NLS-1$
        System.out.println("End Child"); //$NON-NLS-1$

    }
}
