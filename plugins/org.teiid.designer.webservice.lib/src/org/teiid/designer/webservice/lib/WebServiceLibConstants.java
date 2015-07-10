/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.webservice.lib;

/**
 * Constants for the libraries provided by this plugin
 */
@SuppressWarnings( "javadoc" )
public interface WebServiceLibConstants {

    String WEBAPPS = "webapps"; //$NON-NLS-1$

    String WEB_INF = "WEB-INF"; //$NON-NLS-1$

    String LIB = "lib"; //$NON-NLS-1$

    String CLASSES = "classes"; //$NON-NLS-1$

    String IMAGES = "images"; //$NON-NLS-1$

    String WSDL = "wsdl"; //$NON-NLS-1$

    String DOT_WAR = ".war"; //$NON-NLS-1$

    String JBOSS_WEB_XML = "jboss-web.xml"; //$NON-NLS-1$

    String WEB_XML = "web.xml"; //$NON-NLS-1$
    
    String JBOSS_WEB_CXF = "jbossws-cxf.xml"; //$NON-NLS-1$

    String TEIID_REST_PROPS =  "teiidrest.properties"; //$NON-NLS-1$

    String TEIID_SOAP_PROPS =  "teiidsoap.properties"; //$NON-NLS-1$

    /* Rest Related */

    String REST_WAR_RESOURCES = "rest_war_resources"; //$NON-NLS-1$

    String ACTIVATION_JAR = "activation.jar"; //$NON-NLS-1$

    String COMMONS_CODEC_JAR = "commons-codec.jar"; //$NON-NLS-1$

    String COMMONS_LOGGING_JAR = "commons-logging.jar"; //$NON-NLS-1$

    String HTTP_CLIENT_JAR = "httpclient.jar"; //$NON-NLS-1$

    String HTTP_CORE_JAR = "httpcore.jar"; //$NON-NLS-1$

    String JACKSON_CORE_JAR = "jackson-core.jar"; //$NON-NLS-1$

    String JACKSON_JAXRS_JAR = "jackson-jaxrs.jar"; //$NON-NLS-1$

    String JACKSON_DATABIND_JAR = "jackson-databind.jar"; //$NON-NLS-1$
    
    String JACKSON_JAXRS_JSON_PROVIDER_JAR = "jackson-jaxrs-json-provider.jar"; //$NON-NLS-1$

    String JACKSON_MODULE_JSON_SCHEMA_JAR = "jackson-module-jsonSchema.jar"; //$NON-NLS-1$
    
  //  String JACKSON_JAXRS_JSON_PROVIDER_JAR = "jackson-jaxrs-json-provider-2.0.0.jar"; //$NON-NLS-1$
    
  //  String JACKSON_JAXRS_JSON_PROVIDER_JAR = "jackson-jaxrs-json-provider-2.0.0.jar"; //$NON-NLS-1$
    
  //  String JACKSON_JAXRS_JSON_PROVIDER_JAR = "jackson-jaxrs-json-provider-2.0.0.jar"; //$NON-NLS-1$
    
//jackson-annotations-2.1.5.jar
//    jackson-core-2.1.5.jar
//    jackson-databind-2.1.5.jar
//    jackson-jaxrs-json-provider-2.0.0.jar
//    jackson-module-jaxb-annotations-2.0.0.jar
//    jackson-module-jsonSchema-2.1.0.jar
//    jackson-module-scala_2.10-2.1.5.jar

    String JAVASSIST_JAR = "javassist.jar"; //$NON-NLS-1$

    String JAXRS_API_JAR = "jaxrs-api.jar"; //$NON-NLS-1$

    String JCIP_ANNOTATIONS_JAR = "jcip-annotations.jar"; //$NON-NLS-1$

    //String JETTISON_JAR = "jettison.jar"; //$NON-NLS-1$

    String JSON_JAR = "json.jar"; //$NON-NLS-1$

    String RESTEASY_JAXB_PROVIDER_JAR = "resteasy-jaxb-provider.jar"; //$NON-NLS-1$

    String RESTEASY_JAXRS_JAR = "resteasy-jaxrs.Final.jar"; //$NON-NLS-1$

    String RESTEASY_JETTISON_PROVIDER_JAR = "resteasy-jettison-provider-2.3.5.jar"; //$NON-NLS-1$

  //  String SAXONHE_JAR = "saxonhe.jar"; //$NON-NLS-1$

    String SCANANNOTATION_JAR = "scannotation.jar"; //$NON-NLS-1$

   // String SJSXP_JAR = "sjsxp.jar"; //$NON-NLS-1$
    
    String SWAGGER_ANNOTATIONS_JAR = "swagger-annotations.jar"; //$NON-NLS-1$
    
    String SWAGGER_CORE_JAR = "swagger-core_2.10.jar"; //$NON-NLS-1$
    
    String SWAGGER_JAXRS_JAR = "swagger-jaxrs_2.10.jar"; //$NON-NLS-1$
    
    String SERVLET_API = "servlet-api.jar"; //$NON-NLS-1$
    
    String REFLECTIONS = "reflections.jar"; //$NON-NLS-1$
    
    String SCALA_LIBRARY = "scala-library-2.10.0.jar"; //$NON-NLS-1$
    
    String GUAVA = "guava.jar"; //$NON-NLS-1$
    
  //  String JSR11_API = "jsr11_api.jar"; //$NON-NLS-1$
    
    String JSR250_API = "jsr250_api.jar"; //$NON-NLS-1$
    
    String JSR305_API = "jsr305_api.jar"; //$NON-NLS-1$
    
    /* Soap Related */

    String SOAP_WAR_RESOURCES = "war_resources"; //$NON-NLS-1$

    String WSS4J_JAR = "wss4j.jar"; //$NON-NLS-1$
}
