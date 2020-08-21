#!/bin/bash
echo "Hello"

FDIR="io-files/"

echo "Name of input file in ./io-files"
read input_filename
echo "Name of output file in ./io-files"
read output_filename
echo "Perform benchmarking tests? (y/n)"
read b

if [ $b == "y" ]
then
	echo "Running TerrainClassify.main()"
	java -cp ./bin TerrainClassify $FDIR$input_filename $FDIR$output_filename -b
elif [ $b == "n" ]
then
	echo "Running TerrainClassify.main()"
	java -cp ./bin TerrainClassify $FDIR$input_filename $FDIR$output_filename
else
	echo "Invalid input. Please rerun."
fi

