/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors.sqleditor.actions;

//import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorPanel;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
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
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.ui.actions.AbstractAction#dispose()
     */
    @Override
    public void dispose() {
        getFontManager().removeFontChangeListener(this);
        super.dispose();
    }

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
        setEnableState();
    }
}
