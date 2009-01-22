/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;


public interface MultiFacetSet extends FacetSet {
    //
    // Class Constants:
    //
    public static final String LABEL_ADD = GUIFacetHelper.getString("MultiFacetSet.add"); //$NON-NLS-1$
    public static final String LABEL_DESCRIPTION = GUIFacetHelper.getString("MultiFacetSet.desc"); //$NON-NLS-1$
    public static final String LABEL_REMOVE = GUIFacetHelper.getString("MultiFacetSet.remove"); //$NON-NLS-1$
    public static final String LABEL_VALUE = GUIFacetHelper.getString("MultiFacetSet.value"); //$NON-NLS-1$

    //
    // Methods:
    //
    public void addValue(FacetValue fv, boolean reflow);
    public void reflow();
    public void clear();
}
