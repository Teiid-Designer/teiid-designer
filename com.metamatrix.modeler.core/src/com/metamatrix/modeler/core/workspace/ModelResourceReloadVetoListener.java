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

package com.metamatrix.modeler.core.workspace;


/** 
 * A listener for changes to ModelResource when the contents of the model have been changed on the
 * underlying file system.  Implementations that are 
 * {@link ModelWorkspace#addModelResourceReloadVetoListener(ModelResourceReloadVetoListener) registered}
 * will be given the opportunity to veto the reloading of the file's contents.  If any veto listener
 * exercises the veto, the ModelResource will not be reloaded.
 * @since 4.2
 */
public interface ModelResourceReloadVetoListener {

    /** 
     * Return whether the specified model should or should not be reloaded.  Unless the
     * implementation cares, this method should return true.
     * @param modelResource the model whose file contents have changed and that may be reloaded
     * @return false if the model should <i>not</i> be reloaded, or true otherwise
     * @since 4.2
     */
    boolean canReload(ModelResource modelResource);

}
