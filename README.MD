# CS441 Compiler

## Dependencies

- `Java 11`

### Java Libraries

- `org.json`

## Usage

`./comp <FLAGS> < p.441 > p.ir`

To use this program, just run the provided bash script, and redirect your source language input. If no input is given, you can type in your source code, and press enter to continue.

### Optional Flags

- `-noopt`: This flag turns off peephole optimizations.

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
