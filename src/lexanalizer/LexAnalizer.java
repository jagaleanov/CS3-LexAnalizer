package lexanalizer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LexAnalizer {

    private ArrayList<Token> tokenList = new ArrayList();
    private int tokenCounter = 0;

    //GENERALES
    private final String spaceRegEx = "[\\s]+";//espacios: salto de línea, espacio y tabular
    private final String numRegEx = "[0-9.]";//numérico
    private final String alphaRegEx = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]";//alfabético
    private final String alphanumRegEx = "[a-zA-ZááéíóúüñÁÉÍÓÚÜÑ0-9_]";//alfanumérico

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
        "VERDADERO", //true
        "FALSO", //false
        "MIENTRAS", //while
        "PARA", //for
        "SI", //if
        "O_SI", //else if
        "SI_NO_ENTONCES", //else
        "PARE" //break
    };

    public LexAnalizer(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void analize(String string) {
        int state = 0;
        /*
        ESTADOS
        0:   Vacio(inicial)
        -1:  Vacio(final)
        -2:  Comentario
        1:   Espacio
        2:   Identificador
        3:   Número
        4:   Cadena de texto
        5:   Puntuación y caracteres especiales
        100: Operadores: lógicos, aritméticos, relacionales, asignación, fin de sentencia 
        
        TOKENS
        -2:  Comentarios de párrafo
        -1:  Comentarios de línea
        0:   Lexema desconocido (error)
        1:   Palabra reservada
        2:   Identificador
        3:   Cadena de texto
        4:   Número entero
        5:   Número racional
        6:   Puntuación y caracteres especiales
        7:   Operador lógico
        8:   Operador aritmético
        9:   Operador relacional
        10:  Operador de asignación
        11:  Operador de fin de sentencia
         */
        int type = -1000;
        String lexema = "";

        ArrayList<String> lines = separateLines(string, '\n');//separa el texto por líneas en un arrayList

        for (int i = 0; i < lines.size(); i++) {//por cada línea en el texto
            for (int j = 0; j < lines.get(i).length(); j++) {//por cada caracter en la línea
                char charActive;
                char charNext;

                charActive = lines.get(i).charAt(j);

                if (j == lines.get(i).length() - 1) {//si es el último caracter de la línea
                    charNext = ' ';//asignar el siguiente caracter como espacio para las comparaciones
                } else {//si NO es el último caracter de la línea
                    charNext = lines.get(i).charAt(j + 1);//asignar el siguiente caracter de la línea para las comparaciones
                }

                if (state == 0) {//si el estado es vacio
                    state = getInitialState(charActive);//revisar caracter activo y asignar estado
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
                            for (int k = 0; k < reservedWords.length; k++) {//por cada palbra reservada
                                if (String.valueOf(reservedWords[k]).equals(String.valueOf(lexema))) {//checkear si el lexema es palbra reservada
                                    type = 1;
                                    state = 0;
                                    assigned = true;
                                    break;
                                }
                            }

                            if (!assigned) {//si no encontro coincidencias en palabra reservada
                                type = 2;
                                state = 0;
                            }
                        }
                        break;
                    case 3://número
                        lexema += lines.get(i).charAt(j);

                        if (!Pattern.matches(numRegEx, String.valueOf(charNext))) {//si el siguiente caracter no es número o separador decimal
                            if (Pattern.matches(integerRegEx, String.valueOf(lexema))) {//si el lexema es un entero
                                type = 4;
                                state = 0;
                            } else if (Pattern.matches(rationalRegEx, String.valueOf(lexema))) {//si el lexema es un racional
                                type = 5;
                                state = 0;
                            } else {//si el lexema es otra cosa
                                type = 0;
                                state = 0;
                            }
                        }
                        break;
                    case 4://cadena de texto
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches(stringLimRegEx, String.valueOf(charActive)) && lexema.length() > 1) {//si no es el primer caracter del lexema y coincide con el caracter de inicio/fin de cadena
                            if (Pattern.matches(stringRegEx, String.valueOf(lexema))) {//si la cadena es cualquier cosa encerrada entre caracteres de inicio/fin de cadena
                                type = 3;
                                state = 0;
                            } else {//si es otra cosa
                                type = 0;
                                state = 0;
                            }
                        }
                        break;
                    case 5:
                        type = 6;
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
                            type = 7;
                            state = 0;
                        } else if (Pattern.matches(mathCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético compuesto
                            type = 8;
                            state = 0;
                        } else if (Pattern.matches(relCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional compuesto
                            type = 9;
                            state = 0;
                        } else if (Pattern.matches(logicRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador lógico simple
                            type = 7;
                            state = 0;
                        } else if (Pattern.matches(mathRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético simple
                            type = 8;
                            state = 0;
                        } else if (Pattern.matches(relRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = 9;
                            state = 0;
                        } else if (Pattern.matches(assignRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = 10;
                            state = 0;
                        } else if (Pattern.matches(endSentenceRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = 1;
                            state = 0;
                        } else {
                            type = 0;
                            state = 0;
                        }
                        break;
                }

                if (state == 0) {//si el estado final es 0 el lexema esta completo y se puede añadir a la lista
                    tokenCounter++;
                    tokenList.add(new Token(tokenCounter, lexema, type, i + 1, j + 1));
                    lexema = "";
                    type = -1000;
                } else if (state == 1) {//si el estado final es 1 el lexema fue un espacio y será ignorado
                    state = 0;
                }
                //si el estado final es otro, el lexema aún está incompleto

                if (i == lines.size() - 1 && j == lines.get(i).length() - 1) {//si es el uúltimo caracter del texto
                    if (state != 0) {//si no esta vacio
                        type = 0;//el lexema no coincide en su totalidad, puede estar incompleto
                        tokenCounter++;
                        tokenList.add(new Token(tokenCounter, lexema, type, i + 1, j + 1));
                        lexema = "";
                        type = -1000;
                    }
                    state = -1;
                }
            }
        }
    }

    public int getInitialState(char ch) {
        String character = String.valueOf(ch);
        if (Pattern.matches(spaceRegEx, character)) {
            return 1;//espacios
        } else if (Pattern.matches(alphaRegEx, character)) {
            return 2;//identificador
        } else if (Pattern.matches(numRegEx, character)) {
            return 3;//número
        } else if (Pattern.matches(stringLimRegEx, character)) {
            return 4;//string
        } else if (Pattern.matches(specialRegEx, character)) {
            return 5;//puntuación y caracteres especiales
        } else {
            return 100;//operadores
        }
    }

    public ArrayList<String> separateLines(String text, char separator) {
        String line = "";
        ArrayList<String> chain = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {//por cada caracter en el texto
            if (text.charAt(i) != separator) {//si NO coincide con el separador
                line += String.valueOf(text.charAt(i));//concatenar en la línea
            } else {//de lo contrario
                chain.add(line);//iniciar una nueva línea
                line = "";
            }
        }
        chain.add(line);
        return chain;
    }
}
