Android-ParseModel
==================

[![License](https://img.shields.io/github/license/blipinsk/RecyclerViewHeader.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--ParseModel-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2627)
[![Maven Central](https://img.shields.io/maven-central/v/com.bartoszlipinski/parsemodel.svg)](http://gradleplease.appspot.com/#parsemodel)
[![Bintray](https://img.shields.io/bintray/v/blipinsk/maven/Android-ParseModel.svg)](https://bintray.com/blipinsk/maven/Android-ParseModel/_latestVersion)

Annotation-based library that allows for easy data model creation for Parse Android SDK.

If you're using Parse.com Android SDK and you're tired of constantly creating setters and getters for every single field, this library might be for you.

With a simple annotation `@ParseClass` you can take this:

    @ParseClass
    public class Person {
        String name;
        String surname;
        Date birthDate;
        ParseFile photo;
    }
        
and generate this:
        
    @ParseClassName("Person")
    public static class Person extends ParseObject {
    
        public static final String KEY_NAME = "name";
    
        public static final String KEY_SURNAME = "surname";
    
        public static final String KEY_BIRTH_DATE = "birthDate";
    
        public static final String KEY_PHOTO = "photo";
    
        static {
            ParseObject.registerSubclass(Person.class);
        }
        
        public Person() {}
    
        public final static ParseQuery<Person> getQuery() {
            return ParseQuery.getQuery(Person.class);
        }
    
        public final void setName(String name) {
            put(KEY_NAME, name);
        }
    
        public final String getName() {
            return getString(KEY_NAME);
        }
    
        public final void setSurname(String surname) {
            put(KEY_SURNAME, surname);
        }
    
        public final String getSurname() {
            return getString(KEY_SURNAME);
        }
    
        public final void setBirthDate(Date birthDate) {
            put(KEY_BIRTH_DATE, birthDate);
        }
    
        public final Date getBirthDate() {
            return getDate(KEY_BIRTH_DATE);
        }
    
        public final void setPhoto(ParseFile photo) {
            put(KEY_PHOTO, photo);
        }
    
        public final ParseFile getPhoto() {
            return getParseFile(KEY_PHOTO);
        }
    }

Usage
=====
*For a working implementation of this library see the `sample/` folder.*

There are two annotations in the library: `@ParseClass` and `@ParseWrapperClass`. You should use the latter one for your `User`, `Installation` and `Session` model (it works a bit differently).
*Classes annotated with any of those two annotations are later being called `Android-ParseModel` `builder` classes*

Create a `builder` class for any of your Parse Data classes:

  1. Create a new class.
  2. Add appropriate fields (you should be able to use any type that is available in Parse; you can also use other `builder` classes).
  3. Add an annotation `@ParseClass` to your class.
  4. Hit `Build -> Rebuild Project`
  5. And you're done... You can reference it with `ParseModel.<BuilderClassName>`.


        @ParseClass
        public class Person {
            String name;
            String surname;
            Date birthDate;
            ParseFile photo;
        }
    
   after `Rebuild` you can use `ParseModel.Person` class. E.g. you can call:
   
        ParseModel.Person person = new ParseModel.Person();
        person.setName("Bartosz");
        person.saveInBackground();

If you want to generate a `ParseModel` class for your `Parse` `User`, `Installation` or `Session` follow the exact same steps but use `@ParseWrapperClass` annotation (instead of  `@ParseClass`). This annotation generates code a bit differently. E.g. this `User` `builder` class:

    @ParseWrapperClass(ParseUser.class)
    public class User {
        String fullName;
        int age;
    }

will result in generation of this:

    public static class User {
        public static final String KEY_FULL_NAME = "fullName";
    
        public static final String KEY_AGE = "age";
    
        private final ParseUser mParseUser;
    
        public User(ParseUser parseUser) {
          mParseUser = parseUser;
        }
    
        public static User with(ParseUser parseUser) {
          return new User(parseUser);
        }
    
        public ParseUser get() {
          return mParseUser;
        }
    
        public static User getCurrent() {
          return ParseUser.getCurrentUser() != null ? User.with(ParseUser.getCurrentUser()) : null;
        }
    
        public final void setFullName(String fullName) {
          mParseUser.put(KEY_FULL_NAME, fullName);
        }
    
        public final String getFullName() {
          return mParseUser.getString(KEY_FULL_NAME);
        }
    
        public final void setAge(int age) {
          mParseUser.put(KEY_AGE, age);
        }
    
        public final int getAge() {
          return mParseUser.getInt(KEY_AGE);
        }
    }
        
Usage notes
-----------

The library assumes two things considering naming conventions:
 
  1. *Classes* should be named using `UpperCamelCase` ("ThisIsAnExample")
  2. *Fields* should be named using `LowerCamelCase` ("thisIsAnExample")
  
**It doesn't mean the library won't work if you name your classes/fields differently!**
It can just case some unexpected method names (it depends on the `quava` methods behaviour).

Including In Your Project
-------------------------
Add this to your **project** gradle dependencies:

```xml
dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
}
```

In your **module** (application) gradle add (right after the `'com.android.application'`)

```xml
apply plugin: 'com.neenbedankt.android-apt'
```

and dependencies (also in your application gradle):

```xml
dependencies {
    compile 'com.bartoszlipinski:parsemodel:0.0.4'
    apt 'com.bartoszlipinski:parsemodel-compiler:0.0.4'
}
```

Developed by
============
 * Bartosz Lipiński

License
=======

    Copyright 2015 Bartosz Lipiński
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.