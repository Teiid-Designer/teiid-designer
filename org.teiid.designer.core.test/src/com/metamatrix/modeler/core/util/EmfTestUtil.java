/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import com.metamatrix.core.util.CoreArgCheck;

/**
 * @since 3.1
 * @version 3.1
 */
public class EmfTestUtil {
    //############################################################################################################################
    //# Constants                                                                                                                #
    //############################################################################################################################
    

    //############################################################################################################################
    //# Static Methods                                                                                                           #
    //############################################################################################################################

    /**
     * Return the EcorePackage object, from which various built-in data types can be obtained.
     */
    public static EcorePackage getEcorePackage() {
        return EcorePackageImpl.init();
    }
    
//    public static Object getKeyForFirstClass( final ContainerImpl container ) {
//        Object key = null;
//        int count = 0;
//        for (Iterator resources = container.getResources().iterator(); resources.hasNext();) {
//            Resource resource = (Resource) resources.next();
//            Iterator iter2 = resource.getAllContents();
//            while (iter2.hasNext() && key == null) {
//                EObject next = (EObject)iter2.next();
//                if (next instanceof Proxy ) {
//                    ProxyHandler handler = (ProxyHandler)Proxy.getInvocationHandler(next);
//                    if(count++ > 0){
//                        key = handler.getKey();
//                    }
//                }
//            }
//        }
//        
//        return key;
//    }
    
    /**
     * Searches through the methods of the eObject looking for a set method that
     * maps to a Structural Feature on the eClass.  The set method must take
     * only a single string as a parameter.  If no regular set method found.
     * Look for the regular eSet(StructuralFeature, Object) method
     * @param eClass
     * @return EObject
     */
    public static  Method findSetMethod(EObject eObject){
        return findSetMethod(eObject, 1);
    }
    
    /**
     * Searches through the methods of the eObject looking for a set method that
     * maps to a Structural Feature on the eClass.  The set method must take
     * only a single string as a parameter.  Returns the nth match the meets all
     * criteria.  If no regular set method found. Look for the regular
     * eSet(StructuralFeature, Object) method
     * @param eClass
     * @return EObject
     */
    public static  Method findSetMethod(EObject eObject, int n){
        final Method[] methods = eObject.getClass().getMethods();
        int count = 0;
        for(int i = 0; i < methods.length; i++){
            Method temp = methods[i];
            if(temp.getReturnType() == Void.TYPE || temp.getReturnType() == NotificationChain.class){
                if(temp.getName().startsWith("set")  ){ //$NON-NLS-1$
                    if(temp.getParameterTypes().length == 1 && temp.getParameterTypes()[0] == String.class){
                        if(++count == n){
                            return temp;
                        }
                    }
                } 
            }
        }
        
        for(int i = 0; i < methods.length; i++){
            Method temp = methods[i];
            if(temp.getReturnType() == Void.TYPE || temp.getReturnType() == NotificationChain.class){
                if(temp.getName().equals("eSet") ){ //$NON-NLS-1$
                    return temp;
                } 
            }
        }
        
        return null;
    }
    
    /**
     * Searches through the StructuralFeatures of the eClass looking for one
     * that has a String data type.  Returns the nth match the meets the
     * criteria.
     * @return EStructuralFeature
     */
    public static EStructuralFeature findStringSF(EObject eObject, int n){
        int count = 0;
        for (Iterator iter = eObject.eClass().getEAllStructuralFeatures().iterator(); iter.hasNext();) {
            EStructuralFeature element = (EStructuralFeature) iter.next();
            if(element.getEType() != null && element.getEType().getName().equals("EString") ){ //$NON-NLS-1$
                if(++count == n){
                    return element;
                }
            }            
        }
        
        return null;
    }
    
    /**
     * Searches through the StructuralFeatures of the eClass looking for one
     * that has a String data type.
     * @return EStructuralFeature
     */
    public static EStructuralFeature findStringSF(EObject eObject){
        return findStringSF(eObject, 1);
    }

    
    /**
     * Return the name of the class removing the package qualification.
     * @param obj the Object whose class name is being returned
     * @return String the class name or an empty string if the
     * object reference is null.
     */
    public static String getClassName(final Object obj) {
        if (obj == null) {
            return ""; //$NON-NLS-1$
        }
        String className = obj.getClass().getName();
        int beginIndex = 0;
        if (className.lastIndexOf('.') > 0) {
            beginIndex = className.lastIndexOf('.')+1;
        }
        return className.substring(beginIndex,className.length());
    }
    
