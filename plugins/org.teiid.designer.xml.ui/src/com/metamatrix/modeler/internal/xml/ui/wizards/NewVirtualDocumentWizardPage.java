/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xsd.validator.XsdResourceValidator;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationDescriptor;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.mapping.factory.CompositorBasedBuilderStrategy;
import com.metamatrix.modeler.internal.mapping.factory.IterationBasedBuilderStrategy;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.xml.factory.IDocumentsAndFragmentsPopulator;
import com.metamatrix.modeler.internal.xml.factory.VirtualDocumentModelPopulator;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;
import com.metamatrix.modeler.ui.viewsupport.ModelWorkspaceTreeProvider;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;
import com.metamatrix.modeler.xml.PluginConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatedValuesChangeListener;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * NewVirtualDocumentWizardPage is the wizard page contribution for building Virtual XMLDocument models from XML Schema files in
 * the workspace.
 */

public class NewVirtualDocumentWizardPage extends WizardPage implements ModelerXmlUiConstants, IVirtualDocumentFragmentSource {

    private static boolean includeXMLDocuments = true;
    private static boolean includeXMLFragments = false;

    /**
     * Return the static includeXMLDocuments flag
     * 
     * @return setting of the includeXMLDocuments flag
     */
    public static boolean getIncludeXMLDocuments() {
        return includeXMLDocuments;
    }

    /**
     * Set the static includeXMLDocuments flag
     * 
     * @param flag true or false
     */
    public static void setIncludeXMLDocuments( boolean flag ) { // NO_UCD
        includeXMLDocuments = flag;
    }

    /**
     * Return the static includeXMLFragments flag
     * 
     * @return setting of the includeXMLFragments flag
     */
    public static boolean getIncludeXMLFragments() {
        return includeXMLFragments;
    }

    /**
     * Set the static includeXMLFragments flag
     * 
     * @param flag true or false
     */
    public static void setIncludeXMLFragments( boolean flag ) { // NO_UCD
        includeXMLFragments = flag;
    }

    private ISelection selection;
    NewVirtualDocumentWizardPanel panel;
    NewDocumentWizardModel model;
    private XSDElementDeclaration[] docRoots;
    private MappingClassBuilderStrategy strategy;

    /**
     * Constructor for NewVirtualDocumentWizardPage.
     * 
     * @param pageName
     */
    public NewVirtualDocumentWizardPage( NewDocumentWizardModel model,
                                         ISelection selection ) {
        super("specifyVirtualDocumentPage"); //$NON-NLS-1$
        setTitle(Util.getString("NewVirtualDocumentWizardPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("NewVirtualDocumentWizardPage.description")); //$NON-NLS-1$
        this.selection = selection;
        this.model = model;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite parent ) {
        model.setWizHolder(parent);

        panel = new NewVirtualDocumentWizardPanel(parent, model, this);

        // if a builder strategy has been set let the panel know
        if (this.strategy != null) {
            this.panel.setModelBuilderStrategy(this.strategy);
        }

        setControl(panel);
        if (this.selection != null) {
            final Object obj = SelectionUtilities.getSelectedObject(this.selection);
            if ((obj instanceof IFile) && isSchemaFile((IFile)obj)) {
                final XSDElementDeclaration[] roots = this.docRoots;

                // Set up to use a wait-cursor
                Runnable runnable = new Runnable() {
                    public void run() {

                        final boolean startedTxn = ModelerCore.startTxn(false,
                                                                        false,
                                                                        "Create Document Model Populator", NewVirtualDocumentWizardPage.this); //$NON-NLS-1$
                        boolean success = false;

                        try {
                            VirtualDocumentModelPopulator populator = new VirtualDocumentModelPopulator((IFile)obj);

                            // if document roots have been identified set them on the populator
                            if ((roots != null) && (roots.length != 0)) {
                                populator.setSelectedDocuments(Arrays.asList(roots));
                            }

                            panel.setPopulator(populator);
                            success = true;
                        } finally {
                            if (startedTxn) {
                                if (success) {
                                    ModelerCore.commitTxn();
                                } else {
                                    ModelerCore.rollbackTxn();
                                }
                            }
                        }
                    }
                };
                Display.getCurrent().asyncExec(runnable);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Control c = getControl();
        if (c != null) {
            c.dispose();
        } // endif
    }

    public IDocumentsAndFragmentsPopulator getPopulator() {
        return panel.getPopulator();
    }

    private boolean isSchemaFile( IFile file ) {
        return ModelUtil.isXsdFile(file);
    }

    /**
     * Sets the XSD element roots to create in the new model.
     * 
     * @param theXsdRoots the element roots
     * @since 5.0.2
     */
    public void setXsdRoots( XSDElementDeclaration[] theXsdRoots ) {
        if ((theXsdRoots != null) && (theXsdRoots.length != 0)) {
            this.docRoots = theXsdRoots;
        }
    }

    public XmlFragment[] getFragments( ModelResource modelResource,
                                       IProgressMonitor monitor ) {
        XmlFragment[] rv;

        IDocumentsAndFragmentsPopulator populator = getPopulator();
        if (populator != null) {
            // let the populator create the document
            final boolean[] buildEntire = new boolean[1];
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    // hack to change a variable in a anonymous class:
                    buildEntire[0] = model.getBuildEntireDocuments();
                }
            });

            // do not build mapping classes yet (done at completion of wizard, if necessary)
            rv = populator.buildModel(modelResource, buildEntire[0], false, model.getMappingClassBuilderStrategy(), monitor);
            model.setEstimatedNodeCount(populator.getLastEstimatedNodeCount());
            model.setReferencedResources(populator.getUnhandledModelImports());
        } else {
            // just create a single new document:
            XmlDocumentFactory factory = XmlDocumentFactory.eINSTANCE;
            // return an array of length 1:
            XmlFragment fragment = factory.createXmlDocument();
            String sDefaultName = Util.getString("XMLDocumentWizard.defaultXmlDocumentName"); //$NON-NLS-1$
            fragment.setName(sDefaultName);

            // add a root:
            XmlRoot docRoot = factory.createXmlRoot();
            docRoot.setName(Util.getString("XMLDocumentWizard.defaultXmlRootName")); //$NON-NLS-1$
            fragment.setRoot(docRoot);

            model.setSelectedFragmentCount(0); // nothing is selected in accumulator, even though 1 doc will be present
            rv = new XmlFragment[] {fragment};
            model.setEstimatedNodeCount(2);
            monitor.worked(1);
        } // endif

        return rv;
    }

    public void updateSourceFragments( boolean isVisible,
                                       IProgressMonitor monitor ) {
        // do nothing; this is the source for other pages.
    }

    public void setMappingClassBuilderStrategy( MappingClassBuilderStrategy theStrategy ) {
        this.strategy = theStrategy;

        if (this.panel != null) {
            this.panel.setModelBuilderStrategy(theStrategy);
        }
    }

}// end NewVirtualDocumentWizardPage

class NewVirtualDocumentWizardPanel extends ScrolledComposite implements ModelerXmlUiConstants, IAccumulatedValuesChangeListener {

