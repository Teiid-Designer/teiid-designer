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
public class SessionPluginInfo extends PluginInfo {
	public SessionPluginInfo(PluginInfo pi) throws IllegalArgumentException {
		super(getPassedPluginClassName(pi));
		assignFrom(pi);
	}

	public ISessionPlugin getSessionPlugin() {
		return (ISessionPlugin)getPlugin();
	}

	@Override
    void setPlugin(IPlugin value) throws IllegalArgumentException {
		if (value == null) {
			throw new IllegalArgumentException("Null IPlugin passed"); //$NON-NLS-1$
		}
		if (!(value instanceof ISessionPlugin)) {
			throw new IllegalArgumentException("Plugin not an ISessionPlugin"); //$NON-NLS-1$
		}
		super.setPlugin(value);
	}


	private static String getPassedPluginClassName(PluginInfo pi)
			throws IllegalArgumentException {
		if (pi == null) {
			throw new IllegalArgumentException("Null PluginInfo passed"); //$NON-NLS-1$
		}
		return pi.getPluginClassName();
	}
}

