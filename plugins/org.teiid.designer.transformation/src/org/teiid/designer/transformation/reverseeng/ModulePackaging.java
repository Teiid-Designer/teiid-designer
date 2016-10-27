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
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.runtime.spi.TeiidExecutionException;
import org.teiid.designer.transformation.reverseeng.api.Options;
import org.teiid.designer.transformation.reverseeng.util.ObjectConverterUtil;

/**
 * ModulePackaging will package the created pojo jar into a module zip package for deployment
 *  
 * @author vanhalbert
 *
 */
public class ModulePackaging {
	
	private static final String REMOTE_CACHE_TEMPLATE = "org/teiid/designer/transformation/reverseeng/remote_cache_module_template.xml";
	private static final String LIBRARY_MODE_TEMPLATE = "org/teiid/designer/transformation/reverseeng/library_mode_module_template.xml";
	private static final String UNKNOWN_TEMPLATE = "org/teiid/designer/transformation/reverseeng/unknown_module_template.xml";

	private static final String MODULE = "modules";
	
	public void performPackaging(Options options, String packageName, File pojoJarFile, String moduleZipFileName,  File buildLocation, String packageFilePath, File kitLocation) throws Exception {
//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Starting to package module: " + moduleZipFileName);
		
		if (!pojoJarFile.exists()) {
			throw new TeiidExecutionException(IStatus.ERROR, "Pojo file: " + pojoJarFile + " doesn't exist");
		}
	
		// location for module related files
		File moduleRootLocation = new File(buildLocation, MODULE); 
		moduleRootLocation.mkdir();
		
		File moduleDirLoc = new File(moduleRootLocation.getAbsolutePath() + File.separator + packageFilePath.toString());
		moduleDirLoc.mkdirs();
		
		File moduleFile = new File(moduleDirLoc, "module.xml");
		
		String modTemp = UNKNOWN_TEMPLATE;
		
		String template = null;
		if (options.useHibernateAnnotations()) {
			template = LIBRARY_MODE_TEMPLATE;
			modTemp = getModuleTemplate(template);
			modTemp = MessageFormat.format(modTemp, packageName, pojoJarFile.getName());
		} else if (options.useProtobufAnnotations()) {
			template = REMOTE_CACHE_TEMPLATE;
			modTemp = getModuleTemplate(template);
			modTemp = MessageFormat.format(modTemp, packageName, pojoJarFile.getName());

		} else {
			template = UNKNOWN_TEMPLATE;
			modTemp = getModuleTemplate(template);
			modTemp = MessageFormat.format(modTemp, packageName, pojoJarFile.getName());
		}
		
		/**
		 * Mappings:
		 * 0 - package name
		 * 1 - pojo jar name
		 */
		
		
//		ReverseEngineerPlugin.LOGGER.debug("[ReverseEngineering] package and pojo jar: " + packageName + "," + pojoJarFile.getName());
		
		ObjectConverterUtil.write(modTemp.getBytes(), moduleFile.getAbsolutePath());
		
		File pojoDest = new File(moduleFile.getParent(), pojoJarFile.getName());

		FileUtils.copy(pojoJarFile.getAbsolutePath(), pojoDest.getAbsolutePath());

		File[] filesInModule = new File[2];  // jar and module files
		filesInModule[0]=pojoDest;
		filesInModule[1]=moduleFile;
		
		String moduleLocation = options.getProperty(Options.Parms.BUILD_LOCATION);
		createZip(
				filesInModule, 
				moduleLocation + File.separator + moduleZipFileName, 
				MODULE + File.separator + packageFilePath);
		
//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Completed packaging module: " + moduleZipFileName);

		
	}
	
	private String getModuleTemplate(String module_template) throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream(module_template);
		
		String modString = ObjectConverterUtil.convertToString(is);
		
		return modString;
		
	}
	
	final static int BUFFER = 2048;
	private void createZip(File[] files, String targetZip, String packageName) {
        byte[] buffer = new byte[BUFFER];

        if( !targetZip.toUpperCase().endsWith(".ZIP")) {
        	targetZip = targetZip + ".zip";
        }
        
        try{
            
            FileOutputStream    fos = new FileOutputStream(new File(targetZip));
            ZipOutputStream zos = new ZipOutputStream(fos);

			for (int i = 0; i < files.length; i++) {
				if (files[i] == null || !files[i].exists()
						|| files[i].isDirectory())
					continue; // Just in case..

				String fname = files[i].getName();
				ZipEntry ze= new ZipEntry(fname);
				ze.setTime(files[i].lastModified());
				
				
		        zos.putNextEntry(ze);
		        
		        FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
		        
				while (true) {
					int nRead = in.read(buffer, 0, buffer.length);
					if (nRead <= 0)
						break;
					zos.write(buffer, 0, nRead);
					zos.flush();
				}
                
				in.close();	
				zos.closeEntry();

			}
			
			zos.close();
			fos.close();
	
        }catch(IOException ex){
           ex.printStackTrace();
        }
    
	}

}
