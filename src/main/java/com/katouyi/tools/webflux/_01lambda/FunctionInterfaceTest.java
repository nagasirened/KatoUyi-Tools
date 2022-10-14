package com.katouyi.tools.webflux._01lambda;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

public class FunctionInterfaceTest {

    public static void main(String[] args) {
        Function<Integer, String> moneyFormat = i -> new DecimalFormat("#,###").format( i );
        // 转换一个结果，生成一个新的Function
        Function<Integer, String> testFunc1 = moneyFormat.andThen( s -> "人民币：" + s );
        System.out.println( testFunc1.apply( 99999 ) );

        // compose 与 andThen执行顺序相反，先执行compose再执行调用者
        Function<Integer, String> compose = moneyFormat.compose( (Integer num) -> (num * 2) );
        System.out.println( compose.apply( 80 ) );

        // 其他几种
        Consumer<Integer> consumer = System.out::println;
        consumer.accept( 1 );

        Supplier<Integer> supplier = () -> 1;
        System.out.println(supplier.get());

        Predicate<Integer> predicate = i -> i > 0;
        System.out.println(predicate.test( 3 ));

        /**
         * 一元函数:下面两个函数相同
         */
        Function<Integer, Integer> func = (i) -> i + 1;
        UnaryOperator<Integer> unary = (i) -> i + 1;
        IntUnaryOperator intUnary = (i) -> i + 1;
        System.out.println(func.apply( 1 ));
        System.out.println(unary.apply( 1 ));
        System.out.println(intUnary.applyAsInt( 1 ));

    }

}
