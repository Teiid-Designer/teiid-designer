/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipTypeAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.search.runtime.AnnotatedObjectRecord;
import com.metamatrix.modeler.core.search.runtime.ReferencesRecord;
import com.metamatrix.modeler.core.search.runtime.RelatedObjectRecord;
import com.metamatrix.modeler.core.search.runtime.RelationshipRecord;
import com.metamatrix.modeler.core.search.runtime.RelationshipTypeRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceImportRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.core.search.runtime.ResourceRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.core.search.runtime.TypedObjectRecord;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;

/**
 * SearchRuntimeAdapter
 */
public class SearchRuntimeAdapter extends RuntimeAdapter {

	public static void addObjectSearchWords(final EObject eObject, final String modelPath, final List wordEntries) {

	    // add eObject search words
	    addEObjectSearchWords(eObject, modelPath, wordEntries);

        // add typed eObject search words
        addTypedObjectSearchWords(eObject, modelPath, wordEntries);

        // add annotated eObject search words
        addAnnotatedObjectSearchWords(eObject, modelPath, wordEntries);

		// add words for model imports references
		addModelImportsSearchWords(eObject, modelPath, wordEntries);

		// add words for uni directional references
		addUniDirectionalReferencesSearchWords(eObject, wordEntries);

		// add any relationship search words
		addRelationshipIndexWords(eObject, modelPath, wordEntries);
	}

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry}
     * instance representing a "typed" EObject.  A "typed" EObject is one
     * that references a datatype such as a Relational.Column or a
     * XSDSimpleTypeDefinition (references a basetype).
     * This resulting WordEntry is of the form:
     * recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
     */
	public static void addTypedObjectSearchWords(final EObject eObject, final String modelPath, final Collection wordEntries) {

        if (eObject == null) {
            return;
        }
        final EClass eClass = eObject.eClass();
        final ModelEditor editor = ModelerCore.getModelEditor();
        for (final Iterator iter = eClass.getEReferences().iterator(); iter.hasNext();) {
            final EReference eRef = (EReference)iter.next();

            // If this is the datatype feature, create a search index for it
            if (editor.isDatatypeFeature(eObject, eRef)) {

                // Get the referenced datatype ...
                final Object value = eObject.eGet(eRef);
                if (value == null) {
                    continue;
                }
                if (value instanceof EObject) {
                    final EObject datatype = (EObject)value;
                    final StringBuffer sb  = new StringBuffer(getIniitalBufferSize());
                    final String objectID  = createTypedObjectSearchWord(eObject, datatype, modelPath, sb);

                    addNewWordEntryToList(objectID, sb, wordEntries);

                } else if (value instanceof List) {
                    final Object[] values = ((List)value).toArray();
                    for (int i = 0; i != values.length; ++i) {
                        Object obj = values[i];
                        if (obj instanceof EObject) {
                            final EObject datatype = (EObject)obj;
                            final StringBuffer sb  = new StringBuffer(getIniitalBufferSize());
                            final String objectID  = createTypedObjectSearchWord(eObject, datatype, modelPath, sb);

                            addNewWordEntryToList(objectID, sb, wordEntries);
                        }
                    }
                }
            }
        }
	}

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry}
     * instance representing a typed EObject.  This resulting WordEntry is of the form:
     * recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
     */
    private static String createTypedObjectSearchWord(final EObject eObject, final EObject datatype,
                                                      final String modelPath, final StringBuffer sb) {

        final ModelEditor editor = ModelerCore.getModelEditor();

        sb.append(IndexConstants.SEARCH_RECORD_TYPE.TYPED_OBJECT);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the object UUID
        String objectID = getObjectIdString(eObject);

        appendID(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the name of the eObject
        SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
        String name = (sqlAspect != null ? sqlAspect.getName(eObject) : editor.getName(eObject));
        appendObject(name, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the fullname of the eObject
        String fullname = (sqlAspect != null ? sqlAspect.getFullName(eObject) : name);
        appendObject(fullname, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the uri of the eObject
        Object uri = editor.getUri(eObject);
        appendObject(uri, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the referenced datatype name
        SqlDatatypeAspect dtSqlAspect = (SqlDatatypeAspect) AspectManager.getSqlAspect(datatype);
        name = (dtSqlAspect != null ? dtSqlAspect.getName(datatype) : editor.getName(datatype));
        appendObject(name, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the referenced datatype ID
        objectID = (dtSqlAspect != null ? dtSqlAspect.getUuidString(datatype) : getObjectIdString(datatype));

        appendID(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the referenced datatype's runtime type
        String runtimeType = (dtSqlAspect != null ? dtSqlAspect.getRuntimeTypeName(datatype) : null);
        appendObject(runtimeType, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get path to the model path
        appendObject(modelPath, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the meta-class URI
        appendURI(eObject.eClass(), sb, true);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        return objectID;
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry}
     * instance representing an EObject.  This resulting WordEntry is of the form:
     * recordType|objectID|upperName|name|fullname|uri|modelPath|metaclassURI|
     */
    public static void addEObjectSearchWords(final EObject eObject, final String modelPath, final Collection wordEntries) {

        // get objectID
        String objectID = getObjectIdString(eObject);

        // look up sql aspect
        SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);

        // Get the name of the eObject
        String name = (sqlAspect != null ? sqlAspect.getName(eObject) : ModelerCore.getModelEditor().getName(eObject));

        // Get the fullname of the eObject
        String fullname = (sqlAspect != null ? sqlAspect.getFullName(eObject) : null);

        // Get the uri of the eObject
        URI uri = ModelerCore.getModelEditor().getUri(eObject);

        // Get the meta-class URI
        EClass metaClass = eObject.eClass();

        // Create the search word and add it to the collection of entries
        addEObjectSearchWords(objectID, name, fullname, uri, modelPath, metaClass, wordEntries);

        // If the EObject represents a XSDSimpleTypeDefinition then we need to be able to
        // search for it using either a UUID (if one exists) or the URI fragment.  If an
        // index record has already been added for the UUID then add a second one for the
        // URI fragment.
        if (eObject instanceof XSDSimpleTypeDefinition && objectID.startsWith(UUID.PROTOCOL)) {
            if(eObject.eIsProxy()) {
                URI proxyURI = EcoreUtil.getURI(eObject);
                if(proxyURI != null) {
                    objectID = proxyURI.fragment();
                }
            } else {
                objectID = eObject.eResource().getURIFragment(eObject);
            }
            addEObjectSearchWords(objectID, name, fullname, uri, modelPath, metaClass, wordEntries);
        }

    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry}
     * instance representing an EObject.  This resulting WordEntry is of the form:
     * recordType|objectID|upperName|name|fullname|uri|modelPath|metaclassURI|
     */
    public static void addEObjectSearchWords(final String objectID, final String name, final String fullname,
                                             final URI uri, final String modelPath, final EClass metaClass,
                                             final Collection wordEntries) {

        // Construct a string containing the EObject information
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

        sb.append(IndexConstants.SEARCH_RECORD_TYPE.OBJECT);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the object UUID
        appendID(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the upper name of the eObject
        String upperName = (name != null ? name.toUpperCase() : null);
        appendObject(upperName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the name of the eObject
        appendObject(name, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the fullname of the eObject
        appendObject(fullname, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get the uri of the eObject
        appendObject(uri, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // get path to the model
        appendObject(modelPath, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the meta-class URI
        appendURI(metaClass, sb, true);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry}
     * instance representing an annotated EObject.  This resulting WordEntry is of the form:
     * recordType|objectID|name|fullname|uri|tags|description|modelPath|metaclassURI|
     */
    public static void addAnnotatedObjectSearchWords(final EObject eObject, final String modelPath, final Collection wordEntries) {

        final SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
        if (sqlAspect != null && sqlAspect instanceof SqlAnnotationAspect) {
            SqlAnnotationAspect sqlAnnotationAspect = (SqlAnnotationAspect) sqlAspect;

            // Construct a string containing the EObject information
            final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

            sb.append(IndexConstants.SEARCH_RECORD_TYPE.ANNOTATION);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // Append the object UUID
            String objectID = getObjectIdString(sqlAnnotationAspect.getObjectID(eObject));

            appendID(objectID, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // get the name of the eObject
            String name = sqlAnnotationAspect.getName(eObject);
            appendObject(name, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // get the fullname of the eObject
            String fullname = sqlAnnotationAspect.getFullName(eObject);
            appendObject(fullname, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // get the uri of the eObject
            Object uri = sqlAnnotationAspect.getURI(eObject);
            appendObject(uri, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // get the tags
            Map tags = sqlAnnotationAspect.getTags(eObject);
            appendStrings(tags,IndexConstants.RECORD_STRING.LIST_DELIMITER,sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // Append the upper case form of the description
            String description = sqlAnnotationAspect.getDescription(eObject);
            if (description != null) {
                description = description.toUpperCase();
            }
            appendObject(description,sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // get path to the model
            appendObject(modelPath, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            // Append the meta-class URI (this requires the eClass of the annotated object)
            appendURI(sqlAnnotationAspect.getMetaclassURI(eObject), sb, false);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

            addNewWordEntryToList(objectID, sb, wordEntries);
        }
    }

	public static void addUniDirectionalReferencesSearchWords(final EObject eObject, final Collection wordEntries) {

		// Append the meta-class URI
		final EClass eClass = eObject.eClass();
        final Collection references = eClass.getEAllReferences();
		for(final Iterator refIter = references.iterator();refIter.hasNext();) {
			// find the reference feature
			final EReference reference = (EReference) refIter.next();

            // Skip containment references ...
            if ( reference.isContainment() ) {
                continue;
            }

            // -----------------------------------------------------------------------------------
            // Currently (in 4.1) we are only using this information to determine references
            // for refactoring.  Therefore, we do want NO volatile EReferences and
            // NO changeable=false EReferences.
            //
            // For example, some XSD and UML EReferences are computed/volatile/subsetted, and
            // thus there are problems performing this indexing.
            //
            // RMH 4/21/04
            // -----------------------------------------------------------------------------------
            if ( reference.isVolatile() || !reference.isChangeable() ) {
                continue;
            }

			// Skip bi-directional references (since you can follow the reverse) ...
			if ( reference.getEOpposite() != null ) {
				continue;
			}

			// get the referenced value
			Object value = eObject.eGet(reference);
            if ( value == null ) {
                continue;
            }

			// find eObjects in the referenced object
            if( reference.isMany() ) {
                for(Iterator valIter = ((EList) value).iterator();valIter.hasNext();) {
					addUniDirectionalReferenceWord(eObject, (EObject)valIter.next(), wordEntries);
                }
            } else {
				addUniDirectionalReferenceWord(eObject, (EObject)value, wordEntries);
			}
		}
	}

	public static void addUniDirectionalReferenceWord(final EObject eObject, final EObject referencedObj, final Collection wordEntries) {

        //Added safety check sz
        if( referencedObj == null || referencedObj.eResource() == null ) return;

		// Construct a string containing the EObject information
		final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

		// record header
		sb.append(IndexConstants.SEARCH_RECORD_TYPE.OBJECT_REF);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append the referenced object UUID
		final String objectID = getObjectIdString(referencedObj);
		appendID(objectID, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append the object UUID
		appendID(eObject, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		addNewWordEntryToList(objectID, sb, wordEntries);
	}

	public static void addResourceSearchWords(final Object resourceUUID, final IPath resourcePath, final Object resourceURI,
                                              final String metamodelURI, final String modelType, final Collection wordEntries) {
	    if (resourceUUID == null) {
	        return;
        }

		// Construct a string containing the search index info
		final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

		// record header
		sb.append(IndexConstants.SEARCH_RECORD_TYPE.RESOURCE);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append uuid
		appendObject(resourceUUID,sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append path
		appendObject(resourcePath,sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append the resource URI
		appendObject(resourceURI,sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append primary metamodel URI
		appendObject(metamodelURI,sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// Append modelType
		appendObject(modelType,sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		addNewWordEntryToList(resourceUUID.toString(), sb, wordEntries);
	}

	public static void addModelImportsSearchWords(final EObject eObject, final String modelPath, final Collection wordEntries) {

		ImportsAspect importAspect = AspectManager.getModelImportsAspect(eObject);
		if (importAspect == null) {
		    return;
		}
		IPath path = importAspect.getModelPath(eObject);
		if(path == null) {
			return;
		}

		// Construct a string containing the search index info
		final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

		// record type
		sb.append(IndexConstants.SEARCH_RECORD_TYPE.MODEL_IMPORT);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object path of the imported model
		sb.append(path);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object UUID of the importing model
		String resourceUUID = importAspect.getModelUuid(eObject);
		// incase of non-xsds importing xsds
		if(resourceUUID == null && eObject instanceof ModelImport) {
			ModelResource mdlResource = ModelerCore.getModelEditor().findModelResource((ModelImport)eObject);
			if(mdlResource != null) {
				try {
					resourceUUID = mdlResource.getUuid();
				} catch(Exception e) {
					ModelerCore.Util.log(e);
				}
			}
		}
		appendObject(resourceUUID, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object path of the importing model
		appendObject(modelPath, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		addNewWordEntryToList(resourceUUID, sb, wordEntries);
	}

	/**
	 * Create the {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s)
	 * to be used as the index file record(s) for this EObject instance.  The word entries
	 * are added to the list provided by the calling method.
	 * @param eObject
	 * @param modelPath Path to the relationship model
	 * @param wordEntries the list to which WordEntry instances are added
	 */
	public static void addRelationshipIndexWords(final Object eObject, final String modelPath, final Collection wordEntries) {
		RelationshipMetamodelAspect relAspect = AspectManager.getRelationshipAspect((EObject)eObject);
		if (relAspect == null) {
			return;
		}

		// collect word entries for all related objects
		if (relAspect.isRecordType(IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT)) {
			addRelatedObjectWords((RelationshipAspect)relAspect, (EObject)eObject, wordEntries);
		}
		// collect word entries for all relationships
		if (relAspect.isRecordType(IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP)) {
			addRelationshipWord((RelationshipAspect)relAspect, modelPath, (EObject)eObject, wordEntries);
		}
		// collect word entries for all relationship types
		if (relAspect.isRecordType(IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE)) {
			addRelationshipTypeWord((RelationshipTypeAspect)relAspect, (EObject)eObject, wordEntries);
		}
	}

	/**
	 * Create {@link com.metamatrix.internal.core.index.impl.WordEntry} instances
	 * for all related objects in a given relationship.  This resulting <code>WordEntry</code>s
	 * are of the form:
	 * ObjectID|relationshipTypeID|relationshipName|relationshipTypeName|relationshipUri|comment
	 * @param relationAspect The <code>RelationshipAspect</code>
	 * @param modelPath Path to the relationship model
	 * @param eObject The <code>EObject</code> of the relationship object
	 * @param wordEntries The collection of wordEntries to which this word is added
	 */
	public static void addRelationshipWord(final RelationshipAspect relationAspect, final String modelPath, final EObject eObject, final Collection wordEntries) {
		// Construct a string containing the search index info
		final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

		// record type
		sb.append(IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object UUID of the relationship
		Object objectID = relationAspect.getObjectID(eObject);
		appendID(objectID, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// name of the relationship
		String name = relationAspect.getName(eObject);
		String upperName = name != null ? name.toUpperCase() : null;

		// name of the relationship
		appendID(name, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// upper name
		appendID(upperName, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object UUID of relationship type
		appendID(relationAspect.getType(eObject), sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// name of relationship type
		appendID(relationAspect.getTypeName(eObject), sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// relationship uri
		URI uri = eObject != null ? EcoreUtil.getURI(eObject) : null;
		appendObject(uri, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// path to the relationship model
		appendID(modelPath, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		String stringID = objectID != null ? objectID.toString() : ""+IndexConstants.RECORD_STRING.SPACE; //$NON-NLS-1$
		addNewWordEntryToList(stringID, sb, wordEntries);
	}

	/**
	 * Create {@link com.metamatrix.internal.core.index.impl.WordEntry} instances
	 * for all related objects in a given relationship.  This resulting <code>WordEntry</code>s
	 * are of the form:
	 * relationshipTypeName|superTypeID|ObjectID|sourceName|targetRoleName|relationshipUri
	 * @param relationAspect The <code>RelationshipAspect</code>
	 * @param eObject The <code>EObject</code> of the relationship object
	 * @param wordEntries The collection of wordEntries to which this word is added
	 */
	public static void addRelationshipTypeWord(final RelationshipTypeAspect relationAspect, final EObject eObject,
                                               final Collection wordEntries) {

		// Construct a string containing the search index info
		final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

		// record type
		sb.append(IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// name of the relationship type
		appendID(relationAspect.getName(eObject), sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object UUID of the relationship super type
		appendID(relationAspect.getSuperType(eObject), sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object UUID of the relationship type
		Object objectID = relationAspect.getObjectID(eObject);
		appendID(objectID, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// name of the relationship source role
		appendID(relationAspect.getSourceRoleName(eObject), sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// name of the relationship target role
		appendID(relationAspect.getTargetRoleName(eObject), sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// relationship type uri
		URI uri = eObject != null ? EcoreUtil.getURI(eObject) : null;
		appendObject(uri, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		String relationTypeID = objectID != null ? objectID.toString() : ""+IndexConstants.RECORD_STRING.SPACE; //$NON-NLS-1$
		addNewWordEntryToList(relationTypeID, sb, wordEntries);
	}

	/**
	 * Create {@link com.metamatrix.internal.core.index.impl.WordEntry} instances
	 * for all related objects in a given relationship. One entry is made for each of
	 * the sourceObjects in the relationship to each of its targets and vice versa.
	 * This resulting <code>WordEntry</code>s are of the form:
	 * ObjectID|relationshipObjectID|relatedObjID|ObjectMetaclassName|relatedObjmetaclassName|isSource|modelPathofObject|modelPathofRelatedObject
	 * @param relationAspect The <code>RelationshipAspect</code>
	 * @param relObject The <code>EObject</code> of the relationship object
	 * @param wordEntries The collection of wordEntries to which this word is added
	 */
	public static void addRelatedObjectWords(final RelationshipAspect relationAspect, final EObject relObject,
                                             final Collection wordEntries) {

		// iterate over the sources to the relationship
		Iterator srcIter = relationAspect.getSources(relObject).iterator();
		while(srcIter.hasNext()) {
			Object srcObj = srcIter.next();
			// iterate over the targets to the relationship
			Iterator tgtIter = relationAspect.getTargets(relObject).iterator();
			while(tgtIter.hasNext()) {
				Object tgtObj = tgtIter.next();
				// add a word for the relationship of the source to its target
				addRelatedObjectWord(srcObj, tgtObj, relationAspect, relObject, true, wordEntries);
				// add a word for the relationship of the target to its source
				addRelatedObjectWord(tgtObj, srcObj, relationAspect, relObject, false, wordEntries);
			}
		}
	}

	/**
	 * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry}
	 * instance representing a related object.  This resulting WordEntry is of the form:
	 * ObjectID|relationshipObjectID|relatedObjID|ObjectMetaclassUri|relatedObjmetaclassUri|isSource|uriofObject|uriofRelatedObject
	 * @param eObject The <code>EObject</code> of the entity involved in the relationship
	 * @param relatedObj The <code>EObject</code> of the related object
	 * @param relObject The <code>EObject</code> of the relationship object
	 * @param isSource boolean indicating if this object is involved as a source in the relationship
	 * @param wordEntries The collection of wordEntries to which this word is added
	 */
	public static void addRelatedObjectWord(Object eObject, Object relatedObj, RelationshipAspect relationAspect,
                                            EObject relationObj, boolean isSource, final Collection wordEntries) {
		// Construct a string containing the search index info
		final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

		// record type
		sb.append(IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object UUID
		appendID(eObject, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// related object UUID
		String relatedObjectID = getObjectIdString(relatedObj);
		appendID(relatedObj, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// relationship UUID
		appendID(relationObj, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// boolean indicating if the object is involved as a source
		// in the relationship
		appendBoolean(isSource, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// related object metaclass URI
		String metaClassURI = null;
		if(relatedObj instanceof EObject) {
			metaClassURI = ModelerCore.getMetamodelRegistry().getMetaClassURI(((EObject) eObject).eClass());
		}
		appendObject(metaClassURI, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// related object metaclass URI
		String relatedClassURI = null;
		if(relatedObj instanceof EObject) {
			relatedClassURI = ModelerCore.getMetamodelRegistry().getMetaClassURI(((EObject) relatedObj).eClass());
		}
		appendObject(relatedClassURI, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		String sourceRoleName = relationAspect.getSourceRoleName(relationObj);
		String targetRoleName = relationAspect.getTargetRoleName(relationObj);
		if(isSource) {
			// get the source role name for this relationship
			appendObject(sourceRoleName, sb);
			sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

			// get the target role name for this relationship
			appendObject(targetRoleName, sb);
			sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
		} else {
			// get the target role name for this relationship
			appendObject(targetRoleName, sb);
			sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

			// get the source role name for this relationship
			appendObject(sourceRoleName, sb);
			sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
		}

		// object name
		String name = eObject instanceof EObject ? getName((EObject)eObject) : null;
		appendObject(name, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// related object name
		String relatedName = eObject instanceof EObject ? getName((EObject)relatedObj) : null;
		appendObject(relatedName, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// object uri
		URI uri = eObject instanceof EObject ? EcoreUtil.getURI((EObject)eObject) : null;
		appendObject(uri, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// related object uri
		URI relatedUri = eObject instanceof EObject ? EcoreUtil.getURI((EObject)relatedObj) : null;
		appendObject(relatedUri, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// resourcePath
		ModelEditor editor = ModelerCore.getModelEditor();
		ModelResource modelRsc = eObject instanceof EObject ? editor.findModelResource((EObject)eObject) : null;
		String resourcePath = null;
		if(modelRsc != null) {
			resourcePath = modelRsc.getPath().toString();
		}
		appendObject(resourcePath, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		// related resourcePath
		ModelResource relatedModelRsc = eObject instanceof EObject ? editor.findModelResource((EObject)relatedObj) : null;
		String relatedResourcePath = null;
		if(relatedModelRsc != null) {
			relatedResourcePath = relatedModelRsc.getPath().toString();
		}
		appendObject(relatedResourcePath, sb);
		sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

		addNewWordEntryToList(relatedObjectID, sb, wordEntries);
	}

	private static String getName(EObject eObject) {
		final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
		if(adapterFactory != null) {
			final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(eObject,IItemLabelProvider.class);
			if(provider != null) {
				return provider.getText(eObject);
			}
		}

		return null;
	}

	/**
	 * Return the {@link com.metamatrix.modeler.relationship.search.index.SearchRecord}
	 * instances for specified IEntryResult.
	 * @param record The cgar array that is a search record
	 * @return The SearchRecord
	 */
	public static SearchRecord getSearchRecord(final char[] record) {
		if (record == null || record.length == 0) {
			return null;
		}
		switch (record[0]) {
			case IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP: return createRelationshipRecord(record);
			case IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT: return createRelatedObjectRecord(record);
			case IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE: return createRelationshipTypeRecord(record);
			case IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_ROLE: return null;
			case IndexConstants.SEARCH_RECORD_TYPE.RESOURCE: return createResourceRecord(record);
			case IndexConstants.SEARCH_RECORD_TYPE.OBJECT: return createResourceObjectRecord(record);
			case IndexConstants.SEARCH_RECORD_TYPE.OBJECT_REF: return createResourceObjRefRecord(record);
			case IndexConstants.SEARCH_RECORD_TYPE.MODEL_IMPORT: return createResourceImportRecord(record);
            case IndexConstants.SEARCH_RECORD_TYPE.TYPED_OBJECT: return createTypedObjectRecord(record);
            case IndexConstants.SEARCH_RECORD_TYPE.ANNOTATION: return createAnnotatedObjectRecord(record);
			default:
				throw new IllegalArgumentException(ModelerCore.Util.getString("RelationshipRuntimeAdapter.Invalid_relationship_search_record_type_{0}_for_creating_RelationshipSearchRecord._1", record[0])); //$NON-NLS-1$
		}
	}

	/**
	 * Return the {@link com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord}
	 * instances for specified record.  This specified char[] record is of the form:
     * recordType|objectID|upperName|name|fullname|uri|modelPath|metaclassURI|
	 * @param record The char array that is a search record
	 * @return The ResourceImportRecord
	 */
	public static ResourceObjectRecord createResourceObjectRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final ResourceObjectRecordImpl objRecord = new ResourceObjectRecordImpl();

		// read each token and set it on the record
		int tokenIndex = 1;

		// The next token is the UUID of the object
		objRecord.setUUID((String)tokens.get(tokenIndex++));

        // The next token is the upper name of the object
        objRecord.setName((String)tokens.get(tokenIndex++));

		// The next token is the name of the object
		objRecord.setName((String)tokens.get(tokenIndex++));

        // The next token is the fullname of the object
        objRecord.setFullname((String)tokens.get(tokenIndex++));

		// The next token is the URI of the object
		objRecord.setObjectURI((String)tokens.get(tokenIndex++));

		// The next token is the resource path
		objRecord.setResourcePath((String)tokens.get(tokenIndex++));

		// The next token is the URI of the objects metaclass
		objRecord.setMetaclassURI((String)tokens.get(tokenIndex++));

		return objRecord;
	}

    /**
     * Return the {@link com.metamatrix.modeler.core.search.runtime.TypedObjectRecord}
     * instances for specified record.
     * This resulting WordEntry is of the form:
     * recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
     * @param record The char array that is a search record
     * @return The TypedObjectRecord
     */
    public static TypedObjectRecord createTypedObjectRecord(final char[] record) {
        final String str = new String(record);
        final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
        final TypedObjectRecordImpl objRecord = new TypedObjectRecordImpl();

        // read each token and set it on the record
        int tokenIndex = 1;

        // The next token is the UUID of the object
        objRecord.setUUID((String)tokens.get(tokenIndex++));

        // The next token is the name of the object
        objRecord.setName((String)tokens.get(tokenIndex++));

        // The next token is the fullname of the object
        objRecord.setFullname((String)tokens.get(tokenIndex++));

        // The next token is the URI of the object
        objRecord.setObjectURI((String)tokens.get(tokenIndex++));

        // The next token is the name of the referenced datatype
        objRecord.setDatatypeName((String)tokens.get(tokenIndex++));

        // The next token is the name of the referenced datatype ID
        objRecord.setDatatypeID((String)tokens.get(tokenIndex++));

        // The next token is the name of the referenced datatype's runtime type
        objRecord.setRuntimeType((String)tokens.get(tokenIndex++));

        // The next token is the resource path
        objRecord.setResourcePath((String)tokens.get(tokenIndex++));

        // The next token is the URI of the objects metaclass
        objRecord.setMetaclassURI((String)tokens.get(tokenIndex++));

        return objRecord;
    }

    /**
     * Return the {@link com.metamatrix.modeler.core.search.runtime.AnnotatedObjectRecord}
     * instances for specified record.
     * This resulting WordEntry is of the form:
     * recordType|objectID|name|uri|tags|description|modelPath|metaclassURI|
     * @param record The char array that is a search record
     * @return The AnnotatedObjectRecord
     */
    public static AnnotatedObjectRecord createAnnotatedObjectRecord(final char[] record) {
        final String str = new String(record);
        final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
        final AnnotatedObjectRecordImpl annotRecord = new AnnotatedObjectRecordImpl();

        // read each token and set it on the record
        int tokenIndex = 1;

        // The next token is the UUID of the object
        annotRecord.setUUID((String)tokens.get(tokenIndex++));

        // The next token is the name of the object
        annotRecord.setName((String)tokens.get(tokenIndex++));

        // The next token is the fullname of the object
        annotRecord.setFullname((String)tokens.get(tokenIndex++));

        // The next token is the URI of the object
        annotRecord.setObjectURI((String)tokens.get(tokenIndex++));

        // The next token are the properties
        annotRecord.setProperties( getProperties((String)tokens.get(tokenIndex++), IndexConstants.RECORD_STRING.LIST_DELIMITER, IndexConstants.RECORD_STRING.PROP_DELIMITER) );

        // The next token is the description
        annotRecord.setDescription((String)tokens.get(tokenIndex++));

        // The next token is the resource path
        annotRecord.setResourcePath((String)tokens.get(tokenIndex++));

        // The next token is the URI of the objects metaclass
        annotRecord.setMetaclassURI((String)tokens.get(tokenIndex++));

        return annotRecord;
    }

	/**
	 * Return the {@link com.metamatrix.modeler.core.search.runtime.ReferencesRecord}
	 * instances for specified record.
	 * @param record The char array that is a search record
	 * @return The ReferencesRecord
	 */
	public static ReferencesRecord createResourceObjRefRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final ReferencesRecordImpl refRecord = new ReferencesRecordImpl();

		// read each token and set it on the record
		int tokenIndex = 1;

		// The next token is the UUIDs of referenced object
		refRecord.setReferencedUUID((String)tokens.get(tokenIndex++));

		// The next token is the UUID of the resource obj
		refRecord.setUUID((String)tokens.get(tokenIndex++));

		return refRecord;
	}

    /**
     * Obtains the {@link ResourceObjectRecord} for the specified object.
     * @param theEObject the object whose record is being requested
     * @return the record
     * @test PdeTestSearch
     */
    public static ResourceObjectRecord createResourceObjectRecord(EObject theEObject) {
        ResourceObjectRecordImpl objRecord = new ResourceObjectRecordImpl();
        ModelEditor editor = ModelerCore.getModelEditor();

        objRecord.setUUID(ModelerCore.getObjectIdString(theEObject));
        objRecord.setObjectURI(editor.getUri(theEObject).toFileString());
        objRecord.setMetaclassURI(theEObject.eClass().getName());

        // look up info using the sql aspect if possible
        SqlAspect sqlAspect = AspectManager.getSqlAspect(theEObject);

        // name
        String name = (sqlAspect == null) ? editor.getName(theEObject)
                                          : sqlAspect.getName(theEObject);
        objRecord.setName(name);

        // full name
        String fullName = (sqlAspect == null ? null : sqlAspect.getFullName(theEObject));
        objRecord.setFullname(fullName);

        // path
        String path = (sqlAspect == null) ? editor.getModelRelativePathIncludingModel(theEObject).toString()
                                          : sqlAspect.getPath(theEObject).toString();
        objRecord.setResourcePath(path);

        return objRecord;
    }

	/**
	 * Return the {@link com.metamatrix.modeler.core.search.runtime.ResourceRecord}
	 * instances for specified record.
	 * @param record The char array that is a search record
	 * @return The ResourceRecord
	 */
	public static ResourceRecord createResourceRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final ResourceRecordImpl resourceRecord = new ResourceRecordImpl();

		// read each token and set it on the record
		int tokenIndex = 1;

		// The next token is the UUID of the resource
		resourceRecord.setUUID((String)tokens.get(tokenIndex++));

		// The next token is the path of the resource
		resourceRecord.setPath((String)tokens.get(tokenIndex++));

		// The next token is the uri of the resource
		resourceRecord.setURI((String)tokens.get(tokenIndex++));

		// The next token is the URI of the resource's metamodel
		resourceRecord.setMetamodelURI((String)tokens.get(tokenIndex++));

		// The next token is the model type of the resource
		resourceRecord.setModelType((String)tokens.get(tokenIndex++));

		return resourceRecord;
	}

	/**
	 * Return the {@link com.metamatrix.modeler.core.search.runtime.ResourceImportRecord}
	 * instances for specified record.
	 * @param record The char array that is a search record
	 * @return The ResourceImportRecord
	 */
	public static ResourceImportRecord createResourceImportRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final ResourceImportRecordImpl importRecord = new ResourceImportRecordImpl();

		// read each token and set it on the record
		int tokenIndex = 1;

		// The next token is the path of the imported resource
		importRecord.setImportedPath((String)tokens.get(tokenIndex++));

		// The next token is the UUID of the resource
		importRecord.setUUID((String)tokens.get(tokenIndex++));

		// The next token is the path of the resource
		importRecord.setPath((String)tokens.get(tokenIndex++));

		return importRecord;
	}

	/**
	 * Return the {@link com.metamatrix.modeler.relationship.search.index.RelationshipRecord}
	 * instances for specified record.
	 * @param record The char array that is a search record
	 * @return The RelationshipRecord
	 */
	public static RelationshipRecord createRelationshipRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final RelationshipRecordImpl relationRecord = new RelationshipRecordImpl();

		// read each token and set it on the record
		int tokenIndex = 1;

		// The next token is the UUID of the relationship
		relationRecord.setUUID((String)tokens.get(tokenIndex++));

		// The next token is the name of the relationship
		relationRecord.setName((String)tokens.get(tokenIndex++));

		// ignore upper name
		tokenIndex++;

		// The next token is the UUID for the relationship type
		relationRecord.setTypeUUID((String)tokens.get(tokenIndex++));

		// The next token is the name the relationship type
		relationRecord.setTypeName((String)tokens.get(tokenIndex++));

		// The next token is the name the relationship uri
		relationRecord.setUri((String)tokens.get(tokenIndex++));

		// The next token is the name the relationship resource path
		relationRecord.setResourcePath((String)tokens.get(tokenIndex++));

		return relationRecord;
	}

	/**
	 * Return the {@link com.metamatrix.modeler.relationship.search.index.RelationshipTypeRecord}
	 * instances for specified record.
	 * @param record The char array that is a search record
	 * @return The RelationshipTypeRecord
	 */
	public static RelationshipTypeRecord createRelationshipTypeRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final RelationshipTypeRecordImpl relationTypeRecord = new RelationshipTypeRecordImpl();

		// read each token and set it on the record
		int tokenIndex = 1;

		// The next token is the name of the relationship type
		relationTypeRecord.setName((String)tokens.get(tokenIndex++));

		// The next token is the UUID for the relationship super type
		relationTypeRecord.setSuperTypeUUID((String)tokens.get(tokenIndex++));

		// The next token is the UUID of the relationship type
		relationTypeRecord.setUUID((String)tokens.get(tokenIndex++));

		// The next token is the name the relationship source role name
		relationTypeRecord.setSourceRoleName((String)tokens.get(tokenIndex++));

		// The next token is the name the relationship target role name
		relationTypeRecord.setTargetRoleName((String)tokens.get(tokenIndex++));

		// The next token is the name the relationship uri
		relationTypeRecord.setUri((String)tokens.get(tokenIndex++));

		return relationTypeRecord;
	}

	/**
	 * Return the {@link com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord}
	 * instances for specified IEntryResult.
	 * @param record The char array that is a search record
	 * @return The RelatedObjectRecord
	 */
	public static RelatedObjectRecord createRelatedObjectRecord(final char[] record) {
		final String str = new String(record);
		final List tokens = CoreStringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
		final RelatedObjectRecordImpl relatedRecord = new RelatedObjectRecordImpl();

		// The tokens are the standard header values
		int tokenIndex = 1;

		// The next token is the UUID of the object whose related object is looked up
		relatedRecord.setUUID((String)tokens.get(tokenIndex++));

		// The next token is the UUIDs for the related object
		relatedRecord.setRelatedObjectUUID((String)tokens.get(tokenIndex++));

		// The next token is the UUID for the relationship
		relatedRecord.setRelationshipUUID((String)tokens.get(tokenIndex++));

		// Set the boolean flags
		char[] booleanValues = ((String)tokens.get(tokenIndex++)).toCharArray();
		// flag indicating if this is a sourceObject
		relatedRecord.setSourceObject(getBooleanValue(booleanValues[0]));

		// The next token is the object metaclass URI
		relatedRecord.setMetaClassUri((String)tokens.get(tokenIndex++));

		// The next token is the related object metaclass URI
		relatedRecord.setRelatedMetaClassUri((String)tokens.get(tokenIndex++));

		// The next token is the object role name
		relatedRecord.setRoleName((String)tokens.get(tokenIndex++));

		// The next token is the related object role name
		relatedRecord.setRelatedRoleName((String)tokens.get(tokenIndex++));

		// The next token is the name of this obj
		relatedRecord.setName((String)tokens.get(tokenIndex++));

		// The next token is the name of related object
		relatedRecord.setRelatedObjectName((String)tokens.get(tokenIndex++));

		// The next token is the uri of this obj
		relatedRecord.setUri((String)tokens.get(tokenIndex++));

		// The next token is the uri of related object
		relatedRecord.setRelatedObjectUri((String)tokens.get(tokenIndex++));

		// The next token is the path to this obj's resource
		relatedRecord.setResourcePath((String)tokens.get(tokenIndex++));

		// The next token is the path to related object's resource
		relatedRecord.setRelatedResourcePath((String)tokens.get(tokenIndex++));

		return relatedRecord;
	}

}
