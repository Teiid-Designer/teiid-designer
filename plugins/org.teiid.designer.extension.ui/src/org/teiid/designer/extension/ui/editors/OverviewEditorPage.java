/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_OVERVIEW_PAGE;
import static org.teiid.designer.extension.ui.UiConstants.Form.COMBO_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.Form.TEXT_STYLE;

import java.beans.PropertyChangeEvent;
import java.util.Set;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition.PropertyName;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;

public final class OverviewEditorPage extends MedEditorPage {

    private final ErrorMessage descriptionError;
    private final ErrorMessage metamodelUriError;
    private final ErrorMessage namespacePrefixError;
    private final ErrorMessage namespaceUriError;

    private Control firstFocusControl;

    public OverviewEditorPage( ModelExtensionDefinitionEditor medEditor ) {
        super(medEditor, MED_OVERVIEW_PAGE, Messages.medEditorOverviewPageTitle);
        this.descriptionError = new ErrorMessage();
        this.metamodelUriError = new ErrorMessage();
        this.namespacePrefixError = new ErrorMessage();
        this.namespaceUriError = new ErrorMessage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#createBody(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.ui.forms.widgets.FormToolkit)
     */
    @SuppressWarnings("unused")
    @Override
    protected void createBody( Composite body,
                               FormToolkit toolkit ) {
        BODY: {
            body.setLayout(new GridLayout());
            body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }

        final Section finalSection;

        SECTION: {
            Section section = toolkit.createSection(body, ExpandableComposite.NO_TITLE | ExpandableComposite.TITLE_BAR
                    | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
            section.setLayout(new GridLayout());
            section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            finalSection = section;
        }

        final Composite finalContainer;

        CONTAINER: {
            Composite container = toolkit.createComposite(finalSection);
            container.setLayout(new GridLayout(2, false));
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            finalSection.setClient(container);
            finalContainer = container;
        }

        NAMESPACE_PREFIX: {
            toolkit.createLabel(finalContainer, Messages.namespacePrefixLabel);

            Text txtNamespacePrefix = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            txtNamespacePrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtNamespacePrefix.setText(getMed().getNamespacePrefix());
            txtNamespacePrefix.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleNamespacePrefixChanged(((Text)e.widget).getText());
                }
            });

            // associate control with error message
            this.namespacePrefixError.setControl(txtNamespacePrefix);

            // assign control as where to set focus to when page is set to current page
            this.firstFocusControl = txtNamespacePrefix;
        }

        final Text finalTxtNamespaceUri;

