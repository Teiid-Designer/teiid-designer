/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.spi;

import org.eclipse.osgi.util.NLS;
import org.teiid.designer.Messages;


/**
 * The <code>ExecutionConfigurationEvent</code> class is the event that is broadcast from the {@link TeiidServerManager server manager}
 * when a server or connector is added, removed, or changed, or when a server is refreshed.
 *
 * @since 8.0
 */
public final class ExecutionConfigurationEvent {

    public static ExecutionConfigurationEvent createAddDataSourceEvent( ITeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createAddServerEvent( ITeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createDeployVDBEvent( String vdbName ) {
        return new ExecutionConfigurationEvent(EventType.ADD, TargetType.VDB, vdbName);
    }

    public static ExecutionConfigurationEvent createRemoveDataSourceEvent( ITeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createRemoveServerEvent( ITeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createServerRefreshEvent( ITeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.REFRESH, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createServerConnectingEvent( ITeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.CONNECTING, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createServerConnectedEvent( ITeiidServer teiidServer ) {
        return new ExecutionConfigurationEvent(EventType.CONNECTED, TargetType.SERVER, teiidServer);
    }

    public static ExecutionConfigurationEvent createSetDefaultServerEvent( ITeiidServer oldDefaultServer,
                                                                           ITeiidServer newDefaultServer ) {
        return new ExecutionConfigurationEvent(EventType.DEFAULT, TargetType.SERVER, oldDefaultServer, newDefaultServer);
    }

    public static ExecutionConfigurationEvent createUnDeployVDBEvent( String vdbName ) {
        return new ExecutionConfigurationEvent(EventType.REMOVE, TargetType.VDB, vdbName);
    }

    public static ExecutionConfigurationEvent createUpdateDataSourceEvent( ITeiidDataSource dataSource ) {
        return new ExecutionConfigurationEvent(EventType.UPDATE, TargetType.DATA_SOURCE, dataSource);
    }

    public static ExecutionConfigurationEvent createUpdateServerEvent( ITeiidServer teiidServer,
                                                                       ITeiidServer updatedServer ) {
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
        if (target == null) {
            throw new IllegalArgumentException(NLS.bind(Messages.valueCannotBeNull, "target")); //$NON-NLS-1$
        }
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
    public ITeiidDataSource getDataSource() {
        if (this.targetType != TargetType.DATA_SOURCE) {
            throw new IllegalStateException(NLS.bind(Messages.invalidTargetTypeForGetDataSourceMethod, 
                                                           this.targetType,
                                                           TargetType.DATA_SOURCE));
        }

        return (ITeiidDataSource)this.target;
    }

    /**
     * @return the event type (never <code>null</code>)
     */
    public EventType getEventType() {
        return this.eventType;
    }

    /**
     * When changing the default teiid instance, this returns the old default teiid instance.
     * 
     * @return the server involved in the event (may be <code>null</code>)
     * @throws IllegalStateException if method is called for a connector event
     */
    public ITeiidServer getServer() {
        if (this.targetType != TargetType.SERVER) {
            throw new IllegalStateException(NLS.bind(Messages.invalidTargetTypeForGetServerMethod,
                                                           this.targetType,
                                                           TargetType.SERVER));
        }

        return (ITeiidServer)this.target;
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
    public ITeiidTranslator getTranslator() {
        if (this.targetType != TargetType.TRANSLATOR) {
            throw new IllegalStateException(NLS.bind(Messages.invalidTargetTypeForGetTranslatorMethod,
                                                           this.targetType,
                                                           TargetType.TRANSLATOR));
        }

        return (ITeiidTranslator)this.target;
    }

    /**
     * When changing the default teiid instance, this returns the new default teiid instance.
     * 
     * @return the updated server involved in the event (may be <code>null</code>)
     * @throws IllegalStateException if method is called for a connector event
     */
    public ITeiidServer getUpdatedServer() {
        if (this.targetType != TargetType.SERVER) {
            throw new IllegalStateException(NLS.bind(Messages.invalidTargetTypeForGetUpdatedServerMethod,
                                                           this.targetType,
                                                           TargetType.SERVER));
        }

        return (ITeiidServer)this.updatedTarget;
    }

    public enum EventType {
        ADD,
        CONNECTING,
        CONNECTED,
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