    public static String getName(final EObject obj) {
        if (obj == null) {
            return ""; //$NON-NLS-1$
        }
        if (obj instanceof ENamedElement) {
            return ((ENamedElement)obj).getName();
        }
        return null;
    }
    
    public static List getEObjectsByClassName(final Resource resrc, final String className) {
        CoreArgCheck.isNotNull(resrc);
        CoreArgCheck.isNotNull(className);

        final List result = new ArrayList();
        for (Iterator iter = resrc.getAllContents(); iter.hasNext();) {
            final EObject eObj = (EObject) iter.next();
            if (eObj != null && className.equals(getClassName(eObj))) {
                result.add(eObj);
            }
        }
        return result;
    }
    
    public static List getEObjectsByName(final Resource resrc, final String objName) {
        CoreArgCheck.isNotNull(resrc);
        CoreArgCheck.isNotNull(objName);
        return getEObjectsByName(resrc.getAllContents(),objName);
    }
    public static List getEObjectsByName(final Iterator iter, final String objName) {
        CoreArgCheck.isNotNull(iter);
        CoreArgCheck.isNotNull(objName);

        final List result = new ArrayList();
        while (iter.hasNext()) {
            final EObject eObj = (EObject) iter.next();
            if (eObj != null && objName.equals(getName(eObj))) {
                result.add(eObj);
            }
        }
        return result;
    }
    
    public static EStructuralFeature getEStructuralFeatureByName( final EObject container, final String objName ) {
        CoreArgCheck.isNotNull(container);
        CoreArgCheck.isNotNull(objName);

        final Iterator iter = container.eContents().iterator();
        while (iter.hasNext()) {
            final EObject eObj = (EObject) iter.next();
            if (eObj != null && eObj instanceof EStructuralFeature && objName.equals(getName(eObj))) {
                return (EStructuralFeature)eObj;
            }
        }
        return null;
    }
    public static List getEObjectsByInstance(final Resource resrc, final Class targetClass) {
        CoreArgCheck.isNotNull(resrc);
        CoreArgCheck.isNotNull(targetClass);

        final List result = new ArrayList();
        for (Iterator iter = resrc.getAllContents(); iter.hasNext();) {
            final EObject eObj = (EObject) iter.next();
            if (eObj != null && targetClass.isInstance(eObj)) {
                result.add(eObj);
            }
        }
        return result;
    }

    /**
     * Prints the specified description to System.err, followed by the contents of the specified Resource.
     * @param resource    The Resource instance to print; may not be null.
     * @param description The description to be printed before the contents.
     * @since 3.1
     */
    public static void printContents(final Resource resource, final String description) {
        printContents(resource, System.out, description);
    }

