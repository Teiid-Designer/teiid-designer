/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import org.jdom.Document;
import org.jdom.Element;
import com.metamatrix.common.vdb.api.DEFReaderWriter;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStream;
import com.metamatrix.common.xml.XMLReaderWriterImpl;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.runtime.BasicVDBDefn;

public class VDBWriter {

    private VDBWriter() {

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
        if (!(vdbDefn instanceof BasicVDBDefn)) {
            throw new Exception(VdbEditPlugin.Util.getString("VDBWriter.unexpectedVdbDefnClass", vdbDefn.getClass())); //$NON-NLS-1$
        }

        DEFReaderWriter writer = new DEFReaderWriter();
        writer.write(outputStream, (BasicVDBDefn)vdbDefn, headerProps);
        outputStream.close();
    }
}
