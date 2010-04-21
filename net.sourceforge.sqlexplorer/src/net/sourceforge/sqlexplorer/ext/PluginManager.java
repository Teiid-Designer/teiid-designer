package net.sourceforge.sqlexplorer.ext;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sourceforge.sqlexplorer.PluginLoader;
import net.sourceforge.sqlexplorer.URLUtil;
import net.sourceforge.sqlexplorer.dbviewer.model.CatalogNode;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.SchemaNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.TreeViewer;

public class PluginManager {
    public static String asLocalString( final URL url ) throws IOException {
        return asLocalURL(url).getFile();
    }

    /** Logger for this class. */

    public static URL asLocalURL( final URL url ) throws IOException {
        return FileLocator.resolve(url);
        /*		if (!url.getProtocol().equals(PlatformURLHandler.PROTOCOL))
        			return url;
        		java.net.URLConnection connection = url.openConnection();
        		if (!(connection instanceof PlatformURLConnection))
        			return url;
        		//String file = connection.getURL().getFile();
        		//return file;
        		//if (file.endsWith("/") && !file.endsWith(PlatformURLHandler.JAR_SEPARATOR)) //$NON-NLS-1$
        		//	throw new IOException();
        		return ((PlatformURLConnection) connection).getURLAsLocal();*/
    }

    /**
     * Contains a <TT>PluginInfo</TT> object for every plugin that we attempted to load.
     */
    private final List _plugins = new ArrayList();

    /**
     * Contains all plugins (<TT>IPlugin</TT>) successfully loaded. Keyed by <TT>IPlugin.getInternalName()</TT>.
     */
    private final Map _loadedPlugins = new HashMap();

    /**
     * Contains a <TT>SessionPluginInfo</TT> object for evey object in
     * <TT>_loadedPlugins<TT> that is an instance of <TT>ISessionPlugin</TT>.
     */
    private final List _sessionPlugins = new ArrayList();

    /**
     * Contains a <TT>EditorPluginInfo</TT> object for evey object in
     * <TT>_loadedPlugins<TT> that is an instance of <TT>IEditorPlugin</TT>.
     */
    private final List _editorPlugins = new ArrayList();

    /**
     * Collection of active sessions. Keyed by <TT>ISession.getIdentifier()</TT> and contains a <TT>List</TT> of active
     * <TT>ISessionPlugin</TT> objects for the session.
     */
    private final Map _activeSessions = new HashMap();

    public PluginManager() {
    }

    public IAction[] getAddedActions( final SessionTreeNode sessionNode,
                                      final IDbModel node,
                                      final TreeViewer tv ) {
        final List plugins = (List)_activeSessions.get(sessionNode.getIdentifier());
        final List actionList = new ArrayList();
        if (plugins != null) {
            for (final Iterator it = plugins.iterator(); it.hasNext();) {
                final SessionPluginInfo spi = (SessionPluginInfo)it.next();
                final IAction[] objActions = spi.getSessionPlugin().getAddedActions(sessionNode, node, tv);
                if (objActions != null) for (int i = 0; i < objActions.length; ++i)
                    actionList.add(objActions[i]);
            }
            return (IAction[])actionList.toArray(new IAction[0]);
        }
        return null;
    }

    /**
     * @return
     */
    public IActivablePanel[] getAddedPanels( final SessionTreeNode sessionNode,
                                             final IDbModel node ) {
        final List plugins = (List)_activeSessions.get(sessionNode.getIdentifier());
        final List panelsList = new ArrayList();
        if (plugins != null) {
            for (final Iterator it = plugins.iterator(); it.hasNext();) {

                final SessionPluginInfo spi = (SessionPluginInfo)it.next();
                final IActivablePanel[] panels = spi.getSessionPlugin().getAddedPanels(sessionNode, node);
                if (panels != null) for (int i = 0; i < panels.length; ++i)
                    panelsList.add(panels[i]);
            }
            return (IActivablePanel[])panelsList.toArray(new IActivablePanel[0]);
        }
        return new IActivablePanel[0];
    }

    public IDbModel[] getCatalogAddedTypes( final CatalogNode catalogNode,
                                            final SessionTreeNode sessionNode ) {
        final List objTypesList = new ArrayList();
        final List plugins = (List)_activeSessions.get(sessionNode.getIdentifier());
        if (plugins != null) for (final Iterator it = plugins.iterator(); it.hasNext();) {
            final SessionPluginInfo spi = (SessionPluginInfo)it.next();
            final IDbModel[] objTypes = spi.getSessionPlugin().getCatalogAddedTypes(catalogNode, sessionNode);
            if (objTypes != null) for (int i = 0; i < objTypes.length; ++i)
                objTypesList.add(objTypes[i]);
        }
        return (IDbModel[])objTypesList.toArray(new IDbModel[0]);
    }

