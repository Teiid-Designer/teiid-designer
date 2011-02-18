package com.metamatrix.modeler.core.refactor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.metamatrix.core.modeler.util.FileUtils;

public class ResourceRefactorFileHelper {
	
	public static void updateHrefsForFile(final File changedFile, final Map<String, String> refactoredPaths  ) throws IOException {
		
		IPath readPath = new Path(changedFile.getAbsolutePath());
		String writeFileString = readPath.removeFileExtension().toOSString() + "_A.xmi"; //$NON-NLS-1$
		File writeFile = new File(writeFileString);
		
		ResourceRefactorHrefHandler handler = new ResourceRefactorHrefHandler(new FileReader(changedFile), refactoredPaths, writeFile);

		handler.doReadAll();

		handler.doWriteAll();
	    
	    FileUtils.copy(writeFileString, readPath.toOSString(), true);
	    
	    writeFile.delete();
	}

	
	public static void main(String[] args) throws IOException {

	    Map<String, String> tokens = new HashMap<String, String>();
	    tokens.put("TopFolder/BottomFolder/RelModel_999999.xmi", "BottomFolder/RelModel_999999.xmi");  //$NON-NLS-1$//$NON-NLS-2$
	    
	    ResourceRefactorFileHelper.updateHrefsForFile(new File("/home/blafond/Temp/testdata/VirtModel.xmi"), tokens); //$NON-NLS-1$
	}

}
