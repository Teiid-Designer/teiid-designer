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

package com.metamatrix.modeler.ui.actions;

import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * IPasteSpecialContributor
 */
public interface IPasteSpecialContributor extends IWorkbenchWindowActionDelegate, ISelectionListener {

    /**
     * Determine if this contribution can paste the current clipboard contents into
     * the current workbench selection.  This method is used only to determine enablement
     * of the PasteSpecialAction in the workbench.  The actual paste should be implemented in 
     * the run method.
     * @return true if the contribution can paste.
     */
    boolean canPaste();

}
