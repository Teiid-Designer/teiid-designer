/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.actions;

//import org.eclipse.core.runtime.IStatus;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.text.IFontChangeListener;
import com.metamatrix.ui.text.TextFontManager;

/**
 * The <code>CopyAction</code> class is the action that handles the global copy.
 * @since 4.0
 */
public class UpFont extends AbstractAction implements IFontChangeListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private SqlEditorPanel panel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public UpFont( SqlEditorPanel sqlPanel ) {
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.UP_FONT));
        this.panel = sqlPanel;
        
        getFontManager().addFontChangeListener( this );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    

    private TextFontManager getFontManager() {
        return panel.getFontManager();
    }

    
    @Override
    protected void doRun() {
        getFontManager().increase();
        setEnableState();
    }
    
    public void setEnableState() {
        setEnabled( getFontManager().canIncrease() );        
    }
        
    public void fontChanged() {        
//        System.out.println("[UpFont.fontChanged] new size: " + tfmFontManager.getSize() ); //$NON-NLS-1$        
                
        setEnableState();
    }
}
