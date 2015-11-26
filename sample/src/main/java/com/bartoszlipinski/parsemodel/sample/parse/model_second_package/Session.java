package com.bartoszlipinski.parsemodel.sample.parse.model_second_package;

import com.bartoszlipinski.parsemodel.WrapperBuilder;
import com.parse.ParseSession;

import java.util.Date;

@WrapperBuilder(ParseSession.class)
public class Session {
    Date updatedAt;
}
