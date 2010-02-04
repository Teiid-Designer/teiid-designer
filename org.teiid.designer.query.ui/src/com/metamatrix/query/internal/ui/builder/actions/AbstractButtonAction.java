/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * The <code>AbstractButtonAction</code> class creates a {@link org.eclipse.swt.widgets.Button} and
 * associates it's selection with a <code>Runnable</code>. The button's text and tooltip are set
 * via the plugin's properties file.
 */
public abstract class AbstractButtonAction extends AbstractAction
                                           implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    /////////////////////////////////////////////////////////////////////////////////////////////// 

    private Button btn;
    
    private Runnable runnable;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
        
    public AbstractButtonAction(Composite theParent,
                                Runnable theRunnable) {
        super(UiPlugin.getDefault());
        
        runnable = theRunnable;

        btn = new Button(theParent, SWT.NONE);
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent theEvent) {
                run();
            }
        });

        if (theParent.getLayout() instanceof GridLayout) {
        	btn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
        
        // set button text
        String txt = getText();
        
        if (txt != null) {
            btn.setText(txt);
        }
        
        // set button tooltip text
        txt = getToolTipText();
        
        if (txt != null) {
            btn.setToolTipText(txt);
        }
    }
        
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
        
    public Button getButton() {
        return btn;
    }

    @Override
    public void doRun() {
        runnable.run();
    }
        
    @Override
    public void setEnabled(boolean theEnabledFlag) {
        super.setEnabled(theEnabledFlag);
        btn.setEnabled(theEnabledFlag);
    }
    
}
