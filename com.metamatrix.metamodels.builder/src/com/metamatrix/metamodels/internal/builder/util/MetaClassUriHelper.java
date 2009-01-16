package com.metamatrix.metamodels.internal.builder.util;

/** 
 * This is a helper class for getting parts of a MetaClass URI
 */
public class MetaClassUriHelper {
    private static final String METACLASS_URI_DELIMITER = "#//";  //$NON-NLS-1$
	
	// ==================================================================================
    //                        S T A T I C  M E T H O D S
    // ==================================================================================

    /** 
     * Get the packageUri from the metaClassUri string
     * @param metaClassUri the full metaClass URI
     * @return the package URI part of the metaClassURI
     */
	public static String getPackageUri(String metaClassUri) {
	    // find the delimiter
	    int index = metaClassUri.indexOf(METACLASS_URI_DELIMITER);
	    if (index == -1) {
	        // the metaClassUri is bad
	    	return null;
	    }
	    return metaClassUri.substring(0,index);
	}
	
    /** 
     * Get the EClass name from the metaClassUri string
     * @param metaClassUri the full metaClass URI
     * @return the EClass name part of the metaClassURI
     */
	public static String getEClassName(String metaClassUri) {
	    // find the delimiter
	    int index = metaClassUri.indexOf(METACLASS_URI_DELIMITER);
	    if (index == -1) {
	        // the metaClassUri is bad
	    	return null;
	    }
	    return metaClassUri.substring(index+METACLASS_URI_DELIMITER.length());
	}
}
