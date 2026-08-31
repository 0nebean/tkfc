# common（TKFC）

多模块 **Spring Boot 公共框架**（`groupId: com.tkfc`），为业务应用提供可复用的 **core 基础库**、**Starter 自动配置** 与 **外部服务 SDK**。

当前技术基线：

- Java **25**
- Spring Boot **3.5.12**（Jakarta EE）
- 统一版本：`1.0.0-SNAPSHOT`
- 版本与三方依赖由 `dependencies` BOM 集中管理

业务侧典型消费者：`management-backend` 等（按需引入 `welus.starter`、`mybatis.starter`、`gateway.sdk` 等）。

## 仓库结构

```text
common/
├── dependencies/     # BOM：依赖版本、仓库、编译插件
├── core/             # 基础注解、异常、POJO、工具类
├── starter/          # Spring Boot Starters
├── sdk/              # 外部服务客户端
├── build/package/    # 业务应用打包父 POM（com.tkfc.package）
└── pom.xml           # 聚合根 parent
```

## 模块说明

### `dependencies`

依赖管理 BOM（parent 为 `spring-boot-starter-parent`）。子模块一般 **不要自行写版本号**，在 `dependencyManagement` 里引用即可。

### `core`

基础能力，无业务绑定：

| 类别 | 说明 |
|------|------|
| Web 注解 | `@PostJson` / `@GetJson` / `@Authenticated` / `@Body` / `@PathParam` 等（需配合 `welus.starter`） |
| ORM 注解 | `@TableName` / `@FiledName` / `@LogicalDelete` / `@Version` 等 |
| ES 注解 | `annotations/elasticsearch` |
| 异常 | `BizException`、`RunTimException`、`AssertFailException` 等 |
| POJO | `BaseResponse`、`Pagination`、分页请求等 |
| 工具 | `toolkit/`（JSON、HTTP、加解密、集合等） |

### `starter`

| Artifact | 用途 |
|----------|------|
| `welus.starter` | Web 层：自定义路由注解、参数解析、统一响应、鉴权拦截、访问日志 |
| `mybatis.starter` | MyBatis + Druid |
| `elasticsearch.starter` | Elasticsearch Java API Client（当前依赖 ES **9.x**） |
| `apollo.starter` | Apollo 配置中心 |
| `logger.starter` | 结构化日志（Logstash encoder） |
| `dictionary.starter` | 数据字典 |
| `notice.starter` | 邮件等通知 |
| `compress.starter` | HTML/JS/CSS 压缩 Filter |
| `jsch.starter` | SSH / SFTP |
| `cacher.starter` | 缓存抽象实现 |
| `racher.starter` | Redis 缓存（Redisson） |
| `cache-interface` | 缓存接口 |
| `musser.starter` | 消息消费（Disruptor） |
| `russer.starter` | 消息生产 |
| `mus-interface` | 消息契约 |

### `sdk`

| Artifact | 用途 |
|----------|------|
| `gateway.sdk` | 内部网关 API 客户端 |
| `aliyun.oss.sdk` / `aliyun.sms.sdk` | 阿里云 OSS / 短信 |
| `aws.s3.sdk` / `aws.ses.sdk` / `aws.bedrock.sdk` | AWS S3 / SES / Bedrock |
| `google-sdk` | Google 相关客户端 |
| `binance-sdk` | Binance API |

### `build/package`（`com.tkfc.package`）

业务应用的 **打包父工程**：多环境 profile、资源过滤、启动脚本与 Spring Boot 打包约定。业务根 POM 常以它为 parent。

## 快速使用

业务模块引入 BOM（或已继承 `dependencies` / `com.tkfc.package`）后按需加依赖，例如：

```xml
<dependency>
    <groupId>com.tkfc</groupId>
    <artifactId>core</artifactId>
</dependency>
<dependency>
    <groupId>com.tkfc</groupId>
    <artifactId>welus.starter</artifactId>
</dependency>
<dependency>
    <groupId>com.tkfc</groupId>
    <artifactId>mybatis.starter</artifactId>
</dependency>
```

Web 控制器使用自定义注解时，必须引入 **`welus.starter`**（负责方法映射、参数解析与响应包装）。

## 构建

要求：JDK 25、Maven 3.x；IDE 开启 Lombok 注解处理。

```bash
# 全量安装到本地仓库
mvn clean install

# 跳过测试
mvn clean install -DskipTests

# 只构建某个模块
cd starter/mybatis && mvn clean install
```

发布到私服：

```bash
mvn clean deploy
```

当前 `distributionManagement`（见 `dependencies/pom.xml`）：

- Snapshots：`http://8.147.70.20:8115/repository/maven-snapshots/`
- Releases：`http://8.147.70.20:8115/repository/maven-releases/`

本地 `~/.m2/settings.xml` 需配置对应 `repo-nexus` 认证。

## 开发约定

1. **新增 Starter**：在 `starter` 下建模块 → 写入 `starter/pom.xml` 的 `<modules>` → 在 `dependencies/pom.xml` 登记版本与 `dependencyManagement` → 提供 `META-INF/spring.factories`（或 Boot 3 的 `AutoConfiguration.imports`）。
2. **版本**：统一走 `dependencies`；子模块勿散落写死版本。
3. **包名**：`com.tkfc.*`；Web 走 Jakarta（`jakarta.servlet` 等）。
4. **注解位置**：Web → `core/.../annotations/web`；ORM → `.../orm`；ES → `.../elasticsearch`。

## 关键架构要点

**Welus**：在 Spring MVC 之上提供 `@PostJson` / `@GetJson` 等路由、`@Body` / `@PathParam` 参数解析、统一 JSON 包装、`@Authenticated` 鉴权、`@RecordeAccessLog` 访问日志。

**MyBatis**：Druid 数据源 + core 中 ORM 注解（逻辑删除、乐观锁、字段映射）。

**MUS**：`russer` 生产 / `musser` 消费（LMAX Disruptor），契约在 `mus-interface`。

**Cache**：`cache-interface` → `cacher` 通用实现 / `racher` Redis（Redisson）。

## License

MIT License © 0neBean（见 [LICENSE](LICENSE)）。
