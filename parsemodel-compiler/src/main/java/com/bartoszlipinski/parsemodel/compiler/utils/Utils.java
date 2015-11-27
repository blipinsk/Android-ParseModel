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

import java.util.List;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public final class Utils {

    public static String getMainPackageName(Elements elementUtils, List<AnnotatedClass> annotatedClasses)
            throws com.bartoszlipinski.parsemodel.compiler.exception.NoPackageNameException {

        String mainPackageName = null;
        for (AnnotatedClass annotatedClass : annotatedClasses) {
            mainPackageName = getMainPackage(elementUtils, mainPackageName, annotatedClass);
        }
        if (mainPackageName == null) {
            throw new com.bartoszlipinski.parsemodel.compiler.exception.NoPackageNameException(annotatedClasses.get(0).mTypeElement);
        }

        int lastChar = mainPackageName.length() - 1;
        mainPackageName = mainPackageName.charAt(lastChar) == '.' ? mainPackageName.substring(0, lastChar) : mainPackageName;
        return mainPackageName;
    }

    private static String getMainPackage(Elements elementUtils, String mainPackageName, AnnotatedClass annotatedClass) throws com.bartoszlipinski.parsemodel.compiler.exception.NoPackageNameException {
        String packageName;
        TypeElement type = annotatedClass.mTypeElement;
        PackageElement pkg = elementUtils.getPackageOf(type);
        if (pkg.isUnnamed()) {
            throw new com.bartoszlipinski.parsemodel.compiler.exception.NoPackageNameException(type);
        }
        packageName = pkg.getQualifiedName().toString();
        if (mainPackageName == null) {
            mainPackageName = packageName;
        } else {
            mainPackageName = findStartIntersection(mainPackageName, packageName);
        }
        return mainPackageName;
    }

    private static String findStartIntersection(String firstStr, String secondStr) {
        String intersection = "";
        for (int i = 0; i < firstStr.length() && i < secondStr.length(); ++i) {
            char charAt = firstStr.charAt(i);
            if (charAt == secondStr.charAt(i)) {
                intersection += charAt;
            }
        }
        return intersection;
    }
}