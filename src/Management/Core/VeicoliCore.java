/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management.Core;

import Management.Database.DB;
import SerializedObjects.UserObjects.Utente;
import SerializedObjects.coreObjects.Fattura;
import SerializedObjects.coreObjects.Noleggio;
import SerializedObjects.coreObjects.Prenotazione;
import SerializedObjects.coreObjects.Sconto;
import SerializedObjects.coreObjects.Veicolo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian
 */
public class VeicoliCore {

    public static ArrayList<Veicolo> veicoli;
    private static ArrayList<Noleggio> noleggiRichiesti;

    public static double TARIFFA_NOLEGGIO = 0.15;
    public static double TARIFFA_PRENOTAZIONE = 0.10;
    public static double MINUTI_GRATIS_PRENOTAZIONE = 20.0;

    public static void Initialize() {
        veicoli = DB.fetchVeicoli();
        noleggiRichiesti = new ArrayList<>();
    }

    public static void updateVeicoli() {
        veicoli = DB.fetchVeicoli();
    }

    public static Boolean addVeicolo(Veicolo v) {
        String query = "INSERT INTO VEICOLI VALUES (" + v.toStringDB() + ", 1)";
        try {
            int result = DB.executeUpdate(query);
            if (result == 1) {
                veicoli = DB.fetchVeicoli();
                System.out.println("Veicolo aggiunto!");
                return true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
        return false;
    }
    
    public static Boolean removeVeicolo(Veicolo v){
        String query = "DELETE FROM VEICOLI WHERE CODICE = " + v.getCodice();
        try {
            int result = DB.executeUpdate(query);
            if (result == 1) {
                veicoli = DB.fetchVeicoli();
                System.out.println("Veicolo rimosso!");
                return true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public static Boolean prenotaVeicolo(Prenotazione p) throws Exception {
        //Non devono esserci noleggi in corso o altre prenotazioni!!
        Prenotazione p2 = DB.PrenotazioniAperte(p.getUtente());
        Noleggio n = DB.NoleggiAperti(p.getUtente());
        if (p2 != null) {
            throw new Exception("Esiste già una prenotazione aperta!");
        }
        if (n != null) {
            throw new Exception("Esiste un noleggio in corso!");
        }

        if (p.getVeicolo() == null) {
            throw new Exception("Veicolo non presente nella richiesta");
        }

        if (!DB.isVeicoloDisponibile(p.getVeicolo())) {
            throw new Exception("Veicolo non disponibile per la prenotazione");
        }

        p.setInizio(new Date());
        return DB.prenotaVeicolo(p);
    }

    public static Boolean terminaPrenotazione(Utente u) throws Exception {
        Prenotazione p = DB.PrenotazioniAperte(u); //riottengo la prenotazione dal DB (Sicurezza)
        if (p != null) {

            //Imposto la fine della prenotazione
            p.setFine(new Date());

            //Creo la fattura
            Fattura f = new Fattura(TARIFFA_PRENOTAZIONE);

            Long diff = TimeUnit.MILLISECONDS.toSeconds(p.getFine().getTime() - p.getInizio().getTime());
            double minutes = (double) Math.round(diff.doubleValue() / 60 * 100) / 100;
            minutes = minutes - MINUTI_GRATIS_PRENOTAZIONE;
            if (minutes <= 0) {
                f.setDurata(0);
            } else {
                f.setDurata(minutes);
            }

            p.setFattura(f);

            return DB.terminaPrenotazione(p);

        } else {
            throw new Exception("Nessuna prenotazione in corso per l'utente " + u.getId());
        }

    }

    public static Boolean noleggiaVeicolo(Noleggio n) throws Exception {
        for (int x = 0; x < noleggiRichiesti.size(); x++) {
            if (noleggiRichiesti.get(x).getId() == n.getId() && n.getUtenteCorrente().getId() == n.getUtenteCorrente().getId()) {

                if (n.getVeicoloCorrente() == null) {
                    throw new Exception("Veicolo non presente nella richiesta");
                }

                if (!DB.isVeicoloDisponibile(n.getVeicoloCorrente())) {
                    throw new Exception("Veicolo non disponibile per il noleggio");
                }

                n.setInizio(new Date());
        
                noleggiRichiesti.remove(x);
                return DB.noleggiaVeicolo(n);

            }
        }
        return false;
    }

    public static Boolean terminaNoleggio(Utente u) throws Exception {
        Noleggio n = DB.NoleggiAperti(u); //riottengo il noleggio dal DB (Sicurezza)
        if (n != null) {
            //TODO: check dello stato del veicolo

            //Imposto la fine del noleggio
            n.setFine(new Date());

            //Creo la fattura
            Fattura f = new Fattura(TARIFFA_NOLEGGIO);
            if (n.getScontoCorrente() != null) {
                f.setPercentualeSconto(n.getScontoCorrente().getPerc_sconto());
                System.out.println("Sconto applicato");
            }

            Long diff = TimeUnit.MILLISECONDS.toSeconds(n.getFine().getTime() - n.getInizio().getTime());
            double minutes = (double) Math.round(diff.doubleValue() / 60 * 100) / 100;
            f.setDurata(minutes);

            n.setFatturaCorrente(f);

            //Aggiorno il database
            if (DB.terminaNoleggio(n)) {
                return true;
            }

        } else {
            throw new Exception("Nessun noleggio in corso per l'utente " + u.getId());
        }

        return false;
    }

    public static Noleggio richiediNoleggio(Utente u) {
        Noleggio n = DB.NoleggiAperti(u);
        
        if (n == null) {
            n = new Noleggio((int) (Math.random() * 999999999));

            noleggiRichiesti.add(n);
     
        } else {
            System.out.println("Noleggia già esistente");

        }
        n.setUtenteCorrente(u);
        return n;
    }

    public static Veicolo getVeicolo(int codice) {
        for (int x = 0; x < veicoli.size(); x++) {
            Veicolo v = veicoli.get(x);
            if (v.getCodice() == codice) {
                return v;
            }
        }
        return null;
    }
    
}
