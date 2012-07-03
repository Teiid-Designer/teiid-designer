/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.textimport.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.tools.textimport.ui.PluginConstants;
import com.metamatrix.modeler.tools.textimport.ui.TextImportContributionManager;
import com.metamatrix.modeler.tools.textimport.ui.TextImportPlugin;
import com.metamatrix.modeler.tools.textimport.ui.UiConstants;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 4.2
 */
public class ImportTextWizard extends AbstractWizard
    implements PluginConstants.Images, IImportWizard, CoreStringUtil.Constants, UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ImportTextWizard.class);
    private static final String WIDTH = "width"; //$NON-NLS-1$
    private static final String HEIGHT = "height"; //$NON-NLS-1$

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = TextImportPlugin.getDefault().getImageDescriptor(IMPORT_PROJECT_ICON);
    private static final String NOT_LICENSED_MSG = getString("notLicensedMessage"); //$NON-NLS-1$

    // Set Licensed to true. Leave licencing code in, just in case we decide
    // to license in the future...
    private static boolean importLicensed = true;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private ImportTextMainPage importTextMainPage;
    private static ITextImportMainPage[] importers;

    /**
     * @since 4.0
     */
    public ImportTextWizard() {
        super(TextImportPlugin.getDefault(), TITLE, IMAGE);
        importers = TextImportContributionManager.getTextImporters();
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
            // If no dialog size settings, then use default of 500X500
            IDialogSettings settings = getDialogSettings();
            // Try to get height and width settings
            try {
                settings.getInt(WIDTH);
                settings.getInt(HEIGHT);
                // If height or width not found, set 500x500 default
            } catch (NumberFormatException e) {
                settings.put(WIDTH, 500);
                settings.put(HEIGHT, 500);
            }
            super.createPageControls(pageContainer);
        }
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = true;
        String importType = this.importTextMainPage.getImportType();

        for (int i = 0; i < importers.length; i++) {
            if (importers[i].getType().equals(importType)) {
                importers[i].finish();
                break;
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
    	
    	IStructuredSelection finalSelection = selection;
    	if( !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		finalSelection = new StructuredSelection(newProject);
        	}
        }
    	
        if (importLicensed) {
            importTextMainPage = new ImportTextMainPage(finalSelection);
            addPage(importTextMainPage);
            //
            for (int i = 0; i < importers.length; i++) {
                addPage((IWizardPage)importers[i]);
            }
        } else {
            // Create empty page
            WizardPage page = new WizardPage(ImportTextWizard.class.getSimpleName(), TITLE, null) {

                public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            page.setMessage(NOT_LICENSED_MSG, IMessageProvider.ERROR);
            page.setPageComplete(false);
            addPage(page);
        }
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( final IWizardPage thePage ) {
        /*----------------------------------------
         Pages:
            A. importTypeSelectionPage
            B. importPage
        ------------------------------------------*/

        IWizardPage result = null;

        // only need to define logic for those pages where the next page is dynamic.
        // the call to super will handle everything else.

        if (thePage == this.importTextMainPage) {
            String importType = this.importTextMainPage.getImportType();
            for (int i = 0; i < importers.length; i++) {
                if (importers[i].getType().equals(importType)) {
                    result = (IWizardPage)importers[i];
                    break;
                }
            }
            this.importTextMainPage.saveWidgetValues();
        } else {
            boolean isContributed = false;
            // be sure thePage is contributed
            // for( int i=0; i<importers.length; i++ ) {
            for (int i = 0; i < importers.length; i++) {
                if (thePage.equals(importers[i])) {
                    isContributed = true;
                    break;
                }
            }
            if (!isContributed) CoreArgCheck.isTrue(false, "Unexpected TextImport Wizard Page:" + thePage); //$NON-NLS-1$
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage thePage ) {
        IWizardPage pPage = super.getPreviousPage(thePage);
        pPage.setVisible(true);
        return pPage;
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        boolean canFinish = false;
        IWizardPage[] pages = this.getPages();
        // Can finish if all pages are complete
        IWizardPage page1 = pages[0];
        if (page1.isPageComplete()) {
            IWizardPage page2 = null;
            String importType = this.importTextMainPage.getImportType();
            for (int i = 0; i < importers.length; i++) {
                if (importers[i].getType().equals(importType)) {
                    page2 = (IWizardPage)importers[i];
                    break;
                }
            }
            if (page2 != null && page2.isPageComplete()) {
                canFinish = true;
            }
        }
        return canFinish;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
