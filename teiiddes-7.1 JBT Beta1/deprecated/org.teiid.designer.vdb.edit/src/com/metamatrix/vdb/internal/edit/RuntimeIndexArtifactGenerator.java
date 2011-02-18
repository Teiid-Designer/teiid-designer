/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.index.IIndex;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.vdb.edit.VdbArtifactGenerator;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbGenerationContext;

/**
 * @since 4.2
 */
public class RuntimeIndexArtifactGenerator implements VdbArtifactGenerator {

    public static final String PATH_OF_INDEXES_IN_ARCHIVE = "runtime-inf/"; //$NON-NLS-1$

    public static final int GENERATE_FILE_NAMES_ERROR_CODE = 100501;
    public static final int GENERATE_INDEXER_CONTEXT_ERROR_CODE = 100502;
    public static final int GENERATE_INDEX_FILE_ERROR_CODE = 100503;
    public static final int INDEX_FILE_EXISTS_ERROR_CODE = 100504;

    /**
     * @since 5.0
     */
    public RuntimeIndexArtifactGenerator() {
        super();
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbArtifactGenerator#execute(com.metamatrix.vdb.edit.VdbGenerationContext)
     * @since 5.0
     */
    public void execute( final VdbGenerationContext theContext ) {
        ArgCheck.isNotNull(theContext);
        if (!(theContext instanceof InternalVdbGenerationContext)) {
            final String msg = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.InternalVdbGenerationContext_required"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        final InternalVdbGenerationContext context = (InternalVdbGenerationContext)theContext;

        // Set the monitor display message for this generator
        String displayMessage = "Generating runtime indexes ..."; //$NON-NLS-1$
        context.setProgressMessage(displayMessage);

        // --------------------------------------------------------------------------------------------------------
        // Create information needed by the indexer
        // --------------------------------------------------------------------------------------------------------

        // Create the parent folder into which the index files will be written
        final File parentFolder = new File(context.getTemporaryDirectory(), PATH_OF_INDEXES_IN_ARCHIVE);
        if (!parentFolder.exists()) {
            parentFolder.mkdir();
        }

        // Create the different index files ...
        final String[] indexNames = IndexConstants.INDEX_NAME.INDEX_NAMES;
        final File[] indexFilesArray = new File[indexNames.length];
        try {
            for (int i = 0; i < indexNames.length; ++i) {
                final String indexName = indexNames[i];
                indexFilesArray[i] = new File(parentFolder, indexName);
            }
        } catch (Throwable e) {
            final String msg = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Error_generating_index_file_names"); //$NON-NLS-1$
            context.addErrorMessage(msg, GENERATE_FILE_NAMES_ERROR_CODE, e);
            return;
        }

        // Create a list of model resources to index ...
        final Resource[] eResources = context.getModels();
        final List resourcesToIndex = new ArrayList(Arrays.asList(eResources));

        // Add the manifest model
        resourcesToIndex.add(context.getVdbContext().getManifestResource());

        // Add global/shared resources to the list of models to index
        addGlobalResourcesToList(context, resourcesToIndex);

        // Sort resources by name. This may make sorting the index records more efficient
        // since most records have fullname as the first field
        Collections.sort(resourcesToIndex, new ResourceNameComparator());

        // --------------------------------------------------------------------------------------------------------
        // Generate the runtime index files
        // --------------------------------------------------------------------------------------------------------

        // Create the IndexingContext used in the generation of runtime metadata
        IndexingContext indexingContext = null;
        try {
            indexingContext = createIndexingContext(context, resourcesToIndex);
        } catch (Throwable e) {
            final String msg = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Error_creating_indexing_context"); //$NON-NLS-1$
            context.addErrorMessage(msg, GENERATE_INDEXER_CONTEXT_ERROR_CODE, e);
            return;
        }

        // Create a map of model resource URI to its relative path in the VDB - required argument to VdbDocumentImpl
        final Map pathsByResourceUri = new HashMap();
        for (Iterator i = resourcesToIndex.iterator(); i.hasNext();) {
            final Resource model = (Resource)i.next();
            final String path = context.getModelHelper().getPath(model);
            if (!StringUtil.isEmpty(path)) {
                pathsByResourceUri.put(model.getURI(), createNormalizedPath(path).toString());
            }
        }

        // Generate the indexes ...
        IIndexer indexer = null;
        try {
            indexer = (indexingContext != null ? new VdbIndexer(indexingContext) : new VdbIndexer());
            for (int i = 0; i < indexFilesArray.length; ++i) {
                final File indexFile = indexFilesArray[i];
                displayMessage = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Generating_runtime_indexes", indexFile.getName()); //$NON-NLS-1$
                context.setProgressMessage(displayMessage);
                try {
                    produceIndex(indexer, resourcesToIndex, indexFile, pathsByResourceUri);
                } catch (Throwable e) {
                    final String msg = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Error_generating_runtime_index_file", indexFile.getName()); //$NON-NLS-1$
                    context.addErrorMessage(msg, GENERATE_INDEX_FILE_ERROR_CODE, e);
                }
            }
        } catch (Throwable e) {
            final String msg = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Error_generating_runtime_index_files"); //$NON-NLS-1$
            context.addErrorMessage(msg, GENERATE_INDEX_FILE_ERROR_CODE, e);
        } finally {
            if (indexingContext != null) {
                indexingContext.clearState();
            }
            resourcesToIndex.clear();
            indexer = null;
        }

        // --------------------------------------------------------------------------------------------------------
        // Add runtime index file artifacts to the VDB
        // --------------------------------------------------------------------------------------------------------

        for (int i = 0; i < indexFilesArray.length; ++i) {
            final File indexFile = indexFilesArray[i];
            final String pathInVdb = PATH_OF_INDEXES_IN_ARCHIVE + indexFile.getName();
            displayMessage = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Error_adding_runtime_index_file_to_generation_context", indexFile.getName()); //$NON-NLS-1$
            context.setProgressMessage(displayMessage);
            boolean success = context.addGeneratedArtifact(pathInVdb, indexFile);
            if (!success) {
                final String msg = VdbEditPlugin.Util.getString("RuntimeIndexArtifactGenerator.Error_artifact_already_exists", pathInVdb); //$NON-NLS-1$
                context.addWarningMessage(msg, INDEX_FILE_EXISTS_ERROR_CODE);
            }
        }
    }

    protected IndexingContext createIndexingContext( final InternalVdbGenerationContext context,
                                                     final List eResources ) {

        // Create the IndexingContext used in the generation of runtime metadata
        IndexingContext indexingContext = new IndexingContext();

        // Create the collection of resources to be used within the IndexContext. The "resources
        // in context" collection provides the scope of EMF resources used by SqlAspects when
        // resolving UUIDs for index records
        Collection resourcesInScope = new ArrayList(eResources);

        // Add the system resources to the scope if they are in the workspace
        Resource[] systemVdbResources = ModelerCore.getSystemVdbResources();
        List systemUuids = new ArrayList(systemVdbResources.length);
        for (int i = 0; i < systemVdbResources.length; i++) {
            ObjectID uuid = ((EmfResource)systemVdbResources[i]).getUuid();
            if (uuid != null) {
                systemUuids.add(uuid);
            }
        }
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            Resource r = (Resource)i.next();
            if (r instanceof EmfResource) {
                ObjectID uuid = ((EmfResource)r).getUuid();
                if (uuid != null && systemUuids.contains(uuid) && !resourcesInScope.contains(r)) {
                    resourcesInScope.add(r);
                }
            }
        }
        indexingContext.setResourcesInContext(resourcesInScope);

        // Add the materialized view virtual->physical table mappings if they exist
        final Map virtToPhysMappings = (Map)context.getData(InternalVdbGenerationContext.MATERIALIZED_VIEW_TABLE_MAPPINGS);
        if (virtToPhysMappings != null && !virtToPhysMappings.isEmpty()) {
            for (Iterator i = virtToPhysMappings.entrySet().iterator(); i.hasNext();) {
                final Map.Entry entry = (Map.Entry)i.next();
                final EObject virtualTable = (EObject)entry.getKey();
                final Collection physicalTables = (Collection)entry.getValue();
                indexingContext.addMaterializedTables(virtualTable, physicalTables);
            }
        }

        return indexingContext;

    }

    protected void produceIndex( final IIndexer indexer,
                                 final List eResources,
                                 final File indexFile,
                                 final Map pathsByResourceUri ) throws IOException {

        IIndex runtimeIndex = null;
        if (IndexUtil.indexFileExists(indexFile.getAbsolutePath())) {
            runtimeIndex = new Index(indexFile.getAbsolutePath(), true);
        } else {
            runtimeIndex = new Index(indexFile.getAbsolutePath(), false);
        }
        final VdbDocumentImpl document = new VdbDocumentImpl(indexFile.getName(), eResources, pathsByResourceUri);

        runtimeIndex.add(document, indexer);
        runtimeIndex.save();
    }

    protected IPath createNormalizedPath( final String pathInArchive ) {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        return new Path(pathInArchive).makeAbsolute();
    }

    /**
     * Add a reference to the built-in datatypes resource, referenced by the URI
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" to the specified list
     */
    protected void addGlobalResourcesToList( final InternalVdbGenerationContext context,
                                             final List eResources ) {
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            Resource r = (Resource)i.next();
            if (DatatypeConstants.DATATYPES_MODEL_FILE_NAME.equals(r.getURI().lastSegment())) {
                return;
            }
        }
        URI uri = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
        Resource mmTypes = context.getResourceSet().getResource(uri, true);
        if (mmTypes != null && !eResources.contains(mmTypes)) {
            eResources.add(mmTypes);
        }
    }

    static class ResourceNameComparator implements Comparator {

        public int compare( Object obj1,
                            Object obj2 ) {
            if (obj1 == null && obj2 == null) {
                return 0;
            } else if (obj1 == null && obj2 != null) {
                return -1;
            } else if (obj1 != null && obj2 == null) {
                return 1;
            }
            Resource r1 = (Resource)obj1;
            Resource r2 = (Resource)obj2;
            String value1 = r1.getURI().lastSegment();
            String value2 = r2.getURI().lastSegment();
            return value1.compareToIgnoreCase(value2);
        }
    }

}
