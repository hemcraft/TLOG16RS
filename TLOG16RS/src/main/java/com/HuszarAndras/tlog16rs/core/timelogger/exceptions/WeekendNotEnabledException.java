/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.core.timelogger.exceptions;

/**
 *
 * @author Andris
 */
public class WeekendNotEnabledException extends Exception{
    public WeekendNotEnabledException(String message){
        super(message);
        //406
    }
}
