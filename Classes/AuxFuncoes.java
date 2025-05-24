import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuxFuncoes {

    public static String IndiceHash = "Indices/capitulosIndiceHash.db";
    public static String IndiceArvore = "Indices/capitulosIndiceArvore.db";
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
    static Capitulo InstanciaCapitulo() throws IOException {
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

    // Cria um novo capítulo, grava no arquivo, atualiza os índices B+ e Hash.
    static boolean criarCapitulo(Capitulo capitulo, TreeBplus arvore, HashEstendido hash) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(CAPITULOS, "rw")) {
            byte[] bytes = capitulo.toByteArray();
            long endereco = raf.length(); // Endereço atual onde será escrito

            AuxFuncoes.escreverCapitulo(bytes, endereco);
            AuxFuncoes.IncrementaUltimoIdInserido();

            // Atualiza a árvore B+ com o novo ID e endereço
            arvore.inserir(capitulo.getId(), endereco);
            // Atualiza o arquivo de índice
            arvore.salvarFolhasNoArquivo(IndiceArvore);

            hash.inserir(capitulo.getId(), endereco);

            hash.construirDoArquivo(CAPITULOS);

        }
        return true;
    }

    // Lê e exibe um capítulo com base no ID, utilizando os índices B+ e Hash.
    static boolean lerCapitulo(int ID, TreeBplus arvore, HashEstendido hash) throws IOException {
        // Buscar nos dois índices
        Long enderecoArvore = arvore.buscar(ID);
        Long enderecoHash = hash.buscar(ID);

        // Evita imprimir null diretamente
        System.out.println("[Arvore B+] Endereco encontrado: "
                + (enderecoArvore != null ? enderecoArvore : "nao encontrado"));
        System.out.println("[Hash Estendido] Endereco encontrado: "
                + (enderecoHash != null ? enderecoHash : "nao encontrado"));

        // Prioridade: Hash, se não existir, usa da Árvore
        Long endereco = (enderecoHash != null) ? enderecoHash : enderecoArvore;

        if (endereco == null) {
            System.out.println("ID não encontrado em nenhum índice.");
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(CAPITULOS, "rw")) {
            raf.seek(endereco);
            byte valido = raf.readByte();
            int tamanhoVetor = raf.readInt();

            if (valido == 1) {
                byte[] byteArray = new byte[tamanhoVetor];
                raf.readFully(byteArray);

                Capitulo capitulo = new Capitulo();
                capitulo.fromByteArray(byteArray);

                System.out.println(capitulo.toString());
                return true;
            }
        }

        return false;
    }

    // Lê e exibe múltiplos capítulos com base em um vetor de IDs, usando os índices B+ e Hash.
    static void lerCapitulos(int[] ids, TreeBplus arvore, HashEstendido hash) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(CAPITULOS, "r")) {
            for (int id : ids) {
                Long enderecoArvore = arvore.buscar(id);
                Long enderecoHash = hash.buscar(id);

                System.out.println("\n[ID " + id + "]");
                System.out.println("  [Árvore B+] Endereço: " + enderecoArvore);
                System.out.println("  [Hash Estendido] Endereço: " + enderecoHash);

                Long endereco = (enderecoHash != null) ? enderecoHash : enderecoArvore;

                if (endereco != null) {
                    raf.seek(endereco);
                    byte valido = raf.readByte();
                    int tamanhoVetor = raf.readInt();

                    if (valido == 1) {
                        byte[] byteArray = new byte[tamanhoVetor];
                        raf.readFully(byteArray);

                        Capitulo capitulo = new Capitulo();
                        capitulo.fromByteArray(byteArray);

                        System.out.println("  Conteúdo:");
                        System.out.println("  " + capitulo.toString());
                    } else {
                        System.out.println("  Registro marcado como removido.");
                    }
                } else {
                    System.out.println("  Não encontrado em nenhum índice.");
                }
            }
        }
    }

    // Atualiza um capítulo existente: tenta sobrescrever se couber, senão insere no fim e atualiza os índices.
    static boolean atualizarCapitulo(int ID, TreeBplus arvore, HashEstendido hash) throws IOException {
        try (RandomAccessFile RAF = new RandomAccessFile(CAPITULOS, "rw")) {
            Long posicao = arvore.buscar(ID);

            if (posicao == null) {
                System.out.println("ID não encontrado na árvore.");
                return false;
            }

            RAF.seek(posicao);
            byte valido = RAF.readByte();
            int tamanhoVetor = RAF.readInt();

            if (valido == 1) {
                byte[] byteArray = new byte[tamanhoVetor];
                RAF.readFully(byteArray);

                Capitulo capitulo = new Capitulo();
                capitulo.fromByteArray(byteArray);

                if (capitulo.getId() == ID) {
                    Capitulo novoCapitulo = AuxFuncoes.InstanciaCapitulo();
                    novoCapitulo.setId(ID);
                    byte[] novoByteArray = novoCapitulo.toByteArray();

                    if (novoByteArray.length <= tamanhoVetor) {
                        System.out.println("Atualização coube no espaço reservado.");
                        RAF.seek(posicao + 5); // 1 byte lápide + 4 bytes tamanho
                        RAF.write(novoByteArray);
                        RAF.write(new byte[tamanhoVetor - novoByteArray.length]);
                    } else {
                        System.out.println("Atualização não coube. Inserido no fim do arquivo.");

                        // Marcar como removido
                        RAF.seek(posicao);
                        RAF.writeByte(0);

                        // Escrever no fim
                        long novaPosicao = RAF.length();
                        RAF.seek(novaPosicao);
                        RAF.writeByte(1);
                        RAF.writeInt(novoByteArray.length);
                        RAF.write(novoByteArray);

                        // Atualizar índices corretamente
                        arvore.remover(ID); // Remove o antigo
                        arvore.inserir(ID, novaPosicao);

                        hash.remover(ID); // Remove o antigo
                        hash.inserir(ID, novaPosicao);
                    }

                    // Salva arquivos de índice
                    arvore.salvarFolhasNoArquivo(IndiceArvore);
                    return true;
                }
            }
        }

        return false;
    }

    // Realiza a exclusão lógica de um capítulo com base no ID e atualiza os índices B+ e Hash.
    static boolean deletarCapitulo(int ID, TreeBplus arvore, HashEstendido hash) throws IOException {
        try (RandomAccessFile RAF = new RandomAccessFile(CAPITULOS, "rw")) {
            RAF.seek(0);
            int UltimoId = RAF.readInt();

            while (RAF.getFilePointer() < RAF.length()) {
                long ponteiro = RAF.getFilePointer();
                byte valido = RAF.readByte();
                int tamanhoVetor = RAF.readInt();

                if (valido == 1) {
                    byte[] byteArray = new byte[tamanhoVetor];
                    RAF.readFully(byteArray);
                    Capitulo capitulo = new Capitulo();
                    capitulo.fromByteArray(byteArray);

                    if (capitulo.getId() == ID) {
                        // Exclusão lógica no arquivo
                        RAF.seek(ponteiro);
                        RAF.writeByte(0);

                        if (ID == UltimoId) {
                            RAF.seek(0);
                            RAF.writeInt(UltimoId - 1);
                        }

                        // --- Atualiza árvore B+ ---
                        arvore.remover(ID); // Certifique-se de que está implementado
                        arvore.salvarFolhasNoArquivo(IndiceArvore);

                        // --- Atualiza índice hash ---
                        hash.remover(ID);

                        return true;
                    }
                } else {
                    RAF.skipBytes(tamanhoVetor);
                }
            }
        }
        return false;
    }

}
