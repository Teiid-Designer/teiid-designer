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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * <code>ViewerFilter</code> that selects {@link IContainer}s and optionally models found in open 
 * {@link org.eclipse.core.resources.IProject}s.
 * @since 4.2
 */
public class ModelWorkspaceViewerFilter extends ViewerFilter implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Indicates if models should be shown. Default is <code>false</code>. */
    private boolean showModels = false;
    /** Indicates if model contents should be shown. Default is <code>false</code>. */
    private boolean showModelContent = false;
    /** Indicates if closed projects should be shown. Default is <code>false</code>. */
    private boolean showClosedProjects = false;
    
    /**
     * An additional filter that will be used only for resources. Use the one from the navigator if possible.
     * @since 5.0.1
     */
    private ViewerFilter resourceFilter;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>ModelWorkspaceViewerFilter</code> that only selects {@link IContainer}s.
     */
    public ModelWorkspaceViewerFilter() {
    }
    
    
    /**
     * Constructs a <code>ModelWorkspaceViewerFilter</code> that selects {@link IContainer}s and optionally models.
     * @param theShowModelsFlag the flag indicating if models should also be shown
     */
    public ModelWorkspaceViewerFilter(boolean theShowModelsFlag) {
        setShowModels(theShowModelsFlag);
    }
    
    /**
     * Constructs a <code>ModelWorkspaceViewerFilter</code> that selects {@link IContainer}s and optionally models.
     * @param theShowModelsFlag the flag indicating if models should also be shown
     */
    public ModelWorkspaceViewerFilter(boolean theShowModelsFlag, boolean theShowModelContentFlag, boolean theShowClosedProjectsFlag) {
        setShowModels(theShowModelsFlag);
        setShowModelContent(theShowModelContentFlag);
        setShowClosedProjects(theShowClosedProjectsFlag);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Indicates if models will be shown by the filter.
     * @return <code>true</code>if models will be shown; <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isShowingModels() {
        return this.showModels;
    }
    
    /**
     * Indicates if model content will be shown by the filter.
     * @return <code>true</code>if models will be shown; <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isShowingModelContent() {
        return this.showModelContent;
    }
    
    /**
     * Indicates if closed model projects will be shown by the filter.
     * @return <code>true</code>if models will be shown; <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isShowingClosedProjects() {
        return this.showModelContent;
    }
    
    /**
     * Obtains an additional resource <code>ViewerFilter</code>. 
     * @return the filter
     * @since 5.0.1
     */
    private ViewerFilter getResourceFilter() {
        // get filter from the Model Explorer if not already explicitly set
        if (this.resourceFilter == null) {
            this.resourceFilter = UiUtil.getResourceFilter(UiConstants.Extensions.Explorer.VIEW);
        }
        
        return this.resourceFilter;
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean select(Viewer theViewer,
                          Object theParent,
                          Object theElement) {
        boolean result = false;

        if (theElement instanceof IContainer) {
            IProject project = ((IContainer)theElement).getProject();

            // make sure open and a model project or if show closed projects == TRUE, go ahead and 
            // check for model projects
            if (project.isOpen() ) {

                try {
                    if (!project.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)
                        && project.getNature(ModelerCore.NATURE_ID) != null) {
                        result = getResourceFilter().select(theViewer, theParent, theElement);
                    }
                } catch (CoreException theException) {
                    Util.log(theException);
                }
            } else if( showClosedProjects ) {
                // Because Eclipse does not expose "Natures" for closed projects, we need to get the internal
            	// ProjectInfo directly and do the check.
                // jh Defect 21210: examining the .project file directly to determine its 'nature'
                IFile dotProjectFile = DotProjectUtils.getDotProjectFile( (IContainer)theElement );
                
                if ( dotProjectFile != null ) {
                    try {
                        result = DotProjectUtils.isDotProject( dotProjectFile, true );
                    } catch ( Exception e ) {
                        Util.log( e );                    
                    }
                }
            }
        } else if (theElement instanceof IFile) {
            if (ModelUtilities.isModelingRelatedFile((IFile)theElement)) {
                result = this.showModels;
            } else {
                // let the resource filter decide if this should be shown
                result = getResourceFilter().select(theViewer, theParent, theElement);
            }
        } else if( theElement instanceof EObject || 
        		theElement instanceof ImportContainer || 
                theElement instanceof IExtendedModelObject) {
        	result = this.showModelContent;
        }

        return result;
    }
    
    /**
     * Sets the filter used as a preliminary resource filter. 
     * @param theResourceFilter the filter
     * @since 5.0.1
     */
    public void setResourceFilter(ViewerFilter theResourceFilter) {
        this.resourceFilter = theResourceFilter;
    }
    
    /**
     * Sets if the filter will show models or not. 
     * @param theShowModelsFlag the new show models setting
     * @since 4.2
     */
    public void setShowModels(boolean theShowModelsFlag) {
        this.showModels = theShowModelsFlag;
    }
    
    /**
     * Sets if the filter will show model content or not. 
     * @param theShowModelContentFlag the new show models setting
     * @since 4.2
     */
    public void setShowModelContent(boolean theShowModelContentFlag) {
        this.showModelContent = theShowModelContentFlag;
    }
    
    /**
     * Sets if the filter will show closed projects 
     * @param theShowModelsFlag the new show models setting
     * @since 4.2
     */
    public void setShowClosedProjects(boolean theShowClosedProjectsFlag) {
        this.showClosedProjects = theShowClosedProjectsFlag;
    }
    
}
