package com.zspirytus.dmdemo.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zspirytus.dmdemo.Interface.getModRegResponse;
import com.zspirytus.dmdemo.JavaSource.Manager.ActivityManager;
import com.zspirytus.dmdemo.JavaSource.WebServiceUtils.SyncTask.MyAsyncTask;
import com.zspirytus.dmdemo.JavaSource.WebServiceUtils.WebServiceConnector;
import com.zspirytus.dmdemo.R;

import java.util.ArrayList;

public class SubLoginActivity extends BaseActivity {

    private static final String viewKey = "viewKey";
    private static final int REGISTER = 1;
    private static final int MODIFYPWD = 2;
    private final Activity activity = this;
    private EditText mSno;
    private EditText mAccount;
    private EditText mPwd;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        Intent intent = getIntent();
        int view = intent.getIntExtra(viewKey,0);
        mySetContentView(view);
    }

    @Override
    public void onDestroy(){
        ActivityManager.removeActivity(this);
        super.onDestroy();
    }

    /**
     * if it is register view,set register view,else set modifyPwd view
     * @param view register view or modifyPwd view
     */
    private void mySetContentView(int view){
        switch(view){
            case REGISTER:
                setContentView(R.layout.activity_register);
                initRegView();
                break;
            case MODIFYPWD:
                setContentView(R.layout.activity_modifypwd);
                initModifyPwdView();
                break;
        }
    }

    /**
     * init register view
     */
    private void initRegView(){
        mSno = (EditText)findViewById(R.id.edittext_reg_sno);
        mAccount = (EditText)findViewById(R.id.edittext_reg_account);
        mPwd = (EditText)findViewById(R.id.edittext_reg_pwd);
        button = (Button)findViewById(R.id.button_reg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInputLegalForReg()){
                    Toast.makeText(activity,"输入值不符合规范！",Toast.LENGTH_SHORT).show();
                    return;
                }
                MyAsyncTask<getModRegResponse> myAsyncTask = new MyAsyncTask<getModRegResponse>(activity,WebServiceConnector.METHOD_REGISTERACCOUNT);
                myAsyncTask.setListener(new getModRegResponse() {
                    @Override
                    public void getResult(ArrayList<String> result) {
                        boolean isSuccess = result.size() > 0 && result.get(0).indexOf("true") != -1;
                        if(isSuccess){
                            Toast.makeText(activity,"注册成功！请妥善保存好账号密码！",Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                        else
                            Toast.makeText(activity,"注册失败...",Toast.LENGTH_SHORT).show();
                    }
                });
                myAsyncTask.execute(getRegParamType(),getRegInput());
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.New_Member);
    }

    /**
     * is input legal for activity reg?
     * @return true or false
     */
    private boolean isInputLegalForReg(){
        String sno = mSno.getText().toString();
        String acc = mAccount.getText().toString();
        String pwd = mPwd.getText().toString();
        if(sno.length() != 11)
            return false;
        if(acc.length() > 9)
            return false;
        if (pwd.length() > 16)
            return false;
        return true;
    }

    /**
     * get WebService Method： Reg Params Name
     * @return Reg Params Name
     */
    private ArrayList<String> getRegParamType(){
        ArrayList<String> paramType = new ArrayList<>();
        paramType.clear();
        paramType.add(WebServiceConnector.PARAMTYPE_SNO);
        paramType.add(WebServiceConnector.PARAMTYPE_ACCOUNT);
        paramType.add(WebServiceConnector.PARAMTYPE_PWD);
        paramType.add(WebServiceConnector.PARAMTYPE_TYPE);
        return paramType;
    }

    /**
     * get WebService Method： Reg Params
     * @return Reg Params
     */
    private ArrayList<String> getRegInput(){
        ArrayList<String> input = new ArrayList<>();
        boolean isManager = mSno.getText().toString().indexOf("5281") != -1;
        input.add(mSno.getText().toString());
        input.add(mAccount.getText().toString());
        input.add(mPwd.getText().toString());
        if(isManager)
            input.add("1");
        else
            input.add("0");
        return input;
    }

    /**
     * init ModifyPwd view
     */
    private void initModifyPwdView(){
        mAccount = (EditText)findViewById(R.id.edittext_mPwd_account);
        mPwd = (EditText)findViewById(R.id.edittext_mPwd_pwd);
        button = (Button)findViewById(R.id.button_mPwd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInputLegalForMod()){
                    Toast.makeText(activity,"输入值不符合规范！",Toast.LENGTH_SHORT).show();
                    return;
                }
                MyAsyncTask<getModRegResponse> myAsyncTask = new MyAsyncTask<getModRegResponse>(activity,WebServiceConnector.METHOD_MODIFYPWD);
                myAsyncTask.setListener(new getModRegResponse() {
                    @Override
                    public void getResult(ArrayList<String> result) {
                        boolean isSuccess = result.size() > 0 && result.get(0).indexOf("true") != -1;
                        if(isSuccess){
                            Toast.makeText(activity,"修改成功！请妥善保存好账号密码！",Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                        else {
                            Toast.makeText(activity,"修改失败...",Toast.LENGTH_SHORT).show();
                            Toast.makeText(activity,"请确保账号的正确",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                myAsyncTask.execute(getModParamType(),getModInput());
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.Modify_Pwd);
    }

    /**
     * is input legal for activity modify pwd?
     * @return true or false
     */
    private boolean isInputLegalForMod(){
        String acc = mAccount.getText().toString();
        String pwd = mPwd.getText().toString();
        if(acc.length() > 9)
            return false;
        if(pwd.length() > 16)
            return false;
        return true;
    }

    /**
     * get WebService Method: modifyPwd Params Name
     * @return modifyPwd Params Name
     */
    private ArrayList<String> getModParamType(){
        ArrayList<String> paramType = new ArrayList<>();
        paramType.clear();
        paramType.add(WebServiceConnector.PARAMTYPE_ACCOUNT);
        paramType.add(WebServiceConnector.PARAMTYPE_PWD);
        return paramType;
    }

    /**
     * get WebService Method: modifyPwd Params
     * @return modifyPwd Params
     */
    private ArrayList<String> getModInput(){
        ArrayList<String> input = new ArrayList<>();
        input.clear();
        input.add(mAccount.getText().toString());
        input.add(mPwd.getText().toString());
        return input;
    }

    /**
     * Start This Activity
     * @param context Context
     * @param view viewId
     */
    public static void StartThisActivity(Context context,int view){
        Intent intent = new Intent(context, SubLoginActivity.class);
        intent.putExtra(viewKey, view);
        context.startActivity(intent);
    }
}
