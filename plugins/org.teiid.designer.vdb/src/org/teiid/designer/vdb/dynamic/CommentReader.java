/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.comments.Commentable;
import org.teiid.designer.vdb.manifest.ConditionElement;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ImportVdbElement;
import org.teiid.designer.vdb.manifest.MaskElement;
import org.teiid.designer.vdb.manifest.MetadataElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PermissionElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.SourceElement;
import org.teiid.designer.vdb.manifest.TranslatorElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.teiid.designer.vdb.manifest.Visitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Process comments and append them to the {@link VdbElement} manifest
 */
public class CommentReader extends DefaultHandler2 implements Commentable, StringConstants {

    private static class ManifestVisitor implements Visitor {

        private MyElement context;

        /**
         * @param context
         */
        public ManifestVisitor(MyElement context) {
            this.context = context;
            while (this.context.parent() != null) {
                this.context = this.context.parent();
            }
        }

        private boolean checkContext(String elementType) {
            if (context == null)
                return false;

            return context.type().equals(elementType);
        }

        private String attribute(String attrName) {
            if (context == null || context.attributes() == null)
                return EMPTY_STRING;

            return context.attributes().getProperty(attrName);
        }

        private boolean nameAttribute(String name) {
            if (name == null)
                return false;

            return name.equals(attribute(NAME_ATTR));
        }

        @Override
        public void visit(ConditionElement element) {
            if (!checkContext(CONDITION))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());
        }

        @Override
        public void visit(DataRoleElement drElement) {
            if (!checkContext(DATA_ROLE))
                return;

            if (!nameAttribute(drElement.getName()))
                return;

            drElement.getComments().addCommentSet(EMPTY_STRING, context.comments());

            MyElement drContext = this.context;
            for (MyElement child : drContext.children()) {
                this.context = child;

                if (checkContext(DESCRIPTION)) {
                    drElement.getComments().addCommentSet(context.type(), context.comments());
                    continue;
                }

                if (checkContext(MAPPED_ROLE_NAME)) {
                    drElement.getComments().addCommentSet(context.type() + HYPHEN + context.value(), context.comments());
                    continue;
                }

                if (checkContext(PERMISSION)) {
                    for (PermissionElement pe : drElement.getPermissions())
                        pe.accept(this);
                }
            }

            this.context = drContext;
        }

        @Override
        public void visit(EntryElement element) {
            // Not Required
        }

