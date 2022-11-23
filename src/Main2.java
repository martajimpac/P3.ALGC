
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.util.*;

/**
 * Recorrer en profundidad y en anchura solo con listas de adyacencia, añadir limite de profundidad
 */
public class Main2{
    static final int NUMEROFILAS = 15;
    static final int NUMEROCOLUMNAS = 15;
    static final int NUMERONODOS = NUMEROFILAS * NUMEROCOLUMNAS;
    static final int PARED = -10;
    static final int ESPACIO = 0;

    public static int idNodo(int fila,int columna){
            int idNodo = fila * NUMEROCOLUMNAS + columna;
            return(idNodo);
    }

    public static void dibujarMapa(double[][] representar,String nombreFich) throws IOException {
        HeatChart map = new HeatChart(representar);
        Color lightYellow=new Color(255, 255, 70);
        Color darkPink=new Color(153, 0, 76);
        map.setLowValueColour(lightYellow);
        map.setHighValueColour(darkPink);
        map.setShowXAxisValues(false);
        map.setShowYAxisValues(false);
        map.setColourScale(1.7);

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

    public static void busquedaProfundidadListas(GrafoL grafo, int idNodo, int profundidad) {
        grafo.V[idNodo] = profundidad; //nodo visitado //COMO HACER ITERATIVO?? CREO QUE CON UNA PILA
        profundidad++;

        for (int idHijo = 0; idHijo < NUMERONODOS; idHijo++) {
            if (grafo.E.get(idNodo).contains(idHijo) && grafo.V[idHijo] == 0) { //si el nodo está conectado y no lo hemos visitado aun
                busquedaProfundidadListas(grafo, idHijo, profundidad);
            }
        }
    }

    public static void busquedaAnchuraListas(GrafoL grafo, int idNodo,int profundidad){
        grafo.V[idNodo]=profundidad; //nodo visitado
        profundidad++;

        Queue<Integer> cola = new LinkedList<>();
        cola.add(idNodo);

        int nodo;
        while(!cola.isEmpty()){
            nodo = cola.remove();

            for (int idHijo = 0; idHijo < NUMERONODOS; idHijo++) {
                if(grafo.E.get(nodo).contains(idHijo) && grafo.V[idHijo] == 0){ //si el nodo está conectado y no lo hemos visitado aun
                    grafo.V[idHijo]=profundidad;
                    profundidad++;
                    cola.add(idHijo);
                }
            }
        }
    }

    public static double[][] dibujarLaberintosListas(GrafoL grafo,boolean primeroProfundidad) {

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
                            int nodoMenor = grafo.V[idNodo(i,j)];
                            if(grafo.V[idNodo(i,j)]>grafo.V[idNodo(i,j+1)]) {//nos quedamos con el menor
                                nodoMenor = grafo.V[idNodo(i,j+1)];
                            }
                            dif = Math.abs(grafo.V[idNodo(i,j)]-grafo.V[idNodo(i,j+1)]);

                            if(!primeroProfundidad){
                                representar[i * 2 + 1][j * 2 + 2] = nodoMenor;
                            }
                            if(dif==1) representar[i * 2 + 1][j * 2 + 2] = nodoMenor;
                        }
                    }
                }

                //Hacia la abajo
                if (i < NUMEROFILAS-1) {
                    if (grafo.E.get(idNodo(i, j)).contains(idNodo(i+1,j))) {
                        representar[i * 2 + 2][j * 2 + 1] = ESPACIO;

                        if(grafo.V[idNodo(i,j)]!=0 && grafo.V[idNodo(i+1,j)]!=0){ //Pintar pasillos recorrridos
                            int nodoMenor = grafo.V[idNodo(i,j)];
                            if(grafo.V[idNodo(i,j)]>grafo.V[idNodo(i+1,j)]) {//nos quedamos con el menor
                                nodoMenor = grafo.V[idNodo(i+1,j)];
                            }dif = Math.abs(grafo.V[idNodo(i,j)]-grafo.V[idNodo(i+1,j)]);
                            if(!primeroProfundidad){
                                representar[i * 2 + 2][j * 2 + 1] = nodoMenor;
                            }
                            if(dif == 1) representar[i * 2 + 2][j * 2 + 1] = nodoMenor;
                        }
                    }
                }
            }
        }
        return(representar);
    }


    public static void main(String[] args) throws IOException {
        //Parametros de entrada:
        boolean primeroProfundidad = true; //true primero en Profundidad, false primero en anchura
        double probabilidad = 0.7; //numero entre 0 y 1
        int semilla = 70;
        String busqueda = primeroProfundidad == true ? "profundidad": "anchura";
        String nombreFich = "Busqueda "+ busqueda + "-P" + probabilidad + "-S" + semilla + "-D" + NUMEROFILAS + "x" + NUMEROCOLUMNAS+ ".png";

        int idNodoInicio = 0;
        int profundidad = 1;
        GrafoL grafoL;
        double[][] representar;
        grafoL = generarLaberintosListas(probabilidad,semilla);
        if(primeroProfundidad){
            busquedaProfundidadListas(grafoL,idNodoInicio,profundidad);
        }else {
            busquedaAnchuraListas(grafoL, idNodoInicio, profundidad);
        }
        representar = dibujarLaberintosListas(grafoL,primeroProfundidad);
        dibujarMapa(representar,nombreFich);
    }
}
