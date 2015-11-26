package com.bartoszlipinski.parsemodel.sample.parse.model;

import com.bartoszlipinski.parsemodel.ParseClass;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

@ParseClass
final class Movie {
    String title;
    long length;
    Person director;
    List<Person> actors;
    Date releaseDate;
    ParseFile posterPhoto;
}
