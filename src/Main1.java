
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Recorrer en profundidad con matriz de adyacencia y listas de adyacencia
 */
public class Main1 {
    static final int NUMEROFILAS = 15;
    static final int NUMEROCOLUMNAS = 15;
    static final int NUMERONODOS = NUMEROFILAS * NUMEROCOLUMNAS;
    static final int PARED = -10;
    static final int ESPACIO = 0;

    public static int idNodo(int fila,int columna){
            int id = fila * NUMEROCOLUMNAS + columna;
            return(id);
    }

    public static void dibujarMapa(double[][] representar,String nombreFich) throws IOException {
        HeatChart map = new HeatChart(representar);
        Color lightYellow = new Color(255, 255, 70);
        Color darkPink = new Color(153, 0, 76);
        map.setLowValueColour(lightYellow);
        map.setHighValueColour(darkPink);
        map.setShowXAxisValues(false);
        map.setShowYAxisValues(false);
        map.setColourScale(1.7);

        map.saveToFile(new File(nombreFich));
    }


    //Con matriz de adyacencia ------------------------------------------------------------------------------------------
    public static Grafo generarLaberintosMatriz(double probabilidad,int semilla){
        int[] V= new int[NUMERONODOS];
        int[][] E= new int[NUMERONODOS][NUMERONODOS]; //matriz de adyacencias

        Random rnd = new Random(semilla); //inicializar rand-float con semilla
        for(int i=0; i<NUMEROFILAS;i++){
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {
                V[idNodo(i,j)] = 0; //inicilizar a 0
                double random= rnd.nextDouble(); //genera un entero entre 0 y 1
                if(i>0 && random < probabilidad){
                    E[idNodo(i,j)][idNodo(i-1,j)]++;
                    E[idNodo(i-1,j)][idNodo(i,j)]++;
                }
                if(j>0 && random < probabilidad){
                    E[idNodo(i,j)][idNodo(i,j-1)]++;
                    E[idNodo(i,j-1)][idNodo(i,j)]++;
                }
            }
        }
        Grafo grafo = new Grafo(V,E);
        return(grafo);

    }

    public static void busquedaProfundidad(Grafo grafo, int idNodo){
        grafo.V[idNodo]=20; //nodo visitado

        for (int hijo = 0; hijo < NUMERONODOS; hijo++) {
            if(grafo.E[idNodo][hijo] != 0 && grafo.V[hijo] == 0){ //si el nodo está conectado y no lo hemos visitado aun
                busquedaProfundidad(grafo,hijo);
            }
        }
    }

    public static double[][] dibujarLaberintos(Grafo grafo) throws IOException {

        //Crear e Inicializar matriz de mapa de calor con ceros (paredes)
        double[][] representar = new double[NUMEROFILAS * 2 + 1][NUMEROCOLUMNAS * 2 + 1];
        for (int i = 0; i < NUMEROFILAS * 2 + 1; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS * 2 + 1; j++) {
                representar[i][j] = PARED;
            }
        }

        //Recorrer los índices originales de las habitaciones
        for (int i = 0; i < NUMEROFILAS; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {

                //PONER HABITACIONES Y PINTA RECORRIDOS
                if(grafo.V[idNodo(i,j)] != 0){ //si ha sido visitado pintar
                    representar[i * 2 + 1][j * 2 + 1] = grafo.V[idNodo(i,j)];
                } else{
                    representar[i * 2 + 1][j * 2 + 1] = ESPACIO;
                }

                int suma;
                //PONER PASILLOS Y PINTAR RECORRIDOS
                //Hacia la derecha
                if (j < NUMEROCOLUMNAS-1) { //saltamos la ultima columna de habitaciones
                    if(grafo.E[idNodo(i, j)][idNodo(i, j + 1)] !=0) { //Pintar pasillo sin recorrer
                        representar[i * 2 + 1][j * 2 + 2] = ESPACIO;

                        if(grafo.V[idNodo(i,j)]!=0 && grafo.V[idNodo(i,j+1)]!=0){ //Pintar pasillos recorrridos
                            representar[i * 2 + 1][j * 2 + 2] = grafo.V[idNodo(i,j)];
                        }
                    }
                }

                //Hacia la abajo
                if (i < NUMEROFILAS-1) {
                    if (grafo.E[idNodo(i, j)][idNodo(i + 1, j)] !=0) {
                        representar[i * 2 + 2][j * 2 + 1] = ESPACIO;

                        if(grafo.V[idNodo(i,j)]!=0 && grafo.V[idNodo(i+1,j)]!=0){ //Pintar pasillos recorrridos
                            representar[i * 2 + 2][j * 2 + 1] = grafo.V[idNodo(i,j)];
                        }
                    }
                }
            }
        }

