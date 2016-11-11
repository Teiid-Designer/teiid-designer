/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.core.container.Container.OPTIONS;
import org.teiid.designer.metamodels.transformation.TransformationPlugin;
import org.teiid.designer.runtime.spi.TeiidExecutionException;
import org.teiid.designer.transformation.reverseeng.api.AnnotationType;
import org.teiid.designer.transformation.reverseeng.api.Column;
import org.teiid.designer.transformation.reverseeng.api.MetadataProcessor;
import org.teiid.designer.transformation.reverseeng.api.Options;
import org.teiid.designer.transformation.reverseeng.api.Table;
import org.teiid.designer.transformation.reverseeng.api.Options.Parms;
import org.teiid.designer.transformation.reverseeng.util.Util;

/**
 * @author van halbert
 *
 */
public class PojoProcessing {
	
	private static final String LICENSE_TEMPLATE = "org/teiid/reverseeng/license_template.txt";

	private Options options;
	private String path;
	private String packageName;
	private String pojoJarName;
	private String suffixClassName;
	private String moduleZipFileName;
	private AnnotationType annotationType;
	private ModulePackaging module = null;
	private Collection<Exception> errors = new ArrayList<Exception>();
	
	public PojoProcessing() {
	}

	public PojoProcessing(Options options) {
		this.options = options;
		
		this.path = options.getProperty(Options.Parms.BUILD_LOCATION);
		
		annotationType = options.getAnnotationTypeInstance();
		
		packageName = options.getProperty(Options.Parms.POJO_PACKAGE_NAME);

		pojoJarName = options.getProperty(Options.Parms.POJO_JAR_FILE);

		this.suffixClassName = options.getProperty(Options.Parms.CLASS_NAME_SUFFIX);
		
		moduleZipFileName =  options.getProperty(Options.Parms.MODULE_ZIP_FILE);
		
		String doGenerateModule = options.getProperty(Options.Parms.GENERATE_MODULE);
		if (doGenerateModule != null && moduleZipFileName != null) {
			module = new ModulePackaging();
		}

	}
	
	public Collection<Exception> getExceptions() {
		return errors;
	}

	/**
	 * Called to perform the project processing of the metadata.  Will return <code>true</code> if it 
	 * was successful, with no exceptions.  If it returns <code>false</code>, then should call
	 * {@link #getExceptions()} to obtain the errors that occurred.
	 * @param metadata
	 * @return boolean true if successful
	 */
	public boolean processTables(MetadataProcessor metadata, Options options) {
//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Start reverse engineering process");

		try {

			performProcess(metadata, options);

		} catch (Exception e) {
			errors.add(e);
		}
		
//		ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Finished reverse engineering process");
		
		if (errors.isEmpty()) return true;
		
		return false;
	}
	
