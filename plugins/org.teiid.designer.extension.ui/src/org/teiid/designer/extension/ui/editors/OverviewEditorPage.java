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

import java.util.Set;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;

public final class OverviewEditorPage extends MedEditorPage {

    private final ErrorMessage descriptionError;
    private final ErrorMessage metamodelUriError;
    private final ErrorMessage namespacePrefixError;
    private final ErrorMessage namespaceUriError;
    private final ErrorMessage resourcePathError;
    private final ErrorMessage versionError;

    private CCombo cbxMetamodelUris;
    private Text txtDescription;
    private Text txtNamespacePrefix;
    private Text txtNamespaceUri;
    private Text txtResourcePath;
    private Text txtVersion;

    public OverviewEditorPage( FormEditor medEditor,
                               ModelExtensionDefinitionValidator medValidator ) {
        super(medEditor, MED_OVERVIEW_PAGE, Messages.medEditorOverviewPageTitle, medValidator);
        this.descriptionError = new ErrorMessage();
        this.metamodelUriError = new ErrorMessage();
        this.namespacePrefixError = new ErrorMessage();
        this.namespaceUriError = new ErrorMessage();
        this.resourcePathError = new ErrorMessage();
        this.versionError = new ErrorMessage();
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

            this.txtNamespacePrefix = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            this.txtNamespacePrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            if (isEditMode()) {
                this.txtNamespacePrefix.setText(this.getValidator().getNamespacePrefix());
            }

            this.txtNamespacePrefix.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateNamespacePrefix();
                }
            });
        }

        final Text finalTxtNamespaceUri;

        NAMESPACE_URI: {
            toolkit.createLabel(finalContainer, Messages.namespaceUriLabel);

            this.txtNamespaceUri = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            finalTxtNamespaceUri = this.txtNamespaceUri;
            this.txtNamespaceUri.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            if (isEditMode()) {
                this.txtNamespaceUri.setText(this.getValidator().getNamespaceUri());
            }

            this.txtNamespaceUri.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateNamespaceUri();
                }
            });
        }

        METAMODEL_URI: {
            toolkit.createLabel(finalContainer, Messages.extendedMetamodelUriLabel);

            this.cbxMetamodelUris = new CCombo(finalContainer, COMBO_STYLE);
            toolkit.adapt(this.cbxMetamodelUris, true, false);
            this.cbxMetamodelUris.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            // populate URIs
            Set<String> items = ExtensionPlugin.getInstance().getRegistry().getExtendableMetamodelUris();
            this.cbxMetamodelUris.setItems(items.toArray(new String[items.size()]));

            // set value based on MED
            if (isEditMode()) {
                String metamodelUri = getValidator().getMetamodelUri();
                int index = this.cbxMetamodelUris.indexOf(metamodelUri);

                if (index == -1) {
                    UTIL.log(NLS.bind(Messages.overviewPageInvalidMetamodelUriMsg, metamodelUri));
                } else {
                    this.cbxMetamodelUris.select(index);
                }
            }

            this.cbxMetamodelUris.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateMetamodelUri();
                }
            });

            final CCombo cbx = this.cbxMetamodelUris;
            this.cbxMetamodelUris.addControlListener(new ControlAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                 */
                @Override
                public void controlResized( ControlEvent e ) {
                    cbx.setSize(finalTxtNamespaceUri.getSize());
                }
            });
        }

        RESOURCE_PATH: {
            toolkit.createLabel(finalContainer, Messages.resourcePathLabel);

            this.txtResourcePath = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, SWT.READ_ONLY
                    | TEXT_STYLE);
            this.txtResourcePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            ((GridData)this.txtResourcePath.getLayoutData()).verticalIndent += ((GridLayout)finalContainer.getLayout()).verticalSpacing;

            if (isEditMode()) {
                this.txtResourcePath.setText(getValidator().getResourcePath());
            }

            this.txtResourcePath.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateResourcePath();
                }
            });
        }

        VERSION: {
            toolkit.createLabel(finalContainer, Messages.versionLabel);

            this.txtVersion = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            this.txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            if (isEditMode()) {
                this.txtVersion.setText(String.valueOf(getValidator().getVersion()));
            }

            this.txtVersion.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateVersion();
                }
            });
        }

        DESCRIPTION: {
            Label label = toolkit.createLabel(finalContainer, Messages.descriptionLabel);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            this.txtDescription = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER | SWT.MULTI
                    | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
            this.txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            if (isEditMode()) {
                this.txtDescription.setText(getValidator().getDescription());
            }

            this.txtDescription.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateDescription();
                }
            });
        }

        // set error message controls
        this.descriptionError.widget = this.txtDescription;
        this.metamodelUriError.widget = this.cbxMetamodelUris;
        this.namespacePrefixError.widget = this.txtNamespacePrefix;
        this.namespaceUriError.widget = this.txtNamespaceUri;
        this.resourcePathError.widget = this.txtResourcePath;
        this.versionError.widget = this.txtVersion;
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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#setFocus()
     */
    @Override
    public void setFocus() {
        if (this.txtNamespacePrefix != null) {
            this.txtNamespacePrefix.setFocus();
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
        validateResourcePath();
        validateVersion();
    }

    void validateDescription() {
        this.descriptionError.message = ModelExtensionDefinitionValidator.validateDescription(this.txtDescription.getText());
        updateMessage(this.descriptionError);
    }

    void validateMetamodelUri() {
        this.metamodelUriError.message = ModelExtensionDefinitionValidator.validateMetamodelUri(this.cbxMetamodelUris.getText(),
                                                                                                getRegistry().getExtendableMetamodelUris());
        updateMessage(this.metamodelUriError);
    }

    void validateNamespacePrefix() {
        this.namespacePrefixError.message = ModelExtensionDefinitionValidator.validateNamespacePrefix(this.txtNamespacePrefix.getText(),
                                                                                                      getRegistry().getAllNamespacePrefixes());
        updateMessage(this.namespacePrefixError);
    }

    void validateNamespaceUri() {
        this.namespaceUriError.message = ModelExtensionDefinitionValidator.validateNamespaceUri(this.txtNamespaceUri.getText(),
                                                                                                getRegistry().getAllNamespaceUris());
        updateMessage(this.namespaceUriError);
    }

    void validateResourcePath() {
        this.resourcePathError.message = ModelExtensionDefinitionValidator.validateResourcePath(this.txtResourcePath.getText());
        updateMessage(this.resourcePathError);
    }

    void validateVersion() {
        this.versionError.message = ModelExtensionDefinitionValidator.validateVersion(this.txtVersion.getText());
        updateMessage(this.versionError);
    }

}
