package br.com.mcog.appcontagem.app.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import br.com.mcog.appcontagem.app.database.Constants;
import br.com.mcog.appcontagem.app.objects.Contagem;

public class GerenciamentoSessao {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String KEY_SESSAO = "sessao_usario";

    @SuppressLint("CommitPrefEdits")
    public GerenciamentoSessao(Context context){
        sp = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    // Salva sessão se não estiver logado
    public void salvarSessao(Contagem contagem){
        int id = contagem.getIdContagem();
        editor.putInt(KEY_SESSAO, id).commit();
    }

    // Redefine o valor de sessão
    public void removerSessao(){
        editor.putInt(KEY_SESSAO, -1).commit();
    }

    // Retorna o id da contagem quando estiver ativa
    public int getSessao(){
        return sp.getInt(KEY_SESSAO, -1);
    }
}
