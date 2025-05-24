
import java.io.*;
import java.util.Scanner;

public class CRUD {

    public static final String CAPITULOS = "Capitulos/capitulos.db";

    public static void menu(TreeBplus arvore, HashEstendido hash) throws IOException {

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
                    if (AuxFuncoes.criarCapitulo(AuxFuncoes.InstanciaCapitulo(), arvore, hash)) {
                        System.out.println("Criado com sucesso");
                    } else {
                        System.out.println("Falhou na criacao");
                    }
                }
                case 2 -> {
                    if (!AuxFuncoes.lerCapitulo(AuxFuncoes.qualID(), arvore, hash)) {
                        System.out.println("Nao encontrado");
                    }
                }
                case 3 ->
                    AuxFuncoes.lerCapitulos(AuxFuncoes.PerguntaQTD_ID(), arvore, hash);

                case 4 -> {
                    if (AuxFuncoes.atualizarCapitulo(AuxFuncoes.qualID(), arvore, hash)) {
                        System.out.println("Atualizado com sucesso");
                    } else {
                        System.out.println("Falhou na atualizacao");
                    }
                }
                case 5 -> {
                    if (AuxFuncoes.deletarCapitulo(AuxFuncoes.qualID(), arvore, hash)) {
                        System.out.println("Excluido com sucesso");
                    } else {
                        System.out.println("Falhou na exclusao");
                    }
                }
                case 6 -> {
                    System.out.println("Qual versão?");
                    int versao = scanner.nextInt();

                    Huffman.versaoCompressao = versao;
                    LZW.versaoCompressao = versao;

                    System.out.println("\n-----------Huffman-----------");
                    Huffman.Compressao(CAPITULOS);

                    System.out.println("\n-----------LZW-----------");
                    LZW.Compressao(CAPITULOS);

                    AuxFuncoes.CompararCompressao(CAPITULOS, versao, Huffman.ganho, LZW.ganho, Huffman.tempoCompressao, LZW.tempoCompressao);
                }

                case 7 -> {
                    System.out.print("Digite a versao X da compressao para descompactar: ");
                    int versao = scanner.nextInt();

                    System.out.println("-----------Huffman-----------");
                    Huffman.Descompressao(versao);

                    System.out.println("\n-----------LZW-----------");
                    LZW.Descompressao(versao);

                    AuxFuncoes.CompararDescompressao(Huffman.tempoDescompressao, LZW.tempoDescompressao);
                }

                case 8 -> {
                    scanner.close();
                    System.out.println("Saindo...");
                    System.exit(0);
                }
                default ->
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}
