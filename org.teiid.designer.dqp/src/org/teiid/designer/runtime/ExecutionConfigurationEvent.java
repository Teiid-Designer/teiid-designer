/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;

/**
 * The <code>ExecutionConfigurationEvent</code> class is the event that is broadcast from the {@link ServerManager server manager}
 * when a server or connector is added, removed, or changed, or when a server is refreshed.
 */
public final class ExecutionConfigurationEvent {

    public enum EventType {
        ADD,
        REFRESH,
        REMOVE,
        UPDATE;
    }

    public enum TargetType {
        CONNECTOR,
        SERVER,
        SOURCE_BINDING;
    }

    public static ExecutionConfigurationEvent createAddServerEvent( Server server ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.SERVER, server);
    }

    public static ExecutionConfigurationEvent createRemoveServerEvent( Server server ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.SERVER, server);
    }

    public static ExecutionConfigurationEvent createUpdateServerEvent( Server server,
                                                                       Server updatedServer ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.SERVER, server, updatedServer);
    }

    public static ExecutionConfigurationEvent createAddConnectorEvent( Connector connector ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.CONNECTOR, connector);
    }

    public static ExecutionConfigurationEvent createRemoveConnectorEvent( Connector connector ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.CONNECTOR, connector);
    }

    public static ExecutionConfigurationEvent createUpdateConnectorEvent( Connector connector ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.CONNECTOR, connector);
    }

    public static ExecutionConfigurationEvent createAddSourceBindingEvent( SourceBinding binding ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.SOURCE_BINDING, binding);
    }

    public static ExecutionConfigurationEvent createRefreshSourceBindingsEvent() {
        return new ExecutionConfigurationEvent(TargetType.SOURCE_BINDING);
    }

    public static ExecutionConfigurationEvent createRemoveSourceBindingEvent( SourceBinding binding ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.SOURCE_BINDING, binding);
    }

    private final EventType eventType;
    private final TargetType targetType;
    private final Object target;
    private final Object updatedTarget;

    /**
     * Create a refresh event.
     * 
     * @param targetType the target type that was refreshed
     */
    private ExecutionConfigurationEvent( TargetType targetType ) {
        this(EventType.REFRESH, targetType, null, null);
    }

    private ExecutionConfigurationEvent( EventType eventType,
                                         TargetType targetType,
                                         Object target ) {
        this(eventType, targetType, target, null);
        ArgCheck.isNotNull(target, "target"); //$NON-NLS-1$
    }

    private ExecutionConfigurationEvent( EventType eventType,
                                         TargetType targetType,
                                         Object target,
                                         Object updatedTarget ) {
        assert (eventType != null);
        assert (targetType != null);

        this.eventType = eventType;
        this.targetType = targetType;
        this.target = target;
        this.updatedTarget = updatedTarget;
    }

    /**
     * @return the source binding involved in the event
     * @throws IllegalStateException if method is called for a non-source binding event
     */
    public SourceBinding getSourceBinding() {
        if (this.targetType != TargetType.SOURCE_BINDING) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetSourceBindingMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.SOURCE_BINDING));
        }

        return (SourceBinding)this.target;
    }

    /**
     * @return the connector involved in the event
     * @throws IllegalStateException if method is called for a server event
     */
    public Connector getConnector() {
        if (this.targetType != TargetType.CONNECTOR) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetConnectorMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.CONNECTOR));
        }

        return (Connector)this.target;
    }

    /**
     * @return the event type (never <code>null</code>)
     */
    public EventType getEventType() {
        return this.eventType;
    }

    /**
     * @return the server involved in the event
     * @throws IllegalStateException if method is called for a connector event
     */
    public Server getServer() {
        if (this.targetType != TargetType.SERVER) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetServerMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.SERVER));
        }

        return (Server)this.target;
    }

    /**
     * @return the target type (never <code>null</code>)
     */
    public TargetType getTargetType() {
        return this.targetType;
    }

    /**
     * @return the server involved in the event
     * @throws IllegalStateException if method is called for a connector event
     */
    public Server getUpdatedServer() {
        if (this.targetType != TargetType.SERVER) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetUpdatedServerMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.SERVER));
        }

        return (Server)this.updatedTarget;
    }

}
