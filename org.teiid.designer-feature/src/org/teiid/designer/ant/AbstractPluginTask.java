/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

abstract class AbstractPluginTask extends AbstractTask {

    private static final String RESOLUTION = "resolution:"; //$NON-NLS-1$
    private static final String OPTIONAL = "=optional"; //$NON-NLS-1$
    protected static final String CLASSPATH = "classpath"; //$NON-NLS-1$

    /**
     * @param arguments
     */
    public static void main( String[] arguments ) {
        AbstractPluginTask task = new AbstractPluginTask() {

            @Override
            protected void run() throws Exception {
                createPath("test", CLASSPATH); //$NON-NLS-1$
            }
        };
        Project project = new Project();
        project.setBasedir("/Users/jpav/workspace/teiidDesigner/org.teiid.designer.ui"); //$NON-NLS-1$
        project.setProperty("basews", "carbon"); //$NON-NLS-1$ //$NON-NLS-2$
        project.setProperty("baseos", "macosx"); //$NON-NLS-1$ //$NON-NLS-2$
        project.setProperty("targetPlatform", "../targetPlatform-3.4.2-MacOSX-x86-Carbon"); //$NON-NLS-1$ //$NON-NLS-2$
        task.setProject(project);
        task.execute();
        System.out.println(project.getReference("test")); //$NON-NLS-1$
    }

    private String workspace;

    protected AbstractPluginTask() {
    }

    private void addToClasspath( String path,
                                 List<String> classpath ) {
        if (path.startsWith(getWorkspace())) path = path.substring(getWorkspace().length());
        if (!classpath.contains(path)) classpath.add(path);
    }

    private void addRequiredPluginClasspathToClasspath( File plugin,
                                                        String requiredPlugin,
                                                        boolean optional,
                                                        List<String> classpath ) throws Exception {
        File requiredFile = new File(plugin.getParentFile(), requiredPlugin);
        if (!requiredFile.exists()) {
            // Plug-in isn't in workspace, so look for it in the target platform
            final String pluginPfx = requiredPlugin + '_';
            String targetPlatform = getProject().getProperty("targetPlatform"); //$NON-NLS-1$
            // "Else" below relies upon target platform being specified relative to the Ant basedir...
            File pluginsFolder = (plugin.getPath().contains(targetPlatform) ? plugin.getParentFile() : new File(
                                                                                                                plugin,
                                                                                                                targetPlatform
                                                                                                                + "/eclipse/plugins")); //$NON-NLS-1$
            File[] files = pluginsFolder.listFiles(new FilenameFilter() {

                public boolean accept( File folder,
                                       String name ) {
                    return name.startsWith(pluginPfx);
                }
            });
            assert files != null;
            if (files.length == 0) {
                if (optional) return;
                throw new BuildException("Missing required bundle in target platform: " + requiredPlugin); //$NON-NLS-1$
            }
            if (files.length > 1) throw new BuildException(
                                                           "More than one version of required bundle exists in target platform: " + Arrays.asList(files)); //$NON-NLS-1$

            requiredFile = files[0];
        }
        String requiredPath = requiredFile.getPath();
        Map<String, String> props = ensurePropertiesLoaded(requiredPath);
        for (String path : props.get(CLASSPATH).split(",")) { //$NON-NLS-1$
            addToClasspath("../" + path, classpath); //$NON-NLS-1$
        }
    }

    private void cacheBundleManifestProperty( String name,
                                              StringBuilder value,
                                              Map<String, String> properties ) {
        if (value.length() == 0) return;
        int semicolonNdx = value.indexOf(";"); //$NON-NLS-1$
        if (semicolonNdx > 0) {
            // Parse value into separate comma-delimited segments, ignoring commas within quotes
            List<String> entries = new ArrayList<String>();
            int entryNdx = 0;
            for (int commaNdx = value.indexOf(",", entryNdx); commaNdx > 0; commaNdx = value.indexOf(",", entryNdx)) { //$NON-NLS-1$ //$NON-NLS-2$
                int startQuoteNdx = value.indexOf("\"", entryNdx); //$NON-NLS-1$
                if (startQuoteNdx > 0 && commaNdx > startQuoteNdx) {
                    int endQuoteNdx = value.indexOf("\"", startQuoteNdx + 1); //$NON-NLS-1$
                    // Note, code below assumes a comma can only appear within quotes once per entry
                    if (commaNdx < endQuoteNdx) {
                        commaNdx = value.indexOf(",", endQuoteNdx); //$NON-NLS-1$
                        if (commaNdx < 0) break;
                    }
                }
                entries.add(value.substring(entryNdx, commaNdx));
                entryNdx = commaNdx + 1;
            }
            entries.add(value.substring(entryNdx));
            // Strip qualifiers from entries, while adding a marker for entries that are optional
            value.setLength(0);
            for (String entry : entries) {
                if (value.length() > 0) value.append(',');
                semicolonNdx = entry.indexOf(";"); //$NON-NLS-1$
                if (semicolonNdx > 0) {
                    value.append(entry.substring(0, semicolonNdx));
                    if (entry.indexOf(RESOLUTION + OPTIONAL, semicolonNdx) > 0) value.append(OPTIONAL);
                } else value.append(entry);
            }
        }
        cacheProperty(properties, name, value.toString());
        value.setLength(0);
    }

    private void cacheProperty( Map<String, String> properties,
                                String name,
                                String value ) {
        properties.put(name, value);
    }

    private String getWorkspace() {
        if (workspace == null) {
            workspace = getProject().getBaseDir().getParent();
            if (!workspace.endsWith("/")) workspace += '/'; //$NON-NLS-1$
        }
        return workspace;
    }

