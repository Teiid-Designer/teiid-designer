package net.sourceforge.sqlexplorer;

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

import java.net.URL;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

public class URLUtil {
	private URLUtil(){}
	public static URL getResourceURL(String s){
		if(!initialized)
			init();
		URL url = null;
		//if(plugged){
			try {
			  url = new URL(baseURL, s);
			 } catch(Throwable e){
			 }
			 return url;
		//}
		//return ClassLoader.getSystemResource(s);
	}

	static private boolean initialized=false;
	static private void init(){
		SQLExplorerPlugin defaultPlugin=SQLExplorerPlugin.getDefault();
		//if(defaultPlugin!=null){
			baseURL = defaultPlugin.getBundle().getEntry("/");
			//plugged=true;
		//}

		initialized=true;
	}
	private static URL baseURL;

	//private static boolean plugged=false;
	//public static boolean isPlugged(){
	//	if(!initialized)
	//		init();
	//	return plugged;
	//}

	public static URL getPluggableFile(String file){
		if(!initialized)
			init();

		//URL installURL = JFaceDbcPlugin.getDefault().getDescriptor().getInstallURL();
		URL url=null;
		try {
			  //url = new URL(installURL, file);
			url = new URL(getBaseURL(), file);
			 } catch(Throwable e){
			 }

		return url;
	}
	public static URL getBaseURL(){
		if(!initialized)
			init();
		return baseURL;
	}



}


