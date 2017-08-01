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
public class EmptyTimeFieldException extends Exception{
    public EmptyTimeFieldException(String message){
        super(message);
    }

    public EmptyTimeFieldException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
