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
public class DownFont extends AbstractAction implements IFontChangeListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS  
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private SqlEditorPanel panel;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public DownFont(SqlEditorPanel sqlPanel ) {
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.DOWN_FONT));
        this.panel = sqlPanel;
        // NO! listening must be dynamic, like InternalEditorEVents
        //   it must change each time the current editor changes
        // WAIT! that is not right either.  We will only need to listen to 
        //   the wrapper, and he will listen to the others.  So this line of code is correct.
        // EXCEPT! that we must change this to listen to the wrapper, rather than the
        ///  FontMgr directly...
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
        getFontManager().decrease();
        setEnableState();
    }

    public void setEnableState() {
        setEnabled( getFontManager().canDecrease() );        
    }
    
    public void fontChanged() {
        setEnableState();
    }
}
