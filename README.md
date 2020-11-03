# Util

## Overview

Utility library


## Building From Source

[Maven](http://maven.apache.org) is used to build and deploy.

Run build jar:

```sh
$ mvn package
```

The resulting jar files are in `target/`.

Generate the documentation:

```sh
$ mvn javadoc:javadoc
```

The resulting HTML files are in `target/site/apidocs/`.


## Using From Maven

Any Maven based project can use it directly by adding the appropriate entries to the
`dependencies` section of its `pom.xml` file:

```xml
<dependencies>
  <dependency>
    <groupId>org.homedns.mkh</groupId>
    <artifactId>util</artifactId>
    <version>0.0.1</version>
  </dependency>
</dependencies>
```


## Using From Binaries

Packaged jars can be downloaded directly from the [Releases page](https://github.com/khomisha/util/releases).


## Contact

* mkhodonov@gmail.com

## License

Apache License, Version 2.0
Copyright (c) 2012-2020 Mikhail Khodonov.
It is free software and may be redistributed under the terms specified
in the LICENSE and NOTICE files.


