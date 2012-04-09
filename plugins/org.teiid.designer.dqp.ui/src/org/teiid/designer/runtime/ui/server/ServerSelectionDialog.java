/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.runtime.Server;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.TeiidViewTreeProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.Label;

public class ServerSelectionDialog extends TitleAreaDialog implements
		DqpUiConstants, IChangeListener {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(ServerSelectionDialog.class);

	Server selectedServer;
	
	Combo serversCombo;

	/**
	 * @since 5.5.3
	 */
	public ServerSelectionDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
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
	@SuppressWarnings("unused")
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(pnlOuter, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
		setMessage(UTIL.getString(PREFIX + "initialMessage")); //$NON-NLS-1$

//		Group serversGroup = WidgetFactory.createGroup(panel, UTIL.getString(PREFIX + "teiidServers"), GridData.FILL_BOTH, 2, 2); //$NON-NLS-1$
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = 2;
//		serversGroup.setLayoutData(gd);

		ACTION_COMBO: {
			serversCombo = new Combo(panel, SWT.NONE | SWT.READ_ONLY);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			serversCombo.setLayoutData(gd);

			serversCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent ev) {
					selectedServer = null;
					String serverName = serversCombo.getItem(serversCombo.getSelectionIndex());
					
					Collection<Server> servers = DqpPlugin.getInstance().getServerManager().getServers();
					for( Server server : servers ) {
						if( server.getCustomLabel().equalsIgnoreCase(serverName) ) {
							selectedServer = server;
							break;
						}
					}
					
					updateState();
				}
			});
			Collection<Server> servers = DqpPlugin.getInstance().getServerManager().getServers();
			List<String> nameList = new ArrayList<String>();
			for( Server server : servers ) {
				nameList.add(server.getCustomLabel());
			}
			WidgetUtil.setComboItems(serversCombo, nameList, null, true);
		}

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

		if (this.selectedServer == null ) {
			getButton(OK).setEnabled(false);
			if( this.serversCombo.getItemCount() == 0 ) {
				setErrorMessage(UTIL.getString(PREFIX + "noServersExistMessage")); //$NON-NLS-1$
			} else {
				setErrorMessage(UTIL.getString(PREFIX + "noServerSelectedMessage")); //$NON-NLS-1$
			}

			
		} else {
			getButton(OK).setEnabled(true);
			setErrorMessage(null);
			setMessage(UTIL.getString(PREFIX + "okMsg")); //$NON-NLS-1$
		}
	}
}
