package org.teiid.designer.udf;

import java.io.InputStream;
import org.eclipse.core.runtime.IStatus;

/**
 * An interface that can publish a new workspace UDF model and the jar files it depends on.
 * 
 * @since 6.0.0
 */
public interface IWorkspaceUdfPublisher extends IWorkspaceUdfProvider {

    /**
     * @param stream the new UDF model
     * @return a status indicating if the workspace UDF model was successfully replaced
     * @since 6.0.0
     */
    IStatus replaceUdfModel( InputStream stream );

}
