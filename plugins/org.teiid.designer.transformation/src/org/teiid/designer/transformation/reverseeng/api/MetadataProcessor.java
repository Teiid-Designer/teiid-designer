/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.api;

import java.util.List;

import org.teiid.designer.runtime.spi.TeiidExecutionException;

/**
 * @author vanhalbert
 *
 */
public interface MetadataProcessor  {

	void loadMetadata(Object metadataSource, Options options) throws TeiidExecutionException;
	List<Table> getTableMetadata();
}
