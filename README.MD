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

## Milestone 4

For milestone 4, I chose to implement the garbage collection cooperation:

### Garbage Collection 

All of the code for this is located in a single function in `src/cfg/CFGBuilder.java`. It starts on line 360 and I have marked the relevant sections with comments. First I compute the GC map using the data I collected during the type checking phase. Then I get the address of slot -1 and store the GC map there. Through some testing, it seems to work well. I have enclosed a test program `samples/gc.441`. It runs a loop that allocates a class 100 times. This code causes an out of memory error without garbage collection enabled.