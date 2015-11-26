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

import com.bartoszlipinski.parsemodel.compiler.exception.UnsupportedFieldTypeException;
import com.bartoszlipinski.parsemodel.compiler.field.FieldType;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class AnnotatedClass {
    public final String mShortClassName;
    public final TypeElement mTypeElement;
    public List<String> mFieldNames;
    public List<FieldType> mFieldTypes;

    public static AnnotatedClass with(TypeElement annotatedElement) {
        FieldType.addAnnotatedClassName(annotatedElement.getQualifiedName().toString(), annotatedElement.getSimpleName().toString());
        return new AnnotatedClass(annotatedElement);
    }

    protected AnnotatedClass(TypeElement typeElement) {//, List<String> fieldNames, List<FieldType> fieldTypes) {
        this.mTypeElement = typeElement;
        this.mShortClassName = typeElement.getSimpleName().toString();
    }

    public void processFields() throws UnsupportedFieldTypeException {
        mFieldNames = new ArrayList<>();
        mFieldTypes = new ArrayList<>();
        for (Element element : mTypeElement.getEnclosedElements()) {
            if (!(element instanceof VariableElement)) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            TypeMirror typeMirror = variableElement.asType();
            String fieldName = variableElement.getSimpleName().toString();
            FieldType fieldType = FieldType.with(typeMirror);
            if (fieldType == null) {
                throw new UnsupportedFieldTypeException(mShortClassName, fieldName, typeMirror.toString());
            }
            mFieldTypes.add(fieldType);
            mFieldNames.add(fieldName);
        }
    }
}