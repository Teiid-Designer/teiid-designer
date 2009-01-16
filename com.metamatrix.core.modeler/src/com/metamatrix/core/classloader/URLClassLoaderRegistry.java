/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.core.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.util.ArgCheck;

/**
 * This class acts as a registry of URL class loaders. The registry maintains a single URLClassLoader for each distinct URL, and
 * can create lightweight and disposable ClassLoaders that delegate to a list of actual URLClassLoaders.
 * 
 * @since 4.2
 */
public class URLClassLoaderRegistry {

    // ===========================================================================================================================
    // Static Variables

    private static File baseFolder;

    // ===========================================================================================================================
    // Static Model Methods

    public static void setBaseFolder( final String folder ) {
        URLClassLoaderRegistry.baseFolder = (folder == null ? null : new File(folder));
    }

    public static String getBaseFolder() {
        if (baseFolder == null) {
            return null;
        }
        return baseFolder.getAbsolutePath();
    }

    // ===========================================================================================================================
    // Variables

    private final Map classLoaders; // keyed by the stringified URL[]

    /**
     * @since 4.2
     */
    public URLClassLoaderRegistry() {
        super();
        this.classLoaders = new HashMap();
    }

    // ========================================================================================================
    // Individual URL Class Loader methods
    // ========================================================================================================

    public synchronized URLClassLoader removeClassLoader( final URL[] urls ) {
        ArgCheck.isNotNull(urls);
        final Object key = doCreateKey(urls);
        return (URLClassLoader)this.classLoaders.remove(key);
    }

    public synchronized URLClassLoader removeClassLoader( final String[] urlStrings ) {
        ArgCheck.isNotNull(urlStrings);
        final Object key = doCreateKey(urlStrings);
        return (URLClassLoader)this.classLoaders.remove(key);
    }

    // ========================================================================================================
    // Composite Class Loader methods
    // ========================================================================================================

    public URLClassLoader getClassLoader( final String[] urlStrings ) throws MalformedURLException {
        return getClassLoader(urlStrings, null);
    }

    public URLClassLoader getClassLoader( final String[] urlStrings,
                                          final ClassLoader parent ) throws MalformedURLException {
        ArgCheck.isNotNull(urlStrings);

        final Object key = doCreateKey(urlStrings);

        final URL[] urls = new URL[urlStrings.length];
        for (int i = 0; i < urlStrings.length; i++) {
            urls[i] = doCreateUrl(urlStrings[i]);
        }
        return doGetOrCreateClassLoader(key, urls, parent);
    }

    public URLClassLoader getClassLoader( final URL[] urls ) {
        return getClassLoader(urls, null);
    }

    public URLClassLoader getClassLoader( final URL[] urls,
                                          final ClassLoader parent ) {
        ArgCheck.isNotNull(urls);

        final Object key = doCreateKey(urls);
        return doGetOrCreateClassLoader(key, urls, parent);
    }

    // ========================================================================================================
    // Implementation methods
    // ========================================================================================================

    protected int size() {
        return this.classLoaders.size();
    }

    protected Object doCreateKey( final String[] urlStrings ) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < urlStrings.length; i++) {
            final String str = urlStrings[i];
            if (i != 0) {
                sb.append("\n\n"); //$NON-NLS-1$
            }
            sb.append(str);
        }
        return sb.toString();
    }

    protected Object doCreateKey( final URL[] urls ) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < urls.length; i++) {
            final URL url = urls[i];
            if (i != 0) {
                sb.append("\n\n"); //$NON-NLS-1$
            }
            sb.append(url.toString());
        }
        return sb.toString();
    }

    protected URL doCreateUrl( final String urlString ) throws MalformedURLException {
        MalformedURLException mfue = null;
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            // Eat this, and just continue ...
            mfue = e1;
        }

        // If not found as a URL ...
        if (url == null) {
            // Try a local path ...
            final File jarFile = (URLClassLoaderRegistry.baseFolder == null ? new File(urlString) : new File(
                                                                                                             URLClassLoaderRegistry.baseFolder,
                                                                                                             urlString));
            if (jarFile.exists()) {
                try {
                    url = jarFile.toURI().toURL();
                    mfue = null;
                } catch (MalformedURLException e) {
                    mfue = e;
                }
            } else {
                final Object[] params = new Object[] {urlString};
                final String msg = CoreModelerPlugin.Util.getString("URLClassLoaderRegistry.UnableToCreateClassLoaderForUrl_FileDoesNotExist", params); //$NON-NLS-1$
                mfue = new MalformedURLException(msg);
            }
        }

        if (mfue != null) {
            throw mfue;
        }

        return url;
    }

    protected synchronized URLClassLoader doGetOrCreateClassLoader( final Object key,
                                                                    final URL[] urls,
                                                                    final ClassLoader parent ) {
        URLClassLoader loader = (URLClassLoader)this.classLoaders.get(key);
        if (loader == null) {
            loader = doCreateClassLoader(key, urls, parent);
            this.classLoaders.put(key, loader);
        }
        return loader;
    }

    protected URLClassLoader doCreateClassLoader( final Object key,
                                                  final URL[] urls,
                                                  final ClassLoader parent ) {
        return new URLClassLoader(urls, parent);
    }

}
