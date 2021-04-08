package br.com.mcog.appcontagem.app.database;

public class Constants {

    // Constantes em gerenciamento de sessão
    public static final String SHARED_PREF_NAME = "sessao";

    // Colunas da tabela contagem
    static final String ID_LANCAMENTO = "id_lancamento";
    static final String IDENTIFICADOR = "identificador";
    static final String COD_BARRAS = "cod_barras";
    static final String QUANTIDADE = "quantidade";
    static final String SUBTOTAL = "subtotal";
    // Colunas da tabela de produtos (bd externo)
    static final String DESCRICAO = "descricao";
    static final String NOME_MARCA = "nome_marca";
    static final String COD_FABRICA = "cod_fabrica";
    static final String PRECO = "preco";
    static final String QUANTIDADE_EMBALAGEM = "qtd_emb";
    static final String TIPO = "tipo";
    // Colunas da tabela de dados da contagem
    static final String ID_CONTAGEM_INTERNO = "id_contagem_interno"; // Se refere a um identificador no bd interno
    static final String ID_CONTAGEM_EXTERNO = "id_contagem_externo"; // Se refere a um identificador no bd externo
    static final String COD_LEITOR = "cod_leitor";
    static final String DATA_CONTAGEM = "data_contagem";
    static final String AREA = "area";
    static final String UNIDADE_CONTAGEM = "unidade_contagem";
    static final String REINCIDENCIA = "reincidencia";
    static final String COD_OPERADOR = "cod_operador";
    static final String TIPO_CONTAGEM = "tipo_contagem";
    static final String PERMISSAO_EDICAO_CELULAS = "permissao_edicao_celulas";

    // Informações do banco local
    public static final String DB_NAME = "bd_app_contagem.db";
    public static final String TB_CONTAGEM_NAME = "tbl_contagem";
    static final String TB_PRODUTOS_EMPRESA_NAME = "tbl_produtos_empresa";
    static final String TB_DADOS_CONTAGEM = "tbl_dados_contagem";
    static final int DB_VERSION = 1;
    // Informações do banco externo
    public static final String URI_BD_EXTERNA = "jdbc:mysql://"+"IP DO SERVER"+"/"+"NOME DO BANCO";
    public static final String USER_BD_EXTERNO = "USER";
    public static final String SENHA_BD_EXTERNO = "PASS";

    // Query de get no banco externo
    public static final String SELECT_TODOS_PRODUTOS_EMPRESA = "INSIRA SUA QUERY AQUI.";

    // Queries de criação das tabelas
    static final String CREATE_TB_CONTAGEM =
            "CREATE TABLE tbl_contagem(id_lancamento INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "identificador TEXT NOT NULL, cod_barras TEXT NOT NULL, quantidade INTEGER NOT NULL, subtotal INTEGER NOT NULL)";

    static final String CREATE_TB_PRODUTOS_EMPRESA =
            "CREATE TABLE tbl_produtos_empresa(id_lancamento INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "identificador TEXT NOT NULL, descricao TEXT NOT NULL, cod_barras TEXT NOT NULL, nome_marca TEXT NOT NULL, " +
                    "cod_fabrica TEXT NOT NULL, preco REAL NOT NULL, qtd_emb INTEGER NOT NULL, tipo TEXT NOT NULL)";

    static final String CREATE_TB_DADOS_CONTAGEM =
            "CREATE TABLE tbl_dados_contagem(id_contagem_interno INTEGER PRIMARY KEY AUTOINCREMENT, id_contagem_externo INTEGER, " +
                    "cod_leitor TEXT NOT NULL, area TEXT NOT NULL, unidade_contagem TEXT NOT NULL, data_contagem TEXT NOT NULL, " +
                    "reincidencia TEXT NOT NULL, cod_operador TEXT NOT NULL, tipo_contagem TEXT NOT NULL, permissao_edicao_celulas TEXT NOT NULL)";

    // Queries de drop do banco
    static final String DROP_TB_CONTAGEM = "DROP TABLE IF EXISTS "+TB_CONTAGEM_NAME;
    static final String DROP_TB_PRODUTOS_EMPRESA = "DROP TABLE IF EXISTS "+TB_PRODUTOS_EMPRESA_NAME;
    static final String DROP_TB_DADOS_CONTAGEM = "DROP TABLE IF EXISTS "+TB_DADOS_CONTAGEM;
}
