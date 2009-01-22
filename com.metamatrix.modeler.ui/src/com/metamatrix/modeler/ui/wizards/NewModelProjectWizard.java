/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 4.0
 */
public class NewModelProjectWizard extends BasicNewProjectResourceWizard
implements UiConstants {

    /** 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     * @since 4.2 - added to address defect 15096
     */
    @Override
    public void addPages() {
        super.addPages();
        IWizardPage mainPage = super.getPage("basicNewProjectPage"); //$NON-NLS-1$
    	mainPage.setTitle(Util.getString("NewModelProjectWizard.title")); //$NON-NLS-1$
    	mainPage.setDescription(Util.getString("NewModelProjectWizard.description")); //$NON-NLS-1$
    	setWindowTitle(Util.getString("NewModelProjectWizard.title")); //$NON-NLS-1$
    }
    //============================================================================================================================
	// Constants
    
    private static final String[] MODEL_NATURES = new String[] {ModelerCore.NATURE_ID}; 
    
    //============================================================================================================================
	// BasicNewProjectResourceWizard Methods
    
    /**
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#performFinish()
	 * @since 4.0
	 */
	@Override
    public boolean performFinish() {
		// Check the project name validity before proceeding
        IWizardPage mainPage = super.getPage("basicNewProjectPage"); //$NON-NLS-1$
        if(mainPage instanceof WizardNewProjectCreationPage) {
        	String projName = ((WizardNewProjectCreationPage)mainPage).getProjectName();
            final ValidationResultImpl result = new ValidationResultImpl(projName);
            // Prevent usage of this list of chars in project names
            checkInvalidChars(result, projName, UiConstants.NamingAttributes.INVALID_PROJECT_CHARS);
            if (result.hasProblems()) {
            	String eMsg = Util.getString("NewModelProjectWizard.namingError.msg") + '\n' + //$NON-NLS-1$
            	              result.getProblems()[0].getMessage();
                String eTitle = Util.getString("NewModelProjectWizard.namingError.title"); //$NON-NLS-1$
                MessageDialog.openWarning( getShell(), eTitle, eMsg );
            	return false;
            }
            // Prevent usage of the list of reserved project names
            checkReservedProjName(result, projName);
            if (result.hasProblems()) {
            	String eMsg = Util.getString("NewModelProjectWizard.namingError.msg") + '\n' + //$NON-NLS-1$
            	              result.getProblems()[0].getMessage();
                String eTitle = Util.getString("NewModelProjectWizard.namingError.title"); //$NON-NLS-1$
                MessageDialog.openWarning( getShell(), eTitle, eMsg );
            	return false;
            }
        }
        super.performFinish();
        final IProject project = getNewProject();
        if (project == null)
            return false;
        try {
            final IProjectDescription desc = project.getDescription();
            desc.setNatureIds(MODEL_NATURES);
            if (ProductCustomizerMgr.getInstance() != null) {
                String productName = ProductCustomizerMgr.getInstance().getProductName();
                if (!StringUtil.isEmpty(productName)) {
                    desc.setComment(productName + ", version " + ModelerCore.ILicense.VERSION); //$NON-NLS-1$
                }
            }
            project.setDescription(desc, null);
            // Defect 11480 - closing and opening the project sets the overlay icon properly
            project.close(null);
            project.open(null);
            
            /*
             * jh Defect  21210: Code added to ModelWorkspaceViewerFilter (for this defect) to filter out 
             *   non-Model projects will hide a brand-new Model Project because it does not have its
             *   nature at the time the tree is populated and the filter run.  I am adding an extra 
             *   tree refresh at the end of the New Model Project process so we get a second chance to
             *   construct the tree AFTER the new project has its nature established.
             */
            refreshModelExplorerResourceNavigatorTree();

            return true;
        } catch (final CoreException err) {
            Util.log(IStatus.ERROR, err, err.getMessage());
            return false;
        }
	}
    

    private void refreshModelExplorerResourceNavigatorTree() {
        // activate the Model Explorer view (must do this last)
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                try {
                    ModelExplorerResourceNavigator view = 
                        (ModelExplorerResourceNavigator) UiUtil.getWorkbenchPage().showView(Extensions.Explorer.VIEW);
                    view.getTreeViewer().refresh( true );
                } catch (PartInitException err) {
                    Util.log(IStatus.ERROR, err, err.getMessage());
                }
            }
        });

    }

    private void checkInvalidChars(final ValidationResult result,final String stringToValidate, final char[] invalidChars) {
        ArgCheck.isNotNull(stringToValidate);                
        ArgCheck.isNotNull(result);
        final StringNameValidator validator = new StringNameValidator(invalidChars);
        final String reasonInvalid = validator.checkInvalidCharacters(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and addit to the resuly
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
            result.addProblem(problem);
        }
    }
    
    private void checkReservedProjName(final ValidationResult result,final String name) {
        ArgCheck.isNotNull(name);                
        ArgCheck.isNotNull(result);
        if(ModelerCore.isReservedProjectName(name)) {
            String reservedProjMsg = Util.getString("NewModelProjectWizard.reservedProjNameError",name); //$NON-NLS-1$

            // create validation problem and addit to the resuly
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reservedProjMsg);
            result.addProblem(problem);
        }
    }
}
