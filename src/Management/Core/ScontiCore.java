/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management.Core;

import Management.Database.DB;
import SerializedObjects.coreObjects.Sconto;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian
 */
public class ScontiCore {

    public static ArrayList<Sconto> sconti;

    public static void Initialize() {
        sconti = DB.fetchSconti();
    }

    public static Boolean addSconto(Sconto s) {
        String query = "INSERT INTO SCONTI VALUES (" + s.toStringDB() + ")";
        try {
            int result = DB.executeUpdate(query);
            if (result == 1) {
                sconti = DB.fetchSconti();
                System.out.println("Sconto aggiunto!");
                return true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(VeicoliCore.class.getName()).log(Level.WARNING, null, ex);
        }
        return false;
    }

    public static Sconto getSconto(String codice) {
        codice = codice.trim().toLowerCase();
        for (int x = 0; x < sconti.size(); x++) {
            Sconto c = sconti.get(x);
            System.out.println(codice + "   " + c.getCodice());
            if (c.getCodice().trim().toLowerCase().equals(codice)) {
                return c;
            }
        }
        return null;
    }
    
    public static Boolean deleteSconto(String codice){
        String query = "DELETE FROM SCONTI WHERE CODICE = '" + codice +"'";
        try {
            int result = DB.executeUpdate(query);
            if (result == 1) {
                sconti = DB.fetchSconti();
                System.out.println("Sconto rimosso!");
                return true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(VeicoliCore.class.getName()).log(Level.WARNING, null, ex);
        }
        return false;
    }

}
