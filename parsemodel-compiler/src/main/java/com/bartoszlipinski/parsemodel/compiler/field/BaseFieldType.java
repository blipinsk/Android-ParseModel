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

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public class BaseFieldType extends FieldType {

    public static final BaseFieldType INT = new BaseFieldType(TypeName.INT, "Int");

    public static final BaseFieldType DOUBLE = new BaseFieldType(TypeName.DOUBLE, "Double");

    public static final BaseFieldType LONG = new BaseFieldType(TypeName.LONG, "Long");

    public static final BaseFieldType NUMBER = new BaseFieldType(ClassName.get("java.lang", "Number"), "Number");

    public static final BaseFieldType BOOLEAN = new BaseFieldType(TypeName.BOOLEAN, "Boolean");

    public static final BaseFieldType STRING = new BaseFieldType(ClassName.get("java.lang", "String"), "String");

    public static final BaseFieldType DATE = new BaseFieldType(ClassName.get("java.util", "Date"), "Date");

    public static final BaseFieldType OBJECT = new BaseFieldType(ClassName.get("java.lang", "Object"), "");

    public static final BaseFieldType BYTE_ARRAY = new BaseFieldType(ArrayTypeName.of(TypeName.BYTE), "Bytes");

    public static final BaseFieldType JSON_ARRAY = new BaseFieldType(ClassName.get("org.json", "JSONArray"), "JSONArray");

    public static final BaseFieldType JSON_OBJECT = new BaseFieldType(ClassName.get("org.json", "JSONArray"), "JSONObject");

    public static final BaseFieldType PARSE_FILE = new BaseFieldType(ClassName.get("com.parse", "ParseFile"), "ParseFile");

    public static final BaseFieldType PARSE_GEO_POINT = new BaseFieldType(ClassName.get("com.parse", "ParseGeoPoint"), "ParseGeoPoint");

    public static final BaseFieldType PARSE_USER = new BaseFieldType(ClassName.get("com.parse", "ParseUser"), "ParseUser");

    public static final BaseFieldType PARSE_OBJECT = new BaseFieldType(ClassName.get("com.parse", "ParseObject"), "ParseObject");

    public static final BaseFieldType PARSE_RELATION = new BaseFieldType(ClassName.get("com.parse", "ParseRelation"), "Relation");

    public static final BaseFieldType MAP = new BaseFieldType(ClassName.get("java.util", "Map"), "Map");

    public static final BaseFieldType LIST = new BaseFieldType(ClassName.get("java.util", "List"), "List");

    private static final String GETTER_STATEMENT_BASE = "return get%s($L)";

    private static final String WRAPPER_GETTER_STATEMENT_BASE = "return %s.get%s($L)";

    protected BaseFieldType(TypeName typeName, String statementArg) {
        super(typeName, statementArg);
    }

    @Override
    protected String getGetterStatementBase() {
        return String.format(GETTER_STATEMENT_BASE, mStatementArg);
    }

    @Override
    protected String getWrapperGetterStatementBase(String wrappedFieldName) {
        return String.format(WRAPPER_GETTER_STATEMENT_BASE,wrappedFieldName, mStatementArg);
    }

}
