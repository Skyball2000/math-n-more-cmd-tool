# Yan's Command Line Tool
A command line tool that can perform a variety of different tasks, mainly mathematics.

## Syntax:
- `math`
  - `ta,tautologie [expression]`  
    Checks if the given expression is a tautologie.
  - `tr,truth [expression:string]`  
    Generates a truth table to a given expression.
  - `eq,equals`  
    Checks if two expressions lead to the same truth table. Provide expressions with parameters -p1 and -p2.
  - `va,variables [expression:string]`  
    Extracts the variables from a given expression.
  - `trb,truthbuilder`  
    Build yourself a truth table from several expressions.

## Examples
Display a truth table from an expression:
```
> math tr (A => B) AND NOT C
(A => B) AND !C
 A ║ B ║ C ║ out
═══╬═══╬═══╬═════
 0 ║ 0 ║ 0 ║ 1
 0 ║ 0 ║ 1 ║ 0
 0 ║ 1 ║ 0 ║ 1
 0 ║ 1 ║ 1 ║ 0
 1 ║ 0 ║ 0 ║ 0
 1 ║ 0 ║ 1 ║ 0
 1 ║ 1 ║ 0 ║ 1
 1 ║ 1 ║ 1 ║ 0
```
Build a truth table from multiple expressions:
```
> math trb
Enter the input variables, split by a space character:
> A B
Enter one expression per line, leave empty to stop. To assign a new variable, enter [VAR] = [EXPR].
> P = NOT A AND B
> Q = P OR A
>
 A ║ B ║ P = !A AND B ║ Q = P OR A
═══╬═══╬══════════════╬════════════
 0 ║ 0 ║ 0            ║ 0
 0 ║ 1 ║ 1            ║ 1
 1 ║ 0 ║ 0            ║ 1
 1 ║ 1 ║ 0            ║ 1
```

Building a truth table for the expressions:

- `((A AND B) OR (A AND C) OR (B AND C))`
- `((A AND B) OR (C AND NOT (A <=> B)))`

```
> math -trb
Enter the input variables, split by a space character:
 > A C B
Enter one expression per line, leave empty to stop. To assign a new variable, enter [VAR] = [EXPR]. Use [undo] and [restart] to control the input.
 > A AND B
 > restart
Restarting multiline input:
 > P = A AND B
 > Q = A AND C
 > R = B AND C
 > S = NOT (A <=> B)
 > T = C AND S
 > f = P OR Q OR R
 > g = P OR T
 >
 A ║ B ║ C ║ P = A AND B ║ Q = A AND C ║ R = B AND C ║ S =  !(A <=> B) ║ T = C AND S ║ f = P OR Q OR R ║ g = P OR T
═══╬═══╬═══╬═════════════╬═════════════╬═════════════╬═══════════════════╬═════════════╬═════════════════╬════════════
 0 ║ 0 ║ 0 ║ 0           ║ 0           ║ 0           ║ 0                 ║ 0           ║ 0               ║ 0
 0 ║ 0 ║ 1 ║ 0           ║ 0           ║ 0           ║ 0                 ║ 0           ║ 0               ║ 0
 0 ║ 1 ║ 0 ║ 0           ║ 0           ║ 0           ║ 1                 ║ 0           ║ 0               ║ 0
 0 ║ 1 ║ 1 ║ 0           ║ 0           ║ 1           ║ 1                 ║ 1           ║ 1               ║ 1
 1 ║ 0 ║ 0 ║ 0           ║ 0           ║ 0           ║ 1                 ║ 0           ║ 0               ║ 0
 1 ║ 0 ║ 1 ║ 0           ║ 1           ║ 0           ║ 1                 ║ 1           ║ 1               ║ 1
 1 ║ 1 ║ 0 ║ 1           ║ 0           ║ 0           ║ 0                 ║ 0           ║ 1               ║ 1
 1 ║ 1 ║ 1 ║ 1           ║ 1           ║ 1           ║ 0                 ║ 0           ║ 1               ║ 1
```

## Build the tool yourself

1. Clone and install the [org.snim2 tautology-checker](https://github.com/snim2/tautology-checker) using Maven
2. Clone and install the [steos jnafilechooser](https://github.com/steos/jnafilechooser.git) using Maven
3. Build this project using Maven