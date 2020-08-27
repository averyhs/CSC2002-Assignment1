# CSC2002 Assignment 1

## Running the program
The best way to run the program is: 
1. Copy input files to the io-files directory
2. From the root directory run `make run`
3. Follow the prompts

## Make options
* `compile` Compiles java class files
* `docs` Generates javadocs
* `clean` Removes all class files from the bin directory
* `clean-docs` Removes all the docs files in the doc directory
* `run` Runs a shell script that gets input and then runs the `main()` method.

## Notes

### Documentation
The javadocs for these classes are written in a way that (hopefully) shows my approach to the problem and describes some implementation details.

### Error checking
This is not a robust application. It was written mainly to experiment with parallel programming on a few standard test data sets. Because of this, I didn't invest much time in error handling within the application. I assume that a perfect user will be providing perfect commands and perfect files. 
