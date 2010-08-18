/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.designer.runtime.connection.IConnectionProperties;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 *
 */
public class MockObjectFactory {

    /**
     * Creates a mock <code>ConnectionFactory</code>. The name and the properties can be obtained. The connector type property
     * value can be obtained.
     * 
     * @param name the name of the translator
     * @param translatorTypeName the name of the translator type of this translator
     * @return the translator
     */
    public static Translator createTranslator( final String name,
                                               final String translatorTypeName ) {
        final Translator connectionFactory = mock(Translator.class);
        final Properties props = new Properties();
        props.setProperty(IConnectionProperties.CONNECTOR_TYPE, translatorTypeName);

        when(connectionFactory.getName()).thenReturn(name);
        when(connectionFactory.getProperties()).thenReturn(props);
        when(connectionFactory.getPropertyValue(IConnectionProperties.CONNECTOR_TYPE)).thenReturn(translatorTypeName);

        return connectionFactory;
    }

    /**
     * Creates a <code>TeiidTranslator</code> using a mock <code>ConnectionFactory</code> and mock <code>TranslatorType</code>.
     * The names can be obtained from the translator and translator type. The translator type name can be obtained from the
     * translator properties.
     * 
     * @param name the name of the translator
     * @param translatorTypeName the name of the translator type
     * @return the translator
     * @since 7.0
     */
    public static TeiidTranslator createTeiidTranslator( final String name,
                                                         final String translatorTypeName ) {
        final Translator translator = createTranslator(name, translatorTypeName);

        return new TeiidTranslator(translator, new ArrayList<PropertyDefinition>(), createExecutionAdmin());
    }

    /**
     * Creates a <code>TeiidTranslator</code> using a mock <code>ConnectionFactory</code> and mock <code>TranslatorType</code>.
     * The names can be obtained from the translator and translator type. The translator type name can be obtained from the
     * translator properties.
     * 
     * @param name the name of the translator
     * @param translatorTypeName the name of the translator type
     * @return the translator
     * @since 7.0
     */
    public static TeiidTranslator createTeiidTranslator( final String name,
                                                         final String translatorTypeName,
                                                         final Collection<PropertyDefinition> propertyDefs ) {
        final Translator translator = createTranslator(name, translatorTypeName);

        return new TeiidTranslator(translator, propertyDefs, createExecutionAdmin());
    }

    public static ExecutionAdmin createExecutionAdmin() {
        final Server server = mock(Server.class);
        final Admin adminApi = mock(Admin.class);
        final EventManager eventManager = mock(EventManager.class);
        final ExecutionAdmin admin = mock(ExecutionAdmin.class);

        when(admin.getServer()).thenReturn(server);
        when(admin.getAdminApi()).thenReturn(adminApi);
        when(admin.getEventManager()).thenReturn(eventManager);

        return admin;
    }

    /**
     * Test must use <code>@PrepareForTest( {DqpPlugin.class} )</code> annotation.
     * 
     * @return the plugin instance
     */
    public static DqpPlugin createDqpPlugin() {
        mockStatic(DqpPlugin.class);
        DqpPlugin plugin = mock(DqpPlugin.class);
        when(DqpPlugin.getInstance()).thenReturn(plugin);
        IEclipsePreferences prefs = mock(IEclipsePreferences.class);
        when(plugin.getPreferences()).thenReturn(prefs);
        return plugin;
    }

    /**
     * Test must use <code>@PrepareForTest( {ModelerCore.class} )</code> annotation.
     * 
     * @return the model container
     */
    public static Container createModelContainer() {
        mockStatic(ModelerCore.class);
        Container container = mock(Container.class);
        ChangeNotifier changeNotifier = mock(ChangeNotifier.class);
        when(container.getChangeNotifier()).thenReturn(changeNotifier);
        try {
            when(ModelerCore.getModelContainer()).thenReturn(container);
        } catch (CoreException e) {
            // should not happen
        }

        return container;
    }

    /**
     * Creates a mock <code>ModelResource</code>. The item name and parent can be obtained from the resource. The path can be
     * obtained from the parent.
     * 
     * @param name the name of the model
     * @param parentPath the model's parent path
     * @return the model resource
     */
    public static ModelResource createModelResource( final String name,
                                                     final String parentPath ) {
        final ModelResource parent = mock(ModelResource.class);
        when(parent.getPath()).thenReturn(new Path(parentPath));

        final ModelResource modelResource = mock(ModelResource.class);
        when(modelResource.getItemName()).thenReturn(name);
        when(modelResource.getParent()).thenReturn(parent);

        return modelResource;
    }

    /**
     * Test must use <code>@PrepareForTest( {ResourcesPlugin.class} )</code> annotation.
     * 
     * @return the plugin instance
     */
    public static ResourcesPlugin createResourcesPlugin() {
        mockStatic(ResourcesPlugin.class);
        ResourcesPlugin plugin = mock(ResourcesPlugin.class);
        when(ResourcesPlugin.getPlugin()).thenReturn(plugin);
        IWorkspace workspace = mock(IWorkspace.class);
        when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);
        return plugin;
    }

    /**
     * Prevent construction.
     */
    private MockObjectFactory() {
        // nothing to do
    }

}
