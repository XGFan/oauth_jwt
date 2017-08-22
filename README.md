### OAuth2 + Jwt

[![](https://jitpack.io/v/XGFan/oauth-jwt.svg)](https://jitpack.io/#XGFan/oauth-jwt)

快速为Spring Security添加Oauth登陆和Jwt鉴权功能

使用方式

1.添加依赖

```xml
	<dependency>
	    <groupId>com.github.XGFan</groupId>
	    <artifactId>oauth_jwt</artifactId>
	    <version>version</version>
	</dependency>

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```



2.添加注解

```java
@EnableOAuthJwt
@EnableWebSecurity
```

3.修改配置(yaml为例)

```yaml
test4x:
  loginPath: /login #oauth授权地址
  oauth:
    id: 8d84f30cdcca397449c06935910dc6102d5ecefea19285bc5d9b13244ba77bc0
    secret: 60ec51965f60520b6ef1bc394d44c1317ef7128b83a075ef385c87e59c6d6147
    tokenUri: https://gitlab.com/oauth/token
    redirectUri: http://127.0.0.1:9090/login #可能是跳往前端
    grantType: authorization_code
    authenticationScheme: body
    userInfoUri: https://gitlab.com/api/v4/user
  jwt:
    tokenName: X-Auth-Token #jwt存放头
    secret: hello! #加密key
    expiration: 24 #有效期(小时)

```



相关设置（可见[OAuthJwtConfigure.java]((https://github.com/XGFan/oauth_jwt/blob/master/src/main/java/com/test4x/lib/oauth_jwt/OAuthJwtConfigure.java))）

+ 关闭Session
+ 关闭HttpCache
+ 关闭了Spring Security自带的一系列验证方式（HttpBasic……）
+ 开启跨域



总的来说，适合API Server