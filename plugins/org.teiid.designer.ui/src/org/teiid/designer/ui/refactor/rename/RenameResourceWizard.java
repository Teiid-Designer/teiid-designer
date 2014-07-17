/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.rename;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.validation.rules.CoreValidationRulesUtil;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 *
 */
public class RenameResourceWizard extends RefactoringWizard {

    /**
     * @param refactoring
     * @param pageTitle
     */
    public RenameResourceWizard(RenameResourceRefactoring refactoring, String pageTitle) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(pageTitle);
    }

    @Override
    protected void addUserInputPages() {
        IResource resource = ((RenameResourceRefactoring) getRefactoring()).getResource();
        addPage(new RenameResourceDestinationPage(resource));
    }

    private static class RenameResourceDestinationPage extends UserInputWizardPage {
    	
    	static final String STAR_DOT = "*."; //$NON-NLS-1$
    	private String initialFileExtension;
    	
        private final IResource resource;
        
        private Text nameField;

        public RenameResourceDestinationPage(IResource resource) {
            super(RefactorResourcesUtils.getString("RenameRefactoring.renameResourcePage")); //$NON-NLS-1$

            CoreArgCheck.isNotNull(resource);
            this.resource = resource;
            
            initialFileExtension = this.resource.getFileExtension();
        }

        @Override
        public RenameResourceRefactoring getRefactoring() {
            return (RenameResourceRefactoring) super.getRefactoring();
        }

        private RefactoringStatus createErrorStatus(String key, Object... args) {
            return RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString(key, args));
        }
        
        /**
         * Determine if the target resource were changed to the proposed name, is there another resource
         * in the same container already named the proposed name.
         * 
         * @param proposedName
         * @return true if the proposed name clashes with a sibling; otherwise, false.
         */
        private boolean siblingNameClash(String proposedName) {
            IPath newPath = resource.getFullPath().removeLastSegments(1).append(proposedName);
            final IWorkspaceRoot workspaceRoot = resource.getWorkspace().getRoot();
            final boolean result = workspaceRoot.findMember(newPath) != null;
            return result;
        }

        private final void validatePage() {
            String newName = nameField.getText();

            if (newName == null || newName.length() == 0) {
                setPageComplete(createErrorStatus("RenameRefactoring.invalidNoName")); //$NON-NLS-1$
                return;
            }

            // check the name
            char[] validChars = new char[] { '.', '_' };
            final ValidationResultImpl result = new ValidationResultImpl(newName);
            CoreValidationRulesUtil.validateStringNameChars(result, newName, validChars); 
            if (result.hasProblems()) {
                ValidationProblem problem = result.getProblems()[0];
                setPageComplete(RefactoringStatus.createErrorStatus(problem.getMessage()));
                return;
            }
            
            // since name validations allow periods, it is necessary to ensure there is only 1
            for (int i = 0, invalid = 0; i < newName.length(); ++i) {
                if (validChars[0] == newName.charAt(i)) {
                    invalid++;
                }
                
                if (invalid > 1) {
                    setPageComplete(createErrorStatus("RenameRefactoring.invalidPeriodsInName")); //$NON-NLS-1$
                    return;
                }
            }

            // check for meaningless operation
            if (resource.getName().equals(newName)) {
                setPageComplete(createErrorStatus("RenameRefactoring.invalidSameName")); //$NON-NLS-1$
                return;
            }

            // check for siblings
            if (siblingNameClash(newName)) {
                setPageComplete(createErrorStatus("RenameRefactoring.invalidNameClash")); //$NON-NLS-1$
                return;
            }
            
            // Check for proper File extension
            if( initialFileExtension != null ) {
            	if( !newName.toUpperCase().endsWith(StringConstants.DOT + initialFileExtension.toUpperCase())) {
            		String extString = STAR_DOT + initialFileExtension;
            		setPageComplete(createErrorStatus("RenameRefactoring.invalidFileExtension", extString )); //$NON-NLS-1$
                    return;
            	}
            }

            getRefactoring().setNewResourceName(newName);
            setPageComplete(RefactoringStatus.create(Status.OK_STATUS));
        }

        @Override
        public void createControl(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout(2, false));
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            composite.setFont(parent.getFont());

            Label label = new Label(composite, SWT.NONE);
            label.setText(RefactorResourcesUtils.getString("RenameRefactoring.nameLabel")); //$NON-NLS-1$
            label.setLayoutData(new GridData());

            nameField = new Text(composite, SWT.BORDER);
            nameField.setText(resource.getName());
            nameField.setFont(composite.getFont());
            nameField.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
            nameField.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    validatePage();
                }
            });

            nameField.selectAll();
            setPageComplete(false);
            setControl(composite);
        }
    }

}
