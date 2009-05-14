/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 4.0
 */
public class ImportModelerProjectSetWizard extends AbstractWizard
    implements IImportWizard, PluginConstants.Images, StringUtil.Constants, UiConstants, UiConstants.ProductInfo,
    UiConstants.ProductInfo.Capabilities {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ImportModelerProjectSetWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(IMPORT_PROJECT_ICON);
    private static final String NOT_LICENSED_MSG = getString("notLicensedMessage"); //$NON-NLS-1$

    private static boolean importLicensed = true;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private ImportModelerProjectSetMainPage zipPage;

    /**
     * @since 4.0
     */
    public ImportModelerProjectSetWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
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
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = true;

        zipPage.finish();

        return result;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        if (importLicensed) {
            zipPage = createMainPage(selection);
            addPage(zipPage);
        } else {
            // Create empty page
            WizardPage page = new WizardPage(ImportModelerProjectSetWizard.class.getSimpleName(), TITLE, null) {
                public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            page.setMessage(NOT_LICENSED_MSG, IMessageProvider.ERROR);
            page.setPageComplete(false);
            addPage(page);
        }
    }

    protected ImportModelerProjectSetMainPage createMainPage( final IStructuredSelection selection ) {
        return new ImportModelerProjectSetMainPage(selection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        return super.canFinish() && zipPage.hasProjects();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * @since 4.0
     */
    IPath getFolder() {
        return ResourcesPlugin.getWorkspace().getRoot().getRawLocation();
    }
}
