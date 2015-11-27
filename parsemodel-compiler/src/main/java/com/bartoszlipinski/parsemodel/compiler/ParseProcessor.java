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

import com.bartoszlipinski.parsemodel.compiler.generator.BaseGenerator;
import com.bartoszlipinski.parsemodel.compiler.generator.ParseModelGenerator;
import com.bartoszlipinski.parsemodel.compiler.utils.Logger;
import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import static javax.lang.model.SourceVersion.latestSupported;

@AutoService(Processor.class)
public class ParseProcessor extends AbstractProcessor {

    public static final Class<? extends BaseGenerator>[] sGenerators = new Class[]{ParseModelGenerator.class};

    @Override
    public Set getSupportedAnnotationTypes() {

        LinkedHashSet<String> list = new LinkedHashSet();
        for (Class<? extends BaseGenerator> c : sGenerators) {
            try {
                BaseGenerator baseGenerator = c.newInstance();
                for (Class a : baseGenerator.getAnnotations()) {
                    list.add(a.getCanonicalName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new LinkedHashSet(list);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set annotations, RoundEnvironment roundEnv) {

        Logger.initialize(processingEnv);

        for (Class<? extends BaseGenerator> c : sGenerators) {
            try {
                BaseGenerator baseGenerator = c.newInstance();
                baseGenerator.generate(roundEnv, processingEnv);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, e.getMessage());
            }
        }
        return true;
    }
}