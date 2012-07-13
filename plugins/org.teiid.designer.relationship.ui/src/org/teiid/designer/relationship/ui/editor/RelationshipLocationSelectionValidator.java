/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.editor;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.relationship.RelationshipContainer;
import org.teiid.designer.metamodels.relationship.RelationshipPackage;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * RelationshipLocationSelectionValidator
 */
public class RelationshipLocationSelectionValidator 
  implements ISelectionStatusValidator {

    private StatusInfo stStatus;
    private static final String WRONG_CLASS_ERROR_MSG 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipLocationSelectionValidator.wrongClassErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_READONLY_ERROR_MSG 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipLocationSelectionValidator.containerReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_OBJECT_READONLY_ERROR_MSG 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipLocationSelectionValidator.containerObjectReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String CONTAINER_MODEL_READONLY_ERROR_MSG 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipLocationSelectionValidator.containerModelReadOnlyErrorMessage.text"); //$NON-NLS-1$
    private static final String NOTHING_SELECTED 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipLocationSelectionValidator.wrongClassErrorMessage.text"); //$NON-NLS-1$
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    @Override
	public IStatus validate(Object[] selection) {


        // deal with empty selection
        if ( selection == null || selection.length == 0 ) {
            return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, NOTHING_SELECTED);
        }
        
        // check class
        boolean bAllAreValid = true;
        
        for( int i = 0; i < selection.length; i++  ) {
            if ( isRelationshipContainer( selection[ i ] ) 
              || isRelationshipModelResource( selection[ i ] ) 
              || isContainer( selection[ i ] ) ) {
                // ok     
            } else {            
                bAllAreValid = false;
                stStatus =  new StatusInfo( UiConstants.PLUGIN_ID,     
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
                    } else if ( theSelection instanceof RelationshipContainer ) {
                        msg = CONTAINER_OBJECT_READONLY_ERROR_MSG;
                    }
                    bAllAreValid = false;
                    stStatus =  new StatusInfo( UiConstants.PLUGIN_ID,     
                                                IStatus.ERROR, 
                                                msg );                              
                    break;
                }            
            }                     
        }
                
        if ( bAllAreValid ) {
             stStatus =  new StatusInfo( UiConstants.PLUGIN_ID,  
                                        IStatus.OK, 
                                        "" );     //$NON-NLS-1$                
        } 
        
        return stStatus;
    }

    private boolean isRelationshipContainer( Object oSelection ) {         
        return ( oSelection instanceof RelationshipContainer );        
    }
    
    private boolean isRelationshipModelResource( Object oSelection ) {
        boolean bResult = false;
        if ( oSelection instanceof IResource ) {
            try {
                
                IResource resource = (IResource)oSelection;                                            
            
                if ( ModelUtilities.isModelFile( resource ) ) {
                    ModelResource mr = ModelUtil.getModelResource( (IFile)resource, true );
                    
                    if (mr != null) {
                        MetamodelDescriptor descriptor = mr.getPrimaryMetamodelDescriptor();
                        
                        if ((descriptor != null) && descriptor.getNamespaceURI().equals(RelationshipPackage.eNS_URI)) {
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
        
        if ( isRelationshipModelResource( oSelection ) ) {
            try {
                mr = ModelUtil.getModelResource( (IFile)oSelection, true );
            } catch( ModelWorkspaceException mwe ) {
                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());                       
            }
        }
        else
        if ( isRelationshipContainer( oSelection ) ) {
            mr = ModelUtilities.getModelResourceForModelObject( (RelationshipContainer)oSelection );
        }
        else
        if ( isContainer( oSelection ) ) {
            return (! ModelUtil.isIResourceReadOnly(((IContainer) oSelection)));
        }
        
        bResult = !ModelUtilities.isReadOnly( mr );
                        
        return bResult;
    }
    
}
