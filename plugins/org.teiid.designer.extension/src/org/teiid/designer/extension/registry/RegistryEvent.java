/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.registry;

import org.teiid.designer.extension.definition.ModelExtensionDefinition;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * A <code>RegistryEvent</code> is broadcast to all registered listeners when a model extension definition is added, changed, or
 * removed from the registry.
 */
public class RegistryEvent {

    /**
     * @param definition the model extension definition that was added to the registry (cannot be <code>null</code>)
     * @return the event that should be broadcast (never <code>null</code>)
     */
    public static RegistryEvent createAddDefinitionEvent( ModelExtensionDefinition definition ) {
        CoreArgCheck.isNotNull(definition, "definition is null"); //$NON-NLS-1$
        return new RegistryEvent(Type.ADD, definition);
    }

    /**
     * @param definition the model extension definition that was changed in the registry (cannot be <code>null</code>)
     * @return the event that should be broadcast (never <code>null</code>)
     */
    public static RegistryEvent createChangeDefinitionEvent( ModelExtensionDefinition definition ) {
        CoreArgCheck.isNotNull(definition, "definition is null"); //$NON-NLS-1$
        return new RegistryEvent(Type.CHANGE, definition);
    }

    /**
     * @param definition the model extension definition that was removed from the registry (cannot be <code>null</code>)
     * @return the event that should be broadcast (never <code>null</code>)
     */
    public static RegistryEvent createRemoveDefinitionEvent( ModelExtensionDefinition definition ) {
        CoreArgCheck.isNotNull(definition, "definition is null"); //$NON-NLS-1$
        return new RegistryEvent(Type.REMOVE, definition);
    }

    /**
     * The definition that caused the event to be created (never <code>null</code>).
     */
    private final ModelExtensionDefinition definition;

    /**
     * The event type (never <code>null</code>).
     */
    private final Type type;

    /**
     * @param type the event type (cannot be <code>null</code>)
     * @param definition the definition that caused the event to be constructed (cannot be <code>null</code>)
     */
    private RegistryEvent( Type type,
                           ModelExtensionDefinition definition ) {
        assert type != null : "Type should not be null"; //$NON-NLS-1$
        CoreArgCheck.isNotNull(definition, "definition is null"); //$NON-NLS-1$

        this.type = type;
        this.definition = definition;
    }

    /**
     * @return the definition that caused the event to be created (never <code>null</code>)
     */
    public ModelExtensionDefinition getDefinition() {
        return this.definition;
    }

    /**
     * @return <code>true</code> if the event was created because a definition was added to the registry
     */
    public boolean isAdd() {
        return (Type.ADD == this.type);
    }

    /**
     * @return <code>true</code> if the event was created because a definition was changed in the registry
     */
    public boolean isChange() {
        return (Type.CHANGE == this.type);
    }

    /**
     * @return <code>true</code> if the event was created because a definition was removed from the registry
     */
    public boolean isRemove() {
        return (Type.REMOVE == this.type);
    }

    /**
     * The valid event types.
     */
    private enum Type {
        /**
         * The event type for an add definition.
         */
        ADD,

        /**
         * The event type for a change definition.
         */
        CHANGE,

        /**
         * The event type for a remove definition.
         */
        REMOVE
    }

}
