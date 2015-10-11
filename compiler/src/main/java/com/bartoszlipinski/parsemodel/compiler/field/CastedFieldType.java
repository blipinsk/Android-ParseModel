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

import com.bartoszlipinski.parsemodel.compiler.CodeGenerator;
import com.squareup.javapoet.TypeName;

public class CastedFieldType extends FieldType {

    public static final CastedFieldType FLOAT = new CastedFieldType(TypeName.FLOAT, "float", "Double"); //"Double" this is not a mistake

    private static final String GETTER_STATEMENT_BASE = "return (%s)get%s($L)";

    private static final String USER_GETTER_STATEMENT_BASE = "return (%s)" + CodeGenerator.USER_FIELD_NAME + ".get%s($L)";

    private final String mCastedTo;

    private CastedFieldType(TypeName typeName, String castedTo, String statementArg) {
        super(typeName, statementArg);
        mCastedTo = castedTo;
    }

    @Override
    protected String getGetterStatementBase() {
        return String.format(GETTER_STATEMENT_BASE, mCastedTo, mStatementArg);
    }

    @Override
    protected String getUserGetterStatementBase() {
        return String.format(USER_GETTER_STATEMENT_BASE, mCastedTo, mStatementArg);
    }

    public static CastedFieldType with(TypeName typeName, String castedTo, String statementArg) {
        return new CastedFieldType(typeName, castedTo, statementArg);
    }
}
