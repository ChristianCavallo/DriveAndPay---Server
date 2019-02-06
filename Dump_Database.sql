DROP TABLE FATTURE;
DROP TABLE PRENOTAZIONI;
DROP TABLE NOLEGGI;
DROP TABLE SCONTI;
/*DROP TABLE VEICOLI;
DROP TABLE DOCUMENTI;
DROP TABLE CARTACREDITO;
DROP TABLE INFOUTENTI;
DROP TABLE UTENTI;*/

CREATE TABLE UTENTI(ID INTEGER NOT NULL, EMAIL CHAR(50) NOT NULL UNIQUE, PASSWORD CHAR(15) NOT NULL,
PRIMARY KEY(ID));

CREATE TABLE INFOUTENTI(ID INTEGER NOT NULL, NOME CHAR(30) NOT NULL, COGNOME CHAR(30) NOT NULL,
NASCITA CHAR(10) NOT NULL, CF CHAR(16) NOT NULL UNIQUE,
FOREIGN KEY (ID) REFERENCES UTENTI(ID));

CREATE TABLE DOCUMENTI(UTENTE INTEGER NOT NULL,
TIPO INTEGER, RIFERIMENTO CHAR(30),
PRIMARY KEY (UTENTE, TIPO),
FOREIGN KEY (UTENTE) REFERENCES UTENTI(ID));

CREATE TABLE CARTACREDITO(UTENTE INTEGER NOT NULL, NUMERO CHAR(16) UNIQUE NOT NULL,
CVV INT NOT NULL,
PRIMARY KEY(UTENTE, NUMERO),
FOREIGN KEY (UTENTE) REFERENCES UTENTI(ID));

CREATE TABLE VEICOLI(CODICE INTEGER NOT NULL PRIMARY KEY, CARBURANTE INTEGER, LATITUDINE DOUBLE, LONGITUDINE DOUBLE, DISPONIBILE INT);
CREATE TABLE SCONTI(CODICE CHAR(10) PRIMARY KEY, PERCENTUALE INTEGER);

CREATE TABLE NOLEGGI(ID INTEGER PRIMARY KEY, VEICOLO INTEGER, UTENTE INTEGER, SCONTO CHAR(10),
 INIZIO CHAR(25), FINE CHAR(25),
FOREIGN KEY (VEICOLO) REFERENCES VEICOLI(CODICE),
FOREIGN KEY (UTENTE) REFERENCES UTENTI(ID),
FOREIGN KEY (SCONTO) REFERENCES SCONTI(CODICE));

CREATE TABLE PRENOTAZIONI(ID INTEGER PRIMARY KEY, VEICOLO INTEGER, UTENTE INTEGER,
 INIZIO CHAR(25), FINE CHAR(25),
FOREIGN KEY (VEICOLO) REFERENCES VEICOLI(CODICE),
FOREIGN KEY (UTENTE) REFERENCES UTENTI(ID));

CREATE TABLE FATTURE(NOLEGGIO INTEGER, PRENOTAZIONE INTEGER, DURATA DOUBLE, TOTALE DOUBLE,
FOREIGN KEY(PRENOTAZIONE) REFERENCES PRENOTAZIONI(ID),
FOREIGN KEY(NOLEGGIO) REFERENCES NOLEGGI(ID));
