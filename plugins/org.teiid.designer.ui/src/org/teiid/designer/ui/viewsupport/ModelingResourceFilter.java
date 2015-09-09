/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.viewsupport.CompositeViewerFilter;


/**
 * A <code>CompositeViewerFilter</code> that filters resources based on the filter settings in the
 * {@link org.teiid.designer.ui.explorer.ModelExplorerResourceNavigator}. Add other
 * {@link org.eclipse.jface.viewers.ViewerFilter}s if needed. By default, hidden projects are not shown.
 * 
 * @since 8.0
 */
public class ModelingResourceFilter extends CompositeViewerFilter implements UiConstants.Extensions.Explorer {

    private ViewerFilter hiddenProjectFilter;
    private SingleProjectFilter singleProjectFilter;

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
    
    /**
     * After calling this method, remember to refresh the viewer.
     * 
     * @param showHiddenProjects <code>true</code> if hidden projects should be shown
     * @since 5.5.3
     */
    public void setSingleProjectProject( IProject project ) {
        if( project == null ) {
        	if( singleProjectFilter != null ) {
        		removeFilter(this.singleProjectFilter);
        	}
        	this.singleProjectFilter = null;
        } else {
        	if( this.singleProjectFilter == null ) {
        		this.singleProjectFilter = new SingleProjectFilter(project);
        		addFilter(this.singleProjectFilter);
        	} else {
        		this.singleProjectFilter.setProject(project);
        	}
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
                    result = ((IProject)element).isOpen() && 
                    		!((IProject)element).hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID) &&
                    		((IProject)element).hasNature(ModelerCore.NATURE_ID);
                } catch (CoreException e) {
                    UiConstants.Util.log(e);
                }

                return result;
            }

            return true;
        }
    }
    
    class SingleProjectFilter extends ViewerFilter {
    	IProject singleProject;

    	SingleProjectFilter(IProject project) {
    		super();
    		this.singleProject = project;
    	}
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
            	if( singleProject == null) return true;
            	
                IProject proj = (IProject)element;
                if( proj.getName().equals(singleProject.getName()) ) return true;
            } else if( element instanceof IContainer) {
            	if( singleProject == null) return true;
            	
            	IProject proj = ((IContainer)element).getProject();
                if( proj.getName().equals(singleProject.getName()) ) return true;
            }

            return false;
        }
        
        public void setProject(IProject project) {
        	singleProject = project;
        }
    }
}
