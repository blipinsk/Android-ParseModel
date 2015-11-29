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
package com.bartoszlipinski.parsemodel.compiler.code;

import com.bartoszlipinski.parsemodel.compiler.field.FieldType;
import com.bartoszlipinski.parsemodel.compiler.utils.AnnotatedClass;
import com.parse.ParseClassName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

/**
 * Created by Bartosz Lipinski
 * 24.11.2015
 */
public class ElementCodeGenerator extends CodeGenerator {

    private static final String STATIC_BLOCK_STATEMENT = "ParseObject.registerSubclass($L.class)";

    private static final String STATIC_GET_QUERY_METHOD_NAME = "getQuery";
    private static final String STATIC_GET_QUERY_METHOD_RETURN_STATEMENT = "return ParseQuery.getQuery($L.class)";

    private static final String SETTER_METHOD_STATEMENT = "put($L, $L)";

    public static TypeSpec.Builder generate(AnnotatedClass annotated) {
        String classNameUC = UPPER_CAMEL.to(UPPER_CAMEL, annotated.mShortClassName); //to be sure

        ClassName parseQuery = ClassName.get("com.parse", "ParseQuery");
        ClassName thisElement = ClassName.get("", classNameUC);
        TypeName parseQueryOfThisElement = ParameterizedTypeName.get(parseQuery, thisElement);

        TypeSpec.Builder builder = TypeSpec.classBuilder(classNameUC)
                .superclass(ClassName.get("", PARSE_BASE_OBJECT))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .build())
                .addAnnotation(AnnotationSpec.builder(ParseClassName.class)
                        .addMember("value", "$S", classNameUC)
                        .build())
                .addStaticBlock(CodeBlock.builder()
                        .addStatement(STATIC_BLOCK_STATEMENT, classNameUC)
                        .build())
                .addMethod(MethodSpec.methodBuilder(STATIC_GET_QUERY_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .returns(parseQueryOfThisElement)
                        .addStatement(STATIC_GET_QUERY_METHOD_RETURN_STATEMENT, classNameUC)
                        .build());

        String fieldName;
        String fieldNameLC;
        String fieldNameUC;
        String fieldNameUU;

        for (int i = 0; i < annotated.mFieldNames.size(); ++i) {
            fieldName = annotated.mFieldNames.get(i);
            fieldNameLC = LOWER_CAMEL.to(LOWER_CAMEL, fieldName);
            fieldNameUC = LOWER_CAMEL.to(UPPER_CAMEL, fieldName);
            fieldNameUU = STATIC_KEY_FIELD + LOWER_CAMEL.to(UPPER_UNDERSCORE, fieldName);

            builder.addField(FieldSpec
                    .builder(String.class, fieldNameUU, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", fieldName)
                    .build());

            FieldType type = annotated.mFieldTypes.get(i);
            TypeName setterParameter = type.getTypeName();

            builder.addMethod(MethodSpec.methodBuilder(SETTER_METHOD_NAME + fieldNameUC)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addParameter(setterParameter, fieldNameLC)
                    .addStatement(SETTER_METHOD_STATEMENT, fieldNameUU, fieldNameLC)
                    .build());

            MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(GETTER_METHOD_NAME + fieldNameUC)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(setterParameter);
            type.addGetterStatements(getterBuilder, fieldNameUU);

            builder.addMethod(getterBuilder.build());
        }

        return builder;
    }
}
