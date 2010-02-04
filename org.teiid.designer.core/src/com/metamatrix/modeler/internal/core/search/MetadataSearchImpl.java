/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.IPathComparator;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.diagram.DiagramPackage;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;
import com.metamatrix.modeler.core.search.MetadataSearch;
import com.metamatrix.modeler.core.search.commands.FindObjectCommand;
import com.metamatrix.modeler.core.search.commands.FindTypedObjectCommand;
import com.metamatrix.modeler.core.search.runtime.AnnotatedObjectRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.core.search.runtime.TypedObjectRecord;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceSearchIndexSelector;
import com.metamatrix.modeler.internal.core.search.commands.FindObjectCommandImpl;
import com.metamatrix.modeler.internal.core.search.commands.FindTypedObjectCommandImpl;

/**
 * MetadataSearchImpl
 */
public class MetadataSearchImpl implements MetadataSearch {

    private static final EObject[] EMPTY_EOBJECT_ARRAY = new EObject[0];
    private static final String[] RUNTIME_TYPE_NAMES;
    private static final Set TYPED_META_CLASS_NAMES = new HashSet(11);
    private static final Map EXCLUDED_OBJECT_NS_URIS = new HashMap(3);
    static {
        RUNTIME_TYPE_NAMES = getRuntimeTypeNames();
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.relational.Column"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.relational.ProcedureParameter"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.sdt.SimpleDatatype"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("org.eclipse.xsd.XSDSimpleTypeDefinition"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.transformation.InputParameter"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.transformation.MappingClassColumn"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.dataaccess.Element"); //$NON-NLS-1$
        TYPED_META_CLASS_NAMES.add("com.metamatrix.metamodels.dataaccess.ProcedureParameter"); //$NON-NLS-1$

