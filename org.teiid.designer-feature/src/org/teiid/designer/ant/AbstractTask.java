/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Ant task base class that provides methods for creating Ant properties and path references.
 */
abstract class AbstractTask extends Task {

    /**
     * Delimiter used to separate entries in path properties
     */
    protected static final String PATH_DELIMITER = ","; //$NON-NLS-1$

    private static final Map<String, Map<String, String>> propertiesByFolder = new HashMap<String, Map<String, String>>();

    /**
     * Create an Ant path reference with the supplied name and caches its value, or if previously created, just returns its value,
     * 
     * @param name the name of the path reference
     * @throws Exception if any error occurs while creating the path reference
     */
    protected final void createPath( String name ) throws Exception {
        createPath(name, name);
    }

    /**
     * Create an Ant path reference with the supplied name and caches its value, or if previously created, just returns its value,
     * 
     * @param name the name of the path reference
     * @param cachedName the cached name associated with the reference's value
     * @throws Exception if any error occurs while creating the path reference
     */
    protected final void createPath( String name,
                                     String cachedName ) throws Exception {
        // Return if property already created
        Project proj = getProject();
        if (proj.getReference(name) != null) return;
        // Load feature paths and properties from appropriate manifest and properties files
        ensurePropertiesLoaded();
        // Create path from loaded value
        String val = getProperties().get(cachedName);
        Path path = new Path(proj);
        for (String folder : val.split(PATH_DELIMITER)) {
            path.setPath(folder);
        }
        proj.addReference(name, path);
        log(name + " = " + val); //$NON-NLS-1$
    }

    /**
     * Create an Ant property with the supplied name and caches its value, or if previously created, just returns its value,
     * 
     * @param name the name of the property
     * @throws Exception if any error occurs while creating the property
     */
    protected final void createProperty( String name ) throws Exception {
        createProperty(name, name);
    }

    /**
     * Create an Ant property with the supplied name and caches its value, or if previously created, just returns its value,
     * 
     * @param name the name of the property
     * @param cachedName the cached name associated with the property's value
     * @throws Exception if any error occurs while creating the property
     */
    protected final void createProperty( String name,
                                         String cachedName ) throws Exception {
        // Return if property already created
        Project proj = getProject();
        if (proj.getProperty(name) != null) return;
        // Load feature paths and properties from appropriate manifest and properties files
        ensurePropertiesLoaded();
        // Create property from loaded value
        String val = getProperties().get(cachedName);
        proj.setNewProperty(name, val);
        log(name + " = " + val); //$NON-NLS-1$
    }

    protected void ensurePropertiesLoaded() throws Exception {
        ensurePropertiesLoaded(getProject().getBaseDir().getPath());
    }

    /**
     * Ensures the properties for the supplied folder have been loaded and cached.
     * 
     * @param file The file for the Eclipse plug-in to load
     * @return The cached properties
     * @throws Exception if any error occurs while loading the properties
     */
    protected final Map<String, String> ensurePropertiesLoaded( String file ) throws Exception {
        // Return if plug-in already loaded
        String path = new File(file).getCanonicalPath();
        Map<String, String> props = propertiesByFolder.get(path);
        if (props != null) return props;
        // Otherwise, load plug-in
        props = new HashMap<String, String>();
        propertiesByFolder.put(path, props);
        loadProperties(file, props);
        return props;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public final void execute() throws BuildException {
        try {
            run();
        } catch (Throwable error) {
            // Display error message that includes the tasks current property values
            StringBuilder msg = new StringBuilder("Error in task "); //$NON-NLS-1$
            msg.append(getTaskName()).append('(');
            boolean firstFldAdded = false;
            for (Field fld : getClass().getDeclaredFields()) {
                Object val;
                try {
                    val = fld.get(this);
                    if (firstFldAdded) msg.append(", "); //$NON-NLS-1$
                    else firstFldAdded = true;
                    msg.append(fld.getName());
                    msg.append(" = "); //$NON-NLS-1$
                    msg.append(val);
                } catch (IllegalAccessException ignored) {
                }
            }
            msg.append("):\n  "); //$NON-NLS-1$
            msg.append(error.getMessage());
            log(msg.toString(), error, Project.MSG_ERR);
            throw new BuildException(error);
        }
    }

    protected Map<String, String> getProperties() throws IOException {
        return propertiesByFolder.get(getProject().getBaseDir().getCanonicalPath());
    }

    /**
     * Loads the properties for the supplied folder and caches them in the supplied properties cache.
     * 
     * @param file The file for the Eclipse plug-in to load
     * @param properties The properties cache
     * @throws Exception if any error occurs while loading the properties
     */
    protected abstract void loadProperties( String file,
                                            Map<String, String> properties ) throws Exception;

    /**
     * Runs this task
     * 
     * @throws Exception if any error occurs during the execution of this task
     */
    protected abstract void run() throws Exception;
}
