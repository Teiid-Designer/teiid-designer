/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.DiagramHelperManager;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class RenameAction extends ModelObjectAction implements UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RenameAction.class);

    /**
     * @since 4.0
     */
    static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    /**
     * @since 4.0
     */
    static String getString( final String id, final Object arg1, final Object arg2 ) {
        return Util.getString(I18N_PREFIX + id, arg1, arg2);
    }

    /**
     * @since 4.0
     */
    static String getString( final String id,
                             final Object parameter ) {
        return Util.getString(I18N_PREFIX + id, parameter);
    }

    String newName;
    private EAttribute nameAttr;

    /**
     * @since 4.0
     */
    public RenameAction() {
        super(UiPlugin.getDefault());
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        EObject obj = (EObject)getSelectedObject();
        String oldName = ModelerCore.getModelEditor().getName(obj);
        
        // Get object location
        String fullObjectName = null;
        Object parent = ModelUtilities.getModelContentProvider().getParent(obj);
        if(parent instanceof EObject) {
        	fullObjectName = ModelerCore.getModelEditor().getModelRelativePathIncludingModel((EObject)parent).toString();
        } else {
        	fullObjectName = ModelerCore.getModelEditor().getModelName(obj);
        }
        
        
        String objectType = obj.eClass().getName();
        RenameDialog dlg = new RenameDialog(Display.getDefault().getActiveShell(), fullObjectName, oldName, objectType);
            
        if (dlg.open() == Window.OK) {
            ModelObjectUtilities.rename(obj, this.newName, this);
        }
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged( final IWorkbenchPart part,
                                  final ISelection selection ) {
        super.selectionChanged(part, selection);
        determineEnablement();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged( SelectionChangedEvent theEvent ) {
        super.selectionChanged(theEvent);
        determineEnablement();
    }

    /**
     * @since 4.0
     */
    protected EAttribute getNameAttribute() {
        return this.nameAttr;
    }

    /**
     * @since 4.0
     */
    protected void determineEnablement() {
        boolean enable = false;
        if (!isEmptySelection() && !isReadOnly() && canLegallyEditResource()) {
            if (SelectionUtilities.isSingleSelection(getSelection())) {
                final EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
                if (eObj != null) {
                    if (eObj instanceof Diagram) {
                        enable = DiagramHelperManager.canRename((Diagram)eObj);
                    } else if (eObj instanceof ModelImport) {
                        enable = false;
                    } else {
                        enable = ModelerCore.getModelEditor().hasName(eObj);
                    }
                }
            }
        }
        setEnabled(enable);
    }

    /**
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

    class RenameDialog extends TitleAreaDialog {
    	String oldName;
    	String fullPath;
    	String type;
    	StringNameValidator nameValidator = new StringNameValidator();

		/**
		 * @param parent shell
		 * @param fullPath the full path name in model
		 * @param originalName the original object name
		 * @param objectType the name of the object type
		 */
		public RenameDialog(Shell parent, String fullPath, String originalName, String objectType) {
			super(parent);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			this.oldName = originalName;
			this.fullPath = fullPath;
			this.type = objectType;
		}
		
		/**
		 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
		 * @since 5.5.3
		 */
		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText(getString("dialogTitle")); //$NON-NLS-1$
		}
		
        @Override
        protected Control createDialogArea( final Composite parent ) {
            Composite outerPanel = (Composite)super.createDialogArea(parent);
            Composite innerPanel = new Composite(outerPanel, SWT.NONE);
    		GridLayout gridLayout = new GridLayout();
    		gridLayout.numColumns = 2;
    		innerPanel.setLayout(gridLayout);
    		innerPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
            
    		// set title
    		setTitle(getString("subtitle", type, oldName)); //$NON-NLS-1$
    		setMessage(getString("initialMessage")); //$NON-NLS-1$
            
    		WidgetFactory.createLabel(innerPanel, getString("location")); //$NON-NLS-1$
    		WidgetFactory.createLabel(innerPanel, fullPath);
    		
            WidgetFactory.createLabel(innerPanel, getString("nameLabel"));  //$NON-NLS-1$

            final Text nameText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, oldName);
            if (oldName != null) {
                nameText.setSelection(0, oldName.length());
            }
            nameText.addModifyListener(new ModifyListener() {
                @Override
				public void modifyText( final ModifyEvent event ) {
                    handleModifyText(nameText);
                }
            });
            return innerPanel;
        }

        @Override
        protected void createButtonsForButtonBar( final Composite parent ) {
            super.createButtonsForButtonBar(parent);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }

        void handleModifyText( Text nameText ) {
            newName = nameText.getText();
            validate();
        }
        
        public void validate() {
        	IStatus status = Status.OK_STATUS;
        	
    		if( newName == null || newName.trim().length() == 0 ) {
    			status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, getString("emptyNameErrorMessage")); //$NON-NLS-1$
    		} else {
	    		// Validate non-null string
	    		String errorMessage = nameValidator.checkValidName(newName);
	    		if( errorMessage != null && !errorMessage.isEmpty() ) {
	    			status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, errorMessage);
	    		}
    		}
    		if( status.isOK() ) {
    			if( newName.equalsIgnoreCase(oldName)) {
    				status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, getString("sameNameErrorMessage", newName)); //$NON-NLS-1$
    			}
    		}
    		
    		if (status.getSeverity() == IStatus.ERROR) {
    			getButton(OK).setEnabled(false);
    			setErrorMessage(status.getMessage());
    		} else {
    			getButton(OK).setEnabled(true);
    			setErrorMessage(null);
    			setMessage(getString("okMessage")); //$NON-NLS-1$
    		}
        }
    }
    
}
