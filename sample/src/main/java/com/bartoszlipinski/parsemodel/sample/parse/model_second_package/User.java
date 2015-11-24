package com.bartoszlipinski.parsemodel.sample.parse.model_second_package;

import com.bartoszlipinski.parsemodel.WrapperBuilder;
import com.parse.ParseUser;

@WrapperBuilder(ParseUser.class)
public class User {
    String name;
    int age;
    float test;
}
