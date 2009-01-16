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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;

/**
 * ModelWorkspaceDialog is a dialog that displays the workspace tree and allows selection
 */
public class ModelWorkspaceDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

    private static final String DEFAULT_TITLE = UiConstants.Util.getString("ModelWorkspaceDialog.title"); //$NON-NLS-1$
    private static final String PROPERTIES_BUTTON_TEXT = UiConstants.Util.getString("ModelWorkspaceDialog.propertiesButton.text"); //$NON-NLS-1$

    private IWorkspaceRoot root;

    private Button btnProperties;
    private int PROPERTIES_BUTTON_ID = 10234;
    EObject selectedEObject;
    private boolean bShowPropertiesButton = true;

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public ModelWorkspaceDialog( Shell parent ) {
        this(parent, DEFAULT_TITLE, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public ModelWorkspaceDialog( Shell parent,
                                 ILabelProvider labelProvider,
                                 ITreeContentProvider contentProvider ) {
        this(parent, null, labelProvider, contentProvider);
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public ModelWorkspaceDialog( Shell parent,
                                 String title,
                                 ILabelProvider labelProvider,
                                 ITreeContentProvider contentProvider ) {
        super(parent, labelProvider, contentProvider);

        init(title);
    }

    protected void init( String title ) {
        // use resource filter used by model explorer
        addFilter(new ModelingResourceFilter());

        if (title == null) {
            setTitle(DEFAULT_TITLE);
        } else {
            setTitle(title);
        }

        // default to EObject validator
        super.setValidator(new EObjectSelectionValidator());

        // set input
        if (root != null) {
            setInput(root);
        } else {
            // use default root
            setInput(ResourcesPlugin.getWorkspace().getRoot());
        }

    }

    /**
     * Adds a ViewerFilter to this dialog's TreeViewer
     * 
     * @param filter
     */
    public void addViewerFilter( ViewerFilter filter ) {
        super.getTreeViewer().addFilter(filter);
    }

    /**
     * Sets the Validator for this dialog's TreeViewer
     * 
     * @param filter
     */
    @Override
    public void setValidator( ISelectionStatusValidator validator ) {
        super.setValidator(validator);
    }

    public void setShowPropertiesButton( boolean b ) {
        bShowPropertiesButton = b;
        btnProperties.setVisible(bShowPropertiesButton);
    }

    /**
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite parent ) {
        TreeViewer result = super.createTreeViewer(parent);
        // add a filter to remove closed projects
        result.addFilter(new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof IProject) {
                    return ((IProject)element).isOpen();
                }
                return true;
            }

        });
        result.expandToLevel(2);
        return result;
    }

    /**
     * Method declared on Dialog.
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        // add our Properties button first
        btnProperties = createButton(parent, PROPERTIES_BUTTON_ID, PROPERTIES_BUTTON_TEXT, false);

        // then the OK and Cancel buttons
        super.createButtonsForButtonBar(parent);

        btnProperties.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                PropertiesDialog dlg = new PropertiesDialog(selectedEObject, null);
                dlg.open();
            }
        });

        // listen to selection in the tree
        getTreeViewer().addSelectionChangedListener(this);
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection sel = (IStructuredSelection)getTreeViewer().getSelection();

        if (sel.getFirstElement() instanceof EObject) {
            selectedEObject = (EObject)sel.getFirstElement();
            btnProperties.setEnabled(true);
        } else {
            selectedEObject = null;
            btnProperties.setEnabled(false);
        }
    }
}
