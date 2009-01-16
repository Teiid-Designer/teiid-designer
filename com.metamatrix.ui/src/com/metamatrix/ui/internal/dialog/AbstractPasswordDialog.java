/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
        WidgetFactory.createLabel(dlgPanel, PASSWORD_LABEL);
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
