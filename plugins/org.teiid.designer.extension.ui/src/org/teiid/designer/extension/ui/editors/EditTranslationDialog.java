/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.Form.COMBO_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.properties.Translation;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * The <code>EditTranslationDialog</code> is used to create or edit a translation.
 */
public class EditTranslationDialog extends FormDialog {

    private Button btnOk;
    private final List<Translation> existingTranslations;
    private Locale locale;
    private ScrolledForm scrolledForm;

    private final String title;
    private final String translationType;

    private final ErrorMessage localeError;
    private final ErrorMessage translationError;

    private String translation;
    /**
     * The translation being edited or <code>null</code> when creating a translation.
     */
    private Translation translationBeingEdited;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param title the dialog title (cannot be <code>null</code> or empty)
     * @param translationType a word describing what the translation represents (description, display name, etc.) (cannot be
     *            <code>null</code> or empty)
     * @param existingTranslations the existing translations (can be <code>null</code> or empty)
     */
    public EditTranslationDialog( Shell parentShell,
                                  String title,
                                  String translationType,
                                  Collection<Translation> existingTranslations ) {
        super(parentShell);
        CoreArgCheck.isNotNull(title, "title is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(translationType, "translationType is null"); //$NON-NLS-1$

        this.title = title;
        this.translationType = translationType;
        this.existingTranslations = ((existingTranslations == null) ? new ArrayList<Translation>()
                                                                   : new ArrayList<Translation>(existingTranslations));
        this.localeError = new ErrorMessage();
        this.translationError = new ErrorMessage();
    }

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param title the dialog title
     * @param translationType a word describing what the translation represents (description, display name, etc.) (cannot be
     *            <code>null</code> or empty)
     * @param existingTranslations the existing translations (can be <code>null</code> or empty)
     */
    public EditTranslationDialog( Shell parentShell,
                                  String title,
                                  String translationType,
                                  Collection<Translation> existingTranslations,
                                  Translation translationBeingEdited ) {
        this(parentShell, title, translationType, existingTranslations);

        CoreArgCheck.isNotNull(translationBeingEdited, "translationBeingEdited is null"); //$NON-NLS-1$
        this.translationBeingEdited = translationBeingEdited;
        this.locale = this.translationBeingEdited.getLocale();
        this.translation = this.translationBeingEdited.getTranslation();

        // remove the translation being edited
        this.existingTranslations.remove(this.existingTranslations);
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
    @SuppressWarnings("unused")
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        this.scrolledForm = managedForm.getForm();
        this.scrolledForm.setText(this.title);
        this.scrolledForm.setImage(Activator.getDefault().getImage(MED_EDITOR));
        this.scrolledForm.setMessage(Messages.translationDialogMsg, IMessageProvider.NONE);

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        Composite body = this.scrolledForm.getBody();
        body.setLayout(new GridLayout(2, false));

        final Control cbx;

        LOCALE: {
            Label lblLocale = toolkit.createLabel(body, Messages.localeLabel, SWT.NONE);
            lblLocale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

            CCombo cbxLocales = new CCombo(body, COMBO_STYLE);
            this.localeError.setControl(cbxLocales);
            cbx = cbxLocales;
            toolkit.adapt(cbxLocales, true, false);
            cbxLocales.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            ((GridData)cbxLocales.getLayoutData()).widthHint = (int)(getParentShell().getSize().x * 0.8);
            ((GridData)cbxLocales.getLayoutData()).heightHint = cbxLocales.getItemHeight() + 4;

            // populate locale combo
            final List<Locale> locales = new ArrayList<Locale>(Arrays.asList(Locale.getAvailableLocales()));
            Collections.sort(locales, ExtensionConstants.LOCALE_COMPARATOR);

            for (Locale locale : locales) {
                cbxLocales.add(locale.getDisplayName());
            }

            if (isEditMode()) {
                String current = this.translationBeingEdited.getLocale().getDisplayName();
                int index = cbxLocales.indexOf(current);

                if (index != -1) {
                    cbxLocales.select(index);
                }
            }

            cbxLocales.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    int index = ((CCombo)e.widget).getSelectionIndex();
                    handleLocaleChanged(locales.get(index));
                }
            });
        }

        TRANSLATION: {
            Label lblTranslation = toolkit.createLabel(body, Messages.translationLabel, SWT.NONE);
            lblTranslation.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            Text txtTranslation = toolkit.createText(body, null, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
            this.translationError.setControl(txtTranslation);
            txtTranslation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)txtTranslation.getLayoutData()).verticalIndent += ((GridLayout)body.getLayout()).verticalSpacing;
            ((GridData)txtTranslation.getLayoutData()).heightHint = txtTranslation.getLineHeight() * 3;
            ((GridData)txtTranslation.getLayoutData()).widthHint = (int)(getParentShell().getSize().x * 0.8);

            if (isEditMode()) {
                txtTranslation.setText(this.translationBeingEdited.getTranslation());
            }

            txtTranslation.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleTranslationChanged(((Text)e.widget).getText());
                }
            });
        }
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the new or edited translation (never <code>null</code>)
     */
    public Translation getTranslation() {
        if (isEditMode()) {
            this.translationBeingEdited.setLocale(this.locale);
            this.translationBeingEdited.setTranslation(this.translation);
        }

        return new Translation(this.locale, this.translation);
    }

    void handleLocaleChanged( Locale newLocale ) {
        this.locale = newLocale;
        validate();
        updateState();
    }

    void handleTranslationChanged( String newTranslation ) {
        this.translation = newTranslation;
        validate();
        updateState();
    }

    private boolean isEditMode() {
        return (this.translationBeingEdited != null);
    }

    private void updateMessage( ErrorMessage errorMsg ) {
        if (CoreStringUtil.isEmpty(errorMsg.getMessage())) {
            this.scrolledForm.getMessageManager().removeMessage(errorMsg.getKey(), errorMsg.getControl());
        } else {
            this.scrolledForm.getMessageManager().addMessage(errorMsg.getKey(), errorMsg.getMessage(), null,
                                                             errorMsg.getMessageType(), errorMsg.getControl());
        }
    }

    private void updateState() {
        boolean enable = false;

        if (CoreStringUtil.isEmpty(this.localeError.getMessage()) && CoreStringUtil.isEmpty(this.translationError.getMessage())) {
            enable = true;

            if (isEditMode()) {
                Locale localeBeingEdited = this.translationBeingEdited.getLocale();

                if (this.locale == null) {
                    enable = (localeBeingEdited != null);
                } else {
                    enable = ((localeBeingEdited == null) || !localeBeingEdited.equals(this.locale));
                }

                if (enable) {
                    enable = !CoreStringUtil.equals(this.translation, this.translationBeingEdited.getTranslation());
                }
            }

            // set message if changed
            if (!CoreStringUtil.equals(Messages.translationDialogMsg, this.scrolledForm.getMessage())) {
                // a bug in Eclipse doesn't reset the font color going from an error to NONE so first set to INFORMATION to get the
                // font color to change
                this.scrolledForm.setMessage(CoreStringUtil.Constants.EMPTY_STRING, IMessageProvider.INFORMATION);
                this.scrolledForm.setMessage(Messages.translationDialogMsg, IMessageProvider.NONE);
            }
        }

        // set enabled state of OK button
        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }

    private void validate() {
        // validate locale
        this.localeError.setMessage(ModelExtensionDefinitionValidator.validateTranslationLocale(this.locale));

        if (CoreStringUtil.isEmpty(this.localeError.getMessage())) {
            assert (this.locale != null) : "locale is null and should not be"; //$NON-NLS-1$
            Translation newTranslation = new Translation(this.locale, this.translation);
            Collection<Translation> temp = new ArrayList<Translation>(this.existingTranslations);
            temp.add(newTranslation);
            this.localeError.setMessage(ModelExtensionDefinitionValidator.validateTranslations(this.translationType, temp, false));
        }

        updateMessage(this.localeError);

        // validate translation text
        this.translationError.setMessage(ModelExtensionDefinitionValidator.validateTranslationText(this.translation));
        updateMessage(this.translationError);
    }

}