    static final String NO_SCHEMA_SELECTED = Util.getString("NewVirtualDocumentWizardPage.noSchemaSelected"); //$NON-NLS-1$
    static final String MUST_SELECT_SCHEMA = Util.getString("NewVirtualDocumentWizardPage.mustSelectSchema"); //$NON-NLS-1$
    private static final String SELECT_SCHEMA_TITLE = Util.getString("NewVirtualDocumentWizardPage.selectSchemaTitle"); //$NON-NLS-1$
    private static final String SELECT_SCHEMA_MSG = Util.getString("NewVirtualDocumentWizardPage.selectSchemaMsg"); //$NON-NLS-1$
    private static final String BUILD_MAPPING_CLASSES = Util.getString("NewVirtualDocumentWizardPage.buildMappingClasses"); //$NON-NLS-1$
    private static final String BUILD_ENTIRE_DOCUMENTS = Util.getString("NewVirtualDocumentWizardPage.buildEntireDocuments"); //$NON-NLS-1$
    private static final String BUILD_GLOBAL_ONLY = Util.getString("NewVirtualDocumentWizardPage.buildGlobalOnly"); //$NON-NLS-1$
    private static final String MAPPING_OPTIONS = Util.getString("NewVirtualDocumentWizardPage.mappingOptions"); //$NON-NLS-1$
    private static final String DOCUMENT_OPTIONS = Util.getString("NewVirtualDocumentWizardPage.documentOptions"); //$NON-NLS-1$
    private static final String USE_STRING = Util.getString("NewVirtualDocumentWizardPage.useString"); //$NON-NLS-1$
    private static final String USE_SCHEMA_TYPE = Util.getString("NewVirtualDocumentWizardPage.useSchemaType"); //$NON-NLS-1$

    private static final String DATATYPE_OPTIONS = Util.getString("NewVirtualDocumentWizardPage.datatypeOptions"); //$NON-NLS-1$
    private static final String STRATEGY_OPTIONS = Util.getString("NewVirtualDocumentWizardPage.strategyOptions"); //$NON-NLS-1$

    private final static String BROWSE_SHORTHAND = "..."; //$NON-NLS-1$
    private final static int FILE_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);
    private static final int FILE_NAME_TEXT_HEIGHT = 16;
    private final static int ACCUMULATOR_RESET_BUTTON_VERTICAL_MARGIN = 4;

    public final static int DOCUMENTS = 1;
    public final static int FRAGMENTS = 2;

