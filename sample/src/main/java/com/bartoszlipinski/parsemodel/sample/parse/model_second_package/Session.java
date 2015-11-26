package com.bartoszlipinski.parsemodel.sample.parse.model_second_package;

import com.bartoszlipinski.parsemodel.ParseWrapperClass;
import com.parse.ParseSession;

import java.util.Date;

@ParseWrapperClass(ParseSession.class)
public class Session {
    Date updatedAt;
}
