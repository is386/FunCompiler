# CS441 Compiler

## Dependencies

- `Java 11`

### Java Libraries

- `org.json`

## Usage

`./comp <FLAGS> < p.441 > p.ir`

To use this program, just run the provided bash script, and redirect your source language input. If no input is given, you can type in your source code, and press enter to continue.

### Optional Flags

- `-oldssa`: Uses the unoptimized version of SSA.

- `-novn`: Turns off value numbering.

## Milestone 3

For milestone 3, I was able to implement everything that was required.

### Type Checking

I was able to implement type checking by creating a visitor that would traverse my AST. You can find the code for the type checker in `src/types/TypeChecker.java`. It is rather simple: it traverses each node in the AST and checks if each expression and statement is well-typed. If so, the compiler will continue on to build the CFG. If the program is not well-typed, then the compiler will display an error and exit. It should be noted that my error messages are not great, but work fine for the purposes of this assignment. 

### Type-based Optimization

For type-based optimization, I needed to pass the type environment information to my `CFGBuilder`. To do so, I just pass the `TypeChecker`, which keeps track of the type environment. Then my `CFGBuilder` will call fields and methods based on the type of the variable. It also tracks the types of temporary variables, in the case that an object is instantiated in a temporary variable and a field needs to be called from it. When calling a field, updating a field, or calling a method, the builder will add a conditional that checks if the object is null. The compiled program will have no tagged integers or tag checks, and there are no fields maps. 

### Analysis

In `samples/` you will find an untyped and typed version of a counting program, where a class has a count method. This program shows all the different optimizations I was able to implement with type checking. The traces for each program are found in their respective `.trace` files. The traces show that the typed version had significantly less ALU operations (16 vs 50), and conditional branches (12 vs 44). Additionally, the output program is much more readable, since there are no tag checks and field maps. Overall, the typed verison of the program is significantly better than the untyped version.

### Issues

One weird issue I found with my compiler is that there appears to be a bug in my SSA. For some reason, my SSA will set the incorrect version for some variables in rare circumstances. So sometimes, there will be a call to a version of a variable that hasn't been initialized yet. I think removing tagged integers revealed this bug, and my SSA optimization may have inadvertently been relying on that functionality for some cases. I was not able to fix this bug in time for the due date, but I figured I wouldn't prioritize it because it effects a small number of programs and did not indicate any issue with what I was supposed to implement for this assignment. 