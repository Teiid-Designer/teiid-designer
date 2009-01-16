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

package com.metamatrix.modeler.ui.editors;

import org.eclipse.emf.ecore.EObject;


/** 
 * Interface designed to allow detection of objects that will handle in-line renaming of eObjects
 * In particular, the Tree viewer's ModelExplorerNotificationHandler is grabbing focus during new child/sibling actions. Then, 
 * if One new child, it auto-renames inline.  If the source is a DiagramEditor, however, it is not renaming in diagram, as expected.
 * Now the ModelExplorerNotificationHandler can ask if the Active Part is of the this type. If it exists, the tree viewer does
 * NOT rename-inline, but calls the renameInline() of the interface.  This delegates the work back on the source (i.e. DiagramEditor).
 * @since 5.0
 */
public interface IInlineRenameable {

    /**
     *  Implementers of the interface may pass back ITSELF, or some nested or referenced class
     * @param theObj
     * @return
     * @since 5.0
     */
    IInlineRenameable getInlineRenameable(EObject theObj);
    
    /**
     *  This method allows implementers to do a rename-inline edit operation for newly created objects.
     * @param obj
     * @param renameable
     * @since 5.0
     */
    void renameInline(EObject obj, IInlineRenameable renameable);
}
