AutoCopy
=
A small Java application which monitors a directory and moves new detected files to another destination.

## Installation
After checking out the source code you can build a standalone JAR using the command `sbt assembly`.

## Configuration
AutoCopy can be configured using the configuration file `config.conf`, which contains the following two settings:

* input: An input directory which will be monitored
* output: An output directory

Simply said, new files which were detected in the input directory, are moved to the output directory.

AutoCopy relies on Apache Commons VFS, which supports [multiple file systems](http://commons.apache.org/proper/commons-vfs/filesystems.html). For example, you are able to monitor a local directory and move files to another server using FTP:
    input=/path/to/my/local/folder/
    output=ftp://user:password@domain.tld/subfolder/

## Usage
After the configuration, AutoCopy can be started the following way:
* Linux: `java -cp .:autocopy-1.0.jar Main`
* Windows: `java -cp .;autocopy-1.0.jar Main`

Make sure that `config.conf` lies in the classpath. Generally it should be next to the JAR file.
