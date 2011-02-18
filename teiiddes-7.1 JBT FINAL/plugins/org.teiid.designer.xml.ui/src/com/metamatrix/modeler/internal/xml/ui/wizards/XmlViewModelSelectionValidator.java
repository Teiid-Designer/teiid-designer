/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;


/** 
 * @since 5.0
 */
public class XmlViewModelSelectionValidator implements ISelectionStatusValidator, ModelerXmlUiConstants {
    
    private static final String I18N_PREFIX = 
        I18nUtil.getPropertyPrefix(XmlViewModelSelectionValidator.class);
    
    private StatusInfo stStatus;
    
    private static final String WRONG_CLASS_ERROR_MSG               = getString("wrongClassErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_READONLY_ERROR_MSG        = getString("containerReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_MODEL_READONLY_ERROR_MSG  = getString("containerModelReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String NOTHING_SELECTED                    = getString("wrongClassErrorMessage.text"); //$NON-NLS-1$
    
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {


        // deal with empty selection
        if ( selection == null || selection.length == 0) {
            return new StatusInfo(PLUGIN_ID, IStatus.ERROR, NOTHING_SELECTED);
        }
        
        // check class
        boolean bAllAreValid = true;
        
        for( int i = 0; i < selection.length; i++  ) {
            if ( isValidModelResource( selection[ i ] ) 
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
    
    private boolean isValidModelResource( Object oSelection ) {
        boolean bResult = false;
        if ( oSelection instanceof IResource ) {
            try {
                
                IResource resource = (IResource)oSelection;                                            
            
                if ( ModelUtilities.isModelFile( resource ) ) {
                    ModelResource mr = ModelUtil.getModelResource( (IFile)resource, true );
                    
                    if (mr != null&& ModelUtilities.isVirtual(mr) ) {
                        MetamodelDescriptor descriptor = mr.getPrimaryMetamodelDescriptor();
                        
                        if ((descriptor != null) && descriptor.getNamespaceURI().equals(XmlDocumentPackage.eNS_URI)) {
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
        
        if ( isValidModelResource( oSelection ) ) {
            try {
                mr = ModelUtil.getModelResource( (IFile)oSelection, true );
            } catch( ModelWorkspaceException mwe ) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());                       
            }
        }
        else
            if ( isContainer( oSelection ) ) {
                return ( ! ModelUtil.isIResourceReadOnly(((IContainer) oSelection)) );
            }
        
        bResult = !ModelUtilities.isReadOnly( mr );
                        
        return bResult;
    }
    
}
