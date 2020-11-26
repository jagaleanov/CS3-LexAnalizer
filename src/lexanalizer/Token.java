package lexanalizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jgale
 */
public class Token {

    private int id;
    private String lexema;
    private int row;
    private int col;
    private String type;
    private Token next;

    public Token() {

    }

    public Token(int id, String lexema, String typeString, int row, int col) {
        this.id = id;
        this.lexema = lexema;
        this.row = row;
        this.col = col;
        this.type = typeString;
        this.next = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Token getNext() {
        return next;
    }

    public void setNext(Token next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "Token\n"
                + "   Id: " + id + "\n"
                + "   Lexema: " + lexema + "\n"
                + "   Tipo: " + type + "\n"
                + "   Fila:" + row + "\n"
                + "   Columna: " + col + "\n"; 
    }

}
