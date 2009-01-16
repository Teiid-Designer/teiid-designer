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

package com.metamatrix.ui.product;


/**
 * A product context is used to identify areas supported by an application. Usually only used when a
 * feature or parts of a feature are being removed.
 * @since 4.4
 */
public interface IProductContext {

    /**
     * Obtains the unique identifier of the product context. 
     * @return the ID (never <code>null</code>)
     * @since 4.4
     */
    String getId();

}
