/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.runtime.Server;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.TeiidViewTreeProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Label;

public class ServerSelectionDialog extends TitleAreaDialog implements
		DqpUiConstants, IChangeListener {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(ServerSelectionDialog.class);

	Server selectedServer;
	
    TreeViewer viewer;
    TeiidViewTreeProvider treeProvider;

	private Button browseButton;
	private Text selectedServerText;

	/**
	 * @since 5.5.3
	 */
	public ServerSelectionDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 * @since 5.5.3
	 */
	@Override
	public boolean close() {

		if (this.treeProvider != null) {
			this.treeProvider.dispose();
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

		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(pnlOuter, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle("Teiid Server Selection"); //UTIL.getString(PREFIX + "subTitle")); //$NON-NLS-1$
		setMessage("Browse to select an Teiid Server to edit"); //UTIL.getString(PREFIX + "initialMessage")); //$NON-NLS-1$

		Label label = WidgetFactory.createLabel(panel, "Server"); //UTIL.getString(PREFIX + "tableOrProcedure")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		// textfield for named type
		this.selectedServerText = WidgetFactory.createTextField(panel,GridData.FILL_HORIZONTAL/* GridData.HORIZONTAL_ALIGN_FILL */);
		this.selectedServerText.setEditable(false);

		// browse type button
		this.browseButton = WidgetFactory.createButton(panel, "..."); //UTIL.getString(PREFIX + "button.browseType")); //$NON-NLS-1$
//		this.browseButton.setToolTipText(UTIL.getString(PREFIX	+ "button.browseType.tip")); //$NON-NLS-1$
		this.browseButton.setEnabled(true);
		this.browseButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE,
				false, false));
		this.browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				handleBrowseForServerPressed();
			}
		});

		return panel;
	}

	public Server getServer() {
		return this.selectedServer;
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

	private void handleBrowseForServerPressed() {
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Unimplemented Dialog",  //$NON-NLS-1$
				"Browse for Server Selection not yet ready..."); //$NON-NLS-1$
		
//		ModelWorkspaceDialog sdDialog = createTableOrProcedureSelector();
//
//		// add filters
//		((ModelWorkspaceDialog) sdDialog).addFilter(new ClosedProjectFilter());
//
//		sdDialog.open();
//
//		if (sdDialog.getReturnCode() == Window.OK) {
//			Object[] selections = sdDialog.getResult();
//			// should be single selection
//			selectedServer = (Server) selections[0];
//			this.selectedServerText.setText(selectedServer.getCustomLabel());
//
//			updateState();
//		}

	}
}
