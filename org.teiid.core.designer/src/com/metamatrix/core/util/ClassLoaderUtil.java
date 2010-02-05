/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * This ClassLoaderUtil class attempts to find classes that are accessible via the supplied ClassLoader and that are assignable
 * from (e.g., implementations or subtypes of) the supplied types.
 */
public class ClassLoaderUtil {

    private static final Class[] EMPTY_ARGUMENT_LIST = new Class[] {};

    private final URLClassLoader loader;
    private final List problems;

    /**
     * Construct an instance of ClassLoaderUtil.
     */
    public ClassLoaderUtil( final URLClassLoader loader ) {
        ArgCheck.isNotNull(loader);
        this.loader = loader;
        this.problems = new ArrayList();
    }

    /**
     * Fine the classes accessible by the {@link #getClassLoader() class loader} that are assignable to (e.g., subtypes or
     * implementations of) <i>any</i> of the supplied <code>types</code>.
     * <p>
     * This method adds any problems encountered to {@link #getProblems()}.
     * </p>
     * 
     * @param types the types to which the resulting classes are assignable
     * @return the array of Class instances that are assignable to the <code>type</code>; will be empty if no such Class instances
     *         are found
     */
    public Class[] getAssignableClasses( final Class[] types ) {
        if (types == null) {
            final String msg = CoreModelerPlugin.Util.getString("ClassLoaderUtil.The_Class[]_of_types_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (types.length == 0) {
            return new Class[] {};
        }
        // Check that there are no null refs in the 'types' ...
        for (int j = 0; j < types.length; j++) {
            if (types[j] == null) {
                final String msg = CoreModelerPlugin.Util.getString("ClassLoaderUtil.The_Class[]_of_types_may_not_contain_null_references"); //$NON-NLS-1$
                throw new IllegalArgumentException(msg);
            }
        }

        final List classes = new ArrayList();
        final URL[] urls = this.loader.getURLs();
        // Search the classpath of the class loader ...
        for (int i = 0; i < urls.length; ++i) {
            final URL url = urls[i];
            InputStream stream = null;
            try {
                stream = url.openStream();
                // If the stream could be found ...
                if (stream != null) {
                    // Then it should be a jar file ...
                    ZipInputStream zipStream = null;
                    zipStream = new ZipInputStream(stream);

                    // Iterate over the contents of the ZIP/JAR file ...
                    while (true) {
                        final ZipEntry entry = zipStream.getNextEntry();
                        if (entry == null) {
                            break;
                        }
                        // Convert the "a/b/c/ClassName.class" form to "a.b.c.ClassName" ...
                        final String className = changeFileNameToClassName(entry.getName());
                        if (className != null) {
                            // And try to load the class ...
                            Class cls = null;
                            try {
                                cls = this.loader.loadClass(className);
                            } catch (Throwable th) {
                                final Object[] params = new Object[] {className, url};
                                final String msg = CoreModelerPlugin.Util.getString("ClassLoaderUtil.Error_trying_to_load_class_from_file") + params; //$NON-NLS-1$
                                final IStatus error = new Status(IStatus.WARNING, CoreModelerPlugin.PLUGIN_ID, 0, msg, th);
                                this.problems.add(error);
                            }
                            if (cls != null) {
                                for (int j = 0; j < types.length; j++) {
                                    if (types[j].isAssignableFrom(cls)) {
                                        classes.add(cls);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                final Object[] params = new Object[] {url};
                final String msg = CoreModelerPlugin.Util.getString("ClassLoaderUtil.Error_trying_to_load_file", params); //$NON-NLS-1$
                final IStatus error = new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, msg, e);
                this.problems.add(error);
                continue; // do nothing more for this file ...
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e1) {
                        // do nothing
                    }
                }
            }
        }
        return (Class[])classes.toArray(new Class[classes.size()]);
    }

    /**
     * Fine those classes accessible by the {@link #getClassLoader() class loader} that are public classes (i.e., not interfaces)
     * with have no-arg constructors that are assignable to (e.g., subtypes or implementations of) <i>any</i> of the supplied
     * <code>types</code>. This method is useful for finding implementations of an interface or subtypes of a class that are
     * instantiable with a no-arg constructor.
     * <p>
     * This method adds any problems encountered to {@link #getProblems()}.
     * </p>
     * 
     * @param type the type to which the resulting classes are assignable
     * @return the array of Class instances that are assignable to the <code>type</code>; will be empty if no such Class instances
     *         are found
     */
    public Class[] getAssignablePublicClassesWithNoArgConstructors( final Class[] types ) {
        final Class[] assignableClasses = getAssignableClasses(types);
        final List classes = new ArrayList();
        // Search the classpath of the class loader ...
        for (int i = 0; i < assignableClasses.length; ++i) {
            final Class clazz = assignableClasses[i];

            // Check for classes
            if (clazz.isInterface()) {
                continue;
            }

            // Check for abstract classes
            if (isAbstract(clazz)) {
                continue;
            }

            // Check for public classes
            if (!isPublic(clazz)) {
                continue;
            }

            // Check for no-arg constructor
            if (!hasAZeroArgConstructor(clazz)) {
                continue;
            }

            // Passed all tests ...
            classes.add(clazz);
        }
        return (Class[])classes.toArray(new Class[classes.size()]);
    }

    protected boolean isAbstract( final Class clazz ) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    protected boolean isPublic( final Class clazz ) {
        return Modifier.isPublic(clazz.getModifiers());
    }

    protected boolean hasAZeroArgConstructor( final Class clazz ) {
        try {
            final Constructor noArgConstructor = clazz.getConstructor(EMPTY_ARGUMENT_LIST);
            return (noArgConstructor != null);
        } catch (SecurityException e) {
            // do nothing; means we don't want the class
        } catch (NoSuchMethodException e) {
            // do nothing; means we don't want the class
        }
        return false;
    }

    /**
     * Change the passed file name to its corresponding class name. E.G. change &quot;com/metamatrix/Utilities.class&quot; to
     * &quot;com.metamatrix.Utilities&quot;.
     * 
     * @param name the class name to be changed. If this does not represent a Java class then <code>null</code> is returned.
     * @throws IllegalArgumentException if a null <code>name</code> passed.
     */
    public static String changeFileNameToClassName( final String name ) {
        if (name == null) {
            final String msg = CoreModelerPlugin.Util.getString("ClassLoaderUtil.The_name_of_the_class_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        String className = null;
        if (name.toLowerCase().endsWith(".class")) { //$NON-NLS-1$
            className = name.replace('/', '.');
            className = className.replace('\\', '.');
            className = className.substring(0, className.length() - 6);
        }
        return className;
    }

    /**
     * @return
     */
    public boolean hasProblems() {
        return problems.size() != 0;
    }

    /**
     * @return
     */
    public List getProblems() {
        return problems;
    }

    /**
     * @return
     */
    public URLClassLoader getClassLoader() {
        return loader;
    }
}
