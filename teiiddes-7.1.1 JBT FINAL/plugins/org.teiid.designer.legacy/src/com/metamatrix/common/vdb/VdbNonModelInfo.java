/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.vdb;

/**
 * Class used to store NonModelReference information extracted from the MetaMatrix-VdbManifestModel.xmi model
 * 
 * @since 4.3
 */
public class VdbNonModelInfo {

    private String name;
    private String path;
    private long checkSum;

    /**
     * @return Returns the name.
     * @since 4.3
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     * @since 4.3
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return Returns the path.
     * @since 4.3
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @param path The path to set.
     * @since 4.3
     */
    public void setPath( String path ) {
        this.path = path;
    }

    /**
     * @return Returns the checkSum.
     * @since 4.3
     */
    public long getCheckSum() {
        return this.checkSum;
    }

    /**
     * @param checkSum The checkSum to set.
     * @since 4.3
     */
    public void setCheckSum( String checkSum ) {
        this.checkSum = Long.parseLong(checkSum);
    }

    /**
     * Method to print the contents of the VdbModelInfo object.
     * 
     * @param stream the stream
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("Name: "); //$NON-NLS-1$
        sb.append(this.getName());
        sb.append(", Path: "); //$NON-NLS-1$
        sb.append(this.getPath());
        sb.append(", checkSum: "); //$NON-NLS-1$
        sb.append(this.getCheckSum());
        return sb.toString();
    }

}
