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
            case -3:
                return "Comentario de párrafo";
            case -2:
                return "Comentario de línea";
            case -1:
                return "Espacio";
            case 1:
                return "Palabra reservada";
            case 2:
                return "Identificador";
            case 3:
                return "Número";
            case 4:
                return "String";
            case 5:
                return "Char";
            case 6:
                return "Operador aritmético";
            case 7:
                return "Operador lógico";
            case 8:
                return "Operador relacional";
            case 9:
                return "Operador de asignación";
            case 10:
                return "Cierre de sentencia";
            case 11:
                return "Signos de puntuación y caracteres especiales";
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
