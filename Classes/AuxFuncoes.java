
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuxFuncoes {

    public static String CAPITULOS = "Capitulos/capitulos.db";

    private static Scanner sc = new Scanner(System.in);

    // Separa o texto CSV
    public static String[] separarPorVirgula(String texto) {
        String[] campos = texto.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].replaceAll("^\"|\"$", "");
        }

        return campos;
    }

    // Converte a data
    public static String formatarData(String data) throws ParseException {
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatoEntrada.parse(data);
        return formatoSaida.format(date);
    }

    // Pergunta a quantidade de IDs e os coleta
    public static int[] PerguntaQTD_ID() throws IOException {
        RandomAccessFile RAF = new RandomAccessFile(CAPITULOS, "rw");

        RAF.seek(0);
        int ultimoId = RAF.readInt();

        System.out.println("\nDigite a quantidade de capitulos que deseja pesquisar: ");
        int qtdIds = sc.nextInt();
        int[] ids = new int[qtdIds];

        for (int i = 0; i < qtdIds; i++) {
            do {
                System.out.println("Qual o ID do capitulo?");
                ids[i] = sc.nextInt();

                if (ids[i] > ultimoId) {
                    System.out.println("ID invalido. O ultimo ID registrado é " + ultimoId + ". Digite novamente.");
                }
            } while (ids[i] > ultimoId);
        }
        RAF.close();
        return ids;
    }

    // Reescreve o último ID inserido no arquivo
    public static void IncrementaUltimoIdInserido() throws IOException {
        RandomAccessFile RAF = new RandomAccessFile(CAPITULOS, "rw");

        RAF.seek(0);
        int ultimoID = RAF.readInt();

        RAF.seek(0);
        RAF.writeInt(ultimoID + 1);

        RAF.close();
    }

    // Escreve os dados do capítulo no arquivo de forma binária
    public static void escreverCapitulo(byte[] dataBytes, long lugar) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(CAPITULOS, "rw");
        raf.seek(lugar);

        raf.writeByte(1);
        raf.writeInt(dataBytes.length);
        raf.write(dataBytes);
        raf.close();
    }

    // Pergunta ao usuário qual ID ele deseja
    public static int qualID() {
        System.out.println("Qual o ID?");
        return sc.nextInt();
    }

    // Coleta dados para criar um novo capítulo
    static Capitulo CriarNovoCapitulo() throws IOException {
        try (RandomAccessFile RAF = new RandomAccessFile(CAPITULOS, "rw")) {

            RAF.seek(0);
            int UltimoId = RAF.readInt();
            int id = UltimoId + 1;

            System.out.print("(short) Capitulo: ");
            short numCapitulo = sc.nextShort();

            System.out.print("(short) Volume: ");
            short volume = sc.nextShort();
            sc.nextLine(); // Limpa o buffer após nextShort

            System.out.print("(String) Nome: ");
            String nome = sc.nextLine();

            System.out.print("(String) Titulo Original: ");
            String tituloOriginal = sc.nextLine();

            System.out.print("(String) Titulo Ingles: ");
            String tituloIngles = sc.nextLine();

            System.out.print("(short) Paginas: ");
            short paginas = sc.nextShort();
            sc.nextLine(); // Limpa o buffer

            System.out.print("(xx/xx/xxxx) Data: ");
            String data = sc.nextLine();

            System.out.print("(String) Episodio: ");
            String episodio = sc.nextLine();

            String[] titulos = {tituloOriginal, tituloIngles};

            return new Capitulo(id, numCapitulo, volume, nome, titulos, paginas, data, episodio);
        }
    }

    public static void CompararCompressao(String caminhoOriginal, int versao) throws IOException {
    File original = new File(caminhoOriginal);
    File huffmanFile = new File(String.format("Compressao/capitulosHuffmanCompressao%d.db", versao));
    File lzwFile = new File(String.format("Compressao/capitulosLZWCompressao%d.db", versao));


      // Verifica se os arquivos de compressão existem
    if (!huffmanFile.exists() || !lzwFile.exists()) {
        // Se algum arquivo não existir, não faz nada
        return;
    }
    
    long tamanhoOriginal = original.length();
    long tamanhoHuffman = huffmanFile.length();
    long tamanhoLZW = lzwFile.length();

    double ganhoHuffman = 100.0 * (tamanhoOriginal - tamanhoHuffman) / tamanhoOriginal;
    double ganhoLZW = 100.0 * (tamanhoOriginal - tamanhoLZW) / tamanhoOriginal;

    System.out.println("\n--- Comparação de Compressão ---");
    System.out.printf("Huffman: %.2f%% de redução (%d bytes)\n", ganhoHuffman, tamanhoHuffman);
    System.out.printf("LZW:     %.2f%% de redução (%d bytes)\n", ganhoLZW, tamanhoLZW);

    if (ganhoHuffman > ganhoLZW) {
        System.out.println("Huffman teve melhor compressão.");
    } else if (ganhoHuffman < ganhoLZW) {
        System.out.println("LZW teve melhor compressão.");
    } else {
        System.out.println("Ambos tiveram compressão equivalente.");
    }
}

}
