/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * ReconcilerDialog
 */
public class ReconcilerDialog extends ExtendedTitleAreaDialog {
    //=============================================================
    // Instance variables
    //=============================================================
    private ReconcilerPanel panel;
    private EObject transformationObj;
    private boolean cancelled = false;
    private String dialogTitle;
    private int unionSegment = -1;
    private boolean isUnion = false;
    private List builderGroups = new ArrayList();
        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * ReconcilerDialog constructor.
     * 
     * @param parent   parent of this dialog
     * @param transObj the transformation EObject
     * @param title    dialog display title
     * @param unionSegment index of the segment to work on (in the case of a union), 
     *                      -1 if non-union or no segment
     */
    public ReconcilerDialog(Shell parent, EObject transObj, String title, boolean isUnion, int unionSegment, List builderGroups) {
        super(parent, UiPlugin.getDefault());
        this.dialogTitle = title;
        this.transformationObj = transObj;
        this.isUnion = isUnion;
        this.unionSegment = unionSegment;
        Rectangle bounds = parent.getDisplay().getClientArea();
        if (bounds.width < 1280) {
            setInitialSizeRelativeToScreen(75,75);
        } else {
            setInitialSizeRelativeToScreen(50,50);
        }
        this.builderGroups.addAll(builderGroups);
    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        panel = new ReconcilerPanel(composite,this,transformationObj, isUnion, this.unionSegment, builderGroups);
        setDialogTitle(dialogTitle);
        getShell().setMinimumSize(750, 500);
        return composite;
    }
    
    @Override
    protected void cancelPressed() {
        cancelled = true;
        super.cancelPressed();
    }
    
    @Override
    protected void okPressed() {
        panel.preDispose();
        super.okPressed();
    }
    /**
     * Check whether there are any required mods to the SQL or targetGroup.
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasValidModifications( ) {
        return panel.hasValidModifications();
    }

    /**
     * Apply any required mods.
     * @param uIndex index of the segment to work on (in the case of a union), 
     *                -1 if non-union or no segment
     * @param txnSource the transaction source
     */
    public void applyAllModifications(int uIndex, Object txnSource) {
       panel.applyAllModifications(uIndex,txnSource);
    }
    
    
    public void applyPreModifications(Object txnSource) {
        panel.applyPreModifications(txnSource);
    }
    
    public boolean hasPreModifications() {
        return panel.hasPreModifications();
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
        CoreArgCheck.isNotNull(title);
        final Shell shell = getShell();
        if (shell == null) {
            this.dialogTitle = title;
        } else {
            shell.setText(title);
        }
    }

    /**
     * Return boolean indicating whether or not "Cancel" was pressed
     * @return  true if "Cancel" was pressed
     */
    public boolean wasCancelled() {
        return cancelled;
    }
    
}//end ReconcilerDialog
