/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.EventObject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelWorkspaceTreeProvider;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.webservice.util.WebServiceBuildOptions;
import com.metamatrix.modeler.webservice.util.WebServiceEditObject;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.Label;

/**
 * The <code>InterfaceDefinitionPage</code> allows the user to edit Webservice models and their interfaces and operations.
 */
public class InterfaceDefinitionPage extends WizardPage
    implements EventObjectListener, IInternalUiConstants, IInternalUiConstants.HelpContexts {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(InterfaceDefinitionPage.class);

    private final static String BROWSE_TEXT = getString("browseButton.text"); //$NON-NLS-1$

    private final static String SELECT_MODEL_TITLE = getString("selectModelDialog.title"); //$NON-NLS-1$
    private final static String SELECT_MODEL_MSG = getString("selectModelDialog.msg"); //$NON-NLS-1$
    private final static String SELECT_INTERFACE_TITLE = getString("selectInterfaceDialog.title"); //$NON-NLS-1$
    private final static String SELECT_INTERFACE_MSG = getString("selectInterfaceDialog.msg"); //$NON-NLS-1$

    final static String INTERFACE_SELECTION_READ_ONLY_MESSAGE = getString("selectInterfaceDialog.selectionReadonly.msg"); //$NON-NLS-1$
    final static String INTERFACE_SELECTION_INVALID_MESSAGE = getString("selectInterfaceDialog.selectionInvalid.msg"); //$NON-NLS-1$

    /** business object for working with webservice models */
    private WebServiceEditObject webServiceEditObject;

    // Validator for entity names
    private StringNameValidator nameValidator = new StringNameValidator();

    private Text modelNameField;
    private Text interfaceNameField;
    private Text operationNameField;
    private Text operationInputElemNameField;
    private Text operationOutputElemNameField;
    private Text locationContainerText;

    private Button selectModelButton;
    private Button selectInterfaceButton;
    private Button browseButton;

    private ModifyListener modelNameChangeListener;
    private ModifyListener interfaceNameChangeListener;
    private ModifyListener operationNameChangeListener;
    private ModifyListener operationInputMessageNameChangeListener;
    private ModifyListener operationOutputMessageNameChangeListener;

    private XsdElementChooserPanel operationInputElemChooser;
    private CLabel operationOutputElemLabel;
    private Font standardFont;
    private Font boldFont;

    // Need to cache the initial selected object
    private ISelection selection;

    /**
     * Constructor for WebServiceDefinitionPage
     * 
     * @param The current ISelection selection
     */
    public InterfaceDefinitionPage( ISelection selection ) {
        super("interfaceDefinitionPage"); //$NON-NLS-1$
        setTitle(getString("title")); //$NON-NLS-1$
        setDescription(getString("pageDefault.msg")); //$NON-NLS-1$
        this.webServiceEditObject = new WebServiceEditObject();
        this.selection = selection;
    }

    public WebServiceBuildOptions getWebServiceBuildOptions() {
        return this.webServiceEditObject.getWebServiceBuildOptions();
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        Composite primaryContainer = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        primaryContainer.setLayout(new GridLayout(1, false));
        setControl(primaryContainer);

        // add a Location field and BROWSE button to select target project or folder
        // Make sure this isn't displayed in DIMENSION by checking for hiddenProjectCentric
        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            addLocationComposite(primaryContainer);
        }

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(primaryContainer, INTERFACE_DEFINITION_PAGE);

        // Create the EditPanel - RHS of splitter
        createEditPanel(primaryContainer);

        initialize();

        updateUi(null);
    }

    /**
     * Create/add the location widgets to the dialog
     * 
     * @param container the parent Composite
     * @since 5.0
     */
    private void addLocationComposite( Composite container ) {
        Composite locationComposite = new Composite(container, SWT.NULL);
        GridData locationCompositeGridData = new GridData(GridData.FILL_BOTH);
        locationComposite.setLayoutData(locationCompositeGridData);
        GridLayout locationLayout = new GridLayout();
        locationLayout.numColumns = 3;
        locationComposite.setLayout(locationLayout);

        // Instruction label.
        Label locationMsg = new Label(locationComposite, SWT.NULL);
        GridData locationMsgGridData = new GridData();
        locationMsgGridData.horizontalSpan = 3;
        locationMsg.setLayoutData(locationMsgGridData);
        locationMsg.setText(getString("locationMessage")); //$NON-NLS-1$
        locationContainerText = new Text(locationComposite, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        locationContainerText.setLayoutData(gd);
        locationContainerText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                setPageStatus();
            }
        });
        locationContainerText.setEditable(false);

        browseButton = new Button(locationComposite, SWT.PUSH);
        GridData buttonGridData = new GridData();
        // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleBrowse();
            }
        });
    }

    /**
     * Create the EditPanel for the webservice model and its contents
     * 
     * @param parent the parent composite
     */
    private void createEditPanel( Composite parent ) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);

        // ------------------------------------------
        // Top message - styled text
        // ------------------------------------------
        String messageString = getString("messageLabel.text"); //$NON-NLS-1$
        StyledText messageText = new StyledText(panel, SWT.READ_ONLY);
        messageText.setBackground(panel.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        messageText.setText(messageString);

        // ------------------------------------------
        // Top Creation Composite
        // ------------------------------------------
        createTopCreationComposite(panel);

        // ------------------------------------------
        // Operation Properties Panel
        // ------------------------------------------
        Composite opDefnPanel = WidgetFactory.createPanel(panel, SWT.NONE, GridData.FILL_BOTH);

        // Operation Definition Group
        String operationDefnGroupText = getString("operationDefnGroupText.text"); //$NON-NLS-1$
        final Group operationDefnGroup = WidgetFactory.createGroup(opDefnPanel, operationDefnGroupText, GridData.FILL_BOTH);
        {
            createOperationPropertiesComposite(operationDefnGroup);
        }

        // --------------------------------------------
        // Top message - set red and blue styled text
        // --------------------------------------------
        messageText.setFont(this.standardFont);

        StyleRange sRange1 = new StyleRange();
        String boldBlueStr = "Bold Blue"; //$NON-NLS-1$
        int startIndex = messageString.indexOf(boldBlueStr);
        int strLength = boldBlueStr.length();
        sRange1.start = startIndex;
        sRange1.length = strLength;
        sRange1.fontStyle = SWT.BOLD;
        sRange1.foreground = messageText.getDisplay().getSystemColor(SWT.COLOR_BLUE);
        messageText.setStyleRange(sRange1);

        StyleRange sRange2 = new StyleRange();
        String boldRedStr = "Bold Red"; //$NON-NLS-1$
        startIndex = messageString.indexOf(boldRedStr);
        strLength = boldRedStr.length();
        sRange2.start = startIndex;
        sRange2.length = strLength;
        sRange2.fontStyle = SWT.BOLD;
        sRange2.foreground = messageText.getDisplay().getSystemColor(SWT.COLOR_RED);
        messageText.setStyleRange(sRange2);

        // put focus in the inputElemName Field
        this.operationInputElemNameField.forceFocus();
    }

    /**
     * Create the Composite for creating new webservices Models and their interfaces and operations.
     * 
     * @param parent the parent composite
     */
    private void createTopCreationComposite( Composite parent ) {
        // Set up Composite
        Composite topComposite = new Composite(parent, SWT.NONE);
        GridLayout topCompositeLayout = new GridLayout();
        topComposite.setLayout(topCompositeLayout);
        topCompositeLayout.numColumns = 3;
        topCompositeLayout.marginWidth = 0;
        GridData topCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        topCompositeGridData.horizontalIndent = 20;
        topComposite.setLayoutData(topCompositeGridData);

        // ------------------------------------------------
        // WebServices Model Composite
        // ------------------------------------------------

        // Label - Col 1
        String labelText = getString("modelComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(topComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // Model name entry field - col 2
        this.modelNameField = WidgetFactory.createTextField(topComposite, GridData.FILL_HORIZONTAL);

        // Setup standard and bold fonts
        this.standardFont = this.modelNameField.getFont();
        FontData boldData = this.standardFont.getFontData()[0];
        boldData.setStyle(SWT.BOLD);
        this.boldFont = GlobalUiFontManager.getFont(boldData);

        /** ModelNameChangeListener */
        modelNameChangeListener = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleModelNameChanged();
            }
        };

        this.modelNameField.addModifyListener(modelNameChangeListener);

        // Select Model Button - col 3
        this.selectModelButton = WidgetFactory.createButton(topComposite, BROWSE_TEXT);
        selectModelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                selectModelButtonClicked();
            }
        });

        // ------------------------------------------------
        // Interface Composite
        // ------------------------------------------------

        // Title Label - col 1
        labelText = getString("interfaceComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(topComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // interface name entry field - col 2
        this.interfaceNameField = WidgetFactory.createTextField(topComposite, GridData.FILL_HORIZONTAL);

        /** InterfaceNameChangeListener */
        interfaceNameChangeListener = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleInterfaceNameChanged();
            }
        };

        this.interfaceNameField.addModifyListener(interfaceNameChangeListener);

        // Select Model Button - col 3
        this.selectInterfaceButton = WidgetFactory.createButton(topComposite, BROWSE_TEXT);
        selectInterfaceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                selectInterfaceButtonClicked();
            }
        });

        // ------------------------------------------------
        // Operation Composite
        // ------------------------------------------------

        // Title Label - col 1
        labelText = getString("operationComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(topComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // operation name entry field - col 2
        this.operationNameField = WidgetFactory.createTextField(topComposite, GridData.FILL_HORIZONTAL, 2);

        /** OperationNameChangeListener */
        operationNameChangeListener = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleOperationNameChanged();
            }
        };

        this.operationNameField.addModifyListener(operationNameChangeListener);
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field. Possible containers are
     * folder type objects (i.e. Projects & folders)
     */
    void handleBrowse() {
        final IContainer locationContainer = WidgetUtil.showFolderSelectionDialog((IContainer)getTargetContainer(),
                                                                                  new ModelingResourceFilter(),
                                                                                  new ModelProjectSelectionStatusValidator());

        if (locationContainer != null && locationContainerText != null) {
            locationContainerText.setText(locationContainer.getFullPath().makeRelative().toString());
            this.webServiceEditObject.setLocationContainer(locationContainer);
        }

        setPageStatus();
    }

    /**
     * handler for changes to the modelName TextField
     */
    void handleModelNameChanged() {
        String newModelName = this.modelNameField.getText();

        // If model already exists, set current model to typed modelName
        if (this.webServiceEditObject.modelExistsInCurrentProject(newModelName)) {
            ModelResource model = this.webServiceEditObject.getModelFromCurrentProject(newModelName);
            this.webServiceEditObject.setCurrentWebServiceModel(model);
            // Model changed, so change the location path with existing model
            updateLocationPath(model);

            // Model doesnt exist, set current model to modelName string
        } else {
            this.webServiceEditObject.setCurrentWebServiceModel(newModelName);
        }

        // update the ui
        updateUi(this.modelNameField);
    }

    /**
     * handler for changes to the InterfaceName TextField
     */
    void handleInterfaceNameChanged() {
        String newInterfaceName = this.interfaceNameField.getText();

        // Set the interfaceName and update the ui
        this.webServiceEditObject.setCurrentInterfaceName(newInterfaceName);
        updateUi(this.interfaceNameField);
    }

    /**
     * handler for changes to the OperationName TextField
     */
    void handleOperationNameChanged() {
        String newOperationName = this.operationNameField.getText();

        // Set the operationName and update the ui
        this.webServiceEditObject.setCurrentOperationName(newOperationName);
        updateUi(this.operationNameField);
    }

    /**
     * handler for model browse button clicked
     */
    void selectModelButtonClicked() {
        ModelWorkspaceTreeProvider provider = new ModelWorkspaceTreeProvider();
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), provider, provider);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        dialog.setAllowMultiple(false);
        // Must select a Relationship Model
        MetamodelDescriptor descriptor = WebServiceUtil.getWebServiceModelDescriptor();
        dialog.setValidator(new ModelResourceSelectionValidator(descriptor, false));
        dialog.addFilter(wsModelFilter);
        dialog.setTitle(SELECT_MODEL_TITLE);
        dialog.setMessage(SELECT_MODEL_MSG);

        if (dialog.open() == Window.OK) {
            Object[] selection = dialog.getResult();
            if ((selection.length == 1) && (selection[0] instanceof IFile)) {
                IFile sourceFile = (IFile)selection[0];
                ModelResource selectedModel = null;
                modelNameField.setText(""); //$NON-NLS-1$
                boolean exceptionOccurred = false;
                try {
                    selectedModel = ModelUtilities.getModelResource(sourceFile, true);
                    // Update the location path to coincide with the parent of the selected model
                    updateLocationPath(selectedModel);
                } catch (Exception ex) {
                    ModelerCore.Util.log(ex);
                    exceptionOccurred = true;
                }
                if (!exceptionOccurred) {
                    String modelName = ModelerCore.getModelEditor().getModelName(selectedModel);
                    modelNameField.setText(modelName);
                }
            }
        }
    }

    /** Filter for showing just open projects and their folders and webservice models */
    private ViewerFilter wsModelFilter = new ViewerFilter() {
        @Override
        public boolean select( Viewer theViewer,
                               Object theParent,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)theElement).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                    if (theElement instanceof IProject) {
                        result = true;
                        // Show webservice model files, and not .xsd files
                    } else if (theElement instanceof IFile && ModelUtil.isModelFile((IFile)theElement)) {
                        ModelResource theModel = null;
                        try {
                            theModel = ModelUtilities.getModelResource((IFile)theElement, true);
                        } catch (Exception ex) {
                            ModelerCore.Util.log(ex);
                        }
                        if (WebServiceUtil.isWebServiceModelResource(theModel)) {
                            result = true;
                        }
                    }
                }
            }

            return result;
        }
    };

    /**
     * handler for interface browse button clicked
     */
    void selectInterfaceButtonClicked() {

        // Show the Workspaceobject selection dialog
        final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(SELECT_INTERFACE_TITLE,
                                                                                  SELECT_INTERFACE_MSG,
                                                                                  true,
                                                                                  null,
                                                                                  interfaceFilter,
                                                                                  interfaceValidator,
                                                                                  new ModelExplorerLabelProvider(),
                                                                                  new ModelExplorerContentProvider());

        // Update the ui with the new interface selection
        if (selections.length == 1) {
            Object interfaceObj = selections[0];
            // Set the interfaceName and update the ui
            this.webServiceEditObject.setCurrent(interfaceObj);
            updateUi(this.interfaceNameField);
        }
    }

    /**
     * filter to show interfaces in open projects
     */
    final ViewerFilter interfaceFilter = new ModelWorkspaceViewerFilter(true) {

        @Override
        public boolean select( final Viewer viewer,
                               final Object parent,
                               final Object element ) {
            boolean doSelect = false;
            if (element instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)element).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                    if (element instanceof IProject) {
                        doSelect = true;
                    } else if (element instanceof IContainer) {
                        doSelect = true;
                        // Show webservice model files, and not .xsd files
                    } else if (element instanceof IFile && ModelUtil.isModelFile((IFile)element)) {
                        ModelResource theModel = null;
                        try {
                            theModel = ModelUtilities.getModelResource((IFile)element, true);
                        } catch (Exception ex) {
                            ModelerCore.Util.log(ex);
                        }
                        if (WebServiceUtil.isWebServiceModelResource(theModel)) {
                            doSelect = true;
                        }
                    }
                }
            } else if (element instanceof IContainer) {
                doSelect = true;
            } else if (element instanceof EObject && WebServiceUtil.isWebServiceInterface(element)) {
                doSelect = true;
            }

            return doSelect;
        }
    };

    /**
     * validator for interface selection dialog
     */
    final ISelectionStatusValidator interfaceValidator = new ISelectionStatusValidator() {

        public IStatus validate( final Object[] selection ) {
            if (selection.length == 1 && selection[0] instanceof EObject && WebServiceUtil.isWebServiceInterface(selection[0])) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)selection[0]);
                if (mr != null && mr.isReadOnly()) return new Status(IStatus.ERROR, PLUGIN_ID, 0,
                                                                     INTERFACE_SELECTION_READ_ONLY_MESSAGE, null);

                return new Status(IStatus.OK, PLUGIN_ID, 0, "", null); //$NON-NLS-1$
            }

            return new Status(IStatus.ERROR, PLUGIN_ID, 0, INTERFACE_SELECTION_INVALID_MESSAGE, null);
        }
    };

    /**
     * Create the composite which contain the controls for modifying operation properties
     * 
     * @param parent the parent composite
     * @return the created composite
     */
    private Composite createOperationPropertiesComposite( Composite parent ) {
        // Set up Composite

        Composite operationDefinitionComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, 2);

        GridData operationDefnCompositeGridData = new GridData(GridData.FILL_BOTH);
        operationDefnCompositeGridData.horizontalIndent = 20;
        operationDefinitionComposite.setLayoutData(operationDefnCompositeGridData);

        // ------------------------------------------------
        // Operation Input Element
        // ------------------------------------------------

        // Operation Input Element Label - Col 1
        String labelText = getString("operationInputElemComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(operationDefinitionComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // Operation Input Elemeent Chooser - Col 2
        this.operationInputElemChooser = new XsdElementChooserPanel(operationDefinitionComposite);
        this.operationInputElemChooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // Add this as listener for InputElement changes
        this.operationInputElemChooser.addEventListener(this);

        // ------------------------------------------------
        // Operation Input Name
        // ------------------------------------------------

        // Label - Col 1
        labelText = getString("operationInputElemNameComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(operationDefinitionComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // operation name entry field - col 2
        this.operationInputElemNameField = WidgetFactory.createTextField(operationDefinitionComposite,
                                                                         GridData.HORIZONTAL_ALIGN_FILL);

        /** InputMessageNameChangeListener */
        operationInputMessageNameChangeListener = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleOperationInputMessageNameChanged();
            }
        };

        this.operationInputElemNameField.addModifyListener(operationInputMessageNameChangeListener);

        // ------------------------------------------------
        // Operation Output Document Element
        // ------------------------------------------------

        // Label - Col 1
        labelText = getString("operationOutputDocComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(operationDefinitionComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // Operation Output Elemeent Chooser - Col 2
        this.operationOutputElemLabel = WidgetFactory.createLabel(operationDefinitionComposite, GridData.HORIZONTAL_ALIGN_FILL, 1);

        // ------------------------------------------------
        // Operation Output Name
        // ------------------------------------------------

        // Label - Col 1
        labelText = getString("operationOutputDocNameComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(operationDefinitionComposite, GridData.HORIZONTAL_ALIGN_FILL, 1, labelText);

        // operation name entry field - col 2
        this.operationOutputElemNameField = WidgetFactory.createTextField(operationDefinitionComposite,
                                                                          GridData.HORIZONTAL_ALIGN_FILL);

        /** InputMessageNameChangeListener */
        operationOutputMessageNameChangeListener = new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleOperationOutputMessageNameChanged();
            }
        };

        this.operationOutputElemNameField.addModifyListener(operationOutputMessageNameChangeListener);

        return operationDefinitionComposite;
    }

    /**
     * Handler for changes in the operation InputMessage name field
     */
    void handleOperationInputMessageNameChanged() {
        String newOperationInputElemName = this.operationInputElemNameField.getText();

        // set the name on the business object, update the apply button state
        this.webServiceEditObject.setOperationInputMessageName(newOperationInputElemName);

        Display display = this.operationInputElemNameField.getDisplay();
        if (!isValidName(newOperationInputElemName)) {
            this.operationInputElemNameField.setFont(boldFont);
            this.operationInputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else {
            this.operationInputElemNameField.setFont(standardFont);
            this.operationInputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        }

        setPageStatus();
    }

    /**
     * Handler for changes in the operation OutputMessage name field
     */
    void handleOperationOutputMessageNameChanged() {
        String newOperationOutputElemName = this.operationOutputElemNameField.getText();

        // set the name on the business object, update the apply button state
        this.webServiceEditObject.setOperationOutputMessageName(newOperationOutputElemName);

        Display display = this.operationOutputElemNameField.getDisplay();
        if (!isValidName(newOperationOutputElemName)) {
            this.operationOutputElemNameField.setFont(boldFont);
            this.operationOutputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else {
            this.operationOutputElemNameField.setFont(standardFont);
            this.operationOutputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        }

        setPageStatus();
    }

    /**
     * Utility to get localized text.
     * 
     * @param theKey the key whose value is being localized
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    void setPageStatus() {
        // refresh page message
        IStatus status = this.webServiceEditObject.validate();

        // If Status severity is error, set error message and disable completion
        if (status.getSeverity() == IStatus.ERROR) {
            setMessage(status.getMessage(), IMessageProvider.ERROR);
            setPageComplete(false);
            // If Status severity is info or warning, set message but enable completion
        } else if (status.getSeverity() == IStatus.INFO || status.getSeverity() == IStatus.WARNING) {
            setMessage(status.getMessage(), IMessageProvider.WARNING);
            setPageComplete(true);
            // otherwise, set message to default and enable completion
        } else {
            setMessage(getString("pageDefault.msg"), IMessageProvider.NONE); //$NON-NLS-1$
            setPageComplete(true);
        }
    }

    /**
     * Sets the initial workspace selection.
     * 
     * @param theSelection the current workspace selection
     */
    public void setInitialSelection( ISelection theSelection ) {
        this.selection = theSelection;
    }

    /**
     * Updates the entire ui, refreshing from the business object
     */
    private void updateUi( Object updateSource ) {
        // This should not result in any events being fired, just update the Ui
        this.modelNameField.removeModifyListener(modelNameChangeListener);
        this.interfaceNameField.removeModifyListener(interfaceNameChangeListener);
        this.operationNameField.removeModifyListener(operationNameChangeListener);

        String modelName = this.webServiceEditObject.getCurrentWebServiceModelName();
        String interfaceName = this.webServiceEditObject.getCurrentInterfaceName();
        String operationName = this.webServiceEditObject.getCurrentOperationName();

        if (updateSource != this.modelNameField) {
            this.modelNameField.setText(modelName);
        }

        Display display = this.modelNameField.getDisplay();
        if (this.webServiceEditObject.modelExistsInCurrentProject(modelName)) {
            this.modelNameField.setFont(boldFont);
            this.modelNameField.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
            // Need to disable the "browse" button for the location
            browseButton.setEnabled(false);
            // Change model to NOT use location container when generating WS operations (i.e. use existing model)
            this.webServiceEditObject.setUseLocationContainer(false);
        } else if (!isValidName(modelName)) {
            this.modelNameField.setFont(boldFont);
            this.modelNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
            // Need to enable the "browse" button for the location since we're not using existing model
            browseButton.setEnabled(true);
            // Change model to use location container when generating New WS Model
            this.webServiceEditObject.setUseLocationContainer(true);
        } else {
            this.modelNameField.setFont(standardFont);
            this.modelNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            // Need to enable the "browse" button for the location since we're not using existing model
            browseButton.setEnabled(true);
            // Change model to use location container when generating New WS Model
            this.webServiceEditObject.setUseLocationContainer(true);
        }

        if (this.webServiceEditObject.interfaceExistsInCurrentModel(interfaceName)) {
            this.interfaceNameField.setFont(boldFont);
            this.interfaceNameField.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        } else if (!isValidName(interfaceName)) {
            this.interfaceNameField.setFont(boldFont);
            this.interfaceNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else {
            this.interfaceNameField.setFont(standardFont);
            this.interfaceNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        }

        if (updateSource != this.interfaceNameField) {
            this.interfaceNameField.setText(interfaceName);
        }

        if (updateSource != this.operationNameField) {
            this.operationNameField.setText(operationName);
        }

        if (this.webServiceEditObject.operationExistsInCurrentInterface(operationName)) {
            this.operationNameField.setFont(boldFont);
            this.operationNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else if (!isValidName(operationName)) {
            this.operationNameField.setFont(boldFont);
            this.operationNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else {
            this.operationNameField.setFont(standardFont);
            this.operationNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        }

        updateCurrentOperationDetails();

        setPageStatus();

        this.modelNameField.addModifyListener(modelNameChangeListener);
        this.interfaceNameField.addModifyListener(interfaceNameChangeListener);
        this.operationNameField.addModifyListener(operationNameChangeListener);
    }

    /**
     * Updates the Operation Details panel, refreshing from the business object
     */
    private void updateCurrentOperationDetails() {
        this.operationInputElemNameField.removeModifyListener(operationInputMessageNameChangeListener);
        this.operationOutputElemNameField.removeModifyListener(operationOutputMessageNameChangeListener);
        this.operationInputElemChooser.removeEventListener(this);

        String inputName = this.webServiceEditObject.getCurrentOperationInputName();

        String outputName = this.webServiceEditObject.getCurrentOperationOutputName();
        // Object operationOutput = this.webServiceEditorUtil.getCurrentOperationOutput();
        XSDElementDeclaration outputElem = this.webServiceEditObject.getCurrentOperationOutputElem();

        this.operationInputElemNameField.setText(inputName);
        this.operationOutputElemNameField.setText(outputName);

        Display display = this.operationInputElemNameField.getDisplay();
        if (!isValidName(inputName)) {
            this.operationInputElemNameField.setFont(boldFont);
            this.operationInputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else {
            this.operationInputElemNameField.setFont(standardFont);
            this.operationInputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        }
        if (!isValidName(outputName)) {
            this.operationOutputElemNameField.setFont(boldFont);
            this.operationOutputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_RED));
        } else {
            this.operationOutputElemNameField.setFont(standardFont);
            this.operationOutputElemNameField.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        }

        // OutputElement Label
        String text = getElementText(outputElem);
        Image image = getElementImage(outputElem);
        this.operationOutputElemLabel.setText(text);
        this.operationOutputElemLabel.setImage(image);

        this.operationInputElemNameField.addModifyListener(operationInputMessageNameChangeListener);
        this.operationOutputElemNameField.addModifyListener(operationOutputMessageNameChangeListener);
        this.operationInputElemChooser.addEventListener(this);
    }

    /**
     * get text for the provided object
     */
    private String getElementText( Object object ) {
        String result = "<No Selection>"; //$NON-NLS-1$
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getText(object);
            }
        }
        return result;
    }

    /**
     * get image for the provided object
     */
    private Image getElementImage( Object object ) {
        Image result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getImage(object);
            }
        }
        return result;
    }

    /**
     * check a name for validity. If the string is non-zero length it's character string is checked.
     * 
     * @param name the supplied name to check
     * @return 'true' if the name is valid, 'false' if not.
     */
    private boolean isValidName( String name ) {
        boolean isValid = true;
        if (name != null && name.length() > 0) {
            // Validate the typed name
            isValid = nameValidator.isValidName(name);
        }
        return isValid;
    }

    /**
     * Initialize the ui with the provided selection.
     * 
     * @param selection the workbench selection.
     */
    private void initialize() { // ISelection selection) {
        // Must be single selection
        if (SelectionUtilities.isSingleSelection(selection)) {
            final Object sel = SelectionUtilities.getSelectedObject(selection);
            // --------------------------------------------------------
            // Selection is XML Document -
            // get XMLRoot and create WebService Model from it
            // --------------------------------------------------------
            if (this.webServiceEditObject.isXmlDocument(sel)) {
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((XmlDocument)sel);
                ModelProject modelProject = modelResource.getModelProject();
                this.webServiceEditObject = new WebServiceEditObject(modelProject);

                // Get the XmlRoot and use it to Create new Model
                XmlRoot xmlRoot = ((XmlDocument)sel).getRoot();
                this.webServiceEditObject.setDefaultsUsingXmlRoot(xmlRoot);
                // initialize the Location to be the parent of the original model resource
                updateLocationPath(modelResource);
                // --------------------------------------------------------
                // Selection is XML Document Root -
                // create WebService Model from it
                // --------------------------------------------------------
            } else if (this.webServiceEditObject.isXmlRoot(sel)) {
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((XmlRoot)sel);
                ModelProject modelProject = modelResource.getModelProject();
                this.webServiceEditObject = new WebServiceEditObject(modelProject);

                // Use the XmlRoot to Create new Model
                XmlRoot xmlRoot = ((XmlRoot)sel);
                this.webServiceEditObject.setDefaultsUsingXmlRoot(xmlRoot);
                // initialize the Location to be the parent of the original model resource
                updateLocationPath(modelResource);
                // --------------------------------------------------------
                // Selection is XML Document Root -
                // create WebService Model from it
                // --------------------------------------------------------
            } else if (sel instanceof IFile) {
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)sel);
                boolean isWebServiceModel = WebServiceUtil.isWebServiceModelResource(modelResource);
                if (isWebServiceModel) {
                    this.webServiceEditObject.setCurrent(modelResource);
                }
                // initialize the Location to be the parent of the original model resource
                updateLocationPath(modelResource);
            }
        }
    }

    /**
     * Method that handles Events from the ElementChooser panels
     * 
     * @param e the EventObject
     */
    public void processEvent( EventObject e ) {
        Object source = e.getSource();
        if (source != null) {
            if (source.equals(this.operationInputElemChooser)) {
                XSDElementDeclaration selectedElem = this.operationInputElemChooser.getSelectedElement();
                // set the InputElement on the business object, update the apply button state
                this.webServiceEditObject.setOperationInputMessageElem(selectedElem);
                // Check the InputMessage name, may set it also
                String currentInputMsgName = this.webServiceEditObject.getCurrentOperationInputName();
                if (currentInputMsgName == null || currentInputMsgName.length() == 0) {
                    String elemName = selectedElem.getName();
                    this.webServiceEditObject.setOperationInputMessageName(elemName);
                }
                updateCurrentOperationDetails();
                setPageStatus();
            }
        }
    }

    /**
     * Helper method to keep set the location path to the parent of the provided model resource. Also keeps the
     * WebServiceEditObject in syc by setting location container object.
     * 
     * @param modelResource
     * @since 5.0
     */
    private void updateLocationPath( ModelResource modelResource ) {
        try {
            IContainer container = modelResource.getCorrespondingResource().getParent();
            if (locationContainerText != null) {
                locationContainerText.setText(container.getFullPath().makeRelative().toString());
            }
            // setParentPath(container);
            this.webServiceEditObject.setLocationContainer(container);
        } catch (ModelWorkspaceException theException) {
            UTIL.log(theException);
        }
    }

    /**
     * Helper method used by the Browse... location button to get the actual IResource (i.e. container) object represented by the
     * location text widget. This enables initial selection the the File system dialog.
     * 
     * @return
     * @since 5.0
     */
    private IResource getTargetContainer() {
        IResource result = null;
        String containerName = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

            if (hiddenProj != null) {
                containerName = hiddenProj.getFullPath().makeRelative().toString();
            }
        } else {
            containerName = locationContainerText.getText().trim();
        }

        if (containerName != null && !StringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource;
            }
        }

        return result;
    }
}
