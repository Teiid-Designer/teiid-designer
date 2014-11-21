/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.refactor;

import org.eclipse.core.runtime.Path;

/**
 * Class for holding a pair of related paths, usually a path to be replaced
 * and its replacement.
 */
public class PathPair {

    private final String sourcePath;
    private final String targetPath;

    /**
     * Create a new instance
     *
     * @param sourcePath
     * @param targetPath
     */
    public PathPair( String sourcePath,  String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @return the targetPath
     */
    public String getTargetPath() {
        return targetPath;
    }
    
    /**
     * @return the source resource name with no extension
     */
    public String getSourceNameNoExtension() {
    	if( this.sourcePath != null ) {
    		return new Path(sourcePath).removeFileExtension().lastSegment();
    	}
    	return null;
    }
    
    
    /**
     * @return the target resource name with no extension
     */
    public String getTargetNameNoExtension() {
    	if( this.targetPath != null ) {
    		return new Path(targetPath).removeFileExtension().lastSegment();
    	}
    	return null;
    }

    @Override
    public String toString() {
        return "PathPair [sourcePath=" + this.sourcePath + ", targetPath=" + this.targetPath + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.sourcePath == null) ? 0 : this.sourcePath.hashCode());
        result = prime * result + ((this.targetPath == null) ? 0 : this.targetPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PathPair other = (PathPair)obj;
        if (this.sourcePath == null) {
            if (other.sourcePath != null) return false;
        } else if (!this.sourcePath.equals(other.sourcePath)) return false;
        if (this.targetPath == null) {
            if (other.targetPath != null) return false;
        } else if (!this.targetPath.equals(other.targetPath)) return false;
        return true;
    }
}