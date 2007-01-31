Coconut - A Framework for event-driven and highly concurrent services.

Last updated 31 January 2007, Copyright 2004-2007 Kasper Nielsen

This is a small overview of how the source repository is organised, and how to build it.

## Repository organisation
The repository is organised into the following components.

- cache

- core   Contains core interfaces (used throuhout most of the other projects),
				 Filters : Definitions
- coconut-utils

- event

- internal

- management 

- pom



- sandbox          This is where experimental code is placed

- test-framework   This framework is used for testing most coconut projects
                   Most likely you will not need to check this out

- trunks                   
- site                     (not currently present) This is where the web site for www.coconut.org is generated from.




## Building Coconut

1. Prerequisites for building Coconut
  * JDK 5.0 or later 
  * Maven 2 (maven.apache.org)
  * SVN (optional, for checking out the source distribution)

2. Checking out the source
> svn co http://svn.codehaus.org/coconut/trunk
 As an alternative nightly sources can be downloaded from ......
 
3. Building Cocount
In order to build Coconut you need to stand in the top level directory coconut-5.0 (the directory where this file is located) and run
> mvn install
This will build all Coconut modules and run all tests associated with each component.

You can also build parts of the framework, for example, if you just need to build Coconut Cache you can chdir into coconut-cache and run 
> mvn install

##License
see LICENSE.txt in this folder

##Tools

#Findbugs: 
    homepage http://findbugs.sourceforge.net/index.html
mvn homepage http://mojo.codehaus.org/findbugs-maven-plugin/
