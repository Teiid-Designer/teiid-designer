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
import java.util.Map;

/**
 * Base interface for all plugins.
 */
public interface IPlugin
{
	/**
	 * Called on application startup before application started up.
	 *
	 * @param	app	Application API.
	 */
	void load() throws PluginException;

	/**
	 * Called on application startup after application started.
	 */
	void initialize() throws PluginException;

	/**
	 * Called when app shutdown.
	 */
	void unload() throws PluginException;

	/**
	 * Returns the name by which this plugin is uniquely identified.
	 *
	 * @return	the name by which this plugin is uniquely identified.
	 */
	String getInternalName();

	/**
	 * Returns the descriptive name for this plugin.
	 *
	 * @return	the descriptive name for this plugin.
	 */
	String getDescriptiveName();

	/**
	 * Returns the authors name.
	 *
	 * @return	the authors name.
	 */
	String getAuthor();

	/**
	 * Returns a comma separated list of other contributors.
	 *
	 * @return	Contributors names.
	 */
	String getContributors();

	/**
	 * Returns the home page for this plugin.
	 *
	 * @return	the home page for this plugin.
	 */
	String getWebSite();

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return	the current version of this plugin.
	 */
	String getVersion();

	/**
	 * Returns the name of the Help file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Help file name or <TT>null</TT> if plugin doesn't have
	 * 			a help file.
	 */
	String getHelpFileName();

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	String getChangeLogFileName();

	/**
	 * Returns the name of the licence file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the licence file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	String getLicenceFileName();

	File getPluginAppSettingsFolder() throws IOException, IllegalStateException;

	
	File getPluginUserSettingsFolder() throws IllegalStateException, IOException;
	
	
	Map getIconMap();

}