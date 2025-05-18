
import java.io.*;
import java.util.*;

public class Huffman {

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
        // 1. Ler todos os bytes do arquivo original
        FileInputStream leitor = new FileInputStream(caminhoEntrada);
        byte[] dados = leitor.readAllBytes();
        leitor.close();

        // 2. Calcular frequências
        Map<Byte, Integer> frequencias = new HashMap<>();
        for (byte b : dados) {
            frequencias.put(b, frequencias.getOrDefault(b, 0) + 1);
        }

        // 3. Construir a árvore de Huffman
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

        // 4. Codificar os dados
        StringBuilder bits = new StringBuilder();
        for (byte b : dados) {
            bits.append(codigos.get(b));
        }

        // 5. Salvar o mapa de codificação e os dados comprimidos
        BitSet conjuntoBits = new BitSet(bits.length());
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                conjuntoBits.set(i);
            }
        }

        ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(caminhoSaida));
        saida.writeObject(codigos);         // mapa de Huffman
        saida.writeInt(bits.length());      // quantidade de bits significativos
        saida.write(conjuntoBits.toByteArray()); // dados comprimidos
        saida.close();

        System.out.println("Compressão concluída. Arquivo salvo em " + caminhoSaida);

    }

   public static void descomprimir(String caminhoEntrada) throws IOException, ClassNotFoundException {
    File arquivoComprimido = new File(caminhoEntrada);
    String nomeOriginal = arquivoComprimido.getName();
    String pasta = arquivoComprimido.getParent();

    // Gerar o novo nome substituindo "Compressao" por "Descomprimido"
    String nomeDescomprimido = nomeOriginal.replace("Compressao", "Descomprimido");
    String caminhoSaida = (pasta != null ? pasta + File.separator : "") + nomeDescomprimido;

    // Leitura e descompressão
    try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivoComprimido))) {
        Map<Byte, String> codigos = (Map<Byte, String>) entrada.readObject();
        int tamanhoBits = entrada.readInt();
        BitSet conjuntoBits = BitSet.valueOf(entrada.readAllBytes());

        // Inverter mapa de codificação
        Map<String, Byte> inverso = new HashMap<>();
        codigos.forEach((b, codigo) -> inverso.put(codigo, b));

        // Decodificar bits
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

        // Gravar dados descomprimidos substituindo o arquivo comprimido
        try (FileOutputStream fos = new FileOutputStream(arquivoComprimido)) {
            fos.write(saidaBytes.toByteArray());
        }
    }

    // Renomear o arquivo para refletir a descompressão
    File novoArquivo = new File((pasta != null ? pasta + File.separator : "") + nomeDescomprimido);
    if (arquivoComprimido.renameTo(novoArquivo)) {
        System.out.println("Descompressão concluída. Arquivo renomeado para: " + nomeDescomprimido);
    } else {
        System.out.println("Descompressão concluída. Mas falha ao renomear o arquivo.");
    }
}


}
