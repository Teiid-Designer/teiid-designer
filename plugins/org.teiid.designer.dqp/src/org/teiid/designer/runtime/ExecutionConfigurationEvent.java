/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.Util;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;


/**
 * The <code>ExecutionConfigurationEvent</code> class is the event that is broadcast from the {@link TeiidServerManager server manager}
 * when a server or connector is added, removed, or changed, or when a server is refreshed.
 *
 * @since 8.0
 */
public final class ExecutionConfigurationEvent {

    public static ExecutionConfigurationEvent createAddDataSourceEvent( TeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createAddServerEvent( TeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createDeployVDBEvent( String vdbName ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.VDB, vdbName);
    }

    public static ExecutionConfigurationEvent createRemoveDataSourceEvent( TeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createRemoveServerEvent( TeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createServerRefreshEvent( TeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.REFRESH, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createSetDefaultServerEvent( TeiidServer oldDefaultServer,
                                                                           TeiidServer newDefaultServer ) {
        return new ExecutionConfigurationEvent(EventType.DEFAULT, TargetType.SERVER, oldDefaultServer, newDefaultServer);
    }

    public static ExecutionConfigurationEvent createUnDeployVDBEvent( String vdbName ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.VDB, vdbName);
    }

    public static ExecutionConfigurationEvent createUpdateDataSourceEvent( TeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createUpdateServerEvent( TeiidServer teiidServer,
                                                                       TeiidServer updatedServer ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.SERVER, teiidServer, updatedServer);
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
    public TeiidServer getServer() {
        if (this.targetType != TargetType.SERVER) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetServerMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.SERVER));
        }

        return (TeiidServer)this.target;
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
    public TeiidServer getUpdatedServer() {
        if (this.targetType != TargetType.SERVER) {
            throw new IllegalStateException(Util.getString("invalidTargetTypeForGetUpdatedServerMethod", //$NON-NLS-1$
                                                           this.targetType,
                                                           TargetType.SERVER));
        }

        return (TeiidServer)this.updatedTarget;
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
