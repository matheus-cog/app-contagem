package br.com.mcog.appcontagem.app.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Constants.CREATE_TB_CONTAGEM);
            db.execSQL(Constants.CREATE_TB_PRODUTOS_EMPRESA);
            db.execSQL(Constants.CREATE_TB_DADOS_CONTAGEM);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(Constants.DROP_TB_CONTAGEM);
            db.execSQL(Constants.CREATE_TB_CONTAGEM);
            db.execSQL(Constants.DROP_TB_PRODUTOS_EMPRESA);
            db.execSQL(Constants.CREATE_TB_PRODUTOS_EMPRESA);
            db.execSQL(Constants.DROP_TB_DADOS_CONTAGEM);
            db.execSQL(Constants.CREATE_TB_DADOS_CONTAGEM);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