        @Override
        public void visit(ImportVdbElement element) {
            if (!checkContext(IMPORT_VDB))
                return;

            if (!nameAttribute(element.getName()))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());
        }

        @Override
        public void visit(MaskElement element) {
            if (!checkContext(MASK))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());
        }

        @Override
        public void visit(MetadataElement element) {
            if (!checkContext(METADATA))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());
        }

        @Override
        public void visit(ModelElement modelElement) {
            if (!checkContext(MODEL))
                return;

            if (!nameAttribute(modelElement.getName()))
                return;

            modelElement.getComments().addCommentSet(EMPTY_STRING, context.comments());

            MyElement modelContext = this.context;
            for (MyElement child : modelContext.children()) {
                this.context = child;

                if (checkContext(DESCRIPTION)) {
                    modelElement.getComments().addCommentSet(context.type(), context.comments());
                    continue;
                }

                if (checkContext(PROPERTY)) {
                    for (PropertyElement pe : modelElement.getProperties())
                        pe.accept(this);

                    continue;
                }

                if (checkContext(SOURCE)) {
                    for (SourceElement se : modelElement.getSources())
                        se.accept(this);

                    continue;
                }

                if (checkContext(METADATA)) {
                    for (MetadataElement me : modelElement.getMetadata())
                        me.accept(this);

                    continue;
                }
            }

            this.context = modelContext;
        }

        @Override
        public void visit(PermissionElement element) {
            if (!checkContext(PERMISSION))
                return;

            String resourceName = null;
            MyElement permContext = this.context;
            for (MyElement child : context.children()) {
                this.context = child;

                if (checkContext(RESOURCE_NAME)) {
                    resourceName = child.value();
                    break;
                }
            }
            this.context = permContext;

            if (!element.getResourceName().equals(resourceName))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());

            permContext = this.context;
            for (MyElement child : context.children()) {
                this.context = child;

                if (checkContext(RESOURCE_NAME)) {
                    element.getComments().addCommentSet(RESOURCE_NAME, context.comments());
                    continue;
                }

                if (checkContext(MASK)) {
                    element.getMask().accept(this);
                    continue;
                }

                if (checkContext(CONDITION)) {
                    element.getCondition().accept(this);
                    continue;
                }

                if (checkContext(ALLOW_CREATE) || checkContext(ALLOW_READ) || checkContext(ALLOW_UPDATE)
                    || checkContext(ALLOW_DELETE) || checkContext(ALLOW_EXECUTE) || checkContext(ALLOW_ALTER)
                    || checkContext(ALLOW_LANGUAGE)) {
                    element.getComments().addCommentSet(context.type(), context.comments());
                }
            }
            this.context = permContext;

        }

        @Override
        public void visit(ProblemElement element) {
            // Not Required
        }

        @Override
        public void visit(PropertyElement element) {
            if (!checkContext(PROPERTY))
                return;

            if (!nameAttribute(element.getName()))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());
        }

        @Override
        public void visit(SourceElement element) {
            if (!checkContext(SOURCE))
                return;

            if (!nameAttribute(element.getName()))
                return;

            element.getComments().addCommentSet(EMPTY_STRING, context.comments());
        }

        @Override
        public void visit(TranslatorElement trElement) {
            if (!checkContext(TRANSLATOR))
                return;

            if (!nameAttribute(trElement.getName()))
                return;

            trElement.getComments().addCommentSet(EMPTY_STRING, context.comments());

            MyElement trContext = this.context;
            for (MyElement child : trContext.children()) {
                this.context = child;

                if (checkContext(PROPERTY)) {
                    for (PropertyElement pe : trElement.getProperties())
                        pe.accept(this);
                }
            }

            this.context = trContext;
        }

        @Override
        public void visit(VdbElement vdbElement) {
            if (!checkContext(VDB))
                return;

            vdbElement.getComments().addCommentSet(EMPTY_STRING, context.comments());

            MyElement vdbContext = this.context;
            for (MyElement child : vdbContext.children()) {
                this.context = child;

                if (checkContext(DESCRIPTION) || checkContext(CONNECTION_TYPE)) {
                    vdbElement.getComments().addCommentSet(context.type(), context.comments());
                    continue;
                }

                if (checkContext(PROPERTY)) {
                    for (PropertyElement pe : vdbElement.getProperties())
                        pe.accept(this);

                    continue;
                }

                if (checkContext(IMPORT_VDB)) {
                    for (ImportVdbElement ive : vdbElement.getImportVdbEntries())
                        ive.accept(this);

                    continue;
                }

                if (checkContext(MODEL)) {
                    for (ModelElement me : vdbElement.getModels())
                        me.accept(this);

                    continue;
                }

                if (checkContext(TRANSLATOR)) {
                    for (TranslatorElement te : vdbElement.getTranslators())
                        te.accept(this);

                    continue;
                }

                if (checkContext(DATA_ROLE)) {
                    for (DataRoleElement dre : vdbElement.getDataPolicies())
                        dre.accept(this);

                    continue;
                }
            }

            this.context = vdbContext;
        }
    }

    private static class MyElement {

        private String type;

        private String value;

        private int index;

        private Properties attributes = new Properties();

        private List<String> comments;

        private MyElement parent;

        private List<MyElement> children;

        /**
         * @param type
         * @param parent
         */
        public MyElement(String type, MyElement parent) {
            this.type = type;
            this.parent = parent;

            if (this.parent != null)
                parent.addChild(this);
        }

        public List<MyElement> children() {
            return Collections.unmodifiableList(children);
        }

        private void addChild(MyElement child) {
            if (children == null)
                children = new ArrayList<MyElement>();

            children.add(child);

            int index = 0;
            for (MyElement kid : children) {
                if (kid == child) {
                    kid.setIndex(index);
                    break; // Should be at the end of the list anyway
                }

                if (kid.type().equals(child.type()))
                    index++;
            }

            child.setParent(this);
        }

        /**
         * @return the parent
         */
        public MyElement parent() {
            return this.parent;
        }

        /**
         * @param parent the parent to set
         */
        private void setParent(MyElement parent) {
            this.parent = parent;
        }

        /**
         * @return type
         */
        public String type() {
            return type;
        }

        /**
         * @return index
         */
        public int index() {
            return index;
        }

        /**
         * @param index the index to set
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * @return the value
         */
        public String value() {
            return this.value;
        }

        /**
         * @param value
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * @return the attributes
         */
        public Properties attributes() {
            return this.attributes;
        }

        /**
         * @param attributes
         */
        public void setAttributes(Attributes attributes) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getQName(i);
                String value = attributes.getValue(i);
                this.attributes.put(name, value);
            }
        }

        /**
         * @return the comments
         */
        public List<String> comments() {
            if (this.comments == null)
                return Collections.emptyList();

            return this.comments;
        }

        /**
         * @param comments
         */
        public void setComments(List<String> comments) {
            this.comments = new ArrayList<String>();
            this.comments.addAll(comments);
        }
    }

    private final VdbElement manifest;

    private Stack<String> comments = new Stack<String>();

    private MyElement currentElement;

    /**
     * @param manifest
     */
    public CommentReader(VdbElement manifest) {
        this.manifest = manifest;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length);

        if (value.length() == 0)
            return; // ignore white space

        comments.push(value);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String elementType = (localName == null || localName.isEmpty()) ? qName : localName;

        currentElement = new MyElement(elementType, currentElement);
        currentElement.setAttributes(attributes);

        // New element encountered to scoop up the preceding comments
        currentElement.setComments(comments);
        comments.clear();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        currentElement.setValue(value);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentElement == null)
            return;

        if (currentElement.parent() != null)
            currentElement = currentElement.parent();
    }

    /**
     * Process the given xml file and add comments to the manifest
     *
     * @param xmlFile
     * @throws Exception
     */
    public void read(File xmlFile) throws Exception {
        FileInputStream xmlStream = null;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            xmlStream = new FileInputStream(xmlFile);

            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", this); //$NON-NLS-1$
            saxParser.parse(xmlStream, this);

            // Done parsing and completed a tree of MyElements
            // Now visit the manifest using the tree as a context.
            ManifestVisitor visitor = new ManifestVisitor(currentElement);
            manifest.accept(visitor);

        } finally {
            if (xmlStream != null)
                xmlStream.close();
        }
    }

}
