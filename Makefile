# Simple Makefile to compile Assignment 1 source files.
# Adapted from file uploaded to Vula site CSC2001F(2019) by P Marais.

# XXX: bin files compiled with make don't run on my pc because of different versions of JRE

BINDIR=./bin
SRCDIR=./src
DOCDIR=./doc

.SUFFIXES: .java .class

default: clean cleandocs compile docs

# General build rule: .java => .class
${BINDIR}/%.class: ${SRCDIR}/%.java
	javac $< -cp ${BINDIR} -d ${BINDIR}

# Build dependency rules
${BINDIR}/MyFiles.class: ${SRCDIR}/MyFiles.java
${BINDIR}/PointElevation.class: &{SRCDIR}/PointElevation.java
${BINDIR}/ElevationAnalysis.class: ${SRCDIR}/ElevationAnalysis.java ${BINDIR}/PointElevation.class
${BINDIR}/TerrainClassify.class: ${SRCDIR}/TerrainClassify.java ${BINDIR}/ElevationAnalysis.class ${BINDIR}/MyFiles.class

compile:
	javac -d ${BINDIR} ${SRCDIR}/*.java

clean:
	rm -f ${BINDIR}/*.class

docs:
	javadoc  -cp ${BINDIR} -d ${DOCDIR} ${SRCDIR}/*.java

cleandocs:
	rm -rf ${DOCDIR}/*
