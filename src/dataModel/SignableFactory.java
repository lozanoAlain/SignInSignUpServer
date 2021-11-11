/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataModel;

/**
 * Class for the Signable Factory
 *
 * @author Alain Lozano, Ilia Consuegra
 */
public class SignableFactory {

    /**
     * Method that get the signable implementation, and returns the initialized
     * interface.
     *
     * @return signable the interface
     * @throws Exception launched in case there is an error implementating the
     * interface
     */
    public Signable getSignable() throws Exception {
        Signable signable;
        signable = new SignableImplementation();
        return signable;
    }

}