    private Text fileNameText;
    private Button browseButton;
    private TableViewer documentsListViewer;
    private TableViewer fragmentsListViewer = null;
    private AccumulatorPanel documentsAccumulatorPanel = null;
    private AccumulatorPanel fragmentsAccumulatorPanel = null;
    private ILabelProvider accumulatorsLabelProvider;
    private IDocumentsAndFragmentsPopulator populator = null;
    Button buildEntireDocumentsButton;
    Button buildGlobalOnlyButton;
    Button useSchemaTypeButton;
    private Button useStringTypeButton;
    Button buildMappingClassesButton;
    Button iterationStragetyButton;
    Button compositorStrategyButton;
    NewDocumentWizardModel model;
    private WizardPage page;

    public NewVirtualDocumentWizardPanel( Composite parent,
                                          NewDocumentWizardModel model,
                                          WizardPage page ) {
        super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        this.model = model;
        this.page = page;
        initialize();
    }

    private void initialize() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        this.setLayout(layout);
        GridData mainGridData = new GridData();
        this.setLayoutData(mainGridData);

        // customize scroll bars to give better scrolling behavior
        ScrollBar bar = getHorizontalBar();

        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        }

        bar = getVerticalBar();

        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        }

        Composite pnlMain = WidgetFactory.createPanel(this, SWT.NONE, GridData.FILL_BOTH);
        setContent(pnlMain);

        /*
         * XSD File selection group
         */
        Composite fileComposite = new Composite(pnlMain, SWT.NONE);
        GridLayout fileCompositeLayout = new GridLayout();
        fileComposite.setLayout(fileCompositeLayout);
        fileCompositeLayout.numColumns = 3;
        fileCompositeLayout.marginWidth = 0;
        fileComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label schemaNameLabel = new Label(fileComposite, SWT.NONE);
        schemaNameLabel.setText(Util.getString("NewVirtualDocumentWizardPage.xmlSchemaFileLabel")); //$NON-NLS-1$

        fileNameText = WidgetFactory.createTextField(fileComposite, SWT.NONE, null);
        fileNameText.setEditable(false);
        GridData fileNameTextGridData = new GridData();
        fileNameTextGridData.widthHint = FILE_NAME_TEXT_WIDTH;
        fileNameTextGridData.heightHint = FILE_NAME_TEXT_HEIGHT;
        fileNameText.setLayoutData(fileNameTextGridData);

        browseButton = new Button(fileComposite, SWT.PUSH);
        browseButton.setText(BROWSE_SHORTHAND);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                browseButtonClicked();
            }
        });

        accumulatorsLabelProvider = ModelUtilities.getEMFLabelProvider();

        if (NewVirtualDocumentWizardPage.getIncludeXMLDocuments()) {
            Group documentsAccumulatorComposite = new Group(pnlMain, SWT.NONE);
            String documentsGroupName = Util.getString("NewVirtualDocumentWizardPage.virtualXMLDocumentsLabel"); //$NON-NLS-1$
            documentsAccumulatorComposite.setText(documentsGroupName);
            GridLayout documentsAccumulatorCompositeLayout = new GridLayout();
            documentsAccumulatorComposite.setLayout(documentsAccumulatorCompositeLayout);
            documentsAccumulatorCompositeLayout.marginWidth = 0;
            documentsAccumulatorCompositeLayout.marginHeight = 2;
            GridData accumulatorGridData = new GridData(GridData.FILL_BOTH);
            documentsAccumulatorComposite.setLayoutData(accumulatorGridData);

            IAccumulatorSource documentsAccumulatorSource = new NewVirtualDocumentAccumulatorSource(this, DOCUMENTS);
            String documentsAvailableHdr = Util.getString("NewVirtualDocumentWizardPage.documentsAccumulatorLeftLabel"); //$NON-NLS-1$
            String documentsSelectedHdr = Util.getString("NewVirtualDocumentWizardPage.documentsAccumulatorRightLabel"); //$NON-NLS-1$
            documentsAccumulatorPanel = new AccumulatorPanel(documentsAccumulatorComposite, documentsAccumulatorSource,
                                                             new ArrayList(), accumulatorsLabelProvider, documentsAvailableHdr,
                                                             documentsSelectedHdr, ACCUMULATOR_RESET_BUTTON_VERTICAL_MARGIN, -1,
                                                             -1, -1);
            documentsAccumulatorPanel.addAccumulatedValuesChangeListener(this);
        }

        if (NewVirtualDocumentWizardPage.getIncludeXMLFragments()) {
            Group fragmentsAccumulatorComposite = new Group(pnlMain, SWT.NONE);
            fragmentsAccumulatorComposite.setText(Util.getString("NewVirtualDocumentWizardPage.virtualXMLFragmentsLabel")); //$NON-NLS-1$
            GridLayout fragmentsAccumulatorCompositeLayout = new GridLayout();
            fragmentsAccumulatorComposite.setLayout(fragmentsAccumulatorCompositeLayout);
            fragmentsAccumulatorCompositeLayout.marginWidth = 0;
            fragmentsAccumulatorCompositeLayout.marginHeight = 2;
            GridData accumulatorGridData = new GridData(GridData.FILL_HORIZONTAL);
            fragmentsAccumulatorComposite.setLayoutData(accumulatorGridData);

            IAccumulatorSource fragmentsAccumulatorSource = new NewVirtualDocumentAccumulatorSource(this, FRAGMENTS);
            String fragmentsAvailableHdr = Util.getString("NewVirtualDocumentWizardPage.fragmentsAccumulatorLeftLabel"); //$NON-NLS-1$
            String fragmentsSelectedHdr = Util.getString("NewVirtualDocumentWizardPage.fragmentsAccumulatorRightLabel"); //$NON-NLS-1$
            fragmentsAccumulatorPanel = new AccumulatorPanel(fragmentsAccumulatorComposite, fragmentsAccumulatorSource,
                                                             new ArrayList(), accumulatorsLabelProvider, fragmentsAvailableHdr,
                                                             fragmentsSelectedHdr, ACCUMULATOR_RESET_BUTTON_VERTICAL_MARGIN, -1,
                                                             -1, -1);
            fragmentsAccumulatorPanel.addAccumulatedValuesChangeListener(this);
        }

        Group group = new Group(pnlMain, SWT.NONE);
        group.setLayout(new GridLayout(1, true));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText(DOCUMENT_OPTIONS);

        buildEntireDocumentsButton = new Button(group, SWT.RADIO);
        buildEntireDocumentsButton.setSelection(true);
        buildEntireDocumentsButton.setText(BUILD_ENTIRE_DOCUMENTS);

        buildGlobalOnlyButton = new Button(group, SWT.RADIO);
        buildGlobalOnlyButton.setText(BUILD_GLOBAL_ONLY);

        Group typeGroup = new Group(pnlMain, SWT.NONE);
        typeGroup.setLayout(new GridLayout(1, true));
        typeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        typeGroup.setText(MAPPING_OPTIONS);

        buildMappingClassesButton = new Button(typeGroup, SWT.CHECK);
        buildMappingClassesButton.setText(BUILD_MAPPING_CLASSES);
        buildMappingClassesButton.setSelection(true);

        Group strategyGroup = new Group(typeGroup, SWT.NONE);
        strategyGroup.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalIndent = 10;
        strategyGroup.setLayoutData(gd);
        strategyGroup.setText(STRATEGY_OPTIONS);

        MappingClassBuilderStrategy strategy = this.model.getMappingClassBuilderStrategy();
        if(strategy==null) {
        	strategy = MappingClassFactory.getDefaultStrategy();
        }

        boolean isCompositorStrategy = (strategy instanceof CompositorBasedBuilderStrategy);

        iterationStragetyButton = new Button(strategyGroup, SWT.RADIO);
        iterationStragetyButton.setText(MappingClassBuilderStrategy.iterationStrategyDescription);

        compositorStrategyButton = new Button(strategyGroup, SWT.RADIO);
        compositorStrategyButton.setText(MappingClassBuilderStrategy.compositorStrategyDescription);

        if(isCompositorStrategy) {
            compositorStrategyButton.setSelection(true);
            iterationStragetyButton.setSelection(false);
        } else {
        	compositorStrategyButton.setSelection(false);
        	iterationStragetyButton.setSelection(true);
        }

        iterationStragetyButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (iterationStragetyButton.getSelection()) {
                    model.setMappingClassBuilderStrategy(MappingClassBuilderStrategy.iterationStrategy);
                    MappingClassFactory.setDefaultStrategy(MappingClassBuilderStrategy.iterationStrategy);
                    updateComponentEnabledStates();
                }
            }
        });

        compositorStrategyButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (compositorStrategyButton.getSelection()) {
                    model.setMappingClassBuilderStrategy(MappingClassBuilderStrategy.compositorStrategy);
                    MappingClassFactory.setDefaultStrategy(MappingClassBuilderStrategy.compositorStrategy);
                    updateComponentEnabledStates();
                }
            }
        });

        Group datatypeGroup = new Group(typeGroup, SWT.NONE);
        datatypeGroup.setLayout(new GridLayout(1, true));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalIndent = 10;
        datatypeGroup.setLayoutData(gd);
        datatypeGroup.setText(DATATYPE_OPTIONS);

        useSchemaTypeButton = new Button(datatypeGroup, SWT.RADIO);
        useSchemaTypeButton.setSelection(true);
        useSchemaTypeButton.setText(USE_SCHEMA_TYPE);

        useStringTypeButton = new Button(datatypeGroup, SWT.RADIO);
        useStringTypeButton.setSelection(false);
        useStringTypeButton.setText(USE_STRING);

        useSchemaTypeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (model.getUseSchemaTypes() != useSchemaTypeButton.getSelection()) {
                    model.setUseSchemaTypes(useSchemaTypeButton.getSelection());
                    updateComponentEnabledStates();
                }
            }
        });

        useStringTypeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (model.getUseSchemaTypes() != useSchemaTypeButton.getSelection()) {
                    model.setUseSchemaTypes(useSchemaTypeButton.getSelection());
                    updateComponentEnabledStates();
                }
            }
        });

        // set initial states:
        model.setBuildEntireDocuments(buildEntireDocumentsButton.getSelection());
        model.setBuildGlobalOnly(buildGlobalOnlyButton.getSelection());
        model.setBuildMappingClasses(buildMappingClassesButton.getSelection());
        model.setMappingClassBuilderStrategy(MappingClassFactory.getDefaultStrategy());

        buildEntireDocumentsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (model.getBuildEntireDocuments() != buildEntireDocumentsButton.getSelection()) {
                    model.setBuildEntireDocuments(buildEntireDocumentsButton.getSelection());
                    model.setBuildGlobalOnly(buildGlobalOnlyButton.getSelection());
                    updateComponentEnabledStates();
                }
            }
        });

        buildGlobalOnlyButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (model.getBuildGlobalOnly() != buildGlobalOnlyButton.getSelection()) {
                    model.setBuildGlobalOnly(buildGlobalOnlyButton.getSelection());
                    model.setBuildEntireDocuments(buildEntireDocumentsButton.getSelection());
                    updateComponentEnabledStates();
                }
            }
        });

        buildMappingClassesButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (model.getBuildMappingClasses() != buildMappingClassesButton.getSelection()) {
                    model.setBuildMappingClasses(buildMappingClassesButton.getSelection());
                    updateComponentEnabledStates();
                }
            }
        });

        // need to size scroll panel
        Point pt = pnlMain.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        setMinWidth(pt.x);
        setMinHeight(pt.y);
        setExpandHorizontal(true);
        setExpandVertical(true);
    }

    protected void setModelBuilderStrategy( MappingClassBuilderStrategy theStrategy ) {
        this.model.setMappingClassBuilderStrategy(theStrategy);

        if ((this.compositorStrategyButton != null) && (theStrategy instanceof CompositorBasedBuilderStrategy)) {
            this.compositorStrategyButton.setSelection(true);
            this.iterationStragetyButton.setSelection(false);
            updateComponentEnabledStates();
        } else if ((this.iterationStragetyButton != null) && (theStrategy instanceof IterationBasedBuilderStrategy)) {
            this.iterationStragetyButton.setSelection(true);
            this.compositorStrategyButton.setSelection(false);
            updateComponentEnabledStates();
        }
    }

    void updateComponentEnabledStates() {
        buildMappingClassesButton.setEnabled(buildEntireDocumentsButton.getSelection());
        if (!buildEntireDocumentsButton.getSelection()) {
            buildMappingClassesButton.setSelection(false);
        }
        boolean enable = buildMappingClassesButton.getSelection();
        useSchemaTypeButton.setEnabled(enable);
        useStringTypeButton.setEnabled(enable);
        compositorStrategyButton.setEnabled(enable);
        iterationStragetyButton.setEnabled(enable);
    }

    void browseButtonClicked() {
        final IDocumentsAndFragmentsPopulator thePopulator = displaySelectorDialog();
        if (thePopulator != null) {

            // Set up to use a wait-cursor
            Runnable runnable = new Runnable() {
                public void run() {
                    setPopulator(thePopulator);
                }
            };
            UiBusyIndicator.showWhile(null, runnable);
        }
    }

    public void setPopulator( IDocumentsAndFragmentsPopulator populator ) {
        if (populator != null) {
            this.populator = populator;
            String itemName = populator.getItemName();
            // TODO: put some place holder in for the schema icon
            // fileNameText.setImage(fileImageProvider.getImage(populator.getItem()));
            fileNameText.setText(itemName);

            // validate the schema if necessary
            page.setErrorMessage(null);
            IResource resource = (IResource)populator.getItem();
            IEclipsePreferences currentPrefs = ModelerCore.getPreferences(ModelerCore.PLUGIN_ID);

            if (ResourcesPlugin.getWorkspace().isAutoBuilding()
                && currentPrefs.get(ValidationPreferences.XSD_MODEL_VALIDATION, "").equals(ValidationDescriptor.ERROR)) { //$NON-NLS-1$
                // should have already been validated
                if (errorMarkersExist(resource)) {
                    page.setErrorMessage(Util.getString("NewVirtualDocumentWizardPage.invalidSchema")); //$NON-NLS-1$
                    return;
                }
            } else {
                // validate the schema
                ValidationContext context = new ValidationContext(ModelerCore.PLUGIN_ID);
                XsdResourceValidator xsdValidator = new XsdResourceValidator();
                try {
                    XSDValidatorWithProgress validatorWithProgress = new XSDValidatorWithProgress(xsdValidator, resource, context);
                    page.getWizard().getContainer().run(false, true, validatorWithProgress);
                    if (errorMarkersExist(resource)) {
                        page.setErrorMessage(Util.getString("NewVirtualDocumentWizardPage.invalidSchema")); //$NON-NLS-1$
                        return;
                    }
                    // do not care about warnings
                    resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
                } catch (Exception e) {
                    Util.log(e);
                    page.setErrorMessage(Util.getString("NewVirtualDocumentWizardPage.invalidSchema")); //$NON-NLS-1$
                    return;
                }
            }

            if (NewVirtualDocumentWizardPage.getIncludeXMLDocuments()) {
                Collection availableDocuments = populator.getInitialAvailableDocuments();
                if (availableDocuments == null) {
                    availableDocuments = new ArrayList(0);
                }
                emptyList(documentsListViewer);
                Iterator it = availableDocuments.iterator();
                while (it.hasNext()) {
                    Object item = it.next();
                    documentsListViewer.add(item);
                }
                Collection selectedDocuments = populator.getSelectedDocuments();
                documentsAccumulatorPanel.repopulateSelectedItems(selectedDocuments);
                documentsAccumulatorPanel.availableItemsHaveChanged();
            }
            if (NewVirtualDocumentWizardPage.getIncludeXMLFragments()) {
                Collection availableFragments = populator.getInitialAvailableFragments();
                if (availableFragments == null) {
                    availableFragments = new ArrayList(0);
                }
                emptyList(fragmentsListViewer);
                Iterator it = availableFragments.iterator();
                while (it.hasNext()) {
                    Object item = it.next();
                    fragmentsListViewer.add(item);
                }
                Collection selectedFragments = populator.getSelectedFragments();
                fragmentsAccumulatorPanel.repopulateSelectedItems(selectedFragments);
                fragmentsAccumulatorPanel.availableItemsHaveChanged();
            }

            super.layout();

            model.setSelectedFragmentCount(getChosenCount());
        }
    }

    public void accumulatedValuesChanged( AccumulatorPanel source ) {
        model.setSelectedFragmentCount(getChosenCount());
        /* this code does nothing, since the setPageComplete method is commented out.
        		boolean anyValuesAccumulated = false;
        		if (documentsAccumulatorPanel != null) {
        			anyValuesAccumulated = (documentsAccumulatorPanel.getSelectedItems().size() > 0);
        		}
        		if (!anyValuesAccumulated) {
        			if (fragmentsAccumulatorPanel != null) {
        				anyValuesAccumulated = (fragmentsAccumulatorPanel.getSelectedItems().size() > 0);
        			}
        		}
        // end do-nothing code */
        // caller.setPageComplete(anyValuesAccumulated);
    }

    private void emptyList( TableViewer listViewer ) {
        Table list = listViewer.getTable();
        if (list != null) {
            int count = list.getItemCount();
            for (int i = count - 1; i >= 0; i--) {
                Object item = listViewer.getElementAt(i);
                listViewer.remove(item);
            }
        }
        model.setSelectedFragmentCount(getChosenCount());
    }

    private IDocumentsAndFragmentsPopulator displaySelectorDialog() {
        IDocumentsAndFragmentsPopulator result = null;

        ModelWorkspaceTreeProvider provider = new ModelWorkspaceTreeProvider();
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), provider, provider);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        ViewerFilter filter = new ModelingResourceFilter(new SchemaFileViewerFilter());
        dialog.addFilter(filter);
        dialog.setAllowMultiple(false);
        dialog.setValidator(new ISelectionStatusValidator() {
            public IStatus validate( Object[] selection ) {
                if (selection == null) {
                    return new StatusInfo(ModelerXmlUiConstants.PLUGIN_ID, IStatus.ERROR, NO_SCHEMA_SELECTED);
                } else if (selection.length != 1) {
                    return new StatusInfo(ModelerXmlUiConstants.PLUGIN_ID, IStatus.ERROR, NO_SCHEMA_SELECTED);
                } else if (!(selection[0] instanceof IFile)) {
                    return new StatusInfo(ModelerXmlUiConstants.PLUGIN_ID, IStatus.ERROR, MUST_SELECT_SCHEMA);
                } else {
                    return new StatusInfo(ModelerXmlUiConstants.PLUGIN_ID);
                }
            }
        });

        dialog.setTitle(SELECT_SCHEMA_TITLE);
        dialog.setMessage(SELECT_SCHEMA_MSG);

        if (dialog.open() == Window.OK) {
            Object[] selection = dialog.getResult();
            if ((selection.length == 1) && (selection[0] instanceof IFile)) {
                IFile schemaFile = (IFile)selection[0];
                result = new VirtualDocumentModelPopulator(schemaFile);
            }
        }

        return result;
    }

    public IDocumentsAndFragmentsPopulator getPopulator() {
        if (populator == null || populator.getSelectedDocuments().isEmpty()) {
            return null;
        }
        return populator;
    }

    private int getChosenCount() {
        int count = 0;
        if (documentsAccumulatorPanel != null) {
            count += documentsAccumulatorPanel.getSelectedItems().size();
        } // endif

        if (fragmentsAccumulatorPanel != null) {
            count += fragmentsAccumulatorPanel.getSelectedItems().size();
        } // endif

        return count;
    }

    public void documentsAccumulatedValuesRemoved( Collection values ) {
        model.setSelectedFragmentCount(getChosenCount());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            documentsListViewer.add(value);
        }
    }

    public void fragmentsAccumulatedValuesRemoved( Collection values ) {
        model.setSelectedFragmentCount(getChosenCount());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            fragmentsListViewer.add(value);
        }
    }

    public void documentsAccumulatedValuesAdded( Collection values ) {
        model.setSelectedFragmentCount(getChosenCount());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            documentsListViewer.remove(value);
        }
    }

    public void fragmentsAccumulatedValuesAdded( Collection values ) {
        model.setSelectedFragmentCount(getChosenCount());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            fragmentsListViewer.remove(value);
        }
    }

    public Collection getDocumentsAvailableValues() {
        int count = documentsListViewer.getTable().getItemCount();
        ArrayList values = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            Object value = documentsListViewer.getElementAt(i);
            values.add(value);
        }
        return values;
    }

    public Collection getFragmentsAvailableValues() {
        int count = fragmentsListViewer.getTable().getItemCount();
        ArrayList values = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            Object value = fragmentsListViewer.getElementAt(i);
            values.add(value);
        }
        return values;
    }

    public int getDocumentsAvailableValuesCount() {
        int count = documentsListViewer.getTable().getItemCount();
        return count;
    }

    public int getFragmentsAvailableValuesCount() {
        int count = fragmentsListViewer.getTable().getItemCount();
        return count;
    }

    public Collection getDocumentsSelectedAvailableValues() {
        int[] selectionIndices = documentsListViewer.getTable().getSelectionIndices();
        ArrayList selectedValues = new ArrayList(selectionIndices.length);
        for (int i = 0; i < selectionIndices.length; i++) {
            int index = selectionIndices[i];
            selectedValues.add(documentsListViewer.getElementAt(index));
        }
        return selectedValues;
    }

    public Collection getFragmentsSelectedAvailableValues() {
        int[] selectionIndices = fragmentsListViewer.getTable().getSelectionIndices();
        ArrayList selectedValues = new ArrayList(selectionIndices.length);
        for (int i = 0; i < selectionIndices.length; i++) {
            int index = selectionIndices[i];
            selectedValues.add(fragmentsListViewer.getElementAt(index));
        }
        return selectedValues;
    }

    public int getDocumentsSelectedAvailableValuesCount() {
        if (documentsListViewer != null) {
            int count = documentsListViewer.getTable().getSelectionCount();
            return count;
        } // endif

        return 0;
    }

    public int getFragmentsSelectedAvailableValuesCount() {
        if (fragmentsListViewer != null) {
            int count = fragmentsListViewer.getTable().getSelectionCount();
            return count;
        } // endif

        return 0;
    }

    public Control documentsCreateControl( Composite parent ) {
        documentsListViewer = new TableViewer(parent, SWT.MULTI);
        documentsListViewer.setLabelProvider(accumulatorsLabelProvider);
        return documentsListViewer.getControl();
    }

    public Control fragmentsCreateControl( Composite parent ) {
        fragmentsListViewer = new TableViewer(parent, SWT.MULTI);
        fragmentsListViewer.setLabelProvider(accumulatorsLabelProvider);
        return fragmentsListViewer.getControl();
    }

    public void documentsAddSelectionListener( SelectionListener listener ) {
        documentsListViewer.getTable().addSelectionListener(listener);
    }

    public void fragmentsAddSelectionListener( SelectionListener listener ) {
        fragmentsListViewer.getTable().addSelectionListener(listener);
    }

    public void selectedDocumentsChanged() {
        if (populator != null) {
            model.setSelectedFragmentCount(getChosenCount());
            Collection /*<Object>*/selectedDocuments = documentsAccumulatorPanel.getSelectedItems();
            populator.setSelectedDocuments(selectedDocuments);
        }
    }

    public void selectedFragmentsChanged() {
        if (populator != null) {
            model.setSelectedFragmentCount(getChosenCount());
            Collection /*<Object>*/selectedFragments = fragmentsAccumulatorPanel.getSelectedItems();
            populator.setSelectedFragments(selectedFragments);
        }
    }

    // public boolean getBuildEntireDocuments() {
    // return buildEntireDocumentsButton.getSelection();
    // }
    //    
    // public boolean getBuildMappingClasses() {
    // return buildMappingClassesButton.getSelection();
    // }
    //
    // public boolean getUseSchemaType() {
    // return useSchemaTypeButton.getSelection();
    // }
    //
    // public void setFragmentsChanged(boolean chg) {
    // fragChg = chg;
    // }
    //
    // public boolean haveFragmentsChanged() {
    // return fragChg;
    // }

    private boolean errorMarkersExist( IResource resource ) {
        if (resource.exists()) {
            IMarker[] markers = null;

            try {
                markers = ((IFile)resource).findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
            } catch (CoreException theException) {
                Util.log(theException);
            }

            if (markers != null && markers.length > 0) {
                for (int i = 0; i < markers.length; i++) {
                    int severity = markers[i].getAttribute(IMarker.SEVERITY, -1);
                    if (severity == IMarker.SEVERITY_ERROR) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    static class XSDValidatorWithProgress implements IRunnableWithProgress {
        private XsdResourceValidator validator;
        private IResource resource;
        private ValidationContext context;

        XSDValidatorWithProgress( XsdResourceValidator validator,
                                  IResource resource,
                                  ValidationContext context ) {
            XSDValidatorWithProgress.this.validator = validator;
            XSDValidatorWithProgress.this.resource = resource;
            XSDValidatorWithProgress.this.context = context;
        }

        // Implementation of the IRunnableWithProgress interface:
        public void run( IProgressMonitor monitor ) {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            } // endif
            monitor.beginTask(ModelerXmlUiConstants.Util.getString("XMLDocumentWizard.validateSchema"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
            try {
                Set resources = new HashSet(1);
                resources.add(resource);
                validator.validationStarted(resources, context);
                ModelBuildUtil.validateResource(monitor, resource, validator, context);
                validator.validationEnded(context);
            } finally {
                monitor.done();
            }
        }
    }
}// end NewVirtualDocumentWizardPanel

class NewVirtualDocumentAccumulatorSource implements IAccumulatorSource {

    private static final IStatus OK_STATUS = new StatusInfo(PluginConstants.PLUGIN_ID);

    private NewVirtualDocumentWizardPanel caller;
    private int whichAccumulator;

    public NewVirtualDocumentAccumulatorSource( NewVirtualDocumentWizardPanel cllr,
                                                int accType ) {
        super();
        this.caller = cllr;
        this.whichAccumulator = accType;
    }

    public void accumulatedValuesRemoved( Collection values ) {
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                caller.documentsAccumulatedValuesRemoved(values);
                caller.selectedDocumentsChanged();
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                caller.fragmentsAccumulatedValuesRemoved(values);
                caller.selectedFragmentsChanged();
                break;
        }
    }

    public void accumulatedValuesAdded( Collection values ) {
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                caller.documentsAccumulatedValuesAdded(values);
                caller.selectedDocumentsChanged();
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                caller.fragmentsAccumulatedValuesAdded(values);
                caller.selectedFragmentsChanged();
                break;
        }
    }

    public Collection getAvailableValues() {
        Collection values = null;
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                values = caller.getDocumentsAvailableValues();
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                values = caller.getFragmentsAvailableValues();
                break;
        }
        return values;
    }

    public int getAvailableValuesCount() {
        int count = -1;
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                count = caller.getDocumentsAvailableValuesCount();
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                count = caller.getFragmentsAvailableValuesCount();
                break;
        }
        return count;
    }

    public Collection getSelectedAvailableValues() {
        Collection values = null;
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                values = caller.getDocumentsSelectedAvailableValues();
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                values = caller.getFragmentsSelectedAvailableValues();
                break;
        }
        return values;
    }

    public int getSelectedAvailableValuesCount() {
        int count = -1;
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                count = caller.getDocumentsSelectedAvailableValuesCount();
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                count = caller.getFragmentsSelectedAvailableValuesCount();
                break;
        }
        return count;
    }

    public Control createControl( Composite parent ) {
        Control control = null;
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                control = caller.documentsCreateControl(parent);
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                control = caller.fragmentsCreateControl(parent);
                break;
        }
        return control;
    }

    public void addSelectionListener( SelectionListener listener ) {
        switch (whichAccumulator) {
            case NewVirtualDocumentWizardPanel.DOCUMENTS:
                caller.documentsAddSelectionListener(listener);
                break;
            case NewVirtualDocumentWizardPanel.FRAGMENTS:
                caller.fragmentsAddSelectionListener(listener);
                break;
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
     */
    public boolean supportsAddAll() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
     */
    public IStatus getSelectionStatus() {
        return OK_STATUS;
    }
}
