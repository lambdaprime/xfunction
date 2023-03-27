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
