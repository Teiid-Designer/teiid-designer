/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.extension.convertor.mxd.DisplayType;
import org.teiid.designer.extension.convertor.mxd.MetaclassType;
import org.teiid.designer.extension.convertor.mxd.ObjectFactory;
import org.teiid.designer.extension.convertor.mxd.PropertyType;

/**
 *
 */
public class TranslatorAnnotationVisitor extends ASTVisitor implements StringConstants {

    private static final String EXTENSION_METADATA_PROP = "ExtensionMetadataProperty"; //$NON-NLS-1$

    private static final String DESIGNER_PACKAGE = "org.teiid.designer.metamodels.relational.impl"; //$NON-NLS-1$

    private static final String IMPL = "Impl"; //$NON-NLS-1$

    private enum AnnotationProperties {
        APPLICABLE,

        ADVANCED,

        DATATYPE,

        DISPLAY,

        DESCRIPTION,

        REQUIRED;

        /**
         * @return id
         */
        public Object getId() {
            return name().toLowerCase();
        }

        /**
         * @param id
         * @return annotation with id
         */
        public static AnnotationProperties findKey(String id) {
            for (AnnotationProperties property : AnnotationProperties.values()) {
                if (property.getId().equals(id))
                    return property;
            }

            throw new IllegalStateException("Annotation property with id " + id + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Maps the classes named in the applicable property of the Teiid
     * annotations to the designers classes used in the mxd files
     */
    public enum NameMappings {

        /**
         * 
         */
        TABLE,

        /**
         * 
         */
        PROCEDURE,

        /**
         * 
         */
        COLUMN;

        private String applicable;

        private String mapping;

        NameMappings() {
            this.applicable = StringUtilities.upperCaseFirstChar(name().toLowerCase())
                                                                                                    + DOT
                                                                                                    + CLASS;
            this.mapping = DESIGNER_PACKAGE + DOT + this.applicable + IMPL;
        }

        /**
         * @param name
         * @return true is name matches applicable
         */
        public boolean isApplicable(String name) {
            return applicable.equals(name);
        }

        /**
         * @return the mapping
         */
        public String getMapping() {
            return this.mapping;
        }
    }

    private static final String[] validDataTypes = new String[] {
        BigInteger.class.getSimpleName().toLowerCase(),
        BigDecimal.class.getSimpleName().toLowerCase(),
        Blob.class.getSimpleName().toLowerCase(),
        Boolean.class.getSimpleName().toLowerCase(),
        Byte.class.getSimpleName().toLowerCase(),
        char.class.getSimpleName().toLowerCase(),
        Clob.class.getSimpleName().toLowerCase(),
        Date.class.getSimpleName().toLowerCase(),
        Double.class.getSimpleName().toLowerCase(),
        Float.class.getSimpleName().toLowerCase(),
        Integer.class.getSimpleName().toLowerCase(),
        Long.class.getSimpleName().toLowerCase(),
        Object.class.getSimpleName().toLowerCase(),
        short.class.getSimpleName().toLowerCase(),
        String.class.getSimpleName().toLowerCase(),
        Time.class.getSimpleName().toLowerCase(),
        Timestamp.class.getSimpleName().toLowerCase(),
        XML.toLowerCase()
    };

    private class Context {

        private Map<FieldDeclaration, List<PropertyType>> cache = new HashMap<FieldDeclaration, List<PropertyType>>();

        /**
         * @param parent
         * @param propertyType
         */
        public void add(FieldDeclaration parent, PropertyType propertyType) {
            List<PropertyType> properties = cache.get(parent);
            if (properties == null) {
                properties = new ArrayList<PropertyType>();
                cache.put(parent, properties);
            }

            properties.add(propertyType);
        }

        /**
         * @param parent
         * @return properties related to field declaration
         */
        public List<PropertyType> get(FieldDeclaration parent) {
            return cache.get(parent);
        }
    }

    private final ObjectFactory factory = new ObjectFactory();

    private final Context context = new Context();

    private final Map<String, MetaclassType> metaclassTypeMap = new HashMap<String, MetaclassType>();

    /**
     * Should be called after visitor has been used
     *
     * @return meta classes found by this visitor
     */
    public Collection<MetaclassType> getMetaclasses() {
        return metaclassTypeMap.values();
    }
    /*
     * <p:extendedMetaclass name="org.teiid.designer.metamodels.relational.impl.ColumnImpl">
     *   <p:property advanced="false" index="true" masked="false" name="JoinColumn" required="false" type="string">
     *     <p:display locale="en_US">Join Column</p:display>
     *   </p:property>
     *   <p:property advanced="false" index="true" masked="false" name="ComplexType" required="false" type="string">
     *     <p:display locale="en_US">Complex Type</p:display>
     *   </p:property>
     *   <p:property advanced="false" index="true" masked="false" name="ColumnGroup" required="false" type="string">
     *     <p:display locale="en_US">Column Group</p:display>
     *   </p:property>
     * </p:extenclass>
     */
    private boolean isExtMetadataAnnotation(Object node) {
        if (!(node instanceof Annotation))
            return false;

        return EXTENSION_METADATA_PROP.equals(((Annotation)node).getTypeName().toString());
    }

    /**
     * @param string
     * @return
     */
    private boolean toBoolean(String value) {
        if (value == null)
            return false;

        return Boolean.valueOf(value);
    }

    private void validateDataType(String dataType) {
        for (String valid : validDataTypes) {
            if (valid.equals(dataType))
                return;
        }

        throw new IllegalStateException("The data type " + dataType + " is not recognised"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private MetaclassType getMetaclassType(String name) {
        if (name == null)
            throw new IllegalStateException("The MetaclassType name cannot be null"); //$NON-NLS-1$

        MetaclassType metaclassType = metaclassTypeMap.get(name);
        if (metaclassType == null) {
            /*
             * <p:extendedMetaclass name="org.teiid.designer.metamodels.relational.impl.ColumnImpl">
             */
            metaclassType = factory.createMetaclassType();
            if (NameMappings.TABLE.isApplicable(name))
                metaclassType.setName(NameMappings.TABLE.getMapping());
            else if (NameMappings.PROCEDURE.isApplicable(name))
                metaclassType.setName(NameMappings.PROCEDURE.getMapping());
            else if (NameMappings.COLUMN.isApplicable(name))
                metaclassType.setName(NameMappings.COLUMN.getMapping());
            else
                throw new IllegalStateException("Unsupported MetaclassType " + name); //$NON-NLS-1$

            metaclassTypeMap.put(name, metaclassType);
        }

        return metaclassType;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        for (Object modifier : node.modifiers()) {
            if (isExtMetadataAnnotation(modifier)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param node
     * @return
     */
    private Map<AnnotationProperties, String> getAnnotationProperties(NormalAnnotation node) {
        Map<AnnotationProperties, String> valueMap = new HashMap<AnnotationProperties, String>();
        for (Object obj : node.values()) {
            if (!(obj instanceof MemberValuePair))
                continue;

            MemberValuePair annoValuePair = (MemberValuePair)obj;
            String id = annoValuePair.getName().toString();
            AnnotationProperties key = AnnotationProperties.findKey(id);
            valueMap.put(key, annoValuePair.getValue().toString());
        }

        return valueMap;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        if (!isExtMetadataAnnotation(node))
            return false;

        if (!(node.getParent() instanceof FieldDeclaration))
            return false;

        FieldDeclaration parent = (FieldDeclaration)node.getParent();

        Map<AnnotationProperties, String> properties = getAnnotationProperties(node);

        String appProperty = properties.get(AnnotationProperties.APPLICABLE);
        if (appProperty == null)
            throw new IllegalStateException("An annotation " + node.getTypeName() + " does not contain an applicable property"); //$NON-NLS-1$ //$NON-NLS-2$

        String[] appClasses = appProperty.replaceAll("[{|}| ]", EMPTY_STRING).split(COMMA); //$NON-NLS-1$
        for (String appClass : appClasses) {
            MetaclassType metaclassType = getMetaclassType(appClass);
            PropertyType propertyType = factory.createPropertyType();
            metaclassType.getProperty().add(propertyType);

            /*
            *<p:property advanced="false" index="true" masked="false" name="JoinColumn" required="false" type="string">
            *  <p:display locale="en_US">Join Column</p:display>
            *</p:property>
            */
            propertyType.setAdvanced(toBoolean(properties.get(AnnotationProperties.ADVANCED)));
            propertyType.setRequired(toBoolean(properties.get(AnnotationProperties.REQUIRED)));

            String description = properties.get(AnnotationProperties.DESCRIPTION);
            if (description != null) {
                DisplayType descriptionType = factory.createDisplayType();
                descriptionType.setValue(description);
                propertyType.getDescription().add(descriptionType);
            }

            String displayName = properties.get(AnnotationProperties.DISPLAY);
            if (displayName != null) {
                displayName = displayName.replaceAll("\"", EMPTY_STRING); //$NON-NLS-1$
                DisplayType displayType = factory.createDisplayType();
                displayType.setValue(displayName);
                propertyType.getDisplay().add(displayType);
            }

            String dataType = properties.get(AnnotationProperties.DATATYPE);
            if (dataType != null) {
                // Remove .class extension
                dataType = dataType.toLowerCase().split("\\.")[0]; //$NON-NLS-1$
                validateDataType(dataType);
                propertyType.setType(dataType);
            }

            context.add(parent, propertyType);
        }

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        if (!(node.getParent() instanceof FieldDeclaration))
            return false;

        FieldDeclaration parent = (FieldDeclaration) node.getParent();
        List<PropertyType> properties = context.get(parent);
        if (properties == null)
            throw new IllegalStateException("No properties found for the field " + parent.toString()); //$NON-NLS-1$

        String name = node.getInitializer().toString();
        // Teiid tends to have an url prefixed to the literal
        String[] segments = name.split("\\+"); //$NON-NLS-1$
        name = segments[segments.length - 1];
        name = name.replaceAll("[\"| ]", EMPTY_STRING); //$NON-NLS-1$
        
        for(PropertyType property : properties) {
            property.setName(name);
        }

        return false;
    }

    @Override
    public boolean visit(CompilationUnit node) {
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        return true;
    }

    /* *********************** */

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(ArrayAccess node) {
        return false;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        return false;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        return false;
    }

    @Override
    public boolean visit(ArrayType node) {
        return false;
    }

    @Override
    public boolean visit(AssertStatement node) {
        return false;
    }

    @Override
    public boolean visit(Assignment node) {
        return false;
    }

    @Override
    public boolean visit(Block node) {
        return false;
    }

    @Override
    public boolean visit(BlockComment node) {
        return false;
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        return false;
    }

    @Override
    public boolean visit(BreakStatement node) {
        return false;
    }

    @Override
    public boolean visit(CastExpression node) {
        return false;
    }

    @Override
    public boolean visit(CatchClause node) {
        return false;
    }

    @Override
    public boolean visit(CharacterLiteral node) {
        return false;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        return false;
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        return false;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        return false;
    }

    @Override
    public boolean visit(ContinueStatement node) {
        return false;
    }

    @Override
    public boolean visit(DoStatement node) {
        return false;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        return false;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        return false;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        return false;
    }

    @Override
    public boolean visit(FieldAccess node) {
        return false;
    }

    @Override
    public boolean visit(ForStatement node) {
        return false;
    }

    @Override
    public boolean visit(IfStatement node) {
        return false;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(InfixExpression node) {
        return false;
    }

    @Override
    public boolean visit(InstanceofExpression node) {
        return false;
    }

    @Override
    public boolean visit(Initializer node) {
        return false;
    }

    @Override
    public boolean visit(LabeledStatement node) {
        return false;
    }

    @Override
    public boolean visit(LineComment node) {
        return false;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        return false;
    }

    @Override
    public boolean visit(MemberRef node) {
        return false;
    }

    @Override
    public boolean visit(MemberValuePair node) {
        return false;
    }

    @Override
    public boolean visit(MethodRef node) {
        return false;
    }

    @Override
    public boolean visit(MethodRefParameter node) {
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        return false;
    }

    @Override
    public boolean visit(Modifier node) {
        return false;
    }

    @Override
    public boolean visit(NullLiteral node) {
        return false;
    }

    @Override
    public boolean visit(NumberLiteral node) {
        return false;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(ParameterizedType node) {
        return false;
    }

    @Override
    public boolean visit(ParenthesizedExpression node) {
        return false;
    }

    @Override
    public boolean visit(PostfixExpression node) {
        return false;
    }

    @Override
    public boolean visit(PrefixExpression node) {
        return false;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        return false;
    }

    @Override
    public boolean visit(QualifiedName node) {
        return false;
    }

    @Override
    public boolean visit(QualifiedType node) {
        return false;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        return false;
    }

    @Override
    public boolean visit(SimpleName node) {
        return false;
    }

    @Override
    public boolean visit(SimpleType node) {
        return false;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        return false;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(StringLiteral node) {
        return false;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        return false;
    }

    @Override
    public boolean visit(SuperFieldAccess node) {
        return false;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        return false;
    }

    @Override
    public boolean visit(SwitchCase node) {
        return false;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        return false;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        return false;
    }

    @Override
    public boolean visit(TagElement node) {
        return false;
    }

    @Override
    public boolean visit(TextElement node) {
        return false;
    }

    @Override
    public boolean visit(ThisExpression node) {
        return false;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        return false;
    }

    @Override
    public boolean visit(TryStatement node) {
        return false;
    }

    @Override
    public boolean visit(TypeDeclarationStatement node) {
        return false;
    }

    @Override
    public boolean visit(TypeLiteral node) {
        return false;
    }

    @Override
    public boolean visit(TypeParameter node) {
        return false;
    }

    @Override
    public boolean visit(UnionType node) {
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        return false;
    }

    @Override
    public boolean visit(WhileStatement node) {
        return false;
    }

    @Override
    public boolean visit(WildcardType node) {
        return false;
    }
}
