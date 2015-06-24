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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the dynamic vdb
 */
public class GenerateDynamicVdbManager extends AbstractGenerateVdbManager implements UiConstants, StringConstants {

    private IFile archiveVdbFile;

    /**
     * @param archiveVdbFile
     * @throws Exception
     */
    public GenerateDynamicVdbManager(IFile archiveVdbFile) throws Exception {
        super();
        CoreArgCheck.isNotNull(archiveVdbFile);

        this.archiveVdbFile = archiveVdbFile;
        setArchiveVdb(new XmiVdb(archiveVdbFile));

        String vdbName = getArchiveVdb().getName();
        setOutputName(vdbName + ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        setOutputLocation(this.archiveVdbFile.getParent());
    }

    private void checkDynamicVdbGenerated() throws Exception {
        if (isGenerateRequired())
            throw new Exception(Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated);
    }

    /**
     * Generate the dynamic xml from the xmi vdb
     * @throws Exception if error occurs
     */
    public void generate() throws Exception {
        if (!isGenerateRequired())
            return;

        if (getArchiveVdb() == null)
            return;

        //
        // This will convert the xmi vdb and build the dynamic vdb
        //
        setDynamicVdb(getArchiveVdb().convert(DynamicVdb.class));
    }

    @Override
    public String getDynamicVdbXml() throws Exception {
        checkDynamicVdbGenerated();

        return super.getDynamicVdbXml();
    }

    /**
     * @param directory absolute path of a directory to export the dynamic vdb
     * @throws Exception
     */
    public void export(String directory) throws Exception {
        if (directory == null)
            return;

        checkDynamicVdbGenerated();


        File export = new File(directory, getOutputName());
        if (export.exists())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbWizard_exportLocationAlreadyExists, 
                                                                             getOutputName(), directory));

        if (! export.createNewFile())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbWizard_exportLocationFailedToCreateFile, 
                                         getOutputName(), directory));

        FileWriter writer = new FileWriter(export);
        getDynamicVdb().write(writer);
    }

    /**
     * @param monitor
     * @throws Exception
     */
    public void write(IProgressMonitor monitor) throws Exception {
        checkDynamicVdbGenerated();
        
        StringWriter writer = new StringWriter();
        getDynamicVdb().write(writer);

        String xml = writer.toString();

        IFile destination = getDestination();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8")); //$NON-NLS-1$

        destination.create(inputStream, true, monitor);
    }

    /**
     * @return whether dynamic xml generation is required
     */
    public boolean isGenerateRequired() {
        return getDynamicVdb() == null;
    }

    /**
     * Validate the manager's settings
     */
    public void validate() {
        setStatus(Status.OK_STATUS);

        // Check dynamic vdb name
        String proposedVdbName = getOutputName();
        String validationMessage = nameValidator.checkValidName(proposedVdbName);
        if (validationMessage != null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        }

        validationMessage = ModelUtilities.vdbNameReservedValidation(proposedVdbName);
        if (validationMessage != null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        }

        // Check Version # is an integer
        try {
            Integer.parseInt(getVersion());
        } catch (NumberFormatException nfe) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID,
                                NLS.bind(Messages.GenerateDynamicVdbWizard_validation_versionNotInteger, getVersion())));
            return;
        }

        // dynamic vdb file name
        // can't be null && must end with -vdb.xml
        if (getOutputName() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_vdbFileNameUndefined));
            return;
        }

        if (!getOutputName().toLowerCase().endsWith(ITeiidVdb.DYNAMIC_VDB_SUFFIX)) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_vdbMissingXmlExtension));
            return;
        }

        // output location can't be null
        if (getOutputLocation() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_targetLocationUndefined));
            return;
        }

        if (!getOutputLocation().exists()) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_targetLocationNotExist));
            return;
        }

        IFile destination = getDestination();
        if (destination.exists()) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_targetFileAlreadyExists));
            return;
        }

        if (! isGenerateRequired() && getDynamicVdb() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated));
            return;
        }
    }
}
