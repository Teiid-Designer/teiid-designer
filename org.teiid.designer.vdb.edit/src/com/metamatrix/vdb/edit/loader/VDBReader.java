/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jdom.Element;
import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.util.ByteArrayHelper;
import com.metamatrix.common.util.WSDLServletUtil;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStreamImpl;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.FileUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.core.vdb.VDBStatus;
import com.metamatrix.core.vdb.VdbConstants;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.validation.Severity;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.vdb.edit.VdbContext;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.edit.VdbContextImpl;
import com.metamatrix.vdb.internal.edit.VdbEditingContextImpl;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;
import com.metamatrix.vdb.materialization.ScriptType;
import com.metamatrix.vdb.runtime.BasicModelInfo;
import com.metamatrix.vdb.runtime.BasicVDBDefn;

public class VDBReader {

    private static final String TEMP_VDB_NAME = "tmp.vdb";//$NON-NLS-1$
    private static final Random random = new Random(System.currentTimeMillis());

    public final static String[] VDB_READER_EXTENSIONS = new String[] {VdbConstants.VDB_DEF_FILE_EXTENSION,
        VdbConstants.VDB_ARCHIVE_EXTENSION};

    private VdbContext editor = null;
    private TempDirectory tempVDBFiles = null;
    private ConfigurationModelContainer configContainer = null;

    protected VDBReader() {
        super();
    }

    protected VDBReader( ConfigurationModelContainer configContainerModel ) {
        super();
        this.configContainer = configContainerModel;
    }

    public static boolean isValidVDBDefFileIncluded( String vdbArchiveFileName ) throws Exception {
        VDBReader factory = new VDBReader();

        try {
            factory.createVDBContextEditor(new File(vdbArchiveFileName));

            return factory.isValidDefnFile();
        } finally {
            factory.cleanup(true);
        }

    }

    public static boolean isValidVDBDefFileIncluded( byte[] vdbArchive ) throws Exception {
        VDBReader factory = new VDBReader();

        try {
            factory.createVDBContextEditor(TEMP_VDB_NAME, vdbArchive);

            return factory.isValidDefnFile();
        } finally {
            factory.cleanup(true);
        }

    }

    public static File getVDBDefFile( String vdbArchiveFileName ) throws Exception {
        VDBReader factory = new VDBReader();

        try {
            factory.createVDBContextEditor(new File(vdbArchiveFileName));

            return factory.getVDBDefnFile();
        } finally {
            factory.cleanup(true);
        }

    }

    public static File getDataRolesFile( String vdbArchiveFileName ) throws Exception {
        VDBReader factory = new VDBReader();

        try {
            factory.createVDBContextEditor(new File(vdbArchiveFileName));

            return factory.getDataRolesFile();
        } finally {
            factory.cleanup(true);
        }

    }

    /**
     * Create a {@link VDBDefn VDBDefn} from the specified xml formated <code>fileName</code>. If the <code>dir</code> is not
     * specified, it is expected that the <code>fileName</code> will be an absolute path or can be found in the classpath. This
     * method will create a VDBDefn from either a .VDB or .DEF file.
     * 
     * @param fileName
     * @param directory is optional and nullable
     * @return
     * @throws Exception
     * @since 4.2
     */
    public static InputStream createVDBDefnInputStream( VDBDefn vdbDefn,
                                                        Properties headerProps ) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        VDBWriter.writeVDBDefn(bos, vdbDefn, headerProps);
        byte[] contents = bos.toByteArray();
        bos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(contents);

