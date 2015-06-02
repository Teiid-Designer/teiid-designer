/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb.xmi;

import org.teiid.designer.komodo.vdb.BasicVdb;

/**
 * @author blafond
 *
 */
public class XmiVdb extends BasicVdb {
	
	// XMI VDB requires managing (reading & saving) a "*.vdb" archive file
	// There will be no <metadata> tags in this model
	// Models will XMI files accompanied by their corresponding INDEX files

	public XmiVdb() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void export() {
		// TODO Auto-generated method stub
		
	}

}
