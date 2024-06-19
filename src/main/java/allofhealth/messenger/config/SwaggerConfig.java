package allofhealth.messenger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(servers = {@Server(url = "http://192.168.0.79:8070"), @Server(url = "http://192.168.0.80:8070"), @Server(url = "http://localhost:8070")},
        info = @Info(
                title = "AllofHealth platform APIs",
                description = "<p>1. Auth API에서 <b>/api/auth/login</b> (로그인)또는 <b>/api/auth/register</b>(회원가입) 통해 JWT access token과 refresh token을 발급 받는다 <i>(access token : 5분 유효, refresh token : 15분 유효)</i></p>" +
                        "<p>2. 우측 상단 Authorize 버튼 클릭 후 발급 받은 access token을 입력한다. 이제 입력한 access token이 모든 api 요청 header에 자동으로 삽입된다.</p>" +
                        "<p>3. 만약 access token이 만료(expired)될 경우, 우측 상단의 Authorize 버튼 클릭 후 logout. 그리고 개발자 도구 Application/Cookies에서 Refresh Token을 찾아서 입력한다.</p>" +
                        "<p>4. 이제 api 요청 header에 refresh token이 삽입되어 있으므로, Authentication API의 <b>/api/auth/refresh-token</b>으로 새로운 access token과 refresh token을 받아서 2번으로 돌아가면 된다.</p>" +
                        "<p>5. 만약 refresh token마저 만료될 경우, 1번으로 돌아가면 된다.</p>" +
                        "<p>6. 종료하고 로그아웃을 하고 싶으면, <b>/api/auth/logout</b>을 사용하면 해당 사용자의 모든 token 이 삭제되고 로그아웃이 된다.</p>" +
                        "<p>API 중 /get 이 포함되어있는 API 들은 로그인 없이 접근이 가능한 API 들이다.",
                version = "BETA 1.0.0"),
        security ={@SecurityRequirement(name = "bearerAuth")})
@SecurityScheme(name = "bearerAuth", description = "JWT Authentication", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
// 쿠키 사용을 원할 경우, @SecurityScheme의 마지막 부분에 "in = SecuritySchemeIn.COOKIE" 라는 옵션이 존재한다!
public class SwaggerConfig {

}
