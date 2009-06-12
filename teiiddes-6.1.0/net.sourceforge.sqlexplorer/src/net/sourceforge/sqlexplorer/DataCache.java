/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
package net.sourceforge.sqlexplorer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

//import com.bigfoot.colbell.fw.xml.XMLObjectCache;

//import com.bigfoot.colbell.squirrel.IApplication;
//import com.bigfoot.colbell.squirrel.resources.Resources;
//import com.bigfoot.colbell.squirrel.util.ApplicationFiles;


/**
 * XML cache of JDBC drivers and aliases.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataCache {
	
	private final static Class SQL_ALIAS_IMPL = SQLAlias.class;
	private final static Class SQL_DRIVER_IMPL = SQLDriver.class;
         // private Logger _logger;
          private SQLDriverManager _driverMgr;


	/** Application API. */
	//private IApplication _app;

	/** Cache that contains data. */
	private XMLObjectCache _cache = new XMLObjectCache();

	/**
	 * Ctor. Loads drivers and aliases from the XML document.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 				Thrown if <TT>null</TT> <TT>IApplication</TT>
	 * 				passed.
	 *
	 * @throws	IllegalStateException
	 * 				Thrown if no <TT>SQLDriverManager</TT> or <TT>Logger</TT>
	 * 				exists in IApplication.
	 */
	public DataCache(SQLDriverManager dm) throws IllegalArgumentException {
		super();
		_driverMgr=dm;

		loadDrivers();
		loadAliases();
	}

	/**
	 * Save cached objects. JDBC drivers are saved to
	 * <CODE>ApplicationFiles.getUserDriversFileName()</CODE> and aliases are
	 * saved to <CODE>ApplicationFiles.getUserAliasesFileName()</CODE>.
	 */
	public void save() {
		try {
			_cache.saveAllForClass(ApplicationFiles.USER_DRIVER_FILE_NAME, SQL_DRIVER_IMPL);
		} catch (IOException ex) {
			SQLExplorerPlugin.error("Error occured saving drivers",ex); //$NON-NLS-1$		
		} catch (XMLException ex) {
			SQLExplorerPlugin.error("Error occured saving drivers",ex); //$NON-NLS-1$
		
		}
		try {
			_cache.saveAllForClass(ApplicationFiles.USER_ALIAS_FILE_NAME, SQL_ALIAS_IMPL);
		} catch (Exception ex) {
			SQLExplorerPlugin.error("Error occured saving aliases",ex); //$NON-NLS-1$
		}
	}

	/**
	 * Return the <TT>ISQLDriver</TT> for the passed identifier.
	 */
	public ISQLDriver getDriver(IIdentifier id) {
		return (ISQLDriver)_cache.get(SQL_DRIVER_IMPL, id);
	}

	public void addDriver(ISQLDriver sqlDriver) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, DuplicateObjectException,MalformedURLException {
	    _driverMgr.registerSQLDriver(sqlDriver);
		_cache.add(sqlDriver);
	}

	public void removeDriver(ISQLDriver sqlDriver) {
		_cache.remove(SQL_DRIVER_IMPL, sqlDriver.getIdentifier());
		try {
		    _driverMgr.unregisterSQLDriver(sqlDriver);
		} catch (Exception ex) {
			SQLExplorerPlugin.error("Error occured removing driver",ex); //$NON-NLS-1$
		}
	}

	public Iterator drivers() {
		return _cache.getAllForClass(SQL_DRIVER_IMPL);
	}

	public void addDriversListener(IObjectCacheChangeListener lis) {
		_cache.addChangesListener(lis, SQL_DRIVER_IMPL);
	}

	public void removeDriversListener(IObjectCacheChangeListener lis) {
		_cache.removeChangesListener(lis, SQL_DRIVER_IMPL);
	}


	public ISQLAlias getAlias(IIdentifier id) {
		return (ISQLAlias)_cache.get(SQL_ALIAS_IMPL, id);
	}

	public Iterator aliases() {
		return _cache.getAllForClass(SQL_ALIAS_IMPL);
	}

	public void addAlias(ISQLAlias alias) throws DuplicateObjectException {
		_cache.add(alias);
	}

	public void removeAlias(ISQLAlias alias) {
		_cache.remove(SQL_ALIAS_IMPL, alias.getIdentifier());
	}

	public Iterator getAliasesForDriver(ISQLDriver driver) {
		ArrayList data = new ArrayList();
		for (Iterator it = aliases(); it.hasNext();) {
			ISQLAlias alias = (ISQLAlias)it.next();
		    if (driver.equals(getDriver(alias.getDriverIdentifier()))) {
				data.add(alias);
			}
		}
		return data.iterator();
	}

	public void addAliasesListener(IObjectCacheChangeListener lis) {
		_cache.addChangesListener(lis, SQL_ALIAS_IMPL);
	}

	public void removeAliasesListener(IObjectCacheChangeListener lis) {
		_cache.removeChangesListener(lis, SQL_ALIAS_IMPL);
	}

	private void loadDrivers() {
		//final Logger logger = _logger;
		try {
			_cache.load(ApplicationFiles.USER_DRIVER_FILE_NAME);
			if (!drivers().hasNext()) {
				loadDefaultDrivers();
			}
			else{
				fixupDrivers();
			}
		} catch (FileNotFoundException ex) {
			loadDefaultDrivers();// first time user has run pgm.
		} catch (Exception ex) {
			loadDefaultDrivers();
		}

		registerDrivers();
	}

	public ISQLAlias createAlias(IIdentifier id) {
		return new SQLAlias(id);
	}

	public ISQLDriver createDriver(IIdentifier id) {
		return new SQLDriver(id);
	}
	
	private void fixupDrivers()
	{
		for (Iterator it = drivers(); it.hasNext();)
		{
			ISQLDriver driver = (ISQLDriver)it.next();
			String[] fileNames = driver.getJarFileNames();
			if (fileNames == null || fileNames.length == 0)
			{
//				String fileName = null;
				String fileNameArray[] = driver.getJarFileNames(); 
				if (fileNameArray != null && fileNameArray.length > 0)
				{
					driver.setJarFileNames(fileNameArray);
					try
					{
						driver.setJarFileName(null);
					}
					catch (ValidationException ignore)
					{
					}
				}
			}
		}
	}


	private void loadDefaultDrivers() {
		final URL url = URLUtil.getResourceURL("default_drivers.xml"); //$NON-NLS-1$
		try
		{
			InputStreamReader isr = new InputStreamReader(url.openStream());
			try
			{
				_cache.load(isr);
			}
			finally
			{
				isr.close();
			}
		}
		catch (Exception ex)
		{
			SQLExplorerPlugin.error("Error loading default driver file",ex); //$NON-NLS-1$
		}

	}

	private void registerDrivers() {
		SQLDriverManager driverMgr = _driverMgr;
		for (Iterator it = drivers(); it.hasNext();) {
			ISQLDriver sqlDriver = (ISQLDriver)it.next();
			try {
			    driverMgr.registerSQLDriver(sqlDriver);
			} catch (Throwable th) {
		//		s_log.error("Error registering SQL driver ",th); //$NON-NLS-1$
			}
		}
	}

	private void loadAliases() {
		try {
			_cache.load(ApplicationFiles.USER_ALIAS_FILE_NAME);
		} catch (FileNotFoundException ignore) { // first time user has run pgm.
		} catch (XMLException ex) {
			SQLExplorerPlugin.error("Error loading aliases file ",ex); //$NON-NLS-1$
		} catch (DuplicateObjectException ex) {
			SQLExplorerPlugin.error("Error loading aliases file ",ex); //$NON-NLS-1$
		}
	}
}
