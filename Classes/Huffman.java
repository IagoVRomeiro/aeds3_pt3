import java.io.*;
import java.util.*;

public class Huffman {

    public static int versaoCompressao = 1;

    static class NoHuffman implements Comparable<NoHuffman> {

        byte valor;
        int frequencia;
        NoHuffman esquerda, direita;

        NoHuffman(byte valor, int frequencia) {
            this.valor = valor;
            this.frequencia = frequencia;
        }

        NoHuffman(NoHuffman esquerda, NoHuffman direita) {
            this.valor = 0;
            this.frequencia = esquerda.frequencia + direita.frequencia;
            this.esquerda = esquerda;
            this.direita = direita;
        }

        @Override
        public int compareTo(NoHuffman outro) {
            return Integer.compare(this.frequencia, outro.frequencia);
        }

        boolean ehFolha() {
            return esquerda == null && direita == null;
        }
    }

    private static Map<Byte, String> gerarCodigos(NoHuffman raiz) {
        Map<Byte, String> codigos = new HashMap<>();
        gerarCodigos(raiz, "", codigos);
        return codigos;
    }

    private static void gerarCodigos(NoHuffman no, String codigo, Map<Byte, String> mapa) {
        if (no.ehFolha()) {
            mapa.put(no.valor, codigo);
            return;
        }
        gerarCodigos(no.esquerda, codigo + "0", mapa);
        gerarCodigos(no.direita, codigo + "1", mapa);
    }

    public static void comprimir(String caminhoEntrada, String caminhoSaida) throws IOException {
        FileInputStream leitor = new FileInputStream(caminhoEntrada);
        byte[] dados = leitor.readAllBytes();
        leitor.close();

        Map<Byte, Integer> frequencias = new HashMap<>();
        for (byte b : dados) {
            frequencias.put(b, frequencias.getOrDefault(b, 0) + 1);
        }

        PriorityQueue<NoHuffman> fila = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entrada : frequencias.entrySet()) {
            fila.add(new NoHuffman(entrada.getKey(), entrada.getValue()));
        }

        while (fila.size() > 1) {
            NoHuffman esquerda = fila.poll();
            NoHuffman direita = fila.poll();
            fila.add(new NoHuffman(esquerda, direita));
        }

        NoHuffman raiz = fila.poll();
        Map<Byte, String> codigos = gerarCodigos(raiz);

        StringBuilder bits = new StringBuilder();
        for (byte b : dados) {
            bits.append(codigos.get(b));
        }

        BitSet conjuntoBits = new BitSet(bits.length());
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                conjuntoBits.set(i);
            }
        }

        ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(caminhoSaida));
        saida.writeObject(codigos);
        saida.writeInt(bits.length());
        saida.write(conjuntoBits.toByteArray());
        saida.close();

        System.out.println("Compressão concluída. Arquivo salvo em " + caminhoSaida);
    }

    public static void descomprimir(String caminhoEntrada, String caminhoSaida) throws IOException, ClassNotFoundException {
        File arquivoComprimido = new File(caminhoEntrada);

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivoComprimido))) {
            Map<Byte, String> codigos = (Map<Byte, String>) entrada.readObject();
            int tamanhoBits = entrada.readInt();
            BitSet conjuntoBits = BitSet.valueOf(entrada.readAllBytes());

            Map<String, Byte> inverso = new HashMap<>();
            codigos.forEach((b, codigo) -> inverso.put(codigo, b));

            StringBuilder codigoAtual = new StringBuilder();
            ByteArrayOutputStream saidaBytes = new ByteArrayOutputStream();

            for (int i = 0; i < tamanhoBits; i++) {
                codigoAtual.append(conjuntoBits.get(i) ? '1' : '0');
                Byte b = inverso.get(codigoAtual.toString());
                if (b != null) {
                    saidaBytes.write(b);
                    codigoAtual.setLength(0);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(caminhoSaida)) {
                fos.write(saidaBytes.toByteArray());
            }
        }
    }

    public static void Compressao(String CAPITULOS) throws IOException {
        String arquivoCompactado = String.format("Compressao/capitulosHuffmanCompressao%d.db", versaoCompressao);

        long inicio = System.currentTimeMillis();
        Huffman.comprimir(CAPITULOS, arquivoCompactado);
        long fim = System.currentTimeMillis();

        File original = new File(CAPITULOS);
        File comprimido = new File(arquivoCompactado);

        long tamanhoOriginal = original.length();
        long tamanhoCompactado = comprimido.length();
        double ganho = 100.0 * (tamanhoOriginal - tamanhoCompactado) / tamanhoOriginal;

        System.out.println("Compactação concluída em " + (fim - inicio) + " ms");
        System.out.println("Tamanho original: " + tamanhoOriginal + " bytes");
        System.out.println("Tamanho compactado: " + tamanhoCompactado + " bytes");
        System.out.printf("Ganho de compressão: %.2f%%\n", ganho);
    }

    public static void Descompressao(int versao) {
        String arquivoCompactado = String.format("Compressao/capitulosHuffmanCompressao%d.db", versao);

        File f = new File(arquivoCompactado);
        if (!f.exists()) {
            System.out.println("Arquivo compactado não encontrado!");
            return;
        }

        try {
            long inicio = System.currentTimeMillis();

            // Faz a descompressão em um arquivo temporário dentro de Compressao
            String arquivoTemporario = String.format("Compressao/tempDescompressao%d.db", versao);
            descomprimir(arquivoCompactado, arquivoTemporario);

            // Sobrescreve o arquivo compactado original com o arquivo descomprimido
            File arquivoOriginal = new File(arquivoCompactado);
            File arquivoTemp = new File(arquivoTemporario);

            // Apaga o arquivo compactado original para renomear
            if (!arquivoOriginal.delete()) {
                System.out.println("Falha ao apagar o arquivo compactado original.");
                return;
            }

            // Renomeia o temporário para o nome final de descompressão
            String arquivoFinal = String.format("Compressao/capitulosHuffmanDescompressao%d.db", versao);
            File arquivoDestino = new File(arquivoFinal);

            if (arquivoDestino.exists()) {
                arquivoDestino.delete();
            }

            boolean renomeado = arquivoTemp.renameTo(arquivoDestino);

            long fim = System.currentTimeMillis();

            if (renomeado) {
                System.out.println("Descompressão concluída em " + (fim - inicio) + " ms");
                System.out.println("Arquivo final: " + arquivoFinal);
            } else {
                System.out.println("Falha ao renomear arquivo para: " + arquivoFinal);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro na descompressão: " + e.getMessage());
        }
    }
}
