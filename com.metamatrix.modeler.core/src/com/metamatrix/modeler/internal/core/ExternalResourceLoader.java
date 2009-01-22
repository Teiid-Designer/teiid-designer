/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

public class ExternalResourceLoader {

    private static final String BUNDLE_RESOURCE_URL = "bundleresource://"; //$NON-NLS-1$
    protected static final String EXTENSION_CHAR = "."; //$NON-NLS-1$
    protected static final String SUFFIX_JAR = ".JAR"; //$NON-NLS-1$
    protected static final String SUFFIX_ZIP = ".ZIP"; //$NON-NLS-1$
    protected static final String SUFFIX_VDB = ".VDB"; //$NON-NLS-1$

    public ExternalResourceLoader() {
    }

    public Resource load( final ExternalResourceDescriptor descriptor,
                          final Container container ) throws ModelerCoreException {
        if (descriptor == null) {
            ArgCheck.isNotNull(descriptor,
                               ModelerCore.Util.getString("ExternalResourceLoader.The_ExternalResourceDescriptor_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        if (container == null) {
            ArgCheck.isNotNull(container,
                               ModelerCore.Util.getString("ExternalResourceLoader.The_Container_reference_may_not_be_null_2")); //$NON-NLS-1$
        }

        // Validate the information in the descriptor
        this.validateDescriptor(descriptor);

        // Obtain the external resource information for loading
        String resourceUrl = descriptor.getResourceUrl();
        String resourceName = descriptor.getResourceName();
        String internalUri = descriptor.getInternalUri();
        String tempDirPath = this.getTempDirectoryPath(descriptor);

        File resourceFile = retrieveResourceFile(resourceUrl, resourceName, tempDirPath);

        // Check that the retrieved resource is a EMF resource
        if (resourceFile == null) {
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ExternalResourceLoader.An_existing_resource_with_the_name_cannot_retrieved_from_{1}._7", resourceName, resourceUrl)); //$NON-NLS-1$
        }
        if (!ModelUtil.isModelFile(resourceFile)) {
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ExternalResourceLoader.The_external_resource_with_the_name_is_not_a_MetaMatrix_model_resource._8", resourceName)); //$NON-NLS-1$
        }

        // Load the EMF resource into the container
        return loadResource(container, resourceFile, internalUri);
    }

    public void validateDescriptor( final ExternalResourceDescriptor descriptor ) throws ModelerCoreException {
        if (descriptor == null) {
            ArgCheck.isNotNull(descriptor,
                               ModelerCore.Util.getString("ExternalResourceLoader.The_ExternalResourceDescriptor_reference_may_not_be_null_1")); //$NON-NLS-1$
        }

        if (descriptor.getResourceUrl() == null || descriptor.getResourceUrl().length() == 0) {
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ExternalResourceLoader.The_ExternalResourceDescriptor_may_not_have_a_null_or_empty_resource_URL_string_3")); //$NON-NLS-1$
        }
        if (descriptor.getResourceName() == null || descriptor.getResourceName().length() == 0) {
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ExternalResourceLoader.The_ExternalResourceDescriptor_may_not_have_a_null_or_empty_resource_name_string_4")); //$NON-NLS-1$
        }

        final String resourceUrl = descriptor.getResourceUrl();
        final String resourceName = descriptor.getResourceName();

        // validate only if this is file based URL
        if (!resourceUrl.startsWith(BUNDLE_RESOURCE_URL)) {

            // Check if the external resource exists ...
            File f = new File(resourceUrl);
            if (!f.exists()) {
                // Look for resource in classpath (for running outside of Eclipse)
                final URL url = ClassLoader.getSystemResource(resourceUrl);
                if (url == null) {
                    throw new IllegalArgumentException(
                                                       ModelerCore.Util.getString("ExternalResourceLoader.The_resource_with_name_and_location_cannot_be_found_on_the_file_system._2", resourceName, resourceUrl)); //$NON-NLS-1$
                }
                final String loc = url.getPath();
                ((ExternalResourceDescriptorImpl)descriptor).setResourceUrl(loc);
                f = new File(loc);
            }

            // The resource location is an archive ...
            if (this.isArchiveFileName(resourceUrl)) {
            }
            // The resource location is a folder ...
            else if (f.isDirectory()) {
            }
            // The resource location is the resource itself ...
            else if (f.isFile() && f.getName().equalsIgnoreCase(resourceName)) {
            }
            // The specified resource cannot be interpretted
            else {
                throw new ModelerCoreException(
                                               ModelerCore.Util.getString("ExternalResourceLoader.The_resource_with_name_and_location_cannot_be_processed_by_the_loader._6", resourceName, resourceUrl)); //$NON-NLS-1$
            }
        }
    }

    private File retrieveResourceFile( final String resourceUrl,
                                       final String resourceName,
                                       final String tempDirectoryPath ) throws ModelerCoreException {

        File resourceFile = null;

        // since this could be jar/zip/vdb file, and ZipUtil only works with the
        // physical files, we need to materialize this.
        if (resourceUrl.startsWith(BUNDLE_RESOURCE_URL)) {
            String fileName = StringUtil.getLastToken(resourceUrl, "/"); //$NON-NLS-1$
            resourceFile = new File(tempDirectoryPath, fileName);
            if (!resourceFile.exists()) {
                try {
                    FileUtils.write(new URL(resourceUrl).openStream(), resourceFile);
                } catch (IOException e) {
                    throw new ModelerCoreException(
                                                   ModelerCore.Util.getString("ExternalResourceLoader.An_existing_resource_with_the_name_cannot_retrieved_from_{1}._7", resourceName, resourceUrl)); //$NON-NLS-1$				
                }
            }
        } else {
            resourceFile = new File(resourceUrl);
            // Retrieve the resource from the folder
            if (resourceFile.isDirectory()) {
                return retrieveResourceFromFolder(resourceFile, resourceName);
            }
        }

        // Retrieve the resource from the archive
        if (this.isArchiveFileName(resourceFile.getAbsolutePath())) {
            return retrieveResourceFromZip(resourceFile, resourceName, tempDirectoryPath);
        }
        return resourceFile;
    }

    /**
     * Load the model resource into the specified container
     * 
     * @param container to load the resource into
     * @param resourceFile the File instance representing the model resource
     * @param internalUri the string to use as a logical URI reference to this resource
     * @return
     * @throws CoreException
     */
    protected Resource loadResource( final Container container,
                                     final File resourceFile,
                                     final String internalUri ) throws ModelerCoreException {

        if (resourceFile == null) {
            ArgCheck.isNotNull(resourceFile,
                               ModelerCore.Util.getString("ExternalResourceLoader.The_java.io.File_reference_may_not_be_null_11")); //$NON-NLS-1$
        }

        if (!resourceFile.exists()) {
            ArgCheck.isTrue(resourceFile.exists(),
                            ModelerCore.Util.getString("ExternalResourceLoader.The_java.io.File_reference_must_exist_12")); //$NON-NLS-1$
        }

        try {
            // Create the physical URI
            URI physicalURI = URI.createFileURI(resourceFile.getAbsolutePath());

            // Create the logical URI
            URI logicalURI = null;
            if (internalUri != null && internalUri.length() > 0) {
                logicalURI = URI.createURI(internalUri);
            }

            Resource externalResource = null;

            // Create an external resource and load it into the container using the logical URI
            if (logicalURI != null && container instanceof ContainerImpl) {
                // Get the appropriate resource factory to use for creating the new
                // resource instance. We use the physical URI assuming it has a file
                // extension or protocol found in the factory registry maps.
                ResourceSet resourceSet = ((ContainerImpl)container).getResourceSet();
                Resource.Factory resourceFactory = resourceSet.getResourceFactoryRegistry().getFactory(physicalURI);
                if (resourceFactory != null) {

                    // Create resource using the logical URI
                    externalResource = resourceFactory.createResource(logicalURI);
                    resourceSet.getResources().add(externalResource);

                    // Load the external resource from the file
                    FileInputStream fis = null;
                    InputStream bis = null;
                    try {
                        fis = new FileInputStream(resourceFile);
                        bis = new BufferedInputStream(fis);
                        Map options = (externalResource.getResourceSet() != null ? externalResource.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
                        externalResource.load(bis, options);
                    } finally {
                        if (bis != null) {
                            bis.close();
                        }
                        if (fis != null) {
                            fis.close();
                        }
                    }
                }
            }
            // Create an external resource and load it into the container using the physical URI
            if (externalResource == null) {
                externalResource = container.getResource(physicalURI, true);

                // Add a logical URI mapping to the resource set
                if (logicalURI != null) {
                    container.getURIConverter().getURIMap().put(logicalURI, physicalURI);
                }
            }

            return externalResource;
        } catch (Throwable e) {
            throw new ModelerCoreException(
                                           e,
                                           ModelerCore.Util.getString("ExternalResourceLoader.Error_loading_the_external_resource_into_the_container._13", resourceFile)); //$NON-NLS-1$
        }
    }

    /**
     * Return the File constructed from model files found in the specified folder
     * 
     * @param folder
     * @return
     * @throws IOException
     */
    protected File retrieveResourceFromFolder( final File folder,
                                               final String fileName ) throws ModelerCoreException {

        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile() && f.exists() && f.getName().equalsIgnoreCase(fileName)) {
                return f;
            }
        }
        throw new ModelerCoreException(
                                       ModelerCore.Util.getString("ExternalResourceLoader.An_existing_file_with_the_name_cannot_be_found_under_the_directory._14", fileName, folder)); //$NON-NLS-1$
    }

    /**
     * Return the File[] constructed from model files found in the specified archive
     * 
     * @param folder
     * @return
     * @throws IOException
     */
    protected File retrieveResourceFromZip( final File zip,
                                            final String zipEntryName,
                                            final String tempDirectoryLocation ) throws ModelerCoreException {

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zip);

            // Iterate over all entries in the zip file ...
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if (entry == null) {
                    break;
                }
                // Find the specified entry by name
                if (entry.getName().equalsIgnoreCase(zipEntryName)) {

                    // Read the contents of the entry
                    InputStream inputStream = zipFile.getInputStream(entry);

                    // Buffer that would contain the contents of the entry
                    byte[] buffer;
                    int length = (int)entry.getSize();
                    if (length >= 0) {
                        buffer = new byte[length];

                        int offset = 0;
                        do {
                            int n = inputStream.read(buffer, offset, length);
                            offset += n;
                            length -= n;
                        } while (length > 0);
                    } else {
                        buffer = new byte[1024];
                        int n;
                        do {
                            n = inputStream.read(buffer, 0, 1024);
                        } while (n >= 0);
                    }

                    // Set the location for the extracted file
                    File extractDirectory = new File(tempDirectoryLocation);
                    if (!extractDirectory.exists()) {
                        extractDirectory.mkdir();
                    }

                    // Extract the entry that is the external resource
                    // Create a temporary file for the external resource and write
                    // the contents of the zip entry to this file
                    String entryName = (new Path(entry.getName())).lastSegment();
                    File entryFile = new File(extractDirectory + File.separator + entryName);
                    entryFile.createNewFile();
                    entryFile.deleteOnExit();
                    FileOutputStream outputStream = new FileOutputStream(entryFile);
                    outputStream.write(buffer);
                    outputStream.flush();
                    outputStream.close();

                    return entryFile;
                }
            }
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ExternalResourceLoader.An_existing_entry_with_the_name_cannot_be_found_in_the_archive._15", zipEntryName, zip)); //$NON-NLS-1$

        } catch (Throwable e) {
            throw new ModelerCoreException(
                                           e,
                                           ModelerCore.Util.getString("ExternalResourceLoader.An_existing_entry_with_the_name_cannot_be_found_in_the_archive._16", zipEntryName, zip)); //$NON-NLS-1$
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
            }// Ignore
        }
    }

    protected String getTempDirectoryPath( final ExternalResourceDescriptor descriptor ) {
        if (descriptor == null) {
            ArgCheck.isNotNull(descriptor,
                               ModelerCore.Util.getString("ExternalResourceLoader.The_ExternalResourceDescriptor_reference_may_not_be_null_1")); //$NON-NLS-1$
        }

        ArgCheck.isInstanceOf(ExternalResourceDescriptorImpl.class,
                              descriptor,
                              ModelerCore.Util.getString("ExternalResourceLoader.The_ExternalResourceDescriptor_must_be_an_instanceof_ExternalResourceDescriptorImpl_1")); //$NON-NLS-1$

        final ExternalResourceDescriptorImpl descriptorImpl = (ExternalResourceDescriptorImpl)descriptor;
        String tempDirPath = descriptorImpl.getTempDirectoryPath();
        if (tempDirPath == null || tempDirPath.length() == 0) {
            try {
                tempDirPath = File.createTempFile("temp", null).getParent(); //$NON-NLS-1$
            } catch (final IOException err) {
                tempDirPath = System.getProperty("user.dir"); //$NON-NLS-1$
            }
        }
        return tempDirPath;
    }

    /**
     * Returns true iff str.toUpperCase().endsWith(".JAR") || str.toUpperCase().endsWith(".ZIP") ||
     * str.toUpperCase().endsWith(".VDB")
     */
    protected boolean isArchiveFileName( String name ) {
        if (StringUtil.isEmpty(name)) {
            return false;
        }
        final String upperCaseName = name.toUpperCase();
        if (upperCaseName.endsWith(SUFFIX_JAR) || upperCaseName.endsWith(SUFFIX_ZIP) || upperCaseName.endsWith(SUFFIX_VDB)) {
            return true;
        }
        return false;
    }
}
