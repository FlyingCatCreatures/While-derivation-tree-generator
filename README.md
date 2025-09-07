
# While Derivation Tree Generator

This project generates derivation trees for programs written in a simple While language.

## Language Specification and Grammar

**Note:** This project uses a slightly modified grammar for easier use of ASCII symbols. The changes include:

- The negation operator uses `!` instead of the standard symbol.
- The less than or equal to operator uses `<=`.
- The conjunction operator uses `&`.
- Explicitly allow grouping using parentheses.

Below is the full specification used:

```
S ::=   x := a | 
	skip | 
	S1;S2 | 
	if b then S1 else S2 |
	while b do S |
	(S)
a ::=   n | 
	x | 
	a1 + a2 | 
	a1 * a2 | 
	a1 - a2 |
	(a)
b ::=   true |
	false | 
	a1 = a2 | 
	a1 <= a2| 
	!b | 
	b1 & b2 |
	(b)
```

## Usage

To run the program, use the following command:

```sh
java -cp build/classes/java/main while_language.Main WHILE_FILE \[--pdf-maxwidth x\]
```

Where `WHILE_FILE` is the name of a file located in the `input_files` directory and `x` is an integer, which will eventually be the max width of the produced document in cm.

You don't need to build the program, as the pulled repository already has an up to date build, but you want to you can by doing:
```sh
./gradlew build
```

## Important Notes

- **Syntax Feedback:** The feedback for incorrect syntax is currently not very helpful. The program simply throws an exception and exits. I might improve this in the future.
- **Bugs:** There may definitely be bugs in the program. If you encounter any issues, please report them and include the input file you used to help with debugging.


