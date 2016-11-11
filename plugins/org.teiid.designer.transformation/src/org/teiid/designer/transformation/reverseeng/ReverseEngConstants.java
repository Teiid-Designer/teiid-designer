/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng;

public interface ReverseEngConstants {

	String LIBS = "libs"; //$NON-NLS-1$

	String DOT_ZIP = ".zip"; //$NON-NLS-1$

	String PROTOBUF_MODULE_TEMPLATE = "library_mode_module_template.xml"; //$NON-NLS-1$

	String HIBERNATE_MODULE_TEMPLATE = "remote_cache_module_template.xml"; //$NON-NLS-1$

	String UNKOWN_MODULE_TEMPLATE = "unknown_module_template.xml"; //$NON-NLS-1$


	/* PROTOBUF Related */

	String PROTOSTREAM_JAR = "protostream-3.0.5.Final-redhat-1.jar"; //$NON-NLS-1$
	
	String HIBERNATE_SEARCH_JAR = "hibernate-search-engine-4.6.1.Final-redhat-1.jar"; //$NON-NLS-1$
	
    String DEFAULT_PACKAGE_NAME = "org.teiid.jdg.pojo"; //$NON-NLS-1$
    
    String DEFAULT_MODULE_SUFFIX = "_JDG_Module.zip"; //$NON-NLS-1$
    
    String PROTOBUF = "Protobuf";  //$NON-NLS-1$
    String HIBERNATE = "Hibernate";  //$NON-NLS-1$
    String NONE = "NONE";  //$NON-NLS-1$
    String DEFAULT_JDG_MODULE_FOLDER= "/jdg_module";  //$NON-NLS-1$
    
    String INFINISPAN_EXT_ASSISTANT_NS = "infinispan"; //$NON-NLS-1$
    
    String RELATIONAL_EXT_ASSISTANT_NS = "relational"; //$NON-NLS-1$
    
    String SOURCE_MODEL_NAME_SUFFIX = "JDGSource"; //$NON-NLS-1$
    
    public enum Mode  {
    	MATERIALIZE,
    	POJO
    }
}
