/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.internal.ui.viewsupport.NewModelProjectWorker;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;


/** 
 * @since 5.0
 */
public class CloneProjectWizard extends BasicNewProjectResourceWizard
implements UiConstants {
    private WizardNewProjectCreationPage mainPage;
    
    private IProject selectedProject;
    private IProject clonedProject;
    
    /** 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     * @since 4.2 - added to address defect 15096
     */
    @Override
    public void addPages() {
        //super.addPages();
        mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");//$NON-NLS-1$
        mainPage.setTitle(Util.getString("CloneProjectWizard.title")); //$NON-NLS-1$
        mainPage.setDescription(Util.getString("CloneProjectWizard.description")); //$NON-NLS-1$
        setWindowTitle(Util.getString("CloneProjectWizard.title")); //$NON-NLS-1$
        
        this.addPage(mainPage);
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

        String projName = mainPage.getProjectName();
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

        //super.performFinish();
        clonedProject = createProject();
        if (clonedProject == null)
            return false;
        try {
            final IProjectDescription desc = clonedProject.getDescription();
            desc.setNatureIds(MODEL_NATURES);
            if (ProductCustomizerMgr.getInstance() != null) {
                String productName = ProductCustomizerMgr.getInstance().getProductName();
                if (!CoreStringUtil.isEmpty(productName)) {
                    desc.setComment(productName + ", version " + ModelerCore.ILicense.VERSION); //$NON-NLS-1$
                }
            }
            clonedProject.setDescription(desc, null);
            // Defect 11480 - closing and opening the project sets the overlay icon properly
            clonedProject.close(null);
            clonedProject.open(null);
            
            cloneProject();
            
            /*
             * jh Defect  21210: Code added to ModelWorkspaceViewerFilter (for this defect) to filter out 
             *   non-Model projects will hide a brand-new Model Project because it does not have its
             *   nature at the time the tree is populated and the filter run.  I am adding an extra 
             *   tree refresh at the end of the New Model Project process so we get a second chance to
             *   construct the tree AFTER the new project has its nature established.
             */
            
            ModelerUiViewUtils.refreshModelExplorerResourceNavigatorTree();
            
            ModelerUiViewUtils.refreshWorkspace();
            
            return true;
        } catch (final CoreException err) {
            Util.log(IStatus.ERROR, err, err.getMessage());
            return false;
        }
    }

    private void checkInvalidChars(final ValidationResult result,final String stringToValidate, final char[] invalidChars) {
        CoreArgCheck.isNotNull(stringToValidate);                
        CoreArgCheck.isNotNull(result);
        final StringNameValidator validator = new StringNameValidator(invalidChars);
        final String reasonInvalid = validator.checkInvalidCharacters(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and add it to the result
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
            result.addProblem(problem);
        }
    }
    
    public void setProject(IProject theProject) {
        selectedProject = theProject;
    }
    
    private void cloneProject() {
        try {
            ModelerCore.getModelEditor().cloneProject(selectedProject.getLocation().toOSString(), clonedProject.getLocation().toOSString());
        } catch (IOException theException) {
            UiConstants.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
        }
    }
    
    private IProject createProject() {
        IProject proj = null;
        
        IPath locationPath = mainPage.getLocationPath();
        if( locationPath != null && locationPath.toOSString().equalsIgnoreCase(Platform.getLocation().toOSString())) {
            locationPath = null;
        }
        
        NewModelProjectWorker worker = new NewModelProjectWorker();
            
        proj = worker.createNewProject(locationPath, mainPage.getProjectName(), new NullProgressMonitor());

        return proj;
    }
}
