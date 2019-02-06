# DriveAndPay---Server

Questo progetto rappresenta il software server utilizzato per gestire completamente il sistema Drive&Pay.

Presenta le seguenti caratteristiche:
- Comunicazione Asyncrona Multithread
- Crittografia AES dei dati trasmessi
- Database locale (Derby DB)
- Gestione Veicoli
- Gestione Noleggi
- Gestione Utenti
- Gestione Prenotazioni
- Comunicazione ad oggetti Serializzati

# Setup Database
Nella repository è presente un file rar "drive_and_pay.rar". Esso contiene i file necessari per inizializzare il Derby DB.
Per utilizzarlo è necessario:
- Creare una nuova cartella chiamata "Database", nella stessa cartella dove risiede il file jar compilato del server
- decomprimere il file rar nella cartella appena creata
Nel caso in cui si decida di utilizzare un nuovo database, è necessario cambiare l'url di connessione nella classe "DB.java". Inoltre lanciando il server con il parametro "first", verranno inseriti alcuni veicoli e sconti di default nel db.

# Execution
Per eseguire il server, è necessario lanciare il comando: "java -jar <FileCompilato>.jar"
Viene utilizzata la porta 9696 per la connessione.
