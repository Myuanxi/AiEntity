package com.myuanxi;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AiEntity {
    String model();
    String url();
    String apikey();
}