/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.dialog;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**<p>
 * </p>
 * @since 4.0
 */
public abstract class AbstractPasswordDialog extends Dialog implements InternalUiConstants,
                                                                       InternalUiConstants.Widgets {
    //============================================================================================================================
    // Constants
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(AbstractPasswordDialog.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    
    private static final int COLUMN_COUNT = 2;
    
    private String passwordLabel = PASSWORD_LABEL;

    //============================================================================================================================
    // Static Utility Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    //============================================================================================================================
    // Variables
    
    private Text pwdFld;

    //============================================================================================================================
    // Constructors
        
    /**<p>
     * </p>
     * @param parent
     * @param title
     * @since 4.0
     */
    public AbstractPasswordDialog(final Shell shell) {
        super(shell, TITLE);
    }
    
    /**<p>
     * </p>
     * @param parent
     * @param title
     * @since 4.0
     */
    public AbstractPasswordDialog(final Shell shell, String overrideTitle, String passwordLabel) {
        super(shell, overrideTitle);
        if( passwordLabel != null ) {
        	this.passwordLabel = passwordLabel;
        }
    }
    
    //============================================================================================================================
    // Overridden Methods

    /**<p>
     * </p>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite dlgPanel = (Composite)super.createDialogArea(parent);
        ((GridLayout)dlgPanel.getLayout()).numColumns = COLUMN_COUNT;
        WidgetFactory.createLabel(dlgPanel, passwordLabel);
        this.pwdFld = WidgetFactory.createPasswordField(dlgPanel);
        return dlgPanel;
    }
    
    /**<p>
     * </p>
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.0
     */
    @Override
    protected void okPressed() {
        if (isPasswordValid(this.pwdFld.getText())) {
            super.okPressed();
        }
    }
    
    //============================================================================================================================
    // Abstract Methods
    
    /**<p>
     * </p>
     * @since 4.0
     */
    protected abstract boolean isPasswordValid(String password);
}
