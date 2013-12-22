// FUNDAMENTOS DE INTELIGENCIA ARTIFICIAL
// CURSO 2002-03
// INGNENIERIA EN INFORMATICA


import javax.swing.*;
import java.awt.*;

public class PanelTablero extends JPanel{
	Tablero objetoTablero;
	
	public PanelTablero(){
		super();
		objetoTablero = null;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (objetoTablero != null)
			objetoTablero.dibujar(g);
	}
	
	public void ponTablero(Tablero tab){
		objetoTablero=tab;
	}
}
