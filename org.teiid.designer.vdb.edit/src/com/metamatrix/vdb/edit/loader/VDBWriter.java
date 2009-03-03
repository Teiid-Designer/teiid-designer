/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import org.jdom.Document;
import org.jdom.Element;
import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStream;
import com.metamatrix.common.xml.XMLReaderWriterImpl;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.core.util.ZipFileUtil;
import com.metamatrix.core.vdb.VdbConstants;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;

public class VDBWriter {

    private static final Random random = new Random(System.currentTimeMillis());

    private TempDirectory tempVDBFiles = null;

    private VDBWriter() {

    }

    /**
     * Export the vdb archive file the vdbArchiveFileName location. The VDBDefn will be added to the vdb archive. The headerProps
     * are optional, and provides the ability for the application exporting the vdb to add their own properties to the header
     * section in the xml formatted .DEF file.
     * 
     * @param vdbDefn to be added to the archive
     * @param headerProps, optional and nullable
     * @throws Exception
     * @since 4.2
     */
    public static void exportVDBArchive( OutputStream vdbArchive,
                                         VDBDefn vdbDefn,
                                         Properties headerProps ) throws Exception {
        if (vdbDefn == null) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_defintion")); //$NON-NLS-1$
        }

        byte[] vdbContents = vdbDefn.getVDBStream().toByteArray();
        if (vdbContents == null || vdbContents.length == 0) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_archive")); //$NON-NLS-1$
        }
        VDBWriter writer = new VDBWriter();
        try {

            writer.exportToOutputStream(vdbArchive, vdbDefn, headerProps);
        } finally {
            writer.cleanupTempFiles();
        }

    }

    /**
     * Export the vdb archive file the vdbArchiveFileName location. The VDBDefn will be added to the vdb archive. The headerProps
     * are optional, and provides the ability for the application exporting the vdb to add their own properties to the header
     * section in the xml formatted .DEF file.
     * 
     * @param vdbDefn to be added to the archive
     * @param headerProps, optional and nullable
     * @throws Exception
     * @since 4.2
     */
    public static void exportVDBArchive( String vdbArchiveFileName,
                                         VDBDefn vdbDefn,
                                         Properties headerProps ) throws Exception {
        if (vdbDefn == null) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_defintion")); //$NON-NLS-1$
        }

        VDBStream vdbContents = vdbDefn.getVDBStream();
        if (vdbContents == null) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_archive")); //$NON-NLS-1$
        }

        writeVDBArchive(null, vdbArchiveFileName, vdbContents);

        addVDBDefnToArchive(vdbDefn, vdbArchiveFileName, headerProps);
    }

    /**
     * Add the VDBDefn to the specified vdb archive file. The headerProps are optional, and provides the ability for the
     * application exporting the vdb to add their own properties to the header section in the xml formatted .DEF file.
     * 
     * @param vdbDefn VDBDefn that will be loaded into the VDB Archive
     * @param vdbArchiveFileName is an existing .VDB file
     * @param headerProps, optional and nullable
     * @throws Exception
     * @since 4.2
     */
    static void addVDBDefnToArchive( VDBDefn vdbDefn,
                                     String vdbArchiveFileName,
                                     Properties headerProps ) throws Exception {
        if (vdbDefn == null) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_defintion")); //$NON-NLS-1$
        }

        if (vdbArchiveFileName == null || vdbArchiveFileName.trim().length() == 0) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_archive_filename")); //$NON-NLS-1$
        }

        VDBWriter writer = new VDBWriter();
        try {

            writer.addDefnToVDBArchive(vdbDefn, vdbArchiveFileName, headerProps);

        } finally {
            writer.cleanupTempFiles();
        }

    }

    /**
     * Export the VDBDefn to the specified vdbDefnFileName. The .VDB archive will be written to the vdbArchiveFileName. The
     * directory, if specified, will control where the files will be written to. The headerProps are optional, and provides the
     * ability for the application exporting the vdb to add their own properties to the header section in the xml formatted .DEF
     * file. Note: This support the exporting logic prior to 4.3 when both the .DEF and .VDB where exported.
     * 
     * @param vdbDefn VDBDefn that will be exported
     * @param vdbDefnFileName The file location the VDBDefn will be exported to
     * @param vdbArchiveFileName The file location the VDB Arhive will be written to
     * @param directory, optional and nullable
     * @param headerProps, optional and nullable
     * @throws Exception
     * @since 4.3
     */
    public static void exportVDBDefn( VDBDefn vdbDefn,
                                      String vdbDefnFileName,
                                      String vdbArchiveFileName,
                                      String directory,
                                      Properties headerProps ) throws Exception {
        if (vdbDefn == null) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_defintion")); //$NON-NLS-1$
        }

        if (vdbDefnFileName == null || vdbDefnFileName.trim().length() == 0) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_defintion_filename")); //$NON-NLS-1$
        }

        if (vdbArchiveFileName == null || vdbArchiveFileName.trim().length() == 0) {
            ArgCheck.isNotNull(vdbDefn, VdbEditPlugin.Util.getString("VDBWriter.Invalid_VDB_archive_filename")); //$NON-NLS-1$
        }

        // create new definition file
        if (directory != null && directory.trim().length() == 0) {
            directory = null;
        }
        File fDef = new File(directory, vdbDefnFileName);

        FileOutputStream outDef = new FileOutputStream(fDef);

        writeVDBDefn(outDef, vdbDefn, headerProps);
        if (vdbArchiveFileName == null) {
            if (vdbDefn.getFileName() != null) {

                vdbArchiveFileName = vdbDefn.getFileName();
            } else {
                vdbArchiveFileName = FileUtils.toFileNameWithExtension(vdbDefn.getName(), VDBConstants.VDB_ARCHIVE_FILE_EXTENSION);
            }
        }
        writeVDBArchive(directory, vdbDefn.getFileName(), vdbDefn.getVDBStream());

    }

    static void writeVDBArchive( String dir,
                                 String archiveFileName,
                                 VDBStream vdbStream ) {

        if (vdbStream != null) {
            File f = null;
            if (dir != null) {
                f = new File(dir, archiveFileName);
            } else {
                f = new File(archiveFileName);
            }

            if (f.exists()) {
                f.delete();
            }
            try {
                FileUtils.write(vdbStream.getInputStream(), f);
            } catch (IOException e) {
                throw new MetaMatrixRuntimeException(e);
            }
        }

    }

    public static void updateConfigDefFile( File deffile,
                                            String vdbName,
                                            Properties executionProps ) throws Exception {
        VDBDefnXMLHelper helper = new VDBDefnXMLHelper();

        Element root = helper.getRoot(deffile);

        // create a new Document with a root element
        Document doc = root.getDocument();

        helper.updateVDBName(root, vdbName, vdbName + VDBConstants.VDB_ARCHIVE_FILE_EXTENSION);

        if (!executionProps.isEmpty()) {
            helper.updateExecutionProperties(root, executionProps);
        }

        FileOutputStream fos = new FileOutputStream(deffile);

        try {
            new XMLReaderWriterImpl().writeDocument(doc, fos);

        } catch (Exception e) {
            try {
                fos.close();
            } catch (Exception e1) {

            }
        }

    }

    /**
     * Called to export the {@link VDBDefn VDBDefn} to the specified outputStream. Note: This support the exporting logic prior to
     * 4.3 when both the .DEF and .VDB where exported.
     * 
     * @param outputStream
     * @param vdbDefn
     * @param headerProps
     * @throws IOException
     * @throws Exception
     * @since 4.3
     */
    public static void writeVDBDefn( OutputStream outputStream,
                                     VDBDefn vdbDefn,
                                     Properties headerProps ) throws IOException, Exception {

        VDBWriter writer = new VDBWriter();
        writer.writeVDBDefnToOutputStream(outputStream, vdbDefn, null, headerProps);
    }

    private void writeVDBDefnToOutputStream( OutputStream outputStream,
                                             VDBDefn vdbDefn,
                                             String archiveFileName,
                                             Properties headerProps ) throws IOException, Exception {
        XMLConfigurationImportExportUtility exportUtil = new XMLConfigurationImportExportUtility();

        VDBDefnXMLHelper vdbHelper = new VDBDefnXMLHelper();
        Element root = vdbHelper.createRootDocumentElement();

        // create a new Document with a root element
        Document doc = new Document(root);

        // Add header
        vdbHelper.addHeaderElement(root, headerProps);

        // Add VDBInfo
        Element vdbDefnElement = vdbHelper.createVDBInfoElement(vdbDefn, archiveFileName);
        root.addContent(vdbDefnElement);

        // contains the unique set of binding uuids, even though a binding
        // maybe referenced multiple times.
        Set routingNames = new HashSet();

        // Export models
        for (Iterator it = vdbDefn.getModels().iterator(); it.hasNext();) {
            BasicVDBModelDefn md = (BasicVDBModelDefn)it.next();

            Element modelElement = vdbHelper.createVDBModelElement(md);

            List connectorBindingNames = md.getConnectorBindingNames();

            if (connectorBindingNames != null && connectorBindingNames.size() > 0) {
                // List bindingNames = new ArrayList(connectorBindingUUIDs.size());
                // for (Iterator bnit=connectorBindingUUIDs.iterator(); bnit.hasNext();) {
                // String uuid = (String) bnit.next();
                // ConnectorBinding cb = vdbDefn.getConnectorBinding(uuid);
                // if (cb != null) {
                // bindingNames.add(cb.getFullName());
                // }
                // }

                vdbHelper.addConnectorRefs(connectorBindingNames, modelElement);
                routingNames.addAll(connectorBindingNames);
            }

            root.addContent(modelElement);
        }

        ConnectorBinding[] connectorBindings = new ConnectorBinding[routingNames.size()];
        ComponentType[] connectorTypes = new ComponentType[routingNames.size()];

        // Export the unique set of connector bindings and connector types
        Iterator it = routingNames.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            String cbName = (String)it.next();
            ConnectorBinding cb = vdbDefn.getConnectorBindingByName(cbName);
            if (cb != null) {
                connectorBindings[i] = cb;

                ComponentType ctype = vdbDefn.getConnectorType(cb.getComponentTypeID().getName());
                connectorTypes[i] = ctype;
            }
        }

        exportUtil.exportConnectorBindings(connectorBindings, connectorTypes, root);

        new XMLReaderWriterImpl().writeDocument(doc, outputStream);
        outputStream.close();

    }

    /**
     * Call this to add the VDBDefn to the vdbarchive
     * 
     * @param vdbDefn
     * @param vdbArchiveFile
     * @param headerProps
     * @throws IOException
     * @throws Exception
     * @since 4.2
     */
    private void addDefnToVDBArchive( VDBDefn vdbDefn,
                                      String vdbArchiveFile,
                                      Properties headerProps ) throws IOException, Exception {
        createTempDir();

        File arc = new File(vdbArchiveFile);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeVDBDefnToOutputStream(bos, vdbDefn, arc.getName(), headerProps);
        byte[] contents = bos.toByteArray();
        bos.close();

        File defnFile = writeTempFile(contents, VdbConstants.DEF_FILE_NAME);

        File existingDefFile = VDBReader.getVDBDefFile(vdbArchiveFile);

        if (existingDefFile != null) {
            ZipFileUtil.remove(vdbArchiveFile, existingDefFile.getName(), true);
        }

        boolean added = ZipFileUtil.add(vdbArchiveFile, defnFile.getName(), defnFile.getAbsolutePath());
        if (!added) {
            throw new MetaMatrixComponentException(VdbEditPlugin.Util.getString("VDBWriter.Unable_to_add_vdbdefn_to_archive")); //$NON-NLS-1$    
        }

        // write dataroles file to archive
        char[] dataRoleContents = vdbDefn.getDataRoles();
        if (dataRoleContents != null && dataRoleContents.length > 0) {
            // First check for existing file and remove
            File existingRolesFile = VDBReader.getDataRolesFile(vdbArchiveFile);
            if (existingRolesFile != null) {
                ZipFileUtil.remove(vdbArchiveFile, existingRolesFile.getName(), true);
            }

            File dataRolesFile = writeTempFile(dataRoleContents, VdbConstants.DATA_ROLES_FILE);
            added = ZipFileUtil.add(vdbArchiveFile, dataRolesFile.getName(), dataRolesFile.getAbsolutePath());
            if (!added) {
                throw new MetaMatrixComponentException(VdbEditPlugin.Util.getString("VDBWriter.Unable_to_add_roles_to_archive")); //$NON-NLS-1$    
            }
        }
    }

    private void exportToOutputStream( OutputStream vdbArchiveOS,
                                       VDBDefn vdbDefn,
                                       Properties headerProps ) throws IOException, Exception {
        createTempDir();

        // write it out so that the defn can be added
        File archiveFile = writeTempFile(vdbDefn.getVDBStream().toByteArray(),
                                         vdbDefn.getFileName() != null ? vdbDefn.getFileName() : (vdbDefn.getName() + ".vdb"));//$NON-NLS-1$

        // add the defn to the archive
        addDefnToVDBArchive(vdbDefn, archiveFile.getAbsolutePath(), headerProps);

        FileUtils.write(archiveFile, vdbArchiveOS);

    }

    private void createTempDir() throws MetaMatrixComponentException {
        if (tempVDBFiles != null) {
            return;
        }
        try {
            // must write the vdbFile to the filesystem because
            // the vdbcontexteditor only takes a filename, not an
            // inputstream or other type.
            tempVDBFiles = new TempDirectory(System.currentTimeMillis(), random.nextLong());
            tempVDBFiles.create();
        } catch (Exception e) {
            throw new MetaMatrixComponentException(e);
        }

    }

    private File writeTempFile( byte[] contents,
                                String fileName ) throws MetaMatrixComponentException {

        File tempFile = null;
        try {
            tempFile = new File(tempVDBFiles.getPath(), fileName);
            new FileUtil(tempFile.getAbsolutePath()).writeBytes(contents);
        } catch (Exception e) {
            throw new MetaMatrixComponentException(e);
        }
        return tempFile;

    }

    private File writeTempFile( char[] contents,
                                String fileName ) throws MetaMatrixComponentException {

        File tempFile = null;
        try {
            tempFile = new File(tempVDBFiles.getPath(), fileName);
            new FileUtil(tempFile.getAbsolutePath()).write(new String(contents));
        } catch (Exception e) {
            throw new MetaMatrixComponentException(e);
        }
        return tempFile;
    }

    private void cleanupTempFiles() {
        if (tempVDBFiles != null) {
            tempVDBFiles.remove();
            tempVDBFiles = null;
        }
    }
}
