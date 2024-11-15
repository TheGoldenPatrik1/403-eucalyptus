# Eucalyptus Overview

![Eucalyptus Logo](logo.jpg)

_When misused, Eucalyptus is toxic. But with proper patience and contemplation, it can be a vibrant and powerful tool._

## Creating Eucalyptus

The idea for Eucalyptus originated from taking the features that we enjoyed from other languages and combining them into one language. We also wanted to add some features that we wished other languages implemented.

We started with an emphasis on procedural programming. We wanted a language similar to Lisp that would hav single-line statements with no semicolons. One of the keys to Eucalyptus is that it is a keyword- and operator-free language. Everything is done using predefined functions. This helps eliminate various problems, such as the common mistake of using operators for assignment versus comparison.

Another emphasis of Eucalyptus is being a case-opinionated language. We started with the idea of forcing function names to be camelCase and took it one step further.

We decided that Eucalyptus should use case to determine between a variable and function. This allows for variables and functions to be stored in two separate lists, increasing the efficiency of the language when a program contains many variables and functions. Furthermore, constants and mutable variables have different cases, removing the need for a keyword to determine whether a variable can be changed. Using this idea, a user can use one global function def() to define a constant, mutable variable, or function.

Finally, we wanted a feature to help users debug their programs. We decided to create a debug log that allows users to view whenever a variable's value is changed. The log informs the user what line the variable changed on and which, if any, function the line is in. The logs also contain a list of variables that are defined but are never used after the program has ran.

## Using Eucalyptus

There are three major data types in Eucalyptus. The first is literals: Numbers, Strings, Booleans, Lists (a group of any of the previous literals), and Dicts (key-value pairs, where the key is a string and the value is a literal). The second data type is functions. These are callable items, both global and user-defined. Function names must all be camel case (myFunction()). The third data type is variables. Essentially, this is anything that is not a literal or function that stores a value. Mutable variables must be snake case (my_variable) and constants must be screaming snake case (MY_CONSTANT).

Users who plan properly and work slowly will find the most success when it comes to using Eucalyptus. Users should become accustomed to the global functions within Eucalyptus.

TODO: Add a list of global functions with what they do and how to use them.

## Example Programs

The classic hello world problem is very simple in Eucalyptus. It's simply a single function call:

```
print("Hello, World!")
```

TODO: Add more example programs with explanation.
