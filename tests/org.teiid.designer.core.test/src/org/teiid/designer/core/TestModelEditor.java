/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import java.io.File;

import junit.framework.TestCase;

import org.teiid.core.util.SmartTestDesignerSuite;

public final class TestModelEditor extends TestCase {

    public void testCloneProject() throws Exception {
        String testDataPath = SmartTestDesignerSuite.getTestDataPath(getClass()) + File.separator + "cloneProject"; //$NON-NLS-1$
        
        ModelEditorImpl editor = new ModelEditorImpl();
        System.err.println("\n\n\n**********************************\n\n\nPath="+ testDataPath +"\n\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
        
        File[] files = new File(testDataPath).listFiles();
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
