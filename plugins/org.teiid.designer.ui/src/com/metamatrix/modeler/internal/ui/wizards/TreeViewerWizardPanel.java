/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.ModelWorkspaceTreeProvider;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.DotProjectFilter;

/**
 * Extension to Composite containing an InheritanceCheckboxTreeViewer, and intended only for the case where the Composite is the
 * only thing appearing on a WizardPage. The WizardPage is passed in as a constructor argument and two of its methods are called:
 * setErrorMessage(), and setPageComplete(). The wizard page is set to complete iff. at least one item in the tree is checked.
 */
public class TreeViewerWizardPanel extends Composite implements UiConstants {
    private final static String BROWSE_SHORTHAND = "..."; //$NON-NLS-1$
    private final static int FILE_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);
    private final static String SELECT_MODEL_TITLE = Util.getString("StructuralCopyWizardPage.selectModelTitle"); //$NON-NLS-1$
    private final static String SELECT_MODEL_MSG = Util.getString("StructuralCopyWizardPage.selectModelMsg"); //$NON-NLS-1$
    private final static String COPY_ALL_DESCRIPTIONS = Util.getString("StructuralCopyWizardPage.copyAllDescriptions"); //$NON-NLS-1$

    private WizardPage wizardPage;
    private Text fileNameText;
    private Button browseButton;
    private Button copyAllDescriptions;
    private IStructuralCopyTreePopulator populator = null;
    private Tree tree;
    private TreeViewer treeViewer;
    private MetamodelDescriptor metamodelDescriptor;
    private ModelResource selectorDialogResult = null;
    protected boolean targetIsVirtual;
    private boolean firstTimeVisible = true;
    private boolean copyEntire;

    public TreeViewerWizardPanel( Composite parent,
    							  WizardPage wizardPage,
                                  MetamodelDescriptor metamodelDescriptor,
                                  ModelResource selection,
                                  boolean targetIsVirtual ) {
        super(parent, SWT.NULL);
        this.wizardPage = wizardPage;
        this.metamodelDescriptor = metamodelDescriptor;
        this.selectorDialogResult = selection;
        this.targetIsVirtual = targetIsVirtual;
        initialize();
    }

    public TreeViewer getViewer() {
        return treeViewer;
    }

    public IStructuralCopyTreePopulator getTreePopulator() {
        return populator;
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
        treeGroup.setText(Util.getString("TreeViewerWizardPanel.modelContentsLabel")); //$NON-NLS-1$
        GridLayout treeGroupLayout = new GridLayout();
        treeGroup.setLayout(treeGroupLayout);
        GridData treeGroupGridData = new GridData(GridData.FILL_BOTH);
        treeGroup.setLayoutData(treeGroupGridData);

        // selection tree:
        tree = new Tree(treeGroup, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tree.setLayoutData(treeGridData);
        treeViewer = new TreeViewer(tree);

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
            if (selectorDialogResult != null) {
                wizardPage.setPageComplete(true);
                wizardPage.setErrorMessage(null);
            }
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
                    selectorDialogResult = ModelUtil.getModelResource(sourceFile, true);
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
        return new StructuralCopyModelFeaturePopulator(sourceFile);
    }

    /**
     * Updates the enablement of components and message of the wizard page.
     * 
     * @param copyEntireModel if true, ignore the tree checked state and copy all model features.
     */
    void setCopyEntireModel( boolean copyEntireModel ) {
        copyEntire = copyEntireModel;
    }
}// end TreeViewerWizardPanel
