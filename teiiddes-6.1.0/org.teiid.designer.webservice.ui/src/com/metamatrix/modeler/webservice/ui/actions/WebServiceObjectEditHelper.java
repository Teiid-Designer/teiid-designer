/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.actions;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelper;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.webservice.ui.editor.OperationEditorPage;
import com.metamatrix.modeler.webservice.ui.editor.OperationObjectEditorPage;

/** 
 * @since 5.0.2
 */
public class WebServiceObjectEditHelper extends ModelObjectEditHelper {

    /** 
     * 
     * @since 5.0.2
     */
    public WebServiceObjectEditHelper() {
        super();
    }
    
    @Override
    public boolean canClone(Object theObj) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canClone(theObj);
    }

    @Override
    public boolean canCopy(Object theObj) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canCopy(theObj);
    }

    @Override
    public boolean canCut(Object theObj) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canCut(theObj);
    }

    @Override
    public boolean canDelete(Object theObj) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canDelete(theObj);
    }

    @Override
    public boolean canPaste(Object theObj,
                            Object thePasteParent) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canPaste(theObj, thePasteParent);
    }

    @Override
    public boolean canRename(Object theObj) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canRename(theObj);
    }

    @Override
    public boolean canUndoCopy(Object theObj) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canUndoCopy(theObj);
    }

    @Override
    public boolean canUndoCut(Object theObj) {
        return super.canUndoCut(theObj);
    }

    @Override
    public boolean canUndoDelete(Object theObj) {
        return super.canUndoDelete(theObj);
    }

    @Override
    public boolean canUndoPaste(Object theObj,
                                Object thePasteParent) {
        if( isNonEditWidgetInFocus() ) {
            return false;
        }
        return super.canUndoPaste(theObj, thePasteParent);
    }
    
    private boolean isNonEditWidgetInFocus() {
        boolean result = false;
        IWorkbenchPart activePart = UiPlugin.getDefault().getCurrentWorkbenchWindow().getPartService().getActivePart();

        if( activePart instanceof ModelEditor ) {
            IEditorPart activeSubEditorPart = ((ModelEditor)activePart).getActiveEditor();
            
            if( activeSubEditorPart instanceof OperationEditorPage ) {
                // Find the Object editor
                ModelObjectEditorPage objectEditor = ((ModelEditor)activePart).getActiveObjectEditor();
                if( objectEditor != null && objectEditor instanceof OperationObjectEditorPage ) {
                    result = !((OperationObjectEditorPage)objectEditor).allowsExternalEdits();
                }
            }
        }
        return result;
    }

}
