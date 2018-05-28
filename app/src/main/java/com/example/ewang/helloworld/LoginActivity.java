package com.example.ewang.helloworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ewang.helloworld.model.Constants;
import com.example.ewang.helloworld.helper.DialogHelper;
import com.example.ewang.helloworld.service.BaseActivity;
import com.example.ewang.helloworld.service.LoginService;


public class LoginActivity extends BaseActivity {

    String account;

    String password;

    EditText editAccount;

    EditText editPwd;

    Button btnSure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSure = findViewById(R.id.btn_sure);
        editAccount = findViewById(R.id.edit_account);
        editPwd = findViewById(R.id.edit_pwd);

        Intent intent = getIntent();
        int requestType = intent.getIntExtra("request", 0);

        String requestUri = "/user/login";
        if (requestType == MainActivity.request_reg) {
            btnSure.setText("注册");
            requestUri = "/user/reg";
        }

        String requestUrl = Constants.DefaultBasicUrl.getValue() + requestUri;
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                account = editAccount.getText().toString().trim();
                password = editPwd.getText().toString().trim();

                if (judgeParameters(account, password)) {
                    SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.apply();
                    Intent loginIntent = new Intent(LoginActivity.this, LoginService.class)
                            .putExtra("url", requestUrl)
                            .putExtra("account", account)
                            .putExtra("password", password);
                    startService(loginIntent);
                }

            }
        });
    }

    boolean judgeParameters(String account, String pwd) {
        if (account.isEmpty() || null == account || pwd.isEmpty() || null == pwd) {
            DialogHelper.showAlertDialog(LoginActivity.this, "Warning", "用户名或密码不得为空", (dialogInterface, i) -> {
                editAccount.setText("");
                editPwd.setText("");
            }, null);
            return false;
        }
        return true;
    }
}
