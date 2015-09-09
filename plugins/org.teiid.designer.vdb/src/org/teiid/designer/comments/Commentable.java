/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.comments;

/**
 * Xml Element that has comments
 */
public interface Commentable {

    String VDB = "vdb";

    String DESCRIPTION = "description";

    String CONNECTION_TYPE = "connection-type";

    String PROPERTY = "property";

    String IMPORT_VDB = "import-vdb";

    String MODEL = "model";

    String SOURCE = "source";

    String METADATA = "metadata";

    String TRANSLATOR = "translator";

    String DATA_ROLE = "data-role";

    String MAPPED_ROLE_NAME = "mapped-role-name";

    String PERMISSION = "permission";

    String RESOURCE_NAME = "resource-name";

    String ALLOW_CREATE = "allow-create";

    String ALLOW_READ = "allow-read";

    String ALLOW_UPDATE = "allow-update";

    String ALLOW_DELETE = "allow-delete";

    String ALLOW_EXECUTE = "allow-execute";

    String ALLOW_ALTER = "allow-alter";

    String CONDITION = "condition";

    String MASK = "mask";

    String ALLOW_LANGUAGE = "allow-language";

    String NAME_ATTR = "name";
}
