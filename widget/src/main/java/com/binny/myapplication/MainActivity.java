package com.binny.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.ionc.dialog.flow.MnemonicDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] mnemonics = {
                "asdaadad",
                "qqeqeqe",
                "qwqeq",
                "qweqwe",
                "wqeqeeqw",
                "cqwqe",
                "eqwe",
                "qeqwe",
                "qeqvcweq",
                "qweqwe",
                "qweqweq",
                "qweqwe"

        };
        MnemonicDialog mnemonicDialog = new MnemonicDialog(this,mnemonics);
        mnemonicDialog.show();
    }
}
