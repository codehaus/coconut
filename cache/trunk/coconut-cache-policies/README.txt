Coconut Cache Policy- Implementations of various replacement policies

Last updated 14. January 2007, Copyright 2004-2007 Kasper Nielsen








##IDEAS
* Add a getMisses() in Hitable, for example we want to keep entries, 
  that have lots of misses

* Perhaps should ReplacementPolicy.peek() just throws UnsupportedOperation() 
  if it cannot be supported in less then O(n)??