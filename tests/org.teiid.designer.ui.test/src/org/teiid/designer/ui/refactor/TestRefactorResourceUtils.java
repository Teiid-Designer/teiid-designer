/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor;

import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.teiid.designer.core.refactor.PathPair;

/**
 *
 */
public class TestRefactorResourceUtils extends TestCase {

    public static Test suite() {
        return new TestSuite(TestRefactorResourceUtils.class);
    }
    
    public void testGetRelativePath() throws Exception {
        PathPair testPair, resultPair;
        File file = new File("/home/test1/programming/java/td-projects/parts/views/PartsXMLViews.xmi");
        String testFilePath = file.getParentFile().getAbsolutePath();

        // Rename a project directory
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts",
                                                "/home/test1/programming/java/td-projects/pgr");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../", resultPair.getSourcePath());
        assertEquals("../", resultPair.getTargetPath());

        // Move source directory into the same directory as the file
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/views/sources",
                                                "/home/test1/programming/java/td-projects/parts/sources");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("sources", resultPair.getSourcePath());
        assertEquals("../sources", resultPair.getTargetPath());

        // Move source directory into the same directory as the file
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources",
                                                "/home/test1/programming/java/td-projects/parts/views/sources");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources", resultPair.getSourcePath());
        assertEquals("sources", resultPair.getTargetPath());
        
        // Move source directory into the same directory as the file
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources/subdir",
                                                "/home/test1/programming/java/td-projects/parts/views/subdir");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources/subdir", resultPair.getSourcePath());
        assertEquals("subdir", resultPair.getTargetPath());

        // Move the outside-of-project views directory into
        testPair = new PathPair( "/home/test1/programming/java/td-projects/otherdir",
                                                "/home/test1/programming/java/td-projects/parts/otherdir");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../../otherdir", resultPair.getSourcePath());
        assertEquals("../otherdir", resultPair.getTargetPath());

        // Rename a sources sub-directory
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources/subdir",
                                                "/home/test1/programming/java/td-projects/parts/sources/diffdir");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources/subdir", resultPair.getSourcePath());
        assertEquals("../sources/diffdir", resultPair.getTargetPath());

        // Rename sources directory with reference to a file in the sources directory
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/sources12345/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("../sources12345/importedFile.xmi", resultPair.getTargetPath());

        // Rename sources directory ,with reference to a file in the sources directory, to something completely different
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/my12345/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("../my12345/importedFile.xmi", resultPair.getTargetPath());

        // Move file from sources directory to project directory
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/newImportedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("../newImportedFile.xmi", resultPair.getTargetPath());

        // Example where path pair is the same
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/sources/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/sources/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../sources/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("../sources/importedFile.xmi", resultPair.getTargetPath());

        // Example where path pair is the same and a member of the directory
        file = new File("/home/test1/programming/java/td-projects/parts/folder1");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("importedFile.xmi", resultPair.getSourcePath());
        assertEquals("importedFile.xmi", resultPair.getTargetPath());

        // Example where path pair is the same and part of the directory hierarchy
        file = new File("/home/test1/programming/java/td-projects/parts");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("folder1/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("folder1/importedFile.xmi", resultPair.getTargetPath());

        // Example where path pair is the same and a different part of the directory hierarchy
        file = new File("/home/test1/programming/java/td-projects/parts/folder2");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../folder1/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("../folder1/importedFile.xmi", resultPair.getTargetPath());

        // Example where path pair is the same and a different part of the directory hierarchy
        file = new File("/home/test1/programming/java/td-projects/parts/folder2/subfolder");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi",
                                                "/home/test1/programming/java/td-projects/parts/folder1/importedFile.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../../folder1/importedFile.xmi", resultPair.getSourcePath());
        assertEquals("../../folder1/importedFile.xmi", resultPair.getTargetPath());

        // Example of moving second model which is resident in views with view model back into the sources folder
        file = new File("/home/test1/programming/java/td-projects/TestRenameModel/views");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/TestRenameModel/views/SecondModel.xmi",
                                                "/home/test1/programming/java/td-projects/TestRenameModel/sources/SecondModel.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("SecondModel.xmi", resultPair.getSourcePath());
        assertEquals("../sources/SecondModel.xmi", resultPair.getTargetPath());

        // Example of renaming a model
        file = new File("/home/test1/programming/java/td-projects/TestRenameModel");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/TestRenameModel/views/SecondModel.xmi",
                                                "/home/test1/programming/java/td-projects/TestRenameModel/views/ThirdModel.xmi");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("views/SecondModel.xmi", resultPair.getSourcePath());
        assertEquals("views/ThirdModel.xmi", resultPair.getTargetPath());

        // Rename a sources sub-directory
        file = new File("/home/test1/programming/java/td-projects/TestRenameModel/folder1/folder2");
        testFilePath = file.getAbsolutePath();
        testPair = new PathPair( "/home/test1/programming/java/td-projects/TestRenameModel/sources/subdir",
                                                "/home/test1/programming/java/td-projects/TestRenameModel/sources/diffdir");
        resultPair = RefactorResourcesUtils.getRelativePath(testFilePath, testPair);
        assertEquals("../../sources/subdir", resultPair.getSourcePath());
        assertEquals("../../sources/diffdir", resultPair.getTargetPath());
    }
}
