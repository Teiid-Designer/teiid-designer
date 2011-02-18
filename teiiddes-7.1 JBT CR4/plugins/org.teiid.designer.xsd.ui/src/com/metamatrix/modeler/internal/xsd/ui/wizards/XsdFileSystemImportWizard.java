/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.util.List;
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
import org.eclipse.ui.ide.IDE;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiPlugin;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * Wizard for importing XSD resources from the local file system into the workspace.
 * 
 * @since 4.3
 */
public class XsdFileSystemImportWizard extends AbstractWizard implements IImportWizard, ModelerXsdUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(XsdFileSystemImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = ModelerXsdUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_XSD_ICON);
    private static final String NOT_LICENSED_MSG = getString("notLicensedMessage"); //$NON-NLS-1$

    private static boolean importLicensed = true;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private XsdFileSystemImportMainPage importFromFileMainPage;
    private XsdUrlImportMainPage importFromUrlMainPage;
    private XsdImportMainPage importMainPage;
    private IWorkbench workbench;
    private IStructuredSelection selection;

    /**
     * Creates a wizard for importing resources into the workspace from the file system.
     */
    public XsdFileSystemImportWizard() {
        super(ModelerXsdUiPlugin.getDefault(), TITLE, IMAGE);
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
    public void init( IWorkbench workbench,
                      IStructuredSelection currentSelection ) {
        this.workbench = workbench;
        this.selection = currentSelection;

        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        if (importLicensed) {
            importMainPage = new XsdImportMainPage();
            importFromFileMainPage = createFileMainPage(selection);
            importFromUrlMainPage = createUrlMainPage(selection);
            addPage(importMainPage);
            addPage(importFromFileMainPage);
        } else {
            // Create empty page
            WizardPage page = new WizardPage(XsdFileSystemImportWizard.class.getSimpleName(), TITLE, null) {
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

    protected XsdFileSystemImportMainPage createFileMainPage( final IStructuredSelection selection ) {
        return new XsdFileSystemImportMainPage(workbench, selection);
    }

    protected XsdUrlImportMainPage createUrlMainPage( final IStructuredSelection selection ) {
        return new XsdUrlImportMainPage(workbench, selection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = true;
        if (importMainPage.isImportFromUrl()) {
            importFromUrlMainPage.finish();
        } else {
            importFromFileMainPage.finish();
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

    @Override
    public IWizardPage getNextPage( final IWizardPage page ) {
        if (importMainPage.isImportFromUrl()) {
            removePage(importFromFileMainPage);
            if (indexOf(importFromUrlMainPage) == -1) {
                addPage(importFromUrlMainPage);
            }
        } else {
            removePage(importFromUrlMainPage);
            if (indexOf(importFromFileMainPage) == -1) {
                addPage(importFromFileMainPage);
            }
        }
        return super.getNextPage(page);
    }
}
