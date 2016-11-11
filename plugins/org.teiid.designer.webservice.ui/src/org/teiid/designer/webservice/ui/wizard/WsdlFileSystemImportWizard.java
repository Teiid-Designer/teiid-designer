/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.ui.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.compare.ModelGenerator;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.notification.util.DefaultIgnorableNotificationSource;
import org.teiid.designer.core.transaction.UnitOfWorkImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.DeleteAction;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.JobUtils;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.NoOpenProjectsWizardPage;
import org.teiid.designer.ui.product.IModelerProductContexts.Metamodel;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;
import org.teiid.designer.webservice.IWebServiceModelBuilder;
import org.teiid.designer.webservice.WebServiceModelProducer;
import org.teiid.designer.webservice.WebServicePlugin;
import org.teiid.designer.webservice.ui.IInternalUiConstants;
import org.teiid.designer.webservice.ui.WebServiceUiPlugin;
import org.teiid.designer.webservice.ui.wizard.WsdlSelectionPage.EditableNameField;


/**
 * @since 8.0
 */
public class WsdlFileSystemImportWizard extends AbstractWizard implements IImportWizard, IInternalUiConstants {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(WsdlFileSystemImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = WebServiceUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_WSDL);
    private static final String NOT_LICENSED_MSG = getString("notLicensedMessage"); //$NON-NLS-1$
    private static final String WEB_SERVICES_CLASS_ID = "Web Service"; //$NON-NLS-1$
    private static final String DEPRECATED_SUFFIX = " (Deprecated)"; //$NON-NLS-1$

    private static boolean importLicensed = true;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    /** The builder is responsible for building the Web Service model. */
    private IWebServiceModelBuilder builder;

    /** The page where the namespaces are resolved to a file. */
    private WizardPage namespaceResolutionPage;

    /** The page where schema target locations are decided. */
    private WizardPage schemaLocationPage;

    /** The page where the included WSDL file(s) are selected. */
    private WizardPage wsdlSelectionPage;

    /** The page where the user selects which WSDL operations to build. */
    private WizardPage selectWsdlOperationsPage;

    /** The page where the XML file where each web service operation generates and XML document. */
    private WizardPage xmlSelectionPage;

    /**
     * The page where the included WSDL file(s) are validated and the results of that validation are displayed. See: Defect 24620
     * - missing the WSDL validation page.
     */
    private WizardPage wsdlValidationPage;

    private IStructuredSelection selection;
    private IStatus status;
    private IContainer folder;

    /** Defect 23075 - needed global rollback and autoBuildOn flags for use in runnable */
    boolean rollbackNewResources = false;
    private boolean autoBuildOn = false;

    /**
     * Creates a wizard for importing resources into the workspace from the file system.
     */
    public WsdlFileSystemImportWizard() {
        super(WebServiceUiPlugin.getDefault(), TITLE, IMAGE);
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        if (importLicensed) {
            super.createPageControls(pageContainer);
        }
    }

    /**
     * Method declared on IWorkbenchWizard.
     */
    @Override
	public void init( IWorkbench workbench,
                      IStructuredSelection currentSelection ) {
        this.selection = currentSelection;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            // get the project
            this.selection = new StructuredSelection(
                                                     ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject());
        } else {
            List selectedResources = IDE.computeSelectedResources(currentSelection);
            if (!selectedResources.isEmpty()) {
                this.selection = new StructuredSelection(selectedResources);
            }
        }
        
