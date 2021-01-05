En primer lugar respecto a movimiento de las fichas, han sido implementados todos los mivimientos comunes, incluído el posible avance de dos casillas por parte de un peón en su primera tirada.

Ya en relación a las jugadas menos comunes, también han sido implementadas las jugadas de enroque tanto largo como corto, captura al paso y promoción, esta última es el cambio por un caballo, alfil, torre o dama de un peón que alcanza la fila más alejada de su posición inicial).

La jugada de enroque se considera jugada de rey y por lo tanto para efectuarla basta con situar el rey en la posición que quedará tras efectuar el enroque.

Sobre la finalización de la partida, por supuesto el jaque y el jaque mate están implementados, en cambio la terminacion por tablas no ha sido completamente implementada. Para la terminación de la se puede decir que es necesario que se den una de las siguientes condiciones:
- Ahogado. Un jugador no puede mover y tampoco está en jaque.
- En 50 movimientos no se ha matado una ficha ni movido un peón.
- El dibujo del tablero (mismas fichas en mismas posiciones) se repite 3 veces.

Las dos primeras condiciones han sido implementadas, en cambio la tercera no lo ha sido por que suponía un incremeno en el tiempo de realización de cada jugada considerable y es una jugada muy poco probable de forma involuntaria, si la jugada se realiza de forma voluntaria es más sencillo acordar con el contrario el abandonar la partida.
