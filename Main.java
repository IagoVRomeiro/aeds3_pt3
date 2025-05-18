import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

public class Main {

    static String CAPITULOS = "dataset/capitulos.db";
    public static void main(String[] args) throws ParseException, IOException {
        Scanner sc = new Scanner(System.in);

        // Gerar os arquivos capitulos.db e capitulosIndice.db
        CriadorCapitulos.gerarCapitulos();

        // Construir a árvore B+ e Hash
        System.out.print("Digite a ordem da Árvore B+: ");
        int ordem = sc.nextInt();

        TreeBplus arvore = new TreeBplus(ordem);
        arvore.construirArvoreDoArquivo(CAPITULOS);

        HashEstendido hash = new HashEstendido();
        hash.construirDoArquivo(CAPITULOS);

        // Iniciar o menu
        CRUD.CRUD(arvore, hash);
    }
}
