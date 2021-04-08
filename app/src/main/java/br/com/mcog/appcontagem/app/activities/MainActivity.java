package br.com.mcog.appcontagem.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import br.com.mcog.appcontagem.R;
import br.com.mcog.appcontagem.app.database.DBAdapter;
import br.com.mcog.appcontagem.app.tools.GerenciamentoSessao;
import br.com.sapereaude.maskedEditText.MaskedEditText;

public class MainActivity extends AppCompatActivity {

    DBAdapter dbAdapter = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAvancar = findViewById(R.id.btn_avancar);
        MaskedEditText edtDataContagem = findViewById(R.id.edt_data_contagem);
        EditText edtReincidencia = findViewById(R.id.edt_reincidencia);
        EditText edtCodOperador = findViewById(R.id.edt_cod_operador);
        EditText edtNumColetor = findViewById(R.id.edt_num_coletor);
        Spinner cmbUnidade = findViewById(R.id.cmb_unidade);
        Spinner cmbArea = findViewById(R.id.cmb_area);
        Spinner cmbTipoContagem = findViewById(R.id.cmb_tipo_contagem);
        Spinner cmbPermissaoEdicaoCelula = findViewById(R.id.cmb_permissao_edicao_celula);

        // Verifica se já existia alguma sessão aberta
        temSessaoEmAberto();

        btnAvancar.setOnClickListener(v -> {
            // Verifica se os campos estão vazios e se a data tem todos os dígitos
            if(TextUtils.isEmpty(Objects.requireNonNull(edtDataContagem.getText()).toString()) ||
                edtDataContagem.getRawText().length() < 8 ||
                TextUtils.isEmpty(edtReincidencia.getText().toString()) ||
                TextUtils.isEmpty(edtCodOperador.getText().toString()) ||
                TextUtils.isEmpty(edtNumColetor.getText().toString())) {
                Snackbar.make(v, "Todos os campos devem ser preenchidos corretamente.", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(android.R.color.holo_red_dark))
                        .show();

            } else{
                // Dialog confirmando o início da conferência
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirmação de início")
                        .setMessage("Você realmente deseja começar a conferência?")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            // Verifica se há alguma sessão em aberto
                            temSessaoEmAberto();
                            // Set das informaçoes da contagem no banco interno
                            dbAdapter.salvarDadosContagem(1,edtNumColetor.getText().toString(), edtDataContagem.getText().toString(), cmbArea.getSelectedItem().toString(),
                                    cmbUnidade.getSelectedItem().toString(), edtReincidencia.getText().toString(), edtCodOperador.getText().toString(),
                                    cmbTipoContagem.getSelectedItem().toString(), cmbPermissaoEdicaoCelula.getSelectedItem().toString());
                            // Abrir contagem
                            Intent openContagem = new Intent(getApplicationContext(), ContagemActivity.class);
                            startActivity(openContagem);
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

        // Combo box unidade
        ArrayList<String> listaUnidades = new ArrayList<>();
        listaUnidades.add("Centro de distribuição 1");
        listaUnidades.add("Centro de distribuição 2");
        listaUnidades.add("Centro de distribuição 3");
        listaUnidades.add("Centro de distribuição 4");
        listaUnidades.add("Centro de distribuição 5");
        listaUnidades.add("Loja 1");
        listaUnidades.add("Loja 2");
        listaUnidades.add("Loja 3");
        listaUnidades.add("Loja 4");
        listaUnidades.add("Loja 5");

        ArrayAdapter<String> listaUnidadesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listaUnidades);
        listaUnidadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbUnidade.setAdapter(listaUnidadesAdapter);

        // Combo box área
        ArrayList<String> listaArea = new ArrayList<>();
        listaArea.add("Área 1");
        listaArea.add("Área 2");
        listaArea.add("Área 3");
        listaArea.add("Área 4");
        listaArea.add("Área 5");
        listaArea.add("Área 6");
        listaArea.add("Área 7");
        listaArea.add("Área 8");
        listaArea.add("Área 9");
        listaArea.add("Área 10");

        ArrayAdapter<String> listaAreaAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listaArea);
        listaAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbArea.setAdapter(listaAreaAdapter);

        // Combo box tipo de contagem
        ArrayList<String> listaTipoContagem = new ArrayList<>();
        listaTipoContagem.add("Contagem padrão");
        listaTipoContagem.add("Contagem cega");

        ArrayAdapter<String> listaTipoContagemAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listaTipoContagem);
        listaTipoContagemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbTipoContagem.setAdapter(listaTipoContagemAdapter);

        // Combo box permissão para edição das células
        ArrayList<String> listaPermissaoEdicaoCelulas = new ArrayList<>();
        listaPermissaoEdicaoCelulas.add("Não permitir");
        listaPermissaoEdicaoCelulas.add("Permitir");

        ArrayAdapter<String> listaPermissaoEdicaoCelulasAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, listaPermissaoEdicaoCelulas);
        listaPermissaoEdicaoCelulasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbPermissaoEdicaoCelula.setAdapter(listaPermissaoEdicaoCelulasAdapter);
    }

    private void temSessaoEmAberto(){
        GerenciamentoSessao gerenciamentoSessao = new GerenciamentoSessao(MainActivity.this);
        if(gerenciamentoSessao.getSessao() != -1){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Prosseguir com contagem anterior")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage("Deseja continuar com a última contagem?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        Intent openContagem = new Intent(getApplicationContext(), ContagemActivity.class);
                        startActivity(openContagem);
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        gerenciamentoSessao.removerSessao();
                        dbAdapter.deletarProdutosEmpresa();
                        dbAdapter.deletarContagem();
                        dbAdapter.deletarDadosContagem();
                    })
                    .setCancelable(false)
                    .show();
        }
    }
}