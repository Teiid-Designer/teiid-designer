/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 * Wizard which allows importing existing VDB files into the user's workspace. The resulting workspace items will replicate the
 * project/folder/model structure defined within the VDB. All other VDB artifacts, including manifest files and index files will not
 * be extracted into the workspace.
 * 
 * @author BLaFond
 */

public class ImportVdbWizard extends AbstractWizard
    implements IImportWizard, PluginConstants.Images, CoreStringUtil.Constants, VdbUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ImportVdbWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final ImageDescriptor IMAGE = null; // VdbUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_VDB_ICON);

    private static final String NOT_LICENSED_MSG = getString("notLicensedMessage"); //$NON-NLS-1$

    private static boolean importLicensed = true;

    /**
     * @param id
     * @return ?
     * @since 4.0
     */
    private static String getString( final String id ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + id);
    }

    private ImportVdbMainPage zipPage;

    /**
     * @since 4.0
     */
    public ImportVdbWizard() {
        super(VdbUiPlugin.singleton, TITLE, IMAGE);
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * @param selection
     * @return ?
     */
    protected ImportVdbMainPage createMainPage( final IStructuredSelection selection ) {
        return new ImportVdbMainPage();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( final Composite pageContainer ) {
        if (importLicensed) super.createPageControls(pageContainer);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        final boolean result = true;

        zipPage.finish();

        return result;
    }

    IPath getFolder() {
        return ModelerCore.getWorkspace().getRoot().getRawLocation();
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
            final WizardPage page = new WizardPage(ImportVdbWizard.class.getSimpleName(), TITLE, null) {
                public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            page.setMessage(NOT_LICENSED_MSG, IMessageProvider.ERROR);
            page.setPageComplete(false);
            addPage(page);
        }
    }
}
