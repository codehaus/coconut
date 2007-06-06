Coconut Cache - A caching framework.

Last updated 5. June 2007, Copyright 2004-2007 Kasper Nielsen

This is a small overview of the various projects in Coconut Cache.

## Subprojects
The repository is organised into the following major components.

- coconut-cache-api        This is the main api for coconut cache
- coconut-cache-examples   This is where all the examples are located
- coconut-cache-impl       This is the implementation of coconut cache
- coconut-cache-policies   This is where the different cache replacement policies are located
- coconut-cache-test        This is where all the test suites are located for the various cache implementations.


## Minor Subprojects
The repository also contains a number of minor components. That are actively developed
- coconut-cache-standalone This is used to build a single jar
- coconut-cache-analyzer   Used to analyze patterns of cache access in order to optimize prefetching and the active cache policy
- coconut-cache-benchmark  Used for benchmarking Coconut Cache as well as 'competing' products
- coconut-cache-examples   Contains a number of examples to highlight the use of Coconut Cache
- coconut-cache-pocket     A small standalone caching library
- coconut-cache-sandbox    Various classes not yet merged into the main branch
- coconut-cache-store      Storage implementations for Coconut Cache

## Build instructions and Artifacts
To build coconut cache stand in the /coconut-cache and execute
> mvn install                                (requires Maven 2)

The following artifacts are produced when building Coconut Cache
