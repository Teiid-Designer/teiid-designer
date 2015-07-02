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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the dynamic vdb
 */
public class GenerateDynamicVdbManager extends AbstractGenerateVdbManager {

    private IFile archiveVdbFile;
    
    private boolean excludeSourceMetadata;

    /**
     * @param wizard
     * @param archiveVdbFile
     * @throws Exception
     */
    public GenerateDynamicVdbManager(AbstractWizard wizard, IFile archiveVdbFile) throws Exception {
        super(wizard);
        CoreArgCheck.isNotNull(archiveVdbFile);

        this.archiveVdbFile = archiveVdbFile;
        setArchiveVdb(new XmiVdb(archiveVdbFile));

        String vdbName = getArchiveVdb().getName();
        setOutputVdbName(vdbName);
        setOutputVdbFileName(vdbName + ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        setOutputLocation(this.archiveVdbFile.getParent());
    }

    private void checkDynamicVdbGenerated() throws Exception {
        if (isGenerateRequired())
            throw new Exception(Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated);
    }

    /**
     * Generate the dynamic xml from the xmi vdb
     */
    public void generate() {
        if (!isGenerateRequired())
            return;

        if (getArchiveVdb() == null)
            return;
        
        getArchiveVdb().setExcludeSourceMetadata(excludeSourceMetadata);

        //
        // This will convert the xmi vdb and build the dynamic vdb
        //
        GeneratorCallback<DynamicVdb> callback = new GeneratorCallback<DynamicVdb>() {

            @Override
            public XmiVdb getSourceVdb() {
                return getArchiveVdb();
            }

            @Override
            public Class<DynamicVdb> getTargetType() {
                return DynamicVdb.class;
            }

            @Override
            public void onCompletion(IStatus status) {
                if (status.isOK()) {
                	DynamicVdb vdb = getResult();
                	vdb.setVersion(Integer.parseInt(getVersion()));
                	vdb.setName(getOutputVdbName());
                    setDynamicVdb(vdb);
                }
            }
        };

        generateVdbJob(callback);
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

        File export = new File(directory, getOutputVdbFileName());
        if (export.exists())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbWizard_exportLocationAlreadyExists,
            		getOutputVdbFileName(),
                                         directory));

        if (!export.createNewFile())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbWizard_exportLocationFailedToCreateFile,
            		getOutputVdbFileName(),
                                         directory));

        FileWriter writer = new FileWriter(export);
        getDynamicVdb().write(writer);
    }

    /**
     * @throws Exception
     */
    @Override
    public void write() throws Exception {
        checkDynamicVdbGenerated();

        StringWriter writer = new StringWriter();
        getDynamicVdb().write(writer);

        String xml = writer.toString();

        IFile destination = getDestination();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8")); //$NON-NLS-1$

        destination.create(inputStream, true, new NullProgressMonitor());

        super.write();
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
    @Override
    public void validate() {
        super.validate();

        if (! Status.OK_STATUS.equals(getStatus()))
            return; // Something already wrong - no need to check further

        if (!getOutputVdbFileName().toLowerCase().endsWith(ITeiidVdb.DYNAMIC_VDB_SUFFIX)) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_vdbMissingXmlExtension));
            return;
        }

        if (!isGenerateRequired() && getDynamicVdb() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated));
            return;
        }
    }
    
    
    
    @Override
	protected void setOutputVdbFileName(String outputName) {
		if( StringUtilities.areDifferent(outputName,  getOutputVdbFileName())) {
			setDynamicVdb(null);
		}
		super.setOutputVdbFileName(outputName);
	}
    
    /**
     * @param outputName the outputVdbName to set
     */
    protected void setOutputVdbName(String outputVdbName) {
		if( StringUtilities.areDifferent(outputVdbName,  getOutputVdbName())) {
			setDynamicVdb(null);
		}
        super.setOutputVdbName(outputVdbName);
    }

	@Override
	public void setVersion(String version) {
		if( StringUtilities.areDifferent(version,  getVersion())) {
			setDynamicVdb(null);
		}
		super.setVersion(version);
	}

	/**
     * 
     * @return exclude source DDL value
     */

    public boolean isExcludeSourceMetadata() {
		return excludeSourceMetadata;
	}
 
     /**
     * 
     * @param setExcludeSourceDdl
     */
	public void setExcludeSourceMetadata(boolean excludeSourceMetadata) {
		if( excludeSourceMetadata != this.excludeSourceMetadata ) {
			if( !isGenerateRequired() ) setDynamicVdb(null);
			this.excludeSourceMetadata = excludeSourceMetadata;
		}
	}
}
