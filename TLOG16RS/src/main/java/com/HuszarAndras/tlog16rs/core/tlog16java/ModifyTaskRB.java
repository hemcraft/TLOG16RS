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
public class ModifyTaskRB {
    int year;
    int month; 
    int day;
    String taskId;
    String startTime;
    String newTaskId;
    String newComment;
    String newStartTime;
    String newEndTime;
}