	private void performProcess(MetadataProcessor metadata, Options options) throws Exception {
		
	
		// remove if already exist
		File buildLocation = new File(path);
		if (buildLocation.exists()) {
			buildLocation.delete();
		}
		buildLocation.mkdir();
			
		// Location for the .java and .class files
		File classDirLocaton = new File(buildLocation, "class");
		classDirLocaton.mkdir();
		
		// location for created kits, example: .jar or .zip
		File kitLocation = new File(buildLocation, "kit"); 
		kitLocation.mkdir();
		
		// parse the package name to use to create the folder structure
		List<String> nodes = Util.getTokens(packageName, ".");

		StringBuffer jarPackageFilePath = new StringBuffer();
		for (String n : nodes) {
			jarPackageFilePath.append(n).append(File.separator);
		}
		// TODO: future option to have user define the name of the "main" source folder
		nodes.add("main");
		
		// create a file path structure of only the package name, used for creating path structure for artifacts
		StringBuffer modulePackagePath = new StringBuffer();
			
		for (String n : nodes) {
			modulePackagePath.append(n).append(File.separator);
		}
		
		File javaFileLoc = new File(classDirLocaton.getAbsolutePath() + File.separator + jarPackageFilePath.toString());
		javaFileLoc.mkdirs();
		
		File pojoJarFile = null;
		if (pojoJarName != null && pojoJarName.trim().length() > 0) {
			pojoJarFile = new File(kitLocation, pojoJarName);
		} else {
			
			pojoJarFile = new File(kitLocation, Options.Parms_Defaults.DEFAULT_POJO_JAR_FILE); 
		}
		
		File parentPojoJar = pojoJarFile.getParentFile();
		if (!parentPojoJar.exists()) {
			parentPojoJar.mkdirs();
		}

		List<Table> tables = metadata.getTableMetadata();
		
		Table theTable = tables.get(0);
			
		if (!theTable.hasRequiredColumn()) {
			String msg = "*** Table {" + theTable.getName() + "} doesn't have a required key column (i.e., no primary or unique key defined on the source table).  Will not be processed";
			
			TransformationPlugin.Util.log(IStatus.WARNING, msg);

			this.errors.add( new TeiidExecutionException(IStatus.ERROR, msg));
		}
		
		String className = theTable.getClassName();
		String userDefinedClassName = options.getProperty(Parms.POJO_CLASS_NAME);
		if( userDefinedClassName != null ) {
			className = userDefinedClassName;
		}
	    className = className + (this.suffixClassName != null ? this.suffixClassName : "");
		String javaFileName = className + ".java";
		
		File outputFile = new File(javaFileLoc.getAbsolutePath(), javaFileName);
					
		FileOutputStream fileOutput = new FileOutputStream(outputFile);

		PrintWriter outputStream = new PrintWriter(fileOutput);

		printPackage(outputStream, packageName);
		printHeader(outputStream, theTable);
		printImports(outputStream, theTable);
		
		printClass(outputStream, theTable, className);
		printAttributes(outputStream, theTable);
		printGetterSetters(outputStream, theTable);
		printToString(outputStream, theTable);
		printFooter(outputStream, theTable);
		
		fileOutput.close();
		
		TransformationPlugin.Util.log(IStatus.INFO, "[ReverseEngineering] Created java file: " + outputFile.getAbsolutePath());

		
		PojoCompilation.compile(javaFileLoc, classDirLocaton, jarPackageFilePath.toString(), pojoJarFile);	
		
		if (module != null) {
			try {
				module.performPackaging(options, packageName, pojoJarFile, moduleZipFileName, buildLocation, modulePackagePath.toString(), kitLocation);

//				ReverseEngineerPlugin.LOGGER.info("[ReverseEngineering] Created module zip: " + pojoJarName);

			} catch(Exception e) {
				errors.add(e);
			}
		}

		
	}
	
	protected void printPackage(PrintWriter outputStream, String packageName) throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream(LICENSE_TEMPLATE);
		if (is != null) { 
			for( int c = is.read(); c != -1; c = is.read() ) {
				outputStream.print((char) c);
			}
		}
		
