/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.dynamic.ui.editor.dialogs;

import static org.teiid.designer.vdb.ui.VdbUiConstants.Util;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;

/**
 *
 */
public class AddLanguagePropertyDialog extends MessageDialog {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(AddLanguagePropertyDialog.class);

    private Button btnOk;
    private final Set<String> existingNames;
    private String name;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingPropertyNames the existing property names (can be <code>null</code>)
     */
    public AddLanguagePropertyDialog( Shell parentShell,
                              Set<String> existingPropertyNames ) {
        super(parentShell, Util.getString(PREFIX + "title"), null, //$NON-NLS-1$
                Util.getString(PREFIX + "message"), MessageDialog.INFORMATION, //$NON-NLS-1$
                new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

        if( existingPropertyNames == null ) {
        	this.existingNames = new HashSet<String>(0);
        } else {
        	this.existingNames = existingPropertyNames;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        Button btn = super.createButton(parent, id, label, defaultButton);

        if (id == IDialogConstants.OK_ID) {
            // disable OK button initially
            this.btnOk = btn;
            btn.setEnabled(false);
        }

        return btn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblName = new Label(pnl, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblName.setText(Util.getString(PREFIX + "language")); //$NON-NLS-1$

        Text txtName = new Text(pnl, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtName.setToolTipText(Util.getString(PREFIX + "languageText.tooltip")); //$NON-NLS-1$
        txtName.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleNameChanged(((Text)e.widget).getText());
            }
        });

        return pnl;
    }

    /**
     * @return the new language (never <code>null</code>)
     * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
     */
    public String getLanguage() {
        CoreArgCheck.isEqual(getReturnCode(), Window.OK);
        return name;
    }
    

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    void handleNameChanged( String newName ) {
        this.name = newName;
        updateState();
    }

    private void updateState() {
        // check to see if new name is valid
        String msg = validateName();

        // update UI controls
        if (StringUtilities.isEmpty(msg)) {
            if (!this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(true);
            }

            if (this.imageLabel.getImage() != null) {
                this.imageLabel.setImage(null);
            }

            this.imageLabel.setImage(getInfoImage());
            msg = Util.getString(PREFIX + "message"); //$NON-NLS-1$
        } else {
            // value is not valid
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            this.imageLabel.setImage(getErrorImage());
        }

        this.messageLabel.setText(msg);
        this.messageLabel.pack();
    }

    private String validateName() {
        String errorMsg = validateName(this.name);

        if (errorMsg == null) {
            // make sure property ID doesn't already exist
            for (String existingName : this.existingNames) {
                if (existingName.equals(this.name)) {
                    errorMsg = Util.getString(PREFIX + "languageAlreadyExists", this.name); //$NON-NLS-1$
                    break;
                }
            }
        }

        return errorMsg;
    }
    /**
     * @param proposedName the proposed property name
     * @return an error message or <code>null</code> if name is valid
     */
    public String validateName( String proposedName ) {
        // must have a name
        if (StringUtilities.isEmpty(proposedName)) {
            return Util.getString(PREFIX + "emptyLanguageName"); //$NON-NLS-1$
        }

        // make sure only letters
        for (char c : proposedName.toCharArray()) {
            if (!Character.isLetter(c)) {
                return Util.getString(PREFIX + "invalidLanguageName", proposedName); //$NON-NLS-1$
            }
        }

        // valid name
        return null;
    }

}