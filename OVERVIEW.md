# Eucalyptus Overview

![Eucalyptus Logo](logo.jpg)

_When misused, Eucalyptus is toxic. But with proper patience and contemplation, it can be a vibrant and powerful tool._

## Creating Eucalyptus

The idea for Eucalyptus originated from taking the features that we enjoyed from other languages and combining them into one language. We also wanted to add some features that we wished other languages implemented.

We started with an emphasis on procedural programming. We wanted a language similar to Lisp that would hav single-line statements with no semicolons. One of the keys to Eucalyptus is that it is a keyword- and operator-free language. Everything is done using predefined functions. This helps eliminate various problems, such as the common mistake of using operators for assignment versus comparison.

Another emphasis of Eucalyptus is being a case-opinionated language. We started with the idea of forcing function names to be camelCase from Rust and took it one step further.

We decided that Eucalyptus should use case to determine between a variable and function. This allows for variables and functions to be stored in two separate lists, increasing the efficiency of the language when a program contains many variables and functions. Furthermore, constants and mutable variables have different cases, removing the need for a keyword to determine whether a variable can be changed. Using this idea, a user can use one global function def() to define a constant, mutable variable, or function.

Finally, we wanted a feature to help users debug their programs. We decided to create a debug log that allows users to view whenever a variable's value is changed. The log informs the user what line the variable changed on and which, if any, function the line is in. The logs also contain a list of variables that are defined but are never used after the program has ran.

## Using Eucalyptus

There are three major data types in Eucalyptus. The first is literals: Numbers, Strings, Booleans, Lists (a group of any of the previous literals), and Dicts (key-value pairs, where the key is a string and the value is a literal). The second data type is functions. These are callable items, both global and user-defined. Function names must all be camel case (myFunction()). The third data type is variables. Essentially, this is anything that is not a literal or function that stores a value. Mutable variables must be snake case (my_variable) and constants must be screaming snake case (MY_CONSTANT).

Because Eucalyptus is a functional language, there are no classes. However, you can use dicts to have a set of variables and functions all grouped together.

Users who plan properly and work slowly will find the most success when it comes to using Eucalyptus. Users should become accustomed to the global functions within Eucalyptus.

TODO: Add a list of global functions with what they do and how to use them.

| Function    | Description                                                                                                                                               | Implemented |
| ----------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------- |
| add         | Returns the sum of all the parameters. Also concatenates strings. (Can be as many parameters as needed)                                                   | Yes         |
| and         | Returns true if all parameters are true                                                                                                                   | Yes         |
| clock       | TODO: write in description                                                                                                                                | No          |
| dec         | Decrements first parameter by second parameter.                                                                                                           | No          |
| def         | Sets the name designated by the first parameter to the second parameter. Sets variables to values or defines functions with a list of statements.         | Yes         |
| defFunction | Specific function for defining a function. First parameter is function name, second is list of parameters, third is list of statements.                   | Yes         |
| div         | Divides first parameter by the rest of the parameters. (Can be as many parameters as needed)                                                              | No          |
| eq          | Returns true if both parameters are equal.                                                                                                                | Yes         |
| for         | Starts a for loop. First parameter is initial value, second is conditional, third is increment/decrement, fourth is list of statements.                   | No          |
| for_each    | Starts a for loop. First parameter is variable to call each object, second is list to iterate over, third is list of statements.                          | Yes         |
| get         | Returns the object in a list or dict as the first parameter at the given index of the second parameter.                                                   | Yes         |
| gt          | Returns true if first parameter is greater than the second parameter.                                                                                     | No          |
| gteq        | Returns true if first parameter is greater than or equal to the second parameter.                                                                         | No          |
| has         | Returns true if the first parameter list or dict contains the second parameter value.                                                                     | No          |
| if          | If the first parameter is true, executes second parameter's list of statements. Else, executes third parameter's list of statements.                      | Yes         |
| inc         | Increments first parameter by second parameter.                                                                                                           | Yes         |
| index       | Returns the index of a value within a list or the key within a dict. Returns -1 if value is not found. First parameter is list/dict. Second is the value. | No          |
| len         | Returns the length of the given list or dict.                                                                                                             | Yes         |
| lt          | Returns true if the first parameter is less than the second. parameter                                                                                    | Yes         |
| lteq        | Returns true if the first parameter is less than or equal to the second.                                                                                  | No          |
| mod         | Returns the remainder of integer division of the parameters                                                                                               | No          |
| mult        | Returns the product of all parameters. (Can be as many parameters as needed)                                                                              | Yes         |
| not         | If parameter is true, returns false. If parameter is false, return true.                                                                                  | No          |
| or          | Returns true if at least one parameter is true. (Can be as many parameters as needed)                                                                     | Yes         |
| pow         | Returns the first parameter to the power of the second parameter.                                                                                         | No          |
| print       | Prints the value of the input.                                                                                                                            | Yes         |
| random      | Returns a random integer.                                                                                                                                 | No          |
| return      | Returns the value of the parameter and ends function.                                                                                                     | Yes         |
| scan        | Retrieves the first word of user input and stores it in the parameter variable.                                                                           | No          |
| scanLine    | Retrieves the line of user input and stores it in the parameter variable.                                                                                 | No          |
| sqrt        | Returns the square root of the parameter                                                                                                                  | No          |
| sub         | Returns the first parameter minus the rest of the parameters. Also, removes partial strings from the first string and values from a list/dict.            | Yes         |
| switch      | Starts a switch case. The first parameter is the object that changes and the second is a dict of possibilities with statements                            | No          |
| try         | Tries to execute the first parameter. If an error occurs, returns the second parameter.                                                                   | No          |
| type        | Returns the type of the parameter                                                                                                                         | No          |
| while       | Starts a while loop. The first parameter is the conditional, the second is the list of statements to execute.                                             | No          |

## Example Programs

The classic hello world problem is very simple in Eucalyptus. It's simply a single function call:

```
print("Hello, World!")
```

The following program demonstrates how to do basic variable assignment and arithmetic.

```
def(x, 10)
def(y, 20)
def(sum, add(x, y))
print(sum)
```

Output:

```
30
```

For a slightly more complicated program, you can implement conditionals:

```
def(x, 15)

if(lt(x, 20),
    print("x is less than twenty"),
    print("x is greater than twenty"))
```

Output:

```
x is less than twenty
```

Defining functions in Eucalyptus is as easy as variables:

```
def(greet, name,
    print(add("Hello ", name)))

greet("Eucalyptus")
```

Output:

```
Hello Eucalyptus
```

Eucalyptus can handle lists easily with loops:

```
def(numbers, [1,2,3,4,5])

forEach(num, numbers,
    print(num))
```

Output:

```
1
2
3
4
5
```

Here's how you can use dicts to represent the idea of classes in Eucalyptus:

```
def(getDogYears, age, return(mult(age, 7)))

def(buddy, {'age': 3, 'breed': "mutt", 'getDogYears': getDogYears(age)})
```

Here's how the class would look in java:

```
public class Dog {
    // Fields
    private String breed;
    private int age;

    // Constructor
    public Dog() {
        age = 3;
        breed = "mutt";
    }

    // Method to get the person's name
    public int getDogYears() {
        return age * 7;
    }
}
```

TODO: Add more example programs with explanation.
