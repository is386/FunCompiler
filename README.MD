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

## Milestone 1

For milestone 1, I was able to implement everything that was asked:

- `src/parse` contains all the code that does the parsing. `Parser` does all the parsing and returns an `AST` object. This object is the root of the AST and contains a list of all classes, statements, and local variables.

- `src/ast` contains all the nodes of the AST, which include class declarations, method declarations, statements, and expressions.

- `src/cfg` contains the basic blocks, IR statements, and primitives used in the target language. `CFGBuilder` takes in an `AST` object and outputs a `CFG` object. It uses the visitor design pattern to visit each node in the AST and generate the associated basic blocks, IR statements and primitives. Once all the blocks are generated, it connects the graph by setting parents and children for each basic block. My implementation sometimes creates unreachable basic blocks. The `CFGBuilder` deletes these blocks after it has connected the graph. The `CFG` object contains a list of basic blocks, and the variables used in the program.

- `src/ssa` contains the code that transforms the CFG into SSA form. `SSATransformer` is a visitor that visits each block, IR statement, and primitive in the CFG. It will then keep track of the versions of each variable in each block, and insert phi nodes into the necessary blocks. It then calls the visitor `VersionSetter`, which goes through and sets the versions for each variable in each block based on what was found in the SSA phase.

- `src/visitor` contains the base visitor classes used to traverse the AST and CFG.

### Peephole Optimization

For my peephole optimization, I chose to not tag check `%this`. The source code for this optimization is a bit spread out. Basically, each primitive in my CFG has one of two types `INTEGER`, `THIS`, or `UNDECLARED`. Integers and arithmetic primitives are the only `INTEGER` types, the rest are `UNDECLARED`. When a variable is set to a primitive it inherits the type. This way, all I have to do is check if a variable is `UNDECLARED`. If so, I do a tag check on it. A simple example of this can be seen on line 319 in `CFGBuilder`, where I check the type of a variable before printing it. This made the peephole optimization very trivial. Every time I generate a `ThisPrimitive` object, I set its type to `THIS`, instead of `UNDECLARED`. This means that `%this` bypasses all tag checks, since it is not of the `UNDECLARED` type. You can see the type being set on line 725 of `CFGBuilder`, when I create the primitive for the expression `%this`.

My examples for this peephole optimization are located in `samples/`. They are all basic examples where the `%this` variable is used to call a field, call a method, or update a field. With this optimization turned on, `%this` is never tag checked.

## Milestone 2

For milestone 2, I was able to implement everything that was asked:

### Optimized SSA

I was able to implement the optimized SSA algorithm on top of my unoptimized SSA. You can find the code in `src/ssa/SSAOptimized.java`. The optimization starts on line 115. It uses mostly the same code as the unoptimized version, however I changed where how the phi node locations are determined. This leads to a reduction in phi nodes where they are completely unnecessary. The program `samples/ssa.441` shows this. Here is a comparison of the unoptimized vs optimized versions:

#### Not Optimized:

```
l1:
    %x2 = phi(mA, %x1, l6, %x2)
    %cnt1 = phi(mA, %cnt0, l6, %cnt2)
    %y2 = phi(mA, %y1, l6, %y2)
    %z2 = phi(mA, %z1, l6, %z2)
    %1 = %cnt1 & 1
    if %1 then l2 else badNumber
```

#### Optimized:

```
l1:
    %cnt0 = phi(mA, %cnt0, l6, %cnt1)
    %1 = %cnt0 & 1
    if %1 then l2 else badNumber
```

This is just a portion of the compiled program, however it is enough to see that the optimized version removes the phi functions for `%x`, `%y`, and `%z`, as those variables are not used. Note that this code was run without value numbering, so the phi function parameters might vary slightly.

### Value Numbering

I was able to implement the algorithm for Global Value Numbering. However, my code is a bit messy and unoptimized, due to design choices made in milestone 1. The code is located in `src/vn/ValueNumbering.java`. I am able to find and replace redundant arithmetic operations throughout a program. I tested this using the `perf` command in `ir441`. Some issues I have noticed is that my VN will remove assignments correctly, but leave some blocks with no statements, and only a control statement. In most cases, I leave this alone, however in the future, I might change it so that these singular control statements are jumps. I was also able to implement the following 3 identities:

- `x = y - y = 0`

- `x = y + 0 = y`

- `x = y & y = y`

You can find an example that utilizes all three of these identities in `samples/vn.441`. Overall, my VN replaces all of the identities and reduces the number of operations required:

#### No Value Numbering:

```
main:
    %x0 = 1
    %y0 = 1
    %1 = %y0 & 1
    if %1 then l1 else badNumber
l1:
    %2 = %y0 & 1
    if %2 then l2 else badNumber
l2:
    %3 = %y0 - %y0
    %x0 = %3 + 1
    %4 = %x0 & 1
    if %4 then l3 else badNumber
l3:
    %5 = %x0 / 2
    print(%5)
    ret 1
badNumber:
    fail NotANumber
```

As you can see, in `l2` we see the identity `%y0 - %y0`. We want to replace this with `0`. By replacing that with zero, the following line matches an identity as well. It becomes `%x0 = 0 + 1`. This line gets updated to be `%x0 = 1`. This again changes the following line. It should now be `%4 = 1 & 1`, which is the third identity. So it becomes `%4 = 1`. This actually creates a bunch of unused assignments, that will later be removed. As a bonus, I replace conditional control statements with jumps if the condition is equal to `1`. So `if %4 then l3 else badNumber` becomes `if 1 then l3 else badNumber`, which becomes `jump l3`. This all results in the following program:

#### Value Numbering:

```
main:
    %x0 = 1
    %y0 = 1
    jump l1
l1:
    jump l2
l2:
    jump l3
l3:
    %5 = 1 / 2
    print(%5)
    ret 1
badNumber:
    fail NotANumber
```

As you can see, most of the operations are removed. This is because of the identity matching I discussed earlier. Due to that identity matching, there were many unused assignments that are removed. This also leads to all the conditional control statements becoming jumps. This greatly reduces the number of operations in the original program.

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

## Milestone 4

For milestone 4, I chose to implement the garbage collection cooperation:

### Garbage Collection 

All of the code for this is located in a single function in `src/cfg/CFGBuilder.java`. It starts on line 360 and I have marked the relevant sections with comments. First I compute the GC map using the data I collected during the type checking phase. Then I get the address of slot -1 and store the GC map there. Through some testing, it seems to work well. I have enclosed a test program `samples/gc.441`. It runs a loop that allocates a class 100 times. This code causes an out of memory error without garbage collection enabled.
