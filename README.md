Dropwizard admin dashboard for microservices
=========

This repository contains a microservice Dashboard and framework to operate the dashboard. 

Dashboard:
----

  - Metrics and healtcheck viewer for your DropWizard microservices.
  - An overview of all microservices registered with [Apache Zookeeper](http://zookeeper.apache.org/) allong with the [Netflix Curator](https://github.com/Netflix/curator) plugin.
  - A detailed performance metric for each individual service [Netflix Hystrix](https://github.com/Netflix/Hystrix)
  - Server overview that shows the CPU, RAM and Disk usage.
  - Centralized log management without external dependencies! (it reads your file log)

Framework:
----
The dashboard is shipped with a framework / library for dropwizard, which enables you to connect to the admin dashboard and use all its functions with the minimum line of code. It has the following features:

  - Registers and configures your service with [Apache Zookeeper](http://zookeeper.apache.org/) with a single line of code. 
  - It has a build in ServiceLocator, you can call other services by name (without knowing their address and / or port).
  - The ServiceLocator uses the [Netflix Feign](https://github.com/Netflix/feign) library to call other services through Java interfaces.
  - It has [Yammer Tenacity](https://github.com/yammer/tenacity) support build in.
  - It has build in authentication support by adding the following piece to your confguration file.    
 
```        
    authentication:
        servicename: Login_service
        path: /api/token/  
```  
Screenshots
----
![alt tag](https://raw.githubusercontent.com/abduegal/Microservice_admin_dashboard/master/screenshots/large1.png)
![alt tag](https://raw.githubusercontent.com/abduegal/Microservice_admin_dashboard/master/screenshots/large2.png)
![alt tag](https://raw.githubusercontent.com/abduegal/Microservice_admin_dashboard/master/screenshots/large3.png)
![alt tag](https://raw.githubusercontent.com/abduegal/Microservice_admin_dashboard/master/screenshots/large4.png)
![alt tag](https://raw.githubusercontent.com/abduegal/Microservice_admin_dashboard/master/screenshots/large5.png)
![alt tag](https://raw.githubusercontent.com/abduegal/Microservice_admin_dashboard/master/screenshots/large6.png)


Installation:
----
- Install  [Apache Zookeeper](http://zookeeper.apache.org/) and run it.
- Download the dashboard [here](https://github.com/abduegal/Microservice_admin_dashboard/raw/master/dashboard-release-0.8.1.zip) and extract the zip file.
- Run the dashboard through the following command:  
``` java -jar dashboard-0.8.1.jar server config.yml```
- The dashboard should be up and running on [http://localhost:8080](http://localhost:8080)

Getting started
----

> There is an example project included, which you can use as a reference.  

After you have got the dashboard up and running, create a new Dropwizard project.

- Add the following dependency
```xml
    <dependency>
        <groupId>com.github.abduegal</groupId>
        <artifactId>microservice-framework-core</artifactId>
        <version>0.8.1</version>
    </dependency>
```
- Make your dropwizard Configuration class extend from MicroserviceConfig instead of the io.dropwizard.Configuration class.
- Add the following line to your Dropwizard Application class: 

```java    
    @Override
    public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
        bootstrap.addBundle(new MicroserviceBundle<>());
    }
```    
      
- Add the following to your configuration.yml file:

```yml       
    discovery:
      serviceName: Example
      namespace: myapp
      #zookeeper:
      port: 2181
      listenAddress: 127.0.0.1
       
    server:
      applicationConnectors:
      - type: http
        port: 0                          
      adminConnectors:
      - type: http
        port: 0
      requestLog:
          timeZone: UTC
          appenders:
          - type: file
            currentLogFilename: /tmp/example_service.log
            archive: false
            threshold: ALL
      
    logging:
      appenders:
        - type: console
        - type: file
          currentLogFilename: /tmp/example_service.log
          archive: false
          threshold: ALL
```          
- The above snippet does the following things:
  - Defines the zookeeper connection properties.
  - Defines the name of the service (Example) and the namespace (important for the dashboard)
  - Tells the application to use a random port (0)
  - Configures logging (the log file will be used for centralized logging).

Optionally:
- Use the Hystrix Latency and fault tolerance library by adding the following things to your Dropwizard Application class:

```java      
    public enum DependencyKeys implements TenacityPropertyKey {
        Action;

        public static TenacityPropertyKeyFactory getTenacityPropertyKeyFactory() {
            return new TenacityPropertyKeyFactory(){
                @Override
                public TenacityPropertyKey from(String value) {
                    return DependencyKeys.valueOf(value.toUpperCase());
                }
            };
        }
    }
     
    @Override
    public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
        bootstrap.addBundle(new MicroserviceBundle<>());
        bootstrap.addBundle(TenacityBundleBuilder.newBuilder()
                .propertyKeyFactory(DependencyKeys.getTenacityPropertyKeyFactory())
                .propertyKeys(DependencyKeys.values())
                .build());
    }
     
    @Override
    public void run(ExampleConfiguration exampleConfiguration, Environment environment) throws Exception {
        environment.jersey().register(ExampleResource.class);

        InitializeTenacity.initialize(DependencyKeys.values());
    }
```

Version
----

0.8.1

Tech
-----------

This project uses a number of open source projects to work properly:

* [Apache Zookeeper] - for service discovery
* [Netflix Curator] - Zookeeper client wrapper.
* [Netflix Hystrix] - Latency and fault tolerance library
* [Netflix Feign] - java to http client binder
* [Yammer Tenacity] - Dropwizard integration with Netflix Hystrix
* [Dropwizard] - Java Framework
* [AngularJS] - for the front-end
* and more...         

Building the dashboard:
----

The dashboard is build with Dropwizard (backend) and AngularJS (with grunt). The Angular front-end is packaged with
the dashboard as static resource, but you can also run it seperately by navigating to the 'app' directory with grunt
using the following commands:  
```npm install  ```  
```bower install```  

To run it locally:  
```grunt serve```  

To build it:  
```grunt build```  
It will automatically copy the processed front-end files to the 'src/main/resources/static' directory. You can
optionally use the --force command if it complaints about permission problems like:
 - Cannot delete files outside the current working directory. Use --force to continue.

License
----

MIT
