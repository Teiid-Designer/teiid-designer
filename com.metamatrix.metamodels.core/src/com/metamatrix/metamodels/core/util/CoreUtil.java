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

package com.metamatrix.metamodels.core.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

/**
 * CoreUtil
 */
public class CoreUtil {

    /**
     * Construct an instance of CoreUtil.
     * 
     */
    private CoreUtil() {
        super();
    }

    public static void removePropertyDescriptor( final List descriptors, final EStructuralFeature[] features ) {
        if ( features == null || features.length == 0 || descriptors == null ) {
            return;
        }
        final Set featureSet = new HashSet();
        for (int i = 0; i < features.length; ++i) {
            final EStructuralFeature feature = features[i];
            featureSet.add(feature);
        }
        removePropertyDescriptor(descriptors,featureSet);
    }

    public static void removePropertyDescriptor( final List descriptors, final Set features ) {
        if ( features == null || features.size() == 0 || descriptors == null ) {
            return;
        }
        final Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            final ItemPropertyDescriptor desc = (ItemPropertyDescriptor)iter.next();
            if ( features.contains(desc.getFeature(null)) ) {
                iter.remove();
            }
        }
    }

    public static void removePropertyDescriptor( final List descriptors, final EStructuralFeature feature ) {
        if ( feature == null || descriptors == null ) {
            return;
        }
        final Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            final ItemPropertyDescriptor desc = (ItemPropertyDescriptor)iter.next();
            if ( feature.equals(desc.getFeature(null)) ) {
                iter.remove();
            }
        }
    }

}
