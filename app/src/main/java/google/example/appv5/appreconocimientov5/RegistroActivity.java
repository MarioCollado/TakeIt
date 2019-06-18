package google.example.appv5.appreconocimientov5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import google.example.appv5.appreconocimientov5.bbdd.DatabaseHelper;
import google.example.appv5.appreconocimientov5.bbdd.User;

public class RegistroActivity extends AppCompatActivity {

    private CardView login;
    private TextView signin;
    private TextInputLayout txt_psw, txt_user, txt_email;
    private EditText ed_psw, ed_user, ed_email;
    private Button btn_signup;
    private ImageView imageView;

    final DatabaseHelper dbHelper = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        login = (CardView) findViewById(R.id.login);
        txt_user = (TextInputLayout) findViewById(R.id.txt_user);
        txt_psw = (TextInputLayout) findViewById(R.id.txt_psw);
        txt_email = (TextInputLayout) findViewById(R.id.txt_email);
        ed_user = (EditText) findViewById(R.id.ed_user);
        ed_psw = (EditText) findViewById(R.id.ed_psw);
        ed_email = (EditText) findViewById(R.id.ed_email);
        btn_signup = (Button) findViewById(R.id.btn_signunp);
        imageView = (ImageView) findViewById(R.id.img_logo);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camposVacios()) {
                    dbHelper.addUser(new User(ed_email.getText().toString(), ed_user.getText().toString(), ed_psw.getText().toString()));
                    Toast.makeText(RegistroActivity.this, "Usuario añadido", Toast.LENGTH_SHORT).show();
                    ed_email.setText("");
                    ed_user.setText("");
                    ed_psw.setText("");
                    startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
                }else{
                    Toast.makeText(RegistroActivity.this, "Campos vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean camposVacios() {
        if (TextUtils.isEmpty(ed_user.getText().toString())
                || TextUtils.isEmpty(ed_email.getText().toString())
                ||  TextUtils.isEmpty(ed_psw.getText().toString())) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegistroActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
    }
}
