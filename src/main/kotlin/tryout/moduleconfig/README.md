# Module Configuration

This directory contains examples of simple frameworks and patterns for module configuration.

The key considerations when designing/selecting a configuration framework are:
- Type safety
- Fail-fast
- Minimization of module dependencies
- Unit testing ease
- Ease of use

There are three main configuration approaches:
- *pull*
- *push-to-file*
- *push-to-function*

**_Pull_** approach:

- Each module or function requiring configuration makes a direct call to a function or framework that returns application configuration information. The module or function selects and uses whatever configuration properties it needs from the returned application configuration data structure.
- Introduces a dependency of all configurable modules and functions on the configuration framework.
- Is often used and implemented in a simplistic way that exacerbates the above-mentioned dependencies and usually makes it hard to unit test modules.
- This approach can be refined, as described later, to yield a configuration approach that is easy to use and enables modules to be easily unit tested.

**_Push-to-file_** approach:

- Each configurable module has a top-level variable that can be set with configuration information and which is accessed by functions in the file. For example a module can have an exported function `setConfig(configData)` that is called by application initialization logic to set the aforementioned top-level variable.
- The *push-to-file* approach requires more planning, more files, and a bit more work, but it avoids a dependence on a configuration framework and results in easier unit testing than the naive *pull* approach. 
- Using this approach for modules with top-level variables that depend on configuration information leads to circular dependencies and the use of uninitialized variables. This is because, during initialization, the module/package to be configured is loaded so that its exported configuration function can be called by the initialization logic but, as the module/package to be configured is loaded, its top-level variables are initialized and that (by assumption) requires the configuration information that has not yet been injected into the module/package. This problem can be solved by moving top-level variables that depend on configuration information to a separate module/package and having those variables reference the top-level configuration variable in the original module/package.

**_Push-to-function_** approach:

- Each configurable function needs to be created from a factory/constructor function or class that takes the required configuration properties as input. Application initialization logic is responsible for calling the factories/constructors and pass the required configuration information to create configured function instances.
- Goes hand-in-hand with dependency injection. If function f depends on a configurable function g then f needs to be instantiated via a factory function or class fC that takes a configured instance of g as an input.
- Minimizes coupling among modules, provides the greatest unit testing flexibility, and easily enables the creation of multiple instances of the same function with different configurations.
- Requires the most planning, the highest number of files, and the most work of all the configuration approaches.

This directory demonstrates simple frameworks and patterns for:

- Push-to-file configuration.
- Push-to-function configuration.
- Pull-with-push-override configuration -- a configuration approach that combines the ease-of-use of naive pull configuration with testability comparable to that of the push-to-file approach.

Notice that the _push_ frameworks demonstrated here have a _pull_ aspect to them as what is pushed is a thunk function that returns the configuration data, not the configuration data itself. The reason for that is to provide the flexibility to support configuration properties that change dynamically at runtime. There is no real performance penalty associated with the use a thunk instead of the data structure itself as the thunk can simply return a cached data structure by reference.
