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

import com.bartoszlipinski.parsemodel.ParseClass;
import com.bartoszlipinski.parsemodel.ParseUserClass;
import com.bartoszlipinski.parsemodel.compiler.exception.TooManyParseUserClassAnnotatedException;
import com.bartoszlipinski.parsemodel.compiler.field.FieldType;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.bartoszlipinski.parsemodel.compiler.CodeGenerator.generateParseModelClass;
import static com.bartoszlipinski.parsemodel.compiler.CodeGenerator.generateParseModelElementClass;
import static com.bartoszlipinski.parsemodel.compiler.CodeGenerator.generateParseModelUserElementClass;
import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;

@AutoService(Processor.class)
public class ParseModelProcessor extends AbstractProcessor {

    @Override
    public Set getSupportedAnnotationTypes() {
        return singleton(ParseClass.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set annotations, RoundEnvironment roundEnv) {
        try {
            Logger.initialize(processingEnv);

            List<AnnotatedClass> annotatedClasses = new ArrayList<>();
            for (Element element : roundEnv.getElementsAnnotatedWith(ParseClass.class)) {
                // Our annotation is defined with @Target(value=TYPE). Therefore, we can assume that
                // this element is a TypeElement.
                TypeElement annotatedElement = (TypeElement) element;
                annotatedClasses.add(AnnotatedClass.with(annotatedElement));
            }
            Set<? extends Element> parseUserElements = roundEnv.getElementsAnnotatedWith(ParseUserClass.class);
            AnnotatedClass userAnnotatedClass = null;
            if (parseUserElements.size() > 1) {
                throw new TooManyParseUserClassAnnotatedException();
            }
            for (Element element : parseUserElements) {
                userAnnotatedClass = AnnotatedClass.with((TypeElement) element);
            }

            String packageName = Utils.getMainPackageName(processingEnv.getElementUtils(), annotatedClasses, userAnnotatedClass);
            FieldType.setPackageName(packageName);

            for (AnnotatedClass annotatedClass : annotatedClasses) { //we need to have all annotated class names before we can process fields
                annotatedClass.processFields();
            }
            if (userAnnotatedClass != null) {
                userAnnotatedClass.processFields();
            }

            TypeSpec.Builder generatedClass = generateParseModelClass();
            for (AnnotatedClass annotatedClass : annotatedClasses) {
                generatedClass.addType(generateParseModelElementClass(packageName, annotatedClass));
            }
            if (userAnnotatedClass != null) {
                generatedClass.addType(generateParseModelUserElementClass(packageName, userAnnotatedClass));
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
        return true;
    }

}