package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.AuthUtils;
import com.yy.sorter.utils.ThToast;

import th.service.core.AbstractDataServiceFactory;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;

/**
 * Created by Administrator on 2017/5/31.
 */

public class RegisterUi extends BaseUi {

    private EditText usernameEdit,passwordEdit,repasswordEdit;
    private Button registerBtn;

    private  String username,password;

    public RegisterUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null){
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_register,null);

            usernameEdit= (EditText) view.findViewById(R.id.usernameEdit);
            passwordEdit= (EditText) view.findViewById(R.id.passwordEdit);
            repasswordEdit= (EditText) view.findViewById(R.id.repasswordEdit);
            registerBtn= (Button) view.findViewById(R.id.registerBtn);


            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    username=usernameEdit.getText().toString();
                    password=passwordEdit.getText().toString();
                    String repassword=repasswordEdit.getText().toString();

                    if(username.trim().isEmpty()||password.trim().isEmpty()){
                        ThToast.showToast(ctx, FileManager.getInstance().getString(22)); //22#用户名或密码不能为空
                        return;
                    }else if(!password.equals(repassword)){
                        ThToast.showToast(ctx, FileManager.getInstance().getString(23)); //23#两次输入密码不一致
                        return;
                    }
                    AbstractDataServiceFactory.initService(AbstractDataServiceFactory.SERVICE_TYPE_TCP);

                    AbstractDataServiceFactory.getInstance().register(username,password);


                }
            });

        }

        return view;
    }

    @Override
    public void initViewContent() {
        super.initViewContent();

        usernameEdit.setHint(FileManager.getInstance().getString(24));//   24#用户名
        passwordEdit.setHint(FileManager.getInstance().getString(25));// 25#密码
        repasswordEdit.setHint(FileManager.getInstance().getString(26));//26#确认密码
        registerBtn.setText(FileManager.getInstance().getString(27));// 27#注册
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
    }


    @Override
    public int getID() {
        return ConstantValues.VIEW_REGISTER;
    }

    @Override
    public void update(Object var1, final Object var2) {
        super.update(var1,var2);
        mainUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (var2.getClass() == ThPackage.class) {
                    ThPackage thPackage = (ThPackage) var2;
                    if(thPackage.getType()== ThCommand.TCP_USER_REGISTER_CMD){
                        //1:注册成功 2:用户已经注册 3：用户名或密码错误
                         if(thPackage.getExtendType()==1){
                             showToast(FileManager.getInstance().getString(28));//28#注册成功
                             AuthUtils.buildLocalCertificationFile(ctx,username,password);
                             MiddleManger.getInstance().goBack();
                         }else if(thPackage.getExtendType()==2){
                             showToast(FileManager.getInstance().getString(29));//  29#用户已经注册
                         }else if(thPackage.getExtendType()==3){
                             showToast(FileManager.getInstance().getString(30));//  30#用户名或密码错误
                         }
                        AbstractDataServiceFactory.getInstance().closeConnect();
                    }
                }
            }
        });
    }

}
