/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.ui.wizards;

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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.View;
import com.metamatrix.metamodels.xsd.XsdBuilderOptions;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.StatusLabel;

/**
 * This page is used to capture the user preferences for Generation of XSD from Relational objects.
 */

public class GenerateXsdWizardOptionslPage extends WizardPage
    implements InternalUiConstants.Widgets, UiConstants, UiConstants.ProductInfo, UiConstants.ProductInfo.Capabilities {

    private static final String XSD_EXT = ".xsd"; //$NON-NLS-1$
    private static final String XMI_EXT = ".xmi"; //$NON-NLS-1$
    private static final int DEFAULT_LBL_ROWS = 5;

    private final Collection unsavedResources = new HashSet();
    private final Collection invalidResources = new HashSet();
    private final Collection unvalidatedResources = new HashSet();

    private boolean validResources;
    private Button genOutputButton;
    private Button genXmlButton;
    private Button genSqlButton;
    private Button genInputButton;
    private Button genWsButton;
    private Button doFlatButton;
    Text outputNameText;
    Text inputNameText;
    Text wsNameText;
    private StatusLabel statusLbl;
    private LinkedHashSet roots;
    String rootName;
    private String parentPath;

    private Text containerText;
    private ISelection selection;

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
    public void createControl( Composite parent ) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        // add a Location field and BROWSE button to select target project or folder
        // Make sure this isn't displayed in DIMENSIONI by checking for hiddenProjectCentric
        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
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
            locationMsg.setText(Util.getString("GenerateXsdWizard.locationMessage")); //$NON-NLS-1$
            containerText = new Text(locationComposite, SWT.BORDER | SWT.SINGLE);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            containerText.setLayoutData(gd);
            containerText.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent e ) {
                    checkStatus();
                }
            });
            containerText.setEditable(false);

            Button browseButton = new Button(locationComposite, SWT.PUSH);
            GridData buttonGridData = new GridData();
            // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
            browseButton.setLayoutData(buttonGridData);
            browseButton.setText(Util.getString("GenerateXsdWizard.browse")); //$NON-NLS-1$
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleBrowse();
                }
            });
        }

        Composite topComposite = new Composite(container, SWT.NULL);
        GridData topCompositeGridData = new GridData(GridData.FILL_BOTH);
        topComposite.setLayoutData(topCompositeGridData);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 2;
        topComposite.setLayout(topLayout);

        // Instruction label.
        Label prompt = new Label(topComposite, SWT.NULL);
        GridData promptGridData = new GridData();
        promptGridData.horizontalSpan = 2;
        prompt.setLayoutData(promptGridData);
        prompt.setText(Util.getString("GenerateXsdWizard.prompt")); //$NON-NLS-1$

        Label spacer = new Label(topComposite, SWT.NULL);
        GridData spGridData = new GridData();
        spGridData.horizontalSpan = 2;
        spacer.setLayoutData(spGridData);

        Label opLabel = new Label(topComposite, SWT.NULL);
        GridData opGridData = new GridData();
        opGridData.horizontalSpan = 2;
        opLabel.setLayoutData(opGridData);
        opLabel.setText(Util.getString("GenerateXsdWizard.outOptions")); //$NON-NLS-1$

        // Control to prompt for bulding of output XSD
        genOutputButton = new Button(topComposite, SWT.CHECK);
        GridData buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 2;
        genOutputButton.setLayoutData(buttonGridData);
        genOutputButton.setText(Util.getString("GenerateXsdWizard.outModel")); //$NON-NLS-1$
        genOutputButton.setSelection(true);
        genOutputButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleGenOutputButton();
            }
        });

        // Control to capture flat or nested XSD preference
        doFlatButton = new Button(topComposite, SWT.CHECK);
        buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 2;
        doFlatButton.setLayoutData(buttonGridData);
        doFlatButton.setText(Util.getString("GenerateXsdWizard.doFlat")); //$NON-NLS-1$
        doFlatButton.setSelection(true);

        // Control to capture the Model name to use
        Label modelNameLabel = new Label(topComposite, SWT.NULL);
        modelNameLabel.setText(Util.getString("GenerateXsdWizard.modelName")); //$NON-NLS-1$

        outputNameText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        outputNameText.setLayoutData(gd);
        outputNameText.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                checkStatus();
            }
        });

        // Control to prompt for generation of XML Documents
        genXmlButton = new Button(topComposite, SWT.CHECK);
        buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 2;
        genXmlButton.setLayoutData(buttonGridData);
        genXmlButton.setText(Util.getString("GenerateXsdWizard.genXml")); //$NON-NLS-1$
        genXmlButton.setSelection(true);
        genXmlButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleGenXmlButton();
            }
        });

        // Control to prompt for generation of default Mapping class SQL
        genSqlButton = new Button(topComposite, SWT.CHECK);
        buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 2;
        genSqlButton.setLayoutData(buttonGridData);
        genSqlButton.setText(Util.getString("GenerateXsdWizard.genSql")); //$NON-NLS-1$
        genSqlButton.setSelection(true);

        spacer = new Label(topComposite, SWT.NULL);
        spGridData = new GridData();
        spGridData.horizontalSpan = 2;
        spacer.setLayoutData(spGridData);

        Label inLabel = new Label(topComposite, SWT.NULL);
        GridData inGridData = new GridData();
        inGridData.horizontalSpan = 2;
        inLabel.setLayoutData(inGridData);
        inLabel.setText(Util.getString("GenerateXsdWizard.inOptions")); //$NON-NLS-1$

        // Control to prompt for creation of input docs for Procedures
        genInputButton = new Button(topComposite, SWT.CHECK);
        buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 2;
        genInputButton.setLayoutData(buttonGridData);
        genInputButton.setText(Util.getString("GenerateXsdWizard.genInputForProcs")); //$NON-NLS-1$
        genInputButton.setSelection(true);
        genInputButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleGenInputButton();
            }
        });

        // Control to capture the name of the Input Doc model name.
        Label inputNameLabel = new Label(topComposite, SWT.NULL);
        inputNameLabel.setText(Util.getString("GenerateXsdWizard.inputName")); //$NON-NLS-1$

        inputNameText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalSpan = 2;
        inputNameText.setLayoutData(gd);
        inputNameText.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                checkStatus();
            }
        });

        spacer = new Label(topComposite, SWT.NULL);
        spGridData = new GridData();
        spGridData.horizontalSpan = 2;
        spacer.setLayoutData(spGridData);

        Label wsLabel = new Label(topComposite, SWT.NULL);
        GridData wsGridData = new GridData();
        wsGridData.horizontalSpan = 2;
        wsLabel.setLayoutData(wsGridData);
        wsLabel.setText(Util.getString("GenerateXsdWizard.wsOptions")); //$NON-NLS-1$

        // Control to prompt for creation of Web Service Model
        genWsButton = new Button(topComposite, SWT.CHECK);
        buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 2;
        genWsButton.setLayoutData(buttonGridData);
        genWsButton.setText(Util.getString("GenerateXsdWizard.genWs")); //$NON-NLS-1$
        genWsButton.setSelection(true);
        genWsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleGenWsButton();
            }
        });

        // Control to capture the WebService Model name
        Label wsNameLabel = new Label(topComposite, SWT.NULL);
        wsNameLabel.setText(Util.getString("GenerateXsdWizard.wsName")); //$NON-NLS-1$

        wsNameText = new Text(topComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        wsNameText.setLayoutData(gd);
        wsNameText.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                checkStatus();
            }
        });

        spacer = new Label(topComposite, SWT.NULL);
        spGridData = new GridData();
        spGridData.horizontalSpan = 2;
        spacer.setLayoutData(spGridData);

        statusLbl = new StatusLabel(topComposite);
        statusLbl.setRows(DEFAULT_LBL_ROWS);
        GridData statusGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
        statusGridData.horizontalSpan = 2;
        statusLbl.setLayoutData(statusGridData);

        setControl(container);
        checkStatus();
        outputNameText.setFocus();

        setDefaults();
        initialize();
    }

    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog((IContainer)getTargetContainer(),
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

        // You must gen Inputs and Outputs to gen WS
        if (!genInputButton.getSelection()) {
            genWsButton.setSelection(false);
        }

        genWsButton.setEnabled(genInputButton.getSelection() && genOutputButton.getSelection());
        wsNameText.setEnabled(genInputButton.getSelection() && genOutputButton.getSelection());

        checkStatus();
    }

    void handleGenWsButton() {
        // If genWs, enable WS model name field, else disable.
        wsNameText.setEnabled(genWsButton.getSelection());
        checkStatus();
    }

    void handleGenXmlButton() {
        // If genXml, enable genSql, else disable.
        if (!genXmlButton.getSelection()) {
            genWsButton.setSelection(false);
        }

        genSqlButton.setEnabled(genXmlButton.getSelection());
        checkStatus();
    }

    void handleGenOutputButton() {
        // If single model, enable the model name field, else disable.
        outputNameText.setEnabled(genOutputButton.getSelection());
        genSqlButton.setEnabled(genOutputButton.getSelection());
        genXmlButton.setEnabled(genOutputButton.getSelection());

        // You must gen Inputs and Outputs to gen WS
        if (!genOutputButton.getSelection()) {
            genWsButton.setSelection(false);
        }

        genWsButton.setEnabled(genInputButton.getSelection() && genOutputButton.getSelection());
        wsNameText.setEnabled(genInputButton.getSelection() && genOutputButton.getSelection());

        checkStatus();
    }

    /**
     * Tests if the current workbench selection is a suitable container to use. All selections must be Relational (Virtual or
     * Physical). All Tables and Procedure Results within the selection are added to the Collection of root objects to use for
     * building.
     */
    private void initialize() {
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
                if (dir == DirectionKind.OUT_LITERAL && res == null) {
                    addRoot(root);
                } else if (dir == DirectionKind.INOUT_LITERAL) {
                    if (res == null) {
                        addRoot(root);
                    }
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
        boolean hasWarnings = false;
        if (!unsavedResources.isEmpty()) {
            setMessageLabelText(Util.getString("GenerateXsdWizard.unsavedModels"), IStatus.ERROR); //$NON-NLS-1$
            setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (!invalidResources.isEmpty()) {
            setMessageLabelText(Util.getString("GenerateXsdWizard.invalidModels"), IStatus.ERROR); //$NON-NLS-1$
            setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (!unvalidatedResources.isEmpty()) {
            setMessageLabelText(Util.getString("GenerateXsdWizard.unvalidatedModels"), IStatus.ERROR); //$NON-NLS-1$
            setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        // This needs to be after the 3 checks to unsavedResources, unvalidatedResources and invalideResources
        if (!validResources) {
            setMessageLabelText(Util.getString("GenerateXsdWizard.errValidatingModels"), IStatus.ERROR); //$NON-NLS-1$
            setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (!genOutputButton.getSelection() && !genInputButton.getSelection()) {
            setMessageLabelText(Util.getString("GenerateXsdWizard.noBuilds"), IStatus.ERROR); //$NON-NLS-1$
            setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
            setPageComplete(false);
            return;
        }

        if (genOutputButton.getSelection()) {
            String modelName = outputNameText.getText();
            if (modelName.trim().length() == 0) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.enterModelFileName"), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            final String msg = ModelUtilities.validateModelName(modelName, XSD_EXT);
            if (msg != null) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.invalidModelFileName", msg), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            final String fullPath = parentPath + File.separator + modelName + XSD_EXT;
            File file = new File(fullPath);
            if (file.exists()) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.fileAlreadyExistsMessage", modelName + XSD_EXT), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }
        }

        if (genInputButton.getSelection()) {
            String inputName = inputNameText.getText();
            if (inputName.trim().length() == 0) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.enterInputFileName"), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            final String msg = ModelUtilities.validateModelName(inputName, XSD_EXT);
            if (msg != null) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.invalidInputFileName", msg), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            final String fullPath = parentPath + File.separator + inputName + XSD_EXT;
            File file = new File(fullPath);
            if (file.exists()) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.fileAlreadyExistsMessage", inputName + XSD_EXT), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }
        }

        if (genWsButton.getSelection()) {
            String wsName = wsNameText.getText();
            if (wsName.trim().length() == 0) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.enterWsFileName"), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            if (wsName.indexOf(XMI_EXT) > 0) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.noExtWsFileName"), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            final String msg = ModelUtilities.validateModelName(wsName, XMI_EXT);
            if (msg != null) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.invalidWsFileName", msg), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }

            final String fullPath = parentPath + File.separator + wsName + XMI_EXT;
            File file = new File(fullPath);
            if (file.exists()) {
                setMessageLabelText(Util.getString("GenerateXsdWizard.fileAlreadyExistsMessage", wsName + XMI_EXT), IStatus.ERROR); //$NON-NLS-1$
                setMessage(Util.getString("GenerateXsdWizard.errs"), IMessageProvider.ERROR); //$NON-NLS-1$
                setPageComplete(false);
                return;
            }
        }

        if (!hasWarnings) {
            setMessage(Util.getString("GenerateXsdWizard.done"), IMessageProvider.NONE); //$NON-NLS-1$
            setMessageLabelText(null, IStatus.OK);
        }

        setPageComplete(true);
    }

    private void setMessageLabelText( final String text,
                                      final int severity ) {
        if (text == null) {
            statusLbl.setVisible(false);
        } else {
            statusLbl.setText(text, severity);
            statusLbl.update();
            statusLbl.setVisible(true);
        }
    }

    /**
     * Construct and return an options object based on the user selections.
     */
    public XsdBuilderOptions getOptions() {
        final boolean genOutput = genOutputButton.getSelection();
        final boolean genXml = genXmlButton.getSelection() && genOutput;
        final String xsdName = genOutput ? outputNameText.getText() : new String();
        final boolean genSQL = genXmlButton.getSelection() && genOutput && genXml;
        final boolean genInput = genInputButton.getSelection();
        final String inputName = genInput ? inputNameText.getText() : new String();
        final boolean genWs = genWsButton.getSelection() && genOutput && genInput && genXml;
        final String wsName = genWs ? wsNameText.getText() : new String();
        final boolean doFlat = doFlatButton.getSelection();

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

        if (!StringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
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
