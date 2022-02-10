# Anonymous chat
Progetto sviluppato come parte del corso di architetture distribuite per il cloud.

## Scopo del progetto
L'obiettivo del progetto è di creare un sistema peer-to-peer nel quale gli utenti possono inviare e ricevere messaggi all'interno di chat anonime. Ogni utente può entrare nella rete ed eseguire le stesse operazioni di un qualsiasi altro peer, tali operazioni sono:
1. Creazione di una stanza
2. Sottoscrizione ad una stanza
3. Uscita da una stanza
4. Invio di un messaggio
5. Lista delle stanze a cui si è iscritti
6. Lista delle stanze disponibili
7. Eliminazione dalla rete

## Tecnologie utilizzate
- Eclipse
- Java 8
- Apache Maven
- Docker
- TomP2P
- JUnit 5

## Struttura del progetto
Il progetto contiene un file Maven che presenta tutte le dipendenze necessarie, un estratto è riportato di seguito

```
<repositories>
  
	<repository>
	  <id>tomp2p.net</id>
	  <url>http://tomp2p.net/dev/mvn/</url>
	</repository>
	
  </repositories>
  
  <dependencies>

	<dependency>
	  <groupId>net.tomp2p</groupId>
	  <artifactId>tomp2p-all</artifactId>
	  <version>5.0-Beta8</version>
	</dependency>

	<dependency>
	  <groupId>org.junit.jupiter</groupId>
	  <artifactId>junit-jupiter-engine</artifactId>
   	  <version>5.5.2</version>
	</dependency>
 </dependencies>
 ```
 
 Il package `src/main` contiene cinque classi:
 1. `AnonymousUser.java` è la classe responsabile della creazione del peer e dell'esecuzione delle varie operazioni nella rete.
 2. `ChatRoom.java` viene utilizzata per strutturare i dati da salvare.
 3. `Main.java` contiene l'interfaccia atta alla navigazione fra le varie opzioni e alla visualizzazione dei messaggi.
 4. `Message.java` permette di creare un messaggio.
 5. `MessageListener.java` è il listener che si occupa di gestire la ricezione dei messaggi.
 
 Il package `src/test` contiene la classe di test utilizzata per testare tutti i metodi del progetto contenuti nel package appena descritto.
 
 ## Esecuzione del progetto su Docker
 
 Per utilizzare il progetto su Docker è necessario utilizzare il Dockerfile fornito. L'immagine su Docker viene creata tramite il seguente comando:
 `docker build -t anonymous-chat .`
 
 ## Creazione del master peer
 
 In seguito alla creazione del container è possibile generare il master peer con il seguente comando:
 `docker run -i --name master-peer anonymous-chat`
 Tramite le due variabili di ambiente poste nel Dockerfile si stabiliscono l'IP e l'ID del peer, di questi l'ultimo è unico in quanto deve essere possibile identificare ogni peer.
 
 ## Creazione dei generic peer
 
 Dopo aver eseguito i passi precedenti è possibile creare un numero a piacere di altri peer in maniera molto simile, bisogna però ottenere l'IP del container master tramite i   comandi:
 1. `docker ps`
 2. `docker inspect <ID del container>`
 
 Una volta ottenuta questa informazione è possibile generare gli altri peer con lo stesso comando:
 `docker run -i --name <name-of-peer> -e MASTER=<IP master-peer> -e ID=<peer-ID> anonymous-chat`
 
 ### Nota
 Per poter rieseguire il container dopo averlo chiuso è possibile sfruttare il comando
 `docker start -i <nome-peer>`
