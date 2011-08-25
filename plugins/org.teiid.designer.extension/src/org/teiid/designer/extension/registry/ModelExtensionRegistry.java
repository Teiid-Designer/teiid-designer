/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.registry;

import static org.teiid.designer.extension.ExtensionPlugin.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.properties.ModelExtensionProperty;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public final class ModelExtensionRegistry {

    /**
     * Key is namespace prefix, value is model extension definition. Never <code>null</code>.
     */
    private final Map<String, ModelExtensionDefinition> definitions;

    /**
     * A collection of registry listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<RegistryListener> listeners;

    /**
     * Key is namespace URI, value is namespace prefix. Never <code>null</code>.
     */
    private final Map<String, String> namespaces;

    /**
     * Parser for the *.mxd file. Creates the model extension definition. Never <code>null</code>.
     */
    private ModelExtensionDefinitionParser parser;

    /**
     * @throws IllegalStateException if there is a problem with the model extension XSD
     */
    public ModelExtensionRegistry() throws IllegalStateException {
        this.definitions = new HashMap<String, ModelExtensionDefinition>();
        this.listeners = new CopyOnWriteArrayList<RegistryListener>();
        this.namespaces = new HashMap<String, String>();
        this.parser = new ModelExtensionDefinitionParser();
    }

    /**
     * @param definitionStream the model extension input stream (cannot be <code>null</code>)
     * @param assistant the model extension assistant (cannot be <code>null</code>)
     * @return the model extension definition (never <code>null</code>)
     * @throws Exception if the definition file is <code>null</code> or if there is a problem parsing the file
     */
    public ModelExtensionDefinition addDefinition( InputStream definitionStream,
                                                   ModelExtensionAssistant assistant ) throws Exception {
        ModelExtensionDefinition definition = this.parser.parse(definitionStream, assistant);
        assert definition != null : "parser should not return a null model extension definition"; //$NON-NLS-1$

        String namespacePrefix = definition.getNamespacePrefix();

        // don't allow a namespace prefix that has already been registered
        if (this.definitions.containsKey(namespacePrefix)) {
            throw new Exception(NLS.bind(Messages.namespacePrefixAlreadyRegistered, namespacePrefix));
        }

        String namespaceUri = definition.getNamespaceUri();

        // don't allow a namespace URI that has already been registered
        if (this.namespaces.containsKey(namespaceUri)) {
            throw new Exception(NLS.bind(Messages.namespaceUriAlreadyRegistered, namespaceUri));
        }

        // add to registry
        this.definitions.put(namespacePrefix, definition);
        this.namespaces.put(namespaceUri, namespacePrefix);

        // notify registry listeners
        fireEvent(RegistryEvent.createAddDefinitionEvent(definition));

        return definition;
    }

    /**
     * @param listener the listener being added
     * @return <code>true</code> if the listener was successfully added
     */
    public boolean addListener( RegistryListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        return this.listeners.add(listener);
    }

    /**
     * @param metaclassName the metaclass name whose extension properties are being created (cannot be <code>null</code>)
     * @return the created extension properties all set to their default values (never <code>null</code>)
     */
    public Collection<ModelExtensionProperty> createProperties( String metaclassName ) {
        Collection<ModelExtensionProperty> newProperties = new ArrayList<ModelExtensionProperty>();

        for (ModelExtensionDefinition definition : getDefinitions(QueryType.METACLASS, metaclassName)) {
            ModelExtensionAssistant assistant = definition.getModelExtensionAssistant();

            for (ModelExtensionPropertyDefinition propDefn : definition.getPropertyDefinitions(metaclassName)) {
                newProperties.add(assistant.createProperty(propDefn));
            }
        }

        return newProperties;
    }

    /**
     * @param event the event being broadcast (cannot be <code>null</code>)
     */
    private void fireEvent( RegistryEvent event ) {
        assert event != null : "event is null"; //$NON-NLS-1$

        for (RegistryListener listener : this.listeners) {
            try {
                listener.process(event);
            } catch (Exception e) {
                // don't let one listener exception stop others from being notified
                Util.log(e);
                removeListener(listener);
            }
        }
    }

    /**
     * @return a collection of all the model extension definitions (never <code>null</code>)
     */
    public Collection<ModelExtensionDefinition> getAllDefinitions() {
        return this.definitions.values();
    }

    /**
     * @return a collection of all model extension definition namespace prefixes (never <code>null</code>)
     */
    public Set<String> getAllNamespacePrefixes() {
        return new HashSet<String>(namespaces.values());
    }

    /**
     * @return a collection of all model extension definition namespaces (never <code>null</code>)
     */
    public Set<String> getAllNamespaces() {
        return this.namespaces.keySet();
    }

    /**
     * @param namespacePrefix the namespace prefix whose model extension definition is being requested (cannot be <code>null</code>
     *            or empty)
     * @return the model extension definition or <code>null</code> if not found
     */
    public ModelExtensionDefinition getDefinition( String namespacePrefix ) {
        Collection<ModelExtensionDefinition> definitions = getDefinitions(QueryType.NAMESPACE_PREFIX, namespacePrefix);

        if (definitions.isEmpty()) {
            return null;
        }

        assert definitions.size() == 1 : "More than one model extenson definition found for namespace " + namespacePrefix; //$NON-NLS-1$
        return definitions.iterator().next();
    }

    /**
     * @param queryType the type of query being run (cannot be <code>null</code>)
     * @param value the data passed to the query (cannot be <code>null</code> or empty)
     * @return the matching definitions (never <code>null</code>)
     * @throws IllegalStateException if the query type is unknown
     */
    private Collection<ModelExtensionDefinition> getDefinitions( QueryType queryType,
                                                                 String value ) {
        CoreArgCheck.isNotNull(queryType, "queryType is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(value, "value is empty"); //$NON-NLS-1$

        // collect definitions that extend a metaclass
        if (QueryType.METACLASS == queryType) {
            Collection<ModelExtensionDefinition> matchingDefinitions = new ArrayList<ModelExtensionDefinition>();

            for (ModelExtensionDefinition definition : getAllDefinitions()) {
                if (definition.extendsMetaclass(value)) {
                    matchingDefinitions.add(definition);
                }
            }

            return matchingDefinitions;
        }

        // collect definitions that extend a metamodel
        if (QueryType.METAMODEL_URI == queryType) {
            Collection<ModelExtensionDefinition> matchingDefinitions = new ArrayList<ModelExtensionDefinition>();

            for (ModelExtensionDefinition definition : getAllDefinitions()) {
                if (definition.extendsMetamodelUri(value)) {
                    matchingDefinitions.add(definition);
                }
            }

            return matchingDefinitions;
        }

        // collect definition of a namespace
        if (QueryType.NAMESPACE_PREFIX == queryType) {
            ModelExtensionDefinition definition = this.definitions.get(value);

            if (definition == null) {
                return Collections.emptyList();
            }

            return Collections.singleton(definition);
        }

        // collect definitions of a namespace URI
        if (QueryType.NAMESPACE_URI == queryType) {
            String namespacePrefix = this.namespaces.get(value);

            if (namespacePrefix == null) {
                return Collections.emptyList();
            }

            ModelExtensionDefinition definition = this.definitions.get(namespacePrefix);
            assert definition != null : "Should have a model extension definition for prefix " + namespacePrefix; //$NON-NLS-1$

            return Collections.singleton(definition);
        }

        // unknown query type
        assert false;

        // should never get here
        throw new IllegalStateException(NLS.bind(Messages.unknownRegistryQueryType, queryType));
    }

    /**
     * @param namespacePrefix the namespace prefix whose model extension assistant is being requested (cannot be <code>null</code>)
     * @return the model extension assistant (<code>null</code> if an assistant is not found)
     */
    public ModelExtensionAssistant getModelExtensionAssistant( String namespacePrefix ) {
        CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$
        Collection<ModelExtensionDefinition> defns = getDefinitions(QueryType.NAMESPACE_PREFIX, namespacePrefix);

        if (defns.isEmpty()) {
            return null;
        }

        // there is a major problem is more than one definition found
        assert defns.size() == 1 : "Found more than one matching definition for namespace prefix " + namespacePrefix; //$NON-NLS-1$
        return defns.iterator().next().getModelExtensionAssistant();
    }

    /**
     * @param metaclassName the metaclass name whose model extension assistants are being requested
     * @return the model extension assistants of model extension definitions that extend the specified metaclass name (never
     *         <code>null</code>)
     */
    public Collection<ModelExtensionAssistant> getModelExtensionAssistants( String metaclassName ) {
        Collection<ModelExtensionAssistant> assistants = new ArrayList<ModelExtensionAssistant>();

        for (ModelExtensionDefinition definition : getDefinitions(QueryType.METACLASS, metaclassName)) {
            if (definition.extendsMetaclass(metaclassName)) {
                assistants.add(definition.getModelExtensionAssistant());
            }
        }

        return assistants;
    }

    /**
     * @param metaclassName the metaclass name whose property definition is being requested (cannot be <code>null</code> or empty)
     * @param id the property definition identifier (cannot be <code>null</code> or empty)
     * @return the requested property definition or <code>null</code> if not found
     */
    public ModelExtensionPropertyDefinition getPropertyDefinition( String metaclassName,
                                                                   String id ) {
        CoreArgCheck.isNotEmpty(id, "id is empty"); //$NON-NLS-1$

        // get the namespace prefix from the id
        String namespacePrefix = ModelExtensionPropertyDefinition.Utils.getNamespacePrefix(id);

        if (!CoreStringUtil.isEmpty(namespacePrefix)) {
            ModelExtensionDefinition definition = getDefinition(namespacePrefix);

            if (definition != null) {
                for (ModelExtensionPropertyDefinition propDefn : definition.getPropertyDefinitions(metaclassName)) {
                    if (propDefn.getId().equals(id)) {
                        return propDefn;
                    }
                }
            }
        }

        // not found
        return null;
    }

    /**
     * @param namespacePrefix the namespace prefix containing the extension property definitions being requested (cannot be
     *            <code>null</code> or empty )
     * @param metaclassName the metaclass name whose extension property definitions are being requested (cannot be <code>null</code>
     *            or empty )
     * @return the property definitions (never <code>null</code>)
     */
    public Collection<ModelExtensionPropertyDefinition> getPropertyDefinitions( String namespacePrefix,
                                                                                String metaclassName ) {
        CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is empty"); //$NON-NLS-1$

        ModelExtensionDefinition definition = getDefinition(namespacePrefix);

        if (definition != null) {
            return definition.getPropertyDefinitions(metaclassName);
        }

        return Collections.emptyList();
    }

    /**
     * @param namespacePrefix the namespace prefix being checked (cannot be <code>null</code> or empty)
     * @return <code>true</code> if there is a model extension definition with that namespace prefix registered
     */
    public boolean isRegistered( String namespacePrefix ) {
        return this.definitions.containsKey(namespacePrefix);
    }

    /**
     * @param listener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully removed
     */
    public boolean removeListener( RegistryListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * The valid registry query types.
     * 
     * @see ModelExtensionRegistry#getDefinitions(QueryType, String)
     */
    private enum QueryType {

        /**
         * Search by metaclass name.
         */
        METACLASS,

        /**
         * Search by metamodel URI.
         */
        METAMODEL_URI,

        /**
         * Search by namespace prefix.
         */
        NAMESPACE_PREFIX,

        /**
         * Search by namespace URI.
         */
        NAMESPACE_URI
    }

}
