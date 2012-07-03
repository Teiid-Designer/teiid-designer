/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.dialogs;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.runtime.ui.actions.GenerateRestWarAction;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.SingleProjectFilter;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.DesignerProperties;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.Label;

public class GenerateRestWarDialog extends TitleAreaDialog implements IChangeListener {

	private IResource selectedVdb;

	private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

	private Button browseVdbsButton;
	private Text selectedVdbText;

	DesignerProperties designerProperties;

	/**
	 * @param parentShell 
	 * @since 5.5.3
	 */
	public GenerateRestWarDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @param parentShell 
	 * @param properties 
	 * @since 5.5.3
	 */
	public GenerateRestWarDialog(Shell parentShell, Properties properties) {
		this(parentShell);
		this.designerProperties = (DesignerProperties) properties;
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
		shell.setText(Messages.GenerateRestWarDialog_title);
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
	@SuppressWarnings("unused")
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(pnlOuter, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle(Messages.GenerateRestWarDialog_subTitle);
		setMessage(Messages.GenerateRestWarDialog_initialMessage);

		VDB_WIDGETS: {
			Label label = WidgetFactory.createLabel(panel, Messages.GenerateRestWarDialog_vdbName);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

			// textfield for named type
			this.selectedVdbText = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL);
			this.selectedVdbText.setToolTipText(Messages.GenerateRestWarDialog_vdbName);
			this.selectedVdbText.setEditable(false);
			this.selectedVdbText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedVdbText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

			this.browseVdbsButton = WidgetFactory.createButton(panel, Messages.GenerateRestWarDialog_browseExisting);
			this.browseVdbsButton.setToolTipText(Messages.GenerateRestWarDialog_browseTooltip);
			this.browseVdbsButton.setEnabled(true);
			this.browseVdbsButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.browseVdbsButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceForVdbPressed();
				}
			});
		}

		return panel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@SuppressWarnings("javadoc")
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if (this.designerProperties != null) {
			// Check for existing project??
			this.selectedVdb = this.designerProperties.getVDB();
			if (this.selectedVdb != null) {
				this.selectedVdbText.setText(this.selectedVdb.getName());
			}

			updateState();
		}
		return control;
	}

	/**
	 * @return the VDB
	 */
	public IResource getVdb() {
		return this.selectedVdb;
	}

	/*
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 * @since 5.5.3
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		updateState();
	}

	private void updateState() {
		
		if( this.selectedVdb == null ) {
			getButton(OK).setEnabled(false);
			setErrorMessage(Messages.GenerateRestWarDialog_noVdbError);
			return;
		}
		
		if( !GenerateRestWarAction.isRestWarVdb((IFile)this.selectedVdb) ) {
			getButton(OK).setEnabled(false);
			setErrorMessage(Messages.GenerateRestWarDialog_noRestProceduresInVdbError);
			return;
		}
		

		getButton(OK).setEnabled(true);
		setErrorMessage(null);
		setMessage(Messages.GenerateRestWarDialog_clickOkMessage);
	}

	void handleBrowseWorkspaceForVdbPressed() {
		ModelWorkspaceDialog sdDialog = createVdbSelector();

		// add filters
		sdDialog.addFilter(new ClosedProjectFilter());
		sdDialog.addFilter(new SingleProjectFilter(
				this.designerProperties));

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			Object[] selections = sdDialog.getResult();
			// should be single selection
			this.selectedVdb = (IResource) selections[0];
			this.designerProperties.setVdbName(this.selectedVdb.getName());
			this.selectedVdbText.setText(this.selectedVdb.getName());
			
            try {
				// open editor
				IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
				IDE.openEditor(page, (IFile)this.selectedVdb);
			} catch (PartInitException ex) {
				// Do nothing?
			}

			updateState();
		}
	}


	/**
	 * @return the dialog
	 */
	public ModelWorkspaceDialog createVdbSelector() {

		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());

		String title = Messages.GenerateRestWarDialog_selectVdbTitle;
		String message = Messages.GenerateRestWarDialog_selectVdbMessage;
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ResourcesPlugin.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				boolean ok = false;
				if (selection != null && selection.length == 1
						&& selection[0] instanceof IResource) {
					IResource res = (IResource) selection[0];
					ok = ModelUtil.isVdbArchiveFile(res);

				}
				if (!ok) {
					String msg = Messages.GenerateRestWarDialog_selectedObjectNotVdbMessage;
					return new StatusInfo(AdvisorUiConstants.PLUGIN_ID, IStatus.ERROR, msg);
				}
				return new StatusInfo(AdvisorUiConstants.PLUGIN_ID);
			}
		});

		return result;
	}
}
