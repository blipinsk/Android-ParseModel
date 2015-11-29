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
package com.bartoszlipinski.parsemodel.compiler.generator;

import com.bartoszlipinski.parsemodel.ParseClass;
import com.bartoszlipinski.parsemodel.ParseWrapperClass;
import com.bartoszlipinski.parsemodel.compiler.code.BaseCodeGenerator;
import com.bartoszlipinski.parsemodel.compiler.code.ModelCodeGenerator;
import com.bartoszlipinski.parsemodel.compiler.code.ElementCodeGenerator;
import com.bartoszlipinski.parsemodel.compiler.code.WrapperElementCodeGenerator;
import com.bartoszlipinski.parsemodel.compiler.utils.AnnotatedClass;
import com.bartoszlipinski.parsemodel.compiler.utils.AnnotatedWrapperClass;
import com.bartoszlipinski.parsemodel.compiler.utils.Logger;
import com.bartoszlipinski.parsemodel.compiler.utils.Utils;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class ParseModelGenerator extends BaseGenerator {


    @Override
    public Class[] getAnnotations() {
        return new Class[]{ParseClass.class, ParseWrapperClass.class};
    }

    @Override
    public void generate(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
        try {
            List<AnnotatedClass> builderClasses = new ArrayList<>();

            for (Element element : roundEnv.getElementsAnnotatedWith(ParseClass.class)) {
                builderClasses.add(AnnotatedClass.with((TypeElement) element));
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(ParseWrapperClass.class)) {
                builderClasses.add(AnnotatedWrapperClass.with((TypeElement) element));
            }

            String packageName = Utils.getMainPackageName(processingEnv.getElementUtils(), builderClasses);
            FieldType.setPackageName(packageName);

            for (AnnotatedClass annotatedClass : builderClasses) {
                annotatedClass.processFields();
            }

            TypeSpec.Builder generatedClass = ModelCodeGenerator.generate();
            generatedClass.addType(
                    BaseCodeGenerator.generate().build());
            for (AnnotatedClass annotatedClass : builderClasses) {
                if (annotatedClass instanceof AnnotatedWrapperClass) {
                    generatedClass.addType(
                            WrapperElementCodeGenerator.generate(packageName, (AnnotatedWrapperClass) annotatedClass).build());
                } else {
                    generatedClass.addType(
                            ElementCodeGenerator.generate(annotatedClass).build());
                }
            }
            JavaFile javaFile = JavaFile.builder(packageName, generatedClass.build()).build();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IndexOutOfBoundsException e) {
            Logger.getInstance().warning("Don't mind me... " +
                    "I'm just a friendly warning on an Exception that seems to be happening on a pseudo-random manner. " +
                    "Don't worry, I won't prevent Android-ParseModel from working correctly.");
        } catch (Exception e) {
            Logger.getInstance().error(e.getMessage());
        }
    }
}
