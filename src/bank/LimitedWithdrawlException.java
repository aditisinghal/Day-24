/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bank;

/**
 *
 * @author ADITI
 */
public class LimitedWithdrawlException extends Exception{
    public LimitedWithdrawlException(String s)
    {
        super(s);
    }
}
