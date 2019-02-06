/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Management.LoginSystem;

import SerializedObjects.UserObjects.Utente;
import Management.Database.DB;
import java.util.Calendar;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author Christian
 */
public class LoginService {

    
    public static Utente login(String email, String password) {
        Utente u = DB.getUserByEmail(email);
        if (u != null) {
            if(u.getPassword().equals(password)){
                return u;
            }
        } 
        return null;
    }
    
    public static Boolean registerUser(Utente u) throws Exception{
     
        //1. Check if existing
        Utente usr = DB.getUserByEmail(u.getEmail());
        if(usr != null){
            throw new Exception("Utente già esistente!");
        }
        
        //2. Check parameters format
        if(u.getInfoUtente().getNome().length() > 25){
           throw new Exception("Nome troppo lungo! Inserisci qualche abbreviazione o diminuitivo."); 
        }
        
        if(u.getInfoUtente().getCognome().length() > 25){
           throw new Exception("Cognome troppo lungo! Inserisci qualche abbreviazione o diminuitivo."); 
        }
        
        if(u.getPassword().length() > 25){
           throw new Exception("Password troppo lunga..."); 
        }
      
        if (getDiffYears(u.getInfoUtente().getNascita(),new Date()) < 18){
            throw new Exception("Non hai l'età per registrarti (18+)!");
        }
        
        if(u.getInfoUtente().getCartaIdentita()== null){
            throw new Exception("Carta d'identità non caricata!");
        }
        
        if(u.getInfoUtente().getPatente()== null){
            throw new Exception("Patente non caricata!");
        }
        
        if(u.getInfoUtente().getCarta() == null){
            throw new Exception("Devi inserire la carta di credito");
        }
        
        //3. Email format check
       if(EmailValidator.getInstance().isValid(u.getEmail()) == false){
           throw new Exception("L'email inserita non risulta valida!");
       }
       
       //4. Make the registration
       return DB.addUserToDB(u);

        
    }
    
     public static Boolean registerUser_Test(Utente u) throws Exception{
     
        //1. Check if existing
        Utente usr = DB.getUserByEmail(u.getEmail());
        if(usr != null){
            throw new Exception("Utente già esistente!");
        }
        
        //2. Check parameters format
        if(u.getInfoUtente().getNome().length() > 25){
           throw new Exception("Nome troppo lungo! Inserisci qualche abbreviazione o diminuitivo."); 
        }
        
        if(u.getInfoUtente().getCognome().length() > 25){
           throw new Exception("Cognome troppo lungo! Inserisci qualche abbreviazione o diminuitivo."); 
        }
        
        if(u.getPassword().length() > 25){
           throw new Exception("Password troppo lunga..."); 
        }
      
        if (getDiffYears(u.getInfoUtente().getNascita(),new Date()) < 18){
            throw new Exception("Non hai l'età per registrarti (18+)!");
        }
        
        if(u.getInfoUtente().getCartaIdentita()== null){
            throw new Exception("Carta d'identità non caricata!");
        }
        
        if(u.getInfoUtente().getPatente()== null){
            throw new Exception("Patente non caricata!");
        }
        
        if(u.getInfoUtente().getCarta() == null){
            throw new Exception("Devi inserire la carta di credito");
        }
        
        //3. Email format check
       if(EmailValidator.getInstance().isValid(u.getEmail()) == false){
           throw new Exception("L'email inserita non risulta valida!");
       }
       
       //4. Make the registration
       return DB.addUserToDB_Test(u);

        
    }
    
    public static int getDiffYears(Date first, Date last) {
        Long diff = TimeUnit.MILLISECONDS.toDays(last.getTime()-first.getTime())/365;
        return diff.intValue();
    }

}
