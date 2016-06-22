/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng;

import java.util.Collection;

import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.transformation.reverseeng.api.MetadataProcessor;
import org.teiid.designer.transformation.reverseeng.api.Options;

/**
 * @author vanhalbert
 *
 */
public class ReverseEngineerFactory {
	
	private static Collection<Exception> errors;
	
	/**
	 * Call to reverse engineer from a Teiid Designer table metadata object
	 * @param table
	 * @param options
	 * @return boolean indicator if the process was success, return <code>false</code> then check for errors
	 * @throws Exception
	 */
	public static boolean perform(Table table, Options options) throws Exception {
	    errors = null;
	    
		  MetadataProcessor metadata = new RelationalMetadataProcessor();
		  metadata.loadMetadata(table, options);
	  	
		  PojoProcessing tp = new PojoProcessing(options);
		  boolean success = tp.processTables(metadata);

		  if (!success) checkErrors(tp);
		  
		  return success;

	} 
	
	public static Collection<Exception> getErrors() {
		return errors;
	}

	
	private static void checkErrors(PojoProcessing pp) {
		  errors = pp.getExceptions();
		  for (Exception e : errors) {
			  System.err.println("***********************************");
			  System.err.println(e);
			  System.err.println("***********************************");
		  }
	}

}
