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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ContentViewer;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;


/**
 * An action that sorts {@link org.eclipse.jface.viewers.ContentViewer}s that use
 * a {@link com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider}.
 * @since 4.4
 */
public final class SortModelContentsAction extends Action
                                           implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private ContentViewer viewer;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>SortModelContentsAction</code>.
     * @param theViewer the viewer being sorted
     */
    public SortModelContentsAction(ContentViewer theViewer) {
        this.viewer = theViewer;
        
        final String PREFIX = I18nUtil.getPropertyPrefix(getClass());
        
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.ALPHA_SORT_ICON));
        setToolTipText(Util.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        setText(Util.getString(PREFIX + "text")); //$NON-NLS-1$
        setId("modelExplorerResourceNavigator.sortModelContentsAction"); //$NON-NLS-1$

        setInitialState();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Sets initial checked state by using the preference value.
     */
    private void setInitialState() {
        boolean sorting = false;
        String pref = getPreferenceStore().getString(PluginConstants.Prefs.General.SORT_MODEL_CONTENTS);
        
        if (!StringUtil.isEmpty(pref) && pref.equals(MessageDialogWithToggle.ALWAYS)) {
            sorting = true;
        }
        
        setChecked(sorting);
    }
    
    /**
     * Obtains the <code>IPreferenceStore</code> used to store the backing preference. 
     * @return the preference store
     * @since 4.4
     */
    private IPreferenceStore getPreferenceStore() {
        return UiPlugin.getDefault().getPreferenceStore();
    }
    
    /** 
     * @see org.eclipse.jface.action.Action#setChecked(boolean)
     * @since 4.4
     */
    @Override
    public void setChecked(boolean theCheckedFlag) {
        super.setChecked(theCheckedFlag);

        // set preference
        getPreferenceStore().setValue(PluginConstants.Prefs.General.SORT_MODEL_CONTENTS,
                                      (theCheckedFlag ? MessageDialogWithToggle.ALWAYS : MessageDialogWithToggle.NEVER));

        if (this.viewer.getContentProvider() instanceof ModelExplorerContentProvider) {
            ((ModelExplorerContentProvider)this.viewer.getContentProvider()).setEnableModelSorting(theCheckedFlag);

            // refresh viewer so that it will sort/unsort
            this.viewer.refresh();
        }
    }

}
