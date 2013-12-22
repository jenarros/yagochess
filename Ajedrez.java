// FUNDAMENTOS DE INTELIGENCIA ARTIFICIAL
// CURSO 2002-03
// INGNENIERIA EN INFORMATICA

// Inicio del juego: java Ajedrez

import javax.swing.UIManager;

public class Ajedrez extends Object {

	public Ajedrez() {
		Juego frame = new Juego();
		frame.setTitle( "Fundamentos de Inteligencia Artificial: Ajedrez" );
		frame.setVisible( true );
	}

	public static void main( String[] args ) {
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch ( Exception e ) {
			System.out.println( "Error" );
		}

		// Crear una instancia
		new Ajedrez();
	}
}