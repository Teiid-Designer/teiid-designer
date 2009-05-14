package com.metamatrix.modeler.dqp.workspace.udf;

import java.util.Set;

/**
 * An interface that provides the workspace locations of the UDF model and the jar files the UDF model requires.
 * 
 * @since 6.0.0
 */
public interface IWorkspaceUdfProvider {

    /**
     * @return the absolute paths of all jar files (*.jar) the workspace UDF model depends on (can be <code>null</code>)
     * @since 6.0.0
     */
    Set<String> getUdfJarFilePaths();

    /**
     * @return the absolute path of the workspace UDF model (never <code>null</code>)
     * @since 6.0.0
     */
    String getUdfModelPath();

}
