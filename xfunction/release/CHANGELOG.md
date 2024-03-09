# Version 25

- Changing Substitutor::substitute to return list of changed files and be consistent with substitutePerLine
- Adding UriUtils
- Changing XProcess to return CompletableFuture instead of limited Future
- Implementing ImmutableMultiMap
- Fix JSON formatting for arrays inside the Collection
- Change XJson not to quote nested JSON arrays
- Fix failed test for LinesOutputStream
- Adding CommandOptions::withPositionalArguments
- Fixing bug when watchForLineInFile is stuck due to common pool run out of threads
- Adding LinesOutputStream

[xfunction-25.0.jar](https://github.com/lambdaprime/xfunction/raw/master/xfunction/release/xfunction-25.0.jar)

# Version 24

- Fixing UnsupportedAddressTypeException in FreeUdpPortIterator when run in Android
- Fixing bug in IntBitSet::intArray
- Adding TransformSubscriber and TransformPublisher
- Adding SynchronousPublisher and switch TransformProcessor to use it
- Add mask secrets support for XExec
- Allow to peek for messages received inside FixedCollectorSubscriber
- Adding tests for TransformProcessor with SameThreadExecutorService
- Print exception class name if message is empty
- Moving md5 functions to separate class
- Adding XByte::copyToByteArray for longs
- Consistent naming for ProgressMonitorInputStream fields
- Limit stacktrace warning in HttpClientBuilder
- Add XStream::of(Iterator)
- Print warning when insecure connections are used
- Fixing XByte::toInt for negative byte literals, renaming XByte::copyToByteArray, adding tests
- Updates for Preconditions: fixing compile error for equals, allow users to include TracingToken, adding more tests
- Do not collapse stack trace of TransformProcessor source to a string, as it is confusing and creates impression of one suppressed exception nested into another

[xfunction-24.0.jar](https://github.com/lambdaprime/xfunction/raw/master/xfunction/release/xfunction-24.0.jar)

# Version 23

- Update Substitutor and support multiline patterns
- Adding asString to ByteBufferUtils
- Allow CommandOptions to ignore parsing exceptions
- Updating headers
- Adding toInt, toShort to XByte and renaming castToByteArray
- Adding asBytes, minor comment improvements
- Adding isWindows
- Fixing bug in XPaths::appendToFullFileName

[xfunction-23.0.jar](https://github.com/lambdaprime/xfunction/raw/master/xfunction/release/xfunction-23.0.jar)

# Version 22

- Allow forwardStdoutAsync to save output to internal buffer to query it later +fixing race condition between stdoutAsync and stdout
- Adding support for short to XJson
- Include message to PreconditionException when isTrue fails
- Allow users to consume stderr
- Fix XJson to use null as literal based on  ECMA-404_2nd_edition_december_2017.pdf

[xfunction-22.0.jar](https://github.com/lambdaprime/xfunction/raw/master/xfunction/release/xfunction-22.0.jar)

# Version 21

- Adding appendToFullFileName
- Extracting retryIndefinitely to RetryableExecutor and adding retry with maxRetries
- Updating to Gradle 8.0.2

[xfunction-21.0.jar](https://github.com/lambdaprime/xfunction/raw/master/xfunction/release/xfunction-21.0.jar)

# Version 20

- Adding TemporaryHashMap
- Minor improvements
- Do not quote numbers in XJson
- Fixing "FileNotFoundException (Is a directory)" error in Substitutor and adding primitive types to XJsonStringBuilder
- Replacing ArgsUtils with CommandOptions
- Add new predicate to match files with regexp
- Support varargs in Preconditions
- By default log all messages in LoggerNameFilter and change exclude to work based on name prefixes instead of full names
- Allow to initialize XLogger explicitly
- Implementing echo and nonBlockingSystemInput
- Refactoring XExec
- Moving AssertRunCommand to xfunctiontests project
- Implementing retryIndefinitely and retryIndefinitelyAsync
- Renaming "run" method of ThrowingSupplier to "get", similar to original Supplier class

[xfunction-20.0.jar](https://github.com/lambdaprime/xfunction/raw/master/xfunction/release/xfunction-20.0.jar)

# Previous versions

Changelog for previous versions were published in github Releases but then migrated here.
