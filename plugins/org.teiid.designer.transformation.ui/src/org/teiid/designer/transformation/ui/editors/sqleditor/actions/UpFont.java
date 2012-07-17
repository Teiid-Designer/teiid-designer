/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.sqleditor.actions;

//import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorPanel;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.common.text.IFontChangeListener;
import org.teiid.designer.ui.common.text.TextFontManager;


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
     * @see org.teiid.designer.ui.common.actions.AbstractAction#dispose()
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
        
    @Override
	public void fontChanged() {        
        setEnableState();
    }
}
