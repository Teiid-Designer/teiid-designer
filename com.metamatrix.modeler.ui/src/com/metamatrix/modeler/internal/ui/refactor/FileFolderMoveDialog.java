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

package com.metamatrix.modeler.internal.ui.refactor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.modeler.core.refactor.ResourceMoveCommand;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;

/**
 * FileFolderMoveDialog is a dialog that displays the workspace tree and allows selection
 *
      sample of the dialog code:
     private void handleBrowseTypeButtonPressed_TestOfFileFolderMoveDialog() {


        // ==================================
        // launch File/Folder Move Dialog
        // ==================================

        FileFolderMoveDialog ffmdDialog
            = new FileFolderMoveDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell() );

        ffmdDialog.setValidator( new RelationshipTypeSelectionValidator() );
        ffmdDialog.setResource( xxx);
        ffmdDialog.setCommand( xxx );

        ffmdDialog.open();

        if ( ffmdDialog.getReturnCode() == FileFolderMoveDialog.OK ) {
            Object[] oSelectedObjects = ffmdDialog.getResult();
            ...
        }
    }


 */
public class FileFolderMoveDialog extends ElementTreeSelectionDialog {

    private static final String BASE_TITLE = UiConstants.Util.getString("FileFolderMoveDialog.baseTitle.text"); //$NON-NLS-1$
    private static final String MESSAGE = UiConstants.Util.getString("FileFolderMoveDialog.headerLabel.text"); //$NON-NLS-1$

    private IWorkspaceRoot root;
    private ISelectionStatusValidator validator;

    private IResource resource;
    private ResourceMoveCommand command;
    private ModelExplorerContentProvider cpContentProvider;

    /**
     * Construct an instance of FileFolderMoveDialog.  This constructor defaults to the resource root.
     * @param propertiedObject the EObject to display in this
     * @param parent the shell
     *
     *
     */
    public FileFolderMoveDialog( Shell parent, ResourceMoveCommand command, IResource resource ) {
        super( parent,
               new ModelExplorerLabelProvider(),
               new ModelExplorerContentProvider() );
        this.command = command;
        this.resource = resource;

        init();
    }

    /**
     * Construct an instance of FileFolderMoveDialog.  This constructor defaults to the resource root.
     * @param propertiedObject the EObject to display in this
     * @param parent the shell
     *
     *
     */
    public FileFolderMoveDialog( Shell parent, ResourceMoveCommand command, IResource resource, ModelExplorerContentProvider cpContentProvider ) {
        super( parent,
               new ModelExplorerLabelProvider(),
               cpContentProvider );
        this.command = command;
        this.resource = resource;
        this.cpContentProvider = cpContentProvider;

        init();
    }

    /**
     * Construct an instance of FileFolderMoveDialog.  This constructor builds the tree from the supplied root.
     * @param propertiedObject the EObject to display in this
     * @param parent
     * @param root a workspace root
     */
    public FileFolderMoveDialog( Shell parent,
                                 ITreeContentProvider contentProvider,
                                 IWorkspaceRoot root,
                                 ResourceMoveCommand command ) {
        super( parent,
               new ModelExplorerLabelProvider(),
               contentProvider );

        this.root = root;
        this.command = command;

        init();
    }

    private void init() {
        // use resource filter used by model explorer
        addFilter(new ModelingResourceFilter());

        // default to EObject validator
        validator = new ModelContainerSelectionValidator( command );
        super.setValidator( validator );

        // set input
        if ( root != null ) {
            setInput( root );
        } else {
            // use default root
            setInput( ResourcesPlugin.getWorkspace().getRoot() );
        }

        setAllowMultiple( false );

        // set the title from the resource
        setTitle( BASE_TITLE
                  + ' '
                  + resource.getName() );

        super.setMessage(MESSAGE);

        initSelection();
    }

    protected void initSelection() {

        IProject firstProject = null;
        IProject projectToSelect = null;
        Object[] oChildren = cpContentProvider.getChildren( ResourcesPlugin.getWorkspace().getRoot() );

        for ( int i = 0; i < oChildren.length; i++ ) {
            if( oChildren[ i ] instanceof IProject ) {
                IProject projTemp = (IProject)oChildren[ i ];
                if( i == 0 ) {
                    // default to the first project
                    firstProject = projTemp;
                }

                // check for the first open project
                if ( projectToSelect == null && projTemp.isOpen() ) {
                    projectToSelect = projTemp;
                }
            }
        }

        // if we found an open project, set it to be selected, otherwise select first
        if ( projectToSelect != null ) {
            setInitialSelection( projectToSelect );
        }
        else
        if ( firstProject != null ) {
            setInitialSelection( firstProject );
        }
    }

    @Override
    public void setValidator( ISelectionStatusValidator validator ) {
        this.validator = validator;
        super.setValidator( validator );
    }

}
