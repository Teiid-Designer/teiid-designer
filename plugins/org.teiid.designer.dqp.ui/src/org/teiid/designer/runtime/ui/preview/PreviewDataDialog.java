/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.preview;

import java.util.Properties;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.SingleProjectFilter;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.Label;

/**
 * Simple page dialog containing widgets necessary to allow user to select a
 * table or procedure from the workspace.
 * 
 * Upon "FINISH" or "EXECUTE", the "PreviewTableDataContext" action
 */
public class PreviewDataDialog extends TitleAreaDialog implements
		DqpUiConstants, IChangeListener {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(PreviewDataDialog.class);

	private EObject previewableEObject;

	private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

	private Button browseButton;
	private Text selectedEobjectText;

	Properties designerProperties;
	/**
	 * @since 5.5.3
	 */
	public PreviewDataDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	/**
	 * @since 5.5.3
	 */
	public PreviewDataDialog(Shell parentShell, Properties properties) {
		this(parentShell);
		this.designerProperties = properties;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 * @since 5.5.3
	 */
	@Override
	public boolean close() {

		if (this.labelProvider != null) {
			this.labelProvider.dispose();
		}

		return super.close();
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * @since 5.5.3
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(OK).setEnabled(false);

		// set the first selection so that initial validation state is set
		// (doing it here since the selection handler uses OK
		// button)

		return buttonBar;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
        Composite pnlOuter = (Composite)super.createDialogArea(parent);
        Composite panel = new Composite(pnlOuter, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        panel.setLayout(gridLayout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
       

		// set title
		setTitle(UTIL.getString(PREFIX + "subTitle")); //$NON-NLS-1$
		setMessage(UTIL.getString(PREFIX + "initialMessage")); //$NON-NLS-1$
		
		Label label = WidgetFactory.createLabel(panel, UTIL.getString(PREFIX + "tableOrProcedure")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		
        // textfield for named type
        this.selectedEobjectText = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL/*GridData.HORIZONTAL_ALIGN_FILL*/);
        this.selectedEobjectText.setToolTipText(UTIL.getString(PREFIX + "text.typeName.tip")); //$NON-NLS-1$
        this.selectedEobjectText.setEditable(false);
        //this.selectedEobjectText.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));

        // browse type button
        this.browseButton = WidgetFactory.createButton(panel, "..."); //UTIL.getString(PREFIX + "button.browseType")); //$NON-NLS-1$
        this.browseButton.setToolTipText(UTIL.getString(PREFIX + "button.browseType.tip")); //$NON-NLS-1$
        this.browseButton.setEnabled(true);
        this.browseButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
        this.browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
            	handleBrowseWorkspaceForObjectPressed();
            }
        });

		return panel;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if( this.designerProperties != null ) {
            // Get the Preview Target Object (null if not found)
            EObject targetPreviewEObj = DesignerPropertiesUtil.getPreviewTargetObject(this.designerProperties);
            if (targetPreviewEObj != null) {
                this.previewableEObject = targetPreviewEObj;
                this.selectedEobjectText.setText(ModelerCore.getModelEditor().getName(previewableEObject));
            }
            updateState();
		}
		return control;
	}
	
	public EObject getPreviewableEObject() {
		return this.previewableEObject;
	}

	/**
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 * @since 5.5.3
	 */
	public void stateChanged(IChangeNotifier theSource) {
		updateState();
	}

	private void updateState() {
		IStatus status = Status.OK_STATUS;

		if (status.getSeverity() == IStatus.ERROR) {
			getButton(OK).setEnabled(false);
			setErrorMessage(status.getMessage());
		} else {
			getButton(OK).setEnabled(true);
			setErrorMessage(null);
			setMessage(UTIL.getString(PREFIX + "okMsg")); //$NON-NLS-1$
		}
	}
	
	private void handleBrowseWorkspaceForObjectPressed() {
		ModelWorkspaceDialog sdDialog = createTableOrProcedureSelector();

		// add filters
		((ModelWorkspaceDialog)sdDialog).addFilter(new ClosedProjectFilter());
		((ModelWorkspaceDialog) sdDialog).addFilter(new SingleProjectFilter(this.designerProperties));
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            previewableEObject = (EObject)selections[0];
            this.selectedEobjectText.setText(ModelerCore.getModelEditor().getName(previewableEObject));
            
            updateState();
        }

	}
	
	public ModelWorkspaceDialog createTableOrProcedureSelector() {
		
		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(), null,
				new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());

		String title = UTIL.getString(PREFIX + "selectionDialog.title"); //$NON-NLS-1$
		String message = UTIL.getString(PREFIX + "selectionDialog.message"); //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ResourcesPlugin.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length == 0
						|| selection[0] == null
						|| (!(selection[0] instanceof Table) && !(selection[0] instanceof Procedure)) ) {
					String msg = UTIL.getString(PREFIX + "selectionDialog.invalidSelection"); //$NON-NLS-1$
					return new StatusInfo(DqpUiConstants.PLUGIN_ID, IStatus.ERROR,msg);
				}
				return new StatusInfo(DqpUiConstants.PLUGIN_ID);
			}
		});


		return result;
	}
}
