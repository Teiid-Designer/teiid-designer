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

package com.metamatrix.modeler.schema.tools.processing.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public class ElementContentTraversalContext {
	/**
	 * The ElementContentTraversalContext is passed along as a parameter during
	 * the parsing of an array of XDSs. It maintains values for the minimum and
	 * maximum occurance values of an element derived from its own value and
	 * that of its parents. It also maintains Maps of Global elements and types
	 * to ensure that each is only recognized and recorded once.
	 * 
	 */
	// As we move down through groups etc, the list is lengthened, and when the
	// branch is ended,
	// the string is shortened. This is a convenient way to work out the
	// relationship between
	// an element and its possible parents
	List minOccurs = new ArrayList();

	List maxOccurs = new ArrayList();

	private Map globalElements;

	private Map globalTypes;

	private Map elementsByElementNameThenType;

	SchemaObject parentTable = null;

	ElementContentTraversalContext(SchemaObject parentTable,
			ElementContentTraversalContext parentContext) {
		this.parentTable = parentTable;
		if (parentContext == null) {
			globalElements = new HashMap();
			globalTypes = new HashMap();
			elementsByElementNameThenType = new HashMap();
		} else {
			globalElements = parentContext.globalElements;
			globalTypes = parentContext.globalTypes;
			elementsByElementNameThenType = parentContext.elementsByElementNameThenType;
		}
	}

	public List getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(List minOccurs) {
		this.minOccurs = minOccurs;
	}

	public List getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(List maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public void addMinOccurs(Integer integer) {
		minOccurs.add(integer);

	}

	public void addMaxOccurs(Integer integer) {
		maxOccurs.add(integer);

	}

	public void removeMinOccurs(int i) {
		minOccurs.remove(i);

	}

	public void removeMaxOccurs(int i) {
		maxOccurs.remove(i);
	}

	public Object getGlobalElement(Object key) {
		return globalElements.get(key);
	}

	public Object putGlobalElement(Object key, Object value) {
		return globalElements.put(key, value);
	}

	public Object getGlobalType(Object key) {
		return globalTypes.get(key);
	}

	public Object putGlobalType(Object key, Object value) {
		return globalTypes.put(key, value);
	}

	public Map getElementsByNameThenType(Object key) {
		Map tablesForName = (Map) elementsByElementNameThenType.get(key);
		if (tablesForName == null) {
			tablesForName = new HashMap();
			putElementsByNameThenType(key, tablesForName);
		}
		return tablesForName;
	}

	private Map putElementsByNameThenType(Object key, Object value) {
		return (Map) elementsByElementNameThenType.put(key, value);
	}

	public SchemaObject getParentTable() {
		return parentTable;
	}

	public int calculateMinOccurs(int minO) {
		for (int i = 0; i < minOccurs.size(); i++) {
			int min = ((Integer) minOccurs.get(i)).intValue();
			minO *= min;
			if (minO == 0) {
				// no point continuing the processing
				break;
			}
		}
		return minO;
	}

	public int calculateMaxOccurs(int maxO) {
		for (int i = 0; i < maxOccurs.size(); i++) {
			int max = ((Integer) maxOccurs.get(i)).intValue();
			if (max == -1) {
				maxO = -1;
			} else {
				maxO *= max;
			}
			if (maxO == -1) {
				// no point continuing the processing
				break;
			}
		}
		return maxO;
	}
}