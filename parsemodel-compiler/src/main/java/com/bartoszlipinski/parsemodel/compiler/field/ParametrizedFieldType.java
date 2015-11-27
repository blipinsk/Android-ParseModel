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
package com.bartoszlipinski.parsemodel.compiler.field;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class ParametrizedFieldType extends BaseFieldType {

    private ParametrizedFieldType(TypeName typeName, String statementArg) {
        super(typeName, statementArg);
    }

    public static ParametrizedFieldType with(BaseFieldType baseFieldType, TypeName... componentTypeArg) {
        TypeName baseTypeName = baseFieldType.getTypeName();
        if (!(baseTypeName instanceof ClassName)) {
            throw new IllegalArgumentException("ParametrizedFieldType can only be used with BaseFieldTypes with ClassName as a mTypeName");
        }
        TypeName parametrizedTypeName = ParameterizedTypeName.get((ClassName) baseTypeName, componentTypeArg);
        return new ParametrizedFieldType(parametrizedTypeName, baseFieldType.mStatementArg);
    }
}
