/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.api;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.teiid.designer.transformation.reverseeng.annotation.HibernateAnnotation;
import org.teiid.designer.transformation.reverseeng.annotation.ProtobufAnnotation;

/**
 * The Options class is used to provide controls the the build and packaging process of reverse engineering.  
 * It provides controls for the following:
 * <li>{@code Parms.BUILD_LOCATIION} : where the code will be located
 * <li>{@code Parms.POJO_PACKAGE_NAME} : what is the package name used in the class
 * <li>{@code Parms.POJO_JAR_FILE} : what jar file to package the classes
 * <li>{@code Parms.ANNOTATION} : what type of annotations to use
 * <br>
 * There are defaults, {@code Parms_Defaults}, that will be used if any of the above are not provided.
 * 
 * @author vanhalbert
 *
 */
public class Options {
	
	public interface Parms {
		/* [Optional] The location the reverse engineering process will be done */
		public static final String BUILD_LOCATION = "build_location";
		/* [Optional] The package name to use for the java files */
		public static final String POJO_PACKAGE_NAME = "pojo_package_name";
		/* [Optional] The pojo jar file, with preferable, including the full path.  If path not specified, will use build_location */
		public static final String POJO_JAR_FILE = "pojo_jar_file";
		/* [Optional] The pojo jar file, with preferable, including the full path.  If path not specified, will use build_location */
		public static final String POJO_CLASS_NAME = "pojo_class_name";
		/* [Optional] The module zip name, if not specified, the module will not be created */
		public static final String MODULE_ZIP_FILE = "module_zip_file";
		
		/* [Optional] Used to change or remove the suffix of "Cache" added to the class name */
		public static final String CLASS_NAME_SUFFIX = "class_name_suffix";

		/* [Optional] Choose either Hiberanate or Protobuf annotations to the java file */
		public static final String ANNOTATION = "annotation";
		
		/* [Optional] Generate JDG Zip Module */
		public static final String GENERATE_MODULE = "generate_module";
		
	}
	
	public interface Parms_Defaults {
		public static final String DEFAULT_POJO_PACKAGE_NAME = "org.teiid.pojo";
		public static final String DEFAULT_BUILD_LOCATION = ".";
		public static final String DEFAULT_POJO_JAR_FILE = "pojo.jar";
		public static final String DEFAULT_CLASS_NAME_SUFFIX = "Cache";
	}
	
	private Properties properties = new Properties();
	
	public enum Annotation_Type {
//		Hibernate,
		Protobuf,
		Unknown		
	}
	
	public Options() {
		setProperty(Parms.BUILD_LOCATION, Parms_Defaults.DEFAULT_BUILD_LOCATION);
		setProperty(Parms.POJO_PACKAGE_NAME, Parms_Defaults.DEFAULT_POJO_PACKAGE_NAME);
		setProperty(Parms.POJO_JAR_FILE, Parms_Defaults.DEFAULT_POJO_JAR_FILE);
	}
	
	private Annotation_Type annotation_type;
	

	public void setProperty(String name, String value) {	
		if (value == null) return;
		
		properties.setProperty(name, value);
		if (name.equals(Parms.ANNOTATION)) {
			setAnnotationType(Annotation_Type.valueOf(value));
		}
	}
	
	public String getProperty(String property_name) {
		return this.properties.getProperty(property_name);
	}
	
	public void setAnnotationType(Annotation_Type type) {
		this.annotation_type = type;
	}
	
	public Annotation_Type getAnnotationType() {
		return this.annotation_type;
	}
	
	public boolean useHibernateAnnotations() {
		return false; // TODO: enable HIBERNATE (annotation_type != null && annotation_type == Annotation_Type.Hibernate);
	}
	
	
	public boolean useProtobufAnnotations() {
		return (annotation_type != null && annotation_type == Annotation_Type.Protobuf);
	}
	
	public AnnotationType getAnnotationTypeInstance() {
		if (useHibernateAnnotations()) { 
			return new HibernateAnnotation();
		}
		if (useProtobufAnnotations()) {
			return new ProtobufAnnotation();
		}
			
		AnnotationType unknown = new AnnotationType()  {
			
			@Override
			public String getClassAnnotation(Table t) {
				return "";
			}

			@Override
			public String getAttributeAnnotation(Column c) {
				return "";
			}

			@Override
			public String getGetterMethodAnnotation(Column c) {
				return "";
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<String> getImports() {
				return Collections.EMPTY_LIST;
			}
		};
		
		return unknown;
	}

}
