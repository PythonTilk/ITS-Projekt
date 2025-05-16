/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NotizProjekt_All;

/**
 *
 * @author maximilian.kaden
 */
public class User {
    private String UName;
    private String UPasswort;
    private int B_id;

    public User(String UName, String UPasswort, int B_id) {
        this.UName = UName;
        this.UPasswort = UPasswort;
        this.B_id = B_id;
    }

    public int getB_id() {
        return B_id;
    }

 
    public String getUName() {
        return UName;
    }

    public String getUPasswort() {
        return UPasswort;
    }
    
}
