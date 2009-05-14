/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.workers;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ImportContainer;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;


/** 
 * @since 4.2
 */
public class CopyNameWorker extends ModelObjectWorker {
//    private static final String PROBLEM = "CopyNameWorker.problem"; //$NON-NLS-1$
    
    private static final char OLD_DELIM = '/';
    private static final char NEW_DELIM = '.';

    public static final int SHOW_JUST_NAME = 1;
    public static final int SHOW_FULLY_QUALIFIED_NAME = 2;

    // default name style to fully qualified
    private int iNameStyle = SHOW_FULLY_QUALIFIED_NAME;
    
    /** 
     * 
     * @since 4.2
     */
    public CopyNameWorker( boolean enableAfterExecute, int iNameStyle ) {
        super( enableAfterExecute );
        
        if ( (iNameStyle == SHOW_JUST_NAME) || (iNameStyle == SHOW_FULLY_QUALIFIED_NAME) ) {
            this.iNameStyle = iNameStyle;
        }
    }

    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#getEnableState()
     * @since 4.2
     */
    @Override
    public boolean setEnabledState() {
        
        boolean enable = false;
        Object selection = getSelection();
        
        if( selection instanceof ISelection ) {
            ISelection iSelection = (ISelection)selection;
            
            if( SelectionUtilities.isSingleSelection( iSelection ) ) {
                String sName = formatName( iSelection );

                if ( sName != null && !( sName.equals( "" ) ) ) {    //$NON-NLS-1$
                    enable = true;
                }
            }
        }
        
        return enable;
    }

    /** 
     * @see com.metamatrix.ui.actions.IActionWorker#execute()
     * @since 4.2
     */
    @Override
    public boolean execute() {
        
        boolean successful = false;
        Object selection = getSelection();
        
        if( selection instanceof ISelection ) {
            ISelection iSelection = (ISelection)selection;
            
            if( SelectionUtilities.isSingleSelection(iSelection)) {
                String sName = formatName( iSelection );

                SystemClipboardUtilities.setContents( sName );
            }
        }
        return successful;
    }
    
    protected String formatName( ISelection sel ) {
        String sResultName = "";   //$NON-NLS-1$
        
        if ( sel instanceof IStructuredSelection && !sel.isEmpty() ) {
            
            IStructuredSelection selection= (IStructuredSelection) sel;
            Object elem= selection.getFirstElement();
            
            String sFullName = formatMessage( elem );
            
            sResultName = sFullName.replace( OLD_DELIM, NEW_DELIM );
//            System.out.println("[CopyNameWorker.formatName] After replaces, Name is: " + sFullName );   //$NON-NLS-1$
            
            if ( iNameStyle == SHOW_JUST_NAME ) {
                sResultName = parseNameFromFullName( sResultName );
            }
        }

        return sResultName;
    }

    protected String parseNameFromFullName( String sFullName ) {
        String sName = "";    //$NON-NLS-1$
        int iLastDelim = sFullName.lastIndexOf( NEW_DELIM );
        
        if ( iLastDelim != -1 ) {
            // copy the last segment only (this should be the element's name)
            sName = sFullName.substring( iLastDelim + 1 );
        }
        return sName;
    }
    
    protected String formatMessage( Object oSel ) {
        Object elem = oSel;
        if (elem instanceof EObject) {
            return formatEObjectMessage((EObject) elem);
        } else if (elem instanceof IResource) {
            return formatResourceMessage((IResource) elem);
        } else if (elem instanceof ImportContainer) {
            return ((ImportContainer)elem).toString();
        } else {
            return elem.getClass().getName();
        }
    }
        
    public String formatEObjectMessage(EObject element) {
        String result = "";   //$NON-NLS-1$

        if ( element instanceof ENamedElement ) {
            return result + ((ENamedElement) element).getName();
        }
        result += ModelerCore.getModelEditor().getModelRelativePathIncludingModel(element, false);
        
        return result;
    }
        
    private String formatResourceMessage(IResource element) {

        return element.getName(); 
    }   
    
}
