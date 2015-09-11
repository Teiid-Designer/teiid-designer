/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;


//import org.eclipse.core.runtime.IStatus;
import java.util.ResourceBundle;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelObjectEditorPanel;


/**
 * The <code>FindAction</code> class is the action that handles the global Find.
 * @since 8.0
 */
public class FindAction extends AbstractAction 
                implements IPartListener,
                           FocusListener
{

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private FindReplaceAction actEclipseFindReplaceAction;
    private boolean bHasFocus;
    private ModelObjectEditorPanel moepModelEditorPanel = null;

    private FocusEvent feMostRecentFocusGainEvent;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public FindAction() {
        
        super(UiPlugin.getDefault());
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.FIND));                
    }
        
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        setEnableState( thePart );
    }
    
    @Override
    protected void doRun() {
        FindReplaceAction actFindReplace = getFindReplaceAction();
        actFindReplace.update();
        actFindReplace.run();
    }


	private void setEnableState(IWorkbenchPart part) {

		if (part != null && part instanceof ModelEditor) {
			ModelObjectEditorPanel panel = ((ModelEditor) part).getEditorContainer();
			if (panel != null) {

				IFindReplaceTarget target = (IFindReplaceTarget) part.getAdapter(IFindReplaceTarget.class);

				if (target != null) {
					setEnabled(hasFocus() /* target.canPerformFind() */);
				} else {
					setEnabled(false);
				}
			}
		} else {
			setEnabled(false);
		}
	}

    private void setEnableState() {
        setEnableState( getCurrentWorkbenchPart() );
    }

    private FindReplaceAction getFindReplaceAction() {
        ResourceBundle rb = ResourceBundle.getBundle( UiConstants.PLUGIN_ID + ".i18n" ); //$NON-NLS-1$
        
        IWorkbenchPart workbenchPart = getCurrentWorkbenchPart();
        
        actEclipseFindReplaceAction 
            = new FindReplaceAction(rb, "ConstantEditor.title", workbenchPart); //$NON-NLS-1$

        actEclipseFindReplaceAction
            .setHelpContextId(IAbstractTextEditorHelpContextIds.FIND_ACTION);
        actEclipseFindReplaceAction
            .setActionDefinitionId(IWorkbenchActionDefinitionIds.FIND_REPLACE);
        
        return actEclipseFindReplaceAction;
    }

    private IWorkbenchPart getCurrentWorkbenchPart() {
    
        IWorkbenchPage activePage = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage != null) {
            IWorkbenchPart workbenchPart = activePage.getActiveEditor();
            return workbenchPart;
        } // endif
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partActivated(IWorkbenchPart part) {
        
        if ( part instanceof ModelEditor ) {
            moepModelEditorPanel = ((ModelEditor)part).getEditorContainer();
            
            if ( moepModelEditorPanel != null ) {            
                moepModelEditorPanel.addFocusListener( this );
                setEnableState();
            } else {
            	setEnabled(false);         
            }
        }
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partBroughtToTop(IWorkbenchPart part) {
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partClosed(IWorkbenchPart part) {
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partDeactivated(IWorkbenchPart part) {

        if ( part instanceof ModelEditor && moepModelEditorPanel == null) {
            moepModelEditorPanel = ((ModelEditor)part).getEditorContainer();
            
            if ( moepModelEditorPanel != null ) {                          
                moepModelEditorPanel.removeFocusListener( this );
            } 
        }
        moepModelEditorPanel = null;
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
	public void partOpened(IWorkbenchPart part) {
        setEnableState();
    }
    
    public boolean hasFocus() {
        return bHasFocus;        
    }
    
    @Override
	public void focusLost( FocusEvent fe ) {
        
        // if this 'focusLost' is related to our current 'focusGain' widget,
        //  then we are now in a no-focus state:
        if( (feMostRecentFocusGainEvent) != null && (fe.widget == feMostRecentFocusGainEvent.widget) ) {
            bHasFocus = false;               
        }
        
        // reset enable state
        setEnableState();      
    }
    
    
    @Override
	public void focusGained( FocusEvent fe ) {
        
        bHasFocus = true;   
                
        feMostRecentFocusGainEvent = fe;
        
        // reset enable state
        setEnableState();     
    } 
}
