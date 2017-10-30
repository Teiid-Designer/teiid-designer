/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.jdg;

public interface IJDGProfileConstants {

    String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$
    String JDG_TRANSLATOR_NAME = "infinispan-cache-dsl"; //$NON-NLS-1$
    String JDG_TRANSLATOR_TYPE = "infinispan-cache-dsl"; //$NON-NLS-1$
    String JDG7_TRANSLATOR_NAME = "infinispan-hotrod"; //$NON-NLS-1$
    String JDG7_TRANSLATOR_TYPE = "infinispan-hotrod"; //$NON-NLS-1$
    String JDG_RA_TYPE = "infinispan-dsl"; //$NON-NLS-1$
    String JDG7_RA_TYPE = "infinispan-hotrod"; //$NON-NLS-1$
    String JNDI_PROP_ID = "JDGJndi"; //$NON-NLS-1$
    String TRANSLATOR_PROP_ID = "JDGDsTranslator"; //$NON-NLS-1$
    
    String REQUIRED_CLASS_NAME = "org.teiid.resource.adapter.infinispan.dsl.InfinispanManagedConnectionFactory"; //$NON-NLS-1$
    String REQUIRED_JDG7_CLASS_NAME = "org.teiid.resource.adapter.infinispan.hotrod.InfinispanManagedConnectionFactory"; //$NON-NLS-1$
    
    
    /**
     * When a source model is created for the materialized tables, these translator override properties
     * need to get set
     * @author blafond
     *
     */
    interface TranslatorOverrides {
    	String SUPPORTS_DIRECT_QUERY_PROCEDURE ="SupportsDirectQueryProcedure"; //$NON-NLS-1$
    	String SUPPORTS_NATIVE_QUERIES ="SupportsNativeQueries"; //$NON-NLS-1$
    }
    
    interface PropertyKeys {
    	/*
    	 REQUIRED PROPERTY
    	 FORMAT:    cacheName:className[;pkFieldName[:cacheKeyJavaType]]
    	 Map the root Java Object class name to the cache, and identify which attribute is 
    	 the primary key to the cache. Provide the cacheKeyJavaType when the key type is 
    	 different than the pkFieldName attribute type 
    	 */
	    String CACHE_TYPE_MAP = "CacheTypeMap"; //$NON-NLS-1$
	    
	    /*
	     FORMAT:   host:port[;host:port….] 	
	     Specify the host and ports that will be clustered 
	     together to access the caches defined in CacheTypeMap 
	     */
	    String REMOTE_SERVER_LIST = "RemoteServerList"; //$NON-NLS-1$
	    
	    /*
	     The Infinispan Configuration xml file for configuring a local cache 
	     */
	    String CACHE_JNDI_NAME = "CacheJndiName"; //$NON-NLS-1$
	    
	    /*
	     The HotRod properties file for configuring a connection to a remote cache 
	     */
	    String HOT_ROD_CLIENT_PROPERTIES_FILE = "HotRodClientPropertiesFile"; //$NON-NLS-1$
	    
	    /* ------------------------------------------------------------------------
	     NOTE: The following are the additional properties that 
	     REQUIRED to be configured if using the Remote Cache for external materialization
	     
	     Cache name for the staging cache used in materialization 
	     */
	    String STAGING_CACHE_NAME = "StagingCacheName"; //$NON-NLS-1$
	    
	    // Cache name for the alias cache used in tracking aliasing of the caches used in materialization
	    String ALIAS_CACHE_NAME = "AliasCacheName"; //$NON-NLS-1$
	    // ------------------------------------------------------------------------
	    
	    // ------------------------------------------------------------------------
	    // JDG Schema Protobuf properties
	    String PROTOBUF_DEFINITION_FILE = "ProtobufDefinitionFile"; //$NON-NLS-1$ (REQUIRED) Path to the Google Protobin file that's packaged in a jar (ex: /quickstart/addressbook.protobin)
		String MESSAGE_MARSHALLERS = "MessageMarshallers"; //$NON-NLS-1$ (REQUIRED) Contains Class names mapped its respective message marshaller, (className:marshallerClassName,className:marshallerClassName...), that are to be registered for serialization
		String MESSAGE_DESCRIPTOR = "MessageDescriptor"; //$NON-NLS-1$  (REQUIRED) Message descriptor class name for the root object in cache 
	    
	    /*
	     Specify the JBoss AS module that contains the cache classes 
	     that were defined in CacheTypeMap. 
	     */
	    String MODULE = "module"; //$NON-NLS-1$  (OPTIONAL
	    // ------------------------------------------------------------------------
	    
	    String CLASS_NAME = "class-name"; //$NON-NLS-1$
	    
    	String SUPPORTS_DIRECT_QUERY_PROCEDURE = "SupportsDirectQueryProcedure"; //$NON-NLS-1$
    	String SUPPORTS_NATIVE_QUERIES = "SupportsNativeQueries"; //$NON-NLS-1$
    	/*
    	   <config-property>
                <description>{$display:"TrustStore File Name",$description:"File name of the truststore",$required:"false"}</description>
               <config-property-name>TrustStoreFileName</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property> 
    	 */
    	String TRUST_FILE_STORE_NAME = "TrustStoreFileName"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"TrustStore Password",$description:"TrustStore Password",$required:"false",$masked:"true"}</description>
               <config-property-name>TrustStorePassword</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property> 
    	 */
    	String TRUST_STORE_PASSWORD = "TrustStorePassword"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"KeyStore File Name",$description:"File name of the keystore",$required:"false"}</description>
               <config-property-name>KeyStoreFileName</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>  
    	 */
    	String KEY_STORE_FILE_NAME = "KeyStoreFileName"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"KeyStore Password",$description:"KeyStore Password",$required:"false",$masked:"true"}</description>
               <config-property-name>KeyStorePassword</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>   
    	 */
    	
    	String KEY_STORE_PASSWORD = "KeyStorePassword"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"Authentication Server Name",$description:"Authentication Server Name",$required:"false"}</description>
               <config-property-name>AuthenticationServerName</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>  
    	 */
    	String AUTHENTICATION_SERVER_NAME = "AuthenticationServerName"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"Authentication Application Realm",$description:"Authentication Application Realm",$required:"false"}</description>
               <config-property-name>AuthenticationRealm</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>
    	 */
    	String AUTHENTICATION_REALM = "AuthenticationRealm"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"Authentication SASL Mechanism",$description:"Authentication SASL Mechanism",$required:"false"}</description>
               <config-property-name>SaslMechanism</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>  
    	 */
    	String SASL_MECHANISM = "SaslMechanism"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"Authentication User Name",$description:"Authentication User Name",$required:"false"}</description>
               <config-property-name>UserName</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>   
    	 */
    	String AUTHENTICATION_USER_NAME = "UserName"; //$NON-NLS-1$
    	
    	/*
            <config-property>
                <description>{$display:"Authentication User Password",$description:"Authentication User Password",$required:"false",$masked:"true"}</description>
               <config-property-name>Password</config-property-name>
               <config-property-type>java.lang.String</config-property-type>
            </config-property>   
		 */
		String AUTHENTICATION_PASSWORD = "Password"; //$NON-NLS-1$
    }
}
