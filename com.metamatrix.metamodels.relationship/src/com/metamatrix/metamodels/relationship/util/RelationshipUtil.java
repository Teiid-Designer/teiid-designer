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

package com.metamatrix.metamodels.relationship.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;

/**
 * RelationshipUtil.java
 */
public class RelationshipUtil {

	/**
	 * Prevent allocation
	 */
	private RelationshipUtil() {
		super();
	}
    
	protected static void executeVisitor( final Object container, final ModelVisitor visitor, final int depth ) {
		final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
		try {
			if ( container instanceof Resource ) {
				processor.walk((Resource)container,depth);
			} else if ( container instanceof EObject ) {
				processor.walk((EObject)container,depth);
			}
		} catch (ModelerCoreException e) {
			ModelerCore.Util.log(e);
		}
	}
	
	/**
	 * Add any {@link Relationship} instances found under the supplied container
	 * @param container the Resource under which the relationships are to be found; may not be null
	 * @return the relationships that were found; may not be null
	 */
	public static List findRelationships( final Object container ) {
		return findRelationships(container, ModelVisitorProcessor.DEPTH_INFINITE);
	}

	/**
	 * Add any {@link Relationship} instances found under the supplied container
	 * @param container the Resource under which the relationships are to be found; may not be null
	 * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
	 * @return the relationships that were found; may not be null
	 */
	public static List findRelationships( final Object container, final int depth ) {
		final RelationshipFinder finder = new RelationshipFinder();
		executeVisitor(container,finder,depth);
		return finder.getObjects();
	}
	
	/**
	 * Add any {@link RelationshipType} instances found under the supplied container
	 * @param container the Resource under which the relationship types are to be found; may not be null
	 * @return the relationship types that were found; may not be null
	 */
	public static List findRelationshipTypes( final Object container ) {
		return findRelationshipTypes(container, ModelVisitorProcessor.DEPTH_INFINITE);
	}

	/**
	 * Add any {@link RelationshipType} instances found under the supplied container
	 * @param container the Resource under which the relationship types are to be found; may not be null
	 * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
	 * @return the relationship types that were found; may not be null
	 */
	public static List findRelationshipTypes( final Object container, final int depth ) {
		final RelationshipTypeFinder finder = new RelationshipTypeFinder();
		executeVisitor(container,finder,depth);
		return finder.getObjects();
	}

	/**
	 * Add any {@link RelationshipRole} instances found under the supplied container
	 * @param container the EObject or Resource under which the relationship types are to be found; may not be null
	 * @return the relationship roles that were found; may not be null
	 */
	public static List findRelationshipRoles( final Object container ) {
		return findRelationshipRoles(container, ModelVisitorProcessor.DEPTH_INFINITE);
	}

	/**
	 * Add any {@link RelationshipRole} instances found under the supplied container
	 * @param container the EObject or Resource under which the relationship roles are to be found; may not be null
	 * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
	 * @return the relationship roles that were found; may not be null
	 */
	public static List findRelationshipRoles( final Object container, final int depth ) {
		final RelationshipRoleFinder finder = new RelationshipRoleFinder();
		executeVisitor(container,finder,depth);
		return finder.getObjects();
	}
    
    /**
     * Utility method to determine whether the supplied EClass is an ancestor (i.e.., superclass)
     * of the supplied metaclass.
     * @param metaclass the metaclass; may not be null
     * @param superclass the potential superclass; may not be null
     * @return true if <code>superclass</code> is indeed a direct or indirect super type for
     * <code>metaclass</code>
     */
    public static boolean isAncestor( final EClass metaclass, final EClass superclass ) {
        ArgCheck.isNotNull(metaclass);
        ArgCheck.isNotNull(superclass);
        if ( superclass.equals(metaclass) ) {
            return false;
        }

        // Walk up superclass's supertype path to see if we are an existing supertype of it ...
        LinkedList ancestors = new LinkedList();
        ancestors.addAll(metaclass.getESuperTypes());
        while ( ancestors.size() != 0 ) {
            final EClass supertype = (EClass)ancestors.removeFirst();
            if ( supertype.equals(superclass) ) {
                return true;
            }
            final Iterator iter = supertype.getESuperTypes().iterator();
            while (iter.hasNext()) {
                final EClass supertypeSupertype = (EClass)iter.next();
                if ( !ancestors.contains(supertypeSupertype) ) {
                    ancestors.add(supertypeSupertype);
                }
            }
        }
        return false;
    }

    /**
     * Utility method to determine whether the supplied EClass is an ancestor (i.e.., superclass)
     * of the supplied metaclass.
     * @param metaclass the metaclass; may not be null
     * @param superclass the potential superclass; may not be null
     * @return true if one of the <code>superclasses</code> is indeed a direct or indirect super type for
     * <code>metaclass</code>
     */
    public static boolean isAncestor( final EClass metaclass, final Collection superclasses ) {
        ArgCheck.isNotNull(metaclass);
        ArgCheck.isNotNull(superclasses);

        // Walk up metaclass' supertype path to see if we are an existing supertype of it ...
        LinkedList ancestors = new LinkedList();
        ancestors.addAll(metaclass.getESuperTypes());
        while ( ancestors.size() != 0 ) {
            final EClass supertype = (EClass)ancestors.removeFirst();
            if ( superclasses.contains(supertype) ) {
                return true;
            }
            final Iterator iter = supertype.getESuperTypes().iterator();
            while (iter.hasNext()) {
                final EClass supertypeSupertype = (EClass)iter.next();
                if ( !ancestors.contains(supertypeSupertype) ) {
                    ancestors.add(supertypeSupertype);
                }
            }
        }
        return false;
    }


}
