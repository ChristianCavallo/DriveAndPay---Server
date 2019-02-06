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
public class NoleggioTest {

    public NoleggioTest() {
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
    public void VeicoloTest() {
        Veicolo v = new Veicolo(9999, 100, 10.1, 4.5);
        Boolean a = VeicoliCore.addVeicolo(v);

        Boolean b = VeicoliCore.removeVeicolo(v);
        assertTrue(a);
        assertTrue(b);
    }

    @Test
    public void TestCompletoNoleggio() {
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

            Boolean b = VeicoliCore.terminaNoleggio(u);
            assertTrue(b);

            Fattura f = DB.getFatturaByNoleggio(n);
            assertNotNull(f);

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

    @Test
    public void EsisteNoleggioCorrenteTest() {
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

            //Un noleggio esistente presenta una data di inizio impostata
            Noleggio n2 = VeicoliCore.richiediNoleggio(u);
            assertNotNull(n2.getInizio());
            
            String query = "DELETE FROM NOLEGGI WHERE ID = " + n.getId();
            int res = DB.executeUpdate(query);
            assertEquals(res, 1);
            
            query = "UPDATE VEICOLI SET DISPONIBILE = 1 WHERE CODICE = " + v.getCodice();
            res = DB.executeUpdate(query);
            assertEquals(res, 1);
            
        } catch (Exception ex) {
            fail();
            Logger.getLogger(DrivePayServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
