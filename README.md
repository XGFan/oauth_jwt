## OAuth2 + Jwt

[![](https://jitpack.io/v/XGFan/oauth_jwt.svg)](https://jitpack.io/#XGFan/oauth_jwt)

快速为Spring Security添加Oauth登陆和Jwt鉴权功能

#### 使用方式

##### 1.添加依赖

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



##### 2.添加注解

```java
@EnableOAuthJwt
@EnableWebSecurity
```



##### 3.修改配置(yaml为例)

```yaml
oauth-jwt:
  loginPath: /login #oauth授权地址
  oauth:
    id: 8d84f30cdcca397449c06935910dc6102d5ecefea19285bc5d9b13244ba77bc0
    secret: 60ec51965f60520b6ef1bc394d44c1317ef7128b83a075ef385c87e59c6d6147
    tokenUri: https://gitlab.com/oauth/token
    redirectUri: http://127.0.0.1:9090/login #可能是跳往前端
    userInfoUri: https://gitlab.com/api/v4/user
  jwt:
    tokenName: X-Auth-Token #jwt存放头
    secret: hello! #加密key
    expiration: 24 #有效期(小时)
```



##### 4.实现OAuthService

该接口包含三个方法

```kotlin
fun acquireAccessToken(code: String): A //根据授权码获取accessToken对象
fun acquireUserInfo(accessToken: A): R //根据上一步的accessToken对象获取用户信息
fun handleAndAuth(accessToken: A, userInfo: R): Claims //把用户信息进行处理或者生成授权凭证
```

需要注意的是，目前的授权凭证是通过`Claims`的`Subject`进行查找的，所以生成`Claims`时必须放入`Subject`

例子：

```kotlin
    override fun handleAndAuth(accessToken: Map<String, Any>, userInfo: Map<String, Any>): Claims {
   val user = trunTheUserInfoToUser(userInfo) //生成一个业务用户实体
        user.accessToken = accessToken  //保存一下accessToken(方便后期刷新)
        userDao.saveUser(user) //保存或者更新用户
        val defaultClaims = DefaultClaims() //生成Claims
        defaultClaims.subject = user.id
        return defaultClaims
    }
```



更多设置（可见[OauthJwtConfigurer.java]((https://github.com/XGFan/oauth_jwt/blob/master/src/main/java/com/test4x/lib/oauth_jwt/OauthJwtConfigurer.java))）

+ 关闭Session
+ 关闭HttpCache
+ 关闭了Spring Security自带的一系列验证方式（HttpBasic……）
+ 开启跨域



总的来说，适合API Server