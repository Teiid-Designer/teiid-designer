/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.common.util.WidgetUtil;

/**
 * @since 8.0
 */
public class ServerSelectionDialog extends TitleAreaDialog implements
		DqpUiConstants, IChangeListener {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(ServerSelectionDialog.class);
	private static final String NO_DEFAULT = "No Default"; //$NON-NLS-1$

	private ITeiidServer selectedServer;
	
	private Combo serversCombo;
	private boolean includeNoDefaultOption = false;
	
	private Map<String, ITeiidServer> serverMap = new HashMap<String, ITeiidServer>();

	/**
	 * Constructor
	 * @param parentShell the parent shell
	 * @param includeNoDefaultOption 'true' includes a 'No Default' option in addition to available servers
	 */
	public ServerSelectionDialog(Shell parentShell, boolean includeNoDefaultOption) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.includeNoDefaultOption = includeNoDefaultOption;
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
		getButton(OK).setEnabled(true);

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
		gridLayout.marginTop = 25;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
		setMessage(UTIL.getString(PREFIX + "initialMessage")); //$NON-NLS-1$

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
					selectedServer = serverMap.get(serverName);
					
					updateState();
				}
			});
			Collection<ITeiidServer> teiidServers = DqpPlugin.getInstance().getServerManager().getServers();
			ITeiidServer currentDefault = DqpPlugin.getInstance().getServerManager().getDefaultServer();
			String currentServerStr = null;
			if(currentDefault==null) {
				currentServerStr = NO_DEFAULT;
			} else {
				currentServerStr = currentDefault.toString();
			}
			
			// Add NO_DEFAULT option if desired
			if(this.includeNoDefaultOption) serverMap.put(NO_DEFAULT, null);
			
			// Add remaining servers from DQP Plugin
			for( ITeiidServer teiidServer : teiidServers ) {
                serverMap.put(teiidServer.toString(), teiidServer);
			}
			
			WidgetUtil.setComboItems(serversCombo, serverMap.keySet(), null, true);
			// Set initial selection
			boolean setInitial = false;
			for(int i=0; i<serversCombo.getItemCount(); i++) {
				String serverName = serversCombo.getItem(i);
				if(serverName!=null && serverName.equalsIgnoreCase(currentServerStr)) {
					serversCombo.select(i);
					selectedServer = serverMap.get(serverName);
					setInitial = true;
					break;
				}
			}
			// If initial selection was not found, set to first item.
			if(!setInitial && serversCombo.getItemCount()>0) serversCombo.select(0);
		}

		return panel;
	}

	/**
	 * Get the selected server
	 * @return the selected server
	 */
	public ITeiidServer getServer() {
		return this.selectedServer;
	}

	/**
	 * @see org.teiid.core.designer.event.IChangeListener#stateChanged(org.teiid.core.designer.event.IChangeNotifier)
	 * @since 5.5.3
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		updateState();
	}

	private void updateState() {
		// selectedServer == null is a valid choice (No Default)
		getButton(OK).setEnabled(true);
		
		if(!this.includeNoDefaultOption && this.serversCombo.getItemCount()==0) {
			getButton(OK).setEnabled(false);
			setErrorMessage(UTIL.getString(PREFIX + "noServersExistMessage")); //$NON-NLS-1$
			return;
		}
		
		if(this.includeNoDefaultOption && this.serversCombo.getItemCount()==1) {
			setErrorMessage(null);
			setMessage(UTIL.getString(PREFIX + "noServersExistMessage")); //$NON-NLS-1$
		}
	}
}
