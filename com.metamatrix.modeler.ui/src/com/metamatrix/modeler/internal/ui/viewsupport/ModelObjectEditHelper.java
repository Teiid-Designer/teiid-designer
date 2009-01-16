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

import com.metamatrix.modeler.ui.actions.IModelObjectEditHelper;

/**
 * @author BLaFond
 *
 * This Abstract class provides metamodel specific plugins a base class for default global
 * action enabling logic. Since some metamodel objects may have certain global edit policies
 * (i.e. can't delete some objects), there needed to be a way to override the actions.
 * This abstract defines defaults all canXXX() methods to return true.
 */
public abstract class ModelObjectEditHelper implements IModelObjectEditHelper {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canClone(java.lang.Object)
	 */
	public boolean canClone(Object obj) {
		// XXX Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCopy(java.lang.Object)
	 */
	public boolean canCopy(Object obj) {
		// XXX Auto-generated method stub
		return true;
	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCopy(java.lang.Object)
     */
    public boolean canUndoCopy(Object obj) {
        // XXX Auto-generated method stub
        return true;
    }

    /* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCut(java.lang.Object)
	 */
	public boolean canCut(Object obj) {
		// XXX Auto-generated method stub
		return true;
	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCut(java.lang.Object)
     */
    public boolean canUndoCut(Object obj) {
        // XXX Auto-generated method stub
        return true;
    }

    /* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
	 */
	public boolean canDelete(Object obj) {
		// XXX Auto-generated method stub
		return true;
	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    public boolean canUndoDelete(Object obj) {
        // XXX Auto-generated method stub
        return true;
    }

    
    /* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canPaste(java.lang.Object, java.lang.Object)
	 */
	public boolean canPaste(Object obj, Object pasteParent) {
		// XXX Auto-generated method stub
		return true;
	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canPaste(java.lang.Object, java.lang.Object)
     */
    public boolean canUndoPaste(Object obj, Object pasteParent) {
        // XXX Auto-generated method stub
        return true;
    }

    
    /* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canRename(java.lang.Object)
	 */
	public boolean canRename(Object obj) {
		// XXX Auto-generated method stub
		return true;
	}

}
