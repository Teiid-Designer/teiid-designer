/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.io.File;
import junit.framework.TestCase;
import com.metamatrix.core.util.ProjectUtil;

public final class TestModelEditor extends TestCase {

    public void testCloneProject() throws Exception {
        StringBuffer builder = new StringBuffer(ProjectUtil.getProjectPath(getClass()));
        builder.append("testdata"); //$NON-NLS-1$
        builder.append(File.separatorChar);
        builder.append("cloneProject"); //$NON-NLS-1$
        builder.append(File.separatorChar);
        ModelEditorImpl editor = new ModelEditorImpl();
        File[] files = new File(builder.toString()).listFiles();
        File tmpProject = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
        tmpProject.deleteOnExit();
        for (int ndx = files.length; --ndx >= 0;) {
            File file = files[ndx];
            if (file.isDirectory() && file.getName().charAt(0) != '.') {
                String origProjectPath = file.getAbsolutePath();
                String clonedProjectPath = new File(tmpProject, file.getName()).getAbsolutePath();
                editor.cloneProject(origProjectPath, clonedProjectPath);
            }
        }
    }
}
