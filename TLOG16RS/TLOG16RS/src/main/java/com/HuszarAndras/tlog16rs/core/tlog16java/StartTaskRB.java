/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.tlog16java;

/**
 *
 * @author Andris
 */
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
public class StartTaskRB {
    
    private int year;
    private int month;
    private int day;
    private String taskId;
    private String startTime;
    private String comment;
}
