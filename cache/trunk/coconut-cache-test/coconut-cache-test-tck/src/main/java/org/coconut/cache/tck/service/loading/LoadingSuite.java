package org.coconut.cache.tck.service.loading;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ImplicitLoading.class, ExplicitLoading.class, FutureLoading.class })
public class LoadingSuite {}