        return(representar);

    }

    //Con listas de adyacencia -----------------------------------------------------------------------------------------------
    public static GrafoL generarLaberintosListas(double probabilidad,int semilla){
        int V[]= new int[NUMERONODOS];
        ArrayList<ArrayList<Integer>> E = new ArrayList(); //vector de arrayList que contienen la lista de nodos unidos a cada nodo
        ArrayList<Integer> lista = new ArrayList(NUMERONODOS);
        Random rnd = new Random(semilla); //inicializar rand-float con semilla

        for (int i = 0; i < NUMERONODOS; i++) {
            E.add((ArrayList<Integer>)lista.clone());
        }

        for(int i=0; i<NUMEROFILAS;i++){
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {
                V[idNodo(i,j)] = 0; //inicilizar a 0
                double random= rnd.nextDouble(); //genera un entero entre 0 y 1
                if(i>0 && random < probabilidad){
                    E.get(idNodo(i,j)).add(idNodo(i-1,j));
                    E.get(idNodo(i-1,j)).add(idNodo(i,j));
                }
                if(j>0 && random < probabilidad){
                    E.get(idNodo(i,j)).add(idNodo(i,j-1));
                    E.get(idNodo(i,j-1)).add(idNodo(i,j));
                }
            }
        }

        GrafoL grafo = new GrafoL(V,E);
        return(grafo);
    }

    public static ArrayList<Integer> buscarHijosListas(GrafoL grafoL, int idNodo){
        ArrayList<Integer> hijos= new ArrayList<>();
        for (int nodo = 0; nodo < NUMERONODOS; nodo++) {
            //if(grafo.E[idNodo][nodo] != 0 && grafo.V[nodo] == 0){ MATRIZ
            if(grafoL.E.get(idNodo).contains(nodo) && grafoL.V[nodo] == 0){ //si el nodo está conectado y no lo hemos visitado aun
                hijos.add(nodo);
            }
        }
        return (hijos);
    }

    public static void busquedaProfundidadListas(GrafoL grafo, int idNodo){
        grafo.V[idNodo]=20; //nodo visitado

        //ver cuales son sus hijos
        ArrayList<Integer> hijos;
        hijos = buscarHijosListas(grafo,idNodo);

        for (Integer idHijo: hijos) {
            if(grafo.V[idHijo] == 0){ //si el nodo no ha sido visitado aun
                busquedaProfundidadListas(grafo,idHijo);
            }
        }
    }

    public static double[][] dibujarLaberintosListas(GrafoL grafo) {

        //Crear e Inicializar matriz de mapa de calor con ceros (paredes)
        double[][] representar = new double[NUMEROFILAS * 2 + 1][NUMEROCOLUMNAS * 2 + 1];
        for (int i = 0; i < NUMEROFILAS * 2 + 1; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS * 2 + 1; j++) {
                representar[i][j] = PARED;
            }
        }

        //Recorrer los índices originales de las habitaciones
        for (int i = 0; i < NUMEROFILAS; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {

                //PONER HABITACIONES Y PINTA RECORRIDOS
                if(grafo.V[idNodo(i,j)] != 0){ //si ha sido visitado pintar
                    representar[i * 2 + 1][j * 2 + 1] = grafo.V[idNodo(i,j)];
                } else{
                    representar[i * 2 + 1][j * 2 + 1] = ESPACIO;
                }
                int suma;
                //PONER PASILLOS Y PINTAR RECORRIDOS
                //Hacia la derecha
                if (j < NUMEROCOLUMNAS-1) { //saltamos la ultima columna de habitaciones
                    if(grafo.E.get(idNodo(i,j)).contains(idNodo(i,j+1))) { //Pintar pasillo sin recorrer
                        representar[i * 2 + 1][j * 2 + 2] = ESPACIO;

                        if(grafo.V[idNodo(i,j)]!=0 && grafo.V[idNodo(i,j+1)]!=0){ //Pintar pasillos recorrridos
                            representar[i * 2 + 1][j * 2 + 2] = grafo.V[idNodo(i,j)];
                        }
                    }
                }

                //Hacia la abajo
                if (i < NUMEROFILAS-1) {
                    if (grafo.E.get(idNodo(i, j)).contains(idNodo(i+1,j))) {
                        representar[i * 2 + 2][j * 2 + 1] = ESPACIO;

                        if(grafo.V[idNodo(i,j)]!=0 && grafo.V[idNodo(i+1,j)]!=0){ //Pintar pasillos recorrridos
                            representar[i * 2 + 2][j * 2 + 1] = grafo.V[idNodo(i,j)];
                        }
                    }
                }
            }
        }
        return(representar);
    }


    public static void main(String[] args) throws IOException {
        //Parametros de entrada:
        boolean matrizEjes = false; //true matriz adyacencia, false listas adyacencia
        double probabilidad = 0.7; //numero entre 0 y 1
        int semilla = 73;
        String nombreFich = "P " + probabilidad + "-S" + semilla + "-D" + NUMEROFILAS + "x" + NUMEROCOLUMNAS+ ".png";

        long t0,t1,t2;
        t1=0;
        t2=0;
        int idNodoInicio = 0;
        int profundidad = 1;
        Grafo grafo;
        GrafoL grafoL;
        double[][] representar;
        if(matrizEjes){                       //Representar ejes con matriz de adyacencia
        t0 = System.nanoTime();
            grafo = generarLaberintosMatriz(probabilidad,semilla);
            busquedaProfundidad(grafo,idNodoInicio);
            representar= dibujarLaberintos(grafo);
        t1 = System.nanoTime() - t0;

        }else{                                //Representar ejes con listas de adyacencia
        t0 = System.nanoTime();
            grafoL = generarLaberintosListas(probabilidad,semilla);
            busquedaProfundidadListas(grafoL,idNodoInicio);
            representar = dibujarLaberintosListas(grafoL);
        t2 = System.nanoTime() - t0;
        }
        //System.out.println("P " + probabilidad );
        //System.out.printf("Matriz: %.5f seg. Listas: %.5f seg.\n", 1e-9 * t1, 1e-9 * t2);
        dibujarMapa(representar,nombreFich);
    }
}
