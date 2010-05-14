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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>Folder</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public final class PluginInfoBeanInfo extends SimpleBeanInfo
{
	public interface PropertyNames
	{
		String AUTHOR = "author"; //$NON-NLS-1$
		String CONTRIBUTORS = "contributors"; //$NON-NLS-1$
		String DESCRIPTIVE_NAME = "descriptiveName"; //$NON-NLS-1$
		String INTERNAL_NAME = "internalName"; //$NON-NLS-1$
		String IS_LOADED = "isLoaded"; //$NON-NLS-1$
		String PLUGIN_CLASS_NAME = "pluginClassName"; //$NON-NLS-1$
		String VERSION = "version"; //$NON-NLS-1$
		String WEB_SITE = "webSite"; //$NON-NLS-1$
	}

	private static PropertyDescriptor[] s_descriptors;

	public PluginInfoBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[8];
			int idx = 0;
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.PLUGIN_CLASS_NAME, PluginInfo.class,
					"getPluginClassName", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.IS_LOADED, PluginInfo.class,
					"isLoaded", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.INTERNAL_NAME, PluginInfo.class,
					"getInternalName", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.DESCRIPTIVE_NAME, PluginInfo.class,
					"getDescriptiveName", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.AUTHOR, PluginInfo.class,
					"getAuthor", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.CONTRIBUTORS, PluginInfo.class,
					"getContributors", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.WEB_SITE, PluginInfo.class,
					"getWebSite", null); //$NON-NLS-1$
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.VERSION, PluginInfo.class,
					"getVersion", null); //$NON-NLS-1$
		}
	}

	@Override
    public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
