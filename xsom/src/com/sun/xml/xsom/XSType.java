/*
 * @(#)$Id: XSType.java,v 1.2 2005-05-20 23:14:39 kohsuke Exp $
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.xml.xsom;

/**
 * Base interface for {@link XSComplexType} and {@link XSSimpleType}.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSType extends XSDeclaration {
    /**
     * Returns the base type of this type.
     *
     * Note that if this type represents <tt>xs:anyType</tt>, this method returns itself.
     * This is awkward as an API, but it follows the schema specification.
     *
     * @return  always non-null.
     */
    XSType getBaseType();

    final static int EXTENSION = 1;
    final static int RESTRICTION = 2;
    final static int SUBSTITUTION = 4;

    int getDerivationMethod();

    /** Returns true if <code>this instanceof XSSimpleType</code>. */
    boolean isSimpleType();
    /** Returns true if <code>this instanceof XSComplexType</code>. */
    boolean isComplexType();

    /**
     * Lists up types that can substitute this type by using xsi:type.
     * Includes this type itself.
     * <p>
     * This method honors the block flag.
     */
    XSType[] listSubstitutables();


    /** Casts this object to XSSimpleType if possible, otherwise returns null. */
    XSSimpleType asSimpleType();
    /** Casts this object to XSComplexType if possible, otherwise returns null. */
    XSComplexType asComplexType();

    /**
     * Returns true if this type is derived from the specified type.
     *
     * <p>
     * Note that <tt>t.isDerivedFrom(t)</tt> returns true.
     */
    boolean isDerivedFrom( XSType t );
}
