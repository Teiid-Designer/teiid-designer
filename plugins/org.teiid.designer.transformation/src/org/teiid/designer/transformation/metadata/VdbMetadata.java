/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.Index;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * Metadata implementation used by VDB to validate models.
 * 
 * @since 8.0
 */
public class VdbMetadata extends ModelerMetadata {

    /**
     * @param context
     * @since 4.2
     */
    public VdbMetadata( QueryMetadataContext context,
                        Container container ) {
        super(context, container);
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Return all metadata records for the entity that matches the given entity name and is of the type specified by the record
     * type.
     * 
     * @param recordType
     * @param entityName the name to match
     * @param isPartialName true if the entity name is a partially qualified
     * @throws QueryMetadataException
     */
    @Override
    protected Collection findMetadataRecords( final char recordType,
                                              final String entityName,
                                              final boolean isPartialName ) throws QueryMetadataException {

        Collection eObjects = new LinkedList();

        String uuid = null;
        if (CoreStringUtil.startsWithIgnoreCase(entityName, UUID.PROTOCOL)) {
            uuid = entityName.toLowerCase();
        } else {
            String shortName = super.getShortElementName(entityName);
            if (CoreStringUtil.startsWithIgnoreCase(shortName, UUID.PROTOCOL)) {
                uuid = shortName.toLowerCase();
            }
        }
        // if it the element is a UUID
        if (uuid != null) {
            EObject eObj = lookupEObject(uuid);
            if (eObj != null) {
                // 12/31/03 (LLP) : fix for 10825. Prevent NPE when column has been deleted.
                if (eObj.eContainer() != null || eObj.eResource() != null) {
                    eObjects.add(eObj);
                }
            }
        }

        // no eObjects found, could be cause the name is a "user string" or Eobject for UUID could
        // not be found in any of open resources
        // check the System resources
        if (eObjects.isEmpty()) {

            Collection sysObjects = findSystemMetadataRecords(recordType, entityName, isPartialName);

            if (!sysObjects.isEmpty()) {
                return sysObjects;
            }

        }

        // no eObjects found, could be cause the name is a "user string" or Eobject for UUID could
        // not be found in any of open resources
        if (eObjects.isEmpty()) {
            // look up metadata in the index files
            IEntryResult[] results = queryIndex(recordType, entityName, isPartialName);
            Collection records = findMetadataRecords(results);

            if (CoreStringUtil.startsWithIgnoreCase(entityName, UUID.PROTOCOL)) {
                // Filter out ColumnRecord instances that do not match the specified uuid.
                // Due to the pattern matching used to query index files if an index record
                // matched the specified uuid string anywhere in that record it would be returned
                // in the results (for example, if the parent ObjectID in the index record
                // matched the specified uuid).
                filterMetadataRecordForUUID(entityName, records);
            }
            return records;
        }

        // find metadata records for the Eobjects collected
        if (!eObjects.isEmpty()) {
            Collection records = new ArrayList(eObjects.size());
            for (Iterator eObjIter = eObjects.iterator(); eObjIter.hasNext();) {
                MetadataRecord record = createMetadataRecord(recordType, (EObject)eObjIter.next());
                if (record != null) {
                    records.add(record);
                }
            }

            return records;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Find the EObject having the specified UUID using the ObjectManager for the lookup. If an EObject with this UUID cannot be
     * found then null is returned.
     */
    @Override
    protected EObject lookupEObject( final String uuid ) {
        CoreArgCheck.isNotEmpty(uuid);
        // Go to the Container ...
        return (EObject)this.getContainer().getEObjectFinder().find(uuid);
    }

    /**
     * Return the array of MtkIndex instances representing core indexes for the specified record type
     * 
     * @param recordType
     * @param selector
     * @return
     * @throws QueryMetadataException
     */
    @Override
    protected Index[] getIndexes( final char recordType,
                                  final IndexSelector selector ) throws QueryMetadataException {
        // The the index file name for the record type
        try {
            final String indexName = IndexUtil.getIndexFileNameForRecordType(recordType);
            return IndexUtil.getIndexes(indexName, selector);
        } catch (Exception e) {
            throw new QueryMetadataException(
                                                   e,
                                                   TransformationPlugin.Util.getString("TransformationMetadata.Error_trying_to_obtain_index_file_using_IndexSelector_1", selector)); //$NON-NLS-1$
        }
    }
}
