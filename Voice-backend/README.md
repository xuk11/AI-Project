# 有声教育平台后端服务

## 项目简介

这是一个基于Spring Boot的有声教育平台后端服务，提供音频内容管理、用户管理、内容分发等核心功能。

## 技术栈

- 核心框架：Spring Boot 2.7.2
- 数据库：MySQL
- 缓存：Redis
- 搜索引擎：Elasticsearch
- 对象存储：腾讯云COS
- 消息推送：WebSocket
- 文档工具：Knife4j
- 其他工具：
    - MyBatis-Plus
    - Druid
    - Hutool
    - EasyExcel
    - Volcengine SDK

## 环境要求

- JDK 1.8+
- Maven 3.8.1+
- MySQL 5.7+
- Redis 6.0+
- Elasticsearch 7.x

## 快速开始

### 1. 配置环境

1. 创建数据库并导入SQL脚本
2. 配置Redis连接
3. 配置Elasticsearch连接
4. 配置腾讯云COS密钥

### 2. 编译项目

```bash
mvn clean package -DskipTests
```

### 3. 运行项目

```bash
java -jar target/Voice-backend-0.0.1-SNAPSHOT.jar
```

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/zbn/
│   │       ├── config/        # 配置类
│   │       ├── controller/    # 控制器
│   │       ├── service/       # 服务层
│   │       ├── mapper/        # 数据访问层
│   │       ├── model/         # 数据模型
│   │       └── util/          # 工具类
│   └── resources/
│       ├── application.yml    # 主配置文件
│       └── mapper/            # MyBatis映射文件
```

## API文档

项目使用Knife4j生成API文档，启动服务后访问：

```
http://localhost:8101/api/doc.html
```

## 注意事项

1. 确保所有必要的环境变量已正确配置
2. 生产环境部署时注意修改配置文件中的敏感信息
3. 建议使用HTTPS进行API访问
4. 定期备份数据库和重要文件

## 联系方式

- 作者：[zbn](https://github.com/qwerzbn)
- 邮箱：zhubeining36@gmail.com