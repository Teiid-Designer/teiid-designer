/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialogModel;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;

/**
 * @since 8.0
 */
public abstract class RelationalEditorPanel {
    protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

    protected final EditRelationalObjectDialogModel dialogModel;

    protected IStatus currentStatus;

    protected IDialogStatusListener statusListener;

    private boolean canFinish;

    private Text modelNameText, nameText, nameInSourceText;
    private StyledTextEditor descriptionTextEditor;

    private boolean synchronizing = false;

    private Shell shell;

	/**
	 * @param parent the parent panel
	 * @param dialogModel model containing reference object
	 * @param statusListener the dialog status listener
	 */
	public RelationalEditorPanel(Composite parent, EditRelationalObjectDialogModel dialogModel, IDialogStatusListener statusListener) {
		super();
		this.dialogModel = dialogModel;
		this.statusListener = statusListener;
		
		createPanel(parent);
		this.shell = parent.getShell();
		
		synchronizeUI();

		this.nameText.setFocus();
	}

	/**
	 * @return dialog's shell
	 */
	protected Shell getShell() {
	    return shell;
	}

    protected Composite createNameGroup(Composite parent) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 2, 2);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false);
        GridDataFactory.fillDefaults().grab(true, false).minSize(SWT.DEFAULT, 120).applyTo(thePanel);

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.modelFileLabel);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(label);

        this.modelNameText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.modelNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        this.modelNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.modelNameText);
        if (getModelFile() != null) {
            modelNameText.setText(getModelFile().getName());
        }

        label = new Label(thePanel, SWT.NONE);
        label.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.NAME));

        this.nameText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.nameText);
        this.nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
                String value = nameText.getText();
                if (value == null) {
                    value = EMPTY_STRING;
                }

                getRelationalReference().setName(value);
                handleInfoChanged();
            }
        });

        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.nameInSourceLabel);

        this.nameInSourceText = new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameInSourceText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.nameInSourceText);
        this.nameInSourceText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
                String value = nameInSourceText.getText();
                if (value == null) {
                    value = EMPTY_STRING;
                }

                getRelationalReference().setNameInSource(value);
                handleInfoChanged();
            }
        });

        return thePanel;
    }

    protected void handleInfoChanged() {
        if( synchronizing ) {
            return;
        }
        validate();

        synchronizeUI();
    }

    protected TabFolder createTabFolder(Composite parent) {
        TabFolder tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(tabFolder);
        return tabFolder;
    }

    protected Composite createDescriptionPanel(Composite parent) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        final Group descGroup = WidgetFactory.createGroup(thePanel, UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION), GridData.FILL_BOTH, 3);
        descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 40).minSize(SWT.DEFAULT, 30).applyTo(descriptionTextEditor.getTextWidget());
        descriptionTextEditor.setText(""); //$NON-NLS-1$
        descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getRelationalReference().setDescription(descriptionTextEditor.getText());
            }
        });

        return thePanel;
    }

    protected void createDescriptionTab(TabFolder folderParent) {
        Composite thePanel = createDescriptionPanel(folderParent);

        TabItem descriptionTab = new TabItem(folderParent, SWT.NONE);
        descriptionTab.setControl(thePanel);
        descriptionTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DESCRIPTION));
        descriptionTab.setImage(RelationalUiUtil.getDescriptionImage(Status.OK_STATUS));
    }

	protected abstract void createPanel(Composite parent);

	protected boolean isSynchronizing() {
	    return synchronizing;
	}

	/**
	 * Override this method to implement updates to specialized components
	 * only available in sub-classes.
	 */
	protected abstract void synchronizeExtendedUI();

	protected final void synchronizeUI() {
	    if( synchronizing ) {
            return;
        }

	    synchronizing = true;

	    /*
	     * Name components
	     */
        if( getRelationalReference().getName() != null ) {
            if( WidgetUtil.widgetValueChanged(this.nameText, getRelationalReference().getName()) ) {
                this.nameText.setText(getRelationalReference().getName());
            }
        } else {
            if( WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING) ) {
                this.nameText.setText(EMPTY_STRING);
            }
        }

        if( getRelationalReference().getNameInSource() != null ) {
            if( WidgetUtil.widgetValueChanged(this.nameInSourceText, getRelationalReference().getNameInSource()) ) {
                this.nameInSourceText.setText(getRelationalReference().getNameInSource());
            }
        } else {
            if( WidgetUtil.widgetValueChanged(this.nameInSourceText, EMPTY_STRING) ) {
                this.nameInSourceText.setText(EMPTY_STRING);
            }
        }

        /*
         * Description text
         */
        if (descriptionTextEditor != null) {
            if( getRelationalReference().getDescription() != null) {
                if( !StringUtilities.equals(descriptionTextEditor.getText(), getRelationalReference().getDescription()) ) {
                    descriptionTextEditor.setText(getRelationalReference().getDescription());
                }
            } else {
                this.descriptionTextEditor.setText(EMPTY_STRING);
            }
        }

        synchronizeExtendedUI();

        synchronizing = false;
	}

	protected void validate() {
		
	}
	
	protected void setStatus(IStatus status) {
		currentStatus = status;
		
		statusListener.notifyStatusChanged(currentStatus);
	}
	
	/**
	 * @return the relational object reference
	 */
	protected RelationalReference getRelationalReference() {
		return dialogModel.getRelationalObject();
	}
	
	/**
	 * @return the model file
	 */
	protected IFile getModelFile() {
		return dialogModel.getModelFile();
	}
	
	/**
	 * @param value if dialog can finish or not
	 */
	protected final void setCanFinish(boolean value) {
		this.canFinish = value;
	}
	

	/**
	 * @return if dialog can finish
	 */
	public boolean canFinish() {
		return this.canFinish;
	}
}