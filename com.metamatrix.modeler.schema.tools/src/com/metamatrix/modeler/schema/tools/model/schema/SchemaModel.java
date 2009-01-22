/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;

public interface SchemaModel {

    /**
     * Produces a List of potential RootElements derived from the schemas that this SchemModel represents. Roots that have been
     * determined to likely be roots elements can be identified through a call to RootElement.isUseAsRoot(). Elements that have no
     * parents and one or more children are determined to be roots.
     * 
     * @return The List of potential root elements.
     */
    public abstract List getPotentialRootElements();

    /**
     * Produces a partially deep copy of the the array of elements and its relationships. Namespaces and DefaultNamespaces are not
     * copied but use the same reference, as they are fixed.
     * 
     * @return - the copy of the SchemaModel
     */
    public abstract SchemaModel copy();

    /**
     * Returns the List of SchemaObjects contained in this SchemaModel.
     * 
     * @return The list of SchemaObjects.
     */
    public abstract List getElements();

    /**
     * Returns a Map of namespaces keyed by namespace prefix.
     * 
     * @return A Map of namespaces.
     */
    public abstract Map getNamespaces();

    /**
     * Returns a Map of namespace prefixes keyed by namespacr URI.
     * 
     * @return A Map of namespace prefixes.
     */
    public abstract Map getNamespacePrefixes();

    /**
     * Allows the user of the class to provide a HashSet of Elements that have been selected as root elements to the model. This
     * HashSet will inform the model processing algorithm to model only Elements that are under these Elements.
     * 
     * @param rootElements
     * @throws Throwable
     */
    public abstract void setSelectedRootElements( Set rootElements );

    /**
     * Determines if an ElementImpl has been selected as a root by the user. If the SchemaModelImpl is used in a context where no
     * user selections are made and all elements are modeled, then SetSelectedRootElements should not be called and this function
     * will always return true.
     * 
     * @param element
     * @return - true if selected
     */
    public abstract boolean isSelectedRootElement( SchemaObject element );

    // TODO: this is gross it should be removed.
    public abstract void setElements( List nonMergedTables );

    /**
     * Returns from the List of Elements the Elements that have been determined to represent Tables in the Model.
     * 
     * @return List of Tables
     */
    public abstract List getTables();

    /**
     * Looks up a SchemaObject in the SchemaModel by the SchemaObject's name.
     * 
     * @param simpleName - The name of the table.
     * @return The Table or null if the table is not found.
     */
    public abstract Table findTable( String simpleName );

    /**
     * Determines if this SchemaModel was created aware of SchemaTypes. If not, then the SchemaObjects produced by this
     * SchemaModel will only be schema elements. If it is type aware, then it may produce elements and types.
     * 
     * @return true if type aware
     */
    public abstract boolean isTypeAware();

    /**
     * Sets a schema type as the single root element selection for this Schema Model.
     * 
     * @param typeName
     * @param namespace
     * @throws Exception
     */
    public abstract void setTypeAsRoot( String typeName,
                                        String namespace ) throws SchemaProcessingException;
}
