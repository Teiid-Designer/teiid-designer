package com.metamatrix.modeler.dqp.workspace.udf;

import java.io.InputStream;
import org.eclipse.core.runtime.IStatus;

/**
 * An interface that can publish a new workspace UDF model and the jar files it depends on.
 * 
 * @since 6.0.0
 */
public interface IWorkspaceUdfPublisher extends IWorkspaceUdfProvider {

    /**
     * Adds a jar file to the workspace from the zip file being imported. The stream will be closed by this class.
     * 
     * @param jarFileName the name (with file extension) of the jar file (never <code>null</code>)
     * @param stream the jar file the UDF model is dependent on (never <code>null</code>)
     * @return a status indicating if adding the jar file was successful (never <code>null</code>)
     * @since 6.0.0
     */
    IStatus addUdfJarFile( String jarFileName,
                           InputStream stream );

    /**
     * @param stream the new UDF model
     * @return a status indicating if the workspace UDF model was successfully replaced
     * @since 6.0.0
     */
    IStatus replaceUdfModel( InputStream stream );

}
