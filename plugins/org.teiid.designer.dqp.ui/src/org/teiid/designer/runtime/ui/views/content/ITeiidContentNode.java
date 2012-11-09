/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import org.eclipse.wst.server.core.IServer;

/**
 * @param <T>
 */
public interface ITeiidContentNode<T extends ITeiidContainerNode<?>> {

        /**
         * @return returns the server containing this node.
         */
        IServer getServer();

        /**
         * @return the resource containing this node, if any.
         */
        ITeiidResourceNode getParent();

        /**
         * @return the containing node.
         */
        T getContainer();

        /**
         * @return the name of this node.
         */
        String getName();

        /**
         * Frees any resources held by this node.
         */
        void dispose();
    }
