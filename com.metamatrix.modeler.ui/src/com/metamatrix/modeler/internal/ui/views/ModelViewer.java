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

package com.metamatrix.modeler.internal.ui.views;

import org.eclipse.jface.viewers.IDoubleClickListener;

/**
 * ModelViewer is a common interface for all ViewParts and ViewPages that show 
 * model contents and wish to be double-click enabled with the ModelEditor.  
 * ModelViewer may be implemented by any IViewPart or any IPage.
 */
public interface ModelViewer {

    /**
     * Add an IDoubleClickListener to this Part so that double-click of model objects can be sent
     * to the appropriate ModelEditor and ModelEditorPage.
     * @param listener a DoubleClickListener
     */
    void addModelObjectDoubleClickListener(IDoubleClickListener listener);

    /**
     * Remove the specified IDoubleClickListener from this Part.
     * @param listener a DoubleClickListener
     */
    void removeModelObjectDoubleClickListener(IDoubleClickListener listener);

}
