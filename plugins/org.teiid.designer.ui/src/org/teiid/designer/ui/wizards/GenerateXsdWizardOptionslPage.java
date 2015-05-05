/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.metamodels.xsd.XsdBuilderOptions;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;


/**
 * This page is used to capture the user preferences for Generation of XSD from Relational objects.
 *
 * @since 8.0
 */

public class GenerateXsdWizardOptionslPage extends WizardPage
    implements InternalUiConstants.Widgets, UiConstants {

    private static final String XSD_EXT = ".xsd"; //$NON-NLS-1$
    private static final String XMI_EXT = ".xmi"; //$NON-NLS-1$

    private final Collection unsavedResources = new HashSet();
    private final Collection invalidResources = new HashSet();
    private final Collection unvalidatedResources = new HashSet();

    private boolean validResources;
    private Button genSqlButton;
    private Button genInputButton;
    private Button doFlatOutputButton;
    private Button doFlatInputButton;
    Text outputNameText;
    Text inputNameText;
    Text wsNameText;
    private LinkedHashSet roots;
    String rootName;
    private String parentPath;

    private Text containerText;
    private ISelection selection;
    
    private IContainer folder;
    
    private boolean initializing = false;

    /**
     * Constructor for NewModelWizardSpecifyModelPage
     * 
     * @param The current ISelection selection
     */
    public GenerateXsdWizardOptionslPage( ISelection selection ) {
        super("optionsPage"); //$NON-NLS-1$
        setTitle(Util.getString("GenerateXsdWizard.title")); //$NON-NLS-1$
        setDescription(Util.getString("GenerateXsdWizard.optionsDesc")); //$NON-NLS-1$

        this.selection = selection;
        // initialize(selection);
    }

    private void setDefaults() {
        this.getControl().getDisplay().asyncExec(new Runnable() {

            @Override
			public void run() {
                // Set default names if singleSelection
                if (rootName != null) {
                    outputNameText.setText(rootName + Util.getString("GenerateXsdWizard.outSuffix")); //$NON-NLS-1$
                    inputNameText.setText(rootName + Util.getString("GenerateXsdWizard.inSuffix")); //$NON-NLS-1$
                    wsNameText.setText(rootName + Util.getString("GenerateXsdWizard.wsSuffix")); //$NON-NLS-1$
                }
            }

        });
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
	public void createControl( Composite parent ) {
    	DefaultScrolledComposite mainScrolledPanel = new DefaultScrolledComposite(parent);
        
        Composite container = mainScrolledPanel.getPanel();
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        // add a Location field and BROWSE button to select target project or folder

        Group locationGroup = WidgetFactory.createGroup(container, Util.getString("GenerateXsdWizard.locationGroup.label"), GridData.FILL_BOTH, 1, 3); //$NON-NLS-1$

        // Instruction label.
        Label locationMsg = new Label(locationGroup, SWT.NULL);
        GridData locationMsgGridData = new GridData();
        locationMsgGridData.horizontalSpan = 3;
        locationMsg.setLayoutData(locationMsgGridData);
        locationMsg.setText(Util.getString("GenerateXsdWizard.locationMessage")); //$NON-NLS-1$
        containerText = new Text(locationGroup, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( ModifyEvent e ) {
                checkStatus();
            }
        });
        containerText.setEditable(false);

        Button browseButton = new Button(locationGroup, SWT.PUSH);
        GridData buttonGridData = new GridData();
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText(Util.getString("GenerateXsdWizard.browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleBrowse();
            }
        });
        
//        Composite scrolledPanel = new Composite(container, SWT.NULL);
//        GridLayoutFactory.swtDefaults().margins(1, 1).applyTo(scrolledPanel);
        
        {
	        Group modelsGroup = WidgetFactory.createGroup(container, Util.getString("GenerateXsdWizard.modelDefinition"), GridData.FILL_BOTH, 1, 4); //$NON-NLS-1$
	        
	        // Control to prompt for creation of input docs for Procedures
	        genInputButton = new Button(modelsGroup, SWT.CHECK);
	        buttonGridData = new GridData();
	        buttonGridData.horizontalSpan = 1;
	        genInputButton.setLayoutData(buttonGridData);
	        genInputButton.setText(Util.getString("GenerateXsdWizard.generate")); //$NON-NLS-1$
	        genInputButton.setToolTipText(Util.getString("GenerateXsdWizard.generateInputSchemaModel")); //$NON-NLS-1$
	        genInputButton.setSelection(true);
	        genInputButton.addSelectionListener(new SelectionAdapter() {

	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	                handleGenInputButton();
	            }
	        });
	        
	        // Control to capture the name of the Input Doc model name.
	        Label inputNameLabel = new Label(modelsGroup, SWT.NULL);
	        inputNameLabel.setText("Input Schema"); //$NON-NLS-1$

	        inputNameText = new Text(modelsGroup, SWT.BORDER | SWT.SINGLE);
	        gd = new GridData(GridData.FILL_HORIZONTAL);
	        inputNameText.setLayoutData(gd);
	        inputNameText.addModifyListener(new ModifyListener() {

	            @Override
				public void modifyText( ModifyEvent e ) {
	                checkStatus();
	            }
	        });
	        
	        // Control to capture flat or nested XSD preference
	        doFlatInputButton = new Button(modelsGroup, SWT.CHECK);
	        buttonGridData = new GridData();
	        buttonGridData.horizontalSpan = 1;
	        doFlatInputButton.setLayoutData(buttonGridData);
	        doFlatInputButton.setText(Util.getString("GenerateXsdWizard.doFlat")); //$NON-NLS-1$
	        doFlatInputButton.setToolTipText(Util.getString("GenerateXsdWizard.doFlatInputTooltip"));  //$NON-NLS-1$
	        doFlatInputButton.setSelection(true);
	        doFlatInputButton.addSelectionListener(new SelectionAdapter() {

	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	                doFlatOutputButton.setSelection(doFlatInputButton.getSelection());
	            }
	        });
	        
	        GridData spGridData = null;
	        
	        Label spacer = new Label(modelsGroup, SWT.NULL);
	        spGridData = new GridData();
	        spGridData.horizontalSpan = 1;
	        spacer.setLayoutData(spGridData);
	        
	        // Control to capture the Model name to use
	        Label modelNameLabel = new Label(modelsGroup, SWT.NULL);
	        modelNameLabel.setText("Output Schema");
	        
	        outputNameText = new Text(modelsGroup, SWT.BORDER | SWT.SINGLE);
	        GridData outputGridData = new GridData(GridData.FILL_HORIZONTAL);
	        outputGridData.minimumWidth = 200;
	        outputNameText.setLayoutData(outputGridData);
	        outputNameText.addModifyListener(new ModifyListener() {
	
	            @Override
				public void modifyText( ModifyEvent e ) {
	                checkStatus();
	            }
	        });
	        
	        // Control to capture flat or nested XSD preference
	        doFlatOutputButton = new Button(modelsGroup, SWT.CHECK);
	        buttonGridData = new GridData();
	        buttonGridData.horizontalSpan = 1;
	        doFlatOutputButton.setLayoutData(buttonGridData);
	        doFlatOutputButton.setText(Util.getString("GenerateXsdWizard.doFlat")); //$NON-NLS-1$
	        doFlatOutputButton.setToolTipText(Util.getString("GenerateXsdWizard.doFlatOutputTooltip"));  //$NON-NLS-1$
	        doFlatOutputButton.setSelection(true);
	        doFlatOutputButton.addSelectionListener(new SelectionAdapter() {

	            @Override
	            public void widgetSelected( SelectionEvent e ) {
	                doFlatInputButton.setSelection(doFlatOutputButton.getSelection());
	            }
	        });
	        
	        spacer = new Label(modelsGroup, SWT.NULL);
	        spGridData = new GridData();
	        spGridData.horizontalSpan = 1;
	        spacer.setLayoutData(spGridData);
	        
	        // Control to capture the WebService Model name
	        Label wsNameLabel = new Label(modelsGroup, SWT.NULL);
	        wsNameLabel.setText("Web Service"); //$NON-NLS-1$

	        wsNameText = new Text(modelsGroup, SWT.BORDER | SWT.SINGLE);
	        gd = new GridData(GridData.FILL_HORIZONTAL);
	        wsNameText.setLayoutData(gd);
	        wsNameText.addModifyListener(new ModifyListener() {

	            @Override
				public void modifyText( ModifyEvent e ) {
	                checkStatus();
	            }
	        });
	        
	        // Control to prompt for generation of default Mapping class SQL
	        genSqlButton = new Button(modelsGroup, SWT.CHECK);
	        buttonGridData = new GridData();
	        buttonGridData.horizontalSpan = 1;
	        genSqlButton.setLayoutData(buttonGridData);
	        genSqlButton.setText(Util.getString("GenerateXsdWizard.genSql")); //$NON-NLS-1$
	        genSqlButton.setSelection(true);
        }

        setControl(container);
        checkStatus();
        outputNameText.setFocus();

        setDefaults();
        initialize();
        mainScrolledPanel.sizeScrolledPanel();
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleBrowse() {
        folder = WidgetUtil.showFolderSelectionDialog((IContainer)getTargetContainer(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && containerText != null) {
            containerText.setText(folder.getFullPath().makeRelative().toString());
            setParentPath(folder);
        }

        checkStatus();
    }

    void handleGenInputButton() {
        // If genInput, enable input model name field, else disable.
        inputNameText.setEnabled(genInputButton.getSelection());
        doFlatInputButton.setEnabled(genInputButton.getSelection());

        checkStatus();
    }

    /**
     * Tests if the current workbench selection is a suitable container to use. All selections must be Relational (Virtual or
     * Physical). All Tables and Procedure Results within the selection are added to the Collection of root objects to use for
     * building.
     */
    private void initialize() {
    	initializing = true;
        roots = new LinkedHashSet();
        if (SelectionUtilities.isEmptySelection(selection)) {
            return;
        } else if (SelectionUtilities.isSingleSelection(selection)) {
            final Object sel = SelectionUtilities.getSelectedObject(selection);
            processRootObject(sel, true);
            if (sel instanceof IFile) {
                final String name = ((IFile)sel).getName();
                if (name.endsWith(XMI_EXT)) {
                    rootName = name.substring(0, name.indexOf(XMI_EXT));
                } else {
                    rootName = name;
                }
            } else if (sel instanceof RelationalEntity) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject((RelationalEntity)sel);
                if (mr != null) {
                    final String name = mr.getItemName();
                    if (name.endsWith(XMI_EXT)) {
                        rootName = name.substring(0, name.indexOf(XMI_EXT));
                    } else {
                        rootName = name;
                    }
                } else {
                    rootName = ((RelationalEntity)sel).getName();
                }
            }

            if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
                IStructuredSelection ssel = (IStructuredSelection)selection;
                if (ssel.size() > 1) return;
                Object obj = ssel.getFirstElement();
                if (obj instanceof IResource) {
                    IContainer container;
                    if (obj instanceof IContainer) container = (IContainer)obj;
                    else container = ((IResource)obj).getParent();
                    if (containerText != null) {
                        containerText.setText(container.getFullPath().makeRelative().toString());
                    }
                    setParentPath(container);
                    folder = container;
                } else if (sel instanceof RelationalEntity) {
                    ModelResource mr = ModelUtilities.getModelResourceForModelObject((RelationalEntity)sel);
                    if (mr != null) {
                        IResource theResource = null;

                        try {
                            theResource = mr.getCorrespondingResource();
                        } catch (ModelWorkspaceException theException) {
                            Util.log(theException);
                        }

                        if (theResource != null) {
                            IContainer container;
                            container = theResource.getParent();
                            if (containerText != null && container != null) {
                                containerText.setText(container.getFullPath().makeRelative().toString());
                            }
                            setParentPath(container);
                            folder = container;
                        }
                    } else {
                        rootName = ((RelationalEntity)sel).getName();
                    }
                }
            }
        } else {
            final Collection objs = SelectionUtilities.getSelectedObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext()) {
                processRootObject(selections.next(), true);
            }// end while
        }// end else
        
        initializing = false;
    }

    /**
     * Find all the Table and Procedure Result instances in the given resource.
     * 
     * @param rsrc
     * @since 4.1
     */
    private void addRootsFromResource( final Resource rsrc ) {
        if (parentPath == null) {
            final URI uri = rsrc.getURI().trimSegments(1);
            parentPath = uri.toString();
        }

        final Iterator contents = rsrc.getAllContents();
        while (contents.hasNext()) {
            processRootObject(contents.next(), false);
        }
    }

    private void setParentPath( final Object root ) {
        if (root instanceof IContainer) {
            parentPath = ((IContainer)root).getLocation().toString();
        } else if (root instanceof EObject) {
            final URI uri = ((EObject)root).eResource().getURI().trimSegments(1);
            parentPath = uri.toFileString();
        } else if (root instanceof Resource) {
            final URI uri = ((Resource)root).getURI().trimSegments(1);
            parentPath = uri.toFileString();
        } else if (root instanceof IFile) {
            final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)root);
            if (modelResource != null) {
                try {
                    final Resource rsrc = modelResource.getEmfResource();
                    final URI uri = rsrc.getURI().trimSegments(1);
                    parentPath = uri.toFileString();
                } catch (ModelWorkspaceException err) {
                    Util.log(err);
                }
            }
        }
    }

    private void processRootObject( final Object root,
                                    boolean validateResource ) {

        if (root instanceof BaseTable || root instanceof View) {
            if (validateResource) {
                validateResource(root);
            }
            addRoot(root);
        } else if (root instanceof Procedure) {
            if (validateResource) {
                validateResource(root);
            }
            ProcedureResult res = ((Procedure)root).getResult();
            if (res != null) {
                addRoot(res);
            }

            final Iterator params = ((Procedure)root).getParameters().iterator();
            while (params.hasNext()) {
                final ProcedureParameter param = (ProcedureParameter)params.next();
                final DirectionKind dir = param.getDirection();
                if ((dir.equals(DirectionKind.OUT_LITERAL) ||
                	dir.equals(DirectionKind.INOUT_LITERAL) ||
                	dir.equals(DirectionKind.RETURN_LITERAL))
                	  && res == null)  {
                    addRoot(root);
                }
            }
        } else if (root instanceof IFile) {
            final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)root);
            if (modelResource != null) {
                try {
                    validateResource(modelResource);
                    final Resource rsrc = modelResource.getEmfResource();
                    if (validResources && isRelationalModel(rsrc)) {
                        addRootsFromResource(rsrc);
                    }
                } catch (ModelWorkspaceException err) {
                    Util.log(err);
                    return;
                }
            }
        }// end else if
    }

    private void addRoot( Object root ) {
        if (!roots.contains(root)) {
            roots.add(root);
        }
    }

    private void validateResource( Object root ) {
        validResources = true;
        if (GenerateXsdWizard.HEADLESS) {
            return;
        }

        try {
            ModelResource mr = null;
            if (root instanceof ModelResource) {
                mr = (ModelResource)root;
            } else if (root instanceof EObject) {
                final Resource rsrc = ((EObject)root).eResource();
                if (rsrc != null) {
                    mr = ModelerCore.getModelWorkspace().findModelResource(rsrc);
                }
            }

            if (mr == null) {
                validResources = false;
                return;
            }

            if (unsavedResources.contains(mr) || invalidResources.contains(mr) || unvalidatedResources.contains(mr)) {
                return;
            }

            if (ModelUtilities.requiresValidation(mr)) {
                unvalidatedResources.add(mr);
                validResources = false;
                return;
            }

            if (mr.hasUnsavedChanges()) {
                unsavedResources.add(mr);
                validResources = false;
                return;
            }

            final IMarker[] problems = mr.getResource().findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
            if (problems.length > 0) {
                for (int i = 0; i != problems.length; ++i) {
                    final Integer severity = (Integer)problems[i].getAttribute(IMarker.SEVERITY);
                    if (severity != null && severity.intValue() == IMarker.SEVERITY_ERROR) {
                        invalidResources.add(mr);
                        validResources = false;
                        return;
                    }
                }
            }
        } catch (Exception err) {
            validResources = false;
        }
    }

    private boolean isRelationalModel( Resource rsrc ) {
        if (rsrc == null || !(rsrc instanceof EmfResource)) {
            return false;
        }

        EmfResource eResource = (EmfResource)rsrc;
        final ModelAnnotation annot = eResource.getModelAnnotation();

        // Determine if the resource is based on the relational metamodel ...
        if (annot != null && RelationalPackage.eNS_URI.equals(annot.getPrimaryMetamodelUri())) {
            return true;
        }

        return false;
    }

    /**
     * If genOut is selected the user must supply a model name. If genInput is selected the user must supply a model name. User
     * must select at least one of genOut or genInput
     */
    void checkStatus() {
    	if( initializing ) {
    		return;
    	}
    	
        boolean hasWarnings = false;
        if (!unsavedResources.isEmpty()) {
        	setErrorMessage(Util.getString("GenerateXsdWizard.unsavedModels")); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (!invalidResources.isEmpty()) {
            setErrorMessage(Util.getString("GenerateXsdWizard.invalidModels")); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (!unvalidatedResources.isEmpty()) {
        	setErrorMessage(Util.getString("GenerateXsdWizard.unvalidatedModels")); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        // This needs to be after the 3 checks to unsavedResources, unvalidatedResources and invalideResources
        if (!validResources) {
            setErrorMessage(Util.getString("GenerateXsdWizard.errValidatingModels")); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        String modelName = outputNameText.getText();

        IStatus status = ModelNameUtil.validate(modelName, ModelerCore.MODEL_FILE_EXTENSION, folder,
        		ModelNameUtil.IGNORE_CASE | ModelNameUtil.NO_DUPLICATE_MODEL_NAMES | ModelNameUtil.NO_EXISTING_MODEL_AT_LOCATION);
        if (status.getSeverity() == IStatus.ERROR) {
            setErrorMessage(ModelNameUtil.MESSAGES.INVALID_SCHEMA_FILE_NAME + status.getMessage());
            setPageComplete(false);
            return;
        }

        String fullPath = parentPath + File.separator + modelName + XSD_EXT;
        File file = new File(fullPath);
        if (file.exists()) {
            setErrorMessage(Util.getString("GenerateXsdWizard.fileAlreadyExistsMessage", modelName + XSD_EXT)); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (genInputButton.getSelection()) {
            String inputName = inputNameText.getText();

            status = ModelNameUtil.validate(inputName, XSD_EXT, folder,
            		ModelNameUtil.IGNORE_CASE | ModelNameUtil.NO_DUPLICATE_MODEL_NAMES | ModelNameUtil.NO_EXISTING_MODEL_AT_LOCATION);
            
            if (status.getSeverity() == IStatus.ERROR) {
                setErrorMessage(ModelNameUtil.MESSAGES.INVALID_SCHEMA_FILE_NAME + status.getMessage());
                setPageComplete(false);
                return;
            }

            fullPath = parentPath + File.separator + inputName + XSD_EXT;
            file = new File(fullPath);
            if (file.exists()) {
            	setErrorMessage(Util.getString("GenerateXsdWizard.fileAlreadyExistsMessage", inputName + XSD_EXT)); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }
        }

        String wsName = wsNameText.getText();

        status = ModelNameUtil.validate(wsName, XMI_EXT, folder,
        		ModelNameUtil.IGNORE_CASE | ModelNameUtil.NO_DUPLICATE_MODEL_NAMES | ModelNameUtil.NO_EXISTING_MODEL_AT_LOCATION);
        if (status.getSeverity() == IStatus.ERROR) {
            setErrorMessage(ModelNameUtil.MESSAGES.INVALID_VIEW_MODEL_NAME + status.getMessage());
            setPageComplete(false);
            return;
        }

        fullPath = parentPath + File.separator + wsName + XMI_EXT;
        file = new File(fullPath);
        if (file.exists()) {
            setErrorMessage(Util.getString("GenerateXsdWizard.fileAlreadyExistsMessage", wsName + XMI_EXT)); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (!hasWarnings) {
            setMessage(Util.getString("GenerateXsdWizard.done"), IMessageProvider.NONE); //$NON-NLS-1$
            setErrorMessage(null);
        }

        setPageComplete(true);
    }

    /**
     * Construct and return an options object based on the user selections.
     */
    public XsdBuilderOptions getOptions() {
        final boolean genOutput = true;
        final boolean genXml = true;
        final String xsdName = outputNameText.getText();
        final boolean genSQL = genSqlButton.getSelection();
        final boolean genInput = genInputButton.getSelection();
        final String inputName = genInput ? inputNameText.getText() : new String();
        final boolean genWs = true;
        final String wsName = wsNameText.getText();
        final boolean doFlat = doFlatOutputButton.getSelection() || doFlatInputButton.getSelection();

        final XsdBuilderOptions ops = new XsdBuilderOptions(genOutput, genXml, doFlat, roots, xsdName, genSQL, genInput,
                                                            inputName, genWs, wsName, rootName);
        ops.setParentPath(parentPath);

        return ops;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    public IResource getTargetContainer() {
        IResource result = null;
        String containerName = getContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource;
            }
        }

        return result;
    }

    private String getHiddenProjectPath() {
        String result = null;
        IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

        if (hiddenProj != null) {
            result = hiddenProj.getFullPath().makeRelative().toString();
        }

        return result;
    }

    public String getContainerName() {
        String result = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            result = getHiddenProjectPath();
        } else {
            result = containerText.getText().trim();
        }

        return result;
    }
}