        NAMESPACE_URI: {
            toolkit.createLabel(finalContainer, Messages.namespaceUriLabel);

            Text txtNamespaceUri = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            finalTxtNamespaceUri = txtNamespaceUri;
            txtNamespaceUri.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtNamespaceUri.setText(getMed().getNamespaceUri());
            txtNamespaceUri.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleNamespaceUriChanged(((Text)e.widget).getText());
                }
            });

            // associate control with error message
            this.namespaceUriError.setControl(txtNamespaceUri);
        }

        METAMODEL_URI: {
            toolkit.createLabel(finalContainer, Messages.extendedMetamodelUriLabel);

            CCombo cbxMetamodelUris = new CCombo(finalContainer, COMBO_STYLE);
            toolkit.adapt(cbxMetamodelUris, true, false);
            cbxMetamodelUris.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            ((GridData)cbxMetamodelUris.getLayoutData()).heightHint = cbxMetamodelUris.getItemHeight() + 4;

            // populate URIs
            Set<String> items = ExtensionPlugin.getInstance().getRegistry().getExtendableMetamodelUris();
            cbxMetamodelUris.setItems(items.toArray(new String[items.size()]));

            // set value based on MED
            String metamodelUri = getMed().getMetamodelUri();

            if (!CoreStringUtil.isEmpty(metamodelUri)) {
                int index = cbxMetamodelUris.indexOf(metamodelUri);

                if (index == -1) {
                    UTIL.log(NLS.bind(Messages.overviewPageInvalidMetamodelUriMsg, metamodelUri));
                } else {
                    cbxMetamodelUris.select(index);
                }
            }

            cbxMetamodelUris.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleMetamodelUriChanged(((CCombo)e.widget).getText());
                }
            });

            // associate control with error message
            this.metamodelUriError.setControl(cbxMetamodelUris);
        }

        VERSION: {
            toolkit.createLabel(finalContainer, Messages.versionLabel);

            Text txtVersion = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, SWT.READ_ONLY | TEXT_STYLE);
            txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtVersion.setText(String.valueOf(getMed().getVersion()));
        }

        DESCRIPTION: {
            Label label = toolkit.createLabel(finalContainer, Messages.descriptionLabel);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

            Text txtDescription = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER | SWT.MULTI
                    | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
            txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            String description = getMed().getDescription();

            if (!CoreStringUtil.isEmpty(description)) {
                txtDescription.setText(description);
            }

            txtDescription.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleDescriptionChanged(((Text)e.widget).getText());
                }
            });

            // associate control with error message
            this.descriptionError.setControl(txtDescription);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return Messages.medEditorOverviewPageToolTip;
    }

    void handleDescriptionChanged( String newDescription ) {
        getMed().setDescription(newDescription);
    }

    void handleMetamodelUriChanged( String newMetamodelUri ) {
        getMed().setMetamodelUri(newMetamodelUri);
    }

    void handleNamespacePrefixChanged( String newNamespacePrefix ) {
        getMed().setNamespacePrefix(newNamespacePrefix);
    }

    void handleNamespaceUriChanged( String newNamespaceUri ) {
        getMed().setNamespaceUri(newNamespaceUri);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#handlePropertyChanged(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void handlePropertyChanged( PropertyChangeEvent e ) {
        String propName = e.getPropertyName();

        if (PropertyName.DESCRIPTION.toString().equals(propName)) {
            validateDescription();
        } else if (PropertyName.METAMODEL_URI.toString().equals(propName)) {
            validateMetamodelUri();
        } else if (PropertyName.NAMESPACE_PREFIX.toString().equals(propName)) {
            validateNamespacePrefix();
        } else if (PropertyName.NAMESPACE_URI.toString().equals(propName)) {
            validateNamespaceUri();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#setFocus()
     */
    @Override
    public void setFocus() {
        if (this.firstFocusControl != null) {
            this.firstFocusControl.setFocus();
        } else {
            super.setFocus();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#updateAllMessages()
     */
    @Override
    protected void updateAllMessages() {
        validateDescription();
        validateMetamodelUri();
        validateNamespacePrefix();
        validateNamespaceUri();
    }

    private void validateDescription() {
        this.descriptionError.setMessage(ModelExtensionDefinitionValidator.validateDescription(getMed().getDescription()));
        updateMessage(this.descriptionError);
    }

    private void validateMetamodelUri() {
        this.metamodelUriError.setMessage(ModelExtensionDefinitionValidator.validateMetamodelUri(getMed().getMetamodelUri(),
                                                                                                 getRegistry().getExtendableMetamodelUris()));
        updateMessage(this.metamodelUriError);
    }

    private void validateNamespacePrefix() {
        this.namespacePrefixError.setMessage(ModelExtensionDefinitionValidator.validateNamespacePrefix(getMed().getNamespacePrefix(),
                                                                                                       getRegistry().getAllNamespacePrefixes()));
        updateMessage(this.namespacePrefixError);
    }

    private void validateNamespaceUri() {
        this.namespaceUriError.setMessage(ModelExtensionDefinitionValidator.validateNamespaceUri(getMed().getNamespaceUri(),
                                                                                                 getRegistry().getAllNamespaceUris()));
        updateMessage(this.namespaceUriError);
    }

}
