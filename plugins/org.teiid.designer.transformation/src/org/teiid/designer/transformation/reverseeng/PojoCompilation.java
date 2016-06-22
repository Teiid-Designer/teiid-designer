/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.reverseeng.util.Util;

/**
 * DynamicCompiliation is responsible for compiling and assembly from a directory location containing .java files. 
 * Based on the file location provided, it will look for all .java files within the directory structure.
 * Those files will be included in the compilation and then assembled into the final .jar assembly .
 * 
 * @author vanhalbert
 *
 */
public class PojoCompilation implements ReverseEngConstants {

	/**
	 * 
	 * @param loc is where to find the .java files that are to be compiled.  This is based on the 
	 * build location 
	 * @param packageName , in file path format, to use when adding to archive
	 * @param pojoJarFile is the pojo jar file to be created
	 * @throws Exception
	 */
	public static void compile(File loc, String packageName, File pojoJarFile)
			throws Exception {
		
		
//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Creating jar file: " + pojoJarFile.getAbsolutePath());


		File[] javaFiles = Util.findAllFilesInDirectoryHavingExtension(
				loc.getCanonicalPath(), ".java");

		if (javaFiles == null || javaFiles.length == 0) {
			throw new Exception("No java source files found at " +  loc.getCanonicalPath());
		}

		
		compileFiles(javaFiles);

		File[] files = Util.findAllFilesInDirectoryHavingExtension(
				loc.getCanonicalPath(), ".class");

		File parent = pojoJarFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		createJarArchive(pojoJarFile, files, packageName);

//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Created jar file: " + pojoJarFile.getAbsolutePath());

	}

	private static void compileFiles(File[] javaFiles) throws Exception {

		// Compile classes
		JavaCompiler compilerTool = ToolProvider.getSystemJavaCompiler();
		if (compilerTool != null) {
			StandardJavaFileManager fileManager = compilerTool.getStandardFileManager(null, null, null);

			//	        String pathToPojoJar = path.getCanonicalPath() + File.separator + jarName; //$NON-NLS-1$
			List<File> classPaths = new ArrayList<File>();
			
            final String thisPluginPath = TransformationPlugin.getDefault().getInstallPath().toOSString();
            final String libDirectoryName = thisPluginPath + File.separator + LIBS;
            final File libDirectory = new File(libDirectoryName);
            
            String pathToJar1 = libDirectory.getCanonicalPath() + File.separator + PROTOSTREAM_JAR;
            String pathToJar2 = libDirectory.getCanonicalPath() + File.separator + HIBERNATE_SEARCH_JAR;
            
            classPaths.add(new File(pathToJar1));
            classPaths.add(new File(pathToJar2));

			fileManager.setLocation(StandardLocation.CLASS_PATH, classPaths);

			// prepare the source files to compile
			List<File> sourceFileList = new ArrayList<File>();
			
			for (int i = 0; i < javaFiles.length; i++) {
				if (javaFiles[i] == null || !javaFiles[i].exists() || javaFiles[i].isDirectory()) {
					continue; // Just in case...
				}
				
				sourceFileList.add(javaFiles[i]);
			}

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
			/*
			 * Create a diagnostic controller, which holds the compilation
			 * problems
			 */
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			CompilationTask task = compilerTool.getTask(null, fileManager,
					diagnostics, null, null, compilationUnits);
			task.call();
			List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics
					.getDiagnostics();
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
				diagnostic.getKind();
				if (diagnostic.getKind().equals(Kind.ERROR)) {
					throw new Exception(diagnostic.getMessage(null));
				}
			}

			fileManager.close();

		}
	}

	public static int BUFFER_SIZE = 10240;

	protected static void createJarArchive(File archiveFile, File[] tobeJared,
			String packageName) throws Exception {
		
		try {
			byte buffer[] = new byte[BUFFER_SIZE];
			// Open archive file
			FileOutputStream stream = new FileOutputStream(archiveFile);
			JarOutputStream out = new JarOutputStream(stream, new Manifest());

			for (int i = 0; i < tobeJared.length; i++) {
				if (tobeJared[i] == null || !tobeJared[i].exists()
						|| tobeJared[i].isDirectory())
					continue; // Just in case...

				// Add archive entry
				String fname = packageName + tobeJared[i].getName();
				JarEntry jarAdd = new JarEntry(fname);

//				ReverseEngineerPlugin.LOGGER.debug("[ReverseEngineering] Added class: " + fname + " to jar: " + archiveFile);

				jarAdd.setTime(tobeJared[i].lastModified());
				out.putNextEntry(jarAdd);

				// Write file to archive
				FileInputStream in = new FileInputStream(tobeJared[i]);
				while (true) {
					int nRead = in.read(buffer, 0, buffer.length);
					if (nRead <= 0)
						break;
					out.write(buffer, 0, nRead);
				}
				in.close();
			}

			out.close();
			stream.close();
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
		
	}

}
