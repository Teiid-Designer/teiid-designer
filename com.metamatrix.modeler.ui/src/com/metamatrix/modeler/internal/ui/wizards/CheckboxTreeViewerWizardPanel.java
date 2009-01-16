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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelWorkspaceTreeProvider;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.DotProjectFilter;
import com.metamatrix.ui.internal.widget.INodeDescendantsDeselectionHandler;
import com.metamatrix.ui.internal.widget.InheritanceCheckboxTreeViewer;
import com.metamatrix.ui.tree.TreeViewerUtil;

/**
 * Extension to Composite containing an InheritanceCheckboxTreeViewer, and intended only for the case where the Composite is the
 * only thing appearing on a WizardPage. The WizardPage is passed in as a constructor argument and two of its methods are called:
 * setErrorMessage(), and setPageComplete(). The wizard page is set to complete iff. at least one item in the tree is checked.
 */
public class CheckboxTreeViewerWizardPanel extends Composite implements UiConstants, ICheckboxTreeViewerListenerController {
    private final static String BROWSE_SHORTHAND = "..."; //$NON-NLS-1$
    private final static int FILE_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);
    private final static String SELECT_MODEL_TITLE = Util.getString("StructuralCopyWizardPage.selectModelTitle"); //$NON-NLS-1$
    private final static String SELECT_MODEL_MSG = Util.getString("StructuralCopyWizardPage.selectModelMsg"); //$NON-NLS-1$
    private final static String CHECK_ALL = Util.getString("StructuralCopyWizardPage.checkAll"); //$NON-NLS-1$
    private final static String UNCHECK_ALL = Util.getString("StructuralCopyWizardPage.uncheckAll"); //$NON-NLS-1$
    private final static String COPY_ALL_DESCRIPTIONS = Util.getString("StructuralCopyWizardPage.copyAllDescriptions"); //$NON-NLS-1$

    private WizardPage wizardPage;
    private INodeDescendantsDeselectionHandler deselectionHandler;
    private Text fileNameText;
    private Button browseButton;
    private Button checkAllButton;
    private Button uncheckAllButton;
    private Button copyAllDescriptions;
    private IStructuralCopyTreePopulator populator = null;
    private Tree tree;
    InheritanceCheckboxTreeViewer treeViewer;
    private MetamodelDescriptor metamodelDescriptor;
    private ModelResource selectorDialogResult = null;
    boolean listenForCheckboxChanges = true;
    protected boolean targetIsVirtual;
    private boolean firstTimeVisible = true;
    private boolean copyEntire;

    public CheckboxTreeViewerWizardPanel( Composite parent,
                                          WizardPage wizardPage,
                                          INodeDescendantsDeselectionHandler deselectionHandler,
                                          MetamodelDescriptor metamodelDescriptor,
                                          ModelResource selection,
                                          boolean targetIsVirtual ) {
        super(parent, SWT.NULL);
        this.wizardPage = wizardPage;
        this.deselectionHandler = deselectionHandler;
        this.metamodelDescriptor = metamodelDescriptor;
        this.selectorDialogResult = selection;
        this.targetIsVirtual = targetIsVirtual;
        initialize();
    }

    public InheritanceCheckboxTreeViewer getViewer() {
        return treeViewer;
    }

    public IStructuralCopyTreePopulator getTreePopulator() {
        return populator;
    }

    public void enableCheckboxChangeListener( boolean flag ) {
        this.listenForCheckboxChanges = flag;
    }

    public void checkboxStateChanged() {
        checkedItemsChanged();
    }

    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        Composite fileComposite = new Composite(this, SWT.NONE);
        GridLayout fileCompositeLayout = new GridLayout();
        fileComposite.setLayout(fileCompositeLayout);
        fileCompositeLayout.numColumns = 3;
        fileCompositeLayout.marginWidth = 0;
        GridData fileCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        fileComposite.setLayoutData(fileCompositeGridData);
        Label schemaNameLabel = new Label(fileComposite, SWT.NONE);
        schemaNameLabel.setText(Util.getString("StructuralCopyWizardPage.existingModelLabel")); //$NON-NLS-1$
        fileNameText = WidgetFactory.createTextField(fileComposite, GridData.HORIZONTAL_ALIGN_FILL);
        fileNameText.setEditable(false);
        // Line Below will maintain White background, if desired.
        // fileNameText.setBackground(UiUtil.getSystemColor(SWT.COLOR_WHITE));
        GridData fileNameTextGridData = new GridData();
        fileNameTextGridData.widthHint = FILE_NAME_TEXT_WIDTH;
        fileNameText.setLayoutData(fileNameTextGridData);
        browseButton = new Button(fileComposite, SWT.PUSH);
        browseButton.setText(BROWSE_SHORTHAND);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                browseButtonClicked();
            }
        });
        Group treeGroup = new Group(this, SWT.NONE);
        GridLayout treeGroupLayout = new GridLayout();
        treeGroup.setLayout(treeGroupLayout);
        GridData treeGroupGridData = new GridData(GridData.FILL_BOTH);
        treeGroup.setLayoutData(treeGroupGridData);

        // copy selection buttons:
        Button copyEntire = new Button(treeGroup, SWT.RADIO);
        copyEntire.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        copyEntire.setText(Util.getString("StructuralCopyWizardPage.copyEntireLabel")); //$NON-NLS-1$
        final Button selectFeatures = new Button(treeGroup, SWT.RADIO);
        selectFeatures.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        selectFeatures.setText(Util.getString("StructuralCopyWizardPage.selectFeaturesLabel")); //$NON-NLS-1$
        // note that we add selection listeners below the tree stuff and its buttons...

        // selection tree:
        tree = new Tree(treeGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK | SWT.BORDER);
        GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tree.setLayoutData(treeGridData);
        treeViewer = new InheritanceCheckboxTreeViewer(
                                                       tree,
                                                       InheritanceCheckboxTreeViewer.CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_CHECKED_GRAYED_IF_ANY_CHECKED,
                                                       deselectionHandler);
        treeViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged( CheckStateChangedEvent event ) {
                if (listenForCheckboxChanges) {
                    checkedItemsChanged();
                }
            }
        });

        Composite buttonComposite = new Composite(treeGroup, SWT.NONE);
        GridLayout buttonLayout = new GridLayout();
        buttonComposite.setLayout(buttonLayout);
        buttonLayout.numColumns = 2;
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;

        checkAllButton = new Button(buttonComposite, SWT.PUSH);
        checkAllButton.setText(CHECK_ALL);
        checkAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                treeViewer.setListenerEnabled(false);
                TreeViewerUtil.setAllChecked(treeViewer, true, true);
                treeViewer.setListenerEnabled(true);
                checkboxStateChanged();
            }
        });
        GridData buttonGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        checkAllButton.setLayoutData(buttonGridData);

        uncheckAllButton = new Button(buttonComposite, SWT.PUSH);
        uncheckAllButton.setText(UNCHECK_ALL);
        uncheckAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                treeViewer.setListenerEnabled(false);
                TreeViewerUtil.setAllChecked(treeViewer, false, true);
                treeViewer.setListenerEnabled(true);
                checkboxStateChanged();
            }
        });
        buttonGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        uncheckAllButton.setLayoutData(buttonGridData);

        // copy selection buttons
        SelectionListener sl = new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }

            public void widgetSelected( SelectionEvent e ) {
                boolean copyEntire = !selectFeatures.getSelection();
                setCopyEntireModel(copyEntire);
            }
        };
        selectFeatures.addSelectionListener(sl);
        // initial state:
        copyEntire.setSelection(true);
        setCopyEntireModel(true);

        addOptions(this);
    }

    /**
     * Overridable method to add options from subclasses
     * 
     * @param parent
     * @since 5.0
     */
    protected void addOptions( Composite parent ) {

        // add a checkbox to copy descriptions (annotations).
        copyAllDescriptions = new Button(this, SWT.CHECK);
        copyAllDescriptions.setSelection(true);
        copyAllDescriptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        copyAllDescriptions.setText(COPY_ALL_DESCRIPTIONS);
    }

    /**
     * Tells whether the user has explicitly indiciated that they wish to copy the entire selected model, rather than select
     * pieces of it.
     * 
     * @return true if the user wishes to copy the entire model.
     */
    public boolean isCopyEntireModel() {
        // TODO this should also see if everything in the tree is selected, and return true in that case, also.
        // --> be careful, though, since this method is also called in this class.
        return copyEntire;
    }

    /**
     * Tells where the user has explicitly indicated they wish to copy all descriptions from the selected model to the new target
     * model
     * 
     * @return
     * @since 5.0
     */
    public boolean isCopyAllDescriptions() {
        return copyAllDescriptions.getSelection();
    }

    /**
     * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
     * @since 4.2
     */
    @Override
    public void setVisible( boolean theVisible ) {
        super.setVisible(theVisible);

        // can't call the populator before the tree is visible. treeviewers use lightweight treeitems before they're visible
        // so setting the populator prior to being visible would check these temporary treeitems instead of the true
        // treeitems created when the tree is expanded. this is called only the first time visible and a workspace selection.
        if (theVisible && this.firstTimeVisible && this.selectorDialogResult != null) {
            this.firstTimeVisible = false;
            IFile file = (IFile)selectorDialogResult.getResource();
            fileNameText.setText(file.getProjectRelativePath().toString());
            populator = getFeaturePopulator(file);
            populator.populateModelFeaturesTree(treeViewer, selectorDialogResult, targetIsVirtual);
        }
    }

    void browseButtonClicked() {
        IStructuralCopyTreePopulator thePopulator = displaySelectorDialog();
        if (thePopulator != null) {
            populator = thePopulator;
            populator.populateModelFeaturesTree(treeViewer, selectorDialogResult, targetIsVirtual);
            setCopyEntireModel(isCopyEntireModel()); // set to the same to redo things
        }
    }

    private IStructuralCopyTreePopulator displaySelectorDialog() {
        IStructuralCopyTreePopulator result = null;
        this.selectorDialogResult = null;

        ModelWorkspaceTreeProvider provider = new ModelWorkspaceTreeProvider();
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), provider, provider);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        dialog.setAllowMultiple(false);
        dialog.addFilter(new ClosedProjectFilter());
        dialog.addFilter(new DotProjectFilter());
        // use resource filter used by model explorer
        dialog.addFilter(new ModelingResourceFilter());
        dialog.setValidator(new ModelResourceSelectionValidator(this.metamodelDescriptor, false));

        dialog.setTitle(SELECT_MODEL_TITLE);
        dialog.setMessage(SELECT_MODEL_MSG);

        if (dialog.open() == Window.OK) {
            Object[] selection = dialog.getResult();
            if ((selection.length == 1) && (selection[0] instanceof IFile)) {
                IFile sourceFile = (IFile)selection[0];
                result = getFeaturePopulator(sourceFile);
                selectorDialogResult = null;
                fileNameText.setText(""); //$NON-NLS-1$
                boolean exceptionOccurred = false;
                try {
                    selectorDialogResult = ModelUtilities.getModelResource(sourceFile, true);
                } catch (Exception ex) {
                    Util.log(ex);
                    exceptionOccurred = true;
                }
                if (!exceptionOccurred) {
                    fileNameText.setText(sourceFile.getName());
                }
            }
        }

        return result;
    }

    protected StructuralCopyModelFeaturePopulator getFeaturePopulator( IFile sourceFile ) {
        return new StructuralCopyModelFeaturePopulator(sourceFile, this);
    }

    void checkedItemsChanged() {
        int numItemsChecked = treeViewer.getCheckedElements().length;
        boolean complete = (numItemsChecked > 0);
        wizardPage.setPageComplete(complete);
        if (complete) {
            wizardPage.setErrorMessage(null);
        } else {
            // If # of root items == 0, then this method probably cannot have been called anyway,
            // but in case it was we will not disturb the error message because the error
            // message should already be stating that the selected file was not
            // recognizable as an existing model.
            if (TreeViewerUtil.getRootNodes(treeViewer).size() > 0) {
                String msg = Util.getString("StructuralCopyWizardPage.needCheckedItem"); //$NON-NLS-1$
                wizardPage.setErrorMessage(msg);
            }
        }
    }

    /**
     * Updates the enablement of components and message of the wizard page.
     * 
     * @param copyEntireModel if true, ignore the tree checked state and copy all model features.
     */
    void setCopyEntireModel( boolean copyEntireModel ) {
        copyEntire = copyEntireModel;
        tree.setEnabled(!copyEntireModel);
        checkAllButton.setEnabled(!copyEntireModel);
        uncheckAllButton.setEnabled(!copyEntireModel);
        if (copyEntireModel && selectorDialogResult != null) {
            wizardPage.setPageComplete(true);
            wizardPage.setErrorMessage(null);
        } else {
            checkedItemsChanged();
        } // endif
    }
}// end CheckboxTreeViewerWizardPanel
