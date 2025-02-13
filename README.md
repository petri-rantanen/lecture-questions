# Information

This repository contains the source codes for lecture questions application. The application takes any number of questions, ideas or opinions as an input and stores them into an in-memory database. On request, by utilizing [OpenAI's chat completions API](https://platform.openai.com/docs/guides/text-generation), the web application can generate a number of topics with true and false claim usable for group discussion with students.

You can run the application without OpenAI API key, but for generating topics, the key is required.

You can add your key the application.properties file, located in src/main/resources.

# Requirements

The basic requirements for running the application are:
- Java JDK (Oracle or OpenJDK), version 21 or newer.
- Docker
    - [Installation instructions](https://docs.docker.com/engine/install/)
    - Note that if you follow the instructions for ubuntu/debian, docker repositories may not support the latest/testing distribution. In this case you should note the "derivative distribution" instructions, and replace your distribution with an older one (e.g. bookworm for Debian). If you forget this and updating apt repositories gives errors, you can replace the distribution name in the apt sources files (e.g. in /etc/apt/sources.list.d/docker.list, for Debian, replace trixie with bookworm)
- Quarkus (optional)
    - You can also develop via Maven, installing the Quarkus application is not required.
    - If you want to try quarkus client approach, check [the instructions](https://quarkus.io/get-started/)
- Maven
    - Available in package managers on most Linux distributions
    - If this is not the case with your OS, check [the instructions](https://maven.apache.org/)

Also, even though not strictly required, it is **strongly recommended** to use a virtual machine for your tests so that you don't mess up your host computer with failed installations. If you are planning to try the applications using root permissions, it is crucial to use a virtual machine for testing. Popular options are:
- [VirtualBox](https://www.virtualbox.org/)
- [virt-manager] (https://virt-manager.org/)
- VMware products (e.g. Workstation Pro) are also available for free for personal use, but finding the installation packages and going through the registration is unnecessarily complicated
    - if you want to try it out, download links can be found [here](https://www.vmware.com/products/desktop-hypervisor/workstation-and-fusion)
    - If you are having difficulties in finding correct files, take a look at {this](https://www.youtube.com/watch?v=raW5KKMl6PA)
- If you have Finnish university credentials (HAKA access), you might also be able to host a VM at CSC
    - Checkout CSC Pounta instructions [here](https://docs.csc.fi/cloud/pouta/) and [here](https://docs.csc.fi/cloud/pouta/launch-vm-from-web-gui/)

# How to Run?

Clone this repository with git: _git clone https://github.com/petri-rantanen/lecture-questions.git_

You can start the application from the questions-app directory, by running:
- For development setup (live code reloading, debugging), run _mvn quarkus:dev_
- For production setup, first package the application with _mvn package_, and then run it with _java -jar target/quarkus-app/quarkus-run.jar_
- You can find more information about development and productions modes [here](https://quarkus.io/guides/dev-mode-differences)

The application will be available at 0.0.0.0 (or localhost, for dev), in port 8080. Use web browser to access: http://localhost:8080/?id=1
If you want to change the address or port, you can use the flags: _-Dquarkus.http.host=0.0.0.0_ or _-Dquarkus.http.port=9090_

By-default, identifiers 1,2 and 3 are accepted, you can change these in the _application.properties_ file.

# Compilation and Packaking

There are two ways of packaking (and compiling) the application. By default, the provided Dockerfile will compile a native build. You can achieve this by running: _mvn package -Pnative_

Notice that compiling the native build requires Maven to pull the images used for compilation from Docker, and thus you must either run the package command with user that has access to your docker command/application (or by using root permissions).

Compiling a non-native build, usable with JVM is also an option. For this, replace the default _Dockerfile_ with the provided _Dockerfile.jvm_. After this, you can package the application normally, by running _mvn package_.

# Creating Docker Image and Running Container

Regardless of whether you made a native or non-native application, the instructions for making the image and running it are the same.

You can create the Docker image by running: _docker build -t questions-app ._

And run the container with: _docker run -p 8080:8080 questions-app_

The application will be available at 0.0.0.0, in port 8080. Use web browser to access: http://localhost:8080/?id=1

If you want to start the container detached (running in background), you can add _-d_ flag before _-p_ in the run command above. You can find your running containers with _docker container ls_. and stop them with _docker container stop CONTAINERID_ (check the id from the ls output).

# Troubleshooting

By default the application is compiled with Java 21. If you want to use an alternative version, update _pom.xml_ AND _Dockerfile(.jvm)_ files. Using different compilation version and JRE version (in the Docker image) **might** work, but could also create issues. In some cases, incompatible Java versions can cause an application to crash silently without any error messages. Java compilers can, in general, compile source codes for older Java versions, so if you have Java 21+, you should be good to go.

For debugging, you can try to run the container in interactive mode: _docker run -it --entrypoint /bin/sh questions-app_

# Removing Images and Containers

Check that there are not running or stopped containers, before attempting to remove images. You can view containers with: _docker ps -a_, and remove containers with _docker container remove CONTAINERID_ (using id listed in the output of _docker ps -a_ command).

You can view your images by running _docker image ls_. Look for the _questions-app_ image. You can remove the image with _docker image rm IMAGEID_ (using id listed in the output).

Optionally, you can also remove all _quay.io/quarkus*_ images if you have no other need for them.

# Bonus: Create a Simple Test Application

If you only want to try a simple test application, this can be done by following the steps below:
1. Install the requirements listed in the beginning of this page
2. Create example application
    - You can do this either with Quarkus client, by running _quarkus create app com.example:example-app --extensions="resteasy"_
    - Or with Maven: _mvn io.quarkus.platform:quarkus-maven-plugin:3.18.2:create -DprojectGroupId=com.example -DprojectArtifactId=example-app -Dextensions="resteasy"_
3. Test that your example application works
    1. cd example-app && mvn quarkus:dev
    2. Open your web browser at: http://localhost:8080/hello
4.  Compile native binaries
    - _mvn package -Pnative_ (requires permissions to run Docker)
    - If you want to compile non-native package, you can adapt the questions-app instructions above
5. Create Docker image
    1. Create file named _Dockerfile_ in the application root directory, with contents:   
        FROM quay.io/quarkus/quarkus-distroless-image:2.0   
        WORKDIR /app   
        COPY target/*-runner /app/app   
        CMD ["/app/app"]   
        EXPOSE 8080
    2. _docker build -t example-app ._
6. Run it!
    1. _docker run -p 8080:8080 example-app_
    2. Open your web browser at: http://localhost:8080/hello

