Android-ParseModel
==================

[![License](https://img.shields.io/github/license/blipinsk/RecyclerViewHeader.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.bartoszlipinski/parsemodel.svg)](https://bintray.com/blipinsk/maven/Android-ParseModel)

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

    public final class ParseModel {
        
        @ParseClassName("Person")
        public static final class Person extends ParseObject {
        
            public static final String KEY_NAME = "name";
        
            public static final String KEY_SURNAME = "surname";
        
            public static final String KEY_BIRTH_DATE = "birthDate";
        
            public static final String KEY_PHOTO = "photo";
        
            static {
                ParseObject.registerSubclass(Person.class);
            }
        
            public static ParseQuery<Person> getQuery() {
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
    }

        

About the release
=================
(or in other words: why is it marked as 'alpha'?)
-------------------------------------------------

TODO


Usage
=====
*For a working implementation of this library see the `sample/` folder.*

There are two annotations in the library: `@ParseClass` and `@ParseUserClass`. You should use the latter one for your `User` model (it works a bit differently).
*Classes annotated with any of those two annotations are being later called `Android-ParseModel` `builder` classes*

Create a `builder` class for any of your Parse Data classes:

  1. Create a new class.
  2. Add appropriate fields (you should be able to use any type that is available in Parse; you can also use other `builder` classes).
  3. Add an annotation `@ParseClass` (or `@ParseUserClass`) to your class.
  4. Hit `Build -> Rebuild Project`
  5. And you're done... You can reference it with `ParseModel.<BuilderClassName>`.


        @ParseClass
        public class Person {
            String name;
            String surname;
            Date birthDate;
            ParseFile photo;
        }
    
   after `Rebuild` you can call e.g.: `ParseModel.Person.getName();`

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
    compile 'com.bartoszlipinski:parsemodel:0.0.2-alpha'
    apt 'com.bartoszlipinski:parsemodel-compiler:0.0.2-alpha'
}
```

***additionally***: *(if the current version is not available in central repository yet) add this as well:*

```xml
repositories {
    maven {
        url 'https://dl.bintray.com/blipinsk/maven/'
    }
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
