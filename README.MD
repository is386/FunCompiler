# CS441 Compiler

## Dependencies

- `Java 11`

### Java Libraries

- `org.json`

## Usage

`./comp <FLAGS> < p.441 > p.ir`

To use this program, just run the provided bash script, and redirect your source language input. If no input is given, you can type in your source code, and press enter to continue.

### Optional Flags

- `-noopt`: Turns off peephole optimizations.

- `-oldssa`: Uses the unoptimized version of SSA.

- `-novn`: Turns off value numbering.

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
