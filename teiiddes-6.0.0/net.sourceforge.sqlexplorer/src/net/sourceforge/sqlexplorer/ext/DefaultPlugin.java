package net.sourceforge.sqlexplorer.ext;

import java.io.File;
import java.util.Map;

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

public abstract class DefaultPlugin implements IPlugin {
    /**
     * Called on application startup after application started.
     */
    public void initialize() {
    }

    /**
     * Called when app shutdown.
     */
    public void unload() {
    }

    /**
     * Returns a comma separated list of other contributors.
     * 
     * @return Contributors names.
     */
    public String getContributors() {
        return ""; //$NON-NLS-1$
    }

    /**
     * Returns the home page for this plugin.
     * 
     * @return the home page for this plugin.
     */
    /*	public String getWebSite()
    	{
    		return Version.getWebSite();
    	}*/

    /**
     * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
     * <TT>getPluginAppSettingsFolder</TT> directory.
     * 
     * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
     */
    public String getHelpFileName() {
        return null;
    }

    /**
     * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
     * <TT>getPluginAppSettingsFolder</TT> directory.
     * 
     * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
     */
    public String getChangeLogFileName() {
        return null;
    }

    /**
     * Returns the name of the licence file for the plugin. This should be a text or HTML file residing in the
     * <TT>getPluginAppSettingsFolder</TT> directory.
     * 
     * @return the licence file name or <TT>null</TT> if plugin doesn't have a change log.
     */
    public String getLicenceFileName() {
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#load()
     */
    public void load() {

    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getInternalName()
     */
    public String getInternalName() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getDescriptiveName()
     */
    public String getDescriptiveName() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getAuthor()
     */
    public String getAuthor() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getWebSite()
     */
    public String getWebSite() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getVersion()
     */
    public String getVersion() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getPluginAppSettingsFolder()
     */
    public File getPluginAppSettingsFolder() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getPluginUserSettingsFolder()
     */
    public File getPluginUserSettingsFolder() {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.ext.IPlugin#getIconMap()
     */
    public Map getIconMap() {

        return null;
    }

    /**
     * Return the current application API.
     * 
     * @return The current application API.
     */
    /*public final IApplication getApplication()
    {
    	return _app;
    }*/

    /**
     * Return the folder with the Squirrel application folder that belongs to this plugin. If it doesn't exist then create it.
     * This would normally be
     * 
     * <PRE>
     * &lt;squirrel_app&gt;/plugins/&lt;plugin_internal_name&gt;
     * </PRE>
     * 
     * @return Plugins application folder.
     * @throws IllegalStateException if plugin doesn't have an internal name.
     * @throws IOException An error occured retrieving/creating the folder.
     */
    /*	public synchronized File getPluginAppSettingsFolder()
    		throws IllegalStateException, IOException
    	{
    		final String internalName = getInternalName();
    		if (internalName == null || internalName.trim().length() == 0)
    		{
    			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
    		}
    		final File pluginDir = new ApplicationFiles().getPluginsDirectory();
    		final File file = new File(pluginDir, internalName);
    		if (!file.exists())
    		{
    			file.mkdirs();
    		}

    		if (!file.isDirectory())
    		{
    			throw new IOException(
    				"Cannot create directory as a file of the same name already exists: " + file.getAbsolutePath());
    		}

    		return file;
    	}*/

    /**
     * Return the folder with the users home directory that belongs to this plugin. If it doesn't exist then create it. This would
     * normally be
     * 
     * <PRE>
     * &lt;user_home&gt;/.squirrel-sql/plugins/&lt;plugin_internal_name&gt;
     * </PRE>
     * 
     * @return Plugins user folder.
     * @throws IllegalStateException if plugin doesn't have an internal name.
     * @throws IOException An error occured retrieving/creating the folder.
     */
    /*	public synchronized File getPluginUserSettingsFolder()
    		throws IllegalStateException, IOException
    	{
    		final String internalName = getInternalName();
    		if (internalName == null || internalName.trim().length() == 0)
    		{
    			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
    		}
    		String name =
    			new ApplicationFiles().getPluginsUserSettingsDirectory()
    				+ File.separator
    				+ internalName
    				+ File.separator;
    		File file = new File(name);
    		if (!file.exists())
    		{
    			file.mkdirs();
    		}

    		if (!file.isDirectory())
    		{
    			throw new IOException(
    				"Cannot create directory as a file of the same name already exists: " + name);
    		}

    		return file;
    	}*/

    /**
     * Create panels for the Global Preferences dialog.
     * 
     * @return <TT>null</TT> to indicate that this plugin doesn't require any panels in the Global Preferences Dialog.
     */
    /*	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
    	{
    		return null;
    	}*/

    /**
     * Create panels for the New Session Properties dialog.
     * 
     * @return <TT>null</TT>to indicate that this plugin doesn't require any panels in the New Session Properties Dialog.
     */
    /*	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
    	{
    		return null;
    	}*/
}
