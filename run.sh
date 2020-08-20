#!/bin/bash
echo "Hello"

FDIR="io-files/"

echo "Name of input file in ./io-files"
read input_filename
echo "Name of output file in ./io-files"
read output_filename

echo "Running TerrainClassify.main()"
java -cp ./bin TerrainClassify $FDIR$input_filename $FDIR$output_filename
