/**
 * Copyright 2015 Bartosz Lipinski
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bartoszlipinski.parsemodel.compiler.field;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public abstract class FieldType {

    private static List<String> sAnnotatedFullClassNames = new ArrayList<>();
    private static List<String> sAnnotatedShortClassNames = new ArrayList<>();
    private static String sPackageName = "";

    final TypeName mTypeName;
    final String mStatementArg;

    protected FieldType(TypeName typeName, String statementArg) {
        mTypeName = typeName;
        mStatementArg = statementArg;
    }

    public TypeName getTypeName() {
        return mTypeName;
    }

    public boolean fits(TypeName typeName) {
        return mTypeName.equals(typeName);
    }

    public boolean fits(TypeMirror mirror) {
        TypeName typeName = TypeName.get(mirror);
        return mTypeName.equals(typeName);
    }

    protected abstract String getGetterStatementBase();

    protected abstract String getWrapperGetterStatementBase(String wrappedFieldName);

    public final MethodSpec.Builder addGetterStatements(MethodSpec.Builder methodBuilder, String keyVariableName) {
        return methodBuilder.addStatement(getGetterStatementBase(), keyVariableName);
    }

    public final MethodSpec.Builder addWrapperGetterStatements(MethodSpec.Builder methodBuilder, String wrappedFieldName, String keyVariableName) {
        return methodBuilder.addStatement(getWrapperGetterStatementBase(wrappedFieldName), keyVariableName);
    }

    public static void addAnnotatedClassName(String fullClassName, String shortClassName) {
        if (!sAnnotatedFullClassNames.contains(fullClassName)) {
            sAnnotatedFullClassNames.add(fullClassName);
            sAnnotatedShortClassNames.add(shortClassName);
        }
    }

    public static void setPackageName(String packageName) {
        sPackageName = packageName;
    }

    public static FieldType with(TypeMirror mirror) {
        TypeKind kind = mirror.getKind();
        switch (kind) {
            case BOOLEAN:
                return BaseFieldType.BOOLEAN;
            case INT:
                return BaseFieldType.INT;
            case LONG:
                return BaseFieldType.LONG;
            case DOUBLE:
                return BaseFieldType.DOUBLE;
            case FLOAT:
                return CastedFieldType.FLOAT;
            case ARRAY:
                ArrayType arrayType = (ArrayType) mirror;
                TypeKind arrayComponentKind = arrayType.getComponentType().getKind();
                if (arrayComponentKind == TypeKind.BYTE) {
                    return BaseFieldType.BYTE_ARRAY;
                }
                //a place for other arrays being casted to lists
                return null;
            case DECLARED:
                if (BaseFieldType.STRING.fits(mirror)) {
                    return BaseFieldType.STRING;
                } else if (BaseFieldType.DATE.fits(mirror)) {
                    return BaseFieldType.DATE;
                } else if (BaseFieldType.NUMBER.fits(mirror)) {
                    return BaseFieldType.NUMBER;
                } else if (BaseFieldType.OBJECT.fits(mirror)) {
                    return BaseFieldType.OBJECT;
                } else if (BaseFieldType.JSON_ARRAY.fits(mirror)) {
                    return BaseFieldType.JSON_ARRAY;
                } else if (BaseFieldType.JSON_OBJECT.fits(mirror)) {
                    return BaseFieldType.JSON_OBJECT;
                } else if (BaseFieldType.PARSE_FILE.fits(mirror)) {
                    return BaseFieldType.PARSE_FILE;
                } else if (BaseFieldType.PARSE_GEO_POINT.fits(mirror)) {
                    return BaseFieldType.PARSE_GEO_POINT;
                } else if (BaseFieldType.PARSE_USER.fits(mirror)) {
                    return BaseFieldType.PARSE_USER;
                } else if (BaseFieldType.PARSE_OBJECT.fits(mirror)) {
                    return BaseFieldType.PARSE_OBJECT;
                } else {
                    TypeName typeName = TypeName.get(mirror);
                    if (typeName instanceof ParameterizedTypeName) {
                        ParameterizedTypeName parametrized = (ParameterizedTypeName) typeName;
                        ClassName rawType = parametrized.rawType;
                        TypeName parameterType = parametrized.typeArguments.get(0);
                        if (BaseFieldType.LIST.fits(rawType) && parametrized.typeArguments.size() == 1) {
                            if (isOtherAnnotatedClass(parameterType)) {
                                parameterType = getAnnotatedClassShortTypeName(parameterType);
                            }
                            return ParametrizedFieldType.with(BaseFieldType.LIST, parameterType);
                        } else if (BaseFieldType.PARSE_RELATION.fits(rawType) && parametrized.typeArguments.size() == 1) {
                            if (isOtherAnnotatedClass(parameterType)) {
                                parameterType = getAnnotatedClassShortTypeName(parameterType);
                            }
                            return ParametrizedFieldType.with(BaseFieldType.PARSE_RELATION, parameterType);
                        } else if (BaseFieldType.MAP.fits(rawType) &&
                                parametrized.typeArguments.size() == 2 &&
                                parametrized.typeArguments.get(0).equals(BaseFieldType.STRING.getTypeName())) {
                            parameterType = parametrized.typeArguments.get(1);
                            if (isOtherAnnotatedClass(parameterType)) {
                                parameterType = getAnnotatedClassShortTypeName(parameterType);
                            }
                            return ParametrizedFieldType.with(BaseFieldType.MAP, parametrized.typeArguments.get(0), parameterType);
                        } else {
                            return null;
                        }
                    } else if (typeName instanceof ClassName) {
                        if (isOtherAnnotatedClass(typeName)) {
                            String castedTo = getAnnotatedClassShortName(typeName);
                            return CastedFieldType.with(
                                    ClassName.get(sPackageName, castedTo),
                                    castedTo,
                                    BaseFieldType.PARSE_OBJECT.mStatementArg);
                        }
                    }
                }
                return null;
            default:
                return null;
        }
    }

    private static boolean isOtherAnnotatedClass(TypeName typeName) {
        return sAnnotatedFullClassNames.indexOf(typeName.toString()) != -1;
    }

    private static String getAnnotatedClassShortName(TypeName typeName) {
        if (isOtherAnnotatedClass(typeName)) {
            return sAnnotatedShortClassNames.get(sAnnotatedFullClassNames.indexOf(typeName.toString()));
        }
        return null;
    }

    private static ClassName getAnnotatedClassShortTypeName(TypeName typeName) {
        String shortName = getAnnotatedClassShortName(typeName);
        if (shortName != null) {
            return ClassName.get(sPackageName, shortName);
        }
        return null;
    }
}
