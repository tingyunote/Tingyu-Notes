package com.yong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String vid;
    private Long time;
    private int alarmCode;
    private int handResult;
}
