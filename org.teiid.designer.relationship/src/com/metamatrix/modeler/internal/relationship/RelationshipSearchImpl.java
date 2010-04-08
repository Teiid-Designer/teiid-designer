/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.IPathComparator;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;
import com.metamatrix.modeler.core.search.commands.FindRelationshipsCommand;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceSearchIndexSelector;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.RelationshipSearch;

/**
 * RelationshipSearchImpl
 */
public class RelationshipSearchImpl implements RelationshipSearch {

    private RelationshipType typeCriteria;
    private boolean includeSubtypes;
    private boolean namePatternCaseSensitive;
    private String namePattern;
    private final List modelScope;
    private final List readOnlyModelScope;
    private List participantCriteria;
    private final List readOnlyParticipantCriteria;
    private final List results;
    private final List readOnlyResults;
    private final ModelWorkspace workspace;
    private boolean noResourceSelected;

    /**
     * Construct an instance of RelationshipSearchImpl.
     */
    public RelationshipSearchImpl( final ModelWorkspace workspace,
                                   final IndexSelectorFactory selector ) {
        super();
        CoreArgCheck.isNotNull(workspace);
        CoreArgCheck.isNotNull(selector);
        this.workspace = workspace;
        // this.indexSelector = selector;

        // Initialize the remainder ...
        this.results = new LinkedList();
        this.readOnlyResults = Collections.unmodifiableList(this.results);

        this.modelScope = new LinkedList();
        this.readOnlyModelScope = Collections.unmodifiableList(this.modelScope);

        this.participantCriteria = new LinkedList();
        this.readOnlyParticipantCriteria = Collections.unmodifiableList(this.participantCriteria);

        this.namePattern = DEFAULT_NAME_CRITERIA;
        this.namePatternCaseSensitive = DEFAULT_NAME_CASE_SENSITIVE;

        this.includeSubtypes = DEFAULT_INCLUDE_SUBTYPES;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getModelWorkspace()
     */
    public ModelWorkspace getModelWorkspace() {
        return this.workspace;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getParticipantsCriteria()
     */
    public List getParticipantsCriteria() {
        return this.readOnlyParticipantCriteria;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#setParticipantsCriteria(java.util.List)
     */
    public void setParticipantsCriteria( List modelWorkspaceItems ) {
        if (modelWorkspaceItems == null || modelWorkspaceItems.isEmpty()) {
            this.participantCriteria = modelWorkspaceItems;
            return;
        }
        if (this.participantCriteria == null) {
            this.participantCriteria = new LinkedList();
        } else this.participantCriteria.clear();

        Object item = null;
        for (Iterator iter = modelWorkspaceItems.iterator(); iter.hasNext();) {
            item = iter.next();
            if (item instanceof ModelWorkspaceItem) {
                int type = 0;
                type = ((ModelWorkspaceItem)item).getItemType();
                switch (type) {
                    case ModelWorkspaceItem.MODEL_RESOURCE:
                        this.participantCriteria.add(((ModelWorkspaceItem)item).getPath().toString());
                        break;
                    case ModelWorkspaceItem.MODEL_PROJECT:
                        List resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        for (int i = 0; i < resourceList.size(); i++) {
                            this.participantCriteria.add(i, ((ModelWorkspaceItem)resourceList.get(i)).getPath().toString());
                        }
                        break;
                    case ModelWorkspaceItem.MODEL_FOLDER:
                        resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        for (int i = 0; i < resourceList.size(); i++) {
                            this.participantCriteria.add(i, ((ModelWorkspaceItem)resourceList.get(i)).getPath().toString());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getNameCriteria()
     */
    public String getNameCriteria() {
        return this.namePattern;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#isNameCriteriaCaseSensitive()
     */
    public boolean isNameCriteriaCaseSensitive() {
        return this.namePatternCaseSensitive;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#setNameCriteria(java.lang.String, boolean)
     */
    public void setNameCriteria( final String namePattern,
                                 final boolean caseSensitive ) {
        if (namePattern == null || namePattern.trim().length() == 0 || NAME_PATTERN_ANY_STRING.equals(namePattern)) {
            this.namePattern = NAME_PATTERN_ANY_STRING;
        } else {
            this.namePattern = namePattern;
        }
        this.namePatternCaseSensitive = caseSensitive;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getRelationshipTypeCriteria()
     */
    public RelationshipType getRelationshipTypeCriteria() {
        return this.typeCriteria;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#isIncludeSubtypes()
     */
    public boolean isIncludeSubtypes() {
        return this.includeSubtypes;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#setRelationshipTypeCriteria(com.metamatrix.metamodels.relationship.RelationshipType,
     *      boolean)
     */
    public void setRelationshipTypeCriteria( final RelationshipType requiredType,
                                             final boolean includeSubtypes ) {
        this.typeCriteria = requiredType;
        this.includeSubtypes = includeSubtypes;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getRelationshipModelScope()
     */
    public List getRelationshipModelScope() {
        return this.readOnlyModelScope;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#setRelationshipModelScope(java.util.List)
     */
    public void setRelationshipModelScope( final List modelWorkspaceItems ) {

        CoreArgCheck.isNotNull(modelWorkspaceItems);

        noResourceSelected = false;
        HashSet hsSet = new HashSet();

        this.modelScope.clear();
        final int numItems = modelWorkspaceItems.size();
        for (int i = 0; i < numItems; i++) {
            final Object item = modelWorkspaceItems.get(i);
            if (item instanceof ModelWorkspaceItem) {
                int type = ((ModelWorkspaceItem)item).getItemType();
                switch (type) {
                    case ModelWorkspaceItem.MODEL_RESOURCE: {
                        hsSet.add(item);
                    }
                        break;

                    case ModelWorkspaceItem.MODEL_PROJECT: {
                        List resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        hsSet.addAll(resourceList);

                    }
                        break;

                    case ModelWorkspaceItem.MODEL_WORKSPACE: {
                        /*
                         * jh fix for Defect 18513:
                         *  Previously, an empty set of Model Resources represented WORKSPACE scope;
                         *  we needed to change this so that an empty set represents the cases when selection
                         *  resolves to no resources (for example when an empty project or folder is selected),
                         *  so we can handle those cases properly.
                         *  Now, in the case that a user specifies WORKSPACE as the scope, we will get 
                         *  ALL children and identify the scope by a POSITIVE, complete set of models.
                         */
                        LinkedList resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        // System.out.println("[MetadataSearchImpl.setModelScope] WORKSPACE; children found -->resourceList: " +
                        // resourceList );
                        hsSet.addAll(resourceList);
                    }
                        break;

                    case ModelWorkspaceItem.MODEL_FOLDER: {
                        List resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        hsSet.addAll(resourceList);

                    }
                        break;

                    default:
                        break;
                }
            } // endif -- instance of ModelWorkspaceItem
        } // endfor
        // may still be empty, results in failure during canExecute
        this.modelScope.addAll(hsSet);
        // Need to set this flag so execution can be halted if no selected items exists (i.e closed projects selected)
        if (numItems == 0) noResourceSelected = true;
    }

    /**
     * @param modelWorkspaceItem
     * @param resourceList
     * @return
     */
    private List getChildResources( final ModelWorkspaceItem modelWorkspaceItem,
                                    List resourceList ) {
        try {
            final ModelWorkspaceItem[] childItems = modelWorkspaceItem.getChildren();
            int type = 0;
            for (int i = 0; i < childItems.length; i++) {
                ModelWorkspaceItem item = childItems[i];
                type = item.getItemType();
                switch (type) {
                    case ModelWorkspaceItem.MODEL_RESOURCE:
                        resourceList.add(item);
                        break;
                    case ModelWorkspaceItem.MODEL_FOLDER:
                    case ModelWorkspaceItem.MODEL_PROJECT:
                        getChildResources(item, resourceList);
                        break;
                    default:
                        break;
                }
            }
        } catch (ModelWorkspaceException e) {
            // mtkTODO Auto-generated catch block
            e.printStackTrace();
        }
        return resourceList;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#canExecute()
     */
    public IStatus canExecute() {
        if (this.typeCriteria == null) {
            final int code = 0;
            final String msg = RelationshipPlugin.Util.getString("RelationshipSearchImpl.MissingTypeCriteria"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, RelationshipPlugin.PLUGIN_ID, code, msg, null);
        }

        if (this.participantCriteria != null && this.participantCriteria.isEmpty()) {
            final int code = 0;
            final String msg = RelationshipPlugin.Util.getString("RelationshipSearchImpl.0"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, RelationshipPlugin.PLUGIN_ID, code, msg, null);
        }

        final int code = 0;
        final String msg = RelationshipPlugin.Util.getString("RelationshipSearchImpl.The_search_may_be_executed"); //$NON-NLS-1$
        return new Status(IStatus.OK, RelationshipPlugin.PLUGIN_ID, code, msg, null);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus execute( final IProgressMonitor progressMonitor ) {
        final IStatus canStatus = canExecute();
        if (!canStatus.isOK()) {
            return canStatus;
        }

        if (noResourceSelected) {
            final int code = 0;
            final String msg = RelationshipPlugin.Util.getString("RelationshipSearchImpl.NoValidResourcesSelected"); //$NON-NLS-1$
            return new Status(IStatus.OK, RelationshipPlugin.PLUGIN_ID, code, msg, null);
        }

        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();

        // Clear any existing results ...
        this.results.clear();

        // Compute the index selector for the scope ...
        IStatus status = null;
        try {
            // final List includePaths = getPaths(this.readOnlyParticipantCriteria);
            final IndexSelector scopeSelector = new ModelWorkspaceSearchIndexSelector(this.readOnlyModelScope);

            status = doExecute(scopeSelector,
                               monitor,
                               this.typeCriteria,
                               this.includeSubtypes,
                               this.namePattern,
                               this.namePatternCaseSensitive,
                               this.readOnlyParticipantCriteria,
                               this.results);
        } catch (Throwable e) {
            final int code = 0;
            final String msg = e.getLocalizedMessage();
            return new Status(IStatus.OK, RelationshipPlugin.PLUGIN_ID, code, msg, e);
        }

        if (status == null) {
            final int code = 0;
            final String msg = RelationshipPlugin.Util.getString("RelationshipSearchImpl.The_search_completed_successfully"); //$NON-NLS-1$
            status = new Status(IStatus.OK, RelationshipPlugin.PLUGIN_ID, code, msg, null);
        }
        return status;
    }

    protected IStatus doExecute( final IndexSelector scopeSelector,
                                 final IProgressMonitor monitor,
                                 final RelationshipType typeCriteria,
                                 final boolean includeSubtypesOfType,
                                 final String namePattern,
                                 final boolean namePatternCaseSensitive,
                                 final List participantIpaths,
                                 final List results ) {
        // Do nothing
        final FindRelationshipsCommand command = RelationshipPlugin.createFindRelationshipsCommand();

        // Set the search scope ...
        command.setIndexSelector(scopeSelector);

        // Set the participants paths ...
        command.setParticipantList(this.participantCriteria);

        // Set the set relationship resource scope paths ...

        command.setRelationshipResourceScopeList(this.modelScope);

        // Set the name pattern ...
        if (namePattern != null && !namePattern.equals(RelationshipSearch.NAME_PATTERN_ANY_STRING)) {
            command.setCaseSensitive(namePatternCaseSensitive);
            command.setNamePattern(namePattern);
        }

        // Set the type criteria ...
        command.setRelationshipTypeName(typeCriteria.getName());
        command.setIncludeSubtypes(includeSubtypesOfType);

        if (!command.canExecute()) {
            return null;
        }

        final IStatus status = command.execute();
        final Collection relationshipInfo = command.getRelationShipInfo();
        results.addAll(relationshipInfo);
        return status;
    }

    protected List getPaths( final List modelWorkspaceItems ) {
        final LinkedList paths = new LinkedList();
        final Iterator iter = modelWorkspaceItems.iterator();
        while (iter.hasNext()) {
            final ModelWorkspaceItem item = (ModelWorkspaceItem)iter.next();
            final IPath path = item.getPath();
            paths.add(path);
        }
        if (paths.isEmpty()) {
            return paths;
        }
        // Sort the paths to be in order ...
        final Comparator comparator = new IPathComparator();
        Collections.sort(paths, comparator);

        // Remove any paths that are below other paths ...
        final LinkedList validPaths = new LinkedList();
        IPath next = (IPath)paths.removeFirst();
        while (next != null) {
            // See if there is already a path that is above 'next' ...
            boolean skip = false;
            final ListIterator existingPathIter = validPaths.listIterator(validPaths.size());
            while (existingPathIter.hasPrevious()) {
                final IPath existingPath = (IPath)existingPathIter.previous();
                if (existingPath.isPrefixOf(next)) {
                    skip = true;
                    break;
                }
            }
            if (!skip) {
                // Add the path ...
                validPaths.add(next);
            }
            if (paths.size() == 0) {
                next = null;
            } else {
                next = (IPath)paths.removeFirst();
            }
        }
        return validPaths;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getResults()
     */
    public List getResults() {
        return this.readOnlyResults;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.search.ISearchEngine#getSearchCriteria()
     */
    @Override
    public String getSearchCriteria() {
        if (canExecute().isOK()) {
            String prefix = I18nUtil.getPropertyPrefix(RelationshipSearchImpl.class);
            StringBuilder txt = new StringBuilder();

            if (this.typeCriteria == RelationshipSearch.ANY_RELATIONSHIP_TYPE) {
                txt.append(RelationshipPlugin.Util.getString(prefix + "anyTypeCriteria")); //$NON-NLS-1$
            } else if (this.typeCriteria != null) {
                txt.append(RelationshipPlugin.Util.getString(prefix + "typeCriteria", this.typeCriteria.getName())); //$NON-NLS-1$
            }

            if (!DEFAULT_NAME_CRITERIA.equals(this.namePattern)) {
                if (txt.length() > 0) {
                    txt.append(", "); //$NON-NLS-1$
                }

                txt.append(RelationshipPlugin.Util.getString(prefix + "namePatternCriteria", this.namePattern)); //$NON-NLS-1$
            }

            if ((this.participantCriteria != null) && !this.participantCriteria.isEmpty()) {
                if (txt.length() > 0) {
                    txt.append(", "); //$NON-NLS-1$
                }

                txt.append(RelationshipPlugin.Util.getString(prefix + "participantCriteria", this.participantCriteria.size())); //$NON-NLS-1$
            }

            return txt.toString();
        }

        return null;
    }
}
