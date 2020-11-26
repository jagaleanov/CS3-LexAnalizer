package lexanalizer;

import java.util.ArrayList;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jgale
 */
public class LexAnalizer {

    private ArrayList<Token> tokenList = new ArrayList();
    private int tokenCounter = 0;

    //GENERALES
    private String spaceRegEx = "[\\s]+";//espacios: salto de línea, espacio y tabular
    private String numRegEx = "[0-9.]";//numérico
    private String alphaRegEx = "[a-zA-Z]";//alfabético
    private String alphanumRegEx = "[a-zA-Z0-9_]";//alfanumérico

    //OPERADORES
    private String endOp = ";";//operador de cierre de sentencia
    private String stringLim = "\"";//limitador de cadenas de texto
    //Operadores de un caracter (simple)
    private String mathSimpleOpRegEx = "[=+-/%^()<>*]";//operadores matemáticos simples
    private String logicSimpleOpRegEx = "[&|!]";//operadores lógicos simples
    //Operadores de dos caracteres (comp)
    private String mathCompOpRegEx = "([<>=+-][=])|([+][+])|([-][-])";//operadores matemáticos compuestos <=,>=,==,!=,+=,-=,++,--
    private String logicCompOpRegEx = "([&][&])|([|][|])|([!][=])";//operadores lógicos compuestos &&,||,!=

    //TIPOS
    private String integerRegEx = "[0-9]+";//entero 
    private String rationalRegEx = "[0-9]+([.][0-9]+)?|[.][0-9]+";//racional 
    private String stringRegEx = "[" + stringLim + "][^" + stringLim + "]*[" + stringLim + "]";//string 

    public LexAnalizer(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void analize(String string) {
        int state = 0;//0:vacio(inicial),-1:espacio,1:cadena de texto,2:número,999:error
        String type = "";
        String lexema = "";

        ArrayList<String> lines = separate(string, '\n');//separa el texto por líneas en un arrayList

        for (int i = 0; i < lines.size(); i++) {//por cada línea en el texto
            for (int j = 0; j < lines.get(i).length(); j++) {//por cada caracter en la línea

                char charActive;
                char charNext;

                charActive = lines.get(i).charAt(j);

                if (j == lines.get(i).length() - 1) {//si es el último caracter
                    charNext = ' ';//asignar el siguiente caracter como espacio para las compraciones
                } else {//si NO es el último caracter
                    charNext = lines.get(i).charAt(j + 1);//asignar el siguiente caracter de la línea para las compraciones
                }

                if (state == 0) {//si el estado es vacio
                    state = stateTransition(charActive);//revisar caracter activo y asignar estado
                }

                switch (state) {
                    case 1://espacio
                        break;
                    case 2://identificador (inicia con alfabetico)
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches(alphanumRegEx, String.valueOf(charNext))) {//si el siguiente caracter es alfanumérico
                            state = 2;
                        } else {
                            type = "identificador";
                            state = 0;
                        }
                        break;
                    case 3://número
                        lexema += lines.get(i).charAt(j);

                        if (Pattern.matches(numRegEx, String.valueOf(charNext))) {//si el siguiente caracter es número o separador decimal
                            state = 3;
                        } else {
                            if (Pattern.matches(integerRegEx, String.valueOf(lexema))) {
                                type = "Número entero";
                                state = 0;
                            } else if (Pattern.matches(rationalRegEx, String.valueOf(lexema))) {
                                type = "Número racional";
                                state = 0;
                            } else {
                                type = "Error léxico";
                                state = 0;
                            }
                        }
                        break;
                    case 4://finalizacion de sentencia
                        lexema += lines.get(i).charAt(j);
                        type = "Finalización de sentencia";
                        state = 0;
                        break;
                    case 5://math
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches(mathCompOpRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter son un aritmético lógico compuesto
                            state = 5;
                        } else {
                            if (Pattern.matches(mathCompOpRegEx, String.valueOf(lexema))) {
                                type = "Operador aritmético compuesto";
                                state = 0;
                            } else if (Pattern.matches(mathSimpleOpRegEx, String.valueOf(lexema))) {
                                type = "Operador aritmético simple";
                                state = 0;
                            } else {
                                type = "Error léxico";
                                state = 0;
                            }
                        }
                        break;
                    case 6://logic
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches(logicCompOpRegEx, String.valueOf(charActive) + String.valueOf(charNext))) {//si junto al siguiente caracter son un operador lógico compuesto
                            state = 6;
                        } else {
                            if (Pattern.matches(logicCompOpRegEx, String.valueOf(lexema))) {
                                type = "Operador lógico compuesto";
                                state = 0;
                            } else if (Pattern.matches(logicSimpleOpRegEx, String.valueOf(lexema))) {
                                type = "Operador lógico simple";
                                state = 0;
                            } else {
                                type = "Error léxico";
                                state = 0;
                            }
                        }
                        break;
                    case 7://string
                        lexema += lines.get(i).charAt(j);
                        if (Pattern.matches("[" + stringLim + "]", String.valueOf(charActive)) && lexema.length()>1) {//si junto al siguiente caracter son un operador lógico compuesto
                            if (Pattern.matches(stringRegEx, String.valueOf(lexema))) {
                                type = "String";
                                state = 0;
                            } else {
                                type = "Error léxico";
                                state = 0;
                            }
                        } else {
                            state = 7;
                        }
                        break;
                    case 999://error
                        lexema += String.valueOf(lines.get(i).charAt(j));
                        type = "Error léxico";
                        state = 0;
                        break;
                }
                if (state == 0) {//si el estado final es 0 el lexema esta completo y se puede añadir a la lista
                    tokenCounter++;
                    tokenList.add(new Token(tokenCounter, lexema, type, i + 1, j + 1));
                    lexema = "";
                    type = "";
                } else if (state == 1) {//si el estado final es 0 el lexema esta completo y se puede añadir a la lista
                    state = 0;
                }
                //si el estado final es otro, el lexema aún está incompleto
            }
        }
    }

    public int stateTransition(char ch) {
        String character = String.valueOf(ch);
        if (Pattern.matches(spaceRegEx, character)) {//espacios
            return 1;//espacio
        } else if (Pattern.matches(alphaRegEx, character)) {
            return 2;//identificador
        } else if (Pattern.matches(numRegEx, character)) {
            return 3;//número
        } else if (endOp.equals(character)) {
            return 4;//Operador de finalizacion de sentencia
        } else if (Pattern.matches(mathSimpleOpRegEx, character)) {
            return 5;//Operador aritmetico
        } else if (Pattern.matches(logicSimpleOpRegEx, character)) {
            return 6;//Operador lógico
        } else if (stringLim.equals(character)) {
            return 7;//String
        } else {
            return 999;//error
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
