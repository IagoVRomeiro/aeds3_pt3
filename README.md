# aeds3_pt3
Neste trabalho, você deverá implementar Compressão de Dados dentro do contexto do seu Trabalho.  
• Algoritmos de Compressão de Dados: Huffman e LZW. 
• Orientações para a criação da Compressão de Dados: 
o No seu menu de opções apresentado ao usuário, ofereça a possibilidade de escolher 
uma opção para realizar a compressão na base de dados criada e crie uma opção para 
ele realizar a descompressão de alguma versão de compressão criada.  
o Quando o usuário escolher a opção de compressão, a base de dados deve passar pela 
compressão usando os dois algoritmos e os novos arquivos gerados devem seguir o 
nome: “nomeArquivoNomeAlgoritmoCompressaoX”, em que X representa a versão da 
compressão, nomeArquivo o nome original do arquivo e nomeAlgoritmo o nome do 
algoritmo usado.  
o Além de realizar a compressão e gerar os novos arquivos, o algoritmo deve mostrar para 
o usuário a porcentagem de ganho ou perda de cada algoritmo e o tempo de execução 
de cada, comparando as execuções e mostrando qual algoritmo (Huffman ou LZW) foi 
melhor para aquela condição. 
o A compressão deve ser feita em todos os campos do arquivo, incluindo o cabeçalho, 
indicadores de tamanho de strings e afins. 
o Caso o usuário escolha descompactar o arquivo, ele deve passar a versão “X” que 
deseja, e a descompressão deve substituir o arquivo de dados pelo arquivo gerado pela 
descompressão. Novamente, o algoritmo deve mostrar para o usuário o tempo de 
execução de cada, comparando as execuções e mostrando qual algoritmo (Huffman ou 
LZW) foi melhor para aquela condição de descompactação. 
o Para o algoritmo de LZW, você é responsável pela definição do dicionário inicial. 
o As decisões relacionadas ao algoritmo são de responsabilidade do grupo. 
