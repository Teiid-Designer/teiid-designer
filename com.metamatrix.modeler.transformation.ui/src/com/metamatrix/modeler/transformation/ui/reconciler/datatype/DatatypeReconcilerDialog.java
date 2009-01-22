/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler.datatype;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.reconciler.BindingList;
import com.metamatrix.modeler.transformation.ui.reconciler.ColorManager;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * DatatypeReconcilerDialog
 */
public class DatatypeReconcilerDialog extends ExtendedTitleAreaDialog {
    //=============================================================
    // Instance variables
    //=============================================================
    private DatatypeReconcilerPanel panel;
    private BindingList bindingList;
    private boolean targetLocked;
    private ColorManager colorManager;
    private String dialogTitle;
        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * DatatypeReconcilerDialog constructor.
     * 
     * @param parent   parent of this dialog
     * @param bindingList the list of Bindings that have type conflicts
     * @param targetLocked target group locked state
     * @param colorManager the color manager
     * @param title    dialog display title
     */
    public DatatypeReconcilerDialog(Shell parent, BindingList bindingList, boolean targetLocked, 
                                     ColorManager colorManager, String title) {
        super(parent, UiPlugin.getDefault());
        this.dialogTitle = title;
        this.bindingList = bindingList;
        this.targetLocked = targetLocked;
        this.colorManager = colorManager;
    }
    
    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        GridLayout gl = new GridLayout();
        gl.marginWidth = 5;
        composite.setLayout(gl);
        panel = new DatatypeReconcilerPanel(composite,this,bindingList,targetLocked,colorManager);
        setDialogTitle(dialogTitle);
        setInitialSizeRelativeToScreen(50,70);
        return composite;
    }
    
    /**
     * Check whether there are any required mods to the SQL or targetGroup.
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasModifications( ) {
        return panel.hasModifications();
    }

    /**
     * Apply all pending mods to the bindings.
     * @return true if there are pending modifications, false if not.
     */
    public void applyBindingTypeModifications() {
        panel.applyBindingTypeModifications();
    }
    
    /**
     * Check whether there are any required mods to the SQL or targetGroup.
     * @return true if there are pending modifications, false if not.
     */
    public void clearBindingTypeModifications() {
        panel.clearBindingTypeModifications();
    }
    
    //============================================================================================================================
    // Property Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    public String getDialogTitle() {
        final Shell shell = getShell();
        return (shell == null ? this.dialogTitle : getShell().getText());
    }
    
    /**<p>
     * </p>
     * @since 4.0
     */
    public void setDialogTitle(final String title) {
        ArgCheck.isNotNull(title);
        final Shell shell = getShell();
        if (shell == null) {
            this.dialogTitle = title;
        } else {
            shell.setText(title);
        }
    }

    //
    // Overrides:
    //
    @Override
    protected String getDialogSettingsSectionName() {
        // rename the preference so that dialogs will be sized anew once:
        return super.getDialogSettingsSectionName()+"1"; //$NON-NLS-1$
    }
}//end ReconcilerDialog
