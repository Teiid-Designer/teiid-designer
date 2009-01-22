/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.eventsupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.osgi.framework.Bundle;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * The <code>SelectionUtilities</code> class contains utility methods for the {@link ISelection}
 * objects. Methods that return <code>EObject</code> do not return <code>EObject</code>s that are remote
 * object types.
 */
public class SelectionUtilities implements UiConstants,
                                           UiConstants.ExtensionPoints.RemoteObjectType {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(SelectionUtilities.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Collection of <code>Class</code> objects identifying the remote types.
     */
    private static List remoteObjectTypes;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////

    static {
        // get the remoteObjectType extension point
        UiPlugin plugin = UiPlugin.getDefault();
        if (plugin != null) {
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(plugin.getBundle().getSymbolicName(),
			                                                                                   ID);

            // get the all it's extensions to the ModelEditorPage extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length == 0) {
                remoteObjectTypes = Collections.EMPTY_LIST;
            } else {
                remoteObjectTypes = new ArrayList();

                for (int i = 0; i < extensions.length; i++) {
                    Bundle bundle = Platform.getBundle(extensions[i].getNamespaceIdentifier());
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    for (int j = 0; j < elements.length; j++) {
                        try {
                            String className = elements[j].getAttribute(CLASSNAME);
                            remoteObjectTypes.add(bundle.loadClass(className));
                        } catch (Exception theException) {
                            String message = Util.getString(PREFIX + "loadRemoteTypesMessage", //$NON-NLS-1$
                                    new Object[] { elements[j].getAttribute(CLASSNAME),
                                            bundle.getSymbolicName()});
                            InternalUiConstants.Util.log(IStatus.ERROR, theException, message);
                        }
                    }
                }
            }
        } else {
            remoteObjectTypes = Collections.EMPTY_LIST;
        } // endif
    }

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
     * or a multi-selection, or if the single-selected object is not an <code>EObject</code>, or if the
     * object is a remote object.
     * @param theSelection the selection whose selected <code>EObject</code> is being requested
     * @return the selected <code>EObject</code> or <code>null</code>
     */
    public static EObject getSelectedEObject(ISelection theSelection) {
        EObject result = null;
        Object temp = getSelectedObject(theSelection);

        if ((temp != null) && (temp instanceof EObject) && !isRemoteObject(temp)) {
            result = (EObject)temp;
        }

        return result;
    }

    /**
     * Gets the selected <code>EObject</code>s from the collection of selected objects. Remote objects are
     * <strong>NOT</code> returned.
     * @param theSelection the selection whose selected <code>EObject</code>s are being requested
     * @return the selected <code>EObject</code>s or an empty <code>List</code>
     */
    public static List getSelectedEObjects(ISelection theSelection) {
        List temp = getSelectedObjects(theSelection);
        List result = new ArrayList(temp);

        if (!result.isEmpty()) {
            Iterator itr = temp.iterator();

            while (itr.hasNext()) {
                Object obj = itr.next();

                if (!(obj instanceof EObject) || isRemoteObject(obj)) {
                    result.remove(obj);
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
        List temp = getSelectedObjects(theSelection);
        List result = new ArrayList(temp);

        if (!result.isEmpty()) {
            Iterator itr = temp.iterator();

            while (itr.hasNext()) {
                Object obj = itr.next();

                if (!(obj instanceof IResource)) {
                    result.remove(obj);
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
    public static List getSelectedObjects(ISelection theSelection) {
        List result = null;

        if ((theSelection != null) && !theSelection.isEmpty()) {
            if (theSelection instanceof IStructuredSelection) {
                result = ((IStructuredSelection)theSelection).toList();
            }
        }

        return (result == null) ? Collections.EMPTY_LIST
                                : result;
    }

    /**
     * Obtains all the remote objects in the specified selection.
     * @param theSelection the selection whose remote objects are being requested
     * @return the remote objects or an empty <code>List</code>
     */
    public static List getSelectedRemoteObjects(ISelection theSelection) {
        List temp = getSelectedObjects(theSelection);
        List result = new ArrayList(temp);

        if (!result.isEmpty()) {
            Iterator itr = result.iterator();

            while (itr.hasNext()) {
                Object obj = itr.next();

                if (!isRemoteObject(obj)) {
                    result.remove(obj);
                }
            }
        }

        return result;
    }

    /**
     * Indicates if all selected objects are {@link EObject}s. None of the <code>EObject</codes> are remote
     * objects.
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

    private static boolean isRemoteObject(Object theObject) {
        boolean result = false;

        for (int size = remoteObjectTypes.size(), i = 0; i < size; i++) {
            Class remoteType = (Class)remoteObjectTypes.get(i);

            if (remoteType.isInstance(theObject)) {
                result = true;
                break;
            }
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
