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
public class TeiidContentNode<T extends ITeiidContainerNode<?>> implements ITeiidContentNode<T> {

    /** The path separator for addresses. */
    public static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

    private final IServer server;
    private ITeiidResourceNode parent;
    private T container;
    private final String name;

    protected TeiidContentNode(IServer server, String name) {
        this.server = server;
        this.parent = null;
        this.container = null;
        this.name = name;
    }

    protected TeiidContentNode(T container, String name) {
        this.server = container.getServer();
        this.parent = container instanceof ITeiidResourceNode ? (ITeiidResourceNode) container : container.getParent();
        this.container = container;
        this.name = name;
    }

    public ITeiidResourceNode getParent() {
        return parent;
    }

    public T getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    public IServer getServer() {
        return server;
    }

    public void dispose() {
        container = null;
        parent = null;
    }
}
