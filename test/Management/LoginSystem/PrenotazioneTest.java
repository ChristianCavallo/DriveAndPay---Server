/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management.LoginSystem;

import Management.Core.ScontiCore;
import Management.Core.VeicoliCore;
import Management.Database.DB;
import SerializedObjects.UserObjects.Utente;
import SerializedObjects.coreObjects.Fattura;
import SerializedObjects.coreObjects.Noleggio;
import SerializedObjects.coreObjects.Prenotazione;
import SerializedObjects.coreObjects.Veicolo;
import drivepay.server.DrivePayServer;
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
public class PrenotazioneTest {

    public PrenotazioneTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        VeicoliCore.Initialize();
        ScontiCore.Initialize();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void PrenotazioneCompletaTest() {

        try {
            Utente u = new Utente(798379828, "chrychry96@gmail.com", "12345");
            Prenotazione p = new Prenotazione((int) (Math.random() * 999999999));
            Veicolo v = new Veicolo(1111);
            p.setVeicolo(v);
            p.setUtente(u);

            Boolean result = VeicoliCore.prenotaVeicolo(p);

            assertTrue(result);

            result = VeicoliCore.terminaPrenotazione(u);

            assertTrue(result);

            Fattura f = DB.getFatturaByPrenotazione(p);

            assertNotNull(f);

            String query = "DELETE FROM FATTURE WHERE PRENOTAZIONE = " + p.getId();
            int res = DB.executeUpdate(query);
            assertEquals(res, 1);

            query = "DELETE FROM PRENOTAZIONI WHERE ID = " + p.getId();
            res = DB.executeUpdate(query);
            assertEquals(res, 1);

        } catch (Exception ex) {
            Logger.getLogger(DrivePayServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @Test
    public void EsistePrenotazioneCorrenteTest() {

        Prenotazione p = new Prenotazione((int) (Math.random() * 999999999));

        Utente u = new Utente(798379828, "chrychry96@gmail.com", "12345");
        Veicolo v = new Veicolo(1111);
        p.setUtente(u);
        p.setVeicolo(v);

        Boolean a;
        try {
            a = VeicoliCore.prenotaVeicolo(p);
            assertTrue(a);
        } catch (Exception ex) {
            fail();
            Logger.getLogger(PrenotazioneTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            p = new Prenotazione((int) (Math.random() * 999999999));
            p.setUtente(u);
            VeicoliCore.prenotaVeicolo(p);
        } catch (Exception e) {
            
            assertEquals( "Esiste già una prenotazione aperta!", e.getMessage());
        }

        try {
            a = VeicoliCore.terminaPrenotazione(u);
            assertTrue(a);
        } catch (Exception ex) {
            fail();
            Logger.getLogger(PrenotazioneTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @Test
    public void PrenotazioneConNoleggioTest() {

        Utente u = new Utente(798379828, "chrychry96@gmail.com", "12345");
        Noleggio n;
        try {
            n = VeicoliCore.richiediNoleggio(u);
            assertNotNull(n);
            assertNull(n.getInizio());

            Veicolo v = new Veicolo(1111);
            n.setUtenteCorrente(u);
            n.setVeicoloCorrente(v);

            Boolean a = VeicoliCore.noleggiaVeicolo(n);
            assertTrue(a);

            try {
                Prenotazione p = new Prenotazione((int) (Math.random() * 999999999));

                p.setVeicolo(v);
                p.setUtente(u);

                VeicoliCore.prenotaVeicolo(p);

            } catch (Exception e) {
      
                assertEquals(e.getMessage(), "Esiste un noleggio in corso!");
            }

            Boolean b = VeicoliCore.terminaNoleggio(u);
            assertTrue(b);

            String query = "DELETE FROM FATTURE WHERE NOLEGGIO = " + n.getId();
            int res = DB.executeUpdate(query);
            assertEquals(res, 1);

            query = "DELETE FROM NOLEGGI WHERE ID = " + n.getId();
            res = DB.executeUpdate(query);
            assertEquals(res, 1);
        } catch (Exception ex) {
            fail();
            Logger.getLogger(DrivePayServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
   

}
