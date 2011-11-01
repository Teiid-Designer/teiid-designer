/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * The <code>EditMetaclassDialog</code> is used to create or edit a metaclass name.
 */
final class EditMetaclassDialog extends FormDialog {

    private ScrolledForm scrolledForm;
    private Button btnOk;
    private final List<String> currentExtendedMetaclasses;
    private String selectedMetaclassName;
    private TreeViewer treeViewer;
    private MetaclassTreeContentProvider treeContentProvider;
    ExtendableMetaclassNameProvider metaclassNameProvider;

    /**
     * The metaclass name being edited or <code>null</code> when creating a metaclass name.
     */
    private String metaclassNameBeingEdited;

    private final ErrorMessage metaclassError;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param currentExtendedMetaclasses the names of the metaclasses tha currently exist in the model extension definition (never
     *        <code>null</code> but can be empty)
     */
    public EditMetaclassDialog( Shell parentShell,
                                ExtendableMetaclassNameProvider metaclassNameProvider,
                                List<String> currentExtendedMetaclasses ) {
        super(parentShell);
        this.metaclassNameProvider = metaclassNameProvider;
        this.treeContentProvider = new MetaclassTreeContentProvider(metaclassNameProvider, currentExtendedMetaclasses);
        this.currentExtendedMetaclasses = new ArrayList<String>(currentExtendedMetaclasses);
        this.metaclassError = new ErrorMessage();
    }

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param currentExtendedMetaclasses the names of the metaclasses tha currently exist in the model extension definition (never
     *        <code>null</code> but can be empty)
     */
    public EditMetaclassDialog( Shell parentShell,
                                ExtendableMetaclassNameProvider metaclassNameProvider,
                                List<String> currentExtendedMetaclasses,
                                String metaclassNameBeingEdited ) {
        this(parentShell, metaclassNameProvider, currentExtendedMetaclasses);

        CoreArgCheck.isNotNull(metaclassNameBeingEdited, "metaclassNameBeingEdited is null"); //$NON-NLS-1$
        this.metaclassNameBeingEdited = metaclassNameBeingEdited;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);

        if (isEditMode()) {
            newShell.setText(Messages.editMetaclassDialogTitle);
        } else {
            newShell.setText(Messages.addMetaclassDialogTitle);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        Button btn = super.createButton(parent, id, label, defaultButton);

        if (id == IDialogConstants.OK_ID) {
            // disable OK button initially
            this.btnOk = btn;
            btn.setEnabled(false);
        }

        return btn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        this.scrolledForm = managedForm.getForm();
        this.scrolledForm.setText(Messages.metaclassDialogTitle);
        this.scrolledForm.setImage(Activator.getDefault().getImage(MED_EDITOR));
        this.scrolledForm.setMessage(Messages.metaclassDialogMessage, IMessageProvider.NONE);

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite body = scrolledForm.getBody();
        body.setLayout(new GridLayout(1, true));
        Tree tree = toolkit.createTree(body, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        tree.setLayoutData(new GridLayout());
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)tree.getLayoutData()).heightHint = tree.getItemHeight() * 10;
        treeViewer = new TreeViewer(tree);

        treeViewer.setContentProvider(this.treeContentProvider);
        treeViewer.setLabelProvider(this.treeContentProvider);
        treeViewer.setInput(this);
        treeViewer.setSelection(null, false);

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                String selectedMetaclassStr = getSelectedMetaclass();
                handleMetaclassNameSelectionChanged(selectedMetaclassStr);
            }
        });
    }

    private String getSelectedMetaclass() {
        IStructuredSelection selection = (IStructuredSelection)this.treeViewer.getSelection();
        return selection.isEmpty() ? null : (String)selection.getFirstElement();
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the metaclass name being added to the model extension definition (never <code>null</code> or empty when OK button has
     *         been pressed)
     */
    public String getSelectedMetaclassName() {
        return this.selectedMetaclassName;
    }

    void handleMetaclassNameSelectionChanged( String newMetaclassName ) {
        this.selectedMetaclassName = newMetaclassName;
        // Update the status based on the selected metaclass
        if (CoreStringUtil.isEmpty(this.selectedMetaclassName)) {
            this.metaclassError.setMessage(Messages.editMetaclassNothingSelected);
        } else if (this.currentExtendedMetaclasses.contains(this.selectedMetaclassName)) {
            this.metaclassError.setMessage(Messages.editMetaclassAlreadyExtendedMetaclassSelected);
            // Edit Mode message is different, if the selected metaclass is the metaclass being edited
            if (isEditMode() && this.selectedMetaclassName.equals(this.metaclassNameBeingEdited)) {
                this.metaclassError.setMessage(Messages.editMetaclassEditedMetaclassSelected);
            }
        } else {
            this.metaclassError.clearMessage();
        }

        updateState();
    }

    private boolean isEditMode() {
        return (!CoreStringUtil.isEmpty(this.metaclassNameBeingEdited));
    }

    private void updateState() {
        // check to see if new metaclassName is valid
        String errorMsg = this.metaclassError.getMessage();
        int imageType = IMessageProvider.NONE;

        // No Error Message - enable OK button and show default message
        if (CoreStringUtil.isEmpty(errorMsg)) {
            boolean enable = true;

            if (this.btnOk.getEnabled() != enable) {
                this.btnOk.setEnabled(enable);
            }

            errorMsg = Messages.metaclassDialogMessage;
            // Error Message present - disable OK button and show message
        } else {
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            imageType = IMessageProvider.ERROR;
        }

        this.scrolledForm.setMessage(errorMsg, imageType);
    }

    class MetaclassTreeContentProvider extends LabelProvider implements ITreeContentProvider {

        private ExtendableMetaclassNameProvider metaclassNameProvider;

        public MetaclassTreeContentProvider( final ExtendableMetaclassNameProvider metaclassNameProvider,
                                             final List<String> currentExtendedClasses ) {
            super();
            this.metaclassNameProvider = metaclassNameProvider;
        }

        @Override
        public Object[] getChildren( Object parentElement ) {
            return this.metaclassNameProvider.getExtendableMetaclassChildren(parentElement.toString());
        }

        @Override
        public Object getParent( Object element ) {
            return this.metaclassNameProvider.getParent(element.toString());
        }

        @Override
        public boolean hasChildren( Object element ) {
            return getChildren(element).length > 0;
        }

        @Override
        public Object[] getElements( Object inputElement ) {
            return this.metaclassNameProvider.getExtendableMetaclassRoots();
        }

        @Override
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // Do nothing
        }

        @Override
        public Image getImage( Object element ) {
            return null;
        }

        @Override
        public String getText( Object element ) {
            return this.metaclassNameProvider.getLabelText(element.toString());
        }
    }

}
