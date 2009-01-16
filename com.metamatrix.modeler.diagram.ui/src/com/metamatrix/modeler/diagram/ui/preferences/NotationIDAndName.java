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

package com.metamatrix.modeler.diagram.ui.preferences;

/**
 * NotationIDAndName
 * 
 * Data class to encompass a String ID and a String name in displayable form.
 */
public class NotationIDAndName {
	private String id;
	private String displayName;
	
	/**
	 * Constructor.
	 * 
	 * @param id     		Notation ID
	 * @param displayName   Corresponding name in displayable form
	 */
	public NotationIDAndName(String id, String displayName) {
		super();
		this.id = id;
		this.displayName = displayName;
	}
	
	/**
	 * Get the ID
	 * 
	 * @return ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Get the name for display
	 * 
	 * @return  name for display
	 */
	public String getDisplayName() {
		return displayName;
	}
}
