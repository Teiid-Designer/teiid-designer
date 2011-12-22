/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.teiid.designer.vdb.Vdb.Xml;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

import com.metamatrix.core.modeler.util.OperationUtil;
import com.metamatrix.core.modeler.util.OperationUtil.Unreliable;

/**
 * Utility methods used to query VDB manifest and VDB's
 */
public class VdbUtil {
	private static final String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$
	
	
	/**
	 * @param theVdb
	 * @return list of vdb model files
	 */
	public static Collection<IFile> getVdbModels(Vdb theVdb) {
		Collection<IFile> iFiles = new ArrayList<IFile>();
		
		for( VdbModelEntry modelEntry : theVdb.getModelEntries() ) {
			//IPath modelPath = modelEntry.getName();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(modelEntry.getName());
			if( resource.exists() ) {
				iFiles.add((IFile)resource);
			}
		}
		
		return iFiles;
	}
	
	/**
	 * @param file
	 * @return preview attribute value for VDB. true or false
	 */
	public static boolean isPreviewVdb(final IFile file) {
        final boolean[] previewable = new boolean[1];
        
        if( !file.exists() ) {
        	return false;
        }
        
        OperationUtil.perform(new Unreliable() {

            ZipFile archive = null;
            InputStream entryStream = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (entryStream != null) entryStream.close();
                if (archive != null) archive.close();
            }

            @Override
            public void tryToDo() throws Exception {
                archive = new ZipFile(file.getLocation().toString());
                boolean foundManifest = false;
                for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                    final ZipEntry zipEntry = iter.nextElement();
                    entryStream = archive.getInputStream(zipEntry);
                    if (zipEntry.getName().equals(MANIFEST)) {
                        // Initialize using manifest
                    	foundManifest = true;
                        final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                        unmarshaller.setSchema(getManifestSchema());
                        final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(entryStream);

                        // VDB properties
                        for (final PropertyElement property : manifest.getProperties()) {
                            final String name = property.getName();
                            if (Xml.PREVIEW.equals(name)) {
                            	previewable[0] = Boolean.parseBoolean(property.getValue());
                            }
                        }
                    }
                    // Don't process any more than we need to.
                    if( foundManifest ) {
                    	break;
                    }
                }
            }
        });
        return previewable[0];
	}
	
	/**
	 * @param file
	 * @return version the vdb version number
	 */
	public static int getVdbVersion(final IFile file) {
        final int[] vdbVersion = new int[1];
        
        if( !file.exists() ) {
        	return 0;
        }
        OperationUtil.perform(new Unreliable() {

            ZipFile archive = null;
            InputStream entryStream = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (entryStream != null) entryStream.close();
                if (archive != null) archive.close();
            }

            @Override
            public void tryToDo() throws Exception {
                archive = new ZipFile(file.getLocation().toString());
                boolean foundManifest = false;
                for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                    final ZipEntry zipEntry = iter.nextElement();
                    entryStream = archive.getInputStream(zipEntry);
                    if (zipEntry.getName().equals(MANIFEST)) {
                        // Initialize using manifest
                    	foundManifest = true;
                        final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                        unmarshaller.setSchema(getManifestSchema());
                        final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(entryStream);
   
                        vdbVersion[0] = manifest.getVersion();
                    }
                    // Don't process any more than we need to.
                    if( foundManifest ) {
                    	break;
                    }
                }
            }
        });
        return vdbVersion[0];
	}
	
    private static JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] { VdbElement.class });
    }
	
    private static Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
    }

}
