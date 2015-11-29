/**
 * Copyright 2015 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bartoszlipinski.parsemodel.compiler.utils;

import com.bartoszlipinski.parsemodel.ParseWrapperClass;
import com.bartoszlipinski.parsemodel.compiler.code.WrapperElementCodeGenerator;
import com.bartoszlipinski.parsemodel.compiler.field.FieldType;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by Bartosz Lipinski
 * 24.11.2015
 */
public class AnnotatedWrapperClass extends AnnotatedClass {
    public final String mWrappedPackageName;
    public final String mWrappedShortName;

    private AnnotatedWrapperClass(TypeElement typeElement, String packageName, String shortName) {
        super(typeElement);
        mWrappedPackageName = packageName;
        mWrappedShortName = shortName;
    }

    public static AnnotatedWrapperClass with(TypeElement annotatedElement) {
        final String[] wrapped = validateAnnotation(annotatedElement);
        FieldType.addAnnotatedClassName(annotatedElement.getQualifiedName().toString(), annotatedElement.getSimpleName().toString());
        return new AnnotatedWrapperClass(annotatedElement, wrapped[0], wrapped[1]);
    }

    private static String[] validateAnnotation(TypeElement annotatedElement) {
        final ParseWrapperClass annotation =
                annotatedElement.getAnnotation(ParseWrapperClass.class);
        String canonicalName;
        String shortName;
        try {
            Class<?> clazz = annotation.value();
            canonicalName = clazz.getCanonicalName();
            shortName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            canonicalName = classTypeElement.getQualifiedName().toString();
            shortName = classTypeElement.getSimpleName().toString();
        }
        if (!contains(WrapperElementCodeGenerator.ALLOWED_CLASSES, canonicalName)) {
            Logger.getInstance().error("You cannot create a WrapperModel with " + canonicalName +
                    ". See allowed classes in " + WrapperElementCodeGenerator.class.getSimpleName() + ".ALLOWED_CLASSES");
        }
        final String packageName = canonicalName.substring(0, canonicalName.length() - shortName.length() - 1);
        return new String[]{packageName, shortName};
    }

    private static boolean contains(WrapperElementCodeGenerator.AllowedClass[] array, String className) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].getCanonicalName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
