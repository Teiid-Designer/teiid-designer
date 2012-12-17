/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JFileChooser;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.symbol.AliasSymbol;

/**
 *
 */
public class Generator {
    
    private static final String SLASH = "/"; //$NON-NLS-1$

    private static final String DOT = "."; //$NON-NLS-1$

    private static final String NEWLINE = "\n"; //$NON-NLS-1$

    private static final String TAB = "\t"; //$NON-NLS-1$
    
    private static final String SEMI_COLON = ";"; //$NON-NLS-1$
    
    private static final String LICENCE = "" +
        "/*" + NEWLINE +
        " * JBoss, Home of Professional Open Source." + NEWLINE +
        " *" + NEWLINE +
        " * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing." + NEWLINE +
        " *" + NEWLINE +
        " * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors." + NEWLINE +
        " */" + NEWLINE;

    private static final String IMPL = "Impl"; //$NON-NLS-1$

    private static final String JAVA_EXT = ".java"; //$NON-NLS-1$

    private File targetDirectory;

    private void setTargetDirectory() throws Exception {
        JFileChooser chooser = new JFileChooser();
        
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        chooser.setCurrentDirectory(chooser.getCurrentDirectory());
        String path = "/home/phantomjinx/programming/java/tdesigner/git/plugins/teiid/org.teiid8/src/org/teiid8/sql/impl";
        chooser.setCurrentDirectory(new File(path));
        
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
             targetDirectory = chooser.getSelectedFile();
             System.out.println(targetDirectory);
        }
        else
            throw new Exception("Failed to set target directory"); //$NON-NLS-1$
    }
    
    private ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<String>();

        packageName = packageName.replace(DOT, SLASH);
        packageURL = classLoader.getResource(packageName);

        if (packageURL.getProtocol().equals("jar")) { //$NON-NLS-1$
            String jarFileName;
            JarFile jf;
            Enumeration<JarEntry> jarEntries;
            String entryName;

            // build jar file name, then loop through zipped entries
            jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8"); //$NON-NLS-1$
            jarFileName = jarFileName.substring(5, jarFileName.indexOf("!")); //$NON-NLS-1$
            jf = new JarFile(jarFileName);
            jarEntries = jf.entries();
            while (jarEntries.hasMoreElements()) {
                entryName = jarEntries.nextElement().getName();
                if (entryName.startsWith(packageName) && entryName.length() > packageName.length() + 5) {
                    entryName = entryName.substring(packageName.length() + 1, entryName.lastIndexOf('.'));
                    names.add(entryName);
                }
            }

            // loop through files in classpath
        } else {
            File folder = new File(packageURL.getFile());
            File[] contenuti = folder.listFiles();
            String entryName;
            for (File actual : contenuti) {
                entryName = actual.getName();
                entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                names.add(entryName);
            }
        }
        return names;
    }

    private void generate() throws Exception {
        
        setTargetDirectory();
        
        Class<?> createClass = Create.class;
        Package langPackage = createClass.getPackage();
        processClasses(langPackage, getClassNamesFromPackage(langPackage.getName()));
        
        Class<?> blockClass = Block.class;
        Package blockPackage = blockClass.getPackage();
        processClasses(blockPackage, getClassNamesFromPackage(blockPackage.getName()));
        
        Class<?> aliasSymbolClass = AliasSymbol.class;
        Package aliasSymbolPackage = aliasSymbolClass.getPackage();
        processClasses(aliasSymbolPackage, getClassNamesFromPackage(aliasSymbolPackage.getName()));
    }
      
    private void processClasses(Package implPackage, List<String> classes) throws Exception {
        for (String className : classes) {
            if (className.contains("$")) //$NON-NLS-1$
                continue;
            
            Class<?> klazz = Class.forName(implPackage.getName() + DOT + className);
            if (Modifier.isAbstract(klazz.getModifiers()))
                continue;
            
            System.out.println("Class: " + className); //$NON-NLS-1$
            
            String implName = className + IMPL;
            String implFileName = implName + JAVA_EXT;
            File implFile = new File(targetDirectory, implFileName);
            if (! implFile.exists()) {
                createImpl(className, implName, implFile, klazz);
            }
        }
    }

    /**
     * @param className
     * @param implName
     * @param implFile
     * @param srcClass
     * 
     * @throws IOException 
     */
    private void createImpl(String className, String implClassName, File implClassFile, Class<?> srcClass) throws IOException {
        System.out.println("Creating " + implClassName + " in " + implClassFile.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
        
        String implPath = implClassFile.getParent();
        String pkg = implPath.substring(implPath.indexOf("/src/") + 5); //$NON-NLS-1$
        pkg = pkg.replaceAll(SLASH, DOT);
        
        String classParam = Character.toLowerCase(className.charAt(0)) +
                                            (className.length() > 1 ? className.substring(1) : ""); //$NON-NLS-1$
        
        String iFace = "I" + className; //$NON-NLS-1$
        
        StringBuilder builder = new StringBuilder(LICENCE);
        System.out.println(pkg);
        builder.append("package " + pkg + SEMI_COLON);
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        builder.append("/**" + NEWLINE);
        builder.append(" *" + NEWLINE);
        builder.append(" */" + NEWLINE);
        builder.append("public class " + implClassName + " extends LanguageObjectImpl implements " + iFace + " {");
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        builder.append(TAB + "/**" + NEWLINE);
        builder.append(TAB + " * @param " + classParam + NEWLINE);
        builder.append(TAB + " */" + NEWLINE);
        builder.append(TAB + "public " + implClassName + "(" + className + " " + classParam + ") {" + NEWLINE);
        builder.append(TAB + TAB + "super(" + classParam + ");" + NEWLINE);
        builder.append(TAB + "}");
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        builder.append(TAB + "@Override" + NEWLINE);
        builder.append(TAB + "public " + className + " getDelegate() {" + NEWLINE);
        builder.append(TAB + TAB + "return (" + className + ") delegate;" + NEWLINE);
        builder.append(TAB + "}");
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        builder.append(TAB + "@Override" + NEWLINE);
        builder.append(TAB + "public void acceptVisitor(ILanguageVisitor visitor) {" + NEWLINE);
        builder.append(TAB + TAB + "visitor.visit(this);" + NEWLINE);
        builder.append(TAB + "}");
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        builder.append(TAB + "@Override" + NEWLINE);
        builder.append(TAB + "public " + implClassName + " clone() {" + NEWLINE);
        builder.append(TAB + TAB + "return new " + implClassName + "((" + className + ") getDelegate().clone());" + NEWLINE);
        builder.append(TAB + "}" + NEWLINE);
        builder.append("}");
        
        BufferedWriter out = new BufferedWriter(new FileWriter(implClassFile));
        out.write(builder.toString());
        out.close();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        generator.generate();
    }
}
