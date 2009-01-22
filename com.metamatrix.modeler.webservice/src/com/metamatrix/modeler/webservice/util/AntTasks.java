/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.util;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.taskdefs.Zip;

/**
 * Contains integrated Apache Ant tasks. See the online Ant documentation on Apache's Web site for more information about how the
 * tasks work.
 * 
 * @since 4.4
 */
public class AntTasks {

    /**
     * This method will execute the Ant zip task.
     */
    public static void zip(String baseDir,
                           String destFileName) throws Exception {

        /**
         * Extends the Ant ZIP task.
         * 
         * @since 4.4
         */
        final class Archiver extends Zip {

            /**
             * Default constructor.
             * 
             * @since 4.4
             */
            public Archiver() {

                this.setProject(new Project());
                this.getProject().init();
                this.setTaskType("zip"); //$NON-NLS-1$
                this.setTaskName("zip"); //$NON-NLS-1$
                this.setOwningTarget(new Target());
            }
        }

        Archiver archiver = new Archiver();
        archiver.setBasedir(new File(baseDir));
        archiver.setDestFile(new File(destFileName));

        archiver.execute();
    }

    /**
     * This method will execute the Ant unzip task.
     * 
     * @param zipFile
     * @param destDir
     * @since 4.4
     */
    public static void unzip(String zipFile,
                             String destDir) {

        /**
         * Extends the Ant Exapnd task.
         * 
         * @since 4.4
         */
        final class Expander extends Expand {

            /**
             * Default constructor.
             * 
             * @since 4.4
             */
            public Expander() {

                this.setProject(new Project());
                this.getProject().init();
                this.setTaskType("zip"); //$NON-NLS-1$
                this.setTaskName("zip"); //$NON-NLS-1$
                this.setOwningTarget(new Target());
            }
        }

        Expander expander = new Expander();
        expander.setSrc(new File(zipFile));
        expander.setDest(new File(destDir));

        expander.execute();
    }

    /**
     * This method will execute the Ant replace task.
     * 
     * @param file
     * @param token
     * @param value
     * @since 4.4
     */
    public static void replace(File file,
                               String token,
                               String value) {

        /**
         * Extends the Ant replace task.
         * 
         * @since 4.4
         */
        final class Replacer extends Replace {

            /**
             * Default constructor.
             * 
             * @since 4.4
             */
            public Replacer() {

                this.setProject(new Project());
                this.getProject().init();
                this.setTaskType("zip"); //$NON-NLS-1$
                this.setTaskName("zip"); //$NON-NLS-1$
                this.setOwningTarget(new Target());
            }
        }

        Replacer replacer = new Replacer();
        replacer.setFile(file);
        replacer.setToken(token);
        replacer.setValue(value);

        replacer.execute();
    }
}
