/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.bind.Util;

/**
 * {@link AnnotationReader} that uses {@code java.lang.reflect} to
 * read annotations from class files.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public final class RuntimeInlineAnnotationReader extends AbstractInlineAnnotationReaderImpl<Type,Class,Field,Method>
    implements RuntimeAnnotationReader {
	
	private static final Logger logger = Util.getClassLogger();

    public <A extends Annotation> A getFieldAnnotation(Class<A> annotation, Field field, Locatable srcPos) {
        return LocatableAnnotation.create(field.getAnnotation(annotation),srcPos);
    }

    public boolean hasFieldAnnotation(Class<? extends Annotation> annotationType, Field field) {
        return field.isAnnotationPresent(annotationType);
    }

    public boolean hasClassAnnotation(Class clazz, Class<? extends Annotation> annotationType) {
        return clazz.isAnnotationPresent(annotationType);
    }

    public Annotation[] getAllFieldAnnotations(Field field, Locatable srcPos) {
        Annotation[] r = field.getAnnotations();
        for( int i=0; i<r.length; i++ ) {
            r[i] = LocatableAnnotation.create(r[i],srcPos);
        }
        return r;
    }

    public <A extends Annotation> A getMethodAnnotation(Class<A> annotation, Method method, Locatable srcPos) {
        return LocatableAnnotation.create(method.getAnnotation(annotation),srcPos);
    }

    public boolean hasMethodAnnotation(Class<? extends Annotation> annotation, Method method) {
        return method.isAnnotationPresent(annotation);
    }

    public Annotation[] getAllMethodAnnotations(Method method, Locatable srcPos) {
        Annotation[] r = method.getAnnotations();
        for( int i=0; i<r.length; i++ ) {
            r[i] = LocatableAnnotation.create(r[i],srcPos);
        }
        return r;
    }

    public <A extends Annotation> A getMethodParameterAnnotation(Class<A> annotation, Method method, int paramIndex, Locatable srcPos) {
        Annotation[] pa = method.getParameterAnnotations()[paramIndex];
        for( Annotation a : pa ) {
            if(a.annotationType()==annotation)
                return LocatableAnnotation.create((A)a,srcPos);
        }
        return null;
    }

    public <A extends Annotation> A getClassAnnotation(Class<A> a, Class clazz, Locatable srcPos) {
        return LocatableAnnotation.create(((Class<?>)clazz).getAnnotation(a),srcPos);
    }


    /**
     * Cache for package-level annotations.
     */
    private final Map<Class<? extends Annotation>,Map<Package,Annotation>> packageCache =
            new HashMap<Class<? extends Annotation>,Map<Package,Annotation>>();

    public <A extends Annotation> A getPackageAnnotation(Class<A> a, Class clazz, Locatable srcPos) {
        Package p = clazz.getPackage();
        if(p==null) return null;

        Map<Package,Annotation> cache = packageCache.get(a);
        if(cache==null) {
            cache = new HashMap<Package,Annotation>();
            packageCache.put(a,cache);
        }

        if(cache.containsKey(p))
            return (A)cache.get(p);
        else {
            //A ann = LocatableAnnotation.create(p.getAnnotation(a),srcPos);
        	A ann = LocatableAnnotation.create(getJaxbPackageAnnotation(clazz,a),srcPos);
            cache.put(p,ann);
            return ann;
        }
    }
    
    // PACKAGE ANNOTATIONS HACK BY jmgoncalves
    private <A extends Annotation> A getJaxbPackageAnnotation(Class clazz, Class<A> a) {
    	String pkgInfoClassName = clazz.getCanonicalName().substring(0,clazz.getCanonicalName().lastIndexOf("."));
    	Class<?> pkgInfoClass;
    	
		try {
			pkgInfoClass = Class.forName(pkgInfoClassName+".package-info");
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING,e.getMessage(),e);
			return null;
		}
    	
    	return (A)pkgInfoClass.getAnnotation(a);
    }

    public Class getClassValue(Annotation a, String name) {
        try {
            return (Class)a.annotationType().getMethod(name).invoke(a);
        } catch (IllegalAccessException e) {
            // impossible
            throw new IllegalAccessError(e.getMessage());
        } catch (InvocationTargetException e) {
            // impossible
            throw new InternalError(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public Class[] getClassArrayValue(Annotation a, String name) {
        try {
            return (Class[])a.annotationType().getMethod(name).invoke(a);
        } catch (IllegalAccessException e) {
            // impossible
            throw new IllegalAccessError(e.getMessage());
        } catch (InvocationTargetException e) {
            // impossible
            throw new InternalError(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    protected String fullName(Method m) {
        return m.getDeclaringClass().getName()+'#'+m.getName();
    }
}
