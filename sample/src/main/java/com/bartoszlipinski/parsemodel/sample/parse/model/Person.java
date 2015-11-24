package com.bartoszlipinski.parsemodel.sample.parse.model;

import com.bartoszlipinski.parsemodel.Builder;
import com.parse.ParseFile;

import java.util.Date;

@Builder
public class Person {
    String name;
    String surname;
    Date birthDate;
    int height;
    ParseFile photo;
}
