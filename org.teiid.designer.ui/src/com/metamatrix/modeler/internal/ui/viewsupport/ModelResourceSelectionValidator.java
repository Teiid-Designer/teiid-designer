/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * ModelResourceSelectionValidator is an ISelectionStatusValidator that will pass
 * validation on only Model files.  If a MetamodelDescriptor is provided via the
 * constructor, then this validator will only pass ModelResources of the same
 * metamodel type.
 */
public class ModelResourceSelectionValidator implements ISelectionStatusValidator {

    private final static IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);

    private final static String NOTHING_SELECTED = UiConstants.Util.getString(
            "ModelResourceSelectionValidator.noSelection"); //$NON-NLS-1$
    private final static String MULTI_SELECTION = UiConstants.Util.getString(
            "ModelResourceSelectionValidator.noMultiSelection"); //$NON-NLS-1$
    private final static String MUST_SELECT_MODEL = UiConstants.Util.getString(
            "ModelResourceSelectionValidator.mustSelectModel"); //$NON-NLS-1$
    private final static String MUST_SELECT_MODELS = UiConstants.Util.getString(
            "ModelResourceSelectionValidator.mustSelectModels"); //$NON-NLS-1$
    private final static String MUST_MATCH_METAMODEL = "ModelResourceSelectionValidator.mustSelectSameMetamodel"; //$NON-NLS-1$
    private final static String MUST_MATCH_METAMODELS = "ModelResourceSelectionValidator.mustSelectSameMetamodels"; //$NON-NLS-1$


    private MetamodelDescriptor metamodelDescriptor = null;
    private boolean allowMultiSelection = false;

    /**
     * Construct an instance of ModelResourceMetamodelValidator that will only pass
     * selections of metamodel files.
     */
    public ModelResourceSelectionValidator(boolean allowMultiSelection) {
        this(null, allowMultiSelection);
    }

    /**
     * Construct an instance of ModelResourceMetamodelValidator that will only pass
     * ModelResources of the same metamodel.
     */
    public ModelResourceSelectionValidator(
        MetamodelDescriptor descriptorToMatch,
        boolean allowMultiSelection) {
        this.metamodelDescriptor = descriptorToMatch;
        this.allowMultiSelection = allowMultiSelection;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {
        IStatus result = OK_STATUS; 
        
        if ( selection == null || selection.length == 0 ) {
            return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, NOTHING_SELECTED);
        }
            
        if ( ! allowMultiSelection && selection.length > 1 ) {
            return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, MULTI_SELECTION);
        }

        if ( selection.length == 1 ) {
            // single selection logic
            if ( this.metamodelDescriptor != null ) {
                result = checkMetamodel(selection[0], false);
            } else {
                result = checkModelFile(selection[0], false);
            }
        } else {
            // multi-selection logic
            for ( int i=0 ; i<selection.length ; ++i ) {
                if ( this.metamodelDescriptor != null ) {
                    result = checkMetamodel(selection[i], true);
                } else {
                    result = checkModelFile(selection[i], true);
                }

                if ( result != OK_STATUS ) {
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * Check that the specified Object is a File.
     * @param obj
     * @param generateMultiSelectMessage
     * @return
     */
    private IStatus checkFile(Object obj, boolean generateMultiSelectMessage) {
        if ( obj instanceof IFile ) {
            return OK_STATUS;
        }
        if ( generateMultiSelectMessage ) {
            return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, MUST_SELECT_MODELS);
        }
        return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, MUST_SELECT_MODEL);
    }
    
    /**
     * Check that the specified Object is a model file.  This method calls checkFile().
     * @param obj
     * @param generateMultiSelectMessage
     * @return
     */
    private IStatus checkModelFile(Object obj, boolean generateMultiSelectMessage) {
        IStatus result = checkFile(obj, generateMultiSelectMessage);
        if ( result == OK_STATUS ) {
            if ( ! ModelUtilities.isModelFile((IFile) obj)) {
                if ( generateMultiSelectMessage ) {
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, MUST_SELECT_MODELS);
                }
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, MUST_SELECT_MODEL);
            }
        }
        return result;
    }

    /**
     * Check that the specified Object is of the same metamodel type that was specified
     * upon construction of this validator.  This method calls checkModelFile().
     * @param obj
     * @param generateMultiSelectMessage
     * @return
     */
    private IStatus checkMetamodel(Object obj, boolean generateMultiSelectMessage) {
        IStatus result = checkModelFile(obj, generateMultiSelectMessage);
        if ( result == OK_STATUS ){
            boolean exceptionOccurred = false;
            ModelResource modelResource = null;
            try {
                modelResource = ModelUtilities.getModelResource((IFile) obj, true);
            } catch (Exception ex) {
                UiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName());
                if ( generateMultiSelectMessage ) {
                    String message = UiConstants.Util.getString(MUST_MATCH_METAMODELS, this.metamodelDescriptor.getDisplayName());
                    result = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
                } else {
                    String message = UiConstants.Util.getString(MUST_MATCH_METAMODEL, this.metamodelDescriptor.getDisplayName());
                    result = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
                }
                exceptionOccurred = true;
            }
            if (!exceptionOccurred) {
                MetamodelDescriptor descriptor = null;
                try {
                    descriptor = modelResource.getPrimaryMetamodelDescriptor();
                } catch (Exception ex) {
                    UiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName());
                    exceptionOccurred = true;
                    if ( generateMultiSelectMessage ) {
                        String message = UiConstants.Util.getString(MUST_MATCH_METAMODELS, this.metamodelDescriptor.getDisplayName());
                        result = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
                    } else {
                        String message = UiConstants.Util.getString(MUST_MATCH_METAMODEL, this.metamodelDescriptor.getDisplayName());
                        result = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
                    }
                }
                if (!exceptionOccurred) {
                    if (! this.metamodelDescriptor.equals(descriptor)) {
                        if ( generateMultiSelectMessage ) {
                            String message = UiConstants.Util.getString(MUST_MATCH_METAMODELS, this.metamodelDescriptor.getDisplayName());
                            result = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
                        } else {
                            String message = UiConstants.Util.getString(MUST_MATCH_METAMODEL, this.metamodelDescriptor.getDisplayName());
                            result = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
                        }
                    }
                }
            }
        }
        return result;
    }

}
