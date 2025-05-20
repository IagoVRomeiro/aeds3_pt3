
import java.io.*;
import java.util.Scanner;

public class CRUD {

    public String IndiceHash = "Indices/capitulosIndiceHash.db";
    public static String IndiceArvore = "Indices/capitulosIndiceArvore.db";
    public static final String CAPITULOS = "Capitulos/capitulos.db";

    public static void CRUD(TreeBplus arvore, HashEstendido hash) throws IOException {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n--- Menu CRUD Capitulo ---");
            System.out.println("1. Criar Capitulo");
            System.out.println("2. Ler Um Capitulo");
            System.out.println("3. Ler Multiplos Capitulos");
            System.out.println("4. Atualizar Capitulo");
            System.out.println("5. Deletar Capitulo");
            System.out.println("6. Compactar base de dados");
            System.out.println("7. Descompactar base de dados");
            System.out.println("8. Sair");

            System.out.print("Escolha uma opcao: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> {
                    if (criarCapitulo(AuxFuncoes.CriarNovoCapitulo(), arvore, hash)) {
                        System.out.println("Criado com sucesso");
                    } else {
                        System.out.println("Falhou na criacao");
                    }
                }
                case 2 -> {
                    if (!lerCapitulo(AuxFuncoes.qualID(), arvore, hash)) {
                        System.out.println("Nao encontrado");
                    }
                }
                case 3 ->
                    lerCapitulos(AuxFuncoes.PerguntaQTD_ID(), arvore, hash);

                case 4 -> {
                    if (atualizarCapitulo(AuxFuncoes.qualID(), arvore, hash)) {
                        System.out.println("Atualizado com sucesso");
                    } else {
                        System.out.println("Falhou na atualizacao");
                    }
                }
                case 5 -> {
                    if (deletarCapitulo(AuxFuncoes.qualID(), arvore, hash)) {
                        System.out.println("Excluido com sucesso");
                    } else {
                        System.out.println("Falhou na exclusao");
                    }
                }
                case 6 -> {
                    System.out.print("Digite a versao X para salvar a compressao: ");
                    int versao = scanner.nextInt();

                    Huffman.versaoCompressao = versao;
                    LZW.versaoCompressao = versao;

                    Huffman.Compressao(CAPITULOS);
                    System.out.println();
                    LZW.Compressao(CAPITULOS);

                    AuxFuncoes.CompararCompressao(CAPITULOS, versao);
                }

                case 7 -> {
                    System.out.print("Digite a versao X da compressao para descompactar: ");
                    int versao = scanner.nextInt();

                    Huffman.Descompressao(versao);
                    LZW.Descompressao(versao);

                    AuxFuncoes.CompararCompressao(CAPITULOS, versao);
                }

                case 8 -> {
                    System.out.println("Saindo...");
                    System.exit(0);
                }
                default ->
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static boolean criarCapitulo(Capitulo capitulo, TreeBplus arvore, HashEstendido hash) throws IOException {
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

    private static boolean lerCapitulo(int ID, TreeBplus arvore, HashEstendido hash) throws IOException {
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

    private static void lerCapitulos(int[] ids, TreeBplus arvore, HashEstendido hash) throws IOException {
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

    private static boolean atualizarCapitulo(int ID, TreeBplus arvore, HashEstendido hash) throws IOException {
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
                    Capitulo novoCapitulo = AuxFuncoes.CriarNovoCapitulo();
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

                        hash.remover(ID);   // Remove o antigo
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

    private static boolean deletarCapitulo(int ID, TreeBplus arvore, HashEstendido hash) throws IOException {
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
