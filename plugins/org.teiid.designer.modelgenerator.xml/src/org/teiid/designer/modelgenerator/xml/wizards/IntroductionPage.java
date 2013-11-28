/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.PluginUtil;
import org.teiid.designer.modelgenerator.xml.IUiConstants;
import org.teiid.designer.modelgenerator.xml.XmlImporterUiPlugin;
import org.teiid.designer.modelgenerator.xml.model.UserSettings;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * RootElementsPage is the wizard page contribution for building Virtual XMLDocument models from XML Schema files in the
 * workspace.
 *
 * @since 8.0
 */

public class IntroductionPage extends WizardPage implements IUiConstants, IUiConstants.HelpContexts, IUiConstants.Images {

    // ///////////////////////////////////////////////////////////////////////////////
    // Instance variables
    // ///////////////////////////////////////////////////////////////////////////////
    private IntroductionPanel panel;
    private XsdAsRelationalImportWizard wizard;
    private StateManager manager;

    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    // ///////////////////////////////////////////////////////////////////////////////
    // Constructors
    // ///////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor for RootElementsPage.
     * 
     * @param pageName
     */
    public IntroductionPage( XsdAsRelationalImportWizard wizard ) {
        super(IntroductionPage.class.getSimpleName());
        setTitle(util.getString("IntroductionPage.title")); //$NON-NLS-1$
        setDescription(util.getString("IntroductionPage.description")); //$NON-NLS-1$
        this.wizard = wizard;
        this.manager = wizard.getStateManager();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // Instance methods
    // ///////////////////////////////////////////////////////////////////////////////
    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
	public void createControl( Composite parent ) {
        panel = new IntroductionPanel(parent, wizard, manager);
        setControl(panel);
    }

    @Override
    public void dispose() {
        super.dispose();
        Control c = getControl();
        if (c != null) {
            c.dispose();
        } // endif
    }
}

class IntroductionPanel extends Composite implements IUiConstants, IUiConstants.HelpContexts, IUiConstants.Images {
    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    private static final String REQUEST_RESPONSE_OPTIONS = util.getString("IntroductionPage.xmlSourceTypes"); //$NON-NLS-1$
    private static final String SOURCE_TYPE_DOCUMENT = util.getString("IntroductionPage.source.document"); //$NON-NLS-1$
    private static final String SOURCE_TYPE_HTTP_NO_PARAMS = util.getString("IntroductionPage.source.httpNoParams"); //$NON-NLS-1$
    private static final String SOURCE_TYPE_HTTP_PARAMS = util.getString("IntroductionPage.source.httpParams"); //$NON-NLS-1$
    private static final String SOURCE_TYPE_HTTP_REQUEST_DOC = util.getString("IntroductionPage.source.httpDocRequest"); //$NON-NLS-1$
    private static final String NO_CATALOG = getLocalString("noCatalog"); //$NON-NLS-1$
    private static final String NAMESPACE_CATALOG = getLocalString("namespaceCatalog"); //$NON-NLS-1$
    private static final String FILENAME_CATALOG = getLocalString("fileNameCatalog"); //$NON-NLS-1$
    private static final String CUSTOM_CATALOG = getLocalString("customCatalog"); //$NON-NLS-1$
    private static final String CATALOG_DESC = getLocalString("catalogDescription"); //$NON-NLS-1$
    private static final String INCLUDE_GROUP = getLocalString("box.title"); //$NON-NLS-1$

    private Button documentRadioButton;
    private Button httpNoParamsRadioButton;
    private Button httpParamsRadioButton;
    private Button httpRequestDocRadioButton;
    private Button noCatalogButton;
    private Button fileNameCatalogButton;
    private Button nameSpaceCatalogButton;
    private Button customCatalogButton;
    Text customCatalogText;
    UserSettings userSettings;
    String m_custName = new String();

    StateManager manager;

    public IntroductionPanel( Composite parent,
                              XsdAsRelationalImportWizard wizard,
                              StateManager manager ) {
        super(parent, SWT.NULL);
        this.manager = manager;
        userSettings = wizard.getUserSettings();
        initialize();
    }

    private void initialize() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        this.setLayout(layout);

        Group typeGroup = new Group(this, SWT.NONE);
        typeGroup.setLayout(new GridLayout(1, true));
        typeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        typeGroup.setText(REQUEST_RESPONSE_OPTIONS);

        documentRadioButton = new Button(typeGroup, SWT.RADIO);
        documentRadioButton.setSelection(StateManager.SOURCE_DOCUMENT == userSettings.getSourceType());
        documentRadioButton.setText(SOURCE_TYPE_DOCUMENT);

        httpNoParamsRadioButton = new Button(typeGroup, SWT.RADIO);
        httpNoParamsRadioButton.setSelection(StateManager.SOURCE_HTTP_NO_PARAMS == userSettings.getSourceType());
        httpNoParamsRadioButton.setText(SOURCE_TYPE_HTTP_NO_PARAMS);

        httpParamsRadioButton = new Button(typeGroup, SWT.RADIO);
        httpParamsRadioButton.setSelection(StateManager.SOURCE_HTTP_PARAMS == userSettings.getSourceType());
        httpParamsRadioButton.setText(SOURCE_TYPE_HTTP_PARAMS);

        httpRequestDocRadioButton = new Button(typeGroup, SWT.RADIO);
        httpRequestDocRadioButton.setSelection(StateManager.SOURCE_HTTP_REQUEST_DOC == userSettings.getSourceType());
        httpRequestDocRadioButton.setText(SOURCE_TYPE_HTTP_REQUEST_DOC);

