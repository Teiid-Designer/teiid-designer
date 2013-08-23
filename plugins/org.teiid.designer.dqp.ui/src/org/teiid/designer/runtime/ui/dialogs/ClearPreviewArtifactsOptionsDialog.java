/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;

/**
 *
 */
public class ClearPreviewArtifactsOptionsDialog  extends TitleAreaDialog {

    /**
     * Prefix for language NLS properties
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ClearPreviewArtifactsOptionsDialog.class);
    
    private String getString( final String stringId ) {
        return DqpUiConstants.UTIL.getString(PREFIX + stringId);
    }
    
	boolean clearVdbs;
	boolean clearDataSources;
	
	Button includeVdbs;
	Button includeDataSources;

	/**
	 * @param parentShell
	 */
	public ClearPreviewArtifactsOptionsDialog(Shell parentShell) {
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
		shell.setText(getString("title")); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
        setTitle(getString("subTitle")); //$NON-NLS-1$
        setMessage(getString("initialMessage")); //$NON-NLS-1$
        
        this.clearDataSources = true;
        this.clearVdbs = true;
        Composite pnlOuter = (Composite)super.createDialogArea(parent);
        Composite panel = new Composite(pnlOuter, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(panel);
        
        includeVdbs = new Button(panel, SWT.CHECK);
        includeVdbs.setText(getString("clearVdbs")); //$NON-NLS-1$
        includeVdbs.setSelection(true);
        includeVdbs.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearVdbs = includeVdbs.getSelection();
				validate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
        
        includeDataSources = new Button(panel, SWT.CHECK);
        includeDataSources.setText(getString("clearDataSources")); //$NON-NLS-1$
        includeDataSources.setSelection(true);
        includeDataSources.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearDataSources = includeDataSources.getSelection();
				validate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
        
        return panel;
	}

	/**
	 * @return the clearVdbs
	 */
	public boolean doClearVdbs() {
		return this.clearVdbs;
	}

	/**
	 * @return the clearDataSources
	 */
	public boolean doClearDataSources() {
		return this.clearDataSources;
	}
	
	private void validate() {
		if( !this.clearDataSources && !clearVdbs ) {
			// Show error message and disable OK
			setErrorMessage(getString("noArtifactsSelected")); //$NON-NLS-1$
			getButton(OK).setEnabled(false);
			return;
		}
		
		setErrorMessage(null);
		setMessage(getString("initialMessage")); //$NON-NLS-1$
		getButton(OK).setEnabled(true);
	}
	
}
