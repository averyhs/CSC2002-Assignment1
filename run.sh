#!/bin/bash
echo "Hello"

echo "path to input file (relative to ./io-files)"
read input_filename
echo "path to output file (relative to ./io-files)"
read output_filename

cd bin
echo "Running TerrainClassify.main()"
java TerrainClassify $input_filename $output_filename
