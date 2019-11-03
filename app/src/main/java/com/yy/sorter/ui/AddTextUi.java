package com.yy.sorter.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.StringUtils;

import th.service.core.AbstractDataServiceFactory;
import th.service.helper.YYCommand;
import th.service.helper.YYPackage;

public class AddTextUi extends BaseUi {
    private AutoCompleteTextView txtEdit;
    private Button txtBtn;
    public AddTextUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_add_text, null);
            txtEdit = (AutoCompleteTextView) view.findViewById(R.id.txtEdit);
            txtBtn = (Button) view.findViewById(R.id.txtBtn);

            txtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strTxt = txtEdit.getText().toString().trim();

                    if(TextUtils.isEmpty(strTxt))
                    {
                        //1032#添加文本不能为空

                        showToast(FileManager.getInstance().getString(1032,"添加文本不能为空"));
                    }else
                    {
                        byte[] arr = StringUtils.convertStringToByteArray(strTxt);
                        if(arr.length>100)
                        {
                            //1033#输入文本必须小于100个字符
                            showToast(FileManager.getInstance().getString(1033,"输入文本必须小于100个字符"));
                        }else
                        {
                            AbstractDataServiceFactory.getInstance().setText(arr);
                        }
                    }
                }
            });
        }


        return view;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        setLanguage();

    }
    private void setLanguage()
    {
        //142#文本
        txtEdit.setHint(FileManager.getInstance().getString(142,"文本"));
        txtBtn.setText(FileManager.getInstance().getString(143,"添加"));//143#添加
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_ADD_TEXT;
    }

    @Override
    public void receivePacketData(YYPackage packet) {
        if(packet.getType() == YYCommand.ADD_TEXT_CMD)
        {

            //1034#文本设置成功
            //1035#文本设置失败
            showToast(FileManager.getInstance().getString(1034,"文本设置成功"));
        }
    }
}
