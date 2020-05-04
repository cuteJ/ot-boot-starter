# ot-boot-starter

#### 项目介绍
自定义 Spring boot starter

#### 使用方式

````
  git clone https://github.com/cuteJ/ot-boot-starter.git
  cd ot-boot-starter
  mvn clean install
````

````
    <dependency>
      <groupId>com.onlythinking.starter</groupId>
      <artifactId>lettuce-redis-starter</artifactId>
      <version>1.0.0</version>
    </dependency>

    <dependency>
      <groupId>com.onlythinking.starter</groupId>
      <artifactId>app-oss-starter</artifactId>
      <version>1.0.0</version>
    </dependency>
  
    <repositories>
      <repository>
        <id>github</id>
        <name>github nexus</name>
        <url>https://maven.pkg.github.com/cuteJ/ot-server-parent</url>
      </repository>
    </repositories>
````

#### API
[app-oss-starter](https://cutej.github.io/ot-boot-starter/apidocs/oss/)

[lettuce-redis-starter](https://cutej.github.io/ot-boot-starter/apidocs/lettuce/)
