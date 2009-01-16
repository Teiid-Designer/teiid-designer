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

package com.metamatrix.ui.internal.widget;


/**
 * The <code>ICheckableController</code> is a controller for an {@link org.eclipse.jface.viewers.ICheckable}.
 * @since 4.2
 */
public interface ICheckableController {

    /**
     * Indicates if the check state of the specified object can be changed.
     * @param theObject the object being tested
     * @return <code>true</code>if editable; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean isEditable(Object theObject);

}
