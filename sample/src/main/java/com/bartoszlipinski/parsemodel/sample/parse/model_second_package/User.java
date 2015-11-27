package com.bartoszlipinski.parsemodel.sample.parse.model_second_package;

import com.bartoszlipinski.parsemodel.ParseWrapperClass;
import com.parse.ParseUser;

@ParseWrapperClass(ParseUser.class)
public class User {
    String fullName;
    int age;
}
