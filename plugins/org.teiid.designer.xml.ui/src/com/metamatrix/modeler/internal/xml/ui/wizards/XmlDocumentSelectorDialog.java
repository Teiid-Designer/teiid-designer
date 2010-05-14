/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * Dialog designed specifically for users to select XML Document nodes or XML Document Roots Used initiallyl by
 * GenerateWebServiceModelAction2
 * 
 * @since 5.0
 */
public class XmlDocumentSelectorDialog extends ModelWorkspaceDialog implements ModelerXmlUiConstants {

    private EObject selectedEObject;

    private static final String TITLE = ModelerXmlUiConstants.Util.getString("XmlDocumentSelectorDialog.title"); //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = ModelerXmlUiConstants.Util.getString("XmlDocumentSelectorDialog.initialMessage"); //$NON-NLS-1$
    private static final String INVALID_SELECTION_MESSAGE = ModelerXmlUiConstants.Util.getString("XmlDocumentSelectorDialog.invalidSelectionMessage"); //$NON-NLS-1$

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public XmlDocumentSelectorDialog( Shell parent ) {
        this(parent, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public XmlDocumentSelectorDialog( Shell parent,
                                      ILabelProvider labelProvider,
                                      ITreeContentProvider contentProvider ) {
        super(parent, TITLE, labelProvider, contentProvider);
        this.setMessage(INITIAL_MESSAGE);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite composite = (Composite)super.createDialogArea(parent);

        // add code here to include new relationshipmodel panel:
        createModelCreationComposite(composite);

        return composite;
    }

    /**
     * Create the controls for creating a new relationships Model.
     * 
     * @param parent the parent composite
     * @return the created composite
     */
    private Composite createModelCreationComposite( Composite parent ) {

        // Set up Composite
        Composite nameComposite = new Composite(parent, SWT.NONE);
        GridLayout nameCompositeLayout = new GridLayout();
        nameComposite.setLayout(nameCompositeLayout);
        nameCompositeLayout.numColumns = 3;
        nameCompositeLayout.marginWidth = 0;
        GridData nameCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        nameCompositeGridData.horizontalIndent = 20;
        nameComposite.setLayoutData(nameCompositeGridData);

        setCreateControlsEnabled(false);

        return nameComposite;
    }

    @Override
    public Object[] getResult() {

        // if they created a new relational model, return it
        if (selectedEObject != null) {
            return new Object[] {selectedEObject};
        }
        // if they selected an existing relational model, return it
        return null;
    }

    private void updateDialogMessage( String sMessage,
                                      boolean bIsError ) {
        int iStatusCode = IStatus.OK;

        if (bIsError) {
            iStatusCode = IStatus.ERROR;
        }

        IStatus status = new StatusInfo(PLUGIN_ID, iStatusCode, sMessage);

        updateStatus(status);
    }

    protected void registerControls() { // NO_UCD
        getTreeViewer().addSelectionChangedListener(this);
    }

    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        super.selectionChanged(event);

        IStructuredSelection sel = (IStructuredSelection)getTreeViewer().getSelection();

        Object oSelection = sel.getFirstElement();

        if (oSelection instanceof XmlDocument || oSelection instanceof XmlRoot) {
            selectedEObject = (EObject)oSelection;
            setCreateControlsEnabled(true);
            updateOKStatus();
        } else {
            selectedEObject = null;
            updateDialogMessage(INVALID_SELECTION_MESSAGE, true);
            setCreateControlsEnabled(false);
        }

    }

    private void setCreateControlsEnabled( boolean b ) {

    }

    /* (non-Javadoc)
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
                boolean result = true;

                if (element instanceof IProject) {
                    IProject project = (IProject)element;

                    if (!project.isOpen()) {
                        result = false;
                    } else {
                        try {
                            if (!project.hasNature(ModelerCore.NATURE_ID)) {
                                result = false;
                            }
                        } catch (CoreException theException) {
                            ModelerCore.Util.log(theException);
                            result = false;
                        }
                    }
                }

                return result;
            }
        });

        result.expandToLevel(2);
        return result;
    }

    @Override
    public boolean close() {
        getTreeViewer().removeSelectionChangedListener(this);
        return super.close();
    }

}
