package org.teiid.designer.webservice.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class WebServiceLibPlugin extends Plugin implements WebServiceLibConstants {

    /**
     * The singleton instance of this plugin.
     */
    private static WebServiceLibPlugin plugin = null;

    private IPath pluginPath;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	    super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
	}

	/**
     * 
     */
    public static WebServiceLibPlugin getDefault() {
        return plugin;
    }

	/**
	 * @return this plugin's install path
	 *
	 * @throws Exception
	 */
	public IPath getInstallPath() throws Exception {
        if (this.pluginPath == null) {
            URL url = FileLocator.find(plugin.getBundle(), new Path(""), null); //$NON-NLS-1$
            url = FileLocator.toFileURL(url);
            this.pluginPath = new Path(url.getFile());
        }

        return (IPath)this.pluginPath.clone();
    }

    private String getWebLibDirectory(String resourceDirectory) throws Exception {
        final String pluginPath = getInstallPath().toOSString();
        final String webServiceLibFolder = pluginPath + File.separator + resourceDirectory;

        if (new File(webServiceLibFolder).exists()) {
            return webServiceLibFolder;
        }

        String msg = "The web lib directory, " + resourceDirectory + ", does not exist on the file system."; //$NON-NLS-1$ //$NON-NLS-2$
        throw new FileNotFoundException(msg);
    }

    /**
     * @return the soap war resource directory path
     * @throws Exception
     */
    public String getSoapWebLibDirectoryPath() throws Exception {
        return getWebLibDirectory(SOAP_WAR_RESOURCES);
    }

    /**
     * @return the rest war resource directory path
     * @throws Exception
     */
    public String getRestWebLibDirectoryPath() throws Exception {
        return getWebLibDirectory(REST_WAR_RESOURCES);
    }
}
