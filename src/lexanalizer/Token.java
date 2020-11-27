package lexanalizer;

public class Token {

    private int id;
    private String lexema;
    private int row;
    private int col;
    private int type;

    public Token() {

    }

    public Token(int id, String lexema, int type, int row, int col) {
        this.id = id;
        this.lexema = lexema;
        this.row = row;
        this.col = col;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public String getTypeString() {
        switch (type) {
            case -2:
                return "Comentarios de párrafo";
            case -1:
                return "Comentarios de línea";
            case 1:
                return "Palabra reservada";
            case 2:
                return "Identificador";
            case 3:
                return "Cadena de texto";
            case 4:
                return "Número entero";
            case 5:
                return "Número racional";
            case 6:
                return "Puntuación y caracteres especiales";
            case 7:
                return "Operador lógico";
            case 8:
                return "Operador aritmético";
            case 9:
                return "Operador relacional";
            case 10:
                return "Operador de asignación";
            case 11:
                return "Operador de fin de sentencia";
            default:
                return "Lexema desconocido (error)";

        }
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token\n"
                + "   Id: " + id + "\n"
                + "   Lexema: " + lexema + "\n"
                + "   Tipo: (" + type + ") " + getTypeString() + "\n"
                + "   Fila: " + row + "\n"
                + "   Columna: " + col + "\n";
    }
}
