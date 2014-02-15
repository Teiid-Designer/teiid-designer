/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.teiid.runtime.client.Messages;

public class Schema extends AbstractMetadataRecord {

	private static final long serialVersionUID = -5113742472848113008L;

	private boolean physical = true;
    private String primaryMetamodelUri = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$

	private Map<String, FunctionMethod> functions = new TreeMap<String, FunctionMethod>(String.CASE_INSENSITIVE_ORDER);
	
	private List<AbstractMetadataRecord> resolvingOrder = new ArrayList<AbstractMetadataRecord>();

	public void addFunction(FunctionMethod function) {
		function.setParent(this);
		//TODO: ensure that all uuids are unique
		if (this.functions.put(function.getUUID(), function) != null) {
			throw new RuntimeException(Messages.gs(Messages.TEIID.TEIID60015, function.getUUID()));
		}
		resolvingOrder.add(function);
	}	
	
	/**
	 * Get the functions defined in this schema in a map of uuid to {@link FunctionMethod}
	 * @return
	 */
	public Map<String, FunctionMethod> getFunctions() {
		return functions;
	}
	
	/**
	 * Get a function by uid
	 * @param funcName
	 * @return
	 */
	public FunctionMethod getFunction(String uid) {
		return functions.get(uid);
	}	
	
    public String getPrimaryMetamodelUri() {
        return primaryMetamodelUri;
    }

    public boolean isPhysical() {
        return physical;
    }

    /**
     * @param string
     */
    public void setPrimaryMetamodelUri(String string) {
        primaryMetamodelUri = string;
    }

    public void setPhysical(boolean physical) {
		this.physical = physical;
	}
    
    /**
     * 7.1 schemas did not have functions
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	if (this.functions == null) {
    		this.functions = new TreeMap<String, FunctionMethod>(String.CASE_INSENSITIVE_ORDER);
    	}
    	if (this.resolvingOrder == null) {
    		this.resolvingOrder = new ArrayList<AbstractMetadataRecord>();
    		this.resolvingOrder.addAll(this.functions.values());
    	}
    }
    
    public List<AbstractMetadataRecord> getResolvingOrder() {
		return resolvingOrder;
	}
    
}