    private void loadBuildProperties( Reader reader,
                                      Map<String, String> properties ) throws IOException {
        BufferedReader bufReader = new BufferedReader(reader);
        try {
            String name = null;
            StringBuilder val = new StringBuilder();
            for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') continue;
                if (val.length() == 0) {
                    int ndx = line.indexOf('=');
                    if (ndx > 0) {
                        name = line.substring(0, ndx).trim();
                        assert ndx + 1 < line.length();
                        val.append(line.substring(ndx + 1).trim());
                    }
                } else val.append(line);
                if (line.charAt(line.length() - 1) == '\\') val.deleteCharAt(val.length() - 1);
                else {
                    if (name.startsWith("source.")) cacheProperty(properties, "plugin.source.jar", name.substring(7, name.length() - 4)); //$NON-NLS-1$ //$NON-NLS-2$
                    else cacheProperty(properties, name, val.toString());
                    val.setLength(0);
                }
            }
        } finally {
            bufReader.close();
        }
    }

    private void loadBundleManifest( Reader reader,
                                     Map<String, String> properties ) throws IOException {
        BufferedReader bufReader = new BufferedReader(reader);
        try {
            String name = null;
            StringBuilder val = new StringBuilder();
            for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
                line = line.trim();
                // Check if line is a new entry
                int colonNdx = line.indexOf(':');
                if (colonNdx > 0 && colonNdx < line.length() - 1 && line.charAt(colonNdx + 1) != '=') {
                    cacheBundleManifestProperty(name, val, properties);
                    name = line.substring(0, colonNdx).trim();
                    if ("Name".equals(name) || "SHA1-Digest".equals(name)) break; //$NON-NLS-1$ //$NON-NLS-2$
                    val.append(line.substring(colonNdx + 1).trim());
                } else val.append(line);
            }
            cacheBundleManifestProperty(name, val, properties);
        } finally {
            bufReader.close();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#loadProperties(java.lang.String, java.util.Map)
     */
    @Override
    protected void loadProperties( String plugin,
                                   Map<String, String> properties ) throws Exception {
        File pluginFile = new File(plugin);
        List<String> classpath = new ArrayList<String>();
        ZipFile jar = null;
        try {
            if (pluginFile.isDirectory()) {
                loadBundleManifest(new FileReader(new File(plugin, "META-INF/MANIFEST.MF")), properties); //$NON-NLS-1$
                File propertiesFile = new File(plugin, "build.properties"); //$NON-NLS-1$
                if (propertiesFile.exists()) loadBuildProperties(new FileReader(propertiesFile), properties);
                // Add bin folder to the classpath if this is a workspace plug-in
                String parent = pluginFile.getParent();
                if (!parent.endsWith("/")) parent = parent + '/'; //$NON-NLS-1$
                if (getWorkspace().equals(parent)) addToClasspath(new File(pluginFile, "bin").getCanonicalPath(), //$NON-NLS-1$
                                                                  classpath);
            } else {
                // Must be a jar file
                jar = new ZipFile(pluginFile);
                loadBundleManifest(new InputStreamReader(jar.getInputStream(jar.getEntry("META-INF/MANIFEST.MF"))), properties); //$NON-NLS-1$
                ZipEntry entry = jar.getEntry("build.properties"); //$NON-NLS-1$
                if (entry != null) loadBuildProperties(new InputStreamReader(jar.getInputStream(entry)), properties);
                // Add jar to the classpath
                addToClasspath(pluginFile.getCanonicalPath(), classpath);
            }
            // Add the bundle's manifest classpath to the classpath
            String bundleClasspath = properties.get("Bundle-ClassPath"); //$NON-NLS-1$
            if (bundleClasspath != null) {
                for (String bundle : bundleClasspath.split(",")) { //$NON-NLS-1$
                    bundle = bundle.trim();
                    // Skip "current folder" entries
                    if (bundle.equals(".")) continue; //$NON-NLS-1$
                    // Skip bogus jar classpath entries
                    if (jar != null && jar.getEntry(bundle) == null) continue;
                    assert jar == null;
                    addToClasspath(new File(pluginFile, bundle).getCanonicalPath(), classpath);
                }
            }
        } finally {
            if (jar != null) jar.close();
        }
        // Add the classpaths from each of the bundle's requirements
        String requiredBundles = properties.get("Require-Bundle"); //$NON-NLS-1$
        if (requiredBundles != null) {
            for (String bundle : requiredBundles.split(",")) { //$NON-NLS-1$
                bundle = bundle.trim();
                boolean optional = false;
                int ndx = bundle.indexOf(OPTIONAL);
                if (ndx > 0) {
                    bundle = bundle.substring(0, ndx);
                    optional = true;
                }
                if ("system.bundle".equals(bundle) || ".".equals(bundle)) continue; //$NON-NLS-1$ //$NON-NLS-2$
                addRequiredPluginClasspathToClasspath(pluginFile, bundle, optional, classpath);
                // If this is the SWT plug-in, also load the properties for the platform-specific SWT plug-in
                if (bundle.equals("org.eclipse.swt")) { //$NON-NLS-1$
                    addRequiredPluginClasspathToClasspath(pluginFile, bundle + '.' + getProject().getProperty("basews") + '.' //$NON-NLS-1$
                                                                      + getProject().getProperty("baseos"), false, classpath); //$NON-NLS-1$
                }
            }
        }
        // Cache classpath property
        Iterator<String> iter = classpath.iterator();
        StringBuilder val = new StringBuilder();
        if (iter.hasNext()) {
            val.append(iter.next());
            while (iter.hasNext()) {
                val.append(',').append(iter.next());
            }
        }
        cacheProperty(properties, CLASSPATH, val.toString());
    }
}
