package com.binny.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;

import org.ionc.dialog.callback.OnStringCallbcak;
import org.ionc.dialog.check.DialogCheckMnemonic;
import org.ionc.dialog.export.DialogTextMessage;
import org.ionc.dialog.mnemonic.DialogMnemonic;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogMnemonic.OnSavedMnemonicCallback, DialogTextMessage.OnBtnClickedListener, OnStringCallbcak {

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
        DialogMnemonic dialogMnemonic = new DialogMnemonic(this,mnemonics,this);
        dialogMnemonic.show();
    }

    @Override
    public void onToSaved() {
        new DialogTextMessage(this).setTitle("注意")
                .setMessage("请务必妥善保管您的助记词,一旦丢失你,你的财产可能面临重大损失")
                .setBtnText("我已知晓并保存")
                .setHintMsg("")
                .setCopyBtnClickedListener(this).show();
    }

    @Override
    public void onCancel(DialogMnemonic dialogMnemonic) {

    }

    @Override
    public void onBtnClick(DialogTextMessage dialogTextMessage) {
        new DialogCheckMnemonic(this, this).show();
    }

    @Override
    public void onString(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {

    }
}
