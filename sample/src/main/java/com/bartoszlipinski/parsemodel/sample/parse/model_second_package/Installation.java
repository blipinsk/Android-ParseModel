package com.bartoszlipinski.parsemodel.sample.parse.model_second_package;

import com.bartoszlipinski.parsemodel.WrapperBuilder;
import com.parse.ParseInstallation;

import java.util.Date;

@WrapperBuilder(ParseInstallation.class)
public class Installation {
    Date updatedAt;
}
