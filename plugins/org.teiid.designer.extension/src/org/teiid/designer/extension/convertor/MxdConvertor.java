/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.teiid.core.designer.util.ModelType;
import org.teiid.designer.extension.convertor.mxd.MetaclassType;
import org.teiid.designer.extension.convertor.mxd.ModelExtension;
import org.teiid.designer.extension.convertor.mxd.ObjectFactory;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;

/**
 *
 */
public class MxdConvertor {

    private static final String METAMODEL_URI = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$

    private static final String SCHEMA_LOCATION = "http://www.jboss.org/teiiddesigner/ext/2012 http://www.jboss.org/teiiddesigner/ext/2012/modelExtension.xsd"; //$NON-NLS-1$

    private static final String NAMESPACE_URI = "http://www.teiid.org/translator/{NAME}/2014"; //$NON-NLS-1$

    private static MxdConvertor INSTANCE;

    private final ObjectFactory factory = new ObjectFactory();

    /**
     * @return singleton instance
     */
    public static MxdConvertor getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MxdConvertor();

        return INSTANCE;
    }

    private char[] readFromFile(File sourceFile) throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(sourceFile));
            StringBuilder builder = new StringBuilder();
            String ls = System.getProperty("line.separator"); //$NON-NLS-1$

            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(ls);
            }

            return builder.toString().toCharArray();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Reads a teiid annotated source file, ie. containing 'ExtensionMetadataProperty'
     * annotations, parses the annotations and return a collection of {@link MetaclassType}s.
     *
     * @param sourceFile
     * @return collection of {@link MetaclassType}
     * @throws IOException
     */
    public Collection<MetaclassType> read(File sourceFile) throws IOException {
        char[] source = readFromFile(sourceFile);
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source);
        parser.setResolveBindings(true);
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        TranslatorAnnotationVisitor visitor = new TranslatorAnnotationVisitor();
        cu.accept(visitor);

        return visitor.getMetaclasses();
    }

    /**
     * Reads a set of teiid property definitions and returns a
     * collection of {@link MetaclassType}s.
     *
     * @param extensions
     * @return collection of {@link MetaclassType}
     */
    public Collection<MetaclassType> read(Collection<TeiidPropertyDefinition> extensions) {
        TeiidPropertyDefinitionConvertor defnConvertor = new TeiidPropertyDefinitionConvertor();
        return defnConvertor.getMetaclasses(extensions);
    }

    /**
     * @param namespacePrefix name denoting type of data, eg. odata, mongodb
     * @param namespace name defining the namespace URL  eg.  http://www.teiid.org/translator/mongodb/2013
     * @param modelType the type of model represented by the translator
     * @param metaClasses collection of meta classes extracted from a file using {@link #read(File)}
     * @param output the destination output stream of the resulting xml
     *
     * @throws Exception
     */
    public void write(String namespacePrefix, String namespace, ModelType.Type modelType, Collection<MetaclassType> metaClasses, OutputStream output) throws Exception {
        ModelExtension element = factory.createModelExtension();

        // Namespace already included: xmlns="http://www.jboss.org/teiiddesigner/ext/2012">
        element.setMetamodelUri(METAMODEL_URI);

        // version="1"
        element.setVersion(new BigInteger("1")); //$NON-NLS-1$

        // namespacePrefix="odata"
        // namespacePrefix="mongodb"
        element.setNamespacePrefix(namespacePrefix);

        // namespaceUri="http://www.jboss.org/teiiddesigner/ext/odata/2012"
        // namespaceUri="http://www.teiid.org/translator/mongodb/2013"

        String namespaceUri = null;
        if( namespace != null ) {
        	namespaceUri = namespace;
        } else {
        	namespaceUri = NAMESPACE_URI.replace("{NAME}", namespacePrefix); //$NON-NLS-1$
        }
        element.setNamespaceUri(namespaceUri);

        // model type
        element.getModelType().add(modelType.name());

        // adds the meta classes
        element.getExtendedMetaclass().addAll(metaClasses);

        JAXBContext context = JAXBContext.newInstance(ModelExtension.class);        
        Marshaller marshaller = context.createMarshaller();
        // Setting this will auto-add the xsi namespace
        marshaller.setProperty("jaxb.schemaLocation", SCHEMA_LOCATION); //$NON-NLS-1$
        marshaller.setProperty("jaxb.formatted.output",Boolean.TRUE); //$NON-NLS-1$
        marshaller.marshal(element, output);
    }

    /**
     * Convenience method uniting the {@link #read(Collection)} and
     * {@link #write(String, org.teiid.core.designer.util.ModelType.Type, Collection, OutputStream)}
     * functions for a single {@link ITeiidTranslator}.
     *
     * @param translator
     * @param output the destination output stream of the resulting xml
     * @return true if conversion was successful
     *
     * @throws Exception
     */
    public boolean convert(ITeiidTranslator translator, OutputStream output) throws Exception {
        Collection<TeiidPropertyDefinition> extensions = translator.getExtensionPropertyDefinitions();
        if (extensions == null || extensions.isEmpty())
            return false;

        String name = translator.getName();
        // HACK - fixes erroneous excel extension until TEIID-2974 is fixed and available to Designer
        if(name!=null && name.equals("excel")) {  //$NON-NLS-1$
        	for(TeiidPropertyDefinition extDefn : extensions) {
        		String extName = extDefn.getName();
        		if(extName!=null && extName.endsWith("FIRST_DATA_ROW_NUMBER")) { //$NON-NLS-1$
        			extDefn.setOwner("org.teiid.metadata.Table"); //$NON-NLS-1$
        		}
        	}
        }
        String namespace = null;
        if( extensions.size() > 0 ) {
        	String propName = ((TeiidPropertyDefinition)extensions.toArray()[0]).getName();
        	if( propName.startsWith("{http")) {
        		int endIndex = propName.indexOf("}");
        		namespace = propName.substring(1, endIndex);
        	}
        }
        
        try {
            ModelType.Type modelType = ModelType.Type.PHYSICAL;
            Collection<MetaclassType> metaClasses = read(extensions);
            write(name, namespace, modelType, metaClasses, output);
            return true;
        } catch (IllegalArgumentException e) {
            throw new Exception(e);
        }
    }

}
