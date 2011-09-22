/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_OVERVIEW_PAGE;

import java.util.Set;

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
import com.metamatrix.modeler.internal.ui.forms.FormUtil;

/**
 * 
 */
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

    public OverviewEditorPage( FormEditor medEditor ) {
        super(medEditor, MED_OVERVIEW_PAGE, Messages.medEditorOverviewPageTitle);
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
        body.setLayout(FormUtil.createFormGridLayout(false, 2));

        Section section = toolkit.createSection(body, ExpandableComposite.NO_TITLE | ExpandableComposite.TITLE_BAR);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        section.setClient(container);

        NAMESPACE_PREFIX: {
            toolkit.createLabel(container, Messages.namespacePrefixLabel);

            this.txtNamespacePrefix = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER);
            this.txtNamespacePrefix.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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

        final Text txtNamespaceUri;

        NAMESPACE_URI: {
            toolkit.createLabel(container, Messages.namespaceUriLabel);

            this.txtNamespaceUri = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER);
            txtNamespaceUri = this.txtNamespaceUri;
            this.txtNamespaceUri.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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
            toolkit.createLabel(container, Messages.extendedMetamodelUriLabel);

            this.cbxMetamodelUris = new CCombo(container, SWT.FLAT | SWT.READ_ONLY | SWT.BORDER);
            toolkit.adapt(this.cbxMetamodelUris, true, false);

            // populate URIs
            Set<String> items = ExtensionPlugin.getInstance().getRegistry().getExtendableMetamodelUris();
            this.cbxMetamodelUris.setItems(items.toArray(new String[items.size()]));
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
                    cbx.setSize(txtNamespaceUri.getSize());
                }
            });
        }

        RESOURCE_PATH: {
            toolkit.createLabel(container, Messages.resourcePathLabel);

            this.txtResourcePath = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.READ_ONLY | SWT.BORDER);
            this.txtResourcePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            ((GridData)this.txtResourcePath.getLayoutData()).verticalIndent += ((GridLayout)container.getLayout()).verticalSpacing;
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
            toolkit.createLabel(container, Messages.versionLabel);

            this.txtVersion = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER);
            this.txtVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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
            Label label = toolkit.createLabel(container, Messages.descriptionLabel);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            this.txtDescription = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER | SWT.MULTI
                    | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
            this.txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
                                                                                                getRegistry());
        updateMessage(this.metamodelUriError);
    }

    void validateNamespacePrefix() {
        this.namespacePrefixError.message = ModelExtensionDefinitionValidator.validateNamespacePrefix(this.txtNamespacePrefix.getText());
        updateMessage(this.namespacePrefixError);
    }

    void validateNamespaceUri() {
        this.namespaceUriError.message = ModelExtensionDefinitionValidator.validateNamespaceUri(this.txtNamespaceUri.getText());
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
