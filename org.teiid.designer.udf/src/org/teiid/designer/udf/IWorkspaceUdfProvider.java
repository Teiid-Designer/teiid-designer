package org.teiid.designer.udf;


/**
 * An interface that provides the workspace locations of the UDF model and the jar files the UDF model requires.
 * 
 * @since 6.0.0
 */
public interface IWorkspaceUdfProvider {

    /**
     * @return the absolute path of the workspace UDF model (never <code>null</code>)
     * @since 6.0.0
     */
    String getUdfModelPath();

}
