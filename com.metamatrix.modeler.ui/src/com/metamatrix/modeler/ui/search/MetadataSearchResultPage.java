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

package com.metamatrix.modeler.ui.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.IShowInTargetList;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 *
 */
public class MetadataSearchResultPage extends AbstractTextSearchViewPage implements IAdaptable, UiConstants {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * The limit of results displayed.
     * 
     * @since 6.0.0
     */
    private static final int DEFAULT_RESULT_LIMIT = 1000;

    /**
     * The settings key of the result limit.
     * 
     * @since 6.0.0
     */
    private static final String RESULT_LIMIT_KEY = "com.metamatrix.modeler.search.resultpage.limit"; //$NON-NLS-1$

    static final String[] SHOW_IN_TARGETS = new String[] {Extensions.Explorer.VIEW};

    private static final IShowInTargetList SHOW_IN_TARGET_LIST = new IShowInTargetList() {
        public String[] getShowInTargetIds() {
            return SHOW_IN_TARGETS;
        }
    };

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private ResultsContentProvider contentProvider;

    private ILabelProvider labelProvider;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    public MetadataSearchResultPage() {
        this.contentProvider = new ResultsContentProvider();
        this.labelProvider = new MetadataSearchLabelProvider();

        setElementLimit(new Integer(DEFAULT_RESULT_LIMIT));
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    StructuredViewer accessViewer() {
        return getViewer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
     */
    @Override
    protected void clear() {
        if (this.contentProvider != null) {
            this.contentProvider.clear();
            getViewer().refresh();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
     */
    @Override
    protected void configureTableViewer( TableViewer viewer ) {
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(this.contentProvider);
        viewer.setLabelProvider(this.labelProvider);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                focusPage();
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
     */
    @Override
    protected void configureTreeViewer( TreeViewer viewer ) {
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(this.contentProvider);
        viewer.setLabelProvider(this.labelProvider);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                focusPage();
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
     */
    @Override
    protected void elementsChanged( Object[] objects ) {
        // this method is called when search again is run
        if (this.contentProvider != null) {
            this.contentProvider.elementsChanged(objects);
            getViewer().refresh();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    protected void fillContextMenu( IMenuManager mgr ) {

        try {
            ActionService actionService = UiPlugin.getDefault().getActionService(this.getSite().getWorkbenchWindow().getActivePage());
            IAction action = actionService.getAction(IModelerActionConstants.ModelerGlobalActions.EDIT);
            mgr.add(action);
        } catch (CoreException err) {
            Util.log(err);
        }

        super.fillContextMenu(mgr);
    }
    
    /**
     * There is a problem when a new viewer selection occurs the search view does not get activated. This is a workaround.
     * 
     * @since 6.0.6
     */
    void focusPage() {
        if (!getViewer().getControl().isDisposed()) {
            UiUtil.getWorkbenchPage().activate(getViewPart());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (IShowInTargetList.class.equals(adapter)) {
            return SHOW_IN_TARGET_LIST;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getCurrentMatch()
     */
    @Override
    public Match getCurrentMatch() {
        IStructuredSelection selection = (IStructuredSelection)getViewer().getSelection();

        if (!selection.isEmpty()) {
            Object element = selection.getFirstElement();

            if (element instanceof Match) {
                return (Match)element;
            }

            return super.getCurrentMatch();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getDisplayedMatchCount(java.lang.Object)
     */
    @Override
    public int getDisplayedMatchCount( Object element ) {
        if (element instanceof Match) {
            return super.getDisplayedMatchCount(((Match)element).getElement());
        }

        return super.getDisplayedMatchCount(element);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#gotoNextMatch()
     */
    @Override
    public void gotoNextMatch() {
        Match currMatch = getCurrentMatch();
        Match[] matches = getInput().getMatches(currMatch.getElement());
        boolean foundNextMatch = false;

        for (int i = 0; i < matches.length; ++i) {
            if (matches[i].equals(currMatch) && (i < (matches.length - 1))) {
                foundNextMatch = true;
                showMatch(matches[i + 1], 0, 0, true);
                break;
            }
        }

        if (!foundNextMatch) {
            navigate(true);
            showMatch(getCurrentMatch(), 0, 0, true);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#gotoPreviousMatch()
     */
    @Override
    public void gotoPreviousMatch() {
        Match currMatch = getCurrentMatch();
        Match[] matches = getInput().getMatches(currMatch.getElement());
        boolean foundPrevMatch = false;

        for (int i = 0; i < matches.length; ++i) {
            if (matches[i].equals(currMatch) && (i > 0)) {
                foundPrevMatch = true;
                showMatch(matches[i - 1], 0, 0, true);
                break;
            }
        }

        if (!foundPrevMatch) {
            navigate(false);
            showMatch(getCurrentMatch(), 0, 0, true);
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#internalRemoveSelected()
     */
    @Override
    public void internalRemoveSelected() {
        StructuredViewer viewer = getViewer();
        IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
        this.contentProvider.remove(selection.toArray());
        viewer.refresh();
    }

    private void navigate( boolean forward ) {
        if (getLayout() == FLAG_LAYOUT_FLAT) {
            Table table = (Table)getViewer().getControl();
            int itemCount = table.getItemCount();

            if (itemCount == 0) {
                return;
            }

            int[] selection = table.getSelectionIndices();
            int nextIndex = 0;

            if (selection.length > 0) {
                if (forward) {
                    nextIndex = selection[selection.length - 1] + 1;

                    if (nextIndex >= itemCount) {
                        nextIndex = 0;
                    }
                } else {
                    nextIndex = selection[0] - 1;

                    if (nextIndex < 0) {
                        nextIndex = itemCount - 1;
                    }
                }
            }

            table.setSelection(nextIndex);
            table.showSelection();
        } else {
            // TreeItem currentItem = getCurrentItem(forward);
            // if (currentItem == null) return;
            // TreeItem nextItem = null;
            // if (forward) {
            // nextItem = getNextItemForward(currentItem);
            // if (nextItem == null) nextItem = getFirstItem();
            // } else {
            // nextItem = getNextItemBackward(currentItem);
            // if (nextItem == null) nextItem = getLastItem();
            // }
            // if (nextItem != null) {
            // internalSetSelection(nextItem);
            // }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#setElementLimit(java.lang.Integer)
     */
    @Override
    public void setElementLimit( Integer limit ) {
        super.setElementLimit(limit);

        // save new limit
        getSettings().put(RESULT_LIMIT_KEY, limit);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
     */
    @Override
    protected void showMatch( Match match,
                              int currentOffset,
                              int currentLength,
                              boolean activate ) {
        IModelObjectMatch modelObjMatch = (IModelObjectMatch)match;
        ModelEditorManager.open(modelObjMatch.getEObject(), true);
        getViewer().setSelection(new StructuredSelection(match), true);
    }

    // ===========================================================================================================================
    // Inner Class
    // ===========================================================================================================================

    class ResultsContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        // =======================================================================================================================
        // Fields
        // =======================================================================================================================

        private MetadataSearchResult result;

        private Map<MetadataMatchInfo, Match[]> model = new HashMap<MetadataMatchInfo, Match[]>();

        // =======================================================================================================================
        // Methods
        // =======================================================================================================================
        
        public void clear() {
            this.model.clear();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        @Override
        public void dispose() {
            // nothing to do
        }

        public void elementsChanged( Object[] updatedElements ) {
            ensureModelLoaded();
        }

        private void ensureModelLoaded() {
            if (this.result == null) {
                this.model.clear();
            } else {
                for (Object matchInfo : this.result.getElements()) {
                    this.model.put((MetadataMatchInfo)matchInfo, this.result.getMatches(matchInfo));
                }
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren( Object parentElement ) {
            return this.result.getMatches(parentElement);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent( Object element ) {
            if (element instanceof Match) {
                return ((Match)element).getElement();
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            // if the input element is a result the layout could be changing so make sure the model has been loaded
            ensureModelLoaded();

            if (getLayout() == FLAG_LAYOUT_FLAT) {
                // return matches
                List<Match> allMatches = new ArrayList<Match>();

                for (Match[] matches : this.model.values()) {
                    for (Match match : matches) {
                        allMatches.add(match);
                    }
                }

                return allMatches.toArray();
            }

            return this.model.keySet().toArray();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren( Object element ) {
            return (element instanceof MetadataMatchInfo ? true : false);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            this.model.clear();
            this.result = null;

            if (newInput instanceof MetadataSearchResult) {
                this.result = (MetadataSearchResult)newInput;
            }
        }
        
        public void remove(Object[] removedObjects) {
            for (Object obj : removedObjects) {
                if (obj instanceof MetadataMatch) {
                    MetadataMatch match = (MetadataMatch)obj;
                    this.result.removeMatch(match);
                    
                    if (this.result.getMatches(match.getElement()).length == 0) {
                        this.model.remove(match.getElement());
                    }
                } else if (obj instanceof MetadataMatchInfo) {
                    this.result.removeMatches(this.result.getMatches(obj));
                    this.model.remove(obj);
                }
            }
        }
    }
}
