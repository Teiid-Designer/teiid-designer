/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
