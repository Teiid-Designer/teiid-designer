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

package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** LabelProvider that caches the getText value from the supplied
  *  provider.  Since entries are never removed, this class is recommended
  *  for short-term usage, such as in dialogs that are sorted and filtered.
  *  
  * @author PForhan
  */
public class TextCachingLabelProvider extends LabelProvider {
    //
    // Instance variables:
    //
    private int cacheHits;
    private int cacheMisses;
    private final ILabelProvider realProvider;
    private final Map cache = new HashMap();

    //
    // Constructors:
    //
    public TextCachingLabelProvider(ILabelProvider provider) {
        this.realProvider = provider;
    }

    //
    // Overrides:
    //
    @Override
    public String getText(Object element) {
        String value = (String) cache.get(element);
        if (value == null) {
            cacheMisses++;
            value = realProvider.getText(element);
            cache.put(element, value);
        } else {
            cacheHits++;
        } // endif

        return value;
    }

    @Override
    public Image getImage(Object element) {
        return realProvider.getImage(element);
    }
    //
    // Methods:
    //
    public void printStats() {
        System.out.println("Cache size: "+cache.size()+"; hits: "+cacheHits+"; misses: "+cacheMisses); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }
}
