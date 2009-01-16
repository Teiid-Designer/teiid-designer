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

package com.metamatrix.modeler.schema.tools.processing;

import java.util.List;
import java.util.Map;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.processing.internal.ElementContentTraversalContext;
/**
 *Derives a SchemaModel from one or more XSDSchema.  The SchemaModel 
 *produced will combine the elements and optionally the types defined
 *in all the provided XSDSchema into a model that creates relates 
 *parents and children within and across schemas. 
 */
public interface SchemaProcessor {

	/**
	 * Clears the results of any processing and resets the processor for
	 * additional processing with no relation to previous usage.
	 */
	public abstract void clear();

	/**
	 * Begins processing on the passed Schemas. Can be called repeatedly to add
	 * additional results to a previous run. The results of each run will
	 * produced relationships with the previous runs if they exist.
	 * 
	 * @param schemas
	 */
	public abstract void processSchemas(XSDSchema[] schemas) throws SchemaProcessingException;

	/**
	 * Convert the schemaURIs to an array of XSDSchema for the
	 * {@link SchemaProcessorImpl}
	 * 
	 */
	public abstract void processSchemaURIs(List schemaURIs) throws SchemaProcessingException;

	/**
	 * Returns a Map of Namespaces keyed by namespace prefix derived from the 
	 * schemas provided to the SchemaProcessor.
	 * @return - the namespace Map
	 */
	public abstract Map getNamespaces();
	
	/**
	 * Loads the SchemaProcessor with a Map of namespaces. 
	 */
	public abstract void setNamespaces(Map namespaces);

	/**
	 * Gets the SchemaModel derived from the schemas provided to the SchemaProcessor
	 * @return - the SchemaModel
	 */
	public abstract SchemaModel getSchemaModel();
	
	/**
	 * Configures the SchemaProcessor to represent Types Definitions as well as 
	 * Elements.  By default the SchemaProcessor does not represent Types, so it
	 *  is only required to call this method if you need Types. 
	 * @param representTypes True to make the SchemaProcessor represent Types
	 */
	public abstract void representTypes(boolean representTypes);

	public abstract void processType(XSDTypeDefinition type, ElementContentTraversalContext traverseCtx2, XSDSchema schema)
			throws SchemaProcessingException;

	public abstract void processElementText(SchemaObject element);

	public void processElement(XSDElementDeclaration elem, ElementContentTraversalContext traverseCtx, XSDSchema schema)
			throws SchemaProcessingException;
}