package com.katouyi.tools.springcloud.gateway;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 *          自定义断言工厂   示例链接：https://www.jianshu.com/p/dca2d440bd6d
 *  1.断言工厂命名以 RoutePredicateFactory 结尾，前面剩下的内容即为断言配置，以下例子就是  -Token=123\*
 *  2.AbstractRoutePredicateFactory<C>  中的 C 就是要配置的值，一般是固定值、参数名或者正则表达式
 *  3.注入Bean
 */

@Component
public class TokenRoutePredicateFactory extends AbstractRoutePredicateFactory<TokenRoutePredicateFactory.Config> {

    public static final String TOKEN_KEY = "token";

    // 注入Config.class
    public TokenRoutePredicateFactory() {
        super(TokenRoutePredicateFactory.Config.class);
    }

    // 验证用的
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                HttpHeaders headers = serverWebExchange.getRequest().getHeaders();
                List<String> tokens = Optional.ofNullable(headers.get(TOKEN_KEY)).orElse(new ArrayList<>());
                String pattern = config.getTokenPattern();
                for (String token : tokens) {
                    if (token.matches(pattern)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static class Config {

        private String tokenPattern;

        public String getTokenPattern() {
            return tokenPattern;
        }

        public void setTokenPattern(String tokenPattern) {
            this.tokenPattern = tokenPattern;
        }
    }
}
