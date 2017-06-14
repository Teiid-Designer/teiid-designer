package org.teiid.designer.core.util;

public class JndiUtil {
	public static final String JAVA_PREFIX = "java:/";
	
    public static String addJavaPrefix(String name) {
    	if( !name.startsWith(JAVA_PREFIX) ) {
    		return JAVA_PREFIX + name;
    	}
    	
    	return name;
    }
    
	public static String removeJavaPrefix(String nameWithPrefix) {
		if (nameWithPrefix.startsWith(JAVA_PREFIX)) {
			nameWithPrefix = nameWithPrefix.substring(6);
		}
		return nameWithPrefix;
	}
	
	public static boolean hasJavaPrefix(String jndiName) {
		return jndiName.startsWith(JAVA_PREFIX);
	}
}