    public IDbModel[] getDbRootAddedTypes( final DatabaseNode root,
                                           final SessionTreeNode sessionNode ) {
        final List objTypesList = new ArrayList();
        final List plugins = (List)_activeSessions.get(sessionNode.getIdentifier());
        if (plugins != null) for (final Iterator it = plugins.iterator(); it.hasNext();) {

            final SessionPluginInfo spi = (SessionPluginInfo)it.next();
            final IDbModel[] objTypes = spi.getSessionPlugin().getDbRootAddedTypes(root, sessionNode);
            if (objTypes != null) for (int i = 0; i < objTypes.length; ++i)
                objTypesList.add(objTypes[i]);
        }
        return (IDbModel[])objTypesList.toArray(new IDbModel[0]);

    }

    /**
     * @param editor
     * @return
     */
    public IContributionItem[] getEditorContextMenuActions( final SQLEditor editor ) {
        final List plugins = _editorPlugins;
        final List actionList = new ArrayList();
        if (plugins != null) {
            for (final Iterator it = plugins.iterator(); it.hasNext();) {
                final EditorPluginInfo spi = (EditorPluginInfo)it.next();
                final IContributionItem[] objActions = spi.getEditorPlugin().getContextMenuActions(editor);
                if (objActions != null) for (int i = 0; i < objActions.length; ++i)
                    actionList.add(objActions[i]);
            }
            return (IContributionItem[])actionList.toArray(new IContributionItem[0]);
        }
        return null;
    }

    /**
     * @return
     */
    public IAction[] getEditorToolbarActions( final SQLEditor editor ) {
        final List plugins = _editorPlugins;
        final List actionList = new ArrayList();
        if (plugins != null) {
            for (final Iterator it = plugins.iterator(); it.hasNext();) {
                final EditorPluginInfo spi = (EditorPluginInfo)it.next();
                final IAction[] objActions = spi.getEditorPlugin().getEditorToolbarActions(editor);
                if (objActions != null) for (int i = 0; i < objActions.length; ++i)
                    actionList.add(objActions[i]);
            }
            return (IAction[])actionList.toArray(new IAction[0]);
        }
        return null;
    }

