/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd.ui.wizards;

import java.util.Collections;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import com.metamatrix.metamodels.xsd.ui.XsdUiPlugin;

/**
 * This is a simple wizard for creating a new model file.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class XsdModelWizard extends Wizard implements
                                                    INewWizard {

    /**
     * Caches instance of the model package.
     */
    protected XSDPackage xsdPackage = XSDPackage.eINSTANCE;

    /**
     * Caches instance of the model factory.
     * @generated
     */
    protected XSDFactory xsdFactory = xsdPackage.getXSDFactory();

    /**
     * File creation page.
     */
    protected XSDModelWizardNewFileCreationPage newFilePage;

    /**
     * Object creation page.
     */
    protected XSDModelWizardInitialObjectCreationPage initialObjectPage;

    /**
     * Initial Selection
     */
    protected IStructuredSelection initialSelection;

    /**
     * Cached workbench.
     */
    protected IWorkbench workbench;

    /**
     *  Set the initial info.
     */
    public void init(IWorkbench workbench,
                     IStructuredSelection selection) {
        this.workbench = workbench;
        this.initialSelection = selection;
        setDefaultPageImageDescriptor(XsdUiPlugin.INSTANCE.getImageDescriptor("icons/full/wizban/NewXSD")); //$NON-NLS-1$
    }

    /**
     * Creates a model.
     */
    protected EObject createInitialModel() {
        return initialObjectPage.createInitialModel();
    }

    /**
     * Perform the work.
     */
    @Override
    public boolean performFinish() {
        try {
            // Cache the file.
            //
            final IFile modelFile = getModelFile();

            // Perform the work within an operation.

            WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

                @Override
                protected void execute(IProgressMonitor progressMonitor) {
                    try {
                        // Create resource set

                        ResourceSet resourceSet = new ResourceSetImpl();

                        // Get the URI.

                        URI fileURI = URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true);

                        // Create a resource.

                        Resource resource = resourceSet.createResource(fileURI);

                        // Add the initial model object.

                        EObject rootObject = createInitialModel();
                        if (rootObject != null) {
                            resource.getContents().add(rootObject);
                        }

                        // Save to the file system.

                        resource.save(Collections.EMPTY_MAP);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    } finally {
                        progressMonitor.done();
                    }
                }
            };

            getContainer().run(false, false, operation);

            // Select the new file resource in the current view.
            //
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = workbenchWindow.getActivePage();
            final IWorkbenchPart activePart = page.getActivePart();
            if (activePart instanceof ISetSelectionTarget) {
                final ISelection targetSelection = new StructuredSelection(modelFile);
                getShell().getDisplay().asyncExec(new Runnable() {

                    public void run() {
                        ((ISetSelectionTarget)activePart).selectReveal(targetSelection);
                    }
                });
            }

            // Open an editor on the new file.
            //
            try {
				IDE.openEditor(page, modelFile);
            } catch (PartInitException exception) {
                MessageDialog.openError(workbenchWindow.getShell(),
                                        XsdUiPlugin.Util.getString("_UI_OpenEditorError_label"), //$NON-NLS-1$
                                        exception.getMessage());
                XsdUiPlugin.Util.log(exception);
                return false;
            }

            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * First wizard page.
     */
    public class XSDModelWizardNewFileCreationPage extends WizardNewFileCreationPage {

        /**
         * Cache model file.
         */
        protected IFile modelFile;

        /**
         * Create with initial selection.
         * @param pageId
         * @param selection
         */
        public XSDModelWizardNewFileCreationPage(String pageId,
                                                 IStructuredSelection selection) {
            super(pageId, selection);
        }

        /**
         * Check to see if the file is correct.
         */
        @Override
        protected boolean validatePage() {
            if (super.validatePage()) {
                // Make sure the file ends in ".xsd".
                //
                String requiredExt = XsdUiPlugin.Util.getString("_UI_XSDEditorFilenameExtension"); //$NON-NLS-1$
                String enteredExt = new Path(getFileName()).getFileExtension();
                if (enteredExt == null || !enteredExt.equals(requiredExt)) {
                    setErrorMessage(XsdUiPlugin.Util.getString("_WARN_FilenameExtension", new Object[] {requiredExt})); //$NON-NLS-1$
                    return false;
                }
                return true;
            }
            return false;
        }

        /**
         * Cache the file on completion.
         */
        public boolean performFinish() {
            modelFile = getModelFile();
            return true;
        }

        /**
		 * Returns model file.
         */
        public IFile getModelFile() {
            if (getContainerFullPath() == null) {
                return null;
            }

            return modelFile == null ? ResourcesPlugin.getWorkspace().getRoot()
                                                      .getFile(getContainerFullPath().append(getFileName())) : modelFile;
        }
    }

    /**
     * Select the type of object to create.
     */
    public class XSDModelWizardInitialObjectCreationPage extends WizardPage {

        /**
         */
        protected Text nestedSchemaPrefixText;

        /**
         */
        protected Text nestedSchemaNamespaceText;

        /**
         */
        protected Text schemaPrefixText;

        /**
         */
        protected Text schemaNamespaceText;

        
        /**
         * Create with page ID.
         * @param pageId
         */
        public XSDModelWizardInitialObjectCreationPage(String pageId) {
            super(pageId);
        }

        
        /**
         * Create the page and all controls.
         */
        public void createControl(Composite parent) {
        	GridData gd = null;
        	
        	// -------------------------------------------------------------------------------------------------------------------
            Composite composite = new Composite(parent, SWT.NONE);

            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.verticalSpacing = 12;
            composite.setLayout(layout);

            gd = new GridData();
            gd.verticalAlignment = GridData.FILL;
            gd.grabExcessVerticalSpace = true;
            gd.horizontalAlignment = GridData.FILL;
            composite.setLayoutData(gd);

            // -------------------------------------------------------------------------------------------------------------------
            Label schemaPrefixLabel = new Label(composite, SWT.LEFT);

            schemaPrefixLabel.setText(XsdUiPlugin.Util.getString("_UI_SchemaPrefix_label")); //$NON-NLS-1$

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            schemaPrefixLabel.setLayoutData(gd);


            schemaPrefixText = new Text(composite, SWT.SINGLE | SWT.BORDER);

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            schemaPrefixText.setLayoutData(gd);
            schemaPrefixText.setText(XsdUiPlugin.Util.getString("XsdModelWizard.this_6")); //$NON-NLS-1$

            // -------------------------------------------------------------------------------------------------------------------
            Label schemaNamespaceLabel = new Label(composite, SWT.LEFT);

            schemaNamespaceLabel.setText(XsdUiPlugin.Util.getString("_UI_SchemaNamespaceURI_label")); //$NON-NLS-1$

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            schemaNamespaceLabel.setLayoutData(gd);


            schemaNamespaceText = new Text(composite, SWT.SINGLE | SWT.BORDER);

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            schemaNamespaceText.setLayoutData(gd);
            final String nsText = getModelFile() == null ? "http://" : "http://" + getModelFile().getFullPath(); //$NON-NLS-1$ //$NON-NLS-2$
            schemaNamespaceText.setText(nsText);

            // -------------------------------------------------------------------------------------------------------------------
            Label nestedSchemaPrefixLabel = new Label(composite, SWT.LEFT);

        	nestedSchemaPrefixLabel.setText(XsdUiPlugin.Util.getString("_UI_SchemaForSchemaPrefix_label")); //$NON-NLS-1$

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            nestedSchemaPrefixLabel.setLayoutData(gd);


            nestedSchemaPrefixText = new Text(composite, SWT.SINGLE | SWT.BORDER);

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            nestedSchemaPrefixText.setLayoutData(gd);
            nestedSchemaPrefixText.setText("xsd"); //$NON-NLS-1$

            // -------------------------------------------------------------------------------------------------------------------
            Label nestedSchemaNamespaceLabel = new Label(composite, SWT.LEFT);

        	nestedSchemaNamespaceLabel.setText(XsdUiPlugin.Util.getString("_UI_SchemaForSchemaNamespaceURI_label")); //$NON-NLS-1$

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            nestedSchemaNamespaceLabel.setLayoutData(gd);


            nestedSchemaNamespaceText = new Text(composite, SWT.SINGLE | SWT.BORDER);

            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            nestedSchemaNamespaceText.setLayoutData(gd);
            nestedSchemaNamespaceText.setText(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);


            setControl(composite);
        }

        /**
         * Check for file correctness.
         */
        @Override
        public boolean isPageComplete() {
            return super.isPageComplete();
        }

        /**
         *
         */
        public boolean performFinish() {
            return true;
        }

        public EObject createInitialModel() {
            XSDSchema xsdSchema = XSDFactory.eINSTANCE.createXSDSchema();

            Map map = xsdSchema.getQNamePrefixToNamespaceMap();
            map.put(nestedSchemaPrefixText.getText(), nestedSchemaNamespaceText.getText());
            xsdSchema.setSchemaForSchemaQNamePrefix(nestedSchemaPrefixText.getText());
            if (schemaNamespaceText.getText() != null && schemaNamespaceText.getText().trim().length() != 0) {
                xsdSchema.setTargetNamespace(schemaNamespaceText.getText());
                map.put(schemaPrefixText.getText(), schemaNamespaceText.getText());
            }
            return xsdSchema;
        }
    }

    /**
     * Create the contents (pages) of the wizard.
     */
    @Override
    public void addPages() {
        // Create a page, set the title, and the initial model file name.
        //
        newFilePage = new XSDModelWizardNewFileCreationPage(
                                                                    XsdUiPlugin.Util
                                                                                    .getString("XsdModelWizard.Whatever_13"), initialSelection); //$NON-NLS-1$
        newFilePage.setTitle(XsdUiPlugin.Util.getString("_UI_XSDModelWizard_label")); //$NON-NLS-1$
        newFilePage.setDescription(XsdUiPlugin.Util.getString("_UI_XSDModelWizard_description")); //$NON-NLS-1$
        newFilePage
                           .setFileName(XsdUiPlugin.Util.getString("_UI_XSDEditorFilenameDefaultBase") + "." + XsdUiPlugin.Util.getString("_UI_XSDEditorFilenameExtension")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        //add wizard banner
        newFilePage.setImageDescriptor(XsdUiPlugin.INSTANCE.getImageDescriptor("icons/full/wizban/NewXSD.gif")); //$NON-NLS-1$
        addPage(newFilePage);

        // Try and get the resource selection to determine a current directory for the file dialog.
        //
        if (initialSelection != null && !initialSelection.isEmpty()) {
            // Get resource...

            Object selectedElement = initialSelection.iterator().next();
            if (selectedElement instanceof IResource) {
                // Get resource's parent.

                IResource selectedResource = (IResource)selectedElement;
                if (selectedResource.getType() == IResource.FILE) {
                    selectedResource = selectedResource.getParent();
                }

                if ( selectedResource instanceof IProject ||selectedResource instanceof IFolder ) {
                    // Set this for the container.
                    //
                    newFilePage.setContainerFullPath(selectedResource.getFullPath());

                    // Make up a unique new name here.
                    //
                    String defModelBaseFilename = XsdUiPlugin.Util.getString("_UI_XSDEditorFilenameDefaultBase"); //$NON-NLS-1$
                    String defModelFilenameExtension = XsdUiPlugin.Util.getString("_UI_XSDEditorFilenameExtension"); //$NON-NLS-1$
                    String modelFilename = defModelBaseFilename + "." + defModelFilenameExtension; //$NON-NLS-1$
                    for (int i = 1; ((IContainer)selectedResource).findMember(modelFilename) != null; ++i) {
                        modelFilename = defModelBaseFilename + i + "." + defModelFilenameExtension; //$NON-NLS-1$
                    }
                    newFilePage.setFileName(modelFilename);
                }
            }
        }
        initialObjectPage = new XSDModelWizardInitialObjectCreationPage(XsdUiPlugin.Util.getString("XsdModelWizard.Whatever2_23")); //$NON-NLS-1$
        initialObjectPage.setTitle(XsdUiPlugin.Util.getString("_UI_XSDModelWizard_label")); //$NON-NLS-1$
        initialObjectPage.setDescription(XsdUiPlugin.Util.getString("_UI_Wizard_initial_object_description")); //$NON-NLS-1$
        // add wizard banner
        initialObjectPage.setImageDescriptor(XsdUiPlugin.INSTANCE.getImageDescriptor("icons/full/wizban/NewXSD.gif")); //$NON-NLS-1$
        addPage(initialObjectPage);
    }

    /**
     * Get the model file.
     */
    public IFile getModelFile() {
        return newFilePage.getModelFile();
    }
}
