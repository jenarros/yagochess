import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;
public class JugadaValor {
	JugadaValor(){
		j = new Jugada();
        camino = new LinkedList();
        n = 0;
        v = 0;
	}
	int v;//valor de la jugada
    int n;//contador de nodos procesados
	Jugada j;//jugada realizada, puede ser null
    LinkedList camino;//nodos recorridos
}
