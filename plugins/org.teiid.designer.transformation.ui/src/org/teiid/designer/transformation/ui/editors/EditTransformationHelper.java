/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.SelectFromEObjectListDialog;
import org.teiid.designer.ui.viewsupport.SelectModelObjectLabelProvider;


/**
 * This class provides helper methods to open/edit a transformation for a specific ModelResource
 * 
 * @since 8.0
 */
public class EditTransformationHelper {
    private static final String DIALOG_TITLE        = UiConstants.Util.getString("EditTransformationHelper.dialogTitle"); //$NON-NLS-1$
    //private static final String DIALOG_MESSAGE      = UiConstants.Util.getString("EditTransformationHelper.dialogMessage"); //$NON-NLS-1$
    private static final String STAGING_TABLE_STR   = "<Staging Table>"; //$NON-NLS-1$
    private static final String MAPPING_CLASS_STR   = "<Mapping Class>"; //$NON-NLS-1$
    private static final String TABLE_STR           = "<Table>"; //$NON-NLS-1$
    private static final String PROCEDURE_STR       = "<Procedure>"; //$NON-NLS-1$
    //private static final String VIEW_STR            = "<View>"; //$NON-NLS-1$
    
    private ModelResource modelResource;

    /**
     * @since 5.0
     */
    public EditTransformationHelper(ModelResource modelResource) {
        super();
        this.modelResource = modelResource;
    }

    public void setModelResource(ModelResource theModelResource) {
        this.modelResource = theModelResource;
    }

    public void openAndEdit(EObject eObj) {
        ModelEditorManager.edit(eObj, PluginConstants.TRANSFORMATION_EDITOR_ID);
    }

    public Collection getAllTransformationTargets() throws ModelWorkspaceException {
        return getAllTransformationTargets(this.modelResource);
    }

    /**
     * @param theModelResource
     * @return
     * @throws ModelWorkspaceException
     * @since 5.0
     */
    public Collection getAllTransformationTargets(ModelResource theModelResource) throws ModelWorkspaceException {
        List transformations = theModelResource.getModelTransformations().getTransformations();
        int nTransforms = transformations.size();
        Collection allTargets = new ArrayList(nTransforms);

        for (Iterator iter = transformations.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof SqlTransformationMappingRoot) {
                EObject table = ((SqlTransformationMappingRoot)obj).getTarget();
                if (table != null) {
                    allTargets.add(table);
                }
            }
        }

        return allTargets;
    }

    /**
     * @param modelResource
     * @return
     * @throws ModelWorkspaceException
     * @since 5.0
     */
    public EObject queryUserToSelectTarget(ModelResource modelResource) {
        EObject selectedTarget = null;

        try {
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
            String message = UiConstants.Util.getString("EditTransformationHelper.dialogMessage", modelResource.getItemName()); //$NON-NLS-1$
            SelectFromEObjectListDialog dialog = 
                new SelectFromEObjectListDialog(
                         shell, 
                         getAllTransformationTargets(modelResource), 
                         false, 
                         DIALOG_TITLE, 
                         message,
                         new MyLabelProvider());

            dialog.open();

            if (dialog.getReturnCode() == Window.OK) {
                // now select the object
                Object[] results = dialog.getResult();
                selectedTarget = (EObject)results[0];
            }
        } catch (ModelWorkspaceException theException) {
            UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
        }

        return selectedTarget;
    }

    /**
     * @return
     * @throws ModelWorkspaceException
     * @since 5.0
     */
    public EObject queryUserToSelectTarget() {
        return queryUserToSelectTarget(this.modelResource);
    }
    
    class MyLabelProvider extends SelectModelObjectLabelProvider {

        public MyLabelProvider() {
            super();
        }
        
        @Override
        public String getText(Object theElement) {
            if ( theElement instanceof EObject ) {
                EObject eo = (EObject)theElement;
                String sText = ModelerCore.getModelEditor().getName(eo);
                if( showPath ) {
                    String type = getType(eo);
                    if( type != null ) {
                        sText += CoreStringUtil.Constants.SPACE + type + CoreStringUtil.Constants.SPACE;
                    }
                    String path = getAppendedPath(eo);
                    if( path != null ) {
                        sText += " : " + getAppendedPath(eo); //$NON-NLS-1$
                    }
                }
                return sText;
            }
            return super.getText(theElement);
        }
        
        private String getType(EObject eObj) {
            if( TransformationHelper.isStagingTable(eObj) ) {
                return STAGING_TABLE_STR;
            } else if( TransformationHelper.isMappingClass(eObj)) {
                return MAPPING_CLASS_STR;
            } else if( org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(eObj)) {
                return PROCEDURE_STR;
            } else if( org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isTable(eObj)) {
                return TABLE_STR;
            }
            return null;
        }
        
        private String getAppendedPath(EObject eObj) {
            if( TransformationHelper.isMappingClass(eObj)) {
                MappingClass mc = (MappingClass)eObj;
                EObject doc = mc.getMappingClassSet().getTarget();
                IPath pathToDoc = ModelerCore.getModelEditor().getFullPathToParent(doc);
                pathToDoc = pathToDoc.append(ModelerCore.getModelEditor().getName(doc));
                return pathToDoc.toString(); 
            }
            
            return ModelerCore.getModelEditor().getFullPathToParent(eObj).toString(); 
        }
        
    }

}
