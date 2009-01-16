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

package com.metamatrix.query.internal.ui.builder;

import com.metamatrix.query.sql.LanguageObject;

/**
 * The <code>ILanguageObjectInputProvider</code> interface is used by the {@link LanguageObjectContentProvider}
 * to set it's input. In order for the content provider's getChildren() method to be called correctly, the
 * input to the <code>TreeViewer</code> cannot be the root of the tree. If the input is the root, the entire
 * tree is refreshed. This interface was created in order to allow the input to the viewer to not be the 
 * root of the tree (which is the LanguageObject).
 */
public interface ILanguageObjectInputProvider {

    /**
     * Gets the <code>LanguageObject</code> being provided.
     * @return the LanguageObject
     */
    LanguageObject getLanguageObject();
}
