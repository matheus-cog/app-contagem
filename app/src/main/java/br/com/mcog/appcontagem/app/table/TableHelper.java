package br.com.mcog.appcontagem.app.table;

import android.content.Context;

import java.util.ArrayList;

import br.com.mcog.appcontagem.app.database.DBAdapter;
import br.com.mcog.appcontagem.app.objects.Produto;

public class TableHelper {

    Context context;

    public TableHelper(Context context) {
        this.context = context;
    }

    public String[][] getProdutos() {

        ArrayList<Produto> produtoArray = new DBAdapter(context).getProdutosContagem();
        Produto produto;

        String[][] produtos = new String[produtoArray.size()][4];
        for (int i=0; i<produtoArray.size(); i++){
            produto = produtoArray.get(i);

            produtos[i][0]=produto.getIdentificador();
            produtos[i][1]=produto.getCodBarras();
            produtos[i][2]=Integer.toString(produto.getSubTotal());
            produtos[i][3]=Integer.toString(produto.getQuantidade());
        }

        return produtos;
    }
}
