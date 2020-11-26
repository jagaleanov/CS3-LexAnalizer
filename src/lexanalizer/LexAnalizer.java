package lexanalizer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LexAnalizer {

    private ArrayList<Token> tokenList = new ArrayList();
    private int tokenCounter = 0;

    //GENERALES
    private final String spaceRegEx = "[\\s]+";//espacios: salto de línea, espacio y tabular
    private final String numRegEx = "[0-9.]";//numérico
    private final String alphaRegEx = "[a-zA-Z]";//alfabético
    private final String alphanumRegEx = "[a-zA-Z0-9_]";//alfanumérico

    //OPERADORES
    //Operadores de un caracter (simple)
    private final String stringLimRegEx = "[\"]";//limitador de inicio/fin de cadenas de texto
    private final String endSentenceRegEx = "[;]";//operador de cierre de sentencia
    private final String assignRegEx = "[=]";//operador de asignación
    private final String mathRegEx = "[+-/%^()*]";//operadores artiméticos simples
    private final String logicRegEx = "[&|!]";//operadores lógicos simples
    private final String relRegEx = "[<>]";//operadores relacionales simples
    private final String specialRegEx = "[.,{}\\[\\]]";//signos de puntuación y caracteres especiales
    //Operadores de dos caracteres (comp)
    private final String mathCompRegEx = "([+-][=])|([+][+])|([-][-])";//operadores artiméticos compuestos +=,-=,++,--
    private final String logicCompRegEx = "([&][&])|([|][|])";//operadores lógicos compuestos &&,||
    private final String relCompRegEx = "([<>=!][=])";//operadores relacionales compuestos <=,>=,==,!=,

    //TIPOS
    private final String integerRegEx = "[0-9]+";//entero 
    private final String rationalRegEx = "[0-9]+([.][0-9]+)?|[.][0-9]+";//racional 
    private final String stringRegEx = "[\"][^\"]*[\"]";//cadena de texto 

    //PALABRAS RESERVADAS
    private final String[] reservedWords = {
        "verdadero",
        "falso",
        "mientras",
        "para",
        "si",
        "ademas"
    };

    public LexAnalizer(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void analize(String string) {
        int state = 0;
        /*
        -2:  Comentario
        -1:  Fin del texto
        0:   Vacio(inicial)
        1:   Espacio
        2:   Identificador
        3:   Número
        4:   Cadena de texto
        5:   Puntuación y caracteres especiales
        100: Operadores: lógicos, aritméticos, relacionales, asignación, fin de sentencia 
         */
        String type = "";
        String lexema = "";

        ArrayList<String> lines = separate(string, '\n');//separa el texto por líneas en un arrayList

        for (int i = 0; i < lines.size(); i++) {//por cada línea en el texto
            for (int j = 0; j < lines.get(i).length(); j++) {//por cada caracter en la línea

                char charActive;
                char charNext;
                boolean complete = false;

                charActive = lines.get(i).charAt(j);

                if (j == lines.get(i).length() - 1) {//si es el último caracter de la línea
                    charNext = ' ';//asignar el siguiente caracter como espacio para las comparaciones
                } else {//si NO es el último caracter de la línea
                    charNext = lines.get(i).charAt(j + 1);//asignar el siguiente caracter de la línea para las comparaciones
                }

                if (state == 0) {//si el estado es vacio
                    state = stateTransition(charActive);//revisar caracter activo y asignar estado
                }

                switch (state) {
                    case -2://comentario
                        break;
                    case -1://comentario
                        break;
                    case 1://espacio
                        break;
                    case 2://identificador (inicia con alfabético)
                        lexema += lines.get(i).charAt(j);
                        if (!Pattern.matches(alphanumRegEx, String.valueOf(charNext))) {//si el siguiente caracter no es alfanumérico
                            boolean assigned = false;
                            for (int k = 0; k < reservedWords.length; k++) {
                                if (String.valueOf(reservedWords[k]).equals(String.valueOf(lexema))) {
                                    type = "Palabra reservada";
                                    state = 0;
                                    assigned = true;
                                    break;
                                }
                            }

                            if (!assigned) {
                                type = "Identificador";
                                state = 0;
                            }
                        }
                        break;
                    case 3://número
                        lexema += lines.get(i).charAt(j);

                        if (!Pattern.matches(numRegEx, String.valueOf(charNext))) {//si el siguiente caracter no es número o separador decimal
                            if (Pattern.matches(integerRegEx, String.valueOf(lexema))) {//si el lexema es un entero
                                type = "Número entero";
                                state = 0;
                            } else if (Pattern.matches(rationalRegEx, String.valueOf(lexema))) {//si el lexema es un racional
                                type = "Número racional";
                                state = 0;
                            } else {//si el lexema es otra cosa
                                type = "ERROR: Lexema desconocido";
                                state = 0;
                            }
                        }
                        break;
                    case 4://cadena de texto
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches(stringLimRegEx, String.valueOf(charActive)) && lexema.length() > 1) {//si no es el primer caracter del lexema y coincide con el caracter de inicio/fin de cadena
                            if (Pattern.matches(stringRegEx, String.valueOf(lexema))) {//si la cadena es cualquier cosa encerrada entre caracteres de inicio/fin de cadena
                                type = "Cadena de texto";
                                state = 0;
                            } else {//si es otra cosa
                                type = "ERROR: Lexema desconocido";
                                state = 0;
                            }
                        }
                        break;
                    case 5:
                        type = "Puntuación y caracteres especiales";
                        state = 0;
                        break;
                    case 100://operadores
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches(logicCompRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un operador lógico compuesto
                            state = 100;
                        } else if (Pattern.matches(mathCompRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un operador aritmético compuesto
                            state = 100;
                        } else if (Pattern.matches(relCompRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un operador relacional compuesto
                            state = 100;
                        } else if (Pattern.matches(logicCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador lógico compuesto
                            type = "Operador lógico";
                            state = 0;
                        } else if (Pattern.matches(mathCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético compuesto
                            type = "Operador artimético";
                            state = 0;
                        } else if (Pattern.matches(relCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional compuesto
                            type = "Operador relacional";
                            state = 0;
                        } else if (Pattern.matches(logicRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador lógico simple
                            type = "Operador lógico";
                            state = 0;
                        } else if (Pattern.matches(mathRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético simple
                            type = "Operador artimético";
                            state = 0;
                        } else if (Pattern.matches(relRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Operador relacional";
                            state = 0;
                        } else if (Pattern.matches(assignRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Operador de asignación";
                            state = 0;
                        } else if (Pattern.matches(endSentenceRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Operador de fin de sentencia";
                            state = 0;
                        } else {
                            type = "ERROR: Lexema desconocido";
                            state = 0;
                        }
                        break;
                }

                if (state == 0) {//si el estado final es 0 el lexema esta completo y se puede añadir a la lista
                    tokenCounter++;
                    tokenList.add(new Token(tokenCounter, lexema, type, i + 1, j + 1));
                    lexema = "";
                    type = "";
                } else if (state == 1) {//si el estado final es 1 el lexema fue un espacio y sera ignorado
                    state = 0;
                }
                //si el estado final es otro, el lexema aún está incompleto

                if (i == lines.size() - 1 && j == lines.get(i).length() - 1) {//si es el uúltimo caracter del texto
                    if (state != 0) {
                        type = "ERROR: Lexema desconocido";
                        tokenCounter++;
                        tokenList.add(new Token(tokenCounter, lexema, type, i + 1, j + 1));
                    }
                    state = -1;
                }
            }
        }
    }

    public int stateTransition(char ch) {
        String character = String.valueOf(ch);
        if (Pattern.matches(spaceRegEx, character)) {
            return 1;//espacio
        } else if (Pattern.matches(alphaRegEx, character)) {
            return 2;//identificador
        } else if (Pattern.matches(numRegEx, character)) {
            return 3;//número
        } else if (Pattern.matches(stringLimRegEx, character)) {
            return 4;//String
        } else if (Pattern.matches(specialRegEx, character)) {
            return 5;//Puntuación y caracteres especiales
        } else {
            return 100;//operadores
        }
    }

    public ArrayList<String> separate(String text, char separator) {
        String line = "";
        ArrayList<String> chain = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != separator) {
                line += String.valueOf(text.charAt(i));
            } else {
                chain.add(line);
                line = "";
            }
        }
        chain.add(line);
        return chain;
    }
}
