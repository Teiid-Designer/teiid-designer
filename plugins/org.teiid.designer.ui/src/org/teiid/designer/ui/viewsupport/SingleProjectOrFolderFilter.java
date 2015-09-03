package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.viewsupport.CompositeViewerFilter;

public class SingleProjectOrFolderFilter extends CompositeViewerFilter implements UiConstants.Extensions.Explorer {

    private ViewerFilter hiddenProjectFilter;
    private IProject targetProject;

    /**
     * Constructs a <code>ModelingResourceFilter</code> that uses the existing resource filter settings.
     * 
     * @since 5.0.2
     */
    private SingleProjectOrFolderFilter() {
        addFilter(UiUtil.getResourceFilter(VIEW));
        setShowHiddenProjects(false);
    }
    /**
     * Constructs a <code>ModelingResourceFilter</code> and adds the specified filter to it's collection of filters.
     * 
     * @param theFilter the filter being included
     * @since 5.0.2
     */
    public SingleProjectOrFolderFilter( IProject targetProject ) {
        this();
        this.targetProject = targetProject;
        addFilter(new OneProjectFilter());
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
                IProject project = (IProject)element;
                try {
                    result = project.isOpen() && 
                    		!project.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID) &&
                    		project.hasNature(ModelerCore.NATURE_ID);
                } catch (CoreException e) {
                    UiConstants.Util.log(e);
                }

                return result;
            }

            return true;
        }
    }
    
    class OneProjectFilter extends ViewerFilter {

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
                IProject project = (IProject)element;
                try {
                    result = project.isOpen() && 
                    		!project.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID) &&
                    		project.hasNature(ModelerCore.NATURE_ID) &&
                    		targetProject.getName().equals(project.getName());
                } catch (CoreException e) {
                    UiConstants.Util.log(e);
                }

                return result;
            }

            return true;
        }
    }
}
