package br.com.mcog.appcontagem.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import br.com.mcog.appcontagem.R;
import br.com.mcog.appcontagem.app.database.Constants;
import br.com.mcog.appcontagem.app.database.DBAdapter;
import br.com.mcog.appcontagem.app.objects.Contagem;
import br.com.mcog.appcontagem.app.objects.Produto;
import br.com.mcog.appcontagem.app.objects.ProdutoEmpresa;
import br.com.mcog.appcontagem.app.table.TableHelper;
import br.com.mcog.appcontagem.app.tools.GerenciamentoSessao;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ContagemActivity extends AppCompatActivity {

    private final String[] vetorColunasTabelaContagem = {"Iden.","Cód. de barras","Quant.","Subtotal"};
    private boolean permissaoEdicaoCelula;

    // Mensagem de loading
    ProgressDialog progressDialog;
    // Variáveis para conexão com banco
    // Externo
    Connection conn;
    Statement st;
    ResultSet rs;
    // Ambos
    DBAdapter dbAdapter = new DBAdapter(this);

    // Get dos produtos (banco externo)
    @SuppressLint("StaticFieldLeak")
    class Task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(Constants.URI_BD_EXTERNA, Constants.USER_BD_EXTERNO, Constants.SENHA_BD_EXTERNO);
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(Constants.SELECT_TODOS_PRODUTOS_EMPRESA);
                ProdutoEmpresa produto = new ProdutoEmpresa();

                // Para cada repetição do while, um produto empresa novo é lançado no banco interno
                while (rs.next()){
                    produto.setIdentificador(rs.getString(1).replace(" ",""));
                    produto.setDescricao(rs.getString(2));
                    produto.setCodBarras(rs.getString(3).replace(" ",""));
                    produto.setNomeMarca(rs.getString(4).replace(" ",""));
                    produto.setCodFabrica(rs.getString(5).replace(" ",""));
                    produto.setPreco(rs.getDouble(6));
                    produto.setQuantidadeEmbalagem(rs.getInt(7));
                    produto.setTipo(rs.getString(8).replace(" ",""));

                    dbAdapter.salvarProdutoEmpresa(produto);
                }

            } catch(Exception ex){
                ex.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contagem);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("As informações estão sendo carregadas. Por favor, aguarde.");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        ImageButton btnFinalizarContagem = findViewById(R.id.btn_finalizar_contagem);
        ImageButton btnSalvarContagem = findViewById(R.id.btn_salvar_contagem);
        ImageButton btnCancelarContagem = findViewById(R.id.btn_cancelar_contagem);
        ImageButton btnInformacoesContagem = findViewById(R.id.btn_informacoes_contagem);

        TextView txtQuantidade = findViewById(R.id.txt_quantidade);
        TextView txtCodigoBarras = findViewById(R.id.txt_cod_barras);
        EditText edtQuantidade = findViewById(R.id.edt_quantidade);

        EditText edtCodigoBarras = findViewById(R.id.edt_cod_barras);
        edtCodigoBarras.setTextIsSelectable(true);

        TableView<String[]> tableView = findViewById(R.id.table_view);
        TableHelper tableHelper = new TableHelper(this);

        // Instância de contagem para set da sessão e get da informações dela
        Contagem contagem = dbAdapter.verificaTabelaContagens();
        Log.d("", contagem.getPermissaoEdicaoCelulas());

        // Salvar sessão
        GerenciamentoSessao gerenciamentoSessao = new GerenciamentoSessao(ContagemActivity.this);
        if(gerenciamentoSessao.getSessao() == -1){
            gerenciamentoSessao.salvarSessao(contagem);
        }

        // Verifica se os produtos empresa já estão cadastrados na tabela, para não gerar uma recarregamento desnecessário.
        if(!dbAdapter.verificaTabelaProdutoEmpresa()){
            dbAdapter.deletarProdutosEmpresa();
            progressDialog.show();
            new Task().execute();
        }

        // Set exibição do campo quantidade (tipo de contagem)
        if(contagem.getTipoContagem().equals("Contagem padrão")){
            txtQuantidade.setVisibility(View.VISIBLE);
            edtQuantidade.setEnabled(true);
            edtQuantidade.setVisibility(View.VISIBLE);
        } else if(contagem.getTipoContagem().equals("Contagem cega")){
            // Set do EditText e TextView em nova posição no layout
            ConstraintLayout.LayoutParams novoLayoutEditText = (ConstraintLayout.LayoutParams) edtCodigoBarras.getLayoutParams();
            novoLayoutEditText.topMargin = 40;
            novoLayoutEditText.leftMargin = 10;
            novoLayoutEditText.rightMargin = 10;
            ConstraintLayout.LayoutParams novoLayoutTextView = (ConstraintLayout.LayoutParams) txtCodigoBarras.getLayoutParams();
            novoLayoutTextView.leftMargin = 10;
            novoLayoutTextView.bottomMargin = 5;
            edtCodigoBarras.setLayoutParams(novoLayoutEditText);
            txtCodigoBarras.setLayoutParams(novoLayoutTextView);
            // Desabilitação do campo e textview de quantidade
            txtQuantidade.setText("");
            edtQuantidade.setEnabled(false);
            edtQuantidade.setVisibility(View.INVISIBLE);
        }
        // Get da permissão para edição das células
        if(contagem.getPermissaoEdicaoCelulas().equals("Permitir")){
            permissaoEdicaoCelula = true;
        } else if(contagem.getPermissaoEdicaoCelulas().equals("Não permitir")){
            permissaoEdicaoCelula = false;
        }

        // Botões (barra superior)
        btnFinalizarContagem.setOnClickListener(v -> new AlertDialog.Builder(ContagemActivity.this)
                .setTitle("Finalizar contagem")
                .setMessage("Deseja finalizar esta contagem?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Envio da contagem para o banco de dados
                    progressDialog.setMessage("A contagem está sendo enviada para o banco de dados, gentileza aguardar.");
                    progressDialog.show();
                    // Salvar contagem
                    Toast.makeText(ContagemActivity.this, "Agora o método vai enviar a contagem para o banco", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    // Salvando a contagem localmente
                    if (ActivityCompat.checkSelfPermission(ContagemActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        // Instância da classe que exporta o banco
                        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(ContagemActivity.this, Constants.DB_NAME);
                        // Exibição de mensagem para o usuário
                        progressDialog.setMessage("A contagem está sendo exportada. Por favor, aguarde.");

                        sqLiteToExcel.exportSingleTable(Constants.TB_CONTAGEM_NAME,"planilhaContagem.xls", new SQLiteToExcel.ExportListener() {
                            @Override
                            public void onStart() {
                                progressDialog.show();
                            }
                            @Override
                            public void onCompleted(String filePath) {
                                gerenciamentoSessao.removerSessao();
                                // Deleta contagem, os dados da contagem e o produtos empresa para comparação
                                dbAdapter.deletarContagem();
                                dbAdapter.deletarDadosContagem();
                                dbAdapter.deletarProdutosEmpresa();
                                progressDialog.dismiss();
                                Toast.makeText(ContagemActivity.this, "A planilha foi salva em: "+filePath, Toast.LENGTH_LONG).show();
                                finish();
                            }
                            @Override
                            public void onError(Exception e) {
                                Snackbar.make(v, "Ocorreu um erro na exportação, "+e+".", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                                        .show();
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(ContagemActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        btnSalvarContagem.setOnClickListener(v -> new AlertDialog.Builder(ContagemActivity.this)
                .setTitle("Salvar contagem")
                .setMessage("Deseja salvar esta contagem (localmente)?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Verifica se há permissão para escrita no armazenamento
                    if (ActivityCompat.checkSelfPermission(ContagemActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        // Instância da classe que exporta o banco
                        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(this, Constants.DB_NAME);
                        // Exibição de mensagem para o usuário
                        progressDialog.setMessage("A contagem está sendo exportada. Por favor, aguarde.");

                        sqLiteToExcel.exportSingleTable(Constants.TB_CONTAGEM_NAME ,"planilhaContagem.xls", new SQLiteToExcel.ExportListener() {
                            @Override
                            public void onStart() {
                                progressDialog.show();
                            }
                            @Override
                            public void onCompleted(String filePath) {
                                progressDialog.dismiss();
                                Toast.makeText(ContagemActivity.this, "A planilha foi salva em: "+filePath, Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onError(Exception e) {
                                Snackbar.make(v, "Ocorreu um erro na exportação, .", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                                        .show();
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(ContagemActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        btnCancelarContagem.setOnClickListener(v -> {
            final View cancelaLayout = getLayoutInflater().inflate(R.layout.cancela_contagem_layout, null);
            new AlertDialog.Builder(ContagemActivity.this)
                    .setView(cancelaLayout)
                    .setTitle("Cancelar contagem")
                    .setMessage("Insira usuário e senha para cancelar:")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        EditText edtLoginCancela = cancelaLayout.findViewById(R.id.edt_cancela_usuario);
                        EditText edtSenhaCancela = cancelaLayout.findViewById(R.id.edt_cancela_senha);
                        if(TextUtils.isEmpty(edtLoginCancela.getText().toString()) || TextUtils.isEmpty(edtSenhaCancela.getText().toString())){
                            Snackbar.make(v, "Todos os campos devem ser preenchidos.", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                                    .show();
                        } else{
                            if(edtLoginCancela.getText().toString().equals("admin") && edtSenhaCancela.getText().toString().equals("admin")){
                                gerenciamentoSessao.removerSessao();
                                dbAdapter.deletarContagem();
                                dbAdapter.deletarDadosContagem();
                                dbAdapter.deletarProdutosEmpresa();
                                finish();
                            } else{
                                Snackbar.make(v, "Usuário e senha incorretos.", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                                        .show();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                    .show();
        });

        btnInformacoesContagem.setOnClickListener(v -> new AlertDialog.Builder(ContagemActivity.this)
                .setTitle("Informações")
                .setMessage(" Unidade: "+contagem.getUnidadeContagem()
                        + "\n Área de contagem: "+contagem.getArea()
                        + "\n Data da contagem: "+contagem.getDataContagem()
                        + "\n Reincidência: "+contagem.getReincidencia()
                        + "\n Número do coletor: "+contagem.getCodLeitor()
                        + "\n"
                        + "\n ID Operador: "+contagem.getCodOperador()
                        + "\n ID Contagem: "+contagem.getIdContagem())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        // Gerenciamento da tabela

        // Formatação do cabeçalho
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(getApplicationContext(), vetorColunasTabelaContagem);
        simpleTableHeaderAdapter.setTextSize(12);
        tableView.setHeaderBackgroundColor(Color.BLACK);
        simpleTableHeaderAdapter.setTextColor(Color.WHITE);
        simpleTableHeaderAdapter.setPaddingBottom(5);
        simpleTableHeaderAdapter.setPaddingTop(5);
        simpleTableHeaderAdapter.setPaddingLeft(10);
        simpleTableHeaderAdapter.setPaddingRight(0);
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        // Formatação das colunas
        tableView.setColumnCount(4);
        tableView.setColumnWeight(1,2);
        tableView.setColumnWeight(2,1);
        tableView.setColumnWeight(3,1);
        tableView.setColumnWeight(4,2);

        // Formatação e set data das células
        SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(ContagemActivity.this, tableHelper.getProdutos());
        dataAdapter.setTextSize(13);
        dataAdapter.setTextColor(Color.GRAY);
        dataAdapter.setPaddingBottom(5);
        dataAdapter.setPaddingTop(5);
        dataAdapter.setPaddingLeft(10);
        dataAdapter.setPaddingRight(0);
        tableView.setDataAdapter(dataAdapter);

        // Action gerenciadora do campo cód. de barras
        edtCodigoBarras.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Verificação se o código está completo
                if(edtCodigoBarras.getText().toString().length() >= 8){
                    Produto produto = new Produto();
                    produto.setCodBarras(edtCodigoBarras.getText().toString().replace(" ", ""));

                    // Condicional para se o campo quantidade ficar vazio
                    if(edtQuantidade.getText().toString().equals("")){
                        produto.setQuantidade(1);
                        produto.setSubTotal(1);
                    } else{
                        produto.setQuantidade(Integer.parseInt(edtQuantidade.getText().toString()));
                        produto.setSubTotal(Integer.parseInt(edtQuantidade.getText().toString()));
                    }
                    // Limpeza do campo de código de barras para nova leitura
                    edtCodigoBarras.setText(null);
                    edtCodigoBarras.requestFocus();

                    if(new DBAdapter(ContagemActivity.this).salvarProduto(produto)){
                        // Formatação das células e criação do adapter
                        TableHelper tbHelper = new TableHelper(ContagemActivity.this);
                        SimpleTableDataAdapter adapter = new SimpleTableDataAdapter(ContagemActivity.this, tbHelper.getProdutos());
                        adapter.setTextSize(13);
                        adapter.setTextColor(Color.GRAY);
                        adapter.setPaddingBottom(5);
                        adapter.setPaddingTop(5);
                        adapter.setPaddingLeft(10);
                        adapter.setPaddingRight(0);
                        // Refresh dos dados
                        tableView.setDataAdapter(adapter);
                    } else{
                        Toast.makeText(ContagemActivity.this, "Não foi possível inserir o produto, tente novamente.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // OnClick (célula)
        tableView.addDataClickListener((rowIndex, clickedData) -> {
            if(permissaoEdicaoCelula){
                final View editaItem = getLayoutInflater().inflate(R.layout.edita_item_contagem_layout, null);
                new AlertDialog.Builder(ContagemActivity.this)
                        .setView(editaItem)
                        .setTitle("Edição de célula")
                        .setMessage("Insira a nova quantidade para o cód. de barras: "+clickedData[1])
                        .setPositiveButton("SALVAR", (dialog, which) -> {
                            EditText edtNovaQuantidade = editaItem.findViewById(R.id.edt_nova_quantidade);
                            if(TextUtils.isEmpty(edtNovaQuantidade.getText().toString())){
                                Snackbar.make(editaItem, "Insira um valor válido.", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                                        .show();
                            } else{
                                Produto produto = new Produto();
                                produto.setCodBarras(clickedData[1]);
                                if(dbAdapter.updateProduto(produto, Integer.parseInt(edtNovaQuantidade.getText().toString()), Integer.parseInt(edtNovaQuantidade.getText().toString()))){
                                    // Formatação das células e criação do adapter
                                    TableHelper tbHelper = new TableHelper(ContagemActivity.this);
                                    SimpleTableDataAdapter adapter = new SimpleTableDataAdapter(ContagemActivity.this, tbHelper.getProdutos());
                                    adapter.setTextSize(13);
                                    adapter.setTextColor(Color.GRAY);
                                    adapter.setPaddingBottom(5);
                                    adapter.setPaddingTop(5);
                                    adapter.setPaddingLeft(10);
                                    adapter.setPaddingRight(0);
                                    // Refresh dos dados
                                    tableView.setDataAdapter(adapter);
                                    Toast.makeText(ContagemActivity.this, "Item atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ContagemActivity.this, "Ocorreu uma falha na atualização.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                String msg = "Cód. barras: "+clickedData[1]+" | Qtd: "+clickedData[2];
                Toast.makeText(ContagemActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}