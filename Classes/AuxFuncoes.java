
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuxFuncoes {

    public static String CAPITULOS = "Capitulos/capitulos.db";

    public static Scanner sc = new Scanner(System.in);

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
        int[] ids;
        try (RandomAccessFile RAF = new RandomAccessFile(CAPITULOS, "rw")) {
            RAF.seek(0);
            int ultimoId = RAF.readInt();
            System.out.println("\nDigite a quantidade de capitulos que deseja pesquisar: ");
            int qtdIds = sc.nextInt();
            ids = new int[qtdIds];
            for (int i = 0; i < qtdIds; i++) {
                do {
                    System.out.println("Qual o ID do capitulo?");
                    ids[i] = sc.nextInt();

                    if (ids[i] > ultimoId) {
                        System.out.println("ID invalido. O ultimo ID registrado é " + ultimoId + ". Digite novamente.");
                    }
                } while (ids[i] > ultimoId);
            }
        }
        return ids;
    }

    // Incrementa o último ID inserido no início do arquivo
    public static void IncrementaUltimoIdInserido() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(CAPITULOS, "rw")) {
            int ultimoID = raf.readInt();
            raf.seek(0);
            raf.writeInt(ultimoID + 1);
        }
    }

    // Escreve os dados do capítulo no arquivo de forma binária
    public static void escreverCapitulo(byte[] dataBytes, long lugar) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(CAPITULOS, "rw")) {
            raf.seek(lugar);

            raf.writeByte(1);
            raf.writeInt(dataBytes.length);
            raf.write(dataBytes);
        }
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

            short numCapitulo = lerShortValido("(short) Capitulo: ");
            short volume = lerShortValido("(short) Volume: ");

            sc.nextLine(); // Limpa o buffer após ler os shorts

            System.out.print("(String) Nome: ");
            String nome = sc.nextLine();

            System.out.print("(String) Titulo Original: ");
            String tituloOriginal = sc.nextLine();

            System.out.print("(String) Titulo Ingles: ");
            String tituloIngles = sc.nextLine();

            short paginas = lerShortValido("(short) Paginas: ");
            sc.nextLine(); // Limpa o buffer

            System.out.print("(xx/xx/xxxx) Data: ");
            String data = sc.nextLine();

            System.out.print("(String) Episodio: ");
            String episodio = sc.nextLine();

            String[] titulos = {tituloOriginal, tituloIngles};

            return new Capitulo(id, numCapitulo, volume, nome, titulos, paginas, data, episodio);
        }
    }

    // Garante short válido.
    private static short lerShortValido(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return sc.nextShort();
            } catch (InputMismatchException e) {
                System.out.println("Valor inválido. Por favor, insira um número válido do tipo short.");
                sc.nextLine(); // Limpa o buffer para evitar loop infinito
            }
        }
    }

    // Compara os resultados da compressão entre Huffman e LZW.
    public static void CompararCompressao(String caminhoOriginal, int versao, double ganhoHuffman, double ganhoLZW, long tempoHuffman, long tempoLZW) throws IOException {

        File huffmanFile = new File(String.format("Compressao/capitulosHuffmanCompressao%d.db", versao));
        File lzwFile = new File(String.format("Compressao/capitulosLZWCompressao%d.db", versao));

        // Verifica se os arquivos de compressão existem
        if (!huffmanFile.exists() || !lzwFile.exists()) {
            return;
        }

        long tamanhoHuffman = huffmanFile.length();
        long tamanhoLZW = lzwFile.length();

        System.out.println("\n--- Comparação de Compressão ---");
        System.out.printf("Huffman: %.2f%% de redução (%d bytes)\n", ganhoHuffman, tamanhoHuffman);
        System.out.printf("LZW:     %.2f%% de redução (%d bytes)\n", ganhoLZW, tamanhoLZW);

        if (ganhoHuffman > ganhoLZW) {
            System.out.print("Huffman teve melhor compressão");
        } else if (ganhoHuffman < ganhoLZW) {
            System.out.print("LZW teve melhor compressão");
        } else {
            System.out.println("Ambos tiveram compressão equivalente.");
            return;
        }

        if (tempoHuffman > tempoLZW) {
            System.out.println(" e LZW teve melhor tempo de Descompressão.");
        } else if (tempoHuffman < tempoLZW) {
            System.out.println(" e Huffman teve melhor tempo de Descompressão.");
        } else {
            System.out.println(" e ambos tiveram tempo de descompressão equivalente.");
        }
    }

    // Compara tempos de descompressão entre Huffman e LZW.
    public static void CompararDescompressao(long tempoHuffman, long tempoLZW) throws IOException {

        System.out.println();
        if (tempoHuffman > tempoLZW) {
            System.out.println("LZW teve melhor tempo de Descompressão.");
        } else if (tempoHuffman < tempoLZW) {
            System.out.println("Huffman teve melhor tempo de Descompressão.");
        } else {
            System.out.println("Ambos tiveram compressão equivalente.");
        }
    }

    // Cria as pastas "Compressao" e "Indices".
    public static void CriarPastas() {

        // Criar Pasta Compressao
        String caminho = "Compressao";
        File pasta = new File(caminho);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        // Criar Pasta Indices
        caminho = "Indices";
        pasta = new File(caminho);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
    }
}
