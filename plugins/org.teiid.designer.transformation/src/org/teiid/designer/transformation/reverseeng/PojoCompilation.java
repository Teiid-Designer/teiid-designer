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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
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
	public static void compile(File loc, File classDirLocaton, String packageName, File pojoJarFile)
			throws Exception {
		
		
//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Creating jar file: " + pojoJarFile.getAbsolutePath());


		File[] javaFiles = Util.findAllFilesInDirectoryHavingExtension(
				loc.getCanonicalPath(), ".java");

		if (javaFiles == null || javaFiles.length == 0) {
			throw new Exception("No java source files found at " +  loc.getCanonicalPath());
		}

		
		compileFiles(javaFiles);

		File parent = pojoJarFile.getParentFile();

		if (!parent.exists()) {
			parent.mkdirs();
		}
		
		jarIt(pojoJarFile, classDirLocaton);

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

//	public static int BUFFER_SIZE = 10240;
    
//    public static void createJar(File archiveFile, File classDirLocaton, String packagePath) throws IOException
//    {
//      FileOutputStream fos = new FileOutputStream(archiveFile);

//      JarOutputStream target = new JarOutputStream(fos, manifest);
//      add(topDir, target, packagePath);
//      target.close();
//    }

//    private static void add(File source, JarOutputStream target, String packagePath) throws IOException
//    {
//      BufferedInputStream in = null;
//      try
//      {
//        if (source.isDirectory())
//        {
//          String name = source.getPath().replace("\\", "/");
//          if (!name.isEmpty())
//          {
//            if (!name.endsWith("/"))
//              name += "/";
//            JarEntry entry = new JarEntry(name);
//            entry.setTime(source.lastModified());
//            target.putNextEntry(entry);
//            target.closeEntry();
//          }
//          for (File nestedFile: source.listFiles())
//            add(nestedFile, target, packagePath);
//          return;
//        }
//
//        if( source.exists() && !source.getPath().endsWith(".class" )) {
//        	return;
//        }
//        JarEntry entry = new JarEntry(packagePath + source.getName());
//        entry.setTime(source.lastModified());
//        target.putNextEntry(entry);
//        in = new BufferedInputStream(new FileInputStream(source));
//
//        byte[] buffer = new byte[1024];
//        while (true)
//        {
//          int count = in.read(buffer);
//          if (count == -1)
//            break;
//          target.write(buffer, 0, count);
//        }
//        target.closeEntry();
//      }
//      finally
//      {
//        if (in != null)
//          in.close();
//      }
//    }
    
    static void jarIt(File archiveFile, File classDirLocaton) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    	FileOutputStream fos = new FileOutputStream(archiveFile); 
        JarOutputStream jos = new JarOutputStream(fos, manifest); 
        try { 
            addToJarRecursively(jos, classDirLocaton.getAbsoluteFile(), classDirLocaton.getAbsolutePath()); 
        } finally { 
            jos.close();
            fos.close();
        } 
    } 
 
    static void addToJarRecursively(JarOutputStream jar, File source, String rootDirectory) throws IOException { 
        String sourceName = source.getAbsolutePath().replace("\\", "/"); 
        sourceName = sourceName.substring(rootDirectory.length());
 
        if (sourceName.startsWith("/")) { 
            sourceName = sourceName.substring(1); 
        } 
 
        if ("META-INF/MANIFEST.MF".equals(sourceName) || sourceName.toLowerCase().endsWith(".java")) 
            return; 
 
        if (source.isDirectory()) { 
            for (File nested : source.listFiles()) { 
                addToJarRecursively(jar, nested, rootDirectory); 
            } 
            return; 
        } 
 
        JarEntry entry = new JarEntry(sourceName); 
        jar.putNextEntry(entry); 
        InputStream is = new FileInputStream(source); 
        try { 
            byte[] buffer = new byte[1024];
            while (true)
            {
              int count = is.read(buffer);
              if (count == -1)
                break;
              jar.write(buffer, 0, count);
            }
        } finally { 
            jar.closeEntry(); 
            is.close(); 
        } 
    }
    
//	private void createTheJar(File[] files, String targetJarFileName, String packageName) {
//		byte[] buffer = new byte[1024];
//
//		try {
//
//			FileOutputStream fos = new FileOutputStream(new File(targetJarFileName));
//			JarOutputStream zos = new JarOutputStream(fos);
//
//			for (int i = 0; i < files.length; i++) {
//				if (files[i] == null || !files[i].exists() || files[i].isDirectory())
//					continue; // Just in case..
//
//				String fname = packageName + files[i].getName();
//
//				JarEntry ze = new JarEntry(fname);
//				File f;
//				String s;
//
//				s = ze.getName();
//				if (File.separatorChar != '/')
//					s = s.replace('.', File.separatorChar);
//				f = new File(s);
//
//				s = f.getName();
//				if (File.separatorChar != '/')
//					s = s.replace(File.separatorChar, '/');
//				ze = new JarEntry(s);
//
//				ze.setTime(files[i].lastModified());
//
//				zos.putNextEntry(ze);
//
//				FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
//
//				while (true) {
//					int nRead = in.read(buffer, 0, buffer.length);
//					if (nRead <= 0)
//						break;
//					zos.write(buffer, 0, nRead);
//					zos.flush();
//				}
//
//				in.close();
//				zos.closeEntry();
//
//			}
//
//			zos.close();
//			fos.close();
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
}
