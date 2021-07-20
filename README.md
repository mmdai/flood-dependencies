# flood-dependencies

#### 介绍
基础项目平台

floodCloud是一款基于Spring Cloud Alibaba的微服务架构。旨在为大家提供技术框架的基础能力的封装，减少开发工作，让您只关注业务。

#### 软件架构
软件架构说明

核心中间件 |  | 2.0.0
---|---|---
Spring Boot |  | <img src="https://img.shields.io/badge/Spring%20Boot-2.5.2-blue" alt="SpringBoot"/>
Spring Cloud |  | <img src="https://img.shields.io/badge/Spring%20Cloud-2020.0.3-blue" alt="SpringCloud"/>
Spring Cloud Alibaba | | <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.1-blue" alt="SpringCloudAlibaba"/>
Nacos |  | <img src="https://img.shields.io/badge/Nacos-2.0.2-blue" alt="nacos"/>
Sentinel |  | <img src="https://img.shields.io/badge/Sentinel-1.8.1-blue" alt="sentinel"/>

- 主体框架：采用最新的`Spring Cloud 2020.0.3`, `Spring Boot 2.5.2`, `Spring Cloud Alibaba 2021.1`版本进行系统设计；

- 统一注册：支持`Nacos`作为注册中心，实现多配置、分群组、分命名空间、多业务模块的注册和发现功能；

- 统一认证：统一`Oauth2`认证协议，采用jwt的方式，实现统一认证，并支持自定义grant_type实现手机号码登录，第三方登录集成JustAuth实现微信、支付宝等多种登录模式；

- 业务监控：利用`Spring Boot Admin`来监控各个独立Service的运行状态。

- 内部调用：集成了`Feign`支持内部调用，并且可以实现无缝切换，适合新老程序员，快速熟悉项目；

- 业务熔断：采用`Sentinel`实现业务熔断处理，避免服务之间出现雪崩;

- 身份注入：通过注解的方式，实现用户登录信息的快速注入；

- 在线文档：通过接入`swagger`，实现在线API文档的查看与调试;

- 代码生成：基于`Mybatis-generator`自动生成代码，提升开发效率，生成模式不断优化中，暂不支持前端代码生成；

- 消息中心：集成消息中间件`RocketMQ`，对业务进行异步处理;

- 业务分离：采用前后端分离的框架设计，前端采用`layui-admin`
  
- 链路追踪：自定义traceId的方式，实现简单的链路追踪功能
#### 安装教程

1. xxxx
2. xxxx
3. xxxx

#### 使用说明

1. xxxx
2. xxxx
3. xxxx

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)