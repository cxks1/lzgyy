# lzgyy

#### 项目介绍
{**平台简介**
基于springboot容器，zokeeper分布式协调服务，dubbo分布式服务框架,redis缓存,spring jdbctemplate,mybatis持久层框架，mysql数据库等}

#### 软件架构

### 项目结构
lzgyy
  ├── db 数据库文件存放目录
  ├── lzggy-config 配置相关
	├── lzggy-druid-config 数据库连接池
	├── lzggy-dubbo-consumer-config  dubbo消费者通用配置
	├── lzggy-dubbo-provider-config  dubbo生产者通用配置
	├── lzggy-swagger-config swagger swagger接口文档生成配置
  ├── lzgyy-common 工具类
  ├── lzgyy-core 核心类
  ├── lzgyy-manage 管理服务
	├──  lzggy-dubbo-admin-war dubbo管理平台war包
  ├── lzgyy-plugins 插件包
  	├── lzgyy-elasticsearch-plugin es搜索引擎（暂未实现）
  	├── lzgyy-iot-client	物联网客户端
  	├── lzgyy-iot-core   	物联网核心类
  	├── lzgyy-iot-service	物联网服务端
  	├── lzgyy-quartz-plugin 定时任务器（暂未实现）
	├── lzgyy-redis-interf	缓存接口
	├── lzgyy-redis-service 缓存服务类（生产者）
  ├── lzgyy-products
	├── lzgyy-platform-api 	      平台API接口
	├── lzgyy-platform-interf  平台接口
	├── lzgyy-platform-service 平台服务类（生产者）
	├── lzgyy-platform-web     平台网页端（消费者）

### 项目备注

1. 暂未涵盖前端，权限等，具体项目结构后续或许会有所更新变动，仅供参考
2. “缓存服务类”、“lzgyy-platform-service”、“lzgyy-platform-web”启动方式配置有差异，后续以“缓存服务类”配置为准。
3. 关于dubbo问题请参考 http://dubbo.apache.org/zh-cn/

#### 测试例子

1. 生成者消费者运行测试
   1.1 运行平台服务端（生产者）项目lzgyy-platform-service，路径：com.lzgyy.platform.PlatformServiceProviderApp.java --> Run As
   1.2 运行平台网页端（消费者）项目lzgyy-platform-web，路径：com.lzgyy.platform.PlatformServiceConsumerApp.java --> Run As
   1.3 打开浏览器录入以下路径，即可测试
   	   http://localhost:9999/test/v1.0/getDemoList.json
   	   http://localhost:9999/test/v1.0/getDemoList2.json
2. swagger接口文档测试
   2.1 运行平台API接口（消费者）项目lzgyy-platform-api，路径：com.lzgyy.platform.PlatformApiConsumerApp.java --> Run As
   2.1 打开浏览器录入以下路径，即可测试
  	   http://localhost:9999/docs.html
3. netty mqtt运行测试
	1.1 运行物联网服务器端，项目lzgyy-iot-service，路径：com.lzgyy.plugins.iot.ClientApplication.java --> Run As
		或路径：example.MqttMain.java --> Run As
	1.2 运行物联网客户端，项目lzgyy-iot-client，路径：com.lzgyy.plugins.iot.client.BrokerApplication.java --> Run As
