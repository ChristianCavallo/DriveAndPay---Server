/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management.Database;

import Management.Core.VeicoliCore;
import SerializedObjects.UserObjects.InformazioniUtente;
import SerializedObjects.UserObjects.Utente;
import SerializedObjects.coreObjects.Fattura;
import SerializedObjects.coreObjects.Noleggio;
import SerializedObjects.coreObjects.Prenotazione;
import SerializedObjects.coreObjects.Sconto;
import SerializedObjects.coreObjects.Veicolo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian
 */
public class DB {

    public static Connection createDatabaseConnection() throws SQLException, ClassNotFoundException {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);
        String url = "jdbc:derby://localhost:1527/drive_and_pay";

        //System.setProperty("derby.system.home","C:\\Users\\Christian\\Documents\\NetBeansProjects\\UniHome_Server\\Database\\");
        //String url = "jdbc:derby:.//Database//drive_and_pay";
        Connection c = DriverManager.getConnection(url, "root", "password12345");

        return c;
    }

    public static ResultSet executeQuery(String qry) throws SQLException, ClassNotFoundException {

        Statement stmt = createDatabaseConnection().createStatement();
        ResultSet rs = stmt.executeQuery(qry);
        stmt.closeOnCompletion();
        return rs;
    }

    public static int executeUpdate(String qry) throws SQLException, ClassNotFoundException {

        Statement stmt = createDatabaseConnection().createStatement();
        int rs = stmt.executeUpdate(qry);
        stmt.closeOnCompletion();
        return rs;
    }

    public static Utente getUserByEmail(String email) {
        String query = "SELECT U.ID, U.EMAIL, U.PASSWORD, I.NOME, I.COGNOME, I.NASCITA, I.CF FROM UTENTI U JOIN INFOUTENTI I ON U.ID = I.ID WHERE EMAIL =" + "'" + email + "'";
        try {
            ResultSet res = executeQuery(query);
            Utente u = null;
            while (res.next()) {
                u = new Utente(res.getInt("ID"), res.getString("EMAIL").trim(), res.getString("PASSWORD").trim());
                InformazioniUtente i = new InformazioniUtente(res.getString("NOME"), res.getString("COGNOME"), res.getString("NASCITA"), res.getString("CF"));
                u.setInfoUtente(i);
            }

            return u;

        } catch (Exception ex) {
            Logger.getLogger(DB.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }

    }

    public static boolean isVeicoloDisponibile(Veicolo v) {
        String query = "SELECT * FROM VEICOLI WHERE CODICE = " + v.getCodice() + " AND DISPONIBILE = 1";
        try {

            ResultSet res = executeQuery(query);
            int count = 0;
            while (res.next()) {
                count++;
            }
            if (count > 0) {
                return true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex);
        }

        return false;
    }

    public static Boolean prenotaVeicolo(Prenotazione p) {
        try {
            Connection conn = createDatabaseConnection();
            conn.setAutoCommit(false);
            Statement db = conn.createStatement();

            //Imposto il veicolo come non disponibile
            String query = "UPDATE VEICOLI SET DISPONIBILE = 0 WHERE CODICE = " + p.getVeicolo().getCodice();
            int res;
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Veicolo aggiornato a disponibile");

            //Creo un nuovo noleggio
            query = "INSERT INTO PRENOTAZIONI VALUES (" + p.toStringDB() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }

            conn.commit();
            System.out.println("Prenotazione inserita");
            return true;

        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());

            return false;
        }
    }

    public static Prenotazione PrenotazioniAperte(Utente u) {
        try {
            String query = "SELECT * FROM PRENOTAZIONI P JOIN VEICOLI V ON P.VEICOLO = V.CODICE WHERE P.UTENTE = " + u.getId() + " AND P.FINE = ''";

            ResultSet res = executeQuery(query);
            Prenotazione p = null;
            while (res.next()) {
                p = new Prenotazione(res.getInt("ID"));
                String c = res.getString("INIZIO");
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                p.setInizio(format.parse(c));
                Veicolo v = new Veicolo(res.getInt("VEICOLO"));
                p.setVeicolo(v);
                p.setUtente(new Utente(res.getInt("UTENTE"), "", ""));
            }

            return p;

        } catch (SQLException | ClassNotFoundException | ParseException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public static Fattura getFatturaByPrenotazione(Prenotazione p) {
        String query = "SELECT * FROM FATTURE WHERE PRENOTAZIONE = " + p.getId();
        Fattura f = null;
        try {
            ResultSet res = executeQuery(query);
            while (res.next()) {
                f = new Fattura(VeicoliCore.TARIFFA_PRENOTAZIONE);
                f.setDurata(res.getDouble("DURATA"));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
        return f;
    }

    public static Boolean terminaPrenotazione(Prenotazione p) {
        try {
            Connection conn = createDatabaseConnection();
            conn.setAutoCommit(false);
            Statement db = conn.createStatement();

            //Imposto il veicolo come non disponibile
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String query = "UPDATE PRENOTAZIONI SET FINE = '" + format.format(p.getFine()) + "' WHERE ID = " + p.getId();
            int res;
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Prenotazione terminata");

            //Aggiorno il veicolo a disponibile
            query = "UPDATE VEICOLI SET DISPONIBILE = 1 WHERE CODICE = " + p.getVeicolo().getCodice();
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Veicolo aggiornato a disponibile");

            //Creo la fattura
            query = "INSERT INTO FATTURE VALUES (null," + p.getId() + ", " + p.getFattura().getDurata() + ", " + p.getFattura().getTotale() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Fattura creata.");

            conn.commit();

            db.close();
            conn.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }

    public static Boolean terminaNoleggio(Noleggio n) {
        try {
            Connection conn = createDatabaseConnection();
            conn.setAutoCommit(false);
            Statement db = conn.createStatement();

            //Imposto il veicolo come non disponibile
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String query = "UPDATE NOLEGGI SET FINE = '" + format.format(n.getFine()) + "' WHERE ID = " + n.getId();
            int res;
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Noleggio terminato");

            //Aggiorno il veicolo a disponibile
            query = "UPDATE VEICOLI SET DISPONIBILE = 1 WHERE CODICE = " + n.getVeicoloCorrente().getCodice();
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Veicolo aggiornato a disponibile");

            //Creo la fattura
            query = "INSERT INTO FATTURE VALUES (" + n.getId() + ", null," + n.getFatturaCorrente().getDurata() + ", " + n.getFatturaCorrente().getTotale() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Fattura creata.");

            conn.commit();

            db.close();
            conn.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
        }
        return false;
    }

    public static Fattura getFatturaByNoleggio(Noleggio n) {
        String query = "SELECT * FROM FATTURE WHERE NOLEGGIO = " + n.getId();
        Fattura f = null;
        try {
            ResultSet res = executeQuery(query);
            while (res.next()) {
                f = new Fattura(VeicoliCore.TARIFFA_NOLEGGIO);
                f.setDurata(res.getDouble("DURATA"));
                
                try {
                    if (n.getScontoCorrente() != null) {
                        f.setPercentualeSconto(0);
                    } else {
                        f.setPercentualeSconto(n.getScontoCorrente().getPerc_sconto());
                    }

                } catch (Exception ex) {
                     f.setPercentualeSconto(0);
                }

            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
        return f;
    }

    public static Noleggio NoleggiAperti(Utente u) {
        try {
            String query = "SELECT * FROM NOLEGGI N WHERE N.UTENTE = " + u.getId() + " AND N.FINE = ''";

            ResultSet res = executeQuery(query);
            Noleggio n = null;
            String c = null;
            while (res.next()) {
                n = new Noleggio(res.getInt("ID"));
                c = res.getString("SCONTO");

                Veicolo v = new Veicolo(res.getInt("VEICOLO"));
                n.setVeicoloCorrente(v);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    n.setInizio(format.parse(res.getString("INIZIO")));
                } catch (ParseException ex) {
                    System.out.println("Ho parsato male la data.. strano");
                }
            }

            if (c != null && n != null) {
                query = "SELECT * FROM SCONTI WHERE CODICE = '" + c + "'";
                res = executeQuery(query);
                while (res.next()) {
                    Sconto s = new Sconto(c, res.getInt("PERCENTUALE"));
                    n.setScontoCorrente(s);
                }
            }

            return n;
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public static ArrayList<Sconto> fetchSconti() {
        String query = "SELECT * FROM SCONTI";
        try {
            ResultSet res = executeQuery(query);
            ArrayList<Sconto> sconti = new ArrayList<>();
            while (res.next()) {
                Sconto s = new Sconto(res.getString("CODICE"), res.getInt("PERCENTUALE"));
                sconti.add(s);
            }

            return sconti;

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }

    public static ArrayList<Veicolo> fetchVeicoli() {
        String query = "SELECT * FROM VEICOLI WHERE DISPONIBILE = 1";
        try {
            ResultSet res = executeQuery(query);
            ArrayList<Veicolo> veicoli = new ArrayList<>();
            while (res.next()) {
                Veicolo v = new Veicolo(res.getInt("CODICE"));
                v.setCarburante(res.getInt("CARBURANTE"));
                v.setLatitude(res.getDouble("LATITUDINE"));
                v.setLongitude(res.getDouble("LONGITUDINE"));
                veicoli.add(v);
            }

            return veicoli;

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }

    public static Boolean noleggiaVeicolo(Noleggio n) {
        try {
            Connection conn = createDatabaseConnection();
            conn.setAutoCommit(false);
            Statement db = conn.createStatement();

            //Imposto il veicolo come non disponibile
            String query = "UPDATE VEICOLI SET DISPONIBILE = 0 WHERE CODICE = " + n.getVeicoloCorrente().getCodice();
            int res;
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Veicolo aggiornato a non disponibile");

            //Creo un nuovo noleggio
            query = "INSERT INTO NOLEGGI VALUES (" + n.toStringDB() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }

            conn.commit();
            System.out.println("Noleggio inserito");
            return true;

        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());

            return false;
        }
    }

    public static Boolean addUserToDB(Utente u) {

        try {
            Connection conn = createDatabaseConnection();
            conn.setAutoCommit(false);
            Statement db = conn.createStatement();

            //Inserimento nuovo utente
            String query = "INSERT INTO UTENTI VALUES " + u.toStringDB() + "";

            int res;
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Utente inserito");
            //Inserimento info utente
            query = "INSERT INTO INFOUTENTI VALUES (" + u.getId() + ", " + u.getInfoUtente().toStringDB() + ")";
            res = db.executeUpdate(query);

            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("InformazioniUtente inserito");

            //Inserimento documenti
            query = "INSERT INTO DOCUMENTI VALUES (" + u.getId() + ", "
                    + u.getInfoUtente().getCartaIdentita().toStringDB() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Carta d'identità inserita");

            //Inserimento documenti
            query = "INSERT INTO DOCUMENTI VALUES (" + u.getId() + ", "
                    + u.getInfoUtente().getPatente().toStringDB() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Patente inserita");

            //Inserimento carta di creito
            query = "INSERT INTO CARTACREDITO VALUES (" + u.getId() + ", " + u.getInfoUtente().getCarta().toStringDB() + ")";
            System.out.println(query);
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Carta di credito inserita");
            conn.commit();

            db.close();
            conn.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());

            return false;
        }

    }
    
    public static Boolean addUserToDB_Test(Utente u) {

        try {
            Connection conn = createDatabaseConnection();
            conn.setAutoCommit(false);
            Statement db = conn.createStatement();

            //Inserimento nuovo utente
            String query = "INSERT INTO UTENTI VALUES " + u.toStringDB() + "";

            int res;
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Utente inserito");
            //Inserimento info utente
            query = "INSERT INTO INFOUTENTI VALUES (" + u.getId() + ", " + u.getInfoUtente().toStringDB() + ")";
            res = db.executeUpdate(query);

            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("InformazioniUtente inserito");

            //Inserimento documenti
            query = "INSERT INTO DOCUMENTI VALUES (" + u.getId() + ", "
                    + u.getInfoUtente().getCartaIdentita().toStringDB() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Carta d'identità inserita");

            //Inserimento documenti
            query = "INSERT INTO DOCUMENTI VALUES (" + u.getId() + ", "
                    + u.getInfoUtente().getPatente().toStringDB() + ")";
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Patente inserita");

            //Inserimento carta di creito
            query = "INSERT INTO CARTACREDITO VALUES (" + u.getId() + ", " + u.getInfoUtente().getCarta().toStringDB() + ")";
            System.out.println(query);
            res = db.executeUpdate(query);
            if (res == 0) {
                conn.rollback();
                db.close();
                conn.close();
                return false;
            }
            System.out.println("Carta di credito inserita");
            //conn.commit(); Metodo di test non usiamo il commit
            conn.rollback();
            db.close();
            conn.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());

            return false;
        }

    }

}
