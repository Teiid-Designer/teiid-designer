/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.io.StringWriter;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 *
 */
public abstract class AbstractGenerateVdbManager {

    protected static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                                       StringNameValidator.DEFAULT_MAXIMUM_LENGTH,
                                                                                       new char[] {'_', '-', '.'});

    private DynamicVdb dynamicVdb;

    private String version = Integer.toString(1);

    private XmiVdb archiveVdb;

    private IStatus status = Status.OK_STATUS;

    private IContainer outputLocation;

    private String outputName;

    /**
     * @return dynamic vdb
     */
    public DynamicVdb getDynamicVdb() {
    	return dynamicVdb;
    }

    /**
     * @param dynamicVdb the dynamicVdb to set
     */
    protected void setDynamicVdb(DynamicVdb dynamicVdb) {
        this.dynamicVdb = dynamicVdb;
    }

    /**
     * @return archive vdb
     */
    public XmiVdb getArchiveVdb() {
    	return archiveVdb;
    }

    /**
     * @param archiveVdb the archiveVdb to set
     */
    protected void setArchiveVdb(XmiVdb archiveVdb) {
        this.archiveVdb = archiveVdb;
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
    protected void setOutputLocation(IContainer outputLocation) {
        this.outputLocation = outputLocation;
    }

    /**
     * @return the outputName
     */
    public String getOutputName() {
        return this.outputName;
    }

    /**
     * @return destination file
     */
    public IFile getDestination() {
        return outputLocation.getFile(new Path(getOutputName()));
    }

    /**
     * @param outputName the outputName to set
     */
    protected void setOutputName(String outputName) {
        this.outputName = outputName;
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
     * @return the xml string representation of the dynamic vdb
     * @throws Exception if error occurs
     */
    public String getDynamicVdbXml() throws Exception {
        StringWriter writer = new StringWriter();

        //
        // Writer will be closed by write method
        //
        dynamicVdb.write(writer);
        return writer.toString();
    }

    /**
     * @return status
     */
    public IStatus getStatus() {
    	return status;
    }

    /**
     * @param status the status to set
     */
    protected void setStatus(IStatus status) {
        this.status = status;
    }
}
