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
    
    public static double TARIFFA = 0.15;
    
    public static void Initialize(){
        veicoli = DB.fetchVeicoli();
        noleggiRichiesti = new ArrayList<>();
    }
    
    public static void updateVeicoli(){
        veicoli = DB.fetchVeicoli();
    }
    
    public static Boolean addVeicolo(Veicolo v){
        String query = "INSERT INTO VEICOLI VALUES (" + v.toStringDB() + ", 1)";
        try {
           int result = DB.executeUpdate(query);
           if(result == 1){
               veicoli = DB.fetchVeicoli();
               System.out.println("Veicolo aggiunto!");
               return true;
           }
        } catch (SQLException ex) {
            Logger.getLogger(VeicoliCore.class.getName()).log(Level.WARNING, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VeicoliCore.class.getName()).log(Level.WARNING, null, ex);
        }
        return false;
    }
    
    public static Boolean noleggiaVeicolo(Noleggio n) throws Exception{
        for(int x = 0; x < noleggiRichiesti.size(); x++){
            if(noleggiRichiesti.get(x).getId() == n.getId() && n.getUtenteCorrente().getId() == n.getUtenteCorrente().getId()){
                
                if(n.getVeicoloCorrente() == null){
                    throw new Exception("Veicolo non presente nella richiesta");
                }
                
                if(!DB.isVeicoloDisponibile(n.getVeicoloCorrente())){
                    throw new Exception("Veicolo non disponibile per il noleggio");
                }
                
             
                
                n.setInizio(new Date());
                noleggiRichiesti.remove(x);
                return DB.noleggiaVeicolo(n);
                
            }
        }
        return false;
    }
    
    public static Boolean terminaNoleggio(Utente u) throws Exception{
        Noleggio n = DB.NoleggiAperti(u); //riottengo il noleggio dal DB (Sicurezza)
        if(n != null){
            //TODO: check dello stato del veicolo
            
            //Imposto la fine del noleggio
            n.setFine(new Date());
            
            //Creo la fattura
            Fattura f = new Fattura(TARIFFA);
            if(n.getScontoCorrente() != null){
                f.setPercentualeSconto(n.getScontoCorrente().getPerc_sconto());
                System.out.println("Sconto applicato");
            }

            Long diff = TimeUnit.MILLISECONDS.toSeconds(n.getFine().getTime() - n.getInizio().getTime());
            double minutes = (double) Math.round(diff.doubleValue() / 60 * 100) / 100;
            f.setDurata(minutes);
            
            n.setFatturaCorrente(f);
            
            //Aggiorno il database
            if(DB.terminaNoleggio(n)){
                 return true;
            }
            
        } else {
            throw new Exception("Nessun noleggio in corso per l'utente " + u.getId());
        }
        
        return false;   
    }
    
    public static Noleggio richiediNoleggio(Utente u){
        Noleggio n = DB.NoleggiAperti(u);
        if( n == null){
            n = new Noleggio((int)(Math.random()* 999999999));      
            noleggiRichiesti.add(n);  
            System.out.println("Nuovo noleggio creato!");
        } else {
            System.out.println("Noleggia già esistente");
            
        }
         n.setUtenteCorrente(u);
        return n;
    }
    
    public static Veicolo getVeicolo(int codice){    
        for(int x = 0; x < veicoli.size(); x++){
            Veicolo v = veicoli.get(x);
            if(v.getCodice() == codice){
                return v;
            }
        }
        return null;        
    }
    
   
   
}
