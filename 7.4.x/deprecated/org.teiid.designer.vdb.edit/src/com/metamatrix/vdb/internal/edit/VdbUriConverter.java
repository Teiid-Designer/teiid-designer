/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import com.metamatrix.core.util.TempDirectory;

/**
 * @since 4.2
 */
public class VdbUriConverter extends ExtensibleURIConverterImpl {

    private static final String SEGMENT_SEPARATOR = "/"; //$NON-NLS-1$

    private IPath tempDirPath = null;

    /**
     * @since 4.2
     */
    public VdbUriConverter( final TempDirectory theTempDirectory ) {
        super();
        this.tempDirPath = new Path(theTempDirectory.getPath());
    }

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#normalize(org.eclipse.emf.common.util.URI)
     * @since 4.2
     */
    @Override
    public URI normalize( URI uri ) {
        String fragment = uri.fragment();
        URI result = fragment == null ? getInternalURIMap().getURI(uri) : getInternalURIMap().getURI(uri.trimFragment()).appendFragment(fragment);
        String scheme = result.scheme();
        if (scheme == null) {
            if (result.hasAbsolutePath()) {
                final String uriString = URI.decode(uri.toString());
                if (uriString.startsWith(SEGMENT_SEPARATOR)) {
                    final IPath tempDirFilePath = this.tempDirPath.append(new Path(uriString));
                    result = URI.createFileURI(tempDirFilePath.toString());
                } else {
                    result = URI.createURI("file:" + result); //$NON-NLS-1$
                }
            } else {
                result = URI.createFileURI(new File(result.trimFragment().toString()).getAbsolutePath());
                if (fragment != null) {
                    result = result.appendFragment(fragment);
                }
            }
        }

        if (result.equals(uri)) {
            return uri;
        }
        return normalize(result);
    }

}