		outputStream.println("package " + packageName + ";");
		
	}

	/**
	 * prints standard source file header to outputStream
	 * 
	 * @param outputStream
	 *            where to print the header
	 * @param t
	 *            the Table representing the class
	 */
	protected void printHeader(PrintWriter outputStream, Table t) {

		outputStream.println("/**");
		outputStream.println("* Maps a relational database table "
				+ t.getName() + " to a java object, " + t.getClassName());
		outputStream.println("*");
		outputStream.println("* " + (t.getRemarks() != null ? t.getRemarks() : ""));
		outputStream.println("*");
		outputStream.println("* @author	ReverseEngineer");
		outputStream.println("*/");


	} // printHeader()

	/**
	 * prints imports to outputStream
	 * 
	 * @param outputStream
	 *            where to print the header
	 * @param t
	 *            the Table representing the class
	 */
	protected void printImports(PrintWriter outputStream, Table t) {

		outputStream.println("import java.io.Serializable;");
		outputStream.println("import java.sql.*;");
		outputStream.println("import java.util.*;");
		
		if (annotationType != null) {
			List<String> imports = annotationType.getImports();
			for(String i : imports) {
				outputStream.println(i);
			}		
		}		
	} // printImports()	
		
	protected void printClass(PrintWriter outputStream, Table t, String className) {
		outputStream.println("\r");
		if (annotationType != null) {
			String a = annotationType.getClassAnnotation(t);
			if (a != null) {
				outputStream.println(a);
			}
		}
		outputStream.println("public class " + className
				+ " implements Serializable {");

		
	} // printClass
	
	protected void printAttributes(PrintWriter outputStream, Table t) {
		List<Column> columns = t.getColumns();

		for (Column c : columns) {
			if (annotationType != null) {
				outputStream.println("\r");

				String a = annotationType.getAttributeAnnotation(c);
				if (a != null) {
					outputStream.println("\t" + a);			
				}
			}

			outputStream.println(buildAttributeStatement(c));
		}
	}

	protected void printGetterSetters(PrintWriter outputStream, Table t) {
		outputStream.println("\r");

		List<Column> columns = t.getColumns();

		for (Column c : columns) {
			if (annotationType != null) {
				outputStream.println("\r");
	
				String a = annotationType.getGetterMethodAnnotation(c);
				if (a != null) {
					outputStream.println(a); //"\t" + a);			
				}
			}
			outputStream.println(buildGetStatement(c));

			outputStream.println("\r");
			
			outputStream.println(buildSetStatement(c));
		}

	}

	protected void printToString(PrintWriter outputStream, Table t) {
		outputStream.println(buildToString(t));
		
	}

	protected void printFooter(PrintWriter outputStream, Table t) {

		outputStream.println("} // class " + t.getClassName());
		outputStream.close();

	}

	/**
	 * Called to build the Attribute statement based on the Column
	 * 
	 * @param column
	 * @return Attribute statement
	 */
	public String buildAttributeStatement(Column column) {

		StringBuffer result = new StringBuffer();

		result.append("\tpublic ");
		result.append(column.getJavaType());
		result.append(" m_");
		result.append(column.getMemberName());
		result.append(";");

		return result.toString();

	}

	/**
	 * builds a GET statement based on the Column
	 * 
	 * @param column
	 * @return the get statement string
	 */

	public String buildGetStatement(Column column) {

		StringBuffer result = new StringBuffer();

		result.append("\tpublic ");
		result.append(column.getJavaType());
		result.append(" get");
		result.append(column.getMemberName());
		result.append("( ) { \r");
		result.append("\t\treturn this.m_");
		result.append(column.getMemberName());
		result.append(";");
		result.append("\r\t}");

		return result.toString();
	}

	/**
	 * Called to build a SET based on the Column
	 * 
	 * @param column
	 * @return the set statement string
	 */

	public String buildSetStatement(Column column) {

		StringBuffer result = new StringBuffer();

		result.append("\tpublic void set");
		result.append(column.getMemberName());
		result.append("( ");
		result.append(column.getJavaType());
		result.append(" ");
		result.append(column.getName());
		result.append(") { \r\t\t this.m_");
		result.append(column.getMemberName());
		result.append(" = ");
		result.append(column.getName());
		result.append("; \r\t}");

		return result.toString();
	}

	/**
	 * Called to build the toString based on the Table
	 * 
	 * @param table
	 * @return the set statement string
	 */

	public String buildToString(Table table) {

		StringBuffer result = new StringBuffer();
		result.append("\tpublic String toString()  {\n\t\tStringBuffer output = new StringBuffer();\n");

		List<Column> columns = table.getColumns();

		for (Column c : columns) {

			result.append("\t\toutput.append(\"" + c.getName() + ":\");\n");
			result.append("\t\toutput.append(get" + c.getMemberName() + "());\n");
			result.append("\t\toutput.append(\"\\n\");\n");

		}

		result.append("\n\t\treturn output.toString();\n\t}\n");

		return result.toString();
	}
}
