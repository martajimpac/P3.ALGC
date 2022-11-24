
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.util.*;

/**
 * Crear un laberinto que solo tiene un recorrido
 */
public class Main5 {
    static final int NUMEROFILAS = 10;
    static final int NUMEROCOLUMNAS = 10;
    static final int NUMERONODOS = NUMEROFILAS * NUMEROCOLUMNAS;
    static final int PARED = -10;
    static final int ESPACIO = 0;
    static final int COSTURA = -2;

    public static int idNodo(int fila,int columna){
        int idNodo = fila * NUMEROCOLUMNAS + columna;
        return(idNodo);
    }

    //Con listas de adyacencia -----------------------------------------------------------------------------------------------
    public static void dibujarMapa(double[][] representar,String nombreFich) throws IOException {
        HeatChart map = new HeatChart(representar);
        Color lightYellow=new Color(255, 255, 70);
        Color darkPink=new Color(153, 0, 76);
        map.setLowValueColour(lightYellow);
        map.setHighValueColour(darkPink);
        map.setShowXAxisValues(false);
        map.setShowYAxisValues(false);
        map.setColourScale(1.2);

        map.saveToFile(new File(nombreFich));
    }


    //Con listas de adyacencia -----------------------------------------------------------------------------------------------
    public static GrafoL generarLaberintosListas(double probabilidad,int semilla){
        int[] V= new int[NUMERONODOS];
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

    public static void busquedaAnchuraListas(GrafoL grafo, int idNodo,int etiqueta){
        grafo.V[idNodo]=etiqueta; //nodo visitado

        Queue<Integer> cola = new LinkedList<>();
        cola.add(idNodo);

        int nodo;
        while(!cola.isEmpty()){
            nodo = cola.remove();

            for (int idHijo = 0; idHijo < NUMERONODOS; idHijo++) {
                if(grafo.E.get(nodo).contains(idHijo) && grafo.V[idHijo] == 0){ //si el nodo está conectado y no lo hemos visitado aun
                    grafo.V[idHijo]=etiqueta;
                    cola.add(idHijo);
                }
            }
        }
    }

    public static void busquedaProfundidadListas(GrafoL grafo, int idNodo, int etiqueta,int padre){
        grafo.V[idNodo]=etiqueta; //nodo visitado

        for (int idHijo = 0; idHijo < NUMERONODOS; idHijo++) {
            if (grafo.E.get(idNodo).contains(idHijo) && idHijo!= padre) {
                if (grafo.V[idHijo] == 0) { //si el nodo está conectado y no lo hemos visitado aun
                    busquedaProfundidadListas(grafo,idHijo,etiqueta,idNodo);
                }
                else{
                    //si  el nodo esta conectado y lo hemos vistado Y NO ES EL PADRE      SI ELIMINAMOS EJES QUITAMOS PASILLOS
                    int indice1 = grafo.E.get(idNodo).lastIndexOf(idHijo);
                    int indice2 = grafo.E.get(idHijo).lastIndexOf(idNodo);
                    grafo.E.get(idNodo).remove(indice1);
                    grafo.E.get(idHijo).remove(indice2);

                }
            }
        }
    }

    public static GrafoL costuras(GrafoL grafo) {


        //Recorrer los índices originales de las habitaciones
        for (int i = 0; i < NUMEROFILAS; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {
                if (j < NUMEROCOLUMNAS-1) { //saltamos la ultima columna de habitaciones
                    if(!grafo.E.get(idNodo(i,j)).contains(idNodo(i,j+1)) && grafo.V[idNodo(i,j)]!=grafo.V[idNodo(i,j+1)]){
                        grafo.E.get(idNodo(i,j)).add(idNodo(i,j+1));
                        grafo.E.get(idNodo(i,j+1)).add(idNodo(i,j));

                    }
                }
                if (i < NUMEROFILAS-1) {
                    if(!grafo.E.get(idNodo(i,j)).contains(idNodo(i+1,j)) && grafo.V[idNodo(i,j)]!=grafo.V[idNodo(i+1,j)]){
                        grafo.E.get(idNodo(i,j)).add(idNodo(i+1,j));
                        grafo.E.get(idNodo(i+1,j)).add(idNodo(i,j));
                    }
                }
            }
        }
        return grafo;
    }
    public static double[][] dibujarLaberintosListas(GrafoL grafo) {

        //Crear e Inicializar matriz de mapa de calor con ceros (paredes)
        double[][] representar = new double[NUMEROFILAS * 2 + 1][NUMEROCOLUMNAS * 2 + 1];
        for (int i = 0; i < NUMEROFILAS * 2 + 1; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS * 2 + 1; j++) {
                representar[i][j] = PARED;
            }
        }

        int dif;
        //Recorrer los índices originales de las habitaciones
        for (int i = 0; i < NUMEROFILAS; i++) {
            for (int j = 0; j < NUMEROCOLUMNAS; j++) {

                //PONER HABITACIONES Y PINTA RECORRIDOS
                if(grafo.V[idNodo(i,j)] != 0){ //si ha sido visitado pintar
                    representar[i * 2 + 1][j * 2 + 1] = grafo.V[idNodo(i,j)];
                } else{
                    representar[i * 2 + 1][j * 2 + 1] = ESPACIO;
                }
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
        double probabilidad = 0.5; //numero entre 0 y 1
        int semilla = 70;
        String nombreFich = "el UNICO RECORRIDO"+ "-P" + probabilidad + "-S" + semilla + "-D" + NUMEROFILAS + "x" + NUMEROCOLUMNAS+ ".png";

        int profundidad = 1;
        GrafoL grafo;
        double[][] representar;
        //CICLOS
        grafo = generarLaberintosListas(probabilidad,semilla);
        int idNodoInicio;
        int etiqueta = 1;
        for (idNodoInicio = 0; idNodoInicio < NUMERONODOS; idNodoInicio++) {
            if(grafo.V[idNodoInicio]==0) { //si el nodo no ha sido visitado aun
                busquedaProfundidadListas(grafo,idNodoInicio,etiqueta,-1);
                etiqueta++;
            }
        }
        grafo = costuras(grafo);
        for (idNodoInicio = 0; idNodoInicio < NUMERONODOS; idNodoInicio++) {
            grafo.V[idNodoInicio]=0;
        }
        for (idNodoInicio = 0; idNodoInicio < NUMERONODOS; idNodoInicio++) {
            if(grafo.V[idNodoInicio]==0) { //si el nodo no ha sido visitado aun
                busquedaProfundidadListas(grafo,idNodoInicio,etiqueta,-1);
                etiqueta++;
            }
        }

        representar = dibujarLaberintosListas(grafo);
        dibujarMapa(representar,nombreFich);
    }
}
