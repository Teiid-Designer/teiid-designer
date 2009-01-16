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

package com.metamatrix.rose.internal;

import java.util.List;

/**
 * This class represents an ambiguous reference that was encountered during import. An ambiguous reference is a reference by name
 * that can be resolved to multiple typed objects.
 * 
 * @since 4.1
 */
public interface IAmbiguousReference extends
                IRoseConstants.IReferenceTypes {
    //============================================================================================================================
    // Property Methods

    /**
     * @return The list of typed objects that have the same name as the reference; never null, unmodifiable.
     * @since 4.1
     */
    List getAvailableObjects();

    /**
     * @return The name of the ambiguous reference; never null.
     * @since 4.1
     */
    String getName();

    /**
     * @return The object to which this reference will be resolved. A null value indicates either the reference has not been
     *         {@link #isResolved() resolved}or that it has been set to remain an unresolvable reference.
     * @since 4.1
     */
    Object getReferencedObject();

    /**
     * @return The object containing the ambiguous reference; never null.
     * @since 4.1
     */
    Object getReferencer();

    /**
     * @return The type of ambiguous reference; either {@link #GENERALIZATION},{@link #OWNER}, or {@link #TYPE}.
     * @since 4.1
     */
    String getType();

    /**
     * @return True if the reference has been resolved to either one of the {@link #getAvailableObjects() available objects}or
     *         set to remain an unresolvable reference.
     * @since 4.1
     */
    boolean isResolved();
}
