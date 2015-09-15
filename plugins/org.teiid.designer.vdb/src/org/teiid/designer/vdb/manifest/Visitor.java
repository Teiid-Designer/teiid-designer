/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.manifest;

/**
 *
 */
public interface Visitor {

    void visit(ConditionElement element);

    void visit(DataRoleElement element);

    void visit(EntryElement element);

    void visit(ImportVdbElement element);

    void visit(MaskElement element);

    void visit(MetadataElement element);

    void visit(ModelElement element);

    void visit(PermissionElement element);

    void visit(ProblemElement element);

    void visit(PropertyElement element);

    void visit(SourceElement element);

    void visit(TranslatorElement element);

    void visit(VdbElement element);

}
