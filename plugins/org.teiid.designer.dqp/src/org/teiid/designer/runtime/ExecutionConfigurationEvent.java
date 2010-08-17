/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import org.teiid.adminapi.VDB;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.internal.workspace.SourceConnectionBinding;

/**
 * The <code>ExecutionConfigurationEvent</code> class is the event that is broadcast from the {@link ServerManager server manager}
 * when a server or connector is added, removed, or changed, or when a server is refreshed.
 */
public final class ExecutionConfigurationEvent {

    public static ExecutionConfigurationEvent createAddDataSourceEvent( TeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createAddServerEvent( Server server ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.SERVER, server);
    }

    public static ExecutionConfigurationEvent createAddSourceBindingEvent( SourceConnectionBinding binding ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.SOURCE_BINDING, binding);
    }

    public static ExecutionConfigurationEvent createDeployVDBEvent( VDB vdb ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.VDB, vdb);
    }

    public static ExecutionConfigurationEvent createRefreshSourceBindingsEvent() {
        return new ExecutionConfigurationEvent(TargetType.SOURCE_BINDING);
    }

    public static ExecutionConfigurationEvent createRemoveDataSourceEvent( TeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createRemoveServerEvent( Server server ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.SERVER, server);
    }

    public static ExecutionConfigurationEvent createRemoveSourceBindingEvent( SourceConnectionBinding binding ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.SOURCE_BINDING, binding);
    }

    public static ExecutionConfigurationEvent createServerRefreshEvent( Server server ) {
        return new ExecutionConfigurationEvent(EventType.REFRESH, TargetType.SERVER, server);
    }

    public static ExecutionConfigurationEvent createSetDefaultServerEvent( Server oldDefaultServer,
                                                                           Server newDefaultServer ) {
        return new ExecutionConfigurationEvent(EventType.DEFAULT, TargetType.SERVER, oldDefaultServer, newDefaultServer);
    }

    public static ExecutionConfigurationEvent createUnDeployVDBEvent( VDB vdb ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.VDB, vdb);
    }

    public static ExecutionConfigurationEvent createUpdateDataSourceEvent( TeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createUpdateServerEvent( Server server,
                                                                       Server updatedServer ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.SERVER, server, updatedServer);
    }

    private final EventType eventType;

    private final TargetType targetType;

    private final Object target;
    private final Object updatedTarget;

    private ExecutionConfigurationEvent( EventType eventType,
                                         TargetType targetType,
                                         Object target ) {
        this(eventType, targetType, target, null);
        CoreArgCheck.isNotNull(target, "target"); //$NON-NLS-1$
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
     * Create a refresh event.
     * 
     * @param targetType the target type that was refreshed
     */
    private ExecutionConfigurationEvent( TargetType targetType ) {
        this(EventType.REFRESH, targetType, null, null);
    }

    /**
     * @return the connector involved in the event
     * @throws IllegalStateException if method is called for a server event
     */
    public TeiidDataSource getDataSource() {
        if (this.targetType != TargetType.DATA_SOURCE) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetDataSourceMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.DATA_SOURCE));
        }

        return (TeiidDataSource)this.target;
    }

    /**
     * @return the event type (never <code>null</code>)
     */
    public EventType getEventType() {
        return this.eventType;
    }

    /**
     * When changing the default server, this returns the old default server.
     * 
     * @return the server involved in the event (may be <code>null</code>)
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
     * @return the source binding involved in the event
     * @throws IllegalStateException if method is called for a non-source binding event
     */
    public SourceConnectionBinding getSourceBinding() {
        if (this.targetType != TargetType.SOURCE_BINDING) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetSourceBindingMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.SOURCE_BINDING));
        }

        return (SourceConnectionBinding)this.target;
    }

    /**
     * @return the target type (never <code>null</code>)
     */
    public TargetType getTargetType() {
        return this.targetType;
    }

    /**
     * @return the connector involved in the event
     * @throws IllegalStateException if method is called for a server event
     */
    public TeiidTranslator getTranslator() {
        if (this.targetType != TargetType.TRANSLATOR) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetTranslatorMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.TRANSLATOR));
        }

        return (TeiidTranslator)this.target;
    }

    /**
     * When changing the default server, this returns the new default server.
     * 
     * @return the updated server involved in the event (may be <code>null</code>)
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

    public enum EventType {
        ADD,
        REFRESH,
        REMOVE,
        UPDATE,
        DEFAULT;
    }

    public enum TargetType {
        TRANSLATOR,
        DATA_SOURCE,
        SERVER,
        VDB,
        SOURCE_BINDING;
    }

}
