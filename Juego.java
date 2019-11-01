// FUNDAMENTOS DE INTELIGENCIA ARTIFICIAL
// CURSO 2002-03
// INGNENIERIA EN INFORMATICA

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

// Interfaz gráfico
public class Juego extends JFrame {

  private Point posOrigen;
  boolean inicioMov = true;
  static Image[ ] figuras = new Image[ 13 ];
  BorderLayout border1 = new BorderLayout();
  static PanelTablero p = new PanelTablero();
  Container p2 = Box.createVerticalBox();
  Tablero tabla;

  JButton b1 = new JButton( "  Crear     " );
  JButton b2 = new JButton( "Configurar" );
  JButton b3 = new JButton( "  Empezar " );
  JButton b4 = new JButton( "  Cargar   " );
  JButton b5 = new JButton( "  Grabar   " );
  JButton b6 = new JButton( "  Salir      " );

  Component espacio0 = Box.createGlue();
  Component espacio1 = Box.createGlue();
  Component espacio2 = Box.createGlue();
  Component espacio3 = Box.createGlue();
  Component espacio4 = Box.createGlue();
  Component espacio5 = Box.createGlue();
  Component espacio6 = Box.createGlue();


  //private volatile Thread runnit;

  //Constructor
  public Juego() {
    enableEvents( AWTEvent.WINDOW_EVENT_MASK );
    try
    {
      iniciar();
    }
    catch ( Exception e )
    {
      System.out.println("Error:" + e);
    }
  }

