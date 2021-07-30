package com.katouyi.tools.proto.protoStuff.test;

import io.protostuff.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ExtraInfo implements Serializable {

    @Tag(1)
    private String sex;

    @Tag(2)
    private Integer isSingle;
}