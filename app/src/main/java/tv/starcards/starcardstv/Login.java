package tv.starcards.starcardstv;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.data.userdata.UserData;
import tv.starcards.starcardstv.application.http.GetBearerLogin;
import tv.starcards.starcardstv.application.util.NetWorkState;

public class Login extends AppCompatActivity {

    private static final String TYPE = "grant_type";
    private static final String PASSWORD = "password";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    EditText etLogin, etPassword;
    Button loginButton;
    TextView createAccount, forgotPassword;
    ActionProcessButton login;

    Thread main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        main = Thread.currentThread();

        etLogin = (EditText) findViewById(R.id.login_field);
        etPassword = (EditText) findViewById(R.id.password_field);

        login = (ActionProcessButton) findViewById(R.id.btnProcess);
        login.setMode(ActionProcessButton.Mode.PROGRESS);
        login.setProgress(0);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setMode(ActionProcessButton.Mode.ENDLESS);
                login.setProgress(1);
                if (!isNetworkConnected()) {
                    new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Отсутствует подключение к интернету")
                            .setContentText("Проверьте включен ли wifi(рекомендуется) или передача мобильных данных и повторите попытку")
                            .show();
                    login.setProgress(0);
                } else {
                    Log.d("Login", "Login button pressed");
                    loginUser();
                }
            }
        });

        createAccount = (TextView) findViewById(R.id.create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.REGISTER_REDIRECT));
                startActivity(intent);
            }
        });

        forgotPassword = (TextView) findViewById(R.id.remind_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.REMIND_REDIRECT));
                startActivity(intent);
            }
        });

        UserData.getInstance().initUserData(this);
    }

    private void loginUser(){
        final String username = etLogin.getText().toString().trim();
        final String password = etPassword.getText().toString();

        Thread main = Thread.currentThread();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.AUTH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Server is reachable", response);
                        if (response.contains("error")) {
                            Log.d("Login", "Server request error: " + response);
                            login.setError("Error!");
                            login.setErrorText("Error!");
                            login.setProgress(0);
                            showWrongDataMessage();
                        } else if (response.contains("login_token")) {
                            saveTokensToTheDB(response);
                            saveUserDataToTheDB();
                            setLogged();
                            GetBearerLogin request = new GetBearerLogin(Login.this);
                            request.doRequest(GetBearerLogin.USER_INFO_REQUEST);
                            login.setProgress(100);
                            Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showWrongDataMessage();
                        Log.d("Login", "Error... " + error);
                        login.setProgress(0);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(TYPE, PASSWORD);
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean isNetworkConnected() {
        NetWorkState state = new NetWorkState(this);
        return state.networkIsAvailable();
    }

    private void showWrongDataMessage() {
        new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ошибка доступа")
                .setContentText("Проверьте корректность ваших данных.\n" +
                        "Если не помните свои данные - воспользуйтесь напоминанием пароля")
                .show();
    }

    private void showServerResponseError() {
        new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Невозможно")
                .setContentText("Неудачная авторизация")
                .show();
    }

    private void saveTokensToTheDB(String response){
        UserData.getInstance().setLoginTokens(response);
    }

    private void saveUserDataToTheDB() {
        UserData.getInstance().setUserData(etLogin.getText().toString().trim(), etPassword.getText().toString().trim());
    }

    private void setLogged() {
        MainScreenActivity.justEntered = true;
        UserData.getInstance().setLoggedByLogin();
    }
}
