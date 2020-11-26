package lexanalizer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LexAnalizer {

    private ArrayList<Token> tokenList = new ArrayList();
    private int tokenCounter = 0;

    //GENERALES
    private String spaceRegEx = "[\\s]+";//espacios: salto de línea, espacio y tabular
    private String numRegEx = "[0-9.]";//numérico
    private String alphaRegEx = "[a-zA-Z]";//alfabético
    private String alphanumRegEx = "[a-zA-Z0-9_]";//alfanumérico

    //OPERADORES
    private String stringLim = "\"";//limitador de inicio/fin de cadenas de texto
    private String stringLimRegEx = "[" + stringLim + "]";//limitador de inicio/fin de cadenas de texto
    private String endRegEx = "[;]";//operador de cierre de sentencia
    //Operadores de un caracter (simple)
    private String assignRegEx = "[=]";//operador de asignación
    private String argRegEx = "[,]";//separador de argumentos 
    private String mathSimpleRegEx = "[+-/%^()*]";//operadores artiméticos simples
    private String logicSimpleRegEx = "[&|!]";//operadores lógicos simples
    private String relSimpleRegEx = "[<>]";//operadores relacionales simples
    //Operadores de dos caracteres (comp)
    private String mathCompRegEx = "([+-][=])|([+][+])|([-][-])";//operadores artiméticos compuestos +=,-=,++,--
    private String logicCompRegEx = "([&][&])|([|][|])";//operadores lógicos compuestos &&,||
    private String relCompRegEx = "([<>=!][=])";//operadores relacionales compuestos <=,>=,==,!=,

    //TIPOS
    private String integerRegEx = "[0-9]+";//entero 
    private String rationalRegEx = "[0-9]+([.][0-9]+)?|[.][0-9]+";//racional 
    private String stringRegEx = stringLimRegEx + "[^" + stringLim + "]*" + stringLimRegEx;//string 

    public LexAnalizer(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void analize(String string) {
        int state = 0;
        /*
        -1:  Comentario
        0:   Vacio(inicial)
        1:   Espacio
        2:   Identificador
        3:   Número
        5:   Cadena de texto
        100: Operadores: lógicos, aritméticos, relacionales, asignación, fin de sentencia 
         */
        String type = "";
        String lexema = "";

        ArrayList<String> lines = separate(string, '\n');//separa el texto por líneas en un arrayList

        for (int i = 0; i < lines.size(); i++) {//por cada línea en el texto
            for (int j = 0; j < lines.get(i).length(); j++) {//por cada caracter en la línea

                char charActive;
                char charNext;

                charActive = lines.get(i).charAt(j);

                if (i == lines.size() - 1 && j == lines.get(i).length() - 1) {//si es el último caracter
                    charNext = ' ';//asignar el siguiente caracter como espacio para las comparaciones
                } else {//si NO es el último caracter
                    charNext = lines.get(i).charAt(j + 1);//asignar el siguiente caracter de la línea para las comparaciones
                }

                if (state == 0) {//si el estado es vacio
                    state = stateTransition(charActive);//revisar caracter activo y asignar estado
                }

                switch (state) {
                    case -1://comentario
                        break;
                    case 1://espacio
                        break;
                    case 2://identificador (inicia con alfabetico)
                        lexema += lines.get(i).charAt(j);
                        if (!Pattern.matches(alphanumRegEx, String.valueOf(charNext))) {//si el siguiente caracter no es alfanumérico
                            type = "identificador";
                            state = 0;
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
                        } else {//si es cualquier otra cosa diferente al caracter de cierre de cadena

                        }
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
                            type = "Operador lógico compuesto";
                            state = 0;
                        } else if (Pattern.matches(mathCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético compuesto
                            type = "Operador artimético compuesto";
                            state = 0;
                        } else if (Pattern.matches(relCompRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional compuesto
                            type = "Operador relacional compuesto";
                            state = 0;
                        } else if (Pattern.matches(logicSimpleRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador lógico simple
                            type = "Operador lógico";
                            state = 0;
                        } else if (Pattern.matches(mathSimpleRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético simple
                            type = "Operador artimético";
                            state = 0;
                        } else if (Pattern.matches(relSimpleRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Operador relacional";
                            state = 0;
                        } else if (Pattern.matches(assignRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Operador de asignación";
                            state = 0;
                        } else if (Pattern.matches(endRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Operador de fin de sentencia";
                            state = 0;
                        } else if (Pattern.matches(argRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional simple
                            type = "Separador de argumentos";
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
        } else if (stringLim.equals(character)) {
            return 4;//String
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
