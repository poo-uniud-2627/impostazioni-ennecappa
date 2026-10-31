// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);
    
    private static int puntiPerVincere_Locali = STANDARD.puntiPerVincere();
    private static int scartoLocale = STANDARD.scartoMinimo();
    private static int velocitaLocale = STANDARD.velocitaPalla();
    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if (puntiPerVincere < 1 || puntiPerVincere > 99) {
            throw new IllegalArgumentException("puntiPerVincere è " + puntiPerVincere + " ma deve essere fra 1 e 99 compresi.");
        }
        if (scartoMinimo < 1 || scartoMinimo > 10) {
            throw new IllegalArgumentException("scartoMinimo è " + scartoMinimo + " ma deve essere fra 1 e 10 compresi.");
        }
        if (velocitaPalla < 1 || velocitaPalla > 20) {
            throw new IllegalArgumentException("velocitaPalla è " + velocitaPalla + " ma deve essere fra 1 e 20 compresi.");
        }
    }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        if(!righe.isEmpty()){
            //Reset iniziale ai valori standard: garantisce che le chiavi non presenti nel file
            //mantengano il valore di default, e pulisce lo stato in caso di chiamate multiple al metodo.
            puntiPerVincere_Locali = STANDARD.puntiPerVincere();
            scartoLocale = STANDARD.scartoMinimo();
            velocitaLocale = STANDARD.velocitaPalla();
            //Variabili "Flag" per intercettare il caso in cui una chiave venga ripetuta nel file.
            boolean visto_PuntiPerVincere = false;
            boolean visto_Scarto = false;
            boolean visto_Velocita = false;

            String rigaAttuale;
            int j;

            for(int i=0; i<righe.size(); i++){
                rigaAttuale = (righe.get(i)).trim();

                //Ignora le righe vuote o i commenti (che iniziano per #)
                if(!rigaAttuale.startsWith("#") && !rigaAttuale.isEmpty()){

                    //Trova l'indice del separatore "="
                    j = rigaAttuale.indexOf("=");
                    if(j == -1) throw new IllegalArgumentException("riga " + (i+1) + ": manca il segno '=' ");

                    try {
                        //Estrae la parte a sinistra dell'uguale e toglie gli spazi
                        switch (rigaAttuale.substring(0, j).trim()) {

                            case "puntiPerVincere":
                                if(!visto_PuntiPerVincere){
                                    visto_PuntiPerVincere = true;

                                    //Estrae la parte a destra dell'uguale, toglie gli spazi e la converte in numero
                                    puntiPerVincere_Locali = Integer.parseInt((rigaAttuale.substring(j+1).trim()));

                                    //Controllo subito se il valore "puntiPerVincere" è valido, creando un oggetto Impostazioni di prova
                                    try {
                                        new Impostazioni(puntiPerVincere_Locali, STANDARD.scartoMinimo, STANDARD.velocitaPalla);
                                    } catch (IllegalArgumentException e) {

                                        //Si cattura l'eccezione del costruttore e la si rilancia con il prefisso "riga N:" 
                                        throw new IllegalArgumentException("riga " + (i + 1) + ": " + e.getMessage());
                                    }
                                    break;

                                } else throw new IllegalArgumentException("riga " + (i+1) + ": la chiave 'puntiPerVincere' è già stata istanziata in precedenza");

                            case "scartoMinimo":
                                if(!visto_Scarto){
                                    visto_Scarto = true;
                                    scartoLocale = Integer.parseInt((rigaAttuale.substring(j+1).trim()));

                                    //Controllo subito se il valore "scartoMinimo" è valido, creando un oggetto Impostazioni di prova
                                    try {
                                        new Impostazioni(STANDARD.puntiPerVincere, scartoLocale, STANDARD.velocitaPalla);

                                    } catch (IllegalArgumentException e) {
                                        throw new IllegalArgumentException("riga " + (i + 1) + ": " + e.getMessage());
                                    }
                                    break;

                                } else throw new IllegalArgumentException("riga " + (i+1) + ": la chiave 'scartoMinimo' è già stata istanziata in precedenza");
                                
                            case "velocitaPalla":
                                if(!visto_Velocita){
                                    visto_Velocita = true;
                                    velocitaLocale = Integer.parseInt((rigaAttuale.substring(j+1).trim()));

                                    //Controllo subito se il valore "velocitaPalla" è valido, creando un oggetto Impostazioni di prova
                                    try {
                                        new Impostazioni(STANDARD.puntiPerVincere, STANDARD.scartoMinimo, velocitaLocale);

                                    } catch (IllegalArgumentException e) {

                                        throw new IllegalArgumentException("riga " + (i + 1) + ": " + e.getMessage());
                                    }
                                    break;

                                } else throw new IllegalArgumentException("riga " + (i+1) + ": la chiave 'velocitaPalla' è già stata istanziata in precedenza");

                            default:
                                throw new IllegalArgumentException("riga " + (i+1) + ": non è stata trovata una delle tre chiavi");
                        }
                    } catch (NumberFormatException e) {
                        //Errore del valore fornito diverso da un intero
                        throw new IllegalArgumentException("riga " + (i+1) + ": il valore non è un intero");
                    }
                    
                }
            } 
        }

        //Creazione finale del record: userà i valori letti dal file oppure, se assenti, i valori standard assegnati all'inizio
        return new Impostazioni(puntiPerVincere_Locali, scartoLocale, velocitaLocale); // scrivete qui
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
