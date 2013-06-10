/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.eventsupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.teiid.designer.ui.common.UiConstants;

/**
 * The <code>SelectionUtilities</code> class contains utility methods for the {@link ISelection}
 * objects.
 *
 * @since 8.0
 */
public class SelectionUtilities implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** No arg construction not allowed. */
    private SelectionUtilities() {}

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the selected <code>EObject</code>. Returns <code>null</code> if selection is empty
     * or a multi-selection, or if the single-selected object is not an <code>EObject</code>.
     * @param theSelection the selection whose selected <code>EObject</code> is being requested
     * @return the selected <code>EObject</code> or <code>null</code>
     */
    public static EObject getSelectedEObject(ISelection theSelection) {
        EObject result = null;
        Object temp = getSelectedObject(theSelection);

        if ((temp != null) && (temp instanceof EObject)) {
            result = (EObject)temp;
        }

        return result;
    }

    /**
     * Gets the selected <code>EObject</code>s from the collection of selected objects.
     * @param theSelection the selection whose selected <code>EObject</code>s are being requested
     * @return the selected <code>EObject</code>s or an empty <code>List</code>
     */
    public static List<EObject> getSelectedEObjects(ISelection theSelection) {
        List result = getSelectedObjects(theSelection);

        if (!result.isEmpty()) {
            Iterator itr = result.iterator();

            while (itr.hasNext()) {
                Object obj = itr.next();

                if (!(obj instanceof EObject)) {
                    itr.remove();
                }
            }
        }

        return result;
    }

    /**
     * Gets the selected <code>IResource</code>s from the collection of selected objects.
     * @param theSelection the selection whose selected <code>IResource</code>s are being requested
     * @return the selected <code>IResource</code>s or an empty <code>List</code>
     */
    public static List getSelectedIResourceObjects(ISelection theSelection) {
        List result = getSelectedObjects(theSelection);

        if (!result.isEmpty()) {
            Iterator itr = result.iterator();

            while (itr.hasNext()) {
                Object obj = itr.next();

                if (!(obj instanceof IResource)) {
                    itr.remove();
                }
            }
        }

        return result;
    }

    /**
     * Gets the selected <code>Object</code>. Returns <code>null</code> if selection is empty
     * or a multi-selection.
     * @param theSelection the selection whose selected <code>Object</code> is being requested
     * @return the selected <code>Object</code> or <code>null</code>
     */
    public static Object getSelectedObject(ISelection theSelection) {
        Object result = null;

        if (isSingleSelection(theSelection) && (theSelection instanceof IStructuredSelection)) {
            result = ((IStructuredSelection)theSelection).getFirstElement();
        }

        return result;
    }

    /**
     * Gets all selected <code>Object</code>s.
     * @param theSelection the selection whose selected <code>Object</code>s are being requested
     * @return the selected <code>Object</code>s or an empty <code>List</code>
     */
    public static List<Object> getSelectedObjects(ISelection theSelection) {
        List<Object> result = null;

        if ((theSelection != null) && !theSelection.isEmpty()) {
            if (theSelection instanceof IStructuredSelection) {
                result = new ArrayList(((IStructuredSelection)theSelection).toList());
            }
        }

        return (result == null) ? Collections.EMPTY_LIST
                                : result;
    }

    /**
     * Indicates if all selected objects are {@link EObject}s.
     * @param theSelection the selection being checked
     * @return <code>true</code> if all selected objects are <code>EObject</code>; <code>false</code> otherwise.
     */
    public static boolean isAllEObjects(ISelection theSelection) {
        boolean result = ((theSelection != null) && !theSelection.isEmpty() && (theSelection instanceof IStructuredSelection));

        if (result) {
            result = (getSelectedObjects(theSelection).size() == getSelectedEObjects(theSelection).size());
        }

        return result;
    }

    public static boolean isMixedObjectTypes(ISelection theSelection) {
        if( isSingleSelection(theSelection)) {
            return false;
        }

        if( isAllEObjects(theSelection) || isAllIResourceObjects(theSelection)) {
            return false;
        }

        return true;
    }

    /**
     * Indicates if all selected objects are {@link IResource}s.
     * @param theSelection the selection being checked
     * @return <code>true</code> if all selected objects are <code>EObject</code>; <code>false</code> otherwise.
     */
    public static boolean isAllIResourceObjects(ISelection theSelection) {
        boolean result = ((theSelection != null) && !theSelection.isEmpty() && (theSelection instanceof IStructuredSelection));

        if (result) {
            result = (getSelectedObjects(theSelection).size() == getSelectedIResourceObjects(theSelection).size());
        }

        return result;
    }

    /**
     * Indicates if more than one object is selected or if the selection is <code>null</code>.
     * @param theSelection the selection being checked
     * @return <code>true</code> if more than one object is selected; <code>false</code> otherwise.
     */
    public static boolean isMultiSelection(ISelection theSelection) {
        boolean result = (theSelection != null);

        if (result && (theSelection instanceof IStructuredSelection)) {
            result = (((IStructuredSelection)theSelection).size() > 1);
        }

        return result;
    }

    /**
     * Indicates if exactly one object is selected or if the selection is <code>null</code>.
     * @param theSelection the selection being checked
     * @return <code>true</code> if exactly one object is selected; <code>false</code> otherwise.
     */
    public static boolean isSingleSelection(ISelection theSelection) {
        boolean result = (theSelection != null);

        if (result && (theSelection instanceof IStructuredSelection)) {
            result = (((IStructuredSelection)theSelection).size() == 1);
        }

        return result;
    }

    /**
     * Indicates if the specified selection is <code>null</code> or empty.
     * @param theSelection the selection being checked
     * @return <code>true</code> if <code>null</code> or empty; <code>false</code> otherwise.
     */
    public static boolean isEmptySelection(ISelection theSelection) {
        return ((theSelection == null) || theSelection.isEmpty());
    }

}
