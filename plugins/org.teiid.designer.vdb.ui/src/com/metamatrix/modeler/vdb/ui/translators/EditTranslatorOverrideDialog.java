/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.vdb.TranslatorOverride;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * Used to edit a translator override name.
 */
final class EditTranslatorOverrideDialog extends MessageDialog {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(EditTranslatorOverrideDialog.class);

    private Button btnOk;
    private List<String> existingNames;
    private String name;
    private String[] translatorTypes;
    private String type;
    private TableViewer viewer;
    private TranslatorOverride translatorBeingEdited;

    private EditTranslatorOverrideDialog( Shell parentShell,
                                          String title,
                                          String message,
                                          String[] translatorTypes,
                                          Set<TranslatorOverride> existingTranslators ) {
        super(parentShell, title, null, message, MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL,
                IDialogConstants.CANCEL_LABEL }, 0);
        this.translatorTypes = ((translatorTypes == null) ? new String[0] : translatorTypes);

        if ((existingTranslators == null) || existingTranslators.isEmpty()) {
            this.existingNames = Collections.emptyList();
        } else {
            this.existingNames = new ArrayList<String>(existingTranslators.size());

            for (TranslatorOverride translator : existingTranslators) {
                this.existingNames.add(translator.getName());
            }
        }
    }

    /**
     * Create a new translator override.
     * 
     * @param parentShell the parent shell (can be <code>null</code>)
     * @param translatorTypes the collection of translator types (can be <code>null</code>)
     * @param existingTranslators the collection of VDB translator overrides (can be <code>null</code>)
     */
    public EditTranslatorOverrideDialog( Shell parentShell,
                                         String[] translatorTypes,
                                         Set<TranslatorOverride> existingTranslators ) {
        this(parentShell, Util.getString(PREFIX + "newTranslatorTitle"), Util.getString(PREFIX + "newTranslatorInitialMessage"), //$NON-NLS-1$ //$NON-NLS-2$
                translatorTypes, existingTranslators);
    }

    /**
     * Edits a translator override name.
     * 
     * @param parentShell the parent shell (can be <code>null</code>)
     * @param translator the translator whose name is being edited (cannot be <code>null</code>)
     * @param existingTranslators the collection of VDB translator overrides (can be <code>null</code>)
     */
    public EditTranslatorOverrideDialog( Shell parentShell,
                                         TranslatorOverride translator,
                                         Set<TranslatorOverride> existingTranslators ) {
        this(parentShell, Util.getString(PREFIX + "editTranslatorTitle"), Util.getString(PREFIX + "editTranslatorInitialMessage"), //$NON-NLS-1$ //$NON-NLS-2$
                null, existingTranslators);
        CoreArgCheck.isNotNull(translator, "translator is null"); //$NON-NLS-1$

        this.translatorBeingEdited = translator;
        this.name = this.translatorBeingEdited.getName();
        this.type = this.translatorBeingEdited.getType();

        // remove name from list of names so validation won't think current name is a duplicate name
        this.existingNames.remove(this.name);
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
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblName = new Label(pnl, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblName.setText(Util.getString(PREFIX + "lblName.text")); //$NON-NLS-1$

        Text txtName = new Text(pnl, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtName.setToolTipText(Util.getString(PREFIX + "txtName.toolTip")); //$NON-NLS-1$

        if (isEditMode()) {
            txtName.setText(this.translatorBeingEdited.getName());
        }

        txtName.addModifyListener(new ModifyListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleNameChanged(((Text)e.widget).getText());
            }
        });

        Label lblType = new Label(pnl, SWT.NONE);
        lblType.setText(Util.getString(PREFIX + "lblType.text")); //$NON-NLS-1$

        if (isEditMode()) {
            Label lbl = new Label(pnl, SWT.NONE);
            lbl.setText(this.type);
        } else {
            lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)lblType.getLayoutData()).horizontalSpan = 2;

            this.viewer = new TableViewer(pnl, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
            this.viewer.setContentProvider(new IStructuredContentProvider() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
                 */
                @Override
                public void dispose() {
                    // nothing to do
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
                 */
                @Override
                public Object[] getElements( Object inputElement ) {
                    return getTranslatorTypes();
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public void inputChanged( Viewer viewer,
                                          Object oldInput,
                                          Object newInput ) {
                    // nothing to do
                }
            });

            this.viewer.setLabelProvider(new LabelProvider());

            if (!isEditMode()) {
                this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                     */
                    @Override
                    public void selectionChanged( SelectionChangedEvent e ) {
                        handleTypeChanged();
                    }
                });
            }

            this.viewer.setSorter(new ViewerSorter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ViewerComparator#sort(org.eclipse.jface.viewers.Viewer, java.lang.Object[])
                 */
                @Override
                public void sort( Viewer viewer,
                                  Object[] elements ) {
                    Arrays.sort(elements);
                }
            });

            Table table = this.viewer.getTable();
            table.setFont(parent.getFont());

            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd.heightHint = table.getItemHeight() * 8;
            gd.minimumHeight = table.getItemHeight() * 2;
            gd.horizontalSpan = 2;
            table.setLayoutData(gd);

            // don't allow changing type when editing existing translator
            if (isEditMode()) {
                table.setEnabled(false);
            }

            // populate table
            this.viewer.setInput(this);

            // select type if editing an existing translator
            if (isEditMode()) {
                this.viewer.setSelection(new StructuredSelection(this.translatorBeingEdited.getType()), true);
            }
        }

        return pnl;
    }

    /**
     * @return the new translator override name (never <code>null</code> or empty)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public String getName() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    String[] getTranslatorTypes() {
        assert (this.translatorTypes != null) : "translatorTypes is null"; //$NON-NLS-1$
        return this.translatorTypes;
    }

    /**
     * @return the new translator override type (never <code>null</code> or empty)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public String getType() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return this.type;
    }

    void handleNameChanged( String newName ) {
        this.name = newName;
        updateState();
    }

    private boolean isEditMode() {
        return (this.translatorBeingEdited != null);
    }

    void handleTypeChanged() {
        assert !isEditMode() : "Type shouldn't be changed when editing an existing translator"; //$NON-NLS-1$
        this.type = (String)((IStructuredSelection)this.viewer.getSelection()).getFirstElement();
        updateState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#setReturnCode(int)
     */
    @Override
    protected void setReturnCode( int code ) {
        super.setReturnCode(code);

        // update translator name if in edit mode and OK clicked
        if ((code == Window.OK) && isEditMode()) {
            this.translatorBeingEdited.setName(this.name);
        }
    }

    private void updateState() {
        String msg = validate();

        // clear image
        if (this.imageLabel.getImage() != null) {
            this.imageLabel.setImage(null);
        }

        // empty message means name and type are both valid
        if (StringUtilities.isEmpty(msg)) {
            if (isEditMode() && this.translatorBeingEdited.getName().equals(this.name)) {
                // name hasn't changed
                if (this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(false);
                }

                msg = Util.getString(PREFIX + "editTranslatorInitialMessage"); //$NON-NLS-1$
            } else {
                if (!this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(true);
                }
            }

            this.imageLabel.setImage(getInfoImage());

            if (CoreStringUtil.isEmpty(msg)) {
                if (isEditMode()) {
                    msg = Util.getString(PREFIX + "editTranslatorOkMessage", this.translatorBeingEdited.getName(), this.name); //$NON-NLS-1$
                } else {
                    msg = Util.getString(PREFIX + "newTranslatorOkMessage", this.name, this.type); //$NON-NLS-1$
                }
            }
        } else {
            // value is not valid
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            this.imageLabel.setImage(getErrorImage());
        }

        this.messageLabel.setText(msg);
        this.messageLabel.pack();
    }

    private String validate() {
        if (StringUtilities.isEmpty(this.name)) {
            return Util.getString(PREFIX + "emptyTranslatorOverrideName"); //$NON-NLS-1$
        }

        // make sure only letters, numbers, dash, underscore
        for (char c : this.name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && (c != '-') && (c != '_')) {
                return Util.getString(PREFIX + "invalidTranslatorOverrideName"); //$NON-NLS-1$
            }
        }

        // make sure translator override name doesn't already exist
        for (String existingName : this.existingNames) {
            if (existingName.equals(this.name)) {
                return Util.getString(PREFIX + "translatorOverrideAlreadyExists"); //$NON-NLS-1$
            }
        }

        // make sure there is a type
        if (CoreStringUtil.isEmpty(this.type)) {
            return Util.getString(PREFIX + "translatorOverrideTypeMissing", this.name); //$NON-NLS-1$
        }

        return null;
    }

}
