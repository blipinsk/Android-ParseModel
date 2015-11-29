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
import com.bartoszlipinski.parsemodel.compiler.utils.AnnotatedWrapperClass;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
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
public class WrapperElementCodeGenerator extends CodeGenerator {
    public final static AllowedClass[] ALLOWED_CLASSES = {
            AllowedClass.with("com.parse", "ParseUser")
                    .withGetCurrentStatement(
                            "return ParseUser.getCurrentUser() != null ? $L.with(ParseUser.getCurrentUser()) : null"),
            AllowedClass.with("com.parse", "ParseInstallation")
                    .withGetCurrentStatement(
                            "return ParseInstallation.getCurrentInstallation() != null ? $L.with(ParseInstallation.getCurrentInstallation()) : null"),
            AllowedClass.with("com.parse", "ParseSession")};

    private static final String CONSTRUCTOR_FIRST_STATEMENT = "$L = $L";
    private static final String STATIC_WITH_METHOD_NAME = "with";
    private static final String STATIC_WITH_METHOD_RETURN_STATEMENT = "return new $L($L)";
    private static final String GET_METHOD_RETURN_STATEMENT = "return $L";
    private static final String STATIC_GET_CURRENT_METHOD_NAME = "getCurrent";
    private static final String SETTER_METHOD_STATEMENT = "$L.put($L, $L)";

    public static TypeSpec.Builder generate(String packageName, AnnotatedWrapperClass annotated) {
        String classNameUC = UPPER_CAMEL.to(UPPER_CAMEL, annotated.mShortClassName); //to be sure
        ClassName thisElement = ClassName.get("", classNameUC);
        ClassName wrappedClass = ClassName.get(annotated.mWrappedPackageName, annotated.mWrappedShortName);
        String wrappedClassUC = UPPER_CAMEL.to(UPPER_CAMEL, annotated.mWrappedShortName);
        String wrappedClassLC = UPPER_CAMEL.to(LOWER_CAMEL, annotated.mWrappedShortName);
        String wrappedFieldName = "m" + wrappedClassUC;

        TypeSpec.Builder builder = TypeSpec.classBuilder(classNameUC)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(wrappedClass, wrappedFieldName, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(wrappedClass, wrappedClassLC)
                        .addStatement(CONSTRUCTOR_FIRST_STATEMENT, wrappedFieldName, wrappedClassLC)
                        .build())
                .addMethod(MethodSpec.methodBuilder(STATIC_WITH_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(wrappedClass, wrappedClassLC)
                        .returns(thisElement)
                        .addStatement(STATIC_WITH_METHOD_RETURN_STATEMENT, classNameUC, wrappedClassLC)
                        .build())
                .addMethod(MethodSpec.methodBuilder(GETTER_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(wrappedClass)
                        .addStatement(GET_METHOD_RETURN_STATEMENT, wrappedFieldName)
                        .build());

        final AllowedClass allowed = findAllowedClassFor(annotated.mWrappedPackageName, annotated.mWrappedShortName);
        if (allowed.hasGetCurrentStatement()) { //allowed won't be null, it's being checked in AnnotatedWrapperClass
            builder.addMethod(MethodSpec.methodBuilder(STATIC_GET_CURRENT_METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(thisElement)
                    .addStatement(allowed.mGetCurrentStatement, classNameUC)
                    .build());
        }

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
                    .addStatement(SETTER_METHOD_STATEMENT, wrappedFieldName, fieldNameUU, fieldNameLC)
                    .build());

            MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(GETTER_METHOD_NAME + fieldNameUC)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(setterParameter);
            type.addWrapperGetterStatements(getterBuilder, wrappedFieldName, fieldNameUU);

            builder.addMethod(getterBuilder.build());
        }

        return builder;
    }

    private static AllowedClass findAllowedClassFor(String packageName, String shortName) {
        for (AllowedClass allowed : ALLOWED_CLASSES) {
            if (allowed.matches(packageName, shortName)) {
                return allowed;
            }
        }
        return null;
    }

    public static class AllowedClass {
        public String mPackageName;
        public String mShortName;
        public String mGetCurrentStatement;
        public String mWrappedFieldName;

        private AllowedClass(String packageName, String shortName) {
            mPackageName = packageName;
            mShortName = shortName;
        }

        public String getCanonicalName() {
            return mPackageName + "." + mShortName;
        }

        public boolean hasGetCurrentStatement() {
            return mGetCurrentStatement != null;
        }

        private static AllowedClass with(String packageName, String shortName) {
            return new AllowedClass(packageName, shortName);
        }

        private AllowedClass withGetCurrentStatement(String getCurrentStatement) {
            mGetCurrentStatement = getCurrentStatement;
            return this;
        }

        private AllowedClass withWrappedFieldName(String wrappedFieldName) {
            mWrappedFieldName = wrappedFieldName;
            return this;
        }

        public boolean matches(String packageName, String shortName) {
            return mPackageName.equals(packageName) && mShortName.equals(shortName);
        }
    }
}
