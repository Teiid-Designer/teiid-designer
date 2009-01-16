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