        return bis;
    }

    /**
     * Create a {@link VDBDefn VDBDefn} from the specified xml formated <code>fileName</code>. If the <code>dir</code> is not
     * specified, it is expected that the <code>fileName</code> will be an absolute path or can be found in the classpath. This
     * method will create a VDBDefn from either a .VDB or .DEF file.
     * 
     * @param fileName
     * @param directory is optional and nullable
     * @return
     * @throws Exception
     * @since 4.2
     */
    public static VDBDefn loadVDBDefn( String fileName,
                                       String directory ) throws Exception {
        return loadVdbDefn(fileName, directory, true, null);
    }

    /**
     * Create a {@link VDBDefn VDBDefn} from the specified xml formated <code>fileName</code>. If the <code>dir</code> is not
     * specified, it is expected that the <code>fileName</code> will be an absolute path or can be found in the classpath. This
     * method will create a VDBDefn from either a .VDB or .DEF file.
     * 
     * @param fileName
     * @param directory is optional and nullable
     * @param closeContext if true, the VdbEditingContext will be closed after this method is called.
     * @param archiveSoftRef is optional and nullable, when specified will cause the .VDB file not to loaded into memory until
     *        requested from the VDBDefn.
     * @return
     * @throws Exception
     * @since 4.2
     */
    private static VDBDefn loadVdbDefn( String fileName,
                                        String directory,
                                        boolean closeContext,
                                        File archiveSoftRef ) throws Exception {
        VDBReader factory = new VDBReader();
        File f = null;
        if (directory != null) {
            f = new File(directory, fileName);
        } else {
            f = new File(fileName);
        }

        try {

            boolean isDotVDB = isVDBArchive(f.getAbsolutePath());

            if (isDotVDB) {
                factory.createVDBContextEditor(f.getAbsoluteFile());

                byte[] vdbContent = null;
                if (archiveSoftRef == null) {
                    vdbContent = ByteArrayHelper.toByteArray(f);
                }

                // the .DEF file will be extracted from the archive file
                return factory.createVDBDefnFromContextEditor(null, vdbContent, archiveSoftRef, true);
            }

            if (isVDBDef(f.getAbsolutePath())) {
                // process as a .DEF file
                return factory.createVDBDefn(f, archiveSoftRef);
            }

            throw new MetaMatrixRuntimeException(VdbEditPlugin.Util.getString("VDBReader.Invalid_file_type", f.getAbsolutePath()));//$NON-NLS-1$

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;
        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            factory.cleanup(closeContext);
        }

    }

    /**
     * Create a {@link VDBDefn VDBDefn} from the specified <code>defFile</code> and load the VDBDefn withe model information from
     * the <code>vdbFile</code> If the <code>defFile</code> is not passed, then the .DEF file expected to be found inside the
     * vdbFile archive.
     * 
     * @param name
     * @param vdbFile is the .vdb archive file
     * @param defFile is the .DEF file, is optional and nullable
     * @return
     * @throws Exception
     * @since 4.2
     */
    public static VDBDefn loadVDBDefn( String name,
                                       byte[] vdbFile,
                                       char[] defFile ) throws Exception {

        VDBReader factory = new VDBReader();

        try {
            VDBDefn defn = null;
            if (defFile == null || defFile.length == 0) {
                factory.createVDBContextEditor(name, vdbFile);

                // the .DEF file will be extracted from the archive file
                defn = factory.createVDBDefnFromContextEditor(name, vdbFile, null, false);
            } else {
                defn = factory.createVDBDefnFromDEF(name, vdbFile, defFile, null);
            }

            return defn;

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            factory.cleanup(true);
        }
    }

    /**
     * Create a {@link VDBDefn VDBDefn} from the specified <code>vdbFile</code> archive file. Load the VDBDefn withe model
     * information from the <code>vdbFile</code>. This process will not fail if the {@link VdbConstants#DEF_FILE_NAME} is not
     * contained in the archive file.
     * 
     * @param name
     * @param vdbFile is the .vdb archive file
     * @param vdbName, is optional, is the name to assign to the <code>VDBDefn</code>
     * @return VDBDefn
     * @throws Exception
     * @since 4.2
     */

    static VDBDefn loadVDBDefn( byte[] vdbFile,
                                String vdbName ) throws Exception {
        VDBReader factory = new VDBReader();

        try {
            VDBDefn defn = null;
            factory.createVDBContextEditor(vdbName, vdbFile);

            // the .DEF file will be extracted from the archive file if it exist
            defn = factory.createVDBDefnFromContextEditor(vdbName, vdbFile, null, false);

            return defn;

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            factory.cleanup(true);
        }
    }

    /**
     * Call to load a VDBDefn from the vdbFile byte array. Note, this method must write out the vdbFile to the local file system
     * so that the vdbContextEditor can get ahold of it. If 'true', the transferBindingsFromDefn flag will copy the binding info
     * from the supplied vdbDefn, otherwise the binding info from the vdbArchive is retained.
     * 
     * @param vdbDefn the supplied VDBDefn
     * @param vdbArchive the supplied vdb file in byte[] format
     * @param transferBindingsFromDefn flag to determine whether to copy binding info from supplied vdbDefn or supplied
     *        vdbArchive.
     * @return the resultant VDBDefn
     * @throws MetaMatrixCoreException
     */
    public static VDBDefn loadVDBDefn( VDBDefn vdbDefn,
                                       byte[] vdbArchive,
                                       boolean transferBindingsFromDefn ) throws MetaMatrixCoreException {

        VDBReader factory = new VDBReader();

        try {
            return factory.createVDBDefnFromContextEditor(vdbDefn, vdbArchive, transferBindingsFromDefn);

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;
        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            try {
                factory.cleanup(true);
            } catch (Exception e) {

            }
        }
    }

    /**
     * Call to creat a VDBDefn from the contextEditor. Note: The VDBDefn will not contain the vdbarchive.
     * 
     * @param contextEditor
     * @return VDBDefn without the vdbarchive
     * @throws Exception
     * @since 4.2
     */
    public static VDBDefn loadVDBDefn( final VdbEditingContextImpl contextEditor ) throws Exception {
        VDBDefn defn = null;
        if (!contextEditor.isOpen()) {
            return loadVDBDefn(contextEditor, true);
        }

        File vdbArchiveFile = contextEditor.getPathToVdb().toFile();
        File tempDirFolder = contextEditor.getVdbContentsFolder();
        VirtualDatabase vdb = contextEditor.getVirtualDatabase();
        ConfigurationModelContainer configCntr = null;
        byte[] vdbFileContents = null;
        File archiveSoftRef = null;
        boolean failIfNoDef = false;
        defn = createVDBDefnFromContextEditor(vdbArchiveFile,
                                              tempDirFolder,
                                              vdb,
                                              configCntr,
                                              vdbFileContents,
                                              archiveSoftRef,
                                              failIfNoDef);

        return defn;
    }

    public static VDBDefn loadVDBDefn( final VdbEditingContextImpl contextEditor,
                                       final boolean closeEditor ) throws Exception {

        VDBReader factory = new VDBReader();

        try {
            VDBDefn defn = factory.createVDBDefnFromEditor(contextEditor);

            return defn;

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            factory.cleanup(closeEditor);
        }

    }

    public static VDBDefn loadVDBDefn( final VdbContext contextEditor,
                                       final boolean closeEditor ) throws Exception {

        VDBDefn defn = null;
        if (!contextEditor.isOpen()) {
            VDBReader factory = new VDBReader();

            try {
                defn = factory.createVDBDefnFromEditor(contextEditor);
                return defn;

            } catch (MetaMatrixCoreException mmc) {
                throw mmc;

            } catch (Exception e) {
                throw new MetaMatrixRuntimeException(e);
            } finally {
                factory.cleanup(closeEditor);
            }
        }

        File vdbArchiveFile = contextEditor.getVdbFile();
        File tempDirFolder = new File(contextEditor.getTempDirectory().getPath());
        VirtualDatabase vdb = contextEditor.getVirtualDatabase();
        ConfigurationModelContainer configCntr = null;
        byte[] vdbFileContents = null;
        File archiveSoftRef = null;
        boolean failIfNoDef = false;
        defn = createVDBDefnFromContextEditor(vdbArchiveFile,
                                              tempDirFolder,
                                              vdb,
                                              configCntr,
                                              vdbFileContents,
                                              archiveSoftRef,
                                              failIfNoDef);

        return defn;
    }

    /**
     * Call to creat a VDBDefn from the contextEditor. Note: The VDBDefn will not contain the vdbarchive.
     * 
     * @param contextEditor
     * @return VDBDefn without the vdbarchive
     * @throws Exception
     * @since 4.2
     */
    public static VDBDefn loadVDBDefn( VdbEditingContextImpl contextEditor,
                                       boolean closeEditor,
                                       ConfigurationModelContainer configModelContainer ) throws Exception {

        VDBReader factory = new VDBReader(configModelContainer);

        try {
            VDBDefn defn = factory.createVDBDefnFromEditor(contextEditor);

            return defn;

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            factory.cleanup(closeEditor);
        }

    }

    public static VDBDefn loadVDBDefn( VdbContextEditor contextEditor,
                                       boolean closeEditor,
                                       ConfigurationModelContainer configModelContainer ) throws Exception {

        VDBReader factory = new VDBReader(configModelContainer);

        try {
            VDBDefn defn = factory.createVDBDefnFromEditor(contextEditor);

            return defn;

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        } finally {
            factory.cleanup(closeEditor);
        }

    }

    public static VDBDefn loadVDBDefn( final VdbContext context,
                                       final boolean closeEditor,
                                       final ConfigurationModelContainer configModelContainer ) throws Exception {

        VDBDefn defn = null;
        File vdbArchiveFile = context.getVdbFile();
        File tempDirFolder = new File(context.getTempDirectory().getPath());
        VirtualDatabase vdb = context.getVirtualDatabase();
        ConfigurationModelContainer configCntr = configModelContainer;
        byte[] vdbFileContents = null;
        File archiveSoftRef = null;
        boolean failIfNoDef = false;
        defn = createVDBDefnFromContextEditor(vdbArchiveFile,
                                              tempDirFolder,
                                              vdb,
                                              configCntr,
                                              vdbFileContents,
                                              archiveSoftRef,
                                              failIfNoDef);

        return defn;

    }

    /**
     * Create a {@link VDBDefn VDBDefn} from the specified xml formated <code>fileName</code>. If the <code>dir</code> is not
     * specified, it is expected that the <code>fileName</code> will be an absolute path or can be found in the classpath. This
     * method will create a VDBDefn from either a .VDB or .DEF file.
     * 
     * @param fileName
     * @param directory is optional and nullable
     * @param archiveSoftRef is optional and nullable, when specified will cause the .VDB file not to loaded into memory until
     *        requested from the VDBDefn.
     * @return VDBDefn
     * @throws Exception
     * @since 4.2
     */
    public static VDBDefn loadVDBDefn( String fileName,
                                       String directory,
                                       File softRefLocation ) throws Exception {
        return loadVdbDefn(fileName, directory, true, softRefLocation);
    }

    /**
     * This method will do the following - create the VDBDefn based on the vdbArchive. This is the base VDBDefn - Then, o if
     * transferBindingsFromDefn is true, take the vdbDefn types and connector bindings, and overwrite the type and binding defn in
     * the baseDefn. o if transferBindingsFromDefn is false, just use the baseDefn types and bindings. Usecases 1) vdbDefn is
     * created from RMCVersionEntry. All type and binding info needs to be carried over from vdbArchive. 2) vdbDefn contains type
     * and binding edits. In this case, the type and binding info needs to be overlayed over the baseDefn info.
     * 
     * @param vdbDefn the supplied VDBDefn
     * @param vdbArchive the supplied vdb archive
     * @param transferBindingsFromDefn flag determining whether to overlay the vdbDefn type and binding info over the vdb archive
     *        info. If 'true', the vdbDefn binding info is used. If 'false', the vdbArchive binding info is used.
     * @return the modified resultant VDBDefn
     * @throws Exception
     * @since 4.3
     */
    private VDBDefn createVDBDefnFromContextEditor( VDBDefn vdbDefn,
                                                    byte[] vdbArchive,
                                                    boolean transferBindingsFromDefn ) throws Exception {

        // first create the context editor, then process based on it
        String fileName = vdbDefn.getFileName();
        if (fileName == null) {
            fileName = vdbDefn.getName();
        }

        // ------------------------------------------------------------------
        // Create the baseDefn from the supplied vdbArchive
        // ------------------------------------------------------------------
        createVDBContextEditor(fileName, vdbArchive);

        BasicVDBDefn basedefn = (BasicVDBDefn)createVDBDefnFromContextEditor(vdbDefn.getName(), vdbArchive, null, false);

        // ------------------------------------------------------------------
        // Replace baseDefn info from the supplied vdbDefn info
        // ------------------------------------------------------------------
        if (vdbDefn.getVersion() != null) {
            basedefn.setVersion(vdbDefn.getVersion());
        }

        if (vdbDefn.getDateCreated() != null) {
            basedefn.setDateCreated(vdbDefn.getDateCreated());
        }

        if (vdbDefn.getCreatedBy() != null) {
            basedefn.setCreatedBy(vdbDefn.getCreatedBy());
        }

        if (vdbDefn.getDescription() != null) {
            basedefn.setDescription(vdbDefn.getDescription());
        }

        // ----------------------------------------------------------------------------
        // If desired, replace the binding info with the supplied vdbDefn binding info
        // ----------------------------------------------------------------------------
        if (transferBindingsFromDefn) {
            // ---------------------------------------------
            // Add additional connector types from vdbDefn.
            // Save existing connector types from baseDefn
            // ---------------------------------------------
            for (Iterator types = vdbDefn.getConnectorTypes().values().iterator(); types.hasNext();) {
                ConnectorBindingType t = (ConnectorBindingType)types.next();
                basedefn.addConnectorType(t);
            }

            // --------------------------------------------------
            // Remove all connector bindings from the baseDefn.
            // --------------------------------------------------
            Map allBindings = basedefn.getConnectorBindings();
            Iterator bindingNameIter = allBindings.keySet().iterator();
            while (bindingNameIter.hasNext()) {
                String name = (String)bindingNameIter.next();
                basedefn.removeConnectorBindingNameOnly(name);
            }

            // ---------------------------------------------------------
            // Transfer modelInfo and bindings from vdbDefn to baseDefn
            // ---------------------------------------------------------
            for (Iterator mit = vdbDefn.getModels().iterator(); mit.hasNext();) {
                ModelInfo mi = (ModelInfo)mit.next();
                BasicModelInfo miu = (BasicModelInfo)basedefn.getModel(mi.getName());
                // the visibility comes from the souce, in this case its the console deployment state
                if (miu != null) {
                    miu.setIsVisible(mi.isVisible());
                    miu.enableMutliSourceBindings(mi.isMultiSourceBindingEnabled());
                    List bindingNames = mi.getConnectorBindingNames();
                    if (bindingNames != null) {
                        for (Iterator bit = bindingNames.iterator(); bit.hasNext();) {
                            String bname = (String)bit.next();
                            ConnectorBinding cb = vdbDefn.getConnectorBindingByName(bname);
                            if (cb != null) {
                                basedefn.addConnectorBinding(miu.getName(), cb);
                            }
                        }
                    }
                }
            }
        }

        return basedefn;

    }

    /**
     * vdbDefFile should be the full path to the .DEF file
     * 
     * @param vdbDefFile is the .DEF file to load
     * @return
     * @throws Exception
     */
    private VDBDefn createVDBDefn( File vdbDefFile,
                                   File archiveSoftRef ) throws Exception {
        VDBDefn defn = null;

        if (vdbDefFile == null) {
            Assertion.isNotNull(vdbDefFile, VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_arg_null"));//$NON-NLS-1$                
        }

        if (!vdbDefFile.exists()) {
            throw new MetaMatrixRuntimeException(VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_doesnt_exist", vdbDefFile));//$NON-NLS-1$
        }

        if (!vdbDefFile.canRead()) {
            throw new MetaMatrixRuntimeException(VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_not_readable", vdbDefFile));//$NON-NLS-1$
        }

        try {

            VDBDefnXMLHelper helper = createVDBXMLHelper();

            Element root = helper.getRoot(vdbDefFile);

            // create a temp defn so that the editor can be created
            VDBDefn defntemp = helper.createVDBDefn(root, null);

            // use the path of where the parent .DEF file came from and
            // the name of the defn defined in the .DEF file
            File archiveFile = new File(vdbDefFile.getParent(), defntemp.getFileName());
            createVDBContextEditor(archiveFile);

            defn = helper.createVDBDefn(this.editor.getVirtualDatabase(), archiveSoftRef);
            helper.addVDBDefnInfo(defn, root);

            // defn = helper.createVDBDefn(root, archiveSoftRef);

            // if the softref is passed, then do not set the
            // byte array so that is kept in memory
            if (archiveSoftRef == null) {
                ((BasicVDBDefn)defn).setVDBStream(new VDBStreamImpl(archiveFile));
            }

            loadFromContextEditor(this.editor.getVdbFile(), this.editor.getVirtualDatabase(), defn);

            defn = loadAddtionalVDBModelInfo(defn, root, this.configContainer);

            // Set data roles if available
            File dataRolesFile = getDataRolesFile();
            if (dataRolesFile != null && dataRolesFile.exists()) {
                ((BasicVDBDefn)defn).setDataRoles(FileUtil.read(new FileReader(dataRolesFile)).toCharArray());
            }

            determineVDBStatus(defn);

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        }

        return defn;
    }

    private static boolean isVDBArchive( String fileName ) {
        if (fileName.toLowerCase().endsWith(VDB_READER_EXTENSIONS[1])) {
            return true;
        }
        return false;
    }

    private static boolean isVDBDef( String fileName ) {
        if (fileName.toLowerCase().endsWith(VDB_READER_EXTENSIONS[0])) {
            return true;
        }
        return false;
    }

    /**
     * @param defFile
     * @return
     * @since 4.3
     */
    private VDBDefn createVDBDefnFromDEF( String name,
                                          byte[] vdbFile,
                                          char[] defFile,
                                          File archiveSoftRef ) throws Exception {
        VDBDefn defn = null;

        if (defFile == null) {
            Assertion.isNotNull(defFile, "VDB Defn File Name must be specified"); //$NON-NLS-1$
        }
        try {
            VDBDefnXMLHelper helper = createVDBXMLHelper();

            Element root = helper.getRoot(defFile);

            // create a temp defn so that the editor can be created
            VDBDefn defntemp = helper.createVDBDefn(root, null);

            String fileName = defntemp.getFileName();
            if (fileName == null) {
                fileName = name;
            }
            createVDBContextEditor(fileName, vdbFile);

            defn = helper.createVDBDefn(this.editor.getVirtualDatabase(), archiveSoftRef);

            helper.addVDBDefnInfo(defn, root);

            // defn = helper.createVDBDefn(root, archiveSoftRef);

            // if the softref is passed, then do not set the
            // byte array so that is kept in memory
            if (archiveSoftRef == null) {
                ((BasicVDBDefn)defn).setVDBStream(new VDBStreamImpl(vdbFile));
            }

            loadFromContextEditor(this.editor.getVdbFile(), this.editor.getVirtualDatabase(), defn);

            defn = loadAddtionalVDBModelInfo(defn, root, this.configContainer);

            // Set data roles if available
            File dataRolesFile = getDataRolesFile();
            if (dataRolesFile != null && dataRolesFile.exists()) {
                ((BasicVDBDefn)defn).setDataRoles(FileUtil.read(new FileReader(dataRolesFile)).toCharArray());
            }

            if (name != null) {
                ((BasicVDBDefn)defn).setName(name);
            }
            determineVDBStatus(defn);

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        }

        return defn;
    }

    private VDBDefn createVDBDefnFromEditor( VdbEditingContextImpl contextEditor ) throws Exception {
        VDBDefn defn = null;
        File vdbFile = contextEditor.getPathToVdb().toFile();
        this.editor = createVDBContextEditor(vdbFile);
        if (editor instanceof VdbContextImpl && contextEditor.getVirtualDatabase() != null) {
            ((VdbContextImpl)editor).setVirtualDatabase(contextEditor.getVirtualDatabase());
        }

        defn = createVDBDefnFromContextEditor(this.editor.getVirtualDatabase().getName(), null, null, false);
        return defn;
    }

    private VDBDefn createVDBDefnFromEditor( VdbContext contextEditor ) throws Exception {
        VDBDefn defn = null;
        File vdbFile = contextEditor.getVdbFile();
        this.editor = createVDBContextEditor(vdbFile);

        defn = createVDBDefnFromContextEditor(this.editor.getVirtualDatabase().getName(), null, null, false);
        return defn;
    }

    /**
     * This method will build the VDBDefn from the VDBContextEditor, not the .DEF file. Assumption here is that
     * {@link #createVDBContextEditor} has been called prior to calling this method. The vdbFile is not used for loading, only
     * stored on the VDBDefn according to the archiveSoftRef.
     * 
     * @parm vdbFile is the .VDB in byte array form, is optional and nullable
     * @parm achiveSoftRef is the file location to be used as the softreference to the .VDB file. when vdbFile is null
     */
    private static VDBDefn createVDBDefnFromContextEditor( final File vdbArchiveFile,
                                                           final File tempDirFolder,
                                                           final VirtualDatabase vdb,
                                                           final ConfigurationModelContainer configCntr,
                                                           final byte[] vdbFileContents,
                                                           final File archiveSoftRef,
                                                           final boolean failIfNoDef ) throws Exception {

        VDBDefn defn = null;
        File vdbDefnFile = getIncludedFile(tempDirFolder, vdbArchiveFile, VdbConstants.DEF_FILE_NAME);

        // perform these checks if the .DEF is required
        if (failIfNoDef) {
            if (vdbDefnFile == null || vdbDefnFile.length() == 0) {
                throw new MetaMatrixRuntimeException(
                                                     VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_not_in_vdbarchive", vdb.getName()));//$NON-NLS-1$
            }

            if (!vdbDefnFile.exists()) {
                throw new MetaMatrixRuntimeException(VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_not_found"));//$NON-NLS-1$

            }
        }

        try {
            VDBDefnXMLHelper helper = createVDBXMLHelper();
            defn = helper.createVDBDefn(vdb, archiveSoftRef);

            Element root = null;
            if (vdbDefnFile != null) {
                byte[] vdbDefn = ByteArrayHelper.toByteArray(vdbDefnFile);
                String vdbString = new String(vdbDefn);
                root = helper.getRoot(vdbString.toCharArray());
                helper.addVDBDefnInfo(defn, root);
            }

            // helper.createVDBDefn(root, archiveSoftRef);

            // if the softref is passed, then do not set the
            // byte array so that is kept in memory
            if (archiveSoftRef == null && vdbFileContents != null) {
                ((BasicVDBDefn)defn).setVDBStream(new VDBStreamImpl(vdbFileContents));

            }

            loadFromContextEditor(vdbArchiveFile, vdb, defn);

            if (vdbDefnFile != null) {
                defn = loadAddtionalVDBModelInfo(defn, root, configCntr);
            }

            if (vdb.getName() != null) {
                ((BasicVDBDefn)defn).setName(vdb.getName());
            }

            determineVDBStatus(defn);

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        }

        return defn;
    }

    private VDBDefn createVDBDefnFromContextEditor( String name,
                                                    byte[] vdbFile,
                                                    File archiveSoftRef,
                                                    boolean failIfNoDef ) throws Exception {

        VDBDefn defn = null;
        File vdbDefnFile = getVDBDefnFile();

        // perform these checks if the .DEF is required
        if (failIfNoDef) {
            if (vdbDefnFile == null || vdbDefnFile.length() == 0) {
                throw new MetaMatrixRuntimeException(
                                                     VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_not_in_vdbarchive", name));//$NON-NLS-1$
            }

            if (!vdbDefnFile.exists()) {
                throw new MetaMatrixRuntimeException(VdbEditPlugin.Util.getString("VDBReader.VDBDefn_file_not_found"));//$NON-NLS-1$

            }
        }

        try {
            VDBDefnXMLHelper helper = createVDBXMLHelper();
            defn = helper.createVDBDefn(this.editor.getVirtualDatabase(), archiveSoftRef);

            Element root = null;
            if (vdbDefnFile != null) {
                byte[] vdbDefn = ByteArrayHelper.toByteArray(vdbDefnFile);
                String vdbString = new String(vdbDefn);
                root = helper.getRoot(vdbString.toCharArray());
                helper.addVDBDefnInfo(defn, root);
            }

            // helper.createVDBDefn(root, archiveSoftRef);

            // if the softref is passed, then do not set the
            // byte array so that is kept in memory
            if (archiveSoftRef == null && vdbFile != null) {
                ((BasicVDBDefn)defn).setVDBStream(new VDBStreamImpl(vdbFile));
            }

            loadFromContextEditor(this.editor.getVdbFile(), this.editor.getVirtualDatabase(), defn);

            if (vdbDefnFile != null) {
                defn = loadAddtionalVDBModelInfo(defn, root, this.configContainer);
            }

            // Set data roles if available
            File dataRolesFile = getDataRolesFile();
            if (dataRolesFile != null && dataRolesFile.exists()) {
                ((BasicVDBDefn)defn).setDataRoles(FileUtil.read(new FileReader(dataRolesFile)).toCharArray());
            }

            if (name != null) {
                ((BasicVDBDefn)defn).setName(name);
            }

            determineVDBStatus(defn);

        } catch (MetaMatrixCoreException mmc) {
            throw mmc;

        } catch (Exception e) {
            throw new MetaMatrixRuntimeException(e);
        }

        return defn;
    }

    private static VDBDefnXMLHelper createVDBXMLHelper() {

        return new VDBDefnXMLHelper();

    }

    /**
     * Call to load the VDBDefn based on the xml formatted vdb defn file that exists at <code>fileName</code>
     * 
     * @param vdbDefn
     * @param fileName
     * @return
     * @throws VdbProcessingException
     * @throws MetaMatrixComponentException
     * @since 4.2
     */
    private static void loadFromContextEditor( final File vdbFile,
                                               final VirtualDatabase vdb,
                                               final VDBDefn vdbDefn ) throws MetaMatrixComponentException {

        Collection collection = null;
        boolean isIncomplete = false;
        boolean isError = false;
        try {

            BasicVDBDefn defn = (BasicVDBDefn)vdbDefn;
            // com.metamatrix.vdb.edit.manifest.VirtualDatabase vdb = editor.getVirtualDatabase();
            // if (defn.getName() == null) {
            // defn.setName(vdb.getName());
            // }
            // defn.setUUID(vdb.getUuid());
            // String desc = vdb.getDescription();
            // if (desc != null && desc.trim().length() > 0) {
            //                   defn.setDescription( vdb.getDescription());//$NON-NLS-1$                
            // }

            // defn.setCreatedBy(vdb.getTimeLastChanged());
            defn.setHasWSDLDefined(hasWsdl(vdbFile));

            Severity severity = vdb.getSeverity();
            if (severity.getValue() == Severity.ERROR) {
                defn.setModelInfos(new ArrayList(1));
                defn.setStatus(VDBStatus.INCOMPLETE);
                String msg = VdbEditPlugin.Util.getString("VDBReader.VDB_is_at_a_nondeployable_severity_state", new Object[] {vdb.getName(), severity.getName()}); //$NON-NLS-1$
                defn.setVDBValidityError(isIncomplete, msg);
                isIncomplete = true;
                isError = true;
                // VAH 7/28/05 changed not to throw the exception, but to set the state to incomplete

                //                  throw new VdbProcessingException(VdbProcessingException.VDB_NON_DEPLOYABLE_STATE, VdbEditPlugin.Util.getString("VDBReader.VDB_is_at_a_nondeployable_severity_state", new Object[] {editor.getVirtualDatabase().getName(), severity.getName()} ));  //$NON-NLS-1$

            }

            Collection modelRef = vdb.getModels();

            if (modelRef == null || modelRef.isEmpty()) {
                defn.setModelInfos(new ArrayList(1));
                defn.setStatus(VDBStatus.INCOMPLETE);
                String msg = VdbEditPlugin.Util.getString("VDBReader.No_models_in_vdb_1", vdbDefn.getFileName()); //$NON-NLS-1$
                defn.setVDBValidityError(isIncomplete, msg);
                isIncomplete = true;
                return;

                // VAH 7/28/05 changed not to throw the exception, but to set the state to incomplete
                //                  throw new VdbProcessingException(VdbProcessingException.NO_MODELS, VdbEditPlugin.Util.getString("VDBReader.No_models_in_vdb_1", vdbDefn.getFileName()));  //$NON-NLS-1$

            }

            Iterator iter = modelRef.iterator();
            collection = new ArrayList(modelRef.size());

            Severity modseverity = null;

            while (iter.hasNext()) {
                ModelReference model = (ModelReference)iter.next();
                modseverity = model.getSeverity();
                if (modseverity.getValue() == Severity.ERROR) {
                    defn.setStatus(VDBStatus.INCOMPLETE);
                    String msg = VdbEditPlugin.Util.getString("VDBReader.Model_is_at_a_nondeployable_severity_state", new Object[] {model.getName(), modseverity.getName()}); //$NON-NLS-1$
                    defn.setVDBValidityError(isIncomplete, msg);
                    isIncomplete = true;
                    // VAH 7/28/05 changed not to throw the exception, but to set the state to incomplete

                    //                       throw new VdbProcessingException(VdbProcessingException.MODEL_NON_DEPLOYABLE_STATE, VdbEditPlugin.Util.getString("VDBReader.Model_is_at_a_nondeployable_severity_state", new Object[] {model.getName(), modseverity.getName()} ));  //$NON-NLS-1$

                }

                int modelType = model.getModelType().getValue();
                switch (modelType) {
                    case ModelType.PHYSICAL:
                    case ModelType.VIRTUAL:
                    case ModelType.MATERIALIZATION:
                        ModelInfo modelEntry = createModelInfo(model);

                        // if the model uri is null, do not add as a valid model
                        if (modelEntry.getModelURI() != null) {

                            if (modelType == ModelType.MATERIALIZATION) {
                                // Fill in DDL files
                                ZipFile vdbArchive = getVdbArchive(vdbFile);
                                if (vdbArchive != null) {
                                    Map ddlFileNamesToFiles = new HashMap();
                                    for (Enumeration e = vdbArchive.entries(); e.hasMoreElements();) {
                                        ZipEntry entry = (ZipEntry)e.nextElement();
                                        if (entry != null) {
                                            String pathInVdb = entry.getName();
                                            String fileName = new Path(pathInVdb).lastSegment();
                                            if (isVdbMaterializationDDLFile(fileName)) {
                                                InputStream fileStream = vdbArchive.getInputStream(entry);
                                                ddlFileNamesToFiles.put(pathInVdb, ByteArrayHelper.toByteArray(fileStream, 1024));
                                                fileStream.close();
                                            }
                                        }
                                    }
                                    modelEntry.setDDLFiles(ddlFileNamesToFiles);
                                }
                            }

                            collection.add(modelEntry);

                        }

                        break;
                }

            }

            defn.setModelInfos(collection);
            defn.setVDBValidityError(isIncomplete);
            // if the VDB has had an incomplete status set on it due to
            // the serverity of the VDB or model, then do not
            // reset the status by calling determineVDBStatus()
            // because it doesnt know how to determine the severity
            if (!isError) {
                determineVDBStatus(defn);
            }

            // set the file visibility information
            ZipFile vdbArchive = getVdbArchive(vdbFile);
            if (vdbArchive != null) {
                for (Enumeration e = vdbArchive.entries(); e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry)e.nextElement();
                    defn.setVisibility(entry.getName(), isVisible(vdb, entry));
                }
            }
            return;

            // } catch (VdbProcessingException e) {
            // throw e;
        } catch (Exception e) {
            throw new MetaMatrixComponentException(e);
        }

    }

    public static ModelInfo createModelInfo( ModelReference modelRef ) {
        return new BasicVDBModelDefn(modelRef);
    }

    /**
     * The isComplete indicates whether
     * 
     * @param defn
     * @param root
     * @param isComplete
     * @return
     * @throws Exception
     * @since 4.2
     */
    private static VDBDefn loadAddtionalVDBModelInfo( VDBDefn defn,
                                                      Element root,
                                                      ConfigurationModelContainer configCntr ) throws Exception {
        VDBDefnXMLHelper helper = createVDBXMLHelper();
        defn = helper.addModelInfo(root, defn, configCntr);

        return defn;

    }

    // this was made package level scope for unit testing purposes

    VdbContext createVDBContextEditor( String fileName,
                                       byte[] vdbFile ) throws MetaMatrixComponentException {

        if (fileName == null) {
            fileName = TEMP_VDB_NAME;
        } else if (!isVDBArchive(fileName)) {
            fileName = fileName + ".vdb";//$NON-NLS-1$  
        }

        this.tempVDBFiles = null;
        try {
            // must write the vdbFile to the filesystem because
            // the vdbcontexteditor only takes a filename, not an
            // inputstream or other type.
            this.tempVDBFiles = new TempDirectory(System.currentTimeMillis(), random.nextLong());
            this.tempVDBFiles.create();

            File f = new File(this.tempVDBFiles.getPath(), fileName);
            // must first write the file to the local file system
            // in order for the vdb editor to read it
            // It didnt take an inputstream or byte array as an argument

            new FileUtil(f.getAbsolutePath()).writeBytes(vdbFile);
            return createVDBContextEditor(f);

        } catch (MetaMatrixComponentException e) {
            throw e;
        } catch (Exception e) {
            throw new MetaMatrixComponentException(e);
        }
    }

    private VdbContext createVDBContextEditor( File vdbArchive ) throws MetaMatrixComponentException {
        try {
            File vdbWorkingFolder = null;
            if (VdbEditPlugin.getInstance() != null) {
                vdbWorkingFolder = VdbEditPlugin.getVdbWorkingDirectory();
            } else {
                vdbWorkingFolder = new File(FileUtils.TEMP_DIRECTORY);
            }
            if (!vdbWorkingFolder.exists()) {
                vdbWorkingFolder.mkdir();
            }

            this.editor = new VdbContextImpl(vdbArchive, vdbWorkingFolder);

            this.editor.open(new NullProgressMonitor());

        } catch (IOException e) {
            throw new MetaMatrixComponentException(e);
        }
        return this.editor;
    }

    private static void determineVDBStatus( VDBDefn defn ) {
        BasicVDBDefn bvdbdefn = (BasicVDBDefn)defn;
        bvdbdefn.determineVdbsStatus();

    }

    private File getVDBDefnFile() {
        File tempDirFolder = new File(this.editor.getTempDirectory().getPath());
        File vdbFile = this.editor.getVdbFile();
        return getIncludedFile(tempDirFolder, vdbFile, VdbConstants.DEF_FILE_NAME);
    }

    private File getDataRolesFile() {
        File tempDirFolder = new File(this.editor.getTempDirectory().getPath());
        File vdbFile = this.editor.getVdbFile();
        return getIncludedFile(tempDirFolder, vdbFile, VdbConstants.DATA_ROLES_FILE);
    }

    private static File getIncludedFile( final File tempDirFolder,
                                         final File vdbFile,
                                         final String fileName ) {
        File includedFile = new File(tempDirFolder, fileName);

        if (includedFile.length() > 0 && includedFile.exists()) {
            return includedFile;
        }

        // If the .DEF file does not already exists in the temp directory then get from the VDB
        includedFile = null;
        if (vdbFile.exists() && vdbFile.length() > 0) {
            InputStream is = null;
            try {
                ZipFile archive = new ZipFile(vdbFile);
                Enumeration entries = archive.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry)entries.nextElement();

                    if (entry != null && entry.getName().equalsIgnoreCase(fileName)) {
                        is = archive.getInputStream(entry);
                        includedFile = new File(tempDirFolder, fileName);
                        FileUtils.write(is, includedFile);
                        break;
                    }
                }
            } catch (Exception e) {
                includedFile = null;
                VdbEditPlugin.Util.log(IStatus.ERROR, e.getLocalizedMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return includedFile;
    }

    private boolean isValidDefnFile() {
        final File f = getVDBDefnFile();
        if (f == null) {
            return false;
        }
        VDBDefnXMLHelper helper = createVDBXMLHelper();

        try {
            Element root = helper.getRoot(f);

            return helper.containsModelInfo(root);
        } catch (Exception e) {
            return false;
        }
    }

    private void cleanup( final boolean closeEditor ) throws Exception {
        cleanup(this.editor, this.tempVDBFiles, closeEditor);
    }

    private static void cleanup( final VdbContext context,
                                 final TempDirectory tempDir,
                                 final boolean closeEditor ) throws Exception {
        Exception error = null;
        try {
            if (closeEditor && context != null) {
                context.close(new NullProgressMonitor());
                context.dispose();
            }
        } catch (Exception e) {
            error = e;
        }

        try {
            if (tempDir != null) {
                tempDir.remove();
            }

        } catch (Throwable t) {
            // do nothing
        }

        if (error != null) {
            throw error;
        }
    }

    private static ZipFile getVdbArchive( final File vdbFile ) throws ZipException, IOException {
        ArgCheck.isNotNull(vdbFile);
        if (vdbFile != null && vdbFile.exists() && vdbFile.length() > 0) {
            return new ZipFile(vdbFile);
        }
        return null;
    }

    private static boolean hasWsdl( final File vdbFile ) throws ZipException, IOException {
        ArgCheck.isNotNull(vdbFile);

        boolean hasWsdl = false;
        ZipFile archive = getVdbArchive(vdbFile);
        if (archive != null) {
            ZipEntry entry = archive.getEntry(WSDLServletUtil.GENERATED_WSDL_FILENAME);
            if (entry != null) {
                hasWsdl = true;
            }
        }
        return hasWsdl;
    }

    private static boolean isVdbMaterializationDDLFile( final String fileName ) {
        ArgCheck.isNotNull(fileName);
        // ddl suffix
        if (ScriptType.isDDLScript(fileName)) {
            // swap, truncate, load, materialization model contained in file name
            if ((ScriptType.isMaterializationScript(fileName)) || (ScriptType.isCreateScript(fileName))
                || (ScriptType.isSwapScript(fileName)) || (ScriptType.isTruncateScript(fileName))
                || (ScriptType.isLoadScript(fileName))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVisible( final VirtualDatabase vdb,
                                      final ZipEntry entry ) {
        ArgCheck.isNotNull(vdb);
        ArgCheck.isNotNull(entry);
        String pathInArchive = entry.getName();

        ModelReference ref = getModelReference(vdb, pathInArchive);
        if (ref != null) {
            return ref.isVisible();
        }

        String fileName = new Path(pathInArchive).lastSegment();
        if (fileName != null) {
            // index files should not be visible
            if (IndexUtil.isIndexFile(fileName)) {
                return false;
            }
            // manifest file should not be visible
            if (fileName.equalsIgnoreCase(VdbConstants.MANIFEST_MODEL_NAME)) {
                return false;
            }
            // materialization models should not be visible
            if (StringUtil.startsWithIgnoreCase(fileName, VdbConstants.MATERIALIZATION_MODEL_NAME)
                && StringUtil.endsWithIgnoreCase(fileName, VdbConstants.MATERIALIZATION_MODEL_FILE_SUFFIX)) {
                return false;
            }
            // ddl files for materialization should not be visible
            if (isVdbMaterializationDDLFile(fileName)) {
                return false;
            }
            // wldl file should be visible
            if (fileName.equalsIgnoreCase(WSDLServletUtil.GENERATED_WSDL_FILENAME)) {
                return true;
            }
            // any other file should be visible
            return true;
        }
        return false;
    }

    private static ModelReference getModelReference( final VirtualDatabase vdb,
                                                     final String pathInArchive ) {
        ArgCheck.isNotNull(vdb);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);

        final IPath pathToMatch = createNormalizedPath(pathInArchive);
        for (Iterator i = vdb.getModels().iterator(); i.hasNext();) {
            final ModelReference ref = (ModelReference)i.next();
            final IPath refPath = createNormalizedPath(ref.getModelLocation());
            if (pathToMatch.equals(refPath)) {
                return ref;
            }
        }
        final IPath upperPathToMatch = createNormalizedPath(pathInArchive.toUpperCase());
        for (Iterator i = vdb.getModels().iterator(); i.hasNext();) {
            final ModelReference ref = (ModelReference)i.next();
            final IPath upperRefPath = createNormalizedPath(ref.getModelLocation().toUpperCase());
            if (upperPathToMatch.equals(upperRefPath)) {
                return ref;
            }
        }
        return null;
    }

    private static IPath createNormalizedPath( final String pathInArchive ) {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        final IPath path = new Path(pathInArchive);
        return (path.segmentCount() == 1 ? path.removeTrailingSeparator().makeRelative() : path.makeAbsolute());
    }

    // public static void main(String args[]) {
    //
    // Properties props = new Properties();
    //        String dir = "E:\\Plugins\\current\\plugins\\com.metamatrix.server\\testdata\\config"; //$NON-NLS-1$
    //        String fileName = "PartsMetadata.DEF"; //$NON-NLS-1$
    //         
    // props.setProperty(VDB_FILE_ARG, fileName);
    // props.setProperty(IMPORT_DIR_ARG, dir);
    //
    // try {
    // BasicVDBDefn vdbDefn = (BasicVDBDefn) VDBImport.loadVDBDefn(fileName, dir);
    //            
    //            String vdbName = vdbDefn.getName() + "_" + new Date().toString(); //$NON-NLS-1$
    // vdbDefn.setName(vdbName);
    //            VDBImport.importVDBDefn(vdbDefn, "VDBImportTest"); //$NON-NLS-1$
    // System.out.println(vdbDefn);
    // } catch (Exception e) {
    // }
    //
    // System.exit(0);
    //
    //
    // }

}
