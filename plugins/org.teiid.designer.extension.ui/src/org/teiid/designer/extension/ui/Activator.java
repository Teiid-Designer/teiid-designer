/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.LoggingUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelRegistry;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;


/**
 * 
 */
public final class Activator extends AbstractUiPlugin {

    /**
     * The shared instance.
     */
    private static Activator _plugin;

    /**
     * @return the shared instance or <code>null</code> if the Eclipse platform is not running
     */
    public static Activator getDefault() {
        return _plugin;
    }

    /**
     * key=metamodel URI, value=metamodel display name
     */
    private final Map<String, String> metamodels = new HashMap<String, String>();

    /**
     * key=model type, value=model type display name
     */
    private final Map<String, String> modelTypes = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * @return the names of the extendable metamodels (never <code>null</code>)
     */
    public Set<String> getExtendableMetamodelNames() {
        return new HashSet<String>(this.metamodels.values());
    }

    /**
     * @param metamodelUri the URI of the extendable metamodel whose name is being requested (cannot be <code>null</code>)
     * @return the metamodel name associated with the specified URI or <code>null</code> if not found
     */
    public String getMetamodelName( String metamodelUri ) {
        CoreArgCheck.isNotEmpty(metamodelUri, "metamodelUri is empty"); //$NON-NLS-1$

        for (String uri : this.metamodels.keySet()) {
            if (uri.equals(metamodelUri)) {
                return this.metamodels.get(uri);
            }
        }

        // not found
        return null;
    }

    /**
     * @param metamodelName the name of the extendable metamodel whose URI is being requested (cannot be <code>null</code>)
     * @return the metamodel URI associated with the specified name or <code>null</code> if not found
     */
    public String getMetamodelUri( String metamodelName ) {
        CoreArgCheck.isNotEmpty(metamodelName, "metamodelName is empty"); //$NON-NLS-1$

        for (Entry<String, String> entry : this.metamodels.entrySet()) {
            if (entry.getValue().equals(metamodelName)) {
                return entry.getKey();
            }
        }

        // not found
        return null;
    }

    /**
     * @param modelTypeName the name of the model type being requested (cannot be <code>null</code>)
     * @return the model type associated with the specified name or <code>null</code> if not found
     */
    public String getModelType( String modelTypeName ) {
        CoreArgCheck.isNotEmpty(modelTypeName, "modelTypeName is empty"); //$NON-NLS-1$

        for (Entry<String, String> entry : this.modelTypes.entrySet()) {
            if (entry.getValue().equals(modelTypeName)) {
                return entry.getKey();
            }
        }

        // not found
        return null;
    }

    /**
     * @param modelType the nmodel type whose display name is being requested (cannot be <code>null</code>)
     * @return the model type localized display name or <code>null</code> if not found
     */
    public String getModelTypeName( String modelType ) {
        CoreArgCheck.isNotEmpty(modelType, "modelType is empty"); //$NON-NLS-1$

        for (Entry<String, String> entry : this.modelTypes.entrySet()) {
            if (entry.getKey().equals(modelType)) {
                return entry.getValue();
            }
        }

        // not found
        return null;
    }

    /**
     * @param metamodelUri the metamodel namespace URI whose available model types are being requested (cannot be <code>null</code>
     *            or empty)
     * @return the model types (never <code>null</code>)
     */
    public Set<String> getModelTypes( String metamodelUri ) {
        return ModelerCore.getMetamodelRegistry().getModelTypes(metamodelUri);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        _plugin = this;

        // initialize logger first so that other methods can use logger
        ((LoggingUtil)UTIL).initializePlatformLogger(this);

        // load metamodel URI/metamodel name map
        Set<String> metamodelUris = ExtensionPlugin.getInstance().getRegistry().getExtendableMetamodelUris();
        MetamodelRegistry metamodelRegistry = ModelerCore.getMetamodelRegistry();

        for (String metamodelUri : metamodelUris) {
            String name = metamodelRegistry.getMetamodelName(metamodelUri);
            this.metamodels.put(metamodelUri, name);

            for (String modelType : metamodelRegistry.getModelTypes(metamodelUri)) {
                if (!this.modelTypes.containsKey(modelType)) {
                    this.modelTypes.put(modelType, metamodelRegistry.getModelTypeName(modelType));
                }
            }
            
        }
    }

}
