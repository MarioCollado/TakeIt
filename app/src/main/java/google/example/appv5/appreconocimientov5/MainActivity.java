package google.example.appv5.appreconocimientov5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import es.dmoral.toasty.Toasty;
import google.example.appv5.appreconocimientov5.bbdd.DatabaseHelper;
import google.example.appv5.appreconocimientov5.bbdd.User;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private CardView login;
    private TextView signin,txt_newuser,txt_logo;
    private TextInputLayout txt_psw,txt_name;
    private EditText ed_psw, ed_name;
    private Button btn_registro,btn_signin;
    private ImageView img_logo;
    private CheckBox mCheckBox;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";

    private SignInButton btn_signin_Goggle;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG= "SignInActivity";
    private static final int RC_SIGN_IN = 9001;



    final DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (CardView) findViewById(R.id.login);
        txt_name = (TextInputLayout) findViewById(R.id.txt_name);
        txt_psw = (TextInputLayout) findViewById(R.id.txt_psw);
        txt_newuser = (TextView) findViewById(R.id.txt_newuser);
        btn_registro = (Button) findViewById(R.id.btn_registro);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_psw = (EditText) findViewById(R.id.ed_psw);
        img_logo = (ImageView) findViewById(R.id.img_logo);
        txt_logo = (TextView) findViewById(R.id.txt_title);
        mCheckBox = (CheckBox) findViewById(R.id.mcheckbox);



        //SharedPreferences para guardar el login
        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        getPreferenceData();

        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistroActivity.class));
                overridePendingTransition(R.anim.zoom_back_in, R.anim.zoom_back_out);
            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camposVacios()) {
                    User user = dbHelper.queryUser(ed_name.getText().toString(), ed_psw.getText().toString());
                    if (user != null) {

                        if (mCheckBox.isChecked()){
                            Boolean boolIsChecked = mCheckBox.isChecked();
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("pref_name", ed_name.getText().toString());
                            editor.putString("pref_pass", ed_psw.getText().toString());
                            editor.putBoolean("pref_check",boolIsChecked);
                            editor.apply();
                            Toasty.success(MainActivity.this,"Mantendrás tu cuenta guardada!" ,Toast.LENGTH_SHORT,false).show();

                        }else {
                            mPrefs.edit().clear().apply();
                        }

                        Intent intent = new Intent(MainActivity.this, Usuario.class);
                        startActivity(intent);

                        ed_name.getText().clear();
                        ed_psw.getText().clear();

                        Toast.makeText(MainActivity.this, "Bienvenido " , Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        ed_psw.setText("");
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Campos vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*Para poder iniciar la cuenta con google asociada a una bbdd en firebase*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
     }

    private void getPreferenceData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if (sp.contains("pref_name")){
            String u = sp.getString("pref_name","not found");
            ed_name.setText(u.toString());
        }
        if (sp.contains("pref_pass")){
            String p = sp.getString("pref_pass","not found");
            ed_psw.setText(p.toString());
        }
        if (sp.contains("pref_check")){
            Boolean b = sp.getBoolean("pref_check",false);
            mCheckBox.setChecked(b);
        }
    }

    private boolean camposVacios() {
        if (TextUtils.isEmpty(ed_name.getText().toString()) || TextUtils.isEmpty(ed_psw.getText().toString())) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent EXIT = new Intent(Intent.ACTION_MAIN);
        EXIT.addCategory(Intent.CATEGORY_HOME);
        EXIT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(EXIT);
        System.exit(0);
    }

    //Metodos necesarios para el login con google: signin(),onClick(),onActivityResult y handleSignInResult()
    public void signin(){
        Intent signInInternet = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInInternet,RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin_Goggle:
                signin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG,"handleSignnInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Toasty.success(this,"Hola " + acct.getDisplayName() + "!",Toast.LENGTH_SHORT,false).show();
        }else{}
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

}
