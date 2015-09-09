/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.dynamic;

import java.util.List;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.comments.CommentSets;
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
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 */
public class CommentWriter implements Visitor, Commentable, StringConstants {

    private Document document;

    private Node context;

    /**
     * @param document
     */
    public CommentWriter(Document document) {
        this.document = document;
        this.context = child(document, VDB);
    }

    private boolean checkContext(String elementType) {
        if (context == null)
            return false;

        return context.getNodeName().equals(elementType);
    }

    private String attribute(String attrName) {
        if (context == null || context.getAttributes() == null)
            return EMPTY_STRING;

        NamedNodeMap attributes = context.getAttributes();
        Node item = attributes.getNamedItem(attrName);
        return item == null ? EMPTY_STRING : item.getNodeValue();
    }

    private boolean nameAttribute(String name) {
        if (name == null)
            return false;

        return name.equals( attribute(NAME_ATTR));
    }

    private void insertComments(Node followingNode, List<String> comments) {
        if (comments == null)
            return;

        Node parent = followingNode.getParentNode();
        for (String comment : comments) {
            Comment commentNode = document.createComment(comment);
            parent.insertBefore(commentNode, followingNode);
        }
    }

    private Node child(Node parent, String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element && name.equals(child.getNodeName()))
                return child;
        }
        return null;
    }

    @Override
    public void visit(ConditionElement element) {
        if (! checkContext(CONDITION))
            return;

        CommentSets comments = element.getComments();
        insertComments(this.context, comments.getElementCommentSet());
    }

    @Override
    public void visit(DataRoleElement drElement) {
        if (! checkContext(DATA_ROLE))
            return;

        if (! nameAttribute(drElement.getName()))
            return;

        CommentSets commentSets = drElement.getComments();
        List<String> comments = commentSets.getElementCommentSet();
        insertComments(this.context, comments);

        Node drContext = this.context;
        this.context = drContext.getFirstChild();
        while(this.context != null) {

            if (checkContext(DESCRIPTION)) {
                comments = commentSets.getCommentSet(DESCRIPTION);
                insertComments(this.context, comments);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(MAPPED_ROLE_NAME)) {
                comments = commentSets.getCommentSet(MAPPED_ROLE_NAME + HYPHEN + this.context.getTextContent());
                insertComments(this.context, comments);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(PERMISSION)) {
                for (PermissionElement pe : drElement.getPermissions())
                    pe.accept(this);
            }

            this.context = this.context.getNextSibling();
        }

        this.context = drContext;
    }

    @Override
    public void visit(EntryElement element) {
    }

    @Override
    public void visit(ImportVdbElement element) {
        if (! checkContext(IMPORT_VDB))
            return;

        if (! nameAttribute(element.getName()))
            return;

        CommentSets comments = element.getComments();
        insertComments(this.context, comments.getElementCommentSet());
    }

    @Override
    public void visit(MaskElement element) {
        if (! checkContext(MASK))
            return;

        CommentSets comments = element.getComments();
        insertComments(this.context, comments.getElementCommentSet());
    }

    @Override
    public void visit(MetadataElement element) {
        if (! checkContext(METADATA))
            return;

        CommentSets comments = element.getComments();
        insertComments(this.context, comments.getElementCommentSet());
    }

    @Override
    public void visit(ModelElement modelElement) {
        if (! checkContext(MODEL))
            return;

        if (! nameAttribute(modelElement.getName()))
            return;

        CommentSets commentSets = modelElement.getComments();
        List<String> comments = commentSets.getElementCommentSet();
        insertComments(this.context, comments);

        Node modelContext = this.context;
        this.context = modelContext.getFirstChild();
        while(this.context != null) {

            if (checkContext(DESCRIPTION)) {
                comments = commentSets.getCommentSet(DESCRIPTION);
                insertComments(this.context, comments);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(PROPERTY)) {
                for (PropertyElement pe : modelElement.getProperties())
                    pe.accept(this);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(SOURCE)) {
                for (SourceElement se : modelElement.getSources())
                    se.accept(this);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(METADATA)) {
                for (MetadataElement me : modelElement.getMetadata())
                    me.accept(this);
            }

            this.context = this.context.getNextSibling();
        }

        this.context = modelContext;
    }

    @Override
    public void visit(PermissionElement permElement) {
        if (! checkContext(PERMISSION))
            return;

        //
        // Check we have the right permission by checking its
        // resource name sub-element
        //
        String resourceName = null;
        Node permContext = this.context;
        this.context = permContext.getFirstChild();
        while(this.context != null) {
            if (checkContext(RESOURCE_NAME)) {
                resourceName = this.context.getTextContent();
                break;
            }

            this.context = this.context.getNextSibling();
        }
        this.context = permContext;

        if (! permElement.getResourceName().equals(resourceName))
            return;

        CommentSets commentSets = permElement.getComments();
        List<String> comments = commentSets.getElementCommentSet();
        insertComments(this.context, comments);

        this.context = permContext.getFirstChild();
        while(this.context != null) {

            if (checkContext(RESOURCE_NAME)) {
                comments = commentSets.getCommentSet(RESOURCE_NAME);
                insertComments(this.context, comments);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(MASK) && permElement.getMask() != null) {
                permElement.getMask().accept(this);
                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(CONDITION) && permElement.getCondition() != null) {
                permElement.getCondition().accept(this);
                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(ALLOW_CREATE) ||
                checkContext(ALLOW_READ) ||
                checkContext(ALLOW_UPDATE) ||
                checkContext(ALLOW_DELETE) ||
                checkContext(ALLOW_EXECUTE) ||
                checkContext(ALLOW_ALTER) ||
                checkContext(ALLOW_LANGUAGE)) {
                comments = commentSets.getCommentSet(this.context.getNodeName());
                insertComments(this.context, comments);
            }

            this.context = this.context.getNextSibling();
        }
        this.context = permContext;
    }

    @Override
    public void visit(ProblemElement element) {
    }

    @Override
    public void visit(PropertyElement element) {
        if (! checkContext(PROPERTY))
            return;

        if (! nameAttribute(element.getName()))
            return;

        CommentSets comments = element.getComments();
        insertComments(this.context, comments.getElementCommentSet());
    }

    @Override
    public void visit(SourceElement element) {
        if (! checkContext(SOURCE))
            return;

        if (! nameAttribute(element.getName()))
            return;

        CommentSets comments = element.getComments();
        insertComments(this.context, comments.getElementCommentSet());
    }

    @Override
    public void visit(TranslatorElement trElement) {
        if (! checkContext(TRANSLATOR))
            return;

        if (! nameAttribute(trElement.getName()))
            return;

        CommentSets comments = trElement.getComments();
        insertComments(this.context, comments.getElementCommentSet());

        Node trContext = this.context;
        this.context = trContext.getFirstChild();
        while(this.context != null) {

            if (checkContext(PROPERTY)) {
                for (PropertyElement pe : trElement.getProperties())
                    pe.accept(this);
            }

            this.context = this.context.getNextSibling();
        }

        this.context = trContext;
    }

    @Override
    public void visit(VdbElement vdbElement) {
        if (! checkContext(VDB))
            return;

        CommentSets commentSets = vdbElement.getComments();
        List<String> comments = commentSets.getElementCommentSet();
        insertComments(this.context, comments);

        Node vdbContext = this.context;
        this.context = vdbContext.getFirstChild();
        while(this.context != null) {
            if (checkContext(DESCRIPTION) || checkContext(CONNECTION_TYPE)) {
                comments = commentSets.getCommentSet(DESCRIPTION);
                insertComments(this.context, comments);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(PROPERTY)) {
                for (PropertyElement pe : vdbElement.getProperties())
                    pe.accept(this);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(IMPORT_VDB)) {
                for (ImportVdbElement ive : vdbElement.getImportVdbEntries())
                    ive.accept(this);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(MODEL)) {
                for (ModelElement me : vdbElement.getModels())
                    me.accept(this);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(TRANSLATOR)) {
                for (TranslatorElement te : vdbElement.getTranslators())
                    te.accept(this);

                this.context = this.context.getNextSibling();
                continue;
            }

            if (checkContext(DATA_ROLE)) {
                for (DataRoleElement dre : vdbElement.getDataPolicies())
                    dre.accept(this);
            }

            this.context = this.context.getNextSibling();
        }

        this.context = vdbContext;
    }

}
