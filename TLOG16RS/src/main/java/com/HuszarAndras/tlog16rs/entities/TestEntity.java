/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.HuszarAndras.tlog16rs.entities;


/**
 *
 * @author Andris
 */
@lombok.Getter
@lombok.Setter
public class TestEntity {
    private String text;
    private int id;
    
    public TestEntity(){
        id++;
    }
}
