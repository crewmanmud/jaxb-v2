/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class Util {
    private static XSType[] listDirectSubstitutables( XSType _this ) {
        ArrayList r = new ArrayList();
        
        // TODO: handle @block
        Iterator itr = ((SchemaImpl)_this.getOwnerSchema()).parent.iterateTypes();
        while( itr.hasNext() ) {
            XSType t = (XSType)itr.next();
            if( t.getBaseType()==_this )
                r.add(t);
        }
        return (XSType[]) r.toArray(new XSType[r.size()]);
    }

    public static XSType[] listSubstitutables( XSType _this ) {
        Set substitables = new HashSet();
        buildSubstitutables( _this, substitables );
        return (XSType[]) substitables.toArray(new XSType[substitables.size()]);
    }
    
    public static void buildSubstitutables( XSType _this, Set substitutables ) {
        if( _this.isLocal() )    return;
        buildSubstitutables( _this, _this, substitutables );
    }
    
    private static void buildSubstitutables( XSType head, XSType _this, Set substitutables ) {
        if(!isSubstitutable(head,_this))
            return;    // no derived type of _this can substitute head.
        
        if(substitutables.add(_this)) {
            XSType[] child = listDirectSubstitutables(_this);
            for( int i=0; i<child.length; i++ )
                buildSubstitutables( head, child[i], substitutables );
        }
    }
    
    /**
     * Implements
     * <code>Validation Rule: Schema-Validity Assessment (Element) 1.2.1.2.4</code> 
     */
    private static boolean isSubstitutable( XSType _base, XSType derived ) {
        // too ugly to the point that it's almost unbearable.
        // I mean, it's not even transitive. Thus we end up calling this method
        // for each candidate
        if( _base.isComplexType() ) {
            XSComplexType base = _base.asComplexType();
            
            for( ; base!=derived; derived=derived.getBaseType() ) {
                if( base.isSubstitutionProhibited( derived.getDerivationMethod() ) )
                    return false;    // Type Derivation OK (Complex)-1
            }
            return true;
        } else {
            // simple type don't have any @block
            return true;
        }
    }

}