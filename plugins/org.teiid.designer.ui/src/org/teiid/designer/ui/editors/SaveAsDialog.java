/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.refactor.ModelCopyCommand;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;


/**
 * SaveAsDialog is a dialog that displays the workspace tree and allows selection Example: <code>
 // ==================================
 // launch Save As Dialog
 // ==================================

 SaveAsDialog dialog = new SaveAsDialog( getShell(), getModelResource() );
 dialog.open();

 if ( dialog.getReturnCode() == Dialog.OK ) {
 RefactorCommand command = dialog.getCommand();
 command.execute();
 }
 *</code>
 *
 * @since 8.0
 */
public class SaveAsDialog extends ElementTreeSelectionDialog implements UiConstants {

    private static final String TITLE = UiConstants.Util.getString("SaveAsDialog.title"); //$NON-NLS-1$
    private static final String SELECT_MESSAGE = UiConstants.Util.getString("SaveAsDialog.message"); //$NON-NLS-1$
    static final String FOLDER_MESSAGE = UiConstants.Util.getString("SaveAsDialog.folderOrProjectOnly"); //$NON-NLS-1$
    static final String MODEL_PROJECT_MESSAGE = UiConstants.Util.getString("SaveAsDialog.notModelProject"); //$NON-NLS-1$
    static final String CLOSED_PROJECT_MESSAGE = UiConstants.Util.getString("SaveAsDialog.closedProject"); //$NON-NLS-1$
    static final String SAME_NAME_RESOURCE_IN_PROJECT_MESSAGE = UiConstants.Util.getString("SaveAsDialog.sameNameResourceInProject"); //$NON-NLS-1$
    private static final String NAME_MESSAGE = UiConstants.Util.getString("SaveAsDialog.modelName"); //$NON-NLS-1$
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private IResource resource;
    ModelCopyCommand command;
    Text fileNameText;

    /**
     * Construct an instance of FileFolderMoveDialog. This constructor defaults to the resource root.
     * 
     * @param modelResource the ModelResource to be copied to a new model
     * @param parent the shell
     */
    public SaveAsDialog( Shell parent,
                         ModelResource modelResource ) {
        super(parent, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());

        init(modelResource);
    }

    private void init( ModelResource modelResource ) {
        // use resource filter used by model explorer
        addFilter(new ModelingResourceFilter());

        this.command = new ModelCopyCommand();
        command.setModelToCopy(modelResource);
        this.resource = modelResource.getResource();

        setAllowMultiple(false);
        // set the title from the resource
        setTitle(TITLE);
        setMessage(SELECT_MESSAGE);

        super.setValidator(new SaveAsValidator());

        // set input
        setInput(this.resource.getWorkspace().getRoot());

    }

    /**
     * Overridden to insert a text area for the new file name
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout());
        GridData panelData = new GridData(GridData.FILL_BOTH);
        panel.setLayoutData(panelData);

        super.createDialogArea(panel);

        Label label = new Label(panel, SWT.NONE);
        label.setText(NAME_MESSAGE);
        label.setFont(parent.getFont());

        fileNameText = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL, getResourceName());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = convertHeightInCharsToPixels(1);
        fileNameText.setLayoutData(data);
        fileNameText.setText(getResourceName());

        // leave some whitespace between file field and message area
        label = new Label(panel, SWT.NONE);
        label.setText(EMPTY_STRING);

        registerControls();

        return panel;
    }

    @Override
    public void setValidator( ISelectionStatusValidator validator ) {
        // no validators allowed except the SaveAsValidator
    }

    public ModelCopyCommand getCommand() {
        return this.command;

    }

    protected IStatus setContainer( IContainer container ) {
        command.setNewModelDestination(container, fileNameText.getText());
        return command.canExecute();
    }

    private String getResourceName() {
        return resource.getName().substring(0, resource.getName().indexOf('.'));
    }

    private void registerControls() {
        fileNameText.addFocusListener(new FocusListener() {

            @Override
			public void focusGained( final FocusEvent event ) {
            }

            @Override
			public void focusLost( final FocusEvent event ) {
                updateOkButton();
            }
        });

        fileNameText.addKeyListener(new KeyListener() {

            @Override
			public void keyPressed( final KeyEvent event ) {
            }

            @Override
			public void keyReleased( final KeyEvent event ) {
                updateOkButton();
            }
        });
    }

    void updateOkButton() {
        updateOKStatus();
    }

    boolean isModelProject( IContainer container ) {
        IProject project = container.getProject();
        if (project != null) return ModelerCore.hasModelNature(project);

        return false;
    }

    public class SaveAsValidator implements ISelectionStatusValidator {
        @Override
		public IStatus validate( Object[] selection ) {
            IStatus result = null;

            if (selection.length == 1 && selection[0] != null) {
                if (selection[0] instanceof IContainer) {
                	IContainer container = (IContainer)selection[0];
                	// Check if model name is same as initial name check for same model project
                	String newName = fileNameText.getText();
                	String currentName = resource.getProjectRelativePath().removeFileExtension().lastSegment();
                	if( StringUtilities.equals(newName, currentName) && container.getProject() == resource.getProject()) {
                		result = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, SAME_NAME_RESOURCE_IN_PROJECT_MESSAGE, null);
                	} else if (selection[0] instanceof IProject && !((IProject)selection[0]).isOpen()) {
                        result = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, CLOSED_PROJECT_MESSAGE, null);
                    } else if (isModelProject((IContainer)selection[0])) {
                        result = setContainer((IContainer)selection[0]);
                    } else {
                        command.setNewModelDestination(null, fileNameText.getText());
                        result = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, MODEL_PROJECT_MESSAGE, null);
                    }
                } else {
                    command.setNewModelDestination(null, fileNameText.getText());
                    result = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, FOLDER_MESSAGE, null);
                }
            } else {
                result = setContainer(null);
            }

            // the status message can get overly long. this block of code is meant to shorten the message by replacing
            // the validator message with a shorter one. (Defect 12885)
            if (result.getSeverity() == IStatus.ERROR) {
                StringNameValidator validator = new StringNameValidator(null);
                String msg = validator.checkNameCharacters(fileNameText.getText());

                if ((msg != null) && msg.equals(result.getMessage())) {
                    result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, Util.getString("SaveAsDialog.invalidCharacters")); //$NON-NLS-1$
                }
            }

            return result;
        }
    }

    /**
     * @see org.eclipse.ui.dialogs.SelectionDialog#createMessageArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Label createMessageArea( Composite composite ) {
        return super.createMessageArea(composite);
    }

}
