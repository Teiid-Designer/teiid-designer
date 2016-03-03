/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.metadata;

import java.beans.Expression;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.ScriptEngine;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.annotation.Updated;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.xml.IMappingNode;


/**
 * This interface defines the way that query components access metadata.  Any
 * user of a query component will need to implement this interface.  Many
 * of these methods take or return things of type "Object".  Typically, these 
 * objects represent a metadata-implementation-specific metadata ID.  
 * @param <F> the function library class
 * @param <S> the stored procedure info class
 * @param <Q> the query node class
 * @param <M> the mapping node class
 */
public interface IQueryMetadataInterface<F extends IFunctionLibrary, 
                                                                      S extends IStoredProcedureInfo, 
                                                                      Q extends IQueryNode, 
                                                                      M extends IMappingNode> {

    /**
     * Unknown cardinality.
     */
    int UNKNOWN_CARDINALITY = -1;

    /**
     * Default empty set of properties
     */
    Properties EMPTY_PROPS = new Properties();

    /**
     * Support constants for metadata
     */
    public class SupportConstants {

        private SupportConstants() {}

        /**
         * Support contants for groups
         */
        public static class Group {
            private Group() {}

            @SuppressWarnings( "javadoc" )
            public static final int UPDATE = 0;                 
        }

        /**
         * Support constants for elements
         */
        @SuppressWarnings( "javadoc" )
        public static class Element {
            private Element() {}
            
            public static final int SELECT = 0;
            public static final int SEARCHABLE_LIKE = 1;
            public static final int SEARCHABLE_COMPARE = 2;
            public static final int SEARCHABLE_EQUALITY = 3;
            public static final int NULL = 4;
            public static final int UPDATE = 5;
            public static final int DEFAULT_VALUE = 7;
            public static final int AUTO_INCREMENT = 8;
            public static final int CASE_SENSITIVE = 9;
            public static final int NULL_UNKNOWN = 10;
            public static final int SIGNED = 11;
        }

    }

    /**
     * @return the version of teiid for which this metadata is applicable
     */
    ITeiidServerVersion getTeiidVersion();

    /**
     * Get the metadata-implementation identifier object for the given element name.  
     * 
     * @param elementName Fully qualified element name
     * 
     * @return Metadata identifier for this element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getElementID(String elementName) throws Exception;

    /**
     * Get the metadata-implementation identifier object for the given group name.  
     * 
     * @param groupName Fully qualified group name
     * 
     * @return Metadata identifier for this group
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getGroupID(String groupName) throws Exception;

    /**
     * Get a collection of group names that match the partially qualified group name.
     * 
     * @param partialGroupName Partially qualified group name
     * 
     * @return A collection of groups whose names are matched by the partial name.
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Collection getGroupsForPartialName(String partialGroupName) throws Exception;

    /**
     * Get the metadata-implementation identifier object for the model containing the 
     * specified group or element ID.
     * 
     * @param groupOrElementID Metadata group or element ID 
     * 
     * @return Metadata identifier for the model
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getModelID(Object groupOrElementID) throws Exception;

    /**
     * Get the fully qualified (unique) name of the metadata identifier specified.  This metadata
     * identifier was previously returned by some other method.
     * 
     * @param metadataID Metadata identifier
     * 
     * @return Metadata identifier for this model
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getFullName(Object metadataID) throws Exception;

    /**
     * Get the name of the metadata identifier specified.  This metadata
     * identifier was previously returned by some other method.
     * 
     * @param metadataID Metadata identifier
     * 
     * @return Metadata identifier for this model
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getName(Object metadataID) throws Exception;

    /**
     * Get list of metadata element IDs for a group ID
     * 
     * @param groupID Group ID
     * 
     * @return List of Object, where each object is a metadata elementID for element within group
     * 
     * @throws Exception implementation detected a problem during the request
     */
    List getElementIDsInGroupID(Object groupID) throws Exception;

    /**
     * Get containing group ID given element ID
     * 
     * @param elementID Element ID
     * 
     * @return Group ID containing elementID
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getGroupIDForElementID(Object elementID) throws Exception;

    /**
     * Get the the StoredProcedureInfo based on the fully qualified procedure name
     * 
     * @param fullyQualifiedProcedureName the fully qualified stored procedure name
     * 
     * @return StoredProcedureInfo containing the runtime model id
     * 
     * @throws Exception implementation detected a problem during the request
     */
    IStoredProcedureInfo getStoredProcedureInfoForProcedure(String fullyQualifiedProcedureName) throws Exception;

    /**
     * Get the element type name for an element symbol.  These types are defined in 
     * {@link IDataTypeManagerService}.
     * 
     * @param elementID
     * 
     * @return The element data type
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getElementType(Object elementID) throws Exception;

    /**
     * Get the element's default value for an element symbol
     * 
     * @param elementID The element ID
     * 
     * @return The default value of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    @Updated(version=Version.TEIID_8_12_4)
    String getDefaultValue(Object elementID) throws Exception;

    /**
     * Get the element's minimum value for an element symbol
     * 
     * @param elementID The element ID
     * 
     * @return The minimum value of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getMinimumValue(Object elementID) throws Exception;

    /**
     * Get the element's default value for an element symbol
     * 
     * @param elementID The element ID
     * 
     * @return The maximum value of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getMaximumValue(Object elementID) throws Exception;

    /**
     * Get the element's position in the group
     * @param elementID The element ID
     * 
     * @return The position of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    int getPosition(Object elementID) throws Exception;

    /**
     * Get the element's precision
     * 
     * @param elementID The element ID
     * 
     * @return The precision of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    int getPrecision(Object elementID) throws Exception;

    /**
     * Get the element's scale
     * 
     * @param elementID The element ID
     * 
     * @return The scale of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    int getScale(Object elementID) throws Exception;

    /**
     * Get the element's radix
     * 
     * @param elementID The element ID
     * 
     * @return The radix of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    int getRadix(Object elementID) throws Exception;

    /**
     * Get the element's format
     * 
     * @param elementID The element ID
     * 
     * @return The format of the element
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getFormat(Object elementID) throws Exception;

    /**
     * Get the number of distinct values for this column.  Negative values (typically -1)
     * indicate that the NDV is unknown.  Only applicable for physical columns.
     * 
     * @param elementID The element ID
     * 
     * @return The number of distinct values of this element in the data source
     * 
     * @throws Exception implementation detected a problem during the request
     */
    float getDistinctValues(Object elementID) throws Exception;

    /**
     * Get the number of distinct values for this column.  Negative values (typically -1)
     * indicate that the NDV is unknown.  Only applicable for physical columns.
     * 
     * @param elementID The element ID
     * 
     * @return The number of distinct values of this element in the data source
     * 
     * @throws Exception implementation detected a problem during the request
     */
    float getNullValues(Object elementID) throws Exception;

    /**
     * Determine whether a group is virtual or not.
     * 
     * @param groupID
     * 
     * @return True if virtual
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean isVirtualGroup(Object groupID) throws Exception;

    /**
     * Determine whether a model is virtual or not.
     * 
     * @param modelID
     * 
     * @return True if virtual
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean isVirtualModel(Object modelID) throws Exception;

    /**
     * Get virtual plan for a group symbol.
     * 
     * @param groupID
     * 
     * @return Root of tree of QueryNode objects
     * 
     * @throws Exception
     */
    IQueryNode getVirtualPlan(Object groupID) throws Exception;

    /**
     * Get procedure defining the insert plan for this group.
     * 
     * @param groupID
     * 
     * @return A string giving the procedure for inserts.
     * 
     * @throws Exception
     */
    String getInsertPlan(Object groupID) throws Exception;

    /**
     * Get procedure defining the update plan for this group.
     * 
     * @param groupID
     * 
     * @return A string giving the procedure for inserts.
     * 
     * @throws Exception
     */
    String getUpdatePlan(Object groupID) throws Exception;

    /**
     * Get procedure defining the delete plan for this group.
     * 
     * @param groupID
     * 
     * @return A string giving the procedure for inserts.
     * 
     * @throws Exception
     */
    String getDeletePlan(Object groupID) throws Exception;

    /**
     * Determine whether the specified model supports some feature.  
     * 
     * @param modelID Metadata identifier specifying the model
     * @param modelConstant
     * 
     * @return True if model supports feature
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean modelSupports(Object modelID, int modelConstant) throws Exception;

    /**
     * Determine whether the specified group supports some feature.  
     * 
     * @param groupID Group metadata ID 
     * @param groupConstant
     * 
     * @return True if group supports feature
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean groupSupports(Object groupID, int groupConstant) throws Exception;

    /**
     * Determine whether the specified element supports some feature.  
     * 
     * @param elementID Element metadata ID
     * @param elementConstant
     * 
     * @return True if element supports feature
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean elementSupports(Object elementID, int elementConstant) throws Exception;

    /**
     * Get all extension properties defined on this metadata object  
     * 
     * @param metadataID Typically element, group, model, or procedure
     * 
     * @return All extension properties for this object or null for none
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Properties getExtensionProperties(Object metadataID) throws Exception;

    /**
     * Get the max set size for the specified model.
     * @param modelID Metadata identifier specifying model
     * 
     * @return Maximum set size
     * 
     * @throws Exception implementation detected a problem during the request
     */
    int getMaxSetSize(Object modelID) throws Exception;

    /**
     * Get the indexes for the specified group 
     * 
     * @param groupID Metadata identifier specifying group
     * 
     * @return Collection of Object (never null), each object representing an index
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Collection getIndexesInGroup(Object groupID) throws Exception;

    /**
     * Get the unique keys for the specified group (primary and unique keys)
     * 
     * @param groupID Metadata identifier specifying group
     * 
     * @return Collection of Object (never null), each object representing a unique key
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Collection getUniqueKeysInGroup(Object groupID) throws Exception;

    /**
     * Get the foreign keys for the specified group
     * 
     * @param groupID Metadata identifier specifying group
     * 
     * @return Collection of Object (never null), each object representing a key
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Collection getForeignKeysInGroup(Object groupID) throws Exception;

    /**
     * Get the corresponding primary key ID for the specified foreign
     * key ID
     * 
     * @param foreignKeyID Metadata identifier of a foreign key
     * 
     * @return Metadata ID of the corresponding primary key
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getPrimaryKeyIDForForeignKeyID(Object foreignKeyID) throws Exception;

    /**
     * Get the access patterns for the specified group
     * 
     * @param groupID Metadata identifier specifying group
     * 
     * @return Collection of Object (never null), each object representing an access pattern
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Collection getAccessPatternsInGroup(Object groupID) throws Exception;

    /**
     * Get the elements in the index
     * 
     * @param index Index identifier, as returned by {@link #getIndexesInGroup}
     * 
     * @return List of Object, where each object is a metadata element identifier
     * 
     * @throws Exception implementation detected a problem during the request
     */
    List getElementIDsInIndex(Object index) throws Exception;

    /**
     * Get the elements in the key
     * 
     * @param key Key identifier, as returned by {@link #getUniqueKeysInGroup}
     * 
     * @return List of Object, where each object is a metadata element identifier
     * 
     * @throws Exception implementation detected a problem during the request
     */
    List getElementIDsInKey(Object key) throws Exception;

    /**
     * Get the elements in the access pattern
     * 
     * @param accessPattern access pattern identifier, as returned by {@link #getAccessPatternsInGroup}
     * 
     * @return List of Object, where each object is a metadata element identifier
     * 
     * @throws Exception implementation detected a problem during the request
     */
    List getElementIDsInAccessPattern(Object accessPattern) throws Exception;

    /**
     * Determine whether a group is an XML virtual document.
     * 
     * @param groupID Group to check
     *  
     * @return True if group is an XML virtual document
     * 
     * @throws Exception
     */
    boolean isXMLGroup(Object groupID) throws Exception;
    
    /**
     * Return a mapping node from the given groupID
     * 
     * @param groupID
     * @return mapping node
     * @throws Exception
     */
    IMappingNode getMappingNode(Object groupID) throws Exception;

    /**
     * Get the currently connected virtual database name.  If the current metadata is not
     * virtual-database specific, then null should be returned.
     * 
     * @return Name of current virtual database
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getVirtualDatabaseName() throws Exception;

    /**
     * Return a list of all the temp groups used in this document.
     * 
     * @param groupID XML virtual document groupID 
     * 
     * @return List of all the temp groups used in this document.
     * 
     * @throws Exception
     */
    <T> Collection<T> getXMLTempGroups(Object groupID) throws Exception;

    /**
     * Return the cardinality for this group
     * 
     * @param groupID Metadata identifier specifying group
     * 
     * @return cardinality for the given group. If unknown, return UNKNOWN_CARDINALITY. 
     * 
     * @throws Exception
     */
    float getCardinality(Object groupID) throws Exception;

    /**
     * Get XML schemas for a document group.
     * 
     * @param groupID Document group ID
     * 
     * @return List of String where each string is an XML schema for the document
     * 
     * @throws Exception
     */
    List getXMLSchemas(Object groupID) throws Exception;

    /**
     * Get the name in source of the metadata identifier specified. This metadata
     * identifier was previously returned by some other method.
     * 
     * @param metadataID Metadata identifier
     * 
     * @return Name in source as a string.
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getNameInSource(Object metadataID) throws Exception;

    /**
     * Get the element length for a given element ID.  These types are defined in 
     * {@link IDataTypeManagerService}.
     * 
     * @param elementID The element ID
     * 
     * @return The element length
     * 
     * @throws Exception implementation detected a problem during the request
     */
    int getElementLength(Object elementID) throws Exception;

    /**
     * Determine whether given virtual group has an associated <i>Materialization</i>.
     * A Materialization is a cached version of the representation of a virtual group. 
     * 
     * @param groupID the groupID of the virtual group in question. 
     * 
     * @return True if given virtual group has been marked as having a Materialization.
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean hasMaterialization(Object groupID) throws Exception;

    /**
     * Accquire the physical group ID (the <i>Materialization</i>) for the given virtual
     * group ID, or <code>null</code> if the given virtual group has no Materialization.
     * 
     * @param groupID the groupID of a virtual group that has a Materialization.
     * 
     * @return The groupID of the physical group that is a Materialization of the given virtual group.
     *
     * @throws Exception implementation detected a problem during the request
     */
    Object getMaterialization(Object groupID) throws Exception;

    /**
     * Accquire the physical group ID that is used for the staging area for loading
     * (the <i>Materialization</i>) for the given virtual group ID, or <code>null</code>
     * if the given virtual group has no Materialization.  
     * 
     * @param groupID the groupID of a virtual group that has a Materialization.
     * 
     * @return The groupID of the physical group that is the staging table for loading
     * the Materialization of the given virtual group.
     * 
     * @throws Exception implementation detected a problem during the request
     */
    Object getMaterializationStage(Object groupID) throws Exception;

    /**
     * Get the native type of the element specified. This element
     * identifier was previously returned by some other method.
     * 
     * @param elementID Element identifier
     * 
     * @return Native type name
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getNativeType(Object elementID) throws Exception;

    /**
     * Determine whether this is a procedure
     * 
     * @param groupID Group identifier
     * 
     * @return True if it is an procedure; false otherwise
     * 
     * @throws Exception implementation detected a problem during the request
     */
    boolean isProcedure(Object groupID) throws Exception;
    
    /**
     * Determine whether this stored procedure contains a procedure
     * 
     * @param procedureName
     * 
     * @return true if it does
     * @throws Exception
     */
    boolean hasProcedure(String procedureName) throws Exception;

    /**
     * Gets the resource paths of all the resources in the VDB. 
     * 
     * @return an array of resource paths of the resources in the VDB
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String[] getVDBResourcePaths() throws Exception;
    
    /**
     * Get the modelled type for the given elementID
     * 
     * @param elementID
     * @return name of the modelled type
     * 
     * @throws Exception
     */
    String getModeledType(Object elementID) throws Exception;

    /**
     * Get the modelled base type for the given elementID
     * 
     * @param elementID
     * @return name of the modelled base type
     * 
     * @throws Exception
     */
    String getModeledBaseType(Object elementID) throws Exception;

    /**
     * Get the modelled primitive type for the given elementID
     * 
     * @param elementID
     * @return name of the modelled primitive type
     * 
     * @throws Exception
     */
    String getModeledPrimitiveType(Object elementID) throws Exception;

    /**
     * Gets the contents of a VDB resource as a String.
     * 
     * @param resourcePath a path returned by getVDBResourcePaths()
     * 
     * @return the contents of the resource as a String.
     * 
     * @throws Exception implementation detected a problem during the request
     */
    String getCharacterVDBResource(String resourcePath) throws Exception;

    /**
     * Gets the contents of a VDB resource in binary form.
     * 
     * @param resourcePath a path returned by getVDBResourcePaths()
     * 
     * @return the binary contents of the resource in a byte[]
     * 
     * @throws Exception implementation detected a problem during the request
     */
    byte[] getBinaryVDBResource(String resourcePath) throws Exception;
    
    /**
     * Get the primery key of the given metadata id
     * 
     * @param metadataID
     * 
     * @return primary key
     */
    Object getPrimaryKey(Object metadataID);

    /**
     * Get the function library
     * 
     * @return the function library
     */
    IFunctionLibrary getFunctionLibrary();

    /**
     * @param groupID
     *
     * @return true if object is temporary table, false otherwise
     * @throws Exception
     */
    boolean isTemporaryTable(Object groupID) throws Exception;

    /**
     * @param metadataID
     * @param key
     * @param value
     * @return previous value associated with key or null if no mapping had been added
     * @throws Exception
     */
    Object addToMetadataCache(Object metadataID, String key, Object value) throws Exception;

    /**
     * @param metadataID
     * @param key
     * @return metadata associated with the given key parameters
     * @throws Exception
     */
    Object getFromMetadataCache(Object metadataID, String key) throws Exception;

    /**
     * @param groupID
     * @return true if group is scalar, false otherwise
     * @throws Exception
     */
    boolean isScalarGroup(Object groupID) throws Exception;

    /**
     * @param modelId
     * @return true if model is multi source, false otherwise
     * @throws Exception
     */
    boolean isMultiSource(Object modelId) throws Exception;

    /**
     * @param elementId
     * @return true if a multi source element, false otherwise
     * @throws Exception
     */
    boolean isMultiSourceElement(Object elementId) throws Exception;

    /**
     * @return design time metadata, if applicable
     */
    IQueryMetadataInterface getDesignTimeMetadata();

    /**
     * @return session metadata, if applicable
     */
    IQueryMetadataInterface getSessionMetadata();

    /**
     * @return imported models
     */
    Set<String> getImportedModels();

    /**
     * @param language
     * @return script engine for language
     * @throws Exception
     */
    ScriptEngine getScriptEngine(String language) throws Exception;

    /**
     * @param metadataID
     * @return true if metadata id is variadic, false otherwise
     */
    boolean isVariadic(Object metadataID);

    /**
     * @param metadataID
     * @return map of function-based expression for metadata id
     */
    Map<Expression, Integer> getFunctionBasedExpressions(Object metadataID);

    /**
     * @param elementId
     * @return true if pseudo element, false otherwise
     */
    boolean isPseudo(Object elementId);

    /**
     * @param modelName
     * @return model id for model with given name
     * @throws Exception
     */
    Object getModelID(String modelName) throws Exception;

    /**
     * @param metadataID
     * @param key
     * @param checkUnqualified
     * @return extension property for given parameters
     */
    String getExtensionProperty(Object metadataID, String key, boolean checkUnqualified);

    /**
     * @return whether to use output name
     */
    boolean useOutputName();

    /**
     * @return short name use property
     */
    boolean findShortName();

    /**
     * @return the widen comparison to string flag
     */
    @Since(Version.TEIID_8_12_4)
    boolean widenComparisonToString();
}