    public Map getImageDescriptorsMap() {
        final HashMap map = new HashMap();
        for (final Iterator it = _loadedPlugins.values().iterator(); it.hasNext();) {
            final IPlugin plugin = (IPlugin)it.next();
            try {
                final Map mp = plugin.getIconMap();
                if (mp != null) // Fixed NPE
                map.putAll(mp);
            } catch (final Exception e) {
                final String msg = "Error ocurred calling getImageDescriptorsMap: " + plugin.getInternalName(); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, e);
                // _app.showErrorDialog(msg, th);
            }
        }
        return map;
    }

    public synchronized PluginInfo[] getPluginInformation() {
        return (PluginInfo[])_plugins.toArray(new PluginInfo[_plugins.size()]);
    }

    public IDbModel[] getSchemaAddedTypes( final SchemaNode schemaNode,
                                           final SessionTreeNode sessionNode ) {
        final List objTypesList = new ArrayList();
        final List plugins = (List)_activeSessions.get(sessionNode.getIdentifier());
        if (plugins != null) for (final Iterator it = plugins.iterator(); it.hasNext();) {
            final SessionPluginInfo spi = (SessionPluginInfo)it.next();
            final IDbModel[] objTypes = spi.getSessionPlugin().getSchemaAddedTypes(schemaNode, sessionNode);
            if (objTypes != null) for (int i = 0; i < objTypes.length; ++i)
                objTypesList.add(objTypes[i]);
        }
        return (IDbModel[])objTypesList.toArray(new IDbModel[0]);
    }

    /**
     * @param sessionNode
     * @param group
     * @param model
     * @return
     */
    public IAction[] getTypeActions( final SessionTreeNode sessionNode,
                                     final IDbModel node,
                                     final TreeViewer tv ) {
        final List plugins = (List)_activeSessions.get(sessionNode.getIdentifier());
        final List actionList = new ArrayList();
        if (plugins != null) {
            for (final Iterator it = plugins.iterator(); it.hasNext();) {
                final SessionPluginInfo spi = (SessionPluginInfo)it.next();
                final IAction[] objActions = spi.getSessionPlugin().getTypeActionsAdded(sessionNode, node, tv);
                if (objActions != null) for (int i = 0; i < objActions.length; ++i)
                    actionList.add(objActions[i]);
            }
            return (IAction[])actionList.toArray(new IAction[0]);
        }
        return null;
    }

    /**
     * Initialize plugins.
     */
    public void initializePlugins() {
        for (final Iterator it = _loadedPlugins.values().iterator(); it.hasNext();) {
            final IPlugin plugin = (IPlugin)it.next();
            try {
                // long now = System.currentTimeMillis();
                plugin.initialize();

            } catch (final Throwable th) {
                final String msg = "Error occured initializing plugin: " + plugin.getInternalName(); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, th);

            }
        }
    }

    private void loadPlugin( final Class pluginClass ) {
        final PluginInfo pi = new PluginInfo(pluginClass.getName());
        try {
            // long now = System.currentTimeMillis();
            final IPlugin plugin = (IPlugin)pluginClass.newInstance();
            pi.setPlugin(plugin);
            _plugins.add(pi);
            if (validatePlugin(plugin)) {
                plugin.load();
                pi.setLoaded(true);

                _loadedPlugins.put(plugin.getInternalName(), plugin);
                if (ISessionPlugin.class.isAssignableFrom(pluginClass)) _sessionPlugins.add(new SessionPluginInfo(pi));
                if (IEditorPlugin.class.isAssignableFrom(pluginClass)) _editorPlugins.add(new EditorPluginInfo(pi));
            }
        } catch (final Throwable th) {
            final String msg = "Error occured loading class " + pluginClass.getName() + " from plugin"; //$NON-NLS-1$ //$NON-NLS-2$
            SQLExplorerPlugin.error(msg, th);
        }
    }

    /**
     * 
     */
    public void loadPlugins() {
        final List pluginUrls = new ArrayList();
        File dir = null;

        final URL file1 = URLUtil.getPluggableFile("plugins" + File.separator); //$NON-NLS-1$
        try {
            dir = new File(asLocalString(file1));
        } catch (final Throwable e) {
        }

        if (dir == null) return;

        if (dir.isDirectory()) {
            // String[] tab = { IDialogConstants.OK_LABEL };

            final File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                final File file = files[i];
                if (file.isFile()) {
                    final String fileName = file.getAbsolutePath();
                    if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".jar")) try {
                        URL url = URLUtil.getResourceURL("plugins/" + file.getName()); //$NON-NLS-1$

                        url = file.toURI().toURL();
                        pluginUrls.add(url);
                    } catch (final IOException ex) {
                        final String msg = "Unable to load plugin jar: " + fileName; //$NON-NLS-1$
                        SQLExplorerPlugin.error(msg, ex);

                    }
                }
            }
        }

        final URL[] urls = (URL[])pluginUrls.toArray(new URL[pluginUrls.size()]);

        final PluginLoader tl = new PluginLoader(urls);
        final Class[] classes = tl.getPluginClasses();

        for (int i = 0; i < classes.length; ++i) {
            final Class clazz = classes[i];
            try {

                loadPlugin(clazz);
            } catch (final Throwable th) {
                final String msg = "Error occured loading plugin class: " + clazz.getName(); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, th);
            }
        }
    }

    /**
     * A session is ending.
     * 
     * @param session The session ending.
     * @throws IllegalArgumentException Thrown if a <TT>null</TT> ISession</TT> passed.
     */
    public synchronized void sessionEnding( final SessionTreeNode sessionNode ) {
        if (sessionNode == null) throw new IllegalArgumentException("ISession == null"); //$NON-NLS-1$

        final List plugins = (List)_activeSessions.remove(sessionNode.getIdentifier());
        if (plugins != null) for (final Iterator it = plugins.iterator(); it.hasNext();) {
            final SessionPluginInfo spi = (SessionPluginInfo)it.next();
            try {
                spi.getSessionPlugin().sessionEnding(sessionNode);
            } catch (final Throwable th) {
                final String msg = "Error occured in IPlugin.sessionEnding() for " + spi.getPlugin().getDescriptiveName(); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, th);
                // _app.showErrorDialog(msg, th);
            }
        }
    }

    /**
     * A new session is starting.
     * 
     * @param session The new session.
     * @throws IllegalArgumentException Thrown if a <TT>null</TT> ISession</TT> passed.
     */
    public synchronized void sessionStarted( final SessionTreeNode sessionNode ) {
        if (sessionNode == null) throw new IllegalArgumentException("ISession == null"); //$NON-NLS-1$
        final List plugins = new ArrayList();
        _activeSessions.put(sessionNode.getIdentifier(), plugins);
        for (final Iterator it = _sessionPlugins.iterator(); it.hasNext();) {
            final SessionPluginInfo spi = (SessionPluginInfo)it.next();
            try {
                if (spi.getSessionPlugin().sessionStarted(sessionNode)) plugins.add(spi);
            } catch (final Throwable th) {
                final String msg = "Error occured in IPlugin.sessionStarted() for " + spi.getPlugin().getDescriptiveName(); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, th);
                // _app.showErrorDialog(msg, th);
            }
        }
    }

    /**
     * Unload all plugins.
     */
    public synchronized void unloadPlugins() {
        for (final Iterator it = _loadedPlugins.values().iterator(); it.hasNext();) {
            final IPlugin plugin = (IPlugin)it.next();
            try {
                plugin.unload();
            } catch (final Throwable th) {
                final String msg = "Error ocured unloading plugin: " + plugin.getInternalName(); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, th);
                // _app.showErrorDialog(msg, th);
            }
        }
    }

    private boolean validatePlugin( final IPlugin plugin ) {
        final String pluginInternalName = plugin.getInternalName();
        if (pluginInternalName == null || pluginInternalName.trim().length() == 0) {
            SQLExplorerPlugin.error("Plugin " + plugin.getClass().getName() + "doesn't return a valid getInternalName()", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }

        if (_loadedPlugins.get(pluginInternalName) != null) {
            SQLExplorerPlugin.error("A Plugin with the internal name " + pluginInternalName + " has already been loaded", new Exception()); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
        // System.out.println("validated");
        return true;
    }
}
