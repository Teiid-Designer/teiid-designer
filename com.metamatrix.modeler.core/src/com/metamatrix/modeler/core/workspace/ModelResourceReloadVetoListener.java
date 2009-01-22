/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
