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

public abstract class CodeGenerator {

    protected static final String STATIC_KEY_FIELD = "KEY_";

    protected static final String SETTER_METHOD_NAME = "set";

    protected static final String GETTER_METHOD_NAME = "get";

    protected static final String PARSE_BASE_OBJECT = "ParseBaseObject";

}
