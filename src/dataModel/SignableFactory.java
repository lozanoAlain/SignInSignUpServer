/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataModel;

/**
 *
 * @author Usuario
 */
public class SignableFactory {

    public Signable getSignable() throws Exception{
        Signable signable;
        signable = new SignableImplementation();
        return signable;
    }

}
