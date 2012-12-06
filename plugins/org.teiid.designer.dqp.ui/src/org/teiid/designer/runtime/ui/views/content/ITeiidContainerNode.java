/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.List;

/**
 * @param <T>
 */
public interface ITeiidContainerNode<T extends ITeiidContainerNode<?>> extends ITeiidContentNode<T> {

    /**
     * @return the children of this container.
     */
    List<? extends ITeiidContentNode<?>> getChildren();

    /**
     * Does this node have any children
     * 
     * @return true if there are children.
     */
    boolean hasChildren();
    
    /**
     * Loads the content of this container. This method is invoked by the
     * content provider if getChildren() returns null.
     */
    void load();

}
