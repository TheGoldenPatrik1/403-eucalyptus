# Eucalyptus Grammar

The foundational principle of Eucalyptus is that every statement is a function call. As such, the start stymbol of the language, `S`, denotes that a program in the language consists of one or more function calls:

```
S -> function-call S | function-call
```

Function calls can be divided into two categories: user-defined and global-defined:

```
function-call -> user-defined-function | global-defined-function
```

These two types of functions can be defined as follows:

```
user-defined-function -> function-name "(" arguments ")"
global-defined-function -> reserved-function-name "(" arguments ")"
```

The full list of reserved-function-names is drawn from [reserved_functions.txt](/src/reserved_functions.txt). Suffice it to say that:

1. Both reserved-function-names and function-names must be camelCase
2. A function-name cannot be contained within the list of reserved-function-names

Grammatically, this looks like the following, where the `-` operator denotes the subtraction all elements in rule B from rule A:

```
reserved-function-name -> "add" | ... | "defFunction"
function-name -> camel-case - reserved-function-name
```

Further, camel-case is defined as:

```
camel-case -> lowercase-letter (letter-or-digit)*

lowercase-letter -> "a" | "b" | "c" | ... | "z"
letter-or-digit -> letter | digit
letter -> lowercase-letter | uppercase-letter
uppercase-letter -> "A" | "B" | "C" | ... | "Z"
digit -> "0" | "1" | "2" | ... | "9"
```

Now that the two types of function names are defined, one must turn to the arguments, which are an optional comma-separated list:

```
arguments -> argument | argument "," argument | ε
```

Next, it is time to iron out what an individual argument can consist of. This is where the majority of the remaining rules from the language come into play.

Specifically, arguments can consist of one of the three fundamental expression-types of Eucalyptus:

```
argument -> function-call | variable | literal
```

As function-call has already been defined, all that remains are variables and literals. Variables fall into two categories:

```
variable -> mutable-variable | constant-variable
```

As ought to be self-evident, constant-variables cannot be changed after their declaration, while mutable-variables can be. This distinction is grammatically denoted by their case:

```
mutable-variable -> snake-case
constant-variable -> screaming-snake-case
```

These cases - snake\_case and SCREAMING\_SNAKE\_CASE - are defined as follows:

```
snake-case -> lowercase-letter ("_" | lowercase-letter-or-digit)*
screaming-snake-case -> uppercase-letter ("_" | uppercase-letter-or-digit)*
```

Some of those rules have already been defined from the camel-case rule, but it is necessary to add the following rules:

```
lowercase-letter-or-digit -> lowercase-letter | digit
uppercase-letter-or-digit -> uppercase-letter | digit
```

That concludes the definition of variables. It is now time to define literals. Literals can be divided into five main categories, with `null` serving as the catch-all:

```
literal -> string | number | boolean | list | dict | "null"
```

Strings are delineated by a matching pair of quotation marks, either double or single:

```
string -> double-quotation (character-except-double-quotation)* double-quotation
string -> single-quotation (character-except-single-quotation)* single-quotation
```

The characters within a string can be anything except the given quotation-mark and will be denoted as follows, where the `.` operator refers to any character:

```
double-quotation -> '"'
character-except-double-quotation -> . - double-quotation

single-quotation -> '"'
character-except-single-quotation -> . - single-quotation
```

Although escaping characters is not a part of the Eucalyptus grammar at this time, the abillity to use either single or double quotes, when combined with string concatenation functions, makes it possible to generate any possible string, including those containing both single and double quotation marks.

Next, numbers can be divided into two categories:

```
number -> unsigned-number | signed-number
```

A signed-number is simply an unsigned-number preceded by a minus sign:

```
signed-number -> "-" unsigned-number
```

When defining unsigned-numbers, another broad divide is created:

```
unsigned-number -> integer | float
```

Integers and floating point numbers are common concepts, but for the sake of thoroughness, they can be defined as follows:

```
integer -> digit (digit)*
float -> integer "." integer
```

The next literal, booleans, is perhaps the easiest to define:

```
boolean -> "true" | "false"
```

Next is the definition of lists, which are comma-separated linear groupings of items, denoted by square brackets. In terms of what has already been defined, this is simply a pair of square brackets containing arguments.

Surprisingly, there's no grammatical difference between the argument list for a function and the list items in a true list. In fact, lists could really just be a global-defined-function. For instance, Eucalyptus could just as well have `defList(x, 1, 2, 3)` instead of `def(x, [1, 2, 3])`. Although this is possible and would simplify the grammar, it would be very clunky. Moreover, it would be inconsistent with Eucalyptus's design principle that everything can be boiled down to either data (numbers, strings, lists, etc.) or operations upon data (function calls).

Regardless, the final definition for a list is a simple one:

```
list -> "[" arguments "]"
```

That leads to the final literal type, dictionaries - dicts for short. Dicts consist of comma-separated key-value pairs, where the key is a string and the value is an argument (i.e., a function-call, variable, or literal). The end result looks very much like [JSON](https://www.json.org/json-en.html):

```
dict -> "{" dict-items "}"

dict-items -> dict-item | dict-item "," dict-item | ε
dict-item -> string ":" argument
```

This concludes the grammar for Eucalyptus.