/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the dynamic vdb
 */
public class GenerateDynamicVdbManager implements UiConstants, StringConstants {

    private IFile archiveVdbFile;

    private XmiVdb archiveVdb;

    private String dynamicVdbName;
    private String dynamicVdbFileName;
    private String version = Integer.toString(1);

    private DynamicVdb dynamicVdb;

    private IContainer outputLocation;

    private IStatus status = Status.OK_STATUS;

    private static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                                     StringNameValidator.DEFAULT_MAXIMUM_LENGTH,
                                                                                     new char[] {'_', '-', '.'});

    /**
     * @param archiveVdbFile
     * @throws Exception
     */
    public GenerateDynamicVdbManager(IFile archiveVdbFile) throws Exception {
        super();
        CoreArgCheck.isNotNull(archiveVdbFile);

        this.archiveVdbFile = archiveVdbFile;
        this.archiveVdb = new XmiVdb(archiveVdbFile);

        dynamicVdbName = this.archiveVdb.getName();
        dynamicVdbFileName = this.archiveVdb.getName() + ITeiidVdb.DYNAMIC_VDB_SUFFIX;
        outputLocation = this.archiveVdbFile.getParent();
    }

    /**
     * @return dynamic vdb name
     */
    public String getDynamicVdbName() {
        return dynamicVdbName;
    }

    /**
     * @param dynamicVdbName
     */
    public void setDynamicVdbName(String dynamicVdbName) {
        this.dynamicVdbName = dynamicVdbName;
    }

    /**
     * @return dynamic vdb file name
     */
    public String getDynamicVdbFileName() {
        return dynamicVdbFileName;
    }

    /**
     * @param dynamicVdbFileName
     */
    public void setDynamicVdbFileName(String dynamicVdbFileName) {
        this.dynamicVdbFileName = dynamicVdbFileName;
    }

    /**
     * @return dynamic vdb
     */
    public DynamicVdb getDynamicVdb() {
        return dynamicVdb;
    }

    /**
     * @return xmi vdb
     */
    public XmiVdb getArchiveVdb() {
        return archiveVdb;
    }

    /**
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return output location
     */
    public IContainer getOutputLocation() {
        return outputLocation;
    }

    /**
     * @param outputLocation
     */
    public void setOutputLocation(IContainer outputLocation) {
        this.outputLocation = outputLocation;
    }

    private IFile getDestination() {
        IFile destination = outputLocation.getFile(new Path(dynamicVdbFileName));
        return destination;
    }

    /**
     * @return the xml string representation of the dynamic vdb
     * @throws Exception if error occurs
     */
    public String getOutputXml() throws Exception {
        if (isGenerateRequired())
            throw new Exception(Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated);

        StringWriter writer = new StringWriter();

        //
        // Writer will be closed by write method
        //
        dynamicVdb.write(writer);
        return writer.toString();
    }

    /**
     * Generate the dynamic xml from the xmi vdb
     * @throws Exception if error occurs
     */
    public void generate() throws Exception {
        if (!isGenerateRequired())
            return;

        if (archiveVdb == null)
            return;

        //
        // This will convert the xmi vdb and build the dynamic vdb
        //
        dynamicVdb = archiveVdb.convert(DynamicVdb.class);
    }

    /**
     * @param directory absolute path of a directory to export the dynamic vdb
     * @throws Exception
     */
    public void export(String directory) throws Exception {
        if (directory == null)
            return;

        if (isGenerateRequired())
            throw new Exception(Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated);


        File export = new File(directory, getDynamicVdbFileName());
        if (export.exists())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbWizard_exportLocationAlreadyExists, 
                                                                   getDynamicVdbFileName(), directory));

        if (! export.createNewFile())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbWizard_exportLocationFailedToCreateFile, 
                                         getDynamicVdbFileName(), directory));

        FileWriter writer = new FileWriter(export);
        dynamicVdb.write(writer);
    }

    /**
     * @throws Exception
     */
    public void write(IProgressMonitor monitor) throws Exception {
        if (isGenerateRequired())
            throw new Exception(Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated);
        
        StringWriter writer = new StringWriter();
        dynamicVdb.write(writer);

        String xml = writer.toString();

        IFile destination = getDestination();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8")); //$NON-NLS-1$

        destination.create(inputStream, true, monitor);
    }

    /**
     * @return whether dynamic xml generation is required
     */
    public boolean isGenerateRequired() {
        return dynamicVdb == null;
    }

    /**
     * Validate the manager's settings
     */
    public void validate() {
        status = Status.OK_STATUS;

        // Check dynamic vdb name
        String proposedVdbName = getDynamicVdbName();
        String validationMessage = nameValidator.checkValidName(proposedVdbName);
        if (validationMessage != null) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, validationMessage);
            return;
        }

        validationMessage = ModelUtilities.vdbNameReservedValidation(proposedVdbName);
        if (validationMessage != null) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, validationMessage);
            return;
        }

        // Check Version # is an integer
        try {
            Integer.parseInt(getVersion());
        } catch (NumberFormatException nfe) {
            status = new Status(IStatus.ERROR, PLUGIN_ID,
                                NLS.bind(Messages.GenerateDynamicVdbWizard_validation_versionNotInteger, getVersion()));
            return;
        }

        // dynamic vdb file name
        // can't be null && must end with -vdb.xml
        if (dynamicVdbFileName == null) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_vdbFileNameUndefined);
            return;
        }

        if (!dynamicVdbFileName.toLowerCase().endsWith(ITeiidVdb.DYNAMIC_VDB_SUFFIX)) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_vdbMissingXmlExtension);
            return;
        }

        // output location can't be null
        if (outputLocation == null) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_targetLocationUndefined);
            return;
        }

        if (!outputLocation.exists()) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_targetLocationNotExist);
            return;
        }

        IFile destination = getDestination();
        if (destination.exists()) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_targetFileAlreadyExists);
            return;
        }

        if (! isGenerateRequired() && dynamicVdb == null) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated);
            return;
        }
    }

    /**
     * @return status of manager
     */
    public IStatus getStatus() {
        return status;
    }
}
