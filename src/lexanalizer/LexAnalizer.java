package lexanalizer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LexAnalizer {

    private ArrayList<Token> tokenList = new ArrayList();
    private int tokenCounter = 0;

    //EXPRESIONES REGULARES
    //GENERALES
    private final String spaceRegEx = "[\\s]+";//espacios: salto de línea, espacio y tabular
    private final String numRegEx = "[0-9]";//numérico
    private final String numRealRegEx = "[0-9.]";//numérico Real
    private final String alphaRegEx = "[a-zA-Z]";//alfabético
    private final String alphanumRegEx = "[a-zA-Z0-9_]";//alfanumérico

    //COMENTARIOS
    private final String commentLineRegEx = "[/][/]";//COMENTARIOS DE LÍNEA
    private final String commentParagraphRegEx = "[/][*][^[#]{2}]*[*][/]";//COMENTARIOS DE PARRAFO

    //OPERADORES
    private final String endSentenceRegEx = "[;]";//operador de cierre de sentencia
    private final String assignRegEx = "[=]";//operador de asignación
    private final String mathRegEx = "([+-][=])|([+][+])|([-][-])|[+-/%^()*]";//operadores artiméticos
    private final String logicRegEx = "([&][&])|([|][|])|[&|!]";//operadores lógicos 
    private final String relRegEx = "[<>=!][=]|[<>]";//operadores relacionales 
    private final String chainRegEx = "[/][*]|[*][/]|[\"']";//apertura y cierre de cadenas
    private final String specialRegEx = "[.,{}\\[\\]]";//signos de puntuación y caracteres especiales

    //TIPOS
    private final String realRegEx = "([0-9]+([.][0-9]+)?)";//racional 
    private final String stringRegEx = "[\"][^\"]*[\"]";//cadena de texto 
    private final String charRegEx = "[\'][^\']{1}[\']";//caracter 

    //PALABRAS RESERVADAS
    private final String[] keyWords = {
        "abstract",
        "boolean",
        "break",
        "case",
        "char",
        "class",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "extends",
        "false",
        "final",
        "float",
        "for",
        "if",
        "implements",
        "int",
        "interface",
        "long",
        "new",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "String",
        "super",
        "switch",
        "this",
        "throw",
        "true",
        "try",
        "void",
        "while"
    };

    public LexAnalizer(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void analize(String string) {
        int state = 0;
        /*
        ESTADOS
        0:   Vacio(inicial)
        -100:  Vacio(final)
        otro:  procesando lexema 
        
        TOKENS
        -3:  Comentario de párrafo                          /*xxx...
        -2:  Comentario de línea                            //xxx xxx xxx##
        -1:  Espacio                            
        0:   Lexema desconocido (error)
        1:   Palabra reservada 
        2:   Identificador                                  empieza con alfabético y puede contener alfanumérico y _
        3:   Número                                         123.123 ó 123
        4:   String                                         "xxx xxx xxx"
        5:   Char                                           'x'
        6:   Operador aritmético                            + - * / % ^ ( )
        7:   Operador lógico                                & && | || !
        8:   Operador relacional                            < > <= >= == !=
        9:   Operador de asignación                         =
        10:  Operador de fin de sentencia                   ;
        11:  Signos de puntuación y caracteres especiales   . , { } [ ]
         */
        int type = -1000;
        String lexema = "";

        ArrayList<String> lines = separateLines(string);//separa el texto por líneas en un arrayList

        for (int i = 0; i < lines.size(); i++) {//por cada línea en el texto
            for (int j = 0; j < lines.get(i).length(); j++) {//por cada caracter en la línea
                char charBefore;
                char charActive;
                char charNext;

                charActive = lines.get(i).charAt(j);

                if (j == 0) {//si es el primer caracter de la línea
                    charBefore = ' ';//asignar el anterior caracter como espacio para las comparaciones
                } else {//si NO es el último caracter de la línea
                    charBefore = lines.get(i).charAt(j - 1);//asignar el anterior caracter de la línea para las comparaciones
                }

                if (j == lines.get(i).length() - 1) {//si es el último caracter de la línea
                    charNext = ' ';//asignar el siguiente caracter como espacio para las comparaciones
                } else {//si NO es el último caracter de la línea
                    charNext = lines.get(i).charAt(j + 1);//asignar el siguiente caracter de la línea para las comparaciones
                }

                if (state == 0) {//si el estado es vacio
                    state = getInitialState(charActive);//revisar caracter activo y asignar estado
                }

                switch (state) {
                    case -2://comntario linea
                        lexema += lines.get(i).charAt(j);
                        if (j == lines.get(i).length() - 1) {//si es  el último caracter de la linea
                            type = -2;
                            state = 0;
                        }
                        break;
                    case -1://espacio
                        break;
                    case 1://identificador (inicia con alfabético)
                        lexema += lines.get(i).charAt(j);
                        if (!Pattern.matches(alphanumRegEx, String.valueOf(charNext))) {//si el siguiente caracter no es alfanumérico
                            boolean assigned = false;
                            for (int k = 0; k < keyWords.length; k++) {//por cada palbra reservada
                                if (String.valueOf(keyWords[k]).equals(String.valueOf(lexema))) {//checkear si el lexema es palbra reservada
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

                        if (!Pattern.matches(numRealRegEx, String.valueOf(charNext))) {//si el siguiente caracter no es número o separador decimal
                            if (Pattern.matches(realRegEx, String.valueOf(lexema))) {//si el lexema es un entero
                                type = 3;
                                state = 0;
                            } else {//si el lexema es otra cosa
                                type = 0;
                                state = 0;
                            }
                        }
                        break;
                    case 4://chains
                        lexema += lines.get(i).charAt(j);
                        if ((lexema.length() > 3 && Pattern.matches(chainRegEx, String.valueOf(charBefore) + String.valueOf(charActive))) || (lexema.length() > 1 && Pattern.matches(chainRegEx, String.valueOf(charActive)))) {//si no es el primer caracter del lexema y coincide con el caracter de inicio/fin de cadena
                            if (Pattern.matches(stringRegEx, String.valueOf(lexema))) {//si la cadena es cualquier cosa encerrada entre caracteres de inicio/fin de String
                                type = 4;
                                state = 0;
                            } else if (Pattern.matches(charRegEx, String.valueOf(lexema))) {//si la cadena es cualquier cosa encerrada entre caracteres de inicio/fin de char
                                type = 5;
                                state = 0;
                            } else if (Pattern.matches(commentParagraphRegEx, String.valueOf(lexema))) {//si la cadena es cualquier cosa encerrada entre caracteres de inicio/fin de comantario de parrafo
                                type = -3;
                                state = 0;
                            } else {//si es otra cosa
                                type = 0;
                                state = 0;
                            }
                        }
                        break;
                    case 6://simbolos
                        lexema += lines.get(i).charAt(j);

                        if (Pattern.matches(mathRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un operador aritmético compuesto
                            //state = 6;
                        } else if (Pattern.matches(logicRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un operador lógico compuesto
                            //state = 6;
                        } else if (Pattern.matches(relRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un operador relacional compuesto
                            //state = 6
                        } else if (Pattern.matches(commentLineRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un inicio de comentario de línea
                            state = -2;
                        } else if (Pattern.matches(chainRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter forman un inicio de cadena
                            state = 4;
                        } else if (Pattern.matches(mathRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador aritmético 
                            type = 6;
                            state = 0;
                        } else if (Pattern.matches(logicRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador lógivo 
                            type = 7;
                            state = 0;
                        } else if (Pattern.matches(relRegEx, String.valueOf(lexema))) {//si el lexema actual forma un operador relacional 
                            type = 8;
                            state = 0;
                        } else if (Pattern.matches(assignRegEx, String.valueOf(lexema))) {//si el lexema actual es el operador de asignación
                            type = 9;
                            state = 0;
                        } else if (Pattern.matches(endSentenceRegEx, String.valueOf(lexema))) {//si el lexema actual es el operador de fin de sentencia
                            type = 10;
                            state = 0;
                        } else if (Pattern.matches(specialRegEx, String.valueOf(lexema))) {//si el lexema actual es un signo de puntuación o un caracteres especial
                            type = 11;
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
                } else if (state == -1) {//si el estado final es 1 el lexema fue un espacio y será ignorado
                    state = 0;
                }
                //si el estado final es otro, el lexema aún está incompleto

                if (i == lines.size() - 1 && j == lines.get(i).length() - 1) {//si es el último caracter del texto
                    if (state != 0) {//si el lexma aun no esta completo
                        type = 0;//el lexema no coincide en su totalidad, puede estar incompleto
                        tokenCounter++;
                        tokenList.add(new Token(tokenCounter, lexema, type, i + 1, j + 1));
                        lexema = "";
                        type = -1000;
                    }
                    state = -100;
                }
            }
        }
    }

    public ArrayList<String> separateLines(String text) {
        String line = "";
        ArrayList<String> chain = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {//por cada caracter en el texto
            if (text.charAt(i) != '\n') {//si NO coincide con el separador
                line += String.valueOf(text.charAt(i));//concatenar en la línea
            } else {//de lo contrario
                chain.add(line);//iniciar una nueva línea
                line = "";
            }
        }
        chain.add(line);
        return chain;
    }

    public int getInitialState(char ch) {
        String character = String.valueOf(ch);
        if (Pattern.matches(spaceRegEx, character)) {
            return -1;//espacio
        } else if (Pattern.matches(alphaRegEx, character)) {
            return 1;//alfabetico
        } else if (Pattern.matches(numRegEx, character)) {
            return 3;//número
        } else if (Pattern.matches(chainRegEx, character)) {
            return 4;//cadenas (String, char, comentario de parrafo)
        } else {
            return 6;//símbolo
        }
    }
}