        documentRadioButton.addSelectionListener(new SelectionListener() {
            @Override
			public void widgetSelected( SelectionEvent e ) {
                userSettings.setSourceType(StateManager.SOURCE_DOCUMENT);
            }

            @Override
			public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        httpNoParamsRadioButton.addSelectionListener(new SelectionListener() {
            @Override
			public void widgetSelected( SelectionEvent e ) {
                userSettings.setSourceType(StateManager.SOURCE_HTTP_NO_PARAMS);
            }

            @Override
			public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        httpParamsRadioButton.addSelectionListener(new SelectionListener() {
            @Override
			public void widgetSelected( SelectionEvent e ) {
                userSettings.setSourceType(StateManager.SOURCE_HTTP_PARAMS);
            }

            @Override
			public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        httpRequestDocRadioButton.addSelectionListener(new SelectionListener() {
            @Override
			public void widgetSelected( SelectionEvent e ) {
                userSettings.setSourceType(StateManager.SOURCE_HTTP_REQUEST_DOC);
            }

            @Override
			public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        final int columnCount = 3;
        final Group catalogGroup = WidgetFactory.createGroup(this, INCLUDE_GROUP, GridData.HORIZONTAL_ALIGN_FILL, columnCount, 2);

        CLabel catLabel = new CLabel(catalogGroup, SWT.WRAP);
        catLabel.setText(CATALOG_DESC);
        GridData labelData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        labelData.horizontalSpan = 2;
        catLabel.setLayoutData(labelData);

        noCatalogButton = WidgetFactory.createRadioButton(catalogGroup, NO_CATALOG);
        // GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL, GridData.BEGINNING, true);
        noCatalogButton.setSelection(true);
        noCatalogButton.addSelectionListener(new NoCatalogAdapter());
        GridData noCatData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        noCatData.horizontalAlignment = GridData.BEGINNING;
        noCatData.horizontalSpan = 2;
        noCatalogButton.setLayoutData(noCatData);

        nameSpaceCatalogButton = WidgetFactory.createRadioButton(catalogGroup, NAMESPACE_CATALOG);
        nameSpaceCatalogButton.addSelectionListener(new NamespaceCatalogAdapter());
        GridData nsCatData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        nsCatData.horizontalAlignment = GridData.BEGINNING;
        nsCatData.horizontalSpan = 2;
        nameSpaceCatalogButton.setLayoutData(nsCatData);

        fileNameCatalogButton = WidgetFactory.createRadioButton(catalogGroup, FILENAME_CATALOG);
        fileNameCatalogButton.addSelectionListener(new FileNameCatalogAdapter());
        GridData fnCatData = new GridData(GridData.FILL_HORIZONTAL);
        fnCatData.horizontalAlignment = GridData.BEGINNING;
        fnCatData.horizontalSpan = 2;
        fileNameCatalogButton.setLayoutData(fnCatData);

        customCatalogButton = WidgetFactory.createRadioButton(catalogGroup, CUSTOM_CATALOG);
        customCatalogButton.addSelectionListener(new CustomCatalogAdapter());
        GridData customBtnData = new GridData();
        customBtnData.horizontalAlignment = GridData.BEGINNING;
        customCatalogButton.setLayoutData(customBtnData);

        customCatalogText = WidgetFactory.createTextField(catalogGroup, GridData.BEGINNING, m_custName);
        customCatalogText.setEnabled(false);
        GridData catTextData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        catTextData.widthHint = 50;
        customCatalogText.setLayoutData(catTextData);
        customCatalogText.addModifyListener(new CustomTextListener());

    }

    @Override
    public void setVisible( boolean visible ) {
        switch (manager.getCatalogType()) {

            case XsdAsRelationalImportWizard.NO_CATALOG_VAL:
                noCatalogButton.setSelection(true);
                customCatalogText.setEnabled(false);
                break;
            case XsdAsRelationalImportWizard.NAMESPACE_CATALOG_VAL:
                nameSpaceCatalogButton.setSelection(true);
                customCatalogText.setEnabled(false);
                break;
            case XsdAsRelationalImportWizard.FILENAME_CATALOG_VAL:
                fileNameCatalogButton.setSelection(true);
                customCatalogText.setEnabled(true);
                break;
            case XsdAsRelationalImportWizard.CUSTOM_CATALOG_VAL:
                customCatalogButton.setSelection(true);
                customCatalogText.setEnabled(true);
        }

        super.setVisible(visible);
        if (visible) {
        }
    }

    private static String getLocalString( String string ) {
        String qualifiedName = "IntroductionPage." + string; //$NON-NLS-1$
        return LocalMessages.getString(qualifiedName);
    }

    void catalogButtonSelected( int type ) {
        manager.setCatalogType(type);
    }

    class NoCatalogAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
            catalogButtonSelected(XsdAsRelationalImportWizard.NO_CATALOG_VAL);
            customCatalogText.setEnabled(false);
        }
    }

    class NamespaceCatalogAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
            catalogButtonSelected(XsdAsRelationalImportWizard.NAMESPACE_CATALOG_VAL);
            customCatalogText.setEnabled(false);
        }
    }

    class FileNameCatalogAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
            catalogButtonSelected(XsdAsRelationalImportWizard.FILENAME_CATALOG_VAL);
            customCatalogText.setEnabled(false);
        }
    }

    class CustomCatalogAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
            catalogButtonSelected(XsdAsRelationalImportWizard.CUSTOM_CATALOG_VAL);
            customCatalogText.setEnabled(true);
        }
    }

    class CustomTextListener implements ModifyListener {

        @Override
		public void modifyText( ModifyEvent event ) {
            // TODO: do some validation
            m_custName = customCatalogText.getText();
            manager.setCustomCatalogName(m_custName);
        }
    }
}
