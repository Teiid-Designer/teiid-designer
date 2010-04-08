/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.procedure;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDConstraint;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDModelGroup;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * @since 5.0.1
 */
public class DocumentGenerator implements
                              CoreStringUtil.Constants {

    // ===========================================================================================================================
    // Constants

    public static final DocumentGenerator SHARED = new DocumentGenerator();

    private static final int INDENT = 4;

    private static final String XML_DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$

    // ===========================================================================================================================
    // Methods

    private void add(StringBuffer document,
                     XsdInstanceNode node,
                     int depth,
                     int declarationIndex,
                     Map namespacesToPrefixes,
                     List<String> paramValues) {
        indentChild(document, depth);
        int attrIndent = document.length();
        document.append('<');
        XSDConcreteComponent comp = node.getResolvedXsdComponent();
        boolean choice = (comp instanceof XSDModelGroup);
        addCommentStartIfChoice(document, choice);
        String pfx;
        if (declarationIndex == 0) {
            addName(document, node, EMPTY_STRING);
            declarationIndex = document.length();
            pfx = updateNamespaceDeclarations(document, node, declarationIndex, namespacesToPrefixes);
        } else {
            pfx = updateNamespaceDeclarations(document, node, declarationIndex, namespacesToPrefixes);
            addName(document, node, pfx);
        }
        attrIndent = document.length() - attrIndent;
        boolean attrAdded = false;
        boolean childAdded = false;
        XsdInstanceNode[] nodes = node.getChildren();
        for (int nodeNdx = 0; nodeNdx < nodes.length; nodeNdx++) {
            XsdInstanceNode child = nodes[nodeNdx];
            XSDConcreteComponent childComp = child.getResolvedXsdComponent();
            if (childComp instanceof XSDAttributeDeclaration) {
                if (attrAdded) {
                    document.append('\n');
                    indent(document, attrIndent);
                } else {
                    attrAdded = true;
                }
                document.append(' ');
                String attrPfx = updateNamespaceDeclarations(document, child, declarationIndex, namespacesToPrefixes);
                addName(document, child, attrPfx);
                document.append("=\""); //$NON-NLS-1$
                addFixedValue(document, childComp);
                document.append('"');
            } else {
                if (!childAdded) {
                    childAdded = true;
                    addCommentEndIfChoice(document, choice);
                    document.append(">\n"); //$NON-NLS-1$
                }
                add(document, child, depth + 1, declarationIndex, namespacesToPrefixes, paramValues);
            }
        }
        if (childAdded) {
            if (comp instanceof XSDFeature) {
                XSDFeature feature = (XSDFeature)comp;
                if (feature.getConstraint() == XSDConstraint.FIXED_LITERAL) {
                    indentChild(document, depth + 1);
                    document.append(feature.getLexicalValue());
                    document.append('\n');
                }
            }
            indentChild(document, depth);
        } else {
            document.append(">"); //$NON-NLS-1$
            
            if (!addFixedValue(document, comp)) {
                if ((paramValues != null) && !paramValues.isEmpty()) {
                    document.append(paramValues.remove(0));
                }
            }
        }
        document.append('<');
        addCommentStartIfChoice(document, choice);
        document.append('/');
        addName(document, node, pfx);
        addCommentEndIfChoice(document, choice);
        document.append(">\n"); //$NON-NLS-1$
    }

    private boolean addFixedValue(StringBuffer document,
                                  XSDConcreteComponent component) {
        if (component instanceof XSDFeature) {
            XSDFeature feature = (XSDFeature)component;
            if (feature.getConstraint() == XSDConstraint.FIXED_LITERAL) {
                document.append(feature.getLexicalValue());
                return true;
            }
        }
        
        return false;
    }

    private void addCommentEndIfChoice(StringBuffer document,
                                       boolean choice) {
        if (choice) {
            document.append(" --"); //$NON-NLS-1$
        }
    }

    private void addCommentStartIfChoice(StringBuffer document,
                                         boolean choice) {
        if (choice) {
            document.append("!-- "); //$NON-NLS-1$
        }
    }

    private void addName(StringBuffer document,
                         XsdInstanceNode node,
                         String prefix) {
        if (prefix.length() > 0 && node.isNamespaceQualifiedInDocument()) {
            document.append(prefix);
            document.append(':');
        }
        document.append(node.getName());
    }

    public String generate(XsdInstanceNode node,
                           List<String> paramValues) {
        StringBuffer doc = new StringBuffer(XML_DECL);
        add(doc, node, 0, 0, new HashMap(), paramValues);
        return doc.toString();
    }

    public String generate(XsdInstanceNode node) {
        List<String> emptyList = Collections.emptyList();
        return generate(node, emptyList);
    }

    private void indentChild(StringBuffer document,
                             int depth) {
        indent(document, depth * INDENT);
    }

    private void indent(StringBuffer buffer,
                        int indent) {
        for (int ndx = indent; --ndx >= 0;) {
            buffer.append(' ');
        }
    }

    private String updateNamespaceDeclarations(StringBuffer document,
                                               XsdInstanceNode node,
                                               int declarationIndex,
                                               Map namespacesToPrefixes) {
        String ns = node.getTargetNamespace();
        String pfx = (String)namespacesToPrefixes.get(ns);
        if (pfx == null) {
            StringBuffer decl = new StringBuffer();
            decl.append(" xmlns"); //$NON-NLS-1$
            int count = namespacesToPrefixes.size();
            if (count == 0) {
                pfx = EMPTY_STRING;
            } else {
                pfx = "ns" + count; //$NON-NLS-1$
                decl.append(':');
                decl.append(pfx);
            }
            decl.append("=\""); //$NON-NLS-1$
            decl.append(ns);
            decl.append('"');
            if (document.length() > declarationIndex && document.charAt(declarationIndex) == ' ') {
                decl.append('\n');
                indent(decl, declarationIndex - XML_DECL.length());
            }
            document.insert(declarationIndex, decl); 
            namespacesToPrefixes.put(ns, pfx);
        }
        return pfx;
    }
}
