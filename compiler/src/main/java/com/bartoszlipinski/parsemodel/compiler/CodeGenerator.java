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
package com.bartoszlipinski.parsemodel.compiler;

import com.bartoszlipinski.parsemodel.compiler.field.FieldType;
import com.parse.ParseClassName;
import com.parse.ParseObject;
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

public class CodeGenerator {

    public static final String CLASS_NAME = "ParseModel";

    private static final String STATIC_BLOCK_STATEMENT = "ParseObject.registerSubclass($L.class)";
    private static final String STATIC_GET_QUERY_METHOD_NAME = "getQuery";
    private static final String STATIC_GET_QUERY_METHOD_RETURN_STATEMENT = "return ParseQuery.getQuery($L.class)";

    private static final String STATIC_KEY_FIELD = "KEY_";

    private static final String SETTER_METHOD_NAME = "set";
    private static final String SETTER_METHOD_STATEMENT = "put($L, $L)";

    private static final String GETTER_METHOD_NAME = "get";

    public static final String USER_FIELD_NAME = "mParseUser";

    private static final String USER_SETTER_METHOD_STATEMENT = USER_FIELD_NAME + ".put($L, $L)";

    private static final String USER_CONSTRUCTOR_ARGUMENT_NAME = "parseUser";
    private static final String USER_CONSTRUCTOR_FIRST_STATEMENT = USER_FIELD_NAME + " = " + USER_CONSTRUCTOR_ARGUMENT_NAME;
    private static final String USER_CONSTRUCTOR_SECOND_STATEMENT = USER_FIELD_NAME + ".fetchInBackground()";

    private static final String STATIC_WITH_METHOD_NAME = "with";
    private static final String STATIC_WITH_METHOD_RETURN_STATEMENT = "return new $L(" + USER_CONSTRUCTOR_ARGUMENT_NAME + ")";

    private static final String STATIC_GET_CURRENT_USER_METHOD_NAME = "getCurrentUser";
    private static final String STATIC_GET_CURRENT_USER_METHOD_RETURN_STATEMENT =
            "return ParseUser.getCurrentUser() != null ? $L.with(ParseUser.getCurrentUser()) : null";

    private static final String GET_PARSE_USER_METHOD_RETURN_STATEMENT = "return " + USER_FIELD_NAME;

    public static TypeSpec.Builder generateParseModelClass() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build());
        return builder;
    }

    public static TypeSpec generateParseModelElementClass(String packageName, AnnotatedClass annotated) {
        String classNameUC = UPPER_CAMEL.to(UPPER_CAMEL, annotated.mShortClassName); //to be sure

        ClassName parseQuery = ClassName.get("com.parse", "ParseQuery");
        ClassName thisElement = ClassName.get(packageName, classNameUC);
        TypeName parseQueryOfThisElement = ParameterizedTypeName.get(parseQuery, thisElement);

        TypeSpec.Builder builder = TypeSpec.classBuilder(classNameUC)
                .superclass(ParseObject.class)
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

        return builder.build();
    }

    public static TypeSpec generateParseModelUserElementClass(String packageName, AnnotatedClass annotated) {
        String classNameUC = UPPER_CAMEL.to(UPPER_CAMEL, annotated.mShortClassName); //to be sure
        ClassName thisElement = ClassName.get(packageName, classNameUC);
        ClassName parseUser = ClassName.get("com.parse", "ParseUser");

        TypeSpec.Builder builder = TypeSpec.classBuilder(classNameUC)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(parseUser, USER_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parseUser, USER_CONSTRUCTOR_ARGUMENT_NAME)
                        .addStatement(USER_CONSTRUCTOR_FIRST_STATEMENT)
                        .addStatement(USER_CONSTRUCTOR_SECOND_STATEMENT)
                        .build())
                .addMethod(MethodSpec.methodBuilder(STATIC_WITH_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(parseUser, USER_CONSTRUCTOR_ARGUMENT_NAME)
                        .returns(thisElement)
                        .addStatement(STATIC_WITH_METHOD_RETURN_STATEMENT, classNameUC)
                        .build())
                .addMethod(MethodSpec.methodBuilder(STATIC_GET_CURRENT_USER_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(thisElement)
                        .addStatement(STATIC_GET_CURRENT_USER_METHOD_RETURN_STATEMENT, classNameUC)
                        .build())
                .addMethod(MethodSpec.methodBuilder(GETTER_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(parseUser)
                        .addStatement(GET_PARSE_USER_METHOD_RETURN_STATEMENT)
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
                    .addStatement(USER_SETTER_METHOD_STATEMENT, fieldNameUU, fieldNameLC)
                    .build());

            MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(GETTER_METHOD_NAME + fieldNameUC)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(setterParameter);
            type.addUserGetterStatements(getterBuilder, fieldNameUU);

            builder.addMethod(getterBuilder.build());
        }

        return builder.build();
    }
}