        // defect 15660: store all of the excluded metamodel namespace URIs with a listing of any exceptions
        // (objects in the metamodel that should NOT be excluded are placed in the corresponding bucket)
        EXCLUDED_OBJECT_NS_URIS.put(CorePackage.eNS_URI,
                                    new String[] {ModelerCore.getModelEditor().getUri(CorePackage.eINSTANCE.getModelAnnotation()).toString()});
        EXCLUDED_OBJECT_NS_URIS.put(TransformationPackage.eNS_URI, null);
        EXCLUDED_OBJECT_NS_URIS.put(DiagramPackage.eNS_URI,
                                    new String[] {ModelerCore.getModelEditor().getUri(DiagramPackage.eINSTANCE.getDiagram()).toString()});
        EXCLUDED_OBJECT_NS_URIS.put(EcorePackage.eNS_URI, null);
    }

    private EClass metaClass;
    private EObject datatype;

    private boolean includeSubtypes;
    private String runtimeType;

    private String featureName;
    private String textPattern;
    private boolean containsPattern;

    private final List modelScope;
    private final List readOnlyModelScope;

    private final List results;
    private final List readOnlyResults;

    private final ModelWorkspace workspace;

    /**
     * Construct an instance of MetadataSearchImpl.
     */
    public MetadataSearchImpl( final ModelWorkspace workspace,
                               final IndexSelectorFactory selector ) {
        super();
        ArgCheck.isNotNull(workspace);
        ArgCheck.isNotNull(selector);
        this.workspace = workspace;

        // Initialize the remainder ...
        this.results = new LinkedList();
        this.readOnlyResults = Collections.unmodifiableList(this.results);

        this.modelScope = new LinkedList();
        this.readOnlyModelScope = Collections.unmodifiableList(this.modelScope);

        this.includeSubtypes = DEFAULT_INCLUDE_SUBTYPES;
        this.containsPattern = DEFAULT_CONTAINS_PATTERN;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getModelWorkspace()
     */
    public ModelWorkspace getModelWorkspace() {
        return this.workspace;
    }

    // =========================================================================
    // Search Criteria
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getMetaClass()
     * @since 4.1
     */
    public EClass getMetaClass() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#setMetaClass(org.eclipse.emf.ecore.EClass)
     * @since 4.1
     */
    public void setMetaClass( final EClass metaClass ) {
        this.metaClass = metaClass;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getDatatype()
     * @since 4.1
     */
    public EObject getDatatype() {
        return this.datatype;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getDatatypes()
     * @since 4.1
     */
    public EObject[] getDatatypes() {
        try {
            return ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
        } catch (Throwable t) {
            final String msg = ModelerCore.Util.getString("MetadataSearchImpl.Error_retrieving_types_from_DatatypeManager"); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, t, msg);
        }
        return EMPTY_EOBJECT_ARRAY;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getModelScope()
     * @since 4.1
     */
    public List getModelScope() {
        return this.readOnlyModelScope;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getFeatureCriteria()
     * @since 4.1
     */
    public String getFeatureCriteria() {
        return this.textPattern;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getRuntimeType()
     * @since 4.1
     */
    public String getRuntimeType() {
        return this.runtimeType;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getRuntimeTypes()
     * @since 4.1
     */
    public String[] getRuntimeTypes() {
        return RUNTIME_TYPE_NAMES;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getFeaturesNames()
     * @since 4.1
     */
    public String[] getFeaturesNames() {
        return MetadataSearch.ALLOWABLE_SEARCH_FEATURES;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getSelectedFeatureName()
     * @since 4.1
     */
    public String getSelectedFeatureName() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#isIncludeSubtypes()
     * @since 4.1
     */
    public boolean isIncludeSubtypes() {
        return this.includeSubtypes;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#isTypedMetaClass(org.eclipse.emf.ecore.EClass)
     * @since 4.2
     */
    public boolean isTypedMetaClass( final EClass metaClass ) {
        if (metaClass == null) {
            return true;
        }
        if (TYPED_META_CLASS_NAMES.contains(metaClass.getInstanceClassName())) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#setDatatype(org.eclipse.emf.ecore.EObject, boolean)
     * @since 4.1
     */
    public void setDatatype( final EObject datatype,
                             final boolean includeSubtypes ) {
        this.datatype = datatype;
        this.includeSubtypes = includeSubtypes;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#setModelScope(java.util.List)
     * @since 4.1
     */
    public void setModelScope( final List modelWorkspaceItems ) {

        ArgCheck.isNotNull(modelWorkspaceItems);

        // jh fix for Defect 18513: use a set to prevent duplicates when overlapping containers
        // are selected
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
                        // System.out.println("[MetadataSearchImpl.setModelScope] RESOURCE; adding this item --> modelScope: " +
                        // hsSet );
                        break;
                    }
                    case ModelWorkspaceItem.MODEL_PROJECT: {
                        LinkedList resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        // System.out.println("[MetadataSearchImpl.setModelScope] PROJECT; children found -->resourceList: " +
                        // resourceList );
                        hsSet.addAll(resourceList);
                        break;
                    }
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

                        break;
                    }
                    case ModelWorkspaceItem.MODEL_FOLDER: {
                        LinkedList resourceList = new LinkedList();
                        getChildResources((ModelWorkspaceItem)item, resourceList);
                        // System.out.println("[MetadataSearchImpl.setModelScope] FOLDER; children found -->resourceList: " +
                        // resourceList );
                        hsSet.addAll(resourceList);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } // endif -- modelworkspaceitem instance
        } // endfor -- entries

        // copy elements from the hashset to modelScope
        this.modelScope.addAll(hsSet);

        // may still be empty, results in failure during canExecute
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#setFeatureCriteria(java.lang.String, java.lang.String, boolean,
     *      boolean)
     * @since 4.1
     */
    public void setFeatureCriteria( String featureName,
                                    String textPattern,
                                    boolean containsPattern ) {
        this.featureName = featureName;
        this.textPattern = textPattern;
        this.containsPattern = containsPattern;
    }

    /**
     * @see com.metamatrix.modeler.core.search.MetadataSearch#setRuntimeType(java.lang.String)
     * @since 4.1
     */
    public void setRuntimeType( final String runtimeType ) {
        this.runtimeType = runtimeType;
    }

    // =========================================================================
    // Execution
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#canExecute()
     */
    public IStatus canExecute() {
        final boolean invalidMetaclassSearch = (this.metaClass == null);
        final boolean invalidDatatypeSearch = (this.datatype == null && this.runtimeType == null);
        final boolean invalidFeatureSearch = (this.featureName == null || this.textPattern == null || this.textPattern.length() == 0);
        if (invalidMetaclassSearch && invalidDatatypeSearch && invalidFeatureSearch) {
            final int code = 0;
            final String msg = ModelerCore.Util.getString("MetadataSearchImpl.Missing_search_criteria"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, code, msg, null);
        }
        if (this.featureName != null && (this.textPattern == null || this.textPattern.length() == 0)) {
            final int code = 0;
            final String msg = ModelerCore.Util.getString("MetadataSearchImpl.Missing_search_criteria"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, code, msg, null);
        }

        final int code = 0;
        final String msg = ModelerCore.Util.getString("MetadataSearchImpl.Search_may_be_executed"); //$NON-NLS-1$
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, code, msg, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.core.search.MetadataSearch#getSearchCriteria()
     */
    @Override
    public String getSearchCriteria() {
        if (canExecute().isOK()) {
            String prefix = I18nUtil.getPropertyPrefix(MetadataSearchImpl.class);
            StringBuilder txt = new StringBuilder();

            if (this.metaClass != null) {
                txt.append(ModelerCore.Util.getString(prefix + "classCriteria", this.metaClass.getName())); //$NON-NLS-1$
            }

            if (this.datatype != null) {
                if (txt.length() > 0) {
                    txt.append(", "); //$NON-NLS-1$
                }

                txt.append(ModelerCore.Util.getString(prefix + "datatypeCriteria", //$NON-NLS-1$
                                                      ((XSDSimpleTypeDefinition)this.datatype).getName(),
                                                      this.includeSubtypes));
            }

            if (this.runtimeType != null) {
                if (txt.length() > 0) {
                    txt.append(", "); //$NON-NLS-1$
                }

                txt.append(ModelerCore.Util.getString(prefix + "runtimeTypeCriteria", this.runtimeType)); //$NON-NLS-1$
            }

            if (this.featureName != null) {
                if (txt.length() > 0) {
                    txt.append(", "); //$NON-NLS-1$
                }

                txt.append(ModelerCore.Util.getString(prefix + "featureCriteria", this.featureName)); //$NON-NLS-1$
            }

            if (this.textPattern != null) {
                if (txt.length() > 0) {
                    txt.append(", "); //$NON-NLS-1$
                }

                txt.append(ModelerCore.Util.getString(prefix + "textPatternCriteria", this.textPattern)); //$NON-NLS-1$
            }

            return txt.toString();

        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus execute( final IProgressMonitor progressMonitor ) {
        final IStatus canStatus = canExecute();
        if (!canStatus.isOK()) {
            return canStatus;
        }

        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();

        // Clear any existing results ...
        this.results.clear();

        // Compute the index selector for the scope ...
        IStatus status = null;
        try {

            // jh fix: always provide the scopeto the selector, even when empty:
            final IndexSelector scopeSelector = new ModelWorkspaceSearchIndexSelector(this.readOnlyModelScope);

            // Perform a search using datatype criteria ...
            if (this.datatype != null || this.runtimeType != null) {
                final EObject[] otherTypes = (this.includeSubtypes ? getSubtypes(this.datatype) : EMPTY_EOBJECT_ARRAY);
                status = doExecute(scopeSelector,
                                   monitor,
                                   this.metaClass,
                                   this.datatype,
                                   otherTypes,
                                   this.runtimeType,
                                   this.results);

                // Perform a search using feature criteria ...
                if (status.isOK() && (this.metaClass != null || this.textPattern != null)) {
                    final List featureSearchResults = new ArrayList();
                    status = doExecute(scopeSelector,
                                       monitor,
                                       this.metaClass,
                                       this.featureName,
                                       this.textPattern,
                                       this.containsPattern,
                                       featureSearchResults);

                    // Intersect the feature results with the datatype search results
                    this.intersetResultLists(featureSearchResults, this.results);
                    this.results.clear();
                    this.results.addAll(featureSearchResults);
                }

            }
            // Perform searches using feature pattern criteria ...
            else if (this.metaClass != null || this.textPattern != null) {
                status = doExecute(scopeSelector,
                                   monitor,
                                   this.metaClass,
                                   this.featureName,
                                   this.textPattern,
                                   this.containsPattern,
                                   this.results);
            }
            filterRecords(this.results);
        } catch (Throwable e) {
            final int code = 0;
            final String msg = e.getLocalizedMessage();
            return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, code, msg, e);
        }

        if (status == null) {
            final int code = 0;
            final String msg = ModelerCore.Util.getString("MetadataSearchImpl.Search_completed_successfully"); //$NON-NLS-1$
            status = new Status(IStatus.OK, ModelerCore.PLUGIN_ID, code, msg, null);
        }
        return status;
    }

    /**
     * Intersect the two lists of {@link SearchRecord} instances. The intersection is performed by iterating through each record
     * in firstResult and checking if a record with the same identifier exists in secondResult. If one is not found, the record is
     * removed from firstResult. The result of the executing the method is the modified firstResult list.
     * 
     * @param firstResult
     * @param secondResult
     * @since 4.2
     */
    protected void intersetResultLists( final List firstResult,
                                        final List secondResult ) {

        // Creating a Set of the URIs for every record in secondResult
        final Set ids = new HashSet(secondResult.size());
        for (final Iterator iter = secondResult.iterator(); iter.hasNext();) {
            SearchRecord record = (SearchRecord)iter.next();
            String id = getIdentifier(record);
            if (id != null) {
                ids.add(id);
            }
        }

        // Remove any records from firstResult that do not have a corresponding
        // identifier in secondResult
        for (final Iterator iter = firstResult.iterator(); iter.hasNext();) {
            SearchRecord record = (SearchRecord)iter.next();
            String id = getIdentifier(record);
            if (id == null || !ids.contains(id)) {
                iter.remove();
            }
        }

    }

    protected IStatus doExecute( final IndexSelector scopeSelector,
                                 final IProgressMonitor monitor,
                                 final EClass metaClass,
                                 final String featureName,
                                 final String textPattern,
                                 final boolean containsPattern,
                                 final List results ) {
        // Do nothing
        final FindObjectCommand command = new FindObjectCommandImpl();

        // Set the search scope ...
        command.setIndexSelector(scopeSelector);

        // Set the command parameters ...
        command.setMetaClass(metaClass);
        command.setFeatureCriteria(featureName, textPattern, containsPattern);

        if (!command.canExecute()) {
            return null;
        }

        final IStatus status = command.execute();
        final Collection recordInfo = command.getRecordInfo();
        results.addAll(recordInfo);
        return status;
    }

    protected IStatus doExecute( final IndexSelector scopeSelector,
                                 final IProgressMonitor monitor,
                                 final EClass metaClass,
                                 final EObject datatype,
                                 final EObject[] subtypes,
                                 final String runtimeType,
                                 final List results ) {
        // Do nothing
        final FindTypedObjectCommand command = new FindTypedObjectCommandImpl();

        // Set the search scope ...
        command.setIndexSelector(scopeSelector);

        // Set the command parameters ...
        command.setMetaClass(metaClass);
        command.setDatatype(datatype);
        command.setSubTypes(subtypes);
        command.setRuntimeType(runtimeType);

        if (!command.canExecute()) {
            return null;
        }

        final IStatus status = command.execute();
        final Collection recordInfo = command.getRecordInfo();
        results.addAll(recordInfo);
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

    // =========================================================================
    // Search Results
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipSearch#getResults()
     */
    public List getResults() {
        return this.readOnlyResults;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private String getIdentifier( final SearchRecord record ) {
        if (record != null) {
            String id = record.getUUID();
            if (id != null) {
                return id;
            } else if (record instanceof ResourceObjectRecord) {
                id = ((ResourceObjectRecord)record).getObjectURI();
                return id;
            } else if (record instanceof TypedObjectRecord) {
                id = ((TypedObjectRecord)record).getObjectURI();
                return id;
            } else if (record instanceof AnnotatedObjectRecord) {
                id = ((AnnotatedObjectRecord)record).getObjectURI();
                return id;
            }
        }
        return null;
    }

    private static EObject[] getSubtypes( final EObject startingDatatype ) {
        EObject[] otherTypes = EMPTY_EOBJECT_ARRAY;
        if (startingDatatype != null) {
            final Collection tmp = new HashSet();
            addSubtypesToCollection(startingDatatype, tmp);
            otherTypes = (EObject[])tmp.toArray(new EObject[tmp.size()]);
        }
        return otherTypes;
    }

    private static void addSubtypesToCollection( final EObject datatype,
                                                 final Collection subTypes ) {
        DatatypeManager mgr = ModelerCore.getWorkspaceDatatypeManager();
        if (mgr.isSimpleDatatype(datatype)) {
            try {
                EObject[] eObjects = mgr.getSubtypes(datatype);
                for (int i = 0; i < eObjects.length; i++) {
                    final EObject eObject = eObjects[i];
                    if (eObject != null) {
                        subTypes.add(eObject);
                        addSubtypesToCollection(eObject, subTypes);
                    }
                }
            } catch (ModelerCoreException err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
            }
        }
    }

    private static String[] getRuntimeTypeNames() {
        Collection runtimeTypeNames = DatatypeConstants.getRuntimeTypeNames();
        return (String[])new ArrayList(runtimeTypeNames).toArray(new String[runtimeTypeNames.size()]);
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
                    case ModelWorkspaceItem.MODEL_RESOURCE: {
                        resourceList.add(item);
                        break;
                    }
                    case ModelWorkspaceItem.MODEL_PROJECT: {
                        getChildResources(item, resourceList);
                        break;
                    }
                    case ModelWorkspaceItem.MODEL_FOLDER: {
                        getChildResources(item, resourceList);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (ModelWorkspaceException e) {
            // mtkTODO Auto-generated catch block
            e.printStackTrace();
        }
        return resourceList;
    }

    private void filterRecords( final List records ) {
        for (final Iterator it = records.iterator(); it.hasNext();) {
            final Object o = it.next();
            if (o instanceof ResourceObjectRecord) {
                final String metaclassURI = ((ResourceObjectRecord)o).getMetaclassURI();
                if (isMetaclassURIExcluded(metaclassURI)) {
                    it.remove();
                }
            }
        }
    }

    private boolean isMetaclassURIExcluded( final String metaclassURI ) {
        boolean success = false;
        if (metaclassURI != null && metaclassURI.length() > 0) {
            // grab each metamodel namespace URI who's objects should be excluded from the search
            for (Iterator it = MetadataSearchImpl.EXCLUDED_OBJECT_NS_URIS.keySet().iterator(); it.hasNext();) {
                final String excludedMetaclassURI = (String)it.next();
                if (metaclassURI.startsWith(excludedMetaclassURI)) {
                    success = true;
                    // verify that if that this object is not in the "exception" list (the object's metamodel
                    // namespace uri has been excluded, but it is still "searchable")
                    final String[] includedMetaObjects = (String[])MetadataSearchImpl.EXCLUDED_OBJECT_NS_URIS.get(excludedMetaclassURI);
                    if (includedMetaObjects != null && includedMetaObjects.length > 0) {
                        for (int i = 0; i < includedMetaObjects.length; i++) {
                            final String metaObjectName = includedMetaObjects[i];
                            if (metaclassURI.equals(metaObjectName)) {
                                success = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return success;
    }
}
