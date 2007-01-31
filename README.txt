Coconut - A Framework for event-driven and highly concurrent services.

Last updated 31 January 2007, Copyright 2004-2007 Kasper Nielsen

This is a small overview of how the source repository is organised, and how to build it.

## Repository organisation
The repository is organised into the following components.

- cache            Coconut Cache
                   svn co https://svn.codehaus.org/coconut/cache/trunk to work on trunk
                   
- core             Core interfaces, used in most of the other projects
                   svn co https://svn.codehaus.org/coconut/core/trunk to work on trunk

- event            Coconut Event Framework
                   svn co https://svn.codehaus.org/coconut/cache/trunk to work on trunk
                   
- internal         Internal classes and some build tools, you will most likely never need
                   to check these out

- management       Coconut Management
                   svn co https://svn.codehaus.org/coconut/management/trunk to work on trunk

- pom              The root pom for coconut project, you will most likely never need
                   to check these out
                   
- sandbox          This is where experimental code is placed

- test-framework   This framework is used for testing most coconut projects
                   Most likely you will not need to check this out

- trunks           Contains the main trunk of coconut-cache, coconut-core, coconut-event
                   and coconut-management
                   svn co https://svn.codehaus.org/coconut/trunks to work on trunk
                   
- site                     (not currently present) This is where the web site for www.coconut.org is generated from.




## Building Coconut

1. Prerequisites for building Coconut
  * JDK 5.0 or later 
  * Maven 2 (maven.apache.org)
  * SVN (optional, for checking out the source distribution)

2. Checking out the source
> svn co http://svn.codehaus.org/coconut/trunks
 As an alternative nightly sources can be downloaded from ......
 
3. Building Cocount
In order to build Coconut you need to stand in the top level directory trunks and run
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
