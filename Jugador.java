public interface Jugador {
   //constantes que se pueden emplear para definir si la máquina juega con blancas 
   //o con negras	
   public final static int BLANCAS = 1;
   public final static int NEGRAS = -1;
   
   //permite especificar de qué lado juega la máquina. Por defecto
   //(en el constructor de la clase) deberían ser negras
   public void fijarLado(int lado);
   //calcula la mejor jugada para la posición actual de las piezas mediante alfa-beta
   public Jugada hacerJugada(Tablero t);
   //permite fijar la profundidad máxima en el árbol de búsqueda
   public void fijarNivelMaximo(int nivel);
}	