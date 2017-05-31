Reproducer of wildfly embedded-server issue on Windows
===
There are server files being locked even when CLI context terminates

Prerequisites:
===
* Windows environment
* JDK8
* WildFly/JBoss EAP installation
* (optional) handle.exe - can be downloaded from https://technet.microsoft.com/en-us/sysinternals/handle.aspx 
  - can be used to get more info about locked files
  - you must have administrative privilege to run this tool

Usage:
===
mvn clean test "-Dwildfly.home=<PATH_TO_UNZIPPED_WILDFLY>" "-Dhandle.exe=<PATH_TO_HANDLE_EXE>"

if <PATH_TO_HANDLE_EXE> doesn't exist, output of the tests will be less verbose

Example:
--
mvn clean test "-Dwildfly.home=C:\dev\wildfly-11.0.0.Alpha1" "-Dhandle.exe=C:\dev\handle.exe"
