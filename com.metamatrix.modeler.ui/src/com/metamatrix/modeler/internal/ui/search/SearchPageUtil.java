/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.ui.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.ui.IWorkingSet;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.search.IModelObjectMatch;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/**
 * @since 4.2
 */
public final class SearchPageUtil implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(SearchPageUtil.class);

    public static final String ONE_MATCH_MSG = Util.getString(PREFIX + "msg.searchOneMatch"); //$NON-NLS-1$

    public static final String MULTIPLE_MATCHES_MSG = Util.getString(PREFIX + "msg.searchMultipleMatches"); //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Don't allow instance construction.
     */
    private SearchPageUtil() {}

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the <code>IResource</code> for a specified object URI.
     * @param theObjectUri the object's URI
     * @return the object's resource or <code>null</code> if not found
     */
    public static IResource getResource(String theObjectUri) {
        // an object's URI includes the objects's UUID. to find the resource need to strip off the
        // UUID portion.
        int index = theObjectUri.indexOf('#'); // delimeter before UUID
        String uri = (index == -1) ? theObjectUri
                                   : theObjectUri.substring(0, index);

        return WorkspaceResourceFinderUtil.findIResource(uri);
    }

    /**
     * Gets the currently selected {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}s.
     * @param theSearchContainer the access point to the search dialog
     * @return the model workspace items selected in the workspace
     */
    public static List getSelectedModelWorkspaceItems(ISearchPageContainer theSearchContainer) {
        return getModelWorkspaceItemSelection(theSearchContainer).toList();
    }

    /**
     * Gets the current active selection filtered to only contain
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}s.
     * @param theSearchContainer the access point to the search dialog
     * @return the current filtered selection
     */
    public static IStructuredSelection getModelWorkspaceItemSelection(ISearchPageContainer theSearchContainer) {
        IStructuredSelection result = null;
        ISelection tempSelection = theSearchContainer.getSelection();

        if (tempSelection == null || tempSelection.isEmpty() || (!(tempSelection instanceof IStructuredSelection))) {
            result = StructuredSelection.EMPTY;
        } else {
            List validSelections = new ArrayList(((IStructuredSelection)tempSelection).size());
            Iterator itr = ((IStructuredSelection)tempSelection).iterator();

            while (itr.hasNext()) {
                Object selection = getModelWorkspaceResource(itr.next());

                if (selection != null) {
                    validSelections.add(selection);
                }
            }

            // create new selection
            result = (validSelections.isEmpty() ? StructuredSelection.EMPTY
                                                : new StructuredSelection(validSelections));
        }

        return result;
    }

    /**
     * Gets the collection of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}s within the
     * selected working sets.
     * @return the collection of model workspace items
     */
    public static List getWorkingSetWorkspaceItems(ISearchPageContainer theSearchContainer) {
        IWorkingSet[] workingSets = theSearchContainer.getSelectedWorkingSets();
        Set allElements = new HashSet();
        if( workingSets != null ) {
            for (int i = 0; i < workingSets.length; i++) {
                IAdaptable[] elements = workingSets[i].getElements();

                for (int j = 0; j < elements.length; j++) {
                    ModelWorkspaceItem item = getModelWorkspaceResource(elements[j]);

                    if (item != null) {
                        allElements.add(item);
                    }
                }
            }
        }

        if( allElements.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList(allElements);
    }

    public static ModelWorkspaceItem getModelWorkspaceResource(Object theObject) {
        ModelWorkspaceItem result = null;

        if (theObject != null) {
            if (theObject instanceof ModelWorkspaceItem) {
                result = (ModelWorkspaceItem)theObject;
            } else if (theObject instanceof IResource) {
                try {
                    result = ModelWorkspaceManager.getModelWorkspaceManager()
                                                  .findModelWorkspaceItem((IResource)theObject);
                } catch (ModelWorkspaceException theException) {
                    Util.log(IStatus.ERROR,
                             theException,
                             Util.getString(PREFIX + "msg.resourceProblem", //$NON-NLS-1$
                                            new Object[] {theObject, theObject.getClass()}));
                }
            } else if (theObject instanceof IAdaptable) {
                result = getModelWorkspaceResource(((IAdaptable)theObject).getAdapter(IResource.class));
            }
        }

        return result;
    }

    /**
     * Gets the current workspace scope.
     * <p><strong>Should not be called until {@link #setContainer(ISearchPageContainer)} is called by the Eclipse
     * search framework.</strong></p>
     * @return the list of {@link ModelWorkspaceItem}s defining the scope of the search
     */
    public static List getModelWorkspaceScope(ISearchPageContainer theSearchContainer) {
        List result = null;

        switch (theSearchContainer.getSelectedScope()) {
            case ISearchPageContainer.WORKSPACE_SCOPE: {
                // empty list so entire workspace will be searched
                result = Arrays.asList(new Object[] {ModelerCore.getModelWorkspace()});
                break;
            }
            case ISearchPageContainer.SELECTION_SCOPE: {
                result = getSelectedModelWorkspaceItems(theSearchContainer);
                break;
            }
            case ISearchPageContainer.WORKING_SET_SCOPE: {
                result = getWorkingSetWorkspaceItems(theSearchContainer);
                break;
            }
            default: {
                // should not happen
                Assertion.isNotNull(result);
                break;
            }
        }

        return result;
    }

    public static List getEObjectsFromSearchSelection(ISelection theSelection) {

        List objs = SelectionUtilities.getSelectedObjects(theSelection);
        Set<EObject> eObjs = new HashSet<EObject>();
        boolean isSearchResult = false;

        if( objs != null && !objs.isEmpty() ) {
            Object nextObj = null;
            Iterator iter = objs.iterator();

            while( iter.hasNext() ) {
                nextObj = iter.next();

                if (nextObj instanceof IModelObjectMatch) {
                    isSearchResult = true;
                    EObject eObj = ((IModelObjectMatch)nextObj).getEObject();

                    if (eObj != null) {
                        eObjs.add(eObj);
                    }
                }
            }
        }

        if( !isSearchResult )
            return null;

        return new ArrayList(eObjs);
    }
}
