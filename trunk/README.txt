Coconut - A Framework for event-driven and highly concurrent services.

Last updated 8 June 2006, Copyright 2004-2006 Kasper Nielsen

This is a small overview of how the source repository is organised, and how to build Coconut 5.0.

## Repository organisation
The repository is organised into the following components.

- coconut-core   Contains core interfaces (used throuhout most of the other projects),
				 Filters : Definitions

- coconut-aio

- coconut-cache

- coconut-event

- coconut-sandbox          This is where experimental code is placed

- coconut-test-framework   This framework is used for testing all coconut projects, its based on a cvs version of junit 4

- site                     (not currently present) This is where the web site for www.coconut.org is generated from.



## Prerequisites

* JDK 5.0 or later 
* Maven 2 (maven.apache.org)
* SVN (optional, for checking out the source distribution)

## Building Coconut
By coming to this step we assume that you have already gotten hold of all files. If this is not the case you need to run svn ....

In order to build Coconut you need to stand in the top level directory coconut-5.0 (the directory where this file is located) and run
> mvn install
This will build all Coconut modules and run all tests associated with each component.

You can also build parts of the framework, for example, if you just need to build Coconut Cache you can chdir into coconut-cache and run 
> mvn install

##License
see LICENSE.txt in this folder