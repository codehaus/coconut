Coconut Cache - A caching framework.

Last updated 5. June 2007, Copyright 2004-2007 Kasper Nielsen

This is a small overview of the various projects in Coconut Cache.

## Subprojects
The repository is organised into the following major components.

- coconut-cache-api        This is the main api for coconut cache
- coconut-cache-archetypes This directory contains Maven archetypes
- coconut-cache-examples   Contains a number of examples to highlight the use of Coconut Cache
- coconut-cache-impl       This is the implementation of coconut cache
- coconut-cache-policies   This is where the implementation of different cache replacement policies lives
- coconut-cache-standalone This is used to build a single jar, and the pom that people normally will depend on
- coconut-cache-test       This is where all the test suites and benchmarks are located for the various cache implementations.

## Sandbox projects
These projects are currently located in the sandbox http://svn.coconut.codehaus.org/browse/coconut/sandbox/coconut-cache
- coconut-cache-analyzer   Used to analyze patterns of cache access in order to optimize prefetching and the active cache policy
- coconut-cache-pocket     A small standalone caching library
- coconut-cache-sandbox    Various classes not yet merged into the main branch
- coconut-cache-store      Storage implementations for Coconut Cache

## Build instructions and Artifacts
To build coconut cache stand in the /coconut-cache and execute
> mvn install                                (requires Maven 2)

The following artifacts are produced when building Coconut Cache
