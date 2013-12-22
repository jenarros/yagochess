OBJ = Juego.class Tablero.class LibJugada.class Jugador.class
COMP = javac
OPC = 
all:$(OBJ)
	$(COMP) $(OPC) Principal.java
Juego.class:Juego.java
	$(COMP) $(OPC) Juego.java
Tablero.class:Tablero.java
	$(COMP) $(OPC) Tablero.java
LibJugada.class:LibJugada.java
	$(COMP) $(OPC) LibJugada.java
PanelTablero.class:PanelTablero.java
	$(COMP) $(OPC) PanelTablero.java
Jugador.class:Jugador.java
	$(COMP) $(OPC) Jugador.java
clean:
	rm -rf *.class *~
