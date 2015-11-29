package com.bartoszlipinski.parsemodel.compiler.code;

import com.parse.ParseObject;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by Bartosz Lipinski
 * 29.11.2015
 */
public class BaseCodeGenerator extends CodeGenerator {

    public static TypeSpec.Builder generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(PARSE_BASE_OBJECT)
                .superclass(ParseObject.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(FieldSpec
                        .builder(String.class, "KEY_OBJECT_ID", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "objectId")
                        .build())
                .addField(FieldSpec
                        .builder(String.class, "KEY_CREATED_AT", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "createdAt")
                        .build())
                .addField(FieldSpec
                        .builder(String.class, "KEY_UPDATED_AT", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "updatedAt")
                        .build());

        return builder;
    }
}
