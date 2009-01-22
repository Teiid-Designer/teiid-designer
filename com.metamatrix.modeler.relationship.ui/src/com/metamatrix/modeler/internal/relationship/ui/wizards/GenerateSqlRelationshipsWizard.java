/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public class GenerateSqlRelationshipsWizard extends AbstractWizard implements
		PluginConstants.Images, IImportWizard, StringUtil.Constants,
		UiConstants {

	// ============================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil
			.getPropertyPrefix(GenerateSqlRelationshipsWizard.class);

	private static final String WIDTH = "width"; //$NON-NLS-1$

	private static final String HEIGHT = "height"; //$NON-NLS-1$

	private static final String TITLE = getString("title"); //$NON-NLS-1$

	private static final ImageDescriptor IMAGE = UiPlugin.getDefault()
			.getImageDescriptor(GENERATE_SQL_REL_ICON);

	// Set Licensed to true. Leave licencing code in, just in case we decide
	// to license in the future...
	private static boolean importLicensed = true;

	// ============================================================================================================================
	// Static Methods

	/**
	 * <p>
	 * </p>
	 * 
	 * @since 4.0
	 */
	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	// ============================================================================================================================
	// Variables
	private GenerateSqlRelationshipsMainPage mainPage;

	// ============================================================================================================================
	// Constructors

	/**
	 * <p>
	 * </p>
	 * 
	 * @since 4.0
	 */
	public GenerateSqlRelationshipsWizard() {
		super(UiPlugin.getDefault(), TITLE, IMAGE);
	}

	// ============================================================================================================================
	// Implemented Methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    public void createPageControls(Composite pageContainer) {
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
	 * <p>
	 * </p>
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 * @since 4.0
	 */
	@Override
    public boolean finish() {
		boolean result = true;

		mainPage.finish();

		return result;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	public void init(final IWorkbench workbench,
			final IStructuredSelection selection) {
		mainPage = new GenerateSqlRelationshipsMainPage();
		
//		page.setMessage(NOT_LICENSED_MSG, IMessageProvider.ERROR);
		mainPage.setPageComplete(false);
		mainPage.setSelection(selection);
		addPage(mainPage);
	}

	// ============================================================================================================================
	// Overridden Methods

	/**
	 * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
    public IWizardPage getNextPage(final IWizardPage thePage) {
		/*----------------------------------------
		 Pages:
		 A. mainPage
		 ------------------------------------------*/

		IWizardPage result = mainPage;

		return result;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
    public IWizardPage getPreviousPage(IWizardPage thePage) {
		// if (thePage == this.importRelationalPhysicalPage) {
		// // make sure editor is saved
		// this.importRelationalPhysicalPage.handleUnsavedEditor();
		// }
		IWizardPage pPage = super.getPreviousPage(thePage);
		pPage.setVisible(true);
		return pPage;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 * @since 4.0
	 */
	@Override
    public boolean canFinish() {
		boolean canFinish = false;
		if (mainPage.isPageComplete()) {
			canFinish = true;
		}
		return canFinish;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#dispose()
	 * @since 4.0
	 */
	@Override
    public void dispose() {
		super.dispose();
	}

	// ============================================================================================================================
	// Property Methods

	// /**
	// * <p>
	// * </p>
	// *
	// * @since 4.0
	// */
	// IPath getFolder() {
	// return ResourcesPlugin.getWorkspace().getRoot().getRawLocation();
	// }
}