    /**
     * Print the contents of the specified EMF Resource
     * @param resrc the Resource instance to print; may not be null.
     * @param stream the PrintStream to use; may not be null.
     * @param desc a description to be printed with the contents
     * @since 3.1
     */
    public static void printContents(final Resource resrc, final PrintStream stream, final String desc) {
        CoreArgCheck.isNotNull(resrc);
        CoreArgCheck.isNotNull(stream);

        printDescription(desc, stream);
        stream.println("Resource URI= "+resrc.getURI()); //$NON-NLS-1$
        final Iterator iter = resrc.getAllContents();
        while (iter.hasNext()) {
            final EObject eObj = (EObject) iter.next();
            stream.println("EObject ("+getClassName(eObj)+") URIFragment= "+resrc.getURIFragment(eObj)); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Print the contents of the specified EList
     * @param eList the EList instance to print; may not be null.
     * @param stream the PrintStream to use; may not be null.
     * @param desc a description to be printed with the contents
     * @since 3.1
     */
    public static void printContents(final List eList, final PrintStream stream, final String desc) {
        CoreArgCheck.isNotNull(eList);
        CoreArgCheck.isNotNull(stream);

        printDescription(desc, stream);
        final Iterator iter = eList.iterator();
        while (iter.hasNext()) {
        	final Object obj = iter.next();
        	if (obj instanceof EObject) {
	            stream.println("EObject ("+getClassName(obj)+") = "+obj); //$NON-NLS-1$ //$NON-NLS-2$
        	} else if (obj instanceof Resource) {
	            stream.println("Resource ("+getClassName(obj)+") = "+obj); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        }
    }

    /**
     * Print the contents of the specified EClass
     * @param eClass the EClass instance to print; may not be null.
     * @param stream the PrintStream to use; may not be null.
     * @param desc a description to be printed with the contents
     * @param includeInherited if true all inherited contents will
     * also be printed
     * @since 3.1
     */
    public static void printContents(final EClass eClass, final PrintStream stream, 
    							       final String desc, boolean includeInherited) {
        CoreArgCheck.isNotNull(eClass);
        CoreArgCheck.isNotNull(stream);

        printDescription(desc, stream);
		stream.println("EClass name: "+eClass.getName()); //$NON-NLS-1$
		
		stream.println("  EClass attributes: "); //$NON-NLS-1$
		List contents = (includeInherited ? eClass.getEAllAttributes() : eClass.getEAttributes());
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            final EAttribute child = (EAttribute) iter.next();
            stream.println("    " + child.getName() + " [" + child.getLowerBound() + "," + child.getUpperBound() + "], featureID="+child.getFeatureID()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        
        stream.println("  EClass references: "); //$NON-NLS-1$
        contents = (includeInherited ? eClass.getEAllReferences() : eClass.getEReferences());
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            final EReference child = (EReference) iter.next();
            stream.println("    " + child.getName() + " [" + child.getLowerBound() + "," + child.getUpperBound() + "], featureID="+child.getFeatureID()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }

        stream.println("  EClass operations: "); //$NON-NLS-1$
        contents = (includeInherited ? eClass.getEAllOperations() : eClass.getEOperations());
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            final EOperation child = (EOperation) iter.next();
            stream.println("    " + child.getName()); //$NON-NLS-1$
        }
        
    }
	
    /**
     * Print the feature and values contained by the specified EObject
     * @param eObject the EObject instance to print; may not be null.
     * @param stream the PrintStream to use; may not be null.
     * @param desc a description to be printed with the contents
     * @param includeInherited if true all inherited contents will
     * also be printed
     * @since 3.1
     */
    public static void printFeatures(final EObject eObject, final PrintStream stream, 
    							       final String desc, boolean includeInherited) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(stream);

        printDescription(desc, stream);
		EClass eClass = eObject.eClass();
		stream.println("EClass name: "+eClass.getName()); //$NON-NLS-1$
		
		stream.println("  EClass attributes: "); //$NON-NLS-1$
		List contents = eClass.getEAllAttributes();
		for (Iterator iter = contents.iterator(); iter.hasNext();) {
			EAttribute feature = (EAttribute) iter.next();
            stream.println("    " + feature.getName() + " = " + eObject.eGet(feature)); //$NON-NLS-1$ //$NON-NLS-2$
        }
		
		stream.println("  EClass references: "); //$NON-NLS-1$
		contents = eClass.getEAllReferences();
		for (Iterator iter = contents.iterator(); iter.hasNext();) {
			EReference feature = (EReference) iter.next();
            stream.println("    " + feature.getName() + " = " + eObject.eGet(feature)); //$NON-NLS-1$ //$NON-NLS-2$
        }
	}

    /**
     * @since 3.1
     */
    private static void printDescription(final String description, final PrintStream stream) {
        if (description != null  &&  description.length() != 0) {
            stream.println(description);
        }
    }

    /**
     * Prints the specified node to the specified stream, prepending the specified indent to the output.
     * @param node        The node to print; may not be null.
     * @param indent      The indent to print before the node.
     * @param stream      The PrintStream to use; may not be null.
     * @since 3.1
     */
    private static void printNode(final Object node, String indent, final PrintStream stream) {
        stream.print(indent);
        if (node instanceof ResourceSet) {
            final ResourceSet resrcSet = (ResourceSet)node;
            stream.println(resrcSet.getClass().getName());
        } else if (node instanceof Resource) {
            final Resource resrc = (Resource)node;
            stream.println(resrc.getClass().getName() + ": uri=" + resrc.getURI()); //$NON-NLS-1$
        } else {
            final EObject obj = (EObject)node;
            final EClass objClass = obj.eClass();
            stream.println(objClass.getName());
            final EObject ctnr = obj.eContainer();
            if (ctnr != null) {
                stream.println(indent + "-> container=" + ctnr.eClass().getName()); //$NON-NLS-1$
            }
        }
    }

    /**
     * Prints the specified node and all of its children to the specified stream, prepending the specified indent to the output.
     * The indent will be increased as each level of the node's children are printed.
     * @param node        The node to print; may not be null.
     * @param indent      The indent to print before the node.
     * @param stream      The PrintStream to use; may not be null.
     * @since 3.1
     */
    private static void printSubTree(final Object node, String indent, final PrintStream stream) {
        printNode(node, indent, stream);
        indent += "  "; //$NON-NLS-1$
        EList list;
        if (node instanceof ResourceSet) {
            list = ((ResourceSet)node).getResources();
        } else if (node instanceof Resource) {
            list = ((Resource)node).getContents();
        } else {
            list = ((EObject)node).eContents();
        }
        for (final Iterator iter = list.iterator();  iter.hasNext();) {
            printSubTree(iter.next(), indent, stream);
        }
    }

    /**
     * Prints the specified description to System.err, followed by the contents of the tree with the specified root node.  The
     * output will be indented relative to the level being printed.
     * @param root        The root node of the tree to print; may not be null.
     * @param description The description to be printed before the contents.
     * @since 3.1
     */
    public static void printTree(final Object root, final String description) {
        printTree(root, System.err, description);
    }

    /**
     * Prints the specified description to the specified stream, followed by the contents of the tree with the specified root
     * node.  The output will be indented relative to the level being printed.
     * @param root        The root node of the tree to print; may not be null.
     * @param stream      The PrintStream to use; may not be null.
     * @param description The description to be printed before the contents.
     * @since 3.1
     */
    public static void printTree(final Object root, final PrintStream stream, final String description) {
        printDescription(description, stream);
        printSubTree(root, "", stream); //$NON-NLS-1$
    }
    
    /**
     * Builds a dummy model based on the library scenario, creating a resource set.
     * DOES NOT REQUIRE THE PLUGIN ENVIRONMENT TO CREATE THE RESOURCE SET
     * @param displayContents - if true, the model structure will be printed out to the console after it is built
     * @return ResourceSet - the dummy resource set.
     */
    public static ResourceSet genModelAsResourceSet(boolean displayContents) {
        EPackage ePackage = generateMetamodel(displayContents);

        // Create the ResourceSet and save the resource
        final ResourceSet resources = new ResourceSetImpl();
        final ResourceImpl resrc = new ResourceImpl();
        resrc.getContents().add(ePackage);
        helpCheckResource(resrc, displayContents);
        resources.getResources().add(resrc);
        
        return resources;    
    }
    
    /**
     * Builds a dummy model based on the library scenario, creating a resource set.
     * DOES NOT REQUIRE THE PLUGIN ENVIRONMENT TO CREATE THE RESOURCE SET
     * @param displayContents - if true, the model structure will be printed out to the console after it is built
     * @return ResourceSet - the dummy resource set.
     */
    public static EPackage generateMetamodel(boolean displayContents) {
        final String nsURI    = "http://www.metamatrix.com/metabase/3.1/metamodels/Library.xml"; //$NON-NLS-1$
        final String nsPrefix = "Library"; //$NON-NLS-1$

        // Create the package for the model
        EPackage ePackage = createEPackage(null,"library",nsURI,nsPrefix); //$NON-NLS-1$
        EPackage.Registry.INSTANCE.put(nsURI,ePackage);

        // Create necessary data types
        EDataType myString = createEDataType(ePackage,"myString",java.lang.String.class); //$NON-NLS-1$
        EDataType myInt    = createEDataType(ePackage,"myInt",java.lang.Integer.class); //$NON-NLS-1$

        // Create classifiers for the model
        EClass book    = createEClass(ePackage,"Book",false,false); //$NON-NLS-1$
        EClass library = createEClass(ePackage,"Library",false,false); //$NON-NLS-1$
        EClass writer  = createEClass(ePackage,"Writer",false,false); //$NON-NLS-1$

        // Create enumeration for the model
        EEnum bookCategory = createEEnum(ePackage,"BookCategory"); //$NON-NLS-1$

        // Create enumeration literals for the model
        addEnumLiteral(bookCategory,createEnumLiteral("MYSTERY")); //$NON-NLS-1$
        addEnumLiteral(bookCategory,createEnumLiteral("SCIENCE_FICTION")); //$NON-NLS-1$
        addEnumLiteral(bookCategory,createEnumLiteral("BIOGRAPHY")); //$NON-NLS-1$

        // Add the attributes to the book class
        createEAttribute(book,"title",myString); //$NON-NLS-1$
        createEAttribute(book,"pages",myInt); //$NON-NLS-1$
        createEAttribute(book,"category",bookCategory); //$NON-NLS-1$

        // Add the attributes to the writer class
        createEAttribute(writer,"name",myString); //$NON-NLS-1$

        // Add the attributes to the library class
        createEAttribute(library,"name",myString); //$NON-NLS-1$
        
        // Create a two-way reference between books and writers in which
        // a book has only one reference to a writer and a writer
        // has zero or more references to books
        EReference books  = createEReference("books",book,0,ETypedElement.UNBOUNDED_MULTIPLICITY,false); //$NON-NLS-1$
        EReference author = createEReference("author",writer,1,1,false); //$NON-NLS-1$
        author.setEOpposite(books);
        books.setEOpposite(author);
        book.getEReferences().add(author);
        writer.getEReferences().add(books);

        // Create a containment reference in which a library contains
        // one or more books
        EReference libBooks = createEReference("books",book,1,ETypedElement.UNBOUNDED_MULTIPLICITY,true); //$NON-NLS-1$
        library.getEReferences().add(libBooks);

        // Create a containment reference in which a library contains
        // one or more writers
        EReference writers = createEReference("writers",writer,1,ETypedElement.UNBOUNDED_MULTIPLICITY,true); //$NON-NLS-1$
        library.getEReferences().add(writers);
        
        return ePackage;
    }

	public static EPackage createEPackage(Object parent, String name, String nsURI, String nsPrefix) {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName(name);
		if (nsPrefix != null && nsPrefix.length() != 0) {
    		ePackage.setNsPrefix(nsPrefix);
		}
		if (nsURI != null && nsURI.length() != 0) {
    		ePackage.setNsURI(nsURI);
		}
		if (parent instanceof EPackage) {
			 ((EPackage) parent).getESubpackages().add(ePackage);
		} else if (parent instanceof EList) {
			((EList) parent).add(ePackage);
		}
		return ePackage;
	}

	public static EClass createEClass(Object parent, String name, boolean isAbstract, boolean isInterface) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		eClass.setAbstract(isAbstract);
		eClass.setInterface(isInterface);
		if (parent instanceof EPackage) {
			((EPackage) parent).getEClassifiers().add(eClass);
		} else if (parent instanceof EList) {
			((EList) parent).add(eClass);
		}
		return eClass;
	}

	public static EOperation createEOperation(Object parent, String name) {
		EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
		eOperation.setName(name);
		if (parent instanceof EClass) {
			((EClass) parent).getEOperations().add(eOperation);
		} else if (parent instanceof EList) {
			((EList) parent).add(eOperation);
		}
		return eOperation;
	}
    
    public static EParameter createEParameter(Object parent, String name) {
        EParameter eParameter = EcoreFactory.eINSTANCE.createEParameter();
        eParameter.setName(name);
        if (parent instanceof EOperation) {
            ((EOperation) parent).getEParameters().add(eParameter);
        } else if (parent instanceof EList) {
            ((EList) parent).add(eParameter);
        }
        return eParameter;
    }

	public static EAttribute createEAttribute(Object parent, String name, EDataType eType) {
		EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		eAttribute.setName(name);
		eAttribute.setEType(eType);
		if (parent instanceof EClass) {
			((EClass) parent).getEAttributes().add(eAttribute);
		} else if (parent instanceof EList) {
			((EList) parent).add(eAttribute);
		}
		return eAttribute;
	}

	public static EAttribute createEAttribute(Object parent, String name, EDataType eType, int upperBound, int lowerBound, boolean modifiable, boolean nullable) {
		EAttribute eAttribute = createEAttribute(parent,name,eType);
		eAttribute.setLowerBound(lowerBound);
		eAttribute.setUpperBound(upperBound);
		eAttribute.setChangeable(modifiable);
		eAttribute.setUnsettable(nullable);
		return eAttribute;
	}
	
	public static EReference createEReference(String name, EClassifier eType) {
		EReference eReference = EcoreFactory.eINSTANCE.createEReference();
		eReference.setName(name);
		eReference.setEType(eType);
		return eReference;
	}
	
	public static EReference createEReference(String name, EClassifier eType, int lowerBound, int upperBound, boolean containment) {
		EReference eReference = createEReference(name,eType);
		eReference.setLowerBound(lowerBound);
		eReference.setUpperBound(upperBound);
		eReference.setContainment(containment);
		return eReference;
	}

    public static EDataType createEDataType(Object parent, String name, Class instanceClass) {
		EDataType eDataType = EcoreFactory.eINSTANCE.createEDataType();
		eDataType.setName(name);
		eDataType.setInstanceClass(instanceClass);
		if (parent instanceof EPackage) {
			((EPackage) parent).getEClassifiers().add(eDataType);
		} else if (parent instanceof EList) {
			((EList) parent).add(eDataType);
		}
		return eDataType;
    }

    public static EDataType createEDataType(Object parent, String name, String instanceClassName) {
		EDataType eDataType = EcoreFactory.eINSTANCE.createEDataType();
		eDataType.setName(name);
		eDataType.setInstanceClassName(instanceClassName);
		if (parent instanceof EPackage) {
			((EPackage) parent).getEClassifiers().add(eDataType);
		} else if (parent instanceof EList) {
			((EList) parent).add(eDataType);
		}
		return eDataType;
    }

    public static EEnum createEEnum(Object parent, String name) {
        EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
		eEnum.setName(name);
		if (parent instanceof EPackage) {
			((EPackage) parent).getEClassifiers().add(eEnum);
		} else if (parent instanceof EList) {
			((EList) parent).add(eEnum);
		}
		return eEnum;
    }

    public static EEnumLiteral createEnumLiteral(String name) {
		EEnumLiteral eEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
		eEnumLiteral.setName(name);
		return eEnumLiteral;
    }
    
    public static void addEnumLiteral(EEnum parent, EEnumLiteral eEnumLiteral) {
		if (parent.getELiterals() == null) {
			eEnumLiteral.setValue(0);
		} else {
			eEnumLiteral.setValue(parent.getELiterals().size());
		}
		parent.getELiterals().add(eEnumLiteral);
    }
    
    private static void helpCheckResource(Resource resrc, boolean displayContents) {
        System.err.println("Displaying errors..."); //$NON-NLS-1$
        for (final Iterator iter = resrc.getErrors().iterator();  iter.hasNext();) {
            System.err.println("Error: " + iter.next()); //$NON-NLS-1$
        }
        System.err.println("Displaying warnings..."); //$NON-NLS-1$
        for (final Iterator iter = resrc.getWarnings().iterator();  iter.hasNext();) {
            System.err.println("Warning: " + iter.next()); //$NON-NLS-1$
        }
        
        if(displayContents){
            System.err.println("Displaying contents..."); //$NON-NLS-1$
            for (final TreeIterator iter = resrc.getAllContents();  iter.hasNext();) {
                System.err.println("iter.next()=" + iter.next()); //$NON-NLS-1$
            }
        }
    }

    public static void printCollection( java.io.PrintStream stream, String desc, Collection objs ) {
        CoreArgCheck.isNotNull(stream,"The PrintStream reference may not be null"); //$NON-NLS-1$
        if (desc != null) {
            stream.println(desc);
        }
        if (objs == null) {
            stream.println("<null Collection>"); //$NON-NLS-1$
            return;
        }
        if (objs.isEmpty()) {
            stream.println("<empty Collection>"); //$NON-NLS-1$
            return;
        }
        int counter = 0;
        for (Iterator i = objs.iterator(); i.hasNext();) {
            Object obj = i.next();
            if (obj instanceof EClass) {
                stream.println("  Collection[" + counter + "] = " + ((EClass)obj).getName()); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (obj instanceof EStructuralFeature) {
                stream.println("  Collection[" + counter + "] = " + ((EStructuralFeature)obj).getName()); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (obj instanceof EObject) {
                stream.println("  Collection[" + counter + "] = " + ((EObject)obj).eClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (obj instanceof Resource) {
                stream.println("  Collection[" + counter + "] = " + obj); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                stream.println("  Collection[" + counter + "] = " + obj); //$NON-NLS-1$ //$NON-NLS-2$
            }
            counter++;
        }
    }

    //############################################################################################################################
    //# Constructors                                                                                                             #
    //############################################################################################################################
    
    /**
     * @since 3.1
     */
    private EmfTestUtil() {
    }
}
