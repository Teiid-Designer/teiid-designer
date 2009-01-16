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
package com.metamatrix.modeler.ui.viewsupport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.viewsupport.CompositeViewerFilter;

/**
 * A <code>CompositeViewerFilter</code> that filters resources based on the filter settings in the
 * {@link com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator}. Add other
 * {@link org.eclipse.jface.viewers.ViewerFilter}s if needed. By default, hidden projects are not shown.
 * 
 * @since 5.0.2
 */
public class ModelingResourceFilter extends CompositeViewerFilter implements UiConstants.Extensions.Explorer {

    private ViewerFilter hiddenProjectFilter;

    /**
     * Constructs a <code>ModelingResourceFilter</code> that uses the existing resource filter settings.
     * 
     * @since 5.0.2
     */
    public ModelingResourceFilter() {
        addFilter(UiUtil.getResourceFilter(VIEW));
        setShowHiddenProjects(false);
    }

    /**
     * Constructs a <code>ModelingResourceFilter</code> and adds the specified filter to it's collection of filters.
     * 
     * @param theFilter the filter being included
     * @since 5.0.2
     */
    public ModelingResourceFilter( ViewerFilter theFilter ) {
        this();
        addFilter(theFilter);
    }

    /**
     * After calling this method, remember to refresh the viewer.
     * 
     * @param showHiddenProjects <code>true</code> if hidden projects should be shown
     * @since 5.5.3
     */
    public void setShowHiddenProjects( boolean showHiddenProjects ) {
        if (showHiddenProjects) {
            if (this.hiddenProjectFilter != null) {
                removeFilter(this.hiddenProjectFilter);
                this.hiddenProjectFilter = null;
            }
        } else if (this.hiddenProjectFilter == null) {
            this.hiddenProjectFilter = new HiddenProjectFilter();
            addFilter(this.hiddenProjectFilter);
        }
    }

    class HiddenProjectFilter extends ViewerFilter {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        @Override
        public boolean select( Viewer viewer,
                               Object parentElement,
                               Object element ) {
            if (element instanceof IProject) {
                boolean result = false;

                try {
                    result = ((IProject)element).isOpen() && !((IProject)element).hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID);
                } catch (CoreException e) {
                    UiConstants.Util.log(e);
                }

                return result;
            }

            return true;
        }
    }
}
