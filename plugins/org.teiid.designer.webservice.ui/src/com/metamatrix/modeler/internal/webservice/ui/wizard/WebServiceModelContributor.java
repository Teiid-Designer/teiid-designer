/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.webservice.WebServiceModelProducer;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.internal.webservice.ui.wizard.WsdlSelectionPage.EditableNameField;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor2;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>WebServiceModelContributor</code> contributes pages to the new model wizard only if the new model is a Web Services
 * model.
 * 
 * @since 4.2
 */
public final class WebServiceModelContributor implements INewModelWizardContributor2, IInternalUiConstants {

    static final String PREFIX = I18nUtil.getPropertyPrefix(WebServiceModelContributor.class);

    /** The builder is responsible for building the Web Service model. */
    private IWebServiceModelBuilder builder;

    /** The pages contributed to the new model wizard. */
    private IWizardPage[] pages;

    /** The page where the namespaces are resolved to a file. */
    private WizardPage namespaceResolutionPage;

    /** The page where schema target locations are decided. */
    private WizardPage schemaLocationPage;

    /** The page where the included WSDL file(s) are selected. */
    private WizardPage wsdlSelectionPage;

    /** The page where the user selects which WSDL operations to build. */
    private WizardPage selectWsdlOperationsPage;

    /**
     * The page where the included WSDL file(s) are validated and the results of that validation are displayed.
     */
    private WizardPage wsdlValidationPage;

    /** The page where the XML file where each web service operation generates and XML document. */
    private WizardPage xmlSelectionPage;

    /**
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#canFinishEarly(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.2
     */
    public boolean canFinishEarly( IWizardPage theCurrentPage ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#createWizardPages(org.eclipse.jface.viewers.ISelection,
     *      org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IPath,
     *      com.metamatrix.modeler.core.metamodel.MetamodelDescriptor, boolean)
     * @since 4.2
     */
    public void createWizardPages( ISelection theSelection,
                                   IResource theModelResource,
                                   IPath theModelPath,
                                   MetamodelDescriptor theDescriptor,
                                   boolean theIsVirtual ) {

        this.builder = WebServicePlugin.createModelBuilder(theModelResource, theModelPath, theDescriptor);

        // construct pages

        this.wsdlSelectionPage = new WsdlSelectionPage(this.builder, EditableNameField.UNEDITABLE);
        this.wsdlValidationPage = new ImportWsdlValidationPage(this.builder);
        this.selectWsdlOperationsPage = new SelectWsdlOperationsPage(this.builder);
        this.namespaceResolutionPage = new NamespaceResolutionPage(this.builder);
        this.schemaLocationPage = new SchemaLocationPage(this.builder);
        this.xmlSelectionPage = new XmlModelSelectionPage(this.builder);

        this.pages = new IWizardPage[6];
        this.pages[0] = this.wsdlSelectionPage;
        this.pages[1] = this.wsdlValidationPage;
        this.pages[2] = this.namespaceResolutionPage;
        this.pages[3] = this.selectWsdlOperationsPage;
        this.pages[4] = this.schemaLocationPage;
        this.pages[5] = this.xmlSelectionPage;

        // give the WSDL selection page the current workspace selection
        ((WsdlSelectionPage)this.wsdlSelectionPage).setInitialSelection(theSelection);
    }

    /**
     * Disposes of all resources and performs other cleanup if necessary. Should be called when the wizard either finishes or is
     * cancelled.
     * 
     * @since 4.2
     */
    private void dispose() {
        try {
            this.builder.getModelGenerator(true).close();
        } catch (CoreException theException) {
            UTIL.log(theException);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doCancel()
     * @since 4.2
     */
    public void doCancel() {
        List newResources = this.builder.getAllNewResources();

        if (newResources != null && !newResources.isEmpty()) {
            dispose();
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doFinish(com.metamatrix.modeler.core.workspace.ModelResource,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void doFinish( ModelResource theModelResource,
                          IProgressMonitor theMonitor ) {
        int severity = this.builder.validateWSDLNamespaces().getSeverity();
        CoreArgCheck.isTrue(severity < IStatus.ERROR, "ERROR validating WSDL Namespaces"); //$NON-NLS-1$

        severity = this.builder.validateXSDNamespaces().getSeverity();
        CoreArgCheck.isTrue(severity < IStatus.ERROR, "ERROR validating XSD Namespaces"); //$NON-NLS-1$;

        try {
            IStatus status = this.builder.getModelGenerator(true).execute(theMonitor);

            // display & log messages if needed
            if (status != null) {
                // log
                logMessage(status);

                final IStatus filteredStatus = filterStatus(status);

                if (filteredStatus != null) {
                    // display
                    UiUtil.getWorkbenchShellOnlyIfUiThread().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            // ErrorDialog does not open if status OK. And doesn't ever display embedded OK statuses.
                            ErrorDialog.openError(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                                  UTIL.getString(PREFIX + "dialog.messages.title"), //$NON-NLS-1$
                                                  null,
                                                  filteredStatus,
                                                  IStatus.ERROR | IStatus.WARNING | IStatus.INFO);
                        }
                    });
                }
            }
        } catch (CoreException theException) {
            UTIL.log(theException);
            WidgetUtil.showError(UTIL.getString(PREFIX + "generateModelProblem")); //$NON-NLS-1$
        } finally {
            dispose();
        }
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
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#getWizardPages()
     * @since 4.2
     */
    public IWizardPage[] getWizardPages() {
        return this.pages;
    }

    /**
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#inputChanged(org.eclipse.jface.viewers.ISelection,
     *      org.eclipse.core.resources.IResource, com.metamatrix.modeler.core.metamodel.MetamodelDescriptor, boolean)
     * @since 4.2
     */
    public void inputChanged( ISelection theSelection,
                              IResource theTargetResource,
                              MetamodelDescriptor theDescriptor,
                              boolean theIsVirtual ) {
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
            UTIL.log(theStatus.getSeverity(), UTIL.getString(PREFIX + "logMessage", //$NON-NLS-1$
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

    public void currentPageChanged( IWizardPage page ) {

    }

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
            } else {
                /*
                 * if there are no validation messages, we skip this page and move on
                 * to the next.  nothing to show.
                 */
                nextPage = (IWizardPage)pgs.get(ndx + 2);
            }
        }
        return nextPage;
    }

    /**
     * @param page
     * @return
     */
    private int indexOf( IWizardPage page ) {
        List pgs = Arrays.asList(getPages());
        return pgs.indexOf(page);
    }

    /**
     * @return
     */
    private IWizardPage[] getPages() {
        return pages;
    }

    public IWizardPage getPreviousPage( IWizardPage page ) {
        CoreArgCheck.isNotNull(page);
        final int ndx = indexOf(page);
        // Return null if last page or page not found
        if (ndx <= 0) {
            return null;
        }
        return Arrays.asList(getPages()).get(ndx - 1);
    }
}
