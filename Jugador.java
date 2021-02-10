public interface Jugador {
   //constantes que se pueden emplear para definir si la m�quina juega con blancas 
   //o con negras	
   public final static int BLANCAS = 1;
   public final static int NEGRAS = -1;
   
   //permite especificar de qu� lado juega la m�quina. Por defecto
   //(en el constructor de la clase) deber�an ser negras
   public void fijarLado(int lado);
   //calcula la mejor jugada para la posici�n actual de las piezas mediante alfa-beta
   public Jugada hacerJugada(Tablero t);
   //permite fijar la profundidad m�xima en el �rbol de b�squeda
   public void fijarNivelMaximo(int nivel);
}	