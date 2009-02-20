package com.metamatrix.modeler.dqp.workspace.udf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FakeUdfPublisher implements IWorkspaceUdfPublisher {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private Collection<String> addedJars = new ArrayList<String>();

    private boolean udfModelReplaced = false;

    private Set<String> udfModelArchivePaths;

    private final String workspaceUdfModelPath;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    public FakeUdfPublisher( String workspaceUdfModelPath ) {
        this.workspaceUdfModelPath = workspaceUdfModelPath;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfPublisher#addUdfJarFile(java.lang.String, java.io.InputStream)
     */
    @Override
    public IStatus addUdfJarFile( String jarFileName,
                                  InputStream stream ) {
        addedJars.add(jarFileName);
        return Status.OK_STATUS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfProvider#getUdfJarFilePaths()
     */
    @Override
    public Set<String> getUdfJarFilePaths() {
        return this.udfModelArchivePaths;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfProvider#getUdfModelPath()
     */
    @Override
    public String getUdfModelPath() {
        return this.workspaceUdfModelPath;
    }

    public boolean hasJarBeenImported( String jarName ) {
        return this.addedJars.contains(jarName);
    }

    public boolean isUdfModelReplaced() {
        return this.udfModelReplaced;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.workspace.udf.IWorkspaceUdfPublisher#replaceUdfModel(java.io.InputStream)
     */
    @Override
    public IStatus replaceUdfModel( InputStream stream ) {
        this.udfModelReplaced = true;
        return Status.OK_STATUS;
    }

    public void setUdfModelArchivePaths( Set<String> udfModelArchivePaths ) {
        this.udfModelArchivePaths = udfModelArchivePaths;
    }

}
