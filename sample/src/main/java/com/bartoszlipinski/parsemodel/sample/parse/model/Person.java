package com.bartoszlipinski.parsemodel.sample.parse.model;

import com.bartoszlipinski.parsemodel.ParseClass;
import com.parse.ParseFile;

import java.util.Date;

@ParseClass
public class Person {
    String name;
    String surname;
    Date birthDate;
    int height;
    ParseFile photo;
}
