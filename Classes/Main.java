import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

public class Main {

    static String CAPITULOS = "Capitulos/capitulos.db";
    public static void main(String[] args) throws ParseException, IOException {
        Scanner sc = new Scanner(System.in);

        // Gerar os arquivos capitulos.db e capitulosIndice.db
        CriadorCapitulos.gerarCapitulos();

        //Construir a árvore B+ e Hash
        TreeBplus arvore = new TreeBplus();
        arvore.construirArvoreDoArquivo(CAPITULOS);

        HashEstendido hash = new HashEstendido();
        hash.construirDoArquivo(CAPITULOS);

        // Iniciar o menu
        CRUD.CRUD(arvore, hash);
    }
}
