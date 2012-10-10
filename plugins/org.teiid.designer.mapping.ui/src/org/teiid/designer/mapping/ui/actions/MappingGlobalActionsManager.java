/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * MappingGlobalActionsManager is a class of static utility methods that can: Determine if objects support the standard global
 * edit actions: delete, cut, copy, paste, and clone. Execute these actions on the selected object(s). It is intended to work with
 * diagrams of type Mapping or Mapping Transformation.
 *
 * @since 8.0
 */
public class MappingGlobalActionsManager {

    // =======================================
    // Delete
    // =======================================

    public static boolean canDelete( List selectedEObjects ) {
        return allValidMappingClassChildren(selectedEObjects);
    }

    public static void delete( List sourceEObjects ) throws ModelerCoreException { // NO_UCD
        if (sourceEObjects != null && !sourceEObjects.isEmpty()) {

            Iterator iter = sourceEObjects.iterator();
            Object nextEObject = null;
            while (iter.hasNext()) {
                nextEObject = iter.next();
                if (nextEObject instanceof EObject) {
                    ModelerCore.getModelEditor().delete((EObject)nextEObject);
                }
            }
        }
    }

    // =======================================
    // Cut
    // =======================================

    public static boolean canCut( List selectedEObjects ) {
        return allValidMappingClassChildren(selectedEObjects);
    }

    public static void cut( List sourceEObjects ) throws ModelerCoreException { // NO_UCD

        if (sourceEObjects != null && !sourceEObjects.isEmpty()) {
            ModelerCore.getModelEditor().cutAllToClipboard(sourceEObjects);
        }
    }

    // =======================================
    // Copy
    // =======================================

    public static boolean canCopy( List sourceEObjects ) {
        if (sourceEObjects != null && !sourceEObjects.isEmpty() && allValidMappingClassChildren(sourceEObjects)) {
            return true;
        }

        return false;
    }

    public static void copy( List sourceEObjects ) throws ModelerCoreException { // NO_UCD
        if (sourceEObjects != null && !sourceEObjects.isEmpty()) {
            ModelerCore.getModelEditor().copyAllToClipboard(sourceEObjects);
        }
    }

    // =======================================
    // Paste
    // =======================================

    public static boolean canPaste( EObject transformationEObject,
                                    List selectedEObjects ) {
        boolean canPaste = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && TransformationHelper.isSqlTransformationMappingRoot(transformationEObject) && selectedEObjects != null
            && !selectedEObjects.isEmpty() && selectedEObjects.size() == 1) {

            // Let's make sure that the selected object is a MappingClass here....

            EObject selectedEObject = (EObject)selectedEObjects.get(0);
            if (selectedEObject instanceof MappingClass) {
                canPaste = ModelerCore.getModelEditor().isValidPasteParent(selectedEObject);
            }
        }

        return canPaste;
    }

    public static void paste( List selectedEObjects ) throws ModelerCoreException { // NO_UCD
        if (selectedEObjects != null && !selectedEObjects.isEmpty() && selectedEObjects.size() == 1) {
            EObject selectedEObject = (EObject)selectedEObjects.get(0);
            ModelerCore.getModelEditor().pasteFromClipboard(selectedEObject);
        }
    }

    // =======================================
    // Clone
    // =======================================

    public static boolean canClone( List selectedEObjects ) {
        return allValidMappingClassChildren(selectedEObjects);
    }

    public static void clone( List sourceEObjects ) throws ModelerCoreException {

        if (sourceEObjects != null && !sourceEObjects.isEmpty()) {

            Iterator iter = sourceEObjects.iterator();
            Object nextEObject = null;
            while (iter.hasNext()) {
                nextEObject = iter.next();
                if (nextEObject instanceof EObject) {
                    ModelerCore.getModelEditor().clone((EObject)nextEObject);
                }
            }
        }
    }

    // =======================================
    // Utility Methods
    // =======================================

    private static boolean allValidMappingClassChildren( List selectedEObjects ) {
        boolean allOK = true;
        if (selectedEObjects != null && !selectedEObjects.isEmpty()) {

            // walk source objects to determine if all are in the eContents list
            Iterator iter = selectedEObjects.iterator();
            Object oTemp = null;
            EObject eoTemp = null;
            while (iter.hasNext() && allOK) {
                oTemp = iter.next();

                if (oTemp instanceof EObject) {
                    eoTemp = (EObject)oTemp;
                    if (!(eoTemp instanceof MappingClassColumn) && !(eoTemp instanceof InputParameter)) {
                        allOK = false;
                    }
                } else allOK = false;
            }
        } else {
            // couldn't even check
            allOK = false;
        }

        return allOK;
    }

}
