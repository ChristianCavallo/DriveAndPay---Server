/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management.LoginSystem;

import SerializedObjects.UserObjects.CartaCredito;
import SerializedObjects.UserObjects.Documento;
import SerializedObjects.UserObjects.InformazioniUtente;
import SerializedObjects.UserObjects.Utente;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class LoginServiceTest {
    
    public LoginServiceTest() {
    }
    
     @BeforeClass
    public static void setUpClass() {
        //System.out.println("BeforeClass");
    }
    
    @AfterClass
    public static void tearDownClass() {
        //System.out.println("AfterClass");
    }
    
    @Before
    public void setUp() {
        //System.out.println("Before");
    }
    
    @After
    public void tearDown() {
        //System.out.println("After");
    }

    /**
     * Test of login method, of class LoginService.
     */
    @Test
    public void testLogin() {
        System.out.println("login");   
        String email = "chrychry96@gmail.com";
        String password = "12345";

        Utente result = LoginService.login(email, password);

        assertNotNull(result.getInfoUtente());
   
    }

    /**
     * Test of registerUser method without Commit on DB, of class LoginService.
     */
    @Test
    public void testRegisterUser() throws Exception {
        System.out.println("registerUser");
        Utente u = new Utente("noemiBuggea@gmail.com", "12345");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date nascita = null;
        nascita = format.parse("23/09/1996");
        InformazioniUtente i = new InformazioniUtente("Noemi", "Buggea", nascita,"BGGNMO96P63E573K");
        Documento cartaIdentita = new Documento(1, "noemiCarta.png");
        Documento patente = new Documento(2, "noemiPatente.png");
        CartaCredito carta = new CartaCredito("1234432158966589","187");
        i.setCarta(carta);
        i.setCartaIdentita(cartaIdentita);
        i.setPatente(patente);
        u.setInfoUtente(i);
        
        Boolean result = LoginService.registerUser_Test(u);
        assertTrue(result);

    }

    /**
     * Test of getDiffYears method, of class LoginService.
     */
    @Test
    public void testGetDiffYears() {
        System.out.println("getDiffYears");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date first = new Date();
        try {
            first = format.parse("02/10/1996");
        } catch (ParseException ex) {
            Logger.getLogger(LoginServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Date last = new Date();

        int result = LoginService.getDiffYears(first, last);
        System.out.println(result);
        assertTrue(result > 18);
        /*
        first = new Date();
        result = LoginService.getDiffYears(first, last);
        assertTrue(result < 18);
    */
    }

}
