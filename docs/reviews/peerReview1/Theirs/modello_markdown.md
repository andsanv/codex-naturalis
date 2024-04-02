# Peer-Review 1: UML

Samuele Pischedda, Angelo Prete, Gabriele Raveggi, Andrea Sanvito

**Gruppo GC11**

Valutazione del diagramma UML delle classi del gruppo GC01.

## Lati positivi
 
- (**Da mettere anche come confronto tra architetture**) Implementazione (*a differenza nostra*) del
cambiamento di stato dei corner utilizzando un `boolean` (*e non con cambiamento di classe a runtime*).
- Implementazione dello strategy pattern per calcolare i punti delle `ObjectiveCard`
- Separazione di carte che possono essere giocate (`PlayableCard`), con carte che hanno uno scopo diverso (`ObjectiveCard`);
- L'utilizzo delle enumerazioni ottimizza la gestione degli oggetti rappresentati sulle carte di un gioco e dei relativi metodi associati.
## Lati negativi

### Generali
- Tutti gli attributi nell'UML sono pubblici, nonostante siano stati aggiunti dei getter;
- Utilizzo incorretto della sintassi UML (e.g. non è esplicito se sia `Card` che `Deck` siano classi astratte o meno, non hanno infatti nè metodi nè attributi);
- Non sempre si è fedeli alla nomenclatura di gioco usata nel rulebook (e.g. RadixCards (StarterCards), Wolf (Animal), Mushroom (Fungi), ecc.);
- Molte classi mancano ancora di attributi e metodi essenziali al loro funzionamento.


### Class-Specific
- La classe `Room` secondo il nostro parere, contiene entità concettualmente scollegate, che andrebbero separate. È inoltre poco commentata e poco self-explanatory;
- La classe `Room`, se come abbiamo inteso, è l'interfaccia tra model e controller, sarebbe meglio non contenesse metodi e attributi propri del modello di gioco;
- La classe `GoldenCard` non dovrebbe essere sottoclasse di ResourceCard, bensì di PlayableCard (principio di Liskov);
- Il "back" di una carta `RadixCard` sarebbe meglio implementarlo come attributo della classe RadixCard piuttosto che come una sua sottoclasse;
- `RadixCard` è implementata come una carta giocabile, ma viene "assegnata" al giocatore a inizio partita.
- L'implementazione attraverso un Set di posizioni della board di gioco (`Field`) è molto contorta. Forse un'implementazione con una matrice (o una Map<Coordinate, PlayableCard>) sarebbe più adeguata.
- L'entità `Position` è ambigua. Converrebbe rappresentare le posizioni nella board con un sistema di coordinate x, y (non è specificato se già sia così o meno);
- Il punteggio `score` del singolo giocatore sarebbe meglio implementarlo a parte, piuttosto che nella classe Player (e.g. attraverso una classe `ScoreTrack`);
- Le interfacce `CardResources` e `PlayerResources` utilizzate per gli `enum` delle risorse/oggetti sono ridondanti *e non ne è chiaro l'utilità'*.

## Confronto tra le architetture

Individuate i punti di forza dell'architettura dell'altro gruppo rispetto alla
vostra, e quali sono le modifiche che potete fare alla vostra architettura per
migliorarla.

- Hanno implementato (a differenza nostra) un cambiamento di stato dei corner con boolean (e non con cambiamento di classe a runtime).
- L'idea di un'interfaccia comune per accorpare gli enum non è male.