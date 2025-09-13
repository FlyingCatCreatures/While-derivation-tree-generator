
# While Derivation Tree Generator

This project generates derivation trees for programs written in a simple While language.

## Language Specification and Grammar

**Note:** This project uses a slightly modified grammar compared to the course itself, such that it is easier to use. Mainly this was replacing characters with more convenient ones, like:
- The negation operator (`!`).
- The less than or equal to operator (`<=`).
- The conjunction operator (`&`).

Other than that there are two more changes:
- Grouping of statements using parentheses is made explicit in the grammar rather than being implicit
- There is a way to comment code by use of the '%' character. There are no multiline comments though. 

Below is the full grammar specification used:

```
S ::=   x := a | 
	skip | 
	S1;S2 | 
	if b then S1 else S2 |
	while b do S |
    repeat S until b |
    break |
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
java -cp build/classes/java/main while_language.Main WHILE_FILE 
```

Where `WHILE_FILE` is the name of a file located in the `input_files` directory. There are also two optional flags:
```sh
--pdf-maxwidth x
--init-state n var_1 val_1 var_2 val_2 ... var_n val_n
```
Where x, n, and all the val's are integers. The former sets the max width of the eventually produced pdf of the derivation tree, and the latter defines a starting state to execute the program in.

You don't need to build the program just to use it, as the repository already should already have an up to date build. Should you want to you though, you can do so by running `./gradlew build`. This only works if you have a valid Java Development Kit and gradle installed. Doing this also runs the tests in `src/test`.

## Important Notes

- **Syntax Feedback:** The feedback for incorrect syntax is currently not very helpful. The program simply throws an exception and exits. I might improve this in the future.
- **Bugs:** There may definitely be bugs in the program. If you encounter any issues, please report them and include the input file you used to help with debugging.