  private void iniciar() throws Exception {
    //Carga de las figuras
    Toolkit t;
    t = getToolkit();
    figuras[ 0 ] = null;
    figuras[ 1 ] = t.getImage( "img/peon.gif" );
    figuras[ 2 ] = t.getImage( "img/caballo.gif" );
    figuras[ 3 ] = t.getImage( "img/alfil.gif" );
    figuras[ 4 ] = t.getImage( "img/torre.gif" );
    figuras[ 5 ] = t.getImage( "img/reina.gif" );
    figuras[ 6 ] = t.getImage( "img/rey.gif" );
    figuras[ 7 ] = t.getImage( "img/peonNegro.gif" );
    figuras[ 8 ] = t.getImage( "img/caballoNegro.gif" );
    figuras[ 9 ] = t.getImage( "img/alfilNegro.gif" );
    figuras[ 10 ] = t.getImage( "img/torreNegro.gif" );
    figuras[ 11 ] = t.getImage( "img/reinaNegro.gif" );
    figuras[ 12 ] = t.getImage( "img/reyNegro.gif" );

    Container cont = getContentPane();
    cont.setLayout( new BorderLayout() );

    //el panel p es para el tablero
    p.setLayout( new BorderLayout() );
    p.setBackground( Color.lightGray );
    p.setPreferredSize( new Dimension( 530, 520 ) );

    cont.add( p, BorderLayout.WEST );

    p.addMouseListener( new AccionListener() );

    b1.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                              crear( e );
                            }
                          }
                        );

    b2.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                              configurar( e );
                            }
                          }
                        );

    b3.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                              empezar( e );
                            }
                          }
                        );

    b4.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                              cargar( e );
                            }
                          }
                        );
    b5.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                              grabar( e );
                            }
                          }
                        );
    
    b6.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                              salir( e );
                            }
                          }
                        );
    //el panel p2 es para los botones
    p2.setSize( new Dimension( 120, 520 ) );
    b1.setPreferredSize( new Dimension( 110, 40 ) );
    b2.setPreferredSize( new Dimension( 110, 40 ) );
    b3.setPreferredSize( new Dimension( 110, 40 ) );
    b4.setPreferredSize( new Dimension( 110, 40 ) );
    b5.setPreferredSize( new Dimension( 110, 40 ) );
    b6.setPreferredSize( new Dimension( 110, 40 ) );
    p2.add( espacio0 );
    p2.add( b1 );
    p2.add( espacio1 );
    p2.add( b2 );
    p2.add( espacio2 );
    p2.add( b3 );
    p2.add( espacio3 );
    p2.add( b4 );
    p2.add( espacio4 );
    p2.add( b5 );
    p2.add( espacio5 );
    p2.add( b6 );
    p2.add( espacio6 );
    cont.add( p2, BorderLayout.EAST );

    pack();
  }

  protected void processWindowEvent( WindowEvent e ) {
    super.processWindowEvent( e );
    if ( e.getID() == WindowEvent.WINDOW_CLOSING )
    {
      System.exit( 0 );
    }
    else if ( e.getID() == WindowEvent.WINDOW_ACTIVATED )
    {
      e.getWindow().repaint();
    }
  }


  //Método encargado de recuperar una partida almacenada
  void cargar( ActionEvent e ) {
    String nombre;

    try
    {
      FileDialog fDialogo = new FileDialog( this );
      fDialogo.setMode( FileDialog.LOAD );
      fDialogo.show();
      nombre = fDialogo.getFile();
      FileInputStream fileStream = new FileInputStream( nombre );
      ObjectInputStream stream = new ObjectInputStream( fileStream );

      tabla = ( Tablero ) stream.readObject();
      p.ponTablero( tabla );
      p.repaint();
      System.out.println("contador_tablas =" + tabla.contador_tablas);
      System.out.println("acabada =" + tabla.acabada);
      stream.close();
      fileStream.close();
    }
    catch ( Exception exc )
    {
      System.out.println( "Error de lectura en fichero" + exc );
    }
  }

  //Método encargado de grabar una configuración del tablero
  void grabar( ActionEvent e ) {
    String nombre;

    try
    {
      FileDialog fDialogo = new FileDialog( this );
      fDialogo.setMode( FileDialog.SAVE );
      fDialogo.show();
      nombre = fDialogo.getFile();
      FileOutputStream fileStream = new FileOutputStream( nombre );
      ObjectOutputStream stream = new ObjectOutputStream( fileStream );

      stream.writeObject( tabla );
      stream.close();
      fileStream.close();
    }
    catch ( Exception exc )
    {
      System.out.println( "Error de escritura en fichero:" + exc );
    }
  }

  //Método encargado de empezar una partida
  void empezar( ActionEvent e ) {
    if (tabla == null){
      System.out.println("Primero debes crear el juego");
      return;
    }
    
    tabla.contador_tablas = 0;
    System.out.println("contador_tablas = 0");
    tabla.acabada = false;
    System.out.println("acabada = false");
    if(tabla.jugador1.tipo.equals("u") && tabla.jugador2.tipo.equals("m"))
      tabla.moverJugador2();
    //si los dos jugadores son máquinas dirigimos la partida desde aquí
    else if(tabla.jugador2.tipo.equals("m") && tabla.jugador1.tipo.equals("m"))
    {
      while(tabla.acabada == false)
      {
        try{
            Thread.sleep((long)10);
        }catch(Exception exc){
            System.out.println("Error: " + exc);
        }
        
        tabla.moverJugador2();
        if(!tabla.acabada)
          tabla.moverJugador1();
      }
    }
  }
  
  //Método que crea un tablero nuevo
  void crear( ActionEvent e){
    tabla = new Tablero();
    p.ponTablero( tabla );

    p.repaint();
  }
  //Método encargado de configurar los jugadores
  void configurar( ActionEvent e ) {
    if (tabla == null){
      System.out.println("Primero debes crear el juego");
      return;
    }
    int partida;
    BufferedReader entrada = new BufferedReader( new InputStreamReader( System.in ) );
    try
    {
      System.out.println( "******************CONFIGURACION DE JUGADORES******************" );
      System.out.println( "Introduzca el tipo de partida:" );
      System.out.println( "Tipo 1: Jugador1 = maquina1:negras  &  Jugador2 = USUARIO1:blancas" );
      System.out.println( "Tipo 2: Jugador1 = USUARIO1:negras  &  Jugador2 = maquina1:blancas" );
      System.out.println( "Tipo 3: Jugador1 = maquina1:negras  &  Jugador2 = maquina2:blancas" );
      System.out.println( "Tipo 4: Jugador1 = USUARIO1:negras  &  Jugador2 = USUARIO2:blancas" );
      do
      {
        System.out.print( "Introduzca el tipo de partida:[ 1 | 2 | 3 | 4 ]  " );
        partida = Integer.valueOf( entrada.readLine() ).intValue();
      }
      while ( partida < 1 || partida > 4 );

      if ( partida == 1 )
      {
        tabla.jugador1 = new Jenm("maquina1", -1, "m");
        System.out.print("Introduzca el nivel del jugador 1: ");
        tabla.jugador1.nivel = Integer.valueOf(entrada.readLine()).intValue();
        tabla.jugador2 = new Jenm("usuario1", 1, "u");
      }
      else if ( partida == 2 )
      {
        tabla.jugador1 = new Jenm("usuario1", -1, "u");
        tabla.jugador2 = new Jenm("maquina1", 1, "m");
        System.out.print("Introduzca el nivel del jugador 2: ");
        tabla.jugador2.nivel = Integer.valueOf(entrada.readLine()).intValue();
      }
      else if ( partida == 3 )
      {
        tabla.jugador1 = new Jenm("maquina1", -1, "m");
        System.out.print("Introduzca el nivel del jugador 1: ");
        tabla.jugador1.nivel = Integer.valueOf(entrada.readLine()).intValue();
        tabla.jugador2 = new Jenm("maquina2", 1, "m");
        System.out.print("Introduzca el nivel del jugador 2: ");
        tabla.jugador2.nivel = Integer.valueOf(entrada.readLine()).intValue();
        tabla.jugador2.funcion = 2;
      }
      else
      {
        tabla.jugador1 = new Jenm("usuario1", -1, "u");
        tabla.jugador2 = new Jenm("usuario2", 1, "u");
      }
      System.out.println( "JUGADOR \tNOMBRE\t\tTIPO\tNIVEL");
      System.out.println( "1-NEGRAS\t" + tabla.jugador1.nombre + "\t" + tabla.jugador1.tipo + "\t" + tabla.jugador1.nivel);
      System.out.println( "2-BLANCAS\t" + tabla.jugador2.nombre + "\t" + tabla.jugador2.tipo + "\t" + tabla.jugador2.nivel);
    }
    catch ( Exception exc )
    {
      System.out.println( "Error: " + exc );
      System.exit( -1 );
    }
  }

  //Salir del juego



  void salir( ActionEvent e ) {
    System.exit( 0 );
  }

  //***************************************************************************
  //Clase interna para manejar los eventos del ratón sobre el tablero de juego
  //****************************************************************************

  class AccionListener implements MouseListener {
    public Point posOrigen, posDestino;

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
      Point posicion = e.getPoint();
      if ( ( posicion.getX() > 19 ) && ( posicion.getX() < 501 ) &&
           ( posicion.getY() > 19 ) && ( posicion.getY() < 501 ) )
      {
        posOrigen = tabla.devuelveCasilla( posicion );
        inicioMov = false;
        //System.out.println("coger"+posOrigen.getX()+"  "+posOrigen.getY());
      }
    }

    public void mouseReleased( MouseEvent e ) {
      Point posicion = e.getPoint();

      if ( ( posicion.getX() > 19 ) && ( posicion.getX() < 501 ) &&
           ( posicion.getY() > 19 ) && ( posicion.getY() < 501 ) )
      {
        posDestino = tabla.devuelveCasilla( posicion );
        //System.out.println("dejar"+posDestino.getX()+"  "+posDestino.getY());
        inicioMov = true;
        tabla.moverFicha( posOrigen, posDestino );
      }
    }


    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }
  }
}