    	if( !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		this.selection = new StructuredSelection(newProject);
        	} else {
        		addPage(NoOpenProjectsWizardPage.getStandardPage());
        		return;
        	}
        }

        if (importLicensed) {
            createWizardPages(this.selection);
        } else {
            // Create empty page
            WizardPage page = new WizardPage(WsdlFileSystemImportWizard.class.getSimpleName(), TITLE, null) {
                @Override
				public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            page.setMessage(NOT_LICENSED_MSG, IMessageProvider.ERROR);
            page.setPageComplete(false);
            addPage(page);
        }
        setNeedsProgressMonitor(true);

    }

    public void createWizardPages( ISelection theSelection ) {

        if (selection != null && !selection.isEmpty()) {
            final Object obj = selection.getFirstElement();
            final IContainer someFolder = ModelUtil.getContainer(obj);
            try {
                if (someFolder != null && someFolder.getProject().getNature(ModelerCore.NATURE_ID) != null) {
                    this.folder = someFolder;
                }
            } catch (final CoreException err) {
                IInternalUiConstants.UTIL.log(err);
                WidgetUtil.showError(err);
            }
        }

        IPath theModelPath = null;
        if (folder!=null) {
        	theModelPath = folder.getFullPath();
        }
        MetamodelDescriptor theDescriptor = getGenerateFromWsdlDescriptor();

        this.builder = WebServicePlugin.createModelBuilder(folder, theModelPath, theDescriptor);
        // User new method to wait until finished before saving resources Fixes import problem
        this.builder.setSaveAllBeforeFinish(true);

        // construct pages
        this.wsdlSelectionPage = new WsdlSelectionPage(this.builder, EditableNameField.EDITABLE);
        wsdlSelectionPage.setPageComplete(false);
        addPage(wsdlSelectionPage);

        // Defect 24620 - missing the WSDL validation page.
        this.wsdlValidationPage = new ImportWsdlValidationPage(this.builder);
        wsdlValidationPage.setPageComplete(true);
        addPage(wsdlValidationPage);

        this.namespaceResolutionPage = new NamespaceResolutionPage(this.builder);
        namespaceResolutionPage.setPageComplete(false);
        addPage(namespaceResolutionPage);

        this.selectWsdlOperationsPage = new SelectWsdlOperationsPage(this.builder);
        selectWsdlOperationsPage.setPageComplete(false);
        addPage(selectWsdlOperationsPage);

        this.schemaLocationPage = new SchemaLocationPage(this.builder);
        addPage(schemaLocationPage);

        this.xmlSelectionPage = new XmlModelSelectionPage(this.builder);
        addPage(xmlSelectionPage);

        // give the WSDL selection page the current workspace selection
        ((WsdlSelectionPage)this.wsdlSelectionPage).setInitialSelection(theSelection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = true;

        /*
         * 'finish' will use the previously created 'processor' instead of creating a fresh one, if one was previously created.
         */
        autoBuildOn = ModelerCore.getWorkspace().isAutoBuilding();

        if (autoBuildOn) {
            JobUtils.setAutoBuild(false);
        }

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
			public void run( final IProgressMonitor monitor ) {
                // Wrap in transaction so it doesn't result in Significant Undoable
                boolean started = ModelerCore.startTxn(false, false, "Create WS from WSDL on File System", //$NON-NLS-1$
                                                       new DefaultIgnorableNotificationSource(WsdlFileSystemImportWizard.this));
                boolean succeeded = false;
                boolean overrideRollback = false;
                if (started) {
                    overrideRollback = true;
                    ((UnitOfWorkImpl)ModelerCore.getCurrentUoW()).setOverrideRollback(overrideRollback);
                }
                try {
                    // Defect 23075 setting succeeded to the return value of runFinish() so we can catch if the operation was
                    // cancelled or not. We need to delete any new resources if user cancelled.
                    succeeded = !runFinish(monitor);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            if (overrideRollback) {
                                rollbackNewResources = true;
                            } else {
                                ModelerCore.rollbackTxn();
                            }
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        };
        try {
            new ProgressMonitorDialog(getShell()).run(true, true, op);
            switch (this.status.getSeverity()) {
                case IStatus.WARNING:
                case IStatus.INFO:
                case IStatus.OK:
                    break;
                case IStatus.ERROR:
                default:
                    IInternalUiConstants.UTIL.log(this.status);
                    // WidgetUtil.showError(IMPORT_ERROR_MESSAGE);
                    break;
            }
            result = true;
        } catch (Throwable err) {
        } finally {
            if (rollbackNewResources) {
                // Defect 23075 - Process the builder's new resources and DELETE them as part of this rollback.
                final List newResources = builder.getAllNewResources();
                if (newResources != null && !newResources.isEmpty()) {

                    DeleteAction action = new DeleteAction();
                    action.selectionChanged(null, new StructuredSelection(newResources));
                    action.run();

                    // Defect 23075 - if auto-build is ON, we need to listen for the DELETE action being DONE, then we can
                    // re-set the auto-build to ON again. This insures that we don't auto-build on resources that we are going
                    // to delete anyway.

                    if (autoBuildOn) {
                        if (JobUtils.jobExists(IDEWorkbenchMessages.DeleteResourceAction_jobName)) {
                            Job.getJobManager().addJobChangeListener(new SetAutobuildOnJobListener());
                        } else {
                            JobUtils.setAutoBuild(true);
                        }
                    }
                }
            } else {
                JobUtils.setAutoBuild(autoBuildOn);
            }

            dispose();
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        return super.canFinish();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    public boolean runFinish( IProgressMonitor theMonitor ) {
        boolean wasCancelled = false;

        int severity = this.builder.validateWSDLNamespaces().getSeverity();
        CoreArgCheck.isTrue(severity < IStatus.ERROR, "ERROR validating WSDL Namespaces"); //$NON-NLS-1$

        severity = this.builder.validateXSDNamespaces().getSeverity();
        CoreArgCheck.isTrue(severity < IStatus.ERROR, "ERROR validating XSD Namespaces"); //$NON-NLS-1$;

        // Check if user canceled yet (Defect 23075)
        if (!theMonitor.isCanceled()) {
            try {

                this.status = this.builder.getModelGenerator(true).execute(theMonitor);
                // Check again if user cancelled yet (Defect 23075)
                if (!wasCancelled && !theMonitor.isCanceled()) {
                    // display & log messages if needed
                    if (this.status != null) {
                        // log
                        logMessage(this.status);

                        final IStatus filteredStatus = filterStatus(this.status);

                        if (filteredStatus != null) {
                            // display
                            UiUtil.getWorkbenchShellOnlyIfUiThread().getDisplay().asyncExec(new Runnable() {
                                @Override
								public void run() {
                                    // ErrorDialog does not open if status OK. And doesn't ever display embedded OK statuses.
                                    ErrorDialog.openError(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                                          UTIL.getString(I18N_PREFIX + "dialog.messages.title"), //$NON-NLS-1$
                                                          null,
                                                          filteredStatus,
                                                          IStatus.ERROR | IStatus.WARNING | IStatus.INFO);
                                }
                            });
                        }
                    }
                } else {
                    wasCancelled = true;
                }
            } catch (CoreException theException) {
                UTIL.log(theException);
                WidgetUtil.showError(UTIL.getString(I18N_PREFIX + "generateModelProblem")); //$NON-NLS-1$
            }
        } else {
            // Defect 23075
            wasCancelled = true;
        }
        return wasCancelled;
    }

    private IStatus filterStatus( IStatus theStatus ) {

        IStatus result = null;

        if (theStatus instanceof MultiStatus) {
            result = new MultiStatus(theStatus.getPlugin(), theStatus.getCode(), theStatus.getMessage(), theStatus.getException());
        } else {
            result = theStatus;
        }

        if (result instanceof MultiStatus) {
            MultiStatus multiStatus = (MultiStatus)result;
            IStatus[] kids = multiStatus.getChildren();

            for (int i = 0; i < kids.length; i++) {
                IStatus kidStatus = filterStatus(kids[i]);

                if (kidStatus != null) {
                    multiStatus.add(kidStatus);
                }
            }

            if (multiStatus.getChildren().length == 0) {
                result = null;
            }
        } else {
            // filter here
            if (result.getSeverity() == IStatus.WARNING) {
                int code = result.getCode();

                if ((code == WebServiceModelProducer.WARNING_NO_WSDL_OBJECTS) || (code == ModelGenerator.COMPLETED_WITH_WARNINGS)) {
                    result = null;
                }
            }
        }

        return result;
    }

    /**
     * Writes the specified <code>IStatus</code> severity, code, and message to the log. <code>IStatus.OK</code> messages are not
     * logged.
     * 
     * @param theStatus the status being logged
     * @since 4.2
     */
    private void logMessage( IStatus theStatus ) {
        if (theStatus.getSeverity() != IStatus.OK) {
            UTIL.log(theStatus.getSeverity(), UTIL.getString(I18N_PREFIX + "logMessage", //$NON-NLS-1$
                                                             new Object[] {String.valueOf(theStatus.getCode()),
                                                                 theStatus.getMessage()}));

            if (theStatus.isMultiStatus()) {
                IStatus[] kids = theStatus.getChildren();

                for (int i = 0; i < kids.length; i++) {
                    logMessage(kids[i]);
                }
            }
        }
    }

    protected MetamodelDescriptor getGenerateFromWsdlDescriptor() {

        // this method is called during contruction
        Collection /*<MetamodelDescriptor>*/mmdescs = Arrays.asList(ModelerCore.getMetamodelRegistry().getMetamodelDescriptors());
        MetamodelDescriptor wsmmd = null;
        Iterator it = mmdescs.iterator();
        while (it.hasNext() && wsmmd == null) {
            MetamodelDescriptor mmd = (MetamodelDescriptor)it.next();
            String baseDisplayName = mmd.getDisplayName();
            if( baseDisplayName.endsWith(DEPRECATED_SUFFIX) ) {
            	int length = baseDisplayName.length();
            	baseDisplayName = baseDisplayName.substring(0, length - DEPRECATED_SUFFIX.length());
            }
            if (UiPlugin.getDefault().isProductContextValueSupported(Metamodel.URI, mmd.getNamespaceURI())
                && mmd.supportsNewModel()) {
                List typeList = new ArrayList(Arrays.asList(mmd.getAllowableModelTypes()));
                typeList.remove(ModelType.METAMODEL_LITERAL);

                if (mmd.getName().equalsIgnoreCase(WEB_SERVICES_CLASS_ID )
                    || baseDisplayName.equalsIgnoreCase(WEB_SERVICES_CLASS_ID)) {
                    wsmmd = mmd;
                }
            }
        }

        return wsmmd;
    }

    class SetAutobuildOnJobListener extends JobChangeAdapter {

        @Override
        public void done( IJobChangeEvent theEvent ) {
            if (theEvent.getJob().getName() != null
                && theEvent.getJob().getName().equals(IDEWorkbenchMessages.DeleteResourceAction_jobName)) {
                Job.getJobManager().removeJobChangeListener(this);

                Display.getDefault().asyncExec(new Runnable() {
                    @Override
					public void run() {
                        JobUtils.setAutoBuild(true);
                    }
                });
            }
        }

    }

    /**
     * Overriding so we can set the messages in the validation page See: Defect 24620 - missing the WSDL validation page.
     * 
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     * @since 5.0
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        CoreArgCheck.isNotNull(page);
        final int ndx = indexOf(page);
        List pgs = Arrays.asList(getPages());
        // Return null if last page or page not found
        if (ndx == pgs.size() - 1 || ndx < 0) {
            return null;
        }
        IWizardPage nextPage = (IWizardPage)pgs.get(ndx + 1);
        if (nextPage instanceof ImportWsdlValidationPage) {
            Map wsdlMessages = ((WsdlSelectionPage)page).getWsdlValidationMessages();
            ImportWsdlValidationPage validationPage = (ImportWsdlValidationPage)nextPage;
            validationPage.clearValidationMessages();
            if (wsdlMessages.size() > 0) {
                validationPage.setValidationMessages(wsdlMessages);
                validationPage.setPageComplete(false);
            } else {
                /*
                 * if there are no validation messages, we skip this page and move on
                 * to the next.  nothing to show.
                 */
                // If page complete isn't set to
                validationPage.setPageComplete(true);
                nextPage = (IWizardPage)pgs.get(ndx + 2);
            }
        }
        return nextPage;
    }
}
