
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.util.*;

/**
 * Detectar costuras
 */
public class Main4{
    static final int NUMEROFILAS = 7;
    static final int NUMEROCOLUMNAS = 7;
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
            if(grafoL.E.get(idNodo).contains(nodo) && grafoL.V[nodo] == 0){ //si el nodo est?? conectado y no lo hemos visitado aun
                hijos.add(nodo);
            }
        }
        return (hijos);
    }

    public static void busquedaProfundidadListas(GrafoL grafo, int idNodo, int etiqueta){
        grafo.V[idNodo]=etiqueta; //nodo visitado

        //ver cuales son sus hijos
        ArrayList<Integer> hijos;
        hijos = buscarHijosListas(grafo,idNodo);

        for (Integer idHijo: hijos) {
            if(grafo.V[idHijo] == 0){ //si el nodo no ha sido visitado aun
                busquedaProfundidadListas(grafo,idHijo,etiqueta);
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

        //Recorrer los ??ndices originales de las habitaciones
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
                    }else{//SI LOS NODOS NO EST??N CONECTADOS Y EL VALOR DE LAS ETIQUETAS ES DIFERENTE HEMOS ENCONTRADO COSTURA
                        if(grafo.V[idNodo(i,j)]!=grafo.V[idNodo(i,j+1)]){
                            representar[i * 2 + 1][j * 2 + 2] = COSTURA;
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
                    }else{//SI LOS NODOS NO EST??N CONECTADOS Y EL VALOR DE LAS ETIQUETAS ES DIFERENTE HEMOS ENCONTRADO COSTURA
                        if(grafo.V[idNodo(i,j)]!=grafo.V[idNodo(i+1,j)]){
                            representar[i * 2 + 2][j * 2 + 1] = COSTURA;
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
        String nombreFich = "COSTURAS"+ "-P" + probabilidad + "-S" + semilla + "-D" + NUMEROFILAS + "x" + NUMEROCOLUMNAS+ ".png";

        int profundidad = 1;
        GrafoL grafo;
        double[][] representar;
        //Representar ejes con listas de adyacencia
        grafo = generarLaberintosListas(probabilidad,semilla);
        int idNodoInicio;
        int etiqueta = 1;
        for (idNodoInicio = 0; idNodoInicio < NUMERONODOS; idNodoInicio++) {
            if(grafo.V[idNodoInicio]==0){ //si el nodo no ha sido visitado auN
                busquedaProfundidadListas(grafo,idNodoInicio,etiqueta);
                etiqueta++;
            }
        }
        representar = dibujarLaberintosListas(grafo);
        dibujarMapa(representar,nombreFich);
    }
}
