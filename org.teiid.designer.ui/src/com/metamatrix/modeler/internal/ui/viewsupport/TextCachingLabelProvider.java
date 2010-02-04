/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
