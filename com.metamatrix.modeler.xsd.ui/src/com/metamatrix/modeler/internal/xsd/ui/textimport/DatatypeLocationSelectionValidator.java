/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.textimport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;


/** 
 * @since 4.2
 */
public class DatatypeLocationSelectionValidator  implements ISelectionStatusValidator, ModelerXsdUiConstants {

    private StatusInfo stStatus;
    private static final String WRONG_CLASS_ERROR_MSG 
        = Util.getString("DatatypeLocationSelectionValidator.wrongClassErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_READONLY_ERROR_MSG 
        = Util.getString("DatatypeLocationSelectionValidator.containerReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_OBJECT_READONLY_ERROR_MSG 
        = Util.getString("DatatypeLocationSelectionValidator.containerObjectReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_MODEL_READONLY_ERROR_MSG 
        = Util.getString("DatatypeLocationSelectionValidator.containerModelReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String NOTHING_SELECTED 
        = Util.getString("DatatypeLocationSelectionValidator.wrongClassErrorMessage.text"); //$NON-NLS-1$
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {


        // deal with empty selection
        if ( selection == null || selection.length == 0 ) {
            return new StatusInfo(PLUGIN_ID, IStatus.ERROR, NOTHING_SELECTED);
        }
        
        // check class
        boolean bAllAreValid = true;
        
        for( int i = 0; i < selection.length; i++  ) {
            if ( isDatatypeContainer( selection[ i ] ) 
              || isDatatypeModelResource( selection[ i ] ) 
              || isContainer( selection[ i ] ) ) {
                // ok     
            } else {            
                bAllAreValid = false;
                stStatus =  new StatusInfo( PLUGIN_ID,     
                                            IStatus.ERROR, 
                                            WRONG_CLASS_ERROR_MSG );                              
                break;
            }            
        }
                
        
        // if containers are valid, make sure they are modifiable
        if ( bAllAreValid ) {
            for( int i = 0; i < selection.length; i++  ) {
                if ( containerIsModifiable( selection[ i ] ) ) {
                    // ok     
                } else {            
                    final Object theSelection = selection[i];
                    String msg = CONTAINER_READONLY_ERROR_MSG;
                    if ( theSelection instanceof ModelResource ) {
                        msg = CONTAINER_MODEL_READONLY_ERROR_MSG;
                    } else if ( theSelection instanceof IFile ) {
                        msg = CONTAINER_MODEL_READONLY_ERROR_MSG;
                    } else if ( isDatatypeContainer(selection[i]) ) {
                        msg = CONTAINER_OBJECT_READONLY_ERROR_MSG;
                    }
                    bAllAreValid = false;
                    stStatus =  new StatusInfo( PLUGIN_ID,     
                                                IStatus.ERROR, 
                                                msg );                              
                    break;
                }            
            }                     
        }
                
        if ( bAllAreValid ) {
             stStatus =  new StatusInfo( PLUGIN_ID,  
                                        IStatus.OK, 
                                        "" );     //$NON-NLS-1$                
        } 
        
        return stStatus;
    }

    private boolean isDatatypeContainer( Object oSelection ) {
        boolean bResult = false;
        if( oSelection instanceof EObject ) {
            try {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)oSelection);
                if (mr != null) {
                    if(mr.getModelType().getValue() == ModelType.TYPE) {
                        bResult = true;
                    }
                }
            } catch( ModelWorkspaceException mwe ) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());                       
            }
        }

        return bResult;     
    }
    
    private boolean isDatatypeModelResource( Object oSelection ) {
        boolean bResult = false;
        if ( oSelection instanceof IResource ) {
            try {
                
                IResource resource = (IResource)oSelection;                                            
            
                if ( ModelUtilities.isModelFile( resource ) ) {
                    ModelResource mr = ModelUtilities.getModelResource( (IFile)resource, true );
                    if (mr != null) {
                        if(mr.getModelType().getValue() == ModelType.TYPE) {
                            bResult = true;
                        }
                    }
                }       
            } catch( ModelWorkspaceException mwe ) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());                       
            }
        }
        return bResult;
    }

    private boolean isContainer( Object oSelection ) {
        return (oSelection instanceof IContainer);
    }

    private boolean containerIsModifiable( Object oSelection ) {

        boolean bResult = false;
        ModelResource mr = null;
        
        if ( isDatatypeModelResource( oSelection ) ) {
            try {
                mr = ModelUtilities.getModelResource( (IFile)oSelection, true );
            } catch( ModelWorkspaceException mwe ) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());                       
            }
        }
        else
        if ( isDatatypeContainer( oSelection ) ) {
            mr = ModelUtilities.getModelResourceForModelObject( (EObject)oSelection );
        }
        else
        if ( isContainer( oSelection ) ) {
            return ( ! ModelUtil.isIResourceReadOnly(((IContainer) oSelection)) );
        }
        
        bResult = !ModelUtilities.isReadOnly( mr );
                        
        return bResult;
    }
    
}
